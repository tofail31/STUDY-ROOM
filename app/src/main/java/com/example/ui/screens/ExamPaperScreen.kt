package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.rounded.AssignmentTurnedIn
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.calc.PrayerTimeCalculator
import com.example.data.model.ExamPaperStage
import com.example.data.model.ExamPaperStyle
import com.example.data.model.ExamQuestion
import com.example.data.model.ExamUiState
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
import com.example.ui.theme.StonePanel
import com.example.ui.theme.StonePanelInner
import com.example.ui.theme.StoneSuccess
import java.util.Locale

private val ExamWrongRed = Color(0xFFC85545)
private val ExamPaperTrack = Color(0xFFC9C2BA)
private val OptionLettersBn = listOf("ক", "খ", "গ", "ঘ")

@Composable
fun ExamPaperScreen(
    examState: ExamUiState,
    onBack: () -> Unit,
    onSelectOption: (questionId: String, optionIndex: Int) -> Unit,
    onSubmitExam: () -> Unit,
    onShowAnswers: () -> Unit,
    onShowResultPaper: () -> Unit,
    onRetakeExam: () -> Unit,
    onSwitchPaperStyle: (ExamPaperStyle) -> Unit = {}
) {
    val s = LocalAppStrings.current
    val isPlainFloor = examState.paperStyle == ExamPaperStyle.PLAIN_FLOOR

    BackHandler {
        if (examState.stage == ExamPaperStage.REVIEW_ANSWERS) {
            onShowResultPaper()
        } else {
            onBack()
        }
    }

    val subjectName = if (s.isBangla) examState.activeSubjectNameBn else examState.activeSubjectNameEn
    val isCustomCreatedExam = examState.activeChapterId.startsWith("custom_exam_")
    val paperLabel = when {
        examState.isWrongAnswersExam -> if (s.isBangla) "পুনঃপরীক্ষা" else "Review Set"
        isCustomCreatedExam -> if (s.isBangla) "কাস্টম পরীক্ষা" else "Custom Exam"
        examState.activePaper == 1 -> s.trackerFirstPaper
        else -> s.trackerSecondPaper
    }
    val totalCount = examState.questions.size.coerceAtLeast(1)
    val answeredCount = examState.selectedAnswers.size

    Column(
        modifier = Modifier
            .fillMaxSize()
            .then(
                if (isPlainFloor && examState.stage != ExamPaperStage.RESULT) {
                    Modifier.background(StoneBackground)
                } else {
                    Modifier.background(
                        Brush.verticalGradient(listOf(StoneBackgroundTop, StoneBackground, StoneBackgroundBottom))
                    )
                }
            )
            .statusBarsPadding()
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        // Top Header Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            NeumorphicCircleButton(
                onClick = {
                    if (examState.stage == ExamPaperStage.REVIEW_ANSWERS) {
                        onShowResultPaper()
                    } else {
                        onBack()
                    }
                },
                size = 42.dp,
                modifier = Modifier.testTag("exam_paper_back_button")
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
                    text = "$subjectName • $paperLabel",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = StoneInkPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = examState.activeChapterTitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = StoneInkSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Live Timer Pill
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(StonePanelInner)
                    .border(1.dp, StoneDivider, RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Timer,
                    contentDescription = null,
                    tint = StoneAccent,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = formatExamTimer(examState.elapsedSeconds),
                    fontFamily = BarlowCondensedFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = StoneInkPrimary,
                    modifier = Modifier.testTag("exam_timer_text")
                )
            }
        }

        when (examState.stage) {
            ExamPaperStage.TAKING, ExamPaperStage.REVIEW_ANSWERS -> {
                val isReview = examState.stage == ExamPaperStage.REVIEW_ANSWERS

                // Single-page Top-to-Bottom Scrollable Exam Sheet
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Column(
                        modifier = Modifier
                            .widthIn(max = 680.dp)
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 18.dp, vertical = 6.dp)
                            .testTag(if (isReview) "exam_review_scroll" else "exam_paper_scroll"),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        if (isPlainFloor) {
                            // TYPE 2: PLAIN FLOOR (No boxes, pure text paper layout)
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                                    .testTag("exam_plain_floor_header")
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (isReview) {
                                            if (s.isBangla) "উত্তরপত্র পর্যালোচনা" else "ANSWER REVIEW SHEET"
                                        } else {
                                            "$subjectName • $paperLabel"
                                        },
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.9.sp,
                                        color = if (isReview) StoneSuccess else StoneAccent
                                    )

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Text(
                                            text = if (isReview) {
                                                "Score: ${examState.correctCount}/$totalCount"
                                            } else {
                                                "$answeredCount/$totalCount Answered"
                                            },
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = StoneInkPrimary
                                        )
                                        Text(
                                            text = if (s.isBangla) "বক্স মোড" else "Box View",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            textDecoration = TextDecoration.Underline,
                                            color = StoneAccent,
                                            modifier = Modifier
                                                .clickable { onSwitchPaperStyle(ExamPaperStyle.CLASSIC_CARDS) }
                                                .padding(vertical = 4.dp)
                                                .testTag("exam_switch_to_cards_btn")
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = examState.activeChapterTitle,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = StoneInkPrimary
                                )
                                Text(
                                    text = if (s.isBangla) {
                                        "মোট প্রশ্ন: ${PrayerTimeCalculator.toBanglaDigits(totalCount.toString())} • পূর্ণমান: ${PrayerTimeCalculator.toBanglaDigits(totalCount.toString())}"
                                    } else {
                                        "Total Questions: $totalCount • Full Marks: $totalCount"
                                    },
                                    style = MaterialTheme.typography.labelSmall,
                                    color = StoneInkSecondary
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                HorizontalDivider(thickness = 1.5.dp, color = StoneInkPrimary.copy(alpha = 0.25f))
                            }

                            examState.questions.forEachIndexed { index, question ->
                                PlainFloorQuestionItem(
                                    index = index + 1,
                                    question = question,
                                    selectedOptionIndex = examState.selectedAnswers[question.id],
                                    isReviewMode = isReview,
                                    isBangla = s.isBangla,
                                    onSelectOption = { optIdx ->
                                        onSelectOption(question.id, optIdx)
                                    }
                                )
                                if (index < examState.questions.lastIndex) {
                                    HorizontalDivider(color = StoneDivider)
                                }
                            }
                        } else {
                            // TYPE 1: CLASSIC BOXED CARDS (As it is)
                            NeumorphicCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("exam_boxed_cards_header"),
                                cornerRadius = 20.dp,
                                shadowElevation = 6.dp
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 18.dp, vertical = 14.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = if (isReview) {
                                                if (s.isBangla) "উত্তরপত্র পর্যালোচনা (ANSWER SHEET)" else "ANSWER REVIEW SHEET"
                                            } else {
                                                if (s.isBangla) "STUDY ROOM • প্রশ্নপত্র" else "STUDY ROOM • QUESTION PAPER"
                                            },
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 1.0.sp,
                                            color = if (isReview) StoneSuccess else StoneAccent,
                                            modifier = Modifier.weight(1f)
                                        )

                                        val badgeText = if (isReview) {
                                            if (s.isBangla) {
                                                "নম্বর: ${PrayerTimeCalculator.toBanglaDigits("${examState.correctCount}/$totalCount")}"
                                            } else {
                                                "Score: ${examState.correctCount}/$totalCount"
                                            }
                                        } else {
                                            if (s.isBangla) {
                                                "উত্তর: ${PrayerTimeCalculator.toBanglaDigits("$answeredCount/$totalCount")}"
                                            } else {
                                                "Answered: $answeredCount/$totalCount"
                                            }
                                        }
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(if (isReview) StoneSuccess.copy(alpha = 0.14f) else StonePanel)
                                                .padding(horizontal = 9.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = badgeText,
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isReview) StoneSuccess else StoneInkPrimary
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = examState.activeChapterTitle,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = StoneInkPrimary
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = if (s.isBangla) {
                                                "$subjectName ($paperLabel) • মোট প্রশ্ন: ${PrayerTimeCalculator.toBanglaDigits(totalCount.toString())}"
                                            } else {
                                                "$subjectName ($paperLabel) • Total Questions: $totalCount"
                                            },
                                            style = MaterialTheme.typography.labelSmall,
                                            color = StoneInkSecondary
                                        )
                                        Text(
                                            text = if (s.isBangla) "প্লেইন টেক্সট" else "Plain View",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = StoneAccent,
                                            modifier = Modifier
                                                .clickable { onSwitchPaperStyle(ExamPaperStyle.PLAIN_FLOOR) }
                                                .padding(horizontal = 4.dp, vertical = 2.dp)
                                                .testTag("exam_switch_to_plain_btn")
                                        )
                                    }
                                }
                            }

                            // All Questions Stacked Top-to-Bottom on One Scrollable Page
                            examState.questions.forEachIndexed { index, question ->
                                SinglePageQuestionCard(
                                    index = index + 1,
                                    total = examState.questions.size,
                                    question = question,
                                    selectedOptionIndex = examState.selectedAnswers[question.id],
                                    isReviewMode = isReview,
                                    isBangla = s.isBangla,
                                    onSelectOption = { optIdx ->
                                        onSelectOption(question.id, optIdx)
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }

                // Sticky Bottom Action Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(StonePanel.copy(alpha = 0.96f))
                        .border(width = 1.dp, color = StoneDivider)
                        .padding(horizontal = 18.dp, vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (!isReview) {
                        Row(
                            modifier = Modifier
                                .widthIn(max = 680.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = if (s.isBangla) {
                                        "${PrayerTimeCalculator.toBanglaDigits("$answeredCount/$totalCount")} দাগানো হয়েছে"
                                    } else {
                                        "$answeredCount of $totalCount selected"
                                    },
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = StoneInkPrimary
                                )
                                Text(
                                    text = if (s.isBangla) "উপর থেকে নিচে স্ক্রল করুন" else "Scroll top to bottom",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = StoneInkSecondary
                                )
                            }

                            NeumorphicPillButton(
                                text = s.examSubmit,
                                selected = true,
                                leadingContent = {
                                    Icon(
                                        imageVector = Icons.Rounded.AssignmentTurnedIn,
                                        contentDescription = null,
                                        tint = StoneInkPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                onClick = onSubmitExam,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("exam_submit_button")
                            )
                        }
                    } else {
                        Row(
                            modifier = Modifier
                                .widthIn(max = 680.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            NeumorphicPillButton(
                                text = if (s.isBangla) "ফলাফল পত্র" else "Result Paper",
                                selected = false,
                                onClick = onShowResultPaper,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("exam_view_result_btn")
                            )
                            NeumorphicPillButton(
                                text = s.examRetake,
                                selected = true,
                                leadingContent = {
                                    Icon(
                                        imageVector = Icons.Rounded.Refresh,
                                        contentDescription = null,
                                        tint = StoneInkPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                onClick = onRetakeExam,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("exam_review_retake_btn")
                            )
                        }
                    }
                }
            }

            ExamPaperStage.RESULT -> {
                ExamResultPaperContent(
                    examState = examState,
                    subjectName = subjectName,
                    paperLabel = paperLabel,
                    onShowAnswers = onShowAnswers,
                    onRetakeExam = onRetakeExam,
                    onBackToChapters = onBack,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun SinglePageQuestionCard(
    index: Int,
    total: Int,
    question: ExamQuestion,
    selectedOptionIndex: Int?,
    isReviewMode: Boolean,
    isBangla: Boolean,
    onSelectOption: (Int) -> Unit
) {
    val s = LocalAppStrings.current
    val isCorrect = selectedOptionIndex != null && selectedOptionIndex == question.correctIndex
    val isSkipped = selectedOptionIndex == null

    val qHeaderLabel = if (isBangla) {
        "প্রশ্ন ${PrayerTimeCalculator.toBanglaDigits(index.toString())}/${PrayerTimeCalculator.toBanglaDigits(total.toString())}"
    } else {
        "Q$index of $total"
    }

    NeumorphicCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("exam_question_card_$index"),
        cornerRadius = 22.dp,
        shadowElevation = 6.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(StoneInkPrimary)
                        .padding(horizontal = 9.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = qHeaderLabel,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = NeumorphicHighlight
                    )
                }

                if (isReviewMode) {
                    val (statusText, statusColor) = when {
                        isCorrect -> (if (isBangla) "সঠিক (+১)" else "Correct (+1)") to StoneSuccess
                        isSkipped -> s.examSkippedAnswer to StoneInkSecondary
                        else -> (if (isBangla) "ভুল (০)" else "Wrong (0)") to ExamWrongRed
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(statusColor.copy(alpha = 0.14f))
                            .border(1.dp, statusColor.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 9.dp, vertical = 3.dp)
                            .testTag("exam_review_status_$index")
                    ) {
                        Text(
                            text = statusText,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = statusColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (question.uddipok.isNotBlank()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(StonePanelInner)
                        .border(1.dp, StoneAccent.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                        .testTag("exam_uddipok_box_$index")
                ) {
                    Text(
                        text = if (isBangla) "উদ্দীপক (Stimulus)" else "UDDIPOK / STIMULUS",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = StoneAccent
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = question.uddipok,
                        style = MaterialTheme.typography.bodyMedium,
                        color = StoneInkPrimary,
                        lineHeight = 20.sp
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            Text(
                text = question.questionText,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 23.sp,
                color = StoneInkPrimary
            )

            Spacer(modifier = Modifier.height(14.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                question.options.forEachIndexed { optIdx, optionText ->
                    val isUserChoice = selectedOptionIndex == optIdx
                    val isRightChoice = question.correctIndex == optIdx
                    val optionTagLetter = OptionLettersBn.getOrElse(optIdx) { ('A' + optIdx).toString() }

                    val bgColor = when {
                        !isReviewMode && isUserChoice -> StoneInkPrimary
                        !isReviewMode -> StonePanelInner
                        isRightChoice -> StoneSuccess.copy(alpha = 0.16f)
                        isUserChoice && !isRightChoice -> ExamWrongRed.copy(alpha = 0.14f)
                        else -> StonePanelInner
                    }

                    val borderColor = when {
                        !isReviewMode && isUserChoice -> StoneInkPrimary
                        !isReviewMode -> StoneDivider
                        isRightChoice -> StoneSuccess
                        isUserChoice && !isRightChoice -> ExamWrongRed
                        else -> StoneDivider
                    }

                    val textColor = when {
                        !isReviewMode && isUserChoice -> NeumorphicHighlight
                        else -> StoneInkPrimary
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(bgColor)
                            .border(width = 1.2.dp, color = borderColor, shape = RoundedCornerShape(14.dp))
                            .clickable(enabled = !isReviewMode) { onSelectOption(optIdx) }
                            .padding(horizontal = 12.dp, vertical = 11.dp)
                            .testTag("exam_q_${question.id}_opt_$optIdx"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Bubble letter
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        !isReviewMode && isUserChoice -> StoneAccent
                                        isReviewMode && isRightChoice -> StoneSuccess
                                        isReviewMode && isUserChoice -> ExamWrongRed
                                        else -> StonePanel
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = optionTagLetter,
                                fontFamily = PlusJakartaSansFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = if ((!isReviewMode && isUserChoice) || (isReviewMode && (isRightChoice || isUserChoice))) {
                                    NeumorphicHighlight
                                } else {
                                    StoneInkPrimary
                                }
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = optionText,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (isUserChoice || (isReviewMode && isRightChoice)) FontWeight.Bold else FontWeight.Medium,
                                color = textColor
                            )
                            if (isReviewMode && (isUserChoice || isRightChoice)) {
                                Spacer(modifier = Modifier.height(3.dp))
                                val markerText = when {
                                    isUserChoice && isRightChoice -> "${s.examYourAnswer} • ${s.examCorrectAnswer}"
                                    isRightChoice -> s.examCorrectAnswer
                                    else -> s.examYourAnswer
                                }
                                Text(
                                    text = markerText,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isRightChoice) StoneSuccess else ExamWrongRed,
                                    modifier = Modifier.testTag("exam_marker_${question.id}_$optIdx")
                                )
                            }
                        }

                        if (isReviewMode) {
                            if (isRightChoice) {
                                Icon(
                                    imageVector = Icons.Rounded.CheckCircle,
                                    contentDescription = s.examCorrectAnswer,
                                    tint = StoneSuccess,
                                    modifier = Modifier.size(20.dp)
                                )
                            } else if (isUserChoice) {
                                Icon(
                                    imageVector = Icons.Rounded.Cancel,
                                    contentDescription = s.examYourAnswer,
                                    tint = ExamWrongRed,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }

            if (isReviewMode && question.explanation.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(StoneAccent.copy(alpha = 0.10f))
                        .border(1.dp, StoneAccent.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                        .testTag("exam_explanation_$index"),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Lightbulb,
                        contentDescription = null,
                        tint = StoneAccent,
                        modifier = Modifier
                            .size(16.dp)
                            .padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = s.examExplanation,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = StoneAccent
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = question.explanation,
                            style = MaterialTheme.typography.bodySmall,
                            color = StoneInkPrimary,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PlainFloorQuestionItem(
    index: Int,
    question: ExamQuestion,
    selectedOptionIndex: Int?,
    isReviewMode: Boolean,
    isBangla: Boolean,
    onSelectOption: (Int) -> Unit
) {
    val s = LocalAppStrings.current
    val isCorrect = selectedOptionIndex != null && selectedOptionIndex == question.correctIndex
    val isSkipped = selectedOptionIndex == null

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .testTag("exam_plain_question_$index"),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (question.uddipok.isNotBlank()) {
            Text(
                text = if (isBangla) "উদ্দীপক: ${question.uddipok}" else "Uddipok: ${question.uddipok}",
                style = MaterialTheme.typography.bodyMedium,
                fontStyle = FontStyle.Italic,
                color = StoneInkSecondary,
                lineHeight = 21.sp,
                modifier = Modifier.testTag("exam_uddipok_box_$index")
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            val numberStr = if (isBangla) {
                "${PrayerTimeCalculator.toBanglaDigits(index.toString())}।"
            } else {
                "$index."
            }
            Text(
                text = "$numberStr ${question.questionText}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                lineHeight = 23.sp,
                color = StoneInkPrimary,
                modifier = Modifier.weight(1f)
            )

            if (isReviewMode) {
                val (statusText, statusColor) = when {
                    isCorrect -> (if (isBangla) "সঠিক (+১)" else "Correct (+1)") to StoneSuccess
                    isSkipped -> s.examSkippedAnswer to StoneInkSecondary
                    else -> (if (isBangla) "ভুল (০)" else "Wrong (0)") to ExamWrongRed
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = statusColor,
                    modifier = Modifier.testTag("exam_review_status_$index")
                )
            }
        }

        // Plain text options on floor — NO boxes, NO borders, NO background shapes
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 6.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            question.options.forEachIndexed { optIdx, optionText ->
                val isUserChoice = selectedOptionIndex == optIdx
                val isRightChoice = question.correctIndex == optIdx
                val letter = OptionLettersBn.getOrElse(optIdx) { ('A' + optIdx).toString() }

                val optTextColor = when {
                    !isReviewMode && isUserChoice -> StoneAccent
                    !isReviewMode -> StoneInkPrimary
                    isRightChoice -> StoneSuccess
                    isUserChoice && !isRightChoice -> ExamWrongRed
                    else -> StoneInkSecondary
                }

                val prefixSymbol = when {
                    !isReviewMode && isUserChoice -> "✓ "
                    isReviewMode && isRightChoice -> "✓ "
                    isReviewMode && isUserChoice -> "✕ "
                    else -> ""
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("exam_q_${question.id}_opt_$optIdx")
                        .clickable(enabled = !isReviewMode) { onSelectOption(optIdx) }
                        .padding(vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$prefixSymbol$letter)  $optionText",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (isUserChoice || (isReviewMode && isRightChoice)) FontWeight.Bold else FontWeight.Normal,
                        color = optTextColor,
                        textDecoration = if (!isReviewMode && isUserChoice) TextDecoration.Underline else TextDecoration.None,
                        modifier = Modifier.weight(1f)
                    )

                    if (isReviewMode && (isUserChoice || isRightChoice)) {
                        val markerText = when {
                            isUserChoice && isRightChoice -> "${s.examYourAnswer} • ${s.examCorrectAnswer}"
                            isRightChoice -> s.examCorrectAnswer
                            else -> s.examYourAnswer
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = markerText,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isRightChoice) StoneSuccess else ExamWrongRed,
                            modifier = Modifier.testTag("exam_marker_${question.id}_$optIdx")
                        )
                    }
                }
            }
        }

        if (isReviewMode && question.explanation.isNotBlank()) {
            Text(
                text = "${s.examExplanation}: ${question.explanation}",
                style = MaterialTheme.typography.bodySmall,
                color = StoneInkSecondary,
                lineHeight = 18.sp,
                modifier = Modifier
                    .padding(top = 2.dp)
                    .testTag("exam_explanation_$index")
            )
        }
    }
}

@Composable
private fun ExamResultPaperContent(
    examState: ExamUiState,
    subjectName: String,
    paperLabel: String,
    onShowAnswers: () -> Unit,
    onRetakeExam: () -> Unit,
    onBackToChapters: () -> Unit,
    modifier: Modifier = Modifier
) {
    val s = LocalAppStrings.current
    val total = examState.questions.size.coerceAtLeast(1)
    val scorePct = ((examState.correctCount.toFloat() / total.toFloat()) * 100f).toInt()
    val animRatio by animateFloatAsState(
        targetValue = (examState.correctCount.toFloat() / total.toFloat()).coerceIn(0f, 1f),
        label = "exam_score_ring"
    )

    val gradeLabel = when {
        scorePct >= 90 -> if (s.isBangla) "অসাধারণ! (A+)" else "Outstanding! (A+)"
        scorePct >= 66 -> if (s.isBangla) "চমৎকার হয়েছে! (A)" else "Great Job! (A)"
        scorePct >= 33 -> if (s.isBangla) "আরও অনুশীলন প্রয়োজন" else "Keep Practicing"
        else -> if (s.isBangla) "পুনরায় চেষ্টা করুন" else "Needs Revision"
    }
    val gradeColor = if (scorePct >= 66) StoneSuccess else if (scorePct >= 33) StoneAccent else ExamWrongRed

    Box(
        modifier = modifier,
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 640.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 10.dp)
                .testTag("exam_result_paper"),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            NeumorphicCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 26.dp,
                shadowElevation = 9.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 22.dp, vertical = 22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = s.examResultPaperTitle,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.4.sp,
                        color = StoneInkSecondary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = examState.activeChapterTitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = StoneInkPrimary
                    )
                    Text(
                        text = "$subjectName • $paperLabel",
                        style = MaterialTheme.typography.labelMedium,
                        color = StoneInkSecondary
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Score Ring
                    Box(
                        modifier = Modifier.size(132.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.size(132.dp)) {
                            val strokePx = 11.dp.toPx()
                            val d = size.minDimension - strokePx
                            drawArc(
                                color = ExamPaperTrack,
                                startAngle = 0f,
                                sweepAngle = 360f,
                                useCenter = false,
                                topLeft = Offset(strokePx / 2f, strokePx / 2f),
                                size = Size(d, d),
                                style = Stroke(width = strokePx, cap = StrokeCap.Round)
                            )
                            if (animRatio > 0.005f) {
                                drawArc(
                                    color = gradeColor,
                                    startAngle = -90f,
                                    sweepAngle = 360f * animRatio,
                                    useCenter = false,
                                    topLeft = Offset(strokePx / 2f, strokePx / 2f),
                                    size = Size(d, d),
                                    style = Stroke(width = strokePx, cap = StrokeCap.Round)
                                )
                            }
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = if (s.isBangla) {
                                    PrayerTimeCalculator.toBanglaDigits("${examState.correctCount}/$total")
                                } else {
                                    "${examState.correctCount} / $total"
                                },
                                fontFamily = BarlowCondensedFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 36.sp,
                                color = StoneInkPrimary,
                                modifier = Modifier.testTag("exam_result_score_text")
                            )
                            Text(
                                text = if (s.isBangla) "${PrayerTimeCalculator.toBanglaDigits(scorePct.toString())}%" else "$scorePct%",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = gradeColor
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(gradeColor.copy(alpha = 0.14f))
                            .border(1.dp, gradeColor.copy(alpha = 0.45f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = gradeLabel,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = gradeColor
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))
                    HorizontalDivider(color = StoneDivider)
                    Spacer(modifier = Modifier.height(16.dp))

                    // 4 Mini Stat Boxes (Correct / Wrong / Skipped / Time)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ResultStatMiniBox(
                            label = if (s.isBangla) "সঠিক" else "CORRECT",
                            value = if (s.isBangla) PrayerTimeCalculator.toBanglaDigits(examState.correctCount.toString()) else examState.correctCount.toString(),
                            color = StoneSuccess,
                            modifier = Modifier.weight(1f)
                        )
                        ResultStatMiniBox(
                            label = if (s.isBangla) "ভুল" else "WRONG",
                            value = if (s.isBangla) PrayerTimeCalculator.toBanglaDigits(examState.wrongCount.toString()) else examState.wrongCount.toString(),
                            color = ExamWrongRed,
                            modifier = Modifier.weight(1f)
                        )
                        ResultStatMiniBox(
                            label = if (s.isBangla) "বাদ" else "SKIPPED",
                            value = if (s.isBangla) PrayerTimeCalculator.toBanglaDigits(examState.skippedCount.toString()) else examState.skippedCount.toString(),
                            color = StoneInkSecondary,
                            modifier = Modifier.weight(1f)
                        )
                        ResultStatMiniBox(
                            label = if (s.isBangla) "সময়" else "TIME",
                            value = formatExamTimer(examState.elapsedSeconds),
                            color = StoneAccent,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Primary Action: Show Answer -> opens Exam Page in Review mode showing selected + correct answer
            NeumorphicPillButton(
                text = s.examShowAnswer,
                selected = true,
                leadingContent = {
                    Icon(
                        imageVector = Icons.Rounded.Visibility,
                        contentDescription = null,
                        tint = StoneInkPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                },
                onClick = onShowAnswers,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("exam_show_answers_btn")
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                NeumorphicPillButton(
                    text = s.examRetake,
                    selected = false,
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Rounded.Refresh,
                            contentDescription = null,
                            tint = StoneInkPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    onClick = onRetakeExam,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("exam_retake_btn")
                )
                NeumorphicPillButton(
                    text = s.examBackToChapters,
                    selected = false,
                    onClick = onBackToChapters,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("exam_back_btn")
                )
            }
        }
    }
}

@Composable
private fun ResultStatMiniBox(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(StonePanelInner)
            .border(1.dp, StoneDivider, RoundedCornerShape(14.dp))
            .padding(vertical = 10.dp, horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = StoneInkSecondary,
            maxLines = 1
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontFamily = BarlowCondensedFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            color = color
        )
    }
}

private fun formatExamTimer(seconds: Long): String {
    val safe = seconds.coerceAtLeast(0L)
    val mins = safe / 60L
    val secs = safe % 60L
    return String.format(Locale.US, "%02d:%02d", mins, secs)
}
