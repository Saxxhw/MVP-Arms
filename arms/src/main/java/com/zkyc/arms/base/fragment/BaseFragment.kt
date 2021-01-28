package com.zkyc.arms.base.fragment

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.viewbinding.ViewBinding
import com.zkyc.arms.base.presenter.IPresenter
import com.zkyc.arms.base.view.IView
import javax.inject.Inject

abstract class BaseFragment<VB : ViewBinding, V : IView, P : IPresenter<V>>(@LayoutRes contentLayoutId: Int) :
    BaseVBFragment<VB>(contentLayoutId) {

    @Inject
    lateinit var mPresenter: P

    override fun initView(savedInstanceState: Bundle?) {
        @Suppress("UNCHECKED_CAST")
        mPresenter.attachView(this as V)
        super.initView(savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mPresenter.detachView()
    }
}