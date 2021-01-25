package com.dirror.music.data

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

// 详细用户信息
@Keep
data class UserDetailData(
    val level: Int, // 用户等级
    val code: Int?, // 参数，一般为错误代码，可能是空
    val profile: ProfileData?,
)

// 用户简单信息
@Keep
data class ProfileData(
    val nickname: String,
    val userId: Long,
    val avatarUrl: String, // 头像
    val follows: Int, // 关注
    val followeds: Int, // 粉丝
)

@Keep
data class UserPlaylistData(
    val more: Boolean, // 是否有更多
    val playlist: ArrayList<PlaylistData>
)

@Keep
data class PlaylistData(
    @SerializedName("coverImgUrl") val coverImgUrl: String, // 歌单图片
    @SerializedName("name") val name: String, // 歌单名称
    @SerializedName("trackCount") val trackCount: Int, // 歌单歌曲数量
    @SerializedName("id") val id: Long, // 歌单 id
)

@Keep
data class DetailPlaylistData(
    @SerializedName("code") val code: Int,
    @SerializedName("playlist") val playlist: DetailPlaylistInnerData?
)

@Keep
data class DetailPlaylistInnerData(
    @SerializedName("tracks") val tracks: List<TracksData>,
    @SerializedName("trackIds") val trackIds: List<TrackIdsData>,
    @SerializedName("coverImgUrl") val coverImgUrl: String?, // 歌单图片
    @SerializedName("name") val name: String?, // 歌单名字
    @SerializedName("description") val description: String?, // 描述
)

@Keep
data class TracksData(
    val name: String, // 歌曲名称
    val id: Long, // 歌曲 id
)

@Keep
data class TrackIdsData(
    val id: Long, // 歌曲 id
)

// 歌曲评论
@Keep
data class CommentData(
    val hotComments: List<HotComment>, // 热门评论
    val total: Long // 总评论
)

@Keep
data class HotComment(
    val user: CommentUser,
    val content: String, // 评论内容
    val time: Long, // 评论时间
    val likedCount: Long // 点赞数
)

@Keep
data class CommentUser(
    val avatarUrl: String, // 头像
    val nickname: String, // 昵称
    val userId: Long //
)