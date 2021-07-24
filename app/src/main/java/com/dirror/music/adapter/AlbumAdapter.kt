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
import com.dirror.music.MyApp
import com.dirror.music.R
import com.dirror.music.music.standard.data.StandardAlbum
import com.dirror.music.util.dp
import java.text.SimpleDateFormat
import java.util.*

class AlbumAdapter (private val itemClickListener: (StandardAlbum) -> Unit)
    : ListAdapter<StandardAlbum, AlbumAdapter.ViewHolder>(DiffCallback) {

    inner class ViewHolder(view: View, itemClickListener: (StandardAlbum) -> Unit) : RecyclerView.ViewHolder(view) {
        private val clTrack: ConstraintLayout = view.findViewById(R.id.clTrack)
        val ivCover: ImageView = view.findViewById(R.id.ivCover)
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tips: TextView = view.findViewById(R.id.tips)
        val radius = view.context.resources.getDimension(R.dimen.defaultRadius)

        var selectPlaylist: StandardAlbum? = null

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
            val album = getItem(position)
            selectPlaylist = album

            val url = MyApp.cloudMusicManager.getPicture(album.picUrl, 80.dp())
            ivCover.load(url) {
                allowHardware(false)
                size(ViewSizeResolver(ivCover))
                crossfade(300)
            }
            tvName.text = album.name
            val date = SimpleDateFormat("yyyy-MM").format(Date(album.publishTime))
            "${album.artName}・共${album.size}首・${date}发布".also { tips.text = it }
        }
    }

    object DiffCallback : DiffUtil.ItemCallback<StandardAlbum>() {
        override fun areItemsTheSame(oldItem: StandardAlbum, newItem: StandardAlbum): Boolean {
            return oldItem.picUrl == newItem.picUrl && oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: StandardAlbum, newItem: StandardAlbum): Boolean {
            return oldItem == newItem
        }
    }

}