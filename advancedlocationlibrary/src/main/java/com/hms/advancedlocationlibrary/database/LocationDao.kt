package com.hms.advancedlocationlibrary.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.hms.advancedlocationlibrary.database.dto.LocationDto

@Dao
interface LocationDao{
    @Insert
    fun insertLocation(vararg locationDto: LocationDto)

    @Query("SELECT * FROM tbl_location")
    fun getAllLocationUpdates() : List<LocationDto>

    @Query("SELECT * FROM tbl_location ORDER BY location_id DESC LIMIT 1")
    fun getLastLocation() : LocationDto

    @Query("DELETE FROM tbl_location")
    fun clearDB()
}