package com.zkyc.arms.map

import android.app.Activity
import com.afollestad.assent.Permission
import com.afollestad.assent.runWithPermissions
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.blankj.utilcode.util.ToastUtils

class LocationUtil private constructor() {

    companion object {

        fun getInstance(): LocationUtil {
            return SingletonHolder.instance
        }
    }

    private object SingletonHolder {
        val instance = LocationUtil()
    }

    private var mLocationClient: AMapLocationClient? = null

    /**
     * 开始定位
     */
    fun startLocation(activity: Activity, onChanged: (AMapLocation) -> Unit) = with(activity) {
        // 申请定位权限
        runWithPermissions(Permission.ACCESS_COARSE_LOCATION, Permission.ACCESS_FINE_LOCATION) {
            // 获得用户权限
            if (mLocationClient == null) {
                mLocationClient = AMapLocationClient(activity)
            }
            mLocationClient?.run {
                val locationClientOption = AMapLocationClientOption().apply {
                    isOnceLocation = true // 使用单次定位
                    isNeedAddress = true // 返回地址信息
                    isLocationCacheEnable = false // 禁用缓存机制
                }
                setLocationOption(locationClientOption) // 启用定位设置
                setLocationListener { location -> // 绑定监听事件
                    if (location != null && 0 == location.errorCode) {
                        onChanged.invoke(location)
                    } else {
                        ToastUtils.showShort(location?.errorInfo)
                    }
                    mLocationClient?.stopLocation()
                }
                startLocation() // 开始定位
            }
        }
    }

    /**
     * 销毁资源
     */
    fun onDestroy() {
        mLocationClient?.onDestroy()
        mLocationClient = null
    }
}