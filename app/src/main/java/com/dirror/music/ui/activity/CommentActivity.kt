package com.dirror.music.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dirror.music.MyApplication
import com.dirror.music.R
import com.dirror.music.adapter.CommentAdapter
import com.dirror.music.databinding.ActivityCommentBinding
import com.dirror.music.music.standard.SongComment
import com.dirror.music.music.standard.data.SOURCE_NETEASE
import com.dirror.music.ui.base.BaseActivity
import com.dirror.music.util.runOnMainThread
import kotlinx.android.synthetic.main.activity_comment.*
import kotlinx.android.synthetic.main.dirrorx_titlebar_layout.view.*
import java.lang.Appendable

class CommentActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_INT_SOURCE = "extra_int_source"
        const val EXTRA_STRING_ID = "extra_string_id"
    }

    private lateinit var binding: ActivityCommentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initData()
        initView()
    }

    private fun initData() {
        val source = intent.getIntExtra(EXTRA_INT_SOURCE, SOURCE_NETEASE)
        val id = intent.getStringExtra(EXTRA_STRING_ID)
        when (source) {
            SOURCE_NETEASE -> {
                MyApplication.cloudMusicManager.getComment(id?: "", {
                    runOnMainThread {
                        rvComment.layoutManager = LinearLayoutManager(this@CommentActivity)
                        rvComment.adapter = CommentAdapter(it)
                    }
                }, {

                })
            }
        }

    }

    private fun initView() {
        titleBar.tvTitleBar.text = "精彩评论"
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(
            R.anim.anim_no_anim,
            R.anim.anim_slide_exit_bottom
        )
    }

}