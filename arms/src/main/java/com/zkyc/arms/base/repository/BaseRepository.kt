package com.zkyc.arms.base.repository

import com.zkyc.arms.base.view.IView
import com.zkyc.arms.model.ApiException
import com.zkyc.arms.model.ApiResponse
import com.zkyc.arms.util.ExceptionUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.await

abstract class BaseRepository<T>(scope: CoroutineScope, protected val view: IView?) {

    init {
        scope.launch { execute() }
    }

    protected abstract fun start()

    protected abstract fun createCall(): Call<ApiResponse<T>>

    protected open fun processResponse(response: ApiResponse<T>): T = response.data

    protected abstract fun requestSucceed(data: T)

    protected open fun error(e: Exception) {
        view?.dismissProgress() // 关闭进度条对话框
        view?.showError() // 展示异常页
        view?.toast(ExceptionUtil.e2ResId(e)) // 提示错误信息
        if (ExceptionUtil.isTokenTimeout(e)) { // token超时引导用户重新登录
            view?.reLogin()
        }
    }

    private suspend fun execute() {
        try {
            val response = createCall().await()
            if (response.success()) {
                val data = processResponse(response)
                requestSucceed(data)
            } else {
                throw ApiException.create(response)
            }
        } catch (e: Exception) {
            error(e)
        }
    }
}