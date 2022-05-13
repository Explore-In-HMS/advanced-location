package com.huawei.hms.advancedlocationlibrary.managers

import TaskListener
import android.content.Context
import android.content.IntentSender
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.huawei.hms.advancedlocationlibrary.AdvancedLocation.Companion.coroutineScopeIO
import com.huawei.hms.advancedlocationlibrary.data.listeners.ResultListener
import com.huawei.hms.advancedlocationlibrary.data.model.holders.Position
import com.huawei.hms.advancedlocationlibrary.data.model.holders.Result
import com.huawei.hms.advancedlocationlibrary.data.model.holders.TaskData
import com.huawei.hms.advancedlocationlibrary.utils.Constants.DELAY_TIME
import com.huawei.hms.advancedlocationlibrary.utils.Constants.LOG_PREFIX
import com.huawei.hms.advancedlocationlibrary.utils.Constants.MAX_LOCATION_WAITING_TIME
import com.huawei.hms.advancedlocationlibrary.utils.Constants.TRIAL_COUNT
import com.huawei.hms.advancedlocationlibrary.utils.AdvancedLocationException
import com.huawei.hms.advancedlocationlibrary.utils.AdvancedLocationException.Companion.FAILED_TO_START_TASK
import com.huawei.hms.common.ApiException
import com.huawei.hms.common.ResolvableApiException
import com.huawei.hms.location.*
import com.huawei.hms.location.LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
import kotlinx.coroutines.*

/**
 *  Handles all work related to location. Settings, creating location update requests and obtaining
 *  location data.
 */
internal class LocationManager(private val context: Context) {

    companion object {
        private const val TAG = "${LOG_PREFIX}LocationManager"
    }

    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null
    private var mLocationCallback: AdvancedLocationCallback? = null
    private var mLocationRequests = ArrayList<LocationRequest>()
    private var mLocationSettings: LocationSettingsStates? = null
    private val job = SupervisorJob()
    private val coroutineScope = CoroutineScope(Dispatchers.IO + job)

    private var mLocation: Location? = null

    private var isAvailable = false
    private var availabilityTrial = 0

    init {
        init()
    }

    private fun init(activity: FragmentActivity? = null, block: (()->Unit)? = null) {
        runCatching {
            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        }.onSuccess {
            Log.d(TAG, "init --> FusedLocationProviderClient initialized.")
            checkLocationSettings(activity, block)
        }.onFailure {
            Log.w(TAG, "init --> FusedLocationProviderClient initialization failed. Error: ", it)
        }
    }

    private var isWaitingForActivityResult = false

    private fun checkLocationSettings(activity: FragmentActivity? = null, block: (()->Unit)? = null) {
        val settingsClient = LocationServices.getSettingsClient(context)
        val locationSettingsRequest = LocationSettingsRequest.Builder().run {
            addAllLocationRequests(mLocationRequests)
            build()
        }
        settingsClient.checkLocationSettings(locationSettingsRequest)
                .addOnSuccessListener {
                    mLocationSettings = it.locationSettingsStates
                    Log.d(TAG, "checkLocationSettings --> Success.")
                    isAvailable = true
                }
                .addOnFailureListener {
                    Log.w(TAG, "checkLocationSettings --> Failure: ", it)

                    if (isWaitingForActivityResult.not() && activity != null) {
                        if ((it as ApiException).statusCode == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
                            try {
                                val rae: ResolvableApiException = it as ResolvableApiException
                                rae.startResolutionForResult(activity, 0)
                                isWaitingForActivityResult = true
                            } catch (sie: IntentSender.SendIntentException) {
                                sie.printStackTrace()
                                isWaitingForActivityResult = false
                            }
                        }
                    }
                }
    }

