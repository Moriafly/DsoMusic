package com.dirror.music.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import com.dirror.music.R
import com.dirror.music.util.*
import kotlinx.android.synthetic.main.dialog_update.*
import kotlinx.android.synthetic.main.include_foyou.*

class UpdateDialog: Dialog {

    constructor(context: Context) : this(context, 0)

    constructor(context: Context, themeResId: Int) : super(context, R.style.style_default_dialog) {
        setContentView(R.layout.dialog_update)
        // 设置显示位置
        window?.setGravity(Gravity.BOTTOM)
        // 设置大小
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        // 不能点击外部关闭 Dialog
        setCanceledOnTouchOutside(false)
    }

    var url = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        btnCancel.setOnClickListener {
            dismiss()
        }

        btnDownload.setOnClickListener {
            openUrlByBrowser(this.context, url)
        }

    }

    fun showInfo(updateData: UpdateUtil.UpdateData) {
        runOnMainThread {
            tvTitle.text = "发现新版本 ${updateData.name}"
            tvContent.text = updateData.content
            url = updateData.url
        }
    }

}