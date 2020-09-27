package com.dirror.music.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
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
import com.dirror.music.MyApplication
import com.dirror.music.R
import com.dirror.music.api.StandardGET
import com.dirror.music.music.StandardSongData
import com.dirror.music.ui.activity.MainActivity
import com.dirror.music.ui.activity.PlayActivity
import com.dirror.music.util.StorageUtil
import com.dirror.music.util.loge
import com.dirror.music.util.parseArtist

/**
 * 音乐服务
 * @author Wu Shihao
 * @since 2020/9
 */
class MusicService : Service() {
    companion object {
        const val MODE_CIRCLE = 1 // 列表循环
        const val MODE_REPEAT_ONE = 2 // 单曲循环
        const val MODE_RANDOM = 3 // 随机播放

        const val CODE_PREVIOUS = 1 // 按钮事件，上一曲
        const val CODE_PLAY = 2 // 按钮事件，播放或者暂停
        const val CODE_NEXT = 3 // 按钮事件，下一曲

        const val CHANNEL_ID = "my notification id" // 通知通道 ID
    }

    private var mediaPlayer: MediaPlayer? = null // 定义 MediaPlayer
    private val musicBinder by lazy { MusicBinder() } // 懒加载 musicBinder
    private var playlist: ArrayList<StandardSongData>? = null // 当前歌单
    private var position: Int? = 0 // 当前歌曲在 List 中的下标
    private var mode = StorageUtil.getInt(StorageUtil.PlAY_MODE, MODE_CIRCLE)
    private var notificationManager: NotificationManager? = null // 通知管理

    private var mediaSessionCallback: MediaSessionCompat.Callback? = null
    private var mediaSession: MediaSessionCompat? = null

    override fun onCreate() {
        super.onCreate()
        mediaSession = MediaSessionCompat(this, "MusicService")
        initMediaSessionCallback()
        // 初始化通道
        initChannel()

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    /**
     * 初始化 MediaSession 回调
     */
    private fun initMediaSessionCallback() {
        mediaSessionCallback = object : MediaSessionCompat.Callback() {
            override fun onPlay() {
                mediaSession?.setPlaybackState(
                    PlaybackStateCompat.Builder()
                        .setState(
                            PlaybackStateCompat.STATE_PLAYING,
                            (MyApplication.musicBinderInterface?.getProgress() ?: 0).toLong(),
                            1f
                        )
                        .setActions(PlaybackStateCompat.ACTION_SEEK_TO)
                        .build()
                )
            }

            override fun onPause() {
                mediaSession?.setPlaybackState(
                    PlaybackStateCompat.Builder()
                        .setState(
                            PlaybackStateCompat.STATE_PAUSED,
                            (MyApplication.musicBinderInterface?.getProgress() ?: 0).toLong(),
                            1f
                        )
                        .setActions(PlaybackStateCompat.ACTION_SEEK_TO)
                        .build()
                )
            }

            override fun onSkipToNext() {
                musicBinder.playNext()
            }

            override fun onSkipToPrevious() {
                // AudioPlayer.get().prev()
            }

            override fun onStop() {
                // AudioPlayer.get().stopPlayer()
            }

            override fun onSeekTo(pos: Long) {
                mediaPlayer?.seekTo(pos.toInt())
                onPlay()
            }

        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val code = intent?.getIntExtra("int_code", 0)
        loge("通知 code = $code")
        when (code) {
            CODE_PREVIOUS -> {
                loge("通知上一曲")
                musicBinder.playLast()
            }
            CODE_PLAY -> {
                loge("通知播放或暂停")
                musicBinder.changePlayState()
            }
            CODE_NEXT -> {
                loge("通知下一曲")
                musicBinder.playNext()
            }
        }
        // refreshNotification()
        return START_NOT_STICKY // 非粘性服务
    }

    // 绑定
    override fun onBind(p0: Intent?): IBinder? {
        return musicBinder
    }

    private fun initChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel
            val name = "My notification 1"
            // val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            // mChannel.description = descriptionText
            notificationManager?.createNotificationChannel(channel)
        }
    }

