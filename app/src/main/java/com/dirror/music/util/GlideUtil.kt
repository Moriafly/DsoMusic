package com.dirror.music.util

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.dirror.music.MyApplication

object GlideUtil {
    fun load(url: String, imageView: ImageView) {
        Glide.with(MyApplication.context)
            .load(url)
            .into(imageView)
    }
}