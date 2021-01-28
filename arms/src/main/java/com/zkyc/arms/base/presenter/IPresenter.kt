package com.zkyc.arms.base.presenter

import com.zkyc.arms.base.view.IView

interface IPresenter<in V : IView> {

    /**
     * 绑定视图
     * @param view 视图实例
     */
    fun attachView(view: V)

    /**
     * 销毁视图
     */
    fun detachView()
}