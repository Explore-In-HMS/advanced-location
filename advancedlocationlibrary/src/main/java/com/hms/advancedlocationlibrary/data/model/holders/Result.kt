package com.hms.advancedlocationlibrary.data.model.holders

sealed class Result<T> {

    data class Success<T>(var data: T) : Result<T>()
    data class Failure<T>(var exception: Exception? = null) : Result<T>()

    fun isSuccess(): Boolean {
        return this is Success
    }

    val value: T get() = (this as Success).data
    internal val reason: Exception? get() = (this as Failure).exception
}
