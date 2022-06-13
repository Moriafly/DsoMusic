package com.dirror.music.music.bilibili

import android.net.Uri
import com.dirror.music.music.standard.data.SOURCE_BILIBILI
import com.dirror.music.music.standard.data.StandardSongData
import com.dirror.music.util.MagicHttp
import com.dirror.music.util.getStr
import com.dirror.music.util.toast
import org.json.JSONArray
import org.json.JSONObject

object SearchSong {
    const val referer = "https://www.bilibili.com"
    fun search(keywords: String, success: (ArrayList<StandardSongData>) -> Unit) {
        val kw = Uri.encode(keywords)
        val url =
            "https://api.bilibili.com/x/web-interface/search/all/v2?__refresh__=true&_extra=&context=&page=1&page_size=42&order=&duration=&from_source=&from_spmid=333.337&platform=pc&highlight=1&single_column=0&keyword=$kw&preload=true&com2co=true"
        MagicHttp.OkHttpManager().getWithHeader(url, mapOf(
            "Referer" to referer,
            "csrf" to "EUOH79P2LLK",
            "User-Agent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/101.0.0.0 Safari/537.36"
        ), {
            try {
                val resp = JSONObject(it)
                val resultList = resp
                    .getJSONObject("data")
                    .getJSONArray("result")
                val standardSongDataList = ArrayList<StandardSongData>()
                (0 until resultList.length()).forEach {
                    val obj = resultList.getJSONObject(it)
                    if ("video" == obj.getString("result_type")) {
                        val videoList = obj.getJSONArray("data")
                        transform(videoList, standardSongDataList)
                    }
                }
                success.invoke(standardSongDataList)
            } catch (e: Exception) {
                e.printStackTrace()
                toast("网络异常,或者解析错误")
            }
        }, {

        })
        val standardSongDataList = ArrayList<StandardSongData>()
        success(standardSongDataList)
    }

    fun transform(videoList: JSONArray, standardSongDataList: ArrayList<StandardSongData>) {
        (0 until videoList.length()).forEach {
            val item = videoList.getJSONObject(it)
            handlerVideoItem(item)
            val artists = ArrayList<StandardSongData.StandardArtistData>()
            artists.add(StandardSongData.StandardArtistData(0, item.getStr("author")))
            standardSongDataList.add(
                StandardSongData(
                    SOURCE_BILIBILI,
                    item.getStr("aid"),
                    item.getStr("title"),
                    "https:" + item.getStr("pic"),
                    artists,
                    null,
                    null,
                    null
                )
            )
        }
    }

    private fun handlerVideoItem(video: JSONObject) {
        var title: String = video.getStr("title")
        title = title
            .replace("</\\w+>".toRegex(), "")
            .replace("<\\w+( \\w+=\"\\w+\")*>".toRegex(), "")
        video.put("title", title)
    }
}