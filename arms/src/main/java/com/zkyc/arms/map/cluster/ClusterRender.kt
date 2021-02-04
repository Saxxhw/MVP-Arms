package com.zkyc.arms.map.cluster

interface ClusterRender {

    /**
     * 根据聚合点的元素数据返回渲染背景样式
     */
    fun getDrawable(cluster: Cluster): Int
}