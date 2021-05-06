package com.dirror.music.ui.main.viewmodel

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

    val navigationBarHeight = MutableLiveData<Int>().also {
        it.value = 0
    }

    val userId =  MutableLiveData<Long>().also {
        it.value = MyApplication.userManager.getCurrentUid()
    }

    var neteaseLiveVisibility = MutableLiveData(false)

    // 句子推荐可见性
    var sentenceVisibility = MutableLiveData(true)

    /**
     * 设置用户 id
     */
    fun setUserId() {
        userId.value = MyApplication.userManager.getCurrentUid()
    }

    fun updateUI() {
        neteaseLiveVisibility.value = MyApplication.mmkv.decodeBool(Config.USER_NETEASE_CLOUD_MUSIC_API_ENABLE, false)
        singleColumnPlaylist.value = MyApplication.mmkv.decodeBool(Config.SINGLE_COLUMN_USER_PLAYLIST, false)
        sentenceVisibility.value = MyApplication.mmkv.decodeBool(Config.SENTENCE_RECOMMEND, true)
    }

}