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
    val paper: Int = 1,
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

    fun chaptersForPaper(paper: Int): List<TrackerChapter> = chapters.filter { it.paper == paper }

    fun paperTotalCheckboxes(paper: Int): Int =
        chaptersForPaper(paper).size * PrepQuestionTag.entries.size

    fun paperCompletedCheckboxes(paper: Int): Int =
        chaptersForPaper(paper).sumOf { it.completedTags.size }

    fun paperCompletedChaptersCount(paper: Int): Int =
        chaptersForPaper(paper).count { it.completedTags.size == PrepQuestionTag.entries.size }

    fun paperProgressPercent(paper: Int): Int {
        val total = paperTotalCheckboxes(paper)
        if (total == 0) return 0
        return ((paperCompletedCheckboxes(paper).toFloat() / total.toFloat()) * 100f).toInt()
    }
}

object TrackerDefaults {
    private fun paperChapters(
        subjectId: String,
        paper: Int,
        titles: List<String>,
        presetTags: Map<Int, Set<PrepQuestionTag>> = emptyMap()
    ): List<TrackerChapter> = titles.mapIndexed { idx, title ->
        val chapterNo = idx + 1
        TrackerChapter(
            id = "${subjectId}_p${paper}_ch_${chapterNo}",
            title = title,
            paper = paper,
            completedTags = presetTags[chapterNo] ?: emptySet()
        )
    }

