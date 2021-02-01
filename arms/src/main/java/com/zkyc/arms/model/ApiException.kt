package com.zkyc.arms.model

data class ApiException(val code: Int, override val message: String) : RuntimeException(message) {

    companion object {

        /**
         * 生成自定义异常实例
         */
        fun create(response: ApiResponse<*>) = ApiException(response.code, response.message)
    }
}