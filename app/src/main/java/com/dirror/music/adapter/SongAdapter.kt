package com.dirror.music.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.size.ViewSizeResolver
import coil.transform.RoundedCornersTransformation
import com.dirror.music.MyApp.Companion.mmkv
import com.dirror.music.R
import com.dirror.music.music.standard.data.*
import com.dirror.music.service.playMusic
import com.dirror.music.util.*
import com.dirror.music.util.parse
import com.dso.ext.toArrayList

/**
 * 歌曲适配器
 * @author Moriafly
 */
class SongAdapter(
    private val itemMenuClickedListener: (StandardSongData) -> Unit
) : ListAdapter<StandardSongData, SongAdapter.ViewHolder>(DiffCallback) {

    inner class ViewHolder(view: View, itemMenuClickedListener: (StandardSongData) -> Unit) : RecyclerView.ViewHolder(view) {
        val clSong: ConstraintLayout = view.findViewById(R.id.clSong)
        val ivCover: ImageView = view.findViewById(R.id.ivCover)
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvSub: TextView = view.findViewById(R.id.tvSub)
        private val ivMenu: ImageView = view.findViewById(R.id.ivMenu)
        val ivTag: ImageView = view.findViewById(R.id.ivTag)

        val isAnimation = mmkv.decodeBool(Config.PLAYLIST_SCROLL_ANIMATION, true)

        var songData: StandardSongData? = null

        init {
            ivMenu.setOnClickListener {
                songData?.let { it1 -> itemMenuClickedListener(it1) }
            }
            clSong.setOnLongClickListener {
                songData?.let { it1 -> itemMenuClickedListener(it1) }
                return@setOnLongClickListener true
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        LayoutInflater.from(parent.context).inflate(R.layout.recycler_song, parent, false).apply {
            return ViewHolder(this, itemMenuClickedListener)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            val song = getItem(position)
            songData = song
            // 动画
            if (isAnimation) {
                clSong.animation = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.anim_recycle_item)
            }

            if (song.neteaseInfo?.pl == 0) {
                holder.tvTitle.alpha = 0.25f
                holder.tvSub.alpha = 0.25f
            } else {
                holder.tvTitle.alpha = 1f
                holder.tvSub.alpha = 1f
            }

            if (song.quality() == SONG_QUALITY_HQ) {
                holder.ivTag.visibility = View.VISIBLE
            } else {
                holder.ivTag.visibility = View.GONE
            }

            val imageUrl = when (song.source) {
                SOURCE_NETEASE -> {
                    if (song.imageUrl == "https://p2.music.126.net/6y-UleORITEDbvrOLV0Q8A==/5639395138885805.jpg"
                        || song.imageUrl == "https://p1.music.126.net/6y-UleORITEDbvrOLV0Q8A==/5639395138885805.jpg"
                    ) {
                        ""
                        // "$API_FCZBL_VIP/?type=cover&id=${song.id}"
                    } else {
                        val neteaseUrl = "${song.imageUrl}?param=${100}y${100}"
                        // loge(neteaseUrl, "NeteaseUrl")
                        neteaseUrl
                    }
                }
                SOURCE_QQ -> {
                    "https://y.gtimg.cn/music/photo_new/T002R300x300M000${song.imageUrl}.jpg?max_age=2592000"
                }
                else -> song.imageUrl
            }

            ivCover.load(imageUrl) {
                transformations(RoundedCornersTransformation(dp2px(6f)))
                size(ViewSizeResolver(ivCover))
                error(R.drawable.ic_song_cover)
                crossfade(300)
            }

            tvTitle.text = song.name
            val artist = song.artists?.parse()
            tvSub.text = if (artist.isNullOrEmpty()) {
                "未知"
            } else {
                artist
            }
            // 点击项目
            clSong.setOnClickListener {
                playMusic(it.context, song, currentList.toArrayList())
            }
        }
    }

    /**
     * 播放第一首歌
     */
    fun playFirst() {
        playMusic(null, getItem(0), currentList.toArrayList())
    }

    object DiffCallback : DiffUtil.ItemCallback<StandardSongData>() {
        override fun areItemsTheSame(oldItem: StandardSongData, newItem: StandardSongData): Boolean {
            return oldItem.source == newItem.source && oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: StandardSongData, newItem: StandardSongData): Boolean {
            return oldItem == newItem
        }
    }

}