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
import com.dirror.music.data.PlaylistData
import com.dirror.music.util.dp

/**
 * 我的歌单适配器
 */
class MyPlaylistAdapter
    (private val itemClickListener: (PlaylistData) -> Unit)
    : ListAdapter<PlaylistData, MyPlaylistAdapter.ViewHolder>(DiffCallback) {

    inner class ViewHolder(view: View, itemClickListener: (PlaylistData) -> Unit) : RecyclerView.ViewHolder(view) {
        val clTrack: ConstraintLayout = view.findViewById(R.id.clTrack)
        val ivCover: ImageView = view.findViewById(R.id.ivCover)
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvTrackCount: TextView = view.findViewById(R.id.tvTrackCount)

        val radius = view.context.resources.getDimension(R.dimen.defaultRadius)

        var selectPlaylist: PlaylistData? = null

        init {
            clTrack.setOnClickListener {
                selectPlaylist?.let { it1 -> itemClickListener(it1)}
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_playlist, parent, false)
        return ViewHolder(view, itemClickListener)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            val playlist = getItem(position)
            selectPlaylist = playlist

//            if (position == 0) {
//                clTrack.updateLayoutParams<RecyclerView.LayoutParams> {
//                    height = (68 + 16).dp()
//                }
//                clTrack.background = R.drawable.bg_card_item_top.asDrawable(clTrack.context)
//            } else {
//                clTrack.updateLayoutParams<RecyclerView.LayoutParams> {
//                    height = (68).dp()
//                }
//                clTrack.background = R.drawable.bg_card_item.asDrawable(clTrack.context)
//            }

            val url = MyApp.cloudMusicManager.getPicture(playlist.coverImgUrl, 56.dp())
            ivCover.load(url) {
                allowHardware(false)
                size(ViewSizeResolver(ivCover))
                crossfade(300)
            }
            tvName.text = playlist.name
            tvTrackCount.text = holder.itemView.context.getString(R.string.songs, playlist.trackCount)
        }
    }

    object DiffCallback : DiffUtil.ItemCallback<PlaylistData>() {
        override fun areItemsTheSame(oldItem: PlaylistData, newItem: PlaylistData): Boolean {
            return oldItem.coverImgUrl == newItem.coverImgUrl && oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PlaylistData, newItem: PlaylistData): Boolean {
            return oldItem == newItem
        }
    }

}
