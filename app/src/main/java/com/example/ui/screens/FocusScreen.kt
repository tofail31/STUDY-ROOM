package com.example.ui.screens

import android.app.Activity
import android.view.WindowManager
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.VolumeOff
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.audio.FocusAmbientAudio
import com.example.data.calc.PrayerTimeCalculator
import com.example.data.model.FocusSessionMode
import com.example.data.model.StudyDashboardDefaults
import com.example.ui.components.NeumorphicCard
import com.example.ui.components.NeumorphicCircleButton
import com.example.ui.components.NeumorphicPillButton
import com.example.ui.theme.LocalAppStrings
import com.example.ui.theme.NeumorphicHighlight
import com.example.ui.theme.PlusJakartaSansFamily
import com.example.ui.theme.StoneAccent
import com.example.ui.theme.StoneBackground
import com.example.ui.theme.StoneBackgroundBottom
import com.example.ui.theme.StoneBackgroundTop
import com.example.ui.theme.StoneDivider
import com.example.ui.theme.StoneInkMuted
import com.example.ui.theme.StoneInkPrimary
import com.example.ui.theme.StoneInkSecondary
import com.example.ui.theme.StonePanelInner
import com.example.ui.theme.StoneSuccess
import com.example.ui.theme.StoneSurface
import com.example.ui.viewmodel.StudyDashboardUiState
import java.util.Locale
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private val FocusTrackStone = Color(0xFFD7D1CA)
private val FocusCapsuleBg = Color(0xFFDCD6CE)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FocusScreen(
    dashboard: StudyDashboardUiState,
    onBack: () -> Unit,
    onTogglePlayPause: () -> Unit,
    onResetSession: () -> Unit,
    onSelectMode: (FocusSessionMode) -> Unit,
    onSelectSubject: (String) -> Unit,
    onAddSubject: (String, Long, Double) -> Unit,
    onSkipBreak: () -> Unit,
    onToggleKeepScreenOn: () -> Unit,
    onToggleAmbientSound: () -> Unit,
    onOpenLiveRoom: () -> Unit
) {
    val s = LocalAppStrings.current
    val context = LocalContext.current
    val audioScope = rememberCoroutineScope()

    val isRunning = dashboard.activeSubjectId != null
    val activeSubject = remember(dashboard.subjects, dashboard.activeSubjectId, dashboard.focusSubjectId) {
        dashboard.subjects.firstOrNull { it.id == (dashboard.activeSubjectId ?: dashboard.focusSubjectId) }
            ?: dashboard.subjects.first()
    }
    val subjectColor = Color(activeSubject.colorHex)
    val ringColor = if (dashboard.isFocusBreakPhase) StoneSuccess else subjectColor

    var showSubjectSheet by remember { mutableStateOf(false) }
    var showAddSubjectDialog by remember { mutableStateOf(false) }

    // Keep screen on during active focus if enabled
    DisposableEffect(dashboard.keepScreenOnInFocus && isRunning) {
        val window = (context as? Activity)?.window
        if (dashboard.keepScreenOnInFocus && isRunning) {
            window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
        onDispose {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    // Play calm offline ambient brown noise when enabled & active
    DisposableEffect(dashboard.isAmbientSoundOn, isRunning) {
        FocusAmbientAudio.setPlaying(audioScope, dashboard.isAmbientSoundOn && isRunning)
        onDispose {
            FocusAmbientAudio.stop()
        }
    }

    val rawProgress = remember(
        dashboard.focusMode,
        dashboard.focusSessionElapsedSeconds,
        dashboard.focusCountdownSeconds,
        dashboard.isFocusBreakPhase
    ) {
        when (dashboard.focusMode) {
            FocusSessionMode.STOPWATCH -> {
                ((dashboard.focusSessionElapsedSeconds % 3600L) / 3600f).coerceIn(0f, 1f)
            }
            FocusSessionMode.POMODORO_25, FocusSessionMode.DEEP_WORK_50 -> {
                if (dashboard.isFocusBreakPhase) {
                    val total = dashboard.focusMode.breakDurationSeconds.coerceAtLeast(1)
                    ((total - dashboard.focusCountdownSeconds) / total.toFloat()).coerceIn(0f, 1f)
                } else {
                    val total = dashboard.focusMode.durationSeconds.coerceAtLeast(1)
                    ((total - dashboard.focusCountdownSeconds) / total.toFloat()).coerceIn(0f, 1f)
                }
            }
        }
    }

    val animatedProgress by animateFloatAsState(
        targetValue = rawProgress,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "focus_ring_progress"
    )

    val clockReadout = remember(
        dashboard.focusMode,
        dashboard.focusSessionElapsedSeconds,
        dashboard.focusCountdownSeconds
    ) {
        val formatted = when (dashboard.focusMode) {
            FocusSessionMode.STOPWATCH -> formatFocusHms(dashboard.focusSessionElapsedSeconds)
            FocusSessionMode.POMODORO_25, FocusSessionMode.DEEP_WORK_50 ->
                formatCountdownClock(dashboard.focusCountdownSeconds)
        }
        if (s.isBangla) PrayerTimeCalculator.toBanglaDigits(formatted) else formatted
    }

    val statusSubtitle = remember(
        dashboard.focusMode,
        isRunning,
        dashboard.focusSessionElapsedSeconds,
        dashboard.isFocusBreakPhase,
        dashboard.completedPomodoros,
        s
    ) {
        when {
            dashboard.isFocusBreakPhase -> {
                val mins = dashboard.focusMode.breakDurationSeconds / 60
                if (s.isBangla) "বিরতি · ${PrayerTimeCalculator.toBanglaDigits(mins.toString())} মিনিট বিশ্রাম"
                else "Break · $mins min recharge"
            }
            dashboard.focusMode == FocusSessionMode.STOPWATCH -> {
                when {
                    isRunning -> s.focusFocusingStopwatch
                    dashboard.focusSessionElapsedSeconds > 0L -> s.focusPausedLabel
                    else -> s.focusReadyStopwatch
                }
            }
            else -> {
                val modeLabel = if (s.isBangla) dashboard.focusMode.labelBn else dashboard.focusMode.labelEn
                val prefix = if (isRunning) {
                    if (s.isBangla) "ফোকাসিং" else "Focusing"
                } else {
                    if (s.isBangla) "প্রস্তুত" else "Ready"
                }
                val cycleInfo = if (dashboard.completedPomodoros > 0) {
                    val c = if (s.isBangla) PrayerTimeCalculator.toBanglaDigits((dashboard.completedPomodoros + 1).toString())
                    else (dashboard.completedPomodoros + 1).toString()
                    " • #$c"
                } else ""
                "$prefix · $modeLabel$cycleInfo"
            }
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(StoneBackgroundTop, StoneBackground, StoneBackgroundBottom))
            )
            .statusBarsPadding()
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        val isCompactHeight = maxHeight < 660.dp
        val ringDiameter = if (isCompactHeight) 224.dp else 264.dp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Minimal Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 560.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                NeumorphicCircleButton(
                    onClick = onBack,
                    size = 44.dp,
                    modifier = Modifier.testTag("focus_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = stringResource(R.string.cd_back),
                        tint = StoneInkPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Centered Subject Selector (matches "• Physics" top label in shared pic)
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .clickable { showSubjectSheet = true }
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                        .testTag("focus_subject_selector"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(11.dp)
                            .clip(CircleShape)
                            .background(ringColor)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = activeSubject.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = StoneInkPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Rounded.KeyboardArrowDown,
                        contentDescription = s.focusSelectSubject,
                        tint = StoneInkSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                NeumorphicCircleButton(
                    onClick = onOpenLiveRoom,
                    size = 44.dp,
                    modifier = Modifier.testTag("focus_open_live_room")
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Groups,
                        contentDescription = s.focusLiveRoom,
                        tint = StoneInkPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(if (isCompactHeight) 14.dp else 28.dp))

            // Ambient Radial Glow + Large Circular Focus Dial (exact hero match to shared pic)
            Box(
                modifier = Modifier
                    .size(ringDiameter + 64.dp),
                contentAlignment = Alignment.Center
            ) {
                // Soft warm radial aura behind ring
                Box(
                    modifier = Modifier
                        .size(ringDiameter + 64.dp)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    ringColor.copy(alpha = if (isRunning) 0.17f else 0.09f),
                                    ringColor.copy(alpha = 0.04f),
                                    Color.Transparent
                                )
                            ),
                            shape = CircleShape
                        )
                )

                Canvas(
                    modifier = Modifier
                        .size(ringDiameter)
                        .testTag("focus_ring_canvas")
                ) {
                    val strokePx = 17.dp.toPx()
                    val diameter = size.minDimension - strokePx
                    val topLeft = Offset(strokePx / 2f, strokePx / 2f)
                    val arcSize = Size(diameter, diameter)

                    // Recessed outer stone circle track
                    drawArc(
                        color = FocusTrackStone,
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokePx, cap = StrokeCap.Round)
                    )

                    // Colored progress sweep
                    if (animatedProgress > 0.002f) {
                        drawArc(
                            color = ringColor,
                            startAngle = -90f,
                            sweepAngle = 360f * animatedProgress,
                            useCenter = false,
                            topLeft = topLeft,
                            size = arcSize,
                            style = Stroke(width = strokePx, cap = StrokeCap.Round)
                        )
                    }

                    // Indicator dot riding on top of ring track (12 o'clock at 0%)
                    val radius = diameter / 2f
                    val angleRad = ((-90f + 360f * animatedProgress) * PI / 180.0)
                    val dotCenter = Offset(
                        x = center.x + (radius * cos(angleRad)).toFloat(),
                        y = center.y + (radius * sin(angleRad)).toFloat()
                    )

                    drawCircle(
                        color = ringColor.copy(alpha = 0.25f),
                        radius = 12.dp.toPx(),
                        center = dotCenter
                    )
                    drawCircle(
                        color = ringColor,
                        radius = 7.5.dp.toPx(),
                        center = dotCenter
                    )
                }

                // Center Clock & Status Readout inside Ring
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 24.dp)
                ) {
                    Text(
                        text = clockReadout,
                        style = TextStyle(
                            fontFamily = PlusJakartaSansFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = if (isCompactHeight) 40.sp else 48.sp,
                            fontFeatureSettings = "tnum",
                            letterSpacing = (-1).sp,
                            color = StoneInkPrimary
                        ),
                        modifier = Modifier.testTag("focus_timer_readout")
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = statusSubtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = StoneInkSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.testTag("focus_status_subtitle")
                    )
                }
            }

            Spacer(modifier = Modifier.height(if (isCompactHeight) 16.dp else 28.dp))

            // Recessed Capsule Segmented Mode Switcher (Stopwatch | Pomodoro 25/5 | Deep 50/10)
            Row(
                modifier = Modifier
                    .widthIn(max = 460.dp)
                    .clip(RoundedCornerShape(50))
                    .background(FocusCapsuleBg)
                    .border(1.dp, Color.White.copy(alpha = 0.45f), RoundedCornerShape(50))
                    .padding(5.dp)
                    .testTag("focus_mode_switcher"),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FocusSessionMode.entries.forEach { mode ->
                    val selected = dashboard.focusMode == mode
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(50))
                            .background(if (selected) StoneSurface else Color.Transparent)
                            .clickable { onSelectMode(mode) }
                            .padding(horizontal = 10.dp, vertical = 12.dp)
                            .testTag("focus_mode_${mode.name}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (s.isBangla) mode.labelBn else mode.labelEn,
                            fontFamily = PlusJakartaSansFamily,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 12.sp,
                            color = if (selected) StoneInkPrimary else StoneInkSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // Transport Controls: Reset | Start/Pause Focus | Ambient Sound (and Skip Break when in break)
            Row(
                modifier = Modifier
                    .widthIn(max = 440.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NeumorphicCircleButton(
                    onClick = onResetSession,
                    size = 50.dp,
                    modifier = Modifier.testTag("focus_reset_btn")
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Refresh,
                        contentDescription = s.focusResetAction,
                        tint = StoneInkPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Row(
                    modifier = Modifier
                        .height(54.dp)
                        .widthIn(min = 164.dp)
                        .clip(RoundedCornerShape(50))
                        .background(if (isRunning) ringColor else StoneInkPrimary)
                        .clickable(onClick = onTogglePlayPause)
                        .padding(horizontal = 24.dp)
                        .testTag("focus_play_pause_btn"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = if (isRunning) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                        contentDescription = if (isRunning) s.focusPauseAction else s.focusStartAction,
                        tint = NeumorphicHighlight,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isRunning) s.focusPauseAction else s.focusStartAction,
                        fontFamily = PlusJakartaSansFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = NeumorphicHighlight
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                if (dashboard.isFocusBreakPhase) {
                    NeumorphicCircleButton(
                        onClick = onSkipBreak,
                        size = 50.dp,
                        modifier = Modifier.testTag("focus_skip_break_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.SkipNext,
                            contentDescription = s.focusSkipBreak,
                            tint = StoneSuccess,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                } else {
                    NeumorphicCircleButton(
                        onClick = onToggleAmbientSound,
                        size = 50.dp,
                        modifier = Modifier.testTag("focus_sound_btn")
                    ) {
                        Icon(
                            imageVector = if (dashboard.isAmbientSoundOn) Icons.Rounded.VolumeUp else Icons.Rounded.VolumeOff,
                            contentDescription = s.focusAmbientSound,
                            tint = if (dashboard.isAmbientSoundOn) StoneAccent else StoneInkSecondary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // Compact Bottom Study Summary & Utility Strip
            NeumorphicCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 520.dp),
                cornerRadius = 22.dp,
                shadowElevation = 6.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${activeSubject.name} • ${s.todayTotalLabel}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = StoneInkSecondary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            val goalStr = String.format(Locale.US, "%.1fh", activeSubject.goalHours)
                            val todayTime = formatFocusHms(activeSubject.todaySeconds)
                            Text(
                                text = if (s.isBangla) {
                                    "${PrayerTimeCalculator.toBanglaDigits(todayTime)} / ${PrayerTimeCalculator.toBanglaDigits(goalStr)}"
                                } else {
                                    "$todayTime / $goalStr"
                                },
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = StoneInkPrimary
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = s.focusCyclesLabel,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = StoneInkSecondary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            val cyclesStr = if (s.isBangla) {
                                PrayerTimeCalculator.toBanglaDigits(dashboard.completedPomodoros.toString())
                            } else {
                                dashboard.completedPomodoros.toString()
                            }
                            Text(
                                text = "$cyclesStr",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = StoneAccent
                            )
                        }
                    }

                    HorizontalDivider(color = StoneDivider, thickness = 0.8.dp)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        NeumorphicPillButton(
                            text = s.focusKeepAwake,
                            selected = dashboard.keepScreenOnInFocus,
                            leadingContent = {
                                Icon(
                                    imageVector = Icons.Rounded.WbSunny,
                                    contentDescription = null,
                                    tint = if (dashboard.keepScreenOnInFocus) StoneAccent else StoneInkSecondary,
                                    modifier = Modifier.size(14.dp)
                                )
                            },
                            onClick = onToggleKeepScreenOn,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("focus_keep_awake_toggle")
                        )
                        NeumorphicPillButton(
                            text = s.focusAmbientSound,
                            selected = dashboard.isAmbientSoundOn,
                            leadingContent = {
                                Icon(
                                    imageVector = if (dashboard.isAmbientSoundOn) Icons.Rounded.VolumeUp else Icons.Rounded.VolumeOff,
                                    contentDescription = null,
                                    tint = if (dashboard.isAmbientSoundOn) StoneAccent else StoneInkSecondary,
                                    modifier = Modifier.size(14.dp)
                                )
                            },
                            onClick = onToggleAmbientSound,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("focus_ambient_sound_pill")
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (showSubjectSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSubjectSheet = false },
            containerColor = StoneSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = s.focusSelectSubject,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = StoneInkPrimary
                    )
                    TextButton(
                        onClick = {
                            showSubjectSheet = false
                            showAddSubjectDialog = true
                        },
                        modifier = Modifier.testTag("focus_sheet_add_subject")
                    ) {
                        Icon(Icons.Rounded.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add", fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                dashboard.subjects.forEach { subj ->
                    val selected = subj.id == activeSubject.id
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (selected) StonePanelInner else Color.Transparent)
                            .clickable {
                                onSelectSubject(subj.id)
                                showSubjectSheet = false
                            }
                            .padding(horizontal = 14.dp, vertical = 14.dp)
                            .testTag("focus_pick_subject_${subj.id}"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(Color(subj.colorHex))
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = subj.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                            color = StoneInkPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = formatFocusHms(subj.todaySeconds),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = StoneInkSecondary
                        )
                        if (selected) {
                            Spacer(modifier = Modifier.width(10.dp))
                            Icon(
                                imageVector = Icons.Rounded.Check,
                                contentDescription = "Selected",
                                tint = StoneAccent,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddSubjectDialog) {
        FocusAddSubjectDialog(
            onDismiss = { showAddSubjectDialog = false },
            onConfirm = { name, colorHex, goalHours ->
                onAddSubject(name, colorHex, goalHours)
                showAddSubjectDialog = false
            }
        )
    }
}

@Composable
private fun FocusAddSubjectDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Long, Double) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedColor by remember { mutableLongStateOf(StudyDashboardDefaults.subjectColorPalette.first()) }
    var dailyHours by remember { mutableDoubleStateOf(2.0) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = StoneSurface,
        titleContentColor = StoneInkPrimary,
        textContentColor = StoneInkPrimary,
        title = {
            Text(
                text = "Add Subject",
                fontFamily = PlusJakartaSansFamily,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Subject Name") },
                    placeholder = { Text("e.g. Biology, Organic Chem") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = StoneAccent,
                        unfocusedBorderColor = StoneDivider,
                        focusedTextColor = StoneInkPrimary,
                        unfocusedTextColor = StoneInkPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StudyDashboardDefaults.subjectColorPalette.forEach { hex ->
                        val selected = selectedColor == hex
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(Color(hex))
                                .border(
                                    width = if (selected) 3.dp else 0.dp,
                                    color = if (selected) StoneInkPrimary else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { selectedColor = hex }
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(1.0, 2.0, 3.0, 4.0).forEach { hrs ->
                        val active = dailyHours == hrs
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (active) StoneInkPrimary else StonePanelInner)
                                .clickable { dailyHours = hrs }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${hrs.toInt()}h",
                                fontFamily = PlusJakartaSansFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = if (active) NeumorphicHighlight else StoneInkPrimary
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank()) onConfirm(name.trim(), selectedColor, dailyHours)
                }
            ) {
                Text("Add", fontWeight = FontWeight.Bold, color = StoneInkPrimary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = StoneInkSecondary)
            }
        }
    )
}

internal fun formatFocusHms(totalSeconds: Long): String {
    val safe = totalSeconds.coerceAtLeast(0L)
    val h = safe / 3600L
    val m = (safe % 3600L) / 60L
    val sec = safe % 60L
    return String.format(Locale.US, "%d:%02d:%02d", h, m, sec)
}

internal fun formatCountdownClock(totalSeconds: Int): String {
    val safe = totalSeconds.coerceAtLeast(0)
    val h = safe / 3600
    val m = (safe % 3600) / 60
    val sec = safe % 60
    return if (h > 0) {
        String.format(Locale.US, "%d:%02d:%02d", h, m, sec)
    } else {
        String.format(Locale.US, "%02d:%02d", m, sec)
    }
}
