package com.dirror.music.audio

import android.content.Context
import android.media.AudioManager
import com.dirror.music.MyApplication
import com.dirror.music.util.toast
import org.jetbrains.annotations.TestOnly

/**
 * 音量设置
 */
object VolumeManager {

    // 音频管理器
    private val audioManger = MyApplication.context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    // 获取最大媒体音
    val maxVolume = audioManger.getStreamMaxVolume(AudioManager.STREAM_MUSIC)

    // 获取当前系统音量模式
    val mode = audioManger.ringerMode

    /**
     * 设置音量
     */
    fun setStreamVolume(volume: Int) {
        audioManger.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0)
    }

    fun addVolume() {

    }

    // 获取当前媒体音
    fun getCurrentVolume(): Int {
        return audioManger.getStreamVolume(AudioManager.STREAM_MUSIC)
    }

}