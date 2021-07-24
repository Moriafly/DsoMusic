package com.dirror.music.util.cache;

import android.util.Log;

import androidx.annotation.Nullable;

import java.io.Closeable;
import java.io.File;
import java.io.Flushable;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import okhttp3.CipherSuite;
import okhttp3.FormBody;
import okhttp3.Handshake;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.TlsVersion;
import okhttp3.internal.Util;
import okhttp3.internal.cache.CacheRequest;
import okhttp3.internal.cache.CacheStrategy;
import okhttp3.internal.cache.DiskLruCache;
import okhttp3.internal.cache.InternalCache;
import okhttp3.internal.http.HttpHeaders;
import okhttp3.internal.http.StatusLine;
import okhttp3.internal.io.FileSystem;
import okhttp3.internal.platform.Platform;
import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.ByteString;
import okio.ForwardingSink;
import okio.ForwardingSource;
import okio.Okio;
import okio.Sink;
import okio.Source;

public final class Cache implements Closeable, Flushable {

    private static final String TAG = "Cache";

    private static final int VERSION = 201105;
    private static final int ENTRY_METADATA = 0;
    private static final int ENTRY_BODY = 1;
    private static final int ENTRY_COUNT = 2;

    final InternalCache internalCache = new InternalCache() {
        @Override public Response get(Request request) throws IOException {
            return Cache.this.get(request);
        }

        @Override public CacheRequest put(Response response) throws IOException {
            return Cache.this.put(response);
        }

        @Override public void remove(Request request) throws IOException {
            Cache.this.remove(request);
        }

        @Override public void update(Response cached, Response network) {
            Cache.this.update(cached, network);
        }

        @Override public void trackConditionalCacheHit() {
            Cache.this.trackConditionalCacheHit();
        }

        @Override public void trackResponse(CacheStrategy cacheStrategy) {
            Cache.this.trackResponse(cacheStrategy);
        }
    };

    final DiskLruCache cache;

    /* read and write statistics, all guarded by 'this' */
    int writeSuccessCount;
    int writeAbortCount;
    private int networkCount;
    private int hitCount;
    private int requestCount;

    /**
     * Create a cache of at most {@code maxSize} bytes in {@code directory}.
     */
    public Cache(File directory, long maxSize) {
        this(directory, maxSize, FileSystem.SYSTEM);
    }

    Cache(File directory, long maxSize, FileSystem fileSystem) {
        this.cache = DiskLruCache.create(fileSystem, directory, VERSION, ENTRY_COUNT, maxSize);
    }

    private String key(Request request) {
        RequestBody requestBody = request.body();
        String url = request.url().toString();
        StringBuilder sb = new StringBuilder();
        sb.append(url).append("&");
        Charset charset = null;
        MediaType type;
        if (requestBody != null && (type = requestBody.contentType()) != null) {
            charset = type.charset();
        }
        if (charset == null) {
            charset = StandardCharsets.UTF_8;
        }
        if (requestBody != null) {
            try (Buffer buffer = new Buffer()) {
                requestBody.writeTo(buffer);
                sb.append(buffer.readString(charset));
            } catch (Exception e) {
                Log.d(TAG, "read request error: " + e);
            }
        }
        return String.valueOf(sb.toString().hashCode());
    }


    @Nullable
    Response get(Request request) {
        String key = key(request);
        DiskLruCache.Snapshot snapshot;
        Cache.Entry entry;
        try {
            snapshot = cache.get(key);
            if (snapshot == null) {
                return null;
            }
        } catch (IOException e) {
            // Give up because the cache cannot be read.
            return null;
        }

        try {
            entry = new Cache.Entry(snapshot.getSource(ENTRY_METADATA));
        } catch (IOException e) {
            Util.closeQuietly(snapshot);
            return null;
        }


        Response response = entry.response(snapshot);

        if (!entry.matches(request, response)) {
            Util.closeQuietly(response.body());
            return null;
        }

        return response;
    }

    private boolean validatesCache(String method) {
        return method.equals("POST")
                || method.equals("GET");
    }

