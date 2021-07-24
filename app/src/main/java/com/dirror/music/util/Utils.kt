package com.dirror.music.util

import android.os.Build
import androidx.annotation.RequiresApi
import java.security.MessageDigest
import java.util.*
import java.util.Base64.getDecoder
import kotlin.collections.HashMap
import kotlin.experimental.xor


object Utils {

    fun toMap(vararg params: String): Map<String, String> {
        val map = HashMap<String, String>()
        var i = 0
        while (i < params.size) {
            map[params[i]] = params[i + 1]
            i += 2
        }
        return map
    }

    private val byte1 = "3go8&$8*3*3h0k(2)2".toByteArray()

    fun getNeteasePicUrl(id: Long): String {
        val byte2 = id.toString().toByteArray()
        for (i in byte2.indices) {
            byte2[i] = byte2[i] xor byte1[i % byte1.size]
        }
        val m = MD5.getMD5Code(byte2)
        var result = Base64.encode(m!!)
        result = result.replace("/", "_").replace("+", "-")
        return "https://p3.music.126.net/$result/$id.jpg"
    }

}