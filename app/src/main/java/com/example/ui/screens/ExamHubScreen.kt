package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material.icons.rounded.HourglassTop
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.Quiz
import androidx.compose.material.icons.rounded.School
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.calc.PrayerTimeCalculator
import com.example.data.local.ExamQuestionBank
import com.example.data.model.ExamTrackCardInfo
import com.example.data.model.ExamTrackId
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

private val ExamRecessedTrack = Color(0xFFC9C2BA)

@Composable
fun ExamHubScreen(
    trackerSubjects: List<TrackerSubject>,
    examBestScores: Map<String, Int>,
    wrongQuestionIds: Set<String>,
    totalExamsTaken: Int,
    publishedGroundCount: Int = 3,
    userCreatedExamsCount: Int = 0,
    onBack: () -> Unit,
    onOpenHscSubjects: () -> Unit,
    onOpenTracker: () -> Unit,
    onStartWrongAnswersExam: () -> Unit,
    onOpenCreateExam: () -> Unit = {},
    onOpenExamAccount: () -> Unit = {},
    onOpenExamGround: () -> Unit = {}
) {
    val s = LocalAppStrings.current
    var comingSoonTrack by remember { mutableStateOf<ExamTrackCardInfo?>(null) }

    val totalQuestions = remember { ExamQuestionBank.totalQuestionsCount() }
    val totalCheckboxes = trackerSubjects.sumOf { it.totalCheckboxes }.coerceAtLeast(1)
    val completedCheckboxes = trackerSubjects.sumOf { it.completedCheckboxes }
    val trackerOverallPct = ((completedCheckboxes.toFloat() / totalCheckboxes.toFloat()) * 100f).toInt()
    val hscCompletedChapters = examBestScores.size
    val totalHscChapters = trackerSubjects.sumOf { it.chapters.size }

    val tracks = remember(hscCompletedChapters, totalHscChapters, wrongQuestionIds.size) {
        val wrongReady = wrongQuestionIds.isNotEmpty()
        listOf(
            ExamTrackCardInfo(
                id = ExamTrackId.HSC,
                titleEn = "HSC",
                titleBn = "HSC পরীক্ষা",
                subtitleEn = "$hscCompletedChapters/$totalHscChapters Chapters • 7 Subjects",
                subtitleBn = "${PrayerTimeCalculator.toBanglaDigits("$hscCompletedChapters/$totalHscChapters")} অধ্যায় • ৭ বিষয়",
                badgeEn = "LIVE • 3 Qs/CH",
                badgeBn = "চালু • ৩ প্রশ্ন",
                colorHex = 0xFF3E8E63,
                isActive = true
            ),
            ExamTrackCardInfo(
                id = ExamTrackId.MEDICAL,
                titleEn = "MEDICAL",
                titleBn = "মেডিকেল (MEDICAL)",
                subtitleEn = "MBBS • Bio, Chem & Physics",
                subtitleBn = "MBBS ভর্তি • জীব, রসায়ন ও পদার্থ",
                badgeEn = "COMING SOON",
                badgeBn = "শীঘ্রই আসছে",
                colorHex = 0xFFDD6B4D,
                isActive = false
            ),
            ExamTrackCardInfo(
                id = ExamTrackId.BUET,
                titleEn = "BUET",
                titleBn = "বুয়েট (BUET)",
                subtitleEn = "Engineering • Math, Phy & Chem",
                subtitleBn = "ইঞ্জিনিয়ারিং • গণিত, পদার্থ ও রসায়ন",
                badgeEn = "COMING SOON",
                badgeBn = "শীঘ্রই আসছে",
                colorHex = 0xFF4F7AC7,
                isActive = false
            ),
            ExamTrackCardInfo(
                id = ExamTrackId.DU,
                titleEn = "DU",
                titleBn = "ঢাবি (DU)",
                subtitleEn = "Dhaka Univ • Ka Unit Prep",
                subtitleBn = "ঢাকা বিশ্ববিদ্যালয় • ক ইউনিট",
                badgeEn = "COMING SOON",
                badgeBn = "শীঘ্রই আসছে",
                colorHex = 0xFF6B5CE7,
                isActive = false
            ),
            ExamTrackCardInfo(
                id = ExamTrackId.WRONG_ANS,
                titleEn = "YOUR WRONG ANS EXAM",
                titleBn = "ভুল উত্তরের পরীক্ষা",
                subtitleEn = if (wrongReady) "${wrongQuestionIds.size} wrong questions queued" else "Re-test missed HSC questions",
                subtitleBn = if (wrongReady) "${PrayerTimeCalculator.toBanglaDigits(wrongQuestionIds.size.toString())}টি ভুল প্রশ্ন জমা আছে" else "ভুল উত্তরগুলোর পুনঃপরীক্ষা",
                badgeEn = if (wrongReady) "PRACTICE" else "COMING SOON",
                badgeBn = if (wrongReady) "অনুশীলন" else "শীঘ্রই আসছে",
                colorHex = 0xFFB86A35,
                isActive = wrongReady
            )
        )
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(StoneBackgroundTop, StoneBackground, StoneBackgroundBottom))
            )
            .statusBarsPadding()
    ) {
        val isWide = maxWidth >= 600.dp
        val columns = if (isWide) 3 else 2
        val gridTracks = tracks.take(4)
        val wrongTrack = tracks.last()
        val trackRows = remember(gridTracks, columns) { gridTracks.chunked(columns) }

        Column(modifier = Modifier.fillMaxSize()) {
            // Top Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                NeumorphicCircleButton(
                    onClick = onBack,
                    size = 42.dp,
                    modifier = Modifier.testTag("exam_back_button")
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
                        text = s.examTitle,
                        style = MaterialTheme.typography.titleLarge,
                        color = StoneInkPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = s.examSubtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = StoneInkSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Quick Exam Counter Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(StonePanelInner)
                        .border(1.dp, StoneDivider, RoundedCornerShape(14.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    val countText = if (s.isBangla) {
                        "${PrayerTimeCalculator.toBanglaDigits(totalExamsTaken.toString())} পরীক্ষা"
                    } else {
                        "$totalExamsTaken Taken"
                    }
                    Text(
                        text = countText,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = StoneAccent
                    )
                }
            }

            LazyColumn(
                contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 4.dp, bottom = 36.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("exam_hub_list")
            ) {
                // 1. FIRST SECTION: Tracker Subject Card Showing (as requested)
                item(key = "tracker_subject_card_strip") {
                    NeumorphicCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("exam_tracker_preview_card"),
                        cornerRadius = 24.dp,
                        shadowElevation = 7.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 18.dp, vertical = 16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (s.isBangla) "সিলেবাস ট্র্যাকার কার্ড" else "TRACKER SUBJECT CARDS",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.1.sp,
                                        color = StoneInkSecondary
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    val subSummary = if (s.isBangla) {
                                        "সামগ্রিক HSC প্রস্তুতি: ${PrayerTimeCalculator.toBanglaDigits(trackerOverallPct.toString())}% • মোট $totalQuestions MCQ"
                                    } else {
                                        "HSC Syllabus Readiness: $trackerOverallPct% • $totalQuestions MCQs"
                                    }
                                    Text(
                                        text = subSummary,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = StoneAccent
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(StoneInkPrimary)
                                        .clickable(onClick = onOpenTracker)
                                        .padding(horizontal = 11.dp, vertical = 6.dp)
                                        .testTag("exam_open_tracker_chip")
                                ) {
                                    Text(
                                        text = if (s.isBangla) "ট্র্যাকার" else "Tracker",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = NeumorphicHighlight
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(trackerSubjects, key = { it.id }) { subj ->
                                    val subjColor = Color(subj.colorHex)
                                    val subjPct = subj.progressPercent
                                    val examAttempted = subj.chapters.count { it.id in examBestScores }
                                    Box(
                                        modifier = Modifier
                                            .width(136.dp)
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(StonePanelInner)
                                            .border(1.dp, StoneDivider, RoundedCornerShape(16.dp))
                                            .clickable(onClick = onOpenHscSubjects)
                                            .padding(horizontal = 12.dp, vertical = 10.dp)
                                            .testTag("exam_tracker_mini_${subj.id}")
                                    ) {
                                        Column {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(7.dp)
                                                        .clip(CircleShape)
                                                        .background(subjColor)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = (if (s.isBangla) subj.nameBn else subj.nameEn).uppercase(Locale.US),
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = FontWeight.Bold,
                                                    color = StoneInkSecondary,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = if (s.isBangla) "${PrayerTimeCalculator.toBanglaDigits(subjPct.toString())}%" else "$subjPct%",
                                                fontFamily = BarlowCondensedFamily,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 24.sp,
                                                color = StoneInkPrimary
                                            )
                                            Text(
                                                text = if (s.isBangla) {
                                                    "পরীক্ষা: ${PrayerTimeCalculator.toBanglaDigits("$examAttempted/${subj.chapters.size}")}"
                                                } else {
                                                    "Exam: $examAttempted/${subj.chapters.size}"
                                                },
                                                style = MaterialTheme.typography.labelSmall,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = subjColor
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Section Title for Exam Tracks
                item(key = "exam_tracks_header") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (s.isBangla) "পরীক্ষার বিভাগসমূহ (EXAM TRACKS)" else "EXAM TRACKS",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp,
                            color = StoneInkSecondary
                        )
                        Text(
                            text = if (s.isBangla) "HSC এখন চালু আছে" else "HSC Live • Others Soon",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = StoneSuccess
                        )
                    }
                }

                // 2. Grid of HSC, MEDICAL, BUET, DU Tracker-style Boxes
                itemsIndexed(
                    items = trackRows,
                    key = { idx, row -> "track_row_${idx}_${row.first().id}" }
                ) { _, rowTracks ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowTracks.forEach { track ->
                            ExamTrackStatBox(
                                track = track,
                                isBangla = s.isBangla,
                                onClick = {
                                    when (track.id) {
                                        ExamTrackId.HSC -> onOpenHscSubjects()
                                        ExamTrackId.MEDICAL, ExamTrackId.BUET, ExamTrackId.DU -> {
                                            comingSoonTrack = track
                                        }
                                        ExamTrackId.WRONG_ANS -> {
                                            if (wrongQuestionIds.isNotEmpty()) {
                                                onStartWrongAnswersExam()
                                            } else {
                                                comingSoonTrack = track
                                            }
                                        }
                                        ExamTrackId.CREATE_EXAM -> onOpenCreateExam()
                                        ExamTrackId.EXAM_GROUND -> onOpenExamGround()
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        repeat(columns - rowTracks.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }

                // 3. Dedicated Full-Width "YOUR WRONG ANS EXAM" Box Card
                item(key = "track_wrong_ans") {
                    ExamTrackStatBox(
                        track = wrongTrack,
                        isBangla = s.isBangla,
                        isWideCard = true,
                        onClick = {
                            if (wrongQuestionIds.isNotEmpty()) {
                                onStartWrongAnswersExam()
                            } else {
                                comingSoonTrack = wrongTrack
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // 4. Student Exam Creation Studio Header
                item(key = "student_exam_studio_header") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (s.isBangla) "শিক্ষার্থী নির্মিত পরীক্ষা (CREATE & GROUND)" else "STUDENT EXAM STUDIO",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp,
                            color = StoneInkSecondary
                        )
                        Text(
                            text = if (s.isBangla) "তৈরি ও প্রকাশ" else "Create & Publish",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = StoneAccent
                        )
                    }
                }

                // 5. Same Row 2 Columns: 1. CREATE EXAM (Create Exam + Exam Account inside) | 2. EXAM GROUND
                item(key = "track_create_and_ground_row") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Max)
                            .testTag("exam_create_ground_row"),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CreateExamHubBoxCard(
                            userCreatedCount = userCreatedExamsCount,
                            isBangla = s.isBangla,
                            onCreateExamClick = onOpenCreateExam,
                            onExamAccountClick = onOpenExamAccount,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        )

                        ExamTrackStatBox(
                            track = ExamTrackCardInfo(
                                id = ExamTrackId.EXAM_GROUND,
                                titleEn = "2. EXAM GROUND",
                                titleBn = "২. এক্সাম গ্রাউন্ড",
                                subtitleEn = "$publishedGroundCount student exams • Search & enter",
                                subtitleBn = "${PrayerTimeCalculator.toBanglaDigits(publishedGroundCount.toString())}টি পরীক্ষা • প্রবেশ করুন",
                                badgeEn = "LIVE • GROUND",
                                badgeBn = "সবার পরীক্ষা",
                                colorHex = 0xFF3E8E63,
                                isActive = true
                            ),
                            isBangla = s.isBangla,
                            isWideCard = false,
                            onClick = onOpenExamGround,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        )
                    }
                }
            }
        }
    }

    comingSoonTrack?.let { track ->
        val titleText = if (s.isBangla) track.titleBn else track.titleEn
        AlertDialog(
            onDismissRequest = { comingSoonTrack = null },
            containerColor = StoneSurface,
            titleContentColor = StoneInkPrimary,
            textContentColor = StoneInkPrimary,
            icon = {
                Icon(
                    imageVector = Icons.Rounded.HourglassTop,
                    contentDescription = null,
                    tint = Color(track.colorHex),
                    modifier = Modifier.size(34.dp)
                )
            },
            title = {
                Text(
                    text = "$titleText • ${s.examComingSoonTitle}",
                    fontFamily = PlusJakartaSansFamily,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = s.examComingSoonDesc,
                    style = MaterialTheme.typography.bodyMedium,
                    color = StoneInkSecondary
                )
            },
            confirmButton = {
                TextButton(
                    onClick = { comingSoonTrack = null },
                    modifier = Modifier.testTag("exam_coming_soon_ok")
                ) {
                    Text(
                        text = if (s.isBangla) "ঠিক আছে" else "Got It",
                        fontWeight = FontWeight.Bold,
                        color = StoneInkPrimary
                    )
                }
            },
            modifier = Modifier.testTag("exam_coming_soon_dialog")
        )
    }
}

@Composable
private fun ExamTrackStatBox(
    track: ExamTrackCardInfo,
    isBangla: Boolean,
    isWideCard: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accent = Color(track.colorHex)
    val title = if (isBangla) track.titleBn else track.titleEn
    val subtitle = if (isBangla) track.subtitleBn else track.subtitleEn
    val badge = if (isBangla) track.badgeBn else track.badgeEn

    NeumorphicCard(
        onClick = onClick,
        cornerRadius = 22.dp,
        shadowElevation = if (track.isActive) 8.dp else 5.dp,
        modifier = modifier
            .then(
                if (track.isActive) {
                    Modifier.border(1.5.dp, accent.copy(alpha = 0.55f), RoundedCornerShape(22.dp))
                } else Modifier
            )
            .testTag("exam_track_card_${track.id.name}")
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
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (track.isActive) accent.copy(alpha = 0.16f) else StonePanelInner)
                        .border(
                            width = 1.dp,
                            color = if (track.isActive) accent.copy(alpha = 0.45f) else StoneDivider,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = badge,
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.6.sp,
                        color = if (track.isActive) accent else StoneInkSecondary
                    )
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                    contentDescription = "Open $title",
                    tint = if (track.isActive) accent else StoneInkSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val trackIcon = when (track.id) {
                    ExamTrackId.HSC -> Icons.Rounded.School
                    ExamTrackId.EXAM_GROUND -> Icons.Rounded.Public
                    else -> Icons.Rounded.Quiz
                }
                Icon(
                    imageVector = trackIcon,
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(if (isWideCard) 24.dp else 20.dp)
                )
                Text(
                    text = title,
                    fontFamily = BarlowCondensedFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = if (isWideCard) 25.sp else 23.sp,
                    color = StoneInkPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium,
                color = StoneInkSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(50))
                    .background(ExamRecessedTrack)
            ) {
                if (track.isActive) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(if (track.id == ExamTrackId.HSC) 1f else 0.5f)
                            .height(4.dp)
                            .clip(RoundedCornerShape(50))
                            .background(accent)
                    )
                }
            }
        }
    }
}

