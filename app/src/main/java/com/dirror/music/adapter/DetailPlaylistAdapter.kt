package com.dirror.music.adapter

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dirror.music.MyApplication
import com.dirror.music.R
import com.dirror.music.cloudmusic.DetailPlaylistData
import com.dirror.music.cloudmusic.PlaylistData
import com.dirror.music.cloudmusic.SongData
import com.dirror.music.cloudmusic.TracksData
import com.dirror.music.service.MusicService
import com.dirror.music.ui.activity.PlaylistActivity

class DetailPlaylistAdapter(val songData: List<SongData>): RecyclerView.Adapter<DetailPlaylistAdapter.ViewHolder>() {

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val tvNumber: TextView = view.findViewById(R.id.tvNumber)
        val clSong: ConstraintLayout = view.findViewById(R.id.clSong)

        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvArtist: TextView = view.findViewById(R.id.tvArtist)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        LayoutInflater.from(parent.context).inflate(R.layout.layout_detail_playlist, parent, false).apply {
            return ViewHolder(this)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        var url = songData[position].songs[0].al.picUrl
//        url = url.replace("http", "https")
//        Log.e("图片", "$url")
//        Glide.with(holder.ivCover.context)
//            .load(url)
//            // .placeholder(R.drawable.photo_placeholder)
//            .into(holder.ivCover)
        val song = songData[position].songs[0]
        holder.tvName.text = song.name
        var artist = ""
        for (artistName in 0..song.ar.lastIndex) {
            if (artistName != 0) {
                artist += " / "
            }
            artist += song.ar[artistName].name

        }
        holder.tvArtist.text = artist

        holder.clSong.setOnClickListener {
            MyApplication.musicBinderInterface?.setPlaylist(songData)
            MyApplication.musicBinderInterface?.playMusic(position)
        }

        holder.tvNumber.text = (position + 1).toString()
    }

    override fun getItemCount(): Int {
        return songData.size
    }
}