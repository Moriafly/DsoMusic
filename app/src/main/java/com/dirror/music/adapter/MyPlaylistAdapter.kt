package com.dirror.music.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.size.ViewSizeResolver
import com.dirror.music.MyApplication
import com.dirror.music.R
import com.dirror.music.data.PlaylistData
import com.dirror.music.ui.playlist.SongPlaylistActivity
import com.dirror.music.ui.playlist.TAG_NETEASE
import com.dirror.music.util.extensions.dp

/**
 * 我的歌单适配器
 */
class MyPlaylistAdapter(private val playlist: ArrayList<PlaylistData>, val activity: Activity) : RecyclerView.Adapter<MyPlaylistAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val clTrack: ConstraintLayout = view.findViewById(R.id.clTrack)
        val ivCover: ImageView = view.findViewById(R.id.ivCover)
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvTrackCount: TextView = view.findViewById(R.id.tvTrackCount)

        val radius = view.context.resources.getDimension(R.dimen.defaultRadius)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_playlist, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            val play = playlist[position]
            val url = MyApplication.cloudMusicManager.getPicture(play.coverImgUrl, 56.dp())
            ivCover.load(url) {
                allowHardware(false)
                size(ViewSizeResolver(ivCover))
                crossfade(300)
            }
            tvName.text = play.name
            tvTrackCount.text = holder.itemView.context.getString(R.string.songs, play.trackCount)
            clTrack.setOnClickListener {
                val intent = Intent(it.context, SongPlaylistActivity::class.java)
                intent.putExtra(SongPlaylistActivity.EXTRA_TAG, TAG_NETEASE)
                intent.putExtra(SongPlaylistActivity.EXTRA_PLAYLIST_ID, play.id.toString())
                it.context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return playlist.size
    }

}
