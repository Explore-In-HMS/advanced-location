package com.hms.advancedlocationlibrary.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.hms.advancedlocationlibrary.database.dto.LocationDto

@Database(entities = [LocationDto::class], version = 1,
    exportSchema = true)
abstract class LocationDatabase : RoomDatabase() {

    abstract fun locationDao(): LocationDao

    companion object {
        private var instance: LocationDatabase? = null

        fun getLocationDatabase(context: Context): LocationDatabase? {

            if (instance == null) {
                instance = Room.databaseBuilder(
                    context,
                    LocationDatabase::class.java,
                    "location_database"
                ).allowMainThreadQueries()
                    .build()
            }
            return instance
        }
    }
}