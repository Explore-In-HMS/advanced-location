package com.hms.advancedlocationlibrary.managers

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.hms.advancedlocationlibrary.AdvancedLocation.Companion.mActivityTypeDatabase
import com.hms.advancedlocationlibrary.data.UpdateInterval
import com.hms.advancedlocationlibrary.database.dto.ActivityTypeDto
import com.hms.advancedlocationlibrary.receivers.ActivityBroadcastReceiver
import com.hms.advancedlocationlibrary.utils.AdvancedLocationException
import com.hms.advancedlocationlibrary.utils.Constants.LOG_PREFIX
import com.hms.advancedlocationlibrary.utils.Constants.MIN_ACCEPTED_ACTIVITY_POSSIBILITY
import com.huawei.hms.location.ActivityIdentification
import com.huawei.hms.location.ActivityIdentificationResponse

/**
 *  Handles activity identification settings, requests and obtaining activity data.
 */
internal class ActivityManager(private val context: Context) {

    companion object {
        private const val TAG = "${LOG_PREFIX}ActivityManager"
        private const val PENDING_INTENT_REQUEST_CODE = 417
    }

    private val mActivityIdentificationService = ActivityIdentification.getService(context)

    @SuppressLint("UnspecifiedImmutableFlag")
    @JvmName("getPendingIntent2")
    private fun getPendingIntent(
        action: String = ActivityBroadcastReceiver.ACTION_PROCESS_ACTIVITY
    ): PendingIntent? {
        val intent = Intent(context, ActivityBroadcastReceiver::class.java)
        intent.action = action
        return PendingIntent.getBroadcast(
            context,
            PENDING_INTENT_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    fun createActivityIdentificationRequest(@UpdateInterval interval: Long) {
        mActivityIdentificationService
            .createActivityIdentificationUpdates(interval, getPendingIntent())
            .addOnSuccessListener {
                Log.d(TAG, "createActivityIdentificationRequest-onSuccess")
            }
            .addOnFailureListener {
                Log.e(TAG, "createActivityIdentificationRequest-onFailure --> Error: ", it)
            }
    }

    /**
     *  Cancels specified activity identification request.
     */
    fun removeActivityIdentificationRequest(action: String) {
        mActivityIdentificationService
            .deleteActivityIdentificationUpdates(getPendingIntent(action))
            .addOnSuccessListener {
                Log.d(TAG, "removeActivityIdentificationRequest-onSuccess")
            }
            .addOnFailureListener {
                Log.e(TAG, "removeActivityIdentificationRequest-onFailure --> Error: ", it)
            }
    }

    /**
     *  Called only if intent action is "com.huawei.hms.location.ACTION_PROCESS_ACTIVITY".
     */
    fun getAndSaveActivityData(intent: Intent) {
        Log.d(TAG, "getAndSaveActivityData()")

        val action = intent.action

        if (action != null && action.contains(ActivityBroadcastReceiver.ACTION_PROCESS_ACTIVITY)) {

            val activityIdentificationResponse =
                ActivityIdentificationResponse.getDataFromIntent(intent)
            val activityData = activityIdentificationResponse.mostActivityIdentification

            if (activityData.possibility >= MIN_ACCEPTED_ACTIVITY_POSSIBILITY) {
                try {
                    mActivityTypeDatabase?.insertActivityType(
                        ActivityTypeDto(
                            activityTypeCode = activityData.identificationActivity
                        )
                    )
                } catch (e: AdvancedLocationException) {
                    Log.e(TAG, "getActivityData --> Error: ", e)
                }
            } else {
                Log.d(
                    TAG,
                    "getAndSaveActivityData --> Activity data possibility is lower than minimum accepted ratio. Not saving..."
                )
            }
        }
    }
}