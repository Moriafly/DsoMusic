package com.dirror.music.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import com.dirror.music.R
import com.dirror.music.databinding.DialogCreateLocalPlaylistBinding
import com.dirror.music.music.local.LocalPlaylist
import com.dirror.music.util.toast

/**
 * 创建本地歌单 Dialog
 * @author Moriafly
 */
class CreateLocalPlaylistDialog(context: Context) : Dialog(context, R.style.style_default_dialog) {

    private var binding = DialogCreateLocalPlaylistBinding.inflate(layoutInflater)

    init {
        setContentView(binding.root)
        window?.setGravity(Gravity.BOTTOM)
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.apply {
            btnConfirm.setOnClickListener {
                val name = etName.text.toString()
                val description = etDescription.text.toString()
                val imageUrl = etImageUrl.text.toString()
                if (name.isEmpty()) {
                    toast("名称不为空")
                } else {
                    LocalPlaylist.create(name, description, imageUrl)
                }
            }
        }
    }

}