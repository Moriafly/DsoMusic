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

package com.dirror.music.ui.activity

import android.content.Intent
import android.view.View
import androidx.activity.viewModels
import com.dirror.music.R
import com.dirror.music.databinding.ActivityLoginByPhoneBinding
import com.dirror.music.manager.ActivityCollector
import com.dirror.music.manager.User
import com.dirror.music.ui.base.BaseActivity
import com.dirror.music.ui.live.NeteaseCloudMusicApiActivity
import com.dirror.music.ui.viewmodel.LoginCellphoneViewModel
import com.dirror.music.util.runOnMainThread
import com.dirror.music.util.sky.SkySecure
import com.dirror.music.util.toast
import com.dso.ext.md5

/**
 * 通过手机号登录
 *
 * @author Moriafly
 * @since 2021年7月13日
 */
class LoginByPhoneActivity : BaseActivity() {

    private val loginCellphoneViewModel: LoginCellphoneViewModel by viewModels()

    lateinit var binding: ActivityLoginByPhoneBinding

    override fun initBinding() {
        if (getString(R.string.app_name).md5() == SkySecure.getAppNameMd5()) {
            binding = ActivityLoginByPhoneBinding.inflate(layoutInflater)
            setContentView(binding.root)
        } else {
            ActivityCollector.finishAll()
        }
    }

    override fun initListener() {
        binding.itemNeteaseCloudMusicApi.setOnClickListener {
            startActivity(Intent(this, NeteaseCloudMusicApiActivity::class.java))
        }
        binding.btnLoginByPhone.setOnClickListener {
            val phone = binding.etPhone.text.toString()
            val password = binding.etPassword.text.toString()
            if (phone == "" || password == "") {
                toast("请输入手机号或密码")
            } else {
                binding.btnLoginByPhone.visibility = View.GONE
                binding.llLoading.visibility = View.VISIBLE
                binding.lottieLoading.repeatCount = -1
                binding.lottieLoading.playAnimation()
                loginCellphoneViewModel.loginByCellphone(User.neteaseCloudMusicApi, phone, password, {
                    // 发送广播
                    val intent = Intent("com.dirror.music.LOGIN")
                    intent.setPackage(packageName)
                    sendBroadcast(intent)
                    // 通知 Login 关闭
                    setResult(RESULT_OK, Intent())
                    finish()
                }, { code ->
                    runOnMainThread {
                        binding.btnLoginByPhone.visibility = View.VISIBLE
                        binding.llLoading.visibility = View.GONE
                        binding.lottieLoading.cancelAnimation()
                        if (code == 250) {
                            toast("错误代码：250\n当前登录失败，请稍后再试")
                        } else {
                            toast("登录失败，请检查服务、用户名或密码")
                        }
                    }
                })
            }
        }
    }

}