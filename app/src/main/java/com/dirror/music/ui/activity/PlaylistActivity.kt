package com.dirror.music.ui.activity

import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dirror.music.CloudMusic
import com.dirror.music.R
import com.dirror.music.adapter.DetailPlaylistAdapter
import com.dirror.music.cloudmusic.DetailPlaylistData
import com.dirror.music.cloudmusic.SongData
import com.dirror.music.cloudmusic.TracksData
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
        initPlaylist(object : InitPlaylistCallback {

            override fun success(songData: List<SongData>) {
                initRecycleView(songData)
            }

        })

    }

    override fun onStop() {
        super.onStop()
        finish()
    }

    private fun initPlaylist(callback: InitPlaylistCallback) {
        val playlistId = intent.getLongExtra("long_playlist_id", -1)
        CloudMusic.getDetailPlaylist(playlistId, object : CloudMusic.DetailPlaylistCallback {

            override fun success(detailPlaylistData: DetailPlaylistData) {
                val trackIds = detailPlaylistData.playlist.trackIds
                Log.e("总歌曲trackIds", trackIds.size.toString())
                val songList= mutableListOf<SongData>()


//                Log.e("songList获取", songList.size.toString())
//

                var count = 0
                for (trackId in 0..trackIds.lastIndex) {
                    // Log.e("歌曲id", trackIds[trackId].id.toString())
                    getSong(trackIds[trackId].id, object : GetSongCallback {
                        override fun success(songData: SongData) {
                            songList.add(songData)
                            count++
                            // 获取后再循环，还要回调
                            if (count == trackIds.lastIndex) {
                                // 全部加载完全
                                callback.success(songList)
                            }
                        }
                    })
                }
            }

        })
    }

    interface InitPlaylistCallback {
        fun success(songData: List<SongData>)
    }

    private fun initRecycleView(songData: List<SongData>) {
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
                        setMeasuredDimension(widthSpec, (songData.size * dp2px(72f)).toInt())
                    }
                }

            rvDetailPlaylist.layoutManager =  linearLayoutManager
            rvDetailPlaylist.adapter = DetailPlaylistAdapter(songData)

        }
    }


    private fun getSong(id: Long, callback: GetSongCallback) {
        CloudMusic.getSongDetail(id, object : CloudMusic.SongCallback {
            override fun success(songData: SongData) {
                runOnUiThread {
                    Log.e("歌曲详细信息", songData.songs[0].name.toString())
                }
                callback.success(songData)
                // songList.add(songData)
            }
        })
    }

    interface GetSongCallback {
        fun success(songData: SongData)
    }


}