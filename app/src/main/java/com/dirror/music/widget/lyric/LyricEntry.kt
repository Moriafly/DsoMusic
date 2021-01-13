package com.dirror.music.widget.lyric

import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.text.TextUtils

/**
 * 一行歌词实体
 */
internal class LyricEntry : Comparable<LyricEntry?> {

    companion object {
        const val GRAVITY_CENTER = 0
        const val GRAVITY_LEFT = 1
        const val GRAVITY_RIGHT = 2
    }

    val time: Long
    val text: String
    private var secondText: String? = null
    var staticLayout: StaticLayout? = null
        private set

    /**
     * 歌词距离视图顶部的距离
     */
    var offset = Float.MIN_VALUE

    constructor(time: Long, text: String) {
        this.time = time
        this.text = text
    }

    constructor(time: Long, text: String, secondText: String?) {
        this.time = time
        this.text = text
        this.secondText = secondText
    }

    fun init(paint: TextPaint?, width: Int, gravity: Int) {
        val align: Layout.Alignment = when (gravity) {
            GRAVITY_LEFT -> Layout.Alignment.ALIGN_NORMAL
            GRAVITY_CENTER -> Layout.Alignment.ALIGN_CENTER
            GRAVITY_RIGHT -> Layout.Alignment.ALIGN_OPPOSITE
            else -> Layout.Alignment.ALIGN_CENTER
        }
        staticLayout = StaticLayout(showText, paint, width, align, 1f, 0f, false)
        offset = Float.MIN_VALUE
    }

    val height: Int
        get() = if (staticLayout == null) {
            0
        } else staticLayout!!.height

    fun setSecondText(secondText: String?) {
        this.secondText = secondText
    }

    private val showText: String
        private get() = if (!TextUtils.isEmpty(secondText)) {
            """
     $text
     $secondText
     """.trimIndent()
        } else {
            text
        }

    override fun compareTo(entry: LyricEntry?): Int {
        return if (entry == null) {
            -1
        } else (time - entry.time).toInt()
    }


}