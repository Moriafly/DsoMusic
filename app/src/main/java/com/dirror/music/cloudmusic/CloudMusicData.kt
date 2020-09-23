package com.dirror.music.cloudmusic

import com.dirror.music.music.StandardArtistData

class CloudMusicData {
}

// 登录数据
data class LoginData(
    val code: Int,
    val profile: ProfileData,
)

// 详细用户信息
data class UserDetailData(
    val level: Int, // 用户等级
    val code: Int?, // 参数，一般为错误代码，可能是空
    val profile: ProfileData?
)

// 用户简单信息
data class ProfileData(
    val nickname: String,
    val userId: Int,
    val avatarUrl: String, // 头像
)

data class UserPlaylistData(
    val playlist: List<PlaylistData>
)

data class PlaylistData(
    val coverImgUrl: String, // 歌单图片
    val name: String, // 歌单名称
    val trackCount: Int, // 歌单歌曲数量
    val id: Long, // 歌单 id
)

data class DetailPlaylistData(
    val code: Int,
    val playlist: DetailPlaylistInnerData
)

data class DetailPlaylistInnerData(
    val tracks: List<TracksData>,
    val trackIds: List<TrackIdsData>
)

data class TracksData(
    val name: String, // 歌曲名称
    val id: Long, // 歌曲 id
)

data class TrackIdsData(
    val id: Long, // 歌曲 id
)

data class SongData(
    val songs: List<SongInnerData>
)

data class SongInnerData(
    val name: String?,
    val id: Long,
    val ar: ArrayList<StandardArtistData>,
    val al: AlbumData,
)

data class AlbumData( // 专辑
    val picUrl: String
)

data class ArtistData(
    val name: String
)



// 搜索结果
data class SearchData(
    val result: SearchResultData
)
data class SearchResultData(
    val songs: List<SearchSongData>
)

data class SearchSongData(
    val id: Long,
)

// 歌曲评论
data class CommentData(
    val hotComments: List<HotComment>, // 热门评论
    val total: Long // 总评论
)

data class HotComment(
    val user: CommentUser,
    val content: String, // 评论内容
    val time: Long, // 评论时间
    val likedCount: Long // 点赞数
)

data class CommentUser(
    val avatarUrl: String, // 头像
    val nickname: String // 昵称

)