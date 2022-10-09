package com.dirror.music.music.bilibili

import android.util.Log
import com.dirror.music.music.standard.data.SOURCE_BILIBILI
import com.dirror.music.music.standard.data.StandardSongData
import com.dirror.music.util.getStr
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

object SearchSong {
    const val indexUrl = "https://www.bilibili.com/"
    const val url = "https://api.bilibili.com/x/web-interface/search/type"
    const val referer = "https://www.bilibili.com"
    const val TAG = "Bilibili";

    private var defaultHeaders: Headers? = null
    private var client: OkHttpClient? = null

    private var inited = false

    fun defaultHeaders() {
        val headers: MutableMap<String, String> = HashMap()
        headers["Accept"] =
            "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9"
        headers["Accept-Encoding"] = "gzip, deflate, br"
        headers["Accept-Language"] = "zh-CN,zh;q=0.9"
        headers["Cache-Control"] = "no-cache"
        headers["Connection"] = "keep-alive"
        headers["Pragma"] = "no-cache"
        headers["sec-ch-ua"] =
            "\" Not A;Brand\";v=\"99\", \"Chromium\";v=\"101\", \"Google Chrome\";v=\"101\""
        headers["sec-ch-ua-mobile"] = "?0"
        headers["sec-ch-ua-platform"] = "\"Windows\""
        headers["Sec-Fetch-Dest"] = "document"
        headers["Sec-Fetch-Mode"] = "navigate"
        headers["Sec-Fetch-Site"] = "none"
        headers["Sec-Fetch-User"] = "?1"
        headers["Upgrade-Insecure-Requests"] = "1"
        defaultHeaders = Headers.of(headers)
    }

    fun init(keywords: String, success: (ArrayList<StandardSongData>) -> Unit) {
        if (inited) {
            doSearch(keywords, success)
            return
        }
        inited = true

        defaultHeaders()
        if (defaultHeaders == null) {
            return
        }
        val client = OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(3, TimeUnit.SECONDS)
            .writeTimeout(3, TimeUnit.SECONDS)
            .addInterceptor(GzipInterceptor)
            .cookieJar(MyCookieStore)
            .build()
        this.client = client;
        val request = Request.Builder()
            .url(indexUrl)
            .headers(defaultHeaders)
            .get()
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "failure", e)
            }

            override fun onResponse(call: Call, response: Response) {
                doSearch(keywords, success)
            }
        })
    }

    fun search(keywords: String, success: (ArrayList<StandardSongData>) -> Unit) {
        init(keywords, success)
    }

    private fun doSearch(keywords: String, success: (ArrayList<StandardSongData>) -> Unit) {

        val httpUrl = HttpUrl.parse(url) ?: return
        if (client == null) {
            return
        }

        val urlBuilder = httpUrl.newBuilder()
            .addQueryParameter("context", "")
            .addQueryParameter("search_type", "video")
            .addQueryParameter("page", "1")
            .addQueryParameter("order", "")
            .addQueryParameter("keyword", keywords)
            .addQueryParameter("duration", "")
            .addQueryParameter("category_id", "")
            .addQueryParameter("tids_1", "")
            .addQueryParameter("tids_2", "")
            .addQueryParameter("__refresh__", "true")
            .addQueryParameter("_extra", "")
            .addQueryParameter("highlight", "1")
            .addQueryParameter("single_column", "0")

        val request = Request.Builder()
            .url(urlBuilder.build())
            .header("accept", "application/json, text/plain, */*")
            .get()
            .build()
        client!!.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                Log.e(TAG, "onResponse: getWithHeader()")
                val string = response.body()?.string() ?: ""
                val resultList = JSONObject(string)
                    .getJSONObject("data")
                    .getJSONArray("result")
                val standardSongDataList = ArrayList<StandardSongData>()
                (0 until resultList.length()).forEach {
                    val item = resultList.getJSONObject(it)
                    val artists = ArrayList<StandardSongData.StandardArtistData>()
                    artists.add(StandardSongData.StandardArtistData(0, item.getStr("author")))
                    var title = item.getStr("title")
                    title = title.replace("<em class=\"keyword\">", "").replace("</em>", "")
                    standardSongDataList.add(
                        StandardSongData(
                            SOURCE_BILIBILI,
                            item.getStr("aid"),
                            title,
                            "https:" + item.getStr("pic"),
                            artists,
                            null,
                            null,
                            null
                        )
                    )
                }
                success.invoke(standardSongDataList)
            }

            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "err", e)
            }
        })
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