package com.zkyc.arms.base

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.work.Configuration
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.kingja.loadsir.core.LoadSir
import com.tencent.mmkv.MMKV
import com.tencent.smtt.export.external.TbsCoreSettings
import com.tencent.smtt.sdk.QbSdk
import com.zkyc.arms.loadsir.EmptyCallback
import com.zkyc.arms.loadsir.ErrorCallback
import com.zkyc.arms.loadsir.LoadingCallback
import com.zkyc.arms.map.MapStyleInitializer
import java.util.*

abstract class BaseApplication : Application(), Configuration.Provider, Application.ActivityLifecycleCallbacks {

    // Activity栈
    private val activities: Stack<Activity> = Stack()

    /**
     * 获取当前Activity实例
     */
    val curActivity: Activity?
        get() = if (activities.isNullOrEmpty()) null else activities.lastElement()

    override fun onCreate() {
        super.onCreate()
        // 注册Activity生命周期监听事件
        registerActivityLifecycleCallbacks(this)
        // 加载反馈页管理框架初始化
        LoadSir.beginBuilder()
            .addCallback(LoadingCallback.newInstance())
            .addCallback(EmptyCallback.newInstance())
            .addCallback(ErrorCallback.newInstance())
            .setDefaultCallback(LoadingCallback::class.java)
            .commit()
        // 初始化 MMKV
        MMKV.initialize(this)
        // 初始化地图样式相关
        WorkManager.getInstance(this)
            .enqueue(OneTimeWorkRequestBuilder<MapStyleInitializer>().build())
        // 初始化x5内核
        initX5Web()
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder().setMinimumLoggingLevel(Log.INFO).build()
    }

    override fun onActivityStarted(p0: Activity) {}

    override fun onActivityResumed(p0: Activity) {}

    override fun onActivityCreated(p0: Activity, p1: Bundle?) {
        addActivity(p0)
    }

    override fun onActivityPaused(p0: Activity) {}

    override fun onActivityStopped(p0: Activity) {}

    override fun onActivityDestroyed(p0: Activity) {
        removeActivity(p0)
    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {}

    /**
     * activity入栈
     */
    private fun addActivity(activity: Activity?) {
        if (activity == null) {
            return
        }
        activities.add(activity)
    }

    /**
     * activity出栈
     */
    private fun removeActivity(activity: Activity?) {
        if (activity == null) {
            return
        }
        activities.remove(activity)
    }

    /**
     * 结束指定Activity
     */
    private fun finishActivity(activity: Activity?) {
        if (activity != null && !activity.isFinishing) {
            activity.finish()
        }
    }

    /**
     * 初始化x5内核
     */
    private fun initX5Web() {
        // 允许在非WiFi环境下载内核
        QbSdk.setDownloadWithoutWifi(true)
        // 开启优化方案
        val map = mapOf(
            Pair(TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER, true),
            Pair(TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE, true)
        )
        QbSdk.initTbsSettings(map)
        // 初始化
        QbSdk.initX5Environment(this, object : QbSdk.PreInitCallback {

            override fun onCoreInitFinished() {
                println("###### QbSdk ###### onCoreInitFinished")
            }

            override fun onViewInitFinished(p0: Boolean) {
                println("###### QbSdk ###### onViewInitFinished $p0")
            }
        })
    }

    /**
     * 结束全部Activity
     */
    fun finishAllActivities() {
        activities.forEach { it.finish() }
    }

    /**
     * 结束至某个activity
     */
    fun finishToActivity(cls: Class<*>) {
        activities.reversed().forEach {
            if (cls == it.javaClass) {
                return@forEach
            }
            finishActivity(it)
        }
    }
}