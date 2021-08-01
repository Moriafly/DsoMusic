package com.dirror.music.util.cache

import android.util.Log
import com.dirror.music.MyApp
import com.dirror.music.util.HttpUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
import java.math.BigDecimal
import java.util.concurrent.TimeUnit

class CommonCacheInterceptor: Interceptor {
    
    companion object {
        private const val TAG = "CommonCacheInterceptor"
        private val cacheFile = File("${MyApp.context.externalCacheDir}/LocalHttpCache")
        private val cache = Cache(cacheFile, 50*1024*1024L)

        fun getCacheSize(): String {
            return sizeToString(getFileLength(cacheFile).toDouble())
        }

        suspend fun clearCache() = withContext(Dispatchers.IO) {
            if (cacheFile.isDirectory) {
                cacheFile.listFiles()?.forEach { f ->
                    f.delete()
                }
                Log.i(TAG, "cache clear finished")
            }
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
        private fun sizeToString(size: Double): String {
            val kiloByte = size / 1024
            if (kiloByte < 1) {
                return "$size Byte"
            }
            val megaByte = kiloByte / 1024
            if (megaByte < 1) {
                val result1 = BigDecimal(kiloByte.toString())
                return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + " KB"
            }
            val gigaByte = megaByte / 1024
            if (gigaByte < 1) {
                val result2 = BigDecimal(megaByte.toString())
                return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + " MB"
            }
            val teraBytes = gigaByte / 1024
            if (teraBytes < 1) {
                val result3 = BigDecimal(gigaByte.toString())
                return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + " GB"
            }
            val result4 = BigDecimal(teraBytes)
            return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + " TB"
        }
    }

    init {
        Log.d(TAG, "cache file dir is: $cacheFile, fileSize: ${sizeToString(getFileLength(cacheFile).toDouble())}")
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val headers = request.headers()
        val useCache = headers[HttpUtils.USE_CACHE]
        val forceCache = HttpUtils.CACHE_FORCE == useCache
        if (forceCache) {
            val cacheCandidate: Response? = cache.get(request)
            if (cacheCandidate != null) {
                Log.d(TAG, "get cache for ${request.url()}")
                return cacheCandidate
            }
        }
        val response = chain.proceed(request)
        if ((forceCache || HttpUtils.CACHE_UPDATE == useCache) && response.code() == 200) {
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

}