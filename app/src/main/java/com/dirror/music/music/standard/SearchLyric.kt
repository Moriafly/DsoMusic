package com.dirror.music.music.standard

import org.jetbrains.annotations.TestOnly

object SearchLyric {

    /**
     * 标准库获取歌词
     * 传入 [songData] ，返回地址
     */
    @TestOnly
    fun getLyricUrl(songData: StandardSongData): String {
        when (songData.source) {
            // 网易云
            SOURCE_NETEASE -> {
                return ""
            }
            // QQ
            SOURCE_QQ -> {
                return  ""
            }
        }
        return ""
    }

}