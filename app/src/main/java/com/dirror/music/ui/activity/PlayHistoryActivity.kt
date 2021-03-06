package com.dirror.music.ui.activity

import android.annotation.SuppressLint
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import coil.size.ViewSizeResolver
import coil.transform.CircleCropTransformation
import com.dirror.music.MyApplication
import com.dirror.music.R
import com.dirror.music.adapter.DetailPlaylistAdapter
import com.dirror.music.data.PLAYLIST_TAG_HISTORY
import com.dirror.music.databinding.ActivityPlayHistoryBinding
import com.dirror.music.music.local.PlayHistory
import com.dirror.music.music.standard.SongPicture
import com.dirror.music.ui.base.BaseActivity
import com.dirror.music.ui.dialog.PlaylistDialog
import com.dirror.music.util.parseArtist

class PlayHistoryActivity : BaseActivity() {

    private lateinit var binding: ActivityPlayHistoryBinding

    override fun initBinding() {
        binding = ActivityPlayHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun initView() {
        binding.apply {
            rvPlayHistory.layoutManager = LinearLayoutManager(this@PlayHistoryActivity)
            rvPlayHistory.adapter = DetailPlaylistAdapter(PlayHistory.readPlayHistory(), this@PlayHistoryActivity, PLAYLIST_TAG_HISTORY)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun initMiniPlayer() {
        binding.miniPlayer.apply {
            root.setOnClickListener { MyApplication.activityManager.startPlayerActivity(this@PlayHistoryActivity) }
            ivPlayQueue.setOnClickListener {  PlaylistDialog().show(supportFragmentManager, null)  }
            ivStartOrPause.setOnClickListener { MyApplication.musicController.value?.changePlayState() }
        }
        MyApplication.musicController.observe(this, { nullableController ->
            nullableController?.apply {
                getPlayingSongData().observe(this@PlayHistoryActivity, { songData ->
                    songData?.let {
                        binding.miniPlayer.tvTitle.text = songData.name + " - " + songData.artists?.let { parseArtist(it) }
                    }
                })
                isPlaying().observe(this@PlayHistoryActivity, {
                    binding.miniPlayer.ivStartOrPause.setImageResource(getPlayStateSourceId(it))
                })
                getPlayerCover().observe(this@PlayHistoryActivity, { bitmap ->
                    binding.miniPlayer.ivCover.load(bitmap) {
                        size(ViewSizeResolver(binding.miniPlayer.ivCover))
                        error(R.drawable.ic_song_cover)
                    }
                })
            }
        })
    }

}