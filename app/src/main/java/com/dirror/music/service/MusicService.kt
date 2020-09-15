package com.dirror.music.service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder

class MusicService: Service() {
    // 传一个播放列表而不是一首歌

    private var mediaPlayer: MediaPlayer? = null
    private val musicBinder by lazy { MusicBinder() } // 懒加载 musicBinder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // intent 获取

        musicBinder.playMusic()
        return START_NOT_STICKY

    }

    // 绑定
    override fun onBind(p0: Intent?): IBinder? {
        return musicBinder
    }

    // 调用 Service 内部方法
    inner class MusicBinder: Binder(), MusicBinderInterface, MediaPlayer.OnPreparedListener {

        fun playMusic() {
            mediaPlayer = MediaPlayer() // 初始化
            mediaPlayer?.apply {
                setDataSource("https://api.fczbl.vip/163/?type=url&id=1303523637")
                prepareAsync()
                setOnPreparedListener(this@MusicBinder)
            }
            // https://api.fczbl.vip/163/?type=url&id=186016
            // https://music.163.com/song/media/outer/url?id=186016.mp3

        }

        override fun onPrepared(p0: MediaPlayer?) {
            p0?.start()
        }

        override fun updatePlayState() {
            val isPlaying = mediaPlayer?.isPlaying
            isPlaying?.apply {
                if (this) {
                    mediaPlayer?.pause()
                } else {
                    mediaPlayer?.start()
                }
            }
        }

    }
}

interface MusicBinderInterface {
    fun updatePlayState()
}