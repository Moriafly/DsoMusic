package com.dirror.music.ui.activity

import android.content.Intent
import com.dirror.music.R
import com.dirror.music.ui.base.BaseActivity
import com.dirror.music.util.setStatusBarIconColor
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BaseActivity() {
    override fun getLayoutId(): Int {
        return R.layout.activity_login
    }

    override fun initView() {
        // 白色状态栏图标
        setStatusBarIconColor(this, false)

        btnLoginByPhone.setOnClickListener {
            startActivity(Intent(this, LoginByPhoneActivity::class.java))
        }
    }
}