package com.dirror.music.ui.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dirror.music.CloudMusic
import com.dirror.music.MyApplication
import com.dirror.music.R
import com.dirror.music.adapter.DetailPlaylistAdapter
import com.dirror.music.cloudmusic.DetailPlaylistData
import com.dirror.music.cloudmusic.SongData
import com.dirror.music.ui.base.BaseActivity
import com.dirror.music.util.*
import kotlinx.android.synthetic.main.activity_playlist.*
import kotlinx.android.synthetic.main.layout_play.view.*

class PlaylistActivity : BaseActivity() {

    private lateinit var musicBroadcastReceiver: MusicBroadcastReceiver // 音乐广播接收

    override fun getLayoutId(): Int {
        return R.layout.activity_playlist
    }

    override fun initData() {
        val intentFilter = IntentFilter() // Intent 过滤器
        intentFilter.addAction("com.dirror.music.MUSIC_BROADCAST") // 只接收 "com.dirror.foyou.MUSIC_BROADCAST" 标识广播
        musicBroadcastReceiver = MusicBroadcastReceiver() //
        registerReceiver(musicBroadcastReceiver, intentFilter) // 注册接收器
    }

    override fun initView() {
        initPlaylist(object : InitPlaylistCallback {
            override fun success(songData: List<SongData>) {
                initRecycleView(songData)
            }
        })

        layoutPlay.setOnClickListener {
            startActivity(Intent(this, PlayActivity::class.java))
            overridePendingTransition(
                R.anim.anim_slide_enter_bottom,
                R.anim.anim_no_anim
            )
        }

        layoutPlay.ivPlay.setOnClickListener {
            MyApplication.musicBinderInterface?.changePlayState()

        }


    }


    inner class MusicBroadcastReceiver: BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val song = MyApplication.musicBinderInterface?.getNowSongData()?.songs?.get(0)
            if (song != null) {
                layoutPlay.tvName.text = song.name
                layoutPlay.tvArtist.text = song.ar[0].name
                GlideUtil.load(song.al.picUrl, layoutPlay.ivCover)
            }
            refreshPlayState()
        }
    }

    private fun refreshPlayState() {

        if (MyApplication.musicBinderInterface?.getPlayState() == true) {
            layoutPlay.ivPlay.setImageResource(R.drawable.ic_bq_control_pause)
        } else {
            layoutPlay.ivPlay.setImageResource(R.drawable.ic_bq_control_play)
        }
    }

    override fun onStart() {
        super.onStart()
        refreshLayoutPlay()
        refreshPlayState()
    }

    /**
     * 刷新下方播放框
     */
    private fun refreshLayoutPlay() {
        val song = MyApplication.musicBinderInterface?.getNowSongData()?.songs?.get(0)
        if (song != null) {
            GlideUtil.load(song.al.picUrl, layoutPlay.ivCover)
            layoutPlay.tvName.text = song.name
            layoutPlay.tvArtist.text = parseArtist(song.ar)
        }
    }

    override fun onStop() {
        super.onStop()
        // finish()
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
            rvDetailPlaylist.adapter = DetailPlaylistAdapter( songData)

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