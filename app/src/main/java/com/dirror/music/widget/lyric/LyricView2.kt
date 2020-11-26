package com.dirror.music.widget.lyric

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import java.time.OffsetTime

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
     * 主线程安全，不能阻塞
     * SurfaceView 副线程绘图
     */
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        // drawMultiLine(canvas)

    }

    // 封装，尺寸 measure 属性 attributes

    /**
     * 外部方法
     */

    /**
     * 第一步
     * 传入初始化歌词
     */
    fun initLyric() {

    }

    /**
     * 第二步
     * 传入当前毫秒 [ms] 时间
     */
    fun setDuration(ms: Int) {

        // 绘制
        invalidate()
    }

    /**
     * 歌词数据
     */
    data class LyricData(
        val ar: String?, // 艺人名
        val ti: String?, // 歌曲名
        val al: String?, // 专辑
        val by: String?, // 编者，编辑上传 lrc 歌词文件的人
        val offset: Int?, // 时间补偿值，单位是毫秒

    )

}