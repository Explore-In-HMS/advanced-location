package com.hms.advancedlocationlibrary.database.dto

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tbl_activity_type")
data class ActivityTypeDto(
    @PrimaryKey(autoGenerate = true) var activityTypeId : Int = 0,
    var activityTypeCode: Int?
)