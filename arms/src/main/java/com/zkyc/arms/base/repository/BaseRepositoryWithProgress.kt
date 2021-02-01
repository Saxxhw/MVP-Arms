package com.zkyc.arms.base.repository

import com.zkyc.arms.base.view.IView
import kotlinx.coroutines.CoroutineScope

abstract class BaseRepositoryWithProgress<T>(scope: CoroutineScope, v: IView?) :
    BaseRepository<T>(scope, v) {

    override fun start() {
        mView?.showProgress()
    }

    override fun requestSucceed(data: T) {
        mView?.dismissProgress()
    }
}