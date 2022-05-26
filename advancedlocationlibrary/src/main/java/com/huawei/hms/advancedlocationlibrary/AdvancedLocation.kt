package com.huawei.hms.advancedlocationlibrary

import TaskListener
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import com.huawei.hms.advancedlocationlibrary.data.UpdateInterval.Companion.INTERVAL_15_SECONDS
import com.huawei.hms.advancedlocationlibrary.managers.LocationManager
import com.huawei.hms.advancedlocationlibrary.data.listeners.ResultListener
import com.huawei.hms.advancedlocationlibrary.utils.Constants.LOG_PREFIX
import com.huawei.hms.advancedlocationlibrary.utils.AdvancedLocationException
import com.huawei.hms.advancedlocationlibrary.utils.Utils.getFragmentActivity
import com.huawei.hms.advancedlocationlibrary.data.model.enums.LocationType
import com.huawei.hms.advancedlocationlibrary.data.model.holders.*
import com.huawei.hms.advancedlocationlibrary.database.ActivityTypeDatabase
import com.huawei.hms.advancedlocationlibrary.database.ActivityTypeResult
import com.huawei.hms.advancedlocationlibrary.database.BackgroundLocationResult
import com.huawei.hms.advancedlocationlibrary.database.LocationDatabase
import com.huawei.hms.advancedlocationlibrary.managers.ActivityManager
import com.huawei.hms.advancedlocationlibrary.managers.PermissionManager
import com.huawei.hms.advancedlocationlibrary.services.LocationService
import com.huawei.hms.advancedlocationlibrary.utils.Constants.FROM_ACTIVITY
import com.huawei.hms.advancedlocationlibrary.utils.Constants.TASK_REQUEST
import com.huawei.hms.advancedlocationlibrary.utils.Utils.EFFICIENT_POWER_TASK_DATA
import com.huawei.hms.advancedlocationlibrary.utils.Utils.HIGH_ACCURACY_TASK_DATA
import com.huawei.hms.advancedlocationlibrary.utils.Utils.LOW_POWER_TASK_DATA
import com.huawei.hms.advancedlocationlibrary.utils.Utils.PASSIVE_TASK_DATA
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class AdvancedLocation {

    /**
     *  Requests Location Updates.
     *
     *  @param activity is required for pendingIntent of notification.
     *  @param locationType can be HIGH_ACCURACY, EFFICIENT_POWER, LOW_POWER or PASSIVE
     *  @param resultListener returns the position(latitude,longitude)
     */
    @Throws(AdvancedLocationException::class)
    fun requestLocationUpdates(activity: Activity, locationType: LocationType, resultListener: ResultListener<Position>) {
        val methodName = this::requestLocationUpdates.name
        Log.d(TAG, "$methodName()")
        val fragmentActivity = getFragmentActivity(activity)

        mPermissionManager.doWithLocationPermission(fragmentActivity,{
            when(locationType){
                LocationType.HIGH_ACCURACY -> requestHighAccuracyLocation(resultListener)
                LocationType.EFFICIENT_POWER -> requestEfficientPowerLocation(resultListener)
                LocationType.LOW_POWER -> requestLowPowerLocation(resultListener)
                LocationType.PASSIVE -> requestPassiveLocation(resultListener)
            }
        })
    }

    /**
     *  Remove Location Update Request
     *
     */
    @Throws(AdvancedLocationException::class)
    fun removeLocationUpdateRequest() {
        val methodName = this::requestLocationUpdates.name
        Log.d(TAG, "$methodName()")

        mLocationManager.stopLocationUpdates()
    }


    /**
     *  Requests High Accuracy Location Updates.
     *
     *  @param resultListener returns the position(latitude,longitude)
     */
    @Throws(AdvancedLocationException::class)
    private fun requestHighAccuracyLocation(resultListener: ResultListener<Position>) {
        val methodName = this::requestHighAccuracyLocation.name
        Log.d(TAG, "$methodName()")

        mLocationManager.startLocationUpdates(HIGH_ACCURACY_TASK_DATA, resultListener)
    }

    /**
     *  Requests Efficient Power Location Updates.
     *
     *  @param resultListener returns the position(latitude,longitude)
     */
    @Throws(AdvancedLocationException::class)
    private fun requestEfficientPowerLocation(resultListener: ResultListener<Position>) {
        val methodName = this::requestEfficientPowerLocation.name
        Log.d(TAG, "$methodName()")

        mPermissionManager.doIfLocationPermitted {
            mLocationManager.startLocationUpdates(EFFICIENT_POWER_TASK_DATA, resultListener)
        }
    }

    /**
     *  Requests Low Power Location Updates.
     *
     *  @param resultListener returns the position(latitude,longitude)
     */
    @Throws(AdvancedLocationException::class)
    private fun requestLowPowerLocation(resultListener: ResultListener<Position>) {
        val methodName = this::requestLowPowerLocation.name
        Log.d(TAG, "$methodName()")

        mPermissionManager.doIfLocationPermitted {
            mLocationManager.startLocationUpdates(LOW_POWER_TASK_DATA, resultListener)
        }
    }

    /**
     *  Requests Passive Location Updates.
     *
     *  @param resultListener returns the position(latitude,longitude)
     */
    @Throws(AdvancedLocationException::class)
    private fun requestPassiveLocation(resultListener: ResultListener<Position>) {
        val methodName = this::requestPassiveLocation.name
        Log.d(TAG, "$methodName()")

        mPermissionManager.doIfLocationPermitted {
            mLocationManager.startLocationUpdates(PASSIVE_TASK_DATA, resultListener)
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
    fun getLastLocation(activity: Activity, taskListener: TaskListener<Position>) {
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
    fun getCurrentLocation(activity: Activity, resultListener: ResultListener<Position>) {
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
     *  @param updateInterval location update refresh frequency (Default value = 15 secs)
     *  @param resultListener returns the position(latitude,longitude)
     *  @returns BackgroundLocationResult that includes 2 getter methods
     */
    @Throws(AdvancedLocationException::class)
    fun startBackgroundLocationUpdates(
        activity: Activity,
        updateInterval : Long = INTERVAL_15_SECONDS
    ) : BackgroundLocationResult {
        val methodName = this::startBackgroundLocationUpdates.name
        Log.d(TAG, "$methodName()")
        val fragmentActivity = getFragmentActivity(activity)

        Intent(mContext, LocationService::class.java).also {
            it.putExtra(TASK_REQUEST, TaskData(interval = updateInterval))
            it.putExtra(FROM_ACTIVITY, fragmentActivity.javaClass.name)
            mContext.startForegroundService(it)
        }

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
     *  Stops background location updates
     */
    @Throws(AdvancedLocationException::class)
    fun getActivityType() : ActivityTypeResult {
        val methodName = this::getActivityType.name
        Log.d(TAG, "$methodName()")

        mActivityManager.createActivityIdentificationRequest(INTERVAL_15_SECONDS)

        return mActivityTypeResult
    }

    @Throws(AdvancedLocationException::class)
    fun clearDB() {
        val methodName = this::clearDB.name
        Log.d(TAG, "$methodName()")

        mLocationDatabase?.clearDB()
    }

    companion object {
        private const val TAG = "${LOG_PREFIX}AdvancedLocation"

        @SuppressLint("StaticFieldLeak")
        internal lateinit var mContext: Context
        internal val mPermissionManager by lazy { PermissionManager(mContext) }
        internal val mLocationManager by lazy { LocationManager(mContext) }
        internal val mActivityManager by lazy { ActivityManager(mContext) }
        internal val mLocationDatabase by lazy { LocationDatabase.getLocationDatabase(mContext)?.locationDao() }
        private val mBackgroundLocationResult by lazy { BackgroundLocationResult() }
        internal val mActivityTypeDatabase by lazy { ActivityTypeDatabase.getActivityTypeDatabase(mContext)?.activityTypeDao() }
        private val mActivityTypeResult by lazy { ActivityTypeResult() }
        private val mLocationBackgroundService  by lazy { Intent(mContext,LocationService::class.java) }

        private val mainJob = SupervisorJob()
        internal val coroutineScopeIO = CoroutineScope(Dispatchers.IO + mainJob)

        fun init(context: Context) {
            Log.d(TAG, "${this::init.name}()")
            mContext = context.applicationContext
        }

        fun getContext() = mContext
    }

}