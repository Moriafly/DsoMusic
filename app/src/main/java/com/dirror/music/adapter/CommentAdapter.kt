package com.dirror.music.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dirror.music.MyApplication
import com.dirror.music.R
import com.dirror.music.data.CommentData
import com.dirror.music.util.GlideUtil
import com.dirror.music.util.extensions.dp
import com.dirror.music.util.msTimeToFormatDate

/**
 * 评论 Adapter
 */
class CommentAdapter(private val commentData: CommentData, private val activity: Activity): RecyclerView.Adapter<CommentAdapter.ViewHolder>() {

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
        holder.apply {
            tvName.text = commentData.hotComments[position].user.nickname
            tvContent.text = commentData.hotComments[position].content
            tvLikedCount.text = commentData.hotComments[position].likedCount.toString()
            tvTime.text = msTimeToFormatDate(commentData.hotComments[position].time)
            GlideUtil.loadCircle(commentData.hotComments[position].user.avatarUrl, holder.ivCover, 36.dp())

            ivCover.setOnClickListener {
                MyApplication.activityManager.startUserActivity(activity , commentData.hotComments[position].user.userId)
            }

            tvName.setOnClickListener {
                MyApplication.activityManager.startUserActivity(activity , commentData.hotComments[position].user.userId)
            }
        }
    }

    override fun getItemCount(): Int {
        return commentData.hotComments.size
    }

}