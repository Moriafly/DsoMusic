package com.dirror.music.ui.activity

import android.graphics.drawable.Drawable
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dirror.music.MyApplication
import com.dirror.music.R
import com.dirror.music.adapter.DailyRecommendSongAdapter
import com.dirror.music.adapter.playMusic
import com.dirror.music.databinding.ActivityRecommendBinding
import com.dirror.music.music.netease.data.toStandardSongDataArrayList
import com.dirror.music.music.standard.SongPicture
import com.dirror.music.ui.base.BaseActivity
import com.dirror.music.ui.dialog.PlaylistDialog
import com.dirror.music.ui.viewmodel.RecommendActivityViewModel
import com.dirror.music.util.*
import eightbitlab.com.blurview.RenderScriptBlur
import java.util.*

class RecommendActivity : BaseActivity() {

    private lateinit var binding: ActivityRecommendBinding

    private val recommendActivityViewModel: RecommendActivityViewModel by viewModels()

    override fun initBinding() {
        binding = ActivityRecommendBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun initData() {
        recommendActivityViewModel.getRecommendSong({
            val songDataArrayList = it.data.dailySongs.toStandardSongDataArrayList()
            runOnMainThread {
                binding.rvRecommendSong.layoutManager = LinearLayoutManager(this)
                binding.rvRecommendSong.adapter = DailyRecommendSongAdapter(it) { position ->
                    playMusic(this, songDataArrayList[position], songDataArrayList)
                }
            }
        }, {
            toast(it)
        })
    }

    override fun initView() {
        val radius = 15f
        val decorView: View = window.decorView
        val windowBackground: Drawable = decorView.background
        binding.blurView.setupWith(decorView.findViewById(R.id.clRecommend))
            .setFrameClearDrawable(windowBackground)
            .setBlurAlgorithm(RenderScriptBlur(this))
            .setBlurRadius(radius)
            .setHasFixedTransformationMatrix(true)

        binding.tvDate.text = String.format("%02d", Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
        binding.tvMonth.text = String.format("%02d", Calendar.getInstance().get(Calendar.MONTH) + 1)
    }

    override fun initMiniPlayer() {
        binding.miniPlayer.apply {
            root.setOnClickListener { MyApplication.activityManager.startPlayerActivity(this@RecommendActivity) }
            ivPlaylist.setOnClickListener { PlaylistDialog(this@RecommendActivity).show() }
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