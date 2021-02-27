package com.dirror.music.ui.viewmodel

import android.text.TextUtils
import androidx.annotation.Keep
import androidx.lifecycle.ViewModel
import com.dirror.music.MyApplication
import com.dirror.music.api.API_DEFAULT
import com.dirror.music.music.netease.data.UserDetailData
import com.dirror.music.util.ErrorCode
import com.dirror.music.util.MagicHttp
import com.google.gson.Gson
import okhttp3.FormBody
import okhttp3.internal.and
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

@Keep
class LoginCellphoneViewModel: ViewModel() {

    /**
     * 手机号登录
     */
    fun loginByCellphone(api: String,phone: String, password: String, success: (UserDetailData) -> Unit, failure: (Int) -> Unit) {
        val passwordMD5 = getMD5(password)
        val requestBody = FormBody.Builder()
            .add("phone", phone)
            .add("countrycode", "86")
            .add("md5_password", passwordMD5)
            .build()
        MagicHttp.OkHttpManager().newPost("${api}/login/cellphone", requestBody) {
            try {
                val userDetail = Gson().fromJson(it, UserDetailData::class.java)
                if (userDetail.code != 200) {
                    failure.invoke(userDetail.code)
                } else {
                    userDetail.cookie?.let { it1 -> MyApplication.userManager.setCloudMusicCookie(it1) }
                    MyApplication.userManager.setUid(userDetail.profile.userId)
                    success.invoke(userDetail)
                }
            } catch (e: Exception) {
                failure.invoke(ErrorCode.MAGIC_HTTP_ERROR)
            }
        }
    }

    /**
     * 字符串 md5 加密
     */
    private fun getMD5(string: String): String {
        if (TextUtils.isEmpty(string)) {
            return ""
        }
        try {
            val md5 = MessageDigest.getInstance("MD5")
            val bytes: ByteArray = md5.digest(string.toByteArray())
            val result = StringBuilder()
            for (b in bytes) {
                var temp = Integer.toHexString(b and 0xff)
                if (temp.length == 1) {
                    temp = "0$temp"
                }
                result.append(temp)
            }
            return result.toString()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        return ""
    }

}