package com.zkyc.arms.base.presenter

import com.zkyc.arms.base.view.IView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel

abstract class BasePresenter<V : IView> : IPresenter<V>,
    CoroutineScope by CoroutineScope(Job() + Dispatchers.Main) {

    var mView: V? = null

    override fun attachView(view: V) {
        this.mView = view
    }

    override fun detachView() {
        cancel()
        mView = null
    }
}