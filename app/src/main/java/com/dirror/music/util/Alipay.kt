package com.dirror.music.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import java.io.UnsupportedEncodingException
import java.net.URLEncoder

object Alipay {

    private const val URL_CODE = "https://qr.alipay.com/fkx15972kfa5unlvvfbfic5"

    //跳转到支付宝付款界面
    fun donate(context: Context) {
        val intentFullUrl = "intent://platformapi/startapp?saId=10000007&" +
        "clientVersion=3.7.0.0718&qrcode=https%3A%2F%2Fqr.alipay.com%2F${URL_CODE}%3F_s" +
                "%3Dweb-other&_t=1472443966571#Intent;" +
                "scheme=alipayqr;package=com.eg.android.AlipayGphone;end";
        try {
            val intent = Intent.parseUri(intentFullUrl, Intent.URI_INTENT_SCHEME)
            context.startActivity(intent)
        } catch (e: Exception) {
            // e.printStackTrace()
        }
    }

    fun jumpAlipay(context: Context) {
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            var uri: Uri? = null
            try {
                uri = Uri.parse("alipays://platformapi/startapp?saId=10000007&qrcode=" + URLEncoder.encode(URL_CODE, "UTF-8"))
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.data = uri
            context.startActivity(intent)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

}