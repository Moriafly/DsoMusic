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
    const val PLAY_ON_MOBILE = "boolean_play_on_mobile" // 是否在移动数据下播放
    const val SEARCH_ENGINE = "int_search_engine" // 音乐搜索引擎

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

    fun putBoolean(singleName: String, value: Boolean) {
        context.getSharedPreferences(DATA_NAME, Context.MODE_PRIVATE)
            .edit{ putBoolean(singleName, value) }
    }

    fun getBoolean(singleName: String, defaultValue: Boolean): Boolean {
        return context.getSharedPreferences(DATA_NAME, Context.MODE_PRIVATE)
            .getBoolean(singleName, defaultValue)
    }

}