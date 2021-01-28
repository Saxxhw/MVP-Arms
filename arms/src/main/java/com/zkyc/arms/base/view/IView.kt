package com.zkyc.arms.base.view

import androidx.annotation.StringRes

interface IView {

    /**
     * 弹出提示语
     * @param msg 提示内容
     */
    fun toast(msg: String)

    /**
     * 弹出提示语
     * @param msgId 提示内容资源id
     */
    fun toast(@StringRes msgId: Int)

    /**
     * 展示进度条
     */
    fun showProgress()

    /**
     * 关闭进度条后，执行某些操作
     */
    fun dismissProgress()

    /**
     * 展示加载中状态
     */
    fun showLoading()

    /**
     * 展示空状态
     */
    fun showEmpty()

    /**
     * 展示异常状态
     */
    fun showError()

    /**
     * 展示成功状态
     */
    fun showSuccess()
}