package com.dirror.music

import com.cyl.musicapi.BaseApiImpl
import com.cyl.musicapi.bean.SearchData
import com.dirror.music.util.loge

object DirrorMusic {


    /**
     * 搜索全平台歌曲
     * @param query 搜索的文本
     * @param limit 每页返回的数量
     * @param offset 偏移量
     * @param success 成功的回调
     */
    fun searchSong(query: String, limit: Int, offset: Int, success: (result: SearchData) -> Unit, failure: (String?) -> Unit) {
        loge("Dirror Music 准备搜索")
        BaseApiImpl.searchSong(query, limit, offset, {
            loge("Dirror Music 搜索成功")
            success.invoke(it)
        }, {
            loge("Dirror Music 搜索失败")
            failure.invoke(it)
        })
    }

    fun searchSong(query: String, success: (result: String) -> Unit) {
        success.invoke("返回成功")
    }
}