    @Nullable CacheRequest put(Response response) {
        if (!validatesCache(response.request().method())) {
            try {
                remove(response.request());
            } catch (IOException ignored) {
                // The cache cannot be written.
            }
            return null;
        }

        if (HttpHeaders.hasVaryAll(response)) {
            return null;
        }

        Cache.Entry entry = new Cache.Entry(response);
        DiskLruCache.Editor editor = null;
        try {
            editor = cache.edit(key(response.request()));
            if (editor == null) {
                return null;
            }
            entry.writeTo(editor);
            return new Cache.CacheRequestImpl(editor);
        } catch (IOException e) {
            abortQuietly(editor);
            return null;
        }
    }

    void remove(Request request) throws IOException {
        cache.remove(key(request));
    }

    void update(Response cached, Response network) {
        Cache.Entry entry = new Cache.Entry(network);
        DiskLruCache.Snapshot snapshot = ((CacheResponseBody) cached.body()).snapshot;
        DiskLruCache.Editor editor = null;
        try {
            editor = snapshot.edit(); // Returns null if snapshot is not current.
            if (editor != null) {
                entry.writeTo(editor);
                editor.commit();
            }
        } catch (IOException e) {
            abortQuietly(editor);
        }
    }

    private void abortQuietly(@Nullable DiskLruCache.Editor editor) {
        // Give up because the cache cannot be written.
        try {
            if (editor != null) {
                editor.abort();
            }
        } catch (IOException ignored) {
        }
    }

    /**
     * Initialize the cache. This will include reading the journal files from the storage and building
     * up the necessary in-memory cache information.
     *
     * <p>The initialization time may vary depending on the journal file size and the current actual
     * cache size. The application needs to be aware of calling this function during the
     * initialization phase and preferably in a background worker thread.
     *
     * <p>Note that if the application chooses to not call this method to initialize the cache. By
     * default, the okhttp will perform lazy initialization upon the first usage of the cache.
     */
    public void initialize() throws IOException {
        cache.initialize();
    }

    /**
     * Closes the cache and deletes all of its stored values. This will delete all files in the cache
     * directory including files that weren't created by the cache.
     */
    public void delete() throws IOException {
        cache.delete();
    }

    /**
     * Deletes all values stored in the cache. In-flight writes to the cache will complete normally,
     * but the corresponding responses will not be stored.
     */
    public void evictAll() throws IOException {
        cache.evictAll();
    }

    /**
     * Returns an iterator over the URLs in this cache. This iterator doesn't throw {@code
     * ConcurrentModificationException}, but if new responses are added while iterating, their URLs
     * will not be returned. If existing responses are evicted during iteration, they will be absent
     * (unless they were already returned).
     *
     * <p>The iterator supports {@linkplain Iterator#remove}. Removing a URL from the iterator evicts
     * the corresponding response from the cache. Use this to evict selected responses.
     */
    public Iterator<String> urls() throws IOException {
        return new Iterator<String>() {
            final Iterator<DiskLruCache.Snapshot> delegate = cache.snapshots();

            @Nullable String nextUrl;
            boolean canRemove;

            @Override public boolean hasNext() {
                if (nextUrl != null) return true;

                canRemove = false; // Prevent delegate.remove() on the wrong item!
                while (delegate.hasNext()) {
                    DiskLruCache.Snapshot snapshot = delegate.next();
                    try {
                        BufferedSource metadata = Okio.buffer(snapshot.getSource(ENTRY_METADATA));
                        nextUrl = metadata.readUtf8LineStrict();
                        return true;
                    } catch (IOException ignored) {
                        // We couldn't read the metadata for this snapshot; possibly because the host filesystem
                        // has disappeared! Skip it.
                    } finally {
                        snapshot.close();
                    }
                }

                return false;
            }

            @Override public String next() {
                if (!hasNext()) throw new NoSuchElementException();
                String result = nextUrl;
                nextUrl = null;
                canRemove = true;
                return result;
            }

            @Override public void remove() {
                if (!canRemove) throw new IllegalStateException("remove() before next()");
                delegate.remove();
            }
        };
    }

    public synchronized int writeAbortCount() {
        return writeAbortCount;
    }

    public synchronized int writeSuccessCount() {
        return writeSuccessCount;
    }

    public long size() throws IOException {
        return cache.size();
    }

    /** Max size of the cache (in bytes). */
    public long maxSize() {
        return cache.getMaxSize();
    }

    @Override public void flush() throws IOException {
        cache.flush();
    }

    @Override public void close() throws IOException {
        cache.close();
    }

