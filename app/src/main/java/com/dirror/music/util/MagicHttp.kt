package com.dirror.music.util

import android.os.Handler
import android.os.Looper
import okhttp3.*
import java.io.IOException
import java.lang.Exception
import java.util.concurrent.TimeUnit

// 单例
object MagicHttp {

    interface MagicHttpInterface {
        fun get(url: String, callBack: MagicCallback)
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
    }


}

