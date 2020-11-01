package com.dirror.music.util

import android.content.Context
import androidx.core.content.edit
import com.dirror.music.MyApplication

object StorageUtil {
    private val context = MyApplication.context

    private const val DATA_NAME = "foyou"

    const val CLOUD_MUSIC_UID = "long_cloud_music_uid" // 用户 uid
    const val PlAY_MODE = "int_play_mode" // 播放模式
    const val LANGUAGE = "int_language" // 语言

    fun putInt(singleName: String, value: Int) {
        context.getSharedPreferences(DATA_NAME, Context.MODE_PRIVATE)
            .edit{ putInt(singleName, value) }
    }

    fun getInt(singleName: String, defaultValue: Int): Int {
        return context.getSharedPreferences(DATA_NAME, Context.MODE_PRIVATE)
            .getInt(singleName, defaultValue)
    }

    fun putLong(singleName: String, value: Long) {
        context.getSharedPreferences(DATA_NAME, Context.MODE_PRIVATE)
            .edit{ putLong(singleName, value) }
    }

    fun getLong(singleName: String, defaultValue: Long): Long {
        return context.getSharedPreferences(DATA_NAME, Context.MODE_PRIVATE)
            .getLong(singleName, defaultValue)
    }

    fun putFloat(singleName: String, value: Float) {
        context.getSharedPreferences(DATA_NAME, Context.MODE_PRIVATE)
            .edit{ putFloat(singleName, value) }
    }

    fun getFloat(singleName: String, defaultValue: Float): Float {
        return context.getSharedPreferences(DATA_NAME, Context.MODE_PRIVATE)
            .getFloat(singleName, defaultValue)
    }
}