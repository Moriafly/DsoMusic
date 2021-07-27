/**
 * DsoMusic Copyright (C) 2020-2021 Moriafly
 *
 * This program comes with ABSOLUTELY NO WARRANTY; for details type `show w'.
 * This is free software, and you are welcome to redistribute it
 * under certain conditions; type `show c' for details.
 *
 * The hypothetical commands `show w' and `show c' should show the appropriate
 * parts of the General Public License.  Of course, your program's commands
 * might be different; for a GUI interface, you would use an "about box".
 *
 * You should also get your employer (if you work as a programmer) or school,
 * if any, to sign a "copyright disclaimer" for the program, if necessary.
 * For more information on this, and how to apply and follow the GNU GPL, see
 * <https://www.gnu.org/licenses/>.
 *
 * The GNU General Public License does not permit incorporating your program
 * into proprietary programs.  If your program is a subroutine library, you
 * may consider it more useful to permit linking proprietary applications with
 * the library.  If this is what you want to do, use the GNU Lesser General
 * Public License instead of this License.  But first, please read
 * <https://www.gnu.org/licenses/why-not-lgpl.html>.
 */

package com.dirror.music.util

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.dirror.music.util.cache.ACache
import okhttp3.*
import okhttp3.OkHttpClient
import org.jetbrains.annotations.TestOnly
import java.io.IOException
import java.net.Proxy
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * MagicHttp
 * @author Moriafly
 */
object MagicHttp {

    const val TAG = "MagicHttp"

    const val DEFAULT_CACHE_TIME_GET = 4 * ACache.TIME_HOUR
    const val DEFAULT_CACHE_TIME_POST = ACache.TIME_DAY

    interface MagicHttpInterface {

        // @Deprecated("过时方法，请使用协程请求")
        fun newGet(url: String, success: (String) -> Unit, failure: (String) -> Unit)

        fun getWithHeader(url: String, headers: Map<String, String>, success: (String) -> Unit, failure: (String) -> Unit)

        @Deprecated("过时方法，请使用 newPost")
        fun post(url: String, json: String, success: (String) -> Unit)

        fun newPost(api: String, requestBody: RequestBody, success: (String) -> Unit, failure: (Int) -> Unit)

        /**
         * 支持缓存
         */
        fun getByCache(context: Context, url: String, success: (String) -> Unit, failure: (String) -> Unit)

        fun getByCache(context: Context, url: String, time: Int, success: (String) -> Unit, failure: (String) -> Unit)

        fun postByCache(context: Context, url: String, json: String, success: (String) -> Unit)

        /* 协程 GET 请求 */
        @TestOnly
        suspend fun get(url: String): String

    }

    // 运行在主线程，更新 UI
    fun runOnMainThread(runnable: Runnable) {
        Handler(Looper.getMainLooper()).post(runnable)
    }

    /**
     * 可用多个，不是单例类
     */
    class OkHttpManager : MagicHttpInterface {
        override fun getWithHeader(
            url: String,
            headers: Map<String, String>,
            success: (String) -> Unit,
            failure: (String) -> Unit
        ) {
            try {
                val client = OkHttpClient.Builder()
                    .proxy(Proxy.NO_PROXY) // 禁止代理，防止抓包
                    .connectTimeout(5, TimeUnit.SECONDS)
                    .readTimeout(3, TimeUnit.SECONDS)
                    .writeTimeout(3, TimeUnit.SECONDS)
                    .build()
                val request = Request.Builder()
                    .url(url)
                    .headers(Headers.of(headers))
                    .addHeader(
                        "User-Agent",
                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.183 Safari/537.36 Edg/86.0.622.63"
                    )
                    .get()
                    .build()

                client.newCall(request).enqueue(object : Callback {
                    override fun onResponse(call: Call, response: Response) {
                        Log.e(TAG, "onResponse: getWithHeader()")
                        val string = response.body()?.string() ?: ""
                        // val string = response.body?.string()!!
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

        override fun newGet(url: String, success: (String) -> Unit, failure: (String) -> Unit) {
            try {
                val client = OkHttpClient.Builder()
                    .proxy(Proxy.NO_PROXY) // 禁止代理，防止抓包
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
                        Log.e(TAG, "onResponse: newGet()", )
                        val string = response.body()?.string()?:""
                        // val string = response.body?.string()!!
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
                    .proxy(Proxy.NO_PROXY) // 禁止代理，防止抓包
                    .connectTimeout(5, TimeUnit.SECONDS)
                    .readTimeout(3, TimeUnit.SECONDS)
                    .writeTimeout(3, TimeUnit.SECONDS)
                    .build()

                val body: RequestBody = FormBody.Builder()
                    .add("ids", json)
                    .build()
                val request: Request = Request.Builder()
                    .url(url)
                    .post(body)
                    .build()

                client.newCall(request).enqueue(object : Callback {
                    override fun onResponse(call: Call, response: Response) {
                        val string = response.body()?.string() ?: ""
                        success.invoke(string)
                    }

                    override fun onFailure(call: Call, e: IOException) {
                    }
                })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        override fun newPost(api: String, requestBody: RequestBody, success: (String) -> Unit, failure: (Int) -> Unit) {
            try {
                val client = OkHttpClient.Builder()
                    // .proxy(Proxy(Proxy.Type.HTTP, InetSocketAddress("110.243.17.83", 9999)))
                    // .proxy(Proxy.NO_PROXY) // 禁止代理，防止抓包
                    .connectTimeout(5, TimeUnit.SECONDS)
                    .readTimeout(3, TimeUnit.SECONDS)
                    .writeTimeout(3, TimeUnit.SECONDS)
                    .build()

                val request: Request = Request.Builder()
                    .url("${api}?timestamp=${getCurrentTime()}")
//                    .addHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8")
//                    .addHeader("Connection", "close")
//                    .addHeader("Content-Length", "80")
//                    .addHeader("X-Real-IP", "211.161.244.70")
                    .post(requestBody)
                    .build()

                client.newCall(request).enqueue(object : Callback {
                    override fun onResponse(call: Call, response: Response) {
                        val string = response.body()?.string()?:""
                        success.invoke(string)
                    }

                    override fun onFailure(call: Call, e: IOException) {
                        failure(ErrorCode.ERROR_MAGIC_HTTP)
                    }
                })
            } catch (e: Exception) {
                failure(ErrorCode.ERROR_MAGIC_HTTP)
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

        override suspend fun get(url: String): String {
            return suspendCoroutine {
                try {
                    val client = OkHttpClient.Builder()
                        .proxy(Proxy.NO_PROXY) // 禁止代理，防止抓包
                        .connectTimeout(5, TimeUnit.SECONDS)
                        .readTimeout(3, TimeUnit.SECONDS)
                        .writeTimeout(3, TimeUnit.SECONDS)
                        .build()
                    val request = Request.Builder()
                        .url(url)
                        .get()
                        .build()
                    client.newCall(request).enqueue(object : Callback {
                        override fun onResponse(call: Call, response: Response) {
                            val string = response.body()?.string()?:""
                            it.resume(string)
                        }

                        override fun onFailure(call: Call, e: IOException) {
                            it.resumeWithException(e)
                        }
                    })
                } catch (e: Exception) {
                    it.resumeWithException(e)
                }
            }
        }

    }




}



