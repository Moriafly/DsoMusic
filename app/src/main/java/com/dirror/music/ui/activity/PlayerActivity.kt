package com.dirror.music.ui.activity

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.SeekBar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dirror.music.MyApplication
import com.dirror.music.R
import com.dirror.music.databinding.ActivityPlayerBinding
import com.dirror.music.music.standard.SearchLyric
import com.dirror.music.music.standard.SongPicture
import com.dirror.music.service.MusicService
import com.dirror.music.ui.dialog.PlayerMenuMoreDialog
import com.dirror.music.ui.dialog.PlaylistDialog
import com.dirror.music.ui.viewmodel.PlayerViewModel
import com.dirror.music.util.*
import com.dirror.music.widget.SlideBackLayout
import jp.wasabeef.glide.transformations.BlurTransformation

/**
 * 新版 PlayerActivity
 * 用来取代原先的 PlayActivity，1.6.1 版本已上线
 * 更加清晰方便管理，加入 PlayerViewModel
 * @author Moriafly
 * @since 2020年12月15日18:35:46
 */
class PlayerActivity : AppCompatActivity(), SeekBar.OnSeekBarChangeListener {

    companion object {
        private const val MUSIC_BROADCAST_ACTION = "com.dirror.music.MUSIC_BROADCAST"
        private const val DELAY_MILLIS = 500L

        // Handle 消息，播放进度
        private const val MSG_PROGRESS = 0

        // Handle 消息，播放进度
        private const val BACKGROUND_SCALE_Y = 1.5F
        private const val BACKGROUND_SCALE_X = 2.5F
        private val DEFAULT_COLOR = Color.rgb(100, 100, 100)
        private const val CD_SIZE = 240
    }

    private lateinit var binding: ActivityPlayerBinding

    // 音乐广播接收者
    private lateinit var musicBroadcastReceiver: MusicBroadcastReceiver

    // ViewModel 数据和视图分离
    private val playViewModel: PlayerViewModel by viewModels()

