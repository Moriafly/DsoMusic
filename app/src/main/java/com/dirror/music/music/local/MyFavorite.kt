package com.dirror.music.music.local

import com.dirror.music.MyApplication
import com.dirror.music.music.standard.data.StandardSongData
import com.dirror.music.room.MyFavoriteData
import org.jetbrains.annotations.TestOnly
import kotlin.concurrent.thread

/**
 * 本地我喜欢的
 */
object MyFavorite {

    private val myFavoriteDao = MyApplication.appDatabase.myFavoriteDao()

    /**
     * 读取本地歌曲
     */
    @TestOnly
    fun read(success: (ArrayList<StandardSongData>) -> Unit) {
        thread {
            val data = ArrayList<StandardSongData>()
            for (myFavorite in myFavoriteDao.loadAll()) {
                data.add(myFavorite.songData)
            }
            success.invoke(data)
        }
    }

    /**
     * 添加一首歌
     */
    @TestOnly
    fun addSong(songData: StandardSongData) {
        thread {
            myFavoriteDao.insert(MyFavoriteData(songData))
        }
    }

    /**
     * 删除一首歌
     */
    @TestOnly
    fun delete(songData: StandardSongData) {
        thread {
            myFavoriteDao.delete(MyFavoriteData(songData))
        }
    }

    /**
     * 通过 id 删除一首歌
     */
    fun deleteById(id: String) {
        thread {
            myFavoriteDao.deleteById(id)
        }
    }

}
