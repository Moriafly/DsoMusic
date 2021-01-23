package com.dirror.music.room

import androidx.room.*

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

    @Query("delete from MyFavoriteData where id = :id")
    fun deleteById(id: String): Int

}