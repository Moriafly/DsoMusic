package com.dirror.music.widget.lyric

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View

/**
 * 新版 LyricView，为了解决原 LyricView 超级多的 Bug
 * 1. 要稳定，不会闪退
 * 2. 实现多行歌词，没超出的情况
 * @author Moriafly
 * @since 2020.11.24
 */
class LyricView2 : View {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    /**
     * onDraw 绘制歌词
     */
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        // drawMultiLine(canvas)

    }

    /**
     * 外部方法
     */
    fun setLyric() {

    }

}