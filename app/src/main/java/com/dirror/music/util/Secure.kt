package com.dirror.music.util

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.content.pm.Signature
import com.dirror.music.MyApplication

object Secure {
    const val SIGNATURE = -1550371158

    @SuppressLint("PackageManagerGetSignatures")
    fun getSignature(packageName: String): Int {
        val packageManager = MyApplication.context.packageManager
        var signature = 0
        try {
            val packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
            val arrayOfSignature: Array<Signature> = packageInfo.signatures
            signature = arrayOfSignature[0].hashCode()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return signature
    }

}

/**
 * 签名校验
 */
object SignCheck {

}