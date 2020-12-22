package com.dirror.music.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dirror.music.MyApplication
import com.dirror.music.music.standard.data.SOURCE_NETEASE
import com.dirror.music.music.standard.data.SOURCE_QQ
import com.dirror.music.music.standard.data.StandardSongData
import com.dirror.music.util.toast

/**
 * PlayerActivity ViewModel
 * @author Moriafly
 */
class PlayerViewModel: ViewModel() {

    var rotation = 0f
    var rotationBackground = 0f
    var oldY = 0f

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

    var duration = MutableLiveData<Int>().also {
        it.value = MyApplication.musicBinderInterface?.getDuration()
    }

    var progress = MutableLiveData<Int>().also {
        it.value = MyApplication.musicBinderInterface?.getProgress()
    }

    /**
     * 刷新
     */
    fun refresh() {
        playMode.value = MyApplication.musicBinderInterface?.getPlayMode()
        if (standardSongData.value != MyApplication.musicBinderInterface?.getNowSongData()) {
            standardSongData.value = MyApplication.musicBinderInterface?.getNowSongData()
        }
        if (playState.value != MyApplication.musicBinderInterface?.getPlayState()) {
            playState.value = MyApplication.musicBinderInterface?.getPlayState()
        }
        if (duration.value != MyApplication.musicBinderInterface?.getDuration()) {
            duration.value = MyApplication.musicBinderInterface?.getDuration()
        }
    }

    /**
     * 刷新 progress
     */
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

    /**
     * 设置 progress
     */
    fun setProgress(newProgress: Int) {
        MyApplication.musicBinderInterface?.setProgress(newProgress)
    }

    /**
     * 喜欢音乐
     */
    fun likeMusic() {
        standardSongData.let {
            it.value?.let { song ->
                when (song.source) {
                    SOURCE_NETEASE -> {
                        MyApplication.cloudMusicManager.likeSong(song.id, {
                            toast("添加到我喜欢成功")
                        }, {
                            toast("添加到我喜欢失败")
                        })
                    }
                    SOURCE_QQ -> {
                        toast("暂不支持此音源")
                    }
                }
            }

        }
    }

}