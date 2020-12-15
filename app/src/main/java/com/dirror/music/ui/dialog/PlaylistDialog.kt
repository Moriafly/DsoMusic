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
import com.dirror.music.databinding.DialogPlayListBinding

class PlaylistDialog: Dialog {

    private lateinit var binding: DialogPlayListBinding

    constructor(context: Context) : this(context, 0)

    constructor(context: Context, themeResId: Int) : super(context, R.style.style_default_dialog) {
        binding = DialogPlayListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // 设置显示位置
        window?.setGravity(Gravity.BOTTOM)
        // 设置大小
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        // editView.setText("你好")
        // setCanceledOnTouchOutside(false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding.rvPlaylist.layoutManager = LinearLayoutManager(context)
        binding.rvPlaylist.adapter = MyApplication.musicBinderInterface?.getPlaylist()?.let { PlaylistDialogAdapter(it) }

    }
}