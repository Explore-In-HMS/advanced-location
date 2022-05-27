package com.hms.advancedlocationlibrary.database

import com.hms.advancedlocationlibrary.AdvancedLocation.Companion.mLocationDatabase
import com.hms.advancedlocationlibrary.database.dto.LocationDto

class BackgroundLocationResult {

    fun getAllLocationUpdates() : List<LocationDto>? {
        return mLocationDatabase?.getAllLocationUpdates()
    }

    fun getLastLocation() : LocationDto? {
        return mLocationDatabase?.getLastLocation()
    }
}