package com.huawei.hms.advancedlocationlibrary.database

import com.huawei.hms.advancedlocationlibrary.AdvancedLocation.Companion.mActivityTypeDatabase
import com.huawei.hms.advancedlocationlibrary.data.model.enums.ActivityType
import com.huawei.hms.advancedlocationlibrary.database.dto.ActivityTypeDto

class ActivityTypeResult {

    fun getActivityType() : ActivityType? {
        return mActivityTypeDatabase?.getActivityType()?.parse()
    }

    private fun ActivityTypeDto.parse() : ActivityType {
        return ActivityType.getActivityType(this.activityTypeCode)
    }
}