package com.hms.advancedlocation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.hms.advancedlocationlibrary.AdvancedLocation
import com.hms.advancedlocationlibrary.data.UpdateInterval
import com.hms.advancedlocationlibrary.data.listeners.TaskListener
import com.hms.advancedlocationlibrary.data.model.enums.LocationType
import com.hms.advancedlocationlibrary.data.model.holders.Position

class MainActivity : AppCompatActivity() {

    private val advancedLocation = AdvancedLocation()
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonStartService = findViewById<Button>(R.id.buttonStartService)
        val buttonGetCurrentLocation = findViewById<Button>(R.id.buttonGetCurrentLocation)
        val buttonGetLastLocation = findViewById<Button>(R.id.buttonGetLastLocation)
        val buttonClearDB = findViewById<Button>(R.id.buttonClearDB)
        val buttonStopUpdate = findViewById<Button>(R.id.buttonStopUpdate)

        advancedLocation.requestLocationUpdates(this,LocationType.EFFICIENT_POWER,UpdateInterval.INTERVAL_15_SECONDS) {
            Log.d(TAG, "Lat: ${it.latitude}\n Long: ${it.longitude}")
        }

        advancedLocation.requestCustomLocationUpdates(
            this,
            interval = 20L,
            smallestDisplacement = 1F
        ) {
            Log.d(TAG, "Lat: ${it.latitude}\n Long: ${it.longitude}")
        }


        advancedLocation.getLastLocation(
            this
        ) {
            val position = it.value
            Log.d(TAG, "Lat: ${position.latitude}\n Long: ${position.longitude}")
        }

        advancedLocation.removeLocationUpdateRequest()

        advancedLocation.getActivityType().let { activityTypeResult ->
            val activityType = activityTypeResult.getActivityType()
            activityType.let {
                Log.d(TAG, "Activity Type: ${it?.type}")
            }
        }

        buttonStartService.setOnClickListener {
            advancedLocation.startBackgroundLocationUpdates(
                this,
                UpdateInterval.INTERVAL_FIVE_MINUTES
            ).let {
                val list = it.getAllLocationUpdates()
                list?.forEach { location ->
                    Log.d(TAG, "### Lat: ${location.latitude}\n Long: ${location.longitude}")
                }
            }
        }

        buttonGetCurrentLocation.setOnClickListener {
            advancedLocation.getCurrentLocation(this, resultListener = {
                Log.i(TAG,it.toString())
            })
        }

        buttonGetLastLocation.setOnClickListener {
            advancedLocation.getLastLocation(this, taskListener = {
                Log.i(TAG,it.toString())
            })
        }

        buttonClearDB.setOnClickListener {
            advancedLocation.clearLocationDB()
        }

        buttonStopUpdate.setOnClickListener {
            advancedLocation.stopBackgroundLocationUpdates()
        }
    }

    override fun onDestroy() {
        advancedLocation.startBackgroundLocationUpdates(
            this,
            UpdateInterval.INTERVAL_0_SECONDS
        )
        super.onDestroy()
    }
}