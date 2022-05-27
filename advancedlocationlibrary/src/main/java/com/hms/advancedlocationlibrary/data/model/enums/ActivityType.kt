package com.hms.advancedlocationlibrary.data.model.enums

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.hms.advancedlocationlibrary.AdvancedLocation
import com.hms.advancedlocationlibrary.R

enum class ActivityType {

    VEHICLE
    {
        override val code = 100
        override val type = "VEHICLE"
        override val iconBitmap: Bitmap get() {
            return BitmapFactory.decodeResource(AdvancedLocation.getContext().resources, R.drawable.ic_vehicle)
        }
    },
    BIKE
    {
        override val code = 101
        override val type = "BIKE"
        override val iconBitmap: Bitmap get() {
            return BitmapFactory.decodeResource(AdvancedLocation.getContext().resources, R.drawable.ic_bike)
        }
    },
    FOOT
    {
        override val code = 102
        override val type = "FOOT"
        override val iconBitmap: Bitmap get() {
            return BitmapFactory.decodeResource(AdvancedLocation.getContext().resources, R.drawable.ic_foot)
        }
    },
    STILL
    {
        override val code = 103
        override val type = "STILL"
        override val iconBitmap: Bitmap get() {
            return BitmapFactory.decodeResource(AdvancedLocation.getContext().resources, R.drawable.ic_still)
        }
    },
    OTHERS
    {
        override val code = 104
        override val type = "OTHERS"
        override val iconBitmap: Bitmap get() {
            return BitmapFactory.decodeResource(AdvancedLocation.getContext().resources, R.drawable.ic_others)
        }
    },
    WALKING
    {
        override val code = 107
        override val type = "WALKING"
        override val iconBitmap: Bitmap get() {
            return BitmapFactory.decodeResource(AdvancedLocation.getContext().resources, R.drawable.ic_walking)
        }
    },
    RUNNING
    {
        override val code = 108
        override val type = "RUNNING"
        override val iconBitmap: Bitmap get() {
            return BitmapFactory.decodeResource(AdvancedLocation.getContext().resources, R.drawable.ic_running)
        }
    };

    companion object {

        fun toString(activityType: Int? = 104) = when(activityType) {
            VEHICLE.code -> VEHICLE.type
            BIKE.code -> BIKE.type
            FOOT.code -> FOOT.type
            STILL.code -> STILL.type
            WALKING.code -> WALKING.type
            RUNNING.code -> RUNNING.type
            else -> OTHERS.type
        }

        fun getActivityType(activityCode: Int? = 104) = when(activityCode) {
            VEHICLE.code -> VEHICLE
            BIKE.code -> BIKE
            FOOT.code -> FOOT
            STILL.code -> STILL
            WALKING.code -> WALKING
            RUNNING.code -> RUNNING
            else -> OTHERS
        }
    }

    abstract val code: Int
    abstract val type: String
    abstract val iconBitmap: Bitmap
}