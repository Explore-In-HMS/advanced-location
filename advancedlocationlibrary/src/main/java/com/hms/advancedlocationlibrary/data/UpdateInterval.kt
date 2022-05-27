package com.hms.advancedlocationlibrary.data

import androidx.annotation.LongDef

@LongDef(
    UpdateInterval.INTERVAL_0_SECONDS,
    UpdateInterval.INTERVAL_15_SECONDS,
    UpdateInterval.INTERVAL_30_SECONDS,
    UpdateInterval.INTERVAL_ONE_MINUTE,
    UpdateInterval.INTERVAL_TWO_MINUTES,
    UpdateInterval.INTERVAL_FIVE_MINUTES
)
@Retention(AnnotationRetention.SOURCE)
annotation class UpdateInterval {

    companion object {
        const val INTERVAL_0_SECONDS = 0L
        const val INTERVAL_15_SECONDS = 15*1000L
        const val INTERVAL_30_SECONDS = 30*1000L
        const val INTERVAL_ONE_MINUTE = 60*1000L
        const val INTERVAL_TWO_MINUTES = 2*60*1000L
        const val INTERVAL_FIVE_MINUTES = 5*60*1000L
    }
}
