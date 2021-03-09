package com.dirror.music.ui.base

import android.os.Bundle
import androidx.fragment.app.Fragment

abstract class BaseFragment: Fragment() {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initView()
        initListener()
        initObserver()
    }

    open fun initView() {

    }

    open fun initListener() {

    }

    open fun initObserver() {

    }

}