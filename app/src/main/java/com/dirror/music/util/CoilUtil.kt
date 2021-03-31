package com.dirror.music.util

import android.content.Context
import android.graphics.Bitmap
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import coil.imageLoader
import coil.request.ImageRequest
import com.dirror.music.R
import com.dirror.music.util.extensions.dp

object CoilUtil {

//    fun load(context: Context, url: String, size: Int,success: (bitmap: Bitmap) -> Unit) {
//        val request = ImageRequest.Builder(context)
//            .size(size)
//            .data(url)
//            .error(R.drawable.ic_song_cover)
//            .target(
//                onStart = {
//                    // Handle the placeholder drawable.
//                },
//                onSuccess = { result ->
//                    success(result.toBitmap())
//                },
//                onError = { _ ->
//                    ContextCompat.getDrawable(context, R.drawable.ic_song_cover)?.let { it1 ->
//                        success(it1.toBitmap(size, size))
//                    }
//                }
//            )
//            .build()
//        context.imageLoader.enqueue(request)
//    }

    fun load(context: Context, url: String, success: (bitmap: Bitmap) -> Unit) {
        val request = ImageRequest.Builder(context)
            .data(url)
            // .size(256.dp())
            .error(R.drawable.ic_song_cover)
            .target(
                onStart = {
                    // Handle the placeholder drawable.
                },
                onSuccess = { result ->
                    success.invoke(result.toBitmap())
                },
                onError = { _ ->
                    ContextCompat.getDrawable(context, R.drawable.ic_song_cover)?.let { it1 ->
                        success.invoke(it1.toBitmap(128.dp(), 128.dp()))
                    }
                }
            )
            .build()
        context.imageLoader.enqueue(request)
    }

}