package com.dirror.music.service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.dirror.music.cloudmusic.SongData

class MusicService: Service() {
    // 传一个播放列表而不是一首歌

    private var mediaPlayer: MediaPlayer? = null
    private val musicBinder by lazy { MusicBinder() } // 懒加载 musicBinder

    var songList: List<SongData>? = null
    var position : Int? = 0

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // intent 获取
//        val bundle = intent?.extras
//
//        songList = bundle?.getParcelableArrayList<SongData>("SONG_LIST")
//        position = bundle?.getInt("SONG_POSITION")

        // musicBinder.playMusic()
        return START_NOT_STICKY

    }

    // 绑定
    override fun onBind(p0: Intent?): IBinder? {
        return musicBinder
    }

    // 调用 Service 内部方法
    inner class MusicBinder: Binder(), MusicBinderInterface, MediaPlayer.OnPreparedListener {
        override fun setPlaylist(songListData: List<SongData>) {
            songList = songListData
        }

        override fun playMusic(songPosition: Int) {
            position = songPosition
            val songId = songList!![position!!].songs[0].id
            Log.e("音乐服务 songId：", songId.toString())

            // 如果 MediaPlayer 已经存在，释放
            if (mediaPlayer != null) {
                mediaPlayer?.reset()
                mediaPlayer?.release()
                mediaPlayer = null
            }

            mediaPlayer = MediaPlayer() // 初始化
            mediaPlayer?.apply {
                // setDataSource("https://api.fczbl.vip/163/?type=url&id=$songId")
                setDataSource("https://music.163.com/song/media/outer/url?id=$songId.mp3")
                prepareAsync()
                setOnPreparedListener(this@MusicBinder)
            }
            // https://api.fczbl.vip/163/?type=url&id=186016
            // https://music.163.com/song/media/outer/url?id=186016.mp3

        }

        private fun updateUi() {
            val song = songList!![position!!].songs[0]
            var artist = ""
            for (artistName in 0..song.ar.lastIndex) {
                if (artistName != 0) {
                    artist += " / "
                }
                artist += song.ar[artistName].name
            }

            // Service 通知 Activity
            val intent = Intent("com.dirror.music.MUSIC_BROADCAST")
            val name: String = song.name
            intent.putExtra("string_song_name", name)
            intent.putExtra("string_song_artist", artist)
            intent.putExtra("string_song_pic", song.al.picUrl)
            intent.setPackage(packageName)
            sendBroadcast(intent)
        }

        override fun onPrepared(p0: MediaPlayer?) {
            // 播放音乐，通知界面更新
            p0?.start()
            updateUi()
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

        override fun getPlayState(): Boolean {
            return if (mediaPlayer != null) {
                mediaPlayer!!.isPlaying
            } else {
                false
            }
        }

        /**
         * 获取总进度
         */
        override fun getDuration(): Int {
            return mediaPlayer?.duration?:0
        }

        /**
         * 获取当前进度
         */
        override fun getProgress(): Int {
            return mediaPlayer?.currentPosition?:0
        }

        /**
         * 设置进度
         */
        override fun setProgress(newProgress: Int) {
            mediaPlayer?.seekTo(newProgress)
        }

        /**
         * 获取当前播放的音乐的信息
         */
        override fun getNowSongData(): SongData? {
            return songList?.get(position!!)
        }

    }
}

interface MusicBinderInterface {
    fun setPlaylist(songListData: List<SongData>)
    fun playMusic(songPosition: Int)
    fun updatePlayState()
    fun getPlayState(): Boolean
    fun getDuration(): Int
    fun getProgress(): Int
    fun setProgress(newProgress: Int)
    fun getNowSongData(): SongData?
}