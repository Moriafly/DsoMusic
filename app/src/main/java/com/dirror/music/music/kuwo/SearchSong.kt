package com.dirror.music.music.kuwo

import android.net.Uri
import com.dirror.music.music.standard.data.SOURCE_KUWO
import com.dirror.music.music.standard.data.StandardSongData
import com.dirror.music.util.*
import com.google.gson.Gson
import org.json.JSONObject
import java.lang.Exception

/**
 * 搜索酷我音乐
 */
object SearchSong {

    // http://search.kuwo.cn/r.s?songname=%E6%90%81%E6%B5%85&ft=music&rformat=json&encoding=utf8&rn=8&callback=song&vipver=MUSIC_8.0.3.1
    // http://kuwo.cn/api/www/search/searchMusicBykeyWord?key=%E6%90%81%E6%B5%85&pn=1&rn=30&httpsStatus=1&reqId=24020ad0-3ab4-11eb-8b50-cf8a98bef531
    fun search(keywords: String, success: (ArrayList<StandardSongData>) -> Unit) {
        val url =
            "http://kuwo.cn/api/www/search/searchMusicBykeyWord?key=$keywords&pn=1&rn=50&httpsStatus=1&reqId=24020ad0-3ab4-11eb-8b50-cf8a98bef531"
        MagicHttp.OkHttpManager().getWithHeader(url, mapOf(
            "Referer" to Uri.encode("http://kuwo.cn/search/list?key=$keywords"),
            "Cookie" to "kw_token=EUOH79P2LLK",
            "csrf" to "EUOH79P2LLK",
            "User-Agent" to "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1"
        ), {
            try {
                val resp = JSONObject(it)
                val songList = resp
                    .getJSONObject("data")
                    .getJSONArray("list")

                val standardSongDataList = ArrayList<StandardSongData>()
                // 每首歌适配
                (0 until songList.length()).forEach {
                    val songInfo = songList[it] as JSONObject
                    standardSongDataList.add(
                        KuwoSearchData.SongData(
                            songInfo.getIntOrNull("rid").toString(),
                            songInfo.getStr("name", ""),
                            songInfo.getStr("artist", ""),
                            songInfo.getStr("pic", "")
                        ).switchToStandard()
                    )
                }
                success.invoke(standardSongDataList)
            } catch (e: Exception) {
                e.printStackTrace()
                toast("网络异常,或者解析错误")
            }
        }, {

        })
    }


    // http://search.kuwo.cn/r.s?songname=%E6%90%81%E6%B5%85&ft=music&rformat=json&encoding=utf8&rn=8&callback=song&vipver=MUSIC_8.0.3.1
    fun search2(keywords: String, success: (ArrayList<StandardSongData>) -> Unit) {

        val url =
            "http://search.kuwo.cn/r.s?songname=${keywords}&ft=music&rformat=json&encoding=utf8&rn=30&callback=song&vipver=MUSIC_8.0.3.1"
        MagicHttp.OkHttpManager().newGet(url, {
            var string = it
            // 适配 JSON

            string = string.replace("try{var jsondata=", "")
            string = string.replace(
                "\n" +
                        "; song(jsondata);}catch(e){jsonError(e)}", ""
            )
            string = string.replace("\'", "\"")
            string = string.replace("&nbsp;", " ")

            loge(string)
            try {
                val kuwoSearchData = Gson().fromJson(string, KuwoSearchData::class.java)
                val songList = kuwoSearchData.abslist
                val standardSongDataList = ArrayList<StandardSongData>()
                // 每首歌适配
                songList.forEach { kuwoSong ->
                    standardSongDataList.add(kuwoSong.switchToStandard())
                }
                success.invoke(standardSongDataList)
            } catch (e: Exception) {

            }
        }, {

        })
    }

    /**
     * pn 页码数，rn 此页歌曲数
     */
    fun newSearch(keywords: String, success: (ArrayList<StandardSongData>) -> Unit) {
        val url =
            "http://search.kuwo.cn/r.s?all=${keywords}&ft=music&%20itemset=web_2013&client=kt&pn=0&rn=30&rformat=json&encoding=utf8"
        MagicHttp.OkHttpManager().newGet(url, {
            try {
                val kuwoSearchData = Gson().fromJson(it, KuwoSearchData::class.java)
                val songList = kuwoSearchData.abslist
                val standardSongDataList = ArrayList<StandardSongData>()
                // 每首歌适配
                songList.forEach { kuwoSong ->
                    standardSongDataList.add(kuwoSong.switchToStandard())
                }
                success.invoke(standardSongDataList)
            } catch (e: Exception) {
            }
        }, { })

    }

    /**
     * 获取链接
     * 音质
     * 128 / 192 / 320
     */
    fun getUrl(rid: String, success: (String) -> Unit) {

        val id = rid.replace("MUSIC_", "")
        val url =
            "http://www.kuwo.cn/url?format=mp3&rid=${id}&response=url&type=convert_url3&br=990kmp3&from=web&t=${getCurrentTime()}&httpsStatus=1"
        loge("链接: $url")
        MagicHttp.OkHttpManager().newGet(url, {
            try {
                val kuwoUrlData = Gson().fromJson(it, KuwoUrlData::class.java)
                success.invoke(kuwoUrlData.url)
            } catch (e: Exception) {
                toast("获取链接失败")
            }
        }, {

        })
    }

    // http://www.kuwo.cn/url?format=mp3&rid=94239&response=url&type=convert_url3&br=128kmp3&from=web&t=1609079909636&httpsStatus=1

    data class KuwoSearchData(
        val abslist: ArrayList<SongData>
    ) {
        data class SongData(
            val MUSICRID: String,
            val NAME: String,
            val ARTIST: String,
            val hts_MVPIC: String // 图片
        ) {
            fun switchToStandard(): StandardSongData {
                return StandardSongData(
                    SOURCE_KUWO,
                    MUSICRID,
                    NAME,
                    hts_MVPIC,
                    genArtistList(),
                    null,
                    null,
                    null
                )
            }

            private fun genArtistList(): ArrayList<StandardSongData.StandardArtistData> {
                val artistList = ArrayList<StandardSongData.StandardArtistData>()
                artistList.add(StandardSongData.StandardArtistData(0, ARTIST))
                return artistList
            }
        }
    }

    data class KuwoUrlData(
        val url: String
    )

}