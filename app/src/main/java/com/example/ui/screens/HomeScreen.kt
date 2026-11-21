package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.NotificationsOff
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.R
import com.example.data.model.FocusSessionMode
import com.example.data.preferences.UserPrefsState
import com.example.ui.components.AyatBookIcon
import com.example.ui.components.DuaPrayingIcon
import com.example.ui.components.DzikirTasbihIcon
import com.example.ui.components.GroupsIcon
import com.example.ui.components.NeumorphicCard
import com.example.ui.components.NeumorphicCircleButton
import com.example.ui.components.NeumorphicPillButton
import com.example.ui.components.ProfileRingIcon
import com.example.ui.components.QuranStandIcon
import com.example.ui.components.SelengkapnyaGridIcon
import com.example.ui.components.StudyDashboardSection
import com.example.ui.components.TrackerChecklistIcon
import com.example.ui.theme.AmiriArabicFamily
import com.example.ui.theme.BarlowCondensedFamily
import com.example.ui.theme.NeumorphicBevel
import com.example.ui.theme.NeumorphicHighlight
import com.example.ui.theme.NeumorphicShadow
import com.example.ui.theme.StoneAccent
import com.example.ui.theme.StoneBackground
import com.example.ui.theme.StoneBackgroundBottom
import com.example.ui.theme.StoneBackgroundTop
import com.example.ui.theme.StoneDivider
import com.example.ui.theme.StoneInkPrimary
import com.example.ui.theme.StoneInkSecondary
import com.example.ui.theme.StonePanel
import com.example.ui.theme.StoneSuccess
import com.example.ui.theme.StoneSurface
import com.example.ui.viewmodel.HomeUiState
import com.example.ui.viewmodel.StudyDashboardUiState

