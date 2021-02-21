package com.dirror.music.service

import android.app.*
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.media.*
import android.net.Uri
import android.os.*
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.KeyEvent
import androidx.core.app.NotificationCompat
import androidx.lifecycle.MutableLiveData
import com.dirror.music.MyApplication
import com.dirror.music.R
import com.dirror.music.broadcast.BecomingNoisyReceiver
import com.dirror.music.music.kuwo.SearchSong
import com.dirror.music.music.local.PlayHistory
import com.dirror.music.music.netease.SongUrl
import com.dirror.music.music.qq.PlayUrl
import com.dirror.music.music.standard.SongPicture
import com.dirror.music.music.standard.data.*
import com.dirror.music.ui.activity.MainActivity
import com.dirror.music.ui.activity.PlayerActivity
import com.dirror.music.util.*
import org.jetbrains.annotations.TestOnly

/**
 * 音乐服务
 * @author Moriafly
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
    private val musicBinder by lazy { MusicController() } // 懒加载 musicBinder
    private var playlist: ArrayList<StandardSongData>? = null // 当前歌单
    private var position: Int? = 0 // 当前歌曲在 List 中的下标
    private var mode: Int = MyApplication.mmkv.decodeInt(Config.PLAY_MODE, MODE_CIRCLE)
    private var notificationManager: NotificationManager? = null // 通知管理
    private var isAudioFocus = MyApplication.mmkv.decodeBool(Config.ALLOW_AUDIO_FOCUS, true) // 是否开启音频焦点

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

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager // 要在初始化通道前
        // 初始化 MediaSession
        initMediaSession()
        // 初始化通道
        initChannel()
        // 初始化音频焦点（暂时禁用，等待测试）
        initAudioFocus()
        val mediaStyle = androidx.media.app.NotificationCompat.MediaStyle()
            .setMediaSession(mediaSession?.sessionToken)
//            .setShowActionsInCompactView(0, 1, 2)
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_music_launcher_foreground)
//            .setLargeIcon(bitmap)
            .setContentTitle("聆听好音乐")
            .setContentText("Dso Music")
            .setContentIntent(getPendingIntentActivity())
//            .addAction(R.drawable.ic_baseline_skip_previous_24, "Previous", getPendingIntentPrevious())
//            .addAction(getPlayIcon(), "play", getPendingIntentPlay())
//            .addAction(R.drawable.ic_baseline_skip_next_24, "next", getPendingIntentNext())
            .setStyle(mediaStyle)
            .setOngoing(false)
            // .setAutoCancel(true)
            .build()
        startForeground(START_FOREGROUND_ID, notification)
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
                        AudioManager.AUDIOFOCUS_GAIN -> musicBinder.play()
                        AudioManager.AUDIOFOCUS_GAIN_TRANSIENT -> musicBinder.play()
                        AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK -> musicBinder.play()
                        AudioManager.AUDIOFOCUS_LOSS -> musicBinder.pause()
                        AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> musicBinder.pause()
                        AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> musicBinder.pause()
                    }
                }.build()
            if (isAudioFocus) {
                audioManager.requestAudioFocus(audioFocusRequest)
            }
        }

    }

    /**
     * 初始化媒体会话 MediaSession
     */
    private fun initMediaSession() {
        val intentFilter = IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)

        var myNoisyAudioStreamReceiverTag = false
        val myNoisyAudioStreamReceiver = BecomingNoisyReceiver()
        // 媒体会话的回调，Service 控制通知这个 Callback 来控制 MediaPlayer
        mediaSessionCallback = object : MediaSessionCompat.Callback() {
            // 播放
            override fun onPlay() {
                // 注册广播
                if (!myNoisyAudioStreamReceiverTag) {
                    registerReceiver(myNoisyAudioStreamReceiver, intentFilter)
                    myNoisyAudioStreamReceiverTag = true
                }

                mediaSession?.setPlaybackState(
                    PlaybackStateCompat.Builder()
                        .setState(
                            PlaybackStateCompat.STATE_PLAYING,
                            (MyApplication.musicController.value?.getProgress() ?: 0).toLong(),
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
                            (MyApplication.musicController.value?.getProgress() ?: 0).toLong(),
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
                // 注销广播
                if (myNoisyAudioStreamReceiverTag) {
                    unregisterReceiver(myNoisyAudioStreamReceiver)
                    myNoisyAudioStreamReceiverTag = false
                }
                // AudioPlayer.get().stopPlayer()
            }

            // 跳转
            override fun onSeekTo(pos: Long) {
                mediaPlayer?.seekTo(pos.toInt())
                if (musicBinder.getPlayState()) {
                    onPlay()
                }
            }

            override fun onMediaButtonEvent(mediaButtonEvent: Intent?): Boolean {
                if (mediaButtonEvent != null) {
                    val keyEvent = mediaButtonEvent.getParcelableExtra(Intent.EXTRA_KEY_EVENT) as KeyEvent?
                    when (mediaButtonEvent.action) {
                        Intent.ACTION_MEDIA_BUTTON -> {
                            if (keyEvent != null) {
                                when (keyEvent.action) {
                                    // 按键按下
                                    KeyEvent.ACTION_DOWN -> {
                                        when (keyEvent.keyCode) {
                                            KeyEvent.KEYCODE_MEDIA_PLAY -> { // 播放按钮
                                                MyApplication.musicController.value?.play()
                                            }
                                            KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> {
                                                MyApplication.musicController.value?.changePlayState()
                                            }
                                            KeyEvent.KEYCODE_MEDIA_PAUSE -> {
                                                MyApplication.musicController.value?.pause()
                                            }
                                            KeyEvent.KEYCODE_MEDIA_NEXT -> {
                                                MyApplication.musicController.value?.playNext()
                                            }
                                            KeyEvent.KEYCODE_MEDIA_PREVIOUS -> {
                                                MyApplication.musicController.value?.playPrevious()
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                return true
            }

        }
        // 初始化 MediaSession
        mediaSession = MediaSessionCompat(this, "MusicService").apply {
            // 设置 Callback
            setCallback(mediaSessionCallback, Handler(Looper.getMainLooper()))
            // 把 MediaSession 置为 active，这样才能开始接收各种信息
            if (!isActive) {
                isActive = true
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.getIntExtra("int_code", 0)) {
            CODE_PREVIOUS -> musicBinder.playPrevious()
            CODE_PLAY -> {
                if (musicBinder.getPlayState()) {
                    musicBinder.pause()
                } else {
                    musicBinder.play()
                }
            }
            CODE_NEXT -> musicBinder.playNext()
        }
        return START_NOT_STICKY // 非粘性服务
    }

    /**
     * 绑定
     */
    override fun onBind(p0: Intent?): IBinder {
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

    override fun onDestroy() {
        super.onDestroy()
        // 释放 mediaSession
        mediaSession?.let {
            it.setCallback(null)
            it.release()
        }
    }

    /**
     * 内部类
     * MusicBinder
     */
    inner class MusicController : Binder(), MusicControllerInterface, MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {

        private var songData = MutableLiveData<StandardSongData?>()

        private val isSongPlaying = MutableLiveData<Boolean>().also {
            it.value = mediaPlayer?.isPlaying ?: false
        }

        private var isPrepared = false // 音乐是否准备完成

        override fun setPlaylist(songListData: ArrayList<StandardSongData>) {
            playlist = songListData
        }

        override fun getPlaylist(): ArrayList<StandardSongData>? = playlist

        override fun playMusic(songPosition: Int) {


            // audioManager.abandonAudioFocusRequest(audioFocusRequest)
            isPrepared = false
            position = songPosition
            // loge("MusicService songPosition:${position}")
            // loge("MusicService 歌单歌曲数量:${playlist?.size}")
            // 当前的歌曲
            val song = playlist?.get(position ?: 0)
            songData.value = song

            // 如果 MediaPlayer 已经存在，释放
            if (mediaPlayer != null) {
                mediaPlayer?.reset()
                mediaPlayer?.release()
                mediaPlayer = null
            }

            when (song?.source) {
                SOURCE_NETEASE -> {
                    startPlayUrl(SongUrl.getSongUrl(song.id))
                }
                SOURCE_QQ -> {
                    PlayUrl.getPlayUrl(song.id) {
                        loge("QQ 音乐链接：${it}")
                        startPlayUrl(it)
                    }
                }
                SOURCE_LOCAL -> {
                    val id = song.id.toLong()
                    val contentUri: Uri =
                        ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)

                    mediaPlayer = MediaPlayer().apply {
                        setOnPreparedListener(this@MusicController) // 歌曲准备完成的监听
                        setOnCompletionListener(this@MusicController) // 歌曲完成后的回调
                        setOnErrorListener(this@MusicController)
                        // setAudioStreamType(AudioManager.STREAM_MUSIC)
                        setDataSource(applicationContext, contentUri)
                        prepareAsync()
                    }
                }
                SOURCE_DIRROR -> {
                    song.dirrorInfo?.let {
                        // toast(it.url)
                        startPlayUrl(it.url)
                    }
                }
                SOURCE_KUWO -> {
                    SearchSong.getUrl(song.id) {
                        startPlayUrl(it)
                    }
                }
            }

        }

        private fun startPlayUrl(url: String) {

            if (!InternetState.isWifi(MyApplication.context) && !MyApplication.mmkv.decodeBool(
                    Config.PLAY_ON_MOBILE,
                    false
                )
            ) {
                toast("移动网络下已禁止播放，请在设置中打开选项（注意流量哦）")
            } else {
                // 初始化
                mediaPlayer = MediaPlayer()
                mediaPlayer?.let {
                    it.setOnPreparedListener(this@MusicController) // 歌曲准备完成的监听
                    it.setOnCompletionListener(this@MusicController) // 歌曲完成后的回调
                    it.setOnErrorListener(this@MusicController)
                    it.setDataSource(url)
                    it.prepareAsync()
                }
            }
        }

        private fun sendMusicBroadcast() {
            // Service 通知
            val intent = Intent("com.dirror.music.MUSIC_BROADCAST")
            intent.setPackage(packageName)
            sendBroadcast(intent)
        }

        override fun onPrepared(p0: MediaPlayer?) {
            isPrepared = true
            this.play()
            sendMusicBroadcast()
            refreshNotification()
            setPlaybackParams()
            // 添加到播放历史
            getNowSongData()?.let {
                PlayHistory.addPlayHistory(it)
            }
        }

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
            isSongPlaying.value = mediaPlayer?.isPlaying ?: false
        }

        override fun play() {
            mediaPlayer?.start()
            mediaSessionCallback?.onPlay()
            sendMusicBroadcast()
            refreshNotification()
            isSongPlaying.value = mediaPlayer?.isPlaying ?: false
        }

        override fun pause() {
            mediaPlayer?.pause()
            mediaSessionCallback?.onPause()
            sendMusicBroadcast()
            refreshNotification()
            isSongPlaying.value = mediaPlayer?.isPlaying ?: false
        }

        override fun addToNextPlay(standardSongData: StandardSongData) {
            toast("添加")
            val nowPosition = position ?: -1
            if (playlist != null) {
                playlist?.add(nowPosition + 1, standardSongData)
//                if (nowPosition + 1 <= playlist?.lastIndex ?: -1) {
//                    toast("${nowPosition+1} : ${playlist?.lastIndex ?: -1}")
//                    val data: StandardSongData = playlist!![nowPosition + 1]
//                    if (data == standardSongData) {
//                        return
//                    } else {
//                        playlist?.add(nowPosition + 1, standardSongData)
//                    }
//                } else {
//                    // playlist?.add(it + 1, standardSongData)
//                }
            } else {
                playlist = ArrayList<StandardSongData>()
                playlist?.add(standardSongData)
            }
        }

        override fun setAudioFocus(status: Boolean) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (status != isAudioFocus) {
                    if (status) {
                        audioManager.requestAudioFocus(audioFocusRequest)
                    } else {
                        audioManager.abandonAudioFocusRequest(audioFocusRequest)
                    }
                    isAudioFocus = status
                    MyApplication.mmkv.encode(Config.ALLOW_AUDIO_FOCUS, isAudioFocus)
                }
            }
        }

        override fun getPlayState(): Boolean {
            return mediaPlayer?.isPlaying ?: false
        }

        override fun isPlaying(): MutableLiveData<Boolean> = isSongPlaying

        override fun getDuration(): Int {
            return if (isPrepared) {
                mediaPlayer?.duration ?: 0
            } else {
                0
            }
        }

        override fun getProgress(): Int {
            return if (isPrepared) {
                mediaPlayer?.currentPosition ?: 0
            } else {
                0
            }
        }

        override fun setProgress(newProgress: Int) {
            mediaPlayer?.seekTo(newProgress)
            mediaSessionCallback?.onPlay()
            // refreshNotification()
        }

        override fun getNowSongData(): StandardSongData? {
            return playlist?.get(position!!)
        }

        override fun getPlayingSongData(): MutableLiveData<StandardSongData?> = songData

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

        override fun getPlayMode(): Int = mode

        override fun playPrevious() {
            // 设置 position
            position = when (mode) {
                MODE_RANDOM -> {
                    playlist?.let {
                        (0..it.lastIndex).random()
                    }
                }
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

        override fun getNowPosition(): Int {
            return position ?: -1
        }

        override fun getAudioSessionId(): Int {
            return mediaPlayer?.audioSessionId ?: 0
        }

        override fun sendBroadcast() {
            sendMusicBroadcast()
        }

        override fun setSpeed(speed: Float) {
            this@MusicService.speed = speed
            setPlaybackParams()
        }

        override fun getSpeed(): Float = speed

        override fun getPitchLevel(): Int = pitchLevel

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

        private fun setPlaybackParams() {
            if (isPrepared) {
                mediaPlayer?.let {
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            val playbackParams = it.playbackParams
                            // playbackParams.speed = speed // 0 表示暂停
                            playbackParams.pitch = pitch
                            it.playbackParams = playbackParams
                        }
                    } catch (e: Exception) {

                    }
                }
            }
        }

        override fun onCompletion(p0: MediaPlayer?) {
            autoPlayNext()
        }

        private fun autoPlayNext() {
            when (mode) {
                MODE_CIRCLE -> {
                    position = if (position == playlist?.lastIndex) {
                        0
                    } else {
                        position?.plus(1)
                    }
                }
                // 单曲循环
                MODE_REPEAT_ONE -> {
                    setProgress(0)
                    play()
                    return
                }
                MODE_RANDOM -> {
                    position = (0..playlist?.lastIndex!!).random()
                }
            }
            playMusic(position ?: 0)
        }

        override fun onError(p0: MediaPlayer?, p1: Int, p2: Int): Boolean {
            if (MyApplication.mmkv.decodeBool(Config.SKIP_ERROR_MUSIC, true)) {
                // 播放下一首
                toast("播放错误，开始播放下一首")
                playNext()
            } else {
                toast("播放错误")
            }
            return true
        }

    }

    private fun getPendingIntentActivity(): PendingIntent {
        val intentMain = Intent(this, MainActivity::class.java)
        val intentPlayer = Intent(this, PlayerActivity::class.java)
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
                        (MyApplication.musicController.value?.getDuration() ?: 0).toLong()
                    )
                    .build()
            )
            setPlaybackState(
                PlaybackStateCompat.Builder()
                    .setState(
                        PlaybackStateCompat.STATE_PLAYING,
                        (MyApplication.musicController.value?.getProgress() ?: 0).toLong(),
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
            SongPicture.getPlayerActivityCoverBitmap(this, song, 100.dp()) { bitmap ->
                showNotification(mediaStyle, song, bitmap)
            }
        }

    }

    /**
     * 显示通知
     */
    private fun showNotification(
        mediaStyle: androidx.media.app.NotificationCompat.MediaStyle,
        song: StandardSongData,
        bitmap: Bitmap?
    ) {

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
            .setOngoing(true)
            // .setTicker("Dso") 魅族状态栏歌词的实现方法
            // .setAutoCancel(true)
            .build()
        // 更新通知
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