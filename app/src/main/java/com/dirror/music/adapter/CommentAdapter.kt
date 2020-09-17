package com.dirror.music.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.dirror.music.R
import com.dirror.music.cloudmusic.CommentData
import com.dirror.music.util.GlideUtil
import com.dirror.music.util.msTimeToFormatDate
import com.dirror.music.util.openUrlByBrowser


class CommentAdapter(val commentData: CommentData): RecyclerView.Adapter<CommentAdapter.ViewHolder>() {
    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvContent: TextView = view.findViewById(R.id.tvContent)
        val tvLikedCount: TextView = view.findViewById(R.id.tvLikedCount)
        val ivCover: ImageView = view.findViewById(R.id.ivCover)
        val tvTime: TextView = view.findViewById(R.id.tvTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        LayoutInflater.from(parent.context).inflate(R.layout.layout_comment, parent, false).apply {
            return ViewHolder(this)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvName.text = commentData.hotComments[position].user.nickname
        holder.tvContent.text = commentData.hotComments[position].content
        holder.tvLikedCount.text = commentData.hotComments[position].likedCount.toString()
        holder.tvTime.text = msTimeToFormatDate(commentData.hotComments[position].time)
        GlideUtil.loadCloudMusicImage(commentData.hotComments[position].user.avatarUrl, 50, 50, holder.ivCover)
    }

    override fun getItemCount(): Int {
        return commentData.hotComments.size
    }
}