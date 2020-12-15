package com.dirror.music.ui.viewmodel

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Message
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dirror.music.MyApplication
import com.dirror.music.music.standard.data.StandardSongData
import com.dirror.music.ui.activity.PlayActivity

class PlayerViewModel: ViewModel() {



    // 播放模式
    var playMode = MutableLiveData<Int>().also {
        it.value = MyApplication.musicBinderInterface?.getPlayMode()
    }

    // 当前歌曲信息
    var standardSongData = MutableLiveData<StandardSongData?>().also {
        it.value = MyApplication.musicBinderInterface?.getNowSongData()
    }

    var playState = MutableLiveData<Boolean>().also {
        it.value = MyApplication.musicBinderInterface?.getPlayState()
    }

    var progress = MutableLiveData<Int>().also {
        it.value = MyApplication.musicBinderInterface?.getProgress()
    }

    var duration = MutableLiveData<Int>().also {
        it.value = MyApplication.musicBinderInterface?.getDuration()
    }

    /**
     * 刷新
     */
    fun refresh() {
        playMode.value = MyApplication.musicBinderInterface?.getPlayMode()
        standardSongData.value = MyApplication.musicBinderInterface?.getNowSongData()
        playState.value = MyApplication.musicBinderInterface?.getPlayState()
        // progress.value = MyApplication.musicBinderInterface?.getProgress()
        duration.value = MyApplication.musicBinderInterface?.getDuration()
    }

    fun refreshProgress() {
        progress.value = MyApplication.musicBinderInterface?.getProgress()
    }

    /**
     * 改变播放状态
     */
    fun changePlayState() {
        val nowPlayState = MyApplication.musicBinderInterface?.getPlayState()?: false
        if (nowPlayState) {
            MyApplication.musicBinderInterface?.pause()
        } else {
            MyApplication.musicBinderInterface?.start()
        }
    }

    /**
     * 播放上一曲
     */
    fun playLast() {
        MyApplication.musicBinderInterface?.playLast()
    }

    /**
     * 播放下一曲
     */
    fun playNext() {
        MyApplication.musicBinderInterface?.playNext()
    }

    /**
     * 改变播放模式
     */
    fun changePlayMode() {
        MyApplication.musicBinderInterface?.changePlayMode()
    }

    fun setProgress(newProgress: Int) {
        MyApplication.musicBinderInterface?.setProgress(newProgress)
    }

}