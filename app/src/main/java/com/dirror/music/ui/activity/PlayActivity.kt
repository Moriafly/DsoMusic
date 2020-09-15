package com.dirror.music.ui.activity

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.os.Message
import android.util.Log
import com.bumptech.glide.Glide
import com.dirror.music.MyApplication
import com.dirror.music.R
import com.dirror.music.ui.base.BaseActivity
import com.dirror.music.util.TimeUtil
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_play.*
import kotlinx.android.synthetic.main.layout_play.view.*

class PlayActivity : BaseActivity() {

    private lateinit var musicBroadcastReceiver: MusicBroadcastReceiver // 音乐广播接收
    var duration = 0
    private val handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg?.what) {
                MSG_PROGRESS -> updateProgress()
            }
        }
    }
    val MSG_PROGRESS = 0

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
        updateProgress()

        ivPlay.setOnClickListener {
            // 更新
            MyApplication.musicBinderInterface?.updatePlayState()
            if (MyApplication.musicBinderInterface?.getPlayState()!!) {
                // 播放
                ivPlay.setImageResource(R.drawable.ic_play)
                // 开启进度更新
                handler.sendEmptyMessage(MSG_PROGRESS)
            } else {
                // 暂停
                ivPlay.setImageResource(R.drawable.ic_pause)
                // 停止更新进度
                handler.removeMessages(MSG_PROGRESS)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(musicBroadcastReceiver)
    }

    inner class MusicBroadcastReceiver: BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.e("广播", "接收到了广播")
            val name = intent.getStringExtra("string_song_name")
            val artist = intent.getStringExtra("string_song_artist")
            var url = intent.getStringExtra("string_song_pic")

            duration = MyApplication.musicBinderInterface?.getDuration()?:0



            updateProgress()
//            itemPlay.tvName.text = name
//            itemPlay.tvArtist.text = artist
//
//            url = url?.replace("http", "https")
//            Log.e("图片", "$url")
            Glide.with(this@PlayActivity)
                .load(url)
                .into(ivCover)
        }
    }

    /**
     * 更新进度
     */
    private fun updateProgress() {
        // 获取当前进度
        val progress = MyApplication.musicBinderInterface?.getProgress()?:0
        duration = MyApplication.musicBinderInterface?.getDuration()?:0
        // 设置进度条最大值
        seekBar.max = duration
        // 更新进度
        seekBar.progress = progress
        tvProgress.text = TimeUtil.parseDuration(progress)
        tvDuration.text = TimeUtil.parseDuration(duration)
        // 定时获取进度
        handler.sendEmptyMessageDelayed(MSG_PROGRESS,1000)
    }
}