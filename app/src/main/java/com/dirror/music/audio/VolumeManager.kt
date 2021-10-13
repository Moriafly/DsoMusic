package com.dirror.music.audio

import android.content.Context
import android.media.AudioManager
import com.dirror.music.App

/**
 * 音量管理
 * @author Moriafly
 * @since 2021年1月8日19:52:46
 */
object VolumeManager {

    // 音频管理器
    private val audioManger = App.context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    // 获取最大媒体音
    val maxVolume = audioManger.getStreamMaxVolume(AudioManager.STREAM_MUSIC)

    // 获取当前系统音量模式
    // val mode = audioManger.ringerMode

    /**
     * 传入 [volume] 设置音量
     */
    fun setStreamVolume(volume: Int) {
        audioManger.setStreamVolume(AudioManager.STREAM_MUSIC, volume, AudioManager.FLAG_SHOW_UI)
    }

    /**
     * 获取当前媒体音
     * @return 当前音量
     */
    fun getCurrentVolume(): Int {
        return audioManger.getStreamVolume(AudioManager.STREAM_MUSIC)
    }

}