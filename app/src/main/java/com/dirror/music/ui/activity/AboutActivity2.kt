package com.dirror.music.ui.activity

import android.content.Intent
import com.dirror.music.MyApplication
import com.dirror.music.R
import com.dirror.music.databinding.ActivityAbout2Binding
import com.dirror.music.service.test.TestMediaCodeInfo
import com.dirror.music.ui.base.BaseActivity
import com.dirror.music.ui.dialog.AppInfoDialog
import com.dirror.music.util.Secure
import com.dirror.music.util.UpdateUtil
import com.dirror.music.util.getVisionCode
import com.dirror.music.util.getVisionName

/**
 * 2.0 新版 AboutActivity
 */
class AboutActivity2 : BaseActivity() {

    companion object {
        // 更新日志网站
        private const val UPDATE_LOG = "https://github.com/Moriafly/DsoMusic/releases"
        private const val HISTORY_VERSION = "https://moriafly.xyz/foyou/dsomusic/history-version.html"
        // 酷安
        private const val COOLAPK_HOME_PAGE = "coolmarket://u/482045"
        private const val SPONSOR = "https://moriafly.xyz/foyou/sponsor.html"
    }

    private lateinit var binding: ActivityAbout2Binding

    override fun initBinding() {
        binding = ActivityAbout2Binding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun initView() {
        binding.apply {
            val versionType = if (Secure.isDebug()) {
                "测试版"
            } else {
                "正式版"
            }

            try {
                tvVersion.text = resources.getString(R.string.version, getVisionName(), getVisionCode(), versionType)
            } catch (e: Exception) {

            }
        }
        initMediaCodec()
    }

    override fun initListener() {
        binding.apply {
            // 检查更新
            itemCheckForUpdates.setOnClickListener { UpdateUtil.checkNewVersion(this@AboutActivity2, true) }
            // 更新日志
            itemUpdateLog.setOnClickListener { MyApplication.activityManager.startWebActivity(this@AboutActivity2, UPDATE_LOG) }
            // 源代码
            itemSourceCode.setOnClickListener {
                MyApplication.activityManager.startWebActivity(this@AboutActivity2, "https://github.com/Moriafly/DsoMusic", getString(R.string.source_code))
            }
            // 使用开源项目
            itemOpenSourceCode.setOnClickListener { startActivity(Intent(this@AboutActivity2, OpenSourceActivity::class.java)) }
            // 赞赏
            itemSponsor.setOnClickListener {
                MyApplication.activityManager.startWebActivity(this@AboutActivity2, SPONSOR)
            }

            // ivLogo
            ivLogo.setOnLongClickListener {
                AppInfoDialog(this@AboutActivity2).show()
                return@setOnLongClickListener true
            }
        }
    }

    private fun initMediaCodec() {
        val list = TestMediaCodeInfo.getCodec()
        var str = ""
        list.forEach {
            str += "${it.name}\n"
        }
        binding.tvMediaCodec.text = str
    }

}