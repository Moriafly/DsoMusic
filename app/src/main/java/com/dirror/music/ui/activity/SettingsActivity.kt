package com.dirror.music.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dirror.music.MyApplication
import com.dirror.music.R
import com.dirror.music.databinding.ActivitySettingsBinding
import com.dirror.music.util.*

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initData()
        initView()
        initListener()
    }
    
    private fun initData() {

    }

    private fun initView() {
        binding.switchPlayOnMobile.isChecked = MyApplication.mmkv.decodeBool(Config.PLAY_ON_MOBILE, false)
        binding.switchPauseSongAfterUnplugHeadset.isChecked = MyApplication.mmkv.decodeBool(Config.PAUSE_SONG_AFTER_UNPLUG_HEADSET, true)
    }

    private fun initListener() {
        // 反馈
        binding.itemFeedback.setOnClickListener {
            startActivity(Intent(this, FeedbackActivity::class.java))
        }

        binding.itemSourceCode.setOnClickListener {
            openUrlByBrowser(this, "https://github.com/Moriafly/dirror-music")
        }

        binding.itemPlayOnMobile.setOnClickListener {
            binding.switchPlayOnMobile.isChecked = !binding.switchPlayOnMobile.isChecked
        }

        binding.switchPlayOnMobile.setOnCheckedChangeListener { _, isChecked ->
            MyApplication.mmkv.encode(Config.PLAY_ON_MOBILE, isChecked)
        }

        binding.itemPauseSongAfterUnplugHeadset.setOnClickListener {
            binding.switchPauseSongAfterUnplugHeadset.isChecked = !binding.switchPauseSongAfterUnplugHeadset.isChecked
        }

        binding.switchPauseSongAfterUnplugHeadset.setOnCheckedChangeListener { _, isChecked ->
            MyApplication.mmkv.encode(Config.PAUSE_SONG_AFTER_UNPLUG_HEADSET, isChecked)
        }

        binding.itemAbout.setOnClickListener {
            startActivity(Intent(this, AboutActivity::class.java))
        }

        binding.itemTestCookie.setOnClickListener {
            val cookie = MyApplication.userManager.getCloudMusicCookie()
            if (cookie != "") {
                toast("Cookie 存在，是否过时未知，已经导入剪贴板")
                copyToClipboard(this, cookie)
            } else {
                toast("Cookie 不存在")
            }
        }
    }

    override fun finish() {
        super.finish()
//        overridePendingTransition(
//            R.anim.anim_slide_enter_right,
//            R.anim.anim_slide_exit_left
//        )
    }
}