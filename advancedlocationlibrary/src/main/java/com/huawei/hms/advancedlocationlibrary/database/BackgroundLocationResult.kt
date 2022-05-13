package com.huawei.hms.advancedlocationlibrary.database

import com.huawei.hms.advancedlocationlibrary.AdvancedLocation.Companion.mLocationDatabase
import com.huawei.hms.advancedlocationlibrary.database.dto.LocationDto

class BackgroundLocationResult {

    fun getAllLocationUpdates() : List<LocationDto>? {
        return mLocationDatabase?.getAllLocationUpdates()
    }

    fun getLastLocation() : LocationDto? {
        return mLocationDatabase?.getLastLocation()
    }
}