package com.zkyc.arms.progress

import android.content.Context
import android.graphics.Color
import com.zkyc.arms.R
import java.util.*

class ACProgressFlower private constructor(private val mBuilder: Builder) :
    ACProgressBaseDialog(mBuilder.mContext, mBuilder.mTheme) {

    private var mFlowerView: FlowerView? = null

    private var mSpinCount = 0
    private var mTimer: Timer? = null

    init {
        setOnDismissListener {
            mTimer?.cancel()
            mTimer = null
            mSpinCount = 0
            mFlowerView = null
        }
    }

    override fun show() {
        if (mFlowerView == null) {
            val size = (getMinimumSideOfScreen(mBuilder.mContext) * mBuilder.sizeRatio).toInt()
            mFlowerView = FlowerView(
                mBuilder.mContext,
                size,
                mBuilder.backgroundColor,
                mBuilder.backgroundAlpha,
                mBuilder.backgroundCornerRadius,
                mBuilder.petalThickness,
                mBuilder.petalCount,
                mBuilder.petalAlpha,
                mBuilder.borderPadding,
                mBuilder.centerPadding,
                mBuilder.themeColor,
                mBuilder.fadeColor,
            )
        }
        super.setContentView(mFlowerView!!)
        super.show()

        val delay = (1000 / mBuilder.speed).toLong()
        mTimer = Timer()
        mTimer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                val result = mSpinCount % mBuilder.petalCount
                mFlowerView?.updateFocusIndex(mBuilder.petalCount - 1 - result)
                if (result == 0) {
                    mSpinCount = 1
                } else {
                    mSpinCount++
                }
            }
        }, delay, delay)
    }

    class Builder(val mContext: Context, val mTheme: Int = R.style.ACPLDialog) {
        var sizeRatio = 0.25f
        var borderPadding = 0.55f
        var centerPadding = 0.27f

        var backgroundColor = Color.BLACK
        var themeColor = Color.WHITE
        var fadeColor = Color.DKGRAY

        var petalCount = 12
        var petalThickness = 9f
        var petalAlpha = 0.5f

        var backgroundCornerRadius = 20f
        var backgroundAlpha = 0.5f

        var speed = 9f

        fun build(): ACProgressFlower = ACProgressFlower(this)
    }
}