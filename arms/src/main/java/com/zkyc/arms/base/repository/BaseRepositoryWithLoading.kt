package com.zkyc.arms.base.repository

import com.zkyc.arms.base.view.IView
import kotlinx.coroutines.CoroutineScope

abstract class BaseRepositoryWithLoading<T : List<*>>(scope: CoroutineScope, view: IView?) :
    BaseRepository<T>(scope, view) {

    override fun start() {
        view?.showLoading()
    }

    override fun requestSucceed(data: T) {
        if (data.isNullOrEmpty()) {
            view?.showEmpty()
            return
        }
        view?.showSuccess()
    }
}