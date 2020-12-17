package com.dirror.music.ui.activity

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.MotionEvent
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.drawable.toDrawable
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dirror.music.MyApplication
import com.dirror.music.R
import com.dirror.music.databinding.ActivityPlayerBinding
import com.dirror.music.music.standard.SongPicture
import com.dirror.music.music.standard.data.SOURCE_NETEASE
import com.dirror.music.music.standard.data.SOURCE_QQ
import com.dirror.music.music.standard.data.StandardSongData
import com.dirror.music.service.MusicService
import com.dirror.music.ui.dialog.PlayerMenuMoreDialog
import com.dirror.music.ui.dialog.PlaylistDialog
import com.dirror.music.util.*
import jp.wasabeef.glide.transformations.BlurTransformation

/**
 * 预计 2.0.0 版本移除
 */
@Deprecated("过时，使用新版 PlayerActivity")
@Suppress("DEPRECATION")
class PlayActivity : AppCompatActivity(), SeekBar.OnSeekBarChangeListener {

    companion object {
        private const val MSG_PROGRESS = 0 // Handle 消息，播放进度
        private const val MSG_LYRIC = 1 // Handle 消息，播放进度
    }

    private lateinit var binding: ActivityPlayerBinding

    private lateinit var musicBroadcastReceiver: MusicBroadcastReceiver // 音乐广播接收
    private var song: StandardSongData? = null
    private var nowProgress = 0 // 当前进度
    private var duration = 0 // 音乐总时长
    private var mode = MyApplication.mmkv.decodeInt(Config.PLAY_MODE, MusicService.MODE_CIRCLE)
    private val handler = @SuppressLint("HandlerLeak") object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MSG_PROGRESS -> updateProgress()
                MSG_LYRIC -> refreshLyricView()
            }
        }
    } // 可能泄漏，等待以后解决，Handle 过时问题


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initData()
        initView()
        initListener()
    }

    private fun initData() {
        val intentFilter = IntentFilter() // Intent 过滤器
        intentFilter.addAction("com.dirror.music.MUSIC_BROADCAST") // 只接收 "com.dirror.foyou.MUSIC_BROADCAST" 标识广播
        musicBroadcastReceiver = MusicBroadcastReceiver() //
        registerReceiver(musicBroadcastReceiver, intentFilter) // 注册接收器
    }

    private fun initView() {
        // 底部适配
        val navigationBarHeight = getNavigationBarHeight(this).toFloat()
        (binding.clBottom.layoutParams as ConstraintLayout.LayoutParams).apply{
            bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            bottomMargin = navigationBarHeight.toInt()
        }
        // 头部适配
        val statusBarHeight = getStatusBarHeight(window, this).toFloat()
        (binding.titleBar.layoutParams as ConstraintLayout.LayoutParams).apply{
            topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            topMargin = statusBarHeight.toInt()
        }
        // 背景图片
        binding.ivBackground.scaleY = 1.5f
        binding.ivBackground.scaleX = 2.5f

        binding.ivPlay.setColorFilter(Color.rgb(100, 100, 100))
        binding.ivLast.setColorFilter(Color.rgb(100, 100, 100))
        binding.ivNext.setColorFilter(Color.rgb(100, 100, 100))

        initPlayMode()
        // 获取现在歌曲信息
        getNowSongData()
        updateProgress()
        refreshPlayState()

        // 每次打开第一次获取歌词
        refreshLyricView()

    }

    /**
     * 初始化播放模式
     */
    private fun initPlayMode() {
        when (mode) {
            MusicService.MODE_CIRCLE -> binding.ivMode.setImageResource(R.drawable.ic_bq_player_mode_circle)
            MusicService.MODE_REPEAT_ONE -> binding.ivMode.setImageResource(R.drawable.ic_bq_player_mode_repeat_one)
            MusicService.MODE_RANDOM -> binding.ivMode.setImageResource(R.drawable.ic_bq_player_mode_random)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initListener() {
        binding.ivBack.setOnClickListener {
            finish()
        }

        // 播放或者暂停
        binding.ivPlay.setOnClickListener {
            // 更新
            MyApplication.musicBinderInterface?.changePlayState()
            refreshPlayState()
        }

        // 下一曲
        binding.ivNext.setOnClickListener {
            MyApplication.musicBinderInterface?.playNext()
        }

        // 上一曲
        binding.ivLast.setOnClickListener {
            MyApplication.musicBinderInterface?.playLast()
        }

        // 切换播放模式
        binding.ivMode.setOnClickListener {
            MyApplication.musicBinderInterface?.changePlayMode()
        }

        // 点击评论，跳转
        binding.ivComment.setOnClickListener {
            song?.let {
                MyApplication.activityManager.startCommentActivity(this, it.source, it.id)
            }
        }

        binding.ivEqualizer.setOnClickListener {
            IntentUtil.openEqualizer(this)
        }

        var oldY = 0f
        binding.clCd.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    oldY = event.y
                }
                MotionEvent.ACTION_UP -> {
                    if (event.y - oldY > 100f) {
                        finish()
                    } else {
                        if (binding.clLyric.visibility == View.INVISIBLE) {
                            AnimationUtil.fadeOut(binding.clCd, true)
                            AnimationUtil.fadeOut(binding.clMenu, true) // 菜单淡出
                            binding.clLyric.visibility = View.VISIBLE
                        }

                    }
                }
            }
            return@setOnTouchListener true
        }

        binding.clLyric.setOnClickListener {
            AnimationUtil.fadeIn(binding.clCd)
            AnimationUtil.fadeIn(binding.clMenu) // 菜单
            binding.clLyric.visibility = View.INVISIBLE
        }


        // 进度条变化的监听
        binding.seekBar.setOnSeekBarChangeListener(this)

        binding.ivDownload.setOnClickListener {
            toast("暂未开放")
        }

        binding.ivLike.setOnClickListener {
            song?.let {
                when (it.source) {
                    SOURCE_NETEASE -> {
                        MyApplication.cloudMusicManager.likeSong(it.id, {
                            toast("添加到我喜欢成功")
                        }, {
                            toast("添加到我喜欢失败")
                        })
                    }
                    SOURCE_QQ -> {
                        toast("暂不支持此音源")
                    }
                }
            }
        }

        binding.ivMore.setOnClickListener {
            showPlayerMenuMoreDialog()
        }

        binding.ivList.setOnClickListener {
            PlaylistDialog(this).show()
        }
    }

    private fun showPlayerMenuMoreDialog() {
        PlayerMenuMoreDialog(this).apply {
            show()
        }
    }


    override fun finish() {
        super.finish()
        overridePendingTransition(
            R.anim.anim_no_anim,
            R.anim.anim_slide_exit_bottom
        )
    }

    private fun refreshPlayState() {
        // 播放状态可能为空
        if (MyApplication.musicBinderInterface?.getPlayState() == true) {
            // 播放
            binding.ivPlay.setImageResource(R.drawable.ic_pause_btn)
            startRotateAlways()
            // 开启进度更新
            handler.sendEmptyMessageDelayed(MSG_PROGRESS, 500)
            handler.sendEmptyMessage(MSG_LYRIC)
        } else {
            // 暂停
            binding.ivPlay.setImageResource(R.drawable.ic_play_btn)
            pauseRotateAlways()
            // 停止更新进度
            handler.removeMessages(MSG_PROGRESS)
            handler.removeMessages(MSG_LYRIC)
        }
    }

    private var rotation = 0f
    private var rotationBackground = 0f
    private fun pauseRotateAlways() {
        rotation = binding.ivCover.rotation
        rotationBackground = binding.ivBackground.rotation
        loge("rotation:$rotation")
        loge("rotationB:$rotationBackground")
        objectAnimator.pause()
        objectAnimatorBackground.pause()
    }

    private val objectAnimator: ObjectAnimator by lazy {
        ObjectAnimator.ofFloat(binding.ivCover, "rotation", rotation, rotation + 360f).apply {
            interpolator = LinearInterpolator()
            duration = 25000
            repeatCount = -1
            start()
        }
    }

    private val objectAnimatorBackground: ObjectAnimator by lazy {
        ObjectAnimator.ofFloat(binding.ivBackground, "rotation", rotationBackground, rotationBackground + 360f).apply {
            interpolator = LinearInterpolator()
            duration = 50000
            repeatCount = -1
            start()
        }
    }

    private fun startRotateAlways() {
        objectAnimator.resume()
        objectAnimatorBackground.resume()
    }

    override fun onDestroy() {
        super.onDestroy()
        // 取消广播接收器的注册
        unregisterReceiver(musicBroadcastReceiver)
        // 清空 Handler 发送的所有消息，防止内存泄漏
        handler.removeCallbacksAndMessages(null)
    }

    inner class MusicBroadcastReceiver : BroadcastReceiver() {
        // private var mode = MusicService.MODE_CIRCLE // 默认
        override fun onReceive(context: Context, intent: Intent) {
            getNowSongData()
            refreshPlayState()
            // 当前播放模式改不了，修改图标
            refreshPlayMode()
        }
    }

    private fun refreshPlayMode() {
        if (mode != MyApplication.musicBinderInterface?.getPlayMode() ?: mode) {
            // 赋值
            mode = MyApplication.musicBinderInterface?.getPlayMode() ?: mode
            when (mode) {
                MusicService.MODE_CIRCLE -> {
                    binding.ivMode.setImageResource(R.drawable.ic_bq_player_mode_circle)
                    // toast("列表循环")
                }
                MusicService.MODE_REPEAT_ONE -> {
                    binding.ivMode.setImageResource(R.drawable.ic_bq_player_mode_repeat_one)
                    // toast("单曲循环")
                }
                MusicService.MODE_RANDOM -> {
                    binding.ivMode.setImageResource(R.drawable.ic_bq_player_mode_random)
                    // toast("随机播放")
                }
            }
        }
    }

    /**
     * 更新进度
     */
    private fun updateProgress() {
        // 获取当前进度
        nowProgress = MyApplication.musicBinderInterface?.getProgress() ?: 0
        duration = MyApplication.musicBinderInterface?.getDuration() ?: duration
        // 设置进度条最大值
        binding.seekBar.max = duration
        // 更新进度条
        binding.seekBar.progress = nowProgress
        binding.tvProgress.text = TimeUtil.parseDuration(nowProgress)
        binding.tvDuration.text = TimeUtil.parseDuration(duration)
        // 定时获取进度
        handler.sendEmptyMessageDelayed(MSG_PROGRESS, 500)
    }

    // 刷新歌词
    private fun refreshLyricView() {
        val song = MyApplication.musicBinderInterface?.getNowSongData()
        song?.let {
            nowProgress = MyApplication.musicBinderInterface?.getProgress() ?: 0 // 当前时长
            duration = MyApplication.musicBinderInterface?.getDuration() ?: duration // 歌曲时长

//            binding.lyricView.setLyricId(song)
//            binding.lyricView.setSongDuration(duration)
//            // 更新歌词播放进度
//            binding.lyricView.updateProgress(nowProgress)

            // 更新
            handler.sendEmptyMessage(MSG_LYRIC)
        }
    }

    /**
     * 进度改变的回调
     * @param p1 改变之后的进度
     * @param p2 true 通过用户手指拖动 false 通过代码方式改变进度
     */
    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
        // 判断是否为用户
        if (p2) {
            MyApplication.musicBinderInterface?.setProgress(p1)
        }
    }

    // 手指触摸
    override fun onStartTrackingTouch(p0: SeekBar?) {

    }

    // 手指离开
    override fun onStopTrackingTouch(p0: SeekBar?) {
        updateProgress()
    }

    /**
     * 获取当前音乐
     */
    private fun getNowSongData() {
        song = MyApplication.musicBinderInterface?.getNowSongData()
        song?.let { standardSongData ->
            SongPicture.getSongPicture(standardSongData, SongPicture.TYPE_LARGE) {
                loadPicture(it)
            }
            binding.tvName.text = standardSongData.name
            binding.tvArtist.text = standardSongData.artists?.let {
                parseArtist(it)
            }
        }
    }

    /**
     * 加载图片
     */
    private fun loadPicture(bitmap: Bitmap) {
        runOnUiThread {
            val drawable = bitmap.toDrawable(resources)
            binding.ivCover.setImageDrawable(drawable)
            Glide.with(MyApplication.context)
                .load(drawable)
                .placeholder(binding.ivBackground.drawable)
                .apply(RequestOptions.bitmapTransform(BlurTransformation(15, 5)))
                .into(binding.ivBackground)
            Palette.from(bitmap)
                .clearFilters()
                .generate { palette ->
                    if (palette?.vibrantSwatch != null) {
                        palette.vibrantSwatch?.apply {
                            rgb.let {
                                binding.ivPlay.setColorFilter(it)
                                binding.ivLast.setColorFilter(it)
                                binding.ivNext.setColorFilter(it)

                                // 设置 Progress 的颜色
                                binding.seekBar.indeterminateDrawable.colorFilter = PorterDuffColorFilter(it, PorterDuff.Mode.SRC_IN)
                            }
                        }
                    } else {
                        val color = Color.rgb(100, 100, 100)
                        binding.ivPlay.setColorFilter(color)
                        binding.ivLast.setColorFilter(color)
                        binding.ivNext.setColorFilter(color)
                        loge("获取 palette 失败")
                    }

                }
        }

    }
}