package com.zkyc.arms.base.repository

import com.zkyc.arms.base.view.IView
import kotlinx.coroutines.CoroutineScope

abstract class BaseRepositoryWithProgress<T>(scope: CoroutineScope, view: IView?) :
    BaseRepository<T>(scope, view) {

    override fun start() {
        super.start()
        view?.showProgress()
    }

    override fun requestSucceed(data: T) {
        view?.dismissProgress()
    }
}