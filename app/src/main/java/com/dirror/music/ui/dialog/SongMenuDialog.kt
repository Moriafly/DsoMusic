package com.dirror.music.ui.dialog

import android.app.Activity
import android.content.Context
import com.dirror.music.MyApplication
import com.dirror.music.data.PLAYLIST_TAG_MY_FAVORITE
import com.dirror.music.data.PLAYLIST_TAG_NORMAL
import com.dirror.music.databinding.DialogSongMenuBinding
import com.dirror.music.music.local.MyFavorite
import com.dirror.music.music.standard.data.StandardSongData
import com.dirror.music.ui.base.BaseBottomSheetDialog
import com.dirror.music.util.toast

class SongMenuDialog
    @JvmOverloads
    constructor(context: Context,
                private val activity: Activity,
                private val songData: StandardSongData,
                private val tag: Int = PLAYLIST_TAG_NORMAL) : BaseBottomSheetDialog(context) {

    private var binding = DialogSongMenuBinding.inflate(layoutInflater)

    init {
        setContentView(binding.root)
    }

    override fun initListener() {
        super.initListener()
        binding.apply {
            // 添加到本地我喜欢
            itemAddLocalMyFavorite.setOnClickListener {
                MyFavorite.addSong(songData)
                dismiss()
            }
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
                    dismiss()
                }
            }
        }
    }

}