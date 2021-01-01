package com.dirror.music.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dirror.music.MyApplication
import com.dirror.music.R
import com.dirror.music.music.netease.data.LastMsgData
import com.dirror.music.music.netease.data.PrivateLetterData
import com.dirror.music.util.GlideUtil
import com.dirror.music.util.dp
import com.google.gson.Gson

/**
 * 私信适配器
 */
class PrivateLetterAdapter(private val privateLetterMsgsData: ArrayList<PrivateLetterData.MsgsData>) : RecyclerView.Adapter<PrivateLetterAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivCover: ImageView = view.findViewById(R.id.ivCover)
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvContent: TextView = view.findViewById(R.id.tvContent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycle_item_private_letter, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val msg = privateLetterMsgsData[position]
        holder.tvName.text = msg.fromUser.nickname
        val picUrl = MyApplication.cloudMusicManager.getPicture(msg.fromUser.avatarUrl, 48.dp())
        GlideUtil.load(picUrl, holder.ivCover)
        holder.tvContent.text = Gson().fromJson(msg.lastMsg, LastMsgData::class.java).msg
    }

    override fun getItemCount(): Int {
        return privateLetterMsgsData.size
    }

}