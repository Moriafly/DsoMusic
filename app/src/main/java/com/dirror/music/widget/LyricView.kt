package com.dirror.music.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import com.dirror.music.R

/**
 * @name LyricView
 * 自定义歌词 View
 */
class LyricView: View {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val paint by lazy { Paint(Paint.ANTI_ALIAS_FLAG) } // 通过惰性加载创建抗锯齿画笔
    private var bigTextSize = 0f
    private var smallTextSize = 0f
    private var commonColor = 0
    private var focusColor = 0

    init {
        bigTextSize = resources.getDimension(R.dimen.bigTextSize)
        smallTextSize = resources.getDimension(R.dimen.smallTextSize)
        commonColor = resources.getColor(R.color.colorCommonTextForeground)
        focusColor = resources.getColor(R.color.colorTextForeground)
        // paint 属性
        paint.textAlign = Paint.Align.CENTER // 文本 x 坐标居中
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        // 初始化 paint 颜色和大小
        paint.textSize = bigTextSize
        paint.color = focusColor

        val text = "正在加载歌词"
        // 求文本的高度和宽度
        val bounds = Rect()
        paint.getTextBounds(text, 0, text.length, bounds) // 给 bounds 传递数据，c 语言写法
        val textWidth = bounds.width()
        val textHeight = bounds.height()

        val x = width / 2 - textWidth / 2
        val y = height / 2 + textHeight / 2
        // 绘制内容
        canvas?.drawText(text, width / 2 .toFloat(), y.toFloat(), paint)
    }

    // 在 layout 布局完成后执行
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

    }
}