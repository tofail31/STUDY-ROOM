package com.example.data.model

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

enum class MissionPriority(val labelEn: String, val labelBn: String) {
    NORMAL("Normal", "সাধারণ"),
    HIGH("High", "গুরুত্বপূর্ণ"),
    CRITICAL("Urgent", "জরুরি")
}

enum class MissionAlarmMode(
    val offsetMinutes: Int,
    val labelEn: String,
    val labelBn: String
) {
    AT_BLOCK_START(0, "At block start", "স্লট শুরুর সময়"),
    FIFTEEN_MIN_BEFORE(-15, "15 min before", "১৫ মিনিট আগে"),
    THIRTY_MIN_BEFORE(-30, "30 min before", "৩০ মিনিট আগে"),
    CUSTOM_TIME(0, "Custom alarm time", "কাস্টম অ্যালার্ম সময়")
}

data class MissionBlockSlot(
    val index: Int,
    val startHour24: Int,
    val endHour24: Int,
    val rangeShortEn: String,
    val rangeFullEn: String,
    val rangeFullBn: String,
    val phaseTitleEn: String,
    val phaseTitleBn: String,
    val accentColorHex: Long
)

data class MissionItem(
    val id: String,
    val dateKey: String,           // yyyy-MM-dd
    val blockIndex: Int,           // 0..5 (4h windows starting 5 AM)
    val title: String,
    val notes: String = "",
    val category: String = "Study",
    val subjectId: String = "",
    val subjectName: String = "",
    val paper: Int = 1,            // 1 = 1st Paper, 2 = 2nd Paper, 0 = General
    val chapterId: String = "",
    val chapterTitle: String = "",
    val targetTags: Set<PrepQuestionTag> = emptySet(),
    val completedTags: Set<PrepQuestionTag> = emptySet(),
    val priority: MissionPriority = MissionPriority.HIGH,
    val isCompleted: Boolean = false,
    val alarmEnabled: Boolean = true,
    val alarmMode: MissionAlarmMode = MissionAlarmMode.AT_BLOCK_START,
    val customAlarmHour: Int = 5,
    val customAlarmMinute: Int = 0,
    val useAlarmTone: Boolean = true,
    val showInLiveNotification: Boolean = true,
    val updatedAtMillis: Long = 0L
)

object MissionTimeHelper {

    /**
     * 6 exact 4-hour blocks covering a full 24-hour cycle starting at 5:00 AM:
     * 0: 05:00 AM - 09:00 AM
     * 1: 09:00 AM - 01:00 PM
     * 2: 01:00 PM - 05:00 PM
     * 3: 05:00 PM - 09:00 PM
     * 4: 09:00 PM - 01:00 AM
     * 5: 01:00 AM - 05:00 AM
     */
    val blocks: List<MissionBlockSlot> = listOf(
        MissionBlockSlot(
            index = 0,
            startHour24 = 5,
            endHour24 = 9,
            rangeShortEn = "5 AM – 9 AM",
            rangeFullEn = "05:00 AM – 09:00 AM",
            rangeFullBn = "ভোর ৫:০০ – সকাল ৯:০০",
            phaseTitleEn = "Dawn & Early Focus",
            phaseTitleBn = "ভোর ও সকালের ফোকাস",
            accentColorHex = 0xFF8C7556
        ),
        MissionBlockSlot(
            index = 1,
            startHour24 = 9,
            endHour24 = 13,
            rangeShortEn = "9 AM – 1 PM",
            rangeFullEn = "09:00 AM – 01:00 PM",
            rangeFullBn = "সকাল ৯:০০ – দুপুর ১:০০",
            phaseTitleEn = "Forenoon Deep Work",
            phaseTitleBn = "সকালের গভীর অধ্যয়ন",
            accentColorHex = 0xFF5D7A64
        ),
        MissionBlockSlot(
            index = 2,
            startHour24 = 13,
            endHour24 = 17,
            rangeShortEn = "1 PM – 5 PM",
            rangeFullEn = "01:00 PM – 05:00 PM",
            rangeFullBn = "দুপুর ১:০০ – বিকাল ৫:০০",
            phaseTitleEn = "Afternoon Sprint",
            phaseTitleBn = "দুপুর ও বিকালের স্প্রিন্ট",
            accentColorHex = 0xFFC27D38
        ),
        MissionBlockSlot(
            index = 3,
            startHour24 = 17,
            endHour24 = 21,
            rangeShortEn = "5 PM – 9 PM",
            rangeFullEn = "05:00 PM – 09:00 PM",
            rangeFullBn = "বিকাল ৫:০০ – রাত ৯:০০",
            phaseTitleEn = "Evening Mastery",
            phaseTitleBn = "সন্ধ্যার অনুশীলন",
            accentColorHex = 0xFF6B7F9E
        ),
        MissionBlockSlot(
            index = 4,
            startHour24 = 21,
            endHour24 = 1,
            rangeShortEn = "9 PM – 1 AM",
            rangeFullEn = "09:00 PM – 01:00 AM",
            rangeFullBn = "রাত ৯:০০ – রাত ১:০০",
            phaseTitleEn = "Night Revision",
            phaseTitleBn = "রাতের রিভিশন",
            accentColorHex = 0xFF7D6998
        ),
        MissionBlockSlot(
            index = 5,
            startHour24 = 1,
            endHour24 = 5,
            rangeShortEn = "1 AM – 5 AM",
            rangeFullEn = "01:00 AM – 05:00 AM",
            rangeFullBn = "রাত ১:০০ – ভোর ৫:০০",
            phaseTitleEn = "Late Night / Rest",
            phaseTitleBn = "গভীর রাত / বিশ্রাম",
            accentColorHex = 0xFF6A645F
        )
    )

