package com.dirror.music.ui.viewmodel

import androidx.annotation.Keep
import androidx.lifecycle.ViewModel
import com.dirror.music.MyApp
import com.dirror.music.music.netease.data.UserDetailData
import com.dirror.music.util.ErrorCode
import com.dirror.music.util.MagicHttp
import com.dirror.music.util.sky.SkySecure
import com.google.gson.Gson
import okhttp3.FormBody

@Keep
class LoginCellphoneViewModel : ViewModel() {

    /**
     * 手机号登录
     */
    fun loginByCellphone(
        api: String,
        phone: String,
        password: String,
        success: (UserDetailData) -> Unit,
        failure: (Int) -> Unit
    ) {
        val passwordMD5 = SkySecure.getMD5(password)
        val requestBody = FormBody.Builder()
            .add("phone", phone)
            .add("countrycode", "86")
            .add("md5_password", passwordMD5)
            .build()
        MagicHttp.OkHttpManager().newPost("${api}/login/cellphone", requestBody, {
            try {
                val userDetail = Gson().fromJson(it, UserDetailData::class.java)
                if (userDetail.code != 200) {
                    failure.invoke(userDetail.code)
                } else {
                    userDetail.cookie?.let { it1 -> MyApp.userManager.setCloudMusicCookie(it1) }
                    MyApp.userManager.setUid(userDetail.profile.userId)
                    success.invoke(userDetail)
                }
            } catch (e: Exception) {
                failure.invoke(ErrorCode.ERROR_MAGIC_HTTP)
            }
        }, {
            failure(ErrorCode.ERROR_MAGIC_HTTP)
        })
    }


}