package com.dirror.music.ui.main.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.dirror.music.MyApp
import com.dirror.music.R
import com.dirror.music.manager.User
import com.dirror.music.startLocalMusicActivity
import com.dirror.music.ui.activity.PlayHistoryActivity
import com.dirror.music.ui.activity.UserCloudActivity
import com.dirror.music.ui.playlist.SongPlaylistActivity
import com.dirror.music.ui.playlist.TAG_LOCAL_MY_FAVORITE
import com.dirror.music.util.AnimationUtil
import com.dirror.music.util.ErrorCode

class MyFragmentIconAdapter(val context: Context): RecyclerView.Adapter<MyFragmentIconAdapter.ViewHolder>() {

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val clLocal: ConstraintLayout = view.findViewById(R.id.clLocal)
        val clUserCloud: ConstraintLayout = view.findViewById(R.id.clUserCloud)
        val clFavorite: ConstraintLayout = view.findViewById(R.id.clFavorite)
        val clPersonalFM: ConstraintLayout = view.findViewById(R.id.clPersonalFM)
        val clLatest: ConstraintLayout = view.findViewById(R.id.clLatest)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        LayoutInflater.from(parent.context).inflate(R.layout.recycler_fragment_my_icon, parent, false).apply {
            return ViewHolder(this)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.apply {
            clLocal.setOnClickListener {
                AnimationUtil.click(it)
                startLocalMusicActivity(context)
            }
            // 我喜欢的音乐
            clFavorite.setOnClickListener {
                AnimationUtil.click(it)
                val intent = Intent(context, SongPlaylistActivity::class.java).apply {
                    putExtra(SongPlaylistActivity.EXTRA_TAG, TAG_LOCAL_MY_FAVORITE)
//                    putExtra(PlaylistActivity2.EXTRA_LONG_PLAYLIST_ID, 0L)
//                    putExtra(PlaylistActivity2.EXTRA_INT_TAG, PLAYLIST_TAG_MY_FAVORITE)
                }
                context.startActivity(intent)
            }
            // 播放历史
            clLatest.setOnClickListener {
                AnimationUtil.click(it)
                val intent = Intent(context, PlayHistoryActivity::class.java)
                context.startActivity(intent)
            }
            clPersonalFM.setOnClickListener {
                AnimationUtil.click(it)
                if (User.hasCookie) {
                    if (MyApp.musicController.value?.personFM?.value != true) {
                        MyApp.musicController.value?.setPersonFM(true)
                        MyApp.activityManager.startPlayerActivity(context as Activity)
                    } else {
                        MyApp.activityManager.startPlayerActivity(context as Activity)
                    }
                } else {
                    ErrorCode.toast(ErrorCode.ERROR_NOT_COOKIE)
                }
            }
            // 用户云盘
            clUserCloud.setOnClickListener {
                AnimationUtil.click(it)
                if (User.hasCookie) {
                    context.startActivity(Intent(context, UserCloudActivity::class.java))
                } else {
                    ErrorCode.toast(ErrorCode.ERROR_NOT_COOKIE)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return 1
    }

}