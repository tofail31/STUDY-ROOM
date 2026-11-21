package com.example.data.model

enum class PrayerName(val displayName: String) {
    SUBUH("Subuh"),
    SYURUQ("Terbit"),
    DHUHA("Waktu Dhuha"),
    DZUHUR("Dzuhur"),
    ASHAR("Ashar"),
    MAGHRIB("Maghrib"),
    ISYA("Isya")
}

enum class CalculationMethodOption(val id: Int, val label: String, val fajrAngle: Double, val ishaAngle: Double) {
    BANGLADESH(1, "Islamic Foundation Bangladesh (18° / 18°)", 18.0, 18.0),
    MWL(3, "Muslim World League (18° / 17°)", 18.0, 17.0),
    UMM_AL_QURA(4, "Umm Al-Qura Makkah (18.5° / 90m)", 18.5, 18.0),
    ISNA(2, "ISNA (15° / 15°)", 15.0, 15.0),
    KEMENAG(20, "Kemenag RI (20° / 18°)", 20.0, 18.0),
    SINGAPORE(11, "MUIS Singapore (20° / 18°)", 20.0, 18.0)
}

data class PrayerSchedule(
    val subuh: String,
    val syuruq: String,
    val dhuha: String,
    val dzuhur: String,
    val ashar: String,
    val maghrib: String,
    val isya: String,
    val currentWindowLabel: String,
    val nextPrayerName: String,
    val nextPrayerTime: String,
    val countdownText: String,
    val isFromApi: Boolean = false
)

data class CityPreset(
    val id: String,
    val name: String,
    val country: String,
    val latitude: Double,
    val longitude: Double,
    val timeZoneHours: Double
)

data class DailyAyah(
    val id: String,
    val surahNumber: Int,
    val ayahNumber: Int,
    val surahName: String,
    val arabicText: String,
    val translation: String,
    val translationBn: String = ""
)

data class SurahSummary(
    val number: Int,
    val nameLatin: String,
    val nameArabic: String,
    val meaning: String,
    val ayahCount: Int,
    val revelationType: String // Makkiyah / Madaniyah
)

data class AyahItem(
    val numberInSurah: Int,
    val arabic: String,
    val translation: String,
    val translationBn: String = "",
    val transliteration: String = ""
)

data class SurahContent(
    val summary: SurahSummary,
    val ayahs: List<AyahItem>,
    val isFromNetwork: Boolean = false
)

data class DuaItem(
    val id: String,
    val title: String,
    val category: String,
    val arabic: String,
    val latin: String,
    val translation: String,
    val reference: String
)

enum class StudyStatus {
    STUDYING,
    RESTING,
    OFFLINE
}

data class GroupMember(
    val id: String,
    val name: String,
    val initials: String,
    val subjectEn: String,
    val subjectBn: String,
    val todayStudySeconds: Long,
    val status: StudyStatus,
    val streakDays: Int,
    val isCurrentUser: Boolean = false
)

enum class OrganizerRole {
    CREATOR,
    LEADER,
    MODERATOR
}

data class GroupOrganizer(
    val id: String,
    val name: String,
    val initials: String,
    val role: OrganizerRole,
    val noteEn: String,
    val noteBn: String
)

data class StudyGroup(
    val id: String,
    val nameEn: String,
    val nameBn: String,
    val category: String, // "Qur'an & Hifz", "BCS & Varsity", "SSC / HSC", "Tech & Coding"
    val city: String,
    val descriptionEn: String,
    val descriptionBn: String,
    val rulesEn: List<String>,
    val rulesBn: List<String>,
    val dailyTargetHours: Int,
    val rating: Double = 4.9,
    val maxMembers: Int = 30,
    val badgeTextEn: String,
    val badgeTextBn: String,
    val organizers: List<GroupOrganizer> = emptyList(),
    val members: List<GroupMember>
)

data class DzikirPreset(
    val id: String,
    val title: String,
    val arabic: String,
    val latin: String,
    val translation: String,
    val defaultTarget: Int
)

data class AsmaulHusnaItem(
    val number: Int,
    val arabic: String,
    val latin: String,
    val meaning: String
)

data class HijriMilestone(
    val hijriDate: String,
    val title: String,
    val description: String
)

data class GuideStep(
    val stepNumber: Int,
    val title: String,
    val description: String,
    val arabic: String? = null,
    val latin: String? = null,
    val translation: String? = null
)

data class StudySubjectItem(
    val id: String,
    val name: String,
    val colorHex: Long,
    val todaySeconds: Long,
    val goalHours: Double = 2.0
)

data class StudyTodoItem(
    val id: String,
    val title: String,
    val isDone: Boolean = false
)

