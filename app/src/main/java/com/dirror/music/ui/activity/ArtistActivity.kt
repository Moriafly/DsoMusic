package com.dirror.music.ui.activity

import com.dirror.music.App
import com.dirror.music.databinding.ActivityArtistBinding
import com.dirror.music.ui.base.BaseActivity
import com.dirror.music.util.runOnMainThread

/**
 * 艺术家页
 */
class ArtistActivity : BaseActivity() {

    companion object {
        const val EXTRA_LONG_ARTIST_ID = "long_artist_id"
        private const val DEFAULT_ARTIST_ID = 0L
    }

    private lateinit var binding: ActivityArtistBinding

    override fun initBinding() {
        binding = ActivityArtistBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun initView() {
        val artistId = intent.getLongExtra(EXTRA_LONG_ARTIST_ID, DEFAULT_ARTIST_ID)
        App.cloudMusicManager.getArtists(artistId) {
            runOnMainThread {
                binding.titleBar.setTitleBarText(it.artist.name)
                val description = it.artist.briefDesc
                binding.tvDescription.text = if (!description.isNullOrEmpty()) {
                    description
                } else {
                    "暂无介绍"
                }
            }
        }
    }

}