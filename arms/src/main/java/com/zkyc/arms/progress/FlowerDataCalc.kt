package com.zkyc.arms.progress

import android.graphics.Color
import kotlin.math.cos
import kotlin.math.sin

class FlowerDataCalc(segmentCount: Int) {

    private val mCosValues: DoubleArray = DoubleArray(segmentCount)
    private val mSinValues: DoubleArray = DoubleArray(segmentCount)

    fun getSegmentsCoordinates(
        rectSize: Int,
        outPadding: Int,
        inPadding: Int,
        segmentCount: Int,
        finalWidth: Int
    ): List<PetalCoordinate> {
        val coordinates: MutableList<PetalCoordinate> = ArrayList(segmentCount)
        val centerY = rectSize / 2
        val centerX = finalWidth / 2
        val outRadius = (rectSize - outPadding) / 2
        val inRadius = inPadding / 2
        for (i in 0 until segmentCount) {
            val xOutOffset = outRadius * mCosValues[i]
            val yOutOffset = outRadius * mSinValues[i]
            val startX = centerX - xOutOffset
            val startY = centerY + yOutOffset
            val xInOffset = inRadius * mCosValues[i]
            val yInOffset = inRadius * mSinValues[i]
            val endX = centerX - xInOffset
            val endY = centerY + yInOffset
            val coordinate =
                PetalCoordinate(startX.toFloat(), startY.toFloat(), endX.toFloat(), endY.toFloat())
            coordinates.add(coordinate)
        }
        return coordinates
    }

    fun getSegmentsColors(
        themeColor: Int,
        fadeColor: Int,
        petalCount: Int,
        petalAlpha: Int
    ): IntArray {
        val colors = IntArray(petalCount)
        val themeRed: Int = Color.red(themeColor)
        val themeGreen: Int = Color.green(themeColor)
        val themeBlue: Int = Color.blue(themeColor)
        val fadeRed: Int = Color.red(fadeColor)
        val fadeGreen: Int = Color.green(fadeColor)
        val fadeBlue: Int = Color.blue(fadeColor)
        val redDelta = (fadeRed - themeRed).toDouble() / (petalCount - 1)
        val greenDelta = (fadeGreen - themeGreen).toDouble() / (petalCount - 1)
        val blueDelta = (fadeBlue - themeBlue).toDouble() / (petalCount - 1)
        for (i in 0 until petalCount) {
            val color: Int = Color.argb(
                petalAlpha,
                (themeRed + redDelta * i).toInt(),
                (themeGreen + greenDelta * i).toInt(),
                (themeBlue + blueDelta * i).toInt()
            )
            colors[i] = color
        }
        return colors
    }

    init {
        val angleUnit = Math.PI * 2 / segmentCount
        for (i in 0 until segmentCount) {
            val currentAngle = angleUnit * i
            mCosValues[i] = cos(currentAngle)
            mSinValues[i] = sin(currentAngle)
        }
    }
}