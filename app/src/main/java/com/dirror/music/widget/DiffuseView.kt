package com.dirror.music.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.View

import java.util.ArrayList

class DiffuseView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = -1) : View(context, attrs, defStyleAttr) {

    private var mMaxRadiusWidth: Int = 0
    private var mMinRadiusWidth: Int = 0

    private var mIsDiffuse = false

    private val mAlphas = ArrayList<Int>()

    private val mWidths = ArrayList<Int>()

    private var mPaint: Paint? = null

    //扩散圆宽度
    private val mDiffuseWidth = 30

    init {
        init()
    }

    private fun init() {
        mPaint = Paint()
        mPaint!!.isAntiAlias = true
        mPaint!!.style = Paint.Style.STROKE
        mPaint!!.strokeWidth = 2.5f
        mPaint!!.color = Color.GRAY

        val toDp = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 110f,
            this.resources.displayMetrics)
        mMinRadiusWidth = toDp.toInt()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mMaxRadiusWidth = (measuredHeight / 2).coerceAtMost(measuredWidth / 2)
        Log.d(TAG, "this is $mMaxRadiusWidth")
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (mWidths.isEmpty()) {
            mWidths.add(mMinRadiusWidth)
            mAlphas.add(255)
        }

        for (i in mAlphas.indices) {
            val alpha = mAlphas[i]
            mPaint!!.alpha = alpha
            val width = mWidths[i]
            canvas.drawCircle((getWidth() / 2).toFloat(), (height / 2).toFloat(), width.toFloat(), mPaint!!)

            if (alpha >= 2 && width <= mMaxRadiusWidth) {
                mAlphas[i] = alpha - 2
                mWidths[i] = width + 1
            } else if (width <= mMinRadiusWidth) {
                mWidths[i] = width + 1
            }
        }
        // 判断当扩散圆扩散到指定宽度时添加新扩散圆
        if (mWidths[mWidths.size - 1] >= mMinRadiusWidth + mDiffuseWidth) {
            mWidths.add(mMinRadiusWidth)
            mAlphas.add(255)
        }

        //扩散至只指定宽度或圆圈数过多，则删除第一个
        if (mWidths[0] >= mMaxRadiusWidth - 2 || mWidths.size > 10) {
            mWidths.removeAt(0)
            mAlphas.removeAt(0)
        }
        Log.d(TAG, "max Width$mMaxRadiusWidth")

        if (mIsDiffuse) {
            postInvalidateDelayed(30)
        }
    }

    /**
     * 开始扩散
     */
    fun start() {
        if (!mIsDiffuse) {
            mIsDiffuse = true
            invalidate()
        }
    }

    /**
     * 停止扩散
     */
    fun stop() {
        mIsDiffuse = false
    }

    fun setColor(color: Int) {
        mPaint?.color = color
    }

    companion object {
        private val TAG = "DiffuseView"
    }
}