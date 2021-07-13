package com.dirror.music.ui.main.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dirror.music.MyApp
import com.dirror.music.manager.User
import com.dirror.music.util.Config

class MainViewModel: ViewModel() {

    var singleColumnPlaylist = MutableLiveData<Boolean>().also {
        it.value = false
    }

    /** 状态栏高度 */
    val statusBarHeight = MutableLiveData<Int>()

    /** 导航栏高度 */
    val navigationBarHeight = MutableLiveData<Int>()

    val userId =  MutableLiveData<Long>().also {
        it.value = User.uid
    }

    var neteaseLiveVisibility = MutableLiveData(false)

    // 句子推荐可见性
    var sentenceVisibility = MutableLiveData(true)

    /**
     * 设置用户 id
     */
    fun setUserId() {
        userId.value = User.uid
    }

    fun updateUI() {
        neteaseLiveVisibility.value = MyApp.mmkv.decodeBool(Config.USER_NETEASE_CLOUD_MUSIC_API_ENABLE, false)
        singleColumnPlaylist.value = MyApp.mmkv.decodeBool(Config.SINGLE_COLUMN_USER_PLAYLIST, false)
        sentenceVisibility.value = MyApp.mmkv.decodeBool(Config.SENTENCE_RECOMMEND, true)
    }

}