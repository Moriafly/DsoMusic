package com.dirror.music.ui.activity

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dirror.music.MyApplication
import com.dirror.music.R
import com.dirror.music.adapter.DetailPlaylistAdapter
import com.dirror.music.databinding.ActivitySearchBinding
import com.dirror.music.music.netease.SearchUtil
import com.dirror.music.music.qq.SearchSong
import com.dirror.music.music.standard.data.SOURCE_NETEASE
import com.dirror.music.music.standard.data.SOURCE_QQ
import com.dirror.music.music.standard.SongPicture
import com.dirror.music.music.standard.data.StandardSongData
import com.dirror.music.ui.dialog.PlaylistDialog
import com.dirror.music.util.*

class SearchActivity : AppCompatActivity() {

    companion object {
        const val ENGINE_NETEASE = 1
        const val ENGINE_QQ = 2
    }

    private lateinit var binding: ActivitySearchBinding
    private lateinit var musicBroadcastReceiver: MusicBroadcastReceiver // 音乐广播接收

    private var engine = ENGINE_NETEASE
    private var realKeyWord = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
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

        MyApplication.musicBinderInterface?.sendBroadcast()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initView() {
        binding.clTip.visibility = View.GONE
        if (MyApplication.mmkv.decodeBool(Config.SEARCH_ENGINE_TIP, true)) {
            binding.clTip.visibility = View.VISIBLE
        }
        when (MyApplication.mmkv.decodeInt(Config.SEARCH_ENGINE, SOURCE_NETEASE)) {
            SOURCE_NETEASE -> {
                binding.ivEngine.setImageDrawable(getDrawable(R.drawable.ic_cloud_music_engine))
                engine = SOURCE_NETEASE
            }
            SOURCE_QQ -> {
                binding.ivEngine.setImageDrawable(getDrawable(R.drawable.ic_qq_music_engine))
                engine = SOURCE_QQ
            }
        }
        // 获取推荐关键词
        MyApplication.cloudMusicManager.getSearchDefault {
            runOnMainThread {
                // toast(it)
                binding.etSearch.hint = it.data.showKeyword
                realKeyWord = it.data.realkeyword
            }
        }

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initListener() {

        // 切换搜索引擎
        binding.ivEngine.setOnClickListener {
            if (engine == ENGINE_NETEASE) {
                engine = ENGINE_QQ
                binding.ivEngine.setImageDrawable(getDrawable(R.drawable.ic_qq_music_engine))
                // toast("已经切换成 QQ")
                search()
            } else {
                engine = ENGINE_NETEASE
                binding.ivEngine.setImageDrawable(getDrawable(R.drawable.ic_cloud_music_engine))
                // toast("已经切换成网易云")
                search()
            }
        }

        binding.btnCancel.setOnClickListener {
            finish()
        }

        binding.includePlayer.root.setOnClickListener {
            startActivity(Intent(this, PlayerActivity::class.java))
            overridePendingTransition(
                R.anim.anim_slide_enter_bottom,
                R.anim.anim_no_anim
            )
        }

        binding.includePlayer.ivPlay.setOnClickListener {
            // 更新
            MyApplication.musicBinderInterface?.changePlayState()
            refreshPlayState()
        }
        binding.includePlayer.ivPlaylist.setOnClickListener {
            PlaylistDialog(this).show()
        }

        // 搜索框
        binding.etSearch.apply {
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
                    if (binding.etSearch.text.toString() != "") {
                        binding.ivClear.visibility = View.VISIBLE // 有文字，显示清楚按钮
                    } else {
                        binding.ivClear.visibility = View.INVISIBLE // 隐藏
                    }
                }
            })
        }

        binding.btnCloseTip.setOnClickListener {
            MyApplication.mmkv.encode(Config.SEARCH_ENGINE_TIP, false)
            binding.clTip.visibility = View.GONE
        }

        binding.ivClear.setOnClickListener {
            binding.etSearch.setText("")
        }

    }

    /**
     * 搜索音乐
     */
    private fun search() {
        // 关闭软键盘
        val inputMethodManager: InputMethodManager = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(this.window?.decorView?.windowToken, 0)


        var keywords = binding.etSearch.text.toString()
        if (keywords == "") {
            keywords = realKeyWord
        }
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

            binding.rvPlaylist.layoutManager = linearLayoutManager
            binding.rvPlaylist.adapter = DetailPlaylistAdapter(songList, this)

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
                SongPicture.getSongPicture(song, SongPicture.TYPE_LARGE) {
                    binding.includePlayer.ivCover.setImageBitmap(it)
                }
                binding.includePlayer.tvName.text = song.name
                binding.includePlayer.tvArtist.text = song.artists?.let { parseArtist(it) }
            }
            refreshPlayState()
        }
    }

    /**
     * 刷新播放状态
     */
    private fun refreshPlayState() {
        if (MyApplication.musicBinderInterface?.getPlayState()!!) {
            binding.includePlayer.ivPlay.setImageResource(R.drawable.ic_bq_control_pause)
        } else {
            binding.includePlayer.ivPlay.setImageResource(R.drawable.ic_bq_control_play)
        }
    }



}