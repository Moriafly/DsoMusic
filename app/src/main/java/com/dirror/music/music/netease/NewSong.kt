package com.dirror.music.music.netease

import android.content.Context
import com.dirror.music.api.CloudMusicApi
import com.dirror.music.music.netease.data.NewSongData
import com.dirror.music.music.standard.data.SOURCE_NETEASE
import com.dirror.music.music.standard.data.StandardSongData
import com.dirror.music.util.MagicHttp
import com.google.gson.Gson
import java.lang.Exception

object NewSong {

    fun getNewSong(context: Context, success: (ArrayList<StandardSongData>) -> Unit) {

        val url = CloudMusicApi.PERSONALIZED_NEW_SONG
        MagicHttp.OkHttpManager().getByCache(context, url, { string ->
            try {
                val newSongData = Gson().fromJson(string, NewSongData::class.java)

                val standardSongDataList = ArrayList<StandardSongData>()
                newSongData.result.forEach{
                    val standardArtistData = ArrayList<StandardSongData.StandardArtistData>()
                    it.song.artists.forEach { artist ->
                        standardArtistData.add(StandardSongData.StandardArtistData(artist.id, artist.name))
                    }
                    val standardSongData = StandardSongData(
                        SOURCE_NETEASE,
                        it.id.toString(),
                        it.name,
                        it.picUrl,
                        standardArtistData,
                        StandardSongData.NeteaseInfo(
                            it.song.fee, it.song.privilege.pl, it.song.privilege.flag, it.song.privilege.maxbr
                        ), null, null
                    )
                    standardSongDataList.add(standardSongData)
                }
                success.invoke(standardSongDataList)
            } catch (e: Exception) {

            }
        }, {

        })


    }

}