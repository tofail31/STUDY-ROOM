package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.calc.PrayerTimeCalculator
import com.example.data.local.StudyHistoryAnalytics
import com.example.data.local.WeeklyTrendPoint
import com.example.data.model.GroupMember
import com.example.data.model.StudyGroup
import com.example.data.model.StudyStatus
import com.example.ui.components.NeumorphicCard
import com.example.ui.components.NeumorphicPillButton
import com.example.ui.theme.BarlowCondensedFamily
import com.example.ui.theme.LocalAppStrings
import com.example.ui.theme.StoneAccent
import com.example.ui.theme.StoneBackground
import com.example.ui.theme.StoneDivider
import com.example.ui.theme.StoneInkPrimary
import com.example.ui.theme.StoneInkSecondary
import com.example.ui.theme.StonePanel
import com.example.ui.theme.StoneSuccess
import java.util.Calendar
import java.util.Locale

private val monthsEn = listOf(
    "January", "February", "March", "April", "May", "June",
    "July", "August", "September", "October", "November", "December"
)
private val monthsBn = listOf(
    "জানুয়ারি", "ফেব্রুয়ারি", "মার্চ", "এপ্রিল", "মে", "জুন",
    "জুলাই", "আগস্ট", "সেপ্টেম্বর", "অক্টোবর", "নভেম্বর", "ডিসেম্বর"
)

private fun formatCompactHours(totalSeconds: Long, isBangla: Boolean): String {
    val hours = totalSeconds / 3600f
    val formatted = String.format(Locale.US, "%.1f", hours)
    val suffix = if (isBangla) "ঘ." else "h"
    return if (isBangla) "${PrayerTimeCalculator.toBanglaDigits(formatted)}$suffix" else "$formatted$suffix"
}

