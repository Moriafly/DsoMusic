package com.dirror.music.manager

import com.dirror.music.MyApp
import com.dirror.music.manager.interfaces.UserManagerInterface
import com.dirror.music.util.Config

/**
 * user 管理，面向本地
 * @注意 不能写请求网络功能，网络的写在音乐管理中
 */
@Deprecated("过时，使用 User 类")
class UserManager: UserManagerInterface {

    companion object {
        const val defaultUid = 0L // 默认 0L，可设置一个默认用户
        const val DEFAULT_COOKIE = ""
    }

    override fun isUidLogin(): Boolean {
        val uid = MyApp.mmkv.decodeLong(Config.UID, defaultUid)
        return uid != defaultUid
    }

    override fun getCurrentUid(): Long {
        return MyApp.mmkv.decodeLong(Config.UID, defaultUid)
    }

    override fun setUid(uid: Long) {
        MyApp.mmkv.encode(Config.UID, uid)
    }

    override fun getCloudMusicCookie(): String {
        return MyApp.mmkv.decodeString(Config.CLOUD_MUSIC_COOKIE, DEFAULT_COOKIE)
    }

    /**
     * 设置网易云音乐用户 Cookie
     */
    override fun setCloudMusicCookie(cookie: String) {
        MyApp.mmkv.encode(Config.CLOUD_MUSIC_COOKIE, cookie)
    }

    override fun hasCookie(): Boolean {
        return MyApp.userManager.getCloudMusicCookie().isNotEmpty()
    }

    /**
     * 获取用户配置的 NeteaseCloudMusicApi
     */
    fun getUserNeteaseCloudMusicApi(): String {
        return MyApp.mmkv.decodeString(Config.USER_NETEASE_CLOUD_MUSIC_API_URL, "")
    }

}