package com.zkyc.arms.feature

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.os.bundleOf
import com.tencent.smtt.sdk.WebChromeClient
import com.tencent.smtt.sdk.WebSettings
import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebViewClient
import com.zkyc.arms.base.activity.BaseVBActivity
import com.zkyc.arms.databinding.ActivityWebBinding
import com.zkyc.arms.extension.emptyString

/**
 * 网页
 */
class WebActivity : BaseVBActivity<ActivityWebBinding>() {

    companion object {

        // 链接传递标识
        private const val ARG_URL = "url"

        /**
         * 启动[WebActivity]页
         */
        fun startWith(activity: Activity, url: String) = with(activity) {
            val starter = Intent(activity, WebActivity::class.java)
            starter.putExtras(bundleOf(ARG_URL to url))
            startActivity(starter)
        }
    }

    /**
     * 链接
     */
    private lateinit var mUrl: String

    override fun inflateVB(inflater: LayoutInflater) = ActivityWebBinding.inflate(inflater)

    override fun getBundleExtras(extras: Bundle) {
        super.getBundleExtras(extras)
        mUrl = extras.getString(ARG_URL, emptyString())
    }

    override fun initViewAndEvent(savedInstanceState: Bundle?) {
        // 初始化webView
        initWebView(binding.webView)
        // 加载页面
        binding.webView.loadUrl(mUrl)
    }

    override fun onResume() {
        super.onResume()
        binding.webView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.webView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.webView.destroy()
    }

    override fun onBackPressed() {
        if (binding.webView.canGoBack()) {
            binding.webView.goBack()
        } else {
            super.onBackPressed()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView(view: WebView) {
        with(view) {
            // 不缩放
            setInitialScale(100)
            // 开启软硬件加速，开启软硬件加速这个性能提升还是很明显的，但是会耗费更大的内存。
            setLayerType(
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) View.LAYER_TYPE_HARDWARE else View.LAYER_TYPE_SOFTWARE,
                null
            )
            // 移除高危风险js监听
            removeJavascriptInterface("searchBoxJavaBridge_")
            removeJavascriptInterface("accessibility")
            removeJavascriptInterface("accessibilityTraversal")
            // 添加监听
            webChromeClient = mWebChromeClient
            webViewClient = mWebViewClient
            // 其他设置
            with(settings) {
                // 网页内容的宽度是否可大于WebView控件的宽度
                loadWithOverviewMode = false
                // 保存表单数据
                @Suppress("DEPRECATION")
                saveFormData = true
                // 是否应该支持使用其屏幕缩放控件和手势缩放
                setSupportZoom(true)
                // 是否应该支持使用其屏幕缩放控件和手势缩放
                builtInZoomControls = true
                // 隐藏原生的缩放控件
                displayZoomControls = true
                // 启动应用缓存
                setAppCacheEnabled(true)
                // 设置缓存模式
                // 缓存模式如下：
                // LOAD_CACHE_ONLY: 不使用网络，只读取本地缓存数据
                // LOAD_DEFAULT: （默认）根据cache-control决定是否从网络上取数据。
                // LOAD_NO_CACHE: 不使用缓存，只从网络获取数据.
                // LOAD_CACHE_ELSE_NETWORK，只要本地有，无论是否过期，或者no-cache，都使用缓存中的数据。
                cacheMode = WebSettings.LOAD_DEFAULT
                @Suppress("DEPRECATION")
                setAppCacheMaxSize(Long.MAX_VALUE)
                // setDefaultZoom  api19被弃用
                // 设置此属性，可任意比例缩放。
                useWideViewPort = true
                // 告诉WebView启用JavaScript执行。默认的是false。
                // 注意：这个很重要   如果访问的页面中要与Javascript交互，则WebView必须设置支持JavaScrip
                @Suppress("DEPRECATION")
                javaScriptEnabled = true
                // 页面加载好以后，再放开图片
                // ws.setBlockNetworkImage(false);
                // 使用localStorage则必须打开
                domStorageEnabled = true
                // 防止中文乱码
                defaultTextEncodingName = "UTF-8"
                // 排版适应屏幕
                // 用WebView显示图片，可使用这个参数
                // 设置网页布局类型： 1、LayoutAlgorithm.NARROW_COLUMNS：适应内容大小 2、LayoutAlgorithm.SINGLE_COLUMN:适应屏幕，内容将自动缩放
                layoutAlgorithm = WebSettings.LayoutAlgorithm.NARROW_COLUMNS
                // WebView是否新窗口打开(加了后可能打不开网页)
                // ws.setSupportMultipleWindows(true);
                // WebView从5.0开始默认不允许混合模式,https中不能加载http资源,需要设置开启。
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                }
                // 设置字体默认缩放大小
                textZoom = 100
                //设置网页在加载的时候暂时不加载图片
                loadsImagesAutomatically = true
                // 默认不开启密码保存功能
                @Suppress("DEPRECATION")
                savePassword = false
            }
        }
    }

    /**
     *
     */
    private val mWebChromeClient = object : WebChromeClient() {

        override fun onReceivedTitle(p0: WebView?, title: String?) {
            super.onReceivedTitle(p0, title)
            setTitle(title)
        }

        override fun onProgressChanged(p0: WebView?, newProgress: Int) {
            super.onProgressChanged(p0, newProgress)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                binding.cpbLoading.setProgress(newProgress, true)
            } else {
                binding.cpbLoading.progress = newProgress
            }
            val max = 95
            if (newProgress > max) {
                binding.cpbLoading.hide()
            }
        }
    }

    /**
     *
     */
    private val mWebViewClient = object : WebViewClient() {

        /**
         * 这个方法中可以做拦截
         * 主要的作用是处理各种通知和请求事件
         * 返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
         * @param view  view
         * @param url   链接
         * @return  是否自己处理，true表示自己处理
         */
        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            return false
        }
    }
}