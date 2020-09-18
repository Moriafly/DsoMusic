package com.dirror.music.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import com.dirror.music.R
import com.dirror.music.data.LyricData
import com.dirror.music.util.LyricUtil
import java.time.Duration

/**
 * @name LyricView
 * 自定义歌词 View
 */
class LyricView: View {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val paint by lazy { Paint(Paint.ANTI_ALIAS_FLAG) } // 通过惰性加载创建抗锯齿画笔
    private val linePaint by lazy { Paint(Paint.ANTI_ALIAS_FLAG) }
    private val lyricList by lazy { ArrayList<LyricData>() }
    private var centerLine = 10
    private var bigTextSize = 0f
    private var smallTextSize = 0f
    private var commonColor = 0
    private var focusColor = 0
    private var lyricLineHeight = 0

    var songId = -1L

    var duration = 0 // 歌曲总时长
    var progress = 0 // 当前播放进度

    init {
        bigTextSize = resources.getDimension(R.dimen.bigTextSize)
        smallTextSize = resources.getDimension(R.dimen.smallTextSize)
        commonColor = resources.getColor(R.color.colorCommonTextForeground)
        focusColor = resources.getColor(R.color.colorTextForeground)
        lyricLineHeight = resources.getDimensionPixelOffset(R.dimen.lyricLineHeight)

        // paint 属性
        paint.textAlign = Paint.Align.CENTER // 文本 x 坐标居中

        lyricList.add(LyricData(0, ""))
        // 循环添加歌词单句 data
//        for (i in 0..30) {
//            lyricList.add(LyricData(2000 * i, "正在播放第${i}行歌词"))
//        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        // drawSingleLine(canvas)
        drawMultiLine(canvas)

    }

    /**
     * 绘制单行文本
     */
    private fun drawSingleLine(canvas: Canvas?) {
        // 初始化 paint 颜色和大小
        paint.textSize = bigTextSize
        paint.color = focusColor


        val text = ""
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

    /**
     * 绘制多行居中文本
     */
    private fun drawMultiLine(canvas: Canvas?) {
        //
        var lineTime = 0
        if (centerLine == lyricList.lastIndex) {
            lineTime = duration - lyricList.get(centerLine).startTime
        } else {
            val centerStartTime = lyricList[centerLine].startTime
            val nextStartTime = lyricList[centerLine + 1].startTime
            lineTime = nextStartTime - centerStartTime
        }
        // 偏移时间
        val offsetTime = progress - lyricList[centerLine].startTime
        val offsetPercent = offsetTime / (lineTime).toFloat()
        val offsetY = offsetPercent * lyricLineHeight



        val centerText = lyricList.get(centerLine).content
        val bounds = Rect()
        paint.getTextBounds(centerText, 0, centerText.length, bounds) // 给 bounds 传递数据，c 语言写法
        val textHeight = bounds.height()

        val centerTextY = height / 2 + textHeight / 2 - offsetY

        for ((index, value) in lyricList.withIndex()) {
            // paint.isFakeBoldText = true
            if (index == centerLine) {
                // 绘制居中行
                paint.textSize = bigTextSize
                paint.color = focusColor
            } else {
                // 绘制居中行
                paint.textSize = smallTextSize
                paint.color = commonColor
            }
            val currentTextX = width / 2
            val currentTextY = centerTextY + (index - centerLine) * lyricLineHeight

            // 超出边界
            // 超出上边界
            if (currentTextY < 0) continue
            // 超出下边界
            if (currentTextY > height + lyricLineHeight) break

            canvas?.drawText(value.content, currentTextX.toFloat(), currentTextY.toFloat(), paint)
        }
    }

    // 在 layout 布局完成后执行
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

    }

    /**
     * 传递当前播放进度
     */
    fun updateProgress(progress: Int) {
        this.progress = progress
        // 先判断居中行是不是最后一行
        if (progress >= lyricList.get(lyricList.lastIndex).startTime) {
            centerLine = lyricList.lastIndex
        } else {
            // 其他行
            for (index in 0 until lyricList.lastIndex) {
                // progress 大于等于当前行开始时间小于下一行开始时间
                val currentStartTime = lyricList.get(index).startTime
                val nextStartTime = lyricList.get(index + 1).startTime
                if (progress in currentStartTime until nextStartTime) {
                    centerLine = index
                    break
                }
            }
        }

        // 找到后绘制
        invalidate() // onDraw
        // postInvalidate() // onDraw 可以在子线程
        // requestLayout() // view 布局参数改变时刷新
    }



    /**
     * 设置当前播放歌曲总时长
     */
    fun setSongDuration(duration: Int) {
        this.duration = duration
    }

    fun setLyricId(id: Long) {
        if (id != songId) {
            songId = id
            if (id != -1L) {
                LyricUtil.getLyric(id) {
                    lyricList.clear()
                    lyricList.addAll(it)
                }
            } else {

            }
        }
    }

}