enum class FocusSessionMode(
    val labelEn: String,
    val labelBn: String,
    val durationSeconds: Int,
    val breakDurationSeconds: Int = 0
) {
    STOPWATCH("Stopwatch", "স্টপওয়াচ", 0, 0),
    POMODORO_25("Pomodoro 25/5", "পোমোডোরো ২৫/৫", 25 * 60, 5 * 60),
    DEEP_WORK_50("Deep 50/10", "ডিপ ৫০/১০", 50 * 60, 10 * 60)
}

object StudyDashboardDefaults {
    val defaultSubjects = listOf(
        StudySubjectItem("subj_physics", "Physics", 0xFFED6A4A, 124L, 2.0),
        StudySubjectItem("subj_chem", "Chemistry", 0xFF7C5CFC, 4L, 2.0),
        StudySubjectItem("subj_math", "Math", 0xFF5E9E1B, 0L, 2.0),
        StudySubjectItem("subj_bangla", "Bangla", 0xFFF4A236, 0L, 2.0),
        StudySubjectItem("subj_english", "English", 0xFF0D9488, 0L, 2.0)
    )

    val defaultTodos = listOf(
        StudyTodoItem("todo_1", "biology", isDone = false),
        StudyTodoItem("todo_2", "physics chapter 4", isDone = true),
        StudyTodoItem("todo_3", "study 3h", isDone = false)
    )

    val subjectColorPalette = listOf(
        0xFFED6A4A, // Coral
        0xFF7C5CFC, // Indigo
        0xFF5E9E1B, // Leaf Green
        0xFFF4A236, // Amber
        0xFF0D9488, // Teal
        0xFFE11D48, // Rose
        0xFF2563EB  // Royal Blue
    )
}

enum class PrepQuestionTag(val label: String) {
    KA("ক"),
    KHA("খ"),
    GA("গ"),
    GHA("ঘ"),
    CQ("CQ"),
    MCQ("MCQ")
}

data class TrackerChapter(
    val id: String,
    val title: String,
    val completedTags: Set<PrepQuestionTag> = emptySet()
) {
    val doneCount: Int get() = completedTags.size
    val totalCount: Int get() = PrepQuestionTag.entries.size
    val progressPercent: Int
        get() = ((doneCount.toFloat() / totalCount.toFloat()) * 100f).toInt()
}

data class TrackerSubject(
    val id: String,
    val nameEn: String,
    val nameBn: String,
    val colorHex: Long,
    val chapters: List<TrackerChapter>
) {
    val totalCheckboxes: Int get() = chapters.size * PrepQuestionTag.entries.size
    val completedCheckboxes: Int get() = chapters.sumOf { it.completedTags.size }
    val completedChaptersCount: Int get() = chapters.count { it.completedTags.size == PrepQuestionTag.entries.size }
    val progressPercent: Int
        get() = if (totalCheckboxes == 0) 0
        else ((completedCheckboxes.toFloat() / totalCheckboxes.toFloat()) * 100f).toInt()
}

object TrackerDefaults {
    private fun chapters(subjectId: String, titles: List<String>): List<TrackerChapter> =
        titles.mapIndexed { idx, title ->
            TrackerChapter(
                id = "${subjectId}_ch_${idx + 1}",
                title = title,
                completedTags = emptySet()
            )
        }

