package com.hms.advancedlocationlibrary.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.hms.advancedlocationlibrary.database.dto.ActivityTypeDto

@Dao
interface ActivityTypeDao {
    @Insert
    fun insertActivityType(vararg activityTypeDto: ActivityTypeDto)

    @Query("SELECT * FROM tbl_activity_type ORDER BY activity_type_id DESC LIMIT 1")
    fun getActivityType() : ActivityTypeDto

    @Query("DELETE FROM tbl_activity_type")
    fun clearDB()
}