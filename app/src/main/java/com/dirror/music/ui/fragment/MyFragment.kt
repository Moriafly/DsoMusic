package com.dirror.music.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dirror.music.MyApplication
import com.dirror.music.R
import com.dirror.music.adapter.PlaylistAdapter
import com.dirror.music.data.PlaylistData
import com.dirror.music.data.UserDetailData
import com.dirror.music.music.CloudMusic
import com.dirror.music.music.standard.data.StandardLocalPlaylistData
import com.dirror.music.ui.activity.LocalMusicActivity
import com.dirror.music.ui.base.BaseFragment
import com.dirror.music.ui.viewmodel.MainViewModel
import com.dirror.music.util.*
import kotlinx.android.synthetic.main.fragment_my.*

class MyFragment : BaseFragment() {

    private val userPlaylist = ArrayList<PlaylistData>()

    // activity 和 fragment 共享数据
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun getLayoutId(): Int {
        return R.layout.fragment_my
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvPlaylist.layoutManager =  LinearLayoutManager(activity)
        rvPlaylist.adapter = PlaylistAdapter(userPlaylist)

        getUserDetail()
        getPlaylist()

        // 获取本地歌单
        // getLocalPlaylist()

        mainViewModel.getUserId().observe(viewLifecycleOwner, {
            // toast("fragment")
            getUserDetail()
            getPlaylist()
        })
    }

    override fun initListener() {
        itemAccount.setOnClickListener {
            val userId = MyApplication.userManager.getCurrentUid()
            activity?.let {
                MyApplication.activityManager.startUserActivity(it, userId)
            }
        }

        // 新建歌单
        clNewPlaylist.setOnClickListener {
            toast("功能开发中")
        }

        clImportPlaylist.setOnClickListener {
            toast("功能开发中")
        }

        clLocalMusic.setOnClickListener {
            val intent = Intent(this.context, LocalMusicActivity::class.java)
            startActivity(intent)
        }

        clHistory.setOnClickListener {
            toast("功能开发中")
        }

    }

    /**
     * 获取用户详情
     */
    private fun getUserDetail() {
        // 获取是否在线登录成功
        CloudMusic.getLoginStatus {  }

        if (MyApplication.userManager.isUidLogin()) {
            CloudMusic.getUserDetail(MyApplication.userManager.getCurrentUid(), {
                refreshUserDetail(it)
            }, {
                toast(it)
            })
        } else {
            toast("请先登录")
        }
    }

    /**
     * 获取用户详细信息后刷新界面数据
     */
    private fun refreshUserDetail(userDetailData: UserDetailData) {
        runOnMainThread {
            Glide.with(MyApplication.context)
                .load(http2https(userDetailData.profile?.avatarUrl.toString()))
                .into(ivPhoto)
            // 显示昵称
            tvNickname.text = userDetailData.profile?.nickname
            tvLevel.text = "Lv.${userDetailData.level}"
            // 关注和粉丝
            tvFollows.text = "${getString(R.string.follow)} ${userDetailData.profile?.follows}"
            tvFolloweds.text = "${getString(R.string.fans)} ${userDetailData.profile?.followeds}"
        }
    }

    private fun getPlaylist() {
        CloudMusic.getPlaylist(MyApplication.userManager.getCurrentUid()){
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
                rvPlaylist.layoutManager =  gridLayoutManager
                rvPlaylist.adapter = PlaylistAdapter(playlist)
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
            rvLocalPlaylist.layoutManager =  gridLayoutManager
            // rvLocalPlaylist.adapter = PlaylistAdapter()
        }
    }

}