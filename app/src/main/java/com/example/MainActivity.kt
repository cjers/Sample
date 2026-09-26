package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.HourglassDisabled
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.MidnightBackground
import com.example.ui.theme.MidnightBorder
import com.example.ui.theme.MidnightSurface
import com.example.ui.theme.MidnightSurfaceVariant
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.util.Locale

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        Scaffold(
          modifier = Modifier
            .fillMaxSize()
            .testTag("main_scaffold"),
          containerColor = MidnightBackground
        ) { innerPadding ->
          AppRootScreen(
            modifier = Modifier
              .fillMaxSize()
              .padding(innerPadding)
          )
        }
      }
    }
  }
}

/**
 * Root host that checks app availability.
 * If the current time is on or after September 15, 2026 CST,
 * app features cannot be seen or used, and the unavailable message is shown.
 */
@Composable
fun AppRootScreen(
  modifier: Modifier = Modifier,
  viewModel: CountdownViewModel = viewModel()
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val isSimulatedExpired by viewModel.simulatedExpired.collectAsStateWithLifecycle()

  // App features can not be seen or used on or after September 15, 2026 CST
  val isLockedOut = !uiState.isAppAvailable || isSimulatedExpired

  if (isLockedOut) {
    AppUnavailableScreen(
      modifier = modifier,
      expirationFormatted = uiState.expirationFormatted,
      isSimulated = isSimulatedExpired,
      onResetSimulation = { viewModel.toggleSimulatedExpiration() }
    )
  } else {
    CountdownScreen(
      modifier = modifier,
      viewModel = viewModel,
      onSimulateExpired = { viewModel.toggleSimulatedExpiration() }
    )
  }
}

/**
 * Screen presented when the app reaches or passes September 15, 2026 CST.
 * Replaces all app features with a clear message stating the app is no longer available.
 */
@Composable
fun AppUnavailableScreen(
  modifier: Modifier = Modifier,
  expirationFormatted: String,
  isSimulated: Boolean = false,
  onResetSimulation: () -> Unit = {}
) {
  Box(
    modifier = modifier
      .fillMaxSize()
      .background(
        Brush.radialGradient(
          colors = listOf(
            Color(0xFF20162B),
            MidnightBackground
          ),
          radius = 1100f
        )
      )
      .testTag("app_unavailable_screen"),
    contentAlignment = Alignment.Center
  ) {
    Column(
      modifier = Modifier
        .widthIn(max = 540.dp)
        .fillMaxWidth()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 24.dp, vertical = 32.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
      // Lock Icon Badge
      Surface(
        modifier = Modifier.size(96.dp),
        shape = CircleShape,
        color = MidnightSurfaceVariant,
        border = BorderStroke(2.dp, AmberAccent)
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
            imageVector = Icons.Default.HourglassDisabled,
            contentDescription = "App Expired",
            tint = AmberAccent,
            modifier = Modifier.size(48.dp)
          )
        }
      }

      // Expired Category Pill
      Surface(
        shape = CircleShape,
        color = Color(0xFF332211),
        border = BorderStroke(1.dp, AmberAccent.copy(alpha = 0.5f))
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = null,
            tint = AmberAccent,
            modifier = Modifier.size(14.dp)
          )
          Text(
            text = "LIFECYCLE CONCLUDED",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp,
            color = AmberAccent
          )
        }
      }

      // Primary Status Heading
      Text(
        text = "This App Is No Longer Available",
        fontSize = 26.sp,
        fontWeight = FontWeight.ExtraBold,
        color = TextPrimary,
        textAlign = TextAlign.Center,
        modifier = Modifier.testTag("unavailable_title")
      )

      // Primary Requirement Message
      Text(
        text = "This application is no longer available. All features were scheduled to run until September 15, 2026 CST, and access has now expired.",
        fontSize = 15.sp,
        lineHeight = 23.sp,
        color = TextSecondary,
        textAlign = TextAlign.Center,
        modifier = Modifier.testTag("unavailable_message")
      )

      // Information Card
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MidnightSurface),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, MidnightBorder)
      ) {
        Column(
          modifier = Modifier.padding(20.dp),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          DetailRow(
            icon = Icons.Default.Event,
            title = "Expiration Milestone",
            content = if (expirationFormatted.isNotEmpty()) expirationFormatted else "September 15, 2026 • 12:00:00 AM CST",
            tint = AmberAccent
          )

          DetailRow(
            icon = Icons.Default.Block,
            title = "App Features Status",
            content = "Disabled — Inactive as of September 15, 2026 CST",
            tint = Color(0xFFF87171)
          )
        }
      }

      if (isSimulated) {
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedButton(
          onClick = onResetSimulation,
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.outlinedButtonColors(contentColor = CyanAccent),
          border = BorderStroke(1.dp, CyanAccent)
        ) {
          Text("Return to Active Countdown (Preview Mode)")
        }
      }
    }
  }
}

