package com.dirror.music.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dirror.music.adapter.OpenSourceAdapter
import com.dirror.music.adapter.OpenSourceData
import com.dirror.music.databinding.ActivityOpenSourceBinding

class OpenSourceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOpenSourceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOpenSourceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        val openSourceList = listOf(
            OpenSourceData("Kotlin Programming Language", "https://github.com/JetBrains/kotlin","Apache License (Version 2.0)"),
            OpenSourceData("OkHttp", "https://github.com/square/okhttp", "Apache License (Version 2.0)"),
            OpenSourceData("Gson", "https://github.com/google/gson","BSD, part MIT and Apache 2.0"),
            OpenSourceData("Glide", "https://github.com/bumptech/glide","BSD, part MIT and Apache 2.0"),
            OpenSourceData("Glide Transformations", "https://github.com/wasabeef/glide-transformations","Apache License (Version 2.0)"),
            OpenSourceData("Banner", "https://github.com/youth5201314/banner","Apache License (Version 2.0)"),
            OpenSourceData("MMKV", "https://github.com/Tencent/MMKV","BSD 3-Clause license"),
            OpenSourceData("Lottie", "https://github.com/airbnb/lottie-android","Apache-2.0 License"),
            OpenSourceData("EdgeTranslucent", "https://github.com/qinci/EdgeTranslucent","Unknown"),
        )
        binding.rvOpenSource.layoutManager = LinearLayoutManager(this)
        binding.rvOpenSource.adapter = OpenSourceAdapter(openSourceList)
    }
}