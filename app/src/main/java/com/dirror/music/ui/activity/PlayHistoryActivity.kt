package com.dirror.music.ui.activity

import androidx.recyclerview.widget.LinearLayoutManager
import com.dirror.music.adapter.SongDataAdapter
import com.dirror.music.data.PLAYLIST_TAG_HISTORY
import com.dirror.music.databinding.ActivityPlayHistoryBinding
import com.dirror.music.music.local.PlayHistory
import com.dirror.music.ui.base.BaseActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class PlayHistoryActivity : BaseActivity() {

    private lateinit var binding: ActivityPlayHistoryBinding

    override fun initBinding() {
        binding = ActivityPlayHistoryBinding.inflate(layoutInflater)
        miniPlayer = binding.miniPlayer
        setContentView(binding.root)
    }

    override fun initView() {
        binding.apply {
            rvPlayHistory.layoutManager = LinearLayoutManager(this@PlayHistoryActivity)
            rvPlayHistory.adapter = SongDataAdapter(this@PlayHistoryActivity, PLAYLIST_TAG_HISTORY).apply {
                GlobalScope.launch {
                    submitList(PlayHistory.readPlayHistory().toList())
                }
            }
        }
    }

}