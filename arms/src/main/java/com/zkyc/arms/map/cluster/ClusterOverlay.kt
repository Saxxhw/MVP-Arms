package com.zkyc.arms.map.cluster

import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.Message
import android.util.LruCache
import android.util.TypedValue
import android.view.Gravity
import android.widget.TextView
import com.amap.api.maps.AMap
import com.amap.api.maps.AMap.OnCameraChangeListener
import com.amap.api.maps.AMap.OnMarkerClickListener
import com.amap.api.maps.AMapUtils
import com.amap.api.maps.model.*
import com.amap.api.maps.model.animation.AlphaAnimation
import com.amap.api.maps.model.animation.Animation
import com.zkyc.arms.R

class ClusterOverlay(
    context: Context,
    aMap: AMap,
    clusterSize: Int,
    clusterItems: List<ClusterItem>? = null
) : OnCameraChangeListener, OnMarkerClickListener {

    companion object {
        // MarkerHandler相关
        private const val ADD_CLUSTER_LIST = 0
        private const val ADD_SINGLE_CLUSTER = 1
        private const val UPDATE_SINGLE_CLUSTER = 2

        // SignClusterHandler相关
        private const val CALCULATE_CLUSTER = 0
        private const val CALCULATE_SINGLE_CLUSTER = 1
    }

    private var mAMap: AMap = aMap
    private var mContext: Context = context
    private var mClusterItems: MutableList<ClusterItem> = clusterItems?.toMutableList() ?: mutableListOf()
    private var mClusters: MutableList<Cluster> = mutableListOf()
    private var mClusterSize: Int = 0
    private var mClusterClickListener: ClusterClickListener? = null
    private var mClusterRender: ClusterRender? = null
    private val mAddMarkers = mutableListOf<Marker>()
    private var mClusterDistance: Double = 0.0
    private var mLruCache: LruCache<Int, BitmapDescriptor> = object : LruCache<Int, BitmapDescriptor>(80) {}
    private val mMarkerHandlerThread = HandlerThread("addMarker")
    private val mSignClusterThread = HandlerThread("calculateCluster")
    private lateinit var mMarkerHandler: Handler
    private lateinit var mSignClusterHandler: Handler
    private var mPXInMeters: Float = 0f
    private var mIsCanceled: Boolean = false

    init {
        //默认最多会缓存80张图片作为聚合显示元素图片,根据自己显示需求和app使用内存情况,可以修改数量
        mClusterSize = clusterSize
        mPXInMeters = mAMap.scalePerPixel
        mClusterDistance = (mPXInMeters * mClusterSize).toDouble()
        aMap.setOnCameraChangeListener(this)
        aMap.setOnMarkerClickListener(this)
        initThreadHandler()
        assignClusters()
    }

    /**
     * 设置聚合点的点击事件
     *
     * @param listener 点击事件
     */
    fun setOnClusterClickListener(listener: ClusterClickListener) {
        mClusterClickListener = listener
    }

    /**
     * 添加一个聚合点
     *
     * @param item 聚合点
     */
    fun addClusterItem(item: ClusterItem) {
        val message = Message.obtain().apply {
            what = CALCULATE_SINGLE_CLUSTER
            obj = item
        }
        mSignClusterHandler.sendMessage(message)
    }

    /**
     * 设置聚合元素的渲染样式，不设置则默认为气泡加数字形式进行渲染
     *
     * @param render 渲染样式
     */
    fun setClusterRenderer(render: ClusterRender) {
        mClusterRender = render
    }

    /**
     * 销毁资源
     */
    fun onDestroy() {
        mIsCanceled = true
        mSignClusterHandler.removeCallbacksAndMessages(null)
        mMarkerHandler.removeCallbacksAndMessages(null)
        mSignClusterThread.quit()
        mMarkerHandlerThread.quit()
        mAddMarkers.forEach { it.remove() }
        mAddMarkers.clear()
        mLruCache.evictAll()
    }

    //初始化Handler
    private fun initThreadHandler() {
        mMarkerHandlerThread.start()
        mSignClusterThread.start()
        mMarkerHandler = MarkerHandler(mMarkerHandlerThread.looper)
        mSignClusterHandler = SignClusterHandler(mSignClusterThread.looper)
    }

    override fun onCameraChange(p0: CameraPosition?) {

    }

    override fun onCameraChangeFinish(p0: CameraPosition?) {
        mPXInMeters = mAMap.scalePerPixel
        mClusterDistance = mPXInMeters * mClusterSize.toDouble()
        assignClusters()
    }

    override fun onMarkerClick(p0: Marker?): Boolean {
        if (mClusterClickListener == null) {
            return true
        }
        val cluster = p0?.`object` as? Cluster
        if (cluster != null) {
            mClusterClickListener?.onClick(p0, cluster.getClusterItems())
            return true
        }
        return false
    }

    /**
     * 将聚合元素添加至地图上
     */
    private fun addClusterToMap(clusters: List<Cluster>) {
        val removeMarkers = mutableListOf<Marker>()
        removeMarkers.addAll(mAddMarkers)
        val alphaAnimation = AlphaAnimation(1f, 0f)
        val animationListener = AnimationListener(removeMarkers)
        removeMarkers.forEach {
            it.setAnimation(alphaAnimation)
            it.setAnimationListener(animationListener)
            it.startAnimation()
        }
        clusters.forEach { addSingleClusterToMap(it) }
    }

    private val mADDAnimation = AlphaAnimation(0f, 1f)

    /**
     * 将单个聚合元素添加至地图显示
     *
     * @param cluster 聚合元素
     */
    private fun addSingleClusterToMap(cluster: Cluster) {
        val latlng = cluster.latLng
        val markerOptions = MarkerOptions()
        markerOptions.anchor(0.5f, 0.5f).icon(getBitmapDes(cluster)).position(latlng)
        val marker = mAMap.addMarker(markerOptions)
        marker.setAnimation(mADDAnimation)
        marker.setObject(cluster)

        marker.startAnimation()
        cluster.setMarker(marker)
        mAddMarkers.add(marker)
    }

    private fun calculateClusters() {
        mIsCanceled = false
        mClusters.clear()
        val visibleBounds = mAMap.projection.visibleRegion.latLngBounds
        mClusterItems.forEach { clusterItem ->
            if (mIsCanceled) {
                return
            }
            val latlng = clusterItem.getPosition()
            if (latlng != null) {
                if (visibleBounds.contains(latlng)) {
                    var cluster = getCluster(latlng, mClusters)
                    if (cluster == null) {
                        cluster = Cluster(latlng)
                        mClusters.add(cluster)
                    }
                    cluster.addClusterItem(clusterItem)
                }
            }
        }

        //复制一份数据，规避同步
        val clusters = mutableListOf<Cluster>()
        clusters.addAll(mClusters)
        val message = Message.obtain().apply {
            what = ADD_CLUSTER_LIST
            obj = clusters
        }
        if (mIsCanceled) {
            return
        }
        mMarkerHandler.sendMessage(message)
    }

    /**
     * 对点进行聚合
     */
    private fun assignClusters() {
        mIsCanceled = true
        mSignClusterHandler.removeMessages(CALCULATE_CLUSTER)
        mSignClusterHandler.sendEmptyMessage(CALCULATE_CLUSTER)
    }

    /**
     * 在已有的聚合基础上，对添加的单个元素进行聚合
     *
     * @param clusterItem 聚合元素
     */
    private fun calculateSingleCluster(clusterItem: ClusterItem) {
        val visibleBounds = mAMap.projection.visibleRegion.latLngBounds
        val latlng = clusterItem.getPosition()
        if (latlng != null) {
            if (!visibleBounds.contains(latlng)) {
                return
            }
            var cluster = getCluster(latlng, mClusters)
            if (cluster != null) {
                cluster.addClusterItem(clusterItem)
                val message = Message.obtain().apply {
                    what = UPDATE_SINGLE_CLUSTER
                    obj = cluster
                }
                mMarkerHandler.removeMessages(UPDATE_SINGLE_CLUSTER)
                mMarkerHandler.sendMessageDelayed(message, 5)
            } else {
                cluster = Cluster(latlng)
                mClusters.add(cluster)
                cluster.addClusterItem(clusterItem)
                val message = Message.obtain().apply {
                    what = ADD_SINGLE_CLUSTER
                    obj = cluster
                }
                mMarkerHandler.sendMessage(message)
            }
        }
    }

    /**
     * 根据一个点获取是否可以依附的聚合点，没有则返回null
     *
     * @param latLng 经纬度
     * @return 聚合点
     */
    private fun getCluster(latLng: LatLng, clusters: List<Cluster>): Cluster? {
        clusters.forEach {
            val clusterCenterPoint = it.latLng
            val distance = AMapUtils.calculateLineDistance(latLng, clusterCenterPoint)
            if (distance < mClusterDistance && mAMap.cameraPosition.zoom < 19) {
                return it
            }
        }
        return null
    }

    /**
     * 获取每个聚合点的绘制样式
     */
    private fun getBitmapDes(cluster: Cluster): BitmapDescriptor {
        val num = cluster.getClusterCount()
        var bitmapDescriptor: BitmapDescriptor?
        val bg = mClusterRender?.getDrawable(cluster)
        val textView = TextView(mContext)
        if (num == 1) {
            textView.setBackgroundResource(if (mClusterRender != null && bg != null) bg else R.drawable.ic_default_marker)
            bitmapDescriptor = BitmapDescriptorFactory.fromView(textView)
        } else {
            bitmapDescriptor = mLruCache.get(num)
            if (bitmapDescriptor == null) {
                val tile = num.toString()
                textView.text = tile
                textView.gravity = Gravity.CENTER
                textView.setTextColor(Color.BLACK)
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
                textView.setBackgroundResource(if (mClusterRender != null && bg != null) bg else R.drawable.ic_default_clusters)
                bitmapDescriptor = BitmapDescriptorFactory.fromView(textView)
                mLruCache.put(num, bitmapDescriptor)
            }
        }
        return bitmapDescriptor
    }

    /**
     * 更新已加入地图聚合点的样式
     */
    private fun updateCluster(cluster: Cluster) {
        val marker = cluster.getMarker()
        marker.setIcon(getBitmapDes(cluster))
    }

    //-----------------------辅助内部类用---------------------------------------------

    /**
     * 处理market添加，更新等操作
     */
    inner class MarkerHandler(looper: Looper) : Handler(looper) {

        override fun handleMessage(msg: Message) {
            when (msg.what) {
                ADD_CLUSTER_LIST -> {
                    @Suppress("UNCHECKED_CAST") val clusters = msg.obj as List<Cluster>
                    addClusterToMap(clusters)
                }
                ADD_SINGLE_CLUSTER -> {
                    val cluster = msg.obj as Cluster
                    addSingleClusterToMap(cluster)
                }
                UPDATE_SINGLE_CLUSTER -> {
                    val updateCluster = msg.obj as Cluster
                    updateCluster(updateCluster)
                }
            }
        }
    }

    /**
     * 处理聚合点算法线程
     */
    inner class SignClusterHandler(looper: Looper) : Handler(looper) {

        override fun handleMessage(msg: Message) {
            when (msg.what) {
                CALCULATE_CLUSTER -> calculateClusters()
                CALCULATE_SINGLE_CLUSTER -> {
                    val item = msg.obj as ClusterItem
                    mClusterItems.add(item)
                    calculateSingleCluster(item)
                }
            }
        }
    }

    /**
     * marker渐变动画，动画结束后将Marker删除
     */
    class AnimationListener(private val mRemoveMarkers: MutableList<Marker>) :
        Animation.AnimationListener {

        override fun onAnimationStart() {}

        override fun onAnimationEnd() {
            mRemoveMarkers.forEach { it.remove() }
            mRemoveMarkers.clear()
        }
    }
}