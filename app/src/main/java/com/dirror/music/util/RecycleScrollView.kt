package com.dirror.music.util

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.widget.ScrollView


/**
 * Created by YH on 2017/10/10.
 */
class RecyclerScrollView : ScrollView {
    private var slop = 0
    private var touch = 0

    constructor(context: Context) : super(context) {
        setSlop(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        setSlop(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        setSlop(context)
    }

    /**
     * 是否intercept当前的触摸事件
     * @param ev 触摸事件
     * @return true：调用onMotionEvent()方法，并完成滑动操作
     */
    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN ->                 //  保存当前touch的纵坐标值
                touch = ev.rawY.toInt()
            MotionEvent.ACTION_MOVE ->                 //  滑动距离大于slop值时，返回true
                if (Math.abs(ev.rawY.toInt() - touch) > slop) return true
        }
        return super.onInterceptTouchEvent(ev)
    }

    /**
     * 获取相应context的touch slop值（即在用户滑动之前，能够滑动的以像素为单位的距离）
     * @param context ScrollView对应的context
     */
    private fun setSlop(context: Context) {
        slop = ViewConfiguration.get(context).scaledTouchSlop
    }
}