package com.zkyc.arms.util

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import com.blankj.utilcode.util.LogUtils
import okio.IOException
import java.io.File

class AudioPlayerUtil private constructor() {

    companion object {

        fun getInstance(): AudioPlayerUtil {
            return SingletonHolder.instance
        }
    }

    private object SingletonHolder {
        val instance = AudioPlayerUtil()
    }

    private var mediaPlayer: MediaPlayer? = null

    /**
     * 播放资源
     */
    fun play(context: Context, path: String, onCompletion: () -> Unit, onError: (Exception) -> Unit) {
        if (true == mediaPlayer?.isPlaying) {
            mediaPlayer?.stop()
        }
        try {
            mediaPlayer = MediaPlayer().apply {
                val uri = Uri.fromFile(File(path))
                setDataSource(context, uri)
                setOnPreparedListener { start() }
                setOnCompletionListener { onCompletion.invoke() }
                prepareAsync()
            }
        } catch (e: IllegalArgumentException) {
            onError.invoke(e)
            LogUtils.e(e)
        } catch (e: IOException) {
            onError.invoke(e)
            LogUtils.e(e)
        }
    }

    /**
     * 释放资源
     */
    fun destory() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
}