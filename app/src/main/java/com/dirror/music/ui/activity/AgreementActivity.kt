package com.dirror.music.ui.activity

import android.content.Intent
import com.dirror.music.App
import com.dirror.music.databinding.ActivityAgreementBinding
import com.dirror.music.manager.ActivityCollector
import com.dirror.music.ui.base.BaseActivity
import com.dirror.music.ui.main.MainActivity
import com.dirror.music.util.*
import com.dirror.music.util.sky.SkySecure
import java.util.*

/**
 * 忽略上传
 */
class AgreementActivity : BaseActivity() {

    private lateinit var binding: ActivityAgreementBinding

    private val calendar = Calendar.getInstance()
    private val year = calendar.get(Calendar.YEAR)
    private val month = calendar[Calendar.MONTH] + 1
    private val day = calendar[Calendar.DAY_OF_MONTH]
    private val trueKey = SkySecure.getMD5("${getVisionName()}${year}${month}${day}${getVisionCode()}").toUpperCase(Locale.ROOT)

    override fun initBinding() {
        binding = ActivityAgreementBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun initView() {

    }

    override fun initListener() {
        binding.btnAgree.setOnClickListener {
            val key = binding.etKey.text.toString()
            if (key == "我同意") {
                App.mmkv.encode(Config.SHOW_AGREEMENT, false)
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                toast("请重新输入")
            }
        }

        binding.btnExit.setOnClickListener {
            App.musicController.value?.stopMusicService()
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

    external fun cry(): String

}