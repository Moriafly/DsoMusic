package com.dirror.music.util.sky

import android.content.Context

/**
 * SkySecure 接口
 */
interface SkySecureInterface {

    /**
     * 字符串 md5 加密
     */
    fun getMD5(string: String): String

    /**
     * 检查 Dex 完整性
     */
    fun checkDexIntegrity(context: Context, defaultCrc: Long): Boolean

    /**
     * 获取 dex crc
     */
    fun getDexCrc(context: Context): Long
}