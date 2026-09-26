package com.example

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

class ExampleUnitTest {

  @Test
  fun testAppAvailableBeforeSeptember15_2026CST() {
    // 1 second before midnight on Sept 15, 2026 CST
    val beforeInstant = ZonedDateTime.of(
      2026, 9, 14, 23, 59, 59, 0, ZoneId.of("America/Chicago")
    ).toInstant()

    assertTrue(AppAvailabilityChecker.isAppAvailable(beforeInstant))
    assertFalse(AppAvailabilityChecker.isAppExpired(beforeInstant))
  }

  @Test
  fun testAppUnavailableOnSeptember15_2026CST() {
    // Exactly at midnight on Sept 15, 2026 CST
    val exactInstant = ZonedDateTime.of(
      2026, 9, 15, 0, 0, 0, 0, ZoneId.of("America/Chicago")
    ).toInstant()

    assertFalse(AppAvailabilityChecker.isAppAvailable(exactInstant))
    assertTrue(AppAvailabilityChecker.isAppExpired(exactInstant))
  }

  @Test
  fun testAppUnavailableAfterSeptember15_2026CST() {
    // Hours/days after Sept 15, 2026 CST
    val afterInstant = ZonedDateTime.of(
      2026, 9, 16, 12, 0, 0, 0, ZoneId.of("America/Chicago")
    ).toInstant()

    assertFalse(AppAvailabilityChecker.isAppAvailable(afterInstant))
    assertTrue(AppAvailabilityChecker.isAppExpired(afterInstant))
  }

  @Test
  fun testCountdownCalculationGating() {
    val afterInstant = ZonedDateTime.of(
      2026, 9, 16, 0, 0, 0, 0, ZoneId.of("America/Chicago")
    ).toInstant()

    val state = CountdownCalculator.calculate(TimezoneOption.CENTRAL_TIME, afterInstant)
    assertFalse(state.isAppAvailable)
    assertEquals(0L, state.days)
    assertEquals(0L, state.hours)
    assertEquals(0L, state.minutes)
    assertEquals(0L, state.seconds)
  }
}
