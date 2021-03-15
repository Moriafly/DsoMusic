package com.dirror.music.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dirror.music.MyApplication
import com.dirror.music.util.Config

class MainViewModel: ViewModel() {

    var singleColumnPlaylist = MutableLiveData<Boolean>().also {
        it.value = false
    }

    // 状态栏高度
    val statusBarHeight = MutableLiveData<Int>().also {
        it.value = 0
    }

    val userId =  MutableLiveData<Long>().also {
        it.value = MyApplication.userManager.getCurrentUid()
    }

    /**
     * 设置用户 id
     */
    fun setUserId() {
        userId.value = MyApplication.userManager.getCurrentUid()
    }

    fun updateUI() {
        singleColumnPlaylist.value = MyApplication.config.mmkv.decodeBool(Config.SINGLE_COLUMN_USER_PLAYLIST, false)
    }

}