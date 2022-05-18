package com.huawei.hms.advancedlocation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.huawei.hms.advancedlocationlibrary.AdvancedLocation
import com.huawei.hms.advancedlocationlibrary.data.UpdateInterval
import com.huawei.hms.advancedlocationlibrary.data.model.enums.LocationType

class MainActivity : AppCompatActivity() {

    private val advancedLocation = AdvancedLocation()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /*advancedLocation.requestLocationUpdates(this,LocationType.EFFICIENT_POWER) {
            Log.d("LocationTalha", "Lat: ${it.latitude}\n Long: ${it.longitude}")
            //Toast.makeText(this,"Lat: ${it.latitude}\n Long: ${it.longitude}",Toast.LENGTH_LONG).show()
        }*/
        //advancedLocation.stopBackgroundLocationUpdates()

        val buttonStartService = findViewById<Button>(R.id.buttonStartService)

        /*buttonStartService.setOnClickListener {
            advancedLocation.startBackgroundLocationUpdates(
                this,
                UpdateInterval.INTERVAL_0_SECONDS
            ).let {

                val list = it.getAllLocationUpdates()
                list?.forEach { location ->
                    Log.d("LocationTalha", "### Lat: ${location.latitude}\n Long: ${location.longitude}")
                }

            }
        }*/
    }

    override fun onDestroy() {
        advancedLocation.startBackgroundLocationUpdates(
            this,
            UpdateInterval.INTERVAL_0_SECONDS
        )

        super.onDestroy()
    }
}