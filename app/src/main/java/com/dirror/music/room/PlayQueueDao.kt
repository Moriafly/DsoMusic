package com.dirror.music.room

import androidx.room.*

@Dao
interface PlayQueueDao {

    @Insert
    fun insert(playQueueData: PlayQueueData): Long

    @Update
    fun update(playQueueData: PlayQueueData)

    @Query("select * from PlayQueueData")
    fun loadAll(): List<PlayQueueData>

    @Delete
    fun delete(playQueueData: PlayQueueData)

    @Query("delete from PlayQueueData where id = :id")
    fun deleteById(id: String): Int

    @Query("delete from PlayQueueData")
    fun clear()

}