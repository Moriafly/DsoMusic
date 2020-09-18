package com.dirror.music.util

import com.dirror.music.api.API_FCZBL_VIP
import com.dirror.music.data.LyricData
import java.util.*
import kotlin.collections.ArrayList

object LyricUtil {
    fun getLyric(id: Long, success: (List<LyricData>) -> Unit) {
        MagicHttp.OkHttpManager().newGet("${API_FCZBL_VIP}/?type=lrc&id=${id}", {
            val list = ArrayList<LyricData>()

            var string = it.replace("]\n", "]")
            string = string.replace("by", "00:00.00")
            string = string.replace("ar", "00:00.00")
            string = string.replace("al", "00:00.00")
            string = string.replace("ti", "00:00.00")
            string = string.replace("offset", "00:00.00")

            val lineList = string.split('\n')
            for (line in lineList) {
                val lyricDataList: List<LyricData> = parseLine(line)
                list.addAll(lyricDataList)
            }

            success.invoke(list)
        }, {

        })
    }
    // 解析一行歌词
    fun parseLine(line: String): List<LyricData> {
        val list = ArrayList<LyricData>()

        val arr = line.split("]")
        val content = arr[arr.lastIndex]
        for (index in 0 until arr.lastIndex) {
            val startTime = parseTime(arr.get(index))
            list.add(LyricData(startTime, content))
        }
        list.sortBy { it.startTime }

        return list
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