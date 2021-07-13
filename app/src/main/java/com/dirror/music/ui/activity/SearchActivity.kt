package com.dirror.music.ui.activity

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.dirror.music.MyApp
import com.dirror.music.MyApp.Companion.mmkv
import com.dirror.music.R
import com.dirror.music.adapter.SongAdapter
import com.dirror.music.adapter.SearchHotAdapter
import com.dirror.music.databinding.ActivitySearchBinding
import com.dirror.music.music.netease.SearchUtil
import com.dirror.music.music.qq.SearchSong
import com.dirror.music.music.standard.data.StandardSongData
import com.dirror.music.ui.base.BaseActivity
import com.dirror.music.ui.dialog.SongMenuDialog
import com.dirror.music.ui.viewmodel.SearchViewModel
import com.dirror.music.util.*
import com.dirror.music.util.asDrawable

/**
 * 搜索界面
 */
class SearchActivity : BaseActivity() {

    private lateinit var binding: ActivitySearchBinding

    private val searchViewModel: SearchViewModel by viewModels()

    private var realKeyWord = ""

    override fun initBinding() {
        binding = ActivitySearchBinding.inflate(layoutInflater)
        miniPlayer = binding.miniPlayer
        setContentView(binding.root)
    }

    override fun initView() {
        // 获取焦点
        binding.etSearch.apply {
            isFocusable = true
            isFocusableInTouchMode = true
            requestFocus()
        }
        // 获取推荐关键词
        MyApp.cloudMusicManager.getSearchDefault {
            runOnMainThread {
                // toast(it)
                binding.etSearch.hint = it.data.showKeyword
                realKeyWord = it.data.realkeyword
            }
        }
        // 获取热搜
        MyApp.cloudMusicManager.getSearchHot {
            runOnMainThread {
                binding.rvSearchHot.layoutManager = LinearLayoutManager(this)
                val searchHotAdapter = SearchHotAdapter(it)
                searchHotAdapter.setOnItemClick(object : SearchHotAdapter.OnItemClick {
                    override fun onItemClick(view: View?, position: Int) {
                        val searchWord = it.data[position].searchWord
                        binding.etSearch.setText(searchWord)
                        binding.etSearch.setSelection(searchWord.length)
                        search()
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
                changeSearchEngine(SearchViewModel.ENGINE_NETEASE)
            }
            // QQ
            clQQ.setOnClickListener {
                changeSearchEngine(SearchViewModel.ENGINE_QQ)
            }
            // 酷我
            clKuwo.setOnClickListener {
                changeSearchEngine(SearchViewModel.ENGINE_KUWO)
                toast("酷我音源暂只支持精确搜索，需要填入完整歌曲名")
            }

            itemOpenSource.setOnClickListener {
                openUrlByBrowser(this@SearchActivity, "https://github.com/Moriafly/DsoMusic")
            }
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

    override fun initObserver() {
        searchViewModel.searchEngine.observe(this, {
            binding.apply {
                clNetease.background = R.drawable.background_transparency.asDrawable(this@SearchActivity)
                clQQ.background = R.drawable.background_transparency.asDrawable(this@SearchActivity)
                clKuwo.background = R.drawable.background_transparency.asDrawable(this@SearchActivity)
            }
            when (it) {
                SearchViewModel.ENGINE_NETEASE -> {
                    binding.clNetease.background = ContextCompat.getDrawable(this@SearchActivity, R.drawable.bg_edit_text)
                }
                SearchViewModel.ENGINE_QQ -> {
                    binding.clQQ.background = ContextCompat.getDrawable(this@SearchActivity, R.drawable.bg_edit_text)
                }
                SearchViewModel.ENGINE_KUWO -> {
                    binding.clKuwo.background = ContextCompat.getDrawable(this@SearchActivity, R.drawable.bg_edit_text)
                }
            }
        })
    }

    /**
     * 搜索音乐
     */
    private fun search() {
        // 关闭软键盘
        val inputMethodManager: InputMethodManager = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(this.window?.decorView?.windowToken, 0)

        var keywords = binding.etSearch.text.toString()
        // 内部酷我
        if (keywords.startsWith("。")) {
            keywords.replace("。", "")
            searchViewModel.searchEngine.value = SearchViewModel.ENGINE_KUWO
        }
        if (keywords == "") {
            keywords = realKeyWord
            binding.etSearch.setText(keywords)
            binding.etSearch.setSelection(keywords.length)
        }
        if (keywords != "") {
            when (searchViewModel.searchEngine.value) {
                SearchViewModel.ENGINE_NETEASE -> {
                    SearchUtil.searchMusic(keywords, {
                        initRecycleView(it)
                    }, {
                        toast(it)
                    })
                }
                SearchViewModel.ENGINE_QQ -> {
                    SearchSong.search(keywords) {
                        initRecycleView(it)
                    }
                }
                SearchViewModel.ENGINE_KUWO -> {
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
            binding.rvPlaylist.adapter = SongAdapter() {
                SongMenuDialog(this, this, it) {
                    toast("不支持删除")
                }.show()
            }.apply {
                submitList(songList)
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // 保存搜索引擎
        mmkv.encode(Config.SEARCH_ENGINE, searchViewModel.searchEngine.value ?: SearchViewModel.ENGINE_NETEASE)

    }

    override fun finish() {
        super.finish()
        overridePendingTransition(
            R.anim.anim_no_anim,
            R.anim.anim_alpha_exit
        )
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
        searchViewModel.searchEngine.value = engineCode
        if (binding.clPanel.visibility != View.VISIBLE) {
            search()
        }
    }

}