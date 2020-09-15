package com.dirror.music.ui.fragment

import android.content.Intent
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dirror.music.CloudMusic
import com.dirror.music.MyApplication
import com.dirror.music.R
import com.dirror.music.adapter.PlaylistAdapter
import com.dirror.music.cloudmusic.UserDetailData
import com.dirror.music.cloudmusic.UserPlaylistData
import com.dirror.music.ui.activity.LoginActivity
import com.dirror.music.ui.base.BaseFragment
import com.dirror.music.util.*
import kotlinx.android.synthetic.main.fragment_my.*

class MyFragment : BaseFragment() {
    override fun getLayoutId(): Int {
        return R.layout.fragment_my
    }

    override fun initView() {



        itemAccount.setOnClickListener {
            startActivity(Intent(context, LoginActivity::class.java))
        }


    }

    override fun onStart() {
        super.onStart()
        getUserDetail()
        getPlaylist()
    }

    private fun getUserDetail() {
        CloudMusic.getUserDetail(StorageUtil.getInt(StorageUtil.CLOUD_MUSIC_UID, -1), object : CloudMusic.UserDetailCallback {
            override fun success(userDetailData: UserDetailData) {
                runOnMainThread {
                    Glide.with(MyApplication.context)
                        .load(http2https(userDetailData.profile?.avatarUrl.toString()))
                        .into(ivPhoto)
                    tvNickname.text = userDetailData.profile?.nickname
                }
            }

            override fun failure() {
                toast("获取失败")
            }
        })
    }

    private fun getPlaylist() {
        CloudMusic.getPlaylist(StorageUtil.getInt(StorageUtil.CLOUD_MUSIC_UID, -1), object : CloudMusic.PlaylistCallback {
            override fun success(userPlaylistData: UserPlaylistData) {

                val playlist = userPlaylistData.playlist
                runOnMainThread {
                    val linearLayoutManager: LinearLayoutManager =
                        object : LinearLayoutManager(context) {
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
                                setMeasuredDimension(widthSpec, (playlist.size * dp2px(72f)).toInt())
                            }
                        }

                    // rvPlaylist.setHasFixedSize(true)
                    // rvPlaylist.isNestedScrollingEnabled = false
                    rvPlaylist.layoutManager =  linearLayoutManager//FullyLinearLayoutManager(context)
                    rvPlaylist.adapter = PlaylistAdapter(playlist)

                }

            }

        })
    }

}