package com.dirror.music.util

import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.dirror.music.MyApplication

object GlideUtil {
    fun load(url: String, imageView: ImageView) {
        Glide.with(MyApplication.context)
            .load(url)
            .into(imageView)
    }

    fun load(url: String, imageView: ImageView, placeHolder: Drawable) {
        Glide.with(MyApplication.context)
            .load(url)
            .placeholder(placeHolder)
            .into(imageView)
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
}