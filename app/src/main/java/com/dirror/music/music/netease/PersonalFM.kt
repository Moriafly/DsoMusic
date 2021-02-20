package com.dirror.music.music.netease

import com.dirror.music.MyApplication
import com.dirror.music.util.MagicHttp
import com.dirror.music.util.loge
import okhttp3.FormBody

/**
 * 私人 FM 单例
 * @author Moriafly
 * @since 2021年2月19日20:40:15
 */
object PersonalFM {

    private const val API = "https://music.163.com/api/v1/radio/get"
    private const val TEST_API = "https://cloudmusic.moriafly.xyz/personal_fm"

    /**
     * 获取 私人 FM
     * 失败的回调 [failure]
     */
    fun get(failure: () -> Unit) {
        val requestBody = FormBody.Builder()
            .add("crypto", "weapi")
            .add("cookie", MyApplication.userManager.getCloudMusicCookie())
            .add("withCredentials", "true")
            .add("realIP", "211.161.244.70")
            .build()
        MagicHttp.OkHttpManager().newPost(API, requestBody) {
            loge(it, "FM")
        }
    }

}