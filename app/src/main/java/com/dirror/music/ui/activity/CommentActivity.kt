package com.dirror.music.ui.activity

import androidx.recyclerview.widget.LinearLayoutManager
import com.dirror.music.CloudMusic
import com.dirror.music.R
import com.dirror.music.adapter.CommentAdapter
import com.dirror.music.cloudmusic.CommentData
import com.dirror.music.ui.base.BaseActivity
import com.dirror.music.util.runOnMainThread
import kotlinx.android.synthetic.main.activity_comment.*
import kotlinx.android.synthetic.main.dirrorx_titlebar_layout.view.*

class CommentActivity : BaseActivity() {

    override fun getLayoutId(): Int {
        return R.layout.activity_comment
    }

    override fun initView() {
        val id = intent.getLongExtra("long_music_id", -1)
        CloudMusic.getMusicComment(id, object : CloudMusic.MusicCommentCallback {
            override fun success(commentData: CommentData) {
                runOnMainThread {
                    titleBar.tvTitleBar.text = "评论 " + commentData.total.toString()
                    rvComment.layoutManager = LinearLayoutManager(this@CommentActivity)
                    rvComment.adapter = CommentAdapter(commentData)
                }
            }
        })
    }

}