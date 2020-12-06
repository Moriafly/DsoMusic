package com.dirror.music.manager

interface UserManagerInterface {
    /**
     * uid 是否登录
     */
    fun isUidLogin(): Boolean

    /**
     * 获取当前 uid
     */
    fun getCurrentUid(): Long
}