package com.zkyc.arms.map.cluster

import com.amap.api.maps.model.LatLng

interface ClusterItem {

    /**
     * 返回聚合元素的地理位置
     */
    fun getPosition(): LatLng
}