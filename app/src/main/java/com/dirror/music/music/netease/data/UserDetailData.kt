package com.dirror.music.music.netease.data

import androidx.annotation.Keep


/**
 * 用户详细信息
 */
@Keep
data class UserDetailData(
    val level: Int, // 等级
    val listenSongs: Int, // 听过歌的数量
    val profile: ProfileData,

    val code: Int, // 服务器返回
    val createTime: Long,
    val cookie: String?, // 默认空
) {
    @Keep
    data class ProfileData(
        val userId: Long, // 用户 id
        val avatarUrl: String, // 头像链接
        val nickname: String, // 昵称
        val backgroundUrl: String?, // 背景图片
        val signature: String, // 个性签名
        val followeds: String, // 粉丝
        val follows: String, // 关注
    )

}
