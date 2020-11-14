package com.dirror.music.ui.activity

import androidx.recyclerview.widget.LinearLayoutManager
import com.dirror.music.MyApplication
import com.dirror.music.music.CloudMusic
import com.dirror.music.R
import com.dirror.music.adapter.CommentAdapter
import com.dirror.music.music.standard.SongComment
import com.dirror.music.ui.base.BaseActivity
import com.dirror.music.util.runOnMainThread
import kotlinx.android.synthetic.main.activity_comment.*
import kotlinx.android.synthetic.main.dirrorx_titlebar_layout.view.*

class CommentActivity : BaseActivity(R.layout.activity_comment) {

    override fun initView() {
        titleBar.tvTitleBar.text = "精彩评论"
        MyApplication.musicBinderInterface?.getNowSongData()?.let {
            SongComment.getComment(it, {
                runOnMainThread {
                    rvComment.layoutManager = LinearLayoutManager(this@CommentActivity)
                    rvComment.adapter = CommentAdapter(it)
                }
            }, {

            })
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(
            R.anim.anim_no_anim,
            R.anim.anim_slide_exit_bottom
        )
    }

}