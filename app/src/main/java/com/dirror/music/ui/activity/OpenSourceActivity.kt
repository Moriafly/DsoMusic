package com.dirror.music.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dirror.music.adapter.OpenSourceAdapter
import com.dirror.music.data.OpenSourceData
import com.dirror.music.databinding.ActivityOpenSourceBinding

class OpenSourceActivity : AppCompatActivity() {

    companion object {
        private const val AL2 = "Apache License (Version 2.0)"
    }

    private lateinit var binding: ActivityOpenSourceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOpenSourceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        val openSourceList = listOf(
            OpenSourceData("Kotlin Programming Language", "https://github.com/JetBrains/kotlin", AL2),
            OpenSourceData("OkHttp", "https://github.com/square/okhttp", AL2),
            OpenSourceData("Gson", "https://github.com/google/gson","BSD, part MIT and Apache 2.0"),
            OpenSourceData("Glide", "https://github.com/bumptech/glide","BSD, part MIT and Apache 2.0"),
            OpenSourceData("Glide Transformations", "https://github.com/wasabeef/glide-transformations", AL2),
            OpenSourceData("MMKV", "https://github.com/Tencent/MMKV","BSD 3-Clause license"),
            OpenSourceData("Lottie", "https://github.com/airbnb/lottie-android", AL2),
            OpenSourceData("EdgeTranslucent", "https://github.com/qinci/EdgeTranslucent","Unknown"),
            OpenSourceData("LyricViewX", "https://github.com/Moriafly/LyricViewX","GPL-3.0 License"),
            OpenSourceData("Switcher", "https://github.com/bitvale/Switcher", AL2),
            OpenSourceData("Aria", "https://github.com/AriaLyy/Aria", AL2),
            OpenSourceData("BlurView", "https://github.com/Dimezis/BlurView", AL2),
            OpenSourceData("ASimpleCache", "https://github.com/yangfuhai/ASimpleCache", AL2)
            )
        binding.rvOpenSource.layoutManager = LinearLayoutManager(this)
        binding.rvOpenSource.adapter = OpenSourceAdapter(openSourceList)
    }
}