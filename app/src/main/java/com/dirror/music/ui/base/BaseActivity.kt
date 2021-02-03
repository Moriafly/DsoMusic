package com.dirror.music.ui.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
        initView()
        initListener()
        initObserver()
        initBroadcastReceiver()
    }

    override fun onStart() {
        super.onStart()
        initShowDialogListener()
    }

    protected open fun initBinding() { }

    protected open fun initView() { }

    protected open fun initListener() { }

    protected open fun initObserver() { }

    protected open fun initBroadcastReceiver() { }

    protected open fun initShowDialogListener() { }

}