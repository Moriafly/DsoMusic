package com.dirror.music.ui.activity

import com.dirror.music.App
import com.dirror.music.databinding.ActivityFeedbackBinding
import com.dirror.music.ui.base.BaseActivity

class FeedbackActivity : BaseActivity() {

    companion object {
        private const val HELP_URL = "https://moriafly.gitee.io/dso-page/page/help_docs/index.html"
    }

    private lateinit var binding: ActivityFeedbackBinding

    override fun initBinding() {
        binding = ActivityFeedbackBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun initListener() {
        binding.itemHelpDocument.setOnClickListener {
            App.activityManager.startWebActivity(this, HELP_URL)
        }
    }

}