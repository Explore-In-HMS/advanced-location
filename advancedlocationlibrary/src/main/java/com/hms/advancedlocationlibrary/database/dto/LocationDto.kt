package com.hms.advancedlocationlibrary.database.dto

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tbl_location")
data class LocationDto(
    @PrimaryKey(autoGenerate = true) var location_id : Int = 0,
    var latitude: Double?,
    var longitude: Double?,
    var currentTime: Long?
)