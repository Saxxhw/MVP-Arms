package com.zkyc.arms.base.adapter.viewpager

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

abstract class BaseSimplePagerAdapter : FragmentStateAdapter {

    constructor(activity: AppCompatActivity) : super(activity)

    constructor(fragment: Fragment) : super(fragment)
}