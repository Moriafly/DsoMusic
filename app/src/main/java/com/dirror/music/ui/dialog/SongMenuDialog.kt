package com.dirror.music.ui.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import com.dirror.music.MyApplication
import com.dirror.music.R
import com.dirror.music.data.PLAYLIST_TAG_MY_FAVORITE
import com.dirror.music.data.PLAYLIST_TAG_NORMAL
import com.dirror.music.databinding.DialogSongMenuBinding
import com.dirror.music.music.local.MyFavorite
import com.dirror.music.music.standard.data.StandardSongData
import com.dirror.music.util.Secure
import com.dirror.music.util.toast
import com.google.android.material.bottomsheet.BottomSheetDialog

class SongMenuDialog
    @JvmOverloads
    constructor(context: Context, private val tag: Int = PLAYLIST_TAG_NORMAL) : BaseBottomSheetDialog(context) {

    private var binding = DialogSongMenuBinding.inflate(layoutInflater)

    init {
        setContentView(binding.root)
    }

    private lateinit var songData: StandardSongData
    private lateinit var activity: Activity


    override fun initView() {
        super.initView()

    }

    override fun initListener() {
        super.initListener()
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
                if (tag == PLAYLIST_TAG_MY_FAVORITE) {
                    MyFavorite.deleteById(songData.id)
                    toast("删除成功")
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