    val categories: List<String> = listOf(
        "Study",
        "Physics",
        "Chemistry",
        "Math",
        "Biology",
        "Quran & Ibadah",
        "Exam Prep",
        "Health & Rest"
    )

    fun blockForIndex(index: Int): MissionBlockSlot =
        blocks[index.coerceIn(0, blocks.lastIndex)]

    fun blockIndexForHour(hour24: Int): Int {
        val h = ((hour24 % 24) + 24) % 24
        return ((h - 5 + 24) % 24) / 4
    }

    fun elapsedMillisInCurrentBlock(now: Calendar = Calendar.getInstance()): Long {
        val h = now.get(Calendar.HOUR_OF_DAY)
        val m = now.get(Calendar.MINUTE)
        val s = now.get(Calendar.SECOND)
        val ms = now.get(Calendar.MILLISECOND)
        val hourOffsetInBlock = ((h - 5 + 24) % 24) % 4
        return ((hourOffsetInBlock * 3600L + m * 60L + s) * 1000L) + ms
    }

    fun progressInCurrentBlock(now: Calendar = Calendar.getInstance()): Float {
        val fourHoursMs = 4L * 3600L * 1000L
        return (elapsedMillisInCurrentBlock(now).toFloat() / fourHoursMs.toFloat()).coerceIn(0f, 1f)
    }

    fun remainingMillisInCurrentBlock(now: Calendar = Calendar.getInstance()): Long {
        val fourHoursMs = 4L * 3600L * 1000L
        return (fourHoursMs - elapsedMillisInCurrentBlock(now)).coerceAtLeast(0L)
    }

    fun next4HourBoundaryMillis(now: Calendar = Calendar.getInstance()): Long {
        val remaining = remainingMillisInCurrentBlock(now)
        return now.timeInMillis + remaining.coerceAtLeast(1_000L)
    }

    fun formatHmsCountdown(remainingMillis: Long): String {
        val totalSecs = (remainingMillis / 1_000L).coerceAtLeast(0L)
        val hrs = totalSecs / 3600L
        val mins = (totalSecs % 3600L) / 60L
        val secs = totalSecs % 60L
        return String.format(Locale.US, "%02d:%02d:%02d", hrs, mins, secs)
    }

    fun formatRemainingCountdown(remainingMillis: Long, isBangla: Boolean = false): String {
        val totalMinutes = (remainingMillis / 60_000L).coerceAtLeast(0L)
        val hours = totalMinutes / 60L
        val mins = totalMinutes % 60L
        return if (isBangla) {
            "${hours}ঘ ${mins}মি বাকি"
        } else {
            String.format(Locale.US, "%dh %02dm left", hours, mins)
        }
    }

