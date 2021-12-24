package com.dirror.music.music.netease

import android.util.Log
import com.dirror.music.api.API_AUTU
import com.dirror.music.manager.User
import com.dirror.music.music.netease.data.UserCloudData
import com.dirror.music.util.ErrorCode
import com.dirror.music.util.MagicHttp
import com.google.gson.Gson
import okhttp3.FormBody

/**
 * 用户云盘数据
 */
object UserCloud {

    private const val TAG = "UserCloud"

    private const val API = "https://music.163.com/api/v1/cloud/get"
    private const val TEST_API = "${API_AUTU}/user/cloud"

    fun getUserCloud(offset: Int, success: (UserCloudData) -> Unit, failure: (Int) -> Unit) {
        val requestBody = FormBody.Builder()
            .add("crypto", "api")
            .add("cookie", User.cookie)
            .add("withCredentials", "true")
            .add("realIP", "211.161.244.70")
            .add("limit", "50")
            .add("offset", "$offset")
            .build()
        var api = User.neteaseCloudMusicApi
        if (api.isEmpty()) {
            api = "https://olbb.vercel.app"
        }
        MagicHttp.OkHttpManager().newPost("${api}/user/cloud", requestBody, {
            // Log.e(TAG, "getUserCloud: $it", )
            try {
                val userCloudData = Gson().fromJson(it, UserCloudData::class.java)
                success.invoke(userCloudData)
            } catch (e: Exception) {
                failure.invoke(ErrorCode.ERROR_JSON)
            }
        }, {

        })
    }

}