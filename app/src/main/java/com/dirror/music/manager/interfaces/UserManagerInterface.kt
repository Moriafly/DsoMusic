package com.dirror.music.manager.interfaces

interface UserManagerInterface {
    /**
     * uid 是否登录
     */
    fun isUidLogin(): Boolean

    /**
     * 获取当前 uid
     */
    fun getCurrentUid(): Long

    fun setUid(uid: Long)

    fun getCloudMusicCookie(): String

    /**
     * 唯一可以更改 Cookie 的地方
     */
    fun setCloudMusicCookie(cookie: String)

    fun hasCookie(): Boolean
}