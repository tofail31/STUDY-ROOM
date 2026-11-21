package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ExitToApp
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PersonAdd
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.calc.PrayerTimeCalculator
import com.example.data.model.GroupMember
import com.example.data.model.StudyGroup
import com.example.data.model.StudyStatus
import com.example.ui.components.NeumorphicCard
import com.example.ui.components.NeumorphicCircleButton
import com.example.ui.components.NeumorphicPillButton
import com.example.ui.components.StudyTableIllustration
import com.example.ui.theme.BarlowCondensedFamily
import com.example.ui.theme.LocalAppStrings
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
import com.example.ui.viewmodel.StudyRoomUiState
import kotlinx.coroutines.launch

@Composable
fun GroupStudyRoomScreen(
    groupId: String,
    userName: String,
    studyState: StudyRoomUiState,
    onBack: () -> Unit,
    onToggleStudying: () -> Unit,
    onSelectSubject: (String) -> Unit,
    onCheerMember: (String) -> Unit,
    onLeaveGroup: () -> Unit,
    onSwitchGroup: (String) -> Unit = {},
    onBrowseAllGroups: () -> Unit = {}
) {
    val s = LocalAppStrings.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var currentGroupId by remember(groupId) { mutableStateOf(groupId) }
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = 30-Seat Grid, 1 = Ranking, 2 = Rules
    var selectedMemberId by remember { mutableStateOf<String?>(null) }
    var isSearchOpen by remember { mutableStateOf(false) }
    var roomQuery by remember { mutableStateOf("") }
    val gridState = rememberLazyGridState()
    val isScrolledPastHero by remember {
        derivedStateOf { gridState.firstVisibleItemIndex > 0 || gridState.firstVisibleItemScrollOffset > 120 }
    }

    val group: StudyGroup = remember(currentGroupId, studyState.groups) {
        studyState.groups.firstOrNull { it.id == currentGroupId } ?: studyState.groups.first()
    }

    val enteredGroups: List<StudyGroup> = remember(studyState.groups, studyState.joinedGroupIds, group.id) {
        val joined = studyState.groups.filter { it.id in studyState.joinedGroupIds }
        if (joined.any { it.id == group.id }) joined else listOf(group) + joined
    }

    val userInitials = remember(userName) {
        userName.trim().split(" ").filter { it.isNotBlank() }.take(2)
            .joinToString("") { it.first().uppercaseChar().toString() }.ifBlank { "ME" }
    }

    // Build combined roster (User + up to 29 peers = max 30 members per group)
    val userMember = remember(userName, studyState.currentSubject, studyState.todayStudySeconds, studyState.isStudying) {
        GroupMember(
            id = "current_user",
            name = "${s.youLabel} ($userName)",
            initials = userInitials,
            subjectEn = studyState.currentSubject,
            subjectBn = studyState.currentSubject,
            todayStudySeconds = studyState.todayStudySeconds,
            status = if (studyState.isStudying) StudyStatus.STUDYING else StudyStatus.RESTING,
            streakDays = 7,
            isCurrentUser = true
        )
    }

    val activeRoster = remember(group.members, userMember) {
        val peers = group.members.take((group.maxMembers - 1).coerceAtLeast(0))
        listOf(userMember) + peers
    }

    val sortedForGrid = remember(activeRoster) {
        val user = activeRoster.filter { it.isCurrentUser }
        val studying = activeRoster.filter { !it.isCurrentUser && it.status == StudyStatus.STUDYING }
            .sortedByDescending { it.todayStudySeconds }
        val resting = activeRoster.filter { !it.isCurrentUser && it.status == StudyStatus.RESTING }
            .sortedByDescending { it.todayStudySeconds }
        val offline = activeRoster.filter { !it.isCurrentUser && it.status == StudyStatus.OFFLINE }
        user + studying + resting + offline
    }

    val rankedMembers = remember(activeRoster) {
        activeRoster.sortedByDescending { it.todayStudySeconds }
    }
    val userRank = (rankedMembers.indexOfFirst { it.isCurrentUser } + 1).coerceAtLeast(1)

    val studyingCount = activeRoster.count { it.status == StudyStatus.STUDYING }
    val restingCount = activeRoster.count { it.status == StudyStatus.RESTING }
    val openSeatsCount = (group.maxMembers - activeRoster.size).coerceAtLeast(0)

    val groupSubjects = remember(group.id, s.isBangla) {
        group.members.map { if (s.isBangla) it.subjectBn else it.subjectEn }.distinct()
    }

    val filteredGridMembers = remember(sortedForGrid, roomQuery) {
        if (roomQuery.isBlank()) sortedForGrid
        else sortedForGrid.filter { m ->
            m.name.contains(roomQuery, ignoreCase = true) ||
                m.subjectEn.contains(roomQuery, ignoreCase = true) ||
                m.subjectBn.contains(roomQuery, ignoreCase = true)
        }
    }

    val filteredRankedMembers = remember(rankedMembers, roomQuery) {
        if (roomQuery.isBlank()) rankedMembers
        else rankedMembers.filter { m ->
            m.name.contains(roomQuery, ignoreCase = true) ||
                m.subjectEn.contains(roomQuery, ignoreCase = true) ||
                m.subjectBn.contains(roomQuery, ignoreCase = true)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(StoneBackgroundTop, StoneBackground, StoneBackgroundBottom))
            )
            .statusBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            val occupiedStr = if (s.isBangla) {
                PrayerTimeCalculator.toBanglaDigits("${activeRoster.size}/${group.maxMembers}")
            } else {
                "${activeRoster.size}/${group.maxMembers}"
            }
            val studyingStr = if (s.isBangla) {
                PrayerTimeCalculator.toBanglaDigits(studyingCount.toString())
            } else {
                studyingCount.toString()
            }
            val liveTimerStr = formatStudyHms(studyState.todayStudySeconds, s.isBangla)

            // Compact Always-On Top Navigation Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                NeumorphicCircleButton(
                    onClick = onBack,
                    size = 42.dp,
                    modifier = Modifier.testTag("back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = stringResource(R.string.cd_back),
                        tint = StoneInkPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (s.isBangla) group.nameBn else group.nameEn,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = StoneInkPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "$occupiedStr ${s.membersLabel} • $studyingStr ${s.studyingNowLabel}",
                        style = MaterialTheme.typography.labelSmall,
                        color = StoneInkSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                NeumorphicCircleButton(
                    onClick = {
                        if (isSearchOpen) {
                            roomQuery = ""
                            isSearchOpen = false
                        } else {
                            isSearchOpen = true
                        }
                    },
                    size = 40.dp,
                    modifier = Modifier.testTag("room_search_toggle")
                ) {
                    Icon(
                        imageVector = if (isSearchOpen) Icons.Rounded.Close else Icons.Rounded.Search,
                        contentDescription = s.roomSearchHint,
                        tint = StoneInkPrimary,
                        modifier = Modifier.size(19.dp)
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                NeumorphicCircleButton(
                    onClick = onLeaveGroup,
                    size = 40.dp,
                    modifier = Modifier.testTag("leave_group_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ExitToApp,
                        contentDescription = s.leaveGroupButton,
                        tint = StoneAccent,
                        modifier = Modifier.size(19.dp)
                    )
                }
            }

            AnimatedVisibility(visible = isSearchOpen) {
                OutlinedTextField(
                    value = roomQuery,
                    onValueChange = { roomQuery = it },
                    placeholder = { Text(s.roomSearchHint) },
                    leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null, tint = StoneInkSecondary) },
                    trailingIcon = {
                        if (roomQuery.isNotEmpty()) {
                            IconButton(onClick = { roomQuery = "" }) {
                                Icon(Icons.Rounded.Close, contentDescription = null, tint = StoneInkSecondary)
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(18.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = StoneAccent,
                        unfocusedBorderColor = StoneDivider,
                        focusedContainerColor = StoneSurface,
                        unfocusedContainerColor = StoneSurface,
                        focusedTextColor = StoneInkPrimary,
                        unfocusedTextColor = StoneInkPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 8.dp)
                        .testTag("room_search_input")
                )
            }

            // Always-accessible Top Entered Groups Bar — tap any group to see its studying students
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .testTag("live_room_groups_switcher")
            ) {
                items(enteredGroups, key = { it.id }) { grp ->
                    val isSel = grp.id == group.id
                    val grpStudying = grp.members.count { it.status == StudyStatus.STUDYING } +
                        if (studyState.isStudying && isSel) 1 else 0
                    val countLabel = if (s.isBangla) {
                        PrayerTimeCalculator.toBanglaDigits(grpStudying.toString())
                    } else {
                        grpStudying.toString()
                    }
                    NeumorphicPillButton(
                        text = "${if (s.isBangla) grp.nameBn else grp.nameEn} ($countLabel)",
                        selected = isSel,
                        leadingContent = {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(if (isSel) StoneSuccess else StoneAccent)
                            )
                        },
                        onClick = {
                            currentGroupId = grp.id
                            onSwitchGroup(grp.id)
                        },
                        modifier = Modifier.testTag("room_group_chip_${grp.id}")
                    )
                }
                item(key = "browse_more_groups") {
                    NeumorphicPillButton(
                        text = "+ ${s.allGroupsTab}",
                        selected = false,
                        onClick = onBrowseAllGroups,
                        modifier = Modifier.testTag("room_browse_groups")
                    )
                }
            }

            AnimatedVisibility(
                visible = isScrolledPastHero,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                NeumorphicCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 8.dp),
                    cornerRadius = 16.dp,
                    shadowElevation = 5.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (studyState.isStudying) StoneSuccess else StoneAccent)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = liveTimerStr,
                            fontFamily = BarlowCondensedFamily,
                            fontSize = 26.sp,
                            lineHeight = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (studyState.isStudying) StoneSuccess else StoneInkPrimary,
                            modifier = Modifier.testTag("compact_top_timer")
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = studyState.currentSubject,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = StoneInkPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = if (studyState.isStudying) s.statusStudying else s.statusResting,
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (studyState.isStudying) StoneSuccess else StoneAccent,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        NeumorphicPillButton(
                            text = if (studyState.isStudying) s.pauseStudying else s.startStudying,
                            selected = true,
                            leadingContent = {
                                Icon(
                                    imageVector = if (studyState.isStudying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                                    contentDescription = null,
                                    tint = if (studyState.isStudying) StoneSuccess else StoneInkPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            onClick = onToggleStudying,
                            modifier = Modifier.testTag("compact_top_timer_toggle")
                        )
                    }
                }
            }

            if (isScrolledPastHero) {
                HorizontalDivider(color = StoneDivider.copy(alpha = 0.75f))
            }

            // Unified Scrollable Page Content (Hero Timer, Tabs & 30-Seat Grid / Ranking / Rules scroll seamlessly)
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                val columns = if (maxWidth >= 600.dp) 4 else 3
                val seatIndices = (0 until group.maxMembers).toList()

                LazyVerticalGrid(
                    state = gridState,
                    columns = GridCells.Fixed(columns),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 6.dp, bottom = 32.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("study_room_grid")
                ) {
                    // Scrollable Hero Live Timer Card
                    item(key = "hero_timer_card", span = { GridItemSpan(maxLineSpan) }) {
                        NeumorphicCard(
                            modifier = Modifier.fillMaxWidth(),
                            cornerRadius = 22.dp,
                            shadowElevation = 7.dp
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = studyState.currentSubject,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = StoneAccent,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(
                                                (if (studyState.isStudying) StoneSuccess else StoneAccent).copy(alpha = 0.16f)
                                            )
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = if (studyState.isStudying) s.statusStudying else s.statusResting,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = if (studyState.isStudying) StoneSuccess else StoneAccent
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = liveTimerStr,
                                            fontFamily = BarlowCondensedFamily,
                                            fontSize = 46.sp,
                                            lineHeight = 48.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (studyState.isStudying) StoneSuccess else StoneInkPrimary,
                                            modifier = Modifier.testTag("user_study_timer")
                                        )
                                        val rankText = if (s.isBangla) {
                                            PrayerTimeCalculator.toBanglaDigits("#$userRank / ${activeRoster.size}")
                                        } else {
                                            "#$userRank / ${activeRoster.size}"
                                        }
                                        Text(
                                            text = "${s.todayTotalLabel} • $rankText",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = StoneInkSecondary
                                        )
                                    }

                                    NeumorphicPillButton(
                                        text = if (studyState.isStudying) s.pauseStudying else s.startStudying,
                                        selected = true,
                                        leadingContent = {
                                            Icon(
                                                imageVector = if (studyState.isStudying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                                                contentDescription = null,
                                                tint = StoneInkPrimary,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        },
                                        onClick = onToggleStudying,
                                        modifier = Modifier.testTag("toggle_study_timer")
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    items(groupSubjects) { subj ->
                                        NeumorphicPillButton(
                                            text = subj,
                                            selected = studyState.currentSubject == subj,
                                            onClick = { onSelectSubject(subj) }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Scrollable View Switcher Tabs
                    item(key = "room_tabs", span = { GridItemSpan(maxLineSpan) }) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 2.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            NeumorphicPillButton(
                                text = s.tabStudyGrid,
                                selected = selectedTab == 0,
                                onClick = { selectedTab = 0 },
                                modifier = Modifier.weight(1f).testTag("tab_study_grid")
                            )
                            NeumorphicPillButton(
                                text = s.tabRanking,
                                selected = selectedTab == 1,
                                onClick = { selectedTab = 1 },
                                modifier = Modifier.weight(1f).testTag("tab_ranking")
                            )
                            NeumorphicPillButton(
                                text = s.tabRulesInfo,
                                selected = selectedTab == 2,
                                onClick = { selectedTab = 2 },
                                modifier = Modifier.weight(1f).testTag("tab_rules")
                            )
                        }
                    }

                    when (selectedTab) {
                        0 -> {
                            item(key = "grid_legend", span = { GridItemSpan(maxLineSpan) }) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 4.dp, vertical = 2.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val studyingBadge = if (s.isBangla) {
                                        "${s.statusStudying}: ${PrayerTimeCalculator.toBanglaDigits(studyingCount.toString())}"
                                    } else {
                                        "${s.statusStudying}: $studyingCount"
                                    }
                                    val restingBadge = if (s.isBangla) {
                                        "${s.statusResting}: ${PrayerTimeCalculator.toBanglaDigits(restingCount.toString())}"
                                    } else {
                                        "${s.statusResting}: $restingCount"
                                    }
                                    val seatBadge = if (s.isBangla) {
                                        PrayerTimeCalculator.toBanglaDigits("${activeRoster.size}/30")
                                    } else {
                                        "${activeRoster.size}/30"
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(StoneSuccess))
                                        Spacer(modifier = Modifier.width(5.dp))
                                        Text(studyingBadge, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = StoneSuccess)
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(StoneAccent))
                                        Spacer(modifier = Modifier.width(5.dp))
                                        Text(restingBadge, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = StoneAccent)
                                    }
                                    Text(
                                        text = seatBadge,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = StoneInkSecondary
                                    )
                                }
                            }

                            if (roomQuery.isNotBlank()) {
                                items(filteredGridMembers, key = { it.id }) { member ->
                                    val cheered = member.id in studyState.cheeredMemberIds
                                    YptStudentSeatTile(
                                        member = member,
                                        isBangla = s.isBangla,
                                        cheered = cheered,
                                        onClick = { selectedMemberId = member.id }
                                    )
                                }
                            } else {
                                items(seatIndices, key = { it }) { seatIdx ->
                                    val member = sortedForGrid.getOrNull(seatIdx)
                                    if (member != null) {
                                        val cheered = member.id in studyState.cheeredMemberIds
                                        YptStudentSeatTile(
                                            member = member,
                                            isBangla = s.isBangla,
                                            cheered = cheered,
                                            onClick = { selectedMemberId = member.id }
                                        )
                                    } else {
                                        EmptySeatTile(
                                            seatNumber = seatIdx + 1,
                                            openLabel = s.openSeatLabel,
                                            isBangla = s.isBangla
                                        )
                                    }
                                }
                            }
                        }

                        1 -> {
                            itemsIndexed(
                                items = filteredRankedMembers,
                                key = { _, m -> "rank_${m.id}" },
                                span = { _, _ -> GridItemSpan(maxLineSpan) }
                            ) { idx, member ->
                                val rankStr = if (s.isBangla) PrayerTimeCalculator.toBanglaDigits("#${idx + 1}") else "#${idx + 1}"
                                val streakStr = if (s.isBangla) PrayerTimeCalculator.toBanglaDigits("${member.streakDays}d") else "${member.streakDays}d"
                                val statusColor = when (member.status) {
                                    StudyStatus.STUDYING -> StoneSuccess
                                    StudyStatus.RESTING -> StoneAccent
                                    StudyStatus.OFFLINE -> StoneInkSecondary
                                }

                                NeumorphicCard(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("ranking_member_${member.id}"),
                                    cornerRadius = 18.dp,
                                    shadowElevation = 5.dp,
                                    onClick = { selectedMemberId = member.id }
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 14.dp, vertical = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = rankStr,
                                            fontFamily = BarlowCondensedFamily,
                                            fontSize = 22.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (idx < 3) StoneAccent else StoneInkSecondary,
                                            modifier = Modifier.width(34.dp)
                                        )

                                        Box(
                                            modifier = Modifier
                                                .size(38.dp)
                                                .clip(CircleShape)
                                                .background(statusColor.copy(alpha = 0.16f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = member.initials,
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = statusColor
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = member.name,
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = if (member.isCurrentUser) FontWeight.Bold else FontWeight.SemiBold,
                                                color = StoneInkPrimary,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = "${if (s.isBangla) member.subjectBn else member.subjectEn} • 🔥 $streakStr",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = StoneInkSecondary,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }

                                        Text(
                                            text = formatStudyHms(member.todayStudySeconds, s.isBangla),
                                            fontFamily = BarlowCondensedFamily,
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = statusColor
                                        )
                                    }
                                }
                            }
                        }

                        else -> {
                            item(key = "group_desc_card", span = { GridItemSpan(maxLineSpan) }) {
                                NeumorphicCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 20.dp) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(s.groupDescriptionTitle, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = StoneInkPrimary)
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = if (s.isBangla) group.descriptionBn else group.descriptionEn,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = StoneInkSecondary
                                        )
                                    }
                                }
                            }
                            item(key = "group_rules_card", span = { GridItemSpan(maxLineSpan) }) {
                                NeumorphicCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 20.dp) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(s.groupRulesTitle, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = StoneInkPrimary)
                                        Spacer(modifier = Modifier.height(8.dp))
                                        val rules = if (s.isBangla) group.rulesBn else group.rulesEn
                                        rules.forEachIndexed { idx, r ->
                                            val num = if (s.isBangla) PrayerTimeCalculator.toBanglaDigits("${idx + 1}.") else "${idx + 1}."
                                            Row(modifier = Modifier.padding(vertical = 3.dp)) {
                                                Text(num, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = StoneAccent, modifier = Modifier.width(22.dp))
                                                Text(r, style = MaterialTheme.typography.bodyMedium, color = StoneInkPrimary)
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

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .windowInsetsPadding(WindowInsets.navigationBars)
        )

        val selectedMember = selectedMemberId?.let { id ->
            activeRoster.firstOrNull { it.id == id }
        }
        if (selectedMember != null) {
            StudentDetailBottomSheet(
                member = selectedMember,
                group = group,
                isCheered = selectedMember.id in studyState.cheeredMemberIds,
                onCheer = {
                    if (!selectedMember.isCurrentUser) {
                        onCheerMember(selectedMember.id)
                        scope.launch {
                            snackbarHostState.showSnackbar("${s.cheerSentPrefix} ${selectedMember.name}")
                        }
                    }
                },
                onDismiss = { selectedMemberId = null }
            )
        }
    }
}

