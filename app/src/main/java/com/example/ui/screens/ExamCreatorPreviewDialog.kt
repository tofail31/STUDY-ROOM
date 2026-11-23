package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.DoneAll
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Publish
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.ExamPaperStyle
import com.example.data.model.ExamQuestion
import com.example.ui.components.NeumorphicCard
import com.example.ui.components.NeumorphicCircleButton
import com.example.ui.components.NeumorphicPillButton
import com.example.ui.theme.LocalAppStrings
import com.example.ui.theme.NeumorphicHighlight
import com.example.ui.theme.PlusJakartaSansFamily
import com.example.ui.theme.StoneAccent
import com.example.ui.theme.StoneBackground
import com.example.ui.theme.StoneBackgroundBottom
import com.example.ui.theme.StoneBackgroundTop
import com.example.ui.theme.StoneDivider
import com.example.ui.theme.StoneInkPrimary
import com.example.ui.theme.StoneInkSecondary
import com.example.ui.theme.StonePanel
import com.example.ui.theme.StonePanelInner
import com.example.ui.theme.StoneSuccess

private val PreviewLettersBn = listOf("ক", "খ", "গ", "ঘ")
private val PreviewErrorColor = Color(0xFFC85545)

private data class QuestionEditDraft(
    val id: String,
    val uddipok: String,
    val questionText: String,
    val opt0: String,
    val opt1: String,
    val opt2: String,
    val opt3: String,
    val correctIndex: Int,
    val explanation: String
) {
    fun toExamQuestionOrNull(base: ExamQuestion?): ExamQuestion? {
        val qClean = questionText.trim()
        val o0 = opt0.trim()
        val o1 = opt1.trim()
        val o2 = opt2.trim()
        val o3 = opt3.trim()
        if (qClean.isEmpty() || o0.isEmpty() || o1.isEmpty() || o2.isEmpty() || o3.isEmpty()) {
            return null
        }
        return ExamQuestion(
            id = base?.id ?: id,
            subjectId = base?.subjectId ?: "custom_subj",
            chapterId = base?.chapterId ?: "",
            paper = base?.paper ?: 1,
            questionText = qClean,
            options = listOf(o0, o1, o2, o3),
            correctIndex = correctIndex.coerceIn(0, 3),
            explanation = explanation.trim(),
            uddipok = uddipok.trim()
        )
    }

    companion object {
        fun from(q: ExamQuestion): QuestionEditDraft = QuestionEditDraft(
            id = q.id,
            uddipok = q.uddipok,
            questionText = q.questionText,
            opt0 = q.options.getOrElse(0) { "" },
            opt1 = q.options.getOrElse(1) { "" },
            opt2 = q.options.getOrElse(2) { "" },
            opt3 = q.options.getOrElse(3) { "" },
            correctIndex = q.correctIndex.coerceIn(0, 3),
            explanation = q.explanation
        )
    }
}