    val defaultSubjects: List<TrackerSubject> = listOf(
        TrackerSubject(
            id = "trk_bangla",
            nameEn = "Bangla",
            nameBn = "বাংলা",
            colorHex = 0xFFE11D48,
            chapters = listOf(
                TrackerChapter("trk_bangla_ch_1", "অপরিচিতা", setOf(PrepQuestionTag.KA, PrepQuestionTag.KHA, PrepQuestionTag.MCQ)),
                TrackerChapter("trk_bangla_ch_2", "বিলাসী", setOf(PrepQuestionTag.KA, PrepQuestionTag.KHA)),
                TrackerChapter("trk_bangla_ch_3", "আমার পথ"),
                TrackerChapter("trk_bangla_ch_4", "মানব-কল্যাণ"),
                TrackerChapter("trk_bangla_ch_5", "মাসি-পিসি"),
                TrackerChapter("trk_bangla_ch_6", "বায়ান্নর দিনগুলো"),
                TrackerChapter("trk_bangla_ch_7", "রেইনকোট"),
                TrackerChapter("trk_bangla_ch_8", "সোনার তরী ও বিদ্রোহী")
            )
        ),
        TrackerSubject(
            id = "trk_english",
            nameEn = "English",
            nameBn = "ইংরেজি",
            colorHex = 0xFF0D9488,
            chapters = listOf(
                TrackerChapter("trk_english_ch_1", "The Unforgettable History", setOf(PrepQuestionTag.CQ, PrepQuestionTag.MCQ)),
                TrackerChapter("trk_english_ch_2", "Nelson Mandela"),
                TrackerChapter("trk_english_ch_3", "Dreams & Poems"),
                TrackerChapter("trk_english_ch_4", "Adolescence"),
                TrackerChapter("trk_english_ch_5", "Food Adulteration"),
                TrackerChapter("trk_english_ch_6", "Modifiers & Connectors"),
                TrackerChapter("trk_english_ch_7", "Narration & Completing")
            )
        ),
        TrackerSubject(
            id = "trk_ict",
            nameEn = "ICT",
            nameBn = "আইসিটি",
            colorHex = 0xFF2563EB,
            chapters = chapters(
                "trk_ict",
                listOf(
                    "বিশ্ব ও বাংলাদেশ প্রেক্ষিত",
                    "কমিউনিকেশন সিস্টেমস ও নেটওয়ার্কিং",
                    "সংখ্যা পদ্ধতি ও ডিজিটাল ডিভাইস",
                    "ওয়েব ডিজাইন ও HTML",
                    "প্রোগ্রামিং ভাষা (C)",
                    "ডেটাবেজ ম্যানেজমেন্ট সিস্টেম"
                )
            )
        ),
        TrackerSubject(
            id = "trk_physics",
            nameEn = "Physics",
            nameBn = "পদার্থবিজ্ঞান",
            colorHex = 0xFFED6A4A,
            chapters = listOf(
                TrackerChapter("trk_physics_ch_1", "ভৌত জগৎ ও পরিমাপ", setOf(PrepQuestionTag.KA, PrepQuestionTag.KHA, PrepQuestionTag.GA, PrepQuestionTag.MCQ)),
                TrackerChapter("trk_physics_ch_2", "ভেক্টর", setOf(PrepQuestionTag.KA, PrepQuestionTag.KHA)),
                TrackerChapter("trk_physics_ch_3", "গতিবিদ্যা"),
                TrackerChapter("trk_physics_ch_4", "নিউটনীয় বলবিদ্যা"),
                TrackerChapter("trk_physics_ch_5", "কাজ, শক্তি ও ক্ষমতা"),
                TrackerChapter("trk_physics_ch_6", "মহাকর্ষ ও অভিকর্ষ"),
                TrackerChapter("trk_physics_ch_7", "পর্যাবৃত্ত গতি ও তরঙ্গ"),
                TrackerChapter("trk_physics_ch_8", "তাপগতিবিদ্যা ও তড়িৎ")
            )
        ),
        TrackerSubject(
            id = "trk_chemistry",
            nameEn = "Chemistry",
            nameBn = "রসায়ন",
            colorHex = 0xFF7C5CFC,
            chapters = chapters(
                "trk_chemistry",
                listOf(
                    "ল্যাবরেটরির নিরাপদ ব্যবহার",
                    "গুণগত রসায়ন",
                    "মৌলের পর্যায়বৃত্ত ধর্ম ও বন্ধন",
                    "রাসায়নিক পরিবর্তন",
                    "পরিবেশ রসায়ন",
                    "জৈব রসায়ন",
                    "পরিমাণগত রসায়ন",
                    "তড়িৎ রসায়ন"
                )
            )
        ),
        TrackerSubject(
            id = "trk_math",
            nameEn = "Math",
            nameBn = "উচ্চতর গণিত",
            colorHex = 0xFF5E9E1B,
            chapters = chapters(
                "trk_math",
                listOf(
                    "ম্যাট্রিক্স ও নির্ণায়ক",
                    "সরলরেখা",
                    "বৃত্ত",
                    "সংযুক্ত কোণের ত্রিকোণমিতি",
                    "অন্তরীকরণ",
                    "যোগজীকরণ",
                    "জটিল সংখ্যা ও বহুপদী",
                    "কনিকস ও গতিবিদ্যা"
                )
            )
        ),
        TrackerSubject(
            id = "trk_biology",
            nameEn = "Biology",
            nameBn = "জীববিজ্ঞান",
            colorHex = 0xFFF4A236,
            chapters = chapters(
                "trk_biology",
                listOf(
                    "কোষ ও এর গঠন",
                    "কোষ বিভাজন",
                    "অণুজীব (ভাইরাস ও ব্যাকটেরিয়া)",
                    "নগ্নবীজী ও আবৃতবীজী উদ্ভিদ",
                    "উদ্ভিদ শারীরতত্ত্ব",
                    "প্রাণীর শ্রেণিবিন্যাস",
                    "রক্ত ও সঞ্চালন",
                    "জিনতত্ত্ব ও বিবর্তন"
                )
            )
        )
    )
}


