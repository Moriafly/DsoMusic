package com.dirror.music.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
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
import com.dirror.music.util.*

class MyFragment : Fragment() {

    private var _binding: FragmentMyBinding? = null
    private val binding get() = _binding!! // 这里有时候会报一个空指针，现在没发现为什么

    private val userPlaylist = ArrayList<PlaylistData>()

    // activity 和 fragment 共享数据
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initListener()
    }

    private fun initView() {
        binding.rvPlaylist.layoutManager =  LinearLayoutManager(activity)
        binding.rvPlaylist.adapter = activity?.let { PlaylistAdapter(userPlaylist, it) }

        mainViewModel.userId.observe(viewLifecycleOwner, {
            getPlaylist(it)
        })
    }

    private fun initListener() {

        // 新建歌单
        binding.clNewPlaylist.setOnClickListener {
            toast("功能开发中")
        }

        binding.clImportPlaylist.setOnClickListener {
            toast("功能开发中")
        }

        binding.clLocalMusic.setOnClickListener {
            val intent = Intent(this.context, LocalMusicActivity::class.java)
            startActivity(intent)
        }

        binding.clHistory.setOnClickListener {
            // toast("功能开发中，预计不久上线")
            val intent = Intent(this.context, PlayHistoryActivity::class.java)
            startActivity(intent)
        }

    }

    private fun getPlaylist(id: Long) {
        MyApplication.cloudMusicManager.getUserPlaylist(id) {
            val playlist = it.playlist
            loge("大小：${playlist.size}")
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
    }

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

    override fun onDestroyView() {
        super.onDestroyView()
        // _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}