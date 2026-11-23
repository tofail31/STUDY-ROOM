package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.LockOpen
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Quiz
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.ExamGroundDefaults
import com.example.data.model.CustomPublishedExam
import com.example.data.model.ExamPaperStyle
import com.example.ui.components.NeumorphicCard
import com.example.ui.components.NeumorphicCircleButton
import com.example.ui.components.NeumorphicPillButton
import com.example.ui.theme.BarlowCondensedFamily
import com.example.ui.theme.LocalAppStrings
import com.example.ui.theme.NeumorphicHighlight
import com.example.ui.theme.StoneAccent
import com.example.ui.theme.StoneBackground
import com.example.ui.theme.StoneBackgroundBottom
import com.example.ui.theme.StoneBackgroundTop
import com.example.ui.theme.StoneDivider
import com.example.ui.theme.StoneInkPrimary
import com.example.ui.theme.StoneInkSecondary
import com.example.ui.theme.StonePanelInner
import com.example.ui.theme.StoneSuccess

private enum class GroundFilter { ALL, OPEN, LOCKED }

@Composable
fun ExamGroundScreen(
    userCreatedExams: List<CustomPublishedExam>,
    onBack: () -> Unit,
    onCreateExamClick: () -> Unit,
    onEnterExam: (CustomPublishedExam) -> Unit
) {
    val s = LocalAppStrings.current
    var searchQuery by remember { mutableStateOf("") }
    var filterMode by remember { mutableStateOf(GroundFilter.ALL) }
    var selectedEntryExam by remember { mutableStateOf<CustomPublishedExam?>(null) }

    val userExamIds = remember(userCreatedExams) { userCreatedExams.map { it.id }.toSet() }
    val allPublishedExams = remember(userCreatedExams) {
        userCreatedExams.filter { it.isPublished } + ExamGroundDefaults.defaultCommunityExams
    }

    val filteredExams = remember(allPublishedExams, searchQuery, filterMode) {
        val q = searchQuery.trim()
        allPublishedExams.filter { exam ->
            val matchesFilter = when (filterMode) {
                GroundFilter.ALL -> true
                GroundFilter.OPEN -> exam.password.isBlank()
                GroundFilter.LOCKED -> exam.password.isNotBlank()
            }
            val matchesQuery = q.isEmpty() ||
                exam.title.contains(q, ignoreCase = true) ||
                exam.subjectName.contains(q, ignoreCase = true) ||
                exam.chapterTitle.contains(q, ignoreCase = true) ||
                exam.authorName.contains(q, ignoreCase = true)
            matchesFilter && matchesQuery
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(StoneBackgroundTop, StoneBackground, StoneBackgroundBottom))
            )
            .statusBarsPadding()
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            NeumorphicCircleButton(
                onClick = onBack,
                size = 42.dp,
                modifier = Modifier.testTag("exam_ground_back_btn")
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
                    text = s.examGroundTitle,
                    style = MaterialTheme.typography.titleLarge,
                    color = StoneInkPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = s.examGroundSubtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = StoneInkSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            NeumorphicCircleButton(
                onClick = onCreateExamClick,
                size = 42.dp,
                modifier = Modifier.testTag("exam_ground_create_btn")
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = s.createExamTitle,
                    tint = StoneInkPrimary,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        // Search & Filter Bar
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text(
                        if (s.isBangla) "পরীক্ষা, বিষয় বা নির্মাতার নাম খুঁজুন..." else "Search exams, subject, or author..."
                    )
                },
                leadingIcon = {
                    Icon(Icons.Rounded.Search, contentDescription = null, tint = StoneInkSecondary)
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = StoneAccent,
                    unfocusedBorderColor = StoneDivider,
                    focusedTextColor = StoneInkPrimary,
                    unfocusedTextColor = StoneInkPrimary,
                    focusedContainerColor = StonePanelInner,
                    unfocusedContainerColor = StonePanelInner
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("exam_ground_search_input")
            )

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    GroundFilterPill(
                        label = if (s.isBangla) "সব (${allPublishedExams.size})" else "All (${allPublishedExams.size})",
                        selected = filterMode == GroundFilter.ALL,
                        onClick = { filterMode = GroundFilter.ALL }
                    )
                }
                item {
                    GroundFilterPill(
                        label = if (s.isBangla) "উন্মুক্ত (Open)" else "Open",
                        selected = filterMode == GroundFilter.OPEN,
                        onClick = { filterMode = GroundFilter.OPEN }
                    )
                }
                item {
                    GroundFilterPill(
                        label = if (s.isBangla) "পাসওয়ার্ড (Locked)" else "Password Locked",
                        selected = filterMode == GroundFilter.LOCKED,
                        onClick = { filterMode = GroundFilter.LOCKED }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
            contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 4.dp, bottom = 36.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxSize()
                .testTag("exam_ground_list")
        ) {
            if (filteredExams.isEmpty()) {
                item {
                    NeumorphicCard(
                        modifier = Modifier.fillMaxWidth(),
                        cornerRadius = 22.dp,
                        shadowElevation = 5.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Quiz,
                                contentDescription = null,
                                tint = StoneInkSecondary,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (s.isBangla) "কোনো প্রকাশিত পরীক্ষা পাওয়া যায়নি।" else "No matching published exams found.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = StoneInkSecondary
                            )
                        }
                    }
                }
            } else {
                items(filteredExams, key = { it.id }) { exam ->
                    ExamGroundCard(
                        exam = exam,
                        isMine = exam.id in userExamIds,
                        isBangla = s.isBangla,
                        onClick = { selectedEntryExam = exam }
                    )
                }
            }
        }
    }

    selectedEntryExam?.let { exam ->
        ExamGroundEntryBottomSheet(
            exam = exam,
            onDismiss = { selectedEntryExam = null },
            onConfirmEnter = {
                selectedEntryExam = null
                onEnterExam(exam)
            }
        )
    }
}

