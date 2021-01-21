package com.dirror.music.widget.lyric

import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint

/**
 * 一行歌词实体
 * @since 2021年1月19日09:51:40 Moriafly 基于 LrcEntry 改造，转换为 kt ，移除部分过时方法
 * @param time 时间
 * @param text 文本
 */
internal class LyricEntry(val time: Long, val text: String) : Comparable<LyricEntry> {

    companion object {
        const val GRAVITY_CENTER = 0
        const val GRAVITY_LEFT = 1
        const val GRAVITY_RIGHT = 2
    }

    /**
     * 第二文本
     */
    var secondText: String? = null

    /**
     * 显示的文本
     */
    private val showText: String
        get() = if (secondText.isNullOrEmpty()) {
            text
        } else {
            "$text\n$secondText"
        }

    /**
     * staticLayout
     */
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
        get() = staticLayout?.height ?: 0

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
        val staticLayoutBuilder = StaticLayout.Builder
            .obtain(showText, 0, showText.length, paint, width)
            .setAlignment(align)
            .setLineSpacing(0f, 1f)
            .setIncludePad(false)
        staticLayout = staticLayoutBuilder.build()
        offset = Float.MIN_VALUE
    }

    /**
     * 比较
     */
    override fun compareTo(other: LyricEntry): Int {
        return (time - other.time).toInt()
    }

}