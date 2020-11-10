package com.dirror.music.util

import com.google.gson.Gson

object UpdateUtil {

    /**
     * 检查服务器版本
     */
    fun getServerVersion(success: (UpdateData) -> Unit) {
        val url = "https://moriafly.xyz/dirror-music/update.json"
        MagicHttp.OkHttpManager().newGet(url, {
            // 成功
            success.invoke(Gson().fromJson(it, UpdateData::class.java))
        }, {

        })
    }

    data class UpdateData(
        val name: String,
        val code: Int,
        val content: String
    )

}