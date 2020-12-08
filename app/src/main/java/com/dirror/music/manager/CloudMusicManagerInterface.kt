package com.dirror.music.manager

import com.dirror.music.data.CommentData

interface CloudMusicManagerInterface {

    /**
     * 获取评论
     */
    fun getComment(id: String, success: (CommentData) -> Unit, failure: () -> Unit)

}