package com.dirror.music.ui.activity

import android.content.Intent
import com.dirror.music.R
import com.dirror.music.ui.base.BaseActivity
import com.dirror.music.util.getVisionCode
import com.dirror.music.util.getVisionName
import com.dirror.music.util.openUrlByBrowser
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : BaseActivity() {

    override fun getLayoutId(): Int {
        return R.layout.activity_settings
    }

    override fun initData() {

    }

    override fun initView() {
        itemVersion.setValue("${getVisionName()}(${getVisionCode()})")
    }

    override fun initListener() {
        itemOpenSource.setOnClickListener {
            startActivity(Intent(this, OpenSourceActivity::class.java))
        }

        itemSourceCode.setOnClickListener {
            openUrlByBrowser(this, "https://github.com/Moriafly/dirror-music")
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(
            R.anim.anim_slide_enter_right,
            R.anim.anim_slide_exit_left
        )
    }
}