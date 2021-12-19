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

package com.dirror.music.ui.player

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.*
import android.view.animation.LinearInterpolator
import android.widget.SeekBar
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.updateLayoutParams
import androidx.palette.graphics.Palette
import coil.load
import coil.size.ViewSizeResolver
import coil.transform.BlurTransformation
import com.dirror.lyricviewx.OnPlayClickListener
import com.dirror.lyricviewx.OnSingleClickListener
import com.dirror.music.App
import com.dirror.music.R
import com.dirror.music.audio.VolumeManager
import com.dirror.music.databinding.ActivityPlayerBinding
import com.dirror.music.music.local.MyFavorite
import com.dirror.music.music.standard.data.SOURCE_LOCAL
import com.dirror.music.music.standard.data.SOURCE_NETEASE
import com.dirror.music.service.base.BaseMediaService
import com.dirror.music.ui.base.SlideBackActivity
import com.dirror.music.ui.dialog.PlayerMenuMoreDialog
import com.dirror.music.ui.dialog.PlaylistDialog
import com.dirror.music.ui.dialog.SoundEffectDialog
import com.dirror.music.ui.dialog.TimingOffDialog
import com.dirror.music.util.*
import com.dirror.music.util.asColor
import com.dirror.music.util.asDrawable
import com.dirror.music.util.colorAlpha
import com.dso.ext.colorMix

/**
 * PlayerActivity
 *
 * @author Moriafly
 * @since 2020年12月15日18:35:46
 */
