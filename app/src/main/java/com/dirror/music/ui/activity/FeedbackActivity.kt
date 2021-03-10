package com.dirror.music.ui.activity

import com.dirror.music.MyApplication
import com.dirror.music.bmob.BmobManager
import com.dirror.music.databinding.ActivityFeedbackBinding
import com.dirror.music.ui.base.BaseActivity
import com.dirror.music.util.toast

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
        binding.btnUpload.setOnClickListener {
            val feedback = binding.etFeedback.text.toString()
            val contact = binding.etContact.text.toString()
            when {
                feedback.length < 10 -> toast("反馈内容不少于 10 个字符")
                contact.isEmpty() -> toast("请输入联系方式")
                else -> {
                    // 上传反馈内容
                    BmobManager().uploadFeedback(feedback, contact, {
                        // 上传成功
                        toast("上传成功，感谢反馈")
                        finish()
                    }, {
                        // 上传失败
                        toast("上传失败，请稍后重试")
                    })
                }
            }
        }
        binding.itemHelpDocument.setOnClickListener {
            MyApplication.activityManager.startWebActivity(this, HELP_URL)
        }
    }

}