package com.hms.advancedlocation

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object Utils {
    fun getTime(): String? {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        return current.format(formatter)
    }
}