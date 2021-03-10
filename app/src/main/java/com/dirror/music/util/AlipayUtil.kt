package com.dirror.music.util

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.view.Gravity
import android.view.View
import android.widget.TextView
import android.widget.Toast

/**
 * Created by codeest on 16/10/17. https://github.com/fython/AlipayZeroSdk/blob/master/library/src/main/java/moe/feng/alipay/zerosdk/AlipayZeroSdk.java
 */
object AlipayUtil {
    // 支付宝包名
    private const val ALIPAY_PACKAGE_NAME = "com.eg.android.AlipayGphone"

    // 旧版支付宝二维码通用 Intent Scheme Url 格式
    private const val INTENT_URL_FORMAT = "intent://platformapi/startapp?saId=10000007&" +
            "clientVersion=3.7.0.0718&qrcode=https%3A%2F%2Fqr.alipay.com%2F{urlCode}%3F_s" +
            "%3Dweb-other&_t=1472443966571#Intent;" +
            "scheme=alipayqr;package=com.eg.android.AlipayGphone;end"

    /**
     * 打开转账窗口
     *
     * @param activity Parent Activity
     * @return 是否成功调用
     */
    fun startAlipayClient(activity: Activity): Boolean {
        return startIntentUrl(
            activity,
            INTENT_URL_FORMAT.replace("{urlCode}", "fkx15972kfa5unlvvfbfic5")
        )
    }

    /**
     * 打开 Intent Scheme Url
     *
     * @param activity      Parent Activity
     * @param intentFullUrl Intent 跳转地址
     * @return 是否成功调用
     */
    private fun startIntentUrl(activity: Activity, intentFullUrl: String): Boolean {
        return try {
            activity.startActivity(Intent.parseUri(intentFullUrl, Intent.URI_INTENT_SCHEME))
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * 判断支付宝客户端是否已安装，建议调用转账前检查
     * @param context Context
     * @return 支付宝客户端是否已安装
     */
    private fun hasInstalledAlipayClient(context: Context): Boolean {
        val pm = context.packageManager
        return try {
            val info = pm.getPackageInfo(ALIPAY_PACKAGE_NAME, 0)
            info != null
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            false
        }
    }
}