    fun formatShortOffsetCountdown(remainingMillis: Long): String {
        val totalMinutes = (remainingMillis / 60_000L).coerceAtLeast(0L)
        val hours = totalMinutes / 60L
        val mins = totalMinutes % 60L
        return String.format(Locale.US, "-%dh %02dm", hours, mins)
    }

    fun formatTime12h(hour24: Int, minute: Int): String {
        val hNorm = ((hour24 % 24) + 24) % 24
        val amPm = if (hNorm < 12) "AM" else "PM"
        val h12 = when (hNorm % 12) {
            0 -> 12
            else -> hNorm % 12
        }
        return String.format(Locale.US, "%02d:%02d %s", h12, minute.coerceIn(0, 59), amPm)
    }

    fun formatBlockStartShort(slot: MissionBlockSlot): String =
        formatTime12h(slot.startHour24, 0)

    fun formatAcademicSummary(mission: MissionItem, isBangla: Boolean): String {
        val parts = mutableListOf<String>()
        if (mission.subjectName.isNotBlank()) {
            val paperSuffix = when (mission.paper) {
                1 -> if (isBangla) " (১ম)" else " (1st)"
                2 -> if (isBangla) " (২য়)" else " (2nd)"
                else -> ""
            }
            parts.add("${mission.subjectName}$paperSuffix")
        } else if (mission.category.isNotBlank()) {
            parts.add(mission.category)
        }
        if (mission.chapterTitle.isNotBlank()) {
            parts.add(mission.chapterTitle)
        }
        if (mission.targetTags.isNotEmpty()) {
            val tagStr = PrepQuestionTag.entries
                .filter { it in mission.targetTags }
                .joinToString(" ") { tag ->
                    if (tag in mission.completedTags) "✓${tag.label}" else tag.label
                }
            parts.add("[$tagStr]")
        }
        return parts.joinToString(" • ")
    }

