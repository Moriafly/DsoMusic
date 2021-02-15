package com.dirror.music.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.size.ViewSizeResolver
import coil.transform.CircleCropTransformation
import coil.transform.RoundedCornersTransformation
import com.dirror.music.R
import com.dirror.music.music.netease.data.DailyRecommendSongData
import com.dirror.music.util.dp2px
import com.dirror.music.util.parseArtist

class DailyRecommendSongAdapter(private val dailyRecommendSongData: DailyRecommendSongData): RecyclerView.Adapter<DailyRecommendSongAdapter.ViewHolder>() {

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvName)
        val ivCover: ImageView = view.findViewById(R.id.ivCover)
        val tvTag: TextView = view.findViewById(R.id.tvTag)
        val tvArtist: TextView = view.findViewById(R.id.tvArtist)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        LayoutInflater.from(parent.context).inflate(R.layout.recycler_daily_recommend_song, parent, false).apply {
            return ViewHolder(this)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.apply {
            val data = dailyRecommendSongData.data.dailySongs[position]
            ivCover.load(data.al.picUrl + "?param=100y100") {
                // transformations(CircleCropTransformation())
                transformations(RoundedCornersTransformation(dp2px(6f)))
                size(ViewSizeResolver(ivCover))
                error(R.drawable.ic_song_cover)
            }
            tvName.text = data.name
            tvArtist.text = parseArtist(data.ar)
            tvTag.text = data.reason
        }
    }

    override fun getItemCount(): Int {
        return dailyRecommendSongData.data.dailySongs.size
    }

}