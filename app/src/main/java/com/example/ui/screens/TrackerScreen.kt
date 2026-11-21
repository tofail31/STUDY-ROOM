package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.DoneAll
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.AlertDialog
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.calc.PrayerTimeCalculator
import com.example.data.model.PrepQuestionTag
import com.example.data.model.StudyDashboardDefaults
import com.example.data.model.TrackerChapter
import com.example.data.model.TrackerSubject
import com.example.ui.components.NeumorphicCard
import com.example.ui.components.NeumorphicCircleButton
import com.example.ui.components.NeumorphicPillButton
import com.example.ui.theme.BarlowCondensedFamily
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
import java.util.Locale

private val TrackerRecessedTrack = Color(0xFFC9C2BA)

@Composable
fun TrackerScreen(
    subjects: List<TrackerSubject>,
    onBack: () -> Unit,
    onToggleTag: (subjectId: String, chapterId: String, tag: PrepQuestionTag) -> Unit,
    onToggleAllInChapter: (subjectId: String, chapterId: String) -> Unit,
    onAddChapter: (subjectId: String, title: String) -> Unit,
    onDeleteChapter: (subjectId: String, chapterId: String) -> Unit,
    onAddSubject: (name: String, colorHex: Long, firstChapter: String) -> Unit
) {
    val s = LocalAppStrings.current
    var expandedSubjectId by remember { mutableStateOf<String?>(null) }
    var chapterFilterIdx by remember { mutableIntStateOf(0) } // 0 = All, 1 = Pending, 2 = Done
    var showAddSubjectDialog by remember { mutableStateOf(false) }
    var addChapterForSubject by remember { mutableStateOf<TrackerSubject?>(null) }

    val totalCheckboxes = subjects.sumOf { it.totalCheckboxes }.coerceAtLeast(1)
    val completedCheckboxes = subjects.sumOf { it.completedCheckboxes }
    val overallPercent = ((completedCheckboxes.toFloat() / totalCheckboxes.toFloat()) * 100f).toInt()
    val totalChapters = subjects.sumOf { it.chapters.size }
    val completedChapters = subjects.sumOf { it.completedChaptersCount }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(StoneBackgroundTop, StoneBackground, StoneBackgroundBottom))
            )
            .statusBarsPadding()
    ) {
        val columnsPerRow = if (maxWidth >= 600.dp) 3 else 2
        val subjectRows = remember(subjects, columnsPerRow) {
            subjects.chunked(columnsPerRow)
        }

        Column(modifier = Modifier.fillMaxSize()) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                NeumorphicCircleButton(
                    onClick = onBack,
                    size = 42.dp,
                    modifier = Modifier.testTag("tracker_back_button")
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
                        text = s.trackerTitle,
                        style = MaterialTheme.typography.titleLarge,
                        color = StoneInkPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = s.trackerSubtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = StoneInkSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                NeumorphicCircleButton(
                    onClick = { showAddSubjectDialog = true },
                    size = 42.dp,
                    modifier = Modifier.testTag("tracker_add_subject_btn")
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = s.trackerAddSubject,
                        tint = StoneInkPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            LazyColumn(
                contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 4.dp, bottom = 36.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("tracker_list")
            ) {
                // Overall Preparation Hero Summary Card
                item(key = "overall_summary") {
                    NeumorphicCard(
                        modifier = Modifier.fillMaxWidth(),
                        cornerRadius = 24.dp,
                        shadowElevation = 7.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = s.trackerOverallTitle,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.2.sp,
                                    color = StoneInkSecondary
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                val pctText = if (s.isBangla) {
                                    "${PrayerTimeCalculator.toBanglaDigits(overallPercent.toString())}%"
                                } else {
                                    "$overallPercent%"
                                }
                                Text(
                                    text = pctText,
                                    fontFamily = BarlowCondensedFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 42.sp,
                                    lineHeight = 44.sp,
                                    color = StoneInkPrimary,
                                    modifier = Modifier.testTag("tracker_overall_percent")
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                val countsLine = if (s.isBangla) {
                                    "${PrayerTimeCalculator.toBanglaDigits("$completedChapters/$totalChapters")} ${s.trackerChaptersLabel} • ${PrayerTimeCalculator.toBanglaDigits("$completedCheckboxes/$totalCheckboxes")} ${s.trackerDoneLabel}"
                                } else {
                                    "$completedChapters/$totalChapters ${s.trackerChaptersLabel} • $completedCheckboxes/$totalCheckboxes ${s.trackerDoneLabel}"
                                }
                                Text(
                                    text = countsLine,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = StoneAccent,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = s.trackerTapHint,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = StoneInkSecondary
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            val animRatio by animateFloatAsState(
                                targetValue = (completedCheckboxes.toFloat() / totalCheckboxes.toFloat()).coerceIn(0f, 1f),
                                label = "overall_ring"
                            )
                            Box(
                                modifier = Modifier.size(76.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Canvas(modifier = Modifier.size(76.dp)) {
                                    val strokePx = 8.dp.toPx()
                                    val d = size.minDimension - strokePx
                                    drawArc(
                                        color = TrackerRecessedTrack,
                                        startAngle = 0f,
                                        sweepAngle = 360f,
                                        useCenter = false,
                                        topLeft = Offset(strokePx / 2f, strokePx / 2f),
                                        size = Size(d, d),
                                        style = Stroke(width = strokePx, cap = StrokeCap.Round)
                                    )
                                    if (animRatio > 0.005f) {
                                        drawArc(
                                            color = StoneAccent,
                                            startAngle = -90f,
                                            sweepAngle = 360f * animRatio,
                                            useCenter = false,
                                            topLeft = Offset(strokePx / 2f, strokePx / 2f),
                                            size = Size(d, d),
                                            style = Stroke(width = strokePx, cap = StrokeCap.Round)
                                        )
                                    }
                                }
                                Text(
                                    text = if (s.isBangla) "${PrayerTimeCalculator.toBanglaDigits(overallPercent.toString())}%" else "$overallPercent%",
                                    fontFamily = PlusJakartaSansFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = StoneInkPrimary
                                )
                            }
                        }
                    }
                }

                // Subject Grid Rows + Inline Adzan-Style Expand-Down Box per Row
                itemsIndexed(
                    items = subjectRows,
                    key = { idx, row -> "row_${idx}_${row.first().id}" }
                ) { _, rowSubjects ->
                    val expandedSubjectInRow = rowSubjects.firstOrNull { it.id == expandedSubjectId }

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            rowSubjects.forEach { subj ->
                                val isExpanded = subj.id == expandedSubjectId
                                TrackerSubjectStatBox(
                                    subject = subj,
                                    isBangla = s.isBangla,
                                    isExpanded = isExpanded,
                                    onClick = {
                                        expandedSubjectId = if (isExpanded) null else subj.id
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            repeat(columnsPerRow - rowSubjects.size) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }

                        // Adzan-style downward expanding box right underneath the tapped subject's row!
                        AnimatedVisibility(
                            visible = expandedSubjectInRow != null,
                            enter = expandVertically() + fadeIn(),
                            exit = shrinkVertically() + fadeOut()
                        ) {
                            expandedSubjectInRow?.let { activeSubj ->
                                SubjectChaptersDropdownBox(
                                    subject = activeSubj,
                                    isBangla = s.isBangla,
                                    chapterFilterIdx = chapterFilterIdx,
                                    onSelectFilter = { chapterFilterIdx = it },
                                    onToggleTag = { chapterId, tag ->
                                        onToggleTag(activeSubj.id, chapterId, tag)
                                    },
                                    onToggleAllInChapter = { chapterId ->
                                        onToggleAllInChapter(activeSubj.id, chapterId)
                                    },
                                    onAddChapterClick = { addChapterForSubject = activeSubj },
                                    onDeleteChapter = { chapterId ->
                                        onDeleteChapter(activeSubj.id, chapterId)
                                    },
                                    onCollapse = { expandedSubjectId = null }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddSubjectDialog) {
        TrackerAddSubjectDialog(
            onDismiss = { showAddSubjectDialog = false },
            onConfirm = { name, colorHex, firstChap ->
                onAddSubject(name, colorHex, firstChap)
                showAddSubjectDialog = false
            }
        )
    }

    addChapterForSubject?.let { targetSubj ->
        TrackerAddChapterDialog(
            subjectName = if (s.isBangla) targetSubj.nameBn else targetSubj.nameEn,
            onDismiss = { addChapterForSubject = null },
            onConfirm = { title ->
                onAddChapter(targetSubj.id, title)
                addChapterForSubject = null
            }
        )
    }
}

@Composable
private fun TrackerSubjectStatBox(
    subject: TrackerSubject,
    isBangla: Boolean,
    isExpanded: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val s = LocalAppStrings.current
    val subjectColor = Color(subject.colorHex)
    val pct = subject.progressPercent
    val chevronRotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        label = "subj_chevron_${subject.id}"
    )
    val animProgress by animateFloatAsState(
        targetValue = (pct / 100f).coerceIn(0f, 1f),
        label = "subj_bar_${subject.id}"
    )

    val labelName = (if (isBangla) subject.nameBn else subject.nameEn).uppercase(Locale.US)
    val pctDisplay = if (isBangla) "${PrayerTimeCalculator.toBanglaDigits(pct.toString())}%" else "$pct%"
    val subtitleText = if (isBangla) {
        "${PrayerTimeCalculator.toBanglaDigits("${subject.completedCheckboxes}/${subject.totalCheckboxes}")} ${s.trackerDoneLabel}"
    } else {
        "${subject.completedCheckboxes}/${subject.totalCheckboxes} ${s.trackerDoneLabel}"
    }

    NeumorphicCard(
        onClick = onClick,
        cornerRadius = 22.dp,
        shadowElevation = if (isExpanded) 9.dp else 6.dp,
        modifier = modifier
            .then(
                if (isExpanded) {
                    Modifier.border(
                        width = 1.6.dp,
                        color = subjectColor.copy(alpha = 0.72f),
                        shape = RoundedCornerShape(22.dp)
                    )
                } else Modifier
            )
            .testTag("tracker_subject_card_${subject.id}")
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
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(subjectColor)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = labelName,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.0.sp,
                        color = if (isExpanded) subjectColor else StoneInkSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Icon(
                    imageVector = Icons.Rounded.KeyboardArrowDown,
                    contentDescription = "Expand $labelName",
                    tint = if (isExpanded) subjectColor else StoneInkSecondary,
                    modifier = Modifier
                        .size(18.dp)
                        .rotate(chevronRotation)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = pctDisplay,
                fontFamily = BarlowCondensedFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                color = if (pct == 100) StoneSuccess else StoneInkPrimary,
                modifier = Modifier.testTag("tracker_subject_pct_${subject.id}")
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subtitleText,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium,
                color = StoneInkSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(50))
                    .background(TrackerRecessedTrack)
            ) {
                if (animProgress > 0f) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(animProgress.coerceAtLeast(0.04f))
                            .height(4.dp)
                            .clip(RoundedCornerShape(50))
                            .background(if (pct == 100) StoneSuccess else subjectColor)
                    )
                }
            }
        }
    }
}

@Composable
private fun SubjectChaptersDropdownBox(
    subject: TrackerSubject,
    isBangla: Boolean,
    chapterFilterIdx: Int,
    onSelectFilter: (Int) -> Unit,
    onToggleTag: (chapterId: String, tag: PrepQuestionTag) -> Unit,
    onToggleAllInChapter: (chapterId: String) -> Unit,
    onAddChapterClick: () -> Unit,
    onDeleteChapter: (chapterId: String) -> Unit,
    onCollapse: () -> Unit
) {
    val s = LocalAppStrings.current
    val subjectColor = Color(subject.colorHex)
    val subjectTitle = if (isBangla) subject.nameBn else subject.nameEn

    val filteredChapters = remember(subject.chapters, chapterFilterIdx) {
        when (chapterFilterIdx) {
            1 -> subject.chapters.filter { it.completedTags.size < PrepQuestionTag.entries.size }
            2 -> subject.chapters.filter { it.completedTags.size == PrepQuestionTag.entries.size }
            else -> subject.chapters
        }
    }

    NeumorphicCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("tracker_expanded_${subject.id}"),
        cornerRadius = 24.dp,
        shadowElevation = 9.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            // Dropdown Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(11.dp)
                            .clip(CircleShape)
                            .background(subjectColor)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = subjectTitle,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = StoneInkPrimary
                        )
                        val summary = if (isBangla) {
                            "${PrayerTimeCalculator.toBanglaDigits(subject.progressPercent.toString())}% • ${PrayerTimeCalculator.toBanglaDigits("${subject.completedCheckboxes}/${subject.totalCheckboxes}")} ${s.trackerDoneLabel}"
                        } else {
                            "${subject.progressPercent}% • ${subject.completedCheckboxes}/${subject.totalCheckboxes} ${s.trackerDoneLabel}"
                        }
                        Text(
                            text = summary,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = subjectColor
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(StonePanelInner)
                            .clickable(onClick = onAddChapterClick)
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("tracker_add_chapter_${subject.id}"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = s.trackerAddChapter,
                            tint = StoneInkPrimary,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = s.trackerAddChapter,
                            fontFamily = PlusJakartaSansFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = StoneInkPrimary
                        )
                    }

                    IconButton(
                        onClick = onCollapse,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = "Collapse",
                            tint = StoneInkSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Quick Filter Chips inside Expanded Box
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val filterLabels = if (isBangla) listOf("সব", "বাকি", "সম্পন্ন") else listOf("All", "Pending", "Done")
                filterLabels.forEachIndexed { idx, label ->
                    NeumorphicPillButton(
                        text = label,
                        selected = chapterFilterIdx == idx,
                        onClick = { onSelectFilter(idx) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = StoneDivider, thickness = 1.dp)
            Spacer(modifier = Modifier.height(10.dp))

            // Column Guide: Left Chapter Name | Right Prep Tags
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isBangla) "অধ্যায়" else "CHAPTER",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp,
                    color = StoneInkSecondary,
                    modifier = Modifier.weight(0.45f)
                )
                Text(
                    text = if (isBangla) "প্রস্তুতি (ক খ গ ঘ • CQ • MCQ)" else "PREP (ক খ গ ঘ • CQ • MCQ)",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.6.sp,
                    color = StoneInkSecondary,
                    modifier = Modifier.weight(0.55f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (filteredChapters.isEmpty()) {
                Text(
                    text = if (isBangla) "এই ফিল্টারে কোনো অধ্যায় নেই।" else "No chapters match this filter.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = StoneInkSecondary,
                    modifier = Modifier.padding(vertical = 14.dp)
                )
            } else {
                filteredChapters.forEachIndexed { index, chapter ->
                    ChapterPrepRow(
                        index = index + 1,
                        chapter = chapter,
                        subjectColor = subjectColor,
                        canDelete = subject.chapters.size > 1,
                        isBangla = isBangla,
                        onToggleTag = { tag -> onToggleTag(chapter.id, tag) },
                        onToggleAll = { onToggleAllInChapter(chapter.id) },
                        onDelete = { onDeleteChapter(chapter.id) }
                    )
                    if (index < filteredChapters.lastIndex) {
                        HorizontalDivider(
                            color = StoneDivider.copy(alpha = 0.7f),
                            thickness = 0.8.dp,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChapterPrepRow(
    index: Int,
    chapter: TrackerChapter,
    subjectColor: Color,
    canDelete: Boolean,
    isBangla: Boolean,
    onToggleTag: (PrepQuestionTag) -> Unit,
    onToggleAll: () -> Unit,
    onDelete: () -> Unit
) {
    val numPrefix = if (isBangla) PrayerTimeCalculator.toBanglaDigits("$index.") else "$index."
    val progressRatio = (chapter.doneCount.toFloat() / chapter.totalCount.toFloat()).coerceIn(0f, 1f)
    val allDone = chapter.doneCount == chapter.totalCount

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .testTag("tracker_chapter_row_${chapter.id}"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left Side: Chapter Name + Progress % + Mark All / Delete
        Column(
            modifier = Modifier
                .weight(0.45f)
                .padding(end = 8.dp)
        ) {
            Text(
                text = "$numPrefix ${chapter.title}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = StoneInkPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(5.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val statText = if (isBangla) {
                    PrayerTimeCalculator.toBanglaDigits("${chapter.doneCount}/${chapter.totalCount}")
                } else {
                    "${chapter.doneCount}/${chapter.totalCount}"
                }
                Text(
                    text = statText,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (allDone) StoneSuccess else subjectColor
                )

                Icon(
                    imageVector = Icons.Rounded.DoneAll,
                    contentDescription = "Mark all in ${chapter.title}",
                    tint = if (allDone) StoneSuccess else StoneInkMuted,
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onToggleAll)
                        .testTag("tracker_mark_all_${chapter.id}")
                )

                if (canDelete) {
                    Icon(
                        imageVector = Icons.Rounded.DeleteOutline,
                        contentDescription = "Remove chapter",
                        tint = StoneInkMuted,
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .clickable(onClick = onDelete)
                    )
                }
            }
            Spacer(modifier = Modifier.height(5.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.5.dp)
                    .clip(RoundedCornerShape(50))
                    .background(TrackerRecessedTrack)
            ) {
                if (progressRatio > 0f) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progressRatio)
                            .height(3.5.dp)
                            .clip(RoundedCornerShape(50))
                            .background(if (allDone) StoneSuccess else subjectColor)
                    )
                }
            }
        }

        // Right Side: ক, খ, গ, ঘ, CQ, MCQ preparation options in crisp 2x3 rows
        Column(
            modifier = Modifier.weight(0.55f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val row1 = listOf(PrepQuestionTag.KA, PrepQuestionTag.KHA, PrepQuestionTag.GA)
            val row2 = listOf(PrepQuestionTag.GHA, PrepQuestionTag.CQ, PrepQuestionTag.MCQ)

            listOf(row1, row2).forEach { rowTags ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    rowTags.forEach { tag ->
                        val isDone = tag in chapter.completedTags
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(32.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isDone) StoneSuccess else StonePanelInner)
                                .border(
                                    width = 1.dp,
                                    color = if (isDone) StoneSuccess else StoneDivider,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable { onToggleTag(tag) }
                                .testTag("tracker_tag_${chapter.id}_${tag.name}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                if (isDone) {
                                    Icon(
                                        imageVector = Icons.Rounded.Check,
                                        contentDescription = null,
                                        tint = NeumorphicHighlight,
                                        modifier = Modifier.size(11.dp)
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                }
                                Text(
                                    text = tag.label,
                                    fontFamily = PlusJakartaSansFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = if (isDone) NeumorphicHighlight else StoneInkPrimary,
                                    maxLines = 1
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
private fun TrackerAddChapterDialog(
    subjectName: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = StoneSurface,
        titleContentColor = StoneInkPrimary,
        textContentColor = StoneInkPrimary,
        title = {
            Text(
                text = "Add Chapter • $subjectName",
                fontFamily = PlusJakartaSansFamily,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Chapter Title") },
                placeholder = { Text("e.g. আলোর প্রতিফলন") },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = StoneAccent,
                    unfocusedBorderColor = StoneDivider,
                    focusedTextColor = StoneInkPrimary,
                    unfocusedTextColor = StoneInkPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("tracker_chapter_name_input")
            )
        },
        confirmButton = {
            TextButton(
                onClick = { if (title.isNotBlank()) onConfirm(title.trim()) },
                modifier = Modifier.testTag("tracker_chapter_confirm_btn")
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

@Composable
private fun TrackerAddSubjectDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Long, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var firstChapter by remember { mutableStateOf("") }
    var selectedColor by remember { mutableLongStateOf(StudyDashboardDefaults.subjectColorPalette.first()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = StoneSurface,
        titleContentColor = StoneInkPrimary,
        textContentColor = StoneInkPrimary,
        title = {
            Text(
                text = "Add Subject to Tracker",
                fontFamily = PlusJakartaSansFamily,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Subject Name") },
                    placeholder = { Text("e.g. Accounting, Finance") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = StoneAccent,
                        unfocusedBorderColor = StoneDivider,
                        focusedTextColor = StoneInkPrimary,
                        unfocusedTextColor = StoneInkPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = firstChapter,
                    onValueChange = { firstChapter = it },
                    label = { Text("First Chapter") },
                    placeholder = { Text("e.g. Chapter 1") },
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
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank()) {
                        onConfirm(name.trim(), selectedColor, firstChapter.trim())
                    }
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
