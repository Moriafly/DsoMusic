package com.dirror.music.ui.activity

import androidx.recyclerview.widget.LinearLayoutManager
import com.dirror.music.App
import com.dirror.music.R
import com.dirror.music.adapter.CommentAdapter
import com.dirror.music.databinding.ActivityCommentBinding
import com.dirror.music.music.standard.data.SOURCE_NETEASE
import com.dirror.music.ui.base.SlideBackActivity
import com.dirror.music.util.runOnMainThread
import com.dirror.music.util.toast

class  CommentActivity : SlideBackActivity() {

    companion object {
        const val EXTRA_INT_SOURCE = "extra_int_source"
        const val EXTRA_STRING_ID = "extra_string_id"
    }

    private lateinit var binding: ActivityCommentBinding

    private lateinit var id: String
    private var source: Int = SOURCE_NETEASE


    override fun initBinding() {
        binding = ActivityCommentBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun initData() {
        id = intent.getStringExtra(EXTRA_STRING_ID)?:""
        source = intent.getIntExtra(EXTRA_INT_SOURCE, SOURCE_NETEASE)
        when (source) {
            SOURCE_NETEASE -> {
                App.cloudMusicManager.getComment(id, {
                    runOnMainThread {
                        binding.rvComment.layoutManager = LinearLayoutManager(this@CommentActivity)
                        binding.rvComment.adapter = CommentAdapter(it, this@CommentActivity)
                    }
                }, {

                })
            }
        }
    }

    override fun initView() {
        bindSlide(this, binding.clBase)

        var rvPlaylistScrollY = 0
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            binding.rvComment.setOnScrollChangeListener { _, _, _, _, oldScrollY ->
                rvPlaylistScrollY += oldScrollY
                slideBackEnabled = rvPlaylistScrollY == 0
            }
        }
    }

    override fun initListener() {
        binding.btnSendComment.setOnClickListener {
            val content = binding.etCommentContent.text.toString()
            if (content != "") {
                when (source) {
                    SOURCE_NETEASE -> {
                        App.cloudMusicManager.sendComment(1, 0, id, content, 0L, {
                            toast("评论成功")
                        }, {
                            toast("评论失败")
                        })
                    }
                }
            } else {
                toast("请输入")
            }
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