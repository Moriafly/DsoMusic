package com.dirror.music.ui.activity

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dirror.music.R
import com.dirror.music.adapter.DetailPlaylistAdapter
import com.dirror.music.music.SearchUtil
import com.dirror.music.music.StandardSongData
import com.dirror.music.ui.base.BaseActivity
import com.dirror.music.util.dp2px
import com.dirror.music.util.runOnMainThread
import com.dirror.music.util.toast
import kotlinx.android.synthetic.main.activity_search.*

class SearchActivity : BaseActivity() {
    override fun getLayoutId(): Int {
        return R.layout.activity_search
    }

    override fun initData() {

    }

    override fun initView() {

    }

    override fun initListener() {
        btnSearch.setOnClickListener {
            val keywords = etSearch.text.toString()
            if (keywords != "") {
                SearchUtil.searchMusic(keywords, {
                    initRecycleView(it)
                }, {
                    toast(it)
                })
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
                        setMeasuredDimension(widthSpec, (songList.size * dp2px(72f)).toInt())
                    }
                }

            rvPlaylist.layoutManager =  linearLayoutManager
            rvPlaylist.adapter = DetailPlaylistAdapter(songList)

        }
    }
}