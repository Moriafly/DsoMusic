package com.dirror.music.ui.player

import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dirror.music.MyApplication
import com.dirror.music.R
import com.dirror.music.databinding.ActivitySongCoverBinding
import com.dirror.music.ui.base.BaseActivity
import jp.wasabeef.glide.transformations.BlurTransformation

class SongCoverActivity : BaseActivity() {

    private lateinit var binding: ActivitySongCoverBinding

    override fun initBinding() {
        binding = ActivitySongCoverBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun initView() {
        with(binding) {
            MyApplication.musicController.value?.getPlayerCover()?.value?.let {
                // 设置 背景 图片
                Glide.with(this@SongCoverActivity)
                    .load(it)
                    .apply(RequestOptions.bitmapTransform(BlurTransformation(15, 5)))
                    .into(binding.ivBackground)

                photoView.setImageBitmap(it)
            }
        }
    }

    override fun initListener() {
        with(binding) {
            photoView.setOnPhotoTapListener { _, _, _ ->
                finish()
            }
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(
            R.anim.anim_no_anim,
            R.anim.anim_alpha_exit,
        )
    }

}