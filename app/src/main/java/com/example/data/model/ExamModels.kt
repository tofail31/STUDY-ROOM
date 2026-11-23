package com.example.data.model

enum class ExamTrackId {
    HSC,
    MEDICAL,
    BUET,
    DU,
    WRONG_ANS,
    CREATE_EXAM,
    EXAM_GROUND
}

data class ExamTrackCardInfo(
    val id: ExamTrackId,
    val titleEn: String,
    val titleBn: String,
    val subtitleEn: String,
    val subtitleBn: String,
    val badgeEn: String,
    val badgeBn: String,
    val colorHex: Long,
    val isActive: Boolean
)

data class ExamQuestion(
    val id: String,
    val subjectId: String,
    val chapterId: String,
    val paper: Int,
    val questionText: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String,
    val uddipok: String = ""
)

enum class ExamPaperStyle {
    CLASSIC_CARDS,
    PLAIN_FLOOR
}

data class CustomPublishedExam(
    val id: String,
    val title: String,
    val authorName: String,
    val authorBatch: String,
    val subjectId: String,
    val subjectName: String,
    val subjectColorHex: Long,
    val paper: Int,
    val chapterId: String,
    val chapterTitle: String,
    val password: String = "",
    val description: String = "",
    val rules: String = "",
    val showDetailsInEntryPopup: Boolean = true,
    val paperStyle: ExamPaperStyle = ExamPaperStyle.CLASSIC_CARDS,
    val isPublished: Boolean = true,
    val createdAtMillis: Long = System.currentTimeMillis(),
    val attemptCount: Int = 0,
    val questions: List<ExamQuestion> = emptyList()
)

enum class ExamPaperStage {
    TAKING,
    RESULT,
    REVIEW_ANSWERS
}

data class ExamUiState(
    val activeSubjectId: String = "trk_physics",
    val activeSubjectNameEn: String = "Physics",
    val activeSubjectNameBn: String = "পদার্থবিজ্ঞান",
    val activeChapterId: String = "trk_physics_p1_ch_1",
    val activeChapterTitle: String = "",
    val activePaper: Int = 1,
    val paperStyle: ExamPaperStyle = ExamPaperStyle.CLASSIC_CARDS,
    val isWrongAnswersExam: Boolean = false,
    val questions: List<ExamQuestion> = emptyList(),
    val selectedAnswers: Map<String, Int> = emptyMap(),
    val stage: ExamPaperStage = ExamPaperStage.TAKING,
    val elapsedSeconds: Long = 0L,
    val correctCount: Int = 0,
    val wrongCount: Int = 0,
    val skippedCount: Int = 0
)
