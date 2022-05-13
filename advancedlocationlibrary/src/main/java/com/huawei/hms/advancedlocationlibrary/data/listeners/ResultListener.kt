
package com.huawei.hms.advancedlocationlibrary.data.listeners

fun interface ResultListener<T> {
    fun onResult(result: T)
}