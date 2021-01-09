package com.dirror.music.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dirror.music.databinding.ActivityPlaylistInfoBinding

class PlaylistInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlaylistInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlaylistInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}