    /**
     * 内部类
     * MusicBinder
     */
    inner class MusicBinder : Binder(), MusicBinderInterface, MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {

        private var isPrepared = false // 音乐是否准备完成

        /**
         * 设置播放歌单
         */
        override fun setPlaylist(songListData: ArrayList<StandardSongData>) {
            playlist = songListData
        }

        /**
         * 获取当前歌单全部
         */
        override fun getPlaylist(): ArrayList<StandardSongData>? {
            return playlist
        }

        /**
         * 播放音乐
         */
        override fun playMusic(songPosition: Int) {
            isPrepared = false
            position = songPosition
            // 获取当前 position 的歌曲 id
            val songId = playlist!![position!!].id
            // 如果 MediaPlayer 已经存在，释放
            if (mediaPlayer != null) {
                mediaPlayer?.reset()
                mediaPlayer?.release()
                mediaPlayer = null
            }
            // 初始化
            mediaPlayer = MediaPlayer()
            mediaPlayer?.let {
                it.setOnPreparedListener(this@MusicBinder) // 歌曲准备完成的监听
                it.setOnCompletionListener(this@MusicBinder) // 歌曲完成后的回调
                it.setOnErrorListener(this@MusicBinder)
                it.setDataSource("https://music.163.com/song/media/outer/url?id=$songId.mp3")
                it.prepareAsync()
            }
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
            p0?.start()
            sendMusicBroadcast()
            refreshNotification()
        }

        /**
         * 更新播放状态
         */
        override fun changePlayState() {
            val isPlaying = mediaPlayer?.isPlaying
            isPlaying?.apply {
                if (this) {
                    mediaPlayer?.pause()
                    mediaSessionCallback?.onPause()
                } else {
                    mediaPlayer?.start()
                    mediaSessionCallback?.onPlay()
                }
            }
            sendMusicBroadcast()
            refreshNotification()
        }

        /**
         * 获取当前播放状态
         */
        override fun getPlayState(): Boolean {
            return mediaPlayer?.isPlaying ?: false
        }

        /**
         * 获取总进度
         * getDuration 必须在 prepared 回调完成后才可以调用。
         */
        override fun getDuration(): Int? {
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
            mediaSessionCallback?.onPlay()
            // refreshNotification()
        }

        /**
         * 获取当前播放的音乐的信息
         */
        override fun getNowSongData(): StandardSongData? {
            return playlist?.get(position!!)
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
                MODE_RANDOM -> (0..playlist?.lastIndex!!).random()
                // 单曲循环或者歌单顺序播放
                else -> {
                    // 如果当前是第一首，就跳到最后一首播放
                    if (position == 0) {
                        playlist?.lastIndex
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
                MODE_RANDOM -> (0..playlist?.lastIndex!!).random()
                else -> {
                    position = if (position == playlist?.lastIndex) {
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
            return position ?: -1
        }

        /**
         * 获取 AudioSessionId，用于音效
         */
        override fun getAudioSessionId(): Int {
            return mediaPlayer?.audioSessionId ?: 0
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
                    position = if (position == playlist?.lastIndex) {
                        0
                    } else {
                        position?.plus(1)
                    }
                }
                MODE_REPEAT_ONE -> {}
                MODE_RANDOM -> {
                    position = (0..playlist?.lastIndex!!).random()
                }
            }
            playMusic(position ?: 0)
        }

        override fun onError(p0: MediaPlayer?, p1: Int, p2: Int): Boolean {
//            toast("错误")
            return true
        }

    }

    private fun getPendingIntentActivity(): PendingIntent {
        val intentMain = Intent(this, MainActivity::class.java)
        val intentPlayer = Intent(this, PlayActivity::class.java)
        val intents = arrayOf(intentMain, intentPlayer)
        return PendingIntent.getActivities(this, 1, intents, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun getPendingIntentPrevious(): PendingIntent {
        val intent = Intent(this, MusicService::class.java)
        intent.putExtra("int_code", CODE_PREVIOUS)
        return PendingIntent.getService(this, 2, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun getPendingIntentPlay(): PendingIntent {
        val intent = Intent(this, MusicService::class.java)
        intent.putExtra("int_code", CODE_PLAY)
        return PendingIntent.getService(this, 3, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun getPendingIntentNext(): PendingIntent {
        val intent = Intent(this, MusicService::class.java)
        intent.putExtra("int_code", CODE_NEXT)
        return PendingIntent.getService(this, 4, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    /**
     * 刷新通知
     */
    private fun refreshNotification() {
        val song = musicBinder.getNowSongData()

        mediaSession?.setMetadata(
            MediaMetadataCompat.Builder()
                .putLong(
                    MediaMetadata.METADATA_KEY_DURATION,
                    (MyApplication.musicBinderInterface?.getDuration() ?: 0).toLong()
                )
                .build()
        )
        mediaSession?.setPlaybackState(
            PlaybackStateCompat.Builder()
                .setState(
                    PlaybackStateCompat.STATE_PLAYING,
                    (MyApplication.musicBinderInterface?.getProgress() ?: 0).toLong(),
                    1f
                )
                .setActions(PlaybackStateCompat.ACTION_SEEK_TO)
                .build()
        )
        mediaSession?.setCallback(mediaSessionCallback)
        if (!musicBinder.getPlayState()) {
            mediaSessionCallback?.onPause()
        }
        mediaSession?.isActive = true // 必须设置为true，这样才能开始接收各种信息
        val mediaStyle = androidx.media.app.NotificationCompat.MediaStyle()
            .setMediaSession(mediaSession?.sessionToken)
            .setShowActionsInCompactView(0, 1, 2)
        if (song != null) {
            loge("歌曲图片url" + song.imageUrl)
            StandardGET.getSongBitmap(song.id) {
                val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_music_launcher_foreground)
                    .setLargeIcon(it)
                    .setContentTitle(song.name)
                    .setContentText(parseArtist(song.artists))
                    .setContentIntent(getPendingIntentActivity())
                    .addAction(R.drawable.ic_baseline_skip_previous_24, "Previous", getPendingIntentPrevious())
                    .addAction(getPlayIcon(), "play", getPendingIntentPlay())
                    .addAction(R.drawable.ic_baseline_skip_next_24, "next", getPendingIntentNext())
                    .setStyle(mediaStyle)
                    .setOngoing(false)
                    // .setAutoCancel(true)
                    .build()
                // 更新通知
                // notificationManager?.notify(10, notification)
                startForeground(10, notification)
            }
        }

    }

    private fun getPlayIcon(): Int {
        return if (musicBinder.getPlayState()) {
            R.drawable.ic_baseline_pause_24
        } else {
            R.drawable.ic_baseline_play_arrow_24
        }
    }


}

/**
 * 音乐服务接口
 */
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
    fun getNowPosition(): Int
    fun getAudioSessionId(): Int
    fun sendBroadcast()
}

