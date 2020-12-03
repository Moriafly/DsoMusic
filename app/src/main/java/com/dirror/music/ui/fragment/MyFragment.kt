package com.dirror.music.ui.fragment

import android.app.Activity.RESULT_OK
import android.content.Intent
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
import com.dirror.music.ui.activity.LoginActivity2
import com.dirror.music.ui.base.BaseFragment
import com.dirror.music.util.*
import kotlinx.android.synthetic.main.fragment_my.*

class MyFragment : BaseFragment() {

    private val defaultUid = 0L // 默认 0L，可设置一个默认用户

    private val userPlaylist = ArrayList<PlaylistData>()

    override fun getLayoutId(): Int {
        return R.layout.fragment_my
    }

    override fun initData() {
        checkLogin()
    }

    /**
     * 检查是否已经登录
     */
    private fun checkLogin() {
        val uid= MyApplication.mmkv.decodeLong(Config.UID, defaultUid)
        if (uid == 0L) {
            startLoginActivity()
        }
    }

    override fun initView() {
        rvPlaylist.layoutManager =  LinearLayoutManager(activity)
        rvPlaylist.adapter = PlaylistAdapter(userPlaylist)

        getUserDetail()
        getPlaylist()

        // 获取本地歌单
        // getLocalPlaylist()
    }

    override fun initListener() {
        itemAccount.setOnClickListener {
            startLoginActivity()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            0 -> if (resultCode == RESULT_OK) {
                // val user = data?.getBooleanExtra("boolean_user", true)
                getUserDetail()
                getPlaylist()
            }
        }
    }

    /**
     * 获取用户详情
     */
    private fun getUserDetail() {
        // 获取是否在线登录成功
        CloudMusic.getLoginStatus {  }

        val uid = MyApplication.mmkv.decodeLong(Config.UID, defaultUid)

        if (uid != 0L) {
            CloudMusic.getUserDetail(uid, {
                refreshUserDetail(it)
            }, {
                toast(it)
            })
        } else {
            toast("请先登录")
            // startActivity(Intent(context, LoginActivity::class.java))
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
        CloudMusic.getPlaylist(MyApplication.mmkv.decodeLong(Config.UID, defaultUid)){
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

    private fun startLoginActivity() {
        val intent = Intent(context, LoginActivity2::class.java)
        startActivityForResult(intent, 0)
        activity?.overridePendingTransition(
            R.anim.anim_slide_enter_bottom,
            R.anim.anim_no_anim
        )
    }

}