@Composable
fun HomeScreen(
    homeState: HomeUiState,
    prefs: UserPrefsState,
    onOpenProfile: () -> Unit,
    onOpenQuran: () -> Unit,
    onOpenSurah: (Int) -> Unit,
    onOpenDua: () -> Unit,
    onOpenDzikir: () -> Unit,
    onOpenGroups: () -> Unit,
    onOpenTracker: () -> Unit = {},
    onOpenMore: () -> Unit,
    onCycleDailyAyah: () -> Unit,
    onToggleAyahBookmark: (Int, Int) -> Unit,
    onToggleAdzanMaster: (Boolean) -> Unit,
    onToggleAdzanPrayer: (String) -> Unit,
    onTogglePrayerDone: (String) -> Unit,
    studyDashboard: StudyDashboardUiState = StudyDashboardUiState(),
    myGroupsCount: Int = 2,
    liveStudyingCount: Int = 4,
    onToggleSubjectTimer: (String) -> Unit = {},
    onStartSubjectAndOpenRoom: (String) -> Unit = {},
    onOpenFocusMode: () -> Unit = {},
    onStartFocusAndOpen: () -> Unit = {},
    onToggleFocusButton: () -> Unit = {},
    onOpenMyGroups: () -> Unit = onOpenGroups,
    onAddSubject: (String, Long, Double) -> Unit = { _, _, _ -> },
    onUpdateSubjectGoal: (String, Double) -> Unit = { _, _ -> },
    onResetSubjectTimer: (String) -> Unit = {},
    onDeleteSubject: (String) -> Unit = {},
    onAddTodo: (String) -> Unit = {},
    onToggleTodo: (String) -> Unit = {},
    onDeleteTodo: (String) -> Unit = {},
    onUpdateFocusGoalHours: (Int) -> Unit = {},
    onUpdateBatchTag: (String) -> Unit = {},
    onSelectFocusMode: (FocusSessionMode) -> Unit = {},
    onSelectWeekDayIndex: (Int) -> Unit = {}
) {
    val context = LocalContext.current
    var adzanExpanded by remember { mutableStateOf(false) }

    val notificationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        onToggleAdzanMaster(granted)
    }

    fun handleAdzanMasterToggle(desired: Boolean) {
        if (!desired) {
            onToggleAdzanMaster(false)
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPerm = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (hasPerm) {
                onToggleAdzanMaster(true)
            } else {
                notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            onToggleAdzanMaster(true)
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
    ) {
        val isWide = maxWidth >= 600.dp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = if (isWide) 840.dp else 560.dp)
                    .fillMaxWidth()
            ) {
                // Top Header matching screenshot
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.home_brand_title),
                        style = MaterialTheme.typography.headlineMedium,
                        color = StoneInkPrimary
                    )

                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .clickable(onClick = onOpenProfile)
                            .testTag("profile_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        ProfileRingIcon(modifier = Modifier.size(38.dp))
                    }
                }

                // Hero Prayer / Adzan Card
                HeroAdzanCard(
                    homeState = homeState,
                    prefs = prefs,
                    expanded = adzanExpanded,
                    onToggleExpand = { adzanExpanded = !adzanExpanded },
                    onToggleAdzanMaster = ::handleAdzanMasterToggle,
                    onToggleAdzanPrayer = onToggleAdzanPrayer,
                    onTogglePrayerDone = onTogglePrayerDone,
                    onOpenLocationSettings = onOpenProfile,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 22.dp)
                )

                Spacer(modifier = Modifier.height(22.dp))
            }

            // Lower Rounded Neumorphic Panel Container (exact match to screenshot)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .drawBehind {
                        val radiusPx = 34.dp.toPx()
                        drawIntoCanvas { canvas ->
                            val shadowPaint = Paint().apply {
                                asFrameworkPaint().apply {
                                    isAntiAlias = true
                                    color = NeumorphicShadow.copy(alpha = 0.50f).toArgb()
                                    maskFilter = android.graphics.BlurMaskFilter(
                                        14.dp.toPx(),
                                        android.graphics.BlurMaskFilter.Blur.NORMAL
                                    )
                                }
                            }
                            canvas.drawRoundRect(
                                left = 0f,
                                top = (-4).dp.toPx(),
                                right = size.width,
                                bottom = size.height + radiusPx,
                                radiusX = radiusPx,
                                radiusY = radiusPx,
                                paint = shadowPaint
                            )
                        }
                        drawRoundRect(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color(0xFFDAD4CE), StonePanel, Color(0xFFCEC7C1))
                            ),
                            topLeft = Offset.Zero,
                            size = size.copy(height = size.height + radiusPx),
                            cornerRadius = CornerRadius(radiusPx, radiusPx)
                        )
                        drawRoundRect(
                            color = NeumorphicBevel.copy(alpha = 0.7f),
                            topLeft = Offset.Zero,
                            size = size.copy(height = size.height + radiusPx),
                            cornerRadius = CornerRadius(radiusPx, radiusPx),
                            style = Stroke(width = 1.2.dp.toPx())
                        )
                    }
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(top = 28.dp, bottom = 24.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    modifier = Modifier
                        .widthIn(max = if (isWide) 840.dp else 560.dp)
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    // Circular Feature Grid
                    FeatureMenuGrid(
                        onOpenQuran = onOpenQuran,
                        onOpenDua = onOpenDua,
                        onOpenDzikir = onOpenDzikir,
                        onOpenGroups = onOpenGroups,
                        onOpenTracker = onOpenTracker,
                        onOpenMore = onOpenMore
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Ayat Hari Ini Card
                    val ayahKey = "${homeState.dailyAyah.surahNumber}:${homeState.dailyAyah.ayahNumber}"
                    val isBookmarked = ayahKey in prefs.bookmarkedAyahKeys

                    AyatHariIniCard(
                        homeState = homeState,
                        isBookmarked = isBookmarked,
                        onCardClick = { onOpenSurah(homeState.dailyAyah.surahNumber) },
                        onRefreshClick = onCycleDailyAyah,
                        onBookmarkClick = {
                            onToggleAyahBookmark(
                                homeState.dailyAyah.surahNumber,
                                homeState.dailyAyah.ayahNumber
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(22.dp))

                    // Study Room Dashboard matching shared screenshots
                    StudyDashboardSection(
                        dashboard = studyDashboard,
                        myGroupsCount = myGroupsCount,
                        liveStudyingCount = liveStudyingCount,
                        onToggleSubjectTimer = onToggleSubjectTimer,
                        onStartSubjectAndOpenRoom = onStartSubjectAndOpenRoom,
                        onOpenFocusMode = onOpenFocusMode,
                        onStartFocusAndOpen = onStartFocusAndOpen,
                        onToggleFocusButton = onToggleFocusButton,
                        onOpenMyGroups = onOpenMyGroups,
                        onAddSubject = onAddSubject,
                        onUpdateSubjectGoal = onUpdateSubjectGoal,
                        onResetSubjectTimer = onResetSubjectTimer,
                        onDeleteSubject = onDeleteSubject,
                        onAddTodo = onAddTodo,
                        onToggleTodo = onToggleTodo,
                        onDeleteTodo = onDeleteTodo,
                        onUpdateFocusGoalHours = onUpdateFocusGoalHours,
                        onUpdateBatchTag = onUpdateBatchTag,
                        onSelectFocusMode = onSelectFocusMode,
                        onSelectWeekDayIndex = onSelectWeekDayIndex
                    )
                }
            }
        }
    }
}

