package com.dirror.music.ui.activity

import com.dirror.music.App.Companion.mmkv
import com.dirror.music.databinding.ActivityMemoryBinding
import com.dirror.music.ui.base.BaseActivity
import com.dirror.music.util.Config

class MemoryActivity : BaseActivity() {

    private lateinit var binding: ActivityMemoryBinding

    override fun initBinding() {
        binding = ActivityMemoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun initView() {
        with(binding) {
            switcherNeteaseGoodComments.setChecked(mmkv.decodeBool(Config.NETEASE_GOOD_COMMENTS, false))
            switcherQQWebSource.setChecked(mmkv.decodeBool(Config.QQ_WEB_SOURCE, false))
        }
    }

    override fun initListener() {
        with(binding) {
            switcherNeteaseGoodComments.setOnCheckedChangeListener {
                mmkv.encode(Config.NETEASE_GOOD_COMMENTS, it)
            }
            switcherQQWebSource.setOnCheckedChangeListener {
                mmkv.encode(Config.QQ_WEB_SOURCE, it)
            }
        }
    }

}