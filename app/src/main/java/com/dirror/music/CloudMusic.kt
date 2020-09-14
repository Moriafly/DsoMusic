package com.dirror.music

import android.net.UrlQuerySanitizer
import com.dirror.music.util.MagicHttp
import java.net.URL

/**
 * 网易云 api
 * @author Moriafly
 * @since 2020年9月14日15:07:36
 */

object CloudMusic {
    // api 地址
    private const val MUSIC_API_URL = "http://musicapi.leanapp.cn"


    fun loginByPhone(phone: String, password: String) {
        MagicHttp.OkHttpManager().get("$MUSIC_API_URL/login/cellphone?phone=$phone&password=$password", object : MagicHttp.MagicCallBack {
            override fun success(response: String) {
                // 成功
            }

            override fun failure(throwable: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

    fun loginByEmail(email: String, password: String) {

    }
}