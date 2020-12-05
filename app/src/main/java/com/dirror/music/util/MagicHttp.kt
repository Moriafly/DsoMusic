package com.dirror.music.util

import android.os.Handler
import android.os.Looper
// import com.cv4j.proxy.ProxyPool
import com.dirror.music.MyApplication
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Proxy
import java.util.concurrent.TimeUnit


// 单例
object MagicHttp {

    interface MagicHttpInterface {
        @Deprecated("get() 过时，请使用 newGet()")
        fun get(url: String, callBack: MagicCallback)

        fun newGet(url: String, success: (String) -> Unit, failure: (String) -> Unit) // 新的 get 请求接口，使用 Lambda
        fun post(url: String, json: String, success: (String) -> Unit)
    }

    /**
     * 回调接口
     */
    interface MagicCallback {
        fun success(response: String)
        fun failure(throwable: Throwable)
    }

    // 运行在主线程，更新 UI
    fun runOnMainThread(runnable: Runnable) {
        Handler(Looper.getMainLooper()).post(runnable)
    }

    /**
     * 可用多个，不是单例类
     * 继承 MagicHttpInterface，拥有 get 方法
     */
    class OkHttpManager: MagicHttpInterface {

        override fun get(url: String, callBack: MagicCallback) {
            try {
                val client = OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS) //设置连接超时时间
                    .readTimeout(20, TimeUnit.SECONDS) //设置读取超时时间
                    .build()
                val request = Request.Builder()
                    .url(url)
                    // .addHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 7.0; Windows 7)")
                    // .addHeader("X-Real-IP", "211.161.244.70")
                    .get()
                    .build()
                client.newCall(request).enqueue(object : Callback {
                    override fun onResponse(call: Call, response: Response) {
                        val string = response.body?.string()!!
                        callBack.success(string)
                    }

                    override fun onFailure(call: Call, e: IOException) {
                        callBack.failure(e)
                    }
                })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // lambda 表达式版，简化代码，增加了 cookie
        // private val cookieStore: HashMap<String, List<Cookie>> = HashMap()
        override fun newGet(url: String, success: (String) -> Unit, failure: (String) -> Unit) {
            try {
                val client = OkHttpClient.Builder()
                    .connectTimeout(5, TimeUnit.SECONDS)
                    .readTimeout(3, TimeUnit.SECONDS)
                    .writeTimeout(3, TimeUnit.SECONDS)
                        // 跨域 cookie 可能会报错，如将网易云的 cookie 提交给 QQ，这里先禁用了
//                    .cookieJar(object : CookieJar {
//                        override fun loadForRequest(url: HttpUrl): List<Cookie> {
//                            val cookies = MyApplication.cookieStore[url.host]
//                            return cookies ?: ArrayList()
//                        }
//
//                        // 保存 cookie
//                        override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
//                            // MyApplication.mmkv.encode("1", cookies)
//                            MyApplication.cookieStore[url.host] = cookies
//                        }
//
//                    })
                    .build()
                val request = Request.Builder()
                    .url(url)
                    .addHeader("Referer", "https://y.qq.com/portal/player.html")
                    // .addHeader("Cookie", "pgv_pvi=3690785792; pgv_pvid=5976631272; eas_sid=x1K5i9P3q9u4G4H3H9j3M1r8N5; RK=wzYIHclnZy; ptcz=b292331cdc5be024b104b3d5f7ebf54283aafa75e76ddb22db00979596fb8ccb; uin_cookie=o1515390445; ied_qq=o1515390445; LW_sid=D175a9N4T0o8c5J7R481d9z2u8; LW_uid=e1F5t9b4S0i8I5n7l4K1H9R3d4; tvfe_boss_uuid=58182f7b3e8cbdbf; o_cookie=1515390445; pac_uid=1_1515390445; iip=0; psrf_qqaccess_token=EEDA039F8F50E86A00B88C39A3782B86; psrf_qqrefresh_token=96B9C26A8B78AB7137EB537CA282B069; psrf_qqopenid=F97A3B4DE3591B549AA1F748F8CB78D8; euin=oK457KoqoevP7v**; psrf_qqunionid=; tmeLoginType=2; pgv_info=ssid=s472287960; pgv_si=s9264856064; _qpsvr_localtk=0.17452595660376136; psrf_musickey_createtime=1604821434; uin=1515390445; qqmusic_key=Q_H_L_2eRed_50eStwGuHDFXBZLRNm5wxPVsPdpiyqxNLL-4GQMAnlWT3CAUGN--qIax3; psrf_access_token_expiresAt=1612597434; qm_keyst=Q_H_L_2eRed_50eStwGuHDFXBZLRNm5wxPVsPdpiyqxNLL-4GQMAnlWT3CAUGN--qIax3; qqmusic_fromtag=66")
                    .addHeader(
                        "User-Agent",
                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.183 Safari/537.36 Edg/86.0.622.63"
                    )
                    // .addHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 7.0; Windows 7)") // 添加 Header
                    .get()
                    .build()

                client.newCall(request).enqueue(object : Callback {
                    override fun onResponse(call: Call, response: Response) {
                        val string = response.body?.string()!!
                        success.invoke(string)
                    }

                    override fun onFailure(call: Call, e: IOException) {
                        toast("网络连接错误")
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
                // val JSON: MediaType = "application/json; charset=utf-8".toMediaType()
                // val proxyPool = ProxyPool.getProxy()

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
                    // .proxy(Proxy(Proxy.Type.HTTP, InetSocketAddress(proxyPool.ip, proxyPool.port)))
                    .build()

                val body = okhttp3.FormBody.Builder()
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
                        // toast("MagicHttp 错误")
                        // failure.invoke(e.message.toString())
                    }
                })
                // val body = okhttp3.FormBody.Builder().add("")
//                val client = OkHttpClient.Builder()
//                    .connectTimeout(5, TimeUnit.SECONDS)
//                    .readTimeout(3, TimeUnit.SECONDS)
//                    .writeTimeout(3, TimeUnit.SECONDS)
//                    .cookieJar(object : CookieJar {
//                        override fun loadForRequest(url: HttpUrl): List<Cookie> {
//                            val cookies = MyApplication.cookieStore[url.host]
//                            return cookies ?: ArrayList()
//                        }
//
//                        override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
//                            MyApplication.cookieStore[url.host] = cookies
//                        }
//
//                    })
//                    .build()
//                val body = FromBody
            } catch (e: Exception) {
                e.printStackTrace()
                // failure.invoke(e.message.toString())
            }
        }

    }




}



