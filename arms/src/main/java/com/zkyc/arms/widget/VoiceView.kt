package com.zkyc.arms.widget

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import com.blankj.utilcode.util.*
import com.liulishuo.okdownload.DownloadTask
import com.liulishuo.okdownload.core.cause.EndCause
import com.liulishuo.okdownload.kotlin.enqueue2
import com.zkyc.arms.R
import com.zkyc.arms.progress.ACProgressFlower
import com.zkyc.arms.util.AudioPlayerUtil
import java.io.File

class VoiceView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : AppCompatTextView(context, attrs, defStyle), View.OnClickListener {

    companion object {

        // 默认展示时长
        private const val DEFAULT_DURATION = 0

        // 分隔符
        private const val DELIMITER = "/"

        /**
         * 音频文件存储位置
         */
        private val AUDIO_PATH: String = PathUtils.getExternalAppMusicPath()
    }

    /**
     * 音频地址
     */
    var path: String? = null

    /**
     * 时长
     */
    var duration: Int = DEFAULT_DURATION
        set(value) {
            field = value
            text = context.getString(R.string.vv_duration_format, value)
        }

    // 音频播放动画
    private var animDrawable: AnimationDrawable

    // 下载任务
    private var task: DownloadTask? = null

    // 加载框
    private var mProgress: ACProgressFlower? = null

    init {
        // 设置背景
        setBackgroundResource(R.drawable.vv_bg)
        animDrawable = ResourceUtils.getDrawable(R.drawable.vv_anim) as AnimationDrawable
        setCompoundDrawablesWithIntrinsicBounds(animDrawable, null, null, null)
        compoundDrawablePadding = SizeUtils.dp2px(6f)
        // 文本颜色
        setTextColor(Color.WHITE)
        // 绑定监听器
        setOnClickListener(this)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        task?.cancel()
        task = null
        AudioPlayerUtil.getInstance().destory()
    }

    override fun onClick(v: View?) {
        path?.let {
            // 在线地址
            if (RegexUtils.isURL(it)) {
                download(it) { file -> playAudio(file.path) }
            } else {
                playAudio(it)
            }
        }
    }

    /**
     * 下载文件
     */
    private fun download(url: String, onCompleted: (File) -> Unit) {
        task = DownloadTask
            .Builder(url, AUDIO_PATH, url.substringAfterLast(DELIMITER))
            .build()
        task?.enqueue2(
            onTaskStart = { showProgress() },
            onTaskEnd = { task, cause, _ ->
                dismissProgress()
                if (cause == EndCause.COMPLETED) {
                    val file = task.file
                    if (file != null && FileUtils.isFileExists(file)) {
                        onCompleted.invoke(file)
                    }
                }
            }
        )
    }

    /**
     * 播放音频
     */
    private fun playAudio(path: String) {
        animDrawable.start()
        AudioPlayerUtil.getInstance()
            .play(context, path, { animDrawable.stop() }, { animDrawable.stop() })
    }

    /**
     * 展示对话框
     */
    private fun showProgress() {
        if (mProgress == null) {
            mProgress = ACProgressFlower.Builder(context).build()
        }
        mProgress?.show()
    }

    /**
     * 隐藏对话框
     */
    private fun dismissProgress() {
        mProgress?.dismiss()
    }
}