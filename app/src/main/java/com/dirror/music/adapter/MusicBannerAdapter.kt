package com.dirror.music.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dirror.music.R
import com.dirror.music.music.BannerUtil
import com.youth.banner.adapter.BannerAdapter


class MusicBannerAdapter(val data: ArrayList<BannerUtil.BannerData>): BannerAdapter<BannerUtil.BannerData, MusicBannerAdapter.ViewHolder>(data) {

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val ivBackground: ImageView = view.findViewById(R.id.ivBackground)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindView(holder: ViewHolder?, data: BannerUtil.BannerData?, position: Int, size: Int) {
        if (holder != null) {
            Glide.with(holder.ivBackground.context)
                .load(this.data[position].pic)
                // .placeholder(R.drawable.photo_placeholder)
                .into(holder.ivBackground)
        }
    }

    override fun onCreateHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            LayoutInflater.from(parent.context).inflate(R.layout.dirrorx_banner_layout, parent, false).apply {
                return ViewHolder(this)
            }
    }
}