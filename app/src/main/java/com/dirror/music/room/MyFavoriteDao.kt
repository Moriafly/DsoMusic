package com.dirror.music.room

import androidx.room.*
import com.dirror.music.music.standard.data.StandardSongData

@Dao
interface MyFavoriteDao {

    @Insert
    fun insert(myFavoriteData: MyFavoriteData): Long

    @Update
    fun update(myFavoriteData: MyFavoriteData)

    @Query("select * from MyFavoriteData")
    fun loadAll(): List<MyFavoriteData>

    @Delete
    fun delete(myFavoriteData: MyFavoriteData)

}