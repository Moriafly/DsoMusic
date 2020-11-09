package com.dirror.music.music.standard

object SearchLyric {

    /**
     * 标准库
     * 获取歌词
     */
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