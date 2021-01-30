package com.dirror.music.ui.viewmodel

import android.graphics.Color
import androidx.annotation.Keep
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dirror.music.MyApplication
import com.dirror.music.audio.VolumeManager
import com.dirror.music.data.LyricViewData
import com.dirror.music.music.local.MyFavorite
import com.dirror.music.music.standard.SearchLyric
import com.dirror.music.music.standard.data.SOURCE_NETEASE
import com.dirror.music.music.standard.data.SOURCE_QQ
import com.dirror.music.music.standard.data.StandardSongData
import com.dirror.music.util.Config
import com.dirror.music.util.runOnMainThread
import com.dirror.music.util.toast

/**
 * PlayerActivity ViewModel
 * @author Moriafly
 */
@Keep
class PlayerViewModel: ViewModel() {

    companion object {
        val DEFAULT_COLOR = Color.rgb(90, 90, 90)
        // val DEFAULT_COLOR = Color.rgb(219, 44, 31) // 90, 90, 90
    }

    var rotation = 0f
    var rotationBackground = 0f
    var rotationTypeNormal = 0f

    // 播放模式
    var playMode = MutableLiveData<Int>().also {
        it.value = MyApplication.musicBinderInterface?.getPlayMode()
    }

    // 当前歌曲信息
    var standardSongData = MutableLiveData<StandardSongData?>().also {
        it.value = MyApplication.musicBinderInterface?.getNowSongData()
    }

    var playState = MutableLiveData<Boolean>().also {
        it.value = MyApplication.musicBinderInterface?.getPlayState()?: false
    }

    var duration = MutableLiveData<Int>().also {
        it.value = MyApplication.musicBinderInterface?.getDuration()
    }

    var progress = MutableLiveData<Int>().also {
        it.value = MyApplication.musicBinderInterface?.getProgress()
    }

    var lyricTranslation = MutableLiveData<Boolean>().also {
        it.value = MyApplication.mmkv.decodeBool(Config.LYRIC_TRANSLATION, true)
    }

    // 对内
    private var _lyricViewData = MutableLiveData<LyricViewData>().also {
        it.value = LyricViewData("", "")
    }

    var lyricViewData = MutableLiveData<LyricViewData>().also {
        it.value = LyricViewData("", "")
    }

    var currentVolume = MutableLiveData<Int>().also {
        it.value = VolumeManager.getCurrentVolume()
    }

    // 界面按钮颜色
    var color = MutableLiveData<Int>().also {
        it.value = Color.rgb(100, 100, 100)
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
            MyApplication.musicBinderInterface?.play()
        }
    }

    /**
     * 播放上一曲
     */
    fun playLast() {
        MyApplication.musicBinderInterface?.playPrevious()
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
        standardSongData.value?.let {
            MyFavorite.addSong(it)
        }
    }

    /**
     * 更新歌词
     */
    fun updateLyric() {
        // 更改歌词
        standardSongData.value?.let {
            if (it.source == SOURCE_NETEASE) {
                MyApplication.cloudMusicManager.getLyric(it.id.toLong()) { lyric ->
                    runOnMainThread {
                        _lyricViewData.value = LyricViewData(lyric.lrc?.lyric?:"", lyric.tlyric?.lyric?:"")
                        if (lyricTranslation.value == true) {
                            lyricViewData.value = _lyricViewData.value
                        } else {
                            lyricViewData.value = LyricViewData(_lyricViewData.value!!.lyric, "true")
                        }
                    }
                }
            } else {
                SearchLyric.getLyricString(it) { string ->
                    runOnMainThread {
                        _lyricViewData.value = LyricViewData(string, "")
                        lyricViewData.value = _lyricViewData.value
                    }
                }
            }
        }
    }

    /**
     * 设置歌词翻译
     */
    fun setLyricTranslation(open: Boolean) {
        lyricTranslation.value = open
        if (lyricTranslation.value == true) {
            lyricViewData.value = _lyricViewData.value
        } else {
            lyricViewData.value = LyricViewData(_lyricViewData.value!!.lyric, "true")
        }
        // updateLyric()
        MyApplication.mmkv.encode(Config.LYRIC_TRANSLATION, open)
    }

    /**
     * 音量加
     */
    fun addVolume() {
        currentVolume.value?.let {
            if (it < VolumeManager.maxVolume) {
                currentVolume.value = currentVolume.value!!.plus(1)
            } else {
                currentVolume.value = VolumeManager.maxVolume
            }
            VolumeManager.setStreamVolume(currentVolume.value!!)
        }
    }

    /**
     * 音量减
     */
    fun reduceVolume() {
        currentVolume.value?.let {
            if (it > 0) {
                currentVolume.value = currentVolume.value!!.minus(1)
            } else {
                currentVolume.value = 0
            }
            VolumeManager.setStreamVolume(currentVolume.value!!)
        }
    }

}