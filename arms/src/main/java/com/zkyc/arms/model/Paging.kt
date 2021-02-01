package com.zkyc.arms.model

data class Paging<out T>(val total: Int, val list: List<T>)