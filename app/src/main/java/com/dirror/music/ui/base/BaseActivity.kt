package com.dirror.music.ui.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toDrawable
import com.dirror.music.MyApplication
import com.dirror.music.util.*

abstract class BaseActivity : AppCompatActivity {

    constructor() {

    }

    constructor(noBackground: Boolean) {
        this.noBackground = noBackground
    }

    private var noBackground = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
        initView()
        initData()
        initListener()
        initObserver()
        initBroadcastReceiver()
        if (!noBackground) {
            val path = MyApplication.mmkv.decodeString(Config.THEME_BACKGROUND, "")
            if (path.isNotEmpty()) {
                GlideUtil.load(path) {
                    window.setBackgroundDrawable(it.toDrawable(resources))
                }
            }
        }
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

}