package com.dirror.music.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.size.ViewSizeResolver
import coil.transform.RoundedCornersTransformation
import com.dirror.music.R
import com.dirror.music.music.netease.data.TopListData
import com.dirror.music.util.dp

class TopListAdapter(private val topListData: TopListData,
                     private val itemClickedListener: (TopListData.ListData) -> Unit,): RecyclerView.Adapter<TopListAdapter.ViewHolder>() {

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private val clTopList: ConstraintLayout = view.findViewById(R.id.clTopList)
        val ivCover: ImageView = view.findViewById(R.id.ivCover)
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvUpdateFrequency: TextView = view.findViewById(R.id.tvUpdateFrequency)

        var item: TopListData.ListData? = null
        init {
            clTopList.setOnClickListener {
                item?.let { itemClickedListener(it)  }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        LayoutInflater.from(parent.context).inflate(R.layout.recycler_top_list, parent, false).apply {
            return ViewHolder(this)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.apply {
            val listData = topListData.list[position]
            item = listData
            ivCover.load(listData.coverImgUrl) {
                transformations(RoundedCornersTransformation(8.dp().toFloat()))
                size(ViewSizeResolver(ivCover))
                error(R.drawable.ic_song_cover)
            }
            tvName.text = listData.name
            tvUpdateFrequency.text = listData.updateFrequency
        }
    }

    override fun getItemCount(): Int {
        return topListData.list.size
    }
}