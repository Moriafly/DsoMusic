package com.dirror.music.ui.activity

import androidx.recyclerview.widget.LinearLayoutManager
import com.dirror.music.App
import com.dirror.music.adapter.PrivateLetterAdapter
import com.dirror.music.databinding.ActivityPrivateLetterBinding
import com.dirror.music.ui.base.BaseActivity
import com.dirror.music.util.toast

class PrivateLetterActivity : BaseActivity() {
    private lateinit var binding: ActivityPrivateLetterBinding

    override fun initBinding() {
        binding = ActivityPrivateLetterBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun initView() {
        binding.rvPrivateLetter.layoutManager = LinearLayoutManager(this)
        App.cloudMusicManager.getPrivateLetter({
            runOnUiThread {
                binding.rvPrivateLetter.adapter = PrivateLetterAdapter(it.msgs)
            }
        }, {
            toast("获取失败")
        })

    }

}