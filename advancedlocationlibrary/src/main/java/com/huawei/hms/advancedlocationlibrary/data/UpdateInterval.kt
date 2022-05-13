/*
 *  Copyright (C) 2021 Huawei Technologies Co., Ltd. All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.huawei.hms.advancedlocationlibrary.data

import androidx.annotation.LongDef

@LongDef(
    UpdateInterval.INTERVAL_15_SECONDS,
    UpdateInterval.INTERVAL_30_SECONDS,
    UpdateInterval.INTERVAL_ONE_MINUTE,
    UpdateInterval.INTERVAL_TWO_MINUTES
)
@Retention(AnnotationRetention.SOURCE)
annotation class UpdateInterval {

    companion object {
        const val INTERVAL_15_SECONDS = 15*1000L
        const val INTERVAL_30_SECONDS = 30*1000L
        const val INTERVAL_ONE_MINUTE = 60*1000L
        const val INTERVAL_TWO_MINUTES = 2*60*1000L
    }
}
