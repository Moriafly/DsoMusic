package com.dirror.music.ui.activity

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.dirror.music.MyApplication
import com.dirror.music.R
import com.dirror.music.adapter.DetailPlaylistAdapter
import com.dirror.music.music.netease.Playlist
import com.dirror.music.music.netease.PlaylistUtil
import com.dirror.music.music.standard.SongPicture
import com.dirror.music.music.standard.data.StandardSongData
import com.dirror.music.ui.base.BaseActivity
import com.dirror.music.ui.dialog.PlaylistDialog
import com.dirror.music.util.*
import kotlinx.android.synthetic.main.activity_playlist.*
import kotlinx.android.synthetic.main.layout_play.view.*
import kotlin.math.abs

/**
 * 歌单 Activity
 * 最新要求：兼容 网易和 QQ
 */
class PlaylistActivity : BaseActivity(R.layout.activity_playlist) {

    private lateinit var musicBroadcastReceiver: MusicBroadcastReceiver // 音乐广播接收

    private var detailPlaylistAdapter = DetailPlaylistAdapter(ArrayList())

    override fun initData() {
        val intentFilter = IntentFilter() // Intent 过滤器
        intentFilter.addAction("com.dirror.music.MUSIC_BROADCAST") // 只接收 "com.dirror.foyou.MUSIC_BROADCAST" 标识广播
        musicBroadcastReceiver = MusicBroadcastReceiver() //
        registerReceiver(musicBroadcastReceiver, intentFilter) // 注册接收器
    }

    override fun initView() {
        val playlistId = intent.getLongExtra("long_playlist_id", -1)

        initPlaylistInfo(playlistId)
        initPlaylist(playlistId) {
            initRecycleView(it)
            // ivBackground.visibility = View.INVISIBLE
        }

//        nestedScrollView.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
//            scrollChangeHeader(scrollY)
//        }
    }

//    private fun scrollChangeHeader(Y: Int) {
//        val headerHeight = dp2px(128f)
//        var scrollY = Y
//        if (scrollY < 0) {
//            scrollY = 0
//        }
//        val alpha = abs(scrollY) * 1.0f / headerHeight
//        if (scrollY <= headerHeight) {
//            // titleBar.alpha = alpha
//            titleBar.setTitleBarText(getString(R.string.playlist))
//            clNavForeground.visibility = View.GONE
//        } else {
//            clNavForeground.visibility = View.VISIBLE
//            if (titleBar.text.toString() != tvName.text.toString()) {
//                titleBar.setTitleBarText(tvName.text.toString())
//            }
//        }
//    }

    override fun initListener() {
        includePlay.setOnClickListener {
            startActivity(Intent(this, PlayActivity::class.java))
            overridePendingTransition(
                R.anim.anim_slide_enter_bottom,
                R.anim.anim_no_anim
            )
        }
        includePlay.ivPlay.setOnClickListener {
            MyApplication.musicBinderInterface?.changePlayState()
        }
        includePlay.ivPlaylist.setOnClickListener {
            PlaylistDialog(this).show()
        }
        clNav.setOnClickListener {
            detailPlaylistAdapter.playFirst()
        }
//        clNavForeground.setOnClickListener {
//            detailPlaylistAdapter.playFirst()
//        }
    }

    /**
     * 初始化歌单信息
     */
    private fun initPlaylistInfo(id: Long) {
        PlaylistUtil.getPlaylistInfo(id) {
            it.coverImgUrl?.let { it1 -> GlideUtil.load(it1, ivCover) }
            runOnUiThread {
                tvName.text = it.name
                tvDescription.text = it.description
            }
        }
    }

    inner class MusicBroadcastReceiver: BroadcastReceiver() {
        // 接收
        override fun onReceive(context: Context, intent: Intent) {
            refreshLayoutPlay()
            refreshPlayState()
        }
    }

    private fun refreshPlayState() {
        if (MyApplication.musicBinderInterface?.getPlayState() == true) {
            includePlay.ivPlay.setImageResource(R.drawable.ic_bq_control_pause)
        } else {
            includePlay.ivPlay.setImageResource(R.drawable.ic_bq_control_play)
        }
    }

    override fun onStart() {
        super.onStart()
        refreshLayoutPlay()
        refreshPlayState()
    }

    /**
     * 刷新下方播放框
     * 可能导致 stick 丢失
     */
    private fun refreshLayoutPlay() {
        MyApplication.musicBinderInterface?.getNowSongData()?.let { standardSongData ->
            includePlay.tvName.text = standardSongData.name
            includePlay.tvArtist.text = standardSongData.artists?.let { parseArtist(it) }
            SongPicture.getSongPicture(standardSongData, SongPicture.TYPE_LARGE) {
                includePlay.ivCover.setImageBitmap(it)
            }
        }
    }

    private fun initPlaylist(id: Long, success: (ArrayList<StandardSongData>) -> Unit) {
        Playlist.getPlaylist(id, {
            success.invoke(it)
        }, {

        })
    }

    @SuppressLint("SetTextI18n")
    private fun initRecycleView(songList: ArrayList<StandardSongData>) {
        runOnMainThread {
            clLoading.visibility = View.GONE
            val linearLayoutManager = LinearLayoutManager(this@PlaylistActivity)
            detailPlaylistAdapter = DetailPlaylistAdapter(songList)
            rvPlaylist.layoutManager =  linearLayoutManager
            rvPlaylist.adapter = detailPlaylistAdapter
            tvPlayAll.text = "播放全部(${songList.size})"
            // tvPlayAllNavForeground.text = "播放全部(${songList.size})"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // 解绑
        unregisterReceiver(musicBroadcastReceiver)
    }


}