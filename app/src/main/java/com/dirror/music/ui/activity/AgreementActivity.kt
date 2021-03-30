package com.dirror.music.ui.activity

import android.content.Intent
import com.dirror.music.MyApplication
import com.dirror.music.databinding.ActivityAgreementBinding
import com.dirror.music.manager.ActivityCollector
import com.dirror.music.ui.base.BaseActivity
import com.dirror.music.ui.home.MainActivity
import com.dirror.music.util.Config
import com.dirror.music.util.Secure

class AgreementActivity : BaseActivity() {

    private lateinit var binding: ActivityAgreementBinding

    override fun initBinding() {
        binding = ActivityAgreementBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun initListener() {
        binding.btnAgree.setOnClickListener {
            MyApplication.config.mmkv.encode(Config.SHOW_AGREEMENT, false)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.btnExit.setOnClickListener {
            MyApplication.musicController.value?.stopMusicService()
            ActivityCollector.finishAll()

            object : Thread() {
                override fun run() {
                    super.run()
                    sleep(500)
                    Secure.killMyself()
                }
            }.start()
        }
    }

}