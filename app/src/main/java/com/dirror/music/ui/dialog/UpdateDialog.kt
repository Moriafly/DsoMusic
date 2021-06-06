package com.dirror.music.ui.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import com.dirror.music.R
import com.dirror.music.databinding.DialogUpdateBinding
import com.dirror.music.util.*

class UpdateDialog(context: Context, private val updateData: UpdateUtil.UpdateData): Dialog(context, R.style.style_default_dialog) {

    private var binding: DialogUpdateBinding = DialogUpdateBinding.inflate(layoutInflater)

    init {
        setContentView(binding.root)
        // 设置显示位置
        window?.setGravity(Gravity.BOTTOM)
        // 设置大小
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        // 不能点击外部关闭 Dialog
        setCanceledOnTouchOutside(false)
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.tvTitle.text = "发现新版本 ${updateData.name}"
        binding.tvContent.text = updateData.content

        updateData.tagVersion?.let {
            if (getVisionCode() < it) {
                toast("过低版本，请更新")
                openUrlByBrowser(this.context, updateData.url)
                binding.btnCancel.visibility = View.GONE
            }
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.btnDownload.setOnClickListener {
            openUrlByBrowser(this.context, updateData.url)
        }
    }

}