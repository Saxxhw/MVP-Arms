package com.zkyc.arms.feature

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.afollestad.assent.Permission
import com.afollestad.assent.askForPermissions
import com.afollestad.assent.rationale.createDialogRationale
import com.google.zxing.Result
import com.king.zxing.CameraScan
import com.king.zxing.DecodeConfig
import com.king.zxing.DecodeFormatManager
import com.king.zxing.DefaultCameraScan
import com.king.zxing.analyze.MultiFormatAnalyzer
import com.zkyc.arms.R
import com.zkyc.arms.base.activity.BaseVBActivity
import com.zkyc.arms.databinding.ActivityQrCodeScanBinding

class QRCodeScanActivity : BaseVBActivity<ActivityQrCodeScanBinding>(),
    View.OnClickListener,
    CameraScan.OnScanResultCallback {

    companion object {

        /**
         * 启动[QRCodeScanActivity]页
         */
        fun startWith(activity: Activity, requestCode: Int) = with(activity) {
            val starter = Intent(activity, QRCodeScanActivity::class.java)
            startActivityForResult(starter, requestCode)
        }

        /* ************ 获取返回值相关 ************ */

        private const val ARG_RESULT_QR_CODE = "result_qr_code"

        fun obtainResult(data: Intent?): String? {
            return data?.getStringExtra(ARG_RESULT_QR_CODE)
        }
    }

    private var mCameraScan: CameraScan? = null

    override fun inflateVB(inflater: LayoutInflater) = ActivityQrCodeScanBinding.inflate(inflater)

    override fun initViewAndEvent(savedInstanceState: Bundle?) {
        // 初始化扫描
        initCameraScan()
        // 开始扫描
        startCamera()
        // 绑定监听事件
        mBinding.ivFlashLight.setOnClickListener(this)
    }

    override fun onDestroy() {
        // 释放相机
        mCameraScan?.release()
        super.onDestroy()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_flash_light -> mCameraScan?.run {
                val isTorch = isTorchEnabled
                enableTorch(!isTorch)
                v.isSelected = !isTorch
            }
        }
    }

    override fun onScanResultCallback(result: Result?): Boolean {
        setResult(RESULT_OK, Intent().putExtra(ARG_RESULT_QR_CODE, result?.text))
        finish()
        return false
    }

    /**
     * 初始化
     */
    private fun initCameraScan() {
        // 生成扫码实例
        mCameraScan = DefaultCameraScan(this, mBinding.previewView)
        // 初始化解码配置
        val decodeConfig = DecodeConfig()
        decodeConfig
            .setHints(DecodeFormatManager.QR_CODE_HINTS) // 设置仅识别二维码
            .setFullAreaScan(false) // 关闭全区域识别
            .areaRectRatio = 0.5f // 设置识别区域比例
        // 扫码相关的配置设置
        mCameraScan
            ?.setVibrate(true) // 振动
            ?.setPlayBeep(true) // 播放音效
            ?.setAnalyzer(MultiFormatAnalyzer(decodeConfig)) // 设置分析器
            ?.setOnScanResultCallback(this) // 设置回调
    }

    /**
     * 开始扫描
     */
    private fun startCamera() {
        val rationaleHandler = createDialogRationale(R.string.permission_request_title) {
            onPermission(Permission.CAMERA, R.string.permission_camera)
        }
        askForPermissions(Permission.CAMERA, rationaleHandler = rationaleHandler) { result ->
            if (result.isAllGranted(Permission.CAMERA)) {
                // 权限全部通过
                mCameraScan?.startCamera()
            } else {
                // 部分或全部权限未通过
                finish()
            }
        }
    }
}