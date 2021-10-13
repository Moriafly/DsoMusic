package com.dirror.music.music.netease

import com.dirror.music.api.API_AUTU
import com.dirror.music.manager.User
import com.dirror.music.music.netease.data.PersonFMData
import com.dirror.music.music.netease.data.toSongList
import com.dirror.music.music.standard.data.StandardSongData
import com.dirror.music.util.ErrorCode
import com.dirror.music.util.MagicHttp
import com.google.gson.Gson
import okhttp3.FormBody
import kotlin.Exception

/**
 * 私人 FM 单例
 * @author Moriafly
 * @since 2021年2月19日20:40:15
 */
object PersonalFM {

    private const val API = "https://music.163.com/api/v1/radio/get"
    private const val TEST_API = "$API_AUTU/personal_fm"

    /**
     * 获取 私人 FM
     * 失败的回调 [failure]
     */
    fun get(success: (ArrayList<StandardSongData>) -> Unit, failure: (Int) -> Unit) {
        val requestBody = FormBody.Builder()
            .add("crypto", "weapi")
            .add("cookie", User.cookie)
            .add("withCredentials", "true")
            .add("realIP", "211.161.244.70")
            .build()
        MagicHttp.OkHttpManager().newPost(TEST_API, requestBody, {
            try {
                val personFMData = Gson().fromJson(it, PersonFMData::class.java)
                success(personFMData.toSongList())
            } catch (e: Exception) {
               failure(ErrorCode.ERROR_JSON)
            }
        }, {
            failure(ErrorCode.ERROR_MAGIC_HTTP)
        })
    }

}