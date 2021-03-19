package com.dirror.music.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.util.Log
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
import com.dirror.music.MyApplication
import com.dirror.music.R
import com.dirror.music.api.API_FCZBL_VIP
import com.dirror.music.data.PLAYLIST_TAG_NORMAL
import com.dirror.music.music.standard.SongPicture
import com.dirror.music.music.standard.data.SOURCE_NETEASE
import com.dirror.music.music.standard.data.SOURCE_QQ
import com.dirror.music.music.standard.data.StandardSongData
import com.dirror.music.ui.activity.PlayerActivity
import com.dirror.music.ui.dialog.SongMenuDialog
import com.dirror.music.util.*

/**
 * 歌适配器
 */
class SongDataAdapter
@JvmOverloads
constructor(
    private val activity: Activity,
    private val tag: Int? = PLAYLIST_TAG_NORMAL
) : ListAdapter<StandardSongData, SongDataAdapter.ViewHolder>(DiffCallback) {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val clSong: ConstraintLayout = view.findViewById(R.id.clSong)
        val viewPlaying: View = view.findViewById(R.id.viewPlaying)
        val ivCover: ImageView = view.findViewById(R.id.ivCover)
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvSub: TextView = view.findViewById(R.id.tvSub)
        val ivMenu: ImageView = view.findViewById(R.id.ivMenu)
        val ivTag: ImageView = view.findViewById(R.id.ivTag)

        val isAnimation = MyApplication.config.mmkv.decodeBool(Config.PLAYLIST_SCROLL_ANIMATION, true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        LayoutInflater.from(parent.context).inflate(R.layout.recycler_song, parent, false).apply {
            return ViewHolder(this)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.apply {
            // 动画
            if (isAnimation) {
                clSong.animation = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.anim_recycle_item)
            }

            if (position > itemCount) {
                return
            }

            val song = getItem(position)

            if (song.neteaseInfo?.pl == 0) {
                holder.tvTitle.alpha = 0.25f
                holder.tvSub.alpha = 0.25f
                // holder.tvNumber.alpha = 0.25f
            } else {
                holder.tvTitle.alpha = 1f
                holder.tvSub.alpha = 1f
                // holder.tvNumber.alpha = 1f
            }

            if (song.neteaseInfo?.pl ?: 0 >= 320000) {
                holder.ivTag.visibility = View.VISIBLE
            } else {
                holder.ivTag.visibility = View.GONE
            }

            loge(song.imageUrl.toString(), "图片")
            val imageUrl = when (song.source) {
                SOURCE_NETEASE -> {
                    if (song.imageUrl == "https://p2.music.126.net/6y-UleORITEDbvrOLV0Q8A==/5639395138885805.jpg"
                        || song.imageUrl == "https://p1.music.126.net/6y-UleORITEDbvrOLV0Q8A==/5639395138885805.jpg") {
                        ""
                        // "$API_FCZBL_VIP/?type=cover&id=${song.id}&param=${40.dp()}y${40.dp()}"
                    } else {
                        song.imageUrl
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
            }

            // tvNumber.text = (position + 1).toString()
            tvTitle.text = song.name //  + song.neteaseInfo?.pl?.toString()
            val artist = song.artists?.parse()
            tvSub.text = if (artist.isNullOrEmpty()) {
                "未知"
            } else {
                artist
            }
            // 点击项目
            clSong.setOnClickListener {
                if (song.neteaseInfo?.pl != 0) {
                    playMusic(song, it)
                } else {
                    toast("网易云暂无版权或者是 VIP 歌曲，可以试试 QQ 和酷我音源")
                }
            }
            // 更多点击，每首歌右边的三点菜单
            ivMenu.setOnClickListener {
                showSongMenuDialog(song, it)
            }
            // 长按
            clSong.setOnLongClickListener {
                showSongMenuDialog(song, it)
                return@setOnLongClickListener true
            }
        }
    }

    private fun showSongMenuDialog(songData: StandardSongData, view: View) {
        if (tag != null) {
            SongMenuDialog(view.context, activity, songData, tag).show()
        }
    }

    /**
     * 播放第一首歌
     */
    fun playFirst() {
        playMusic(getItem(0), null)
    }

    /**
     * 播放音乐
     */
    private fun playMusic(songData: StandardSongData, view: View?) {
        // 歌单相同
        if (MyApplication.musicController.value?.getPlaylist() == currentList) {
            // position 相同
            if (songData == MyApplication.musicController.value?.getPlayingSongData()?.value) {
                if (view != null) {
                    view.context.startActivity(Intent(view.context, PlayerActivity::class.java))
                    (view.context as Activity).overridePendingTransition(
                        R.anim.anim_slide_enter_bottom,
                        R.anim.anim_no_anim
                    )
                }
            } else {
                MyApplication.musicController.value?.playMusic(songData)
            }
        } else {
            // 设置歌单
            MyApplication.musicController.value?.setPlaylist(currentList.toArrayList())
            // 播放歌单
            MyApplication.musicController.value?.playMusic(songData)
        }
    }

    object DiffCallback : DiffUtil.ItemCallback<StandardSongData>() {
        override fun areItemsTheSame(oldItem: StandardSongData, newItem: StandardSongData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: StandardSongData, newItem: StandardSongData): Boolean {
            return oldItem == newItem
        }
    }

}