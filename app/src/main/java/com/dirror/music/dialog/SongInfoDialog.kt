package com.dirror.music.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import com.dirror.music.MyApplication
import com.dirror.music.R
import com.dirror.music.api.StandardGET
import com.dirror.music.util.runOnMainThread
import kotlinx.android.synthetic.main.dialog_song_info.*

class SongInfoDialog: Dialog {
    // var editTextStr = ""
    constructor(context: Context) : this(context, 0)

    constructor(context: Context, themeResId: Int) : super(context, R.style.style_default_dialog) {
        setContentView(R.layout.dialog_song_info)
        // 设置显示位置
        window?.setGravity(Gravity.BOTTOM)
        // 设置大小
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        // editView.setText("你好")
        // setCanceledOnTouchOutside(false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // editView.setText(editTextStr)
        StandardGET.getSongInfo(MyApplication.musicBinderInterface?.getNowSongData()?.id?:-1) {
            runOnMainThread {
                valueViewBitrate.setValue("${it.br} B")
                valueViewSize.setValue("${it.size} B")
                valueViewType.setValue(it.type?:"未知")
            }
        }
    }
}