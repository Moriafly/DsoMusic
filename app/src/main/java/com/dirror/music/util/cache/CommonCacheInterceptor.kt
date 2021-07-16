package com.dirror.music.util.cache

import android.util.Log
import com.dirror.music.MyApp
import com.dirror.music.util.HttpUtils
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.internal.Util
import okhttp3.internal.cache.CacheRequest
import okhttp3.internal.http.HttpCodec
import okhttp3.internal.http.RealResponseBody
import okio.Buffer
import okio.Source
import okio.Timeout
import okio.buffer
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

class CommonCacheInterceptor: Interceptor {
    
    private companion object {
        const val TAG = "CommonCacheInterceptor"
        const val ONE_KB_SIZE = 1024L
        const val ONE_MB_SIZE = 1024 * 1024L
        const val ONE_GB_SIZE = 1024 * 1024 * 1024
        const val BT_TAG = "byte"
        const val KB_TAG = "KB"
        const val MB_TAG = "MB"
        const val GB_TAG = "GB"
    }

    private val cacheFile = File("${MyApp.context.externalCacheDir}/LocalHttpCache")
    private val cache = Cache(cacheFile, 50*1024*1024L)

    init {
        Log.d(TAG, "cache file dir is: $cacheFile, fileSize: ${sizeToString(getFileLength(cacheFile).toFloat())}")
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val headers = request.headers()
        val useCache = headers[HttpUtils.USE_CACHE]
        val forceCache = HttpUtils.FORCE_CACHE == useCache
        if (forceCache) {
            val cacheCandidate: Response? = cache.get(request)
            if (cacheCandidate != null) {
                Log.d(TAG, "get cache for ${request.url()}")
                return cacheCandidate
            }
        }
        val response = chain.proceed(request)
        if (forceCache && response.code() == 200) {
            Log.d(TAG, "add cache for ${request.url()}")
            val cacheRequest = cache.put(response)
            return cacheWritingResponse(cacheRequest, response)
        }
        return response
    }

    @Throws(IOException::class)
    private fun cacheWritingResponse(cacheRequest: CacheRequest?, response: Response): Response {
        // Some apps return a null body; for compatibility we treat that like a null cache request.
        if (cacheRequest == null) return response
        val cacheBodyUnbuffered = cacheRequest.body() ?: return response
        val source = response.body()!!.source()
        val cacheBody = cacheBodyUnbuffered.buffer()
        val cacheWritingSource: Source = object : Source {
            var cacheRequestClosed = false

            @Throws(IOException::class)
            override fun read(sink: Buffer, byteCount: Long): Long {
                val bytesRead: Long
                try {
                    bytesRead = source.read(sink, byteCount)
                } catch (e: IOException) {
                    if (!cacheRequestClosed) {
                        cacheRequestClosed = true
                        cacheRequest.abort() // Failed to write a complete cache response.
                    }
                    throw e
                }
                if (bytesRead == -1L) {
                    if (!cacheRequestClosed) {
                        cacheRequestClosed = true
                        cacheBody.close() // The cache response is complete!
                    }
                    return -1
                }
                sink.copyTo(cacheBody.buffer(), sink.size - bytesRead, bytesRead)
                cacheBody.emitCompleteSegments()
                return bytesRead
            }

            override fun timeout(): Timeout {
                return source.timeout()
            }

            @Throws(IOException::class)
            override fun close() {
                if (!cacheRequestClosed
                    && !Util.discard(
                        this,
                        HttpCodec.DISCARD_STREAM_TIMEOUT_MILLIS,
                        TimeUnit.MILLISECONDS
                    )
                ) {
                    cacheRequestClosed = true
                    cacheRequest.abort()
                }
                source.close()
            }
        }
        val contentType = response.header("Content-Type")
        val contentLength = response.body()!!.contentLength()
        return response.newBuilder()
            .body(RealResponseBody(contentType, contentLength, cacheWritingSource.buffer()))
            .build()
    }

    // 递归遍历每个文件的大小
    private fun getFileLength(file: File?): Long {
        if (file == null) return -1
        var size: Long = 0
        if (!file.isDirectory) {
            size = file.length()
        } else {
            file.listFiles()?.forEach { f ->
                size += getFileLength(f)
            }
        }
        return size
    }


    // 把文件大小转化成容易理解的格式显示，如多少M
    private fun sizeToString(size: Float): String {
        return if (size >= 0 && size < ONE_KB_SIZE) {
            "$size $BT_TAG"
        } else if (size >= ONE_KB_SIZE && size < ONE_MB_SIZE) {
            "${size / ONE_KB_SIZE} $KB_TAG"
        } else if (size >= ONE_MB_SIZE && size < ONE_GB_SIZE) {
            "${size / ONE_MB_SIZE} $MB_TAG"
        } else {
            "${size / ONE_GB_SIZE} $GB_TAG"
        }
    }


}