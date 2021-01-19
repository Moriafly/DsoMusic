package com.dirror.music.widget.lyric

import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.text.TextUtils

/**
 * 一行歌词实体
 * @since 2021年1月19日09:51:40 Moriafly 基于 LrcEntry 改造，转换为 kt ，移除部分过时方法
 */
internal class LyricEntry : Comparable<LyricEntry?> {

    companion object {
        const val GRAVITY_CENTER = 0
        const val GRAVITY_LEFT = 1
        const val GRAVITY_RIGHT = 2
    }

    constructor(time: Long, text: String) {
        this.time = time
        this.text = text
    }

    constructor(time: Long, text: String, secondText: String?) {
        this.time = time
        this.text = text
        this.secondText = secondText
    }

    // 时间
    val time: Long

    // 文本
    val text: String

    // 第二文本
    private var secondText: String? = null

    private val showText: String
        get() = if (!TextUtils.isEmpty(secondText)) {
            "$text\\n$secondText"
        } else {
            text
        }

    // staticLayout
    var staticLayout: StaticLayout? = null
        private set

    /**
     * 歌词距离视图顶部的距离
     */
    var offset = Float.MIN_VALUE

    /**
     * 高度
     */
    val height: Int
        get() = if (staticLayout == null) {
            0
        } else {
            staticLayout!!.height
        }

    /**
     * 初始化
     */
    fun init(paint: TextPaint, width: Int, gravity: Int) {
        val align: Layout.Alignment = when (gravity) {
            GRAVITY_LEFT -> Layout.Alignment.ALIGN_NORMAL
            GRAVITY_CENTER -> Layout.Alignment.ALIGN_CENTER
            GRAVITY_RIGHT -> Layout.Alignment.ALIGN_OPPOSITE
            else -> Layout.Alignment.ALIGN_CENTER
        }
        val staticLayoutBuilder = StaticLayout.Builder.obtain(showText, 0, showText.lastIndex + 1, paint, width)
            .setAlignment(align)
            .setLineSpacing(0f, 1f)
            .setIncludePad(false)
        staticLayout = staticLayoutBuilder.build()
        // staticLayout = StaticLayout(showText, paint, width, align, 1f, 0f, false)
        offset = Float.MIN_VALUE
    }

    /**
     * 设置第二文本
     */
    fun setSecondText(secondText: String?) {
        this.secondText = secondText
    }

    /**
     * 比较
     */
    override fun compareTo(other: LyricEntry?): Int {
        return if (other == null) {
            -1
        } else {
            (time - other.time).toInt()
        }
    }

}