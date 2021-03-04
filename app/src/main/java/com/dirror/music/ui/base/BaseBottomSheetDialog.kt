package com.dirror.music.ui.base

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import com.dirror.music.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog


abstract class BaseBottomSheetDialog(context: Context): BottomSheetDialog(context, R.style.style_default_dialog) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // for transparent
//        val screenHeight: Int = ScreenUtil.getScreenHeight(context)
//        val statusBarHeight: Int = ScreenUtil.getStatusBarHeight(context)
//        val dialogHeight = screenHeight - statusBarHeight
//        val window: Window? = window
//        window?.setLayout(
//            ViewGroup.LayoutParams.MATCH_PARENT,
//            if (dialogHeight == 0) ViewGroup.LayoutParams.MATCH_PARENT else dialogHeight
//        )

        window?.setGravity(Gravity.BOTTOM)
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window?.setWindowAnimations(R.style.dialog_animation)
        initView()
        initListener()
    }

    open fun initView() {

    }

    open fun initListener() {

    }

    override fun onStart() {
        super.onStart()
        // for landscape mode
        val behavior: BottomSheetBehavior<*> = behavior
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

}