package com.dirror.music.music.standard

import com.dirror.music.api.API_FCZBL_VIP
import com.dirror.music.data.LyricData
import com.dirror.music.util.MagicHttp
import com.dirror.music.util.loge
import com.google.gson.Gson
import org.jetbrains.annotations.TestOnly

object SearchLyric {

    /**
     * 标准库获取歌词
     * 传入 [songData] ，返回地址
     */
    fun getLyric(songData: StandardSongData, success: (List<LyricData>) -> Unit) {
        var url = ""
        when (songData.source) {
            // 网易云
            SOURCE_NETEASE -> {
                url = "$API_FCZBL_VIP/?type=lrc&id=${songData.id}"
            }
            // QQ
            SOURCE_QQ -> {
                url = "https://c.y.qq.com/lyric/fcgi-bin/fcg_query_lyric_new.fcg?songmid=${songData.id}&format=json&nobase64=1"
            }
        }
        MagicHttp.OkHttpManager().newGet(url, { response ->
            var lyric = response
            if (songData.source == SOURCE_QQ) {
                lyric = Gson().fromJson(lyric, QQSongLyric::class.java).lyric
            }


            loge("歌词：${response}")
            val char = lyric.find {
                it == '['
            }
            if (char != null) {
                val source = lyric.replace("这似乎是一首纯音乐呢，请尽情欣赏它吧！","纯音乐，请欣赏")
                success.invoke(parseLyric(source))
            } else {
                success.invoke(listOf(LyricData(0, "暂无歌词")))
            }
        }, {

        })
    }

    data class QQSongLyric(
        val lyric: String
    )

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

    /**
     * 解析时间
     */
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