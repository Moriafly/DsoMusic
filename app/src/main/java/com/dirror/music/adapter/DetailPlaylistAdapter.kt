package com.dirror.music.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.size.ViewSizeResolver
import coil.transform.RoundedCornersTransformation
import com.dirror.music.MyApplication
import com.dirror.music.R
import com.dirror.music.data.PLAYLIST_TAG_HISTORY
import com.dirror.music.data.PLAYLIST_TAG_NORMAL
import com.dirror.music.music.standard.data.SOURCE_LOCAL
import com.dirror.music.music.standard.data.StandardSongData
import com.dirror.music.ui.activity.PlayerActivity
import com.dirror.music.ui.dialog.SongMenuDialog
import com.dirror.music.util.Config
import com.dirror.music.util.dp2px
import com.dirror.music.util.parseArtist
import com.dirror.music.util.toast

/**
 * 歌适配器
 */
class DetailPlaylistAdapter
    @JvmOverloads
    constructor(private val songDataList: ArrayList<StandardSongData>,
                private val activity: Activity,
                private val tag: Int? = PLAYLIST_TAG_NORMAL): RecyclerView.Adapter<DetailPlaylistAdapter.ViewHolder>() {

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {

        val ivCover: ImageView = view.findViewById(R.id.ivCover)
        val tvNumber: TextView = view.findViewById(R.id.tvNumber)
        val clSong: ConstraintLayout = view.findViewById(R.id.clSong)

        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvArtist: TextView = view.findViewById(R.id.tvArtist)
        val ivMore: ImageView = view.findViewById(R.id.ivMore)
        val ivHq: ImageView = view.findViewById(R.id.ivHq)

        val isAnimation = MyApplication.mmkv.decodeBool(Config.PLAYLIST_SCROLL_ANIMATION, true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        LayoutInflater.from(parent.context).inflate(R.layout.layout_detail_playlist, parent, false).apply {
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
            // 获取当前 song
            val song = songDataList[position]

            if (song.neteaseInfo?.pl == 0) {
                holder.tvName.alpha = 0.25f
                holder.tvArtist.alpha = 0.25f
                holder.tvNumber.alpha = 0.25f
            } else {
                holder.tvName.alpha = 1f
                holder.tvArtist.alpha = 1f
                holder.tvNumber.alpha = 1f
            }

            if (song.neteaseInfo?.pl?: 0 >= 320000) {
                holder.ivHq.visibility = View.VISIBLE
            } else {
                holder.ivHq.visibility = View.GONE
            }

            if (song.source == SOURCE_LOCAL &&
                tag == PLAYLIST_TAG_NORMAL && tag != PLAYLIST_TAG_HISTORY) {
                ivCover.visibility = View.VISIBLE
                ivCover.load(song.imageUrl) {
                    transformations(RoundedCornersTransformation(dp2px(6f)))
                    size(ViewSizeResolver(ivCover))
                    error(R.drawable.ic_song_cover)
                }
                (clSong.layoutParams as RecyclerView.LayoutParams).apply {
                    marginStart = dp2px(8f).toInt()
                }
                (tvName.layoutParams as ConstraintLayout.LayoutParams).apply {
                    marginStart = dp2px(4f).toInt()
                }
                (tvArtist.layoutParams as ConstraintLayout.LayoutParams).apply {
                    marginStart = dp2px(4f).toInt()
                }
            } else {
                ivCover.visibility = View.INVISIBLE
                (clSong.layoutParams as RecyclerView.LayoutParams).apply {
                    marginStart = dp2px(0f).toInt()
                }
                (tvName.layoutParams as ConstraintLayout.LayoutParams).apply {
                    marginStart = dp2px(0f).toInt()
                }
                (tvArtist.layoutParams as ConstraintLayout.LayoutParams).apply {
                    marginStart = dp2px(0f).toInt()
                }
            }

            tvNumber.text = (position + 1).toString()
            tvName.text = song.name //  + song.neteaseInfo?.pl?.toString()
            tvArtist.text = song.artists?.let { parseArtist(it) }
            // 点击项目
            clSong.setOnClickListener {
                if (song.neteaseInfo?.pl != 0) {
                    playMusic(song, it)
                } else {
                    toast("网易云暂无版权或者是 VIP 歌曲，可以试试 QQ 和酷我音源")
                }
            }
            // 更多点击，每首歌右边的三点菜单
            ivMore.setOnClickListener {
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

    override fun getItemCount(): Int {
        return songDataList.size
    }

    /**
     * 播放第一首歌
     */
    fun playFirst() {
        playMusic(songDataList[0], null)
    }

    /**
     * 播放音乐
     */
    private fun playMusic(songData: StandardSongData, view: View?) {
        // 歌单相同
        if (MyApplication.musicController.value?.getPlaylist() == songDataList) {
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
            MyApplication.musicController.value?.setPlaylist(songDataList)
            // 播放歌单
            MyApplication.musicController.value?.playMusic(songData)
        }
    }

}