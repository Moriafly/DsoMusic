package com.dirror.music.ui.main.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dirror.music.MyApp
import com.dirror.music.manager.User
import com.dirror.music.util.Config

/**
 * MainActivity ViewModel
 */
class MainViewModel: ViewModel() {

    /** 状态栏高度 */
    val statusBarHeight = MutableLiveData<Int>()

    /** 导航栏高度 */
    val navigationBarHeight = MutableLiveData<Int>()

    /**
     * 用户 id
     */
    val userId =  MutableLiveData<Long>().also {
        it.value = User.uid
    }

    /**
     * 网易登录后才可见
     */
    val neteaseLiveVisibility = MutableLiveData<Boolean>().also {
        it.value = MyApp.mmkv.decodeBool(Config.USER_NETEASE_CLOUD_MUSIC_API_ENABLE, false)
    }

    /**
     * 句子推荐可见性
     */
    var sentenceVisibility = MutableLiveData<Boolean>().also {
        it.value = MyApp.mmkv.decodeBool(Config.SENTENCE_RECOMMEND, true)
    }

    /**
     * 设置用户 id
     */
    fun setUserId() {
        userId.value = User.uid
    }

    /**
     * 刷新 UI
     */
    fun updateUI() {
        neteaseLiveVisibility.value = MyApp.mmkv.decodeBool(Config.USER_NETEASE_CLOUD_MUSIC_API_ENABLE, false)
        sentenceVisibility.value = MyApp.mmkv.decodeBool(Config.SENTENCE_RECOMMEND, true)
    }

}