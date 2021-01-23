package com.dirror.music.ui.dialog

import android.content.Context
import com.dirror.music.databinding.DialogCreateLocalPlaylistBinding
import com.dirror.music.music.local.LocalPlaylist
import com.dirror.music.util.toast

/**
 * 创建本地歌单 Dialog
 * @author Moriafly
 */
class CreateLocalPlaylistDialog(context: Context) : BaseBottomSheetDialog(context) {

    private var binding = DialogCreateLocalPlaylistBinding.inflate(layoutInflater)

    init {
        setContentView(binding.root)
    }

    override fun initListener() {
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