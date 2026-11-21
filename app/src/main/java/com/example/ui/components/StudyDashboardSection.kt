package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FocusSessionMode
import com.example.data.model.StudyDashboardDefaults
import com.example.data.model.StudySubjectItem
import com.example.data.model.StudyTodoItem
import com.example.ui.theme.BarlowCondensedFamily
import com.example.ui.theme.NeumorphicHighlight
import com.example.ui.theme.PlusJakartaSansFamily
import com.example.ui.theme.StoneAccent
import com.example.ui.theme.StoneAccentSoft
import com.example.ui.theme.StoneBackground
import com.example.ui.theme.StoneDivider
import com.example.ui.theme.StoneInkMuted
import com.example.ui.theme.StoneInkPrimary
import com.example.ui.theme.StoneInkSecondary
import com.example.ui.theme.StonePanelInner
import com.example.ui.theme.StoneSuccess
import com.example.ui.theme.StoneSurface
import com.example.ui.viewmodel.StudyDashboardUiState
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

private val StoneRecessedTrack = Color(0xFFC5BEB7)

@Composable
fun StudyDashboardSection(
    dashboard: StudyDashboardUiState,
    myGroupsCount: Int,
    liveStudyingCount: Int,
    onToggleSubjectTimer: (String) -> Unit,
    onStartSubjectAndOpenRoom: (String) -> Unit,
    onOpenFocusMode: () -> Unit,
    onStartFocusAndOpen: () -> Unit,
    onToggleFocusButton: () -> Unit,
    onOpenMyGroups: () -> Unit,
    onAddSubject: (String, Long, Double) -> Unit,
    onUpdateSubjectGoal: (String, Double) -> Unit,
    onResetSubjectTimer: (String) -> Unit,
    onDeleteSubject: (String) -> Unit,
    onAddTodo: (String) -> Unit,
    onToggleTodo: (String) -> Unit,
    onDeleteTodo: (String) -> Unit,
    onUpdateFocusGoalHours: (Int) -> Unit,
    onUpdateBatchTag: (String) -> Unit,
    onSelectFocusMode: (FocusSessionMode) -> Unit,
    onSelectWeekDayIndex: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddSubjectDialog by remember { mutableStateOf(false) }
    var showAddTodoDialog by remember { mutableStateOf(false) }
    var showGoalBatchDialog by remember { mutableStateOf(false) }
    var editingSubjectForGoal by remember { mutableStateOf<StudySubjectItem?>(null) }

    val cal = remember { Calendar.getInstance() }
    val dayHeader = remember {
        SimpleDateFormat("EEEE, MMM d", Locale.US).format(cal.time).uppercase(Locale.US)
    }
    val greetingText = remember {
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        when {
            hour < 12 -> "Good morning \uD83D\uDC4B"
            hour < 17 -> "Good afternoon \uD83D\uDC4B"
            else -> "Good evening \uD83D\uDC4B"
        }
    }

    val weekDays = remember { buildCurrentWeekDays(cal) }
    val todayWeekIdx = weekDays.indexOfFirst { it.isToday }.coerceAtLeast(0)
    val activeDayIdx = if (dashboard.selectedWeekDayIdx in 0..6) dashboard.selectedWeekDayIdx else todayWeekIdx

    val totalHours = dashboard.totalTodaySeconds / 3600.0
    val goalHours = dashboard.dailyGoalHours.coerceAtLeast(1)
    val goalRatio = (totalHours / goalHours.toDouble()).toFloat().coerceIn(0f, 1f)
    val displayPercent = if (dashboard.totalTodaySeconds > 0L) {
        ((totalHours / goalHours.toDouble()) * 100.0).toInt().coerceIn(1, 100)
    } else 0

    val batchRank = remember(dashboard.totalTodaySeconds) {
        val sec = dashboard.totalTodaySeconds
        when {
            sec >= 21_600L -> 1
            sec >= 18_000L -> 2
            sec >= 14_400L -> 3
            sec >= 10_800L -> 4
            sec >= 7_200L -> 5
            sec >= 3_600L -> 6
            sec >= 1_800L -> 7
            sec >= 600L -> 8
            else -> 9
        }
    }

    val userLevel = remember(totalHours, dashboard.streakDays) {
        1 + (totalHours / 2.0).toInt() + (dashboard.streakDays / 7)
    }
    val levelTitle = when {
        userLevel >= 5 -> "Sage"
        userLevel >= 4 -> "Master"
        userLevel >= 3 -> "Scholar"
        userLevel >= 2 -> "Focused"
        else -> "Rookie"
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("study_dashboard_section"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Subtle separator between Ayat card and Study Dashboard
        HorizontalDivider(color = StoneDivider, thickness = 1.dp)

        // 1. Top Date + Batch Tag & Greeting (Main Stone App Typography)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .clickable { showGoalBatchDialog = true }
                .padding(horizontal = 4.dp, vertical = 2.dp)
        ) {
            Text(
                text = "$dayHeader · ${dashboard.batchTag}",
                fontFamily = PlusJakartaSansFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp,
                letterSpacing = 1.0.sp,
                color = StoneInkSecondary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = greetingText,
                style = MaterialTheme.typography.headlineMedium,
                fontSize = 24.sp,
                color = StoneInkPrimary,
                modifier = Modifier.testTag("dashboard_greeting")
            )
        }

        // 2. M T W T F S S Week Strip (Neumorphic Warm Stone Pills)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            weekDays.forEachIndexed { idx, dayInfo ->
                val bgColor = when {
                    dayInfo.isToday -> StoneAccent
                    idx < todayWeekIdx -> StoneInkPrimary
                    else -> StoneDivider
                }
                val textColor = when {
                    dayInfo.isToday || idx < todayWeekIdx -> NeumorphicHighlight
                    else -> StoneInkPrimary
                }
                val isSelectedOther = idx == activeDayIdx && !dayInfo.isToday

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { onSelectWeekDayIndex(idx) }
                        .padding(horizontal = 2.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = dayInfo.letter,
                        fontFamily = PlusJakartaSansFamily,
                        fontWeight = if (idx == activeDayIdx) FontWeight.Bold else FontWeight.SemiBold,
                        fontSize = 10.sp,
                        color = if (idx == activeDayIdx) StoneInkPrimary else StoneInkSecondary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .neumorphicRaised(
                                cornerRadius = 20.dp,
                                shadowElevation = if (dayInfo.isToday) 6.dp else 4.dp,
                                backgroundColor = bgColor
                            )
                            .clip(CircleShape)
                            .background(bgColor)
                            .then(
                                if (isSelectedOther) {
                                    Modifier.border(2.dp, StoneAccent, CircleShape)
                                } else Modifier
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = dayInfo.dayNumber.toString(),
                            fontFamily = PlusJakartaSansFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = textColor
                        )
                    }
                }
            }
        }

        // 3. STUDY TIME Card + 7 Capsule Bars
        NeumorphicCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 24.dp,
            shadowElevation = 7.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "STUDY TIME",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp,
                        color = StoneInkSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = String.format(Locale.US, "%.1f", totalHours),
                            fontFamily = BarlowCondensedFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 46.sp,
                            lineHeight = 48.sp,
                            color = StoneInkPrimary,
                            modifier = Modifier.testTag("dashboard_study_hours")
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "hrs",
                            style = MaterialTheme.typography.bodyMedium,
                            color = StoneInkSecondary,
                            modifier = Modifier.padding(bottom = 7.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(color = StoneInkSecondary, fontWeight = FontWeight.Medium)) {
                                append("This week ")
                            }
                            withStyle(SpanStyle(color = StoneAccent, fontWeight = FontWeight.Bold)) {
                                append("+${String.format(Locale.US, "%.1f", totalHours)} h \u2191")
                            }
                        },
                        style = MaterialTheme.typography.labelMedium
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // 7 Vertical Capsule Tracks (M T W T F S S)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    weekDays.forEachIndexed { idx, dayInfo ->
                        val ratio = if (dayInfo.isToday) {
                            (totalHours / goalHours.toDouble()).toFloat().coerceIn(0f, 1f)
                        } else {
                            0f
                        }
                        val animatedRatio by animateFloatAsState(targetValue = ratio, label = "cap_$idx")

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .width(8.dp)
                                    .height(86.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(StoneRecessedTrack),
                                contentAlignment = Alignment.BottomCenter
                            ) {
                                val fillColor = if (dayInfo.isToday) StoneAccent else StoneInkMuted
                                val fillHeight = max(6f, 86f * animatedRatio)
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(fillHeight.dp)
                                        .clip(RoundedCornerShape(50))
                                        .background(fillColor)
                                )
                            }
                            Spacer(modifier = Modifier.height(7.dp))
                            Text(
                                text = dayInfo.letter,
                                fontFamily = PlusJakartaSansFamily,
                                fontWeight = if (dayInfo.isToday) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 10.sp,
                                color = if (dayInfo.isToday) StoneInkPrimary else StoneInkSecondary
                            )
                        }
                    }
                }
            }
        }

        // 4. FOCUS GOAL + Top 3 Subject Progress Cards
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Max),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Left: Focus Goal Circular Ring
            NeumorphicCard(
                onClick = { showGoalBatchDialog = true },
                cornerRadius = 24.dp,
                shadowElevation = 7.dp,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .testTag("focus_goal_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "FOCUS GOAL",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp,
                        color = StoneInkSecondary,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    val ringFraction = if (dashboard.totalTodaySeconds > 0L) {
                        max(goalRatio, 0.015f)
                    } else 0f
                    val animRing by animateFloatAsState(targetValue = ringFraction, label = "goal_ring")

                    Box(
                        modifier = Modifier.size(104.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.size(104.dp)) {
                            val strokeW = 11.dp.toPx()
                            val diameter = size.minDimension - strokeW
                            val topLeft = Offset(strokeW / 2f, strokeW / 2f)
                            val arcSize = Size(diameter, diameter)

                            drawArc(
                                color = StoneRecessedTrack,
                                startAngle = 0f,
                                sweepAngle = 360f,
                                useCenter = false,
                                topLeft = topLeft,
                                size = arcSize,
                                style = Stroke(width = strokeW)
                            )

                            if (animRing > 0f) {
                                val sweep = 360f * animRing
                                drawArc(
                                    color = StoneAccent,
                                    startAngle = -90f,
                                    sweepAngle = sweep,
                                    useCenter = false,
                                    topLeft = topLeft,
                                    size = arcSize,
                                    style = Stroke(width = strokeW, cap = StrokeCap.Round)
                                )
                                val endAngleRad = ((-90f + sweep) * PI / 180.0)
                                val r = diameter / 2f
                                val cx = size.width / 2f + (r * cos(endAngleRad)).toFloat()
                                val cy = size.height / 2f + (r * sin(endAngleRad)).toFloat()
                                drawCircle(
                                    color = StoneAccent,
                                    radius = strokeW * 0.56f,
                                    center = Offset(cx, cy)
                                )
                            }
                        }

                        Text(
                            text = "$displayPercent%",
                            fontFamily = PlusJakartaSansFamily,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp,
                            color = StoneInkPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(color = StoneAccent, fontWeight = FontWeight.Bold)) {
                                append(String.format(Locale.US, "%.1f", totalHours))
                            }
                            withStyle(SpanStyle(color = StoneInkSecondary, fontWeight = FontWeight.Medium)) {
                                append(" / $goalHours")
                            }
                        },
                        fontFamily = PlusJakartaSansFamily,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "hours",
                        style = MaterialTheme.typography.labelSmall,
                        color = StoneInkSecondary
                    )
                }
            }

            // Right: Top 3 Subjects Goal Bars
            NeumorphicCard(
                cornerRadius = 24.dp,
                shadowElevation = 7.dp,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    val topThree = dashboard.subjects.take(3)
                    topThree.forEach { subj ->
                        val subjHrs = subj.todaySeconds / 3600.0
                        val targetHrs = subj.goalHours.coerceAtLeast(0.5)
                        val prog = (subjHrs / targetHrs).toFloat().coerceIn(0f, 1f)

                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = subj.name.uppercase(Locale.US),
                                    fontFamily = PlusJakartaSansFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    letterSpacing = 0.8.sp,
                                    color = StoneInkSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = buildAnnotatedString {
                                        withStyle(SpanStyle(color = StoneInkPrimary, fontWeight = FontWeight.Bold)) {
                                            append("${String.format(Locale.US, "%.1f", subjHrs)}h")
                                        }
                                        withStyle(SpanStyle(color = StoneInkSecondary, fontWeight = FontWeight.Medium)) {
                                            append(" / ${String.format(Locale.US, "%.1f", targetHrs)}h")
                                        }
                                    },
                                    fontFamily = PlusJakartaSansFamily,
                                    fontSize = 11.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(StoneRecessedTrack)
                            ) {
                                if (subj.todaySeconds > 0L) {
                                    val dotFrac = max(prog, 0.05f)
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(dotFrac)
                                            .height(4.dp)
                                            .clip(RoundedCornerShape(50))
                                            .background(StoneInkPrimary)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 5. STREAK / RANK / LEVEL Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Max),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MiniStatCard(
                label = "STREAK",
                value = dashboard.streakDays.toString(),
                subtitle = "days \uD83D\uDD25",
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )
            MiniStatCard(
                label = "RANK",
                value = "#$batchRank",
                subtitle = "in ${dashboard.batchTag}",
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )
            MiniStatCard(
                label = "LEVEL",
                value = userLevel.toString(),
                subtitle = levelTitle,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )
        }

        // 6. Focus & Live Room Action Cards
        val isAnyRunning = dashboard.activeSubjectId != null
        val activeSubjectName = dashboard.subjects.firstOrNull { it.id == dashboard.activeSubjectId }?.name

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Max),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            NeumorphicCard(
                onClick = onOpenFocusMode,
                cornerRadius = 24.dp,
                shadowElevation = 7.dp,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .testTag("dashboard_focus_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Focus",
                            style = MaterialTheme.typography.titleLarge,
                            color = StoneInkPrimary
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = if (isAnyRunning && activeSubjectName != null) {
                                if (dashboard.focusMode != FocusSessionMode.STOPWATCH) {
                                    "$activeSubjectName • ${formatMmSs(dashboard.focusCountdownSeconds)}"
                                } else {
                                    "$activeSubjectName • ${formatSubjectTimer(dashboard.focusSessionElapsedSeconds)}"
                                }
                            } else {
                                "Timer, pomodoro & goals"
                            },
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Medium,
                            color = if (isAnyRunning) StoneAccent else StoneInkSecondary,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .clip(RoundedCornerShape(50))
                            .background(if (isAnyRunning) StoneAccent else StoneInkPrimary)
                            .clickable(onClick = onStartFocusAndOpen)
                            .testTag("dashboard_focus_start_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isAnyRunning) "Open" else "Start",
                            fontFamily = PlusJakartaSansFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = NeumorphicHighlight
                        )
                    }
                }
            }

            NeumorphicCard(
                onClick = onOpenMyGroups,
                cornerRadius = 24.dp,
                shadowElevation = 7.dp,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .testTag("dashboard_my_groups_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "My Groups",
                            style = MaterialTheme.typography.titleLarge,
                            color = StoneInkPrimary
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "$myGroupsCount joined • $liveStudyingCount studying",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Medium,
                            color = StoneInkSecondary,
                            maxLines = 2
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .clip(RoundedCornerShape(50))
                            .background(StoneInkPrimary)
                            .clickable(onClick = onOpenMyGroups)
                            .testTag("dashboard_my_groups_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Open",
                            fontFamily = PlusJakartaSansFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = NeumorphicHighlight
                        )
                    }
                }
            }
        }

        // 7. SUBJECTS Card
        NeumorphicCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 24.dp,
            shadowElevation = 7.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SUBJECTS",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp,
                        color = StoneInkSecondary
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { showAddSubjectDialog = true }
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                            .testTag("dashboard_add_subject_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = "Add Subject",
                            tint = StoneInkPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "Add",
                            fontFamily = PlusJakartaSansFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = StoneInkPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                dashboard.subjects.forEachIndexed { index, subject ->
                    val isThisRunning = dashboard.activeSubjectId == subject.id
                    SubjectTimerRow(
                        subject = subject,
                        isRunning = isThisRunning,
                        canDelete = dashboard.subjects.size > 1,
                        onRowClick = { onStartSubjectAndOpenRoom(subject.id) },
                        onToggleRun = {
                            if (isThisRunning) {
                                onToggleSubjectTimer(subject.id)
                            } else {
                                onStartSubjectAndOpenRoom(subject.id)
                            }
                        },
                        onEditGoal = { editingSubjectForGoal = subject },
                        onReset = { onResetSubjectTimer(subject.id) },
                        onDelete = { onDeleteSubject(subject.id) }
                    )
                    if (index < dashboard.subjects.lastIndex) {
                        HorizontalDivider(color = StoneDivider, thickness = 0.8.dp)
                    }
                }
            }
        }

        // 8. SESSION SETUP Card
        NeumorphicCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 24.dp,
            shadowElevation = 7.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SESSION SETUP",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp,
                        color = StoneInkSecondary
                    )
                    Text(
                        text = "${dashboard.batchTag} • ${goalHours}h Goal",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = StoneAccent,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { showGoalBatchDialog = true }
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FocusSessionMode.entries.forEach { mode ->
                        val selected = dashboard.focusMode == mode
                        NeumorphicPillButton(
                            text = mode.labelEn,
                            selected = selected,
                            onClick = { onSelectFocusMode(mode) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // 9. TO-DO LIST Card
        NeumorphicCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 24.dp,
            shadowElevation = 7.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TO-DO LIST",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp,
                        color = StoneInkSecondary
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { showAddTodoDialog = true }
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                            .testTag("dashboard_add_todo_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = "Add To-Do",
                            tint = StoneInkPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "Add",
                            fontFamily = PlusJakartaSansFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = StoneInkPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                if (dashboard.todos.isEmpty()) {
                    Text(
                        text = "Tap + Add to create your study checklist.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = StoneInkSecondary,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                } else {
                    dashboard.todos.forEachIndexed { idx, todo ->
                        TodoChecklistRow(
                            item = todo,
                            onToggle = { onToggleTodo(todo.id) },
                            onDelete = { onDeleteTodo(todo.id) }
                        )
                        if (idx < dashboard.todos.lastIndex) {
                            HorizontalDivider(color = StoneDivider, thickness = 0.8.dp)
                        }
                    }
                }
            }
        }
    }

    if (showAddSubjectDialog) {
        AddSubjectDialog(
            onDismiss = { showAddSubjectDialog = false },
            onConfirm = { name, color, hours ->
                onAddSubject(name, color, hours)
                showAddSubjectDialog = false
            }
        )
    }

    if (showAddTodoDialog) {
        AddTodoDialog(
            onDismiss = { showAddTodoDialog = false },
            onConfirm = { title ->
                onAddTodo(title)
                showAddTodoDialog = false
            }
        )
    }

    if (showGoalBatchDialog) {
        FocusGoalAndBatchDialog(
            currentGoalHours = dashboard.dailyGoalHours,
            currentBatchTag = dashboard.batchTag,
            onDismiss = { showGoalBatchDialog = false },
            onConfirm = { newGoal, newBatch ->
                onUpdateFocusGoalHours(newGoal)
                onUpdateBatchTag(newBatch)
                showGoalBatchDialog = false
            }
        )
    }

    editingSubjectForGoal?.let { subj ->
        EditSubjectGoalDialog(
            subject = subj,
            onDismiss = { editingSubjectForGoal = null },
            onConfirm = { hrs ->
                onUpdateSubjectGoal(subj.id, hrs)
                editingSubjectForGoal = null
            }
        )
    }
}

@Composable
private fun MiniStatCard(
    label: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    NeumorphicCard(
        modifier = modifier,
        cornerRadius = 22.dp,
        shadowElevation = 6.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.1.sp,
                color = StoneInkSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                fontFamily = BarlowCondensedFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                color = StoneInkPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium,
                color = StoneInkSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun SubjectTimerRow(
    subject: StudySubjectItem,
    isRunning: Boolean,
    canDelete: Boolean,
    onRowClick: () -> Unit,
    onToggleRun: () -> Unit,
    onEditGoal: () -> Unit,
    onReset: () -> Unit,
    onDelete: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }
    val dotColor = Color(subject.colorHex)
    val goalSeconds = (subject.goalHours * 3600.0).coerceAtLeast(60.0)
    val progress = (subject.todaySeconds / goalSeconds).toFloat().coerceIn(0f, 1f)
    val barFill = if (subject.todaySeconds > 0L) max(progress, 0.04f) else 0f

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onRowClick)
            .padding(vertical = 12.dp)
            .testTag("subject_row_${subject.id}"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(dotColor)
        )
        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = subject.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = StoneInkPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(7.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(50))
                    .background(StoneRecessedTrack)
            ) {
                if (barFill > 0f) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(barFill)
                            .height(4.dp)
                            .clip(RoundedCornerShape(50))
                            .background(dotColor)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = formatSubjectTimer(subject.todaySeconds),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isRunning) FontWeight.Bold else FontWeight.SemiBold,
            color = if (isRunning) StoneAccent else StoneInkPrimary
        )

        Spacer(modifier = Modifier.width(10.dp))

        Box(
            modifier = Modifier
                .size(40.dp)
                .neumorphicRaised(
                    cornerRadius = 20.dp,
                    shadowElevation = 4.dp,
                    backgroundColor = if (isRunning) StoneAccent else StoneInkPrimary
                )
                .clip(CircleShape)
                .background(if (isRunning) StoneAccent else StoneInkPrimary)
                .clickable(onClick = onToggleRun)
                .testTag("play_subject_${subject.id}"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isRunning) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                contentDescription = if (isRunning) "Pause ${subject.name}" else "Start ${subject.name}",
                tint = NeumorphicHighlight,
                modifier = Modifier.size(20.dp)
            )
        }

        Box {
            IconButton(
                onClick = { menuExpanded = true },
                modifier = Modifier.size(34.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.MoreVert,
                    contentDescription = "Subject options",
                    tint = StoneInkSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }
            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Set Goal (${String.format(Locale.US, "%.1f", subject.goalHours)}h)") },
                    onClick = {
                        menuExpanded = false
                        onEditGoal()
                    }
                )
                DropdownMenuItem(
                    text = { Text("Reset Timer") },
                    onClick = {
                        menuExpanded = false
                        onReset()
                    }
                )
                if (canDelete) {
                    DropdownMenuItem(
                        text = { Text("Delete Subject", color = StoneAccent) },
                        onClick = {
                            menuExpanded = false
                            onDelete()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun TodoChecklistRow(
    item: StudyTodoItem,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .padding(vertical = 12.dp)
            .testTag("todo_row_${item.id}"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(if (item.isDone) StoneSuccess else StonePanelInner)
                .border(
                    width = 1.5.dp,
                    color = if (item.isDone) StoneSuccess else StoneInkMuted,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (item.isDone) {
                Icon(
                    imageVector = Icons.Rounded.Check,
                    contentDescription = "Completed",
                    tint = NeumorphicHighlight,
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

        Text(
            text = item.title,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            color = if (item.isDone) StoneInkMuted else StoneInkPrimary,
            textDecoration = if (item.isDone) TextDecoration.LineThrough else TextDecoration.None,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )

        IconButton(
            onClick = onDelete,
            modifier = Modifier
                .size(36.dp)
                .testTag("delete_todo_${item.id}")
        ) {
            Icon(
                imageVector = Icons.Rounded.Close,
                contentDescription = "Remove task",
                tint = StoneInkSecondary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun AddSubjectDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Long, Double) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedColor by remember { mutableLongStateOf(StudyDashboardDefaults.subjectColorPalette.first()) }
    var goalHours by remember { mutableDoubleStateOf(2.0) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = StoneSurface,
        titleContentColor = StoneInkPrimary,
        textContentColor = StoneInkPrimary,
        title = { Text("Add Study Subject", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Subject Name (e.g. Biology)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = StoneAccent,
                        unfocusedBorderColor = StoneDivider,
                        focusedTextColor = StoneInkPrimary,
                        unfocusedTextColor = StoneInkPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_subject_input")
                )
                Text("Color", fontSize = 12.sp, color = StoneInkSecondary, fontWeight = FontWeight.SemiBold)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    StudyDashboardDefaults.subjectColorPalette.forEach { hex ->
                        val isSel = selectedColor == hex
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Color(hex))
                                .then(if (isSel) Modifier.border(2.5.dp, StoneInkPrimary, CircleShape) else Modifier)
                                .clickable { selectedColor = hex }
                        )
                    }
                }
                Text("Daily Target Hours", fontSize = 12.sp, color = StoneInkSecondary, fontWeight = FontWeight.SemiBold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(1.0, 2.0, 3.0, 4.0).forEach { hrs ->
                        val isSel = goalHours == hrs
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSel) StoneAccent else StoneBackground)
                                .clickable { goalHours = hrs }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "${hrs.toInt()}h",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSel) NeumorphicHighlight else StoneInkPrimary
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { if (name.isNotBlank()) onConfirm(name, selectedColor, goalHours) },
                modifier = Modifier.testTag("confirm_add_subject")
            ) {
                Text("Add", fontWeight = FontWeight.Bold, color = StoneAccent)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = StoneInkSecondary) }
        }
    )
}

@Composable
private fun AddTodoDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = StoneSurface,
        titleContentColor = StoneInkPrimary,
        textContentColor = StoneInkPrimary,
        title = { Text("Add To-Do Task", fontWeight = FontWeight.Bold) },
        text = {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Task (e.g. chemistry chapter 3)") },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = StoneAccent,
                    unfocusedBorderColor = StoneDivider,
                    focusedTextColor = StoneInkPrimary,
                    unfocusedTextColor = StoneInkPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("add_todo_input")
            )
        },
        confirmButton = {
            TextButton(
                onClick = { if (title.isNotBlank()) onConfirm(title) },
                modifier = Modifier.testTag("confirm_add_todo")
            ) {
                Text("Add", fontWeight = FontWeight.Bold, color = StoneAccent)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = StoneInkSecondary) }
        }
    )
}

