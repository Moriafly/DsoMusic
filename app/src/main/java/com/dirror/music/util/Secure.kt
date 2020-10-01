package com.dirror.music.util

import android.annotation.SuppressLint
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.pm.Signature
import com.dirror.music.MyApplication


object Secure {
    const val SIGNATURE = -1550371158
    private const val PACKAGE_NAME = "com.dirror.music"
    /**
     * 获取当前签名 Hash 值
     */
    @SuppressLint("PackageManagerGetSignatures")
    fun getSignature(): Int {
        val packageManager = MyApplication.context.packageManager
        var signature = 0
        try {
            val packageInfo = packageManager.getPackageInfo(PACKAGE_NAME, PackageManager.GET_SIGNATURES)
            val arrayOfSignature: Array<Signature> = packageInfo.signatures
            signature = arrayOfSignature[0].hashCode()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return signature
    }

    /**
     * 判断当前是否是 Debug 状态
     */
    fun isDebug(): Boolean {
        return try {
            val info: ApplicationInfo = MyApplication.context.getApplicationInfo()
            info.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
        } catch (e: java.lang.Exception) {
            false
        }
    }

}

/**
 * 签名校验
 */
object SignCheck {

}