package com.dirror.music.ui.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity2(): AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData() // 初始化数据
        initView()
        initListener() // 监听
    }

    /**
     * 初始化数据
     */
    protected open fun initData() {

    }

    /**
     * 初始化 View
     */
    protected open fun initView() {

    }

    /**
     * 一些 button 的 setOnClickListener
     */
    protected open fun initListener() {

    }
}