package com.huawei.hms.advancedlocationlibrary.utils

import android.app.Activity
import android.content.Context
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Icon
import android.util.Log
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.FragmentActivity
import com.huawei.hms.advancedlocationlibrary.data.model.holders.TaskData
import kotlin.random.Random

internal object Utils {

    private const val TAG = "${Constants.LOG_PREFIX}Utils"
    const val SEPARATOR = "::"

    val HIGH_ACCURACY_TASK_DATA = TaskData(
        interval = 0L,
        smallestDisplacement = 0F
    )

    val EFFICIENT_POWER_TASK_DATA = TaskData(
        interval = 3L,
        smallestDisplacement = 1F
    )

    val LOW_POWER_TASK_DATA = TaskData(
        interval = 10L,
        smallestDisplacement = 2F
    )

    val PASSIVE_TASK_DATA = TaskData(
        interval = 30L,
        smallestDisplacement = 5F
    )

    fun getCurrentTime(): Long {
        return System.currentTimeMillis()
    }

    fun getBoundAppsName(context: Context): String {
        Log.d(TAG, "getBoundAppsName()")
        val applicationInfo = context.applicationInfo
        val stringId = applicationInfo.labelRes

        return if (stringId == 0) {
            applicationInfo.nonLocalizedLabel.toString()
        } else {
            context.getString(stringId)
        }
    }

    fun getBoundAppsIcon(context: Context): Icon {
        val methodName = this::getBoundAppsIcon.name
        Log.d(TAG, "$methodName()")
        val drawable = context.packageManager.getApplicationIcon(context.packageName)

        val bitmapDrawable = try {
            drawable as BitmapDrawable
        } catch (e: ClassCastException) {
            Log.w(TAG, "$methodName --> Exception: ", e)

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                drawable as AdaptiveIconDrawable
            } else {
                drawable
            }
        }
        return Icon.createWithBitmap(bitmapDrawable.toBitmap())
    }

    fun getFragmentActivity(activity: Activity): FragmentActivity {
        return activity as FragmentActivity
    }
}