@Composable
private fun FocusGoalAndBatchDialog(
    currentGoalHours: Int,
    currentBatchTag: String,
    onDismiss: () -> Unit,
    onConfirm: (Int, String) -> Unit
) {
    var goalHours by remember { mutableStateOf(currentGoalHours) }
    var batchTag by remember { mutableStateOf(currentBatchTag) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = StoneSurface,
        titleContentColor = StoneInkPrimary,
        textContentColor = StoneInkPrimary,
        title = { Text("Focus Goal & Batch", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = batchTag,
                    onValueChange = { batchTag = it },
                    label = { Text("Batch / Circle Tag (e.g. HSC-26)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = StoneAccent,
                        unfocusedBorderColor = StoneDivider,
                        focusedTextColor = StoneInkPrimary,
                        unfocusedTextColor = StoneInkPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Text("Daily Study Goal", fontSize = 12.sp, color = StoneInkSecondary, fontWeight = FontWeight.SemiBold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(2, 4, 6, 8, 10).forEach { hrs ->
                        val isSel = goalHours == hrs
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSel) StoneAccent else StoneBackground)
                                .clickable { goalHours = hrs }
                                .padding(horizontal = 11.dp, vertical = 7.dp)
                        ) {
                            Text(
                                text = "${hrs}h",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSel) NeumorphicHighlight else StoneInkPrimary
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(goalHours, batchTag) }) {
                Text("Save", fontWeight = FontWeight.Bold, color = StoneAccent)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = StoneInkSecondary) }
        }
    )
}