/**
 * Main active countdown screen, visible ONLY prior to September 15, 2026 CST.
 */
@Composable
fun CountdownScreen(
  modifier: Modifier = Modifier,
  viewModel: CountdownViewModel = viewModel(),
  onSimulateExpired: () -> Unit = {}
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val timezoneOption by viewModel.timezoneOption.collectAsStateWithLifecycle()

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(
        Brush.radialGradient(
          colors = listOf(
            Color(0xFF14213D),
            MidnightBackground
          ),
          radius = 1200f
        )
      )
      .testTag("countdown_screen"),
    contentAlignment = Alignment.TopCenter
  ) {
    Column(
      modifier = Modifier
        .widthIn(max = 600.dp)
        .fillMaxWidth()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 20.dp, vertical = 24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
      // Header Section
      HeaderSection()

      // Target Header Card
      TargetHeaderCard(
        selectedOption = timezoneOption,
        onOptionSelected = { viewModel.setTimezoneOption(it) }
      )

      // Main Countdown Units (Days, Hours, Minutes, Seconds)
      CountdownGrid(
        days = uiState.days,
        hours = uiState.hours,
        minutes = uiState.minutes,
        seconds = uiState.seconds
      )

      // Millisecond Ticker Pill
      MillisecondTickerPill(millis = uiState.millis)

      // Seconds Progress Indicator
      val secondsProgress = (uiState.seconds % 60f + uiState.millis / 1000f) / 60f
      Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        LinearProgressIndicator(
          progress = { secondsProgress },
          modifier = Modifier
            .fillMaxWidth()
            .height(4.dp)
            .clip(CircleShape),
          color = CyanAccent,
          trackColor = MidnightSurfaceVariant
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = "Minute cadence: ${60 - uiState.seconds}s remaining in minute",
          fontSize = 12.sp,
          color = TextMuted
        )
      }

      // Exact Target & Device Time Details Card
      TargetDetailsCard(uiState = uiState)

      // Total Equivalents (Hours, Minutes, Seconds)
      TotalEquivalentsCard(
        totalHours = uiState.totalHours,
        totalMinutes = uiState.totalMinutes,
        totalSeconds = uiState.totalSeconds
      )

      // Test toggle for previewing expired state
      Surface(
        modifier = Modifier
          .clip(RoundedCornerShape(12.dp))
          .clickable { onSimulateExpired() }
          .padding(8.dp),
        color = Color.Transparent
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Info,
            contentDescription = null,
            tint = TextMuted,
            modifier = Modifier.size(14.dp)
          )
          Text(
            text = "Preview Post-Sep 15, 2026 Unavailable Message",
            fontSize = 12.sp,
            color = TextMuted
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))
    }
  }
}

