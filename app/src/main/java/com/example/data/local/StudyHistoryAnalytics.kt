package com.example.data.local

import com.example.data.model.GroupMember
import com.example.data.model.StudyGroup
import java.util.Calendar
import kotlin.math.abs
import kotlin.math.roundToInt

data class SubjectDurationShare(
    val subject: String,
    val percent: Int,
    val seconds: Long
)

data class StudentDayRecord(
    val year: Int,
    val monthZeroBased: Int,
    val dayOfMonth: Int,
    val totalSeconds: Long,
    val targetSeconds: Long,
    val focusScorePercent: Int,
    val longestBlockMinutes: Int,
    val hourlyMinutes: List<Int>, // 24 slots (0..23), values 0..60
    val subjectShares: List<SubjectDurationShare>,
    val isFuture: Boolean,
    val isToday: Boolean
)

data class MonthStudySummary(
    val totalSeconds: Long,
    val activeDays: Int,
    val dailyAverageSeconds: Long
)

data class WeeklyTrendPoint(
    val dayNumber: Int,
    val weekdayShort: String,
    val hours: Float,
    val isSelected: Boolean,
    val isFuture: Boolean
)

object StudyHistoryAnalytics {

    private fun isAfterDay(
        y: Int,
        m: Int,
        d: Int,
        refYear: Int,
        refMonth: Int,
        refDay: Int
    ): Boolean {
        if (y != refYear) return y > refYear
        if (m != refMonth) return m > refMonth
        return d > refDay
    }

    private fun isSameDay(
        y: Int,
        m: Int,
        d: Int,
        refYear: Int,
        refMonth: Int,
        refDay: Int
    ): Boolean = y == refYear && m == refMonth && d == refDay

    private fun stableHash(seed: String): Long {
        var h = 1125899906842597L
        for (ch in seed) {
            h = 31L * h + ch.code.toLong()
        }
        return abs(h)
    }

    fun getDayRecord(
        member: GroupMember,
        group: StudyGroup,
        year: Int,
        monthZeroBased: Int,
        dayOfMonth: Int,
        isBangla: Boolean,
        nowCal: Calendar = Calendar.getInstance()
    ): StudentDayRecord {
        val refY = nowCal.get(Calendar.YEAR)
        val refM = nowCal.get(Calendar.MONTH)
        val refD = nowCal.get(Calendar.DAY_OF_MONTH)
        val targetSec = (group.dailyTargetHours * 3600L).coerceAtLeast(3600L)

        val isFuture = isAfterDay(year, monthZeroBased, dayOfMonth, refY, refM, refD)
        val isToday = isSameDay(year, monthZeroBased, dayOfMonth, refY, refM, refD)

        if (isFuture) {
            return StudentDayRecord(
                year = year,
                monthZeroBased = monthZeroBased,
                dayOfMonth = dayOfMonth,
                totalSeconds = 0L,
                targetSeconds = targetSec,
                focusScorePercent = 0,
                longestBlockMinutes = 0,
                hourlyMinutes = List(24) { 0 },
                subjectShares = emptyList(),
                isFuture = true,
                isToday = false
            )
        }

        val hash = stableHash("${member.id}_${year}_${monthZeroBased}_${dayOfMonth}")
        val totalSeconds = if (isToday) {
            member.todayStudySeconds
        } else {
            // Check if this date is within member's recent streak
            val cellCal = Calendar.getInstance().apply { set(year, monthZeroBased, dayOfMonth, 12, 0, 0) }
            val todayNoon = Calendar.getInstance().apply { set(refY, refM, refD, 12, 0, 0) }
            val daysAgo = ((todayNoon.timeInMillis - cellCal.timeInMillis) / 86_400_000L).toInt()
            val withinStreak = daysAgo in 0..member.streakDays

            val isRestDay = !withinStreak && (hash % 6L == 0L)
            if (isRestDay) {
                0L
            } else {
                val baseAnchor = if (member.todayStudySeconds > 2_400L) {
                    member.todayStudySeconds
                } else {
                    (group.dailyTargetHours * 2700L)
                }
                val factorPct = 55L + (hash % 75L) // 55%..129% of anchor
                val jitterSec = (hash / 7L) % 1750L
                ((baseAnchor * factorPct) / 100L + jitterSec).coerceIn(1_200L, 11 * 3600L)
            }
        }

        val totalMinutes = (totalSeconds / 60L).toInt()
        val hourly = buildHourlyDistribution(totalMinutes, hash, isToday, nowCal.get(Calendar.HOUR_OF_DAY))

        val subjectPool = group.members
            .map { if (isBangla) it.subjectBn else it.subjectEn }
            .distinct()
            .ifEmpty { listOf(if (isBangla) member.subjectBn else member.subjectEn) }

        val primarySubj = if (isBangla) member.subjectBn else member.subjectEn
        val orderedSubjects = (listOf(primarySubj) + subjectPool).distinct().take(4)
        val shares = buildSubjectShares(totalSeconds, orderedSubjects, hash)

        val focusScore = if (totalSeconds == 0L) {
            0
        } else {
            (74 + (hash % 24)).toInt().coerceIn(72, 98)
        }
        val maxHourMin = hourly.maxOrNull() ?: 0
        val longestBlock = if (totalMinutes == 0) 0 else maxOf(maxHourMin, (totalMinutes / 3).coerceIn(15, 95))

        return StudentDayRecord(
            year = year,
            monthZeroBased = monthZeroBased,
            dayOfMonth = dayOfMonth,
            totalSeconds = totalSeconds,
            targetSeconds = targetSec,
            focusScorePercent = focusScore,
            longestBlockMinutes = longestBlock,
            hourlyMinutes = hourly,
            subjectShares = shares,
            isFuture = false,
            isToday = isToday
        )
    }

