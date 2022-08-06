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

package com.dirror.music.ui.viewmodel

import androidx.annotation.Keep
import androidx.lifecycle.ViewModel
import com.dirror.music.manager.User
import com.dirror.music.music.netease.data.UserDetailData
import com.dirror.music.util.AppConfig
import com.dirror.music.util.EMPTY
import com.dirror.music.util.ErrorCode
import com.dirror.music.util.HttpUtils
import com.dirror.music.util.sky.SkySecure
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * 手机号登录 ViewModel
 *
 * @author Moriafly
 * @since 2021年7月13日
 */
@Keep
class LoginCellphoneViewModel : ViewModel() {

    /**
     * 手机号登录
     */
    fun loginByCellphone(
        api: String,
        phone: String,
        password: String,
        success: (UserDetailData) -> Unit,
        failure: (Int) -> Unit
    ) {
        val passwordMD5 = SkySecure.getMD5(password)
        GlobalScope.launch {
            val map = HashMap<String, String>()
            map["phone"] = phone
            map["countrycode"] = "86"
            map["md5_password"] = passwordMD5
            val userDetail =
                HttpUtils.loginPost("${api}/login/cellphone", map, UserDetailData::class.java)
            if (userDetail != null && userDetail.code == 200) {
                User.apply {
                    AppConfig.cookie = userDetail.cookie ?: String.EMPTY
                    uid = userDetail.profile.userId
                    vipType = userDetail.profile.vipType
                }
                success.invoke(userDetail)
            } else {
                failure.invoke(ErrorCode.ERROR_MAGIC_HTTP)
            }
        }
    }

}