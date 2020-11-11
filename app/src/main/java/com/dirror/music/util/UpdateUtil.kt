package com.dirror.music.util

import com.google.gson.Gson
import java.lang.Exception

object UpdateUtil {

    /**
     * 检查服务器版本
     */
    fun getServerVersion(success: (UpdateData) -> Unit, failure: () -> Unit) {
        val url = "https://moriafly.xyz/dirror-music/update.json"
        MagicHttp.OkHttpManager().newGet(url, {
            // 成功
            try {
                success.invoke(Gson().fromJson(it, UpdateData::class.java))
            } catch (e: Exception) {
                failure.invoke()
            }
        }, {

        })
    }

    data class UpdateData(
        val name: String,
        val code: Int,
        val content: String,
        val url: String, // 下载链接
    )

}