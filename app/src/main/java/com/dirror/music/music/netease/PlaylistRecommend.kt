package com.dirror.music.music.netease

import com.dirror.music.util.MagicHttp
import com.dirror.music.util.loge
import com.google.gson.Gson

/**
 * 获取网易云推荐歌单
 */
object PlaylistRecommend {

    fun getPlaylistRecommend(success: (ArrayList<PlaylistRecommendDataResult>) -> Unit, failure: () -> Unit) {

        val url = "http://musicapi.leanapp.cn/personalized?limit=6"

        MagicHttp.OkHttpManager().newGet(url, {
            loge("推荐歌单：$it")
            val playlistRecommendDataResultArrayList = Gson().fromJson(it, PlaylistRecommendData::class.java).result
            success.invoke(playlistRecommendDataResultArrayList)
        }, {
            failure.invoke()
        })

    }

    data class PlaylistRecommendData(
        val result: ArrayList<PlaylistRecommendDataResult>
    )

    data class PlaylistRecommendDataResult(
        val id: Long,
        val picUrl: String,
        val name: String,
        val playCount: Long, // 播放数
    )

}


