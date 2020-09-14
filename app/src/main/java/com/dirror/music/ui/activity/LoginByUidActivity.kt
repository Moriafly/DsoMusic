package com.dirror.music.ui.activity

import com.dirror.music.CloudMusic
import com.dirror.music.R
import com.dirror.music.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_login_by_uid.*

class LoginByUidActivity : BaseActivity() {

    override fun getLayoutId(): Int {
        return R.layout.activity_login_by_uid
    }

    override fun initView() {
        btnLogin.setOnClickListener {
            CloudMusic.loginByUid(etUid.text.toString().toInt())
        }
    }
}