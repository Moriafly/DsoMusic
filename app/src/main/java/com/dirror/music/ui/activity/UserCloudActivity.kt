package com.dirror.music.ui.activity

import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dirror.music.adapter.SongDataAdapter
import com.dirror.music.databinding.ActivityUserCloudBinding
import com.dirror.music.ui.base.BaseActivity
import com.dirror.music.ui.dialog.SongMenuDialog
import com.dirror.music.ui.viewmodel.UserCloudViewModel

/**
 * 用户云盘
 */
class UserCloudActivity : BaseActivity() {

    private lateinit var binding: ActivityUserCloudBinding

    private val userCloudViewModel: UserCloudViewModel by viewModels()

    private lateinit var songAdapter: SongDataAdapter

    override fun initBinding() {
        binding = ActivityUserCloudBinding.inflate(layoutInflater)
        miniPlayer = binding.miniPlayer
        setContentView(binding.root)
    }

    override fun initView() {
        songAdapter = SongDataAdapter() {
            SongMenuDialog(this, this, it) {

            }
        }
        binding.rvUserCloud.apply {
            adapter = songAdapter
            layoutManager = LinearLayoutManager(this@UserCloudActivity)
        }
        userCloudViewModel.fetchData()
    }

    override fun initListener() {
        binding.rvUserCloud.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) { // 向下滑动
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    if (layoutManager.findLastVisibleItemPosition() == songAdapter.itemCount - 2) {
                        userCloudViewModel.fetchData()
                    }
                }
            }
        })
    }

    override fun initObserver() {
        userCloudViewModel.size.observe(this, {
            binding.tvSize.text = it
        })
        userCloudViewModel.songlist.observe(this, {
            songAdapter.submitList(it)
        })
    }


}