@Composable
private fun CreateExamHubBoxCard(
    userCreatedCount: Int,
    isBangla: Boolean,
    onCreateExamClick: () -> Unit,
    onExamAccountClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accent = StoneAccent
    NeumorphicCard(
        onClick = onCreateExamClick,
        cornerRadius = 22.dp,
        shadowElevation = 8.dp,
        modifier = modifier
            .border(1.5.dp, accent.copy(alpha = 0.50f), RoundedCornerShape(22.dp))
            .testTag("exam_track_card_CREATE_EXAM")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(accent.copy(alpha = 0.16f))
                            .border(1.dp, accent.copy(alpha = 0.45f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 7.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = if (isBangla) {
                                "১. স্টুডিও • ${PrayerTimeCalculator.toBanglaDigits(userCreatedCount.toString())}"
                            } else {
                                "1. STUDIO • $userCreatedCount"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp,
                            color = accent
                        )
                    }

                    Icon(
                        imageVector = Icons.Rounded.EditNote,
                        contentDescription = null,
                        tint = accent,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (isBangla) "CREATE EXAM" else "1. CREATE EXAM",
                    fontFamily = BarlowCondensedFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = StoneInkPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = if (isBangla) "পরীক্ষা তৈরি ও অ্যাকাউন্ট" else "Build Exam & Account",
                    style = MaterialTheme.typography.labelSmall,
                    color = StoneInkSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Both 1. Create Exam & Exam Account options stacked inside the SAME half-width card
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NeumorphicPillButton(
                    text = if (isBangla) "পরীক্ষা তৈরি" else "Create Exam",
                    selected = true,
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Rounded.EditNote,
                            contentDescription = null,
                            tint = StoneInkPrimary,
                            modifier = Modifier.size(15.dp)
                        )
                    },
                    onClick = onCreateExamClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp)
                        .testTag("exam_btn_create_exam")
                )

                NeumorphicPillButton(
                    text = if (isBangla) "অ্যাকাউন্ট" else "Exam Account",
                    selected = false,
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Rounded.Person,
                            contentDescription = null,
                            tint = StoneInkPrimary,
                            modifier = Modifier.size(15.dp)
                        )
                    },
                    onClick = onExamAccountClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp)
                        .testTag("exam_btn_exam_account")
                )
            }
        }
    }
}

