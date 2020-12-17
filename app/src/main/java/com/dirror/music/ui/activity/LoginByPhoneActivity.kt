package com.dirror.music.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dirror.music.MyApplication
import com.dirror.music.databinding.ActivityLoginByPhoneBinding
import com.dirror.music.util.toast

class LoginByPhoneActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginByPhoneBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginByPhoneBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnLoginByPhone.setOnClickListener {
            val phone = binding.etPhone.text.toString()
            val password = binding.etPassword.text.toString()
            MyApplication.cloudMusicManager.loginByTell(phone, password, {
                // 发送广播
                val intent = Intent("com.dirror.music.LOGIN")
                intent.setPackage(packageName)
                sendBroadcast(intent)
                // 通知 Login 关闭
                setResult(RESULT_OK, Intent())
                finish()
            }, {
                toast("")
            })
        }
    }

}