    private fun buildHourlyDistribution(
        totalMinutes: Int,
        hash: Long,
        isToday: Boolean,
        currentHour: Int
    ): List<Int> {
        if (totalMinutes <= 0) return List(24) { 0 }

        // Weight hours like Dhaka student YPT pattern: post-Fajr (5-7), morning (9-12), afternoon (15-17), night (20-23)
        val templateWeights = intArrayOf(
            0, 0, 0, 0, 8,   // 0..4
            35, 48, 36, 18,  // 5..8 (Fajr & morning)
            42, 52, 40, 14,  // 9..12
            10, 24, 38, 44,  // 13..16
            22, 16, 36, 54,  // 17..20 (Evening/Maghrib-Isha)
            58, 46, 22       // 21..23 (Late night)
        )

        val weights = IntArray(24) { h ->
            if (isToday && h > currentHour) {
                0
            } else {
                val base = templateWeights[h]
                val mod = ((hash shr (h % 20)) and 0x7L).toInt()
                if (base == 0 && mod < 6) 0 else base + mod * 4
            }
        }
        val weightSum = weights.sum().coerceAtLeast(1)

        val result = MutableList(24) { 0 }
        var remaining = totalMinutes
        // First proportional pass capped at 60 mins/hr
        for (h in 0 until 24) {
            if (weights[h] > 0 && remaining > 0) {
                val alloc = ((totalMinutes.toFloat() * weights[h]) / weightSum).roundToInt().coerceIn(0, 60)
                val placed = alloc.coerceAtMost(remaining)
                result[h] = placed
                remaining -= placed
            }
        }
        // Distribute any leftover minutes into eligible peak slots
        if (remaining > 0) {
            val priorityHours = (0 until 24)
                .filter { !isToday || it <= currentHour }
                .sortedByDescending { weights[it] }
            for (h in priorityHours) {
                if (remaining <= 0) break
                val room = 60 - result[h]
                if (room > 0) {
                    val add = room.coerceAtMost(remaining)
                    result[h] += add
                    remaining -= add
                }
            }
        }
        return result
    }

