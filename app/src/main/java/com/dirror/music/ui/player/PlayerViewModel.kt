package com.dirror.music.ui.player

import android.graphics.Color
import androidx.annotation.Keep
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dirror.music.App
import com.dirror.music.audio.VolumeManager
import com.dirror.music.data.LyricViewData
import com.dirror.music.music.local.MyFavorite
import com.dirror.music.music.standard.SearchLyric
import com.dirror.music.music.standard.data.SOURCE_NETEASE
import com.dirror.music.service.base.BaseMediaService
import com.dirror.music.util.Config
import com.dirror.music.util.runOnMainThread

/**
 * PlayerActivity ViewModel
 * @author Moriafly
 */
@Keep
class PlayerViewModel: ViewModel() {

    companion object {
        val DEFAULT_COLOR = Color.rgb(90, 90, 90)
        lateinit var fragmentManager : FragmentManager
    }

    var rotation = 0f
    var rotationBackground = 0f

    /** 状态栏高度 */
    var navigationBarHeight = MutableLiveData<Int>()

    // 播放模式
    var playMode = MutableLiveData<Int>().also {
        it.value = App.musicController.value?.getPlayMode() ?: BaseMediaService.MODE_CIRCLE
    }

    var duration = MutableLiveData<Int>().also {
        it.value = App.musicController.value?.getDuration() ?: 0
    }

    var progress = MutableLiveData<Int>().also {
        it.value = App.musicController.value?.getProgress() ?: 0
    }

    var lyricTranslation = MutableLiveData<Boolean>().also {
        it.value = App.mmkv.decodeBool(Config.LYRIC_TRANSLATION, true)
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
     * 当前歌曲是否被点红心，
     * 若有，实心，保持红色
     * 若无，空心，跟随封面变色
     */
    val heart = MutableLiveData<Boolean>()

    /** 平常颜色 */
    var normalColor = MutableLiveData<Int>()

    /**
     * 刷新
     */
    fun refresh() {
        playMode.value = App.musicController.value?.getPlayMode()
        if (duration.value != App.musicController.value?.getDuration()) {
            duration.value = App.musicController.value?.getDuration()
        }
    }

    /**
     * 刷新 progress
     */
    fun refreshProgress() {
        progress.value = App.musicController.value?.getProgress()
    }

    /**
     * 改变播放状态
     */
    fun changePlayState() {
        val nowPlayState = App.musicController.value?.isPlaying()?.value?: false
        if (nowPlayState) {
            App.musicController.value?.pause()
        } else {
            App.musicController.value?.play()
        }
    }

    /**
     * 播放上一曲
     */
    fun playLast() {
        App.musicController.value?.playPrevious()
    }

    /**
     * 播放下一曲
     */
    fun playNext() {
        App.musicController.value?.playNext()
    }

    /**
     * 改变播放模式
     */
    fun changePlayMode() {
        App.musicController.value?.changePlayMode()
    }

    /**
     * 设置 progress
     */
    fun setProgress(newProgress: Int) {
        App.musicController.value?.setProgress(newProgress)
    }

    /**
     * 喜欢音乐
     * true
     */
    fun likeMusic(success: (Boolean) -> Unit) {
        App.musicController.value?.getPlayingSongData()?.value?.let {
            MyFavorite.isExist(it) { exist ->
                if (exist) {
                    MyFavorite.deleteById(it.id?:"")
                    success.invoke(false)
                } else {
                    MyFavorite.addSong(it)
                    success.invoke(true)
                }
            }
        }
    }

    /**
     * 更新歌词
     */
    fun updateLyric() {
        // 更改歌词
        App.musicController.value?.getPlayingSongData()?.value?.let {
            if (it.source == SOURCE_NETEASE) {
                App.cloudMusicManager.getLyric(it.id?.toLong()?:0) { lyric ->
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
        App.mmkv.encode(Config.LYRIC_TRANSLATION, open)
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

    /**
     * 获取模式的提示
     */
    fun getModeContentDescription(mode: Int): String {
        return when (mode) {
            BaseMediaService.MODE_CIRCLE -> "当前是列表循环模式，点击切换为单曲循环"
            BaseMediaService.MODE_REPEAT_ONE -> "当前是单曲循环模式，点击切换为随机播放"
            BaseMediaService.MODE_RANDOM -> "当前是随机播放模式，点击切换为列表循环"
            else -> ""
        }
    }

}