package com.dirror.music.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dirror.music.music.CloudMusic
import com.dirror.music.R
import com.dirror.music.databinding.ActivityLoginByUidBinding
import com.dirror.music.util.toast

/**
 * 通过网易云 UID 登录
 */
class LoginByUidActivity : AppCompatActivity(R.layout.activity_login_by_uid) {

    private lateinit var binding: ActivityLoginByUidBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginByUidBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initListener()
    }

    private fun initListener() {
        // 点击登录按钮
        binding.btnLogin.setOnClickListener {
            // 获取输入
            val text = binding.etUid.text.toString()
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