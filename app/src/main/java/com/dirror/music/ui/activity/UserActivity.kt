package com.dirror.music.ui.activity

import android.annotation.SuppressLint
import androidx.constraintlayout.widget.ConstraintLayout
import coil.load
import coil.size.ViewSizeResolver
import com.dirror.music.App
import com.dirror.music.databinding.ActivityUserBinding
import com.dirror.music.ui.base.BaseActivity
import com.dirror.music.util.getStatusBarHeight
import com.dirror.music.util.runOnMainThread

class UserActivity : BaseActivity() {

    companion object {
        const val EXTRA_LONG_USER_ID = "extra_long_user_id"
    }

    private lateinit var binding: ActivityUserBinding

    override fun initBinding() {
        binding = ActivityUserBinding.inflate(layoutInflater)
        miniPlayer = binding.miniPlayer
        setContentView(binding.root)
    }

    @SuppressLint("SetTextI18n")
    override fun initView() {
        (binding.titleBar.layoutParams as ConstraintLayout.LayoutParams).apply{
            topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            topMargin = getStatusBarHeight(window, this@UserActivity)
        }

        val userId = intent.getLongExtra(EXTRA_LONG_USER_ID, 0L)
        // toast("userId = $userId")
        App.cloudMusicManager.getUserDetail(userId, {
            runOnMainThread {
                it.profile.backgroundUrl?.let { it1 ->
                    binding.ivBackground.load(it1) {
                        size(ViewSizeResolver(binding.ivBackground))
                        crossfade(300)
                    }
                }
                binding.ivCover.load(it.profile.avatarUrl) {
                    size(ViewSizeResolver(binding.ivBackground))
                    crossfade(300)
                }
                binding.apply {
                    tvName.text = it.profile.nickname
                    tvUserId.text = "UID ${it.profile.userId}"
                    tvListenSongs.text = "累计听歌 ${it.listenSongs} 首"
                    tvSignature.text = it.profile.signature
                    tvFollow.text = "关注 ${it.profile.follows}    粉丝 ${it.profile.followeds}"
                }
            }
        }, {
            // 失败
        })
    }
}