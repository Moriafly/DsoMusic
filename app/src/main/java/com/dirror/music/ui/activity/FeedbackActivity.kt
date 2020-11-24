package com.dirror.music.ui.activity

import com.dirror.music.R
import com.dirror.music.bmob.BmobManager
import com.dirror.music.ui.base.BaseActivity
import com.dirror.music.util.toast
import kotlinx.android.synthetic.main.activity_feedback.*

class FeedbackActivity : BaseActivity(R.layout.activity_feedback) {

    override fun initData() {

    }

    override fun initView() {

    }

    override fun initListener() {
        btnUpload.setOnClickListener {
            val feedback = etFeedback.text.toString()
            val contact = etContact.text.toString()
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
    }

}