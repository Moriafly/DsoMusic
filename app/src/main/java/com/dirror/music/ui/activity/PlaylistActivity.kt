package com.dirror.music.ui.activity

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dirror.music.CloudMusic
import com.dirror.music.R
import com.dirror.music.adapter.DetailPlaylistAdapter
import com.dirror.music.cloudmusic.DetailPlaylistData
import com.dirror.music.ui.base.BaseActivity
import com.dirror.music.util.dp2px
import com.dirror.music.util.runOnMainThread
import kotlinx.android.synthetic.main.activity_playlist.*
import kotlinx.android.synthetic.main.fragment_my.*

class PlaylistActivity : BaseActivity() {
    override fun getLayoutId(): Int {
        return R.layout.activity_playlist
    }

    override fun initData() {

    }

    override fun initView() {
        CloudMusic.getDetailPlaylist(2876988135, object : CloudMusic.DetailPlaylistCallback {

            override fun success(detailPlaylistData: DetailPlaylistData) {
                val tracks = detailPlaylistData.playlist.tracks
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
                                setMeasuredDimension(widthSpec, (tracks.size * dp2px(72f)).toInt())
                            }
                        }

                    rvDetailPlaylist.layoutManager =  linearLayoutManager
                    rvDetailPlaylist.adapter = DetailPlaylistAdapter(tracks)

                }
            }

        })
    }
}