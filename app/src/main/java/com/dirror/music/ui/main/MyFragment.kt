package com.dirror.music.ui.main

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dirror.music.MyApplication
import com.dirror.music.adapter.MyPlaylistAdapter
import com.dirror.music.data.PlaylistData
import com.dirror.music.ui.base.BaseFragment
import com.dirror.music.ui.main.adapter.MyFragmentIconAdapter
import com.dirror.music.ui.main.adapter.MyFragmentUserAdapter
import com.dirror.music.ui.main.viewmodel.MainViewModel
import com.dirror.music.ui.main.viewmodel.MyFragmentViewModel
import com.dirror.music.ui.playlist.SongPlaylistActivity
import com.dirror.music.ui.playlist.TAG_NETEASE
import com.dirror.music.util.*

/**
 * 我的
 */
class MyFragment : BaseFragment() {

    // activity 和 fragment 共享数据
    private val mainViewModel: MainViewModel by activityViewModels()

    private val myFragmentViewModel: MyFragmentViewModel by viewModels()

    private lateinit var rvMy: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        rvMy = RecyclerView(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
        return rvMy
    }

    private lateinit var myFragmentUserAdapter: MyFragmentUserAdapter

    private lateinit var myPlaylistAdapter: MyPlaylistAdapter

    override fun initView() {

        myFragmentUserAdapter = MyFragmentUserAdapter() {
            if (MyApplication.userManager.getCurrentUid() == 0L) {
                MyApplication.activityManager.startLoginActivity(requireActivity())
            } else {
                MyApplication.activityManager.startUserActivity(
                    requireActivity(),
                    MyApplication.userManager.getCurrentUid()
                )
            }
        }

        myPlaylistAdapter = MyPlaylistAdapter {
            val intent = Intent(requireContext(), SongPlaylistActivity::class.java)
            intent.putExtra(SongPlaylistActivity.EXTRA_TAG, TAG_NETEASE)
            intent.putExtra(SongPlaylistActivity.EXTRA_PLAYLIST_ID, it.id.toString())
            requireContext().startActivity(intent)
        }

        val myFragmentIconAdapter = MyFragmentIconAdapter(requireContext())

        val concatAdapter = ConcatAdapter(myFragmentUserAdapter, myFragmentIconAdapter, myPlaylistAdapter)

        rvMy.layoutManager = LinearLayoutManager(requireContext())
        rvMy.adapter = concatAdapter

    }

    override fun initListener() {

    }

    @SuppressLint("SetTextI18n")
    override fun initObserver() {
        mainViewModel.userId.observe(viewLifecycleOwner, {
            // 清空歌单
            myFragmentViewModel.clearPlaylist()
            // toast("更新歌单")
            myFragmentViewModel.updatePlaylist()
        })
        // 用户歌单的观察
        myFragmentViewModel.userPlaylistList.observe(viewLifecycleOwner, {
            setPlaylist(it)
        })
        mainViewModel.userId.observe(viewLifecycleOwner, { userId ->
            if (userId == 0L) {
                myFragmentUserAdapter.adapterUser = MyFragmentUserAdapter.AdapterUser(
                    null, "立即登录", null
                )
            } else {
                MyApplication.cloudMusicManager.getUserDetail(userId, {
                    runOnMainThread {
                        myFragmentUserAdapter.adapterUser = MyFragmentUserAdapter.AdapterUser(
                            it.profile.avatarUrl, it.profile.nickname, it.level
                        )
                    }
                }, {

                })
            }
        })
        mainViewModel.singleColumnPlaylist.observe(viewLifecycleOwner, {
            val count = if (it) {
                1
            } else {
                2
            }
        })
//        mainViewModel.neteaseLiveVisibility.observe(viewLifecycleOwner, {
//            if (it) {
//                binding.clUserCloud.visibility = View.VISIBLE
//                binding.clPersonalFM.visibility = View.VISIBLE
//            } else {
//                binding.clUserCloud.visibility = View.GONE
//                binding.clPersonalFM.visibility = View.GONE
//            }
//
//        })
    }

    /**
     * 设置歌单
     */
    private fun setPlaylist(playlist: ArrayList<PlaylistData>) {
        runOnMainThread {
            myPlaylistAdapter.submitList(playlist)
        }
    }

}