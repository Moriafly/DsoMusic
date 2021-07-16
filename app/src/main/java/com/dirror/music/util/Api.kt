package com.dirror.music.util

import android.util.Log
import com.dirror.music.api.API_MUSIC_ELEUU
import com.dirror.music.data.DetailPlaylistData
import com.dirror.music.data.DetailPlaylistInnerData
import com.dirror.music.music.compat.CompatSearchData
import com.dirror.music.music.compat.compatSearchDataToStandardPlaylistData
import com.dirror.music.music.netease.Playlist
import com.dirror.music.music.standard.data.StandardSongData
import com.dso.ext.averageAssignFixLength

object Api {

    private const val TAG = "API"

    private const val SPLIT_PLAYLIST_NUMBER = 1000 // 切割歌单
    private const val CHEATING_CODE = -460 // Cheating 错误

    suspend fun getPlayListInfo(id: Long): DetailPlaylistInnerData? {
        val url = "$API_MUSIC_ELEUU/playlist/detail?id=${id}"
        return HttpUtils.get(url, DetailPlaylistData::class.java, true)?.playlist
    }

    suspend fun getPlayListByUID(id: Long): ArrayList<StandardSongData> {
        val url = "$API_MUSIC_ELEUU/playlist/detail?id=${id}"
        val playlist = HttpUtils.get(url, Playlist.PlaylistData::class.java, true)
        val trackIds = ArrayList<Long>()
        playlist?.playlist?.trackIds?.forEach {
            trackId -> trackIds.add(trackId.id)
        }
        val list = ArrayList<StandardSongData>()
        if (trackIds.size > 0) {
            trackIds.averageAssignFixLength(SPLIT_PLAYLIST_NUMBER).forEach lit@ { subTrackIds ->
                Log.d(TAG, "subTrackIds size is ${subTrackIds.size}")
                val idsBuilder = StringBuilder()
                for (trackId in subTrackIds) {
                    if (idsBuilder.isNotEmpty()) {
                        idsBuilder.append(",")
                    }
                    idsBuilder.append(trackId)
                }
                val ids = idsBuilder.toString()
                val data = HttpUtils.post("$API_MUSIC_ELEUU/song/detail?hash=${ids.hashCode()}",
                    Utils.toMap("ids", ids), CompatSearchData::class.java, true)
//                val data = HttpUtils.get("$API_MUSIC_ELEUU/song/detail?ids=${ids}", CompatSearchData::class.java)
                if (data != null) {
                    if (data.code == CHEATING_CODE) {
                        toast("-460 Cheating")
                        // 发生了欺骗立刻返回
                        return@lit
                    } else {
                        Log.i(TAG, "get result ${data.songs.size}")
                        list.addAll(compatSearchDataToStandardPlaylistData(data))
                    }
                }
            }

        }
        Log.d(TAG, "get playlist id $id, size:${list.size} , origin size:${trackIds.size}")
        return list
    }

}