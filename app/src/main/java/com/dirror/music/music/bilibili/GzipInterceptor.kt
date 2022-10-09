package com.dirror.music.music.bilibili

import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody
import okio.GzipSource
import okio.buffer
import java.nio.charset.Charset
import java.util.regex.Pattern

object GzipInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())

        return if (isGzipped(response)) {
            unzip(response)
        } else {
            response
        }
    }

    private fun unzip(response: Response): Response {
        if (response.body() == null) {
            return response
        }

        var charset = "UTF-8"
        val ct = response.header("content-type")
        if (ct != null) {
            val p = Pattern.compile("\\bcharset=([\\w\\-]+)\\b")
            val m = p.matcher(ct)
            if (m.find()) {
                charset = m.group(1)
            }
        }

        val gzipSource = GzipSource(response.body()!!.source())
        val bodyString = gzipSource.buffer().readString(Charset.forName(charset))

        val responseBody = ResponseBody.create(response.body()!!.contentType(), bodyString)
        val strippedHeaders = response.headers().newBuilder()
            .removeAll("Content-Encoding")
            .removeAll("Content-Length")
            .build()
        return response.newBuilder()
            .headers(strippedHeaders)
            .body(responseBody)
            .message(response.message())
            .build()
    }

    private fun isGzipped(response: Response): Boolean {
        return response.header("Content-Encoding") != null && response.header("Content-Encoding") == "gzip"
    }
}