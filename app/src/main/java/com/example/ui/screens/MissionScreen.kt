package com.example.ui.screens

import android.Manifest
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material.icons.outlined.AlarmOff
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.NotificationsOff
import androidx.compose.material.icons.outlined.Widgets
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import com.example.R
import com.example.data.calc.PrayerTimeCalculator
import com.example.data.model.MissionAlarmMode
import com.example.data.model.MissionBlockSlot
import com.example.data.model.MissionItem
import com.example.data.model.MissionPriority
import com.example.data.model.MissionTimeHelper
import com.example.data.model.PrepQuestionTag
import com.example.data.model.TrackerDefaults
import com.example.data.model.TrackerSubject
import com.example.ui.components.MissionTargetIcon
import com.example.ui.components.NeumorphicCard
import com.example.ui.components.NeumorphicPillButton
import com.example.ui.theme.BarlowCondensedFamily
import com.example.ui.theme.LocalAppStrings
import com.example.ui.theme.NeumorphicHighlight
import com.example.ui.theme.StoneAccent
import com.example.ui.theme.StoneAccentSoft
import com.example.ui.theme.StoneBackground
import com.example.ui.theme.StoneBackgroundBottom
import com.example.ui.theme.StoneBackgroundTop
import com.example.ui.theme.StoneDivider
import com.example.ui.theme.StoneInkPrimary
import com.example.ui.theme.StoneInkSecondary
import com.example.ui.theme.StonePanel
import com.example.ui.theme.StoneSuccess
import com.example.ui.theme.StoneSurface
import com.example.widget.MissionWidgetProvider
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private enum class MissionViewStage {
    DATE_LIST,
    DAY_24H
}

@Composable
fun MissionScreen(
    missions: List<MissionItem>,
    liveNotifEnabled: Boolean,
    trackerSubjects: List<TrackerSubject> = TrackerDefaults.defaultSubjects,
    onBack: () -> Unit,
    onSaveMission: (MissionItem, Boolean) -> Unit,
    onToggleMissionCompleted: (String) -> Unit,
    onToggleMissionTag: (String, PrepQuestionTag) -> Unit = { _, _ -> },
    onToggleMissionAlarm: (String) -> Unit = {},
    onDeleteMission: (String) -> Unit,
    onToggleLiveNotification: (Boolean) -> Unit,
    onCopyDayToNext7Days: (String) -> Unit,
    onSyncWidgetNow: () -> Unit
) {
    val s = LocalAppStrings.current
    val context = LocalContext.current

    // 1-second real-time ticker so the 4-hour block countdown updates smoothly until block end & auto-rolls over
    var nowMillis by remember { mutableStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1_000L)
            nowMillis = System.currentTimeMillis()
        }
    }

    val nowCal = remember(nowMillis) {
        Calendar.getInstance().apply { timeInMillis = nowMillis }
    }
    val todayKey = remember(nowCal) { MissionTimeHelper.todayDateKey(nowCal) }
    val activeBlockIdx = remember(nowCal) {
        MissionTimeHelper.blockIndexForHour(nowCal.get(Calendar.HOUR_OF_DAY))
    }
    val activeProgress = remember(nowCal) { MissionTimeHelper.progressInCurrentBlock(nowCal) }
    val activeRemaining = remember(nowCal) { MissionTimeHelper.remainingMillisInCurrentBlock(nowCal) }
    val combinedTodayDisplay = remember(todayKey, s.isBangla) {
        PrayerTimeCalculator.formatCombinedDate(calendar = nowCal, isBangla = s.isBangla)
    }

    var stage by remember { mutableStateOf(MissionViewStage.DATE_LIST) }
    var adzanStyleExpanded by remember { mutableStateOf(false) }
    var selectedDateKey by remember { mutableStateOf(todayKey) }
    var displayedMonthOffset by remember { mutableIntStateOf(0) }
    var statusBanner by remember { mutableStateOf<String?>(null) }

    // Mission editor dialog state
    var editingBlockIndex by remember { mutableStateOf<Int?>(null) }
    var editingExistingMission by remember { mutableStateOf<MissionItem?>(null) }

    BackHandler(enabled = stage == MissionViewStage.DAY_24H) {
        stage = MissionViewStage.DATE_LIST
    }

    val notifPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        onToggleLiveNotification(granted)
        statusBanner = if (granted) {
            if (s.isBangla) "লাইভ ৪ ঘণ্টার নোটিফিকেশন ও অ্যালার্ম চালু হয়েছে" else "Live 4-hour notifications & alarms enabled"
        } else {
            if (s.isBangla) "নোটিফিকেশন অনুমতি দেওয়া হয়নি" else "Notification permission denied"
        }
    }

    fun requestLiveNotifToggle(desired: Boolean) {
        if (!desired) {
            onToggleLiveNotification(false)
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (granted) {
                onToggleLiveNotification(true)
            } else {
                notifPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            onToggleLiveNotification(true)
        }
    }

    val alarmPermPromptLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> }

    fun ensureAlarmNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) {
                alarmPermPromptLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    fun triggerPinWidget() {
        onSyncWidgetNow()
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val provider = ComponentName(context, MissionWidgetProvider::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && appWidgetManager.isRequestPinAppWidgetSupported) {
            val pinnedCallbackIntent = Intent(context, MissionWidgetProvider::class.java).apply {
                action = MissionWidgetProvider.ACTION_REFRESH_WIDGET
            }
            val successCallback = PendingIntent.getBroadcast(
                context,
                9100,
                pinnedCallbackIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val requested = appWidgetManager.requestPinAppWidget(provider, null, successCallback)
            statusBanner = if (requested) {
                if (s.isBangla) "হোম স্ক্রিনে ৪ ঘণ্টার মিশন উইজেট পিন করার অনুরোধ পাঠানো হয়েছে!" else "Pin request sent! Confirm on your Home Screen to add the 4h Mission widget."
            } else {
                if (s.isBangla) "হোম স্ক্রিন চেপে ধরে 'Mission' উইজেট যোগ করুন।" else "Long-press your Home Screen -> Widgets -> Study Room Mission."
            }
        } else {
            statusBanner = if (s.isBangla) {
                "আপনার হোম স্ক্রিনে লং-প্রেস করে Widgets > Mission থেকে উইজেট যোগ করুন।"
            } else {
                "Long-press your Home Screen -> Widgets -> Mission to add the auto-updating 4h widget."
            }
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(StoneBackgroundTop, StoneBackground, StoneBackgroundBottom)
                )
            )
            .statusBarsPadding()
            .testTag("mission_screen")
    ) {
        val isWide = maxWidth >= 600.dp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.navigationBars),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar matching Home Page header aesthetic
            Row(
                modifier = Modifier
                    .widthIn(max = if (isWide) 860.dp else 560.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        if (stage == MissionViewStage.DAY_24H) {
                            stage = MissionViewStage.DATE_LIST
                        } else {
                            onBack()
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("mission_back_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = stringResource(R.string.cd_back),
                        tint = StoneInkPrimary
                    )
                }

                MissionTargetIcon(modifier = Modifier.size(30.dp))
                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = s.missionTitle,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = StoneInkPrimary
                    )
                    Text(
                        text = s.missionSubtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = StoneInkSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Segmented Dates / 24h Day pill toggle
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(18.dp))
                        .background(StonePanel)
                        .border(1.dp, StoneDivider, RoundedCornerShape(18.dp))
                        .padding(3.dp)
                ) {
                    StageTabPill(
                        label = if (s.isBangla) "তারিখ" else "Dates",
                        selected = stage == MissionViewStage.DATE_LIST,
                        tag = "mission_tab_dates",
                        onClick = { stage = MissionViewStage.DATE_LIST }
                    )
                    StageTabPill(
                        label = if (s.isBangla) "২৪ঘ" else "24h",
                        selected = stage == MissionViewStage.DAY_24H,
                        tag = "mission_tab_24h",
                        onClick = { stage = MissionViewStage.DAY_24H }
                    )
                }
            }

            if (statusBanner != null) {
                Surface(
                    color = StoneAccentSoft,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .widthIn(max = if (isWide) 860.dp else 560.dp)
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 4.dp)
                        .clickable { statusBanner = null }
                        .testTag("mission_status_banner")
                ) {
                    Text(
                        text = statusBanner.orEmpty(),
                        style = MaterialTheme.typography.bodySmall,
                        color = StoneInkPrimary,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                    )
                }
            }

            if (stage == MissionViewStage.DATE_LIST) {
                MissionDateHubView(
                    isWide = isWide,
                    todayKey = todayKey,
                    combinedTodayDisplay = combinedTodayDisplay,
                    activeBlockIdx = activeBlockIdx,
                    activeProgress = activeProgress,
                    activeRemaining = activeRemaining,
                    displayedMonthOffset = displayedMonthOffset,
                    missions = missions,
                    liveNotifEnabled = liveNotifEnabled,
                    adzanPopupExpanded = adzanStyleExpanded,
                    onToggleAdzanPopup = { adzanStyleExpanded = !adzanStyleExpanded },
                    onPrevMonth = { displayedMonthOffset -= 1 },
                    onNextMonth = { displayedMonthOffset += 1 },
                    onOpenDate24h = { dateKey ->
                        selectedDateKey = dateKey
                        stage = MissionViewStage.DAY_24H
                    },
                    onToggleMissionCompleted = onToggleMissionCompleted,
                    onToggleMissionAlarm = onToggleMissionAlarm,
                    onToggleLiveNotif = ::requestLiveNotifToggle,
                    onPinWidget = ::triggerPinWidget,
                    onQuickEditSlot = { slotIdx, existing ->
                        selectedDateKey = todayKey
                        editingBlockIndex = slotIdx
                        editingExistingMission = existing
                    }
                )
            } else {
                MissionDay24hView(
                    isWide = isWide,
                    selectedDateKey = selectedDateKey,
                    todayKey = todayKey,
                    activeBlockIdx = activeBlockIdx,
                    activeProgress = activeProgress,
                    activeRemaining = activeRemaining,
                    missions = missions,
                    trackerSubjects = trackerSubjects,
                    liveNotifEnabled = liveNotifEnabled,
                    onSelectDateKey = { selectedDateKey = it },
                    onBackToDates = { stage = MissionViewStage.DATE_LIST },
                    onOpenSlotEditor = { blockIdx, existing ->
                        editingBlockIndex = blockIdx
                        editingExistingMission = existing
                    },
                    onSaveInlineMission = { item ->
                        if (item.alarmEnabled) {
                            ensureAlarmNotificationPermissionIfNeeded()
                        }
                        onSaveMission(item, false)
                        statusBanner = if (s.isBangla) {
                            "মিশন সেট যোগ হয়েছে ও উইজেট/অ্যালার্ম সিঙ্ক হয়েছে।"
                        } else {
                            "Mission set added! Widget & 4h alerts synced."
                        }
                    },
                    onToggleMissionCompleted = onToggleMissionCompleted,
                    onToggleMissionTag = onToggleMissionTag,
                    onToggleMissionAlarm = onToggleMissionAlarm,
                    onToggleLiveNotif = ::requestLiveNotifToggle,
                    onPinWidget = ::triggerPinWidget,
                    onDeleteMission = onDeleteMission,
                    onCopyToNext7Days = {
                        onCopyDayToNext7Days(selectedDateKey)
                        statusBanner = if (s.isBangla) {
                            "এই দিনের ২৪ ঘণ্টার মিশন পরবর্তী ৭ দিনে কপি হয়েছে।"
                        } else {
                            "Copied this 24-hour mission plan & alarms to the next 7 days."
                        }
                    }
                )
            }
        }
    }

    val activeEditSlotIdx = editingBlockIndex
    if (activeEditSlotIdx != null) {
        MissionEditorDialog(
            dateKey = selectedDateKey,
            blockSlot = MissionTimeHelper.blockForIndex(activeEditSlotIdx),
            trackerSubjects = trackerSubjects,
            existing = editingExistingMission,
            onDismiss = {
                editingBlockIndex = null
                editingExistingMission = null
            },
            onSave = { item, repeat7Days ->
                if (item.alarmEnabled) {
                    ensureAlarmNotificationPermissionIfNeeded()
                }
                onSaveMission(item, repeat7Days)
                editingBlockIndex = null
                editingExistingMission = null
                statusBanner = if (s.isBangla) {
                    "মিশন সেট সংরক্ষিত হয়েছে ও রিয়েল-টাইম উইজেট/অ্যালার্ম সিঙ্ক হয়েছে।"
                } else {
                    "Mission set saved! Widget & 4h notification alerts updated."
                }
            }
        )
    }
}

