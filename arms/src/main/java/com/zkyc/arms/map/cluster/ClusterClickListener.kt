package com.zkyc.arms.map.cluster

import com.amap.api.maps.model.Marker

interface ClusterClickListener {

    /**
     * 点击聚合点的回调函数
     * @param marker 点击的聚合点
     * @param clusterItems 聚合点所包含的元素
     */
    fun onClick(marker: Marker, clusterItems: List<ClusterItem>)
}