private fun formatHmDuration(totalSeconds: Long, isBangla: Boolean): String {
    val h = totalSeconds / 3600L
    val m = (totalSeconds % 3600L) / 60L
    val raw = if (h > 0) "${h}h ${m}m" else "${m}m"
    return if (isBangla) {
        PrayerTimeCalculator.toBanglaDigits(raw.replace("h", "ঘ").replace("m", "মি"))
    } else raw
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentDetailBottomSheet(
    member: GroupMember,
    group: StudyGroup,
    isCheered: Boolean,
    onCheer: () -> Unit,
    onDismiss: () -> Unit
) {
    val s = LocalAppStrings.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val nowCal = remember { Calendar.getInstance() }
    val currentYear = nowCal.get(Calendar.YEAR)
    val currentMonth = nowCal.get(Calendar.MONTH)
    val currentDay = nowCal.get(Calendar.DAY_OF_MONTH)

    var viewYear by remember(member.id) { mutableIntStateOf(currentYear) }
    var viewMonth by remember(member.id) { mutableIntStateOf(currentMonth) }
    var selectedDay by remember(member.id) { mutableIntStateOf(currentDay) }

    val statusColor = when (member.status) {
        StudyStatus.STUDYING -> StoneSuccess
        StudyStatus.RESTING -> StoneAccent
        StudyStatus.OFFLINE -> StoneInkSecondary
    }
    val statusLabel = when (member.status) {
        StudyStatus.STUDYING -> s.statusStudying
        StudyStatus.RESTING -> s.statusResting
        StudyStatus.OFFLINE -> s.statusOffline
    }

    val monthCal = remember(viewYear, viewMonth) {
        Calendar.getInstance().apply { set(viewYear, viewMonth, 1) }
    }
    val daysInMonth = monthCal.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDayOffset = monthCal.get(Calendar.DAY_OF_WEEK) - 1 // Sunday = 0
    val safeSelectedDay = selectedDay.coerceIn(1, daysInMonth)

    val monthRecords = remember(member.id, member.todayStudySeconds, viewYear, viewMonth, s.isBangla) {
        (1..daysInMonth).associateWith { d ->
            StudyHistoryAnalytics.getDayRecord(member, group, viewYear, viewMonth, d, s.isBangla, nowCal)
        }
    }
    val selectedRecord = monthRecords[safeSelectedDay]
        ?: StudyHistoryAnalytics.getDayRecord(member, group, viewYear, viewMonth, safeSelectedDay, s.isBangla, nowCal)

    val monthSummary = remember(member.id, member.todayStudySeconds, viewYear, viewMonth, s.isBangla) {
        StudyHistoryAnalytics.getMonthSummary(member, group, viewYear, viewMonth, s.isBangla, nowCal)
    }

    val weeklyPoints = remember(member.id, member.todayStudySeconds, viewYear, viewMonth, safeSelectedDay, s.isBangla) {
        StudyHistoryAnalytics.getSevenDayTrend(member, group, viewYear, viewMonth, safeSelectedDay, s.isBangla, nowCal)
    }

    val canGoNextMonth = viewYear < currentYear || (viewYear == currentYear && viewMonth < currentMonth)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = StoneBackground,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 26.dp)
        ) {
            // 1. Student Profile Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(statusColor.copy(alpha = 0.16f))
                        .border(2.dp, statusColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = member.initials,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = statusColor
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = member.name,
                        style = MaterialTheme.typography.titleLarge,
                        color = StoneInkPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(statusColor)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        val subj = if (s.isBangla) member.subjectBn else member.subjectEn
                        Text(
                            text = "$statusLabel • $subj",
                            style = MaterialTheme.typography.labelMedium,
                            color = StoneInkSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                if (!member.isCurrentUser) {
                    Spacer(modifier = Modifier.width(8.dp))
                    NeumorphicPillButton(
                        text = if (isCheered) s.salamSentLabel else s.sendSalamButton,
                        selected = isCheered,
                        leadingContent = {
                            Icon(
                                imageVector = if (isCheered) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                                contentDescription = null,
                                tint = if (isCheered) StoneAccent else StoneInkPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        onClick = onCheer,
                        modifier = Modifier.testTag("student_sheet_cheer_btn")
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 2. KPI Summary Row
            val streakVal = if (s.isBangla) {
                PrayerTimeCalculator.toBanglaDigits("${member.streakDays} দিন")
            } else {
                "${member.streakDays}d"
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StudentMetricTile(
                    label = s.monthlyTotalLabel,
                    value = formatCompactHours(monthSummary.totalSeconds, s.isBangla),
                    accentColor = StoneSuccess,
                    modifier = Modifier.weight(1f)
                )
                StudentMetricTile(
                    label = s.dailyAvgLabel,
                    value = formatHmDuration(monthSummary.dailyAverageSeconds, s.isBangla),
                    accentColor = StoneInkPrimary,
                    modifier = Modifier.weight(1f)
                )
                StudentMetricTile(
                    label = s.streakLabel,
                    value = streakVal,
                    accentColor = StoneAccent,
                    leadingFire = true,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 3. Interactive Monthly Calendar Card
            NeumorphicCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 22.dp,
                shadowElevation = 6.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Rounded.CalendarMonth,
                                contentDescription = null,
                                tint = StoneAccent,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = s.studyCalendarTitle,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = StoneInkPrimary
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = {
                                    if (viewMonth == 0) {
                                        viewYear -= 1
                                        viewMonth = 11
                                    } else {
                                        viewMonth -= 1
                                    }
                                    selectedDay = 1
                                },
                                modifier = Modifier
                                    .size(36.dp)
                                    .testTag("student_calendar_prev_month")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowLeft,
                                    contentDescription = "Previous Month",
                                    tint = StoneInkPrimary
                                )
                            }

                            val monthName = if (s.isBangla) monthsBn[viewMonth] else monthsEn[viewMonth]
                            val yearStr = if (s.isBangla) PrayerTimeCalculator.toBanglaDigits(viewYear.toString()) else viewYear.toString()
                            Text(
                                text = "$monthName $yearStr",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = StoneInkPrimary
                            )

                            IconButton(
                                onClick = {
                                    if (canGoNextMonth) {
                                        if (viewMonth == 11) {
                                            viewYear += 1
                                            viewMonth = 0
                                        } else {
                                            viewMonth += 1
                                        }
                                        selectedDay = if (viewYear == currentYear && viewMonth == currentMonth) {
                                            currentDay
                                        } else {
                                            1
                                        }
                                    }
                                },
                                enabled = canGoNextMonth,
                                modifier = Modifier
                                    .size(36.dp)
                                    .testTag("student_calendar_next_month")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                                    contentDescription = "Next Month",
                                    tint = if (canGoNextMonth) StoneInkPrimary else StoneDivider
                                )
                            }
                        }
                    }

                    Text(
                        text = s.tapDateHintLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = StoneInkSecondary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    val weekdays = if (s.isBangla) {
                        listOf("র", "সো", "ম", "বু", "বৃ", "শু", "শ")
                    } else {
                        listOf("S", "M", "T", "W", "T", "F", "S")
                    }
                    Row(modifier = Modifier.fillMaxWidth()) {
                        weekdays.forEach { w ->
                            Text(
                                text = w,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = StoneInkSecondary,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    val totalCells = firstDayOffset + daysInMonth
                    val totalRows = (totalCells + 6) / 7

                    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                        for (rowIdx in 0 until totalRows) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                for (colIdx in 0 until 7) {
                                    val cellIdx = rowIdx * 7 + colIdx
                                    val dayNum = cellIdx - firstDayOffset + 1
                                    if (dayNum in 1..daysInMonth) {
                                        val rec = monthRecords[dayNum]
                                        val hrs = (rec?.totalSeconds ?: 0L) / 3600f
                                        val isSelected = dayNum == safeSelectedDay
                                        val isTodayCell = rec?.isToday == true
                                        val isFutureCell = rec?.isFuture == true

                                        val heatBg = when {
                                            isFutureCell -> StonePanel.copy(alpha = 0.35f)
                                            hrs >= 5f -> StoneSuccess.copy(alpha = 0.42f)
                                            hrs >= 2f -> StoneSuccess.copy(alpha = 0.24f)
                                            hrs > 0f -> StoneSuccess.copy(alpha = 0.12f)
                                            else -> StonePanel.copy(alpha = 0.55f)
                                        }

                                        val dayText = if (s.isBangla) {
                                            PrayerTimeCalculator.toBanglaDigits(dayNum.toString())
                                        } else {
                                            dayNum.toString()
                                        }

                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(40.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(heatBg)
                                                .then(
                                                    if (isSelected) {
                                                        Modifier.border(2.dp, StoneAccent, RoundedCornerShape(10.dp))
                                                    } else {
                                                        Modifier
                                                    }
                                                )
                                                .clickable { selectedDay = dayNum }
                                                .testTag("calendar_day_$dayNum"),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text(
                                                    text = dayText,
                                                    style = MaterialTheme.typography.labelMedium,
                                                    fontWeight = if (isSelected || isTodayCell) FontWeight.Bold else FontWeight.Medium,
                                                    color = if (isFutureCell) StoneInkSecondary.copy(alpha = 0.55f) else StoneInkPrimary
                                                )
                                                if (!isFutureCell && hrs > 0f) {
                                                    val barWidth = (hrs / 8f).coerceIn(0.25f, 1f)
                                                    Box(
                                                        modifier = Modifier
                                                            .padding(top = 2.dp)
                                                            .width((18 * barWidth).dp)
                                                            .height(3.dp)
                                                            .clip(CircleShape)
                                                            .background(if (isSelected) StoneAccent else StoneSuccess)
                                                    )
                                                } else if (isTodayCell) {
                                                    Box(
                                                        modifier = Modifier
                                                            .padding(top = 2.dp)
                                                            .size(4.dp)
                                                            .clip(CircleShape)
                                                            .background(StoneAccent)
                                                    )
                                                }
                                            }
                                        }
                                    } else {
                                        Spacer(modifier = Modifier.weight(1f).height(40.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 4. Selected Date Study Hours Card (Directly below Calendar)
            val dateMonthName = if (s.isBangla) monthsBn[viewMonth] else monthsEn[viewMonth].take(3)
            val dateNumStr = if (s.isBangla) PrayerTimeCalculator.toBanglaDigits(safeSelectedDay.toString()) else safeSelectedDay.toString()
            val dateYearStr = if (s.isBangla) PrayerTimeCalculator.toBanglaDigits(viewYear.toString()) else viewYear.toString()
            val selectedDateFormatted = "$dateNumStr $dateMonthName $dateYearStr"

            NeumorphicCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 22.dp,
                shadowElevation = 6.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = s.selectedDateStudyTitle,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = StoneInkSecondary
                            )
                            Text(
                                text = selectedDateFormatted,
                                style = MaterialTheme.typography.titleMedium,
                                color = StoneInkPrimary
                            )
                        }

                        Text(
                            text = formatStudyHms(selectedRecord.totalSeconds, s.isBangla),
                            fontFamily = BarlowCondensedFamily,
                            fontSize = 38.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedRecord.totalSeconds > 0L) StoneSuccess else StoneInkSecondary,
                            modifier = Modifier.testTag("selected_day_study_time")
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    val progressRatio = (selectedRecord.totalSeconds.toFloat() / selectedRecord.targetSeconds.toFloat()).coerceIn(0f, 1f)
                    val pctInt = (progressRatio * 100).toInt()
                    val pctText = if (s.isBangla) PrayerTimeCalculator.toBanglaDigits("$pctInt%") else "$pctInt%"
                    val targetGoalText = if (s.isBangla) {
                        PrayerTimeCalculator.toBanglaDigits("${group.dailyTargetHours} ঘ.")
                    } else {
                        "${group.dailyTargetHours}h"
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(CircleShape)
                            .background(StoneDivider)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(progressRatio)
                                .height(8.dp)
                                .clip(CircleShape)
                                .background(if (progressRatio >= 1f) StoneSuccess else StoneAccent)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${s.dailyTargetPrefix}: $targetGoalText ($pctText)",
                            style = MaterialTheme.typography.labelSmall,
                            color = StoneInkSecondary
                        )
                    }

                    if (selectedRecord.totalSeconds == 0L) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = s.noStudyOnDateLabel,
                            style = MaterialTheme.typography.bodySmall,
                            color = StoneInkSecondary
                        )
                    } else {
                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = StoneDivider)
                        Spacer(modifier = Modifier.height(10.dp))

                        val focusText = if (s.isBangla) PrayerTimeCalculator.toBanglaDigits("${selectedRecord.focusScorePercent}%") else "${selectedRecord.focusScorePercent}%"
                        val maxBlockText = if (s.isBangla) PrayerTimeCalculator.toBanglaDigits("${selectedRecord.longestBlockMinutes} মি") else "${selectedRecord.longestBlockMinutes}m"
                        val topSubj = selectedRecord.subjectShares.firstOrNull()?.subject ?: (if (s.isBangla) member.subjectBn else member.subjectEn)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            DayDetailChip(s.focusScoreLabel, focusText, StoneSuccess, Modifier.weight(1f))
                            DayDetailChip(s.longestSessionLabel, maxBlockText, StoneAccent, Modifier.weight(1f))
                            DayDetailChip(s.topSubjectLabel, topSubj, StoneInkPrimary, Modifier.weight(1.3f))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 5. Graphs Section (24-Hour Focus Timeline + 7-Day Trend + Subject Breakdown)
            NeumorphicCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 22.dp,
                shadowElevation = 6.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // 5A. 24-Hour Focus Timeline Graph
                    Text(
                        text = s.hourlyTimelineTitle,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = StoneInkPrimary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    HourlyFocusBarGraph(
                        hourlyMinutes = selectedRecord.hourlyMinutes,
                        isBangla = s.isBangla,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(104.dp)
                            .testTag("hourly_focus_graph")
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = StoneDivider)
                    Spacer(modifier = Modifier.height(14.dp))

                    // 5B. 7-Day Study Hours Trend Graph
                    Text(
                        text = s.weeklyTrendTitle,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = StoneInkPrimary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    WeeklyTrendBarLineGraph(
                        points = weeklyPoints,
                        isBangla = s.isBangla,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("weekly_trend_graph")
                    )

                    if (selectedRecord.subjectShares.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = StoneDivider)
                        Spacer(modifier = Modifier.height(14.dp))

                        // 5C. Subject Distribution Bars
                        Text(
                            text = s.subjectBreakdownTitle,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = StoneInkPrimary
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            selectedRecord.subjectShares.forEachIndexed { idx, share ->
                                val barColor = when (idx) {
                                    0 -> StoneSuccess
                                    1 -> StoneAccent
                                    else -> StoneInkSecondary
                                }
                                val pctStr = if (s.isBangla) PrayerTimeCalculator.toBanglaDigits("${share.percent}%") else "${share.percent}%"
                                val durStr = formatHmDuration(share.seconds, s.isBangla)

                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = share.subject,
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = StoneInkPrimary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "$durStr ($pctStr)",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = barColor
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(7.dp)
                                            .clip(CircleShape)
                                            .background(StoneDivider)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth((share.percent / 100f).coerceIn(0.04f, 1f))
                                                .height(7.dp)
                                                .clip(CircleShape)
                                                .background(barColor)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StudentMetricTile(
    label: String,
    value: String,
    accentColor: Color,
    modifier: Modifier = Modifier,
    leadingFire: Boolean = false
) {
    NeumorphicCard(
        modifier = modifier,
        cornerRadius = 16.dp,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (leadingFire) {
                    Icon(
                        imageVector = Icons.Rounded.LocalFireDepartment,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                }
                Text(
                    text = value,
                    fontFamily = BarlowCondensedFamily,
                    fontSize = 23.sp,
                    fontWeight = FontWeight.Bold,
                    color = accentColor,
                    maxLines = 1
                )
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = StoneInkSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun DayDetailChip(
    label: String,
    value: String,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(StonePanel.copy(alpha = 0.65f))
            .padding(horizontal = 8.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 10.sp,
            color = StoneInkSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = valueColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun HourlyFocusBarGraph(
    hourlyMinutes: List<Int>,
    isBangla: Boolean,
    modifier: Modifier = Modifier
) {
    val peakHour = remember(hourlyMinutes) {
        val maxVal = hourlyMinutes.maxOrNull() ?: 0
        if (maxVal > 0) hourlyMinutes.indexOf(maxVal) else -1
    }

    Column(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            val chartHeight = size.height
            val chartWidth = size.width
            val dashEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)

            // Reference guide lines: 30m (50%) and 60m (top)
            listOf(0.5f, 0.04f).forEach { frac ->
                val y = chartHeight * frac
                drawLine(
                    color = StoneDivider,
                    start = Offset(0f, y),
                    end = Offset(chartWidth, y),
                    strokeWidth = 1.dp.toPx(),
                    pathEffect = dashEffect
                )
            }

            val slotCount = 24
            val slotWidth = chartWidth / slotCount
            val barWidth = (slotWidth * 0.62f).coerceAtLeast(3f)
            val corner = CornerRadius(3.dp.toPx(), 3.dp.toPx())

            for (h in 0 until slotCount) {
                val mins = hourlyMinutes.getOrElse(h) { 0 }.coerceIn(0, 60)
                val x = h * slotWidth + (slotWidth - barWidth) / 2f
                if (mins > 0) {
                    val barHeight = ((mins / 60f) * (chartHeight * 0.92f)).coerceAtLeast(4.dp.toPx())
                    val topY = chartHeight - barHeight
                    drawRoundRect(
                        color = if (h == peakHour) StoneAccent else StoneSuccess,
                        topLeft = Offset(x, topY),
                        size = Size(barWidth, barHeight),
                        cornerRadius = corner
                    )
                } else {
                    drawRoundRect(
                        color = StoneDivider.copy(alpha = 0.65f),
                        topLeft = Offset(x, chartHeight - 2.dp.toPx()),
                        size = Size(barWidth, 2.dp.toPx()),
                        cornerRadius = corner
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        val ticks = if (isBangla) {
            listOf("০০", "০৬", "১২", "১৮", "২৩")
        } else {
            listOf("00h", "06h", "12h", "18h", "23h")
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ticks.forEach { t ->
                Text(
                    text = t,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 10.sp,
                    color = StoneInkSecondary
                )
            }
        }
    }
}

@Composable
private fun WeeklyTrendBarLineGraph(
    points: List<WeeklyTrendPoint>,
    isBangla: Boolean,
    modifier: Modifier = Modifier
) {
    val maxHours = remember(points) {
        (points.maxOfOrNull { it.hours } ?: 1f).coerceAtLeast(4f)
    }

    Column(modifier = modifier) {
        // Hours labels row above bars
        Row(modifier = Modifier.fillMaxWidth()) {
            points.forEach { pt ->
                val txt = if (pt.hours > 0f) {
                    val raw = String.format(Locale.US, "%.1f", pt.hours)
                    if (isBangla) PrayerTimeCalculator.toBanglaDigits(raw) else "${raw}h"
                } else {
                    "-"
                }
                Text(
                    text = txt,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 10.sp,
                    fontWeight = if (pt.isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (pt.isSelected) StoneAccent else StoneInkSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(86.dp)
        ) {
            val w = size.width
            val h = size.height
            val count = points.size.coerceAtLeast(1)
            val slotW = w / count
            val barW = (slotW * 0.44f).coerceAtLeast(8.dp.toPx())
            val corner = CornerRadius(6.dp.toPx(), 6.dp.toPx())

            drawLine(
                color = StoneDivider,
                start = Offset(0f, h - 1.dp.toPx()),
                end = Offset(w, h - 1.dp.toPx()),
                strokeWidth = 1.dp.toPx()
            )

            val nodeOffsets = mutableListOf<Offset>()
            points.forEachIndexed { idx, pt ->
                val cx = idx * slotW + slotW / 2f
                val ratio = (pt.hours / maxHours).coerceIn(0f, 1f)
                val barH = if (pt.hours > 0f) (ratio * (h - 14.dp.toPx())).coerceAtLeast(6.dp.toPx()) else 3.dp.toPx()
                val topY = h - barH

                drawRoundRect(
                    color = when {
                        pt.isSelected -> StoneAccent
                        pt.isFuture -> StoneDivider.copy(alpha = 0.5f)
                        else -> StoneSuccess.copy(alpha = 0.65f)
                    },
                    topLeft = Offset(cx - barW / 2f, topY),
                    size = Size(barW, barH),
                    cornerRadius = corner
                )
                nodeOffsets += Offset(cx, topY)
            }

            if (nodeOffsets.size >= 2) {
                val path = Path().apply {
                    moveTo(nodeOffsets.first().x, nodeOffsets.first().y)
                    for (i in 1 until nodeOffsets.size) {
                        lineTo(nodeOffsets[i].x, nodeOffsets[i].y)
                    }
                }
                drawPath(
                    path = path,
                    color = StoneInkPrimary.copy(alpha = 0.55f),
                    style = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round)
                )
                nodeOffsets.forEachIndexed { idx, center ->
                    val pt = points[idx]
                    drawCircle(
                        color = if (pt.isSelected) StoneAccent else StoneInkPrimary,
                        radius = if (pt.isSelected) 4.dp.toPx() else 2.8.dp.toPx(),
                        center = center
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            points.forEach { pt ->
                val dayNum = if (isBangla) PrayerTimeCalculator.toBanglaDigits(pt.dayNumber.toString()) else pt.dayNumber.toString()
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = pt.weekdayShort,
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp,
                        fontWeight = if (pt.isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (pt.isSelected) StoneAccent else StoneInkSecondary
                    )
                    Text(
                        text = dayNum,
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp,
                        fontWeight = if (pt.isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (pt.isSelected) StoneInkPrimary else StoneInkSecondary
                    )
                }
            }
        }
    }
}
