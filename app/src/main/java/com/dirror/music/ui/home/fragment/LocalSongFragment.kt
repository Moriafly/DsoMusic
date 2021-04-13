package com.dirror.music.ui.home.fragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.dirror.music.adapter.item.BlankAdapter
import com.dirror.music.adapter.SongAdapter
import com.dirror.music.adapter.item.ItemSearchAdapter
import com.dirror.music.databinding.FragmentLocalSongBinding
import com.dirror.music.music.local.LocalMusic
import com.dirror.music.music.standard.data.StandardSongData
import com.dirror.music.ui.base.BaseFragment
import com.dirror.music.ui.dialog.SongMenuDialog
import com.dirror.music.ui.home.MainViewModel
import com.dirror.music.ui.playlist.SongSearchActivity
import com.dirror.music.ui.playlist.SongSearchTransmit
import com.dirror.music.util.extensions.dp
import com.dirror.music.util.runOnMainThread
import com.dirror.music.util.toast
import kotlin.concurrent.thread

class LocalSongFragment : BaseFragment() {

    private var _binding: FragmentLocalSongBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel: MainViewModel by activityViewModels()

    var songList = ArrayList<StandardSongData>()

    private val itemSearchAdapter = ItemSearchAdapter() {
        thread {
            SongSearchTransmit.songList = songList
            runOnMainThread {
                startActivity(Intent(requireContext(), SongSearchActivity::class.java))
            }
        }
    }

    private val adapter = SongAdapter() {
        SongMenuDialog(requireContext(), requireActivity(), it) {
            toast("不支持删除")
        }.show()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLocalSongBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun initView() {

        val blankAdapter = BlankAdapter(56.dp() + (mainViewModel.navigationBarHeight.value ?: 0))
        val concatAdapter = ConcatAdapter(
            itemSearchAdapter, adapter, blankAdapter
        )

        binding.rvSongs.adapter = concatAdapter
        binding.rvSongs.layoutManager = LinearLayoutManager(requireContext())
        scanLocalMusicByCheckPermission()
    }

    private fun scanLocalMusicByCheckPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                1
            )
        } else {
            scanLocalMusic()
        }
    }

    private fun scanLocalMusic() {
        LocalMusic.scanLocalMusic(requireContext(), {
            runOnMainThread {
//            binding.titleBar.setTitleBarText("本地音乐(${it.size})")
                songList = it
                adapter.submitList(it)
                itemSearchAdapter.text = "搜索此列表歌曲 ${it.size} 首"
                binding.rvSongs.scrollToPosition(0)
            }
        }, {

        })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                scanLocalMusic()
            } else {
                toast("拒绝权限无法扫描本地音乐")
            }
            else -> {

            }
        }
    }

}