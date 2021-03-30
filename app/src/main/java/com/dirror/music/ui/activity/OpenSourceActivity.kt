package com.dirror.music.ui.activity

import androidx.recyclerview.widget.LinearLayoutManager
import com.dirror.music.adapter.OpenSourceAdapter
import com.dirror.music.data.OpenSourceData
import com.dirror.music.databinding.ActivityOpenSourceBinding
import com.dirror.music.ui.base.BaseActivity

class OpenSourceActivity : BaseActivity() {

    companion object {
        private const val AL2 = "Apache License (Version 2.0)"
    }

    private lateinit var binding: ActivityOpenSourceBinding

    override fun initBinding() {
        binding = ActivityOpenSourceBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun initView() {
        val openSourceList = listOf(
            OpenSourceData("Android Jetpack", "https://source.google.com", AL2),
            OpenSourceData("AndroidX", "https://source.google.com", AL2),
            OpenSourceData("Animer", "https://github.com/MartinRGB/Animer", AL2),
            OpenSourceData("ASimpleCache", "https://github.com/yangfuhai/ASimpleCache", AL2),
            OpenSourceData("BlurView", "https://github.com/Dimezis/BlurView", AL2),
            OpenSourceData("Coil", "https://github.com/coil-kt/coil", AL2),
            OpenSourceData("DsoKotlinExtensions", "https://github.com/Moriafly/DsoKotlinExtensions", AL2),
            OpenSourceData("EdgeTranslucent", "https://github.com/qinci/EdgeTranslucent","Unknown"),
            OpenSourceData("Glide", "https://github.com/bumptech/glide","BSD, part MIT and Apache 2.0"),
            OpenSourceData("Glide Transformations", "https://github.com/wasabeef/glide-transformations", AL2),
            OpenSourceData("Gson", "https://github.com/google/gson","BSD, part MIT and Apache 2.0"),
            OpenSourceData("Kotlin Programming Language", "https://github.com/JetBrains/kotlin", AL2),
            OpenSourceData("Lottie", "https://github.com/airbnb/lottie-android", AL2),
            OpenSourceData("LyricViewX", "https://github.com/Moriafly/LyricViewX","GPL-3.0 License"),
            OpenSourceData("MMKV", "https://github.com/Tencent/MMKV","BSD 3-Clause license"),
            OpenSourceData("OkHttp", "https://github.com/square/okhttp", AL2),
            OpenSourceData("PhotoView", "https://github.com/Baseflow/PhotoView", AL2),
            OpenSourceData("RgView", "https://github.com/roger1245/RgView", "Unknown"),
            OpenSourceData("Switcher", "https://github.com/bitvale/Switcher", AL2),
            )
        binding.rvOpenSource.layoutManager = LinearLayoutManager(this)
        binding.rvOpenSource.adapter = OpenSourceAdapter(openSourceList)
    }
}