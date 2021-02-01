package com.zkyc.arms.glide

import android.widget.ImageView

/**
 * 加载图片
 * @param any 图片地址
 * @param placeholder 占位图
 * @param circleCrop 是否剪裁成圆形图
 */
fun ImageView.load(any: Any?, placeholder: Int? = null, circleCrop: Boolean = false) {
    GlideApp.with(this).load(any).apply {
        if (placeholder != null) {
            placeholder(placeholder)
        }
        if (circleCrop) {
            circleCrop()
        }
    }.into(this)
}