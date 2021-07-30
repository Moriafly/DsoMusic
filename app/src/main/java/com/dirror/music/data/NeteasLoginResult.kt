package com.dirror.music.data


data class NeteaseGetKey(
    val code: Int,
    val data: NeteaseGetKeyData
)

data class NeteaseGetKeyData(
    val code: Int,
    val unikey: String?
)

data class NeteaseQRCodeResult(
    val code: Int,
    val data: NeteaseQRCodeData
)

data class NeteaseQRCodeData(
    val qrimg: String,
    val qrurl: String
)

data class NeteaseLoginResult(
    val avatarUrl: String?,
    val code: Int,
    val cookie: String?,
    val message: String?,
    val nickname: String?
)