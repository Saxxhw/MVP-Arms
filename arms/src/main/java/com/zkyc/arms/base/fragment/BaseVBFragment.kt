package com.zkyc.arms.base.fragment

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.blankj.utilcode.util.ToastUtils
import com.kingja.loadsir.callback.Callback
import com.kingja.loadsir.callback.SuccessCallback
import com.kingja.loadsir.core.LoadService
import com.kingja.loadsir.core.LoadSir
import com.zkyc.arms.base.view.IView
import com.zkyc.arms.loadsir.EmptyCallback
import com.zkyc.arms.loadsir.ErrorCallback
import com.zkyc.arms.loadsir.LoadingCallback
import com.zkyc.arms.progress.ACProgressFlower
import org.greenrobot.eventbus.EventBus

abstract class BaseVBFragment<VB : ViewBinding>(@LayoutRes val contentLayoutId: Int) :
    Fragment(contentLayoutId), IView, Callback.OnReloadListener {

    /**
     * 视图绑定实例
     */
    private var _binding: VB? = null
    protected val mBinding get() = _binding!!

    /**
     * 懒加载标识
     */
    private var isLoaded = false

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
        arguments?.let { extras -> getBundleExtras(extras) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = bindVB(view)
        super.onViewCreated(view, savedInstanceState)
        getLoadLayout()?.let { loadService = LoadSir.getDefault().register(it, this) }
        initView(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        if (needEventBus()) {
            EventBus.getDefault().register(this)
        }
    }

    override fun onResume() {
        super.onResume()
        if (!isLoaded && !isHidden) {
            initData()
            isLoaded = true
        }
    }

    override fun onStop() {
        super.onStop()
        if (needEventBus()) {
            EventBus.getDefault().unregister(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        isLoaded = false
        _binding = null
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
     * 加载更多结束
     */
    override fun loadMoreEnd() {}

    /**
     * 加载更多失败
     */
    override fun loadMoreFail() {}

    /**
     * 展示进度条
     */
    override fun showProgress() {
        if (progressDialog == null) {
            progressDialog = ACProgressFlower.Builder(requireContext()).build()
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

    override fun reLogin() {

    }

    override fun onReload(v: View?) {}

    protected abstract fun bindVB(view: View): VB

    protected open fun getLoadLayout(): View? = null

    protected open fun getBundleExtras(extras: Bundle) {}

    protected open fun initView(savedInstanceState: Bundle?) {}

    protected open fun initData() {}

    protected open fun needEventBus() = false
}