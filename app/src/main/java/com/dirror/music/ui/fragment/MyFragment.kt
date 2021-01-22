package com.dirror.music.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dirror.music.MyApplication
import com.dirror.music.adapter.PlaylistAdapter
import com.dirror.music.data.PlaylistData
import com.dirror.music.databinding.FragmentMyBinding
import com.dirror.music.music.standard.data.StandardLocalPlaylistData
import com.dirror.music.ui.activity.LocalMusicActivity
import com.dirror.music.ui.activity.PlayHistoryActivity
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

    private val myFragmentViewModel: MyFragmentViewModel by viewModels()

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

    private fun initView() {

    }

    private fun initListener() {
        binding.apply {
            // 新建歌单
            clNewPlaylist.setOnClickListener {
                toast("功能开发中，敬请期待")
                // this.context?.let { it1 -> CreateLocalPlaylistDialog(it1).show() }
            }
            // 导入歌单
//            clImportPlaylist.setOnClickListener { toast("功能开发中") }
            // 本地音乐
            clLocalMusic.setOnClickListener {
                val intent = Intent(this@MyFragment.context, LocalMusicActivity::class.java)
                startActivity(intent)
            }
            // 播放历史
            clHistory.setOnClickListener {
                // toast("功能开发中，预计不久上线")
                val intent = Intent(this@MyFragment.context, PlayHistoryActivity::class.java)
                startActivity(intent)
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
            // toast(it.size.toString())
            setPlaylist(it)
        })
    }

    /**
     * 设置歌单
     */
    private fun setPlaylist(playlist: ArrayList<PlaylistData>) {
        val gridLayoutManager: GridLayoutManager =
            object : GridLayoutManager(activity, 2, VERTICAL, false) {
                override fun canScrollVertically(): Boolean {
                    return false
                }

                override fun onMeasure(
                    recycler: RecyclerView.Recycler,
                    state: RecyclerView.State,
                    widthSpec: Int,
                    heightSpec: Int
                ) {
                    super.onMeasure(recycler, state, widthSpec, heightSpec)
                    setMeasuredDimension(widthSpec, (playlist.size / 2 * dp2px(80f)).toInt())
                }
            }.apply { orientation = LinearLayoutManager.VERTICAL }

        runOnMainThread {
            binding.rvPlaylist.layoutManager =  gridLayoutManager
            binding.rvPlaylist.adapter = activity?.let { it1 -> PlaylistAdapter(playlist, it1) }
        }
    }

    /**
     * 获取本地歌单
     */
    private fun getLocalPlaylist() {
        val localPlaylist = MyApplication.mmkv.decodeParcelable(Config.LOCAL_PLAYLIST, StandardLocalPlaylistData::class.java)
        val gridLayoutManager: GridLayoutManager =
            object : GridLayoutManager(activity, 2, VERTICAL, false) {
                override fun canScrollVertically(): Boolean {
                    return false
                }

                override fun onMeasure(
                    recycler: RecyclerView.Recycler,
                    state: RecyclerView.State,
                    widthSpec: Int,
                    heightSpec: Int
                ) {
                    super.onMeasure(recycler, state, widthSpec, heightSpec)
                    setMeasuredDimension(widthSpec, (localPlaylist.playlists.size / 2 * dp2px(80f)).toInt())
                }
            }.apply { orientation = LinearLayoutManager.VERTICAL }
        runOnMainThread {
            binding.rvLocalPlaylist.layoutManager =  gridLayoutManager
            // rvLocalPlaylist.adapter = PlaylistAdapter()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // 屏幕旋转，这里有时候会报一个空指针
        // _binding = null
    }

}