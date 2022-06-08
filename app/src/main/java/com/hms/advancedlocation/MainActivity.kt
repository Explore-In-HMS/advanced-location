package com.hms.advancedlocation

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.hms.advancedlocation.Utils.getTime
import com.hms.advancedlocation.databinding.ActivityMainBinding
import com.hms.advancedlocationlibrary.AdvancedLocation
import com.hms.advancedlocationlibrary.data.UpdateInterval
import com.hms.advancedlocationlibrary.data.model.enums.ActivityType
import com.hms.advancedlocationlibrary.data.model.enums.LocationType
import com.hms.advancedlocationlibrary.database.ActivityTypeResult
import com.hms.advancedlocationlibrary.database.BackgroundLocationResult
import com.hms.advancedlocationlibrary.database.dto.LocationDto

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val advancedLocation = AdvancedLocation()
    private val TAG = "MainActivity"

    private val CHECK_INTERVAL = 5000L
    private lateinit var mainHandler: Handler
    private lateinit var getLocationFromDBTask: Runnable
    private lateinit var secondHandler: Handler
    private lateinit var getActivityTypeFromDBTask: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initClickListeners()
        binding.tvLocation.movementMethod = ScrollingMovementMethod()
    }

    @SuppressLint("SetTextI18n")
    private fun initClickListeners() {
        binding.buttonRequestLocationUpdates.setOnClickListener {
            binding.tvLocation.text = ""
            requestLocationUpdates()
            //requestCustomLocationUpdates()
        }

        binding.buttonStopLocationUpdates.setOnClickListener {
            // stops location updates
            advancedLocation.removeLocationUpdateRequest()
            showToast("Location update request removed!")
        }

        binding.buttonGetCurrentLocation.setOnClickListener {
            getCurrentLocation()
        }

        binding.buttonGetLastLocation.setOnClickListener {
            getLastLocation()
        }

        binding.buttonRequestBackgroundLocation.setOnClickListener {
            binding.tvLocation.text = "*** BACKGROUND LOCATION ***\n"
            startBackgroundLocation()
        }

        binding.buttonStopBackgroundLocation.setOnClickListener {
            advancedLocation.stopBackgroundLocationUpdates()
            mainHandler.removeCallbacks(getLocationFromDBTask)
        }

        binding.buttonGetActivityType.setOnClickListener {
            binding.tvActivityType.text = "-"
            getActivityType()
        }
    }

    private fun requestLocationUpdates() {
        // Request Location every 15 sec with HIGH_ACCURACY
        advancedLocation.requestLocationUpdates(
            this,
            LocationType.HIGH_ACCURACY,
            UpdateInterval.INTERVAL_15_SECONDS
        ) {
            val locationStr = "Lat: ${it.latitude}\nLong: ${it.longitude}"
            Log.d(TAG, locationStr)
            binding.tvLocation.append(locationStr + "      " + (getTime() ?: "") + "\n")
            showToast("Location Updated!")
        }
    }

    private fun requestCustomLocationUpdates() {
        // Request Location every 20 sec with 1F displacement
        advancedLocation.requestCustomLocationUpdates(
            this,
            interval = 20L,
            smallestDisplacement = 1F
        ) {
            val locationStr = "Lat: ${it.latitude}\nLong: ${it.longitude}"
            Log.d(TAG, locationStr)
            binding.tvLocation.append(locationStr + "      " + (getTime() ?: "") + "\n")
            showToast("Location Updated!")
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getCurrentLocation() {
        advancedLocation.getCurrentLocation(
            this
        ) {
            val locationStr = "Lat: ${it.latitude} Long: ${it.longitude}"
            Log.d(TAG, locationStr)
            binding.tvLocation.text =
                "*** Current Location ***\n $locationStr  ${getTime() ?: ""} \n"
            showToast("Current location received!")
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getLastLocation() {
        advancedLocation.getLastLocation(
            this
        ) {
            val position = it.value
            val locationStr = "Lat: ${position.latitude}\n Long: ${position.longitude}"
            Log.d(TAG, locationStr)
            binding.tvLocation.text = "*** Last Location ***\n $locationStr  ${getTime() ?: ""} \n"
            showToast("Last location received!")
        }
    }

    private fun startBackgroundLocation() {
        advancedLocation.startBackgroundLocationUpdates(
            this,
            "Advanced Location Demo",
            "Reaching your location in background..",
            UpdateInterval.INTERVAL_FIVE_MINUTES
        ).let {
            checkForLocationUpdatesFromDB(it)
        }
    }

    private fun checkForLocationUpdatesFromDB(backgroundLocationResult: BackgroundLocationResult) {
        mainHandler = Handler(Looper.getMainLooper())
        getLocationFromDBTask = object : Runnable {
            override fun run() {
                val location = backgroundLocationResult.getLastLocation()
                updateLocationTextView(location)
                mainHandler.postDelayed(this, CHECK_INTERVAL)
            }
        }
        mainHandler.post(getLocationFromDBTask)
    }

    private fun updateLocationTextView(location: LocationDto?) {
        location?.let {
            val locationStr = "Lat: ${it.latitude}\nLong: ${it.longitude}     ${getTime() ?: ""} \n"
            binding.tvLocation.append(locationStr)
        }
    }

    private fun getActivityType() {
        advancedLocation.clearActivityTypeDB() // to remove cached data
        advancedLocation.getActivityType(this).let {
            checkForActivityTypeUpdateFromDB(it)
        }
    }

    private fun checkForActivityTypeUpdateFromDB(activityTypeResult: ActivityTypeResult) {
        secondHandler = Handler(Looper.getMainLooper())
        getActivityTypeFromDBTask = object : Runnable {
            override fun run() {
                val activityType = activityTypeResult.getActivityType()
                updateActivityTypeTextView(activityType)
                secondHandler.postDelayed(this, CHECK_INTERVAL)
            }
        }
        secondHandler.post(getActivityTypeFromDBTask)
    }

    private fun updateActivityTypeTextView(activityType: ActivityType?) {
        activityType?.let {
            binding.tvActivityType.text = it.type
            secondHandler.removeCallbacks(getActivityTypeFromDBTask)
        }
    }

    private fun clearBackgroundLocationDB() {
        advancedLocation.clearLocationDB()
    }

    private fun clearActivityTypeDB() {
        advancedLocation.clearActivityTypeDB()
    }

    private fun showToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        mainHandler.removeCallbacks(getLocationFromDBTask)
        secondHandler.removeCallbacks(getActivityTypeFromDBTask)
    }
}