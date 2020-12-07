package com.dirror.music.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import com.dirror.music.R
import com.dirror.music.music.standard.data.StandardSongData
import com.dirror.music.util.toast
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val itemSongInfo = findViewById<ItemLayout>(R.id.itemSongInfo)

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

    }

    fun setSongData(standardSongData: StandardSongData) {
        songData = standardSongData
    }

}