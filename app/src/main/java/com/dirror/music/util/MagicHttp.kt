package com.dirror.music.util

import android.os.Handler
import android.os.Looper
import okhttp3.*
import java.io.IOException
import java.lang.Exception
// 单例
object MagicHttp {

    interface MagicHttpInterface {
        fun get(url: String, callBack: MagicCallBack)
    }

    /**
     * 回调接口
     */
    interface MagicCallBack {
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

        override fun get(url: String, callBack: MagicCallBack) {
            try {
                val client = OkHttpClient()
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

