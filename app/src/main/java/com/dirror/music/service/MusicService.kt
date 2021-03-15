/**
 * DsoMusic Copyright (C) 2020-2021 Moriafly
 *
 * This program comes with ABSOLUTELY NO WARRANTY; for details type `show w'.
 * This is free software, and you are welcome to redistribute it
 * under certain conditions; type `show c' for details.
 *
 * The hypothetical commands `show w' and `show c' should show the appropriate
 * parts of the General Public License.  Of course, your program's commands
 * might be different; for a GUI interface, you would use an "about box".
 *
 * You should also get your employer (if you work as a programmer) or school,
 * if any, to sign a "copyright disclaimer" for the program, if necessary.
 * For more information on this, and how to apply and follow the GNU GPL, see
 * <https://www.gnu.org/licenses/>.
 *
 * The GNU General Public License does not permit incorporating your program
 * into proprietary programs.  If your program is a subroutine library, you
 * may consider it more useful to permit linking proprietary applications with
 * the library.  If this is what you want to do, use the GNU Lesser General
 * Public License instead of this License.  But first, please read
 * <https://www.gnu.org/licenses/why-not-lgpl.html>.
 */

package com.dirror.music.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.media.*
import android.net.Uri
import android.os.*
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.view.KeyEvent
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.MutableLiveData
import coil.imageLoader
import coil.request.ImageRequest
import com.dirror.lyricviewx.LyricEntry
import com.dirror.music.MyApplication
import com.dirror.music.R
import com.dirror.music.broadcast.BecomingNoisyReceiver
import com.dirror.music.music.local.PlayHistory
import com.dirror.music.music.standard.SongPicture
import com.dirror.music.music.standard.data.*
import com.dirror.music.service.base.BaseMediaService
import com.dirror.music.ui.activity.MainActivity
import com.dirror.music.ui.activity.PlayerActivity
import com.dirror.music.util.*
import com.dirror.music.util.extensions.next
import com.dirror.music.util.extensions.previous
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Dso Music 音乐播放服务
 * @author Moriafly
 * @since 2020/9
 */
open class MusicService : BaseMediaService() {

    companion object {
        /* Flyme 状态栏歌词 TICKER 一直显示 */
        private const val FLAG_ALWAYS_SHOW_TICKER = 0x1000000
        /* 只更新 Flyme 状态栏歌词 */
        private const val FLAG_ONLY_UPDATE_TICKER = 0x2000000
        /* MSG 状态栏歌词 */
        private const val MSG_STATUS_BAR_LYRIC = 0
    }