    public File directory() {
        return cache.getDirectory();
    }

    public boolean isClosed() {
        return cache.isClosed();
    }

    synchronized void trackResponse(CacheStrategy cacheStrategy) {
        requestCount++;

        if (cacheStrategy.networkRequest != null) {
            // If this is a conditional request, we'll increment hitCount if/when it hits.
            networkCount++;
        } else if (cacheStrategy.cacheResponse != null) {
            // This response uses the cache and not the network. That's a cache hit.
            hitCount++;
        }
    }

    synchronized void trackConditionalCacheHit() {
        hitCount++;
    }

    public synchronized int networkCount() {
        return networkCount;
    }

    public synchronized int hitCount() {
        return hitCount;
    }

    public synchronized int requestCount() {
        return requestCount;
    }

    private final class CacheRequestImpl implements CacheRequest {
        private final DiskLruCache.Editor editor;
        private Sink cacheOut;
        private Sink body;
        boolean done;

        CacheRequestImpl(final DiskLruCache.Editor editor) {
            this.editor = editor;
            this.cacheOut = editor.newSink(ENTRY_BODY);
            this.body = new ForwardingSink(cacheOut) {
                @Override public void close() throws IOException {
                    synchronized (Cache.this) {
                        if (done) {
                            return;
                        }
                        done = true;
                        writeSuccessCount++;
                    }
                    super.close();
                    editor.commit();
                }
            };
        }

        @Override public void abort() {
            synchronized (Cache.this) {
                if (done) {
                    return;
                }
                done = true;
                writeAbortCount++;
            }
            Util.closeQuietly(cacheOut);
            try {
                editor.abort();
            } catch (IOException ignored) {
            }
        }

        @Override public Sink body() {
            return body;
        }
    }

    private static final class Entry {
        /** Synthetic response header: the local time when the request was sent. */
        private static final String SENT_MILLIS = Platform.get().getPrefix() + "-Sent-Millis";

        /** Synthetic response header: the local time when the response was received. */
        private static final String RECEIVED_MILLIS = Platform.get().getPrefix() + "-Received-Millis";

        private final String url;
        private final Headers varyHeaders;
        private final String requestMethod;
        private final Protocol protocol;
        private final int code;
        private final String message;
        private final Headers responseHeaders;
        private final @Nullable
        Handshake handshake;
        private final long sentRequestMillis;
        private final long receivedResponseMillis;

