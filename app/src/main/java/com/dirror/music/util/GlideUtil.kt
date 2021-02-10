package com.dirror.music.util

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.dirror.music.MyApplication

object GlideUtil {

    fun load(url: String, imageView: ImageView) {
        runOnMainThread {
            Glide.with(MyApplication.context)
                .load(url)
                .into(imageView)
        }
    }

    fun load(url: String, imageView: ImageView, size: Int) {
        runOnMainThread {
            Glide.with(MyApplication.context)
                .load(url)
                .override(size)
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

    fun load(url: String, success: (Bitmap) -> Unit) {
            Glide.with(MyApplication.context)
                .asBitmap()
                .load(url)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        loge("获取成功")
                        success.invoke(resource)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {

                    }
                })
    }

    @Deprecated("过时方法")
    fun loadCloudMusicImage(url: String, width: Int, height: Int, imageView: ImageView) {
        val imageUrl = "$url?param=${width}y${height}"
        load(imageUrl, imageView)
    }

    /**
     * 加载圆形图片
     */
    fun loadCircle(url: String, imageView: ImageView) {
        Glide.with(MyApplication.context)
            .load(url)
            .apply(RequestOptions.bitmapTransform(CircleCrop()))
            .into(imageView)
    }

    fun loadCircle(url: String, imageView: ImageView, needSize: Int) {
        Glide.with(MyApplication.context)
            .load(url)
            .override(needSize)
            .apply(RequestOptions.bitmapTransform(CircleCrop()))
            .into(imageView)
    }

    /**
     *
     */
    fun loadCircle(url: String, imageView: ImageView, dpSize: Int, needSize: Int) {
        Glide.with(MyApplication.context)
            .load(url)
            .apply(RequestOptions()
                .transforms(CenterCrop(), RoundedCorners(dpSize)
                ))
            .into(imageView)

    }

}