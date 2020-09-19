package com.dirror.music.music

import android.util.Log
import com.dirror.music.CloudMusic
import com.dirror.music.api.API_MUSIC_API
import com.dirror.music.cloudmusic.DetailPlaylistData
import com.dirror.music.cloudmusic.SongData
import com.dirror.music.ui.activity.PlaylistActivity
import com.dirror.music.util.MagicHttp
import com.google.gson.Gson

object PlaylistUtil {

    fun getDetailPlaylist(id: Long, success: (ArrayList<StandardSongData>) -> Unit, failure: (String) -> Unit) {
        val url = "$API_MUSIC_API/playlist/detail?id=$id"
        MagicHttp.OkHttpManager().newGet(url, { response ->
            val detailPlaylistData = Gson().fromJson(response, DetailPlaylistData::class.java)
            PlaylistUtil.detailPlaylistDataToStandardSongDataList(detailPlaylistData){
                success.invoke(it)
            }
        }, {
            failure.invoke(it.message.toString())
        })
    }

    private fun detailPlaylistDataToStandardSongDataList(detailPlaylistData: DetailPlaylistData, success: (ArrayList<StandardSongData>) -> Unit) {


        // 获取全 id
        val trackIds = detailPlaylistData.playlist.trackIds

        // 初始化歌单
        val standardSongDataList = ArrayList<StandardSongData>()


        var count = 0 // 计数器
        for (trackId in 0..trackIds.lastIndex) {  // 遍历 id
            // Log.e("歌曲id", trackIds[trackId].id.toString())
            CloudMusic.getSongDetail(trackIds[trackId].id){
                standardSongDataList.add(it)
                count++
            }
            if (count == trackIds.lastIndex) {
                // 全部加载完全
                success.invoke(standardSongDataList)
            }
        }
    }
}





