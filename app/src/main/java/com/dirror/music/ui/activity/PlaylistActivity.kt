package com.dirror.music.ui.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dirror.music.CloudMusic
import com.dirror.music.MyApplication
import com.dirror.music.R
import com.dirror.music.adapter.DetailPlaylistAdapter
import com.dirror.music.music.PlaylistUtil
import com.dirror.music.music.StandardSongData
import com.dirror.music.ui.base.BaseActivity
import com.dirror.music.ui.dialog.PlaylistDialog
import com.dirror.music.util.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_playlist.*
import kotlinx.android.synthetic.main.layout_play.view.*

class PlaylistActivity : BaseActivity() {

    private lateinit var musicBroadcastReceiver: MusicBroadcastReceiver // 音乐广播接收

    override fun getLayoutId(): Int {
        return R.layout.activity_playlist
    }

    override fun initData() {
        val intentFilter = IntentFilter() // Intent 过滤器
        intentFilter.addAction("com.dirror.music.MUSIC_BROADCAST") // 只接收 "com.dirror.foyou.MUSIC_BROADCAST" 标识广播
        musicBroadcastReceiver = MusicBroadcastReceiver() //
        registerReceiver(musicBroadcastReceiver, intentFilter) // 注册接收器
    }

    override fun initView() {
        initPlaylist(){
            initRecycleView(it)
            ivBackground.visibility = View.INVISIBLE
        }

        layoutPlay.setOnClickListener {
            startActivity(Intent(this, PlayActivity::class.java))
            overridePendingTransition(
                R.anim.anim_slide_enter_bottom,
                R.anim.anim_no_anim
            )
        }
        layoutPlay.ivPlay.setOnClickListener {
            MyApplication.musicBinderInterface?.changePlayState()
        }
        layoutPlay.ivPlaylist.setOnClickListener {
            PlaylistDialog(this).show()
        }

    }

    inner class MusicBroadcastReceiver: BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val song = MyApplication.musicBinderInterface?.getNowSongData()
            if (song != null) {
                layoutPlay.tvName.text = song.name
                layoutPlay.tvArtist.text = parseArtist(song.artists)
                GlideUtil.load(CloudMusic.getMusicCoverUrl(song.id), layoutPlay.ivCover, layoutPlay.ivCover)
            }
            refreshPlayState()
        }
    }

    private fun refreshPlayState() {
        if (MyApplication.musicBinderInterface?.getPlayState() == true) {
            layoutPlay.ivPlay.setImageResource(R.drawable.ic_bq_control_pause)
        } else {
            layoutPlay.ivPlay.setImageResource(R.drawable.ic_bq_control_play)
        }
    }

    override fun onStart() {
        super.onStart()
        refreshLayoutPlay()
        refreshPlayState()
    }

    /**
     * 刷新下方播放框
     */
    private fun refreshLayoutPlay() {
        val song = MyApplication.musicBinderInterface?.getNowSongData()
        if (song != null) {
            GlideUtil.load(CloudMusic.getMusicCoverUrl(song.id), layoutPlay.ivCover)
            layoutPlay.tvName.text = song.name
            layoutPlay.tvArtist.text = parseArtist(song.artists)
        }
    }

    private fun initPlaylist(success: (ArrayList<StandardSongData>) -> Unit) {
        val playlistId = intent.getLongExtra("long_playlist_id", -1)
        PlaylistUtil.getDetailPlaylist(playlistId, {
            success.invoke(it)
        }, {

        })
    }

    private fun initRecycleView(songList: ArrayList<StandardSongData>) {
        runOnMainThread {
            val linearLayoutManager: LinearLayoutManager =
                object : LinearLayoutManager(this@PlaylistActivity) {
                    override fun canScrollVertically(): Boolean {
                        return false
                    }

                    override fun onMeasure(
                        recycler: RecyclerView.Recycler,
                        state: RecyclerView.State,
                        widthSpec: Int,
                        heightSpec: Int
                    ) {
                        super.onMeasure(recycler, state, widthSpec, heightSpec)
                        setMeasuredDimension(widthSpec, (songList.size * dp2px(64f)).toInt())
                    }
                }

            rvDetailPlaylist.layoutManager =  linearLayoutManager
            rvDetailPlaylist.adapter = DetailPlaylistAdapter(songList)

        }
    }


}