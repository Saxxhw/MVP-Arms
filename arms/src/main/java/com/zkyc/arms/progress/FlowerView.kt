package com.zkyc.arms.progress

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.View
import java.lang.ref.WeakReference

@SuppressLint("ViewConstructor")
class FlowerView(
    context: Context,
    size: Int,
    bgColor: Int,
    bgAlpha: Float,
    bgCornerRadius: Float,
    petalThickness: Float,
    petalCount: Int,
    petalAlpha: Float,
    borderPadding: Float,
    centerPadding: Float,
    themeColor: Int,
    fadeColor: Int
) : View(context) {

    private val mSize: Int

    private val mPetalCount: Int
    private val mBackgroundCornerRadius: Float

    private var mBackgroundRect: RectF
    private var mBackgroundPaint: Paint
    private var mPetalPaint: Paint

    private var mPetalCoordinates: List<PetalCoordinate>
    private var mPetalColors: IntArray

    private lateinit var mHandler: Handler
    private var mCurrentFocusIndex: Int = 0

    init {

        val looper = Looper.myLooper()
        if (looper != null) {
            mHandler = FlowerUpdateHandler(looper, this)
        }

        mSize = size
        mPetalCount = petalCount
        mBackgroundCornerRadius = bgCornerRadius

        mBackgroundPaint = Paint().apply {
            isAntiAlias = true
            color = bgColor
            alpha = (bgAlpha * 255).toInt()
        }

        mPetalPaint = Paint().apply {
            isAntiAlias = true
            strokeWidth = petalThickness
            strokeCap = Paint.Cap.ROUND
        }

        mBackgroundRect = RectF(0f, 0f, mSize.toFloat(), mSize.toFloat())

        val calc = FlowerDataCalc(petalCount)
        mPetalCoordinates = calc.getSegmentsCoordinates(mSize, (borderPadding * mSize).toInt(), (centerPadding * mSize).toInt(), petalCount, mSize)
        mPetalColors = calc.getSegmentsColors(themeColor, fadeColor, petalCount, (petalAlpha * 255).toInt())
    }

    fun updateFocusIndex(index: Int) {
        mCurrentFocusIndex = index
        mHandler.sendEmptyMessage(0)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(mSize, mSize)
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.drawRoundRect(
            mBackgroundRect,
            mBackgroundCornerRadius,
            mBackgroundCornerRadius,
            mBackgroundPaint
        )
        var coordinate: PetalCoordinate
        for (i in 0 until mPetalCount) {
            coordinate = mPetalCoordinates[i]
            val index = (mCurrentFocusIndex + i) % mPetalCount
            mPetalPaint.color = mPetalColors[index]
            canvas?.drawLine(
                coordinate.startX,
                coordinate.startY,
                coordinate.endX,
                coordinate.endY,
                mPetalPaint
            )
        }
    }

    private class FlowerUpdateHandler(looper: Looper, flowerView: FlowerView) : Handler(looper) {

        private val reference: WeakReference<FlowerView> = WeakReference(flowerView)

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            reference.get()?.invalidate()
        }
    }
}