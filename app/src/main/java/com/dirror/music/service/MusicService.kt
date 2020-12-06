package com.dirror.music.service

import android.app.*
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.media.*
import android.net.Uri
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
import com.dirror.music.broadcast.BecomingNoisyReceiver
import com.dirror.music.music.netease.SongUrl
import com.dirror.music.music.qq.PlayUrl
import com.dirror.music.music.standard.data.SOURCE_LOCAL
import com.dirror.music.music.standard.data.SOURCE_NETEASE
import com.dirror.music.music.standard.data.SOURCE_QQ
import com.dirror.music.music.standard.data.StandardSongData
import com.dirror.music.ui.activity.MainActivity
import com.dirror.music.ui.activity.PlayActivity
import com.dirror.music.util.*
import org.jetbrains.annotations.TestOnly

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

        const val CHANNEL_ID = "Dso Music Channel Id" // 通知通道 ID
        const val START_FOREGROUND_ID = 10 // 开启前台服务的 ID
    }

    private var mediaPlayer: MediaPlayer? = null // 定义 MediaPlayer
    private val musicBinder by lazy { MusicBinder() } // 懒加载 musicBinder
    private var playlist: ArrayList<StandardSongData>? = null // 当前歌单
    private var position: Int? = 0 // 当前歌曲在 List 中的下标
    private var mode: Int = MyApplication.mmkv.decodeInt(Config.PLAY_MODE, MODE_CIRCLE)
    private var notificationManager: NotificationManager? = null // 通知管理

    private var mediaSessionCallback: MediaSessionCompat.Callback? = null
    private var mediaSession: MediaSessionCompat? = null

    private var speed = 1f // 默认播放速度，0f 表示暂停
    private var pitch = 1f // 默认音高
    private var pitchLevel = 0 // 音高等级
    private val pitchUnit = 0.05f // 音高单元，每次改变的音高单位

    private lateinit var audioManager: AudioManager
    private lateinit var audioAttributes: AudioAttributes
    private lateinit var audioFocusRequest: AudioFocusRequest

    override fun onCreate() {
        super.onCreate()
        mediaSession = MediaSessionCompat(this, "MusicService")
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager // 要在初始化通道前
        // 初始化 MediaSession 回调
        initMediaSessionCallback()
        // 初始化通道
        initChannel()
        // 初始化音频焦点（暂时禁用，等待测试）
        // initAudioFocus()
    }

    /**
     * 初始化音频焦点
     */
    @TestOnly
    private fun initAudioFocus() {
        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(audioAttributes)
                .setOnAudioFocusChangeListener { focusChange ->
                    when (focusChange) {
                        AudioManager.AUDIOFOCUS_GAIN -> musicBinder.start()
                        AudioManager.AUDIOFOCUS_GAIN_TRANSIENT -> musicBinder.start()
                        AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK -> musicBinder.start()
                        AudioManager.AUDIOFOCUS_LOSS -> musicBinder.pause()
                        AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> musicBinder.pause()
                        AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> musicBinder.pause()
                    }
                }.build()
            audioManager.requestAudioFocus(audioFocusRequest)
            // audioManager.abandonAudioFocusRequest(audioFocusRequest)

        }

    }

    /**
     * 初始化 MediaSession 回调
     * 媒体绘话的回调
     */
    private fun initMediaSessionCallback() {
        val intentFilter = IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
        val myNoisyAudioStreamReceiver = BecomingNoisyReceiver()

        // 媒体会话的回调，Service 控制通知这个 Callback 来控制 MediaPlayer
        mediaSessionCallback = object : MediaSessionCompat.Callback() {
            // 播放
            override fun onPlay() {
                registerReceiver(myNoisyAudioStreamReceiver, intentFilter)
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

            // 暂停
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

            // 播放下一首
            override fun onSkipToNext() {
                musicBinder.playNext()
            }

            // 播放上一首
            override fun onSkipToPrevious() {
                // AudioPlayer.get().prev()
            }

            // 关闭
            override fun onStop() {
                unregisterReceiver(myNoisyAudioStreamReceiver)
                // AudioPlayer.get().stopPlayer()
            }

            // 跳转
            override fun onSeekTo(pos: Long) {
                mediaPlayer?.seekTo(pos.toInt())
                if (musicBinder.getPlayState()) {
                    onPlay()
                }
            }

        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.getIntExtra("int_code", 0)) {
            CODE_PREVIOUS -> musicBinder.playLast()
            CODE_PLAY -> {
                if (musicBinder.getPlayState()) {
                    musicBinder.pause()
                } else {
                    musicBinder.start()
                }
            }
            CODE_NEXT -> musicBinder.playNext()
        }
        return START_NOT_STICKY // 非粘性服务
    }

    /**
     * 绑定
     */
    override fun onBind(p0: Intent?): IBinder? {
        return musicBinder
    }

    private fun initChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel
            val name = "Dso Music Notification"
            val descriptionText = "Dso Music 音乐通知"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            channel.description = descriptionText
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
            // 当前的歌曲
            val song = playlist?.get(position ?: 0)

            // 如果 MediaPlayer 已经存在，释放
            if (mediaPlayer != null) {
                mediaPlayer?.reset()
                mediaPlayer?.release()
                mediaPlayer = null
            }

            when (song?.source) {
                SOURCE_NETEASE -> {
                    startPlayUrl(SongUrl.getSongUrl(song.id.toString()))
                }
                SOURCE_QQ -> {
                    PlayUrl.getPlayUrl(song.id as String) {
                        loge("QQ 音乐链接：${it}")
                        startPlayUrl(it)
                    }
                }
                SOURCE_LOCAL -> {
                    val id = song.id!!.toLong()
                    val contentUri: Uri =
                        ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)

                    mediaPlayer = MediaPlayer().apply {
                        setOnPreparedListener(this@MusicBinder) // 歌曲准备完成的监听
                        setOnCompletionListener(this@MusicBinder) // 歌曲完成后的回调
                        setOnErrorListener(this@MusicBinder)
                        // setAudioStreamType(AudioManager.STREAM_MUSIC)
                        setDataSource(applicationContext, contentUri)
                        prepareAsync()
                    }

                    // ...prepare and start...

                }
            }

        }

        private fun startPlayUrl(url: String) {

            if (!InternetState.isWifi(MyApplication.context) && !MyApplication.mmkv.decodeBool(Config.PLAY_ON_MOBILE, false)) {
                toast("移动网络下已禁止播放，请在设置中打开选项（注意流量哦）")
            } else {
                // 初始化
                mediaPlayer = MediaPlayer()
                mediaPlayer?.let {
                    it.setOnPreparedListener(this@MusicBinder) // 歌曲准备完成的监听
                    it.setOnCompletionListener(this@MusicBinder) // 歌曲完成后的回调
                    it.setOnErrorListener(this@MusicBinder)
                    it.setDataSource(url)
                    it.prepareAsync()
                }
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
            setPlaybackParams()
        }

        /**
         * 更新播放状态
         * 播放或者暂停
         */
        @Deprecated("不推荐使用，切换为 start 或者 pause")
        override fun changePlayState() {
            val isPlaying = mediaPlayer?.isPlaying
            isPlaying?.let {
                if (it) {
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
         * 开始播放
         */
        override fun start() {
            mediaPlayer?.start()
            mediaSessionCallback?.onPlay()
            sendMusicBroadcast()
            refreshNotification()
        }

        /**
         * 暂停播放
         */
        override fun pause() {
            mediaPlayer?.pause()
            mediaSessionCallback?.onPause()
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
        override fun getDuration(): Int {
            return if (isPrepared) {
                mediaPlayer?.duration ?: 0
            } else {
                0
            }
        }

        /**
         * 获取当前进度
         */
        override fun getProgress(): Int {
            return if (isPrepared) {
                mediaPlayer?.currentPosition ?: 0
            } else {
                0
            }
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
            MyApplication.mmkv.encode(Config.PLAY_MODE, mode)
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
            playlist?.let {
                position = when (mode) {
                    MODE_RANDOM -> {
                        (0..it.lastIndex).random()
                    }
                    else -> {
                        if (position == it.lastIndex) {
                            0
                        } else {
                            position?.plus(1)
                        }
                    }
                }
            }
            position?.let {
                playMusic(it)
            }
        }

        /**
         * 获取当前 position
         */
        override fun getNowPosition(): Int {
            return position ?: -1
        }

        /**
         * 获取 AudioSessionId，用于音效
         * 无则返回 0
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
         * 设置播放速度
         */
        override fun setSpeed(speed: Float) {
            this@MusicService.speed = speed
            setPlaybackParams()
        }

        /**
         * 获取播放速度
         */
        override fun getSpeed(): Float {
            return speed
        }

        /**
         * 获取音高等级
         */
        override fun getPitchLevel(): Int {
            return pitchLevel
        }

        /**
         * 升调
         */
        override fun increasePitchLevel() {
            pitchLevel++
            val value = pitchUnit * (pitchLevel + 1f / pitchUnit)
            if (value < 1.5f) {
                pitch = value
                setPlaybackParams()
            } else {
                decreasePitchLevel()
            }
        }

        /**
         * 降调
         */
        override fun decreasePitchLevel() {
            pitchLevel--
            val value = pitchUnit * (pitchLevel + 1f / pitchUnit)
            if (value > 0.5f) {
                pitch = value
                setPlaybackParams()
            } else {
                increasePitchLevel()
            }
        }

        /**
         * 设置 setPlaybackParams
         */
        private fun setPlaybackParams() {
            if (isPrepared) {
                mediaPlayer?.let {
                    val playbackParams = it.playbackParams
                    // playbackParams.speed = speed // 0 表示暂停
                    playbackParams.pitch = pitch
                    it.playbackParams = playbackParams
                }
            }
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
                MODE_REPEAT_ONE -> {
                }
                MODE_RANDOM -> {
                    position = (0..playlist?.lastIndex!!).random()
                }
            }
            playMusic(position ?: 0)
        }

        override fun onError(p0: MediaPlayer?, p1: Int, p2: Int): Boolean {
            toast("播放错误")
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
        mediaSession?.apply {
            setMetadata(
                MediaMetadataCompat.Builder()
                    .putLong(
                        MediaMetadata.METADATA_KEY_DURATION,
                        (MyApplication.musicBinderInterface?.getDuration() ?: 0).toLong()
                    )
                    .build()
            )
            setPlaybackState(
                PlaybackStateCompat.Builder()
                    .setState(
                        PlaybackStateCompat.STATE_PLAYING,
                        (MyApplication.musicBinderInterface?.getProgress() ?: 0).toLong(),
                        1f
                    )
                    .setActions(PlaybackStateCompat.ACTION_SEEK_TO)
                    .build()
            )
            setCallback(mediaSessionCallback)
            isActive = true // 必须设置为true，这样才能开始接收各种信息
        }

        if (!musicBinder.getPlayState()) {
            mediaSessionCallback?.onPause()
        }

        val mediaStyle = androidx.media.app.NotificationCompat.MediaStyle()
            .setMediaSession(mediaSession?.sessionToken)
            .setShowActionsInCompactView(0, 1, 2)
        if (song != null) {
            StandardGET.getSongBitmap(song) { bitmap ->
                showNotification(mediaStyle, song, bitmap)
            }
        }

    }

    /**
     * 显示通知
     */
    private fun showNotification(mediaStyle: androidx.media.app.NotificationCompat.MediaStyle, song: StandardSongData, bitmap: Bitmap?) {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_music_launcher_foreground)
            .setLargeIcon(bitmap)
            .setContentTitle(song.name)
            .setContentText(song.artists?.let { it1 -> parseArtist(it1) })
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
        startForeground(START_FOREGROUND_ID, notification)
    }

    /**
     * 获取通知栏播放的图标
     */
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
    fun getDuration(): Int
    fun getProgress(): Int
    fun setProgress(newProgress: Int)
    fun getNowSongData(): StandardSongData?
    fun changePlayMode()
    fun getPlayMode(): Int
    fun playLast()
    fun playNext()
    fun getNowPosition(): Int
    fun getAudioSessionId(): Int // 获取音频 Session ID
    fun sendBroadcast() // 发送广播
    fun setSpeed(speed: Float) // 设置播放速度
    fun getSpeed(): Float // 获取播放速度
    fun getPitchLevel(): Int // 获取音高等级
    fun increasePitchLevel() // 升调
    fun decreasePitchLevel() // 降调
    fun start() // 开始播放
    fun pause() // 暂停播放
}