package com.dirror.music.util

import android.annotation.SuppressLint
import android.content.Context
import java.security.MessageDigest


object Encrypt {
    fun stringToHash(string: String): ByteArray? {
        var hashed: ByteArray? = null

            val saltedPassword = (string + 2048).toByteArray()
            val sha1 = MessageDigest.getInstance("SHA-1").digest(saltedPassword)
            val md5 = MessageDigest.getInstance("MD5").digest(saltedPassword)
            hashed = (sha1.toHex() + md5.toHex()).toByteArray()

        return hashed
    }

    /**
     * 获取设备 SALT
     */
    @SuppressLint("PrivateApi")
    fun getSalt(): String {
        val clazz1 = Class.forName("com.android.internal.widget.LockPatternUtils")
        val lockUtils = clazz1.getConstructor(Context::class.java).newInstance(this)
        val lockUtilsClazz: Class<*> = lockUtils.javaClass
        val getSaltM = lockUtilsClazz.getDeclaredMethod("getSalt", Int::class.javaPrimitiveType)
        getSaltM.isAccessible = true
        val saltObj = getSaltM.invoke(lockUtils, 0)
        loge("salt 代码：" + saltObj.toString())
        return saltObj.toString()
    }

}

object HexUtil {

    /**
     * 十六进制String转换成Byte[]
     * @param hexString the hex string
     * *
     * @return byte[]
     */
    fun hexStringToBytes(hexString: String?): ByteArray? {
        var hexString = hexString
        if (hexString == null || hexString == "") {
            return null
        }
        hexString = hexString.toUpperCase()
        val length = hexString.length / 2
        val hexChars = hexString.toCharArray()
        val d = ByteArray(length)
        for (i in 0..length - 1) {
            val pos = i * 2
            d[i] = (charToByte(hexChars[pos]).toInt() shl 4 or charToByte(hexChars[pos + 1]).toInt()).toByte()
        }
        return d
    }

    /**
     * Convert char to byte
     * @param c char
     * *
     * @return byte
     */
    private fun charToByte(c: Char): Byte {

        return "0123456789ABCDEF".indexOf(c).toByte()
    }

    /* 这里我们可以将byte转换成int，然后利用Integer.toHexString(int)来转换成16进制字符串。
        * @param src byte[] data
        * @return hex string
        */
    fun bytesToHexString(src: ByteArray?): String? {
        val stringBuilder = StringBuilder("")
        if (src == null || src.isEmpty()) {
            return null
        }
        for (element in src) {
            val v = element.toInt() and 0xFF
            val hv = Integer.toHexString(v)
            if (hv.length < 2) {
                stringBuilder.append(0)
            }
            stringBuilder.append(hv)
        }
        return stringBuilder.toString()
    }


}




