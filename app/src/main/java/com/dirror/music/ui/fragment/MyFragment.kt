package com.dirror.music.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.dirror.music.MyApplication
import com.dirror.music.adapter.PlaylistAdapter
import com.dirror.music.data.PLAYLIST_TAG_MY_FAVORITE
import com.dirror.music.data.PlaylistData
import com.dirror.music.databinding.FragmentMyBinding
import com.dirror.music.music.standard.data.SOURCE_LOCAL
import com.dirror.music.ui.activity.LocalMusicActivity
import com.dirror.music.ui.activity.PlayHistoryActivity
import com.dirror.music.ui.activity.PlaylistActivity2
import com.dirror.music.ui.viewmodel.MainViewModel
import com.dirror.music.ui.viewmodel.MyFragmentViewModel
import com.dirror.music.util.*

/**
 * 我的
 */
class MyFragment : Fragment() {

    private var _binding: FragmentMyBinding? = null
    private val binding get() = _binding!!

    // activity 和 fragment 共享数据
    private val mainViewModel: MainViewModel by activityViewModels()
    private val myFragmentViewModel: MyFragmentViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initListener()
        initObserver()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    private fun initView() {

    }

    private fun initListener() {
        binding.apply {
            // 我喜欢的音乐
            clFavorite.setOnClickListener {
                AnimationUtil.click(it)
                val intent = Intent(this@MyFragment.context, PlaylistActivity2::class.java).apply {
                    putExtra(PlaylistActivity2.EXTRA_PLAYLIST_SOURCE, SOURCE_LOCAL)
                    putExtra(PlaylistActivity2.EXTRA_LONG_PLAYLIST_ID, 0L)
                    putExtra(PlaylistActivity2.EXTRA_INT_TAG, PLAYLIST_TAG_MY_FAVORITE)
                }
                startActivity(intent)
            }
            // 新建歌单
            clNewPlaylist.setOnClickListener {
                toast("功能开发中，敬请期待")
            }
            // 本地音乐
            clLocal.setOnClickListener {
                AnimationUtil.click(it)
                val intent = Intent(this@MyFragment.context, LocalMusicActivity::class.java)
                startActivity(intent)
            }
            // 播放历史
            clLatest.setOnClickListener {
                AnimationUtil.click(it)
                // toast("功能开发中，预计不久上线")
                val intent = Intent(this@MyFragment.context, PlayHistoryActivity::class.java)
                startActivity(intent)
            }
            clPersonalFM.setOnClickListener {
                AnimationUtil.click(it)
                toast("还未做好，占个位置~")
//                PersonalFM.get {
//
//                }
            }
        }
    }

    private fun initObserver() {
        // binding.rvPlaylist.layoutManager =  gridLayoutManager
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
        mainViewModel.statusBarHeight.observe(viewLifecycleOwner, {
            (binding.llTop.layoutParams as LinearLayout.LayoutParams).apply {
                topMargin = it + 56.dp()
                // setMargins(0, it + 56.dp(), 0, 0)
            }
        })
    }

    /**
     * 设置歌单
     */
    private fun setPlaylist(playlist: ArrayList<PlaylistData>) {
        runOnMainThread {
            binding.rvPlaylist.layoutManager = if (MyApplication.mmkv.decodeBool(Config.DOUBLE_ROW_MY_PLAYLIST, false)) {
                GridLayoutManager(this.context, 2)
            } else {
                LinearLayoutManager(this.context)
            }
            binding.rvPlaylist.adapter = activity?.let { it1 -> PlaylistAdapter(playlist, it1) }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // 屏幕旋转，这里有时候会报一个空指针
        // _binding = null
    }

}