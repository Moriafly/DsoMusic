package com.dirror.music.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.dirror.music.MyApplication
import com.dirror.music.R
import com.dirror.music.data.PlaylistData
import com.dirror.music.music.standard.data.SOURCE_NETEASE
import com.dirror.music.ui.activity.PlaylistActivity
import com.dirror.music.util.GlideUtil
import com.dirror.music.util.dp
// Rename the Pair class from the Android framework to avoid a name clash
import android.util.Pair as UtilPair

/**
 * 我的歌单适配器
 */
class PlaylistAdapter(private val playlist: ArrayList<PlaylistData>, val activity: Activity) : RecyclerView.Adapter<PlaylistAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val clTrack: ConstraintLayout = view.findViewById(R.id.clTrack)
        val ivCover: ImageView = view.findViewById(R.id.ivCover)
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvTrackCount: TextView = view.findViewById(R.id.tvTrackCount)
        val cvCover: CardView = view.findViewById(R.id.cvCover)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_playlist, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val play = playlist[position]
        val url = MyApplication.cloudMusicManager.getPicture(play.coverImgUrl, 56.dp())
        GlideUtil.load(url, holder.ivCover)
        holder.tvName.text = play.name
        holder.tvTrackCount.text = holder.itemView.context.getString(R.string.songs, play.trackCount)
        holder.clTrack.setOnClickListener {
            val intent = Intent(it.context, PlaylistActivity::class.java)

            // val options = ActivityOptions.makeSceneTransitionAnimation(activity, holder.cvCover, "playlistCover")
            val options = ActivityOptions.makeSceneTransitionAnimation(activity,
                    // UtilPair.create(holder.ivCover, "transIvCover"),
                    // UtilPair.create(holder.cvCover, "transCvCover"),
                    )
            intent.putExtra(PlaylistActivity.EXTRA_PLAYLIST_SOURCE, SOURCE_NETEASE)
            intent.putExtra(PlaylistActivity.EXTRA_LONG_PLAYLIST_ID, play.id)
            it.context.startActivity(intent, options.toBundle())
        }
    }

    override fun getItemCount(): Int {
        return playlist.size
    }

}
