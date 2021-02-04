package com.zkyc.arms.map

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.blankj.utilcode.util.ResourceUtils

internal class MapStyleInitializer(appContext: Context, workerParameters: WorkerParameters) : CoroutineWorker(appContext, workerParameters) {

    override suspend fun doWork(): Result {
        val copySuccess = ResourceUtils.copyFileFromAssets(MapStyleUtil.STYLE_FOLDER_NAME, MapStyleUtil.STYLE_FOLDER_PATH)
        return if (copySuccess) Result.success() else Result.retry()
    }
}