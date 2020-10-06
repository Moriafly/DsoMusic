package com.dirror.music.ui.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity(private val layoutId: Int): AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId)
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