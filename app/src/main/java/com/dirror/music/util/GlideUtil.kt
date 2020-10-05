package com.dirror.music.util

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.core.view.drawToBitmap
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.dirror.music.MyApplication
import com.dirror.music.api.API_FCZBL_VIP
import jp.wasabeef.glide.transformations.BlurTransformation


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

    fun loadPlayerBackground(url: String, imageView: ImageView, placeHolderImageView: ImageView) {
        runOnMainThread {
            Glide.with(MyApplication.context)
                // .load(R.color.colorBackground)
                .load(url)
                .placeholder(placeHolderImageView.drawable)
                .apply(RequestOptions.bitmapTransform(BlurTransformation(20, 10)))
                .into(imageView)
        }
    }

    fun loadPlayerBackground(url: String, imageView: ImageView) {
        runOnMainThread {
            Glide.with(MyApplication.context)
                // .load(R.color.colorBackground)
                .load(url)
                .apply(RequestOptions.bitmapTransform(BlurTransformation(20, 10)))
                .into(imageView)
        }
    }

}