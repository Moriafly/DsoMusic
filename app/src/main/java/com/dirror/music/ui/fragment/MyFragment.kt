package com.dirror.music.ui.fragment

import android.app.Activity.RESULT_OK
import android.content.Intent
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dirror.music.music.CloudMusic
import com.dirror.music.MyApplication
import com.dirror.music.R
import com.dirror.music.adapter.PlaylistAdapter
import com.dirror.music.data.PlaylistData
import com.dirror.music.data.UserDetailData
import com.dirror.music.ui.activity.LoginActivity
import com.dirror.music.ui.base.BaseFragment
import com.dirror.music.util.*
import kotlinx.android.synthetic.main.fragment_my.*

class MyFragment : BaseFragment() {

    // private val defaultUid = 316065764L
    private val defaultUid = -1L // 默认 -1，可设置一个默认用户

    private val userPlaylist = ArrayList<PlaylistData>()

    override fun getLayoutId(): Int {
        return R.layout.fragment_my
    }

    override fun initData() {

    }

    override fun initView() {
        rvPlaylist.layoutManager =  LinearLayoutManager(activity)
        rvPlaylist.adapter = PlaylistAdapter(userPlaylist)

        getUserDetail()
        getPlaylist()

    }

    override fun initListener() {
        itemAccount.setOnClickListener {
            val intent = Intent(context, LoginActivity::class.java)
            startActivityForResult(intent, 0)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            0 -> if (resultCode == RESULT_OK) {
                val user = data?.getBooleanExtra("boolean_user", true)
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

        val uid = StorageUtil.getLong(StorageUtil.CLOUD_MUSIC_UID, defaultUid)
        if (uid != -1L) {
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
            tvNickname.text = userDetailData.profile?.nickname
            tvLevel.text = "Lv.${userDetailData.level}"
        }
    }

    private fun getPlaylist() {
        CloudMusic.getPlaylist(StorageUtil.getLong(StorageUtil.CLOUD_MUSIC_UID, defaultUid)){
            val playlist = it.playlist
            loge("大小：${playlist.size}")
            val linearLayoutManager: LinearLayoutManager =
                object : LinearLayoutManager(activity) {
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
                        setMeasuredDimension(widthSpec, (playlist.size * dp2px(64f)).toInt())
                    }
                }.apply { orientation = LinearLayoutManager.VERTICAL }

            runOnMainThread {
                rvPlaylist.layoutManager =  linearLayoutManager
                rvPlaylist.adapter = PlaylistAdapter(playlist)
            }
        }
    }

}