package com.dirror.music.service

import androidx.lifecycle.MutableLiveData
import com.dirror.music.music.standard.data.StandardSongData

/**
 * 音乐 Binder 接口
 * MyApplication.musicBinderInterface 是音乐服务外部唯一管理者
 */
interface MusicControllerInterface {

    /**
     * 设置播放列表（歌单）
     */
    fun setPlaylist(songListData: ArrayList<StandardSongData>)

    /**
     * 获取当前歌单
     */
    fun getPlaylist(): ArrayList<StandardSongData>?

    /**
     * 播放歌单指定位置 [songPosition] 歌曲
     */
    fun playMusic(songPosition: Int)

    /**
     * 改变播放状态
     * 取消过时，继续用，也可以用 [play] 或者 [pause]
     */
    fun changePlayState()

    /**
     * 获取播放状态
     * @return true 正在播放，false 暂停
     * @新 [isPlaying]
     */
    @Deprecated("过时方法")
    fun getPlayState(): Boolean

    /**
     * 是否正在播放
     * @return true 正在播放，false 暂停
     */
    fun isPlaying(): MutableLiveData<Boolean>

    /**
     * 获取当前歌曲时长
     */
    fun getDuration(): Int

    /**
     * 获取当前播放进度
     */
    fun getProgress(): Int

    /**
     * 设置播放进度
     */
    fun setProgress(newProgress: Int)

    /**
     * 获取当前播放歌曲
     * @新 [getPlayingSongData]
     */
    @Deprecated("过时方法，请使用 getPlayingSongData()")
    fun getNowSongData(): StandardSongData?

    /**
     * 新版获取 LiveData 正在播放歌曲
     */
    fun getPlayingSongData(): MutableLiveData<StandardSongData?>

    /**
     * 改变当前播放模式
     */
    fun changePlayMode()

    /**
     * 获取当前播放模式
     */
    fun getPlayMode(): Int

    /**
     * 播放上一首
     */
    fun playPrevious()

    /**
     * 播放下一首
     */
    fun playNext()

    /**
     * 获取当前歌曲在歌单中的位置
     */
    fun getNowPosition(): Int

    /**
     * 获取音频 Session ID
     */
    fun getAudioSessionId(): Int

    /**
     * 请求服务主动发送广播
     */
    fun sendBroadcast()

    /**
     * 设置播放速度
     */
    fun setSpeed(speed: Float)

    /**
     * 获取播放速度
     */
    fun getSpeed(): Float

    /**
     * 获取音高等级
     */
    fun getPitchLevel(): Int

    /**
     * 升调
     */
    fun increasePitchLevel()

    /**
     * 降调
     */
    fun decreasePitchLevel()

    /**
     * 开始播放
     */
    fun play()

    /**
     * 暂停播放
     */
    fun pause()

    /**
     * 下一首播放
     */
    fun addToNextPlay(standardSongData: StandardSongData)

    /**
     * 设置是否开启音频焦点
     * @param status true 开启 false 关闭
     */
    fun setAudioFocus(status: Boolean)

    fun updateTag(tag: String?)

}