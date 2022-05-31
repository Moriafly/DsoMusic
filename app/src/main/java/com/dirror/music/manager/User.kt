/**
 * DsoMusic Copyright (C) 2020-2021 Moriafly
 *
 * This program comes with ABSOLUTELY NO WARRANTY; for details type `show w'.
 * This is free software, and you are welcome to redistribute it
 * under certain conditions; type `show c' for details.
 *
 * The hypothetical commands `show w' and `show c' should show the appropriate
 * parts of the General Public License.  Of course, your program's commands
 * might be different; for a GUI interface, you would use an "about box".
 *
 * You should also get your employer (if you work as a programmer) or school,
 * if any, to sign a "copyright disclaimer" for the program, if necessary.
 * For more information on this, and how to apply and follow the GNU GPL, see
 * <https://www.gnu.org/licenses/>.
 *
 * The GNU General Public License does not permit incorporating your program
 * into proprietary programs.  If your program is a subroutine library, you
 * may consider it more useful to permit linking proprietary applications with
 * the library.  If this is what you want to do, use the GNU Lesser General
 * Public License instead of this License.  But first, please read
 * <https://www.gnu.org/licenses/why-not-lgpl.html>.
 */

package com.dirror.music.manager

import android.os.Parcelable
import com.dirror.music.App.Companion.mmkv
import com.dirror.music.music.netease.data.UserDetailData
import com.dirror.music.util.AppConfig
import com.dirror.music.util.Config
import com.dirror.music.util.EMPTY
import kotlinx.parcelize.Parcelize

// @IgnoredOnParcel
private const val DEFAULT_UID = 0L

// @IgnoredOnParcel
private const val DEFAULT_VIP_TYPE = 0

/**
 * 网易云音乐用户
 *
 * @author Moriafly
 * @since 2021年7月12日
 */
object User {

    val dsoUser: DsoUser = mmkv.decodeParcelable(Config.DSO_USER, DsoUser::class.java, DsoUser())!!

    /** 用户 uid */
    var uid: Long = DEFAULT_UID
        get() = mmkv.decodeLong(Config.UID, DEFAULT_UID)
        set(value) {
            mmkv.encode(Config.UID, value)
            field = value
        }

    /** 用户 Cookie */
    var cookie: String = AppConfig.cookie

    /**
     * 获取用户配置的 NeteaseCloudMusicApi
     */
    var neteaseCloudMusicApi: String = String.EMPTY
        get() = mmkv.decodeString(Config.USER_NETEASE_CLOUD_MUSIC_API_URL, String.EMPTY)!!
        set(value) {
            mmkv.encode(Config.USER_NETEASE_CLOUD_MUSIC_API_URL, value)
            field = value
        }

    /**
     * 用户 VIP 类型
     */
    var vipType: Int = DEFAULT_VIP_TYPE
        get() = mmkv.decodeInt(Config.VIP_TYPE, DEFAULT_VIP_TYPE)
        set(value) {
            mmkv.encode(Config.VIP_TYPE, value)
            field = value
        }

    /** 是否通过 uid 登录 */
    val isUidLogin: Boolean
        get() {
            val uid = mmkv.decodeLong(Config.UID, DEFAULT_UID)
            return uid != DEFAULT_UID
        }

    /** 是否有 cookie */
    val hasCookie: Boolean
        get() = AppConfig.cookie.isNotEmpty()

    /**
     * 是否是 VIP 用户
     */
    fun isVip(): Boolean {
        return vipType != 0
    }

}

/**
 * Dso Music 用户
 */
@Parcelize
data class DsoUser(

    /** 昵称 */
    var nickname: String = String.EMPTY

): Parcelable {

    /**
     * 从网络更新用户数据
     */
    fun updateFromNet(userDetailData: UserDetailData) {
        nickname = userDetailData.profile.nickname
        save()
    }

    /**
     * 保存数据
     */
    private fun save() {
        mmkv.encode(Config.DSO_USER, this)
    }

}