@Composable
fun ExamCreatorPreviewDialog(
    subjectName: String,
    examHeading: String,
    authorName: String,
    paperStyle: ExamPaperStyle,
    password: String,
    description: String,
    rules: String,
    questions: List<ExamQuestion>,
    onDismiss: () -> Unit,
    onPublishNow: () -> Unit,
    onQuestionsChanged: ((List<ExamQuestion>) -> Unit)? = null,
    onPaperStyleChanged: ((ExamPaperStyle) -> Unit)? = null,
    primaryActionText: String? = null
) {
    val s = LocalAppStrings.current
    var previewStyle by remember(paperStyle) { mutableStateOf(paperStyle) }
    val isPlain = previewStyle == ExamPaperStyle.PLAIN_FLOOR

    val workingQuestions = remember { mutableStateListOf<ExamQuestion>() }
    val activeDrafts = remember { mutableStateMapOf<String, QuestionEditDraft>() }
    var editAllMode by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf<String?>(null) }
    var isErrorStatus by remember { mutableStateOf(false) }

    LaunchedEffect(questions) {
        workingQuestions.clear()
        workingQuestions.addAll(questions)
        if (editAllMode) {
            workingQuestions.forEach { q ->
                if (!activeDrafts.containsKey(q.id)) {
                    activeDrafts[q.id] = QuestionEditDraft.from(q)
                }
            }
        }
    }

    val canMutate = onQuestionsChanged != null

    fun openDraftForQuestion(q: ExamQuestion) {
        activeDrafts[q.id] = QuestionEditDraft.from(q)
        isErrorStatus = false
        statusMessage = null
    }

    fun toggleEditAll() {
        if (!editAllMode) {
            workingQuestions.forEach { q ->
                activeDrafts[q.id] = QuestionEditDraft.from(q)
            }
            editAllMode = true
            isErrorStatus = false
            statusMessage = null
        } else {
            editAllMode = false
            activeDrafts.clear()
            isErrorStatus = false
            statusMessage = null
        }
    }

    fun saveSingleQuestion(index: Int, qId: String) {
        val draft = activeDrafts[qId] ?: return
        val existing = workingQuestions.getOrNull(index)
        val built = draft.toExamQuestionOrNull(existing)
        if (built == null) {
            isErrorStatus = true
            statusMessage = if (s.isBangla) {
                "প্রশ্ন ${index + 1}-এর লেখা ও ৪টি অপশন পূরণ করুন।"
            } else {
                "Fill in question ${index + 1} text and all 4 options before saving."
            }
            return
        }
        workingQuestions[index] = built
        activeDrafts.remove(qId)
        onQuestionsChanged?.invoke(workingQuestions.toList())
        isErrorStatus = false
        statusMessage = if (s.isBangla) {
            "প্রশ্ন ${index + 1} আপডেট হয়েছে!"
        } else {
            "Question ${index + 1} updated!"
        }
    }

    fun saveAllEditedQuestions(): Boolean {
        val updated = mutableListOf<ExamQuestion>()
        workingQuestions.forEachIndexed { idx, existing ->
            val draft = activeDrafts[existing.id]
            if (draft != null) {
                val built = draft.toExamQuestionOrNull(existing)
                if (built == null) {
                    isErrorStatus = true
                    statusMessage = if (s.isBangla) {
                        "প্রশ্ন ${idx + 1}-এর লেখা ও ৪টি অপশন পূরণ করুন।"
                    } else {
                        "Complete question ${idx + 1} text and all 4 options before saving all."
                    }
                    return false
                }
                updated.add(built)
            } else {
                updated.add(existing)
            }
        }
        workingQuestions.clear()
        workingQuestions.addAll(updated)
        activeDrafts.clear()
        editAllMode = false
        onQuestionsChanged?.invoke(updated)
        isErrorStatus = false
        statusMessage = if (s.isBangla) {
            "সবগুলো প্রশ্ন সফলভাবে সেভ হয়েছে!"
        } else {
            "All ${updated.size} questions updated!"
        }
        return true
    }

    fun deleteQuestionAt(index: Int) {
        if (index !in workingQuestions.indices) return
        val removed = workingQuestions.removeAt(index)
        activeDrafts.remove(removed.id)
        onQuestionsChanged?.invoke(workingQuestions.toList())
        isErrorStatus = false
        statusMessage = if (s.isBangla) {
            "প্রশ্ন ${index + 1} মুছে ফেলা হয়েছে।"
        } else {
            "Deleted question ${index + 1} (${workingQuestions.size} left)."
        }
    }

    fun addNewBlankQuestionInPreview() {
        val nextNum = workingQuestions.size + 1
        val newId = "pq_${System.currentTimeMillis()}_$nextNum"
        val starter = ExamQuestion(
            id = newId,
            subjectId = "custom_subj",
            chapterId = "",
            paper = 1,
            questionText = if (s.isBangla) "নতুন প্রশ্ন $nextNum" else "New Question $nextNum",
            options = listOf("Option A", "Option B", "Option C", "Option D"),
            correctIndex = 0,
            explanation = "",
            uddipok = ""
        )
        workingQuestions.add(starter)
        activeDrafts[newId] = QuestionEditDraft(
            id = newId,
            uddipok = "",
            questionText = "",
            opt0 = "",
            opt1 = "",
            opt2 = "",
            opt3 = "",
            correctIndex = 0,
            explanation = ""
        )
        onQuestionsChanged?.invoke(workingQuestions.toList())
        isErrorStatus = false
        statusMessage = if (s.isBangla) {
            "নতুন প্রশ্ন যোগ করা হয়েছে — এখন সম্পাদনা করে সেভ করুন।"
        } else {
            "New question slot added below — fill in & tap Save Q."
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (isPlain) {
                        Modifier.background(StoneBackground)
                    } else {
                        Modifier.background(
                            Brush.verticalGradient(listOf(StoneBackgroundTop, StoneBackground, StoneBackgroundBottom))
                        )
                    }
                )
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
                .testTag("create_exam_preview_dialog")
        ) {
            // Top Preview Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                NeumorphicCircleButton(
                    onClick = onDismiss,
                    size = 42.dp,
                    modifier = Modifier.testTag("create_exam_preview_close_btn")
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "Close Preview",
                        tint = StoneInkPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.Visibility,
                            contentDescription = null,
                            tint = StoneAccent,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = if (s.isBangla) "প্রিভিউ ও প্রশ্ন এডিটর" else "EXAM PREVIEW & EDITOR",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = StoneAccent,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Text(
                        text = "$subjectName • ${workingQuestions.size} Qs",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = StoneInkPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (canMutate && workingQuestions.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .heightIn(min = 38.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (editAllMode) StoneInkPrimary else StonePanelInner)
                            .border(
                                width = 1.dp,
                                color = if (editAllMode) StoneInkPrimary else StoneAccent.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { toggleEditAll() }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("create_exam_preview_edit_all_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (editAllMode) Icons.Rounded.DoneAll else Icons.Rounded.Edit,
                                contentDescription = "Edit All Questions",
                                tint = if (editAllMode) NeumorphicHighlight else StoneAccent,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (editAllMode) {
                                    if (s.isBangla) "এডিট বন্ধ" else "Cancel All"
                                } else {
                                    if (s.isBangla) "সব এডিট" else "Edit All"
                                },
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (editAllMode) NeumorphicHighlight else StoneInkPrimary
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                }

                Text(
                    text = if (isPlain) {
                        if (s.isBangla) "বক্স মোড" else "Box View"
                    } else {
                        if (s.isBangla) "প্লেইন মোড" else "Plain View"
                    },
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    textDecoration = TextDecoration.Underline,
                    color = StoneAccent,
                    modifier = Modifier
                        .clickable {
                            val next = if (isPlain) ExamPaperStyle.CLASSIC_CARDS else ExamPaperStyle.PLAIN_FLOOR
                            previewStyle = next
                            onPaperStyleChanged?.invoke(next)
                        }
                        .padding(horizontal = 6.dp, vertical = 8.dp)
                        .testTag("create_exam_preview_toggle_style")
                )
            }

            // Scrollable Preview Content showing ALL created questions & inline edit/delete controls
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
                        .testTag("create_exam_preview_scroll"),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Status / Validation Banner inside Preview
                    if (!statusMessage.isNullOrBlank()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    if (isErrorStatus) PreviewErrorColor.copy(alpha = 0.14f)
                                    else StoneSuccess.copy(alpha = 0.14f)
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isErrorStatus) PreviewErrorColor.copy(alpha = 0.45f)
                                    else StoneSuccess.copy(alpha = 0.45f),
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                                .testTag("create_exam_preview_status")
                        ) {
                            Text(
                                text = statusMessage.orEmpty(),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isErrorStatus) PreviewErrorColor else StoneSuccess
                            )
                        }
                    }

                    // Bulk Save All Bar when editing multiple / all questions
                    if (canMutate && activeDrafts.isNotEmpty()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(StoneInkPrimary)
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (s.isBangla) {
                                    "${activeDrafts.size}টি প্রশ্ন সম্পাদনা চলছে"
                                } else {
                                    "Editing ${activeDrafts.size} question(s)"
                                },
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = NeumorphicHighlight
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(StoneAccent)
                                    .clickable { saveAllEditedQuestions() }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                                    .testTag("create_exam_preview_save_all_btn")
                            ) {
                                Text(
                                    text = if (s.isBangla) "সব সেভ করুন" else "Save All Changes",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = NeumorphicHighlight
                                )
                            }
                        }
                    }

                    // Metadata Header Summary
                    if (isPlain) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = subjectName,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = StoneAccent
                            )
                            Text(
                                text = examHeading,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = StoneInkPrimary
                            )
                            val meta = buildString {
                                append("By $authorName • ${workingQuestions.size} Questions • Plain Floor")
                                if (password.isNotBlank()) append(" • Password Locked")
                            }
                            Text(
                                text = meta,
                                style = MaterialTheme.typography.labelSmall,
                                color = StoneInkSecondary
                            )
                            if (description.isNotBlank()) {
                                Text(
                                    text = description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = StoneInkPrimary
                                )
                            }
                            if (rules.isNotBlank()) {
                                Text(
                                    text = rules,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = StoneInkSecondary
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            HorizontalDivider(thickness = 1.5.dp, color = StoneInkPrimary.copy(alpha = 0.25f))
                        }
                    } else {
                        NeumorphicCard(
                            modifier = Modifier.fillMaxWidth(),
                            cornerRadius = 20.dp,
                            shadowElevation = 6.dp
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 14.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "$subjectName • Boxed Cards",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = StoneAccent
                                    )
                                    if (password.isNotBlank()) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Rounded.Lock,
                                                contentDescription = null,
                                                tint = StoneAccent,
                                                modifier = Modifier.size(13.dp)
                                            )
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text(
                                                text = "Locked",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = StoneAccent
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = examHeading,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = StoneInkPrimary
                                )
                                Text(
                                    text = "By $authorName • ${workingQuestions.size} Questions",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = StoneInkSecondary
                                )
                                if (description.isNotBlank()) {
                                    HorizontalDivider(color = StoneDivider)
                                    Text(
                                        text = description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = StoneInkPrimary
                                    )
                                }
                                if (rules.isNotBlank()) {
                                    Text(
                                        text = rules,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = StoneInkSecondary
                                    )
                                }
                            }
                        }
                    }

                    if (workingQuestions.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (s.isBangla) {
                                    "এখনো কোনো প্রশ্ন নেই। নিচ থেকে নতুন প্রশ্ন যোগ করুন।"
                                } else {
                                    "No questions yet. Add questions manually, via JSON, or tap + Add Question below."
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = StoneInkSecondary
                            )
                        }
                    } else {
                        workingQuestions.forEachIndexed { idx, q ->
                            val qNumber = idx + 1
                            val draft = activeDrafts[q.id]
                            val isEditingThis = canMutate && draft != null

                            if (isPlain) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp)
                                        .testTag("create_exam_preview_q_$qNumber"),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    PreviewQuestionActionHeader(
                                        qNumber = qNumber,
                                        total = workingQuestions.size,
                                        isEditing = isEditingThis,
                                        canMutate = canMutate,
                                        isBangla = s.isBangla,
                                        onToggleEdit = {
                                            if (isEditingThis) {
                                                activeDrafts.remove(q.id)
                                            } else {
                                                openDraftForQuestion(q)
                                            }
                                        },
                                        onDelete = { deleteQuestionAt(idx) }
                                    )

                                    if (isEditingThis && draft != null) {
                                        InlinePreviewQuestionEditor(
                                            qNumber = qNumber,
                                            draft = draft,
                                            isBangla = s.isBangla,
                                            onDraftChange = { activeDrafts[q.id] = it },
                                            onSaveQuestion = { saveSingleQuestion(idx, q.id) },
                                            onCancelEdit = { activeDrafts.remove(q.id) }
                                        )
                                    } else {
                                        if (q.uddipok.isNotBlank()) {
                                            Text(
                                                text = "উদ্দীপক: ${q.uddipok}",
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontStyle = FontStyle.Italic,
                                                color = StoneInkSecondary
                                            )
                                        }
                                        Text(
                                            text = "$qNumber. ${q.questionText}",
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = StoneInkPrimary
                                        )
                                        q.options.forEachIndexed { optIdx, optText ->
                                            val isRight = optIdx == q.correctIndex
                                            val letter = PreviewLettersBn.getOrElse(optIdx) { ('A' + optIdx).toString() }
                                            Text(
                                                text = (if (isRight) "✓ " else "") + "$letter)  $optText" + (if (isRight) "  [Answer]" else ""),
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = if (isRight) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isRight) StoneSuccess else StoneInkPrimary,
                                                modifier = Modifier.padding(start = 6.dp, top = 2.dp, bottom = 2.dp)
                                            )
                                        }
                                        if (q.explanation.isNotBlank()) {
                                            Text(
                                                text = "${s.examExplanation}: ${q.explanation}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = StoneInkSecondary
                                            )
                                        }
                                    }
                                }
                                if (idx < workingQuestions.lastIndex) {
                                    HorizontalDivider(color = StoneDivider)
                                }
                            } else {
                                NeumorphicCard(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("create_exam_preview_q_$qNumber"),
                                    cornerRadius = 20.dp,
                                    shadowElevation = 5.dp
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 14.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        PreviewQuestionActionHeader(
                                            qNumber = qNumber,
                                            total = workingQuestions.size,
                                            isEditing = isEditingThis,
                                            canMutate = canMutate,
                                            isBangla = s.isBangla,
                                            onToggleEdit = {
                                                if (isEditingThis) {
                                                    activeDrafts.remove(q.id)
                                                } else {
                                                    openDraftForQuestion(q)
                                                }
                                            },
                                            onDelete = { deleteQuestionAt(idx) }
                                        )

                                        if (isEditingThis && draft != null) {
                                            InlinePreviewQuestionEditor(
                                                qNumber = qNumber,
                                                draft = draft,
                                                isBangla = s.isBangla,
                                                onDraftChange = { activeDrafts[q.id] = it },
                                                onSaveQuestion = { saveSingleQuestion(idx, q.id) },
                                                onCancelEdit = { activeDrafts.remove(q.id) }
                                            )
                                        } else {
                                            if (q.uddipok.isNotBlank()) {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clip(RoundedCornerShape(10.dp))
                                                        .background(StonePanelInner)
                                                        .border(1.dp, StoneAccent.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                                                        .padding(10.dp)
                                                ) {
                                                    Text(
                                                        text = q.uddipok,
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = StoneInkPrimary
                                                    )
                                                }
                                            }
                                            Text(
                                                text = q.questionText,
                                                style = MaterialTheme.typography.bodyLarge,
                                                fontWeight = FontWeight.SemiBold,
                                                color = StoneInkPrimary
                                            )
                                            q.options.forEachIndexed { optIdx, optText ->
                                                val isRight = optIdx == q.correctIndex
                                                val letter = PreviewLettersBn.getOrElse(optIdx) { ('A' + optIdx).toString() }
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clip(RoundedCornerShape(12.dp))
                                                        .background(if (isRight) StoneSuccess.copy(alpha = 0.14f) else StonePanelInner)
                                                        .border(
                                                            width = 1.dp,
                                                            color = if (isRight) StoneSuccess else StoneDivider,
                                                            shape = RoundedCornerShape(12.dp)
                                                        )
                                                        .padding(horizontal = 12.dp, vertical = 9.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(24.dp)
                                                            .clip(CircleShape)
                                                            .background(if (isRight) StoneSuccess else StonePanel),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Text(
                                                            text = letter,
                                                            fontSize = 12.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = if (isRight) NeumorphicHighlight else StoneInkPrimary
                                                        )
                                                    }
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text(
                                                        text = optText,
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        fontWeight = if (isRight) FontWeight.Bold else FontWeight.Normal,
                                                        color = StoneInkPrimary,
                                                        modifier = Modifier.weight(1f)
                                                    )
                                                    if (isRight) {
                                                        Icon(
                                                            imageVector = Icons.Rounded.CheckCircle,
                                                            contentDescription = "Correct",
                                                            tint = StoneSuccess,
                                                            modifier = Modifier.size(18.dp)
                                                        )
                                                    }
                                                }
                                            }
                                            if (q.explanation.isNotBlank()) {
                                                Text(
                                                    text = "${s.examExplanation}: ${q.explanation}",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = StoneAccent
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (canMutate) {
                        NeumorphicPillButton(
                            text = if (s.isBangla) "+ প্রিভিউতে নতুন প্রশ্ন যোগ করুন" else "+ Add Question in Preview",
                            selected = false,
                            leadingContent = {
                                Icon(
                                    imageVector = Icons.Rounded.Add,
                                    contentDescription = null,
                                    tint = StoneAccent,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            onClick = { addNewBlankQuestionInPreview() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("create_exam_preview_add_q_btn")
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

            // Bottom Actions in Preview
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(StonePanel.copy(alpha = 0.96f))
                    .border(1.dp, StoneDivider)
                    .padding(horizontal = 18.dp, vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier
                        .widthIn(max = 680.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    NeumorphicPillButton(
                        text = if (s.isBangla) "ফিরুন" else "Back to Edit",
                        selected = false,
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("create_exam_preview_back_btn")
                    )
                    if (workingQuestions.isNotEmpty()) {
                        NeumorphicPillButton(
                            text = primaryActionText ?: s.examPublishAction,
                            selected = true,
                            leadingContent = {
                                Icon(
                                    imageVector = Icons.Rounded.Publish,
                                    contentDescription = null,
                                    tint = StoneInkPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            onClick = {
                                if (activeDrafts.isNotEmpty() && !saveAllEditedQuestions()) {
                                    return@NeumorphicPillButton
                                }
                                onDismiss()
                                onPublishNow()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("create_exam_preview_publish_btn")
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PreviewQuestionActionHeader(
    qNumber: Int,
    total: Int,
    isEditing: Boolean,
    canMutate: Boolean,
    isBangla: Boolean,
    onToggleEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Q$qNumber of $total",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = StoneAccent
        )
        if (canMutate) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .heightIn(min = 34.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isEditing) StoneInkPrimary else StonePanelInner)
                        .border(
                            width = 1.dp,
                            color = if (isEditing) StoneInkPrimary else StoneDivider,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .clickable(onClick = onToggleEdit)
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                        .testTag("create_exam_preview_edit_q_$qNumber"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.Edit,
                            contentDescription = "Edit Question $qNumber",
                            tint = if (isEditing) NeumorphicHighlight else StoneInkPrimary,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isEditing) {
                                if (isBangla) "বন্ধ" else "Cancel"
                            } else {
                                if (isBangla) "এডিট" else "Edit"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isEditing) NeumorphicHighlight else StoneInkPrimary
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .heightIn(min = 34.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(PreviewErrorColor.copy(alpha = 0.12f))
                        .border(1.dp, PreviewErrorColor.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                        .clickable(onClick = onDelete)
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                        .testTag("create_exam_preview_delete_q_$qNumber"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.DeleteOutline,
                            contentDescription = "Delete Question $qNumber",
                            tint = PreviewErrorColor,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = if (isBangla) "মুছুন" else "Delete",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = PreviewErrorColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InlinePreviewQuestionEditor(
    qNumber: Int,
    draft: QuestionEditDraft,
    isBangla: Boolean,
    onDraftChange: (QuestionEditDraft) -> Unit,
    onSaveQuestion: () -> Unit,
    onCancelEdit: () -> Unit
) {
    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = StoneAccent,
        unfocusedBorderColor = StoneDivider,
        focusedTextColor = StoneInkPrimary,
        unfocusedTextColor = StoneInkPrimary,
        focusedContainerColor = StonePanelInner,
        unfocusedContainerColor = StonePanelInner
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(StonePanel.copy(alpha = 0.72f))
            .border(1.dp, StoneAccent.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
            .padding(12.dp)
            .testTag("create_exam_preview_editor_$qNumber"),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = draft.uddipok,
            onValueChange = { onDraftChange(draft.copy(uddipok = it)) },
            label = { Text(if (isBangla) "উদ্দীপক (ঐচ্ছিক)" else "Uddipok / Stimulus (Optional)") },
            minLines = 1,
            maxLines = 3,
            colors = fieldColors,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("create_exam_preview_uddipok_$qNumber")
        )

        OutlinedTextField(
            value = draft.questionText,
            onValueChange = { onDraftChange(draft.copy(questionText = it)) },
            label = { Text(if (isBangla) "প্রশ্ন $qNumber" else "Question $qNumber Text") },
            minLines = 2,
            maxLines = 4,
            colors = fieldColors,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("create_exam_preview_qtext_$qNumber")
        )

        val opts = listOf(draft.opt0, draft.opt1, draft.opt2, draft.opt3)
        opts.forEachIndexed { optIdx, optValue ->
            val isCorrect = draft.correctIndex == optIdx
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(if (isCorrect) StoneSuccess else StonePanelInner)
                        .border(
                            width = 1.5.dp,
                            color = if (isCorrect) StoneSuccess else StoneDivider,
                            shape = CircleShape
                        )
                        .clickable { onDraftChange(draft.copy(correctIndex = optIdx)) }
                        .testTag("create_exam_preview_correct_${qNumber}_$optIdx"),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (isCorrect) {
                            Icon(
                                imageVector = Icons.Rounded.Check,
                                contentDescription = "Correct Option",
                                tint = NeumorphicHighlight,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                        Text(
                            text = PreviewLettersBn[optIdx],
                            fontFamily = PlusJakartaSansFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (isCorrect) NeumorphicHighlight else StoneInkPrimary
                        )
                    }
                }

                OutlinedTextField(
                    value = optValue,
                    onValueChange = { nextVal ->
                        onDraftChange(
                            when (optIdx) {
                                0 -> draft.copy(opt0 = nextVal)
                                1 -> draft.copy(opt1 = nextVal)
                                2 -> draft.copy(opt2 = nextVal)
                                else -> draft.copy(opt3 = nextVal)
                            }
                        )
                    },
                    label = { Text("Option ${PreviewLettersBn[optIdx]}") },
                    singleLine = true,
                    colors = fieldColors,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("create_exam_preview_opt_${qNumber}_$optIdx")
                )
            }
        }

        OutlinedTextField(
            value = draft.explanation,
            onValueChange = { onDraftChange(draft.copy(explanation = it)) },
            label = { Text(if (isBangla) "ব্যাখ্যা (ঐচ্ছিক)" else "Explanation (Optional)") },
            singleLine = true,
            colors = fieldColors,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("create_exam_preview_expl_$qNumber")
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(StonePanelInner)
                    .border(1.dp, StoneDivider, RoundedCornerShape(12.dp))
                    .clickable(onClick = onCancelEdit),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isBangla) "বাতিল" else "Cancel",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = StoneInkSecondary
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(StoneSuccess)
                    .clickable(onClick = onSaveQuestion)
                    .testTag("create_exam_preview_save_q_$qNumber"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isBangla) "প্রশ্ন সেভ করুন" else "Save Q$qNumber",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = NeumorphicHighlight
                )
            }
        }
    }
}
