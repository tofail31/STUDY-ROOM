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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Notes
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.Dashboard
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Publish
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.ExamJsonImporter
import com.example.data.model.CustomPublishedExam
import com.example.data.model.ExamPaperStyle
import com.example.data.model.ExamQuestion
import com.example.data.model.TrackerSubject
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

private val OptionLabels = listOf("ক", "খ", "গ", "ঘ")
private val ErrorColor = Color(0xFFC85545)

@Composable
fun CreateExamScreen(
    trackerSubjects: List<TrackerSubject> = emptyList(),
    extraSubjectNames: List<String> = emptyList(),
    initialExam: CustomPublishedExam? = null,
    authorName: String,
    authorBatch: String,
    onBack: () -> Unit,
    onDeleteExistingExam: (() -> Unit)? = null,
    onSaveOrPublish: (exam: CustomPublishedExam, publishToGround: Boolean) -> Unit
) {
    val s = LocalAppStrings.current
    val isEditingExisting = initialExam != null

    // Suggested subject names so creators can click to set OR type their own
    val suggestedSubjects = remember(trackerSubjects, extraSubjectNames, s.isBangla) {
        val base = if (trackerSubjects.isNotEmpty()) {
            trackerSubjects.map { if (s.isBangla) it.nameBn else it.nameEn }
        } else {
            if (s.isBangla) {
                listOf("পদার্থবিজ্ঞান", "রসায়ন", "উচ্চতর গণিত", "জীববিজ্ঞান", "বাংলা", "ইংরেজি", "আইসিটি")
            } else {
                listOf("Physics", "Chemistry", "Higher Math", "Biology", "Bangla", "English", "ICT")
            }
        }
        (base + extraSubjectNames.filter { it.isNotBlank() }).distinct()
    }

    // Creator sets Subject Name, Exam Heading (no chapter), and Exam Paper Screen Style
    var subjectNameInput by remember(initialExam?.id) { mutableStateOf(initialExam?.subjectName.orEmpty()) }
    var examHeadingInput by remember(initialExam?.id) { mutableStateOf(initialExam?.title.orEmpty()) }
    var selectedPaperStyle by remember(initialExam?.id) {
        mutableStateOf(initialExam?.paperStyle ?: ExamPaperStyle.CLASSIC_CARDS)
    }

    // Collapsible Settings (Password, Description & Rules — can hide)
    var settingsExpanded by remember(initialExam?.id) { mutableStateOf(initialExam == null) }
    var examPassword by remember(initialExam?.id) { mutableStateOf(initialExam?.password.orEmpty()) }
    var entryDescription by remember(initialExam?.id) { mutableStateOf(initialExam?.description.orEmpty()) }
    var entryRules by remember(initialExam?.id) {
        mutableStateOf(
            initialExam?.rules ?: "1. Read all questions carefully.\n2. Honest attempt only."
        )
    }
    var showDetailsInEntryPopup by remember(initialExam?.id) {
        mutableStateOf(initialExam?.showDetailsInEntryPopup ?: true)
    }

    // Builder Mode: Manual Question Editor vs Quick Create (JSON)
    var isQuickJsonMode by remember { mutableStateOf(false) }
    var quickJsonInput by remember { mutableStateOf("") }
    var showPreviewDialog by remember { mutableStateOf(false) }

    // Question Builder State
    val savedQuestions = remember(initialExam?.id) {
        mutableStateListOf<ExamQuestion>().apply {
            initialExam?.questions?.let { addAll(it) }
        }
    }
    var editingQuestionIndex by remember { mutableIntStateOf(-1) }

    var showUddipokField by remember { mutableStateOf(false) }
    var uddipokText by remember { mutableStateOf("") }
    var questionText by remember { mutableStateOf("") }
    var option0 by remember { mutableStateOf("") }
    var option1 by remember { mutableStateOf("") }
    var option2 by remember { mutableStateOf("") }
    var option3 by remember { mutableStateOf("") }
    var correctIndex by remember { mutableIntStateOf(0) }
    var explanationText by remember { mutableStateOf("") }

    var statusBanner by remember { mutableStateOf<String?>(null) }
    var isErrorBanner by remember { mutableStateOf(false) }

    fun clearQuestionDraft() {
        editingQuestionIndex = -1
        uddipokText = ""
        showUddipokField = false
        questionText = ""
        option0 = ""
        option1 = ""
        option2 = ""
        option3 = ""
        correctIndex = 0
        explanationText = ""
    }

    fun loadQuestionIntoEditor(index: Int) {
        val q = savedQuestions.getOrNull(index) ?: return
        editingQuestionIndex = index
        uddipokText = q.uddipok
        showUddipokField = q.uddipok.isNotBlank()
        questionText = q.questionText
        option0 = q.options.getOrElse(0) { "" }
        option1 = q.options.getOrElse(1) { "" }
        option2 = q.options.getOrElse(2) { "" }
        option3 = q.options.getOrElse(3) { "" }
        correctIndex = q.correctIndex.coerceIn(0, 3)
        explanationText = q.explanation
    }

    fun buildCurrentDraftQuestionOrNull(): ExamQuestion? {
        val qClean = questionText.trim()
        val opts = listOf(option0.trim(), option1.trim(), option2.trim(), option3.trim())
        if (qClean.isEmpty() || opts.any { it.isEmpty() }) return null
        val existingId = if (editingQuestionIndex in savedQuestions.indices) {
            savedQuestions[editingQuestionIndex].id
        } else {
            "cq_${System.currentTimeMillis()}_${savedQuestions.size + 1}"
        }
        return ExamQuestion(
            id = existingId,
            subjectId = "custom_subj",
            chapterId = "",
            paper = 1,
            questionText = qClean,
            options = opts,
            correctIndex = correctIndex,
            explanation = explanationText.trim(),
            uddipok = if (showUddipokField) uddipokText.trim() else ""
        )
    }

    fun saveCurrentQuestionAndAdvance(): Boolean {
        val q = buildCurrentDraftQuestionOrNull()
        if (q == null) {
            isErrorBanner = true
            statusBanner = if (s.isBangla) {
                "অনুগ্রহ করে প্রশ্ন এবং ৪টি অপশন (ক, খ, গ, ঘ) পূরণ করুন।"
            } else {
                "Please fill in the question text and all 4 options."
            }
            return false
        }
        if (editingQuestionIndex in savedQuestions.indices) {
            savedQuestions[editingQuestionIndex] = q
        } else {
            savedQuestions.add(q)
        }
        isErrorBanner = false
        statusBanner = if (s.isBangla) {
            "প্রশ্ন ${savedQuestions.size} সংরক্ষিত! পরবর্তী প্রশ্ন লিখুন।"
        } else {
            "Question saved (${savedQuestions.size} total). Write next question!"
        }
        clearQuestionDraft()
        return true
    }

    fun finalizeExam(publish: Boolean) {
        val hasAnyInput = questionText.isNotBlank() || option0.isNotBlank() || option1.isNotBlank()
        if (hasAnyInput) {
            val built = buildCurrentDraftQuestionOrNull()
            if (built == null) {
                isErrorBanner = true
                statusBanner = if (s.isBangla) {
                    "চলমান প্রশ্নটি সম্পূর্ণ করুন অথবা খালি রাখুন।"
                } else {
                    "Finish all 4 options for the active question or clear it before publishing."
                }
                return
            }
            if (editingQuestionIndex in savedQuestions.indices) {
                savedQuestions[editingQuestionIndex] = built
            } else {
                savedQuestions.add(built)
            }
            clearQuestionDraft()
        }

        if (savedQuestions.isEmpty()) {
            isErrorBanner = true
            statusBanner = if (s.isBangla) {
                "কমপক্ষে ১টি প্রশ্ন যোগ করুন।"
            } else {
                "Add at least 1 question before saving or publishing."
            }
            return
        }

        val resolvedSubject = subjectNameInput.trim().ifEmpty {
            if (s.isBangla) "সাধারণ বিষয়" else "Custom Subject"
        }
        val resolvedHeading = examHeadingInput.trim().ifEmpty {
            "$resolvedSubject Model Test"
        }

        val exam = CustomPublishedExam(
            id = initialExam?.id ?: "custom_exam_${System.currentTimeMillis()}",
            title = resolvedHeading,
            authorName = initialExam?.authorName ?: authorName.ifBlank { "Student" },
            authorBatch = initialExam?.authorBatch ?: authorBatch.ifBlank { "HSC-26" },
            subjectId = initialExam?.subjectId ?: "custom_subj",
            subjectName = resolvedSubject,
            subjectColorHex = initialExam?.subjectColorHex ?: 0xFFE66D45,
            paper = initialExam?.paper ?: 1,
            chapterId = initialExam?.chapterId ?: "",
            chapterTitle = initialExam?.chapterTitle ?: "",
            password = examPassword.trim(),
            description = entryDescription.trim(),
            rules = entryRules.trim(),
            showDetailsInEntryPopup = showDetailsInEntryPopup,
            paperStyle = selectedPaperStyle,
            isPublished = publish,
            createdAtMillis = initialExam?.createdAtMillis ?: System.currentTimeMillis(),
            attemptCount = initialExam?.attemptCount ?: 0,
            questions = savedQuestions.toList()
        )
        onSaveOrPublish(exam, publish)
    }

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
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(StoneBackgroundTop, StoneBackground, StoneBackgroundBottom))
            )
            .statusBarsPadding()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .imePadding()
    ) {
        // Top Header Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            NeumorphicCircleButton(
                onClick = onBack,
                size = 42.dp,
                modifier = Modifier.testTag("create_exam_back_btn")
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
                    text = if (isEditingExisting) {
                        if (s.isBangla) "পরীক্ষা সম্পাদনা" else "Edit Exam"
                    } else {
                        s.createExamTitle
                    },
                    style = MaterialTheme.typography.titleLarge,
                    color = StoneInkPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = if (isEditingExisting) {
                        if (s.isBangla) "প্রশ্ন এডিট বা ডিলিট করে আপডেট করুন" else "Edit or delete questions & save changes"
                    } else {
                        s.createExamSubtitle
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = StoneInkSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (isEditingExisting && onDeleteExistingExam != null) {
                    NeumorphicCircleButton(
                        onClick = onDeleteExistingExam,
                        size = 38.dp,
                        modifier = Modifier.testTag("create_exam_delete_existing_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.DeleteOutline,
                            contentDescription = "Delete Exam",
                            tint = ErrorColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(StonePanelInner)
                        .border(1.dp, StoneAccent.copy(alpha = 0.55f), RoundedCornerShape(12.dp))
                        .clickable { showPreviewDialog = true }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("create_exam_preview_btn")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.Visibility,
                            contentDescription = "Preview Exam",
                            tint = StoneAccent,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (s.isBangla) "প্রিভিউ" else "Preview",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = StoneInkPrimary
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(StoneInkPrimary)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "${savedQuestions.size} Qs",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = NeumorphicHighlight,
                        modifier = Modifier.testTag("create_exam_q_count_pill")
                    )
                }
            }
        }

        // Main Scrollable Form
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
                    .testTag("create_exam_scroll"),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Status / Validation Banner
                if (!statusBanner.isNullOrBlank()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isErrorBanner) ErrorColor.copy(alpha = 0.14f) else StoneSuccess.copy(alpha = 0.14f))
                            .border(
                                width = 1.dp,
                                color = if (isErrorBanner) ErrorColor.copy(alpha = 0.45f) else StoneSuccess.copy(alpha = 0.45f),
                                shape = RoundedCornerShape(14.dp)
                            )
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = statusBanner.orEmpty(),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isErrorBanner) ErrorColor else StoneSuccess
                        )
                    }
                }

                // SECTION 1: Creator Subject Name, Exam Heading Text & Paper Screen Format
                NeumorphicCard(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 22.dp,
                    shadowElevation = 6.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = if (s.isBangla) "১. বিষয়, শিরোনাম ও প্রশ্নপত্রের ধরন" else "1. SUBJECT, EXAM HEADING & PAPER STYLE",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.0.sp,
                            color = StoneInkSecondary
                        )

                        OutlinedTextField(
                            value = subjectNameInput,
                            onValueChange = { subjectNameInput = it },
                            label = { Text(if (s.isBangla) "বিষয়ের নাম লিখুন বা নিচ থেকে বাছুন" else "Subject Name (Type or Tap Below)") },
                            placeholder = { Text(if (s.isBangla) "যেমন: পদার্থবিজ্ঞান ১ম পত্র / Higher Math" else "e.g. Physics 1st Paper / Biology") },
                            singleLine = true,
                            colors = fieldColors,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("create_exam_subject_input")
                        )

                        // Quick Subject Name Chips — tap to set OR create your own above
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("create_exam_subject_suggestions")
                        ) {
                            itemsIndexed(suggestedSubjects) { idx, subjLabel ->
                                val selected = subjectNameInput.trim().equals(subjLabel, ignoreCase = true)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (selected) StoneInkPrimary else StonePanelInner)
                                        .border(
                                            width = 1.dp,
                                            color = if (selected) StoneInkPrimary else StoneDivider,
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .clickable { subjectNameInput = subjLabel }
                                        .padding(horizontal = 11.dp, vertical = 7.dp)
                                        .testTag("create_exam_subj_chip_$idx")
                                ) {
                                    Text(
                                        text = subjLabel,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (selected) NeumorphicHighlight else StoneInkPrimary
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = examHeadingInput,
                            onValueChange = { examHeadingInput = it },
                            label = { Text(if (s.isBangla) "পরীক্ষার হেডিং / শিরোনাম লিখুন" else "Exam Heading / Title") },
                            placeholder = { Text(if (s.isBangla) "যেমন: ফাইনাল রিভিশন মডেল টেস্ট ০১" else "e.g. Mega Board Revision Model Test 01") },
                            singleLine = true,
                            colors = fieldColors,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("create_exam_heading_input")
                        )

                        // Creator chooses which Exam Paper screen style students see
                        Text(
                            text = if (s.isBangla) "পরীক্ষার পেজ স্টাইল নির্বাচন করুন" else "Choose Exam Paper Screen Style",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = StoneInkPrimary
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            PaperStyleOptionTile(
                                title = if (s.isBangla) "১. বক্স কার্ড" else "1. Boxed Cards",
                                subtitle = if (s.isBangla) "ডিফল্ট কার্ড ডিজাইন" else "Classic card & boxes",
                                icon = Icons.Rounded.Dashboard,
                                selected = selectedPaperStyle == ExamPaperStyle.CLASSIC_CARDS,
                                onClick = { selectedPaperStyle = ExamPaperStyle.CLASSIC_CARDS },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("create_exam_style_cards")
                            )

                            PaperStyleOptionTile(
                                title = if (s.isBangla) "২. প্লেইন ফ্লোর" else "2. Plain Floor",
                                subtitle = if (s.isBangla) "বক্স ছাড়া শুধু টেক্সট" else "No boxes • text only",
                                icon = Icons.AutoMirrored.Rounded.Notes,
                                selected = selectedPaperStyle == ExamPaperStyle.PLAIN_FLOOR,
                                onClick = { selectedPaperStyle = ExamPaperStyle.PLAIN_FLOOR },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("create_exam_style_plain")
                            )
                        }
                    }
                }

                // SECTION 2: Password, Entry Popup Description & Rules (Can Hide / Collapse)
                NeumorphicCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("create_exam_settings_card"),
                    cornerRadius = 22.dp,
                    shadowElevation = 6.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { settingsExpanded = !settingsExpanded }
                                .padding(vertical = 4.dp)
                                .testTag("create_exam_settings_toggle"),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Lock,
                                    contentDescription = null,
                                    tint = StoneAccent,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = if (s.isBangla) "২. পাসওয়ার্ড, এন্ট্রি বিবরণ ও নিয়মাবলি" else "2. PASSWORD, DESCRIPTION & RULES",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.8.sp,
                                        color = StoneInkPrimary
                                    )
                                    Text(
                                        text = if (settingsExpanded) {
                                            if (s.isBangla) "লুকাতে ট্যাপ করুন" else "Tap to hide settings"
                                        } else {
                                            if (s.isBangla) "খুলতে ট্যাপ করুন (লুকানো)" else "Hidden — tap to expand"
                                        },
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 10.sp,
                                        color = StoneInkSecondary
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (settingsExpanded) {
                                        if (s.isBangla) "লুকান" else "Hide"
                                    } else {
                                        if (s.isBangla) "দেখুন" else "Show"
                                    },
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = StoneAccent
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = if (settingsExpanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                                    contentDescription = "Toggle Settings",
                                    tint = StoneAccent
                                )
                            }
                        }

                        AnimatedVisibility(
                            visible = settingsExpanded,
                            enter = expandVertically() + fadeIn(),
                            exit = shrinkVertically() + fadeOut()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 12.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                HorizontalDivider(color = StoneDivider)

                                OutlinedTextField(
                                    value = examPassword,
                                    onValueChange = { examPassword = it },
                                    label = { Text(if (s.isBangla) "পাসওয়ার্ড (ঐচ্ছিক)" else "Entry Password (Optional)") },
                                    placeholder = { Text(if (s.isBangla) "উন্মুক্ত রাখতে খালি রাখুন" else "Leave blank for open exam") },
                                    singleLine = true,
                                    colors = fieldColors,
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("create_exam_password_input")
                                )

                                OutlinedTextField(
                                    value = entryDescription,
                                    onValueChange = { entryDescription = it },
                                    label = { Text(if (s.isBangla) "এন্ট্রি পপআপ বিবরণ" else "Entry Popup Description") },
                                    placeholder = { Text(if (s.isBangla) "পরীক্ষার সংক্ষিপ্ত বিবরণ লিখুন..." else "Write short details for students...") },
                                    minLines = 2,
                                    maxLines = 4,
                                    colors = fieldColors,
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("create_exam_desc_input")
                                )

                                OutlinedTextField(
                                    value = entryRules,
                                    onValueChange = { entryRules = it },
                                    label = { Text(if (s.isBangla) "এন্ট্রি পপআপ নিয়মাবলি" else "Entry Popup Rules") },
                                    placeholder = { Text("1. No negative marking...") },
                                    minLines = 2,
                                    maxLines = 4,
                                    colors = fieldColors,
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("create_exam_rules_input")
                                )

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(StonePanelInner)
                                        .border(1.dp, StoneDivider, RoundedCornerShape(12.dp))
                                        .padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(
                                            imageVector = if (showDetailsInEntryPopup) Icons.Rounded.Visibility else Icons.Rounded.VisibilityOff,
                                            contentDescription = null,
                                            tint = StoneInkSecondary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = if (s.isBangla) "এন্ট্রি পপআপে বিবরণ ও নিয়ম দেখান" else "Show description & rules in entry popup",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = StoneInkPrimary
                                        )
                                    }
                                    Switch(
                                        checked = showDetailsInEntryPopup,
                                        onCheckedChange = { showDetailsInEntryPopup = it },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = NeumorphicHighlight,
                                            checkedTrackColor = StoneAccent
                                        ),
                                        modifier = Modifier.testTag("create_exam_show_details_switch")
                                    )
                                }
                            }
                        }
                    }
                }

                // SECTION 3: Question Builder (Optional Uddipok + Question + 4 Options + Select Answer + Create Next)
                NeumorphicCard(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 22.dp,
                    shadowElevation = 7.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Mode Toggle: Manual Builder vs Quick Create (JSON)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            NeumorphicPillButton(
                                text = if (s.isBangla) "ম্যানুয়াল তৈরি" else "Manual Builder",
                                selected = !isQuickJsonMode,
                                onClick = { isQuickJsonMode = false },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("create_exam_mode_manual")
                            )
                            NeumorphicPillButton(
                                text = if (s.isBangla) "কুইক ক্রিয়েট (JSON)" else "Quick Create (JSON)",
                                selected = isQuickJsonMode,
                                leadingContent = {
                                    Icon(
                                        imageVector = Icons.Rounded.Code,
                                        contentDescription = null,
                                        tint = StoneInkPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                },
                                onClick = { isQuickJsonMode = true },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("create_exam_mode_quick")
                            )
                        }

                        if (!isQuickJsonMode) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (s.isBangla) {
                                        "৩. প্রশ্ন তৈরি (${if (editingQuestionIndex >= 0) "Q${editingQuestionIndex + 1} সম্পাদনা" else "প্রশ্ন ${savedQuestions.size + 1}"})"
                                    } else {
                                        "3. QUESTION BUILDER (${if (editingQuestionIndex >= 0) "EDIT Q${editingQuestionIndex + 1}" else "QUESTION ${savedQuestions.size + 1}"})"
                                    },
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.0.sp,
                                    color = StoneInkSecondary
                                )

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (showUddipokField) StoneAccent else StonePanelInner)
                                        .border(
                                            width = 1.dp,
                                            color = if (showUddipokField) StoneAccent else StoneDivider,
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                        .clickable { showUddipokField = !showUddipokField }
                                        .padding(horizontal = 10.dp, vertical = 5.dp)
                                        .testTag("create_exam_uddipok_toggle")
                                ) {
                                    Text(
                                        text = if (showUddipokField) "- Hide Uddipok" else "+ Uddipok (Optional)",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (showUddipokField) NeumorphicHighlight else StoneInkPrimary
                                    )
                                }
                            }
                        } else {
                            // QUICK CREATE (JSON Paste -> Auto Create Exam Questions)
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("create_exam_quick_card"),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = if (s.isBangla) {
                                        "৩. কুইক ক্রিয়েট — JSON কোড পেস্ট করুন, প্রশ্ন ও বিষয় অটো তৈরি হবে"
                                    } else {
                                        "3. QUICK CREATE — PASTE JSON TO AUTO-GENERATE EXAM QUESTIONS"
                                    },
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.8.sp,
                                    color = StoneInkSecondary
                                )

                                OutlinedTextField(
                                    value = quickJsonInput,
                                    onValueChange = { quickJsonInput = it },
                                    label = { Text(if (s.isBangla) "এখানে JSON কোড পেস্ট করুন" else "Paste JSON Code Here") },
                                    placeholder = {
                                        Text("{\"subject\":\"Physics\",\"title\":\"Model 1\",\"questions\":[{\"question\":\"...\",\"options\":[\"A\",\"B\",\"C\",\"D\"],\"correctIndex\":0}]}")
                                    },
                                    minLines = 5,
                                    maxLines = 10,
                                    colors = fieldColors,
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("create_exam_quick_json_input")
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .heightIn(min = 42.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(StonePanelInner)
                                            .border(1.dp, StoneDivider, RoundedCornerShape(12.dp))
                                            .clickable {
                                                quickJsonInput = ExamJsonImporter.sampleJsonTemplate(s.isBangla)
                                            }
                                            .padding(horizontal = 10.dp, vertical = 8.dp)
                                            .testTag("create_exam_quick_template_btn"),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = if (s.isBangla) "স্যাম্পল JSON বসান" else "Paste Sample JSON",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = StoneAccent
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .heightIn(min = 42.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(StonePanelInner)
                                            .border(1.dp, StoneDivider, RoundedCornerShape(12.dp))
                                            .clickable { quickJsonInput = "" }
                                            .padding(horizontal = 14.dp, vertical = 8.dp)
                                            .testTag("create_exam_quick_clear_btn"),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = if (s.isBangla) "মুছুন" else "Clear",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.SemiBold,
                                            color = StoneInkSecondary
                                        )
                                    }
                                }

                                NeumorphicPillButton(
                                    text = if (s.isBangla) "অটো প্রশ্ন তৈরি করুন" else "Auto Create Questions",
                                    selected = true,
                                    leadingContent = {
                                        Icon(
                                            imageVector = Icons.Rounded.AutoAwesome,
                                            contentDescription = null,
                                            tint = StoneInkPrimary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    },
                                    onClick = {
                                        ExamJsonImporter.parse(quickJsonInput).fold(
                                            onSuccess = { parsed ->
                                                parsed.subjectName?.let { subjectNameInput = it }
                                                parsed.examHeading?.let { examHeadingInput = it }
                                                parsed.password?.let { examPassword = it }
                                                parsed.description?.let { entryDescription = it }
                                                parsed.rules?.let { entryRules = it }
                                                parsed.paperStyle?.let { selectedPaperStyle = it }
                                                savedQuestions.addAll(parsed.questions)
                                                isErrorBanner = false
                                                statusBanner = if (s.isBangla) {
                                                    "${parsed.questions.size}টি প্রশ্ন অটো তৈরি হয়েছে! মোট প্রশ্ন: ${savedQuestions.size}।"
                                                } else {
                                                    "Auto-created ${parsed.questions.size} questions! Total: ${savedQuestions.size} Qs."
                                                }
                                            },
                                            onFailure = { err ->
                                                isErrorBanner = true
                                                statusBanner = err.message ?: "Invalid JSON format."
                                            }
                                        )
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("create_exam_quick_parse_btn")
                                )
                            }
                        }

                        if (savedQuestions.isNotEmpty()) {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                itemsIndexed(savedQuestions) { idx, _ ->
                                    val isEditing = !isQuickJsonMode && editingQuestionIndex == idx
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(if (isEditing) StoneInkPrimary else StoneSuccess.copy(alpha = 0.14f))
                                            .border(
                                                width = 1.dp,
                                                color = if (isEditing) StoneInkPrimary else StoneSuccess.copy(alpha = 0.5f),
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                            .clickable {
                                                isQuickJsonMode = false
                                                loadQuestionIntoEditor(idx)
                                            }
                                            .padding(start = 10.dp, end = 4.dp, top = 4.dp, bottom = 4.dp)
                                            .testTag("create_exam_saved_q_chip_$idx")
                                    ) {
                                        Text(
                                            text = "Q${idx + 1}",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isEditing) NeumorphicHighlight else StoneSuccess
                                        )
                                        IconButton(
                                            onClick = {
                                                savedQuestions.removeAt(idx)
                                                if (editingQuestionIndex == idx) {
                                                    clearQuestionDraft()
                                                } else if (editingQuestionIndex > idx) {
                                                    editingQuestionIndex -= 1
                                                }
                                            },
                                            modifier = Modifier.size(22.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Rounded.DeleteOutline,
                                                contentDescription = "Delete Q${idx + 1}",
                                                tint = if (isEditing) NeumorphicHighlight else StoneInkSecondary,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                }

                                if (editingQuestionIndex >= 0 && !isQuickJsonMode) {
                                    item(key = "new_q_slot") {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(StonePanelInner)
                                                .border(1.dp, StoneDivider, RoundedCornerShape(12.dp))
                                                .clickable { clearQuestionDraft() }
                                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                        ) {
                                            Text(
                                                text = "+ New Q${savedQuestions.size + 1}",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = StoneAccent
                                            )
                                        }
                                    }
                                }

                                item(key = "manage_all_preview_chip") {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(StoneAccent.copy(alpha = 0.14f))
                                            .border(1.dp, StoneAccent.copy(alpha = 0.45f), RoundedCornerShape(12.dp))
                                            .clickable { showPreviewDialog = true }
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                            .testTag("create_exam_manage_preview_btn")
                                    ) {
                                        Text(
                                            text = if (s.isBangla) "প্রিভিউতে সব এডিট/ডিলিট" else "Edit All / Delete",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = StoneAccent
                                        )
                                    }
                                }
                            }
                        }

                        if (!isQuickJsonMode) {

                        AnimatedVisibility(
                            visible = showUddipokField,
                            enter = expandVertically() + fadeIn(),
                            exit = shrinkVertically() + fadeOut()
                        ) {
                            OutlinedTextField(
                                value = uddipokText,
                                onValueChange = { uddipokText = it },
                                label = { Text(s.examUddipokOptional) },
                                placeholder = { Text("উদ্দীপক লিখুন (ঐচ্ছিক)... যেমন: একটি বস্তু 10 m/s বেগে...") },
                                minLines = 2,
                                maxLines = 4,
                                colors = fieldColors,
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("create_exam_uddipok_input")
                            )
                        }

                        OutlinedTextField(
                            value = questionText,
                            onValueChange = { questionText = it },
                            label = { Text(if (s.isBangla) "প্রশ্ন লিখুন" else "Question Text") },
                            placeholder = { Text(if (s.isBangla) "মূল প্রশ্নটি লিখুন..." else "Enter your MCQ question...") },
                            minLines = 2,
                            maxLines = 4,
                            colors = fieldColors,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("create_exam_question_input")
                        )

                        Text(
                            text = if (s.isBangla) "৪টি অপশন লিখুন এবং বাম পাশের ক/খ/গ/ঘ ট্যাপ করে সঠিক উত্তর বেছে নিন" else "Set Options & Tap ক/খ/গ/ঘ to Select Correct Answer",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = StoneAccent
                        )

                        val optValues = listOf(option0, option1, option2, option3)
                        val optSetters: List<(String) -> Unit> = listOf(
                            { option0 = it },
                            { option1 = it },
                            { option2 = it },
                            { option3 = it }
                        )

                        optValues.forEachIndexed { idx, optVal ->
                            val isCorrect = correctIndex == idx
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .testTag("create_exam_correct_$idx")
                                        .clip(CircleShape)
                                        .background(if (isCorrect) StoneSuccess else StonePanelInner)
                                        .border(
                                            width = 1.5.dp,
                                            color = if (isCorrect) StoneSuccess else StoneDivider,
                                            shape = CircleShape
                                        )
                                        .clickable { correctIndex = idx },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        if (isCorrect) {
                                            Icon(
                                                imageVector = Icons.Rounded.Check,
                                                contentDescription = "Selected Correct",
                                                tint = NeumorphicHighlight,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                        Text(
                                            text = OptionLabels[idx],
                                            fontFamily = PlusJakartaSansFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = if (isCorrect) NeumorphicHighlight else StoneInkPrimary
                                        )
                                    }
                                }

                                OutlinedTextField(
                                    value = optVal,
                                    onValueChange = optSetters[idx],
                                    label = { Text("Option ${OptionLabels[idx]}") },
                                    singleLine = true,
                                    colors = fieldColors,
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("create_exam_opt_$idx")
                                )
                            }
                        }

                        OutlinedTextField(
                            value = explanationText,
                            onValueChange = { explanationText = it },
                            label = { Text(if (s.isBangla) "উত্তরের ব্যাখ্যা (ঐচ্ছিক)" else "Answer Explanation (Optional)") },
                            placeholder = { Text(if (s.isBangla) "কেন এটি সঠিক উত্তর তা লিখুন..." else "Why this option is correct...") },
                            singleLine = true,
                            colors = fieldColors,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("create_exam_explanation_input")
                        )

                        NeumorphicPillButton(
                            text = if (editingQuestionIndex >= 0) {
                                if (s.isBangla) "আপডেট ও পরবর্তী প্রশ্ন" else "Update & Create Next"
                            } else {
                                s.examCreateNextQuestion
                            },
                            selected = true,
                            leadingContent = {
                                Icon(
                                    imageVector = Icons.Rounded.Add,
                                    contentDescription = null,
                                    tint = StoneInkPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            onClick = { saveCurrentQuestionAndAdvance() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("create_exam_next_q_btn")
                        )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        // Sticky Bottom Bar: Save Draft (Account) & Publish to Exam Ground
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
                    text = s.examSaveDraftAction,
                    selected = false,
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Rounded.Save,
                            contentDescription = null,
                            tint = StoneInkPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    onClick = { finalizeExam(publish = false) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("create_exam_save_draft_btn")
                )

                NeumorphicPillButton(
                    text = s.examPublishAction,
                    selected = true,
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Rounded.Publish,
                            contentDescription = null,
                            tint = StoneInkPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    onClick = { finalizeExam(publish = true) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("create_exam_publish_btn")
                )
            }
        }
    }

    if (showPreviewDialog) {
        val activeDraft = buildCurrentDraftQuestionOrNull()
        val hasActiveNewDraft = activeDraft != null && editingQuestionIndex < 0
        val previewQuestions = if (hasActiveNewDraft && activeDraft != null) {
            savedQuestions.toList() + activeDraft
        } else {
            savedQuestions.toList()
        }
        val previewSubject = subjectNameInput.trim().ifEmpty {
            if (s.isBangla) "সাধারণ বিষয়" else "Custom Subject"
        }
        val previewHeading = examHeadingInput.trim().ifEmpty {
            "$previewSubject Model Test"
        }
        ExamCreatorPreviewDialog(
            subjectName = previewSubject,
            examHeading = previewHeading,
            authorName = authorName.ifBlank { "Student" },
            paperStyle = selectedPaperStyle,
            password = examPassword.trim(),
            description = entryDescription.trim(),
            rules = entryRules.trim(),
            questions = previewQuestions,
            onDismiss = { showPreviewDialog = false },
            onPublishNow = { finalizeExam(publish = true) },
            onQuestionsChanged = { updatedList ->
                savedQuestions.clear()
                savedQuestions.addAll(updatedList)
                if (hasActiveNewDraft) {
                    clearQuestionDraft()
                } else if (editingQuestionIndex !in savedQuestions.indices) {
                    editingQuestionIndex = -1
                } else {
                    loadQuestionIntoEditor(editingQuestionIndex)
                }
            },
            onPaperStyleChanged = { newStyle ->
                selectedPaperStyle = newStyle
            }
        )
    }
}

@Composable
private fun PaperStyleOptionTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .heightIn(min = 68.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(if (selected) StoneInkPrimary else StonePanelInner)
            .border(
                width = 1.5.dp,
                color = if (selected) StoneAccent else StoneDivider,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (selected) StoneAccent else StoneInkPrimary,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = if (selected) NeumorphicHighlight else StoneInkPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Text(
            text = subtitle,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 10.sp,
            color = if (selected) NeumorphicHighlight.copy(alpha = 0.78f) else StoneInkSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
