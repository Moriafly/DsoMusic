package com.dirror.music.ui.activity

import android.content.Intent
import com.dirror.music.R
import com.dirror.music.ui.base.BaseActivity
import com.dirror.music.util.UpdateUtil
import com.dirror.music.util.getVisionCode
import com.dirror.music.util.getVisionName
import com.dirror.music.util.openUrlByBrowser
import kotlinx.android.synthetic.main.activity_about.*

class AboutActivity : BaseActivity(R.layout.activity_about) {

    override fun initData() {

    }

    override fun initView() {
        tvVersion.text = getVisionName()
        tvVersionCode.text = getVisionCode().toString()
    }

    override fun initListener() {
        // 检查更新
        itemCheckForUpdates.setOnClickListener {
            UpdateUtil.checkNewVersion(this, true)
        }

        // 打开链接
        tvUrl.setOnClickListener {
            openUrlByBrowser(this, "https://moriafly.xyz")
        }

        //
        itemOpenSource.setOnClickListener {
            startActivity(Intent(this, OpenSourceActivity::class.java))
        }

    }

}