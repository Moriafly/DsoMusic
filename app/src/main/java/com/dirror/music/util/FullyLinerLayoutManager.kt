package com.dirror.music.util


import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler


/**
 * Author:    ZhuWenWu
 * Version    V1.0
 * Date:      2015/2/26  14:15.
 * Description:
 * Modification  History:
 * Date             Author                Version            Description
 * -----------------------------------------------------------------------------------
 * 2015/2/26        ZhuWenWu            1.0                    1.0
 * Why & What is modified:
 */
class FullyLinearLayoutManager : LinearLayoutManager {
    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, orientation: Int, reverseLayout: Boolean) : super(
        context,
        orientation,
        reverseLayout
    ) {
    }

    private val mMeasuredDimension = IntArray(2)
    override fun onMeasure(
        recycler: Recycler, state: RecyclerView.State,
        widthSpec: Int, heightSpec: Int
    ) {
        val widthMode = View.MeasureSpec.getMode(widthSpec)
        val heightMode = View.MeasureSpec.getMode(heightSpec)
        val widthSize = View.MeasureSpec.getSize(widthSpec)
        val heightSize = View.MeasureSpec.getSize(heightSpec)
        Log.i(
            TAG, """onMeasure called. 
widthMode $widthMode 
heightMode $heightSpec 
widthSize $widthSize 
heightSize $heightSize 
getItemCount() $itemCount"""
        )
        var width = 0
        var height = 0
        for (i in 0 until itemCount) {
            measureScrapChild(
                recycler, i,
                View.MeasureSpec.makeMeasureSpec(i, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(i, View.MeasureSpec.UNSPECIFIED),
                mMeasuredDimension
            )
            if (orientation == HORIZONTAL) {
                width = width + mMeasuredDimension[0]
                if (i == 0) {
                    height = mMeasuredDimension[1]
                }
            } else {
                height = height + mMeasuredDimension[1]
                if (i == 0) {
                    width = mMeasuredDimension[0]
                }
            }
        }
        when (widthMode) {
            View.MeasureSpec.EXACTLY -> width = widthSize
            View.MeasureSpec.AT_MOST, View.MeasureSpec.UNSPECIFIED -> {
            }
        }
        when (heightMode) {
            View.MeasureSpec.EXACTLY -> height = heightSize
            View.MeasureSpec.AT_MOST, View.MeasureSpec.UNSPECIFIED -> {
            }
        }
        setMeasuredDimension(width, height)
    }

    private fun measureScrapChild(
        recycler: Recycler, position: Int, widthSpec: Int,
        heightSpec: Int, measuredDimension: IntArray
    ) {
        try {
            val view = recycler.getViewForPosition(0) //fix 动态添加时报IndexOutOfBoundsException
            if (view != null) {
                val p = view.layoutParams as RecyclerView.LayoutParams
                val childWidthSpec = ViewGroup.getChildMeasureSpec(
                    widthSpec,
                    paddingLeft + paddingRight, p.width
                )
                val childHeightSpec = ViewGroup.getChildMeasureSpec(
                    heightSpec,
                    paddingTop + paddingBottom, p.height
                )
                view.measure(childWidthSpec, childHeightSpec)
                measuredDimension[0] = view.measuredWidth + p.leftMargin + p.rightMargin
                measuredDimension[1] = view.measuredHeight + p.bottomMargin + p.topMargin
                recycler.recycleView(view)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
        }
    }

    companion object {
        private val TAG = FullyLinearLayoutManager::class.java.simpleName
    }
}