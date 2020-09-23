package com.dirror.music.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.dirror.music.MyApplication
import com.dirror.music.R
import com.dirror.music.adapter.PlaylistDialogAdapter
import kotlinx.android.synthetic.main.dialog_play_list.*
import kotlinx.android.synthetic.main.dialog_play_more.*

class PlaylistDialog: Dialog {
    constructor(context: Context) : this(context, 0)

    constructor(context: Context, themeResId: Int) : super(context, R.style.style_default_dialog) {
        setContentView(R.layout.dialog_play_list)
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
        rvPlaylist.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = MyApplication.musicBinderInterface?.getPlaylist()?.let { PlaylistDialogAdapter(it) }
        }
    }
}