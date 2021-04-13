package com.dirror.music.adapter.item

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dirror.music.R

class ItemSearchAdapter(
    private val clickListener: () -> Unit
) : RecyclerView.Adapter<ItemSearchAdapter.ViewHolder>() {

    var text: String = "搜索此列表歌曲"
        set(value) {
            field = value
            notifyItemChanged(0)
        }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTag: TextView = view.findViewById(R.id.tvTag)

        init {
            tvTag.setOnClickListener {
                clickListener()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        LayoutInflater.from(parent.context).inflate(R.layout.recycler_search, parent, false).apply {
            return ViewHolder(this)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.apply {
            tvTag.text = text
        }
    }

    override fun getItemCount(): Int {
        return 1
    }


}