package com.zkyc.arms.base.activity

import android.view.View
import androidx.viewbinding.ViewBinding
import com.zkyc.arms.base.presenter.IPresenter
import com.zkyc.arms.base.view.IView
import javax.inject.Inject

abstract class BaseActivity<VB : ViewBinding, V : IView, P : IPresenter<V>> : BaseVBActivity<VB>() {

    @Inject
    lateinit var mPresenter: P

    override fun setContentView(view: View?) {
        super.setContentView(view)
        @Suppress("UNCHECKED_CAST")
        mPresenter.attachView(this as V)
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter.detachView()
    }
}