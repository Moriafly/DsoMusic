package com.dirror.music.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dirror.music.R
import com.dirror.music.music.netease.data.BannerData
import com.youth.banner.adapter.BannerAdapter

class BannerAdapter(private val data: BannerData) : BannerAdapter<BannerData.BannersData, com.dirror.music.adapter.BannerAdapter.BannerViewHolder>(data.banners) {

    inner class BannerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivBackground: ImageView = itemView.findViewById(R.id.ivBackground)
    }

    override fun onCreateHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.dirrorx_banner_layout, parent, false)
        return BannerViewHolder(itemView)
    }


    override fun getData(position: Int): BannerData.BannersData {
        return data.banners[position]
    }

    override fun onBindView(holder: BannerViewHolder, data: BannerData.BannersData, position: Int, size: Int) {
        Glide.with(holder.itemView)
            .load(data.pic)
            .into(holder.ivBackground)
    }

}