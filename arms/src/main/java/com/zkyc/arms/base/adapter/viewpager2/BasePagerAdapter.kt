package com.zkyc.arms.base.adapter.viewpager2

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

abstract class BasePagerAdapter<T> : BaseSimplePagerAdapter {

    private val mList: List<T>

    constructor(activity: AppCompatActivity, list: List<T>) : super(activity) {
        mList = list
    }

    constructor(fragment: Fragment, list: List<T>) : super(fragment) {
        mList = list
    }

    override fun getItemCount() = mList.size

    override fun createFragment(position: Int) = createFragment(position, mList[position])

    protected abstract fun createFragment(position: Int, item: T): Fragment
}