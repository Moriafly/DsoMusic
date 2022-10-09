package com.dirror.music.music.bilibili

import android.util.Log
import com.dirror.music.util.HttpUtils
import com.dirror.music.util.getStr
import org.json.JSONObject
import java.util.regex.Matcher
import java.util.regex.Pattern

object BilibiliUrl {
    const val bilibiliAPI = "https://api.bilibili.com/x/player/playurl?"
    const val bilibiliBangumiAPI = "https://api.bilibili.com/pgc/player/web/playurl?"
    const val bilibiliTokenAPI = "https://api.bilibili.com/x/player/playurl/token?"
    const val referer = "https://www.bilibili.com"
    val headers = HashMap<String, String>()

    suspend fun getPlayUrl(aid: String): String {
        try {
            headers["referer"] = referer
            headers["origin"] = referer
            headers["User-Agent"] = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/101.0.0.0 Safari/537.36"
            val videoUrl = "https://www.bilibili.com/video/av$aid";
            val html = HttpUtils.get(videoUrl, String::class.java, headers = headers) ?: return ""

            val pageData = getMultiPageData(html)
            val page = pageData.getJSONObject("videoData").getJSONArray("pages").getJSONObject(0)
            val api = genAPI(
                pageData.getInt("aid"),
                page.getInt("cid"),
                127,
                pageData.getStr("bvid")
            )

            val jsonString = HttpUtils.get(api, String::class.java, headers = headers) ?: return ""
            Log.i("API_RESPONSE", jsonString)
            val data = JSONObject(jsonString)
            val description = data.getJSONObject("data").getJSONArray("accept_description")
            val dashData: JSONObject
            if (description.length() == 0) {
                dashData = data.getJSONObject("result");
            } else {
                dashData = data.getJSONObject("data");
            }
            val audioList = dashData.getJSONObject("dash").getJSONArray("audio")
            var max = 0
            var url = ""
            (0 until audioList.length()).forEach {
                val stream = audioList.getJSONObject(it)
                Log.i("AUDIO_URL", stream.getString("baseUrl"))
                val bandwidth = stream.getInt("bandwidth")
                if (bandwidth > max) {
                    max = bandwidth
                    url = stream.getString("baseUrl")
                }
            }
            return url
        } catch (e: Exception) {
            Log.e("FYERROR", "error", e)
        }
        return ""
    }

    private fun genAPI(
        aid: Int,
        cid: Int,
        quality: Int,
        bvid: String
    ): String {

        val params = String.format(
            "avid=%d&cid=%d&bvid=%s&qn=%d&type=&otype=json&fourk=1&fnver=0&fnval=2000",
            aid, cid, bvid, quality
        )
        return bilibiliAPI + params
    }

    private fun getMultiPageData(html: String): JSONObject {
        val p: Pattern = Pattern.compile("window.__INITIAL_STATE__=(.+?);\\(function")
        val matcher: Matcher = p.matcher(html)
        if (!matcher.find()) {
            throw RuntimeException("not find page info")
        }
        val json: String = matcher.group(1)
        return JSONObject(json)
    }
}