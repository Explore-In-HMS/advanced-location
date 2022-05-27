package com.huawei.hms.advancedlocation

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.hms.advancedlocation.R
import com.huawei.hms.advancedlocationlibrary.AdvancedLocation
import com.huawei.hms.advancedlocationlibrary.data.UpdateInterval
import com.huawei.hms.advancedlocationlibrary.data.model.enums.LocationType

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

        advancedLocation.requestLocationUpdates(this,LocationType.EFFICIENT_POWER) {
            Log.d(TAG, "Lat: ${it.latitude}\n Long: ${it.longitude}")
        }

        advancedLocation.getActivityType().let {

            Log.i(TAG, it.toString())
        }

        buttonStartService.setOnClickListener {
            advancedLocation.startBackgroundLocationUpdates(
                this,
                UpdateInterval.INTERVAL_0_SECONDS
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
            advancedLocation.clearDB()
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