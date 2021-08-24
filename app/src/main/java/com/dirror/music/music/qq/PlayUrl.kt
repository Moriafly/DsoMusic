package com.dirror.music.music.qq

import com.dirror.music.music.dirror.SearchSong
import com.dirror.music.util.HttpUtils
import com.dirror.music.util.MagicHttp
import com.dirror.music.util.loge
import com.google.gson.Gson
import kotlin.math.abs

object PlayUrl {

    suspend fun getPlayUrl(songmid: String) :String {
        if (SearchSong.getDirrorSongUrl(songmid) != "") {
            return SearchSong.getDirrorSongUrl(songmid)
        } else {
            // 地址
            val url = """https://u.y.qq.com/cgi-bin/musicu.fcg?g_tk=5381&loginUin=0&hostUin=0&format=json&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq.json&needNewCode=0&data={"req":{"module":"CDN.SrfCdnDispatchServer","method":"GetCdnDispatch","param":{"guid":"8348972662","calltype":0,"userip":""}},"req_0":{"module":"vkey.GetVkeyServer","method":"CgiGetVkey","param":{"guid":"8348972662","songmid":["${songmid}"],"songtype":[1],"uin":"0","loginflag":1,"platform":"20"}},"comm":{"uin":0,"format":"json","ct":24,"cv":0}}""".trimIndent()
            loge("请求地址：${url}")
            HttpUtils.get(url,VkeyData::class.java)?.apply {
                val ip = req.data.freeflowsip[0]
                // 获取 vkey
                val purl = req_0.data.midurlinfo[0].purl
                if (purl != "") {
                    loge(ip + purl)
                    loge("http://dl.stream.qqmusic.qq.com/M800001L6DVu3OB1c9.mp3?vkey=419F0FD2E2552C95C4E76BFDFBF2BC331A2983AAEDC9CF91BBD4195603FE19B6550874248A1A87410D84D0405B2ABBD0F533F192C9B8EB35&guid=YYFM&uin=123456&fromtag=53\n")
                    return ip + purl
                }
            }
            // 获取 Dirror 音乐
            return SearchSong.getDirrorSongUrl(songmid)
        }
    }


    data class VkeyData(
        val req: ReqData,
        val req_0: Req_0Data
    )

    data class ReqData(
        val data: Freeflowsip
    )

    data class Freeflowsip(
        val freeflowsip: ArrayList<String>
    )

    data class Req_0Data(
        val data: Midurlinfo
    )

    data class Midurlinfo(
        val midurlinfo: ArrayList<VkeyReqData>
    )

    data class VkeyReqData(
        val purl: String,
        val vkey: String
    )

}