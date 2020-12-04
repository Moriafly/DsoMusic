package com.dirror.music.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.dirror.music.R
import com.dirror.music.music.netease.PlaylistRecommend
import com.dirror.music.ui.activity.PlaylistActivity
import com.dirror.music.util.GlideUtil
import com.dirror.music.util.dp2px


class PlaylistRecommendAdapter(private val playlistRecommendDataResult: ArrayList<PlaylistRecommend.PlaylistRecommendDataResult>) : RecyclerView.Adapter<PlaylistRecommendAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val clTrack: ConstraintLayout = view.findViewById(R.id.clTrack)
        val ivCover: ImageView = view.findViewById(R.id.ivCover)
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvPlayCount: TextView = view.findViewById(R.id.tvPlayCount)
        // val tvTrackCount: TextView = view.findViewById(R.id.tvTrackCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycle_view_playlist_recommend, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position == 0) {
            val layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.MATCH_PARENT)
            layoutParams.setMargins(dp2px(12f).toInt(), 0, 0, 0)
            holder.clTrack.layoutParams = layoutParams
        }


        // 获取当前歌单
        val playlist = playlistRecommendDataResult[position]

        GlideUtil.load(playlist.picUrl, holder.ivCover)
        holder.clTrack.setOnClickListener {
            val intent = Intent(it.context, PlaylistActivity::class.java)
            intent.putExtra("long_playlist_id", playlist.id)
            it.context.startActivity(intent)
        }
        holder.tvName.text = playlist.name
        holder.tvPlayCount.text = playlist.playCount.toString()
    }

    override fun getItemCount(): Int {
        return playlistRecommendDataResult.size
    }

}
