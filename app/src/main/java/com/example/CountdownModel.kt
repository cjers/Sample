package com.example

import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

enum class TimezoneOption(
    val id: String,
    val displayName: String,
    val zoneId: ZoneId,
    val offsetDescription: String
) {
    CENTRAL_TIME(
        id = "central_time",
        displayName = "Central Time (America/Chicago)",
        zoneId = ZoneId.of("America/Chicago"),
        offsetDescription = "CDT (UTC-5 in Sep)"
    ),
    STRICT_CST(
        id = "strict_cst",
        displayName = "Strict CST (UTC-6)",
        zoneId = ZoneOffset.ofHours(-6),
        offsetDescription = "Standard Time (UTC-6)"
    )
}

/**
 * Gatekeeper object providing the function to verify whether
 * the app features can be seen or used.
 */
object AppAvailabilityChecker {
    // September 15, 2026 00:00:00 Central Standard Time (America/Chicago)
    val EXPIRATION_TARGET_CST: ZonedDateTime = ZonedDateTime.of(
        2026, 9, 15, 0, 0, 0, 0, ZoneId.of("America/Chicago")
    )
    val EXPIRATION_INSTANT: Instant = EXPIRATION_TARGET_CST.toInstant()

    /**
     * Function that determines if the app features can be seen or used.
     * Returns true ONLY prior to September 15, 2026 CST.
     * On or after September 15, 2026 CST, returns false.
     */
    fun isAppAvailable(now: Instant = Instant.now()): Boolean {
        return now.isBefore(EXPIRATION_INSTANT)
    }

    /**
     * Function that determines if the app has expired.
     * Returns true on or after September 15, 2026 CST.
     */
    fun isAppExpired(now: Instant = Instant.now()): Boolean {
        return !isAppAvailable(now)
    }
}

data class CountdownUiState(
    val isAppAvailable: Boolean = true,
    val timezoneOption: TimezoneOption = TimezoneOption.CENTRAL_TIME,
    val targetZonedDateTime: ZonedDateTime = ZonedDateTime.of(2026, 9, 15, 0, 0, 0, 0, TimezoneOption.CENTRAL_TIME.zoneId),
    val nowInstant: Instant = Instant.now(),
    val days: Long = 0,
    val hours: Long = 0,
    val minutes: Long = 0,
    val seconds: Long = 0,
    val millis: Long = 0,
    val totalHours: Long = 0,
    val totalMinutes: Long = 0,
    val totalSeconds: Long = 0,
    val localFormattedTarget: String = "",
    val centralFormattedTarget: String = "",
    val deviceCurrentFormatted: String = "",
    val expirationFormatted: String = ""
)

object CountdownCalculator {
    private val fullDateFormatter: DateTimeFormatter =
        DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy • hh:mm:ss a z", Locale.ENGLISH)
    private val shortTimeFormatter: DateTimeFormatter =
        DateTimeFormatter.ofPattern("EEE, MMM d, yyyy • hh:mm:ss a z", Locale.ENGLISH)

    fun calculate(
        option: TimezoneOption,
        now: Instant = Instant.now()
    ): CountdownUiState {
        val available = AppAvailabilityChecker.isAppAvailable(now)

        val targetZoned = ZonedDateTime.of(2026, 9, 15, 0, 0, 0, 0, option.zoneId)
        val targetInstant = targetZoned.toInstant()
        val diffMillis = targetInstant.toEpochMilli() - now.toEpochMilli()

        val remainingMillis = if (!available || diffMillis <= 0) 0L else diffMillis

        val days = remainingMillis / (24 * 3600 * 1000L)
        val hours = (remainingMillis % (24 * 3600 * 1000L)) / (3600 * 1000L)
        val minutes = (remainingMillis % (3600 * 1000L)) / (60 * 1000L)
        val seconds = (remainingMillis % (60 * 1000L)) / 1000L
        val millis = remainingMillis % 1000L

        val totalHours = remainingMillis / (3600 * 1000L)
        val totalMinutes = remainingMillis / (60 * 1000L)
        val totalSeconds = remainingMillis / 1000L

        val deviceZone = ZoneId.systemDefault()
        val targetInDeviceZone = targetZoned.withZoneSameInstant(deviceZone)

        val localFormattedTarget = fullDateFormatter.format(targetInDeviceZone)
        val centralFormattedTarget = fullDateFormatter.format(targetZoned)
        val deviceCurrentFormatted = shortTimeFormatter.format(now.atZone(deviceZone))
        val expirationFormatted = fullDateFormatter.format(AppAvailabilityChecker.EXPIRATION_TARGET_CST)

        return CountdownUiState(
            isAppAvailable = available,
            timezoneOption = option,
            targetZonedDateTime = targetZoned,
            nowInstant = now,
            days = days,
            hours = hours,
            minutes = minutes,
            seconds = seconds,
            millis = millis,
            totalHours = totalHours,
            totalMinutes = totalMinutes,
            totalSeconds = totalSeconds,
            localFormattedTarget = localFormattedTarget,
            centralFormattedTarget = centralFormattedTarget,
            deviceCurrentFormatted = deviceCurrentFormatted,
            expirationFormatted = expirationFormatted
        )
    }
}
