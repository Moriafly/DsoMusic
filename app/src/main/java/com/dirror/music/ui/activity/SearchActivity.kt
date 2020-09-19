package com.dirror.music.ui.activity

import com.dirror.music.R
import com.dirror.music.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_search.*

class SearchActivity : BaseActivity() {
    override fun getLayoutId(): Int {
        return R.layout.activity_search
    }

    override fun initData() {

    }

    override fun initView() {

    }

    override fun initListener() {
        val keywords = etSearch.text.toString()

    }
}