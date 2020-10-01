package com.dirror.music.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import com.dirror.music.MyApplication
import com.dirror.music.R
import kotlinx.android.synthetic.main.dialog_play_more.*
import kotlin.math.abs

class PlayerMenuMoreDialog : Dialog {
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

    private var speed = 1f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        speed = MyApplication.musicBinderInterface?.getSpeed() ?: 1f


        refreshPitch()


        // editView.setText(editTextStr)
        itemSongInfo.setOnClickListener {
            SongInfoDialog(context).apply {
                show()
            }
            // 自己消失
            dismiss()
        }

        ivIncreasePitch.setOnClickListener {
            MyApplication.musicBinderInterface?.increasePitchLevel()
            refreshPitch()
        }

        ivDecreasePitch.setOnClickListener {
            MyApplication.musicBinderInterface?.decreasePitchLevel()
            refreshPitch()
        }

        itemSpeed.setOnClickListener {
            MyApplication.musicBinderInterface?.setSpeed(1f)
        }

        clDialog.setOnClickListener {
            dismiss()
        }


    }

    /**
     * 刷新 Pitch
     */
    private fun refreshPitch() {
        tvPitch.text = MyApplication.musicBinderInterface?.getPitchLevel().toString()
    }
}