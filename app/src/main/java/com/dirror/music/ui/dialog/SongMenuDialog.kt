package com.dirror.music.ui.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import com.dirror.music.MyApplication
import com.dirror.music.R
import com.dirror.music.databinding.DialogSongMenuBinding
import com.dirror.music.music.standard.data.StandardSongData
import com.dirror.music.util.Secure
import com.dirror.music.util.toast

class SongMenuDialog(context: Context) : Dialog(context, R.style.style_default_dialog) {

    private var binding = DialogSongMenuBinding.inflate(layoutInflater)

    init {
        setContentView(binding.root)
        window?.setGravity(Gravity.BOTTOM)
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private lateinit var songData: StandardSongData
    private lateinit var activity: Activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initListener()
    }

    private fun initListener() {
        binding.apply {
            // 歌曲信息
            itemSongInfo.setOnClickListener {
                // toast("歌曲信息 ${ songData.id }")
                SongInfoDialog(context, songData).show()
                // 自己消失
                dismiss()
            }
            // 歌曲评论
            itemSongComment.setOnClickListener {
                MyApplication.activityManager.startCommentActivity(activity, songData.source, songData.id)
                dismiss()
            }
            // 歌曲删除
            itemDeleteSong.setOnClickListener {
                if (Secure.isDebug()) {
                    toast("开发中")
                } else {
                    toast("测试")
                }
            }
        }
    }

    fun setSongData(standardSongData: StandardSongData) {
        songData = standardSongData
    }

    fun setActivity(activity: Activity) {
        this.activity = activity
    }

}