@Composable
private fun GroundFilterPill(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (selected) StoneInkPrimary else StonePanelInner)
            .border(1.dp, if (selected) StoneInkPrimary else StoneDivider, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 7.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = if (selected) NeumorphicHighlight else StoneInkPrimary
        )
    }
}

@Composable
private fun ExamGroundCard(
    exam: CustomPublishedExam,
    isMine: Boolean,
    isBangla: Boolean,
    onClick: () -> Unit
) {
    val accent = Color(exam.subjectColorHex)
    val isLocked = exam.password.isNotBlank()
    val styleBadge = if (exam.paperStyle == ExamPaperStyle.PLAIN_FLOOR) {
        if (isBangla) "প্লেইন ফ্লোর" else "Plain Floor"
    } else {
        if (isBangla) "বক্স কার্ড" else "Boxed Cards"
    }

    NeumorphicCard(
        onClick = onClick,
        cornerRadius = 22.dp,
        shadowElevation = 6.dp,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("exam_ground_card_${exam.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(accent)
                    )
                    Text(
                        text = "${exam.subjectName} • $styleBadge",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = accent,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (isMine) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(StoneSuccess.copy(alpha = 0.16f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (isBangla) "আপনার" else "YOURS",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = StoneSuccess
                            )
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(StonePanelInner)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Icon(
                        imageVector = if (isLocked) Icons.Rounded.Lock else Icons.Rounded.LockOpen,
                        contentDescription = null,
                        tint = if (isLocked) StoneAccent else StoneSuccess,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = if (isLocked) "Locked" else "Open",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isLocked) StoneAccent else StoneSuccess
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = exam.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = StoneInkPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            if (exam.chapterTitle.isNotBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = exam.chapterTitle,
                    style = MaterialTheme.typography.labelMedium,
                    color = StoneInkSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.Person,
                        contentDescription = null,
                        tint = StoneInkSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${exam.authorName} • ${exam.questions.size} Qs • ${exam.attemptCount} taken",
                        style = MaterialTheme.typography.labelSmall,
                        color = StoneInkSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(StoneInkPrimary)
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (isBangla) "প্রবেশ" else "ENTER",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = NeumorphicHighlight
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Icon(
                            imageVector = Icons.Rounded.PlayArrow,
                            contentDescription = null,
                            tint = NeumorphicHighlight,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExamGroundEntryBottomSheet(
    exam: CustomPublishedExam,
    onDismiss: () -> Unit,
    onConfirmEnter: () -> Unit
) {
    val s = LocalAppStrings.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val accent = Color(exam.subjectColorHex)
    val isLocked = exam.password.isNotBlank()

    var passwordInput by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var detailsExpanded by remember { mutableStateOf(exam.showDetailsInEntryPopup) }

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
                .testTag("exam_ground_entry_sheet_${exam.id}")
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(accent)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = exam.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = StoneInkPrimary,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            val styleLabel = if (exam.paperStyle == ExamPaperStyle.PLAIN_FLOOR) {
                if (s.isBangla) "প্লেইন ফ্লোর (টেক্সট)" else "Plain Floor (Text Only)"
            } else {
                if (s.isBangla) "বক্স কার্ড" else "Boxed Cards"
            }
            val metaLine = if (exam.chapterTitle.isNotBlank()) {
                "${exam.subjectName} • ${exam.chapterTitle} • $styleLabel"
            } else {
                "${exam.subjectName} • $styleLabel"
            }
            Text(
                text = metaLine,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = accent
            )
            Text(
                text = "By ${exam.authorName} (${exam.authorBatch}) • ${exam.questions.size} Questions",
                style = MaterialTheme.typography.labelSmall,
                color = StoneInkSecondary
            )

            Spacer(modifier = Modifier.height(14.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Entry Popup Description & Rules (respects showDetailsInEntryPopup and allows Hide/Show)
                if (exam.showDetailsInEntryPopup && (exam.description.isNotBlank() || exam.rules.isNotBlank())) {
                    NeumorphicCard(
                        modifier = Modifier.fillMaxWidth(),
                        cornerRadius = 18.dp,
                        shadowElevation = 4.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { detailsExpanded = !detailsExpanded }
                                    .testTag("exam_entry_details_toggle"),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (s.isBangla) "বিবরণ ও নিয়মাবলি" else "Description & Rules",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = StoneInkPrimary
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = if (detailsExpanded) "Hide" else "Show",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = StoneAccent
                                    )
                                    Icon(
                                        imageVector = if (detailsExpanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                                        contentDescription = null,
                                        tint = StoneAccent,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            AnimatedVisibility(visible = detailsExpanded) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    HorizontalDivider(color = StoneDivider)
                                    if (exam.description.isNotBlank()) {
                                        Text(
                                            text = exam.description,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = StoneInkPrimary
                                        )
                                    }
                                    if (exam.rules.isNotBlank()) {
                                        Text(
                                            text = exam.rules,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = StoneInkSecondary
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else if (!exam.showDetailsInEntryPopup) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(StonePanelInner)
                            .border(1.dp, StoneDivider, RoundedCornerShape(14.dp))
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                            .testTag("exam_entry_details_hidden_note")
                    ) {
                        Text(
                            text = if (s.isBangla) "পরীক্ষার বিবরণ ও নিয়ম নির্মাতা কর্তৃক লুকানো রয়েছে।" else "Description & Rules hidden by exam creator.",
                            style = MaterialTheme.typography.labelSmall,
                            color = StoneInkSecondary
                        )
                    }
                }

                // Password Gate if locked
                if (isLocked) {
                    OutlinedTextField(
                        value = passwordInput,
                        onValueChange = {
                            passwordInput = it
                            passwordError = null
                        },
                        label = { Text(if (s.isBangla) "পরীক্ষার পাসওয়ার্ড লিখুন" else "Enter Exam Password") },
                        leadingIcon = {
                            Icon(Icons.Rounded.Lock, contentDescription = null, tint = StoneAccent)
                        },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        isError = passwordError != null,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = StoneAccent,
                            unfocusedBorderColor = StoneDivider,
                            focusedTextColor = StoneInkPrimary,
                            unfocusedTextColor = StoneInkPrimary,
                            focusedContainerColor = StonePanelInner,
                            unfocusedContainerColor = StonePanelInner
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("exam_entry_password_input")
                    )
                    if (!passwordError.isNullOrBlank()) {
                        Text(
                            text = passwordError.orEmpty(),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFC85545),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.testTag("exam_entry_password_error")
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
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
                    text = s.examEnterExamButton,
                    selected = true,
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Rounded.PlayArrow,
                            contentDescription = null,
                            tint = StoneInkPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    onClick = {
                        if (isLocked && passwordInput.trim() != exam.password.trim()) {
                            passwordError = if (s.isBangla) {
                                "ভুল পাসওয়ার্ড! সঠিক পাসওয়ার্ড দিন।"
                            } else {
                                "Incorrect password. Please try again."
                            }
                        } else {
                            onConfirmEnter()
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("exam_ground_confirm_enter")
                )
            }
        }
    }
}
