package com.dirror.music.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dirror.music.MyApplication
import com.dirror.music.databinding.ActivityArtistBinding
import com.dirror.music.util.runOnMainThread
import com.dirror.music.util.toast

class ArtistActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_LONG_ARTIST_ID = "long_artist_id"

        private const val DEFAULT_ARTIST_ID = 0L
    }

    private lateinit var binding: ActivityArtistBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArtistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
    }

    private fun initView() {
        val artistId = intent.getLongExtra(EXTRA_LONG_ARTIST_ID, DEFAULT_ARTIST_ID)
        MyApplication.cloudMusicManager.getArtists(artistId) {
            runOnMainThread {
                binding.titleBar.setTitleBarText(it.artist.name)
                binding.tvDescription.text = it.artist.briefDesc
            }
        }
    }

}