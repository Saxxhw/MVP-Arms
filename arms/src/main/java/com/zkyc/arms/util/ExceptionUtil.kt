package com.zkyc.arms.util

import com.zkyc.arms.R
import com.zkyc.arms.model.ApiException
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

object ExceptionUtil {

    // http相关错误码
    private const val ERR_401 = 401
    private const val ERR_402 = 402
    private const val ERR_403 = 403
    private const val ERR_404 = 404
    private const val ERR_422 = 422

    // 应用内相关错误码
    private const val ERR_10001 = 10001
    private const val ERR_10002 = 10002
    private const val ERR_10003 = 10003
    private const val ERR_10004 = 10004
    private const val ERR_10005 = 10005
    private const val ERR_10007 = 10007
    private const val ERR_10008 = 10008
    private const val ERR_10009 = 10009
    private const val ERR_100009 = 100009
    private const val ERR_1000 = 1000
    private const val ERR_1001 = 1001
    private const val ERR_1002 = 1002
    private const val ERR_20001 = 20001
    private const val ERR_20002 = 20002
    private const val ERR_30001 = 30001
    private const val ERR_40001 = 40001
    private const val ERR_500001 = 500001

    /**
     * 判断当前错误是否为“token超时”
     */
    fun isTokenTimeout(e: Exception): Boolean {
        return e is ApiException && ERR_10001 == e.code
    }

    /**
     * 将异常转换成文本资源字符串id
     */
    fun e2ResId(e: Exception) = when (e) {
        is UnknownHostException -> R.string.err_unknown_host
        is SocketTimeoutException -> R.string.err_socket_timeout
        is HttpException -> {
            when (e.code()) {
                ERR_401 -> R.string.err_401
                ERR_402 -> R.string.err_402
                ERR_403 -> R.string.err_403
                ERR_404 -> R.string.err_404
                ERR_422 -> R.string.err_422
                else -> R.string.unknown_error
            }
        }
        is ApiException -> when (e.code) {
            ERR_10001 -> R.string.err_10001
            ERR_10002 -> R.string.err_10002
            ERR_10003 -> R.string.err_10003
            ERR_10004 -> R.string.err_10004
            ERR_10005 -> R.string.err_10005
            ERR_10007 -> R.string.err_10007
            ERR_10008 -> R.string.err_10008
            ERR_10009 -> R.string.err_10009
            ERR_100009 -> R.string.err_100009
            ERR_1000 -> R.string.err_1000
            ERR_1001 -> R.string.err_1001
            ERR_1002 -> R.string.err_1002
            ERR_20001 -> R.string.err_20001
            ERR_20002 -> R.string.err_20002
            ERR_30001 -> R.string.err_30001
            ERR_40001 -> R.string.err_40001
            ERR_500001 -> R.string.err_500001
            else -> R.string.unknown_error
        }
        else -> R.string.unknown_error
    }
}