package com.dirror.music.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.dirror.music.MyApplication
import com.dirror.music.adapter.PrivateLetterAdapter
import com.dirror.music.databinding.ActivityPrivateLetterBinding
import com.dirror.music.util.toast

class PrivateLetterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPrivateLetterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrivateLetterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        binding.rvPrivateLetter.layoutManager = LinearLayoutManager(this)
        MyApplication.cloudMusicManager.getPrivateLetter({
            runOnUiThread {
                binding.rvPrivateLetter.adapter = PrivateLetterAdapter(it.msgs)
            }
        }, {
            toast("获取失败")
        })

    }

}