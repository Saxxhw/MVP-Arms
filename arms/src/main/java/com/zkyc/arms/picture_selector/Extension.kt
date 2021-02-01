package com.zkyc.arms.picture_selector

import android.app.Activity
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.listener.OnResultCallbackListener
import com.luck.picture.lib.tools.PictureFileUtils

/**
 * 启动图片选择器
 */
inline fun Activity.startPictureSelector(
    maxSelectNum: Int,
    crossinline onResult: (List<String>) -> Unit
) {
    PictureSelector.create(this)
        .openGallery(PictureMimeType.ofImage())
        .imageEngine(GlideEngine.instance)
        .isCamera(true)
        .isCompress(true)
        .isPreviewEggs(true)
        .maxSelectNum(maxSelectNum)
        .forResult(object : OnResultCallbackListener<LocalMedia> {

            override fun onResult(result: MutableList<LocalMedia>?) {
                if (result != null && result.isNotEmpty()) {
                    onResult.invoke(result.map { if (it.isCompressed) it.compressPath else it.path })
                }
            }

            override fun onCancel() {

            }
        })
}

/**
 * 清除缓存
 */
fun Activity.deleteCacheDirFile() {
    PictureFileUtils.deleteAllCacheDirFile(this)
}