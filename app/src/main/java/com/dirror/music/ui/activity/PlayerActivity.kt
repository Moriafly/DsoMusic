package com.dirror.music.ui.activity

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.widget.SeekBar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.dirror.music.MyApplication
import com.dirror.music.R
import com.dirror.music.databinding.ActivityPlayBinding
import com.dirror.music.service.MusicService
import com.dirror.music.ui.viewmodel.PlayerViewModel
import com.dirror.music.util.*

/**
 * 新版 PlayActivity
 * 用来取代原先的 PlayActivity，预计 2.0.0 版本上线
 * @目标 更加清晰方便管理，加入 PlayerViewModel
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
        private const val MSG_LYRIC = 1
        private const val BACKGROUND_SCALE_Y = 1.5F
        private const val BACKGROUND_SCALE_X = 2.5F
        private val DEFAULT_COLOR = Color.rgb(100, 100, 100)
    }

    // 目前还是 activity_play 布局
    private lateinit var binding: ActivityPlayBinding

    // 音乐广播接收者
    private lateinit var musicBroadcastReceiver: MusicBroadcastReceiver

    // ViewModel 数据和视图分离
    private val playViewModel: PlayerViewModel by viewModels()

    // Looper + Handler，目前还不会
    private val handler = @SuppressLint("HandlerLeak") object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MSG_PROGRESS -> {
                    loge("refreshProgress()")
                    playViewModel.refreshProgress()
                }
                // MSG_LYRIC -> refreshLyricView()
            }
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
                it?.let {
                    binding.tvName.text = it.name
                    binding.tvArtist.text = it.artists?.let { artists ->
                        parseArtist(artists)
                    }
                }
            })
            // 播放状态的观察
            playState.observe(this@PlayerActivity, {
                if (it) {
                    binding.ivPlay.setImageResource(R.drawable.ic_pause_btn)
                    handler.sendEmptyMessageDelayed(MSG_PROGRESS, DELAY_MILLIS)
                } else {
                    binding.ivPlay.setImageResource(R.drawable.ic_play_btn)
                    handler.removeMessages(MSG_PROGRESS)

                    // handler.removeMessages(PlayActivity.MSG_LYRIC)
                }
            })
            // 进度的观察
            progress.observe(this@PlayerActivity, {
                binding.seekBar.progress = it
                binding.tvProgress.text = TimeUtil.parseDuration(it)
                handler.sendEmptyMessageDelayed(MSG_PROGRESS, DELAY_MILLIS)
            })
            // 总时长的观察
            duration.observe(this@PlayerActivity, {
                binding.seekBar.max = it
                binding.tvDuration.text = TimeUtil.parseDuration(it)
            })
        }

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
     * 内部类
     * 音乐广播接收器
     */
    inner class MusicBroadcastReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            // ViewModel 数据刷新
            playViewModel.refresh()
        }

    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        // 判断是否为用户
        if (fromUser) {
            playViewModel.setProgress(progress)
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) { }

    override fun onStopTrackingTouch(seekBar: SeekBar?) { }


}