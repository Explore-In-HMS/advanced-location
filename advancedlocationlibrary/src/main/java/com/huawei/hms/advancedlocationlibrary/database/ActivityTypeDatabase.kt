package com.huawei.hms.advancedlocationlibrary.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.huawei.hms.advancedlocationlibrary.database.dto.ActivityTypeDto

@Database(entities = [ActivityTypeDto::class], version = 1,
    exportSchema = true)
abstract class ActivityTypeDatabase : RoomDatabase() {

    abstract fun activityTypeDao(): ActivityTypeDao

    companion object {
        private var instance: ActivityTypeDatabase? = null

        fun getActivityTypeDatabase(context: Context): ActivityTypeDatabase? {

            if (instance == null) {
                instance = Room.databaseBuilder(
                    context,
                    ActivityTypeDatabase::class.java,
                    "activity_type_database"
                ).allowMainThreadQueries()
                    .build()
            }
            return instance
        }
    }
}