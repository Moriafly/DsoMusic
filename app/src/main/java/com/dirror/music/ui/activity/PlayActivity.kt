package com.dirror.music.ui.activity

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.os.Message
import android.view.animation.LinearInterpolator
import android.widget.SeekBar
import com.dirror.music.music.CloudMusic
import com.dirror.music.MyApplication
import com.dirror.music.R
import com.dirror.music.ui.dialog.PlayerMenuMoreDialog
import com.dirror.music.music.standard.StandardSongData
import com.dirror.music.service.MusicService
import com.dirror.music.ui.base.BaseActivity
import com.dirror.music.ui.dialog.PlaylistDialog
import com.dirror.music.util.*
import kotlinx.android.synthetic.main.activity_play.*


private const val MSG_PROGRESS = 0 // Handle 消息，播放进度

class PlayActivity : BaseActivity(), SeekBar.OnSeekBarChangeListener {

    private lateinit var musicBroadcastReceiver: MusicBroadcastReceiver // 音乐广播接收
    private var duration = 0 // 音乐总时长
    private var mode = StorageUtil.getInt(StorageUtil.PlAY_MODE, MusicService.MODE_CIRCLE)
    private val handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MSG_PROGRESS -> updateProgress()
            }
        }
    } // 可能泄漏，等待以后解决，Handle 过时问题

    override fun getLayoutId(): Int {
        return R.layout.activity_play
    }

    override fun initData() {
        val intentFilter = IntentFilter() // Intent 过滤器
        intentFilter.addAction("com.dirror.music.MUSIC_BROADCAST") // 只接收 "com.dirror.foyou.MUSIC_BROADCAST" 标识广播
        musicBroadcastReceiver = MusicBroadcastReceiver() //
        registerReceiver(musicBroadcastReceiver, intentFilter) // 注册接收器
    }

    override fun initView() {
        initPlayMode()
        // 获取现在歌曲信息
        getNowSongData()
        updateProgress()
        refreshPlayState()

        ivPlay.setOnClickListener {
            // 更新
            MyApplication.musicBinderInterface?.changePlayState()
            refreshPlayState()
        }

        // 下一曲
        ivNext.setOnClickListener {
            MyApplication.musicBinderInterface?.playNext()
        }

        // 上一曲
        ivLast.setOnClickListener {
            MyApplication.musicBinderInterface?.playLast()
        }

        // 切换播放模式
        ivMode.setOnClickListener {
            MyApplication.musicBinderInterface?.changePlayMode()
        }

        // 点击评论，跳转
        ivComment.setOnClickListener {
            if (song != null) {
                val intent = Intent(this, CommentActivity::class.java)
                intent.putExtra("long_music_id", song?.id)
                startActivity(intent)
                overridePendingTransition(
                    R.anim.anim_slide_enter_bottom,
                    R.anim.anim_no_anim
                )
            } else {
                toast("无评论")
            }
        }

        ivEqualizer.setOnClickListener {
            IntentUtil.openEqualizer(this)
        }

        // 点击播放器 cd 界面，cd 界面淡出
        clCd.setOnClickListener {
            AnimationUtil.fadeOut(clCd, true)
            AnimationUtil.fadeOut(clMenu, true) // 菜单淡出
        }

        clLyric.setOnClickListener {
            AnimationUtil.fadeIn(clCd)
            AnimationUtil.fadeIn(clMenu) // 菜单
        }


        // 进度条变化的监听
        seekBar.setOnSeekBarChangeListener(this)

    }

    /**
     * 初始化播放模式
     */
    private fun initPlayMode() {
        when (mode) {
            MusicService.MODE_CIRCLE -> ivMode.setImageResource(R.drawable.ic_bq_player_mode_circle)
            MusicService.MODE_REPEAT_ONE -> ivMode.setImageResource(R.drawable.ic_bq_player_mode_repeat_one)
            MusicService.MODE_RANDOM -> ivMode.setImageResource(R.drawable.ic_bq_player_mode_random)
        }
    }

    override fun initListener() {
        ivDownload.setOnClickListener {
            toast("暂未开放")
        }

        ivLike.setOnClickListener {
            if (song != null) {
                CloudMusic.likeSong(song!!.id?:-1L)
            }
        }

        ivMore.setOnClickListener {
            showPlayerMenuMoreDialog()
        }

        ivList.setOnClickListener {
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
            ivPlay.setImageResource(R.drawable.ic_pause_btn)
            startRotateAlways()
            // 开启进度更新
            handler.sendEmptyMessage(MSG_PROGRESS)
        } else {
            // 暂停
            ivPlay.setImageResource(R.drawable.ic_play_btn)
            pauseRotateAlways()
            // 停止更新进度
            handler.removeMessages(MSG_PROGRESS)
        }
    }

    private var rotation = 0f
    private fun pauseRotateAlways() {
        rotation = ivCover.rotation
        loge("rotation:$rotation")
        objectAnimator.pause()
    }

    private val objectAnimator: ObjectAnimator by lazy {
        ObjectAnimator.ofFloat(ivCover, "rotation", rotation, rotation + 360f).apply {
            interpolator = LinearInterpolator()
            duration = 25000
            repeatCount = -1
            start()
        }
    }


    private fun startRotateAlways() {
        objectAnimator.resume()
    }

    override fun onDestroy() {
        super.onDestroy()
        // 取消广播接收器的注册
        unregisterReceiver(musicBroadcastReceiver)
        // 清空 Handler 发送的所有消息，防止内存泄漏
        handler.removeCallbacksAndMessages(null)
    }

    inner class MusicBroadcastReceiver: BroadcastReceiver() {
        // private var mode = MusicService.MODE_CIRCLE // 默认
        override fun onReceive(context: Context, intent: Intent) {
            getNowSongData()
            refreshPlayState()
            // 当前播放模式改不了，修改图标
            refreshPlayMode()
        }
    }

    private fun refreshPlayMode() {
        if (mode != MyApplication.musicBinderInterface?.getPlayMode()?:mode) {
            // 赋值
            mode = MyApplication.musicBinderInterface?.getPlayMode()?:mode
            when (mode) {
                MusicService.MODE_CIRCLE -> {
                    ivMode.setImageResource(R.drawable.ic_bq_player_mode_circle)
                    toast("列表循环")
                }
                MusicService.MODE_REPEAT_ONE -> {
                    ivMode.setImageResource(R.drawable.ic_bq_player_mode_repeat_one)
                    toast("单曲循环")
                }
                MusicService.MODE_RANDOM -> {
                    ivMode.setImageResource(R.drawable.ic_bq_player_mode_random)
                    toast("随机播放")
                }
            }
        }
    }

    /**
     * 更新进度
     */
    private fun updateProgress() {
        lyricView.setLyricId(song?.id?:-1L)
        // 获取当前进度
        val progress = MyApplication.musicBinderInterface?.getProgress()?:0
        duration = MyApplication.musicBinderInterface?.getDuration()?:duration
        lyricView.setSongDuration(duration)
        // 设置进度条最大值
        seekBar.max = duration
        // 更新进度条
        seekBar.progress = progress
        tvProgress.text = TimeUtil.parseDuration(progress)
        tvDuration.text = TimeUtil.parseDuration(duration)
        // 更新歌词播放进度
        lyricView.updateProgress(progress)
        // 定时获取进度
        handler.sendEmptyMessage(MSG_PROGRESS)
    }

    /**
     * 进度改变的回调
     * @param p1 改变之后的进度
     * @param p2 true 通过用户手指拖动 false 通过代码方式改变进度
     */
    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
        // 判断是否为用户
        if (!p2) return
        // Log.e("手指拖到了：", p1.toString())
        MyApplication.musicBinderInterface?.setProgress(p1)
        updateProgress()
    }

    // 手指触摸
    override fun onStartTrackingTouch(p0: SeekBar?) {

    }

    // 手指离开
    override fun onStopTrackingTouch(p0: SeekBar?) {

    }

    private var song: StandardSongData? = null
    private fun getNowSongData() {
        song = MyApplication.musicBinderInterface?.getNowSongData()
        if (song != null) {
            CloudMusic.getSongImage(song!!.id?:-1L){
                // val bitmap = (ivCover.drawable as BitmapDrawable).bitmap
                if (ivCover.drawable != null) {
                    GlideUtil.load(it, ivCover, ivCover.drawable)
                } else {
                    GlideUtil.load(it, ivCover)
                }
            }



            tvName.text = song!!.name
            tvArtist.text = song!!.artists?.let { parseArtist(it) }
        }


    }
}