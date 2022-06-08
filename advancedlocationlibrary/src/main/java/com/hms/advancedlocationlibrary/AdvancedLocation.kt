package com.hms.advancedlocationlibrary

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import com.hms.advancedlocationlibrary.data.UpdateInterval.Companion.INTERVAL_15_SECONDS
import com.hms.advancedlocationlibrary.data.UpdateInterval.Companion.INTERVAL_30_SECONDS
import com.hms.advancedlocationlibrary.data.UpdateInterval.Companion.INTERVAL_FIVE_MINUTES
import com.hms.advancedlocationlibrary.data.listeners.ResultListener
import com.hms.advancedlocationlibrary.data.listeners.TaskListener
import com.hms.advancedlocationlibrary.data.model.enums.LocationType
import com.hms.advancedlocationlibrary.data.model.holders.Position
import com.hms.advancedlocationlibrary.database.ActivityTypeDatabase
import com.hms.advancedlocationlibrary.database.ActivityTypeResult
import com.hms.advancedlocationlibrary.database.BackgroundLocationResult
import com.hms.advancedlocationlibrary.database.LocationDatabase
import com.hms.advancedlocationlibrary.managers.ActivityManager
import com.hms.advancedlocationlibrary.managers.LocationManager
import com.hms.advancedlocationlibrary.managers.PermissionManager
import com.hms.advancedlocationlibrary.services.LocationService
import com.hms.advancedlocationlibrary.utils.AdvancedLocationException
import com.hms.advancedlocationlibrary.utils.Constants.FROM_ACTIVITY
import com.hms.advancedlocationlibrary.utils.Constants.INTERVAL
import com.hms.advancedlocationlibrary.utils.Constants.LOG_PREFIX
import com.hms.advancedlocationlibrary.utils.Constants.NOTIFICATION_DESCRIPTION
import com.hms.advancedlocationlibrary.utils.Constants.NOTIFICATION_TITLE
import com.hms.advancedlocationlibrary.utils.Utils.getFragmentActivity
import com.huawei.hms.location.LocationRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class AdvancedLocation {

    /**
     *  Requests Location Updates.
     *
     *  @param activity is required to check for location permission
     *  @param locationType can be HIGH_ACCURACY, EFFICIENT_POWER, LOW_POWER or PASSIVE
     *  @param interval interval(refresh frequency) --> default value = 0L
     *  @param resultListener returns an object of position(latitude,longitude)
     */
    @Throws(AdvancedLocationException::class)
    fun requestLocationUpdates(
        activity: Activity,
        locationType: LocationType,
        interval: Long = INTERVAL_15_SECONDS,
        resultListener: ResultListener<Position>
    ) {
        val methodName = "requestLocationUpdates"
        Log.d(TAG, "$methodName()")
        val fragmentActivity = getFragmentActivity(activity)

        mPermissionManager.doWithLocationPermission(fragmentActivity, {
            when (locationType) {
                LocationType.HIGH_ACCURACY -> requestHighAccuracyLocation(interval, resultListener)
                LocationType.EFFICIENT_POWER -> requestEfficientPowerLocation(
                    interval,
                    resultListener
                )
                LocationType.LOW_POWER -> requestLowPowerLocation(interval, resultListener)
                LocationType.PASSIVE -> requestPassiveLocation(interval, resultListener)
            }
        })
    }

    /**
     *  Requests Location Updates with custom values.
     *
     *  @param activity is required to check for location permission
     *  @param interval interval(refresh frequency) --> default value = 0L
     *  @param smallestDisplacement(max difference between positions) --> default value = 0F
     *  @param resultListener returns an object of position(latitude,longitude)
     */
    @Throws(AdvancedLocationException::class)
    fun requestCustomLocationUpdates(
        activity: Activity,
        interval: Long = INTERVAL_15_SECONDS,
        smallestDisplacement: Float = 0F,
        resultListener: ResultListener<Position>
    ) {
        val methodName = "requestLocationUpdates"
        Log.d(TAG, "$methodName()")
        val fragmentActivity = getFragmentActivity(activity)

        mPermissionManager.doWithLocationPermission(fragmentActivity, {
            mLocationManager.startCustomLocationUpdates(
                interval,
                smallestDisplacement,
                resultListener
            )
        })
    }

    /**
     *  Remove Location Update Request
     *
     */
    @Throws(AdvancedLocationException::class)
    fun removeLocationUpdateRequest() {
        val methodName = this::removeLocationUpdateRequest.name
        Log.d(TAG, "$methodName()")

        mLocationManager.stopLocationUpdates()
    }


    /**
     *  Used to request the most accurate location.
     *
     *  @param resultListener returns the position(latitude,longitude)
     */
    @Throws(AdvancedLocationException::class)
    private fun requestHighAccuracyLocation(
        interval: Long,
        resultListener: ResultListener<Position>
    ) {
        val methodName = this::requestHighAccuracyLocation.name
        Log.d(TAG, "$methodName()")

        mLocationManager.startLocationUpdates(
            LocationRequest.PRIORITY_HIGH_ACCURACY,
            interval,
            resultListener
        )
    }

    /**
     *  Requests Efficient Power(Block-level) Location Updates.
     *
     *  @param resultListener returns the position(latitude,longitude)
     */
    @Throws(AdvancedLocationException::class)
    private fun requestEfficientPowerLocation(
        interval: Long,
        resultListener: ResultListener<Position>
    ) {
        val methodName = this::requestEfficientPowerLocation.name
        Log.d(TAG, "$methodName()")

        mPermissionManager.doIfLocationPermitted {
            mLocationManager.startLocationUpdates(
                LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY,
                interval,
                resultListener
            )
        }
    }

    /**
     *  Requests Low Power(City-Level) Location Updates.
     *
     *  @param resultListener returns the position(latitude,longitude)
     */
    @Throws(AdvancedLocationException::class)
    private fun requestLowPowerLocation(
        interval: Long,
        resultListener: ResultListener<Position>
    ) {
        val methodName = this::requestLowPowerLocation.name
        Log.d(TAG, "$methodName()")

        mPermissionManager.doIfLocationPermitted {
            mLocationManager.startLocationUpdates(
                LocationRequest.PRIORITY_LOW_POWER,
                interval,
                resultListener
            )
        }
    }

    /**
     *  Requests the location with the optimal accuracy without additional power consumption.
     *
     *  @param resultListener returns the position(latitude,longitude)
     */
    @Throws(AdvancedLocationException::class)
    private fun requestPassiveLocation(
        interval: Long,
        resultListener: ResultListener<Position>
    ) {
        val methodName = this::requestPassiveLocation.name
        Log.d(TAG, "$methodName()")

        mPermissionManager.doIfLocationPermitted {
            mLocationManager.startLocationUpdates(
                LocationRequest.PRIORITY_NO_POWER,
                interval,
                resultListener
            )
        }
    }

    /**
     *  One time request to get Last Known Location.
     *
     *  @param activity is required in case initialization fails
     *  @param taskListener returns the position(latitude,longitude) on success
     *  or returns an exception on failure
     */
    @Throws(AdvancedLocationException::class)
    fun getLastLocation(
        activity: Activity,
        taskListener: TaskListener<Position>
    ) {
        val methodName = this::getLastLocation.name
        Log.d(TAG, "$methodName()")
        val fragmentActivity = getFragmentActivity(activity)

        mPermissionManager.doWithLocationPermission(fragmentActivity, {
            mLocationManager.getLastLocation(fragmentActivity, taskListener)
        })
    }

    /**
     *  One time request to get Current Location.
     *
     *  @param activity is required for permission
     *  @param resultListener returns the position(latitude,longitude)
     */
    @Throws(AdvancedLocationException::class)
    fun getCurrentLocation(
        activity: Activity,
        resultListener: ResultListener<Position>
    ) {
        val methodName = this::getCurrentLocation.name
        Log.d(TAG, "$methodName()")
        val fragmentActivity = getFragmentActivity(activity)

        mPermissionManager.doWithLocationPermission(fragmentActivity, {
            mLocationManager.getCurrentLocation(resultListener)
        })
    }

    /**
     *  Starts background location updates
     *
     *  @param activity is required for permission
     *  @param notificationTitle is title for Notification that displayed along with foreground service (if Empty app name will be displayed)
     *  @param notificationDescription is description for Notification that displayed along with foreground service(if Empty default desc will be displayed)
     *  @param updateInterval location update refresh frequency (Default value = 5 Mins)
     *  @returns BackgroundLocationResult that accesses to RoomDB (only get)
     */
    @Throws(AdvancedLocationException::class)
    fun startBackgroundLocationUpdates(
        activity: Activity,
        notificationTitle: String,
        notificationDescription: String,
        updateInterval: Long = INTERVAL_FIVE_MINUTES
    ): BackgroundLocationResult {
        val methodName = this::startBackgroundLocationUpdates.name
        Log.d(TAG, "$methodName()")
        val fragmentActivity = getFragmentActivity(activity)

        mPermissionManager.doWithLocationPermission(
            fragmentActivity, {
                Intent(mContext, LocationService::class.java).also {
                    it.putExtra(NOTIFICATION_TITLE, notificationTitle)
                    it.putExtra(NOTIFICATION_DESCRIPTION, notificationDescription)
                    it.putExtra(INTERVAL, updateInterval)
                    it.putExtra(FROM_ACTIVITY, fragmentActivity.javaClass.name)
                    mContext.startForegroundService(it)
                }
            }, true
        )


        return mBackgroundLocationResult
    }

    /**
     *  Stops background location updates
     */
    @Throws(AdvancedLocationException::class)
    fun stopBackgroundLocationUpdates() {
        val methodName = this::stopBackgroundLocationUpdates.name
        Log.d(TAG, "$methodName()")

        getContext().stopService(mLocationBackgroundService)
    }

    /**
     *  Requests the current activity type and stores the result to Room DB
     */
    @Throws(AdvancedLocationException::class)
    fun getActivityType(
        activity: Activity
    ): ActivityTypeResult {
        val methodName = this::getActivityType.name
        Log.d(TAG, "$methodName()")
        val fragmentActivity = getFragmentActivity(activity)

        mPermissionManager.doWithActivityPermission(fragmentActivity) {
            mActivityManager.createActivityIdentificationRequest(INTERVAL_30_SECONDS)
        }

        return mActivityTypeResult
    }

    /**
     *  Clears stored locations in the DB
     */
    @Throws(AdvancedLocationException::class)
    fun clearLocationDB() {
        val methodName = this::clearLocationDB.name
        Log.d(TAG, "$methodName()")

        mLocationDatabase?.clearDB()
    }

    /**
     *  Clears stored activity types in the DB
     */
    @Throws(AdvancedLocationException::class)
    fun clearActivityTypeDB() {
        val methodName = this::clearActivityTypeDB.name
        Log.d(TAG, "$methodName()")

        mActivityTypeDatabase?.clearDB()
    }

    companion object {
        private const val TAG = "${LOG_PREFIX}AdvancedLocation"

        @SuppressLint("StaticFieldLeak")
        internal lateinit var mContext: Context
        internal val mPermissionManager by lazy { PermissionManager(mContext) }
        internal val mLocationManager by lazy { LocationManager(mContext) }
        internal val mActivityManager by lazy { ActivityManager(mContext) }
        internal val mLocationDatabase by lazy {
            LocationDatabase.getLocationDatabase(mContext)?.locationDao()
        }
        private val mBackgroundLocationResult by lazy { BackgroundLocationResult() }
        internal val mActivityTypeDatabase by lazy {
            ActivityTypeDatabase.getActivityTypeDatabase(
                mContext
            )?.activityTypeDao()
        }
        private val mActivityTypeResult by lazy { ActivityTypeResult() }
        private val mLocationBackgroundService by lazy {
            Intent(
                mContext,
                LocationService::class.java
            )
        }

        private val mainJob = SupervisorJob()
        internal val coroutineScopeIO = CoroutineScope(Dispatchers.IO + mainJob)

        fun init(context: Context) {
            Log.d(TAG, "${this::init.name}()")
            mContext = context.applicationContext
        }

        fun getContext() = mContext
    }

}