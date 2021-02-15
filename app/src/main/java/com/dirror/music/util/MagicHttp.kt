package com.dirror.music.util

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.dirror.music.MyApplication
import com.dirror.music.util.cache.ACache
import okhttp3.*
import okhttp3.OkHttpClient
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * MagicHttp
 * @author Moriafly
 */
object MagicHttp {

    const val DEFAULT_CACHE_TIME_GET = 4 * ACache.TIME_HOUR
    const val DEFAULT_CACHE_TIME_POST = ACache.TIME_DAY

    interface MagicHttpInterface {

        fun newGet(url: String, success: (String) -> Unit, failure: (String) -> Unit)

        fun post(url: String, json: String, success: (String) -> Unit)

        /**
         * 支持缓存
         */
        fun getByCache(context: Context, url: String, success: (String) -> Unit, failure: (String) -> Unit)

        fun getByCache(context: Context, url: String, time: Int, success: (String) -> Unit, failure: (String) -> Unit)

        fun postByCache(context: Context, url: String, json: String, success: (String) -> Unit)
    }

    // 运行在主线程，更新 UI
    fun runOnMainThread(runnable: Runnable) {
        Handler(Looper.getMainLooper()).post(runnable)
    }

    /**
     * 可用多个，不是单例类
     */
    class OkHttpManager: MagicHttpInterface {

        override fun newGet(url: String, success: (String) -> Unit, failure: (String) -> Unit) {
            try {
                val client = OkHttpClient.Builder()
                    .connectTimeout(5, TimeUnit.SECONDS)
                    .readTimeout(3, TimeUnit.SECONDS)
                    .writeTimeout(3, TimeUnit.SECONDS)
                    .build()
                val request = Request.Builder()
                    .url(url)
                    .addHeader("Referer", "https://y.qq.com/portal/player.html")
                    .addHeader(
                        "User-Agent",
                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.183 Safari/537.36 Edg/86.0.622.63"
                    )
                    .get()
                    .build()

                client.newCall(request).enqueue(object : Callback {
                    override fun onResponse(call: Call, response: Response) {
                        val string = response.body?.string()!!
                        success.invoke(string)
                    }

                    override fun onFailure(call: Call, e: IOException) {
                        failure.invoke(e.message.toString())
                    }
                })
            } catch (e: Exception) {
                e.printStackTrace()
                failure.invoke(e.message.toString())
            }
        }

        /**
         * post 请求
         */
        override fun post(url: String, json: String, success: (String) -> Unit) {
            try {
                val client = OkHttpClient.Builder()
                    .connectTimeout(5, TimeUnit.SECONDS)
                    .readTimeout(3, TimeUnit.SECONDS)
                    .writeTimeout(3, TimeUnit.SECONDS)
                    .cookieJar(object : CookieJar {
                        override fun loadForRequest(url: HttpUrl): List<Cookie> {
                            val cookies = MyApplication.cookieStore[url.host]
                            return cookies ?: ArrayList()
                        }

                        override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
                            MyApplication.cookieStore[url.host] = cookies
                        }

                    })
                    .build()

                val body = FormBody.Builder()
                    .add("ids", json)
                    .build()
                val request: Request = Request.Builder()
                    .url("${url}?timestamp=${getCurrentTime()}")
                    .post(body)
                    .build()

                client.newCall(request).enqueue(object : Callback {
                    override fun onResponse(call: Call, response: Response) {
                        val string = response.body?.string()!!
                        success.invoke(string)
                    }

                    override fun onFailure(call: Call, e: IOException) {
                    }
                })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        override fun getByCache(context: Context, url: String, success: (String) -> Unit, failure: (String) -> Unit) {
            val aCache = ACache.get(context)
            val cache: String? = aCache.getAsString(url)
            if (cache.isNullOrEmpty()) {
                newGet(url, {
                    aCache.put(url, it, DEFAULT_CACHE_TIME_GET)
                    success.invoke(it)
                }, {
                    failure.invoke(it)
                })
            } else {
                success.invoke(cache)
            }
        }

        override fun getByCache(
            context: Context,
            url: String,
            time: Int,
            success: (String) -> Unit,
            failure: (String) -> Unit
        ) {
            val aCache = ACache.get(context)
            val cache: String? = aCache.getAsString(url)
            if (cache.isNullOrEmpty()) {
                newGet(url, {
                    aCache.put(url, it, time)
                    success.invoke(it)
                }, {
                    failure.invoke(it)
                })
            } else {
                success.invoke(cache)
            }
        }

        override fun postByCache(context: Context, url: String, json: String, success: (String) -> Unit) {
            val aCache = ACache.get(context)
            val cache: String? = aCache.getAsString(url + json)
            if (cache.isNullOrEmpty()) {
                post(url, json) {
                    aCache.put(url, it, DEFAULT_CACHE_TIME_POST)
                    success.invoke(it)
                }
            } else {
                success.invoke(cache)
            }
        }

    }




}