    private fun buildSubjectShares(
        totalSeconds: Long,
        subjects: List<String>,
        hash: Long
    ): List<SubjectDurationShare> {
        if (totalSeconds <= 0L || subjects.isEmpty()) return emptyList()
        if (subjects.size == 1) {
            return listOf(SubjectDurationShare(subjects.first(), 100, totalSeconds))
        }
        val rawWeights = subjects.mapIndexed { idx, _ ->
            val base = when (idx) {
                0 -> 48
                1 -> 28
                2 -> 16
                else -> 10
            }
            base + (((hash shr (idx * 3)) and 0x7L).toInt() * 2)
        }
        val sum = rawWeights.sum().coerceAtLeast(1)
        var pctAcc = 0
        var secAcc = 0L
        return subjects.mapIndexed { idx, subj ->
            if (idx == subjects.lastIndex) {
                SubjectDurationShare(
                    subject = subj,
                    percent = (100 - pctAcc).coerceAtLeast(1),
                    seconds = (totalSeconds - secAcc).coerceAtLeast(0L)
                )
            } else {
                val pct = ((rawWeights[idx] * 100f) / sum).roundToInt().coerceIn(5, 90)
                val sec = (totalSeconds * pct) / 100L
                pctAcc += pct
                secAcc += sec
                SubjectDurationShare(subject = subj, percent = pct, seconds = sec)
            }
        }
    }

    fun getMonthSummary(
        member: GroupMember,
        group: StudyGroup,
        year: Int,
        monthZeroBased: Int,
        isBangla: Boolean,
        nowCal: Calendar = Calendar.getInstance()
    ): MonthStudySummary {
        val cal = Calendar.getInstance().apply { set(year, monthZeroBased, 1) }
        val maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        var totalSec = 0L
        var activeDays = 0
        var elapsedDays = 0
        for (d in 1..maxDays) {
            val rec = getDayRecord(member, group, year, monthZeroBased, d, isBangla, nowCal)
            if (!rec.isFuture) {
                elapsedDays++
                totalSec += rec.totalSeconds
                if (rec.totalSeconds > 0L) activeDays++
            }
        }
        val avg = if (elapsedDays > 0) totalSec / elapsedDays else 0L
        return MonthStudySummary(totalSeconds = totalSec, activeDays = activeDays, dailyAverageSeconds = avg)
    }

    fun getSevenDayTrend(
        member: GroupMember,
        group: StudyGroup,
        year: Int,
        monthZeroBased: Int,
        selectedDay: Int,
        isBangla: Boolean,
        nowCal: Calendar = Calendar.getInstance()
    ): List<WeeklyTrendPoint> {
        val weekdayEn = arrayOf("", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
        val weekdayBn = arrayOf("", "রবি", "সোম", "মঙ্গল", "বুধ", "বৃহ", "শুক্র", "শনি")

        val centerCal = Calendar.getInstance().apply { set(year, monthZeroBased, selectedDay) }
        val points = mutableListOf<WeeklyTrendPoint>()
        // Show 7-day window ending on selectedDay (or if selectedDay < 7 still smoothly crosses month boundary)
        for (offset in -6..0) {
            val c = (centerCal.clone() as Calendar).apply { add(Calendar.DAY_OF_MONTH, offset) }
            val y = c.get(Calendar.YEAR)
            val m = c.get(Calendar.MONTH)
            val d = c.get(Calendar.DAY_OF_MONTH)
            val dow = c.get(Calendar.DAY_OF_WEEK)
            val rec = getDayRecord(member, group, y, m, d, isBangla, nowCal)
            val hrs = (rec.totalSeconds / 3600f)
            points += WeeklyTrendPoint(
                dayNumber = d,
                weekdayShort = if (isBangla) weekdayBn[dow] else weekdayEn[dow],
                hours = hrs,
                isSelected = offset == 0,
                isFuture = rec.isFuture
            )
        }
        return points
    }
}
