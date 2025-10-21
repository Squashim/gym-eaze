package com.example.gymeaze.util

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import android.util.Log

fun String.formatToRelativeTime(): String {
    return try {
        val instant = Instant.parse(this)
        val now = Instant.now()
        val minutesAgo = ChronoUnit.MINUTES.between(instant, now)
        val hoursAgo = ChronoUnit.HOURS.between(instant, now)
        val daysAgo = ChronoUnit.DAYS.between(instant, now)

        when {
            minutesAgo < 1 -> "przed chwilą"
            minutesAgo < 60 -> "${minutesAgo}min temu"
            hoursAgo < 24 -> "${hoursAgo}h temu"
            daysAgo < 7 -> "${daysAgo}d temu"
            daysAgo < 30 -> "${daysAgo / 7}tyg temu"
            else -> DateTimeFormatter.ofPattern("dd.MM.yyyy")
                .withZone(ZoneId.systemDefault())
                .format(instant)
        }
    } catch (e: Exception) {
        Log.e("DateUtils", "Error formatting date: $this", e)
        this
    }
}