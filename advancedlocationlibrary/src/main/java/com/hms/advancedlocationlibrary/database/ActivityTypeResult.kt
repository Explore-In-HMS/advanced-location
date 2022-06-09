package com.hms.advancedlocationlibrary.database

import com.hms.advancedlocationlibrary.AdvancedLocation.Companion.mActivityTypeDatabase
import com.hms.advancedlocationlibrary.data.model.enums.ActivityType
import com.hms.advancedlocationlibrary.database.dto.ActivityTypeDto

class ActivityTypeResult {

    fun getActivityType(): ActivityType? {
        return mActivityTypeDatabase?.getActivityType()?.parse()
    }

    private fun ActivityTypeDto.parse(): ActivityType {
        return ActivityType.getActivityType(this.activityTypeCode)
    }
}