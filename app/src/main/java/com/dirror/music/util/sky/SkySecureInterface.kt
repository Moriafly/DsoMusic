package com.dirror.music.util.sky

interface SkySecureInterface {

    /**
     * 字符串 md5 加密
     */
    fun getMD5(string: String): String
}