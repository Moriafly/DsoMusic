package com.dirror.music.manager

import com.dirror.music.data.CommentData
import com.dirror.music.music.netease.data.UserDetailData

/**
 * 网易云音乐接口管理
 */
interface CloudMusicManagerInterface {

    /**
     * 获取评论
     */
    fun getComment(id: String, success: (CommentData) -> Unit, failure: () -> Unit)

    /**
     * 获取用户详细资料
     */
    fun getUserDetail(userId: Long, success: (UserDetailData) -> Unit, failure: () -> Unit)

    fun loginByTell(tell: String, password: String, success: (UserDetailData) -> Unit, failure: () -> Unit)

    fun likeSong(songId: String, success: () -> Unit, failure: () -> Unit)

}