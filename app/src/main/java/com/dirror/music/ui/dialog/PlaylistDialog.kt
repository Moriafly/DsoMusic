package com.dirror.music.ui.dialog

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import com.dirror.music.MyApplication
import com.dirror.music.R
import com.dirror.music.adapter.PlaylistDialogAdapter
import com.dirror.music.databinding.DialogPlayListBinding

class PlaylistDialog(context: Context): BaseBottomSheetDialog(context) {

    private var binding: DialogPlayListBinding = DialogPlayListBinding.inflate(layoutInflater)

    init {
        setContentView(binding.root)
    }

    override fun initView() {
        super.initView()
        binding.rvPlaylist.layoutManager = LinearLayoutManager(context)
        MyApplication.musicBinderInterface?.getPlaylist()?.let {
            binding.rvPlaylist.adapter = PlaylistDialogAdapter(it)
            binding.tvPlaylist.text = this.context.getString(R.string.playlist_number, it.size)
        }
    }
}