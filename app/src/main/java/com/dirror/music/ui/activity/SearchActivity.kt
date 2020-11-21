package com.dirror.music.ui.activity

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dirror.music.music.CloudMusic
import com.dirror.music.MyApplication
import com.dirror.music.R
import com.dirror.music.adapter.DetailPlaylistAdapter
import com.dirror.music.music.netease.SearchUtil
import com.dirror.music.music.qq.Picture
import com.dirror.music.music.qq.SearchSong
import com.dirror.music.music.standard.SOURCE_NETEASE
import com.dirror.music.music.standard.SOURCE_QQ
import com.dirror.music.music.standard.SongPicture
import com.dirror.music.music.standard.StandardSongData
import com.dirror.music.ui.base.BaseActivity
import com.dirror.music.ui.dialog.PlaylistDialog
import com.dirror.music.util.*
import kotlinx.android.synthetic.main.activity_main.*

import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.activity_search.itemPlay
import kotlinx.android.synthetic.main.layout_play.view.*

class SearchActivity : BaseActivity(R.layout.activity_search) {

    companion object {
        const val ENGINE_NETEASE = 1
        const val ENGINE_QQ = 2
    }

    private lateinit var musicBroadcastReceiver: MusicBroadcastReceiver // 音乐广播接收

    private var engine = ENGINE_NETEASE

    override fun initData() {
        val intentFilter = IntentFilter() // Intent 过滤器
        intentFilter.addAction("com.dirror.music.MUSIC_BROADCAST") // 只接收 "com.dirror.foyou.MUSIC_BROADCAST" 标识广播
        musicBroadcastReceiver = MusicBroadcastReceiver() //
        registerReceiver(musicBroadcastReceiver, intentFilter) // 注册接收器

        MyApplication.musicBinderInterface?.sendBroadcast()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun initView() {
        when (MyApplication.mmkv.decodeInt(Config.SEARCH_ENGINE, SOURCE_NETEASE)) {
            SOURCE_NETEASE -> {
                ivEngine.setImageDrawable(getDrawable(R.drawable.ic_cloud_music_engine))
                engine = SOURCE_NETEASE
            }
            SOURCE_QQ -> {
                ivEngine.setImageDrawable(getDrawable(R.drawable.ic_qq_music_engine))
                engine = SOURCE_QQ
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun initListener() {

        // 切换搜索引擎
        ivEngine.setOnClickListener {
            if (engine == ENGINE_NETEASE) {
                engine = ENGINE_QQ
                ivEngine.setImageDrawable(getDrawable(R.drawable.ic_qq_music_engine))
                toast("已经切换成 QQ")
                search()
            } else {
                engine = ENGINE_NETEASE
                ivEngine.setImageDrawable(getDrawable(R.drawable.ic_cloud_music_engine))
                toast("已经切换成网易云")
                search()
            }
        }

        btnCancel.setOnClickListener {
            finish()
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
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
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
        // 关闭软键盘
        val inputMethodManager: InputMethodManager = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(this.window?.decorView?.windowToken, 0)


        val keywords = etSearch.text.toString()
        if (keywords != "") {
            when (engine) {
                ENGINE_NETEASE -> {
                    SearchUtil.searchMusic(keywords, {
                        initRecycleView(it)
                    }, {
                        toast(it)
                    })
                }
                ENGINE_QQ -> {
                    SearchSong.search(keywords) {
                        initRecycleView(it)
                    }
                }
            }
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

            rvPlaylist.layoutManager = linearLayoutManager
            rvPlaylist.adapter = DetailPlaylistAdapter(songList)

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // 解绑广播接收
        unregisterReceiver(musicBroadcastReceiver)
        // 保存搜索引擎
        MyApplication.mmkv.encode(Config.SEARCH_ENGINE, engine)
    }

    inner class MusicBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val song = MyApplication.musicBinderInterface?.getNowSongData()
            if (song != null) {
                GlideUtil.load(SongPicture.getSongPictureUrl(song, SongPicture.TYPE_LARGE), itemPlay.ivCover, itemPlay.ivCover)
                itemPlay.tvName.text = song.name
                itemPlay.tvArtist.text = song.artists?.let { parseArtist(it) }
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