        /**
         * Reads an entry from an input stream. A typical entry looks like this:
         * <pre>{@code
         *   http://google.com/foo
         *   GET
         *   2
         *   Accept-Language: fr-CA
         *   Accept-Charset: UTF-8
         *   HTTP/1.1 200 OK
         *   3
         *   Content-Type: image/png
         *   Content-Length: 100
         *   Cache-Control: max-age=600
         * }</pre>
         *
         * <p>A typical HTTPS file looks like this:
         * <pre>{@code
         *   https://google.com/foo
         *   GET
         *   2
         *   Accept-Language: fr-CA
         *   Accept-Charset: UTF-8
         *   HTTP/1.1 200 OK
         *   3
         *   Content-Type: image/png
         *   Content-Length: 100
         *   Cache-Control: max-age=600
         *
         *   AES_256_WITH_MD5
         *   2
         *   base64-encoded peerCertificate[0]
         *   base64-encoded peerCertificate[1]
         *   -1
         *   TLSv1.2
         * }</pre>
         * The file is newline separated. The first two lines are the URL and the request method. Next
         * is the number of HTTP Vary request header lines, followed by those lines.
         *
         * <p>Next is the response status line, followed by the number of HTTP response header lines,
         * followed by those lines.
         *
         * <p>HTTPS responses also contain SSL session information. This begins with a blank line, and
         * then a line containing the cipher suite. Next is the length of the peer certificate chain.
         * These certificates are base64-encoded and appear each on their own line. The next line
         * contains the length of the local certificate chain. These certificates are also
         * base64-encoded and appear each on their own line. A length of -1 is used to encode a null
         * array. The last line is optional. If present, it contains the TLS version.
         */
        Entry(Source in) throws IOException {
            try {
                BufferedSource source = Okio.buffer(in);
                url = source.readUtf8LineStrict();
                requestMethod = source.readUtf8LineStrict();
                Headers.Builder varyHeadersBuilder = new Headers.Builder();
                int varyRequestHeaderLineCount = readInt(source);
                for (int i = 0; i < varyRequestHeaderLineCount; i++) {
                    varyHeadersBuilder.add(source.readUtf8LineStrict());
                }
                varyHeaders = varyHeadersBuilder.build();

                StatusLine statusLine = StatusLine.parse(source.readUtf8LineStrict());
                protocol = statusLine.protocol;
                code = statusLine.code;
                message = statusLine.message;
                Headers.Builder responseHeadersBuilder = new Headers.Builder();
                int responseHeaderLineCount = readInt(source);
                for (int i = 0; i < responseHeaderLineCount; i++) {
                    responseHeadersBuilder.add(source.readUtf8LineStrict());
                }
                String sendRequestMillisString = responseHeadersBuilder.get(SENT_MILLIS);
                String receivedResponseMillisString = responseHeadersBuilder.get(RECEIVED_MILLIS);
                responseHeadersBuilder.removeAll(SENT_MILLIS);
                responseHeadersBuilder.removeAll(RECEIVED_MILLIS);
                sentRequestMillis = sendRequestMillisString != null
                        ? Long.parseLong(sendRequestMillisString)
                        : 0L;
                receivedResponseMillis = receivedResponseMillisString != null
                        ? Long.parseLong(receivedResponseMillisString)
                        : 0L;
                responseHeaders = responseHeadersBuilder.build();

                if (isHttps()) {
                    String blank = source.readUtf8LineStrict();
                    if (blank.length() > 0) {
                        throw new IOException("expected \"\" but was \"" + blank + "\"");
                    }
                    String cipherSuiteString = source.readUtf8LineStrict();
                    CipherSuite cipherSuite = CipherSuite.forJavaName(cipherSuiteString);
                    List<Certificate> peerCertificates = readCertificateList(source);
                    List<Certificate> localCertificates = readCertificateList(source);
                    TlsVersion tlsVersion = !source.exhausted()
                            ? TlsVersion.forJavaName(source.readUtf8LineStrict())
                            : TlsVersion.SSL_3_0;
                    handshake = Handshake.get(tlsVersion, cipherSuite, peerCertificates, localCertificates);
                } else {
                    handshake = null;
                }
            } finally {
                in.close();
            }
        }

        Entry(Response response) {
            this.url = response.request().url().toString();
            this.varyHeaders = HttpHeaders.varyHeaders(response);
            this.requestMethod = response.request().method();
            this.protocol = response.protocol();
            this.code = response.code();
            this.message = response.message();
            this.responseHeaders = response.headers();
            this.handshake = response.handshake();
            this.sentRequestMillis = response.sentRequestAtMillis();
            this.receivedResponseMillis = response.receivedResponseAtMillis();
        }

        public void writeTo(DiskLruCache.Editor editor) throws IOException {
            BufferedSink sink = Okio.buffer(editor.newSink(ENTRY_METADATA));

            sink.writeUtf8(url)
                    .writeByte('\n');
            sink.writeUtf8(requestMethod)
                    .writeByte('\n');
            sink.writeDecimalLong(varyHeaders.size())
                    .writeByte('\n');
            for (int i = 0, size = varyHeaders.size(); i < size; i++) {
                sink.writeUtf8(varyHeaders.name(i))
                        .writeUtf8(": ")
                        .writeUtf8(varyHeaders.value(i))
                        .writeByte('\n');
            }

            sink.writeUtf8(new StatusLine(protocol, code, message).toString())
                    .writeByte('\n');
            sink.writeDecimalLong(responseHeaders.size() + 2)
                    .writeByte('\n');
            for (int i = 0, size = responseHeaders.size(); i < size; i++) {
                sink.writeUtf8(responseHeaders.name(i))
                        .writeUtf8(": ")
                        .writeUtf8(responseHeaders.value(i))
                        .writeByte('\n');
            }
            sink.writeUtf8(SENT_MILLIS)
                    .writeUtf8(": ")
                    .writeDecimalLong(sentRequestMillis)
                    .writeByte('\n');
            sink.writeUtf8(RECEIVED_MILLIS)
                    .writeUtf8(": ")
                    .writeDecimalLong(receivedResponseMillis)
                    .writeByte('\n');

            if (isHttps()) {
                sink.writeByte('\n');
                sink.writeUtf8(handshake.cipherSuite().javaName())
                        .writeByte('\n');
                writeCertList(sink, handshake.peerCertificates());
                writeCertList(sink, handshake.localCertificates());
                sink.writeUtf8(handshake.tlsVersion().javaName()).writeByte('\n');
            }
            sink.close();
        }

