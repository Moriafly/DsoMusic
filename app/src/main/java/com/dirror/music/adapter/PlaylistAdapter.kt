package com.dirror.music.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dirror.music.R
import com.dirror.music.cloudmusic.PlaylistData
import com.dirror.music.cloudmusic.UserPlaylistData
import kotlinx.android.synthetic.main.layout_playlist.view.*

class PlaylistAdapter(val playlist: List<PlaylistData>): RecyclerView.Adapter<PlaylistAdapter.ViewHolder>() {

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val ivCover: ImageView = view.findViewById(R.id.ivCover)
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvTrackCount: TextView = view.findViewById(R.id.tvTrackCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        LayoutInflater.from(parent.context).inflate(R.layout.layout_playlist, parent, false).apply {
            return ViewHolder(this)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val play = playlist[position]
        var url = play.coverImgUrl
        url = url.replace("http", "https")
        Log.e("图片", "$url")
        Glide.with(holder.ivCover.context)
            .load(url)
            // .placeholder(R.drawable.photo_placeholder)
            .into(holder.ivCover)
        holder.tvName.text = play.name
        holder.tvTrackCount.text = "${play.trackCount} 首"
    }

    override fun getItemCount(): Int {
        return playlist.size
    }
}
