package com.dirror.music.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import coil.size.ViewSizeResolver
import coil.transform.CircleCropTransformation
import com.dirror.music.MyApplication
import com.dirror.music.R
import com.dirror.music.adapter.TopListAdapter
import com.dirror.music.databinding.ActivityTopListBinding
import com.dirror.music.music.netease.TopList
import com.dirror.music.music.standard.SongPicture
import com.dirror.music.music.standard.data.SOURCE_NETEASE
import com.dirror.music.ui.base.BaseActivity
import com.dirror.music.ui.dialog.PlaylistDialog
import com.dirror.music.util.parseArtist
import com.dirror.music.util.runOnMainThread

class TopListActivity : BaseActivity() {

    private lateinit var binding: ActivityTopListBinding

    override fun initBinding() {
        binding = ActivityTopListBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun initView() {
        TopList.getTopList(this, {
            runOnMainThread {
                binding.rvTopList.layoutManager = LinearLayoutManager(this)
                binding.rvTopList.adapter = TopListAdapter(it) { listData ->
                    val intent = Intent(this, PlaylistActivity::class.java)
                    intent.putExtra(PlaylistActivity.EXTRA_PLAYLIST_SOURCE, SOURCE_NETEASE)
                    intent.putExtra(PlaylistActivity.EXTRA_LONG_PLAYLIST_ID, listData.id)
                    // intent.putExtra(PlaylistActivity2.EXTRA_INT_TAG, PLAYLIST_TAG_NORMAL)
                    startActivity(intent)
                }
            }
        }, {

        })
    }

    @SuppressLint("SetTextI18n")
    override fun initMiniPlayer() {
        binding.miniPlayer.apply {
            root.setOnClickListener { MyApplication.activityManager.startPlayerActivity(this@TopListActivity) }
            ivPlayQueue.setOnClickListener { PlaylistDialog().show(supportFragmentManager, null)  }
            ivStartOrPause.setOnClickListener { MyApplication.musicController.value?.changePlayState() }
        }
        MyApplication.musicController.observe(this, { nullableController ->
            nullableController?.let { controller ->
                controller.getPlayingSongData().observe(this, { songData ->
                    songData?.let {
                        binding.miniPlayer.tvTitle.text = songData.name + " - " + songData.artists?.let { parseArtist(it) }
                        binding.miniPlayer.ivCover.load(SongPicture.getMiniPlayerSongPicture(songData)) {
                            transformations(CircleCropTransformation())
                            size(ViewSizeResolver(binding.miniPlayer.ivCover))
                            error(R.drawable.ic_song_cover)
                        }
                    }
                })
                controller.isPlaying().observe(this, {
                    binding.miniPlayer.ivStartOrPause.setImageResource(getPlayStateSourceId(it))
                })
            }
        })
    }

}