package com.dirror.music.ui.activity

import androidx.recyclerview.widget.LinearLayoutManager
import com.dirror.music.MyApplication
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

    override fun initMiniPlayer() {
        binding.miniPlayer.apply {
            root.setOnClickListener { MyApplication.activityManager.startPlayerActivity(this@PlayHistoryActivity) }
            ivPlaylist.setOnClickListener { PlaylistDialog(this@PlayHistoryActivity).show() }
            ivPlay.setOnClickListener { MyApplication.musicController.value?.changePlayState() }
        }
        MyApplication.musicController.observe(this, { nullableController ->
            nullableController?.let { controller ->
                controller.getPlayingSongData().observe(this, { songData ->
                    songData?.let {
                        binding.miniPlayer.tvName.text = songData.name
                        binding.miniPlayer.tvArtist.text = songData.artists?.let { parseArtist(it) }
                        // 这里应该用小的，等待修改
                        SongPicture.getSongPicture(this, songData, SongPicture.TYPE_LARGE) { bitmap ->
                            binding.miniPlayer.ivCover.setImageBitmap(bitmap)
                        }
                    }
                })
                controller.isPlaying().observe(this, {
                    binding.miniPlayer.ivPlay.setImageResource(getPlayStateSourceId(it))
                })
            }
        })
    }

}