@Composable
fun ExamHscSubjectsScreen(
    subjects: List<TrackerSubject>,
    examBestScores: Map<String, Int>,
    onBack: () -> Unit,
    onSelectChapterExam: (TrackerSubject, TrackerChapter) -> Unit
) {
    val s = LocalAppStrings.current
    var activeSubjectId by remember { mutableStateOf<String?>(null) }
    var selectedPaper by remember { mutableIntStateOf(1) }

    val activePopupSubject = remember(activeSubjectId, subjects) {
        activeSubjectId?.let { id -> subjects.firstOrNull { it.id == id } }
    }

    val totalChapters = subjects.sumOf { it.chapters.size }.coerceAtLeast(1)
    val attemptedChapters = subjects.sumOf { subj -> subj.chapters.count { it.id in examBestScores } }
    val totalCorrectEarned = subjects.sumOf { subj ->
        subj.chapters.sumOf { ch -> examBestScores[ch.id] ?: 0 }
    }
    val attemptedMax = (attemptedChapters * 3).coerceAtLeast(1)
    val avgAccuracyPct = if (attemptedChapters == 0) 0 else ((totalCorrectEarned.toFloat() / attemptedMax) * 100f).toInt()

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(StoneBackgroundTop, StoneBackground, StoneBackgroundBottom))
            )
            .statusBarsPadding()
    ) {
        val columnsPerRow = if (maxWidth >= 600.dp) 3 else 2
        val subjectRows = remember(subjects, columnsPerRow) { subjects.chunked(columnsPerRow) }

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
                    modifier = Modifier.testTag("exam_hsc_back_button")
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
                        text = s.examAllSubjectsTitle,
                        style = MaterialTheme.typography.titleLarge,
                        color = StoneInkPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = s.examAllSubjectsSubtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = StoneInkSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            LazyColumn(
                contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 4.dp, bottom = 36.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("exam_hsc_subjects_list")
            ) {
                // HSC Overview Summary Card
                item(key = "hsc_exam_summary") {
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
                                    text = if (s.isBangla) "HSC বোর্ড স্ট্যান্ডার্ড MCQ" else "HSC BOARD MODEL TEST",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.1.sp,
                                    color = StoneInkSecondary
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = if (s.isBangla) {
                                        "${PrayerTimeCalculator.toBanglaDigits("$attemptedChapters/$totalChapters")} অধ্যায় সম্পন্ন"
                                    } else {
                                        "$attemptedChapters / $totalChapters Chapters Tested"
                                    },
                                    fontFamily = BarlowCondensedFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 30.sp,
                                    color = StoneInkPrimary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (s.isBangla) {
                                        "প্রতি অধ্যায়ে ৩টি করে HSC প্রশ্ন • গড় সঠিকতা ${PrayerTimeCalculator.toBanglaDigits(avgAccuracyPct.toString())}%"
                                    } else {
                                        "3 HSC MCQs per chapter • Avg Accuracy $avgAccuracyPct%"
                                    },
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = StoneAccent
                                )
                            }
                        }
                    }
                }

                // 7 HSC Subjects Grid
                itemsIndexed(
                    items = subjectRows,
                    key = { idx, row -> "exam_subj_row_${idx}_${row.first().id}" }
                ) { _, rowSubjects ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowSubjects.forEach { subj ->
                            ExamHscSubjectCard(
                                subject = subj,
                                examBestScores = examBestScores,
                                isBangla = s.isBangla,
                                isSelected = subj.id == activeSubjectId,
                                onClick = {
                                    activeSubjectId = subj.id
                                    selectedPaper = 1
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        repeat(columnsPerRow - rowSubjects.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }

    // Down-to-top ModalBottomSheet popup showing 1st / 2nd Paper chapters
    if (activePopupSubject != null) {
        ExamSubjectChaptersBottomSheet(
            subject = activePopupSubject,
            examBestScores = examBestScores,
            selectedPaper = selectedPaper,
            onSelectPaper = { selectedPaper = it },
            onSelectChapter = { chapter ->
                activeSubjectId = null
                onSelectChapterExam(activePopupSubject, chapter)
            },
            onDismiss = { activeSubjectId = null }
        )
    }
}

@Composable
private fun ExamHscSubjectCard(
    subject: TrackerSubject,
    examBestScores: Map<String, Int>,
    isBangla: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val subjColor = Color(subject.colorHex)
    val totalCh = subject.chapters.size.coerceAtLeast(1)
    val doneCh = subject.chapters.count { it.id in examBestScores }
    val p1Total = subject.chaptersForPaper(1).size
    val p2Total = subject.chaptersForPaper(2).size
    val ratio by animateFloatAsState(
        targetValue = (doneCh.toFloat() / totalCh.toFloat()).coerceIn(0f, 1f),
        label = "exam_subj_${subject.id}"
    )
    val labelName = (if (isBangla) subject.nameBn else subject.nameEn).uppercase(Locale.US)

    NeumorphicCard(
        onClick = onClick,
        cornerRadius = 22.dp,
        shadowElevation = if (isSelected) 9.dp else 6.dp,
        modifier = modifier.testTag("exam_subject_card_${subject.id}")
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
                            .background(subjColor)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = labelName,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.9.sp,
                        color = StoneInkSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                    contentDescription = "Open $labelName",
                    tint = StoneInkSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = if (isBangla) {
                    PrayerTimeCalculator.toBanglaDigits("$doneCh/$totalCh")
                } else {
                    "$doneCh/$totalCh"
                },
                fontFamily = BarlowCondensedFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                color = if (doneCh == totalCh) StoneSuccess else StoneInkPrimary
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = if (isBangla) {
                    "১ম: ${PrayerTimeCalculator.toBanglaDigits(p1Total.toString())} • ২য়: ${PrayerTimeCalculator.toBanglaDigits(p2Total.toString())} অধ্যায়"
                } else {
                    "1st: $p1Total • 2nd: $p2Total ch"
                },
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = subjColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(50))
                    .background(ExamRecessedTrack)
            ) {
                if (ratio > 0f) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(ratio.coerceAtLeast(0.05f))
                            .height(4.dp)
                            .clip(RoundedCornerShape(50))
                            .background(if (doneCh == totalCh) StoneSuccess else subjColor)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExamSubjectChaptersBottomSheet(
    subject: TrackerSubject,
    examBestScores: Map<String, Int>,
    selectedPaper: Int,
    onSelectPaper: (Int) -> Unit,
    onSelectChapter: (TrackerChapter) -> Unit,
    onDismiss: () -> Unit
) {
    val s = LocalAppStrings.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val subjectColor = Color(subject.colorHex)
    val subjectTitle = if (s.isBangla) subject.nameBn else subject.nameEn

    val p1Chapters = remember(subject.chapters) { subject.chaptersForPaper(1) }
    val p2Chapters = remember(subject.chapters) { subject.chaptersForPaper(2) }
    val paperChapters = if (selectedPaper == 1) p1Chapters else p2Chapters

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
                .testTag("exam_subject_popup_${subject.id}")
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(subjectColor)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = subjectTitle,
                    style = MaterialTheme.typography.titleLarge,
                    color = StoneInkPrimary
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (s.isBangla) {
                    "প্রতিটি অধ্যায়ে ৩টি HSC বোর্ড স্ট্যান্ডার্ড MCQ প্রশ্ন"
                } else {
                    "Tap any chapter to start 3-question HSC MCQ exam"
                },
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = subjectColor
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 1st Paper | 2nd Paper Segmented Switcher
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val p1Label = if (s.isBangla) {
                    "${s.trackerFirstPaper} (${PrayerTimeCalculator.toBanglaDigits(p1Chapters.size.toString())})"
                } else {
                    "${s.trackerFirstPaper} (${p1Chapters.size})"
                }
                val p2Label = if (s.isBangla) {
                    "${s.trackerSecondPaper} (${PrayerTimeCalculator.toBanglaDigits(p2Chapters.size.toString())})"
                } else {
                    "${s.trackerSecondPaper} (${p2Chapters.size})"
                }
                NeumorphicPillButton(
                    text = p1Label,
                    selected = selectedPaper == 1,
                    onClick = { onSelectPaper(1) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("exam_paper_tab_1_${subject.id}")
                )
                NeumorphicPillButton(
                    text = p2Label,
                    selected = selectedPaper == 2,
                    onClick = { onSelectPaper(2) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("exam_paper_tab_2_${subject.id}")
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Chapters list
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                paperChapters.forEachIndexed { idx, chapter ->
                    val bestScore = examBestScores[chapter.id]
                    val numPrefix = if (s.isBangla) {
                        PrayerTimeCalculator.toBanglaDigits("${idx + 1}.")
                    } else {
                        "${idx + 1}."
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(StonePanelInner)
                            .border(
                                width = 1.dp,
                                color = if (bestScore == 3) StoneSuccess.copy(alpha = 0.6f) else StoneDivider,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable { onSelectChapter(chapter) }
                            .padding(horizontal = 14.dp, vertical = 12.dp)
                            .testTag("exam_chapter_row_${chapter.id}"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "$numPrefix ${chapter.title}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = StoneInkPrimary,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = if (s.isBangla) "৩টি MCQ" else "3 MCQs",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = StoneInkSecondary
                                )
                                if (bestScore != null) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.CheckCircle,
                                            contentDescription = null,
                                            tint = if (bestScore >= 2) StoneSuccess else StoneAccent,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Text(
                                            text = if (s.isBangla) {
                                                "সেরা: ${PrayerTimeCalculator.toBanglaDigits("$bestScore/3")}"
                                            } else {
                                                "Best: $bestScore/3"
                                            },
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = if (bestScore >= 2) StoneSuccess else StoneAccent
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(StoneInkPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.PlayArrow,
                                contentDescription = "Start exam for ${chapter.title}",
                                tint = NeumorphicHighlight,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            HorizontalDivider(color = StoneDivider)
            Spacer(modifier = Modifier.height(10.dp))

            NeumorphicPillButton(
                text = s.cancelButton,
                selected = false,
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("exam_popup_close")
            )
        }
    }
}
