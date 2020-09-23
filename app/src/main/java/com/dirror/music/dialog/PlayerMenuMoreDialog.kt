package com.dirror.music.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import com.dirror.music.R
import kotlinx.android.synthetic.main.dialog_play_more.*

class PlayerMenuMoreDialog: Dialog {
    constructor(context: Context) : this(context, 0)

    constructor(context: Context, themeResId: Int) : super(context, R.style.style_default_dialog) {
        setContentView(R.layout.dialog_play_more)
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
        itemSongInfo.setOnClickListener {
            SongInfoDialog(context).apply {
                show()
            }
            // 自己消失
            dismiss()
        }

        clDialog.setOnClickListener {
            dismiss()
        }
    }
}