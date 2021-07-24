package com.dirror.music.data

import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import com.dirror.music.R

enum class SearchType {

    SINGLE,SINGER,ALBUM,PLAYLIST;

    companion object {
        fun getSearchType(@IdRes idRes: Int):SearchType {
            return when (idRes) {
                R.id.search_type_singer -> SINGER
                R.id.search_type_album -> ALBUM
                R.id.search_type_playlist -> PLAYLIST
                R.id.search_type_single -> SINGLE
                else -> SINGLE
            }
        }

        @DrawableRes
        fun getIconRes(type: SearchType): Int {
            return when(type) {
                SINGLE -> R.drawable.ic_baseline_music_single_24
                SINGER -> R.drawable.ic_baseline_singer_24
                ALBUM -> R.drawable.ic_baseline_album_24
                PLAYLIST -> R.drawable.ic_baseline_playlist_24
            }
        }

        fun getSearchTypeInt(type: SearchType): Int {
            return when(type) {
                SINGLE -> 1
                ALBUM -> 10
                SINGER -> 100
                PLAYLIST -> 1000
            }
        }
    }



}