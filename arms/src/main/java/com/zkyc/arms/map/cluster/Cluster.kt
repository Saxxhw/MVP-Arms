package com.zkyc.arms.map.cluster

import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.Marker

class Cluster(val latLng: LatLng) {

    private val clusterItems: MutableList<ClusterItem> = mutableListOf()
    private lateinit var marker: Marker

    fun addClusterItem(item: ClusterItem) {
        this.clusterItems.add(item)
    }

    fun getClusterCount(): Int {
        return this.clusterItems.size
    }

    fun getClusterItems() = this.clusterItems

    fun setMarker(marker: Marker) {
        this.marker = marker
    }

    fun getMarker() = this.marker
}