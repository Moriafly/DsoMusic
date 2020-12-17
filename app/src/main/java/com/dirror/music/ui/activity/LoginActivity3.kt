package com.dirror.music.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dirror.music.databinding.ActivityLogin3Binding

class LoginActivity3 : AppCompatActivity() {

    private lateinit var binding: ActivityLogin3Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogin3Binding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        binding.lottieBackground.repeatCount = -1
        binding.lottieBackground.playAnimation()
        binding.lottieBackground.speed = 1f
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.lottieBackground.resumeAnimation()
    }

}