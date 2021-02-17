package com.dirror.music.ui.activity

import android.R.attr
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.core.graphics.drawable.toDrawable
import com.dirror.music.MyApplication
import com.dirror.music.databinding.ActivitySettingsBinding
import com.dirror.music.ui.base.BaseActivity
import com.dirror.music.util.Config
import com.dirror.music.util.DarkThemeUtil
import com.dirror.music.util.GlideUtil


/**
 * 设置 Activity
 */
class SettingsActivity : BaseActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun initBinding() {
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun initView() {
        // 按钮
        binding.apply {
            switcherParseHomeNavigation.setChecked(MyApplication.mmkv.decodeBool(Config.PARSE_NAVIGATION, true))
            switcherPlaylistScrollAnimation.setChecked(MyApplication.mmkv.decodeBool(Config.PLAYLIST_SCROLL_ANIMATION, true))
            switcherDoubleRowMyPlaylist.setChecked(MyApplication.mmkv.decodeBool(Config.DOUBLE_ROW_MY_PLAYLIST, false))
            switcherDarkTheme.setChecked(MyApplication.mmkv.decodeBool(Config.DARK_THEME, false))
            switcherSentenceRecommend.setChecked(MyApplication.mmkv.decodeBool(Config.SENTENCE_RECOMMEND, true))
            switcherPlayOnMobile.setChecked(MyApplication.mmkv.decodeBool(Config.PLAY_ON_MOBILE, false))
            switcherPauseSongAfterUnplugHeadset.setChecked(
                MyApplication.mmkv.decodeBool(
                    Config.PAUSE_SONG_AFTER_UNPLUG_HEADSET,
                    true
                )
            )
            switcherSkipErrorMusic.setChecked(MyApplication.mmkv.decodeBool(Config.SKIP_ERROR_MUSIC, true))
            switcherFilterRecord.setChecked(MyApplication.mmkv.decodeBool(Config.FILTER_RECORD, true))
            switcherLocalMusicParseLyric.setChecked(
                MyApplication.mmkv.decodeBool(
                    Config.PARSE_INTERNET_LYRIC_LOCAL_MUSIC,
                    true
                )
            )
            switcherSmartFilter.setChecked(MyApplication.mmkv.decodeBool(Config.SMART_FILTER, true))
        }

    }

    override fun initListener() {
        binding.apply {

            switcherParseHomeNavigation.setOnCheckedChangeListener { MyApplication.mmkv.encode(Config.PARSE_NAVIGATION, it) }

            switcherPlaylistScrollAnimation.setOnCheckedChangeListener { MyApplication.mmkv.encode(
                Config.PLAYLIST_SCROLL_ANIMATION,
                it
            ) }

            switcherDoubleRowMyPlaylist.setOnCheckedChangeListener {
                MyApplication.mmkv.encode(Config.DOUBLE_ROW_MY_PLAYLIST, it)
            }

            switcherDarkTheme.setOnCheckedChangeListener {
                DarkThemeUtil.setDarkTheme(it)
            }

            switcherSentenceRecommend.setOnCheckedChangeListener {
                MyApplication.mmkv.encode(Config.SENTENCE_RECOMMEND, it)
            }

            switcherFilterRecord.setOnCheckedChangeListener { MyApplication.mmkv.encode(Config.FILTER_RECORD, it) }

            switcherLocalMusicParseLyric.setOnCheckedChangeListener { MyApplication.mmkv.encode(
                Config.PARSE_INTERNET_LYRIC_LOCAL_MUSIC,
                it
            ) }

            switcherSkipErrorMusic.setOnCheckedChangeListener { MyApplication.mmkv.encode(Config.SKIP_ERROR_MUSIC, it) }

            switcherPlayOnMobile.setOnCheckedChangeListener { MyApplication.mmkv.encode(Config.PLAY_ON_MOBILE, it) }

            switcherPauseSongAfterUnplugHeadset.setOnCheckedChangeListener { MyApplication.mmkv.encode(
                Config.PAUSE_SONG_AFTER_UNPLUG_HEADSET,
                it
            ) }

            switcherSmartFilter.setOnCheckedChangeListener { MyApplication.mmkv.encode(Config.SMART_FILTER, it) }

            itemCustomBackground.setOnClickListener {
                val intent = Intent(Intent.ACTION_PICK, null)
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
                startActivityForResult(intent, 2)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2) {
            // 从相册返回的数据
            // 得到图片的全路径
            val path = data?.data.toString()
            MyApplication.mmkv.encode(Config.THEME_BACKGROUND, path)
            path.let {
                GlideUtil.load(it) { bitmap ->
                    window.setBackgroundDrawable(bitmap.toDrawable(resources))
                }
            }

        }
    }

}