package com.zkyc.arms.map.cluster

import android.graphics.drawable.Drawable

interface ClusterRender {

    /**
     * 根据聚合点的元素数据返回渲染背景样式
     */
    fun getDrawable(cluster: Cluster):Drawable
}