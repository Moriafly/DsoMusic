package com.dirror.music.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.dirror.music.MyApplication
import com.dirror.music.R
import com.dirror.music.data.PLAYLIST_TAG_NORMAL
import com.dirror.music.music.standard.data.StandardSongData
import com.dirror.music.ui.activity.PlayerActivity
import com.dirror.music.ui.dialog.SongMenuDialog
import com.dirror.music.util.Config
import com.dirror.music.util.parseArtist
import com.dirror.music.util.toast

/**
 * 歌单适配器
 */
class DetailPlaylistAdapter
    @JvmOverloads
    constructor(private val songDataList: ArrayList<StandardSongData>,
                private val activity: Activity,
                private val tag: Int? = PLAYLIST_TAG_NORMAL): RecyclerView.Adapter<DetailPlaylistAdapter.ViewHolder>() {

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val tvNumber: TextView = view.findViewById(R.id.tvNumber)
        val clSong: ConstraintLayout = view.findViewById(R.id.clSong)

        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvArtist: TextView = view.findViewById(R.id.tvArtist)
        val ivMore: ImageView = view.findViewById(R.id.ivMore)

        val isAnimation = MyApplication.mmkv.decodeBool(Config.PLAYLIST_SCROLL_ANIMATION, true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        LayoutInflater.from(parent.context).inflate(R.layout.layout_detail_playlist, parent, false).apply {
            return ViewHolder(this)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.apply {
            // 动画
            if (isAnimation) {
                clSong.animation = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.anim_recycle_item)
            }
            // 获取当前 song
            val song = songDataList[position]
            if (song.neteaseInfo?.pl == 0) {
                holder.tvNumber.setTextColor(ContextCompat.getColor(MyApplication.context, R.color.songUnable))
                holder.tvName.setTextColor(ContextCompat.getColor(MyApplication.context, R.color.songUnable))
                holder.tvArtist.setTextColor(ContextCompat.getColor(MyApplication.context, R.color.songUnable))
            } else {
                holder.tvNumber.setTextColor(ContextCompat.getColor(MyApplication.context, R.color.colorSubTextForeground))
                holder.tvName.setTextColor(ContextCompat.getColor(MyApplication.context, R.color.colorTextForeground))
                holder.tvArtist.setTextColor(ContextCompat.getColor(MyApplication.context, R.color.colorSubTextForeground))
            }
            tvNumber.text = (position + 1).toString()
            tvName.text = song.name
            tvArtist.text = song.artists?.let { parseArtist(it) }
            // 点击项目
            clSong.setOnClickListener {
                if (song.neteaseInfo?.pl != 0) {
                    playMusic(position, it)
                } else {
                    toast("网易云暂无版权，可以试试 QQ 和酷我音源")
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
        playMusic(0, null)
    }

    /**
     * 播放音乐
     */
    private fun playMusic(position: Int, view: View?) {
        // 歌单相同
        if (MyApplication.musicBinderInterface?.getPlaylist() == songDataList) {
            // position 相同
            if (position == MyApplication.musicBinderInterface?.getNowPosition()) {
                if (view != null) {
                    view.context.startActivity(Intent(view.context, PlayerActivity::class.java))
                    (view.context as Activity).overridePendingTransition(
                        R.anim.anim_slide_enter_bottom,
                        R.anim.anim_no_anim
                    )
                }
            } else {
                MyApplication.musicBinderInterface?.playMusic(position)
            }
        } else {
            // 设置歌单
            MyApplication.musicBinderInterface?.setPlaylist(songDataList)
            // 播放歌单
            MyApplication.musicBinderInterface?.playMusic(position)
        }
    }

}