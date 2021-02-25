package com.dirror.music.ui.activity

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.KeyEvent
import android.view.View
import android.view.WindowInsetsController
import android.view.animation.LinearInterpolator
import android.widget.SeekBar
import androidx.activity.viewModels
import androidx.annotation.Keep
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.palette.graphics.Palette
import coil.load
import coil.size.ViewSizeResolver
import coil.transform.CircleCropTransformation
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dirror.lyricviewx.OnPlayClickListener
import com.dirror.lyricviewx.OnSingleClickListener
import com.dirror.music.MyApplication
import com.dirror.music.R
import com.dirror.music.audio.VolumeManager
import com.dirror.music.databinding.ActivityPlayerBinding
import com.dirror.music.music.local.MyFavorite
import com.dirror.music.music.standard.SongPicture
import com.dirror.music.music.standard.data.SOURCE_LOCAL
import com.dirror.music.music.standard.data.SOURCE_NETEASE
import com.dirror.music.service.MusicService
import com.dirror.music.ui.base.SlideBackActivity
import com.dirror.music.ui.dialog.PlayerMenuMoreDialog
import com.dirror.music.ui.dialog.PlaylistDialog
import com.dirror.music.ui.dialog.SoundEffectDialog
import com.dirror.music.ui.viewmodel.PlayerViewModel
import com.dirror.music.util.*
import jp.wasabeef.glide.transformations.BlurTransformation

/**
 * 新版 PlayerActivity
 * 用来取代原先的 PlayActivity，1.6.1 版本已上线
 * 更加清晰方便管理，加入 PlayerViewModel
 * @author Moriafly
 * @since 2020年12月15日18:35:46
 * 加个 @Keep 是因为一直有个空指针没查出来
 */
@Keep
class PlayerActivity : SlideBackActivity() {

    companion object {
        private const val MUSIC_BROADCAST_ACTION = "com.dirror.music.MUSIC_BROADCAST"
        private const val DELAY_MILLIS = 500L

        // Handle 消息，播放进度
        private const val MSG_PROGRESS = 0

        private const val BACKGROUND_SCALE_Y = 1.5F
        private const val BACKGROUND_SCALE_X = 2.5F
        private const val CD_SIZE = 240

        // 背景模糊系数
        private const val BLUR_RADIUS = 15
        private const val BLUR_SAMPLING = 5

        // 动画循环时长
        private const val DURATION_CD = 27_500L
        private const val ANIMATION_REPEAT_COUNTS = -1
        private const val ANIMATION_PROPERTY_NAME = "rotation"
    }

    private lateinit var binding: ActivityPlayerBinding

    // 是否是横屏状态
    private var isLandScape = false

    // 音乐广播接收者
    private lateinit var musicBroadcastReceiver: MusicBroadcastReceiver

    // ViewModel 数据和视图分离
    private val playViewModel: PlayerViewModel by viewModels()

    // Looper + Handler
    private val handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            if (msg.what == MSG_PROGRESS) {
                playViewModel.refreshProgress()
            }
        }
    }

    // CD 旋转动画
    private val objectAnimator: ObjectAnimator by lazy {
        ObjectAnimator.ofFloat(binding.ivCover, ANIMATION_PROPERTY_NAME, 0f, 360f).apply {
            interpolator = LinearInterpolator()
            duration = DURATION_CD
            repeatCount = ANIMATION_REPEAT_COUNTS
            start()
        }
    }

    override fun initBinding() {
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initView() {
        // 设置 SlideBackLayout
        bindSlide(this, binding.clBase)
        // 屏幕旋转
        val configuration = this.resources.configuration //获取设置的配置信息
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            isLandScape = true
            // 横屏隐藏状态栏
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                window.insetsController?.hide(WindowInsetsController.BEHAVIOR_SHOW_BARS_BY_SWIPE)
            } else {
                val uiOptions = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)
                window.decorView.systemUiVisibility = uiOptions
            }
        }
        // 页面状态栏适配
        binding.titleBar?.let {
            (it.layoutParams as ConstraintLayout.LayoutParams).apply {
                topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                topMargin = getStatusBarHeight(window, this@PlayerActivity)
            }
        }
