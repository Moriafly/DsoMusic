package com.dirror.music.widget

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.customview.widget.ViewDragHelper

class SlideBackLayout (
    context: Context,
    private val scrollableView: View
) : FrameLayout(context) {
    private var mDecorView: ViewGroup? = null
    private var mRootView: View? = null
    private var mActivity: Activity? = null
    private var mViewDragHelper: ViewDragHelper? = null
    private var mSlideHeight: Float = 0.toFloat()
    private var mScreenHeight: Int = 0

    init {
        init(context)
    }

    private fun init(context: Context) {
        mActivity = context as Activity
        mViewDragHelper = ViewDragHelper.create(this, DragCallback())
    }

    fun bind() {
        mDecorView = mActivity!!.window.decorView as ViewGroup
        mRootView = mDecorView!!.getChildAt(0)
        mDecorView!!.removeView(mRootView)
        this.addView(mRootView)
        mDecorView!!.addView(this)

        val dm = DisplayMetrics()
        mActivity!!.windowManager.defaultDisplay.getMetrics(dm)
        mScreenHeight = dm.heightPixels
        mSlideHeight = dm.heightPixels * 0.3f
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return if (scrollableView.scrollY == 0) {
            mViewDragHelper!!.shouldInterceptTouchEvent(event)
        } else {
            false
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        mViewDragHelper!!.processTouchEvent(event)
        return true
    }

    internal inner class DragCallback : ViewDragHelper.Callback() {

        override fun tryCaptureView(child: View, pointerId: Int): Boolean {
            return true
        }

        override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
            val top = releasedChild.top
            if (top <= mSlideHeight) {
                mViewDragHelper!!.settleCapturedViewAt(0, 0)
            } else {
                mViewDragHelper!!.settleCapturedViewAt(0, mScreenHeight)
            }
            invalidate()
        }

        override fun getViewVerticalDragRange(child: View): Int {
            return mScreenHeight
        }

        override fun onViewPositionChanged(changedView: View, left: Int, top: Int, dx: Int, dy: Int) {
            if (changedView === mRootView && top >= mScreenHeight) {
                mActivity!!.finish()
            }
        }

        override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
            return 0
        }

        override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
            var top = top
            top = if (top >= 0) top else 0
            return top
        }
    }

    override fun computeScroll() {
        if (mViewDragHelper!!.continueSettling(true))
            invalidate()

    }

}