package com.dirror.music.ui.activity

import androidx.recyclerview.widget.LinearLayoutManager
import com.dirror.music.R
import com.dirror.music.adapter.OpenSourceAdapter
import com.dirror.music.adapter.OpenSourceData
import com.dirror.music.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_open_source.*

class OpenSourceActivity : BaseActivity(R.layout.activity_open_source) {

    override fun initView() {
        val openSourceList = listOf(
            OpenSourceData("Kotlin Programming Language", "https://github.com/JetBrains/kotlin","Apache License (Version 2.0)"),
            OpenSourceData("OkHttp", "https://github.com/square/okhttp", "Apache License (Version 2.0)"),
            OpenSourceData("Gson", "https://github.com/google/gson","BSD, part MIT and Apache 2.0"),
            OpenSourceData("Glide", "https://github.com/bumptech/glide","BSD, part MIT and Apache 2.0"),
            OpenSourceData("BlurView", "https://github.com/Dimezis/BlurView","Apache License (Version 2.0)"),
            OpenSourceData("Banner", "https://github.com/youth5201314/banner","Apache License (Version 2.0)"),
        )
        rvOpenSource.layoutManager = LinearLayoutManager(this)
        rvOpenSource.adapter = OpenSourceAdapter(openSourceList)



    }
}