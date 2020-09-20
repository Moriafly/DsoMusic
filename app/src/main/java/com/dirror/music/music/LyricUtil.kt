package com.dirror.music.music

import com.dirror.music.api.API_FCZBL_VIP
import com.dirror.music.data.LyricData
import com.dirror.music.util.MagicHttp
import kotlin.collections.ArrayList

object LyricUtil {
    fun getLyric(id: Long, success: (List<LyricData>) -> Unit) {
        MagicHttp.OkHttpManager().newGet("${API_FCZBL_VIP}/?type=lrc&id=${id}", {
            val source = it.replace("这似乎是一首纯音乐呢，请尽情欣赏它吧！","纯音乐，请欣赏")
            success.invoke(parseLyric(source))
        }, {

        })
    }

    /**
     * 解析 Lyric
     */
    private fun parseLyric(source: String): ArrayList<LyricData> {
        val lyricDataList =ArrayList<LyricData>()

        val singleLineList = source.split("\n")
        for (singleLine in singleLineList) {
            if (singleLine.isNotEmpty()) {
                if (singleLine.substring(singleLine.lastIndex) != "]" && singleLine.first() == '[') {
                    // 获取单行集合
                    val newSingleLine = singleLine.replace("[", "")
                    // 获取了非空 singleLine
                    val splitSingleLineList = newSingleLine.split("]")
                    val timeSource = splitSingleLineList[0]
                    val content = splitSingleLineList[1]
                    val time = parseTime(timeSource)

                    val lyricData = LyricData(time, content)
                    lyricDataList.add(lyricData)
                }
            }

        }

        return lyricDataList
    }

    private fun parseTime(get: String): Int {
        val time = get.substring(1)
        val list = time.split(":")
        var hour = 0
        var min = 0
        var sec = 0f
        if (list.size == 3) {
            // 小时
            hour = (list[0].toInt()) * 60 * 60 * 1000

            min = (list[1].toInt()) * 60 * 1000
            sec = (list[2].toFloat()) * 1000
        } else {
            // 没有小时
            min = (list[0].toInt()) * 60 * 1000
            sec = (list[1].toFloat()) * 1000
        }
        return  hour + min + sec.toInt()
    }
}