    fun todayDateKey(now: Calendar = Calendar.getInstance()): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.US).format(now.time)

    fun generateDateStripKeys(centerDateKey: String? = null, daysBefore: Int = 3, daysAfter: Int = 14): List<String> {
        val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val base = Calendar.getInstance()
        if (!centerDateKey.isNullOrBlank()) {
            try {
                fmt.parse(centerDateKey)?.let { base.time = it }
            } catch (_: Exception) {
            }
        }
        val list = mutableListOf<String>()
        val cursor = (base.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, -daysBefore) }
        repeat(daysBefore + 1 + daysAfter) {
            list.add(fmt.format(cursor.time))
            cursor.add(Calendar.DAY_OF_YEAR, 1)
        }
        return list
    }

    fun formatDateChipParts(dateKey: String, isBangla: Boolean): Triple<String, String, String> {
        val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val cal = Calendar.getInstance()
        try {
            fmt.parse(dateKey)?.let { cal.time = it }
        } catch (_: Exception) {
        }
        val locale = if (isBangla) Locale.forLanguageTag("bn-BD") else Locale.US
        val dayOfWeek = SimpleDateFormat("EEE", locale).format(cal.time)
        val dayNum = SimpleDateFormat("dd", Locale.US).format(cal.time)
        val monthShort = SimpleDateFormat("MMM", locale).format(cal.time)
        return Triple(dayOfWeek, dayNum, monthShort)
    }

    fun formatDateFullHeader(dateKey: String, isBangla: Boolean): String {
        val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val cal = Calendar.getInstance()
        try {
            fmt.parse(dateKey)?.let { cal.time = it }
        } catch (_: Exception) {
        }
        val locale = if (isBangla) Locale.forLanguageTag("bn-BD") else Locale.US
        return SimpleDateFormat("EEEE, dd MMMM yyyy", locale).format(cal.time)
    }

    fun resolveAlarmTriggerTimeMillis(mission: MissionItem): Long {
        val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val cal = Calendar.getInstance()
        try {
            fmt.parse(mission.dateKey)?.let { cal.time = it }
        } catch (_: Exception) {
        }
        val slot = blockForIndex(mission.blockIndex)
        if (mission.alarmMode == MissionAlarmMode.CUSTOM_TIME) {
            cal.set(Calendar.HOUR_OF_DAY, mission.customAlarmHour.coerceIn(0, 23))
            cal.set(Calendar.MINUTE, mission.customAlarmMinute.coerceIn(0, 59))
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            return cal.timeInMillis
        }

        cal.set(Calendar.HOUR_OF_DAY, slot.startHour24)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        if (mission.alarmMode.offsetMinutes != 0) {
            cal.add(Calendar.MINUTE, mission.alarmMode.offsetMinutes)
        }
        return cal.timeInMillis
    }

    fun formatAlarmSummary(mission: MissionItem, isBangla: Boolean): String {
        if (!mission.alarmEnabled) return if (isBangla) "অ্যালার্ম বন্ধ" else "Alarm off"
        val slot = blockForIndex(mission.blockIndex)
        val cal = Calendar.getInstance().apply {
            if (mission.alarmMode == MissionAlarmMode.CUSTOM_TIME) {
                set(Calendar.HOUR_OF_DAY, mission.customAlarmHour.coerceIn(0, 23))
                set(Calendar.MINUTE, mission.customAlarmMinute.coerceIn(0, 59))
            } else {
                set(Calendar.HOUR_OF_DAY, slot.startHour24)
                set(Calendar.MINUTE, 0)
                add(Calendar.MINUTE, mission.alarmMode.offsetMinutes)
            }
        }
        val formattedTime = formatTime12h(
            cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE)
        )
        return if (isBangla) {
            "অ্যালার্ম $formattedTime"
        } else {
            "Alarm $formattedTime"
        }
    }

    fun createStarterMissions(todayKey: String = todayDateKey()): List<MissionItem> = listOf(
        MissionItem(
            id = "msn_starter_0",
            dateKey = todayKey,
            blockIndex = 0,
            title = "Fajr, Quran & Physics Vector Basics",
            notes = "20 mins Tilawat + Solve Ka & Kha concepts",
            category = "Physics",
            subjectId = "trk_physics",
            subjectName = "Physics",
            paper = 1,
            chapterId = "trk_physics_p1_ch_2",
            chapterTitle = "ভেক্টর",
            targetTags = setOf(PrepQuestionTag.KA, PrepQuestionTag.KHA, PrepQuestionTag.MCQ),
            completedTags = setOf(PrepQuestionTag.KA),
            priority = MissionPriority.HIGH,
            isCompleted = false,
            alarmEnabled = true,
            alarmMode = MissionAlarmMode.AT_BLOCK_START,
            customAlarmHour = 5,
            customAlarmMinute = 0,
            useAlarmTone = true
        ),
        MissionItem(
            id = "msn_starter_1",
            dateKey = todayKey,
            blockIndex = 1,
            title = "Vector CQ + Organic Reactions",
            notes = "Solve 2 Board CQ (গ ও ঘ) + 25 MCQs",
            category = "Physics",
            subjectId = "trk_physics",
            subjectName = "Physics",
            paper = 1,
            chapterId = "trk_physics_p1_ch_4",
            chapterTitle = "নিউটনীয় বলবিদ্যা",
            targetTags = setOf(PrepQuestionTag.GA, PrepQuestionTag.GHA, PrepQuestionTag.CQ, PrepQuestionTag.MCQ),
            completedTags = emptySet(),
            priority = MissionPriority.CRITICAL,
            isCompleted = false,
            alarmEnabled = true,
            alarmMode = MissionAlarmMode.AT_BLOCK_START,
            customAlarmHour = 9,
            customAlarmMinute = 0,
            useAlarmTone = true
        ),
        MissionItem(
            id = "msn_starter_3",
            dateKey = todayKey,
            blockIndex = 3,
            title = "Chemistry Qualitative Analysis Mock",
            notes = "Evening timed CQ + MCQ set",
            category = "Chemistry",
            subjectId = "trk_chemistry",
            subjectName = "Chemistry",
            paper = 1,
            chapterId = "trk_chemistry_p1_ch_2",
            chapterTitle = "গুণগত রসায়ন",
            targetTags = setOf(PrepQuestionTag.KA, PrepQuestionTag.KHA, PrepQuestionTag.CQ, PrepQuestionTag.MCQ),
            completedTags = emptySet(),
            priority = MissionPriority.HIGH,
            isCompleted = false,
            alarmEnabled = true,
            alarmMode = MissionAlarmMode.FIFTEEN_MIN_BEFORE,
            customAlarmHour = 17,
            customAlarmMinute = 0,
            useAlarmTone = true
        )
    )
}
