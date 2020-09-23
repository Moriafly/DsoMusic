package com.dirror.music.service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.dirror.music.api.StandardGET
import com.dirror.music.cloudmusic.SongData
import com.dirror.music.music.StandardSongData
import com.dirror.music.util.loge
import com.dirror.music.util.toast

class MusicService : Service() {
    // 传一个播放列表而不是一首歌
    companion object {
        const val MODE_CIRCLE = 1 // 列表循环
        const val MODE_REPEAT_ONE = 2 // 单曲循环
        const val MODE_RANDOM = 3 // 随机播放
    }

    private var mediaPlayer: MediaPlayer? = null
    private val musicBinder by lazy { MusicBinder() } // 懒加载 musicBinder

    var songList: ArrayList<StandardSongData>? = null // 当前歌单
    var position: Int? = 0 // 当前歌曲在 List 中的下标

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
    inner class MusicBinder : Binder(), MusicBinderInterface, MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {

        var isPrepared = false // 音乐是否准备完成

        /**
         * 设置播放歌单
         */
        override fun setPlaylist(songListData: ArrayList<StandardSongData>) {
            songList = songListData
        }

        /**
         * 获取当前歌单全部
         */
        override fun getPlaylist(): ArrayList<StandardSongData>? {
            return songList
        }

        /**
         * 播放音乐
         */
        override fun playMusic(songPosition: Int) {
            isPrepared = false
            position = songPosition
            // val songId = songList!![position!!].songs[0].id // 获取当前 position 的歌曲 id
            val songId = songList!![position!!].id // 获取当前 position 的歌曲 id
            // Log.e("音乐服务 songId：", songId.toString())

            // 如果 MediaPlayer 已经存在，释放
            if (mediaPlayer != null) {
                mediaPlayer?.reset()
                mediaPlayer?.release()
                mediaPlayer = null
            }

            mediaPlayer = MediaPlayer() // 初始化
            mediaPlayer?.let {
                // setDataSource("https://api.fczbl.vip/163/?type=url&id=$songId")
                it.setOnPreparedListener(this@MusicBinder) // 歌曲准备完成的监听
                it.setOnCompletionListener(this@MusicBinder) // 歌曲完成后的回调
                it.setOnErrorListener(this@MusicBinder)

                StandardGET.getSongUrl(songId) { response ->
//                    try {
                    loge("getSongUrl 回调结果：${response?:"null"}")
                        it.setDataSource(response?:"https://music.163.com/song/media/outer/url?id=$songId.mp3")
                        it.prepareAsync()
//                    } catch (e: Exception) {
//
//                    }
                }
                // it.setDataSource("https://music.163.com/song/media/outer/url?id=$songId.mp3")

            }
            // https://api.fczbl.vip/163/?type=url&id=186016
            // https://music.163.com/song/media/outer/url?id=186016.mp3

        }

        /**
         * 发送广播
         */
        private fun sendMusicBroadcast() {
            // Service 通知
            val intent = Intent("com.dirror.music.MUSIC_BROADCAST")
            intent.setPackage(packageName)
            sendBroadcast(intent)
        }

        override fun onPrepared(p0: MediaPlayer?) {
            isPrepared = true
            // p0?.prepare()
            // 播放音乐，通知界面更新
            p0?.apply {
                start()
            }
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
            return mediaPlayer?.isPlaying ?: false
        }

        /**
         * 获取总进度
         */
        override fun getDuration(): Int? {
            // getDuration必须在prepared回调完成后才可以调用。
            return if (isPrepared) {
                mediaPlayer?.duration ?: 0
            } else {
                null
            }
        }

        /**
         * 获取当前进度
         */
        override fun getProgress(): Int {
            return mediaPlayer?.currentPosition ?: 0
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
        override fun getNowSongData(): StandardSongData? {
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
            sendMusicBroadcast()
        }

        /**
         * 获取当前播放模式
         */
        override fun getPlayMode(): Int {
            return mode
        }

        /**
         * 播放上一曲
         */
        override fun playLast() {
            // 设置 position
            position = when (mode) {
                MODE_RANDOM -> (0..songList?.lastIndex!!).random()
                // 单曲循环或者歌单顺序播放
                else -> {
                    // 如果当前是第一首，就跳到最后一首播放
                    if (position == 0) {
                        songList?.lastIndex
                    } else {
                        // 否则播放上一首
                        position?.minus(1)
                    }
                }
            }
            // position 非空，调用播放方法
            position?.let { playMusic(it) }
        }

        /**
         * 播放下一曲
         */
        override fun playNext() {
            when (mode) {
                MODE_RANDOM -> (0..songList?.lastIndex!!).random()
                else -> {
                    position = if (position == songList?.lastIndex) {
                        0
                    } else {
                        position?.plus(1)
                    }
                }
            }
            position?.let { playMusic(it) }
        }

        /**
         * 获取当前 position
         */
        override fun getNowPosition(): Int {
            return position?:-1
        }

        /**
         * 获取 AudioSessionId，用于音效
         */
        override fun getAudioSessionId(): Int {
            return mediaPlayer?.audioSessionId?:0
        }

        /**
         * 外部请求广播
         */
        override fun sendBroadcast() {
            sendMusicBroadcast()
        }

        /**
         * 歌曲完成后的回调，自动播放下一曲
         */
        override fun onCompletion(p0: MediaPlayer?) {
            // 下面两句解决 MediaPlayerNative: error (-38, 0) 问题
            // https://blog.csdn.net/u014520745/article/details/46672327
//            mediaPlayer?.pause()
//            mediaPlayer?.seekTo(0)
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
            playMusic(position ?: 0)
        }

        override fun onError(p0: MediaPlayer?, p1: Int, p2: Int): Boolean {
//            toast("错误")
            // playNext()
            return true
        }

    }
}

interface MusicBinderInterface {
    fun setPlaylist(songListData: ArrayList<StandardSongData>)
    fun getPlaylist(): ArrayList<StandardSongData>?
    fun playMusic(songPosition: Int)
    fun changePlayState()
    fun getPlayState(): Boolean
    fun getDuration(): Int?
    fun getProgress(): Int
    fun setProgress(newProgress: Int)
    fun getNowSongData(): StandardSongData?
    fun changePlayMode()
    fun getPlayMode(): Int
    fun playLast()
    fun playNext()
    fun getNowPosition():Int
    fun getAudioSessionId():Int
    fun sendBroadcast()
}