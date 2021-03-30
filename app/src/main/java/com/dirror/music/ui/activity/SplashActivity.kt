package com.dirror.music.ui.activity

import android.content.Intent
import com.dirror.music.MyApplication
import com.dirror.music.databinding.ActivitySplashBinding
import com.dirror.music.ui.base.BaseActivity
import com.dirror.music.ui.home.MainActivity
import com.dirror.music.util.Config

/**
 * 启动页 Activity
 */
class SplashActivity : BaseActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun initBinding() {
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()
        if (MyApplication.config.mmkv.decodeBool(Config.SHOW_AGREEMENT, true)) {
            startActivity(Intent(this, AgreementActivity::class.java))
        } else {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        finish()
    }

}