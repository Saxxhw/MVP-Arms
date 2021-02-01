package com.zkyc.arms.base.repository

import com.zkyc.arms.base.view.IView
import kotlinx.coroutines.CoroutineScope

abstract class BaseRepositoryWithLoading<T : List<*>>(scope: CoroutineScope, v: IView?) :
    BaseRepository<T>(scope, v) {

    override fun start() {
        mView?.showLoading()
    }

    override fun requestSucceed(data: T) {
        if (data.isNullOrEmpty()) {
            mView?.showEmpty()
            return
        }
        mView?.showSuccess()
    }
}