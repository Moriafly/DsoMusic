package com.dirror.music.service

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import com.dirror.music.App
import com.dirror.music.App.Companion.musicController
import com.dirror.music.music.standard.data.StandardSongData
import com.dirror.music.room.toSongList
import com.dirror.music.util.Config
import com.dirror.music.util.runOnMainThread
import kotlin.concurrent.thread

/**
 * 音乐服务连接
 */
class MusicServiceConnection : ServiceConnection {

    /**
     * 服务连接后
     */
    override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
        musicController.value = p1 as MusicService.MusicController
        thread {
            // 恢复 SongData
            val recoverSong = App.mmkv.decodeParcelable(Config.SERVICE_CURRENT_SONG, StandardSongData::class.java)
            val recoverProgress = App.mmkv.decodeInt(Config.SERVICE_RECOVER_PROGRESS, 0)
            val recoverPlayQueue = App.appDatabase.playQueueDao().loadAll().toSongList()
            recoverSong?.let { song ->
                // recover = true
                if (recoverSong in recoverPlayQueue) {
                    runOnMainThread {
                        musicController.value?.let {
                            it.setRecover(true)
                            it.setRecoverProgress(recoverProgress)
                            it.setPlaylist(recoverPlayQueue)
                            it.playMusic(song)
                        }
                    }
                }
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