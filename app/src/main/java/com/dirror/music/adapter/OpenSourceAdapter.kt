package com.dirror.music.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dirror.music.R
import com.dirror.music.data.OpenSourceData
import com.dirror.music.util.openUrlByBrowser

class OpenSourceAdapter(private val openSourceList: List<OpenSourceData>): RecyclerView.Adapter<OpenSourceAdapter.ViewHolder>() {
    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvUrl: TextView = view.findViewById(R.id.tvUrl)
        val tvLicense: TextView = view.findViewById(R.id.tvLicense)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        LayoutInflater.from(parent.context).inflate(R.layout.layout_open_source_item, parent, false).apply {
            return ViewHolder(this)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvName.text = openSourceList[position].name
        holder.tvUrl.text = openSourceList[position].url
        holder.tvLicense.text = openSourceList[position].license
        holder.tvUrl.setOnClickListener {
            openUrlByBrowser(it.context, openSourceList[position].url)
        }
    }

    override fun getItemCount(): Int {
        return openSourceList.size
    }

}


