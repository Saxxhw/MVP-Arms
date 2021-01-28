package com.zkyc.arms.extension

import androidx.viewpager2.widget.ViewPager2

/**
 * ViewPager翻到下一页
 */
fun ViewPager2.nextPage() {
    currentItem = ++currentItem
}

/**
 * ViewPager翻到下一页
 */
fun ViewPager2.previousPage() {
    currentItem = --currentItem
}