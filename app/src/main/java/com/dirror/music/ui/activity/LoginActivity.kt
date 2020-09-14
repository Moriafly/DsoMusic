package com.dirror.music.ui.activity

import com.dirror.music.R
import com.dirror.music.ui.base.BaseActivity
import com.dirror.music.util.setStatusBarIconColor

class LoginActivity : BaseActivity() {
    override fun getLayoutId(): Int {
        return R.layout.activity_login
    }

    override fun initView() {
        setStatusBarIconColor(this, false)
    }
}