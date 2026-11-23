package com.example.ui.screens

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.PublicOff
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
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

@Composable
fun ExamAccountScreen(
    userName: String,
    batchTag: String,
    totalExamsTaken: Int,
    userCreatedExams: List<CustomPublishedExam>,
    onBack: () -> Unit,
    onCreateExamClick: () -> Unit,
    onTogglePublish: (String) -> Unit,
    onDeleteExam: (String) -> Unit,
    onTakeCustomExam: (CustomPublishedExam) -> Unit,
    onEditExam: (CustomPublishedExam) -> Unit = {},
    onUpdateExam: (CustomPublishedExam) -> Unit = {}
) {
    val s = LocalAppStrings.current
    val publishedCount = userCreatedExams.count { it.isPublished }
    val totalAttemptsOnYours = userCreatedExams.sumOf { it.attemptCount }
    var previewingExamId by remember { mutableStateOf<String?>(null) }

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
                modifier = Modifier.testTag("exam_account_back_btn")
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
                    text = s.examAccountTitle,
                    style = MaterialTheme.typography.titleLarge,
                    color = StoneInkPrimary
                )
                Text(
                    text = s.examAccountSubtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = StoneInkSecondary
                )
            }

            NeumorphicCircleButton(
                onClick = onCreateExamClick,
                size = 42.dp,
                modifier = Modifier.testTag("exam_account_create_btn")
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = s.createExamTitle,
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
                .testTag("exam_account_list")
        ) {
            // Creator Account Card
            item(key = "account_profile_card") {
                NeumorphicCard(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 24.dp,
                    shadowElevation = 7.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(StoneInkPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Person,
                                    contentDescription = null,
                                    tint = NeumorphicHighlight,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = userName.ifBlank { "Student" },
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = StoneInkPrimary
                                )
                                Text(
                                    text = "$batchTag • Study Room Exam Creator",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = StoneAccent,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AccountStatBox(
                                label = if (s.isBangla) "তৈরি" else "CREATED",
                                value = userCreatedExams.size.toString(),
                                color = StoneInkPrimary,
                                modifier = Modifier.weight(1f)
                            )
                            AccountStatBox(
                                label = if (s.isBangla) "প্রকাশিত" else "PUBLISHED",
                                value = publishedCount.toString(),
                                color = StoneSuccess,
                                modifier = Modifier.weight(1f)
                            )
                            AccountStatBox(
                                label = if (s.isBangla) "অংশগ্রহণ" else "ATTEMPTS",
                                value = totalAttemptsOnYours.toString(),
                                color = StoneAccent,
                                modifier = Modifier.weight(1f)
                            )
                            AccountStatBox(
                                label = if (s.isBangla) "সম্পন্ন" else "TAKEN",
                                value = totalExamsTaken.toString(),
                                color = StoneInkPrimary,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            item(key = "my_exams_header") {
                Text(
                    text = if (s.isBangla) "আমার তৈরি পরীক্ষাসমূহ" else "MY CREATED EXAMS",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.1.sp,
                    color = StoneInkSecondary,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }

            if (userCreatedExams.isEmpty()) {
                item(key = "empty_account_exams") {
                    NeumorphicCard(
                        modifier = Modifier.fillMaxWidth(),
                        cornerRadius = 22.dp,
                        shadowElevation = 5.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = if (s.isBangla) "এখনো কোনো পরীক্ষা তৈরি করেননি" else "No custom exams created yet",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = StoneInkPrimary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (s.isBangla) "নতুন HSC পরীক্ষা তৈরি করে Exam Ground-এ প্রকাশ করুন।" else "Build an HSC exam with optional Uddipok, rules & password and publish it to Exam Ground.",
                                style = MaterialTheme.typography.bodySmall,
                                color = StoneInkSecondary
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            NeumorphicPillButton(
                                text = s.createExamTitle,
                                selected = true,
                                onClick = onCreateExamClick,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            } else {
                items(userCreatedExams, key = { it.id }) { exam ->
                    val accent = Color(exam.subjectColorHex)
                    NeumorphicCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("exam_account_item_${exam.id}"),
                        cornerRadius = 22.dp,
                        shadowElevation = 6.dp
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
                                            .background(accent)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    val styleBadge = if (exam.paperStyle == ExamPaperStyle.PLAIN_FLOOR) "Plain Floor" else "Boxed Cards"
                                    Text(
                                        text = "${exam.subjectName} • $styleBadge",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = accent
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (exam.isPublished) StoneSuccess.copy(alpha = 0.14f) else StonePanelInner)
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = if (exam.isPublished) "PUBLISHED" else "DRAFT",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (exam.isPublished) StoneSuccess else StoneInkSecondary
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                val subMeta = if (exam.chapterTitle.isNotBlank()) {
                                    "${exam.chapterTitle} • ${exam.questions.size} Qs"
                                } else {
                                    "${exam.questions.size} Qs"
                                }
                                Text(
                                    text = subMeta,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = StoneInkSecondary
                                )
                                if (exam.password.isNotBlank()) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = Icons.Rounded.Lock,
                                        contentDescription = "Password Protected",
                                        tint = StoneAccent,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Row 1: Edit Full Exam | Preview & Edit/Delete Qs | Delete Exam
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .testTag("exam_account_edit_${exam.id}")
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(StoneAccent.copy(alpha = 0.14f))
                                        .border(1.dp, StoneAccent.copy(alpha = 0.45f), RoundedCornerShape(12.dp))
                                        .clickable { onEditExam(exam) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Rounded.Edit,
                                            contentDescription = "Edit Exam",
                                            tint = StoneAccent,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (s.isBangla) "সম্পাদনা" else "Edit Exam",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = StoneAccent,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .testTag("exam_account_preview_qs_${exam.id}")
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(StonePanelInner)
                                        .border(1.dp, StoneDivider, RoundedCornerShape(12.dp))
                                        .clickable { previewingExamId = exam.id },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Rounded.Visibility,
                                            contentDescription = "Edit Questions in Preview",
                                            tint = StoneInkPrimary,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (s.isBangla) "প্রশ্ন দেখুন" else "Edit Qs",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = StoneInkPrimary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }

                                IconButton(
                                    onClick = { onDeleteExam(exam.id) },
                                    modifier = Modifier
                                        .size(48.dp)
                                        .testTag("exam_account_delete_${exam.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.DeleteOutline,
                                        contentDescription = "Delete Exam",
                                        tint = StoneInkSecondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Row 2: Publish/Unpublish | Take / Test Exam
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .testTag("exam_account_toggle_pub_${exam.id}")
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(StonePanelInner)
                                        .border(1.dp, StoneDivider, RoundedCornerShape(12.dp))
                                        .clickable { onTogglePublish(exam.id) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = if (exam.isPublished) Icons.Rounded.PublicOff else Icons.Rounded.Public,
                                            contentDescription = null,
                                            tint = if (exam.isPublished) StoneInkSecondary else StoneSuccess,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (exam.isPublished) "Unpublish" else "Publish",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = if (exam.isPublished) StoneInkPrimary else StoneSuccess
                                        )
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .testTag("exam_account_play_${exam.id}")
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(StoneInkPrimary)
                                        .clickable { onTakeCustomExam(exam) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Rounded.PlayArrow,
                                            contentDescription = null,
                                            tint = NeumorphicHighlight,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (s.isBangla) "টেস্ট দিন" else "Test Exam",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = NeumorphicHighlight
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

    val previewingExam = previewingExamId?.let { targetId ->
        userCreatedExams.firstOrNull { it.id == targetId }
    }
    if (previewingExam != null) {
        ExamCreatorPreviewDialog(
            subjectName = previewingExam.subjectName,
            examHeading = previewingExam.title,
            authorName = previewingExam.authorName,
            paperStyle = previewingExam.paperStyle,
            password = previewingExam.password,
            description = previewingExam.description,
            rules = previewingExam.rules,
            questions = previewingExam.questions,
            onDismiss = { previewingExamId = null },
            onPublishNow = {
                if (!previewingExam.isPublished) {
                    onUpdateExam(previewingExam.copy(isPublished = true))
                }
            },
            onQuestionsChanged = { updatedQuestions ->
                onUpdateExam(previewingExam.copy(questions = updatedQuestions))
            },
            onPaperStyleChanged = { nextStyle ->
                onUpdateExam(previewingExam.copy(paperStyle = nextStyle))
            },
            primaryActionText = if (previewingExam.isPublished) {
                if (s.isBangla) "সম্পন্ন" else "Done"
            } else {
                s.examPublishAction
            }
        )
    }
}

@Composable
private fun AccountStatBox(
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
            .padding(vertical = 10.dp, horizontal = 4.dp),
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
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            fontFamily = BarlowCondensedFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            color = color
        )
    }
}
