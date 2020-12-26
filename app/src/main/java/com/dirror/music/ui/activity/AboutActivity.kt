package com.dirror.music.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.PictureDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.dirror.music.MyApplication
import com.dirror.music.databinding.ActivityAboutBinding
import com.dirror.music.foyou.sentence.foyoulibrary.FoyouLibrary
import com.dirror.music.util.*
import com.dirror.music.util.GlideUtil
import com.google.gson.Gson


class AboutActivity : AppCompatActivity() {

    companion object {
        private const val WEBSITE = "https://moriafly.xyz" // 官方网站
        private const val UPDATE_LOG = "https://github.com/Moriafly/dirror-music/releases" // 更新日志网站
        private const val WEB_INFO = "https://moriafly.xyz/dirror-music/info.json"
        private const val HISTORY_VERSION = "https://moriafly.xyz/foyou/dsomusic/history-version.html"
    }

    private lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        initListener()
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        val versionType = if (Secure.isDebug()) {
            "测试版"
        } else {
            "正式版"
        }
        binding.tvVersion.text = "${versionType}\n版本：${getVisionName()}(${getVisionCode()})"
        binding.itemFoyouVersion.setValue(FoyouLibrary.VERSION)

        MagicHttp.OkHttpManager().newGet(WEB_INFO, {
            try {
                val infoData: InfoData = Gson().fromJson(it, InfoData::class.java)
                runOnUiThread {
                    binding.itemCloudMusicVersion.setValue(infoData.cloudMusic.version)
                }
            } catch (e: Exception) {
                runOnUiThread {
                    binding.itemCloudMusicVersion.setValue("Unknown")
                }
            }
        }, {
            runOnMainThread {
                binding.itemCloudMusicVersion.setValue("Unknown")
            }
        })


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
            MyApplication.activityManager.startWebActivity(this, UPDATE_LOG)
        }

        binding.itemHistoryVersion.setOnClickListener {
            MyApplication.activityManager.startWebActivity(this, HISTORY_VERSION)
        }

    }

    data class InfoData(
        val cloudMusic: CloudMusicData
    ) {
        data class CloudMusicData(
            val version: String
        )
    }

}