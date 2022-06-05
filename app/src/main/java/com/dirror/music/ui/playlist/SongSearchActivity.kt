package com.dirror.music.ui.playlist

import android.text.Editable
import android.text.TextWatcher
import androidx.recyclerview.widget.LinearLayoutManager
import com.dirror.music.adapter.SongAdapter
import com.dirror.music.databinding.ActivitySongSearchBinding
import com.dirror.music.music.standard.data.StandardSongData
import com.dirror.music.ui.base.BaseActivity
import com.dirror.music.ui.dialog.SongMenuDialog
import com.dirror.music.util.parse
import com.dirror.music.util.toast
import java.util.*

class SongSearchActivity : BaseActivity() {

    private lateinit var binding: ActivitySongSearchBinding

    val adapter = SongAdapter {
        SongMenuDialog(this, this, it) {
            toast("不支持删除")
        }.show()
    }

    override fun initBinding() {
        binding = ActivitySongSearchBinding.inflate(layoutInflater)
        miniPlayer = binding.miniPlayer
        setContentView(binding.root)
    }

    override fun initView() {

        with(binding) {
            // adapter.submitList(SongSearchTransmit.songList)
            rvSongList.layoutManager = LinearLayoutManager(this@SongSearchActivity)
            rvSongList.adapter = adapter
        }
    }

    override fun initListener() {
        binding.etSearch.apply {
            setOnEditorActionListener { _, p1, _ ->
//                if (p1 == EditorInfo.IME_ACTION_SEARCH) { // 软键盘点击了搜索
//
//                }
                false
            }

            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun afterTextChanged(s: Editable) {
                    search()
//                    if (binding.etSearch.text.toString() != "") {
//                        binding.ivClear.visibility = View.VISIBLE // 有文字，显示清楚按钮
//                    } else {
//                        binding.ivClear.visibility = View.INVISIBLE // 隐藏
//                    }
                }
            })
        }
    }

    private fun search() {
        val keywords = binding.etSearch.text.toString()
        if (keywords.isEmpty()) {
            return
        }
        val keyArrayList = ArrayList<StandardSongData>()
        SongSearchTransmit.songList.forEach {
            val key = it.name + it.artists?.parse()
            if (keywords in key) {
                keyArrayList.add(it)
            }
        }
        adapter.submitList(keyArrayList)
    }

}