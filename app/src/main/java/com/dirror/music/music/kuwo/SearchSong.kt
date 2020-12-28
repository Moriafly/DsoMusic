package com.dirror.music.music.kuwo

import com.dirror.music.util.MagicHttp

/**
 * 搜索酷我音乐
 */
object SearchSong {

    // http://search.kuwo.cn/r.s?songname=%E6%90%81%E6%B5%85&ft=music&rformat=json&encoding=utf8&rn=8&callback=song&vipver=MUSIC_8.0.3.1
    fun search(keywords: String) {

        val url = "http://search.kuwo.cn/r.s?songname=${keywords}&ft=music&rformat=json&encoding=utf8&rn=8&callback=song&vipver=MUSIC_8.0.3.1"
        MagicHttp.OkHttpManager().newGet(url, {
            var string = it
            // 适配 JSON


            string = string.replace("\'", "\"")

        }, {

        })
    }

    // http://www.kuwo.cn/url?format=mp3&rid=94239&response=url&type=convert_url3&br=128kmp3&from=web&t=1609079909636&httpsStatus=1
}