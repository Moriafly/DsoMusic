package com.dirror.music.cloudmusic

class CloudMusicData {
}

data class LoginData (
    val code: Int,
    val profile: ProfileData
)

data class ProfileData (
    val nickname: String
)