package com.dirror.music.ui.activity

import com.dirror.music.music.CloudMusic
import com.dirror.music.R
import com.dirror.music.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_login_by_phone.*

class LoginByPhoneActivity : BaseActivity(R.layout.activity_login_by_phone) {

    override fun initView() {
        btnLogin.setOnClickListener {
            CloudMusic.loginByPhone(etPhone.text.toString(), etPassword.text.toString()) {
                finish()
            }
        }
    }

}