    // Looper + Handler
    private val handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MSG_PROGRESS -> {
                    playViewModel.refreshProgress()
                }
            }
        }
    }

    // SlideBackLayout 拖拽关闭 Activity
    private lateinit var slideBackLayout: SlideBackLayout

    // CD 旋转动画
    private val objectAnimator: ObjectAnimator by lazy {
        ObjectAnimator.ofFloat(binding.ivCover, "rotation", playViewModel.rotation, playViewModel.rotation + 360f).apply {
            interpolator = LinearInterpolator()
            duration = 25000
            repeatCount = -1
            start()
        }
    }

    // 背景 旋转动画
    private val objectAnimatorBackground: ObjectAnimator by lazy {
        ObjectAnimator.ofFloat(
            binding.ivBackground,
            "rotation",
            playViewModel.rotationBackground,
            playViewModel.rotationBackground + 360f
        ).apply {
            interpolator = LinearInterpolator()
            duration = 50000
            repeatCount = -1
            start()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // 设置 SlideBackLayout
        slideBackLayout = SlideBackLayout(this, binding.clBase)
        slideBackLayout.bind()
        // 进度条变化的监听
        binding.seekBar.setOnSeekBarChangeListener(this)
        // 初始化广播接受者
        initBroadcastReceiver()
        // 初始化视图
        initView()
        // 初始化监听
        initListener()
        // 初始化 ViewModel 观察
        initViewModelObserve()
    }

    /**
     * 初始化广播接受者
     */
    private fun initBroadcastReceiver() {
        // Intent 过滤器，只接收 "com.dirror.foyou.MUSIC_BROADCAST" 标识广播
        val intentFilter = IntentFilter()
        intentFilter.addAction(MUSIC_BROADCAST_ACTION)
        musicBroadcastReceiver = MusicBroadcastReceiver()
        // 注册接收器
        registerReceiver(musicBroadcastReceiver, intentFilter)
    }

    /**
     * 初始化视图
     */
    private fun initView() {
        // 时长右对齐
        binding.ttvDuration.setAlignRight()
        // 页面状态栏适配
        (binding.titleBar.layoutParams as ConstraintLayout.LayoutParams).apply {
            topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            topMargin = getStatusBarHeight(window, this@PlayerActivity)
        }
        // 页面导航栏适配
        (binding.clBottom.layoutParams as ConstraintLayout.LayoutParams).apply {
            bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            bottomMargin = getNavigationBarHeight(this@PlayerActivity)
        }
        // 背景图片放大
        binding.ivBackground.scaleY = BACKGROUND_SCALE_Y
        binding.ivBackground.scaleX = BACKGROUND_SCALE_X
        // 开始 / 暂停、上一曲和下一曲设置初始颜色
        binding.ivPlay.setColorFilter(DEFAULT_COLOR)
        binding.ivLast.setColorFilter(DEFAULT_COLOR)
        binding.ivNext.setColorFilter(DEFAULT_COLOR)
    }

    /**
     * 初始化监听
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun initListener() {
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
            // 均衡器
            ivEqualizer.setOnClickListener { IntentUtil.openEqualizer(this@PlayerActivity) }
            // 评论
            ivComment.setOnClickListener {
                playViewModel.standardSongData.value?.let {
                    MyApplication.activityManager.startCommentActivity(this@PlayerActivity, it.source, it.id)
                }
            }
            // 下载歌曲
            ivDownload.setOnClickListener { toast("暂未开放") }
            // 更多菜单
            ivMore.setOnClickListener { PlayerMenuMoreDialog(this@PlayerActivity).show() }
            // 播放列表
            ivList.setOnClickListener { PlaylistDialog(this@PlayerActivity).show() }
            // 喜欢音乐
            ivLike.setOnClickListener { playViewModel.likeMusic() }
            // CD
            clCd.setOnClickListener {
                if (binding.clLyric.visibility == View.INVISIBLE) {
                    AnimationUtil.fadeOut(binding.clCd, true)
                    AnimationUtil.fadeOut(binding.clMenu, true)
                    binding.clLyric.visibility = View.VISIBLE
                    slideBackLayout.viewEnabled = false
                }
            }
            // lyricView
            lyricView.setDraggable(true) { time ->
                playViewModel.setProgress(time.toInt())
                true
            }
            lyricView.setOnSingerClickListener {
                AnimationUtil.fadeIn(binding.clCd)
                AnimationUtil.fadeIn(binding.clMenu)
                binding.clLyric.visibility = View.INVISIBLE
                slideBackLayout.viewEnabled = true
            }
            clLyric.setOnClickListener {
                AnimationUtil.fadeIn(binding.clCd)
                AnimationUtil.fadeIn(binding.clMenu)
                binding.clLyric.visibility = View.INVISIBLE
                slideBackLayout.viewEnabled = true
            }
        }
    }

    /**
     * 初始化观察者
     */
    private fun initViewModelObserve() {
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
            standardSongData.observe(this@PlayerActivity, {
                // toast("歌曲改变")
                it?.let {
                    binding.tvName.text = it.name
                    binding.tvArtist.text = it.artists?.let { artists ->
                        parseArtist(artists)
                    }
                    // val url = MyApplication.cloudMusicManager.getPicture(imageUrl, CD_SIZE.dp())
                    SongPicture.getPlayerActivityCoverBitmap(it, CD_SIZE.dp()) { bitmap ->
                        // 设置 CD 图片
                        binding.ivCover.setImageBitmap(bitmap)
                        // 设置 背景 图片
                        Glide.with(MyApplication.context)
                            .load(bitmap)
                            .placeholder(binding.ivBackground.drawable)
                            .apply(RequestOptions.bitmapTransform(BlurTransformation(15, 5)))
                            .into(binding.ivBackground)
                        // 设置色调
                        Palette.from(bitmap)
                            .clearFilters()
                            .generate { palette ->
                                if (palette?.vibrantSwatch != null) {
                                    palette.vibrantSwatch?.rgb?.let { rgb ->
                                        binding.ivPlay.setColorFilter(rgb)
                                        binding.ivLast.setColorFilter(rgb)
                                        binding.ivNext.setColorFilter(rgb)
                                    }
                                } else {
                                    binding.ivPlay.setColorFilter(DEFAULT_COLOR)
                                    binding.ivLast.setColorFilter(DEFAULT_COLOR)
                                    binding.ivNext.setColorFilter(DEFAULT_COLOR)
                                }
                            }
                    }
                    // 更改歌词
                    SearchLyric.getLyricString(it) { string ->
                        binding.lyricView.loadLrc(string)
                    }
                }
            })
            // 播放状态的观察
            playState.observe(this@PlayerActivity, {
                if (it) {
                    binding.ivPlay.setImageResource(R.drawable.ic_pause_btn)
                    startRotateAlways()
                    handler.sendEmptyMessageDelayed(MSG_PROGRESS, DELAY_MILLIS)
                } else {
                    binding.ivPlay.setImageResource(R.drawable.ic_play_btn)
                    pauseRotateAlways()
                    handler.removeMessages(MSG_PROGRESS)
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
        }

    }

    /**
     * 开启旋转动画
     */
    private fun startRotateAlways() {
        objectAnimator.resume()
        objectAnimatorBackground.resume()
    }

    /**
     * 关闭旋转动画
     */
    private fun pauseRotateAlways() {
        playViewModel.rotation = binding.ivCover.rotation
        playViewModel.rotationBackground = binding.ivBackground.rotation
        objectAnimator.pause()
        objectAnimatorBackground.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        // 取消广播接收器的注册
        unregisterReceiver(musicBroadcastReceiver)
        // 清空 Handler 发送的所有消息，防止内存泄漏
        handler.removeCallbacksAndMessages(null)
    }

    /**
     * 内部类
     * 音乐广播接收器
     */
    inner class MusicBroadcastReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            // ViewModel 数据刷新
            playViewModel.refresh()
        }

    }

    /**
     * 滚动条监听
     */
    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        // 判断是否为用户
        if (fromUser) {
            playViewModel.setProgress(progress)
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {}

    override fun onStopTrackingTouch(seekBar: SeekBar?) {}

}