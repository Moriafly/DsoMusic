package com.dirror.music.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.dirror.music.data.LyricData
import com.dirror.music.music.netease.LyricUtil

/**
 * 新版 LyricView
 * @version 1.0.0
 * @since 2020/10/6
 */
class LyricView2: View {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)


    private val content = ArrayList<LyricData>() // 歌词内容
    private var tip = "" // 当没歌词时候的提示文字

    /**
     * 设置歌词
     */
    fun setLyric(lyric: String?) {
        if (lyric == null) { // 无歌词
            tip = "纯音乐，请欣赏"
        } else { // 有歌词
            tip = ""
            // 搜索是否有 '[' 存在
            val char = lyric.find {
                it == '['
            }
            if (char == null) { // 没有 [
                // 无法滚动歌词

            } else {

            }
        }

//        if (char != null) {
//            // 可滚动歌词
//            val source = lyric.replace("这似乎是一首纯音乐呢，请尽情欣赏它吧！","纯音乐，请欣赏")
//            LyricUtil.parseLyric(source)
//        } else {

// }
    }


    /**
     * 判断歌词类型
     */


}