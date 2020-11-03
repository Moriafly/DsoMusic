package com.dirror.music.ui.activity

import android.content.Intent
import android.view.View
import com.dirror.music.R
import com.dirror.music.foyou.sentence.foyoulibrary.FoyouLibrary
import com.dirror.music.ui.base.BaseActivity
import com.dirror.music.util.*
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : BaseActivity(R.layout.activity_settings) {

    override fun initData() {

    }

    override fun initView() {
        switchPlayOnMobile.isChecked = StorageUtil.getBoolean(StorageUtil.PLAY_ON_MOBILE, false)

        itemVersion.setValue("${getVisionName()}(${getVisionCode()})")
        itemFoyouVersion.setValue(FoyouLibrary.VERSION)
    }

    override fun initListener() {
        // 反馈
        itemFeedback.setOnClickListener {
            startActivity(Intent(this, FeedbackActivity::class.java))
        }

        itemOpenSource.setOnClickListener {
            startActivity(Intent(this, OpenSourceActivity::class.java))
        }

        itemSourceCode.setOnClickListener {
            openUrlByBrowser(this, "https://github.com/Moriafly/dirror-music")
        }

        // 语言
        itemLanguage.setOnClickListener {
            var language = StorageUtil.getInt(StorageUtil.LANGUAGE, 0)
            language = if (language == 0) {
                1
            } else {
                0
            }
            StorageUtil.putInt(StorageUtil.LANGUAGE, language)
            LanguageUtils.setLanguage(language)
        }

        itemPlayOnMobile.setOnClickListener {
            switchPlayOnMobile.isChecked = !switchPlayOnMobile.isChecked
        }

        switchPlayOnMobile.setOnCheckedChangeListener { buttonView, isChecked ->
            StorageUtil.putBoolean(StorageUtil.PLAY_ON_MOBILE, isChecked)
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