@Composable
private fun YptStudentSeatTile(
    member: GroupMember,
    isBangla: Boolean,
    cheered: Boolean,
    onClick: () -> Unit
) {
    val statusColor = when (member.status) {
        StudyStatus.STUDYING -> StoneSuccess
        StudyStatus.RESTING -> StoneAccent
        StudyStatus.OFFLINE -> StoneInkSecondary
    }

    NeumorphicCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(166.dp)
            .testTag("student_tile_${member.id}")
            .let {
                if (member.isCurrentUser) {
                    it.border(1.5.dp, StoneAccent, RoundedCornerShape(18.dp))
                } else it
            },
        cornerRadius = 18.dp,
        shadowElevation = if (member.status == StudyStatus.STUDYING) 7.dp else 3.dp,
        onClick = onClick
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Full-box Study Table illustration filling the entire student card
            StudyTableIllustration(
                status = member.status,
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("student_desk_${member.id}")
                    .semantics {
                        contentDescription = member.name
                    }
            )

            // Top status dot + streak / cheer overlay
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(horizontal = 8.dp, vertical = 7.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(statusColor)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(StoneSurface.copy(alpha = 0.78f))
                        .padding(horizontal = 5.dp, vertical = 1.dp)
                ) {
                    if (cheered) {
                        Icon(
                            imageVector = Icons.Rounded.Favorite,
                            contentDescription = null,
                            tint = StoneAccent,
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                    }
                    Icon(
                        imageVector = Icons.Rounded.LocalFireDepartment,
                        contentDescription = null,
                        tint = StoneAccent,
                        modifier = Modifier.size(11.dp)
                    )
                    Text(
                        text = if (isBangla) PrayerTimeCalculator.toBanglaDigits(member.streakDays.toString()) else member.streakDays.toString(),
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp,
                        color = StoneInkSecondary
                    )
                }
            }

            // Bottom frosted info footer over the studio floor
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                StoneSurface.copy(alpha = 0.88f),
                                StoneSurface.copy(alpha = 0.96f)
                            )
                        )
                    )
                    .padding(start = 6.dp, end = 6.dp, top = 12.dp, bottom = 7.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = member.name,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = if (member.isCurrentUser) FontWeight.Bold else FontWeight.SemiBold,
                    color = StoneInkPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = if (isBangla) member.subjectBn else member.subjectEn,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 10.sp,
                    color = StoneInkSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = formatStudyHms(member.todayStudySeconds, isBangla),
                    fontFamily = BarlowCondensedFamily,
                    fontSize = 18.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (member.status == StudyStatus.STUDYING) StoneSuccess else StoneInkPrimary,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun EmptySeatTile(
    seatNumber: Int,
    openLabel: String,
    isBangla: Boolean
) {
    val num = if (isBangla) PrayerTimeCalculator.toBanglaDigits(seatNumber.toString()) else seatNumber.toString()
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(166.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(StonePanel.copy(alpha = 0.55f))
            .border(1.dp, StoneDivider, RoundedCornerShape(18.dp))
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Rounded.PersonAdd,
                contentDescription = null,
                tint = StoneInkSecondary.copy(alpha = 0.6f),
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "#$num",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = StoneInkSecondary
            )
            Text(
                text = openLabel,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 10.sp,
                color = StoneInkSecondary.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )
        }
    }
}
