
package com.hms.lib.livelocation.utils.device

import android.annotation.SuppressLint
import android.content.Context
import android.os.BatteryManager
import android.os.Build
import android.util.Log
import com.huawei.hms.advancedlocationlibrary.utils.Constants

internal object DeviceUtils {

    private const val TAG = "${Constants.LOG_PREFIX}DeviceUtils"

    fun isAboveQ() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
    fun isAboveOreo() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

    @SuppressLint("ObsoleteSdkInt")
    fun isAboveM() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
}