package com.dirror.music.ui.activity

import android.graphics.drawable.Drawable
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dirror.music.MyApplication
import com.dirror.music.R
import com.dirror.music.adapter.DailyRecommendSongAdapter
import com.dirror.music.databinding.ActivityRecommendBinding
import com.dirror.music.music.standard.SongPicture
import com.dirror.music.ui.base.BaseActivity
import com.dirror.music.ui.dialog.PlaylistDialog
import com.dirror.music.ui.viewmodel.RecommendActivityViewModel
import com.dirror.music.util.*
import eightbitlab.com.blurview.RenderScriptBlur

class RecommendActivity : BaseActivity() {

    private lateinit var binding: ActivityRecommendBinding

    private val recommendActivityViewModel: RecommendActivityViewModel by viewModels()

    override fun initBinding() {
        binding = ActivityRecommendBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun initData() {
        recommendActivityViewModel.getRecommendSong({
            runOnMainThread {
                binding.rvRecommendSong.layoutManager = LinearLayoutManager(this)
                binding.rvRecommendSong.adapter = DailyRecommendSongAdapter(it)
            }
        }, {
            toast(it)
        })
    }

    override fun initView() {
        val radius = 20f
        val decorView: View = window.decorView
        val windowBackground: Drawable = decorView.background
        binding.blurView.setupWith(decorView.findViewById(R.id.rvRecommendSong))
            .setFrameClearDrawable(windowBackground)
            .setBlurAlgorithm(RenderScriptBlur(this))
            .setBlurRadius(radius)
            .setHasFixedTransformationMatrix(true)
    }

    override fun initMiniPlayer() {
        binding.miniPlayer.root.setOnClickListener { MyApplication.activityManager.startPlayerActivity(this) }
        binding.miniPlayer.ivPlaylist.setOnClickListener { PlaylistDialog(this).show() }
        binding.miniPlayer.ivPlay.setOnClickListener { MyApplication.musicController.value?.changePlayState() }
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
                    if (it) {
                        binding.miniPlayer.ivPlay.setImageResource(R.drawable.ic_mini_player_pause)
                    } else {
                        binding.miniPlayer.ivPlay.setImageResource(R.drawable.ic_mini_player_play)
                    }
                })
            }
        })
    }

}