        private boolean isHttps() {
            return url.startsWith("https://");
        }

        private List<Certificate> readCertificateList(BufferedSource source) throws IOException {
            int length = readInt(source);
            if (length == -1) return Collections.emptyList(); // OkHttp v1.2 used -1 to indicate null.

            try {
                CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
                List<Certificate> result = new ArrayList<>(length);
                for (int i = 0; i < length; i++) {
                    String line = source.readUtf8LineStrict();
                    Buffer bytes = new Buffer();
                    bytes.write(ByteString.decodeBase64(line));
                    result.add(certificateFactory.generateCertificate(bytes.inputStream()));
                }
                return result;
            } catch (CertificateException e) {
                throw new IOException(e.getMessage());
            }
        }

        private void writeCertList(BufferedSink sink, List<Certificate> certificates)
                throws IOException {
            try {
                sink.writeDecimalLong(certificates.size())
                        .writeByte('\n');
                for (int i = 0, size = certificates.size(); i < size; i++) {
                    byte[] bytes = certificates.get(i).getEncoded();
                    String line = ByteString.of(bytes).base64();
                    sink.writeUtf8(line)
                            .writeByte('\n');
                }
            } catch (CertificateEncodingException e) {
                throw new IOException(e.getMessage());
            }
        }

        public boolean matches(Request request, Response response) {
            return url.equals(request.url().toString())
                    && requestMethod.equals(request.method())
                    && HttpHeaders.varyMatches(response, varyHeaders, request);
        }

        public Response response(DiskLruCache.Snapshot snapshot) {
            String contentType = responseHeaders.get("Content-Type");
            String contentLength = responseHeaders.get("Content-Length");
            RequestBody requestBody = null;
            if ("POST".equals(requestMethod)) {
                requestBody = new FormBody.Builder().build();
            }
            new FormBody.Builder().build();
            Request cacheRequest = new Request.Builder()
                    .url(url)
                    .method(requestMethod, requestBody)
                    .headers(varyHeaders)
                    .build();
            return new Response.Builder()
                    .request(cacheRequest)
                    .protocol(protocol)
                    .code(code)
                    .message(message)
                    .headers(responseHeaders)
                    .body(new Cache.CacheResponseBody(snapshot, contentType, contentLength))
                    .handshake(handshake)
                    .sentRequestAtMillis(sentRequestMillis)
                    .receivedResponseAtMillis(receivedResponseMillis)
                    .build();
        }
    }

    static int readInt(BufferedSource source) throws IOException {
        try {
            long result = source.readDecimalLong();
            String line = source.readUtf8LineStrict();
            if (result < 0 || result > Integer.MAX_VALUE || !line.isEmpty()) {
                throw new IOException("expected an int but was \"" + result + line + "\"");
            }
            return (int) result;
        } catch (NumberFormatException e) {
            throw new IOException(e.getMessage());
        }
    }

    private static class CacheResponseBody extends ResponseBody {
        final DiskLruCache.Snapshot snapshot;
        private final BufferedSource bodySource;
        private final @Nullable String contentType;
        private final @Nullable String contentLength;

        CacheResponseBody(final DiskLruCache.Snapshot snapshot,
                          String contentType, String contentLength) {
            this.snapshot = snapshot;
            this.contentType = contentType;
            this.contentLength = contentLength;
            Source source = snapshot.getSource(ENTRY_BODY);
            bodySource = Okio.buffer(new ForwardingSource(source) {
                @Override public void close() throws IOException {
                    snapshot.close();
                    super.close();
                }
            });
        }

        @Override public MediaType contentType() {
            return contentType != null ? MediaType.parse(contentType) : null;
        }

        @Override public long contentLength() {
            try {
                return contentLength != null ? Long.parseLong(contentLength) : -1;
            } catch (NumberFormatException e) {
                return -1;
            }
        }

        @Override public BufferedSource source() {
            return bodySource;
        }
    }
}
