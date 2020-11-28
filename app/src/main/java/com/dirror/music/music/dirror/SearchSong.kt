package com.dirror.music.music.dirror

import org.jetbrains.annotations.TestOnly

object SearchSong {

    @TestOnly
    fun getDirrorSongUrl(songmid: String): String {
        return when (songmid) {
            "812400", "001BaZ263wqkmP" -> "https://link.gimhoy.com/1drv/aHR0cHM6Ly8xZHJ2Lm1zL3UvcyFBajFUUjdlaXVuaWRoZ1FDYVZIbHNPTkZHbnVMP2U9Z0VVZzhU.mp3" // ラムジ - PLANET
            "1487943004", "0001r55f1SLVWd" -> "https://link.gimhoy.com/1drv/aHR0cHM6Ly8xZHJ2Lm1zL3UvcyFBajFUUjdlaXVuaWRoWDNnNHQ4czVHTDJKTVluP2U9WU5EMUJm.mp3" // 幸存者 - 林俊杰
            "002eqWNw0eVgZg" -> "https://link.gimhoy.com/1drv/aHR0cHM6Ly8xZHJ2Lm1zL3UvcyFBajFUUjdlaXVuaWRoWDRhajdsRHkxcHJEd21aP2U9QkJ2bjhq.mp3" // TORTOISE KNIGHT - 岩崎太整 / 二宮愛
            "0039MnYb0qxYhV" -> "https://link.gimhoy.com/1drv/aHR0cHM6Ly8xZHJ2Lm1zL3UvcyFBajFUUjdlaXVuaWRoWDg2aHV1S2JORDVYcFFZP2U9OVpYWVhn.mp3" // 晴天 - 周杰伦
            "003aAYrm3GE0Ac" -> "https://link.gimhoy.com/1drv/aHR0cHM6Ly8xZHJ2Lm1zL3UvcyFBajFUUjdlaXVuaWRoZ0plWXc3YTFka0hybVppP2U9Z05TbGk0.mp3" // 稻香 - 周杰伦
            else -> ""
        }
    }

}