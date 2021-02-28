package com.dirror.music.util.sky

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/**
 * SkySecure
 * 安全防护
 */
object SkySecure: SkySecureInterface {

    external fun getAppNameMd5(): String

    external fun checkXposed(): Boolean

    override fun getMD5(string: String): String {
        if (string.isEmpty()) {
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

    infix fun Byte.and(mask: Int): Int = toInt() and mask

}