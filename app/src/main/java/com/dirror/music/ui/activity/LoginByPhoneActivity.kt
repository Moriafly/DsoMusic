package com.dirror.music.ui.activity

import android.content.Intent
import android.view.View
import androidx.activity.viewModels
import com.dirror.music.MyApplication
import com.dirror.music.R
import com.dirror.music.databinding.ActivityLoginByPhoneBinding
import com.dirror.music.ui.base.BaseActivity
import com.dirror.music.ui.viewmodel.LoginCellphoneViewModel
import com.dirror.music.util.Secure
import com.dirror.music.util.runOnMainThread
import com.dirror.music.util.toast

class LoginByPhoneActivity : BaseActivity() {

    companion object {
        const val SE = "Dso Music"
    }

    private val loginCellphoneViewModel: LoginCellphoneViewModel by viewModels()

    lateinit var binding: ActivityLoginByPhoneBinding

    override fun initBinding() {
        if (getString(R.string.app_name) == SE) {
            binding = ActivityLoginByPhoneBinding.inflate(layoutInflater)
            setContentView(binding.root)
        } else {
            Secure.killMyself()
        }
    }

    override fun initListener() {
        binding.btnLoginByPhone.setOnClickListener {
            val phone = binding.etPhone.text.toString()
            val password = binding.etPassword.text.toString()
            if (phone == "" || password == "") {
                toast("请输入手机号或密码")
            } else {
                binding.btnLoginByPhone.visibility = View.GONE
                binding.llLoading.visibility = View.VISIBLE
                binding.lottieLoading.repeatCount = -1
                binding.lottieLoading.playAnimation()
                loginCellphoneViewModel.loginByCellphone(phone, password, {
                    // 发送广播
                    val intent = Intent("com.dirror.music.LOGIN")
                    intent.setPackage(packageName)
                    sendBroadcast(intent)
                    // 通知 Login 关闭
                    setResult(RESULT_OK, Intent())
                    finish()
                }, {
                    runOnMainThread {
                        binding.btnLoginByPhone.visibility = View.VISIBLE
                        binding.llLoading.visibility = View.GONE
                        binding.lottieLoading.cancelAnimation()
                        toast("登录失败，请检查用户名或者密码")
                    }
                })
            }
        }
    }

}