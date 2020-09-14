package com.dirror.music.cloudmusic

class CloudMusicData {
}

// 登录数据
data class LoginData (
    val code: Int,
    val profile: ProfileData,
)

// 用户简单信息
data class ProfileData (
    val nickname: String,
    val userId: Int,
)