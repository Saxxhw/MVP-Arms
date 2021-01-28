package com.zkyc.arms.loadsir

import android.content.Context
import android.view.View
import com.kingja.loadsir.callback.Callback
import com.zkyc.arms.R

class LoadingCallback private constructor() : Callback() {

    companion object {
        fun newInstance() = LoadingCallback()
    }

    override fun onCreateView() = R.layout.state_loading

    override fun onReloadEvent(context: Context?, view: View?) = true
}