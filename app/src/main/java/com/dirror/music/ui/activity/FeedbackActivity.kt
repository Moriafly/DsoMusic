package com.dirror.music.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dirror.music.R
import com.dirror.music.bmob.BmobManager
import com.dirror.music.databinding.ActivityFeedbackBinding
import com.dirror.music.util.toast

class FeedbackActivity : AppCompatActivity(R.layout.activity_feedback) {

    private lateinit var binding: ActivityFeedbackBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedbackBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initListener()
    }

    private fun initListener() {
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
    }

}