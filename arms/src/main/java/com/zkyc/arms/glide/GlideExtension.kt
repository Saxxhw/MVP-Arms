package com.zkyc.arms.glide

import com.bumptech.glide.annotation.GlideExtension
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.annotation.GlideOption
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.BaseRequestOptions
import com.bumptech.glide.request.RequestOptions

@GlideExtension
class GlideExtension private constructor() {

    companion object {

        @GlideOption
        fun circleCrop(options: BaseRequestOptions<*>): BaseRequestOptions<*> {
            return options.apply(RequestOptions().circleCrop())
        }
    }
}

@GlideModule
class AppGlideModule : AppGlideModule() {

    /**
     * 完全禁用清单解析，改善 Glide 的初始启动时间，避免尝试解析元数据时的一些潜在问题
     */
    override fun isManifestParsingEnabled() = false
}

