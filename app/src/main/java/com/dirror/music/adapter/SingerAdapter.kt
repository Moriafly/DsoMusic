package com.dirror.music.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.size.ViewSizeResolver
import com.dirror.music.App
import com.dirror.music.R
import com.dirror.music.music.standard.data.StandardSinger
import com.dirror.music.util.dp

class SingerAdapter (private val itemClickListener: (StandardSinger) -> Unit)
    : ListAdapter<StandardSinger, SingerAdapter.ViewHolder>(DiffCallback) {

    inner class ViewHolder(view: View, itemClickListener: (StandardSinger) -> Unit) : RecyclerView.ViewHolder(view) {
        private val clTrack: ConstraintLayout = view.findViewById(R.id.clTrack)
        val ivCover: ImageView = view.findViewById(R.id.ivCover)
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tips: TextView = view.findViewById(R.id.tips)
        val radius = view.context.resources.getDimension(R.dimen.defaultRadius)

        var selectPlaylist: StandardSinger? = null

        init {
            clTrack.setOnClickListener {
                selectPlaylist?.let { it1 -> itemClickListener(it1)}
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_album, parent, false)
        return ViewHolder(view, itemClickListener)
    }

    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            val singer = getItem(position)
            selectPlaylist = singer

            val url = App.cloudMusicManager.getPicture(singer.picUrl, 80.dp())
            ivCover.load(url) {
                allowHardware(false)
                size(ViewSizeResolver(ivCover))
                crossfade(300)
            }
            tvName.text = singer.name
        }
    }

    object DiffCallback : DiffUtil.ItemCallback<StandardSinger>() {
        override fun areItemsTheSame(oldItem: StandardSinger, newItem: StandardSinger): Boolean {
            return oldItem.picUrl == newItem.picUrl && oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: StandardSinger, newItem: StandardSinger): Boolean {
            return oldItem == newItem
        }
    }

}