package com.dirror.music.ui.activity

import com.dirror.music.R
import com.dirror.music.ui.base.BaseActivity

class SettingsActivity : BaseActivity() {

    override fun getLayoutId(): Int {
        return R.layout.activity_settings
    }

    override fun initData() {

    }

    override fun initView() {

    }

    override fun finish() {
        super.finish()
        overridePendingTransition(
            R.anim.anim_slide_enter_right,
            R.anim.anim_slide_exit_left
        )
    }
}