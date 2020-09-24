package com.dirror.music.service

import android.app.*
import android.content.Intent
import android.media.MediaMetadata
import android.media.MediaPlayer

import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat


import com.dirror.music.R
import com.dirror.music.api.StandardGET
import com.dirror.music.music.StandardSongData
import com.dirror.music.ui.activity.MainActivity
import com.dirror.music.util.StorageUtil
import com.dirror.music.util.loge
import com.dirror.music.util.parseArtist


const val CHANNEL_ID = "my notification id"

/**
 * 音乐服务
 * 传一个播放列表而不是一首歌
 */
class MusicService : Service() {
    companion object {
        const val MODE_CIRCLE = 1 // 列表循环
        const val MODE_REPEAT_ONE = 2 // 单曲循环
        const val MODE_RANDOM = 3 // 随机播放

        const val REQUEST_CODE_PREVIOUS = 1
    }

    private var mediaPlayer: MediaPlayer? = null // 定义 MediaPlayer
    private val musicBinder by lazy { MusicBinder() } // 懒加载 musicBinder

    var list: ArrayList<StandardSongData>? = null // 当前歌单
    var position: Int? = 0 // 当前歌曲在 List 中的下标
    var mode = StorageUtil.getInt(StorageUtil.PlAY_MODE, MODE_CIRCLE)



    override fun onCreate() {
        super.onCreate()
        // var token = MediaSessionCompat.Token()
        createChannel()



    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY // 非粘性服务
    }

    // 绑定
    override fun onBind(p0: Intent?): IBinder? {
        return musicBinder
    }

    fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel
            val name = "My notification 1"
            // val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            // mChannel.description = descriptionText
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

    }

    // 调用 Service 内部方法
    inner class MusicBinder : Binder(), MusicBinderInterface, MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {

        var isPrepared = false // 音乐是否准备完成

        /**
         * 设置播放歌单
         */
        override fun setPlaylist(songListData: ArrayList<StandardSongData>) {
            list = songListData
        }

        /**
         * 获取当前歌单全部
         */
        override fun getPlaylist(): ArrayList<StandardSongData>? {
            return list
        }

        /**
         * 播放音乐
         */
        override fun playMusic(songPosition: Int) {
            isPrepared = false
            position = songPosition
            // val songId = songList!![position!!].songs[0].id // 获取当前 position 的歌曲 id
            val songId = list!![position!!].id // 获取当前 position 的歌曲 id
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

//                StandardGET.getSongUrl(songId) { response ->
////                    try {
//                    loge("getSongUrl 回调结果：${response?:"null"}")
//                        it.setDataSource(response?:"https://music.163.com/song/media/outer/url?id=$songId.mp3")
//                        it.prepareAsync()
////                    } catch (e: Exception) {
////
////                    }
//                }
                it.setDataSource("https://music.163.com/song/media/outer/url?id=$songId.mp3")
                it.prepareAsync()
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

        /**
         * 准备完成
         */
        override fun onPrepared(p0: MediaPlayer?) {
            isPrepared = true
            refreshNotification()
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
            return list?.get(position!!)
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
            // 将播放模式存储
            StorageUtil.putInt(StorageUtil.PlAY_MODE, mode)
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
                MODE_RANDOM -> (0..list?.lastIndex!!).random()
                // 单曲循环或者歌单顺序播放
                else -> {
                    // 如果当前是第一首，就跳到最后一首播放
                    if (position == 0) {
                        list?.lastIndex
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
                MODE_RANDOM -> (0..list?.lastIndex!!).random()
                else -> {
                    position = if (position == list?.lastIndex) {
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
         * 外部请求发送广播
         */
        override fun sendBroadcast() {
            sendMusicBroadcast()
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
                    position = if (position == list?.lastIndex) {
                        0
                    } else {
                        position?.plus(1)
                    }
                }
                MODE_REPEAT_ONE -> {

                }
                MODE_RANDOM -> {
                    position = (0..list?.lastIndex!!).random()
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

    val pendingIntent by lazy { PendingIntent.getActivity(
        this,
        0,
        Intent(this, MainActivity::class.java),
        PendingIntent.FLAG_UPDATE_CURRENT
    ) }
    // 上一曲点击事件

    val pendingIntentPrevious by lazy { PendingIntent.getService(
        this,
        REQUEST_CODE_PREVIOUS,
        Intent(this, MusicService::class.java),
        PendingIntent.FLAG_UPDATE_CURRENT
    ) }

    /**
     * 刷新通知
     */
    private fun refreshNotification() {
        val song = musicBinder.getNowSongData()
        val mediaSession = MediaSessionCompat(this, "MusicService")
        mediaSession.setMetadata(
            MediaMetadataCompat.Builder()
                .putLong(MediaMetadata.METADATA_KEY_DURATION, (musicBinder.getDuration()?:0).toLong())
                .build()
        )
        mediaSession.setPlaybackState(
            PlaybackStateCompat.Builder()
                .setState(PlaybackStateCompat.STATE_PLAYING, musicBinder.getProgress().toLong(), 1f)
                .setActions(PlaybackStateCompat.ACTION_SEEK_TO)
                .build()
        )
        val mediaStyle = androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(mediaSession.sessionToken)
        if (song != null) {
            loge("歌曲图片url" + song.imageUrl)
            StandardGET.getSongBitmap(song.id) {
                val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_cloud_music)
                    .setContentTitle(song?.name)
                    .setContentText(song?.artists?.let { parseArtist(it) })
                    .setContentIntent(pendingIntent)
//                    .addAction(android.R.drawable.ic_media_previous, "Previous", pendingIntentPrevious)
                    .setStyle(mediaStyle)
                    .setLargeIcon(it)
                    .setAutoCancel(true)
//            .setStyle(object :androidx.media.app.NotificationCompat.MediaStyle()
//                .setMediaSession(token))
                    //.setMediaSession())
                    .build()
                // 开启前台服务
                startForeground(1, notification)
            }
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

