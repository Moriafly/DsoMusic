package com.dirror.music.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.dirror.music.MyApp
import com.dirror.music.R
import com.dirror.music.music.standard.data.StandardSongData
import com.dirror.music.util.parseArtist

/**
 * 播放列表适配器
 */
class PlaylistDialogAdapter(private val list: ArrayList<StandardSongData>): RecyclerView.Adapter<PlaylistDialogAdapter.ViewHolder>() {

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvArtist: TextView = view.findViewById(R.id.tvArtist)
        val clSong: ConstraintLayout = view.findViewById(R.id.clSong)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        LayoutInflater.from(parent.context).inflate(R.layout.layout_playlist_dialog, parent, false).apply {
            return ViewHolder(this)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val lightSongData = MyApp.musicController.value?.getPlayingSongData()?.value
        val songData = list[position]
        if (songData == lightSongData) {
            holder.tvName.setTextColor(ContextCompat.getColor(holder.tvName.context, R.color.colorAppThemeColor))
            holder.tvArtist.setTextColor(ContextCompat.getColor(holder.tvName.context, R.color.colorAppThemeColor))
        } else {
            holder.tvName.setTextColor(ContextCompat.getColor(holder.tvName.context, R.color.colorTextForeground))
            holder.tvArtist.setTextColor(ContextCompat.getColor(holder.tvName.context, R.color.colorTextForeground))
        }

        holder.tvName.text = songData.name
        holder.tvArtist.text = list[position].artists?.let { parseArtist(it) }
        holder.clSong.setOnClickListener {
            MyApp.musicController.value?.playMusic(songData)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

}