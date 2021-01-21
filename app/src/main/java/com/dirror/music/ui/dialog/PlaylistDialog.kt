package com.dirror.music.ui.dialog

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.dirror.music.MyApplication
import com.dirror.music.R
import com.dirror.music.adapter.PlaylistDialogAdapter
import com.dirror.music.databinding.DialogPlayListBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class PlaylistDialog: BottomSheetDialog {

    private var binding: DialogPlayListBinding = DialogPlayListBinding.inflate(layoutInflater)

    constructor(context: Context) : this(context, 0)

    constructor(context: Context, themeResId: Int) : super(context, R.style.style_default_dialog) {
        setContentView(binding.root)
        // 设置显示位置
        window?.setGravity(Gravity.BOTTOM)
        // 设置大小
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window?.setWindowAnimations(R.style.dialog_animation)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.rvPlaylist.layoutManager = LinearLayoutManager(context)
        MyApplication.musicBinderInterface?.getPlaylist()?.let {
            binding.rvPlaylist.adapter = PlaylistDialogAdapter(it)
            binding.tvPlaylist.text = this.context.getString(R.string.playlist_number, it.size)
        }

    }
}