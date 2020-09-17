package com.dirror.music.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.dirror.music.R
import com.dirror.music.adapter.OpenSourceAdapter
import com.dirror.music.adapter.OpenSourceData
import com.dirror.music.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_open_source.*

class OpenSourceActivity : BaseActivity() {

    override fun getLayoutId(): Int {
        return R.layout.activity_open_source
    }

    override fun initView() {
        val openSourceList = listOf(
            OpenSourceData("Kotlin Programming Language", "https://github.com/JetBrains/kotlin","""
                Apache License (Version 2.0)
            """.trimIndent()),

            OpenSourceData("OkHttp", "https://github.com/square/okhttp","""
                Copyright 2019 Square, Inc.

                Licensed under the Apache License, Version 2.0 (the "License");
                you may not use this file except in compliance with the License.
                You may obtain a copy of the License at

                   http://www.apache.org/licenses/LICENSE-2.0

                Unless required by applicable law or agreed to in writing, software
                distributed under the License is distributed on an "AS IS" BASIS,
                WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
                See the License for the specific language governing permissions and
                limitations under the License.
            """.trimIndent()),

            OpenSourceData("Gson", "https://github.com/google/gson","BSD, part MIT and Apache 2.0"),

            OpenSourceData("Glide", "https://github.com/bumptech/glide","BSD, part MIT and Apache 2.0"),

            OpenSourceData("BlurView", "https://github.com/Dimezis/BlurView","""
                Copyright 2016 Dmitry Saviuk

                Licensed under the Apache License, Version 2.0 (the "License");
                you may not use this file except in compliance with the License.
                You may obtain a copy of the License at

                   http://www.apache.org/licenses/LICENSE-2.0

                Unless required by applicable law or agreed to in writing, software
                distributed under the License is distributed on an "AS IS" BASIS,
                WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
                See the License for the specific language governing permissions and
                limitations under the License.
            """.trimIndent()),
        )
        rvOpenSource.layoutManager = LinearLayoutManager(this)
        rvOpenSource.adapter = OpenSourceAdapter(openSourceList)



    }
}