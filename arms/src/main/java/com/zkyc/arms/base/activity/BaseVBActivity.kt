package com.zkyc.arms.base.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.blankj.utilcode.util.ToastUtils
import com.google.android.material.appbar.MaterialToolbar
import com.kingja.loadsir.callback.Callback
import com.kingja.loadsir.callback.SuccessCallback
import com.kingja.loadsir.core.LoadService
import com.kingja.loadsir.core.LoadSir
import com.zkyc.arms.R
import com.zkyc.arms.base.view.IView
import com.zkyc.arms.loadsir.EmptyCallback
import com.zkyc.arms.loadsir.ErrorCallback
import com.zkyc.arms.loadsir.LoadingCallback
import com.zkyc.arms.progress.ACProgressFlower

abstract class BaseVBActivity<VB : ViewBinding> : AppCompatActivity(), IView,
    Callback.OnReloadListener {

    /**
     * 视图绑定实例
     */
    lateinit var binding: VB

    /**
     * 加载对话框
     */
    private var progressDialog: ACProgressFlower? = null

    /**
     * 加载反馈页管理框架
     */
    private var loadService: LoadService<*>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = inflateVB(layoutInflater)
        setContentView(binding.root)
        initToolbar()
        intent?.extras?.let { getBundleExtras(it) }
        getLoadLayout()?.let { loadService = LoadSir.getDefault().register(it, this) }
        initViewAndEvent(savedInstanceState)
    }

    /**
     * 弹出提示语
     * @param msg 提示内容
     */
    override fun toast(msg: String) {
        ToastUtils.showShort(msg)
    }

    /**
     * 弹出提示语
     * @param msgId 提示内容资源id
     */
    override fun toast(msgId: Int) {
        ToastUtils.showShort(msgId)
    }

    /**
     * 展示进度条
     */
    override fun showProgress() {
        if (progressDialog == null) {
            progressDialog = ACProgressFlower.Builder(this).build()
        }
        progressDialog?.show()
    }

    /**
     * 关闭进度条后，执行某些操作
     */
    override fun dismissProgress() {
        progressDialog?.dismiss()
    }

    /**
     * 展示加载中状态
     */
    override fun showLoading() {
        if (SuccessCallback::class.java != loadService?.currentCallback) {
            loadService?.showCallback(LoadingCallback::class.java)
        }
    }

    /**
     * 展示空状态
     */
    override fun showEmpty() {
        loadService?.showCallback(EmptyCallback::class.java)
    }

    /**
     * 展示异常状态
     */
    override fun showError() {
        loadService?.showCallback(ErrorCallback::class.java)
    }

    /**
     * 展示成功状态
     */
    override fun showSuccess() {
        loadService?.showCallback(SuccessCallback::class.java)
    }

    /**
     * 初始化标题栏
     */
    private fun initToolbar() {
        val toolbar = findViewById<MaterialToolbar>(R.id.material_toolbar)
        if (toolbar != null) {
            setSupportActionBar(toolbar)
            if (showHomeButton()) {
                supportActionBar?.run {
                    setHomeButtonEnabled(true)
                    setDisplayHomeAsUpEnabled(true)
                    initNavigation(toolbar)
                }
            }
        }
    }

    override fun onReload(v: View?) {}

    abstract fun inflateVB(inflater: LayoutInflater): VB

    open fun showHomeButton() = true

    open fun initNavigation(toolbar: MaterialToolbar) = toolbar.setNavigationOnClickListener { onBackPressed() }

    open fun getLoadLayout(): View? = null

    open fun getBundleExtras(extras: Bundle) {}

    abstract fun initViewAndEvent(savedInstanceState: Bundle?)
}