@Composable
private fun HeroAdzanCard(
    homeState: HomeUiState,
    prefs: UserPrefsState,
    expanded: Boolean,
    onToggleExpand: () -> Unit,
    onToggleAdzanMaster: (Boolean) -> Unit,
    onToggleAdzanPrayer: (String) -> Unit,
    onTogglePrayerDone: (String) -> Unit,
    onOpenLocationSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val s = com.example.ui.theme.LocalAppStrings.current
    val chevronRotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "adzan_chevron"
    )
    val schedule = homeState.prayerSchedule

    fun localizedWindow(raw: String): String = when (raw) {
        "Tahajjud Time" -> s.windowTahajjud
        "Fajr Time" -> s.windowFajr
        "Sunrise" -> s.windowSunrise
        "Dhuha Time" -> s.windowDhuha
        "Dhuhr Time" -> s.windowDhuhr
        "Asr Time" -> s.windowAsr
        "Maghrib Time" -> s.windowMaghrib
        "Isha Time" -> s.windowIsha
        else -> raw
    }

    fun localizedPrayer(raw: String): String = when (raw) {
        "Fajr" -> s.prayerFajr
        "Sunrise" -> s.prayerSunrise
        "Dhuhr" -> s.prayerDhuhr
        "Asr" -> s.prayerAsr
        "Maghrib" -> s.prayerMaghrib
        "Isha" -> s.prayerIsha
        else -> raw
    }

    NeumorphicCard(
        modifier = modifier,
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
                        text = homeState.combinedDateDisplay,
                        style = MaterialTheme.typography.bodyMedium,
                        color = StoneInkPrimary,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = homeState.clockDisplay,
                        fontFamily = BarlowCondensedFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 60.sp,
                        lineHeight = 62.sp,
                        color = StoneInkPrimary,
                        letterSpacing = (-0.5).sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = localizedWindow(schedule.currentWindowLabel),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        color = StoneInkPrimary
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
                            .testTag("adzan_dropdown_button"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = s.labelAdzan,
                                style = MaterialTheme.typography.titleMedium,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = StoneInkPrimary
                            )
                            Icon(
                                imageVector = Icons.Rounded.KeyboardArrowDown,
                                contentDescription = stringResource(R.string.cd_adzan_toggle),
                                tint = StoneInkPrimary,
                                modifier = Modifier
                                    .size(22.dp)
                                    .rotate(chevronRotation)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "${localizedPrayer(schedule.nextPrayerName)} ${schedule.nextPrayerTime}",
                        style = MaterialTheme.typography.labelMedium,
                        color = StoneInkSecondary
                    )
                    Text(
                        text = "-${schedule.countdownText}",
                        style = MaterialTheme.typography.labelSmall,
                        color = StoneAccent,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = StoneDivider, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable(onClick = onOpenLocationSettings)
                                .padding(vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.LocationOn,
                                contentDescription = null,
                                tint = StoneAccent,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = prefs.cityName,
                                style = MaterialTheme.typography.labelMedium,
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
                                checked = prefs.adzanNotificationsEnabled,
                                onCheckedChange = onToggleAdzanMaster,
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

                    val isSameDate = prefs.completedPrayersDate == homeState.todayKey
                    val completedToday = if (isSameDate) prefs.completedPrayersToday else emptySet()

                    val rows = listOf(
                        Triple("Fajr", schedule.subuh, true),
                        Triple("Sunrise", schedule.syuruq, false),
                        Triple("Dhuhr", schedule.dzuhur, true),
                        Triple("Asr", schedule.ashar, true),
                        Triple("Maghrib", schedule.maghrib, true),
                        Triple("Isha", schedule.isya, true)
                    )

                    rows.forEach { (keyName, time, isObligatory) ->
                        val isDone = keyName in completedToday
                        val isAlertOn = keyName in prefs.enabledPrayersForAdzan && prefs.adzanNotificationsEnabled
                        val isNext = schedule.nextPrayerName == keyName

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isNext) StoneAccent.copy(alpha = 0.12f) else Color.Transparent)
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (isObligatory) {
                                IconButton(
                                    onClick = { onTogglePrayerDone(keyName) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isDone) Icons.Rounded.CheckCircle else Icons.Rounded.RadioButtonUnchecked,
                                        contentDescription = "Mark $keyName completed",
                                        tint = if (isDone) StoneSuccess else StoneInkSecondary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            } else {
                                Spacer(modifier = Modifier.size(32.dp))
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            Text(
                                text = localizedPrayer(keyName),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (isNext) FontWeight.Bold else FontWeight.SemiBold,
                                color = StoneInkPrimary,
                                modifier = Modifier.weight(1f)
                            )

                            Text(
                                text = time,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isNext) StoneAccent else StoneInkPrimary
                            )

                            if (isObligatory) {
                                Spacer(modifier = Modifier.width(8.dp))
                                IconButton(
                                    onClick = { onToggleAdzanPrayer(keyName) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isAlertOn) Icons.Outlined.Notifications else Icons.Outlined.NotificationsOff,
                                        contentDescription = "Toggle adzan for $keyName",
                                        tint = if (isAlertOn) StoneAccent else StoneInkSecondary.copy(alpha = 0.55f),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            } else {
                                Spacer(modifier = Modifier.width(40.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    NeumorphicPillButton(
                        text = s.changeCityMethod,
                        onClick = onOpenLocationSettings,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun FeatureMenuGrid(
    onOpenQuran: () -> Unit,
    onOpenDua: () -> Unit,
    onOpenDzikir: () -> Unit,
    onOpenGroups: () -> Unit,
    onOpenTracker: () -> Unit,
    onOpenMore: () -> Unit
) {
    val s = com.example.ui.theme.LocalAppStrings.current
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            MenuGridItem(
                label = s.menuQuran,
                testTag = "menu_alquran",
                onClick = onOpenQuran,
                modifier = Modifier.weight(1f)
            ) {
                QuranStandIcon(modifier = Modifier.size(32.dp))
            }
            MenuGridItem(
                label = s.menuDua,
                testTag = "menu_doa",
                onClick = onOpenDua,
                modifier = Modifier.weight(1f)
            ) {
                DuaPrayingIcon(modifier = Modifier.size(32.dp))
            }
            MenuGridItem(
                label = s.menuDzikir,
                testTag = "menu_dzikir",
                onClick = onOpenDzikir,
                modifier = Modifier.weight(1f)
            ) {
                DzikirTasbihIcon(modifier = Modifier.size(32.dp))
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            MenuGridItem(
                label = s.menuGroups,
                testTag = "menu_groups",
                onClick = onOpenGroups,
                modifier = Modifier.weight(1f)
            ) {
                GroupsIcon(modifier = Modifier.size(32.dp))
            }
            MenuGridItem(
                label = s.menuTracker,
                testTag = "menu_tracker",
                onClick = onOpenTracker,
                modifier = Modifier.weight(1f)
            ) {
                TrackerChecklistIcon(modifier = Modifier.size(32.dp))
            }
            MenuGridItem(
                label = s.menuMore,
                testTag = "menu_selengkapnya",
                onClick = onOpenMore,
                modifier = Modifier.weight(1f)
            ) {
                SelengkapnyaGridIcon(modifier = Modifier.size(28.dp))
            }
        }
    }
}

@Composable
private fun MenuGridItem(
    label: String,
    testTag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit
) {
    Column(
        modifier = modifier.padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        NeumorphicCircleButton(
            onClick = onClick,
            size = 62.dp,
            modifier = Modifier.testTag(testTag)
        ) {
            icon()
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            lineHeight = 15.sp,
            color = StoneInkPrimary,
            textAlign = TextAlign.Center,
            maxLines = 2
        )
    }
}

@Composable
private fun AyatHariIniCard(
    homeState: HomeUiState,
    isBookmarked: Boolean,
    onCardClick: () -> Unit,
    onRefreshClick: () -> Unit,
    onBookmarkClick: () -> Unit
) {
    val s = com.example.ui.theme.LocalAppStrings.current
    val ayah = homeState.dailyAyah

    NeumorphicCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("ayat_hari_ini_card"),
        cornerRadius = 24.dp,
        shadowElevation = 8.dp,
        onClick = onCardClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AyatBookIcon(modifier = Modifier.size(38.dp))
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = s.ayatOfDay,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = StoneInkPrimary
                    )
                    Text(
                        text = "(${ayah.surahName})",
                        style = MaterialTheme.typography.labelMedium,
                        color = StoneInkSecondary
                    )
                }
                IconButton(
                    onClick = onBookmarkClick,
                    modifier = Modifier.testTag("bookmark_daily_ayah")
                ) {
                    Icon(
                        imageVector = if (isBookmarked) Icons.Rounded.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = stringResource(R.string.cd_bookmark_ayat),
                        tint = if (isBookmarked) StoneAccent else StoneInkSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                IconButton(
                    onClick = onRefreshClick,
                    modifier = Modifier.testTag("refresh_daily_ayah")
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Refresh,
                        contentDescription = stringResource(R.string.cd_refresh_ayat),
                        tint = StoneInkSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = ayah.arabicText,
                fontFamily = AmiriArabicFamily,
                fontSize = 25.sp,
                lineHeight = 40.sp,
                color = StoneInkPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            val displayedTranslation = if (s.isBangla && ayah.translationBn.isNotBlank()) {
                ayah.translationBn
            } else {
                ayah.translation
            }
            Text(
                text = "\"$displayedTranslation\"",
                style = MaterialTheme.typography.bodyMedium,
                color = StoneInkPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
