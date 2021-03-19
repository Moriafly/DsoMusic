package com.dirror.music.ui.base

import android.annotation.SuppressLint
import android.content.ComponentName
import android.os.Bundle
import android.os.RemoteException
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.appcompat.app.AppCompatActivity
import coil.load
import coil.size.ViewSizeResolver
import com.dirror.music.MyApplication
import com.dirror.music.R
import com.dirror.music.databinding.MiniPlayerBinding
import com.dirror.music.manager.ActivityCollector
import com.dirror.music.service.MusicService
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
        initData()
        initView()
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

//    // 音乐相关
//    private fun initBrowser() {
//        mediaBrowser = MediaBrowserCompat(
//            this,
//            ComponentName(this, MusicService::class.java), mConnectionCallback, null
//        )
//    }
//
//    private val mConnectionCallback: MediaBrowserCompat.ConnectionCallback =
//        object : MediaBrowserCompat.ConnectionCallback() {
//            override fun onConnected() {
//                //说明已经连接上了
//                try {
//                    mediaBrowser?.let { connectToSession(it.sessionToken) }
//                } catch (e: RemoteException) {
//                    e.printStackTrace()
//                }
//            }
//        }
//
//    @Throws(RemoteException::class)
//    private fun connectToSession(token: MediaSessionCompat.Token) {
//        mediaController = MediaControllerCompat(this, token)
//        MediaControllerCompat.setMediaController(this, mediaController)
//        onMediaBrowserConnected()
//        onMediaControllerConnected(mediaController.sessionToken)
//    }
//
//    protected open fun onMediaControllerConnected(token: MediaSessionCompat.Token?) {
//        // empty implementation, can be overridden by clients.
//    }
//
//    protected open fun onMediaBrowserConnected() {
//        // empty implementation, can be overridden by clients.
//    }

}

/**
 * Created by ckw
 * on 2018/4/11.
 */
interface MediaBrowserProvider {
    var mediaBrowser: MediaBrowserCompat?
}