    private suspend fun checkAvailability(activity: FragmentActivity? = null, block: (() -> Unit)? = null) = coroutineScope.launch {
        while (isAvailable.not()) {
            Log.d(TAG, "checkAvailability --> Not available.")
            delay(DELAY_TIME)
            availabilityTrial++
            if (availabilityTrial > TRIAL_COUNT) {
                init(activity, block)
                availabilityTrial = 0
                delay(DELAY_TIME*4)
                Log.d(TAG, "checkAvailability --> Not available. Initializing LocationManager again...")
            }
        }
        block?.invoke()
        Log.d(TAG, "checkAvailability --> Available.")
    }

    fun stopLocationUpdates() {
        mFusedLocationProviderClient?.removeLocationUpdates(mLocationCallback)
    }

    /**
     *  Requests location updates.
     *
     *  @param period to specify the duration of location update request in milliseconds.
     *  @return obtained results as Position with ResultListener.
     */
    fun startLocationUpdates(taskData: TaskData, listener: ResultListener<Position>) {
        Log.d(TAG, "startLocationUpdates()")
        mLocationCallback = AdvancedLocationCallback(listener)
        mFusedLocationProviderClient?.requestLocationUpdates(
            LocationRequest().also {
                it.interval = taskData.interval
                it.smallestDisplacement = taskData.smallestDisplacement
            },
            mLocationCallback,
            Looper.getMainLooper()
        )
    }

    inner class AdvancedLocationCallback(private val listener: ResultListener<Position>) : LocationCallback() {

        override fun onLocationAvailability(availability: LocationAvailability?) {
            super.onLocationAvailability(availability)
            Log.d(TAG, "onLocationAvailability --> Availability: $availability")
        }

        override fun onLocationResult(result: LocationResult?) {
            super.onLocationResult(result)
            Log.d(TAG, "onLocationResult --> Result: $result")

            result?.also {
                val location = it.locations.first()
                val position = Position(location.latitude,location.longitude)
                listener.onResult(position)
            }
        }
    }

    /**
     *  Gets last known location of user.
     */
    fun getLastLocation(activity: FragmentActivity, listener: TaskListener<Position>) = coroutineScopeIO.launch {
        Log.d(TAG, "getLastLocation()")
        checkAvailability(activity) {
            val task = mFusedLocationProviderClient?.lastLocation
                    ?.addOnSuccessListener {
                        if (it != null) {
                            mLocation = it
                            Log.d(TAG, "getLastLocation --> $it")
                            val position = Position(it.latitude, it.longitude)
                            val result = Result.Success(position)
                            listener.onCompleted(result)
                        }
                    }
                    ?.addOnFailureListener {
                        Log.w(TAG, "getLastLocation --> Error: ", it)
                        listener.onCompleted(Result.Failure(it))
                    }

            if (task == null) {
                Log.d(TAG, "getLastLocation --> Initializing LocationManager...")
                init()
                listener.onCompleted(
                        Result.Failure(
                                AdvancedLocationException(
                                        FAILED_TO_START_TASK,
                                        "FusedLocationProvider.getLastLocation task is null."
                                )
                        )
                )
            }
        }
    }

    /**
     *  Gets current location with one time request only.
     */
    fun getCurrentLocation(listener: ResultListener<Position>) {

        val locationRequest = LocationRequest().apply {
            numUpdates = 1
            needAddress = false
            maxWaitTime = MAX_LOCATION_WAITING_TIME
            priority = PRIORITY_BALANCED_POWER_ACCURACY
        }
        val task = mFusedLocationProviderClient
            ?.requestLocationUpdates(
                locationRequest,
                object : LocationCallback() {

                    override fun onLocationAvailability(availability: LocationAvailability?) {
                        super.onLocationAvailability(availability)
                        Log.d(TAG, "getCurrentLocation-onLocationAvailability --> Availability: $availability")
                    }

                    override fun onLocationResult(result: LocationResult?) {
                        super.onLocationResult(result)
                        Log.d(TAG, "getCurrentLocation-onLocationResult --> LocationResult: $result")
                        result?.lastLocation?.also {
                            listener.onResult(Position(it.latitude, it.longitude))
                        }
                    }
                }, Looper.getMainLooper())

        if (task == null) {
            Log.d(TAG, "getLastLocation --> Initializing LocationManager...")
            init()
        }
    }

}