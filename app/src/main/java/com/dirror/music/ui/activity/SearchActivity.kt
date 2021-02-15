package com.dirror.music.ui.activity

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.dirror.music.MyApplication
import com.dirror.music.R
import com.dirror.music.adapter.DetailPlaylistAdapter
import com.dirror.music.adapter.SearchHotAdapter
import com.dirror.music.databinding.ActivitySearchBinding
import com.dirror.music.music.netease.SearchUtil
import com.dirror.music.music.qq.SearchSong
import com.dirror.music.music.standard.data.SOURCE_NETEASE
import com.dirror.music.music.standard.SongPicture
import com.dirror.music.music.standard.data.StandardSongData
import com.dirror.music.ui.base.BaseActivity
import com.dirror.music.ui.dialog.PlaylistDialog
import com.dirror.music.util.*

class SearchActivity : BaseActivity() {

    companion object {
        const val ENGINE_NETEASE = 1
        const val ENGINE_QQ = 2
        const val ENGINE_KUWO = 3
    }

    private lateinit var binding: ActivitySearchBinding
    private lateinit var musicBroadcastReceiver: MusicBroadcastReceiver // 音乐广播接收

    private var engine = ENGINE_NETEASE
    private var realKeyWord = ""

    override fun initBinding() {
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun initData() {
        val intentFilter = IntentFilter() // Intent 过滤器
        intentFilter.addAction("com.dirror.music.MUSIC_BROADCAST") // 只接收 "com.dirror.foyou.MUSIC_BROADCAST" 标识广播
        musicBroadcastReceiver = MusicBroadcastReceiver() //
        registerReceiver(musicBroadcastReceiver, intentFilter) // 注册接收器

        MyApplication.musicController.value?.sendBroadcast()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun initView() {
        // 获取焦点
        binding.etSearch.apply {
            isFocusable = true
            isFocusableInTouchMode = true
            requestFocus()
        }
        // 关闭软键盘
//        val inputMethodManager: InputMethodManager = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//        inputMethodManager.showSoftInput(binding.etSearch, InputMethodManager.SHOW_IMPLICIT);

        binding.clNetease.background = ContextCompat.getDrawable(this, R.drawable.bg_edit_text)

        changeSearchEngine(MyApplication.mmkv.decodeInt(Config.SEARCH_ENGINE, SOURCE_NETEASE))

        // 获取推荐关键词
        MyApplication.cloudMusicManager.getSearchDefault {
            runOnMainThread {
                // toast(it)
                binding.etSearch.hint = it.data.showKeyword
                realKeyWord = it.data.realkeyword
            }
        }
        // 获取热搜
        MyApplication.cloudMusicManager.getSearchHot {
            runOnMainThread {
                binding.rvSearchHot.layoutManager = LinearLayoutManager(this)
                val searchHotAdapter = SearchHotAdapter(it)
                searchHotAdapter.setOnItemClick(object : SearchHotAdapter.OnItemClick {
                    override fun onItemClick(view: View?, position: Int) {
                        val searchWord = it.data[position].searchWord
                        binding.etSearch.setText(searchWord)
                        binding.etSearch.setSelection(searchWord.length)
                        search()
                        // toast(it.data[position].searchWord)
                    }
                })
                binding.rvSearchHot.adapter = searchHotAdapter
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun initListener() {
        binding.apply {
            // ivBack
            ivBack.setOnClickListener {
                if (clPanel.visibility == View.VISIBLE) {
                    finish()
                } else {
                    clPanel.visibility = View.VISIBLE
                }
            }
            // 搜索
            btnSearch.setOnClickListener { search() }

            // 网易云
            clNetease.setOnClickListener {
                changeSearchEngine(ENGINE_NETEASE)
            }
            // QQ
            clQQ.setOnClickListener {
                changeSearchEngine(ENGINE_QQ)
            }
            clKuwo.setOnClickListener {
                changeSearchEngine(ENGINE_KUWO)
                toast("酷我音源暂只支持精确搜索，需要填入完整歌曲名")
            }

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
            MyApplication.musicController.value?.changePlayState()
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
            binding.etSearch.setText(keywords)
            binding.etSearch.setSelection(keywords.length)
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
                ENGINE_KUWO -> {
                    com.dirror.music.music.kuwo.SearchSong.search(keywords) {
                        initRecycleView(it)
                    }
                }
            }
            binding.clPanel.visibility = View.GONE
        }


    }

    private fun initRecycleView(songList: ArrayList<StandardSongData>) {
        runOnMainThread {
            binding.rvPlaylist.layoutManager = LinearLayoutManager(this)
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

    override fun onBackPressed() {
        if (binding.clPanel.visibility == View.VISIBLE) {
            super.onBackPressed()
        } else {
            binding.clPanel.visibility = View.VISIBLE
        }
    }

    /**
     * 改变搜索引擎
     */
    private fun changeSearchEngine(engineCode: Int) {
        binding.apply {
            clNetease.background = ContextCompat.getDrawable(this@SearchActivity, R.drawable.background_transparency)
            clQQ.background = ContextCompat.getDrawable(this@SearchActivity, R.drawable.background_transparency)
            clKuwo.background = ContextCompat.getDrawable(this@SearchActivity, R.drawable.background_transparency)
        }
        when (engineCode) {
            ENGINE_NETEASE -> {
                binding.clNetease.background = ContextCompat.getDrawable(this@SearchActivity, R.drawable.bg_edit_text)
            }
            ENGINE_QQ -> {
                binding.clQQ.background = ContextCompat.getDrawable(this@SearchActivity, R.drawable.bg_edit_text)
            }
            ENGINE_KUWO -> {
                binding.clKuwo.background = ContextCompat.getDrawable(this@SearchActivity, R.drawable.bg_edit_text)
            }
        }
        engine = engineCode
        if (binding.clPanel.visibility != View.VISIBLE) {
            search()
        }
    }


    inner class MusicBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val song = MyApplication.musicController.value?.getNowSongData()
            if (song != null) {
                SongPicture.getSongPicture(this@SearchActivity, song, SongPicture.TYPE_LARGE) {
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
        if (MyApplication.musicController.value?.getPlayState()!!) {
            binding.includePlayer.ivPlay.setImageResource(R.drawable.ic_mini_player_pause)
        } else {
            binding.includePlayer.ivPlay.setImageResource(R.drawable.ic_mini_player_play)
        }
    }

}