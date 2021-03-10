package com.dirror.music.ui.base

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.animation.LinearInterpolator
import androidx.appcompat.app.AppCompatActivity
import coil.load
import coil.size.ViewSizeResolver
import com.dirror.music.MyApplication
import com.dirror.music.R
import com.dirror.music.databinding.MiniPlayerBinding
import com.dirror.music.manager.ActivityCollector
import com.dirror.music.ui.dialog.PlaylistDialog
import com.dirror.music.util.*
import com.dirror.music.util.sky.SkySecure

/**
 * 基类 Activity
 */
abstract class BaseActivity : AppCompatActivity() {

    var miniPlayer: MiniPlayerBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityCollector.addActivity(this)
        initBinding()
        initView()
        initData()
        initListener()
        initObserver()
        initBroadcastReceiver()
        initMiniPlayer()
    }

    override fun onStart() {
        super.onStart()
        if (SkySecure.checkXposed()) {
            ActivityCollector.finishAll()
        }
        if (DarkThemeUtil.isDarkTheme(this)) {
            setStatusBarIconColor(this, false)
        }
        initShowDialogListener()
    }

    protected open fun initBinding() {}

    protected open fun initView() {}

    protected open fun initData() {}

    protected open fun initListener() {}

    protected open fun initObserver() {}

    protected open fun initBroadcastReceiver() {}

    protected open fun initShowDialogListener() {}

    @SuppressLint("SetTextI18n")
    private fun initMiniPlayer() {
        miniPlayer?.let { mini ->

            // CD 旋转动画
            val objectAnimator: ObjectAnimator by lazy {
                ObjectAnimator.ofFloat(mini.ivCover, "rotation", 0f, 360f).apply {
                    interpolator = LinearInterpolator()
                    duration = 27_500L
                    repeatCount = -1
                    start()
                }
            }

            mini.apply {
                root.setOnClickListener { MyApplication.activityManager.startPlayerActivity(this@BaseActivity) }
                ivPlayQueue.setOnClickListener { PlaylistDialog().show(supportFragmentManager, null) }
                ivStartOrPause.setOnClickListener { MyApplication.musicController.value?.changePlayState() }
            }
            MyApplication.musicController.observe(this, { nullableController ->
                nullableController?.apply {
                    getPlayingSongData().observe(this@BaseActivity, { songData ->
                        songData?.let {
                            mini.tvTitle.text = songData.name + " - " + songData.artists?.let { parseArtist(it) }
                        }
                    })
                    isPlaying().observe(this@BaseActivity, {
//                        if (it) {
//                            objectAnimator.resume()
//                        } else {
//                            objectAnimator.pause()
//                        }
                        mini.ivStartOrPause.setImageResource(getPlayStateSourceId(it))
                    })
                    getPlayerCover().observe(this@BaseActivity, { bitmap ->
                        mini.ivCover.load(bitmap) {
                            size(ViewSizeResolver(mini.ivCover))
                            error(R.drawable.ic_song_cover)
                        }
                    })
                }
            })
        }

    }


    /**
     * 获取播放状态 MiniPlayer 图标
     */
    private fun getPlayStateSourceId(playing: Boolean): Int {
        return if (playing) {
            R.drawable.ic_mini_player_pause
        } else {
            R.drawable.ic_mini_player_play
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        miniPlayer = null
        ActivityCollector.removeActivity(this)
    }

}