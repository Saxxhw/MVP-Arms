package com.zkyc.arms.model

data class ApiResponse<T>(val code: Int, val message: String, val data: T) {

    companion object {
        /**
         * 请求成功标志
         */
        const val SUCCEEDED = 1
    }

    /**
     * 判断请求是否成功
     */
    fun success() = SUCCEEDED == code
}