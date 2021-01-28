package com.zkyc.arms.extension

import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.TextView

/**
 * 设置是否显示文本
 */
fun TextView.setTransformationMethod(show: Boolean) {
    transformationMethod =
        if (show) HideReturnsTransformationMethod.getInstance() else PasswordTransformationMethod.getInstance()
}

/**
 * 清空控件相关信息
 */
fun TextView.clear() {
    text = null
    tag = null
}