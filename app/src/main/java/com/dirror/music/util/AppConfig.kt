package com.dirror.music.util

import com.drake.serialize.serialize.serialLazy

object AppConfig {

    var cookie by serialLazy("", Config.CLOUD_MUSIC_COOKIE)

}