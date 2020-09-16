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
    companion object {
        const val MODE_CIRCLE = 1 // 列表循环
        const val MODE_REPEAT_ONE = 2 // 单曲循环
        const val MODE_RANDOM = 3 // 随机播放
    }

    private var mediaPlayer: MediaPlayer? = null
    private val musicBinder by lazy { MusicBinder() } // 懒加载 musicBinder

    var songList: List<SongData>? = null // 当前歌单
    var position : Int? = 0 // 当前歌曲在 List 中的下标

    var mode = MODE_CIRCLE // 当前模式

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
    inner class MusicBinder: Binder(), MusicBinderInterface, MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener {
        override fun setPlaylist(songListData: List<SongData>) {
            songList = songListData
        }

        /**
         * 播放音乐
         */
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
                setOnPreparedListener(this@MusicBinder) // 歌曲准备完成的监听
                setOnCompletionListener(this@MusicBinder)
            }
            // https://api.fczbl.vip/163/?type=url&id=186016
            // https://music.163.com/song/media/outer/url?id=186016.mp3

        }

        private fun sendMusicBroadcast() {
            // Service 通知
            val intent = Intent("com.dirror.music.MUSIC_BROADCAST")
            intent.setPackage(packageName)
            sendBroadcast(intent)
        }

        override fun onPrepared(p0: MediaPlayer?) {
            // 播放音乐，通知界面更新
            p0?.start()
            sendMusicBroadcast()
        }

        /**
         * 更新播放状态
         */
        override fun changePlayState() {
            val isPlaying = mediaPlayer?.isPlaying
            isPlaying?.apply {
                if (this) {
                    mediaPlayer?.pause()
                } else {
                    mediaPlayer?.start()
                }
            }
            sendMusicBroadcast()
        }

        /**
         * 获取当前播放状态
         */
        override fun getPlayState(): Boolean {
            return mediaPlayer?.isPlaying?:false
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

        /**
         * 改变播放模式
         */
        override fun changePlayMode() {
            when (mode) {
                MODE_CIRCLE -> mode = MODE_REPEAT_ONE
                MODE_REPEAT_ONE -> mode = MODE_RANDOM
                MODE_RANDOM -> mode = MODE_CIRCLE
            }
        }

        /**
         * 获取当前播放模式
         */
        override fun getPlayMode(): Int {
            return mode
        }

        /**
         * 歌曲完成后的回调，自动播放下一曲
         */
        override fun onCompletion(p0: MediaPlayer?) {
            autoPlayNext()
        }

        /**
         * 根据播放模式自动播放下一曲
         */
        private fun autoPlayNext() {
            when (mode) {
                MODE_CIRCLE -> {
                    position = if (position == songList?.lastIndex) {
                        0
                    } else {
                        position?.plus(1)
                    }
                }
                MODE_REPEAT_ONE -> {

                }
                MODE_RANDOM -> {
                    position = (0..songList?.lastIndex!!).random()
                }
            }
            playMusic(position?:0)
        }

    }
}

interface MusicBinderInterface {
    fun setPlaylist(songListData: List<SongData>)
    fun playMusic(songPosition: Int)
    fun changePlayState()
    fun getPlayState(): Boolean
    fun getDuration(): Int
    fun getProgress(): Int
    fun setProgress(newProgress: Int)
    fun getNowSongData(): SongData?
    fun changePlayMode()
    fun getPlayMode(): Int
}