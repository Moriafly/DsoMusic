package com.dirror.music.util

import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout

object LayoutParamsUtil {

    fun getConstraintLayoutLayoutParams(
        width: Int,
        height: Int,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int
    ): ConstraintLayout.LayoutParams {
        val layoutParams = ConstraintLayout.LayoutParams(width, height)
        layoutParams.setMargins(left, top, right, bottom)
        return layoutParams
    }

    fun getViewGroupLayoutParams(
        width: Int,
        height: Int,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int
    ): ViewGroup.LayoutParams {
        val layoutParams = ViewGroup.LayoutParams(width, height)
        // layoutParams.setMargins(left, top, right, bottom)
        return layoutParams
    }

}