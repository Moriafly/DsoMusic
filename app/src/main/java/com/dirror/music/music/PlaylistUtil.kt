package com.dirror.music.music

import com.dirror.music.api.API_MUSIC_API
import com.dirror.music.data.DetailPlaylistData
import com.dirror.music.data.TrackIdsData
import com.dirror.music.util.MagicHttp
import com.dirror.music.util.loge
import com.google.gson.Gson

object PlaylistUtil {

    fun getDetailPlaylist(id: Long, success: (ArrayList<StandardSongData>) -> Unit, failure: (String) -> Unit) {
        loge("准备获取详细歌单${id}")
        val url = "$API_MUSIC_API/playlist/detail?id=$id"
        MagicHttp.OkHttpManager().newGet(url, { response ->

            val detailPlaylistData = Gson().fromJson(response, DetailPlaylistData::class.java)
            loge("读取歌单 json 成功")
            detailPlaylistDataToStandardSongDataList(detailPlaylistData) {
                loge("转换成功")
                success.invoke(it)
            }
        }, {
            failure.invoke(it)
        })
    }


    private var trackIds: List<TrackIdsData>? = null
    var i = 0
    var standardSongDataList = ArrayList<StandardSongData>()
    private fun detailPlaylistDataToStandardSongDataList(
        detailPlaylistData: DetailPlaylistData,
        success: (ArrayList<StandardSongData>) -> Unit
    ) {
        // 获取全 id
        trackIds = detailPlaylistData.playlist.trackIds
        val ids = ArrayList<Long>()
        for (trackId in 0..trackIds!!.lastIndex) {
            val id = trackIds!![trackId].id
            loge(id.toString())
            ids.add(id)
        }
        getSongListByIds(ids) {
            success.invoke(it)
        }

    }


    fun getSongListByIds(ids: ArrayList<Long>, success: (ArrayList<StandardSongData>) -> Unit) {
        var idsString = ""
        for (id in 0..ids.lastIndex) {
            idsString = "$idsString${ids[id]},"
        }
        idsString = idsString.substring(0, idsString.lastIndex)
        val url = "http://music.163.com/api/song/detail/?ids=%5B${idsString}%5D"
        loge(url)


        MagicHttp.OkHttpManager().newGet(url, {
            val data = Gson().fromJson(it, CompatSearchData::class.java)
            success.invoke(compatSearchDataToStandardPlaylistData(data))
        }, {

        })

    }

}

// http://music.163.com/api/song/detail/?id=1423062698&ids=%5B1423062698,521351799%5D
// http://www.pinlue.com/article/2020/07/0300/2810948441933.html



