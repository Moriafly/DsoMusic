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

data class NeteaseUserInfo(
    val account: Account?,
    val code: Int,
//    val profile: Profile?
)

data class Account(
    val anonimousUser: Boolean,
    val ban: Int,
    val baoyueVersion: Int,
    val createTime: Long,
    val donateVersion: Int,
    val id: Long,
    val paidFee: Boolean,
    val status: Int,
    val tokenVersion: Int,
    val type: Int,
    val userName: String?,
    val vipType: Int,
    val whitelistAuthority: Int
)

data class Profile(
    val accountStatus: Int,
    val accountType: Int,
    val anchor: Boolean,
    val authStatus: Int,
    val authenticated: Boolean,
    val authenticationTypes: Int,
    val authority: Int,
    val avatarDetail: Any,
    val avatarImgId: Long,
    val avatarUrl: String?,
    val backgroundImgId: Long,
    val backgroundUrl: String?,
    val birthday: Long,
    val city: Int,
    val createTime: Long,
    val defaultAvatar: Boolean,
    val description: Any,
    val detailDescription: Any,
    val djStatus: Int,
    val expertTags: Any,
    val experts: Any,
    val followed: Boolean,
    val gender: Int,
    val lastLoginIP: String?,
    val lastLoginTime: Long,
    val locationStatus: Int,
    val mutual: Boolean,
    val nickname: String,
    val province: Int,
    val remarkName: Any,
    val shortUserName: String?,
    val signature: Any,
    val userId: Long,
    val userName: String?,
    val userType: Int,
    val vipType: Int,
    val viptypeVersion: Int
)
