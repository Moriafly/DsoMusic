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
import androidx.recyclerview.widget.RecyclerView
import com.dirror.music.MyApplication
import com.dirror.music.R
import com.dirror.music.music.standard.data.StandardSongData
import com.dirror.music.ui.activity.PlayerActivity
import com.dirror.music.ui.dialog.SongMenuDialog
import com.dirror.music.util.parseArtist

/**
 * 歌单适配器
 */
class DetailPlaylistAdapter(private val songDataList: ArrayList<StandardSongData>, private val activity: Activity): RecyclerView.Adapter<DetailPlaylistAdapter.ViewHolder>() {

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val tvNumber: TextView = view.findViewById(R.id.tvNumber)
        val clSong: ConstraintLayout = view.findViewById(R.id.clSong)

        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvArtist: TextView = view.findViewById(R.id.tvArtist)
        val ivMore: ImageView = view.findViewById(R.id.ivMore)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        LayoutInflater.from(parent.context).inflate(R.layout.layout_detail_playlist, parent, false).apply {
            return ViewHolder(this)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.apply {
            // 动画
            clSong.animation = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.anim_recycle_item)
            // 获取当前 song
            val song = songDataList[position]
            // 1 是要 vip，0 不一定（无语）
//        if (song.neteaseInfo?.fee == 1) {
//            holder.tvNumber.setTextColor(MyApplication.context.getColor(R.color.songUnable))
//            holder.tvName.setTextColor(MyApplication.context.getColor(R.color.songUnable))
//            holder.tvArtist.setTextColor(MyApplication.context.getColor(R.color.songUnable))
//        }
            tvNumber.text = (position + 1).toString()
            tvName.text = song.name
            tvArtist.text = song.artists?.let { parseArtist(it) }
            // 点击项目
            clSong.setOnClickListener {
                playMusic(position, it)
            }
            // 更多点击，每首歌右边的三点菜单
            ivMore.setOnClickListener {
                SongMenuDialog(it.context).apply {
                    setSongData(song)
                    setActivity(activity)
                    show()
                }
            }
            // 长按
            clSong.setOnLongClickListener {
                SongMenuDialog(it.context).apply {
                    setSongData(song)
                    setActivity(activity)
                    show()
                }
                return@setOnLongClickListener true
            }
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
        // 空歌单跳出
//        if (MyApplication.musicBinderInterface?.getPlaylist() == null) {
//            return
//        }
        // 歌单相同
        if (MyApplication.musicBinderInterface?.getPlaylist() == songDataList) {
            // toast("歌单相同")
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