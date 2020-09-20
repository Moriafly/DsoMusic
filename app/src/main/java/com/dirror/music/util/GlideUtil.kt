package com.dirror.music.util

import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.dirror.music.MyApplication
import com.dirror.music.api.API_FCZBL_VIP

object GlideUtil {
    fun load(url: String, imageView: ImageView) {
        runOnMainThread {
            Glide.with(MyApplication.context)
                .load(url)
                .into(imageView)
        }
    }

    fun load(url: String, imageView: ImageView, placeHolder: Drawable) {
        runOnMainThread {
            Glide.with(MyApplication.context)
                .load(url)
                .placeholder(placeHolder)
                .into(imageView)
        }
    }

    fun load(url: String, imageView: ImageView, placeHolderImageView: ImageView) {
        if (placeHolderImageView.drawable != null) {
            Glide.with(MyApplication.context)
                .load(url)
                .placeholder(placeHolderImageView.drawable)
                .into(imageView)
        } else {
            Glide.with(MyApplication.context)
                .load(url)
                .into(imageView)
        }
    }

    fun loadCloudMusicImage(url: String, width: Int, height: Int, imageView: ImageView) {
        val imageUrl = "$url?param=${width}y${height}"
        load(imageUrl, imageView)
    }

    /**
     * 加载音乐封面
     */
    fun loadMusicCover(musicId: Long, imageView: ImageView) {
        val imageUrl = "${API_FCZBL_VIP}/?type=single&id=$musicId"
        load(imageUrl, imageView)
    }
}