package com.dirror.music.ui.fragment

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dirror.music.CloudMusic
import com.dirror.music.MyApplication
import com.dirror.music.R
import com.dirror.music.adapter.PlaylistAdapter
import com.dirror.music.cloudmusic.UserPlaylistData
import com.dirror.music.ui.base.BaseFragment
import com.dirror.music.util.FullyLinearLayoutManager
import com.dirror.music.util.dp2px
import com.dirror.music.util.runOnMainThread
import kotlinx.android.synthetic.main.fragment_my.*

class MyFragment : BaseFragment() {
    override fun getLayoutId(): Int {
        return R.layout.fragment_my
    }

    override fun initView() {

        CloudMusic.getPlaylist(411311194, object : CloudMusic.PlaylistCallback {
            override fun success(userPlaylistData: UserPlaylistData) {

                val playlist = userPlaylistData.playlist
                runOnMainThread {
                    val linearLayoutManager: LinearLayoutManager =
                        object : LinearLayoutManager(context) {
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
                                setMeasuredDimension(widthSpec, (playlist.size * dp2px(MyApplication.context, 68f)).toInt())
                            }
                        }

                    // rvPlaylist.setHasFixedSize(true)
                    // rvPlaylist.isNestedScrollingEnabled = false
                    rvPlaylist.layoutManager =  linearLayoutManager//FullyLinearLayoutManager(context)
                    rvPlaylist.adapter = PlaylistAdapter(playlist)

                }

            }

        })
    }

}