package com.dirror.music.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.PixelFormat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import coil.imageLoader
import coil.request.ImageRequest
import com.dirror.music.App
import com.dirror.music.R
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

object CoilUtil {

    fun load(context: Context, data: Any, success: (bitmap: Bitmap) -> Unit) {

        val request = ImageRequest.Builder(App.context)
            .data(data)
            // .size(256.dp())
            .allowHardware(false)
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

    fun load(data: Any, success: (bitmap: Bitmap) -> Unit) {
        // Coil
//        val request = ImageRequest.Builder(context)
//            .data(url)
//            .target(
//                onStart = { placeholder ->
//                    // Handle the placeholder drawable.
//                },
//                onSuccess = { result ->
//                    // Handle the successful result.
//                },
//                onError = { error ->
//                    // Handle the error drawable.
//                }
//            )
//            .build()
//        context.imageLoader.enqueue(request)

        GlobalScope.launch {
            val request = ImageRequest.Builder(App.context)
                .data(data)
                .allowHardware(false)
                .build()
            val drawable = App.context.imageLoader.execute(request).drawable
            drawable?.let {
                success(drawable.toBitmap(
                    drawable.intrinsicWidth,
                    drawable.intrinsicHeight,
                    if (drawable.getOpacity() != PixelFormat.OPAQUE) {
                        Bitmap.Config.ARGB_8888
                    } else {
                        Bitmap.Config.RGB_565
                    }))
            }

        }
    }

}