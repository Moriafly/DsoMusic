package com.dirror.music.util

import android.annotation.SuppressLint
import android.app.Application
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build.VERSION
import com.dirror.music.MyApp
import org.jetbrains.annotations.TestOnly

/**
 * 安全防护
 * @版本 1.0.2
 * @更新时间 2021/1/3
 * @author Moriafly
 * @since 2020/10/4
 */
@SuppressLint("StaticFieldLeak")
object Secure {

    private const val PACKAGE_NAME = "com.dirror.music" // 包名

    private const val SKIP_DEBUG_MODE = true // Debug 模式下是否跳过安全检查

    val context = MyApp.context

    private fun getSignatureHash(): Int {
        return -1550371158
    }

    /**
     * 主要方法
     * 是否安全
     */
    fun isSecure(): Boolean {
        if (isDebug()) {
            // toast("DEBUG 模式")
            if (SKIP_DEBUG_MODE) {
                // toast("跳过安全检查")
                return true
            }
        }
        // 签名错误
        if (!isSignatureCorrect()) {
            return false
        }
        // 检查抓包
        if (!checkProxy()) {
            return false
        }

        return true
    }

    /**
     * 签名检查
     */
    private fun isSignatureCorrect(): Boolean {
        // 获取当前签名 Hash 值
        val signature = getSignature()
        return signature == getSignatureHash()
    }

    /**
     * 获取当前签名 Hash 值
     */
    @SuppressLint("PackageManagerGetSignatures")
    private fun getSignature(): Int {
        val packageManager = MyApp.context.packageManager
        var signature = 0
        try {
            signature = if (VERSION.SDK_INT >= 28) {
                val packageInfo: PackageInfo = packageManager.getPackageInfo(PACKAGE_NAME, PackageManager.GET_SIGNING_CERTIFICATES)
                val arrayOfSignature = packageInfo.signingInfo.apkContentsSigners
                arrayOfSignature[0].hashCode()
            } else {
                val packageInfo: PackageInfo = packageManager.getPackageInfo(PACKAGE_NAME, PackageManager.GET_SIGNATURES)
                val arrayOfSignature = packageInfo.signatures
                arrayOfSignature[0].hashCode()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return signature
    }

    /**
     * 判断当前是否是 Debug 状态
     */
    fun isDebug(): Boolean {
        return context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
    }

    /**
     * 杀死自己
     * 适用于 MyApplication
     */
    fun killMyself() {
        android.os.Process.killProcess(android.os.Process.myPid())
    }

    /**
     * 检查 App
     */
    @TestOnly
    private fun isApplicationCorrect(): Boolean {
        val nowApplication = Application()
        val trueApplicationName = "MyApplication"
        val nowApplicationName = nowApplication.javaClass.simpleName
        return trueApplicationName == nowApplicationName
    }

    /**
     * 防抓包，检查是否有代理
     */
    private fun checkProxy(): Boolean {
        return System.getProperty("http.proxyHost") == null && System.getProperty("http.proxyPort")  == null
    }

}

