package com.dirror.music.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dirror.music.R
import com.dirror.music.cloudmusic.PlaylistData
import com.dirror.music.cloudmusic.UserPlaylistData
import com.dirror.music.ui.activity.PlaylistActivity
import kotlinx.android.synthetic.main.layout_playlist.view.*

class PlaylistAdapter(private val playlist: List<PlaylistData>): RecyclerView.Adapter<PlaylistAdapter.ViewHolder>() {

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val clTrack: ConstraintLayout = view.findViewById(R.id.clTrack)
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
        holder.clTrack.setOnClickListener {
            val intent = Intent(it.context, PlaylistActivity::class.java)
            intent.putExtra("long_playlist_id", play.id)
            it.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return playlist.size
    }
}
