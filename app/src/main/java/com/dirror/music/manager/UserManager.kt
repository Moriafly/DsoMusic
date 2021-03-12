package com.dirror.music.manager

import com.dirror.music.MyApplication
import com.dirror.music.manager.interfaces.UserManagerInterface
import com.dirror.music.util.Config

/**
 * user 管理，面向本地
 * @注意 不能写请求网络功能，网络的写在音乐管理中
 */
class UserManager: UserManagerInterface {

    companion object {
        const val defaultUid = 0L // 默认 0L，可设置一个默认用户
        const val DEFAULT_COOKIE = ""
    }

    override fun isUidLogin(): Boolean {
        val uid = MyApplication.mmkv.decodeLong(Config.UID, defaultUid)
        return uid != defaultUid
    }

    override fun getCurrentUid(): Long {
        return MyApplication.mmkv.decodeLong(Config.UID, defaultUid)
    }

    override fun setUid(uid: Long) {
        MyApplication.mmkv.encode(Config.UID, uid)
    }

    override fun getCloudMusicCookie(): String {
        return MyApplication.mmkv.decodeString(Config.CLOUD_MUSIC_COOKIE, DEFAULT_COOKIE)
    }

    /**
     * 设置网易云音乐用户 Cookie
     */
    override fun setCloudMusicCookie(cookie: String) {
        MyApplication.mmkv.encode(Config.CLOUD_MUSIC_COOKIE, cookie)
    }

    override fun hasCookie(): Boolean {
        return MyApplication.userManager.getCloudMusicCookie().isNotEmpty()
    }

}