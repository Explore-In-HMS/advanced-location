package com.hms.advancedlocationlibrary.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.hms.advancedlocationlibrary.AdvancedLocation.Companion.mActivityManager
import com.hms.advancedlocationlibrary.utils.Constants.LOG_PREFIX

internal class ActivityBroadcastReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "${LOG_PREFIX}ActivityBroadcastReceiver"
        const val ACTION_PROCESS_ACTIVITY = "com.hms.advancedlocation.ACTION_PROCESS_ACTIVITY"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "onReceive --> Intent.action: ${intent?.action}")
        handleIntent(context, intent)
    }

    private fun handleIntent(context: Context?, intent: Intent?) {
        if (intent == null || intent.action == null)
            return

        if (intent.action!!.contains(ACTION_PROCESS_ACTIVITY)) {
            mActivityManager.getAndSaveActivityData(intent)
            unregisterSelf(context, intent)
        }
    }

    private fun unregisterSelf(context: Context?, intent: Intent) {
        val action = intent.action
        if (action != null) {
            mActivityManager.removeActivityIdentificationRequest(action)
        }

        try {
            context?.unregisterReceiver(this)
        } catch (e: IllegalArgumentException) {
            Log.w(TAG, "onReceive --> Couldn't unregister BroadcastReceiver. Error: ", e)
        }
    }
}