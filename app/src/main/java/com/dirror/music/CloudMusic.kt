package com.dirror.music

import android.net.UrlQuerySanitizer
import android.util.Log
import com.dirror.music.cloudmusic.LoginData
import com.dirror.music.util.MagicHttp
import com.dirror.music.util.toast
import com.google.gson.Gson
import java.net.URL

/**
 * 网易云 api
 * @author Moriafly
 * @since 2020年9月14日15:07:36
 */

object CloudMusic {
    // api 地址
    private const val MUSIC_API_URL = "https://musicapi.leanapp.cn"


    fun loginByPhone(phone: String, password: String) {
        MagicHttp.OkHttpManager().get(
            "$MUSIC_API_URL/login/cellphone?phone=$phone&password=$password",
            object : MagicHttp.MagicCallBack {
                override fun success(response: String) {
                    Log.e("返回数据", response)
                    // 成功
                    // 解析 json
                    val loginData = Gson().fromJson(response, LoginData::class.java)
                    // 登录成功
                    MagicHttp.runOnMainThread {
                        if (loginData.code == 200) {
                            toast("登录成功\n用户名：${loginData.profile.nickname}")
                        } else {
                            toast("登录失败\n错误代码：${loginData.code}")
                        }
                    }
                }

                override fun failure(throwable: Throwable) {
                    Log.e("错误", throwable.message.toString())
                }
            })
    }

    fun loginByEmail(email: String, password: String) {

    }
}