package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Login
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material.icons.rounded.WorkspacePremium
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.calc.PrayerTimeCalculator
import com.example.data.model.OrganizerRole
import com.example.data.model.StudyGroup
import com.example.ui.components.NeumorphicCard
import com.example.ui.components.NeumorphicCircleButton
import com.example.ui.components.NeumorphicPillButton
import com.example.ui.theme.LocalAppStrings
import com.example.ui.theme.StoneAccent
import com.example.ui.theme.StoneBackground
import com.example.ui.theme.StoneBackgroundBottom
import com.example.ui.theme.StoneBackgroundTop
import com.example.ui.theme.StoneDivider
import com.example.ui.theme.StoneInkPrimary
import com.example.ui.theme.StoneInkSecondary
import com.example.ui.theme.StoneSuccess
import com.example.ui.theme.StoneSurface
import com.example.ui.viewmodel.StudyRoomUiState
import java.util.Locale

internal fun formatStudyHms(totalSeconds: Long, isBangla: Boolean = false): String {
    val safe = totalSeconds.coerceAtLeast(0L)
    val h = safe / 3600L
    val m = (safe % 3600L) / 60L
    val sec = safe % 60L
    val raw = String.format(Locale.US, "%02d:%02d:%02d", h, m, sec)
    return if (isBangla) PrayerTimeCalculator.toBanglaDigits(raw) else raw
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupsScreen(
    studyState: StudyRoomUiState,
    initialMyGroupsOnly: Boolean = false,
    onBack: () -> Unit,
    onEnterGroupRoom: (groupId: String, subject: String) -> Unit
) {
    val s = LocalAppStrings.current
    var query by remember { mutableStateOf("") }
    var isSearchOpen by remember { mutableStateOf(false) }
    var showMyGroupsOnly by remember(initialMyGroupsOnly) { mutableStateOf(initialMyGroupsOnly) }
    var selectedCategory by remember { mutableStateOf("ALL") }
    var previewGroupId by remember { mutableStateOf<String?>(null) }
    val listState = rememberLazyListState()
    val isScrolled by remember {
        derivedStateOf { listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 8 }
    }

    val categories = remember(s.isBangla) {
        listOf(
            "ALL" to s.filterAll,
            "Qur'an & Hifz" to if (s.isBangla) "কুরআন ও হিফজ" else "Qur'an & Hifz",
            "BCS & Varsity" to if (s.isBangla) "বিসিএস ও ভর্তি" else "BCS & Varsity",
            "SSC / HSC" to if (s.isBangla) "এসএসসি / এইচএসসি" else "SSC / HSC",
            "Tech & Coding" to if (s.isBangla) "টেক ও কোডিং" else "Tech & Coding"
        )
    }

    val myGroupsCount = remember(studyState.groups, studyState.joinedGroupIds) {
        studyState.groups.count { it.id in studyState.joinedGroupIds }
    }

    val filteredGroups = remember(query, selectedCategory, showMyGroupsOnly, studyState.groups, studyState.joinedGroupIds) {
        studyState.groups.filter { grp ->
            val matchesJoined = !showMyGroupsOnly || grp.id in studyState.joinedGroupIds
            val matchesCat = selectedCategory == "ALL" || grp.category == selectedCategory
            val matchesQuery = query.isBlank() ||
                grp.nameEn.contains(query, ignoreCase = true) ||
                grp.nameBn.contains(query, ignoreCase = true) ||
                grp.city.contains(query, ignoreCase = true) ||
                grp.category.contains(query, ignoreCase = true) ||
                grp.descriptionEn.contains(query, ignoreCase = true)
            matchesJoined && matchesCat && matchesQuery
        }
    }

    val previewGroup = remember(previewGroupId, studyState.groups) {
        previewGroupId?.let { id -> studyState.groups.firstOrNull { it.id == id } }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(StoneBackgroundTop, StoneBackground, StoneBackgroundBottom))
            )
            .statusBarsPadding()
    ) {
        // Compact always-present top navigation bar with top-right Search icon
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 8.dp),
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
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (showMyGroupsOnly) s.myGroupsTab else s.groupsTitle,
                    style = MaterialTheme.typography.titleLarge,
                    color = StoneInkPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = s.groupsSubtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = StoneInkSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            NeumorphicCircleButton(
                onClick = {
                    if (isSearchOpen) {
                        query = ""
                        isSearchOpen = false
                    } else {
                        isSearchOpen = true
                    }
                },
                size = 42.dp,
                modifier = Modifier.testTag("groups_search_toggle")
            ) {
                Icon(
                    imageVector = if (isSearchOpen) Icons.Rounded.Close else Icons.Rounded.Search,
                    contentDescription = s.groupsSearchHint,
                    tint = StoneInkPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        AnimatedVisibility(visible = isSearchOpen) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text(s.groupsSearchHint) },
                leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null, tint = StoneInkSecondary) },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { query = "" }) {
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
                    .padding(horizontal = 18.dp)
                    .padding(bottom = 8.dp)
                    .testTag("groups_search_input")
            )
        }

        if (isScrolled) {
            HorizontalDivider(color = StoneDivider.copy(alpha = 0.75f))
        }

        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 6.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.navigationBars)
                .testTag("groups_list")
        ) {
            item(key = "my_all_tabs") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    NeumorphicPillButton(
                        text = "${s.myGroupsTab} ($myGroupsCount)",
                        selected = showMyGroupsOnly,
                        onClick = { showMyGroupsOnly = true },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("tab_my_groups")
                    )
                    NeumorphicPillButton(
                        text = "${s.allGroupsTab} (${studyState.groups.size})",
                        selected = !showMyGroupsOnly,
                        onClick = { showMyGroupsOnly = false },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("tab_all_groups")
                    )
                }
            }

            item(key = "category_filters") {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(categories) { (key, label) ->
                        NeumorphicPillButton(
                            text = label,
                            selected = selectedCategory == key,
                            onClick = { selectedCategory = key }
                        )
                    }
                }
            }

            if (filteredGroups.isEmpty()) {
                item(key = "empty_state") {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp, horizontal = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = if (showMyGroupsOnly) s.myGroupsEmpty else s.groupsEmpty,
                            style = MaterialTheme.typography.bodyLarge,
                            color = StoneInkSecondary,
                            textAlign = TextAlign.Center
                        )
                        if (showMyGroupsOnly) {
                            NeumorphicPillButton(
                                text = s.browseAllGroups,
                                selected = true,
                                onClick = { showMyGroupsOnly = false }
                            )
                        }
                    }
                }
            } else {
                items(filteredGroups, key = { it.id }) { group ->
                    val isJoined = group.id in studyState.joinedGroupIds || studyState.activeGroupId == group.id
                    val occupiedCount = (group.members.size + if (isJoined) 1 else 0).coerceAtMost(group.maxMembers)
                    val seatsOpen = (group.maxMembers - occupiedCount).coerceAtLeast(0)

                    val occupiedStr = if (s.isBangla) PrayerTimeCalculator.toBanglaDigits("$occupiedCount/${group.maxMembers}") else "$occupiedCount/${group.maxMembers}"
                    val seatsStr = if (s.isBangla) PrayerTimeCalculator.toBanglaDigits(seatsOpen.toString()) else seatsOpen.toString()
                    val rawRating = String.format(Locale.US, "%.1f", group.rating)
                    val ratingStr = if (s.isBangla) PrayerTimeCalculator.toBanglaDigits(rawRating) else rawRating

                    val categoryLabel = categories.firstOrNull { it.first == group.category }?.second ?: group.category

                    NeumorphicCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("group_card_${group.id}"),
                        cornerRadius = 22.dp
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f, fill = false)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(StoneSuccess.copy(alpha = 0.14f))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Rounded.Verified,
                                            contentDescription = null,
                                            tint = StoneSuccess,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(5.dp))
                                        val verifiedText = if (s.isBangla) {
                                            "যাচাইকৃত • ${group.badgeTextBn}"
                                        } else {
                                            "Verified • ${group.badgeTextEn}"
                                        }
                                        Text(
                                            text = verifiedText,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = StoneSuccess,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Rounded.Star, contentDescription = null, tint = StoneAccent, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = ratingStr,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = StoneInkPrimary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = if (s.isBangla) group.nameBn else group.nameEn,
                                style = MaterialTheme.typography.titleMedium,
                                color = StoneInkPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.AutoMirrored.Rounded.MenuBook, contentDescription = null, tint = StoneInkSecondary, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = "$categoryLabel • ${group.city}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = StoneInkSecondary
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "$occupiedStr ${s.membersLabel} • $seatsStr ${s.seatsLeftLabel}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = StoneAccent,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.weight(1f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Spacer(modifier = Modifier.width(10.dp))

                                NeumorphicPillButton(
                                    text = s.enterButton,
                                    selected = true,
                                    leadingContent = {
                                        Icon(
                                            Icons.AutoMirrored.Rounded.Login,
                                            contentDescription = null,
                                            tint = StoneInkPrimary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    },
                                    onClick = {
                                        if (showMyGroupsOnly) {
                                            onEnterGroupRoom(group.id, studyState.currentSubject)
                                        } else {
                                            previewGroupId = group.id
                                        }
                                    },
                                    modifier = Modifier.testTag("enter_group_${group.id}")
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (previewGroup != null) {
        GroupEnterPreviewBottomSheet(
            group = previewGroup,
            defaultSubject = studyState.currentSubject,
            onDismiss = { previewGroupId = null },
            onConfirmEnter = { subject ->
                previewGroupId = null
                onEnterGroupRoom(previewGroup.id, subject)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GroupEnterPreviewBottomSheet(
    group: StudyGroup,
    defaultSubject: String,
    onDismiss: () -> Unit,
    onConfirmEnter: (String) -> Unit
) {
    val s = LocalAppStrings.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val groupSubjects = remember(group.id, s.isBangla) {
        val raw = group.members.map { if (s.isBangla) it.subjectBn else it.subjectEn }.distinct()
        if (raw.isEmpty()) listOf(defaultSubject) else raw
    }
    var selectedSubject by remember(group.id) {
        mutableStateOf(groupSubjects.firstOrNull() ?: defaultSubject)
    }

    val seatsTaken = group.members.size
    val occupiedLabel = if (s.isBangla) {
        PrayerTimeCalculator.toBanglaDigits("$seatsTaken/${group.maxMembers}")
    } else {
        "$seatsTaken/${group.maxMembers}"
    }
    val targetLabel = if (s.isBangla) {
        PrayerTimeCalculator.toBanglaDigits("${group.dailyTargetHours} ঘ./দিন")
    } else {
        "${group.dailyTargetHours}h/day"
    }

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
                .padding(horizontal = 20.dp)
                .padding(bottom = 20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = if (s.isBangla) group.nameBn else group.nameEn,
                    style = MaterialTheme.typography.titleLarge,
                    color = StoneInkPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$occupiedLabel ${s.membersLabel} • ${s.dailyTargetPrefix} $targetLabel",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = StoneAccent
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Compact Group Leader / Group Creator / Moderator Section
                NeumorphicCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("group_organizers_card"),
                    cornerRadius = 16.dp,
                    shadowElevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Text(
                            text = s.groupPreviewTitle,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = StoneInkSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        group.organizers.forEach { organizer ->
                            val roleLabel = when (organizer.role) {
                                OrganizerRole.CREATOR -> s.roleCreator
                                OrganizerRole.LEADER -> s.roleLeader
                                OrganizerRole.MODERATOR -> s.roleModerator
                            }
                            val roleColor = when (organizer.role) {
                                OrganizerRole.CREATOR -> StoneAccent
                                OrganizerRole.LEADER -> StoneSuccess
                                OrganizerRole.MODERATOR -> StoneInkPrimary
                            }
                            val roleIcon = when (organizer.role) {
                                OrganizerRole.CREATOR -> Icons.Rounded.WorkspacePremium
                                OrganizerRole.LEADER -> Icons.Rounded.Star
                                OrganizerRole.MODERATOR -> Icons.Rounded.Shield
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(26.dp)
                                        .clip(CircleShape)
                                        .background(roleColor.copy(alpha = 0.14f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = organizer.initials,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = roleColor
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Text(
                                    text = organizer.name,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = StoneInkPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )

                                Spacer(modifier = Modifier.width(6.dp))

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(roleColor.copy(alpha = 0.13f))
                                        .padding(horizontal = 7.dp, vertical = 2.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = roleIcon,
                                            contentDescription = null,
                                            tint = roleColor,
                                            modifier = Modifier.size(11.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = roleLabel,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = roleColor
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Description Card
                NeumorphicCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 18.dp, shadowElevation = 4.dp) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = s.groupDescriptionTitle,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = StoneInkPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (s.isBangla) group.descriptionBn else group.descriptionEn,
                            style = MaterialTheme.typography.bodyMedium,
                            color = StoneInkSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Rules Card
                NeumorphicCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 20.dp, shadowElevation = 5.dp) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = s.groupRulesTitle,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = StoneInkPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        val rules = if (s.isBangla) group.rulesBn else group.rulesEn
                        rules.forEachIndexed { idx, rule ->
                            val num = if (s.isBangla) PrayerTimeCalculator.toBanglaDigits("${idx + 1}.") else "${idx + 1}."
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text(
                                    text = num,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = StoneAccent,
                                    modifier = Modifier.width(22.dp)
                                )
                                Text(
                                    text = rule,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = StoneInkPrimary,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = s.chooseSubjectLabel,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = StoneInkSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(groupSubjects) { subj ->
                        NeumorphicPillButton(
                            text = subj,
                            selected = selectedSubject == subj,
                            onClick = { selectedSubject = subj }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            HorizontalDivider(color = StoneDivider)
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                NeumorphicPillButton(
                    text = s.cancelButton,
                    selected = false,
                    onClick = onDismiss
                )
                NeumorphicPillButton(
                    text = s.enterStudyRoomButton,
                    selected = true,
                    leadingContent = {
                        Icon(
                            Icons.AutoMirrored.Rounded.Login,
                            contentDescription = null,
                            tint = StoneInkPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    onClick = { onConfirmEnter(selectedSubject) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("confirm_enter_group")
                )
            }
        }
    }
}
