package com.hms.advancedlocationlibrary.utils

internal object Constants {
    const val FROM_ACTIVITY = "FromActivity"
    const val LOG_PREFIX = "LIVE_"
    const val TASK_REQUEST = "TaskRequest"
    const val DELAY_TIME = 200L
    const val MAX_LOCATION_WAITING_TIME = 12*1000L //12 Seconds.
    const val EXPIRED_TASK_DELETE_PERIOD: Long = 60*60*1000L // ONE_HOUR
    const val TRIAL_COUNT = 3
    const val MIN_ACCEPTED_ACTIVITY_POSSIBILITY = 40
    const val ABORT_TASK = "AbortTask"
    const val TRUE = "true"
    const val FALSE = "false"
}