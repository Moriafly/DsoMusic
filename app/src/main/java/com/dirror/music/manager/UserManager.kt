package com.dirror.music.manager

import com.dirror.music.MyApplication
import com.dirror.music.util.Config

class UserManager: UserManagerInterface {

    private val defaultUid = 0L // 默认 0L，可设置一个默认用户

    override fun isUidLogin(): Boolean {
        val uid = MyApplication.mmkv.decodeLong(Config.UID, defaultUid)
        return uid != defaultUid
    }

    override fun getCurrentUid(): Long {
        return MyApplication.mmkv.decodeLong(Config.UID, defaultUid)
    }

}