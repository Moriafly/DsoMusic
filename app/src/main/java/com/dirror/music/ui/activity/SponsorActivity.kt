package com.dirror.music.ui.activity

import com.dirror.music.databinding.ActivitySponsorBinding
import com.dirror.music.ui.base.BaseActivity
import com.dirror.music.util.Alipay
import com.dirror.music.util.AlipayUtil

class SponsorActivity : BaseActivity() {

    private lateinit var binding: ActivitySponsorBinding

    override fun initBinding() {
        binding = ActivitySponsorBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun initListener() {
        binding.apply {
            itemAlipay.setOnClickListener {
                AlipayUtil.startAlipayClient(this@SponsorActivity)
            }
        }
    }

}