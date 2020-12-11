package com.dirror.music.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dirror.music.MyApplication
import com.dirror.music.music.CloudMusic
import com.dirror.music.R
import com.dirror.music.databinding.ActivityLoginByPhoneBinding
import com.dirror.music.ui.base.BaseActivity
import com.dirror.music.util.toast
import kotlinx.android.synthetic.main.activity_login_by_phone.*

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
                val intent = Intent()
                setResult(RESULT_OK, intent)
                finish()
            }, {
                toast("")
            })
        }
    }

}