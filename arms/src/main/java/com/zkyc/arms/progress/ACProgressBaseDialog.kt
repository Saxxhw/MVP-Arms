package com.zkyc.arms.progress

import android.app.Dialog
import android.content.Context
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.view.WindowManager
import com.zkyc.arms.R
import kotlin.math.min

abstract class ACProgressBaseDialog @JvmOverloads constructor(
    context: Context,
    theme: Int = R.style.ACPLDialog
) : Dialog(context, theme) {

    init {
        window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
    }

    fun getMinimumSideOfScreen(context: Context): Int {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        @Suppress("DEPRECATION") val display = windowManager.defaultDisplay
        val size = Point()
        @Suppress("DEPRECATION")
        display.getSize(size)
        return min(size.x, size.y)
    }
}