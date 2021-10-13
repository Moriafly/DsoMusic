package com.dirror.music.ui.activity

import android.content.Intent
import com.dirror.music.App
import com.dirror.music.R
import com.dirror.music.databinding.ActivityAboutBinding
import com.dirror.music.service.test.TestMediaCodeInfo
import com.dirror.music.ui.base.BaseActivity
import com.dirror.music.ui.dialog.AppInfoDialog
import com.dirror.music.util.*

class AboutActivity : BaseActivity() {

    companion object {
        // 更新日志网站
        private const val UPDATE_LOG = "https://github.com/Moriafly/DsoMusic/releases"

        const val SPONSOR = "https://moriafly.gitee.io/dso-page/page/sponsor.html"

        private const val QQ_GROUP_KEY = "p-8u-ET_WM5c_QWByacQHG6DD3fgE0SP"
    }

    private lateinit var binding: ActivityAboutBinding

    override fun initBinding() {
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun initView() {
        with(binding) {
            // ivLogo.setColorFilter(R.color.colorSubIconForeground.asColor(this@AboutActivity))
        }
        binding.apply {
            val versionType = if (Secure.isDebug()) {
                "测试版"
            } else {
                "正式版"
            }

            try {
                tvVersion.text = resources.getString(R.string.version, getVisionName(), versionType)
            } catch (e: Exception) {

            }
        }
        initMediaCodec()
    }

    override fun initListener() {
        binding.apply {
            // 检查更新
            itemCheckForUpdates.setOnClickListener { UpdateUtil.checkNewVersion(this@AboutActivity, true) }
            // 更新日志
            itemUpdateLog.setOnClickListener { App.activityManager.startWebActivity(this@AboutActivity, UPDATE_LOG) }
            // 源代码
            itemSourceCode.setOnClickListener {
                App.activityManager.startWebActivity(this@AboutActivity, "https://github.com/Moriafly/DsoMusic", getString(R.string.source_code))
            }
            // 使用开源项目
            itemOpenSourceCode.setOnClickListener { startActivity(Intent(this@AboutActivity, OpenSourceActivity::class.java)) }

            tvDsoMusic.setOnLongClickListener {
                AppInfoDialog(this@AboutActivity).show()
                return@setOnLongClickListener true
            }

            itemJoinQQGroup.setOnClickListener {
                joinQQGroup(this@AboutActivity, QQ_GROUP_KEY)
            }

            moriafly.setOnLongClickListener {
                toast("Moriafly settings reset")
                App.mmkv.encode(Config.SHOW_AGREEMENT, true)
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