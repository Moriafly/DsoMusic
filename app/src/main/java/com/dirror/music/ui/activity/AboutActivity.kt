package com.dirror.music.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dirror.music.MyApplication
import com.dirror.music.databinding.ActivityAboutBinding
import com.dirror.music.foyou.sentence.foyoulibrary.FoyouLibrary
import com.dirror.music.util.*
import com.google.gson.Gson
import java.lang.Exception

class AboutActivity : AppCompatActivity() {

    companion object {
        private const val WEBSITE = "https://moriafly.xyz" // 官方网站
        private const val UPDATE_LOG = "https://github.com/Moriafly/dirror-music/releases" // 更新日志网站
        private const val WEB_INFO = "https://moriafly.xyz/dirror-music/info.json"
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

    }

    data class InfoData(
        val cloudMusic: CloudMusicData
    ) {
        data class CloudMusicData(
            val version: String
        )
    }

}