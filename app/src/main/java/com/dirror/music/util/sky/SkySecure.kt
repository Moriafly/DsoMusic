package com.dirror.music.util.sky

import android.content.Context
import java.io.IOException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

/**
 * SkySecure 安全防护
 * 字符串 MD5 加密、Xposed 监测、应用名称监测
 * Dex 文件完整性校验（配合联网校验）
 * @version 20210228
 * @author Moriafly
 * @since 2021 年 2 月 28 日
 */
object SkySecure: SkySecureInterface {

    fun getAppNameMd5(): String {
        return "9884b247104cbdb489aeeaca91f49584"
    }

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

    override fun checkDexIntegrity(context: Context, defaultCrc: Long): Boolean {
        return getDexCrc(context) == defaultCrc
    }

    override fun getDexCrc(context: Context): Long {
        try {
            val zipFile = ZipFile(context.packageCodePath)
            val zipEntry: ZipEntry = zipFile.getEntry("classes.dex")
            return zipEntry.crc
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return 0
    }

    infix fun Byte.and(mask: Int): Int = toInt() and mask

}