@Composable
private fun StageTabPill(
    label: String,
    selected: Boolean,
    tag: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (selected) StoneInkPrimary else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .testTag(tag),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = if (selected) NeumorphicHighlight else StoneInkSecondary
        )
    }
}

/**
 * Home-page top timer box look (`HeroAdzanCard` style) + Adzan-style expandable 6 x 4-hour Mission popup.
 */
@Composable
private fun HeroMissionTimerCard(
    todayKey: String,
    combinedDateDisplay: String,
    activeBlockIdx: Int,
    activeProgress: Float,
    activeRemainingMillis: Long,
    missions: List<MissionItem>,
    liveNotifEnabled: Boolean,
    expanded: Boolean,
    onToggleExpand: () -> Unit,
    onToggleMissionCompleted: (String) -> Unit,
    onToggleMissionAlarm: (String) -> Unit,
    onToggleLiveNotif: (Boolean) -> Unit,
    onPinWidget: () -> Unit,
    onQuickEditSlot: (Int, MissionItem?) -> Unit,
    onOpen24hSchedule: () -> Unit,
    modifier: Modifier = Modifier
) {
    val s = LocalAppStrings.current
    val chevronRotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "mission_adzan_chevron"
    )

    val activeSlot = MissionTimeHelper.blockForIndex(activeBlockIdx)
    val nextBlockIdx = (activeBlockIdx + 1) % MissionTimeHelper.blocks.size
    val nextSlot = MissionTimeHelper.blockForIndex(nextBlockIdx)
    val todayMissions = remember(missions, todayKey) { missions.filter { it.dateKey == todayKey } }
    val activeSlotMissions = remember(todayMissions, activeBlockIdx) {
        todayMissions.filter { it.blockIndex == activeBlockIdx }
    }
    val nextSlotMissions = remember(todayMissions, nextBlockIdx) {
        todayMissions.filter { it.blockIndex == nextBlockIdx }
    }
    val primaryActiveMission = activeSlotMissions.firstOrNull { !it.isCompleted }
        ?: activeSlotMissions.firstOrNull()

    val hmsCountdown = MissionTimeHelper.formatHmsCountdown(activeRemainingMillis)
    val displayCountdown = if (s.isBangla) PrayerTimeCalculator.toBanglaDigits(hmsCountdown) else hmsCountdown
    val shortOffset = MissionTimeHelper.formatShortOffsetCountdown(activeRemainingMillis)
    val nextSlotStartStr = MissionTimeHelper.formatBlockStartShort(nextSlot)

    NeumorphicCard(
        modifier = modifier.testTag("mission_live_hero_card"),
        cornerRadius = 26.dp,
        shadowElevation = 9.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp, vertical = 18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = combinedDateDisplay,
                        style = MaterialTheme.typography.bodyMedium,
                        color = StoneInkPrimary,
                        fontSize = 13.5.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = displayCountdown,
                        fontFamily = BarlowCondensedFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 54.sp,
                        lineHeight = 56.sp,
                        color = StoneInkPrimary,
                        letterSpacing = (-0.5).sp,
                        modifier = Modifier.testTag("mission_hero_countdown")
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = if (s.isBangla) {
                            "${activeSlot.rangeFullBn} • ${activeSlot.phaseTitleBn}"
                        } else {
                            "${activeSlot.rangeFullEn} • ${activeSlot.phaseTitleEn}"
                        },
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = StoneInkPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .clickable(onClick = onToggleExpand)
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                            .testTag("mission_dropdown_button"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = s.missionTitle,
                                style = MaterialTheme.typography.titleMedium,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = StoneInkPrimary
                            )
                            Icon(
                                imageVector = Icons.Rounded.KeyboardArrowDown,
                                contentDescription = "Toggle 4-hour mission timetable",
                                tint = StoneInkPrimary,
                                modifier = Modifier
                                    .size(22.dp)
                                    .rotate(chevronRotation)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (s.isBangla) "পরবর্তী $nextSlotStartStr" else "Next $nextSlotStartStr",
                        style = MaterialTheme.typography.labelMedium,
                        color = StoneInkSecondary
                    )
                    Text(
                        text = shortOffset,
                        style = MaterialTheme.typography.labelSmall,
                        color = StoneAccent,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Smooth 4-hour progress bar
            LinearProgressIndicator(
                progress = { activeProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = StoneAccent,
                trackColor = StoneDivider
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Active Mission Quick Pill row inside Hero Box
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(StonePanel.copy(alpha = 0.72f))
                    .clickable(onClick = onOpen24hSchedule)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (primaryActiveMission != null) {
                    IconButton(
                        onClick = { onToggleMissionCompleted(primaryActiveMission.id) },
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            imageVector = if (primaryActiveMission.isCompleted) {
                                Icons.Rounded.CheckCircle
                            } else {
                                Icons.Rounded.RadioButtonUnchecked
                            },
                            contentDescription = "Complete active 4h mission",
                            tint = if (primaryActiveMission.isCompleted) StoneSuccess else StoneInkSecondary
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        val setCountLabel = if (activeSlotMissions.size > 1) {
                            " (+${activeSlotMissions.size - 1} sets)"
                        } else ""
                        Text(
                            text = "${primaryActiveMission.title}$setCountLabel",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = StoneInkPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textDecoration = if (primaryActiveMission.isCompleted) TextDecoration.LineThrough else null
                        )
                        val academic = MissionTimeHelper.formatAcademicSummary(primaryActiveMission, s.isBangla)
                        if (academic.isNotBlank()) {
                            Text(
                                text = academic,
                                style = MaterialTheme.typography.labelSmall,
                                color = StoneAccent,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                } else {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (s.isBangla) {
                                "${activeSlot.rangeShortEn} — কোনো মিশন সেট নেই"
                            } else {
                                "No set for ${activeSlot.rangeShortEn}"
                            },
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = StoneInkPrimary
                        )
                        Text(
                            text = if (s.isBangla) "ট্যাপ করে বিষয় ও লক্ষ্য যোগ করুন" else "Tap to add Subject, Chapter & ক/খ/গ/ঘ/MCQ/CQ",
                            style = MaterialTheme.typography.labelSmall,
                            color = StoneInkSecondary
                        )
                    }
                }
                TextButton(
                    onClick = { onQuickEditSlot(activeBlockIdx, null) },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (s.isBangla) "+ সেট" else "+ Add Set",
                        color = StoneAccent,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Next 4h preview
            val nextPreviewMission = nextSlotMissions.firstOrNull()
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = if (nextPreviewMission != null) {
                    if (s.isBangla) {
                        "পরবর্তী (${nextSlot.rangeShortEn}): ${nextPreviewMission.title}"
                    } else {
                        "Next (${nextSlot.rangeShortEn}): ${nextPreviewMission.title}"
                    }
                } else {
                    if (s.isBangla) {
                        "পরবর্তী (${nextSlot.rangeShortEn}): খালি স্লট • সময় শেষে অটো পরিবর্তন"
                    } else {
                        "Next (${nextSlot.rangeShortEn}): Open slot • auto-switches when timer ends"
                    }
                },
                style = MaterialTheme.typography.labelSmall,
                color = StoneInkSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Adzan-style Expanded 6 x 4h Mission Popup Drawer
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("mission_adzan_popup_content")
                ) {
                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = StoneDivider, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Top bar inside Adzan-style popup: Live 4h Alerts Switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.NotificationsActive,
                                contentDescription = null,
                                tint = StoneAccent,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = s.missionLiveNotifLabel,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = StoneInkPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = s.reminders,
                                style = MaterialTheme.typography.labelSmall,
                                color = StoneInkSecondary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Switch(
                                checked = liveNotifEnabled,
                                onCheckedChange = onToggleLiveNotif,
                                modifier = Modifier.testTag("mission_live_notif_switch"),
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = NeumorphicHighlight,
                                    checkedTrackColor = StoneAccent,
                                    uncheckedThumbColor = StoneInkSecondary,
                                    uncheckedTrackColor = StoneBackground
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // 6 rows matching Adzan prayer timetable rows (5 AM -> 9 AM .. 1 AM -> 5 AM)
                    MissionTimeHelper.blocks.forEach { slot ->
                        val slotMissions = todayMissions.filter { it.blockIndex == slot.index }
                        val firstMission = slotMissions.firstOrNull()
                        val allDone = slotMissions.isNotEmpty() && slotMissions.all { it.isCompleted }
                        val isAlertOn = firstMission?.alarmEnabled == true
                        val isCurrent = slot.index == activeBlockIdx

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isCurrent) StoneAccent.copy(alpha = 0.14f) else Color.Transparent)
                                .clickable { onQuickEditSlot(slot.index, firstMission) }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                                .testTag("mission_adzan_row_${slot.index}"),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = {
                                    if (firstMission != null) {
                                        onToggleMissionCompleted(firstMission.id)
                                    } else {
                                        onQuickEditSlot(slot.index, null)
                                    }
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = when {
                                        allDone -> Icons.Rounded.CheckCircle
                                        slotMissions.isNotEmpty() -> Icons.Rounded.RadioButtonUnchecked
                                        else -> Icons.Rounded.Add
                                    },
                                    contentDescription = "Toggle or add for ${slot.rangeShortEn}",
                                    tint = when {
                                        allDone -> StoneSuccess
                                        isCurrent -> StoneAccent
                                        else -> StoneInkSecondary
                                    },
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = slot.rangeShortEn,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.SemiBold,
                                    color = StoneInkPrimary
                                )
                                val detailText = when {
                                    slotMissions.isEmpty() ->
                                        if (s.isBangla) "খালি স্লট • ট্যাপ করে সেট যোগ করুন" else "Open • Tap to add subject set"
                                    slotMissions.size == 1 -> {
                                        val m = slotMissions.first()
                                        val acad = MissionTimeHelper.formatAcademicSummary(m, s.isBangla)
                                        if (acad.isNotBlank()) "${m.title} • $acad" else m.title
                                    }
                                    else -> "${slotMissions.size} sets • ${slotMissions.joinToString(", ") { it.subjectName.ifBlank { it.title } }}"
                                }
                                Text(
                                    text = detailText,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isCurrent) StoneAccent else StoneInkSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Text(
                                text = MissionTimeHelper.formatBlockStartShort(slot),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isCurrent) StoneAccent else StoneInkPrimary
                            )

                            Spacer(modifier = Modifier.width(6.dp))

                            IconButton(
                                onClick = {
                                    if (firstMission != null) {
                                        onToggleMissionAlarm(firstMission.id)
                                    } else {
                                        onQuickEditSlot(slot.index, null)
                                    }
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = if (isAlertOn) {
                                        Icons.Outlined.Notifications
                                    } else {
                                        Icons.Outlined.NotificationsOff
                                    },
                                    contentDescription = "Toggle alarm for ${slot.rangeShortEn}",
                                    tint = if (isAlertOn) StoneAccent else StoneInkSecondary.copy(alpha = 0.5f),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        NeumorphicPillButton(
                            text = s.missionPinWidgetAction,
                            onClick = onPinWidget,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("mission_pin_widget_btn")
                        )
                        NeumorphicPillButton(
                            text = if (s.isBangla) "২৪ঘ রুটিন" else "24h Schedule",
                            onClick = onOpen24hSchedule,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MissionDateHubView(
    isWide: Boolean,
    todayKey: String,
    combinedTodayDisplay: String,
    activeBlockIdx: Int,
    activeProgress: Float,
    activeRemaining: Long,
    displayedMonthOffset: Int,
    missions: List<MissionItem>,
    liveNotifEnabled: Boolean,
    adzanPopupExpanded: Boolean,
    onToggleAdzanPopup: () -> Unit,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onOpenDate24h: (String) -> Unit,
    onToggleMissionCompleted: (String) -> Unit,
    onToggleMissionAlarm: (String) -> Unit,
    onToggleLiveNotif: (Boolean) -> Unit,
    onPinWidget: () -> Unit,
    onQuickEditSlot: (Int, MissionItem?) -> Unit
) {
    val s = LocalAppStrings.current
    val quickDateKeys = remember(todayKey) {
        MissionTimeHelper.generateDateStripKeys(centerDateKey = todayKey, daysBefore = 1, daysAfter = 10)
    }

    LazyColumn(
        modifier = Modifier
            .widthIn(max = if (isWide) 860.dp else 560.dp)
            .fillMaxSize()
            .testTag("mission_date_hub_list"),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Home-page style Top Timer Box + Adzan-style Expandable Mission Popup
        item(key = "hero_timer_card") {
            HeroMissionTimerCard(
                todayKey = todayKey,
                combinedDateDisplay = combinedTodayDisplay,
                activeBlockIdx = activeBlockIdx,
                activeProgress = activeProgress,
                activeRemainingMillis = activeRemaining,
                missions = missions,
                liveNotifEnabled = liveNotifEnabled,
                expanded = adzanPopupExpanded,
                onToggleExpand = onToggleAdzanPopup,
                onToggleMissionCompleted = onToggleMissionCompleted,
                onToggleMissionAlarm = onToggleMissionAlarm,
                onToggleLiveNotif = onToggleLiveNotif,
                onPinWidget = onPinWidget,
                onQuickEditSlot = onQuickEditSlot,
                onOpen24hSchedule = { onOpenDate24h(todayKey) }
            )
        }

        // Quick Widget + Notification Shortcut Pill Strip when Adzan popup is collapsed
        if (!adzanPopupExpanded) {
            item(key = "widget_notif_bar") {
                Surface(
                    color = StoneSurface,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, StoneDivider, RoundedCornerShape(16.dp))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.NotificationsActive,
                                contentDescription = null,
                                tint = if (liveNotifEnabled) StoneSuccess else StoneAccent,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = s.missionLiveNotifLabel,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = StoneInkPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Switch(
                                checked = liveNotifEnabled,
                                onCheckedChange = onToggleLiveNotif,
                                modifier = Modifier.testTag("mission_live_notif_switch"),
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = NeumorphicHighlight,
                                    checkedTrackColor = StoneAccent
                                )
                            )
                        }

                        OutlinedButton(
                            onClick = onPinWidget,
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .height(38.dp)
                                .testTag("mission_pin_widget_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Widgets,
                                contentDescription = null,
                                tint = StoneInkPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = s.missionPinWidgetAction,
                                style = MaterialTheme.typography.labelSmall,
                                color = StoneInkPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Section Header: Calendar Date Selection
        item(key = "calendar_header") {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = s.missionSelectDateTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = StoneInkPrimary
                )
                Text(
                    text = if (s.isBangla) {
                        "যেকোনো তারিখে ট্যাপ করলে ৫টা–৯টা, ৯টা–১টা অনুযায়ী বিষয়/অধ্যায় ও ক,খ,গ,ঘ/MCQ/CQ মিশন খুলবে"
                    } else {
                        "Tap any date to open its 24h 4h-block timeline with Subject/Chapter & ক,খ,গ,ঘ/MCQ/CQ sets"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = StoneInkSecondary
                )
            }
        }

        // Interactive Monthly Calendar Grid
        item(key = "calendar_grid") {
            MonthlyMissionCalendarCard(
                todayKey = todayKey,
                monthOffset = displayedMonthOffset,
                missions = missions,
                isBangla = s.isBangla,
                onPrevMonth = onPrevMonth,
                onNextMonth = onNextMonth,
                onSelectDate = onOpenDate24h
            )
        }

        // Upcoming Dates Summary Cards
        item(key = "upcoming_header") {
            Text(
                text = if (s.isBangla) "আসন্ন দিনসমূহের সারসংক্ষেপ" else "Upcoming Dates & 4h Coverage",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = StoneInkPrimary
            )
        }

        items(quickDateKeys, key = { it }) { dateKey ->
            val dayMissions = missions.filter { it.dateKey == dateKey }
            val completedCount = dayMissions.count { it.isCompleted }
            val alarmCount = dayMissions.count { it.alarmEnabled && !it.isCompleted }
            val filledBlocks = dayMissions.map { it.blockIndex }.toSet().size
            val isToday = dateKey == todayKey
            val header = MissionTimeHelper.formatDateFullHeader(dateKey, s.isBangla)

            Surface(
                color = if (isToday) Color(0xFFE7DFD5) else StoneSurface,
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = if (isToday) 1.5.dp else 1.dp,
                        color = if (isToday) StoneAccent else StoneDivider,
                        shape = RoundedCornerShape(18.dp)
                    )
                    .clip(RoundedCornerShape(18.dp))
                    .clickable { onOpenDate24h(dateKey) }
                    .testTag("mission_date_card_$dateKey")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = header,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = StoneInkPrimary
                                )
                                if (isToday) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(StoneAccent)
                                            .padding(horizontal = 7.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = if (s.isBangla) "আজ" else "TODAY",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = NeumorphicHighlight,
                                            fontSize = 10.sp
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = if (dayMissions.isEmpty()) {
                                    if (s.isBangla) "ট্যাপ করে ২৪ ঘণ্টার মিশন সেট করুন" else "Open day -> Tap to set 4h subject/chapter missions"
                                } else {
                                    if (s.isBangla) {
                                        "$filledBlocks/৬ স্লট (${dayMissions.size} সেট) • $completedCount সম্পন্ন • $alarmCount অ্যালার্ম"
                                    } else {
                                        "$filledBlocks/6 blocks (${dayMissions.size} sets) • $completedCount done • $alarmCount alarms"
                                    }
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = StoneInkSecondary
                            )
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                            contentDescription = "Open 24h view",
                            tint = StoneInkPrimary
                        )
                    }

                    // 6 mini indicator bars representing the 6 × 4-hour blocks (5 AM -> 5 AM)
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        MissionTimeHelper.blocks.forEach { slot ->
                            val slotMissions = dayMissions.filter { it.blockIndex == slot.index }
                            val isCurrentLiveSlot = isToday && slot.index == activeBlockIdx
                            val barColor = when {
                                slotMissions.isNotEmpty() && slotMissions.all { it.isCompleted } -> StoneSuccess
                                slotMissions.isNotEmpty() -> StoneAccent
                                isCurrentLiveSlot -> Color(0xFFC27D38)
                                else -> StoneDivider.copy(alpha = 0.7f)
                            }
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(barColor)
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = "${slot.startHour24}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 9.sp,
                                    color = if (isCurrentLiveSlot) StoneAccent else StoneInkSecondary
                                )
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
private fun MonthlyMissionCalendarCard(
    todayKey: String,
    monthOffset: Int,
    missions: List<MissionItem>,
    isBangla: Boolean,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onSelectDate: (String) -> Unit
) {
    val cal = remember(monthOffset) {
        Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
            add(Calendar.MONTH, monthOffset)
        }
    }
    val locale = if (isBangla) Locale.forLanguageTag("bn-BD") else Locale.US
    val monthTitle = remember(cal, isBangla) {
        SimpleDateFormat("MMMM yyyy", locale).format(cal.time)
    }
    val year = cal.get(Calendar.YEAR)
    val month = cal.get(Calendar.MONTH)
    val firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY
    val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

    val missionsByDate = remember(missions) { missions.groupBy { it.dateKey } }

    Surface(
        color = StoneSurface,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, StoneDivider, RoundedCornerShape(20.dp))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onPrevMonth,
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("mission_cal_prev_month")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowLeft,
                        contentDescription = "Previous month",
                        tint = StoneInkPrimary
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.CalendarMonth,
                        contentDescription = null,
                        tint = StoneAccent,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = monthTitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = StoneInkPrimary
                    )
                }

                IconButton(
                    onClick = onNextMonth,
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("mission_cal_next_month")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                        contentDescription = "Next month",
                        tint = StoneInkPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            val weekLabels = if (isBangla) {
                listOf("রবি", "সোম", "মঙ্গল", "বুধ", "বৃহঃ", "শুক্র", "শনি")
            } else {
                listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                weekLabels.forEach { wd ->
                    Text(
                        text = wd,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = StoneInkSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            val totalCells = firstDayOfWeek + daysInMonth
            val rows = (totalCells + 6) / 7
            for (row in 0 until rows) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    for (col in 0 until 7) {
                        val cellIdx = row * 7 + col
                        val dayNum = cellIdx - firstDayOfWeek + 1
                        if (dayNum in 1..daysInMonth) {
                            val dateKey = String.format(
                                Locale.US,
                                "%04d-%02d-%02d",
                                year,
                                month + 1,
                                dayNum
                            )
                            val dayMissions = missionsByDate[dateKey].orEmpty()
                            val isToday = dateKey == todayKey
                            val hasMissions = dayMissions.isNotEmpty()
                            val allDone = hasMissions && dayMissions.all { it.isCompleted }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .padding(vertical = 2.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        when {
                                            isToday -> StoneAccent
                                            hasMissions -> StoneAccentSoft
                                            else -> StonePanel.copy(alpha = 0.65f)
                                        }
                                    )
                                    .clickable { onSelectDate(dateKey) }
                                    .testTag("mission_cal_cell_$dateKey"),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = dayNum.toString(),
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = if (isToday || hasMissions) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isToday) NeumorphicHighlight else StoneInkPrimary
                                    )
                                    if (hasMissions) {
                                        Box(
                                            modifier = Modifier
                                                .size(5.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    when {
                                                        isToday -> NeumorphicHighlight
                                                        allDone -> StoneSuccess
                                                        else -> StoneAccent
                                                    }
                                                )
                                        )
                                    }
                                }
                            }
                        } else {
                            Spacer(modifier = Modifier.weight(1f).height(44.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MissionDay24hView(
    isWide: Boolean,
    selectedDateKey: String,
    todayKey: String,
    activeBlockIdx: Int,
    activeProgress: Float,
    activeRemaining: Long,
    missions: List<MissionItem>,
    trackerSubjects: List<TrackerSubject>,
    liveNotifEnabled: Boolean,
    onSelectDateKey: (String) -> Unit,
    onBackToDates: () -> Unit,
    onOpenSlotEditor: (Int, MissionItem?) -> Unit,
    onSaveInlineMission: (MissionItem) -> Unit,
    onToggleMissionCompleted: (String) -> Unit,
    onToggleMissionTag: (String, PrepQuestionTag) -> Unit,
    onToggleMissionAlarm: (String) -> Unit,
    onToggleLiveNotif: (Boolean) -> Unit,
    onPinWidget: () -> Unit,
    onDeleteMission: (String) -> Unit,
    onCopyToNext7Days: () -> Unit
) {
    val s = LocalAppStrings.current
    val stripKeys = remember(selectedDateKey) {
        MissionTimeHelper.generateDateStripKeys(centerDateKey = selectedDateKey, daysBefore = 2, daysAfter = 7)
    }
    val dayMissions = remember(missions, selectedDateKey) {
        missions.filter { it.dateKey == selectedDateKey }
    }
    val isSelectedToday = selectedDateKey == todayKey
    val fullDateTitle = MissionTimeHelper.formatDateFullHeader(selectedDateKey, s.isBangla)
    val totalPlanned = dayMissions.size
    val totalCompleted = dayMissions.count { it.isCompleted }

    var expandedBlockIndices by remember(selectedDateKey) {
        mutableStateOf(emptySet<Int>())
    }

    LazyColumn(
        modifier = Modifier
            .widthIn(max = if (isWide) 860.dp else 560.dp)
            .fillMaxSize()
            .testTag("mission_24h_list"),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Compact Date Strip + Selected Date & Widget/Live Alert Controls
        item(key = "date_strip_header") {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    stripKeys.forEach { dKey ->
                        val (wd, dayNum, mon) = MissionTimeHelper.formatDateChipParts(dKey, s.isBangla)
                        val isSelected = dKey == selectedDateKey
                        val countForDay = missions.count { it.dateKey == dKey }
                        Surface(
                            color = if (isSelected) StoneInkPrimary else StoneSurface,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .border(
                                    1.dp,
                                    if (isSelected) StoneInkPrimary else StoneDivider,
                                    RoundedCornerShape(14.dp)
                                )
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { onSelectDateKey(dKey) }
                                .testTag("mission_strip_date_$dKey")
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = wd,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isSelected) Color(0xFFD9C7B2) else StoneInkSecondary
                                )
                                Text(
                                    text = dayNum,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) NeumorphicHighlight else StoneInkPrimary
                                )
                                Text(
                                    text = if (countForDay > 0) "$countForDay★" else mon,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 10.sp,
                                    color = if (isSelected) Color(0xFFE6C99E) else StoneAccent
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Surface(
                    color = StoneSurface,
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, StoneDivider, RoundedCornerShape(18.dp))
                ) {
                    Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = fullDateTitle,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = StoneInkPrimary,
                                    modifier = Modifier.testTag("mission_selected_date_header")
                                )
                                Text(
                                    text = if (s.isBangla) {
                                        "${s.mission24hTimelineTitle} • $totalCompleted/$totalPlanned সেট সম্পন্ন"
                                    } else {
                                        "${s.mission24hTimelineTitle} • $totalCompleted/$totalPlanned sets completed"
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = StoneInkSecondary
                                )
                            }

                            TextButton(
                                onClick = onBackToDates,
                                modifier = Modifier.testTag("mission_all_dates_btn")
                            ) {
                                Text(
                                    text = if (s.isBangla) "সব তারিখ" else "All Dates",
                                    fontWeight = FontWeight.Bold,
                                    color = StoneAccent
                                )
                            }
                        }

                        HorizontalDivider(color = StoneDivider.copy(alpha = 0.7f), thickness = 1.dp)
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.NotificationsActive,
                                    contentDescription = null,
                                    tint = if (liveNotifEnabled) StoneSuccess else StoneAccent,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = s.missionLiveNotifLabel,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = StoneInkPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Switch(
                                    checked = liveNotifEnabled,
                                    onCheckedChange = onToggleLiveNotif,
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = NeumorphicHighlight,
                                        checkedTrackColor = StoneAccent
                                    )
                                )
                            }

                            OutlinedButton(
                                onClick = onPinWidget,
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Widgets,
                                    contentDescription = null,
                                    tint = StoneInkPrimary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = s.missionPinWidgetAction,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = StoneInkPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Exact 6 × 4-Hour Cards — EVERY card looks like Home Top Timer Box & opens Adzan-style down popup when clicked!
        items(MissionTimeHelper.blocks, key = { it.index }) { slot ->
            val slotMissions = dayMissions.filter { it.blockIndex == slot.index }
            val isLiveSlot = isSelectedToday && slot.index == activeBlockIdx
            val isExpanded = slot.index in expandedBlockIndices

            Mission4HourSlotCard(
                dateKey = selectedDateKey,
                slot = slot,
                slotMissions = slotMissions,
                trackerSubjects = trackerSubjects,
                isLiveSlot = isLiveSlot,
                liveProgress = if (isLiveSlot) activeProgress else 0f,
                liveRemainingMillis = activeRemaining,
                expanded = isExpanded,
                onToggleExpanded = {
                    expandedBlockIndices = if (isExpanded) {
                        expandedBlockIndices - slot.index
                    } else {
                        expandedBlockIndices + slot.index
                    }
                },
                onAddMissionDialog = { onOpenSlotEditor(slot.index, null) },
                onSaveInlineMission = onSaveInlineMission,
                onEditMission = { existing -> onOpenSlotEditor(slot.index, existing) },
                onToggleCompleted = onToggleMissionCompleted,
                onTogglePrepTag = onToggleMissionTag,
                onToggleAlarm = onToggleMissionAlarm,
                onDeleteMission = onDeleteMission
            )
        }

        // Copy Day to Next 7 Days utility
        item(key = "copy_week_btn") {
            OutlinedButton(
                onClick = onCopyToNext7Days,
                enabled = dayMissions.isNotEmpty(),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("mission_copy_week_btn")
            ) {
                Icon(
                    imageVector = Icons.Rounded.Schedule,
                    contentDescription = null,
                    tint = if (dayMissions.isNotEmpty()) StoneInkPrimary else StoneInkSecondary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = s.missionRepeatWeekLabel,
                    fontWeight = FontWeight.Bold,
                    color = if (dayMissions.isNotEmpty()) StoneInkPrimary else StoneInkSecondary
                )
            }
            Spacer(modifier = Modifier.height(18.dp))
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Mission4HourSlotCard(
    dateKey: String,
    slot: MissionBlockSlot,
    slotMissions: List<MissionItem>,
    trackerSubjects: List<TrackerSubject>,
    isLiveSlot: Boolean,
    liveProgress: Float,
    liveRemainingMillis: Long,
    expanded: Boolean,
    onToggleExpanded: () -> Unit,
    onAddMissionDialog: () -> Unit,
    onSaveInlineMission: (MissionItem) -> Unit,
    onEditMission: (MissionItem) -> Unit,
    onToggleCompleted: (String) -> Unit,
    onTogglePrepTag: (String, PrepQuestionTag) -> Unit,
    onToggleAlarm: (String) -> Unit,
    onDeleteMission: (String) -> Unit
) {
    val s = LocalAppStrings.current
    val chevronRotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "slot_adzan_chevron_${slot.index}"
    )

    val rangeTitle = if (s.isBangla) slot.rangeFullBn else slot.rangeFullEn
    val phaseTitle = if (s.isBangla) slot.phaseTitleBn else slot.phaseTitleEn
    val nextSlot = MissionTimeHelper.blockForIndex((slot.index + 1) % MissionTimeHelper.blocks.size)
    val nextStartStr = MissionTimeHelper.formatBlockStartShort(nextSlot)

    val primaryMission = slotMissions.firstOrNull { !it.isCompleted } ?: slotMissions.firstOrNull()
    val doneCount = slotMissions.count { it.isCompleted }
    val allDone = slotMissions.isNotEmpty() && doneCount == slotMissions.size

    val rawClockStr = if (isLiveSlot) {
        MissionTimeHelper.formatHmsCountdown(liveRemainingMillis)
    } else {
        "04:00:00"
    }
    val displayClockStr = if (s.isBangla) PrayerTimeCalculator.toBanglaDigits(rawClockStr) else rawClockStr
    val displayProgress = when {
        isLiveSlot -> liveProgress
        slotMissions.isNotEmpty() -> doneCount.toFloat() / slotMissions.size.toFloat()
        else -> 0f
    }

    // Inline Down Builder states (Click-to-Select Subject, Paper, Chapter, ক-খ-গ-ঘ/CQ/MCQ, Alarm)
    var inlineSubjectId by remember(dateKey, slot.index) { mutableStateOf("") }
    var inlinePaper by remember(dateKey, slot.index) { mutableIntStateOf(1) }
    var inlineChapterId by remember(dateKey, slot.index) { mutableStateOf("") }
    var inlineChapterTitle by remember(dateKey, slot.index) { mutableStateOf("") }
    var inlineTitleInput by remember(dateKey, slot.index) { mutableStateOf("") }
    var inlineTags by remember(dateKey, slot.index) {
        mutableStateOf(setOf(PrepQuestionTag.KA, PrepQuestionTag.KHA, PrepQuestionTag.MCQ))
    }
    var inlineAlarmEnabled by remember(dateKey, slot.index) { mutableStateOf(true) }
    var inlineAlarmMode by remember(dateKey, slot.index) {
        mutableStateOf(MissionAlarmMode.AT_BLOCK_START)
    }

    val activeSubject = remember(inlineSubjectId, trackerSubjects) {
        trackerSubjects.firstOrNull { it.id == inlineSubjectId }
    }
    val inlineChapters = remember(activeSubject, inlinePaper) {
        activeSubject?.chaptersForPaper(inlinePaper).orEmpty()
    }

    NeumorphicCard(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (isLiveSlot) {
                    Modifier.border(1.6.dp, StoneAccent.copy(alpha = 0.7f), RoundedCornerShape(26.dp))
                } else {
                    Modifier
                }
            )
            .testTag("mission_slot_card_${slot.index}"),
        cornerRadius = 26.dp,
        shadowElevation = if (isLiveSlot) 9.dp else 6.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // Top Timer Box Header (matches Home HeroAdzanCard look — tap anywhere to expand Adzan-style popup)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onToggleExpanded),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(9.dp)
                                .clip(CircleShape)
                                .background(Color(slot.accentColorHex))
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = rangeTitle,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = StoneInkPrimary,
                            fontSize = 13.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (isLiveSlot) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(StoneAccent)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                    .testTag("mission_active_badge_${slot.index}")
                            ) {
                                Text(
                                    text = s.missionActiveNowBadge,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeumorphicHighlight
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = displayClockStr,
                        fontFamily = BarlowCondensedFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 56.sp,
                        lineHeight = 58.sp,
                        color = if (allDone) StoneSuccess else StoneInkPrimary,
                        letterSpacing = (-0.5).sp,
                        modifier = Modifier.testTag("mission_slot_clock_${slot.index}")
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = phaseTitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        color = StoneInkPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .clickable(onClick = onToggleExpanded)
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                            .testTag("mission_slot_dropdown_${slot.index}"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = s.missionTitle,
                                style = MaterialTheme.typography.titleMedium,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = StoneInkPrimary
                            )
                            Icon(
                                imageVector = Icons.Rounded.KeyboardArrowDown,
                                contentDescription = "Open ${slot.rangeShortEn} Adzan popup",
                                tint = StoneInkPrimary,
                                modifier = Modifier
                                    .size(22.dp)
                                    .rotate(chevronRotation)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (isLiveSlot) {
                            if (s.isBangla) "পরবর্তী $nextStartStr" else "Next $nextStartStr"
                        } else {
                            MissionTimeHelper.formatBlockStartShort(slot)
                        },
                        style = MaterialTheme.typography.labelMedium,
                        color = StoneInkSecondary
                    )
                    Text(
                        text = when {
                            isLiveSlot -> MissionTimeHelper.formatShortOffsetCountdown(liveRemainingMillis)
                            slotMissions.isNotEmpty() -> "$doneCount/${slotMissions.size} sets"
                            else -> if (s.isBangla) "ট্যাপ করে সেট" else "Tap to add"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = if (allDone) StoneSuccess else StoneAccent,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            LinearProgressIndicator(
                progress = { displayProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (allDone) StoneSuccess else StoneAccent,
                trackColor = StoneDivider
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Quick Bottom Strip inside every 4h timer box (tap summary to open popup; tap + button for full custom dialog)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(StonePanel.copy(alpha = 0.72f))
                    .clickable(onClick = onToggleExpanded)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    if (primaryMission != null) {
                        val extra = if (slotMissions.size > 1) " (+${slotMissions.size - 1})" else ""
                        Text(
                            text = "${primaryMission.title}$extra",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = StoneInkPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textDecoration = if (allDone) TextDecoration.LineThrough else null
                        )
                        val academic = MissionTimeHelper.formatAcademicSummary(primaryMission, s.isBangla)
                        Text(
                            text = academic.ifBlank {
                                if (s.isBangla) "ট্যাপ করে ক/খ/গ/ঘ ও আরও সেট দেখুন" else "Tap box to open Adzan menu & options"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = StoneAccent,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    } else {
                        Text(
                            text = if (s.isBangla) "কোনো সেট নেই • ট্যাপ করে যোগ করুন" else "Open 4h block • Tap to open menu",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = StoneInkPrimary
                        )
                        Text(
                            text = if (s.isBangla) "বিষয়, অধ্যায়, ক-খ-গ-ঘ / MCQ / CQ নির্বাচন করুন" else "Select Subject, Chapter & ক খ গ ঘ CQ MCQ",
                            style = MaterialTheme.typography.labelSmall,
                            color = StoneInkSecondary
                        )
                    }
                }

                TextButton(
                    onClick = onAddMissionDialog,
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.testTag(
                        if (slotMissions.isEmpty()) "mission_set_slot_${slot.index}" else "mission_add_more_set_${slot.index}"
                    )
                ) {
                    Text(
                        text = if (slotMissions.isEmpty()) {
                            if (s.isBangla) "+ সেট" else "+ Set"
                        } else {
                            if (s.isBangla) "+ আরও" else "+ More"
                        },
                        color = StoneAccent,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Expandable Adzan-style Down Popup Drawer — SAME-TO-SAME Fajr / Dhuhr / Asr / Maghrib / Isha checklist look!
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("mission_slot_popup_${slot.index}")
                ) {
                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = StoneDivider, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Adzan-style Header Row (matches Home Adzan Location + Reminders row)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Schedule,
                                contentDescription = null,
                                tint = StoneAccent,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (s.isBangla) {
                                    "${slot.rangeShortEn} • মিশন তালিকা"
                                } else {
                                    "${slot.rangeShortEn} • Mission List"
                                },
                                style = MaterialTheme.typography.labelMedium,
                                color = StoneInkPrimary,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Text(
                            text = if (slotMissions.isEmpty()) {
                                if (s.isBangla) "কোনো সেট নেই" else "No sets yet"
                            } else {
                                "$doneCount/${slotMissions.size} ${if (s.isBangla) "সম্পন্ন" else "done"}"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = if (allDone) StoneSuccess else StoneAccent,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Section A: Clean Fajr / Dhuhr / Asr / Maghrib / Isha style To-Do Rows (zero boxed cards!)
                    if (slotMissions.isEmpty()) {
                        Text(
                            text = if (s.isBangla) {
                                "নিচ থেকে বিষয়, অধ্যায় ও ক/খ/গ/ঘ সিলেক্ট করে যোগ করুন।"
                            } else {
                                "Select options below to add your first mission set to this 4h block."
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = StoneInkSecondary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                        )
                    } else {
                        slotMissions.forEach { mission ->
                            val isNextHighlight = !mission.isCompleted && mission == primaryMission
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isNextHighlight) StoneAccent.copy(alpha = 0.12f) else Color.Transparent
                                    )
                                    .clickable { onEditMission(mission) }
                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                                    .testTag("mission_item_${mission.id}")
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Check Circle — exact 32.dp Fajr/Dhuhr done button
                                    IconButton(
                                        onClick = { onToggleCompleted(mission.id) },
                                        modifier = Modifier
                                            .size(32.dp)
                                            .testTag("mission_toggle_done_${mission.id}")
                                    ) {
                                        Icon(
                                            imageVector = if (mission.isCompleted) {
                                                Icons.Rounded.CheckCircle
                                            } else {
                                                Icons.Rounded.RadioButtonUnchecked
                                            },
                                            contentDescription = "Complete ${mission.title}",
                                            tint = if (mission.isCompleted) StoneSuccess else StoneInkSecondary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(6.dp))

                                    // Middle Title + Subject/Chapter subline
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = mission.title,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = if (isNextHighlight) FontWeight.Bold else FontWeight.SemiBold,
                                            color = StoneInkPrimary,
                                            textDecoration = if (mission.isCompleted) {
                                                TextDecoration.LineThrough
                                            } else null,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        if (mission.subjectName.isNotBlank() || mission.chapterTitle.isNotBlank()) {
                                            val paperStr = when (mission.paper) {
                                                1 -> if (s.isBangla) "১ম পত্র" else "1st Paper"
                                                2 -> if (s.isBangla) "২য় পত্র" else "2nd Paper"
                                                else -> ""
                                            }
                                            val subjPart = if (mission.subjectName.isNotBlank() && paperStr.isNotEmpty()) {
                                                "${mission.subjectName} · $paperStr"
                                            } else {
                                                mission.subjectName
                                            }
                                            val metaLine = listOf(subjPart, mission.chapterTitle)
                                                .filter { it.isNotBlank() }
                                                .joinToString(" • ")
                                            Text(
                                                text = metaLine,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = StoneAccent,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(6.dp))

                                    // Bold Right-Aligned Time — exact Adzan right time column
                                    Text(
                                        text = MissionTimeHelper.formatAlarmSummary(mission, s.isBangla),
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isNextHighlight) StoneAccent else StoneInkPrimary
                                    )

                                    Spacer(modifier = Modifier.width(6.dp))

                                    // Notification Bell Toggle — exact 32.dp Adzan bell button
                                    IconButton(
                                        onClick = { onToggleAlarm(mission.id) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (mission.alarmEnabled) {
                                                Icons.Outlined.Notifications
                                            } else {
                                                Icons.Outlined.NotificationsOff
                                            },
                                            contentDescription = "Toggle alarm ${mission.title}",
                                            tint = if (mission.alarmEnabled) StoneAccent else StoneInkSecondary.copy(alpha = 0.45f),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }

                                // Slim inline ক, খ, গ, ঘ, CQ, MCQ checklist + subtle Edit/Delete on same indented line
                                if (mission.targetTags.isNotEmpty() || mission.notes.isNotBlank()) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(start = 38.dp, top = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        FlowRow(
                                            modifier = Modifier.weight(1f),
                                            horizontalArrangement = Arrangement.spacedBy(5.dp),
                                            verticalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            PrepQuestionTag.entries
                                                .filter { it in mission.targetTags }
                                                .forEach { tag ->
                                                    val done = tag in mission.completedTags
                                                    Box(
                                                        modifier = Modifier
                                                            .clip(RoundedCornerShape(8.dp))
                                                            .background(
                                                                if (done) StoneSuccess else Color(0xFFEBE4DB)
                                                            )
                                                            .clickable { onTogglePrepTag(mission.id, tag) }
                                                            .padding(horizontal = 7.dp, vertical = 3.dp)
                                                            .testTag("mission_item_tag_${mission.id}_${tag.name}")
                                                    ) {
                                                        Text(
                                                            text = if (done) "✓ ${tag.label}" else tag.label,
                                                            style = MaterialTheme.typography.labelSmall,
                                                            fontSize = 10.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = if (done) NeumorphicHighlight else StoneInkPrimary
                                                        )
                                                    }
                                                }
                                        }

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            IconButton(
                                                onClick = { onEditMission(mission) },
                                                modifier = Modifier
                                                    .size(28.dp)
                                                    .testTag("mission_edit_${mission.id}")
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Rounded.Edit,
                                                    contentDescription = "Edit ${mission.title}",
                                                    tint = StoneInkSecondary,
                                                    modifier = Modifier.size(15.dp)
                                                )
                                            }
                                            IconButton(
                                                onClick = { onDeleteMission(mission.id) },
                                                modifier = Modifier
                                                    .size(28.dp)
                                                    .testTag("mission_delete_${mission.id}")
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Rounded.DeleteOutline,
                                                    contentDescription = "Delete ${mission.title}",
                                                    tint = Color(0xFFB35340),
                                                    modifier = Modifier.size(15.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Section B: DOWN OPTION — Click-to-Select "Add Mission Set" Builder directly inside the Adzan Popup!
                    Surface(
                        color = StonePanel.copy(alpha = 0.85f),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, StoneDivider, RoundedCornerShape(16.dp))
                            .testTag("mission_slot_inline_add_${slot.index}")
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (s.isBangla) {
                                        "+ নতুন সেট যোগ করুন (ক্লিক করে সিলেক্ট)"
                                    } else {
                                        "+ Add Set (Click Options to Select)"
                                    },
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = StoneInkPrimary
                                )
                                Text(
                                    text = if (s.isBangla) "ফুল এডিটর" else "Custom Alarm",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = StoneAccent,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable(onClick = onAddMissionDialog)
                                        .padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }

                            // 1. Click-to-select Subject Chips
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                val isGeneralSelected = inlineSubjectId.isBlank()
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isGeneralSelected) StoneInkPrimary else StoneSurface)
                                        .border(1.dp, StoneDivider, RoundedCornerShape(10.dp))
                                        .clickable {
                                            inlineSubjectId = ""
                                            inlineChapterId = ""
                                            inlineChapterTitle = ""
                                        }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                        .testTag("mission_inline_subj_${slot.index}_custom")
                                ) {
                                    Text(
                                        text = if (s.isBangla) "সাধারণ" else "General",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isGeneralSelected) NeumorphicHighlight else StoneInkPrimary
                                    )
                                }

                                trackerSubjects.forEach { subj ->
                                    val selected = inlineSubjectId == subj.id
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(if (selected) StoneInkPrimary else StoneSurface)
                                            .border(
                                                1.dp,
                                                if (selected) StoneInkPrimary else Color(subj.colorHex).copy(alpha = 0.55f),
                                                RoundedCornerShape(10.dp)
                                            )
                                            .clickable {
                                                inlineSubjectId = subj.id
                                                val firstCh = subj.chaptersForPaper(inlinePaper).firstOrNull()
                                                inlineChapterId = firstCh?.id.orEmpty()
                                                inlineChapterTitle = firstCh?.title.orEmpty()
                                            }
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                            .testTag("mission_inline_subj_${slot.index}_${subj.id}")
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .size(7.dp)
                                                    .clip(CircleShape)
                                                    .background(Color(subj.colorHex))
                                            )
                                            Spacer(modifier = Modifier.width(5.dp))
                                            Text(
                                                text = if (s.isBangla) subj.nameBn else subj.nameEn,
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = if (selected) NeumorphicHighlight else StoneInkPrimary
                                            )
                                        }
                                    }
                                }
                            }

                            // 2. Paper + Chapter Click-to-Select when a subject is selected
                            if (activeSubject != null) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    listOf(1 to s.trackerFirstPaper, 2 to s.trackerSecondPaper).forEach { (paperNum, label) ->
                                        val selected = inlinePaper == paperNum
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(9.dp))
                                                .background(if (selected) StoneAccent else StoneSurface)
                                                .border(1.dp, StoneDivider, RoundedCornerShape(9.dp))
                                                .clickable {
                                                    inlinePaper = paperNum
                                                    val firstCh = activeSubject.chaptersForPaper(paperNum).firstOrNull()
                                                    inlineChapterId = firstCh?.id.orEmpty()
                                                    inlineChapterTitle = firstCh?.title.orEmpty()
                                                }
                                                .padding(vertical = 6.dp)
                                                .testTag("mission_inline_paper_${slot.index}_$paperNum"),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = label,
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = if (selected) NeumorphicHighlight else StoneInkPrimary
                                            )
                                        }
                                    }
                                }

                                if (inlineChapters.isNotEmpty()) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .horizontalScroll(rememberScrollState()),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        inlineChapters.forEach { ch ->
                                            val selected = inlineChapterId == ch.id
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(9.dp))
                                                    .background(if (selected) StoneInkPrimary else StoneSurface)
                                                    .border(1.dp, StoneDivider, RoundedCornerShape(9.dp))
                                                    .clickable {
                                                        inlineChapterId = ch.id
                                                        inlineChapterTitle = ch.title
                                                    }
                                                    .padding(horizontal = 9.dp, vertical = 5.dp)
                                                    .testTag("mission_inline_ch_${slot.index}_${ch.id}")
                                            ) {
                                                Text(
                                                    text = ch.title,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = if (selected) NeumorphicHighlight else StoneInkPrimary,
                                                    maxLines = 1
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            // 3. Click-to-Select Question Options: ক, খ, গ, ঘ, CQ, MCQ
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (s.isBangla) "অপশন নির্বাচন:" else "Select Options:",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = StoneInkSecondary
                                )
                                Text(
                                    text = if (inlineTags.size == PrepQuestionTag.entries.size) {
                                        if (s.isBangla) "সব বাতিল" else "Clear All"
                                    } else {
                                        if (s.isBangla) "সব নিন" else "Select All"
                                    },
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = StoneAccent,
                                    modifier = Modifier
                                        .clickable {
                                            inlineTags = if (inlineTags.size == PrepQuestionTag.entries.size) {
                                                emptySet()
                                            } else {
                                                PrepQuestionTag.entries.toSet()
                                            }
                                        }
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                        .testTag("mission_inline_tag_all_${slot.index}")
                                )
                            }

                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                PrepQuestionTag.entries.forEach { tag ->
                                    val selected = tag in inlineTags
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(9.dp))
                                            .background(if (selected) StoneAccent else StoneSurface)
                                            .border(
                                                1.dp,
                                                if (selected) StoneAccent else StoneDivider,
                                                RoundedCornerShape(9.dp)
                                            )
                                            .clickable {
                                                inlineTags = if (selected) inlineTags - tag else inlineTags + tag
                                            }
                                            .padding(horizontal = 11.dp, vertical = 6.dp)
                                            .testTag("mission_inline_tag_${slot.index}_${tag.name}")
                                    ) {
                                        Text(
                                            text = tag.label,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = if (selected) NeumorphicHighlight else StoneInkPrimary
                                        )
                                    }
                                }
                            }

                            // 4. Quick Alarm Option Chips
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                listOf(
                                    MissionAlarmMode.AT_BLOCK_START,
                                    MissionAlarmMode.FIFTEEN_MIN_BEFORE,
                                    MissionAlarmMode.THIRTY_MIN_BEFORE
                                ).forEach { mode ->
                                    val selected = inlineAlarmEnabled && inlineAlarmMode == mode
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(9.dp))
                                            .background(if (selected) StoneInkPrimary else StoneSurface)
                                            .border(1.dp, StoneDivider, RoundedCornerShape(9.dp))
                                            .clickable {
                                                inlineAlarmEnabled = true
                                                inlineAlarmMode = mode
                                            }
                                            .padding(horizontal = 9.dp, vertical = 5.dp)
                                            .testTag("mission_inline_alarm_${slot.index}_${mode.name}")
                                    ) {
                                        Text(
                                            text = if (s.isBangla) mode.labelBn else mode.labelEn,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (selected) NeumorphicHighlight else StoneInkPrimary,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(9.dp))
                                        .background(if (!inlineAlarmEnabled) StoneInkPrimary else StoneSurface)
                                        .border(1.dp, StoneDivider, RoundedCornerShape(9.dp))
                                        .clickable { inlineAlarmEnabled = false }
                                        .padding(horizontal = 9.dp, vertical = 5.dp)
                                        .testTag("mission_inline_alarm_${slot.index}_OFF")
                                ) {
                                    Text(
                                        text = if (s.isBangla) "অ্যালার্ম বন্ধ" else "No Alarm",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (!inlineAlarmEnabled) NeumorphicHighlight else StoneInkPrimary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            // 5. Optional Title Note + Primary Click-to-Add Button
                            OutlinedTextField(
                                value = inlineTitleInput,
                                onValueChange = { inlineTitleInput = it },
                                placeholder = {
                                    val hint = buildString {
                                        val subjText = activeSubject?.let { if (s.isBangla) it.nameBn else it.nameEn }.orEmpty()
                                        if (subjText.isNotBlank()) {
                                            append(subjText)
                                            append(if (s.isBangla) " (${inlinePaper}ম)" else " (${if (inlinePaper == 1) "1st" else "2nd"})")
                                        }
                                        if (inlineChapterTitle.isNotBlank()) {
                                            if (isNotEmpty()) append(" • ")
                                            append(inlineChapterTitle)
                                        }
                                    }.ifBlank {
                                        if (s.isBangla) "ঐচ্ছিক শিরোনাম (খালি থাকলেও চলবে)" else "Optional title (auto-generated if blank)"
                                    }
                                    Text(hint, style = MaterialTheme.typography.bodySmall)
                                },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("mission_inline_title_${slot.index}")
                            )

                            Button(
                                onClick = {
                                    val subjName = activeSubject?.let { if (s.isBangla) it.nameBn else it.nameEn }.orEmpty()
                                    val autoTitle = buildString {
                                        if (subjName.isNotBlank()) {
                                            append(subjName)
                                            append(if (s.isBangla) " (${inlinePaper}ম)" else " (${if (inlinePaper == 1) "1st" else "2nd"})")
                                        }
                                        if (inlineChapterTitle.isNotBlank()) {
                                            if (isNotEmpty()) append(" • ")
                                            append(inlineChapterTitle)
                                        }
                                        if (isEmpty()) {
                                            append(if (s.isBangla) slot.phaseTitleBn else slot.phaseTitleEn)
                                            if (inlineTags.isNotEmpty()) {
                                                append(" (${inlineTags.joinToString(",") { it.label }})")
                                            }
                                        }
                                    }
                                    val finalTitle = inlineTitleInput.trim().ifEmpty { autoTitle }
                                    val newItem = MissionItem(
                                        id = "msn_${System.currentTimeMillis()}_${System.nanoTime() % 100000}_${slot.index}",
                                        dateKey = dateKey,
                                        blockIndex = slot.index,
                                        title = finalTitle,
                                        notes = "",
                                        category = subjName.ifBlank { "Study" },
                                        subjectId = inlineSubjectId,
                                        subjectName = subjName,
                                        paper = inlinePaper,
                                        chapterId = inlineChapterId,
                                        chapterTitle = inlineChapterTitle,
                                        targetTags = inlineTags,
                                        completedTags = emptySet(),
                                        priority = MissionPriority.HIGH,
                                        isCompleted = false,
                                        alarmEnabled = inlineAlarmEnabled,
                                        alarmMode = inlineAlarmMode,
                                        customAlarmHour = slot.startHour24,
                                        customAlarmMinute = 0,
                                        useAlarmTone = true,
                                        showInLiveNotification = true,
                                        updatedAtMillis = System.currentTimeMillis()
                                    )
                                    onSaveInlineMission(newItem)
                                    inlineTitleInput = ""
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = StoneInkPrimary,
                                    contentColor = NeumorphicHighlight
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .testTag("mission_inline_save_${slot.index}")
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Add,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (s.isBangla) "এই ৪ ঘণ্টায় যোগ করুন (${slot.rangeShortEn})" else "Add to ${slot.rangeShortEn}",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SmallTagChip(
    text: String,
    bg: Color,
    fg: Color
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 10.sp,
            color = fg,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MissionEditorDialog(
    dateKey: String,
    blockSlot: MissionBlockSlot,
    trackerSubjects: List<TrackerSubject>,
    existing: MissionItem?,
    onDismiss: () -> Unit,
    onSave: (MissionItem, Boolean) -> Unit
) {
    val s = LocalAppStrings.current

    var selectedSubjectId by remember(existing) { mutableStateOf(existing?.subjectId ?: "") }
    var customSubjectName by remember(existing) { mutableStateOf(existing?.subjectName ?: "") }
    var selectedPaper by remember(existing) { mutableIntStateOf(existing?.paper ?: 1) }
    var selectedChapterId by remember(existing) { mutableStateOf(existing?.chapterId ?: "") }
    var chapterTitleInput by remember(existing) { mutableStateOf(existing?.chapterTitle ?: "") }
    var selectedTargetTags by remember(existing) {
        mutableStateOf(existing?.targetTags ?: setOf(PrepQuestionTag.KA, PrepQuestionTag.KHA, PrepQuestionTag.MCQ))
    }

    var title by remember(existing) { mutableStateOf(existing?.title ?: "") }
    var notes by remember(existing) { mutableStateOf(existing?.notes ?: "") }
    var category by remember(existing) { mutableStateOf(existing?.category ?: "Study") }
    var priority by remember(existing) { mutableStateOf(existing?.priority ?: MissionPriority.HIGH) }
    var alarmEnabled by remember(existing) { mutableStateOf(existing?.alarmEnabled ?: true) }
    var alarmMode by remember(existing) {
        mutableStateOf(existing?.alarmMode ?: MissionAlarmMode.AT_BLOCK_START)
    }
    var customHour by remember(existing) {
        mutableIntStateOf(existing?.customAlarmHour ?: blockSlot.startHour24)
    }
    var customMinute by remember(existing) {
        mutableIntStateOf(existing?.customAlarmMinute ?: 0)
    }
    var useAlarmTone by remember(existing) { mutableStateOf(existing?.useAlarmTone ?: true) }
    var showInLive by remember(existing) {
        mutableStateOf(existing?.showInLiveNotification ?: true)
    }
    var repeatNext7Days by remember { mutableStateOf(false) }

    val activeSubjectObj = remember(selectedSubjectId, trackerSubjects) {
        trackerSubjects.firstOrNull { it.id == selectedSubjectId }
    }
    val chaptersForSelectedPaper = remember(activeSubjectObj, selectedPaper) {
        activeSubjectObj?.chaptersForPaper(selectedPaper).orEmpty()
    }

    val effectiveSubjectName = activeSubjectObj?.let {
        if (s.isBangla) it.nameBn else it.nameEn
    } ?: customSubjectName.trim()

    val effectiveTitleSuggestion = remember(effectiveSubjectName, chapterTitleInput, selectedPaper) {
        buildString {
            if (effectiveSubjectName.isNotBlank()) {
                append(effectiveSubjectName)
                if (selectedPaper in 1..2 && activeSubjectObj != null) {
                    append(if (s.isBangla) " (${selectedPaper}ম)" else " (${if (selectedPaper == 1) "1st" else "2nd"})")
                }
            }
            if (chapterTitleInput.isNotBlank()) {
                if (isNotEmpty()) append(" • ")
                append(chapterTitleInput.trim())
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            color = Color(0xFFF3EEE8),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .widthIn(max = 540.dp)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 18.dp)
                .testTag("mission_editor_dialog")
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = s.missionEditMissionTitle,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = StoneInkPrimary
                )
                Text(
                    text = "${MissionTimeHelper.formatDateFullHeader(dateKey, s.isBangla)} • ${blockSlot.rangeFullEn}",
                    style = MaterialTheme.typography.labelMedium,
                    color = StoneAccent,
                    fontWeight = FontWeight.SemiBold
                )

                // 1. Academic Subject / Paper / Chapter Selection Card
                Surface(
                    color = StoneSurface,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, StoneDivider, RoundedCornerShape(16.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = if (s.isBangla) "বিষয়, পত্র ও অধ্যায় নির্বাচন" else "Select Subject, Paper & Chapter",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = StoneInkPrimary
                        )

                        // Subject Horizontal Strip
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // General / Custom pill
                            val isGeneral = selectedSubjectId.isBlank()
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isGeneral) StoneInkPrimary else Color(0xFFF6F2EC))
                                    .border(1.dp, StoneDivider, RoundedCornerShape(12.dp))
                                    .clickable {
                                        selectedSubjectId = ""
                                        selectedChapterId = ""
                                    }
                                    .padding(horizontal = 12.dp, vertical = 7.dp)
                                    .testTag("mission_subject_chip_custom")
                            ) {
                                Text(
                                    text = if (s.isBangla) "সাধারণ / কাস্টম" else "General / Custom",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isGeneral) NeumorphicHighlight else StoneInkPrimary
                                )
                            }

                            trackerSubjects.forEach { subj ->
                                val selected = selectedSubjectId == subj.id
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (selected) StoneInkPrimary else Color(0xFFF6F2EC))
                                        .border(
                                            1.dp,
                                            if (selected) StoneInkPrimary else Color(subj.colorHex).copy(alpha = 0.55f),
                                            RoundedCornerShape(12.dp)
                                        )
                                        .clickable {
                                            selectedSubjectId = subj.id
                                            customSubjectName = if (s.isBangla) subj.nameBn else subj.nameEn
                                            category = subj.nameEn
                                            val firstCh = subj.chaptersForPaper(selectedPaper).firstOrNull()
                                            if (firstCh != null) {
                                                selectedChapterId = firstCh.id
                                                chapterTitleInput = firstCh.title
                                            }
                                        }
                                        .padding(horizontal = 12.dp, vertical = 7.dp)
                                        .testTag("mission_subject_chip_${subj.id}")
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(Color(subj.colorHex))
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (s.isBangla) subj.nameBn else subj.nameEn,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = if (selected) NeumorphicHighlight else StoneInkPrimary
                                        )
                                    }
                                }
                            }
                        }

                        // Paper 1 / Paper 2 & Chapter list when a tracker subject is chosen
                        if (activeSubjectObj != null) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf(1 to s.trackerFirstPaper, 2 to s.trackerSecondPaper).forEach { (paperNum, label) ->
                                    val selected = selectedPaper == paperNum
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(if (selected) StoneAccent else Color(0xFFF6F2EC))
                                            .clickable {
                                                selectedPaper = paperNum
                                                val firstCh = activeSubjectObj.chaptersForPaper(paperNum).firstOrNull()
                                                selectedChapterId = firstCh?.id.orEmpty()
                                                chapterTitleInput = firstCh?.title.orEmpty()
                                            }
                                            .padding(vertical = 7.dp)
                                            .testTag("mission_paper_btn_$paperNum"),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = label,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = if (selected) NeumorphicHighlight else StoneInkPrimary
                                        )
                                    }
                                }
                            }

                            if (chaptersForSelectedPaper.isNotEmpty()) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    chaptersForSelectedPaper.forEach { ch ->
                                        val selected = selectedChapterId == ch.id
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(if (selected) StoneInkPrimary else Color(0xFFF6F2EC))
                                                .border(1.dp, StoneDivider, RoundedCornerShape(10.dp))
                                                .clickable {
                                                    selectedChapterId = ch.id
                                                    chapterTitleInput = ch.title
                                                }
                                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                                .testTag("mission_chapter_chip_${ch.id}")
                                        ) {
                                            Text(
                                                text = ch.title,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = if (selected) NeumorphicHighlight else StoneInkPrimary,
                                                maxLines = 1
                                            )
                                        }
                                    }
                                }
                            }
                        } else {
                            OutlinedTextField(
                                value = customSubjectName,
                                onValueChange = { customSubjectName = it },
                                label = { Text(if (s.isBangla) "কাস্টম বিষয় (ঐচ্ছিক)" else "Custom Subject (Optional)") },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("mission_custom_subject_input")
                            )
                        }

                        OutlinedTextField(
                            value = chapterTitleInput,
                            onValueChange = {
                                chapterTitleInput = it
                                selectedChapterId = ""
                            },
                            label = { Text(if (s.isBangla) "অধ্যায় / টপিক" else "Chapter / Topic") },
                            placeholder = {
                                Text(if (s.isBangla) "অধ্যায় লিখুন বা উপর থেকে নির্বাচন করুন" else "Pick above or type chapter/topic")
                            },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("mission_chapter_input")
                        )

                        // Question Option Multi-Select: ক, খ, গ, ঘ, CQ, MCQ
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (s.isBangla) "টার্গেট অংশ (ক, খ, গ, ঘ, CQ, MCQ)" else "Study Targets (ক, খ, গ, ঘ, CQ, MCQ)",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = StoneInkPrimary
                            )
                            Text(
                                text = if (selectedTargetTags.size == PrepQuestionTag.entries.size) {
                                    if (s.isBangla) "সব বাতিল" else "Clear"
                                } else {
                                    if (s.isBangla) "সবগুলো" else "Select All"
                                },
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = StoneAccent,
                                modifier = Modifier
                                    .clickable {
                                        selectedTargetTags = if (selectedTargetTags.size == PrepQuestionTag.entries.size) {
                                            emptySet()
                                        } else {
                                            PrepQuestionTag.entries.toSet()
                                        }
                                    }
                                    .padding(4.dp)
                                    .testTag("mission_tags_select_all")
                            )
                        }

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            PrepQuestionTag.entries.forEach { tag ->
                                val selected = tag in selectedTargetTags
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (selected) StoneAccent else Color(0xFFF6F2EC))
                                        .border(
                                            1.dp,
                                            if (selected) StoneAccent else StoneDivider,
                                            RoundedCornerShape(10.dp)
                                        )
                                        .clickable {
                                            selectedTargetTags = if (selected) {
                                                selectedTargetTags - tag
                                            } else {
                                                selectedTargetTags + tag
                                            }
                                        }
                                        .padding(horizontal = 13.dp, vertical = 8.dp)
                                        .testTag("mission_tag_select_${tag.name}")
                                ) {
                                    Text(
                                        text = tag.label,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (selected) NeumorphicHighlight else StoneInkPrimary
                                    )
                                }
                            }
                        }
                    }
                }

                // 2. Mission Title & Focus Notes
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(if (s.isBangla) "মিশন শিরোনাম (খালি রাখলে বিষয়/অধ্যায় বসবে)" else "Mission Title (auto-fills from Subject/Chapter)") },
                    placeholder = {
                        Text(
                            effectiveTitleSuggestion.ifBlank {
                                if (s.isBangla) "যেমন: পদার্থবিজ্ঞান ২য় অধ্যায় রিভিশন" else "e.g. Physics Vector Problems + 20 MCQs"
                            }
                        )
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = StoneAccent,
                        unfocusedBorderColor = StoneDivider,
                        focusedTextColor = StoneInkPrimary,
                        unfocusedTextColor = StoneInkPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("mission_title_input")
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text(if (s.isBangla) "নোট / বিস্তারিত লক্ষ্য" else "Focus Notes / Sub-goals") },
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = StoneAccent,
                        unfocusedBorderColor = StoneDivider,
                        focusedTextColor = StoneInkPrimary,
                        unfocusedTextColor = StoneInkPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("mission_notes_input")
                )

                // Priority Selector
                Text(
                    text = if (s.isBangla) "গুরুত্ব (Priority)" else "Priority",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = StoneInkPrimary
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MissionPriority.entries.forEach { pri ->
                        val selected = priority == pri
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (selected) StoneAccent else StoneSurface)
                                .clickable { priority = pri }
                                .padding(vertical = 9.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (s.isBangla) pri.labelBn else pri.labelEn,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (selected) NeumorphicHighlight else StoneInkPrimary
                            )
                        }
                    }
                }

                // 3. Alarm & Notification Configuration Box
                Surface(
                    color = StoneSurface,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, StoneDivider, RoundedCornerShape(16.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.Alarm,
                                    contentDescription = null,
                                    tint = StoneAccent
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (s.isBangla) "অ্যালার্ম ও নোটিফিকেশন সেট" else "Enable Alarm & Alert",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = StoneInkPrimary
                                )
                            }
                            Switch(
                                checked = alarmEnabled,
                                onCheckedChange = { alarmEnabled = it },
                                modifier = Modifier.testTag("mission_alarm_switch"),
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = NeumorphicHighlight,
                                    checkedTrackColor = StoneAccent
                                )
                            )
                        }

                        AnimatedVisibility(visible = alarmEnabled) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    MissionAlarmMode.entries.forEach { mode ->
                                        val selected = alarmMode == mode
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(
                                                    if (selected) StoneInkPrimary else Color(0xFFF6F2EC)
                                                )
                                                .border(1.dp, StoneDivider, RoundedCornerShape(10.dp))
                                                .clickable { alarmMode = mode }
                                                .padding(horizontal = 10.dp, vertical = 7.dp)
                                                .testTag("mission_alarm_mode_${mode.name}")
                                        ) {
                                            Text(
                                                text = if (s.isBangla) mode.labelBn else mode.labelEn,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = if (selected) NeumorphicHighlight else StoneInkPrimary,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }
                                }

                                if (alarmMode == MissionAlarmMode.CUSTOM_TIME) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color(0xFFF6F2EC))
                                            .padding(10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = MissionTimeHelper.formatTime12h(customHour, customMinute),
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = StoneInkPrimary,
                                            modifier = Modifier.testTag("mission_custom_time_display")
                                        )
                                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            OutlinedButton(
                                                onClick = { customHour = (customHour + 1) % 24 },
                                                contentPadding = PaddingValues(horizontal = 10.dp),
                                                modifier = Modifier
                                                    .height(40.dp)
                                                    .testTag("mission_custom_hour_plus")
                                            ) {
                                                Text("+1 Hr", color = StoneInkPrimary)
                                            }
                                            OutlinedButton(
                                                onClick = { customMinute = (customMinute + 15) % 60 },
                                                contentPadding = PaddingValues(horizontal = 10.dp),
                                                modifier = Modifier
                                                    .height(40.dp)
                                                    .testTag("mission_custom_min_plus")
                                            ) {
                                                Text("+15m", color = StoneInkPrimary)
                                            }
                                        }
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (s.isBangla) "অ্যালার্ম রিংটোন ও ভাইব্রেশন" else "Alarm Sound & Vibration",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = StoneInkPrimary
                                    )
                                    Switch(
                                        checked = useAlarmTone,
                                        onCheckedChange = { useAlarmTone = it },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = NeumorphicHighlight,
                                            checkedTrackColor = StoneSuccess
                                        )
                                    )
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (s.isBangla) "উইজেট ও লাইভ নোটিফিকেশনে দেখান" else "Show on Live Widget & Banner",
                                style = MaterialTheme.typography.bodySmall,
                                color = StoneInkPrimary
                            )
                            Switch(
                                checked = showInLive,
                                onCheckedChange = { showInLive = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = NeumorphicHighlight,
                                    checkedTrackColor = StoneAccent
                                )
                            )
                        }

                        if (existing == null) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (s.isBangla) "আগামী ৭ দিন একই সময়ে রাখুন" else "Repeat this 4h mission for next 7 days",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = StoneInkPrimary
                                )
                                Switch(
                                    checked = repeatNext7Days,
                                    onCheckedChange = { repeatNext7Days = it },
                                    modifier = Modifier.testTag("mission_repeat_7days_switch"),
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = NeumorphicHighlight,
                                        checkedTrackColor = StoneAccent
                                    )
                                )
                            }
                        }
                    }
                }

                val finalResolvedTitle = title.trim().ifEmpty { effectiveTitleSuggestion }

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.height(48.dp)
                    ) {
                        Text(s.cancelButton, color = StoneInkSecondary)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = {
                            if (finalResolvedTitle.isEmpty()) return@Button
                            val preservedDone = existing?.completedTags
                                ?.intersect(selectedTargetTags)
                                ?: emptySet()
                            val item = MissionItem(
                                id = existing?.id ?: "msn_${System.currentTimeMillis()}_${System.nanoTime() % 100000}_${blockSlot.index}",
                                dateKey = dateKey,
                                blockIndex = blockSlot.index,
                                title = finalResolvedTitle,
                                notes = notes.trim(),
                                category = effectiveSubjectName.ifBlank { category },
                                subjectId = selectedSubjectId,
                                subjectName = effectiveSubjectName,
                                paper = selectedPaper,
                                chapterId = selectedChapterId,
                                chapterTitle = chapterTitleInput.trim(),
                                targetTags = selectedTargetTags,
                                completedTags = preservedDone,
                                priority = priority,
                                isCompleted = existing?.isCompleted ?: false,
                                alarmEnabled = alarmEnabled,
                                alarmMode = alarmMode,
                                customAlarmHour = customHour,
                                customAlarmMinute = customMinute,
                                useAlarmTone = useAlarmTone,
                                showInLiveNotification = showInLive,
                                updatedAtMillis = System.currentTimeMillis()
                            )
                            onSave(item, repeatNext7Days)
                        },
                        enabled = finalResolvedTitle.isNotEmpty(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = StoneInkPrimary,
                            contentColor = NeumorphicHighlight
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("mission_save_btn")
                    ) {
                        Text(
                            text = if (s.isBangla) "সংরক্ষণ করুন" else "Save Mission Set",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
