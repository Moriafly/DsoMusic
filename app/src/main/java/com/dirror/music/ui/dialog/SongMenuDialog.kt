package com.dirror.music.ui.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import com.dirror.music.MyApplication
import com.dirror.music.R
import com.dirror.music.music.standard.data.StandardSongData
import com.dirror.music.widget.ItemLayout

class SongMenuDialog : Dialog {
    constructor(context: Context) : this(context, 0)

    constructor(context: Context, themeResId: Int) : super(context, R.style.style_default_dialog) {
        setContentView(R.layout.dialog_song_menu)
        // 设置显示位置
        window?.setGravity(Gravity.BOTTOM)
        // 设置大小
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private lateinit var songData: StandardSongData
    private lateinit var activity: Activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val itemSongInfo = findViewById<ItemLayout>(R.id.itemSongInfo)
        val itemSongComment = findViewById<ItemLayout>(R.id.itemSongComment)

        // 歌曲信息
        itemSongInfo.setOnClickListener {
            // toast("歌曲信息 ${ songData.id }")
            SongInfoDialog(context).let { dialog ->
                dialog.setSongData(songData)
                dialog.show()
            }
            // 自己消失
            dismiss()
        }

        // 歌曲评论
        itemSongComment.setOnClickListener {
            MyApplication.activityManager.startCommentActivity(activity, songData.source, songData.id)
            dismiss()
        }

    }

    fun setSongData(standardSongData: StandardSongData) {
        songData = standardSongData
    }

    fun setActivity(activity: Activity) {
        this.activity = activity
    }

}