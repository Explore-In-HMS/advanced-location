package com.hms.advancedlocationlibrary.data.model.holders

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TaskData(
    var interval: Long = 0L,
    var smallestDisplacement: Float = 0F
) : Parcelable