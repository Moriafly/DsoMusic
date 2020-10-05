package com.dirror.music.music.netease

fun main() {
    val source = """
        [by:天草筱]
        [ti:0]
        [ar:0]
        [al:0]
        [by:0]
        [offset:0]
        [00:00.000] 作曲 : 只有影子
        [00:01.000] 作词 : 没有羊先生
        [00:06.28]双抛桥
        [00:08.54]
        [00:09.80]作词：没有羊先生
        [00:10.91]作曲：只有影子
        [00:12.33]编曲：潇儿
        [00:13.80]演唱：只有影子 还有小烟
        [00:16.33]
        [00:18.80]“相传昔有王氏子与陶氏女相好，
        [00:23.67]父母夺其志，月夜同投此桥下，故名。”
        [00:25.66]——清·《闽杂记》
        [00:30.23]
        [00:31.03]若随风 往事不随风
        [00:34.98]它偶然飘落入梦中
        [00:38.59]前尘多少故事曾翻涌
        [00:45.37]那夜霜重露正浓
        [00:49.64]
        [00:51.38]若生离难与他相逢
        [00:55.91]惟死别才能够相认
        [00:59.11]流水落花比起无情人
        [01:05.77]也许更眷顾我们
        [01:10.31]
        [01:10.71]桥上谁家玉笛暗飞声
        [01:17.29]那年他和她手提一盏灯
        [01:24.52]如果殉情这一词听起来太残忍
        [01:32.11]他们只是造访一座城
        [01:37.68]
        [01:38.09]桥下住着一双有心人
        [01:44.71]桥上来去匆匆的冬与春
        [01:52.21]岸边垂柳都长出漫长的年轮
        [01:59.35]如果路过陌路的情人
        [02:07.17]他们会不会心疼
        [02:14.94]
        [02:32.55]红酥手，黄藤酒
        [02:39.53]城春宫墙柳
        [02:46.83]东风恶，欢情薄
        [02:53.34]一怀愁绪与离索
        [02:57.85]
        [03:01.28]若生离难与他相逢
        [03:05.78]惟死别才能够相认
        [03:08.97]流水落花比起无情人
        [03:15.81]也许更眷顾我们
        [03:19.80]
        [03:21.20]桥下住着一双有心人
        [03:27.43]桥上的年华似水了无痕
        [03:34.76]河畔芦苇都忘了那一年的风
        [03:42.40]它们只道新人换旧人
        [03:46.30]
        [03:48.63]今夜谁家玉笛暗飞声
        [03:54.84]那年他和他手提一盏灯
        [04:02.15]如果殉情这一词听起来太残忍
        [04:09.84]他们只是造访一座城
        [04:17.61]城里只有两个人
        [04:22.63]
        [04:27.35]【攻徳无量扫文组整理】
        [04:31.87]
    """.trimIndent()
    ParseLyric.parse(source) {

    }
}

fun test(lyric: ParseLyric.Lyric) {

}

/**
 * 解析歌词类
 */

object ParseLyric {
    /**
     * 歌词数据类
     */
    data class Lyric(
        val artists: String, // 艺术家，艺人名，歌手名
        val title: String, // 曲名
        val album: String, // 专辑名
        val by: String, // 编辑歌词文件的人
        val offset: Int, // 时间补偿值
        val content: ArrayList<LyricContent>,
        val other: String // 其他部分，不能滚动的文字
    )

    /**
     * 单行歌词数据
     */
    data class LyricContent(
            val time: Int, // 时间
            val text: String // 文本内容
    )

    /**
     * 解析歌词，将源数据 [source] 转成 [Lyric] 类型
     */
    fun parse(source: String, success: (Lyric) -> Unit) {
        // 定义 lyric
        Thread {
            var artists = ""
            val title: String // 曲名
            val album: String // 专辑名
            val by: String // 编辑歌词文件的人
            val offset: Int // 时间补偿值
            val content = ArrayList<LyricContent>() // 内容
            val other: String // 其他部分，不能滚动的文字

            // 解析 source
            // 换行
            val lines = source.split("\n")

            for (line in lines) {
                // ar
                if (line.startsWith("[ar")) {
                    artists = ""
                }
                println(line)
            }

            success.invoke(
                Lyric(
                artists = "",
                title = "",
                by = "",
                album = "",
                offset = 0,
                content = content,
                other = ""
            )
            )
        }



    }
}