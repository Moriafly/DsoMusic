package com.dirror.music.ui.activity

import com.dirror.music.music.CloudMusic
import com.dirror.music.R
import com.dirror.music.ui.base.BaseActivity
import com.dirror.music.util.toast
import kotlinx.android.synthetic.main.activity_login_by_uid.*

/**
 * 通过网易云 UID 登录
 */
class LoginByUidActivity : BaseActivity(R.layout.activity_login_by_uid) {

    override fun initView() {

    }

    override fun initListener() {
        // 点击登录按钮
        btnLogin.setOnClickListener {
            // 获取输入
            val text = etUid.text.toString()
            // 判断是否直接是网易云分享用户链接
            if (text != "") {
                val index = text.indexOf("id=")
                if (index != -1) {
                    val netease = text.subSequence(index + 3, text.length).toString()
                    CloudMusic.loginByUid(netease.toLong()) {
                        finish()
                    }
                } else {
                    CloudMusic.loginByUid(text.toLong()) {
                        finish()
                    }
                }
            } else {
                toast("请输入 UID")
            }

        }
    }

}