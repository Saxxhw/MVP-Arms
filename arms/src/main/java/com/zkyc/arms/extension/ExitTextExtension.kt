package com.zkyc.arms.extension

import android.widget.EditText

/**
 * 移动光标至末尾
 */
fun EditText.moveLast() {
    setSelection(length())
}