package com.hms.advancedlocationlibrary.utils

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.hms.advancedlocationlibrary.AdvancedLocation
import com.hms.advancedlocationlibrary.services.LocationService
import com.hms.advancedlocationlibrary.utils.Constants.LOG_PREFIX
import com.hms.advancedlocationlibrary.utils.device.DeviceUtils
import com.hms.advancedlocationlibrary.R

internal object NotificationUtils {

    private const val TAG = "${LOG_PREFIX}NotificationUtils"

    private val CHANNEL_ID_LOCATION_SHARING =
        "${AdvancedLocation.getContext().packageName}.locationSharing"
    private val CHANNEL_NAME_LOCATION_SHARING =
        AdvancedLocation.getContext().getString(R.string.channel_name_location_sharing)
    private val CHANNEL_DESCRIPTION_LOCATION_SHARING =
        AdvancedLocation.getContext().getString(R.string.channel_description_location_sharing)

    /**
     *  Prepares notification for LocationSharingService.
     *
     *  @param activityClass is used to create the Intent to be triggered when user clicks the
     *  notification.
     */
    @SuppressLint("UnspecifiedImmutableFlag")
    fun getForegroundServiceNotification(
        context: Context,
        titleP: String? = null,
        descriptionP: String? = null,
        activityClass: Class<out Activity?>?
    ): Notification {
        Log.d(TAG, "getForegroundServiceNotification()")

        val notificationIntent = Intent(context, activityClass)
        val pendingIntent = PendingIntent.getActivity(
            context,
            LocationService.INTENT_REQUEST_CODE, notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        var title = Utils.getBoundAppsName(AdvancedLocation.getContext())
        var description = context.getString(R.string.sharing_location)

        if (titleP.isNullOrBlank().not())
            title = titleP!!
        if (descriptionP.isNullOrBlank().not())
            description = descriptionP!!


        return getNotification(
            context,
            title,
            description,
            pendingIntent,
            CHANNEL_ID_LOCATION_SHARING,
            CHANNEL_NAME_LOCATION_SHARING,
            CHANNEL_DESCRIPTION_LOCATION_SHARING
        )
    }

    /**
     *  Prepares notification with given parameters.
     *
     *  @return notification with bound app's icon.
     */
    private fun getNotification(
        context: Context,
        title: String,
        description: String,
        pendingIntent: PendingIntent,
        channelId: String,
        channelName: String,
        channelDescription: String
    ): Notification {

        return if (DeviceUtils.isAboveOreo()) {
            createNotificationChannel(context, channelId, channelName, channelDescription)
            Notification.Builder(context, channelId)
        } else {
            Notification.Builder(context)
        }.run {
            setContentTitle(title)
            setContentText(description)
            setSmallIcon(Utils.getBoundAppsIcon(context))
            setContentIntent(pendingIntent)
            build()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(
        context: Context,
        channelId: String,
        channelName: String,
        channelDescription: String
    ) {
        val channel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_DEFAULT
        ).also {
            it.description = channelDescription
        }
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }
}