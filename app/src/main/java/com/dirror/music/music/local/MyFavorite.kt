package com.dirror.music.music.local

import android.os.Parcelable
import com.dirror.music.MyApplication
import com.dirror.music.music.standard.data.StandardSongData
import kotlinx.parcelize.Parcelize
import org.jetbrains.annotations.TestOnly

/**
 * 本地我喜欢的
 */
object MyFavorite {

    private const val ARRAY_MY_FAVORITE = "array_my_favorite"

    /**
     * 读取本地歌曲
     */
    @TestOnly
    fun read(): ArrayList<StandardSongData> {
        val myFavoriteData = MyApplication.mmkv.decodeParcelable(ARRAY_MY_FAVORITE, MyFavoriteData::class.java, MyFavoriteData(ArrayList()))
        return myFavoriteData.songList
    }

    fun addSong() {

    }

    /**
     * 我喜欢数据
     */
    @Parcelize
    data class MyFavoriteData(
        val songList: ArrayList<StandardSongData>
    ) : Parcelable

}
