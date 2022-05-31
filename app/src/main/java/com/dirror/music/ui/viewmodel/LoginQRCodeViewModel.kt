package com.dirror.music.ui.viewmodel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import androidx.annotation.Keep
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dirror.music.manager.User
import com.dirror.music.util.Api
import com.dirror.music.util.AppConfig
import com.dirror.music.util.toast
import kotlinx.coroutines.*


@Keep
class LoginQRCodeViewModel : ViewModel() {

    companion object {
        const val TAG = "LoginQRCodeViewModel"
    }

    var mQRCodeBitMutable = MutableLiveData<Bitmap>()
    var mStatusCodeMutable = MutableLiveData<Int>()

    private lateinit var key : String

    fun doLogin() {
        GlobalScope.launch {
             getQRCode()?.let {
                withContext(Dispatchers.Main) {
                    mQRCodeBitMutable.value = it
                }
            }
        }

    }

    private suspend fun getQRCode():Bitmap? {
        Api.getLoginKey()?.data?.unikey?.let { key ->
            this.key = key
            Api.getLoginQRCode(key)?.data?.qrimg?.let { imageStr ->
                if (imageStr.isNotEmpty()) {
                    try {
                        val start = "data:image/png;base64,"
                        if (imageStr.startsWith(start)) {
                            val sub = imageStr.substring(start.length)
                            val decodedString: ByteArray = Base64.decode(sub, Base64.DEFAULT)
                            return BitmapFactory.decodeByteArray(
                                decodedString,
                                0,
                                decodedString.size
                            )
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
        toast("获取二维码失败，请重试")
        return null
    }

    suspend fun checkLoginStatus() {
        var statusCode = 0
        do {
            Api.checkLoginResult(key)?.apply {
                Log.d(TAG, "login status code $code")
                statusCode = code
                if (code == 803) {
                    Log.i(TAG, "do login success, cookies is $cookie")
                    cookie?.let {
                        Api.getUserInfo(it)?.apply {
                            Log.i(TAG, "login success, $this")
                            account?.id?.let { uid ->
                                AppConfig.cookie = cookie
                                User.uid = uid
                            }
                        }
                    }

                }
                withContext(Dispatchers.Main) {
                    mStatusCodeMutable.value = code
                }
            }
            delay(5000)
        } while (statusCode == 801 || statusCode == 802)
    }


}