//        binding.llBase?.let {
//            (it.layoutParams as ConstraintLayout.LayoutParams).apply {
//                topMargin = getStatusBarHeight(window, this@PlayerActivity)
//            }
//        }
        // 页面导航栏适配
        val navigationHeight = if (MyApplication.mmkv.decodeBool(Config.PARSE_NAVIGATION, true)) {
            getNavigationBarHeight(this@PlayerActivity)
        } else {
            0
        }
        (binding.clBottom.layoutParams as ConstraintLayout.LayoutParams).apply {
            bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            bottomMargin = navigationHeight
        }
        binding.apply {
            // 时长右对齐
            ttvDuration.setAlignRight()
            // 背景图片放大
            ivBackground.scaleY = BACKGROUND_SCALE_Y
            ivBackground.scaleX = BACKGROUND_SCALE_X
            // 默认隐藏翻译按钮
            ivTranslation.visibility = View.GONE
            // 初始化音量调节
            seekBarVolume.max = VolumeManager.maxVolume
            seekBarVolume.progress = VolumeManager.getCurrentVolume()

            lyricView.setLabel("聆听好音乐")
            lyricView.setTimelineTextColor(ContextCompat.getColor(this@PlayerActivity, R.color.colorTextForeground))
        }
        if (isLandScape) {
            slideBackEnabled = false
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initListener() {
        binding.apply {
            // 返回按钮
            ivBack.setOnClickListener { finish() }
            // 开始 / 暂停播放按钮
            ivPlay.setOnClickListener { playViewModel.changePlayState() }
            // 上一曲
            ivLast.setOnClickListener { playViewModel.playLast() }
            // 下一曲
            ivNext.setOnClickListener { playViewModel.playNext() }
            // 切换播放模式
            ivMode.setOnClickListener { playViewModel.changePlayMode() }
            // 评论
            ivComment.setOnClickListener {
                MyApplication.musicController.value?.getPlayingSongData()?.value?.let {
                    if (it.source != SOURCE_LOCAL) {
                        MyApplication.activityManager.startCommentActivity(this@PlayerActivity, it.source, it.id)
                    } else {
                        toast("暂无评论")
                    }
                }
            }
            // 下载歌曲
            ivDownload.setOnClickListener { toast("还在研究，要再等一段时间呀~") }
            // 喜欢音乐
            ivLike.setOnClickListener { playViewModel.likeMusic {
                runOnMainThread {
                    if (it) {
                        binding.ivLike.setImageDrawable(ContextCompat.getDrawable(this@PlayerActivity, R.drawable.mc_collectingview_red_heart))
                    } else {
                        binding.ivLike.setImageDrawable(ContextCompat.getDrawable(this@PlayerActivity, R.drawable.mz_titlebar_ic_collect))
                    }
                }
            } }
            // CD
            clCd.setOnClickListener {
                if (binding.clLyric.visibility == View.INVISIBLE && !isLandScape) {
                    AnimationUtil.fadeOut(binding.clCd, true)
                    AnimationUtil.fadeOut(binding.clMenu, true)
                    binding.clLyric.visibility = View.VISIBLE
                    slideBackEnabled = false
                }
            }
            // lyricView
            lyricView.setDraggable(true, object : OnPlayClickListener {
                override fun onPlayClick(time: Long): Boolean {
                    playViewModel.setProgress(time.toInt())
                    return true
                }
            })
            lyricView.setOnSingerClickListener(object : OnSingleClickListener {
                override fun onClick() {
                    if (!isLandScape) {
                        AnimationUtil.fadeIn(binding.clCd)
                        AnimationUtil.fadeIn(binding.clMenu)
                        binding.clLyric.visibility = View.INVISIBLE
                        slideBackEnabled = true
                    }
                }
            })
            edgeTransparentView.setOnClickListener {
                if (!isLandScape) {
                    AnimationUtil.fadeIn(binding.clCd)
                    AnimationUtil.fadeIn(binding.clMenu)
                    binding.clLyric.visibility = View.INVISIBLE
                    slideBackEnabled = true
                }
            }
            // 艺术家
            tvArtist.setOnClickListener {
                // 测试
                MyApplication.musicController.value?.getPlayingSongData()?.value?.let { standardSongData ->
                    if (standardSongData.source == SOURCE_NETEASE) {
                        standardSongData.artists?.let {
                            it[0].artistId?.let { artistId ->
                                MyApplication.activityManager.startArtistActivity(this@PlayerActivity, artistId)
                            }
                        }
                    } else {
                        toast("未找到信息")
                    }
                }
            }
            // 翻译更改
            ivTranslation.setOnClickListener {
                playViewModel.setLyricTranslation(playViewModel.lyricTranslation.value != true)
            }
            // 歌曲进度条变化的监听
            seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    // 判断是否为用户
                    if (fromUser) {
                        playViewModel.setProgress(progress)
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}

                override fun onStopTrackingTouch(seekBar: SeekBar?) {}

            })
            // 音量调节监听
            seekBarVolume.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    // 判断是否为用户
                    if (fromUser) {
                        playViewModel.currentVolume.value = progress
                        VolumeManager.setStreamVolume(progress)
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}

                override fun onStopTrackingTouch(seekBar: SeekBar?) {}

            })
        }
    }

    override fun initShowDialogListener() {
        binding.apply {
            // 均衡器
            ivEqualizer.setOnClickListener { SoundEffectDialog(this@PlayerActivity, this@PlayerActivity).show() }
            // 更多菜单
            ivMore.setOnClickListener { PlayerMenuMoreDialog(this@PlayerActivity).show() }
            // 播放列表
            ivList.setOnClickListener { PlaylistDialog(this@PlayerActivity).show() }
        }
    }

    override fun initBroadcastReceiver() {
        // Intent 过滤器，只接收 "com.dirror.foyou.MUSIC_BROADCAST" 标识广播
        val intentFilter = IntentFilter()
        intentFilter.addAction(MUSIC_BROADCAST_ACTION)
        musicBroadcastReceiver = MusicBroadcastReceiver()
        // 注册接收器
        registerReceiver(musicBroadcastReceiver, intentFilter)
    }

    override fun initObserver() {
        playViewModel.apply {
            // 播放模式的观察
            playMode.observe(this@PlayerActivity, {
                when (it) {
                    MusicService.MODE_CIRCLE -> binding.ivMode.setImageResource(R.drawable.ic_bq_player_mode_circle)
                    MusicService.MODE_REPEAT_ONE -> binding.ivMode.setImageResource(R.drawable.ic_bq_player_mode_repeat_one)
                    MusicService.MODE_RANDOM -> binding.ivMode.setImageResource(R.drawable.ic_bq_player_mode_random)
                }
            })
            // 当前歌曲的观察
            MyApplication.musicController.value?.getPlayingSongData()?.observe(this@PlayerActivity, {
                objectAnimator.cancel()
                objectAnimator.start()

                it?.let {
                    binding.tvName.text = it.name
                    binding.tvArtist.text = it.artists?.let { artists ->
                        parseArtist(artists)
                    }
                    // val url = MyApplication.cloudMusicManager.getPicture(imageUrl, CD_SIZE.dp())
                    SongPicture.getPlayerActivityCoverBitmap(this@PlayerActivity, it, CD_SIZE.dp()) { bitmap ->
                        // 设置 CD 图片
                        binding.ivCover.load(bitmap) {
                            placeholder(binding.ivCover.drawable)
                            transformations(CircleCropTransformation())
                            size(ViewSizeResolver(binding.ivCover))
                        }
                        // 设置 背景 图片
                        Glide.with(MyApplication.context)
                            .load(bitmap)
                            .placeholder(binding.ivBackground.drawable)
                            .apply(RequestOptions.bitmapTransform(BlurTransformation(BLUR_RADIUS, BLUR_SAMPLING)))
                            .into(binding.ivBackground)
                        // 设置色调
                        Palette.from(bitmap)
                            .clearFilters()
                            .generate { palette ->
                                palette?.let {
                                    val muteColor = if (DarkThemeUtil.isDarkTheme(this@PlayerActivity)) {
                                        palette.getLightMutedColor(PlayerViewModel.DEFAULT_COLOR)
                                    } else {
                                        palette.getDarkMutedColor(PlayerViewModel.DEFAULT_COLOR)
                                    }
                                    val vibrantColor = palette.getVibrantColor(PlayerViewModel.DEFAULT_COLOR)
                                    playViewModel.color.value = muteColor.colorMix(vibrantColor)
                                }
                            }
                    }
                    // 刷新歌词
                    playViewModel.updateLyric()
                    // 是否有红心
                    MyFavorite.isExist(it) { exist ->
                        runOnMainThread {
                            if (exist) {
                                binding.ivLike.setImageDrawable(ContextCompat.getDrawable(this@PlayerActivity, R.drawable.mc_collectingview_red_heart))
                            } else {
                                binding.ivLike.setImageDrawable(ContextCompat.getDrawable(this@PlayerActivity, R.drawable.mz_titlebar_ic_collect))
                            }
                        }
                    }
                }
            })
            // 播放状态的观察
            MyApplication.musicController.observe(this@PlayerActivity, { nullableController ->
                nullableController?.let { controller ->
                    controller.isPlaying().observe(this@PlayerActivity, {
                        if (it) {
                            binding.ivPlay.setImageResource(R.drawable.ic_pause_btn)
                            handler.sendEmptyMessageDelayed(MSG_PROGRESS, DELAY_MILLIS)
                            startRotateAlways()
                            binding.diffuseView.start()
                        } else {
                            binding.ivPlay.setImageResource(R.drawable.ic_play_btn)
                            handler.removeMessages(MSG_PROGRESS)
                            pauseRotateAlways()
                            binding.diffuseView.stop()
                        }
                    })
                }
            })
            // 总时长的观察
            duration.observe(this@PlayerActivity, {
                binding.seekBar.max = it
                binding.ttvDuration.setText(it)
            })
            // 进度的观察
            progress.observe(this@PlayerActivity, {
                binding.seekBar.progress = it
                binding.ttvProgress.setText(it)
                handler.sendEmptyMessageDelayed(MSG_PROGRESS, DELAY_MILLIS)
                // 更新歌词播放进度
                binding.lyricView.updateTime(it.toLong())
                // MyApplication.musicController.value?.updateTag(binding.lyricView.getCurrentLineLyricEntry()?.text)
            })
            // 翻译观察
            lyricTranslation.observe(this@PlayerActivity, {
                if (it == true) {
                    binding.ivTranslation.alpha = 1F
                } else {
                    binding.ivTranslation.alpha = 0.3F
                }
            })
            // 歌词观察
            lyricViewData.observe(this@PlayerActivity, {
                // 翻译歌词为空
                binding.ivTranslation.visibility = if (it.secondLyric.isEmpty()) {
                    View.GONE
                } else {
                    View.VISIBLE
                }
                if (playViewModel.lyricTranslation.value == true) {
                    binding.lyricView.loadLyric(it.lyric, it.secondLyric)
                } else {
                    binding.lyricView.loadLyric(it.lyric)
                }
            })
            // 音量观察
            currentVolume.observe(this@PlayerActivity, {
                binding.seekBarVolume.progress = it
            })
            // 颜色观察
            color.observe(this@PlayerActivity, {
                binding.apply {
                    ivPlay.setColorFilter(it)
                    ivLast.setColorFilter(it)
                    ivNext.setColorFilter(it)
                    tvName.setTextColor(it)
                    tvArtist.setTextColor(it)
                    ivBack.setColorFilter(it)
                    diffuseView.setColor(it)
                    lyricView.apply {
                        setCurrentColor(it)
                        setTimeTextColor(it)
                        setTimelineColor(it.colorAlpha(0.25f))
                        setTimelineTextColor(it)
                        setNormalColor(it.colorAlpha(0.5f))
                    }
                }

            })
//            currentLyric.observe(this@PlayerActivity, {
//                it?.let {
//
//                }
//            })
        }
    }

    /**
     * 开启旋转动画
     */
    private fun startRotateAlways() {
        objectAnimator.resume()
    }

    /**
     * 关闭旋转动画
     */
    private fun pauseRotateAlways() {
        playViewModel.rotation = binding.ivCover.rotation
        playViewModel.rotationBackground = binding.ivBackground.rotation
        objectAnimator.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        // 取消广播接收器的注册
        unregisterReceiver(musicBroadcastReceiver)
        // 清空 Handler 发送的所有消息，防止内存泄漏
        handler.removeCallbacksAndMessages(null)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(
            R.anim.anim_no_anim,
            R.anim.anim_slide_exit_bottom
        )
    }

    /**
     * 内部类 - 音乐广播接收器
     */
    inner class MusicBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // ViewModel 数据刷新
            playViewModel.refresh()
        }
    }

    /**
     * 监听音量物理按键
     */
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_UP -> {
                playViewModel.addVolume()
                return true
            }
            KeyEvent.KEYCODE_VOLUME_DOWN -> {
                playViewModel.reduceVolume()
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

}