    val defaultSubjects: List<TrackerSubject> = listOf(
        TrackerSubject(
            id = "trk_bangla",
            nameEn = "Bangla",
            nameBn = "বাংলা",
            colorHex = 0xFFE11D48,
            chapters = paperChapters(
                subjectId = "trk_bangla",
                paper = 1,
                titles = listOf(
                    "অপরিচিতা (গদ্য)",
                    "বিলাসী (গদ্য)",
                    "আমার পথ (গদ্য)",
                    "মানব-কল্যাণ (গদ্য)",
                    "মাসি-পিসি (গদ্য)",
                    "বায়ান্নর দিনগুলো (গদ্য)",
                    "রেইনকোট (গদ্য)",
                    "সোনার তরী (পদ্য)",
                    "বিদ্রোহী (পদ্য)",
                    "প্রতিদান (পদ্য)",
                    "তাহারেই পড়ে মনে (পদ্য)",
                    "আঠারো বছর বয়স (পদ্য)",
                    "ফেব্রুয়ারি ১৯৬৯ ও আমি কিংবদন্তির কথা বলছি",
                    "সিরাজউদ্দৌলা (নাটক)",
                    "লালসালু (উপন্যাস)"
                ),
                presetTags = mapOf(
                    1 to setOf(PrepQuestionTag.KA, PrepQuestionTag.KHA, PrepQuestionTag.MCQ),
                    2 to setOf(PrepQuestionTag.KA, PrepQuestionTag.KHA)
                )
            ) + paperChapters(
                subjectId = "trk_bangla",
                paper = 2,
                titles = listOf(
                    "বাংলা উচ্চারণের নিয়ম",
                    "বাংলা বানানের নিয়ম",
                    "বাংলা ভাষার ব্যাকরণিক শব্দশ্রেণি",
                    "বাংলা শব্দ গঠন (উপসর্গ, প্রত্যয় ও সমাস)",
                    "বাক্যতত্ত্ব",
                    "বাংলা ভাষার অপপ্রয়োগ ও শুদ্ধ প্রয়োগ",
                    "পারিভাষিক শব্দ ও অনুবাদ",
                    "দিনলিপি ও প্রতিবেদন রচনা",
                    "বৈদ্যুতিন চিঠি ও আবেদনপত্র",
                    "সারাংশ, সারমর্ম ও ভাব-সম্প্রসারণ",
                    "সংলাপ ও ক্ষুদে গল্প রচনা",
                    "প্রবন্ধ-নিবন্ধ রচনা"
                )
            )
        ),
        TrackerSubject(
            id = "trk_english",
            nameEn = "English",
            nameBn = "ইংরেজি",
            colorHex = 0xFF0D9488,
            chapters = paperChapters(
                subjectId = "trk_english",
                paper = 1,
                titles = listOf(
                    "Unit 1: The Unforgettable History & Mandela",
                    "Unit 2: Education and Life",
                    "Unit 3: Dreams",
                    "Unit 4: Youthful Achievers",
                    "Unit 5: Relationships",
                    "Unit 6: Adolescence",
                    "Unit 7: Lifestyle & Food Adulteration",
                    "Unit 8: Human Rights",
                    "Unit 9: Tours and Travels",
                    "Unit 10: Environment and Nature",
                    "Unit 11: Art and Craft",
                    "Unit 12: Myths and Literature"
                ),
                presetTags = mapOf(
                    1 to setOf(PrepQuestionTag.CQ, PrepQuestionTag.MCQ)
                )
            ) + paperChapters(
                subjectId = "trk_english",
                paper = 2,
                titles = listOf(
                    "Use of Prepositions",
                    "Special Uses of Words & Phrases",
                    "Completing Sentences (Clauses)",
                    "Right Form of Verbs",
                    "Narration & Direct/Indirect Speech",
                    "Use of Modifiers",
                    "Use of Sentence Connectors",
                    "Synonym & Antonym",
                    "Punctuation",
                    "Formal Letter & E-mail",
                    "Report Writing",
                    "Paragraph & Composition"
                )
            )
        ),
        TrackerSubject(
            id = "trk_ict",
            nameEn = "ICT",
            nameBn = "আইসিটি",
            colorHex = 0xFF2563EB,
            chapters = paperChapters(
                subjectId = "trk_ict",
                paper = 1,
                titles = listOf(
                    "অধ্যায় ১: বিশ্ব ও বাংলাদেশ প্রেক্ষিত",
                    "অধ্যায় ২: কমিউনিকেশন সিস্টেমস ও নেটওয়ার্কিং",
                    "অধ্যায় ৩ (ক): সংখ্যা পদ্ধতি ও রূপান্তর",
                    "অধ্যায় ৩ (খ): ডিজিটাল ডিভাইস ও লজিক গেট"
                )
            ) + paperChapters(
                subjectId = "trk_ict",
                paper = 2,
                titles = listOf(
                    "অধ্যায় ৪: ওয়েব ডিজাইন পরিচিতি এবং HTML",
                    "অধ্যায় ৫ (ক): অ্যালগরিদম ও ফ্লোচার্ট",
                    "অধ্যায় ৫ (খ): প্রোগ্রামিং ভাষা (C)",
                    "অধ্যায় ৬: ডেটাবেজ ম্যানেজমেন্ট সিস্টেম (SQL)"
                )
            )
        ),
        TrackerSubject(
            id = "trk_physics",
            nameEn = "Physics",
            nameBn = "পদার্থবিজ্ঞান",
            colorHex = 0xFFED6A4A,
            chapters = paperChapters(
                subjectId = "trk_physics",
                paper = 1,
                titles = listOf(
                    "ভৌত জগৎ ও পরিমাপ",
                    "ভেক্টর",
                    "গতিবিদ্যা",
                    "নিউটনীয় বলবিদ্যা",
                    "কাজ, শক্তি ও ক্ষমতা",
                    "মহাকর্ষ ও অভিকর্ষ",
                    "পদার্থের গাঠনিক ধর্ম",
                    "পর্যাবৃত্ত গতি",
                    "তরঙ্গ",
                    "আদর্শ গ্যাস ও গ্যাসের গতিতত্ত্ব"
                ),
                presetTags = mapOf(
                    1 to setOf(PrepQuestionTag.KA, PrepQuestionTag.KHA, PrepQuestionTag.GA, PrepQuestionTag.MCQ),
                    2 to setOf(PrepQuestionTag.KA, PrepQuestionTag.KHA)
                )
            ) + paperChapters(
                subjectId = "trk_physics",
                paper = 2,
                titles = listOf(
                    "তাপগতিবিদ্যা",
                    "স্থির তড়িৎ",
                    "চল তড়িৎ",
                    "তড়িৎ প্রবাহের চৌম্বক ক্রিয়া ও চুম্বকত্ব",
                    "তাড়িতচৌম্বক আবেশ ও পরিবর্তী প্রবাহ",
                    "জ্যামিতিক আলোকবিজ্ঞান",
                    "ভৌত আলোকবিজ্ঞান",
                    "আধুনিক পদার্থবিজ্ঞানের সূচনা",
                    "পরমাণুর মডেল ও নিউক্লিয়ার পদার্থবিজ্ঞান",
                    "সেমিকন্ডাক্টর ও ইলেকট্রনিক্স",
                    "জ্যোতির্বিজ্ঞান"
                )
            )
        ),
        TrackerSubject(
            id = "trk_chemistry",
            nameEn = "Chemistry",
            nameBn = "রসায়ন",
            colorHex = 0xFF7C5CFC,
            chapters = paperChapters(
                subjectId = "trk_chemistry",
                paper = 1,
                titles = listOf(
                    "ল্যাবরেটরির নিরাপদ ব্যবহার",
                    "গুণগত রসায়ন",
                    "মৌলের পর্যায়বৃত্ত ধর্ম ও রাসায়নিক বন্ধন",
                    "রাসায়নিক পরিবর্তন",
                    "কর্মমুখী রসায়ন"
                )
            ) + paperChapters(
                subjectId = "trk_chemistry",
                paper = 2,
                titles = listOf(
                    "পরিবেশ রসায়ন",
                    "জৈব রসায়ন",
                    "পরিমাণগত রসায়ন",
                    "তড়িৎ রসায়ন",
                    "অর্থনৈতিক রসায়ন"
                )
            )
        ),
        TrackerSubject(
            id = "trk_math",
            nameEn = "Math",
            nameBn = "উচ্চতর গণিত",
            colorHex = 0xFF5E9E1B,
            chapters = paperChapters(
                subjectId = "trk_math",
                paper = 1,
                titles = listOf(
                    "ম্যাট্রিক্স ও নির্ণায়ক",
                    "ভেক্টর",
                    "সরলরেখা",
                    "বৃত্ত",
                    "বিন্যাস ও সমাবেশ",
                    "ত্রিকোণমিতিক অনুপাত",
                    "সংযুক্ত কোণের ত্রিকোণমিতিক অনুপাত",
                    "ফাংশন ও ফাংশনের লেখচিত্র",
                    "অন্তরীকরণ",
                    "যোগজীকরণ"
                )
            ) + paperChapters(
                subjectId = "trk_math",
                paper = 2,
                titles = listOf(
                    "বাস্তব সংখ্যা ও অসমতা",
                    "যোগাশ্রয়ী প্রোগ্রাম",
                    "জটিল সংখ্যা",
                    "বহুপদী ও বহুপদী সমীকরণ",
                    "দ্বিপদী বিস্তৃতি",
                    "কনিকস",
                    "বিপরীত ত্রিকোণমিতিক ফাংশন ও সমীকরণ",
                    "স্থিতিবিদ্যা",
                    "সমতলে বস্তুকণার গতি",
                    "বিস্তার পরিমাপ ও সম্ভাবনা"
                )
            )
        ),
        TrackerSubject(
            id = "trk_biology",
            nameEn = "Biology",
            nameBn = "জীববিজ্ঞান",
            colorHex = 0xFFF4A236,
            chapters = paperChapters(
                subjectId = "trk_biology",
                paper = 1,
                titles = listOf(
                    "কোষ ও এর গঠন (উদ্ভিদবিজ্ঞান)",
                    "কোষ বিভাজন",
                    "কোষ রসায়ন",
                    "অণুজীব",
                    "শৈবাল ও ছত্রাক",
                    "ব্রায়োফাইটা ও টেরিডোফাইটা",
                    "নগ্নবীজী ও আবৃতবীজী উদ্ভিদ",
                    "টিস্যু ও টিস্যুতন্ত্র",
                    "উদ্ভিদ শারীরতত্ত্ব",
                    "উদ্ভিদ প্রজনন",
                    "জীবপ্রযুক্তি",
                    "জীবের পরিবেশ, বিস্তার ও সংরক্ষণ"
                )
            ) + paperChapters(
                subjectId = "trk_biology",
                paper = 2,
                titles = listOf(
                    "প্রাণীর বিভিন্নতা ও শ্রেণিবিন্যাস (প্রাণিবিজ্ঞান)",
                    "প্রাণীর পরিচিতি (হাইড্রা, ঘাসফড়িং ও রুই)",
                    "পরিপাক ও শোষণ",
                    "রক্ত ও সঞ্চালন",
                    "শ্বাসক্রিয়া ও শ্বসন",
                    "বর্জ্য ও নিষ্কাশন",
                    "চলন ও অঙ্গচালনা",
                    "সমন্বয় ও নিয়ন্ত্রণ",
                    "মানব জীবনের ধারাবাহিকতা",
                    "মানবদেহের প্রতিরক্ষা",
                    "জিনতত্ত্ব ও বিবর্তন",
                    "প্রাণীর আচরণ"
                )
            )
        )
    )
}


