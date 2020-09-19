package com.dirror.music.ui.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
        initData() // 初始化数据
        initView()
        initListener() // 监听
    }

    /**
     * 获取布局 id
     */
    abstract fun getLayoutId(): Int

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