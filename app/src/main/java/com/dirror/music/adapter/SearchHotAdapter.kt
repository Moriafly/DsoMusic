package com.dirror.music.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.dirror.music.R
import com.dirror.music.music.netease.data.SearchHotData
import com.dirror.music.util.dp


class SearchHotAdapter(private val searchHotData: SearchHotData): RecyclerView.Adapter<SearchHotAdapter.ViewHolder>(),
    View.OnClickListener {

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val tvNumber: TextView = view.findViewById(R.id.tvNumber)
        val tvText: TextView = view.findViewById(R.id.tvText)
        val tvContent: TextView = view.findViewById(R.id.tvContent)
        val tvScore: TextView = view.findViewById(R.id.tvScore)
        val tvHot: TextView = view.findViewById(R.id.tvHot)
        val clItem: ConstraintLayout = view.findViewById(R.id.clItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        LayoutInflater.from(parent.context).inflate(R.layout.recycle_item_search_hot, parent, false).apply {
            setOnClickListener(this@SearchHotAdapter)
            return ViewHolder(this)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position in 0..2) {
            holder.tvNumber.setTextColor(ContextCompat.getColor(holder.tvNumber.context, R.color.colorAppThemeColor))
        } else {
            holder.tvNumber.setTextColor(ContextCompat.getColor(holder.tvNumber.context, R.color.colorSubTextForeground))
        }
        val data = searchHotData.data[position]
        holder.apply {
            tvNumber.text = (position + 1).toString()
            tvText.text = data.searchWord
            tvContent.text = data.content
            tvScore.text = data.score.toString()
            clItem.tag = position
            tvHot.visibility = if (data.iconType == 1) {
                View.VISIBLE
            } else {
                View.INVISIBLE
            }
        }
    }


    override fun getItemCount(): Int {
        return searchHotData.data.size
    }

    interface OnItemClick {
        fun onItemClick(view: View?, position: Int)
    }

    private var onItemClick: OnItemClick? = null

    fun setOnItemClick(onItemClick: OnItemClick?) {
        this.onItemClick = onItemClick
    }

    override fun onClick(v: View) {
        if (onItemClick != null) {
            onItemClick!!.onItemClick(v, v.tag as Int)
        }
    }

}