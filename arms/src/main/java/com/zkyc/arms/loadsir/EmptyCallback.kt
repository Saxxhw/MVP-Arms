package com.zkyc.arms.loadsir

import com.kingja.loadsir.callback.Callback
import com.zkyc.arms.R

class EmptyCallback private constructor() : Callback() {

    companion object {
        fun newInstance() = EmptyCallback()
    }

    override fun onCreateView() = R.layout.state_empty
}