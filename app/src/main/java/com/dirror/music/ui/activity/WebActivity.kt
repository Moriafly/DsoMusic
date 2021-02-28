package com.dirror.music.ui.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Message
import android.webkit.*
import androidx.core.content.ContextCompat
import com.dirror.music.databinding.ActivityWebBinding
import com.dirror.music.ui.base.BaseActivity

class WebActivity : BaseActivity() {

    companion object {
        const val EXTRA_TITLE = "extra_title"
    }

    private lateinit var binding: ActivityWebBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val extraWebUrlStr = intent.getStringExtra("extra_webUrlStr")
        val extraTitle = intent.getStringExtra(EXTRA_TITLE)

        binding.webView.settings.javaScriptEnabled = false // 禁用 JavaScript
        binding.webView.settings.allowFileAccess = false // 禁止访问私有文件数据
        binding.webView.settings.cacheMode = WebSettings.LOAD_NO_CACHE
        binding.webView.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                // view?.loadUrl("javascript:function setTop(){document.querySelector('#J-superLayer-main > a > img').style.display=\"none\";}setTop();")

                when {
                    request?.url.toString().startsWith("https://baike.baidu.com/item/") -> {
                        return false // 加载
                    }
                    request?.url.toString().startsWith("https://www.baidu.com/") -> {
                        finish()
                        return false
                    }
                    else -> { // 不是百度页面，跳转新页面
                        val intent = Intent(application, WebActivity::class.java)
                        intent.putExtra("extra_webUrlStr", request?.url.toString())
                        // intent.putExtra("extra_webTitleStr", request?.)
                        startActivity(intent)
                        // return super.shouldOverrideUrlLoading(view, request)
                        return true // 不加载
                    }
                }
            }

            override fun onLoadResource(view: WebView?, url: String?) {
                val arrayStr = arrayOf(
                    "#BK_before_content_wrapper > div.full-screen-second_prestrain", // 屏蔽秒懂百科
                    "#J-super-layer-promote", // 屏蔽百科左下方弹窗
                    "#sfr-app > div > div.rt-head", // 屏蔽百科标题
                    "#header_wrapper", // 屏蔽百科目录和TA说
                    "#J-extra-info > ul > li.extra-list-item.feature-flag",  // 屏蔽特色词条
                    "#BK_content_wrapper > div.quickNav", // 屏蔽快速导航
                    "#BK_content_wrapper > div.BK-main-content > div:nth-child(13) > div", // 屏蔽内嵌TA说
                    "#J_yitiao_container", // 屏蔽广告
                    "#qtqy_container", // 屏蔽搜索发现
                    "#tashuo_list", // 屏蔽底部TA说
                    "#OWTPDboEu7", // 秒ding本尊答
                    "#TmjYqfd5", // Hot
                    "#bottomMenu",
                    "#BK_body_content_wrapper > div.BK-after-content-wrapper > div.copyright",
                    "#BK_body_content_wrapper > div.BK-after-content-wrapper > div.bottom-logo",

                    // github
                    // ".subnav",
                )
                for (array in arrayStr) {
                    view?.loadUrl(getSelectorUrl(array))
                }
                super.onLoadResource(view, url)
            }



        }

        binding.webView.webChromeClient = object : WebChromeClient() {
            override fun onReceivedTitle(view: WebView, title: String) {
                super.onReceivedTitle(view, title)
                if (extraTitle.isNullOrEmpty()) {
                    binding.titleBar.setTitleBarText(title)
                } else {
                    binding.titleBar.setTitleBarText(extraTitle)
                }
            }


            override fun onCreateWindow(
                view: WebView?,
                isDialog: Boolean,
                isUserGesture: Boolean,
                resultMsg: Message?
            ): Boolean {

                return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg)
            }
        }

        binding.webView.loadUrl(extraWebUrlStr.toString())
        // Toast.makeText(this@WebActivity, "%" + extraWebUrlStr.toString() + "%", Toast.LENGTH_SHORT).show()

        binding.webView.setDownloadListener { url, userAgent, contentDisposition, mimeType, contentLength ->
            val intent = Intent()
            intent.action = "android.intent.action.VIEW"
            val contentUrl = Uri.parse(url)
            intent.data = contentUrl
            ContextCompat.startActivity(this, intent, Bundle())
        }
    }

    private fun getSelectorUrl(selector: String): String {
        return "javascript:function setTop(){document.querySelector('${selector}').style.display=\"none\";}setTop();"
    }

}