class PlayerActivity : SlideBackActivity() {

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
                if (App.musicController.value?.isPlaying()?.value == true) {
                    playViewModel.refreshProgress()
                }
            }
        }
    }

    // CD 旋转动画
    private val objectAnimator: ObjectAnimator by lazy {
        ObjectAnimator.ofFloat(binding.includePlayerCover.root, ANIMATION_PROPERTY_NAME, 0f, 360f).apply {
            interpolator = LinearInterpolator()
            duration = DURATION_CD
            repeatCount = ANIMATION_REPEAT_COUNTS
            start()
        }
    }

    private var previousBitmap: Bitmap? = null

    override fun initBinding() {
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        binding.root.setOnApplyWindowInsetsListener { _, insets ->
            playViewModel.systemWindowInsetTop.value = insets.systemWindowInsetTop
            playViewModel.systemWindowInsetLeft.value = insets.systemWindowInsetLeft
            playViewModel.systemWindowInsetRight.value = insets.systemWindowInsetRight
            playViewModel.systemWindowInsetBottom.value = insets.systemWindowInsetBottom
            insets
        }
        setContentView(binding.root)
    }

    override fun initView() {
        if (DarkThemeUtil.isDarkTheme(this)) {
            playViewModel.normalColor.value = Color.WHITE
        } else {
            playViewModel.normalColor.value = Color.rgb(50, 50, 50)
        }
        if (App.mmkv.decodeBool(Config.NETEASE_GOOD_COMMENTS, true)) {
            binding.ivComment.visibility = View.VISIBLE
        } else {
            binding.ivComment.visibility = View.GONE
        }
        // window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        // 设置 SlideBackLayout
        bindSlide(this, binding.clBase)
        // 屏幕旋转
        val configuration = this.resources.configuration //获取设置的配置信息
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            isLandScape = true
//            // 横屏隐藏状态栏
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
//                window.insetsController?.hide(WindowInsetsController.BEHAVIOR_SHOW_BARS_BY_SWIPE)
//            }
        }


        binding.apply {
            // 时长右对齐
            ttvDuration.setAlignRight()
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

        PlayerViewModel.fragmentManager = supportFragmentManager
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
            ivMode.setOnClickListener {
                singleClick {
                    playViewModel.changePlayMode()
                }
            }
            // 定时关闭
            ivSleepTimer.setOnClickListener {
                TimingOffDialog(this@PlayerActivity).show()
            }
            // 评论
            ivComment.setOnClickListener {
                App.musicController.value?.getPlayingSongData()?.value?.let {
                    if (it.source != SOURCE_LOCAL) {
                        App.activityManager.startCommentActivity(
                            this@PlayerActivity,
                            it.source ?: SOURCE_NETEASE,
                            it.id ?: ""
                        )
                    } else {
                        toast("暂无评论")
                    }
                }
            }
            if (!isLandScape) {
                includePlayerCover.root.setOnLongClickListener {
                    startActivity(Intent(this@PlayerActivity, SongCoverActivity::class.java))
                    overridePendingTransition(
                        R.anim.anim_alpha_enter,
                        R.anim.anim_no_anim,
                    )
                    return@setOnLongClickListener true
                }
            }
            // 喜欢音乐
            ivLike.setOnClickListener {
                playViewModel.likeMusic {
                    runOnMainThread {
                        playViewModel.heart.value = it
                    }
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

            if (!isLandScape) {
                includePlayerCover.root.setOnClickListener {
                    if (slideBackEnabled) {
                        AnimationUtil.fadeOut(binding.clCd, true)
                        AnimationUtil.fadeOut(binding.clMenu, true)
                        binding.clLyric.visibility = View.VISIBLE
                        slideBackEnabled = false
                    }
                }
                clCd.setOnClickListener {
                    if (slideBackEnabled) {
                        AnimationUtil.fadeOut(binding.clCd, true)
                        AnimationUtil.fadeOut(binding.clMenu, true)
                        binding.clLyric.visibility = View.VISIBLE
                        slideBackEnabled = false
                    }
                }
            }
            lyricView.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    slideBackEnabled = false
                }
                return@setOnTouchListener false
            }
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
                App.musicController.value?.getPlayingSongData()?.value?.let { standardSongData ->
                    if (standardSongData.source == SOURCE_NETEASE) {
                        standardSongData.artists?.let {
                            it[0].artistId?.let { artistId ->
                                App.activityManager.startArtistActivity(this@PlayerActivity, artistId)
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
            ivEqualizer.setOnClickListener {
                singleClick {
                    SoundEffectDialog(this@PlayerActivity, this@PlayerActivity).show()
                }
            }
            // 更多菜单
            ivMore.setOnClickListener {
                singleClick {
                    PlayerMenuMoreDialog(this@PlayerActivity).show()
                }
            }
            // 播放列表
            ivList.setOnClickListener {
                singleClick {
                    PlaylistDialog().show(supportFragmentManager, null)
                }
            }
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
        App.musicController.observe(this@PlayerActivity, { nullableController ->
            nullableController?.let { controller ->
                // 当前歌曲的观察
                controller.getPlayingSongData().observe(this@PlayerActivity, {
                    objectAnimator.cancel()
                    objectAnimator.start()

                    it?.let {
                        binding.tvName.text = it.name
                        binding.tvArtist.text = it.artists?.let { artists ->
                            parseArtist(artists)
                        }
                        // 刷新歌词
                        playViewModel.updateLyric()
                        // 是否有红心
                        MyFavorite.isExist(it) { exist ->
                            runOnMainThread {
                                playViewModel.heart.value = exist
                            }
                        }
                    }
                })
                // 封面观察
                controller.getPlayerCover().observe(this@PlayerActivity, { bitmap ->
                    runOnMainThread {
                        // 设置 CD 图片
                        binding.includePlayerCover.ivCover.load(bitmap) {
                            placeholder(previousBitmap?.toDrawable(resources))
                            size(ViewSizeResolver(binding.includePlayerCover.ivCover))
                            crossfade(500)
                        }
                        binding.ivLyricsBackground.load(bitmap) {
                            size(ViewSizeResolver(binding.includePlayerCover.ivCover))
                            transformations(BlurTransformation(this@PlayerActivity, 15F, 30F))
                            crossfade(500)
                        }
                        previousBitmap = bitmap

                        // 设置色调
                        bitmap?.let {
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
                                        playViewModel.normalColor.value =  if (DarkThemeUtil.isDarkTheme(this@PlayerActivity)) {
                                            muteColor.colorMix(vibrantColor, Color.WHITE, Color.WHITE, Color.WHITE)
                                        } else {
                                            muteColor.colorMix(vibrantColor, Color.BLACK)
                                        }
                                        playViewModel.color.value =  if (DarkThemeUtil.isDarkTheme(this@PlayerActivity)) {
                                            vibrantColor // muteColor.colorMix(vibrantColor)
                                        } else {
                                            vibrantColor // muteColor.colorMix(vibrantColor)
                                        }
                                    }
                                }
                        }
                    }

                })
                controller.isPlaying().observe(this@PlayerActivity, {
                    if (it) {
                        binding.ivPlay.contentDescription = getString(R.string.pause_music)
                        binding.ivPlay.setImageResource(R.drawable.ic_player_playing)
                        handler.sendEmptyMessageDelayed(MSG_PROGRESS, DELAY_MILLIS)
                        startRotateAlways()
                        // binding.diffuseView.start()
                    } else {
                        binding.ivPlay.contentDescription = getString(R.string.play_music)
                        binding.ivPlay.setImageResource(R.drawable.ic_player_paused)
                        handler.removeMessages(MSG_PROGRESS)
                        pauseRotateAlways()
                        // binding.diffuseView.stop()
                    }
                })
                controller.personFM.observe(this, {
                    with(binding) {
                        if (it) {
                            ivMode.isClickable = false
                            ivLast.isClickable = false
                            ivList.isClickable = false
                            tvTag?.text = getString(R.string.personal_fm)
                        } else {
                            ivMode.isClickable = true
                            ivLast.isClickable = true
                            ivList.isClickable = true
                            tvTag?.text = ""
                        }
                    }
                })
            }
        })
        playViewModel.apply {
            // 播放模式的观察
            playMode.observe(this@PlayerActivity, {
                binding.ivMode.contentDescription = this.getModeContentDescription(it)
                when (it) {
                    BaseMediaService.MODE_CIRCLE -> binding.ivMode.setImageResource(R.drawable.ic_player_circle)
                    BaseMediaService.MODE_REPEAT_ONE -> binding.ivMode.setImageResource(R.drawable.ic_player_repeat_one)
                    BaseMediaService.MODE_RANDOM -> binding.ivMode.setImageResource(R.drawable.ic_player_random)
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
                    val darkMode = DarkThemeUtil.isDarkTheme(this@PlayerActivity)
                    val secondColor = if (darkMode) {
                        Color.rgb(45, 45, 45)
                    } else {
                        Color.WHITE
                    }
                    val mixColor = if (darkMode) {
                        it.colorMix(secondColor, secondColor, secondColor)
                    } else {
                        it.colorMix(Color.WHITE, Color.WHITE, Color.WHITE)
                    }

                }

            })

            normalColor.observe(this@PlayerActivity, {
                binding.lyricView.apply {
                    setCurrentColor(it)
                    setTimeTextColor(it)
                    setTimelineColor(it.colorAlpha(0.25f))
                    setTimelineTextColor(it)
                    setNormalColor(it.colorAlpha(0.35f))
                }
                with(binding) {
                    ttvProgress.textColor = it
                    ttvDuration.textColor = it
                    tvTag?.setTextColor(it)

                    ivPlay.setColorFilter(it)
                    ivLast.setColorFilter(it)
                    ivNext.setColorFilter(it)
                    tvName.setTextColor(it)
                    tvArtist.setTextColor(it)
                    ivBack.setColorFilter(it)

                    ivEqualizer.setColorFilter(it)
                    ivSleepTimer.setColorFilter(it)
                    ivLike.setColorFilter(it)
                    ivComment.setColorFilter(it)
                    ivMore.setColorFilter(it)

                    ivMode.setColorFilter(it)
                    ivList.setColorFilter(it)

                    seekBar.thumb.colorFilter = PorterDuffColorFilter(it, PorterDuff.Mode.SRC_IN)
                    seekBar.progressDrawable.colorFilter = PorterDuffColorFilter(it, PorterDuff.Mode.SRC_IN)
                    seekBarVolume.thumb.colorFilter = PorterDuffColorFilter(it, PorterDuff.Mode.SRC_IN)
                    seekBarVolume.progressDrawable.colorFilter = PorterDuffColorFilter(it, PorterDuff.Mode.SRC_IN)

                    ivVolume.setColorFilter(it)
                    if (playViewModel.heart.value == true) {
                        playViewModel.heart.value = true
                    }
                }
            })

            heart.observe(this@PlayerActivity, {
                if (it) {
                    binding.ivLike.setImageDrawable(R.drawable.ic_player_heart.asDrawable(this@PlayerActivity))
                    binding.ivLike.setColorFilter(R.color.colorAppThemeColor.asColor(this@PlayerActivity))
                } else {
                    binding.ivLike.setImageDrawable(R.drawable.ic_player_heart_outline.asDrawable(this@PlayerActivity))
                    playViewModel.normalColor.value?.let { it1 -> binding.ivLike.setColorFilter(it1) }
                }
            })
            systemWindowInsetTop.observe(this@PlayerActivity, { top ->
                // 页面状态栏适配
                binding.titleBar?.let {
                    (it.layoutParams as ConstraintLayout.LayoutParams).apply {
                        topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                        topMargin = top
                    }
                }
                binding.llBase?.let {
                    it.updateLayoutParams<ConstraintLayout.LayoutParams> {
                        topMargin = top
                    }
                }
            })
            systemWindowInsetBottom.observe(this@PlayerActivity, { bottom ->
                if (isLandScape) {
                    binding.llBase?.let {
                        it.updateLayoutParams<ConstraintLayout.LayoutParams> {
                            bottomMargin = bottom
                        }
                    }
                } else {
                    binding.clBottom.updateLayoutParams<ConstraintLayout.LayoutParams> {
                        bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                        bottomMargin = bottom
                    }
                }

            })
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
        playViewModel.rotation = binding.includePlayerCover.root.rotation
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

    companion object {
        private const val MUSIC_BROADCAST_ACTION = "com.dirror.music.MUSIC_BROADCAST"
        private const val DELAY_MILLIS = 500L

        // Handle 消息，播放进度
        private const val MSG_PROGRESS = 0

        // 动画循环时长
        private const val DURATION_CD = 32_000L
        private const val ANIMATION_REPEAT_COUNTS = -1
        private const val ANIMATION_PROPERTY_NAME = "rotation"
    }

}