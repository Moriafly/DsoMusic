package com.dirror.music.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.dirror.music.MyApplication
import com.dirror.music.R
import com.dirror.music.music.netease.data.PrivateLetterData
import com.dirror.music.util.GlideUtil
import com.dirror.music.util.dp


class PrivateLetterAdapter(private val privateLetterMsgsData: ArrayList<PrivateLetterData.MsgsData>) : RecyclerView.Adapter<PrivateLetterAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val clTrack: ConstraintLayout = view.findViewById(R.id.clTrack)
        val ivCover: ImageView = view.findViewById(R.id.ivCover)
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvContent: TextView = view.findViewById(R.id.tvContent)
        val tvPlayCount: TextView = view.findViewById(R.id.tvPlayCount)
        // val tvTrackCount: TextView = view.findViewById(R.id.tvTrackCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycle_view_playlist_recommend, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val msg = privateLetterMsgsData[position]
        holder.tvName.text = msg.fromUser.nickname
        val picUrl = MyApplication.cloudMusicManager.getPicture(msg.fromUser.avatarUrl, 40.dp())
        GlideUtil.load(picUrl, holder.ivCover)
        holder.tvContent.text = msg.lastMsg
    }

    override fun getItemCount(): Int {
        return privateLetterMsgsData.size
    }

}