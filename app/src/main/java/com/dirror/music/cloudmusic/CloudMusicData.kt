package com.dirror.music.cloudmusic

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

class CloudMusicData {
}

// 登录数据
data class LoginData(
    val code: Int,
    val profile: ProfileData,
)

// 详细用户信息
data class UserDetailData(
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

@Parcelize
data class SongData(
    val songs: List<SongInnerData>

) : Parcelable

@Parcelize
data class SongInnerData(
    val name: String,
    val id: Long,
    val ar: List<Artist>,
    val al: AlbumData,
) : Parcelable

@Parcelize
data class AlbumData( // 专辑
    val picUrl: String
) : Parcelable

@Parcelize
data class Artist(
    val name: String
) : Parcelable