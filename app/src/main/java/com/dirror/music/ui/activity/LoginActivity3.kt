package com.dirror.music.ui.activity

import android.content.Intent
import android.graphics.Typeface
import androidx.constraintlayout.widget.ConstraintLayout
import com.dirror.music.MyApplication
import com.dirror.music.databinding.ActivityLogin3Binding
import com.dirror.music.ui.base.BaseActivity
import com.dirror.music.util.getStatusBarHeight

/**
 * 预计 2.0 版
 * LoginActivity3 界面，取代原来的 LoginActivity2
 */
class LoginActivity3 : BaseActivity() {

    private lateinit var binding: ActivityLogin3Binding

    override fun initBinding() {
        binding = ActivityLogin3Binding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun initView() {
        (binding.btnCancel.layoutParams as ConstraintLayout.LayoutParams).apply {
            topMargin = getStatusBarHeight(window, this@LoginActivity3)
        }

        binding.lottieBackground.repeatCount = -1
        binding.lottieBackground.playAnimation()
        binding.lottieBackground.speed = 1f

        try {
            val typeface = Typeface.createFromAsset(assets, "fonts/Moriafly-Regular.ttf")
            binding.tvLogo.typeface = typeface
            binding.tvVersion.typeface = typeface
        } catch (e: Exception) {

        }
    }

    override fun initListener() {
        binding.apply{
            // 取消
            btnCancel.setOnClickListener { finish() }
            // 手机号登录
            btnLoginByPhone.setOnClickListener {
                MyApplication.activityManager.startLoginByPhoneActivity(this@LoginActivity3)
            }
            // UID 登录
            btnLoginByUid.setOnClickListener {
                MyApplication.activityManager.startLoginByUidActivity(this@LoginActivity3)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.lottieBackground.cancelAnimation()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            0 -> if (resultCode == RESULT_OK) {
                finish()
            }
        }
    }

}