    /* Dso Music 音乐控制器 */
    private val musicController by lazy { MusicController() }
    /* 播放模式 */
    private var mode: Int = MyApplication.mmkv.decodeInt(Config.PLAY_MODE, MODE_CIRCLE)
    /* 通知管理 */
    private var notificationManager: NotificationManager? = null
    /* 是否开启音频焦点 */
    private var isAudioFocus = MyApplication.mmkv.decodeBool(Config.ALLOW_AUDIO_FOCUS, true) // 是否开启音频焦点
    /* 音频会话 */
    private var mediaSession: MediaSessionCompat? = null
    /* 音频会话回调 */
    private var mediaSessionCallback: MediaSessionCompat.Callback? = null
    /* 音频控制器 */
    private var mediaController: MediaControllerCompat? = null
    /* 默认播放速度，0f 表示暂停 */
    private var speed = 1f
    /* 默认音高 */
    private var pitch = 1f
    /* 音高等级 */
    private var pitchLevel = 0
    /* 音高单元，每次改变的音高单位 */
    private val pitchUnit = 0.05f
    /* 音频管理 */
    private lateinit var audioManager: AudioManager
    /* AudioAttributes */
    private lateinit var audioAttributes: AudioAttributes
    /* AudioFocusRequest */
    private lateinit var audioFocusRequest: AudioFocusRequest
    /* 当前状态栏歌曲 */
    private var currentStatusBarTag = ""
    /* 单句歌词集合 */
    private val lyricEntryList: ArrayList<LyricEntry> = ArrayList()
    /* Live */
    private var liveLyricEntryList = MutableLiveData<ArrayList<LyricEntry>>(ArrayList())
    /* Handler */
    private val handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            if (msg.what == MSG_STATUS_BAR_LYRIC) {
                if (lyricEntryList.isNotEmpty()) {
                    if (getCurrentLineLyricEntry()?.text ?: "" != currentStatusBarTag) {
                        currentStatusBarTag = getCurrentLineLyricEntry()?.text ?: ""
                        updateNotification(true)
                    }
                    if (musicController.isPlaying().value == true) {
                        sendEmptyMessageDelayed(MSG_STATUS_BAR_LYRIC, 100L)
                    }
                }
            }
        }
    }

    override fun onCreate() {
        // 在 super.onCreate() 前
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager // 通知管理
        super.onCreate()
        updateNotification(false)
    }

    override fun initChannel() {
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

    override fun initAudioFocus() {
        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()

            audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(audioAttributes)
                .setOnAudioFocusChangeListener { focusChange ->
                    when (focusChange) {
                        // AudioManager.AUDIOFOCUS_GAIN -> musicBinder.play()
                        // AudioManager.AUDIOFOCUS_GAIN_TRANSIENT -> musicBinder.play()
                        // AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK -> musicBinder.play()
                        AudioManager.AUDIOFOCUS_LOSS -> {
                            // audioManager.abandonAudioFocusRequest(audioFocusRequest)
                            musicController.pause()
                        }
                        AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> musicController.pause()
                        AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> musicController.pause()
                    }
                }.build()
            if (isAudioFocus) {
                audioManager.requestAudioFocus(audioFocusRequest)
            }
        }
    }

    override fun initMediaSession() {
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
                musicController.playNext()
            }

            // 播放上一首
            override fun onSkipToPrevious() {
                musicController.playPrevious()
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
                if (musicController.isPlaying().value == true) {
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
                                            KeyEvent.KEYCODE_MEDIA_PLAY -> musicController.play()
                                            KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> musicController.changePlayState()
                                            KeyEvent.KEYCODE_MEDIA_PAUSE -> musicController.pause()
                                            KeyEvent.KEYCODE_MEDIA_NEXT -> musicController.playNext()
                                            KeyEvent.KEYCODE_MEDIA_PREVIOUS -> musicController.playPrevious()
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
            CODE_PREVIOUS -> musicController.playPrevious()
            CODE_PLAY -> {
                loge(musicController.isPlaying().value.toString(), TAG)
                if (musicController.isPlaying().value == true) {
                    loge("按钮请求暂停音乐", TAG)
                    musicController.pause()
                } else {
                    loge("按钮请求继续播放音乐", TAG)
                    musicController.play()
                }
            }
            CODE_NEXT -> musicController.playNext()
        }
        return START_NOT_STICKY // 非粘性服务
    }

    /**
     * 绑定
     */
    override fun onBind(p0: Intent?): IBinder {
        return musicController
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.e(TAG, "onUnbind: 解绑")
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e(TAG, "onDestroy: 解绑")
        // 释放 mediaSession
        mediaSession?.let {
            it.setCallback(null)
            it.release()
        }
        mediaPlayer?.release()

    }
    
    /**
     * inner class Music Controller
     */
    inner class MusicController : Binder(), MusicControllerInterface, MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {

        /* 是否是恢复 */
        private var recover = false
        private var recoverProgress = 0

        private var songData = MutableLiveData<StandardSongData?>()

        private val isSongPlaying = MutableLiveData<Boolean>().also {
            it.value = mediaPlayer?.isPlaying ?: false
        }

        private var isPrepared = false // 音乐是否准备完成

        /* Song cover bitmap*/
        private val coverBitmap = MutableLiveData<Bitmap?>()

        override fun setPlaylist(songListData: ArrayList<StandardSongData>) {
            PlayQueue.setNormal(songListData)
            if (mode == MODE_RANDOM) {
                PlayQueue.random()
            }
        }

        override fun getPlaylist(): ArrayList<StandardSongData>? = PlayQueue.currentQueue.value

        override fun playMusic(song: StandardSongData) {
            isPrepared = false
            songData.value = song
            // 保存当前播放音乐
            MyApplication.mmkv.encode(Config.SERVICE_CURRENT_SONG, song)
            Log.e(TAG, "onDestroy: 成功保存歌曲恢复到 mmkv：${song.name}")

            // 如果 MediaPlayer 已经存在，释放
            if (mediaPlayer != null) {
                mediaPlayer?.reset()
                mediaPlayer?.release()
                mediaPlayer = null
            }
            // 初始化
            mediaPlayer = MediaPlayer().apply {
                ServiceSongUrl.getUrl(song) {
                    when (it) {
                        is String -> {
                            if (!InternetState.isWifi(MyApplication.context) && !MyApplication.mmkv.decodeBool(
                                    Config.PLAY_ON_MOBILE,
                                    false
                                )
                            ) {
                                toast("移动网络下已禁止播放，请在设置中打开选项（注意流量哦）")
                                return@getUrl
                            } else {
                                setDataSource(it)
                            }
                        }
                        is Uri -> setDataSource(applicationContext, it)
                    }
                    setOnPreparedListener(this@MusicController) // 歌曲准备完成的监听
                    setOnCompletionListener(this@MusicController) // 歌曲完成后的回调
                    setOnErrorListener(this@MusicController)
                    prepareAsync()
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
            mediaController = mediaSession?.sessionToken?.let { it1 -> MediaControllerCompat(this@MusicService, it1) }
////            MediaControllerCompat.setMediaController(this@MusicService, mediaController)
////            onMediaBrowserConnected();
////            onMediaControllerConnected(mediaController.getSessionToken())
            if (recover) {
                recover = false
                this.setProgress(recoverProgress)
            } else {
                this.play()
            }
            sendMusicBroadcast()
            // updateNotification()
//            setPlaybackParams()
//
            songData.value?.let { standardSongData ->
                // 获取封面
                SongPicture.getPlayerActivityCoverBitmap(
                    this@MusicService.applicationContext,
                    standardSongData,
                    240.dp()
                ) { bitmap ->
                    coverBitmap.value = bitmap
                    updateNotification()
                }
                // 获取歌词
                ServiceSongUrl.getLyric(standardSongData) {
                    val mainLyricText = it.lyric
                    val secondLyricText = it.secondLyric
                    lyricEntryList.clear()
                    val sb = StringBuilder("file://")
                    sb.append(mainLyricText)
                    sb.append("#").append(secondLyricText)
                    GlobalScope.launch {
                        val entryList = LyricUtil.parseLrc(arrayOf(mainLyricText, secondLyricText))
                        if (entryList != null && entryList.isNotEmpty()) {
                            lyricEntryList.addAll(entryList)
                        }
                        lyricEntryList.sort()
                        runOnMainThread {
                            liveLyricEntryList.value = lyricEntryList
                        }
                        updateNotification(true)
                    }
                }
            }
            // 添加到播放历史
            getPlayingSongData().value?.let {
                PlayHistory.addPlayHistory(it)
            }
        }

        override fun changePlayState() {
            isSongPlaying.value?.let {
                if (it) {
                    mediaPlayer?.pause()
                    mediaSessionCallback?.onPause()
                } else {
                    mediaPlayer?.start()
                    mediaSessionCallback?.onPlay()
                }
                isSongPlaying.value = mediaPlayer?.isPlaying ?: false
            }
            sendMusicBroadcast()
            updateNotification()
        }

        override fun play() {
            if (isPrepared) {
                mediaPlayer?.start()
                isSongPlaying.value = mediaPlayer?.isPlaying ?: false
                mediaSessionCallback?.onPlay()
                sendMusicBroadcast()
                updateNotification()
                handler.sendEmptyMessageDelayed(MSG_STATUS_BAR_LYRIC, 100L)
            }
        }

        override fun pause() {
            if (isPrepared) {
                mediaPlayer?.pause()
                isSongPlaying.value = mediaPlayer?.isPlaying ?: false
                mediaSessionCallback?.onPause()
                sendMusicBroadcast()
                updateNotification()
            }
        }

        override fun addToNextPlay(standardSongData: StandardSongData) {
            if (standardSongData == songData.value) {
                return
            }
            if (PlayQueue.currentQueue.value?.contains(standardSongData) == true) {
                PlayQueue.currentQueue.value?.remove(standardSongData)
            }
            val currentPosition = PlayQueue.currentQueue.value?.indexOf(songData.value) ?: -1
            PlayQueue.currentQueue.value?.add(currentPosition + 1, standardSongData)
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

        override fun stopMusicService() {
            stopSelf(-1)
        }

        override fun getPlayerCover(): MutableLiveData<Bitmap?> = coverBitmap

        override fun getLyricEntryList(): MutableLiveData<ArrayList<LyricEntry>> = liveLyricEntryList

        override suspend fun getSongCover(size: Int?): Bitmap {
            return suspendCoroutine {
                if (size == null) {
                    coverBitmap.value?.let { bitmap ->
                        it.resume(bitmap)
                    }
                } else {
                    Log.e(TAG, "getSongCover: Coil 获取图片开始")
                    val request = ImageRequest.Builder(this@MusicService)
                        .size(size)
                        .data(coverBitmap.value)
                        .error(R.drawable.ic_song_cover)
                        .target(
                            onStart = {
                                // Handle the placeholder drawable.
                            },
                            onSuccess = { result ->
                                Log.e(TAG, "getSongCover: Coil 成功获取图片")
                                it.resume(result.toBitmap())
                            },
                            onError = { _ ->
                                Log.e(TAG, "getSongCover: Coil 获取图片失败")
                                ContextCompat.getDrawable(this@MusicService, R.drawable.ic_song_cover)?.let { it1 ->
                                    it.resume(it1.toBitmap(size, size))
                                }
                            }
                        )
                        .build()
                    this@MusicService.imageLoader.enqueue(request)
                }
            }
        }

        override fun setRecover(value: Boolean) {
            recover = value
        }

        override fun setRecoverProgress(value: Int) {
            recoverProgress = value
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
        }

        override fun getPlayingSongData(): MutableLiveData<StandardSongData?> = songData

        override fun changePlayMode() {
            when (mode) {
                MODE_CIRCLE -> mode = MODE_REPEAT_ONE
                MODE_REPEAT_ONE -> {
                    mode = MODE_RANDOM
                    PlayQueue.random()
                }
                MODE_RANDOM -> {
                    mode = MODE_CIRCLE
                    PlayQueue.normal()
                }
            }
            // 将播放模式存储
            MyApplication.mmkv.encode(Config.PLAY_MODE, mode)
            sendMusicBroadcast()
        }

        override fun getPlayMode(): Int = mode

        override fun playPrevious() {
            PlayQueue.currentQueue.value?.previous(songData.value)?.let {
                playMusic(it)
            }
        }

        override fun playNext() {
            PlayQueue.currentQueue.value?.next(songData.value)?.let {
                playMusic(it)
            }
        }

        override fun getNowPosition(): Int {
            return PlayQueue.currentQueue.value?.indexOf(songData.value) ?: -1
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
            if (mode == MODE_REPEAT_ONE) {
                setProgress(0)
                play()
                return
            }
            PlayQueue.currentQueue.value?.next(songData.value)?.let {
                playMusic(it)
            }
        }

        override fun onError(p0: MediaPlayer?, p1: Int, p2: Int): Boolean {
            if (MyApplication.mmkv.decodeBool(Config.SKIP_ERROR_MUSIC, true)) {
                // 播放下一首
                toast("播放错误 (${p1},${p2}) ，开始播放下一首")
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
     * 更新通知
     */
    private fun updateNotification(fromLyric: Boolean = false) {
        val song = musicController.getPlayingSongData().value
        GlobalScope.launch {
            Log.e(TAG, "refreshNotification: 协程开启")
            val bitmap = musicController.getSongCover(128.dp())
            Log.e(TAG, "refreshNotification: 获取到图片")
            runOnMainThread {
                showNotification(fromLyric, song, bitmap)
            }
        }
    }

    /**
     * 显示通知
     */
    private fun showNotification(fromLyric: Boolean = false, song: StandardSongData?, bitmap: Bitmap?) {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID).apply {
            setSmallIcon(R.drawable.ic_music_launcher_foreground)
            setLargeIcon(bitmap)
            setContentTitle(song?.name)
            setContentText(song?.artists?.let { it1 -> parseArtist(it1) })
            setContentIntent(getPendingIntentActivity())
            addAction(R.drawable.ic_baseline_skip_previous_24, "Previous", getPendingIntentPrevious())
            addAction(getPlayIcon(), "play", getPendingIntentPlay())
            addAction(R.drawable.ic_baseline_skip_next_24, "next", getPendingIntentNext())
            setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSession?.sessionToken)
                    .setShowActionsInCompactView(0, 1, 2)
            )
            setOngoing(true)
            if (getCurrentLineLyricEntry()?.text != null && fromLyric) {
                setTicker(getCurrentLineLyricEntry()?.text) // 魅族状态栏歌词的实现方法
            }
            // .setAutoCancel(true)
        }.build()

        notification.extras.putInt("ticker_icon", R.drawable.ic_music_launcher_foreground)
        notification.extras.putBoolean("ticker_icon_switch", false)
        notification.flags = notification.flags.or(FLAG_ALWAYS_SHOW_TICKER)
        // 是否只更新 Ticker
        if (fromLyric) {
            notification.flags = notification.flags.or(FLAG_ONLY_UPDATE_TICKER)
        }
//

        mediaSession?.apply {
            setMetadata(
                MediaMetadataCompat.Builder().apply {
                    putString(MediaMetadataCompat.METADATA_KEY_TITLE, song?.name)
                    putString(MediaMetadataCompat.METADATA_KEY_ARTIST, song?.artists?.parse())
                    putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, bitmap)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        putLong(
                            MediaMetadata.METADATA_KEY_DURATION,
                            (MyApplication.musicController.value?.getDuration() ?: 0).toLong()
                        )
                    }
                }.build()
            )
            setPlaybackState(
                PlaybackStateCompat.Builder()
                    .setState(
                        PlaybackStateCompat.STATE_PLAYING,
                        (MyApplication.musicController.value?.getProgress() ?: 0).toLong(),
                        1f
                    )
                    .setActions(MEDIA_SESSION_ACTIONS)
                    .build()
            )
            setCallback(mediaSessionCallback)
            isActive = true
        }
        if (musicController.isPlaying().value != true) {
            mediaSessionCallback?.onPause()
        }
        // 更新通知
        startForeground(START_FOREGROUND_ID, notification)
    }

    /**
     * 获取通知栏播放的图标
     */
    private fun getPlayIcon(): Int {
        return if (musicController.isPlaying().value == true) {
            R.drawable.ic_baseline_pause_24
        } else {
            R.drawable.ic_baseline_play_arrow_24
        }
    }

    private fun getCurrentLineLyricEntry(): LyricEntry? {
        val progress = musicController.getProgress()
        val line = findShowLine(progress.toLong())
        if (line <= lyricEntryList.lastIndex) {
            return lyricEntryList[line]
        }
        return null
    }

    /**
     * 二分法查找当前时间应该显示的行数（最后一个 <= time 的行数）
     */
    private fun findShowLine(time: Long): Int {
        if (lyricEntryList.isNotEmpty()) {
            var left = 0
            var right = lyricEntryList.size
            while (left <= right) {
                val middle = (left + right) / 2
                val middleTime = lyricEntryList[middle].time
                if (time < middleTime) {
                    right = middle - 1
                } else {
                    if (middle + 1 >= lyricEntryList.size || time < lyricEntryList[middle + 1].time) {
                        return middle
                    }
                    left = middle + 1
                }
            }
        }
        return 0
    }

}