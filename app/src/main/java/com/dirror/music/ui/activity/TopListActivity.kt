package com.dirror.music.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import coil.size.ViewSizeResolver
import coil.transform.CircleCropTransformation
import com.dirror.music.MyApplication
import com.dirror.music.R
import com.dirror.music.adapter.TopListAdapter
import com.dirror.music.databinding.ActivityTopListBinding
import com.dirror.music.music.netease.TopList
import com.dirror.music.music.standard.SongPicture
import com.dirror.music.music.standard.data.SOURCE_NETEASE
import com.dirror.music.ui.base.BaseActivity
import com.dirror.music.ui.dialog.PlaylistDialog
import com.dirror.music.util.parseArtist
import com.dirror.music.util.runOnMainThread

class TopListActivity : BaseActivity() {

    private lateinit var binding: ActivityTopListBinding

    override fun initBinding() {
        binding = ActivityTopListBinding.inflate(layoutInflater)
        miniPlayer = binding.miniPlayer
        setContentView(binding.root)
    }

    override fun initView() {
        TopList.getTopList(this, {
            runOnMainThread {
                binding.rvTopList.layoutManager = LinearLayoutManager(this)
                binding.rvTopList.adapter = TopListAdapter(it) { listData ->
                    val intent = Intent(this, PlaylistActivity::class.java)
                    intent.putExtra(PlaylistActivity.EXTRA_PLAYLIST_SOURCE, SOURCE_NETEASE)
                    intent.putExtra(PlaylistActivity.EXTRA_LONG_PLAYLIST_ID, listData.id)
                    // intent.putExtra(PlaylistActivity2.EXTRA_INT_TAG, PLAYLIST_TAG_NORMAL)
                    startActivity(intent)
                }
            }
        }, {

        })
    }

}