@Composable
private fun EditSubjectGoalDialog(
    subject: StudySubjectItem,
    onDismiss: () -> Unit,
    onConfirm: (Double) -> Unit
) {
    var goal by remember { mutableDoubleStateOf(subject.goalHours) }
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = StoneSurface,
        titleContentColor = StoneInkPrimary,
        textContentColor = StoneInkPrimary,
        title = { Text("${subject.name} Target", fontWeight = FontWeight.Bold) },
        text = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(1.0, 2.0, 3.0, 4.0, 5.0).forEach { hrs ->
                    val isSel = goal == hrs
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSel) StoneAccent else StoneAccentSoft)
                            .clickable { goal = hrs }
                            .padding(horizontal = 11.dp, vertical = 7.dp)
                    ) {
                        Text(
                            text = "${hrs.toInt()}h",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSel) NeumorphicHighlight else StoneInkPrimary
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(goal) }) {
                Text("Save", fontWeight = FontWeight.Bold, color = StoneAccent)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = StoneInkSecondary) }
        }
    )
}

private data class WeekDayInfo(
    val letter: String,
    val dayNumber: Int,
    val isToday: Boolean
)

private fun buildCurrentWeekDays(now: Calendar): List<WeekDayInfo> {
    val letters = listOf("M", "T", "W", "T", "F", "S", "S")
    val todayDayOfWeek = now.get(Calendar.DAY_OF_WEEK)
    val mondayOffset = when (todayDayOfWeek) {
        Calendar.MONDAY -> 0
        Calendar.TUESDAY -> -1
        Calendar.WEDNESDAY -> -2
        Calendar.THURSDAY -> -3
        Calendar.FRIDAY -> -4
        Calendar.SATURDAY -> -5
        Calendar.SUNDAY -> -6
        else -> 0
    }
    val todayIndex = -mondayOffset

    return (0..6).map { idx ->
        val c = (now.clone() as Calendar).apply {
            add(Calendar.DAY_OF_MONTH, mondayOffset + idx)
        }
        WeekDayInfo(
            letter = letters[idx],
            dayNumber = c.get(Calendar.DAY_OF_MONTH),
            isToday = idx == todayIndex
        )
    }
}

fun formatSubjectTimer(seconds: Long): String {
    val safe = seconds.coerceAtLeast(0L)
    val h = safe / 3600L
    val m = (safe % 3600L) / 60L
    val s = safe % 60L
    return String.format(Locale.US, "%d:%02d:%02d", h, m, s)
}

private fun formatMmSs(seconds: Int): String {
    val safe = seconds.coerceAtLeast(0)
    val m = safe / 60
    val s = safe % 60
    return String.format(Locale.US, "%02d:%02d", m, s)
}
