package com.zkyc.arms.loadsir

import com.kingja.loadsir.callback.Callback
import com.zkyc.arms.R

class ErrorCallback private constructor() : Callback() {

    companion object {
        fun newInstance() = ErrorCallback()
    }

    override fun onCreateView() = R.layout.state_error
}