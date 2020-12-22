package com.dirror.music.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.dirror.music.adapter.DetailPlaylistAdapter
import com.dirror.music.databinding.ActivityPlayHistoryBinding
import com.dirror.music.music.local.PlayHistory

class PlayHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayHistoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()

    }

    private fun initView() {
        binding.apply {
            rvPlayHistory.layoutManager = LinearLayoutManager(this@PlayHistoryActivity)
            rvPlayHistory.adapter = DetailPlaylistAdapter(PlayHistory.readPlayHistory(), this@PlayHistoryActivity)
        }
    }

}