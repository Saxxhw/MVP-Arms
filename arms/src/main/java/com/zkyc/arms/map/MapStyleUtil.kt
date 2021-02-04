package com.zkyc.arms.map

import com.amap.api.maps.model.CustomMapStyleOptions
import com.blankj.utilcode.util.PathUtils

object MapStyleUtil {

    /**
     * 地图相关样式文件夹名
     */
    internal const val STYLE_FOLDER_NAME = "map_style"

    /**
     * 地图相关样式文件夹路径
     */
    internal val STYLE_FOLDER_PATH = "${PathUtils.getInternalAppFilesPath()}/$STYLE_FOLDER_NAME"

    /**
     * style文件存储位置
     */
    private val ASSETS_STYLE_PATH = "$STYLE_FOLDER_PATH/style.data"

    /**
     * style_extra文件存储位置
     */
    private val ASSETS_STYLE_EXTRA_PATH = "$STYLE_FOLDER_PATH/style_extra.data"

    /**
     * 获取自定义地图样式
     */
    fun getCustomMapStyleOptions() =
        CustomMapStyleOptions().setEnable(true).setStyleDataPath(ASSETS_STYLE_PATH)
            .setStyleExtraPath(ASSETS_STYLE_EXTRA_PATH)
}