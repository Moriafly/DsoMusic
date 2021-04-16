package com.dirror.music.util

/**
 * 配置
 */
object Config {
    const val UID = "long_uid"

    const val PAUSE_SONG_AFTER_UNPLUG_HEADSET = "pause_song_after_unplug_headset"

    // 是否在移动数据下播放
    const val PLAY_ON_MOBILE = "boolean_play_on_mobile"

    // 播放历史
    const val PLAY_HISTORY = "list_play_history"

    // 播放模式
    const val PLAY_MODE = "int_play_mode"

    // 音乐搜索引擎
    const val SEARCH_ENGINE = "int_search_engine"

    // 网易云 Cookie
    const val CLOUD_MUSIC_COOKIE = "string_cloud_music_cookie"

    // 过滤录音
    const val FILTER_RECORD = "boolean_filter_record"

    // 为本地音乐自动匹配网络歌词
    const val PARSE_INTERNET_LYRIC_LOCAL_MUSIC = "boolean_parse_internet_lyric_local_music"

    // 跳过错误原因，自动播放下一首
    const val SKIP_ERROR_MUSIC = "boolean_skip_error_music"

    // 歌词翻译
    const val LYRIC_TRANSLATION = "boolean_lyric_translation"

    // 智能过滤
    const val SMART_FILTER = "smart_filter"

    // 首页导航栏适配
    const val PARSE_NAVIGATION = "parse_home_navigation"

    // 歌单滑动动画
    const val PLAYLIST_SCROLL_ANIMATION = "boolean_playlist_scroll_animation"

    // 深色主题
    const val DARK_THEME = "boolean_dark_theme"

    // 是否开启句子推荐
    const val SENTENCE_RECOMMEND = "boolean_sentence_recommend"

    // 自定义背景
    const val APP_THEME_BACKGROUND = "bitmap_app_theme_background"

    // 允许与其他应用同时播放
    const val ALLOW_AUDIO_FOCUS = "boolean_allow_audio_focus"

    // 锁屏显示
    const val SHOW_IN_LOCK_SCREEN = "boolean_show_in_lock_screen"

    /* 单列歌单 */
    const val SINGLE_COLUMN_USER_PLAYLIST = "boolean_single_column_user_playlist"

    /* 服务保存的当前歌曲 */
    const val SERVICE_CURRENT_SONG = "service_current_song"

    /* 服务保存的当前歌曲进度 */
    const val SERVICE_RECOVER_PROGRESS = "service_recover_progress"

    /* 魅族状态栏歌词 */
    const val MEIZU_STATUS_BAR_LYRIC = "boolean_meizu_status_bar_lyric"

    /* 是否启动显示协议 */
    const val SHOW_AGREEMENT = "boolean_show_agreement"

    /* 墨水屏模式 */
    const val INK_SCREEN_MODE = "boolean_ink_screen_mode"

    /* 是否启用用户自定义 NeteaseCloudMusicApi */
    const val USER_NETEASE_CLOUD_MUSIC_API_ENABLE = "boolean_user_netease_cloud_music_api_enable"

    /* 用户自定义 NeteaseCloudMusicApi */
    const val USER_NETEASE_CLOUD_MUSIC_API_URL = "string_user_netease_cloud_music_api_url"

    // 是否打开了私人 FM 模式
    const val PERSON_FM_MODE = "boolean_person_fm_mode"

    // 网易云精彩评论
    const val NETEASE_GOOD_COMMENTS = "boolean_netease_good_comments"

    // QQ 网页版源
    const val QQ_WEB_SOURCE = "boolean_qq_web_source"

    // 选择的页面
    const val SELECT_FRAGMENT = "int_select_fragment"
}