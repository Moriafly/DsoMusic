package com.dirror.music.ui.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dirror.music.music.CloudMusic
import com.dirror.music.MyApplication
import com.dirror.music.R
import com.dirror.music.adapter.DetailPlaylistAdapter
import com.dirror.music.music.netease.SearchUtil
import com.dirror.music.music.standard.StandardSongData
import com.dirror.music.ui.base.BaseActivity
import com.dirror.music.ui.dialog.PlaylistDialog
import com.dirror.music.util.*

import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.activity_search.itemPlay
import kotlinx.android.synthetic.main.layout_play.view.*

class SearchActivity : BaseActivity(R.layout.activity_search) {

    private lateinit var musicBroadcastReceiver: MusicBroadcastReceiver // 音乐广播接收

    override fun initData() {
        val intentFilter = IntentFilter() // Intent 过滤器
        intentFilter.addAction("com.dirror.music.MUSIC_BROADCAST") // 只接收 "com.dirror.foyou.MUSIC_BROADCAST" 标识广播
        musicBroadcastReceiver = MusicBroadcastReceiver() //
        registerReceiver(musicBroadcastReceiver, intentFilter) // 注册接收器

        MyApplication.musicBinderInterface?.sendBroadcast()
    }

    override fun initView() {

    }

    override fun initListener() {
        btnSearch.setOnClickListener {
            search()

        }

        itemPlay.setOnClickListener {
            startActivity(Intent(this, PlayActivity::class.java))
            overridePendingTransition(
                R.anim.anim_slide_enter_bottom,
                R.anim.anim_no_anim
            )
        }

        itemPlay.ivPlay.setOnClickListener {
            // 更新
            MyApplication.musicBinderInterface?.changePlayState()
            refreshPlayState()
        }
        itemPlay.ivPlaylist.setOnClickListener {
            PlaylistDialog(this).show()
        }

        // 搜索框
        etSearch.apply {
            setOnEditorActionListener { _, p1, _ ->
                if (p1 == EditorInfo.IME_ACTION_SEARCH) { // 软键盘点击了搜索
                    search()
                }
                false
            }

            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }
                override fun afterTextChanged(s: Editable) {
                    if (etSearch.text.toString() != "") {
                        // btnClear.visibility = View.VISIBLE // 有文字，显示清楚按钮
                    } else {
                        // btnClear.visibility = View.INVISIBLE // 隐藏
                    }
                }
            })
        }

    }

    /**
     * 搜索音乐
     */
    private fun search() {
        val keywords = etSearch.text.toString()
        if (keywords != "") {
            SearchUtil.searchMusic(keywords, {
                initRecycleView(it)
            }, {
                toast(it)
            })
        }
    }

    private fun initRecycleView(songList: ArrayList<StandardSongData>) {
        runOnMainThread {
            val linearLayoutManager: LinearLayoutManager =
                object : LinearLayoutManager(this) {
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

            rvPlaylist.layoutManager =  linearLayoutManager
            rvPlaylist.adapter = DetailPlaylistAdapter(songList)

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // 解绑广播接收
        unregisterReceiver(musicBroadcastReceiver)
    }

    inner class MusicBroadcastReceiver: BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val song = MyApplication.musicBinderInterface?.getNowSongData()
            if (song != null) {
                itemPlay.tvName.text = song.name
                itemPlay.tvArtist.text = song.artists?.let { parseArtist(it) }
                GlideUtil.load(CloudMusic.getMusicCoverUrl(song.id?:-1L), itemPlay.ivCover, itemPlay.ivCover)
            }
            refreshPlayState()
        }
    }

    /**
     * 刷新播放状态
     */
    private fun refreshPlayState() {
        if (MyApplication.musicBinderInterface?.getPlayState()!!) {
            itemPlay.ivPlay.setImageResource(R.drawable.ic_bq_control_pause)
        } else {
            itemPlay.ivPlay.setImageResource(R.drawable.ic_bq_control_play)
        }
    }
}