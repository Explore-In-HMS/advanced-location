package com.huawei.hms.advancedlocationlibrary.utils

class AdvancedLocationException(type: Int = 0) : Exception() {

    companion object {
        private const val PREFIX = "LiveLocationException: "

        const val UNKNOWN = 0
        const val NO_INTERNET_CONNECTION = 2
        const val MISSING_PERMISSION = 3
        const val FAILED_TO_START_TASK = 10
        const val WRONG_INTERVAL_VALUE = 11
    }

    constructor(type: Int, permissions: Array<String>) : this() {
        errorType = type
        if (type == MISSING_PERMISSION) {
            missingPermissions = permissions
        }
    }

    constructor(type: Int, messageExtension: String) : this() {
        errorType = type
        this.messageExtension = messageExtension
    }

    private var errorType = type
    private var missingPermissions: Array<String>? = null
    private var messageExtension: String? = null

    fun getType() = errorType

    override val message: String
        get() = when(errorType) {
            UNKNOWN -> "${PREFIX}An unknown problem occurred. ${messageExtension ?: ""}"
            NO_INTERNET_CONNECTION -> "${PREFIX}No internet connection. ${messageExtension ?: ""}"
            FAILED_TO_START_TASK -> "${PREFIX}Failed to start the ordered task. ${messageExtension ?: ""}"
            WRONG_INTERVAL_VALUE -> "${PREFIX}Wrong interval value is passed. Choose an interval annotated with @SharingInterval ${messageExtension ?: ""}"
            else -> "${PREFIX}Unknown type."
        }

    override val cause: Throwable?
        get() = super.cause
}