package com.dirror.music.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dirror.music.R
import com.dirror.music.music.netease.BannerUtil
import com.youth.banner.adapter.BannerAdapter

class MusicBannerAdapter(data: List<BannerUtil.BannerData>) : BannerAdapter<BannerUtil.BannerData, BannerViewHolder>(data) {

    private val mData = data
    override fun onCreateHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.dirrorx_banner_layout, parent, false)
        return BannerViewHolder(itemView)
    }

    override fun onBindView(holder: BannerViewHolder, data: BannerUtil.BannerData, position: Int, size: Int) {
        Glide.with(holder.ivBackground.context)
            .load(data.pic)
            .into(holder.ivBackground)

//        holder.itemView.tvTitle.text = data.name
//        holder.itemView.tvSubTitle.text = data.info
    }

    override fun getData(position: Int): BannerUtil.BannerData {
        return mData[position]
    }

}

class BannerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val ivBackground: ImageView = itemView.findViewById(R.id.ivBackground)
}
