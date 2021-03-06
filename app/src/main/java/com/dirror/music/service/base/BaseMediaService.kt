package com.dirror.music.service.base

import android.media.MediaPlayer
import androidx.media.MediaBrowserServiceCompat

/**
 * 抽象类
 */
abstract class BaseMediaService: MediaBrowserServiceCompat() {

    /**
     * MediaPlayer
     */
    var mediaPlayer: MediaPlayer? = null

    override fun onCreate() {
        super.onCreate()
        initChannel()
        initMediaSession()
        initAudioFocus()
    }

    /**
     * 初始化媒体会话 MediaSession
     */
    open fun initMediaSession() {

    }

    open fun initChannel() {

    }

    /**
     * 初始化音频焦点
     */
    open fun initAudioFocus() {

    }

    companion object {
        const val TAG = "MusicService"

        const val MODE_CIRCLE = 1 // 列表循环
        const val MODE_REPEAT_ONE = 2 // 单曲循环
        const val MODE_RANDOM = 3 // 随机播放

        const val CODE_PREVIOUS = 1 // 按钮事件，上一曲
        const val CODE_PLAY = 2 // 按钮事件，播放或者暂停
        const val CODE_NEXT = 3 // 按钮事件，下一曲

        const val CHANNEL_ID = "Dso Music Channel Id" // 通知通道 ID
        const val START_FOREGROUND_ID = 10 // 开启前台服务的 ID
    }

}