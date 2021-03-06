package com.dirror.music.ui.activity

import com.dirror.music.databinding.ActivityPlaylistInfoBinding
import com.dirror.music.ui.base.BaseActivity

class PlaylistInfoActivity : BaseActivity() {

    private lateinit var binding: ActivityPlaylistInfoBinding

    override fun initBinding() {
        binding = ActivityPlaylistInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

}