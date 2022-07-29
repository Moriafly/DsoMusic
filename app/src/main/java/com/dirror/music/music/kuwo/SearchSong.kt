package com.dirror.music.music.kuwo

import android.net.Uri
import com.dirror.music.App
import com.dirror.music.data.SearchType
import com.dirror.music.music.standard.data.*
import com.dirror.music.plugin.PluginConstants
import com.dirror.music.plugin.PluginSupport
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
    fun search(keywords: String,searchType: SearchType, success: (StandardSearchResult) -> Unit) {//不是歌单其余都是搜索单曲a
        val url =
            "http://kuwo.cn/api/www/search/${if (searchType == SearchType.PLAYLIST) "searchPlayListBykeyWord" else "searchMusicBykeyWord"}?key=$keywords&pn=1&rn=50&httpsStatus=1&reqId=24020ad0-3ab4-11eb-8b50-cf8a98bef531"
        MagicHttp.OkHttpManager().getWithHeader(url, mapOf(
            "Referer" to Uri.encode("http://kuwo.cn/search/list?key=$keywords"),
            "Cookie" to "kw_token=EUOH79P2LLK",
            "csrf" to "EUOH79P2LLK",
            "User-Agent" to "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1"
        ), {
            try {
                val resp = JSONObject(it)
                val dataList = resp
                    .getJSONObject("data")
                    .getJSONArray("list")

                //歌曲
                val standardSongDataList = ArrayList<StandardSongData>()
                //歌单
                val standardPlaylist = ArrayList<StandardPlaylist>()
                // 每首歌适配
                (0 until dataList.length()).forEach {
                    val item = dataList[it] as JSONObject
                    if (searchType == SearchType.SINGLE){//标准类型歌曲集合
                        standardSongDataList.add(
                            KuwoSearchData.SongData(
                                item.getIntOrNull("rid").toString(),
                                item.getStr("name", ""),
                                item.getStr("artist", ""),
                                item.getStr("pic", "")
                            ).switchToStandard()
                        )
                    }else{//标准类型歌单集合
                        standardPlaylist.add(
                            StandardPlaylist(
                                item.getLong("id"),
                                item.getStr("name", ""),
                                item.getStr("img", ""),
                                "",
                                item.getStr("uname", ""),
                                item.getIntOrNull("total"),
                                item.getLong("listencnt")
                            )
                        )
                    }
                }
                success.invoke(StandardSearchResult(standardSongDataList,standardPlaylist, emptyList(), emptyList()))
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
    suspend fun getUrl(rid: String): String {
        PluginSupport.setRid(rid)
        val pluginUrl = PluginSupport.apply(PluginConstants.POINT_KUWO_URL)
        if (pluginUrl != null && pluginUrl is String) {
            return pluginUrl
        }
        val id = rid.replace("MUSIC_", "")
        val url =
            "http://antiserver.kuwo.cn/anti.s?format=mp3&rid=${id}&response=url&type=convert_url3&br=320kmp3"
        loge("链接: $url")
        HttpUtils.get(url, KuwoUrlData::class.java)?.let {
            return it.url ?: ""
        }
        toast("获取链接失败")
        return ""
    }

    /**
     * 获取歌单详情
     */
    suspend fun getPlaylist(id: Long): PlaylistWrapData {
        val url =
            "https://nplserver.kuwo.cn/pl.svc?op=getlistinfo&pn=0&rn=30&encode=utf-8&keyset=pl2012&pcmp4=1&pid=${id}&vipver=MUSIC_9.0.2.0_W1&newver=1"
        val resJson = MagicHttp.OkHttpManager().get(url)

        val resp = JSONObject(resJson)

        val musicList = resp.getJSONArray("musiclist")

        val standardSongDataList = ArrayList<StandardSongData>()
        // 每首歌适配
        (0 until musicList.length()).forEach {
            val item = musicList[it] as JSONObject
            standardSongDataList.add(
                KuwoSearchData.SongData(
                    item.getIntOrNull("id").toString(),
                    item.getStr("name", ""),
                    item.getStr("artist", ""),
                    item.getStr("pic", resp.getString("pic"))//歌单的歌曲条目貌似没有返回歌曲图片,用歌单图替代
                ).switchToStandard()
            )
        }
        return PlaylistWrapData(standardSongDataList,resp.getString("pic"),resp.getString("title"),resp.getString("info"))
    }

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
        val url: String?
    )

    /**
     * 包裹着歌单信息和已转换的通用歌曲类型集合
     */
    data class PlaylistWrapData(
        val songList: ArrayList<StandardSongData>,
        val playlistUrl :String?,
        val playlistTitle :String?,
        val playlistDescription :String?,
    )
}