package com.dirror.music.service

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import com.dirror.music.MyApplication
import com.dirror.music.MyApplication.Companion.musicController
import com.dirror.music.music.standard.data.StandardSongData
import com.dirror.music.util.Config
import com.dirror.music.util.toast

/**
 * 音乐服务连接
 */
class MusicServiceConnection : ServiceConnection {

    /**
     * 服务连接后
     */
    override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
        toast("服务链接")
        musicController.value = p1 as MusicControllerInterface
        // 恢复 SongData
        val recoverSong = MyApplication.mmkv.decodeParcelable(Config.SERVICE_CURRENT_SONG, StandardSongData::class.java)
        val recoverProgress = MyApplication.mmkv.decodeInt(Config.SERVICE_RECOVER_PROGRESS, 0)
        recoverSong?.let { song ->
            // recover = true
            musicController.value?.let {
                it.setRecover(true)
                it.setRecoverProgress(recoverProgress)
                it.setPlaylist(arrayListOf(song))
                it.playMusic(song)
            }
        }
    }

    /**
     * 服务意外断开连接
     */
    override fun onServiceDisconnected(p0: ComponentName?) {
        musicController.value = null
    }

}