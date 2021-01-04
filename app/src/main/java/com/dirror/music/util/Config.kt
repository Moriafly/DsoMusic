package com.dirror.music.util

object Config {

    const val UID = "long_uid"

    const val PAUSE_SONG_AFTER_UNPLUG_HEADSET = "pause_song_after_unplug_headset"

    const val LOCAL_PLAYLIST = "local_playlist"

    const val PLAY_ON_MOBILE = "boolean_play_on_mobile" // 是否在移动数据下播放

    const val PLAY_HISTORY = "list_play_history" // 播放历史

    const val PLAY_MODE = "int_play_mode" // 播放模式

    const val SEARCH_ENGINE = "int_search_engine" // 音乐搜索引擎

    @Deprecated("弃用")
    const val SEARCH_ENGINE_TIP = "boolean_search_engine_tip" // 音乐搜索引擎提示

    const val CLOUD_MUSIC_COOKIE = "string_cloud_music_cookie"

    const val FILTER_RECORD = "boolean_filter_record" // 过滤录音

    const val PARSE_INTERNET_LYRIC_LOCAL_MUSIC = "boolean_parse_internet_lyric_local_music" // 为本地音乐自动匹配网络歌词

    // const val FIRST_USE_APP = "boolean_first_use_app" // 第一次使用 APP
}