@Composable
private fun HeaderSection() {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(6.dp)
  ) {
    Surface(
      shape = CircleShape,
      color = MidnightSurfaceVariant,
      border = BorderStroke(1.dp, MidnightBorder)
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Icon(
          imageVector = Icons.Default.HourglassTop,
          contentDescription = "Countdown icon",
          tint = CyanAccent,
          modifier = Modifier.size(16.dp)
        )
        Text(
          text = "OFFICIAL COUNTDOWN",
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 2.sp,
          color = CyanAccent
        )
      }
    }

    Text(
      text = "September 15, 2026",
      fontSize = 28.sp,
      fontWeight = FontWeight.ExtraBold,
      color = TextPrimary,
      textAlign = TextAlign.Center
    )

    Text(
      text = "Target CST / Central Time (00:00:00)",
      fontSize = 14.sp,
      color = TextSecondary,
      textAlign = TextAlign.Center
    )
  }
}

@Composable
private fun TargetHeaderCard(
  selectedOption: TimezoneOption,
  onOptionSelected: (TimezoneOption) -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("timezone_selector"),
    colors = CardDefaults.cardColors(containerColor = MidnightSurface),
    shape = RoundedCornerShape(16.dp),
    border = BorderStroke(1.dp, MidnightBorder)
  ) {
    Column(
      modifier = Modifier.padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Icon(
          imageVector = Icons.Default.Public,
          contentDescription = "Timezone icon",
          tint = AmberAccent,
          modifier = Modifier.size(18.dp)
        )
        Text(
          text = "Timezone Reference",
          fontSize = 13.sp,
          fontWeight = FontWeight.SemiBold,
          color = TextPrimary
        )
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        TimezoneOption.values().forEach { option ->
          val isSelected = option == selectedOption
          Surface(
            modifier = Modifier
              .weight(1f)
              .clickable { onOptionSelected(option) },
            shape = RoundedCornerShape(10.dp),
            color = if (isSelected) CyanGlow else MidnightSurfaceVariant,
            border = BorderStroke(
              1.dp,
              if (isSelected) CyanAccent else Color.Transparent
            )
          ) {
            Column(
              modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text(
                text = if (option == TimezoneOption.CENTRAL_TIME) "Central Time" else "Strict CST",
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) CyanAccent else TextPrimary
              )
              Text(
                text = option.offsetDescription,
                fontSize = 11.sp,
                color = TextSecondary
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun CountdownGrid(
  days: Long,
  hours: Long,
  minutes: Long,
  seconds: Long
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("countdown_container"),
    horizontalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    TimeCard(
      modifier = Modifier.weight(1f).testTag("card_days"),
      value = days.toString(),
      label = "DAYS",
      accentColor = CyanAccent
    )
    TimeCard(
      modifier = Modifier.weight(1f).testTag("card_hours"),
      value = String.format(Locale.US, "%02d", hours),
      label = "HOURS",
      accentColor = AmberAccent
    )
    TimeCard(
      modifier = Modifier.weight(1f).testTag("card_minutes"),
      value = String.format(Locale.US, "%02d", minutes),
      label = "MINUTES",
      accentColor = EmeraldAccent
    )
    TimeCard(
      modifier = Modifier.weight(1f).testTag("card_seconds"),
      value = String.format(Locale.US, "%02d", seconds),
      label = "SECONDS",
      accentColor = CyanAccent
    )
  }
}

@Composable
private fun TimeCard(
  modifier: Modifier = Modifier,
  value: String,
  label: String,
  accentColor: Color
) {
  Card(
    modifier = modifier,
    colors = CardDefaults.cardColors(containerColor = MidnightSurface),
    shape = RoundedCornerShape(16.dp),
    border = BorderStroke(1.dp, MidnightBorder)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 18.dp, horizontal = 4.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Text(
        text = value,
        fontSize = 32.sp,
        fontWeight = FontWeight.Black,
        fontFamily = FontFamily.Monospace,
        color = accentColor,
        textAlign = TextAlign.Center
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = label,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
        color = TextSecondary,
        textAlign = TextAlign.Center
      )
    }
  }
}

@Composable
private fun MillisecondTickerPill(millis: Long) {
  Surface(
    modifier = Modifier.testTag("card_millis"),
    shape = RoundedCornerShape(20.dp),
    color = MidnightSurface,
    border = BorderStroke(1.dp, MidnightBorder)
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Box(
        modifier = Modifier
          .size(8.dp)
          .clip(CircleShape)
          .background(CyanAccent)
      )
      Text(
        text = "LIVE TICKER",
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
        color = TextMuted
      )
      Text(
        text = ".${String.format(Locale.US, "%03d", millis)}",
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Monospace,
        color = CyanAccent
      )
      Text(
        text = "ms",
        fontSize = 11.sp,
        color = TextSecondary
      )
    }
  }
}

@Composable
private fun TargetDetailsCard(uiState: CountdownUiState) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("target_info_card"),
    colors = CardDefaults.cardColors(containerColor = MidnightSurface),
    shape = RoundedCornerShape(16.dp),
    border = BorderStroke(1.dp, MidnightBorder)
  ) {
    Column(
      modifier = Modifier.padding(18.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      DetailRow(
        icon = Icons.Default.Event,
        title = "Target Date & Time",
        content = uiState.centralFormattedTarget,
        tint = CyanAccent
      )

      DetailRow(
        icon = Icons.Default.Public,
        title = "In Your Local Timezone",
        content = uiState.localFormattedTarget,
        tint = AmberAccent
      )

      DetailRow(
        icon = Icons.Default.AccessTime,
        title = "Current Device Time",
        content = uiState.deviceCurrentFormatted,
        tint = EmeraldAccent
      )
    }
  }
}

@Composable
private fun DetailRow(
  icon: ImageVector,
  title: String,
  content: String,
  tint: Color
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(12.dp),
    verticalAlignment = Alignment.Top
  ) {
    Surface(
      modifier = Modifier.size(34.dp),
      shape = RoundedCornerShape(8.dp),
      color = MidnightSurfaceVariant
    ) {
      Box(contentAlignment = Alignment.Center) {
        Icon(
          imageVector = icon,
          contentDescription = title,
          tint = tint,
          modifier = Modifier.size(18.dp)
        )
      }
    }
    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = title,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        color = TextMuted
      )
      Text(
        text = content,
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        color = TextPrimary
      )
    }
  }
}

