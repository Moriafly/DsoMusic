package com.dirror.music.ui.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dirror.music.util.*

abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
        initView()
        initData()
        initListener()
        initObserver()
        initBroadcastReceiver()
        initMiniPlayer()
    }

    override fun onStart() {
        super.onStart()
        if (DarkThemeUtil.isDarkTheme(this)) {
            setStatusBarIconColor(this, false)
        }
        initShowDialogListener()
    }

    protected open fun initBinding() { }

    protected open fun initView() { }

    protected open fun initData() { }

    protected open fun initListener() { }

    protected open fun initObserver() { }

    protected open fun initBroadcastReceiver() { }

    protected open fun initShowDialogListener() { }

    protected open fun initMiniPlayer() { }

}