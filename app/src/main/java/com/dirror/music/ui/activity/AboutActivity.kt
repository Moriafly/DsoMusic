package com.dirror.music.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dirror.music.databinding.ActivityAboutBinding
import com.dirror.music.foyou.sentence.foyoulibrary.FoyouLibrary
import com.dirror.music.util.UpdateUtil
import com.dirror.music.util.getVisionCode
import com.dirror.music.util.getVisionName
import com.dirror.music.util.openUrlByBrowser


class AboutActivity : AppCompatActivity() {

    companion object {
        private const val WEBSITE = "https://moriafly.xyz" // 官方网站
        private const val UPDATE_LOG = "https://github.com/Moriafly/dirror-music/releases" // 更新日志网站
    }

    private lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        initListener()
    }

    private fun initView() {
        binding.tvVersion.text = getVisionName()
        binding.tvVersionCode.text = getVisionCode().toString()
        binding.itemFoyouVersion.setValue(FoyouLibrary.VERSION)
    }

    private fun initListener() {
        // 检查更新
        binding.itemCheckForUpdates.setOnClickListener {
            UpdateUtil.checkNewVersion(this, true)
        }

        // 打开链接
        binding.tvUrl.setOnClickListener {
            openUrlByBrowser(this, WEBSITE)
        }

        // 开源
        binding.itemOpenSource.setOnClickListener {
            startActivity(Intent(this, OpenSourceActivity::class.java))
        }

        binding.itemUpdateLog.setOnClickListener {
            openUrlByBrowser(this, UPDATE_LOG)
        }

    }

}