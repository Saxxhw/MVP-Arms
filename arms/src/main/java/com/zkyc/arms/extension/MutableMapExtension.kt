package com.zkyc.arms.extension

fun <K, V> MutableMap<K, V>.putIfNotNull(k: K, v: V?) {
    if (v != null) put(k, v)
}