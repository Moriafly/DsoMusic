//package com.dirror.music.widget
//
//import android.content.Context
//import android.graphics.Canvas
//import android.graphics.Color
//import android.graphics.Paint
//import android.graphics.Paint.Align
//import android.text.Layout
//import android.text.StaticLayout
//import android.text.TextPaint
//import android.util.AttributeSet
//import android.view.View
//import com.dirror.music.data.LyricData
//import com.dirror.music.music.netease.LyricUtil
//import com.dirror.music.util.dp
//
//
///**
// * 新版 LyricView
// * @version 1.0.1
// * @since 2020/10/6
// */
//class LyricView2: View {
//    constructor(context: Context?) : super(context)
//    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
//    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
//
//
//    private val lyricDataList = ArrayList<LyricData>() // 歌词内容
//    private var autoScroll = true // 歌词能否滚动
//    private val paint = Paint() // 画笔
//    private val textPaint = TextPaint()
//
//    private val lyricHeight = height - 32.dp() // 歌词区域高度
//    private val lyricWidth = width - 32.dp() // 歌词区域宽度
//    private val lyricLeft = 16.dp()
//
//    private var focusIndex = 0 // 高亮行
//    private val lineSpacing = 0 // 歌词行间距
//    private val singleLineSpacing = 0 // 单行内间距
//
//    private var DC = 0
//
//    /**
//     * 初始化
//     */
//    init {
//        paint.isAntiAlias = true // 抗锯齿
//        paint.textAlign = Paint.Align.LEFT // 左对齐
//        paint.textSize = 16f
//
//        textPaint.isAntiAlias = true
//        paint.textSize = 16f
//    }
//
////    override fun onDraw(canvas: Canvas?) {
////        super.onDraw(canvas)
////        for ((index, value) in lyricDataList) {
////            val staticLayout: StaticLayout = StaticLayout.Builder.obtain(value, 0, value.length, textPaint, lyricWidth)
////                .setAlignment(android.text.Layout.Alignment.ALIGN_NORMAL)
////                .setLineSpacing(0.0f, 1.0f)
////                .setIncludePad(false)
////                .build()
////            canvas?.save()
////            canvas?.translate(lyricLeft.toFloat(), b / f);
////            staticLayout.draw(canvas)
////            canvas?.restore()
////        }
////    }
//
//    override fun onDraw(canvas: Canvas) {
//        // 判断是否有歌词
//            // YY 是一个 Int 类型数据，0 左对齐，1 居中，2 右对齐
//            when (this.YY) {
//                0 -> textPaint.setTextAlign(Align.LEFT)
//                1 -> textPaint.setTextAlign(Align.CENTER)
//                2 -> textPaint.setTextAlign(Align.RIGHT)
//                else -> {
//                }
//            }
////            if (this.Zn) {
////                g(canvas)
////            }
//            // 循环，DA 应该是行数
//            for (i in 0 until lyricDataList.size) {
//                // 行开始横坐标
//                val lineStartX: Float = lyricLeft.toFloat()
//                // 当前句子纵坐标
//                val lineY: Float = b(i, this.Zi)
//                if (ca(i) + lineY >= 0.0f) {
//                    if (lineY <= height.toFloat()) {
//                        var f: Float
//                        var build: StaticLayout
//                        if (i == this.DC) {
//                            f = if (this.Zn) 1.0f else this.Za
//                            this.mTextPaint.setColor(this.YX)
//                            this.ZA = lineY
//                        } else if (a(i, lineY) && this.Zn) {
//                            this.ZB = lineY
//                            this.Zr = i
//                            this.mTextPaint.setColor(this.YX)
//                            f = 1.0f
//                        } else {
//                            if (i == this.Zm) {
//                                f = this.Zb
//                            } else if (i == this.DC) {
//                                f = this.Za
//                            } else {
//                                textPaint.setTextSize(textSize as Float)
//                                f = 1.0f
//                            }
//                            if (this.Zl) {
//                                var alpha: Int = Color.alpha(this.mDefaultColor) - Math.min(
//                                    Math.abs(i - (this.DC - 1)),
//                                    if (this.Zn) Math.abs(i - (this.Zr - 1)) else Int.MAX_VALUE
//                                ) * 18
//                                if (alpha < 40) {
//                                    alpha = 40
//                                }
//                                textPaint.setColor(
//                                    Color.argb(
//                                        alpha,
//                                        Color.red(this.mDefaultColor),
//                                        Color.green(this.mDefaultColor),
//                                        Color.blue(this.mDefaultColor)
//                                    )
//                                )
//                            } else {
//                                textPaint.setColor(this.mDefaultColor)
//                            }
//                        }
//                        val lineInfo = lyricDataList[i]
//
//                            build = StaticLayout.Builder.obtain(
//                                lineInfo.content,
//                                0,
//                                lineInfo.content.length,
//                                textPaint,
//                                lyricWidth
//                            )
//                                .setAlignment(
//                                    Layout.Alignment.ALIGN_NORMAL
//                                ).setLineSpacing(0.0f, 1.0f).setIncludePad(false).build()
//
//                        canvas.save()
//                        canvas.scale(f, f)
//                        canvas.translate(lineStartX / f, lineY / f)
//                        build.draw(canvas)
//                        canvas.restore()
//                    } else {
//                        return
//                    }
//                }
//            }
//            return
//
//        // textPaint.setTextAlign(Align.CENTER)
//        // textPaint.setColor(this.mHintColor)
//        // canvas.drawText(this.DH, (width / 2).toFloat(), (height / 2).toFloat(), textPaint)
//    }
//
//    private fun b(i: Int, f: Float): Float {
//        val startY: Float = getStartY() + bY(i) - f + bZ(i)
//        if (i > this.DC) {
//            return startY + (this.DG.songLines.get(this.DC) as LineInfo).height * 0.20000005f / 2.0f
//        }
//        return if (i == this.DC) startY - (this.DG.songLines.get(this.DC) as LineInfo).height * 0.20000005f / 2.0f else startY
//    }
//
//    /**
//     * 设置歌词
//     */
//    fun setLyric(lyric: String?) {
//        // 清空歌词
//        lyricDataList.clear()
//        if (lyric == null) { // 无歌词
//            autoScroll = false
//            val lyricData = LyricData(0, "纯音乐，请欣赏")
//            lyricDataList.add(lyricData)
//        } else { // 有歌词
//            // 搜索是否有 '[' 存在
//            val char = lyric.find {
//                it == '['
//            }
//            if (char == null) { // 没有 [
//                // 无法滚动歌词
//                autoScroll = false
//                parseNotScrollLyric(lyric)
//            } else { // 有 [
//                // 可以滚动的歌词
//                autoScroll = true
//                parseCanScrollLyric(lyric)
//            }
//        }
//    }
//
//    /**
//     * 解析可以滚动歌词
//     */
//    private fun parseCanScrollLyric(lyric: String) {
//        val source = lyric.replace("这似乎是一首纯音乐呢，请尽情欣赏它吧！", "纯音乐，请欣赏")
//        lyricDataList.addAll(LyricUtil.parseLyric(source))
//    }
//
//    /**
//     * 解析无法滚动歌词
//     */
//    private fun parseNotScrollLyric(lyric: String) {
//
//    }
//
//    /**
//     * onDraw
//     */
//
//
//
//}