@Composable
private fun TotalEquivalentsCard(
  totalHours: Long,
  totalMinutes: Long,
  totalSeconds: Long
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("total_equivalents_card"),
    colors = CardDefaults.cardColors(containerColor = MidnightSurface),
    shape = RoundedCornerShape(16.dp),
    border = BorderStroke(1.dp, MidnightBorder)
  ) {
    Column(
      modifier = Modifier.padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Icon(
          imageVector = Icons.Default.Timer,
          contentDescription = "Summary icon",
          tint = CyanAccent,
          modifier = Modifier.size(18.dp)
        )
        Text(
          text = "Total Remaining Units",
          fontSize = 13.sp,
          fontWeight = FontWeight.SemiBold,
          color = TextPrimary
        )
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        SummaryStat(
          label = "Total Hours",
          value = String.format(Locale.US, "%,d", totalHours)
        )
        SummaryStat(
          label = "Total Minutes",
          value = String.format(Locale.US, "%,d", totalMinutes)
        )
        SummaryStat(
          label = "Total Seconds",
          value = String.format(Locale.US, "%,d", totalSeconds)
        )
      }
    }
  }
}

@Composable
private fun SummaryStat(label: String, value: String) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Text(
      text = value,
      fontSize = 15.sp,
      fontWeight = FontWeight.Bold,
      fontFamily = FontFamily.Monospace,
      color = TextPrimary
    )
    Text(
      text = label,
      fontSize = 11.sp,
      color = TextMuted
    )
  }
}

/**
 * Kept for testing compatibility with existing screenshot test
 */
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
  Text(text = "Hello $name!", modifier = modifier)
}

@Preview(showBackground = true)
@Composable
fun CountdownPreview() {
  MyApplicationTheme {
    AppRootScreen()
  }
}

@Preview(showBackground = true)
@Composable
fun UnavailablePreview() {
  MyApplicationTheme {
    AppUnavailableScreen(
      expirationFormatted = "Tuesday, September 15, 2026 • 12:00:00 AM CDT"
    )
  }
}
