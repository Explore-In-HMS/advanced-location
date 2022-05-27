package com.hms.advancedlocationlibrary.data.listeners

import com.hms.advancedlocationlibrary.data.model.holders.Result

fun interface TaskListener<T> {
    fun onCompleted(result: Result<T>)
}