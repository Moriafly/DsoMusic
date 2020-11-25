package com.dirror.music.music.standard

import com.dirror.music.data.LyricData
import com.dirror.music.util.MagicHttp
import com.google.gson.Gson

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
                url = "http://music.eleuu.com/lyric?id=${songData.id}"
                //url = "$API_FCZBL_VIP/?type=lrc&id=${songData.id}"
            }
            // QQ
            SOURCE_QQ -> {
                url = "https://c.y.qq.com/lyric/fcgi-bin/fcg_query_lyric_new.fcg?songmid=${songData.id}&format=json&nobase64=1"
            }
        }

        MagicHttp.OkHttpManager().newGet(url, { response ->
            var lyric = response
            println("歌词：$lyric")

            when (songData.source) {
                SOURCE_QQ -> {
                    if (Gson().fromJson(lyric, QQSongLyric::class.java).lyric != null) {
                        lyric = Gson().fromJson(lyric, QQSongLyric::class.java).lyric.toString()
                    } else {
                        success.invoke(listOf(LyricData(0, "暂无歌词")))
                    }
                }
                SOURCE_NETEASE -> {
                    if (Gson().fromJson(lyric, NeteaseSongLyric::class.java).lrc != null) {
                        lyric = Gson().fromJson(lyric, NeteaseSongLyric::class.java).lrc?.lyric.toString()
                    } else {
                        success.invoke(listOf(LyricData(0, "暂无歌词")))
                    }
                }
            }


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
        val lyric: String?
    )

    data class NeteaseSongLyric(
        val lrc: NeteaseSongLrc?
    )

    data class NeteaseSongLrc(
        val lyric: String?
    )


    /**
     * 解析 Lyric
     */
    fun parseLyric(source: String): ArrayList<LyricData> {
        val lyricDataList =ArrayList<LyricData>()
        val parseSource = source.replace("\r", "")
        val singleLineList = parseSource.split("\n")
        for (singleLine in singleLineList) {
            if (singleLine.isNotEmpty()) {
                if (singleLine.substring(singleLine.lastIndex) != "]" && singleLine.first() == '[') {
                    // 获取单行集合
                    val newSingleLine = singleLine.replace("[", "")
                    // 获取了非空 singleLine
                    val splitSingleLineList = newSingleLine.split("]")
                    val timeSource = splitSingleLineList[0]
                    val content = splitSingleLineList[1]

                    if (content != "\n") {
                        println(content)
                        val time = parseTime(timeSource)
                        val lyricData = LyricData(time, content)
                        lyricDataList.add(lyricData)
                    }
                }
            }

        }

        return lyricDataList
    }

    /**
     * 解析时间
     */
    private fun parseTime(get: String): Int {
        println(get)
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

//fun main() {
//    val a = "[ti:寒山客]\r\n[ar:西瓜JUN]\r\n[al:寒山客]\r\n[by:]\r\n[offset:0]\r\n[00:00.00]寒山客 - 西瓜JUN\r\n[00:03.59]词：南岐\r\n[00:05.64]曲：西瓜JUN\r\n[00:08.20]编曲：西瓜JUN\r\n[00:11.28]制作人：西瓜JUN\r\n[00:14.87]和声：西瓜JUN\r\n[00:17.95]和声设计：西瓜JUN\r\n[00:22.06]二胡：浅浅\r\n[00:24.62]混缩：三鹿\r\n[00:27.19]曲绘：花\r\n[00:29.24]视频：栗原\r\n[00:31.80]感谢：提琴boy\r\n[00:34.88]未经许可 不得翻唱或使用\r\n[00:41.04]等不到山色新萤织就烟火\r\n[00:47.32]旧岁清荷早已从湖心跌落\r\n[00:53.57]熏风迟来一日歇在林间丘壑\r\n[00:59.98]不知此生要远赴哪处山河\r\n[01:06.50]再回看云烟深处的岁月轮廓\r\n[01:12.84]是眼底化不开的天地苍茫雾色\r\n[01:19.28]山色里枕过烟波也相对而坐\r\n[01:26.02]那时一同看风月共婆娑\r\n[02:01.14]于膝上抚琴也摘花数萤火\r\n[02:07.14]等过四时交替和温柔月色\r\n[02:13.55]不过是平凡人间的悲欢喜乐\r\n[02:19.92]却在旧梦翻涌时不忍离舍\r\n[02:26.42]于是在红尘中捧出一缕心火\r\n[02:32.63]要栽种这一世相逢铸就的因果\r\n[02:39.15]纵光阴眷恋好梦曾偏爱你我\r\n[02:45.96]也有浩荡青冥遥遥相隔\r\n[02:53.41]是泪眼温热淌向最漫长星河\r\n[02:59.98]啊\r\n[03:06.43]百年一梦又何处解离合\r\n[03:11.12]愿向人间 不做蓬莱客\r\n[03:17.64]只身在红尘中捧出一缕心火\r\n[03:23.92]好成全这一世相逢种下的因果\r\n[03:30.42]此生被岁月赋予的所有曲折\r\n[03:37.30]只为了来世相遇那一刻\r\n[03:43.81]只为了来世相遇那一刻"
//    // println(a)
//    SearchLyric.parseLyric(a)
////    for (i in list) {
////        println(i.content)
////    }
//}