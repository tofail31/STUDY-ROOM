package com.example

import com.example.data.calc.PrayerTimeCalculator
import com.example.data.local.AsmaulHusnaData
import com.example.data.local.IslamicContentData
import com.example.data.local.QuranCatalogData
import com.example.data.local.StudyGroupsData
import com.example.data.local.StudyHistoryAnalytics
import com.example.data.model.CalculationMethodOption
import com.example.ui.screens.formatStudyHms
import com.example.ui.theme.BanglaStrings
import com.example.ui.theme.EnglishStrings
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

class ExampleUnitTest {
    @Test
    fun `hijri date matches 9 Rajab 1444 H on 31 January 2023`() {
        val cal = Calendar.getInstance().apply {
            set(2023, Calendar.JANUARY, 31, 6, 4, 0)
        }
        val formatted = PrayerTimeCalculator.formatCombinedDate(cal)
        assertEquals("31 January / 9 Rajab 1444 H", formatted)

        val formattedBn = PrayerTimeCalculator.formatCombinedDate(cal, isBangla = true)
        assertEquals("৩১ জানুয়ারি / ৯ রজব ১৪৪৪ হি.", formattedBn)
        assertFalse(EnglishStrings.isBangla)
        assertTrue(BanglaStrings.isBangla)
    }

    @Test
    fun `dhaka prayer schedule and catalogs are valid`() {
        val schedule = PrayerTimeCalculator.calculateDaySchedule(
            latitude = 23.8103,
            longitude = 90.4125,
            timeZoneHours = 6.0,
            method = CalculationMethodOption.BANGLADESH
        )
        assertTrue(schedule.subuh.contains(":"))
        assertTrue(schedule.dzuhur.contains(":"))
        assertTrue(schedule.maghrib.contains(":"))
        assertEquals("dhaka", IslamicContentData.cityPresets.first().id)
        assertEquals(114, QuranCatalogData.allSurahs.size)
        assertEquals(99, AsmaulHusnaData.items.size)
    }

    @Test
    fun `study groups adhere to 30-member cap and format timers in EN and BN`() {
        assertTrue(StudyGroupsData.groups.isNotEmpty())
        StudyGroupsData.groups.forEach { group ->
            assertEquals(30, group.maxMembers)
            assertTrue(group.members.size in 1..29)
            assertTrue(group.rulesEn.isNotEmpty())
            assertTrue(group.rulesBn.isNotEmpty())
        }
        assertEquals("01:05:09", formatStudyHms(3909L, isBangla = false))
        assertEquals("০১:০৫:০৯", formatStudyHms(3909L, isBangla = true))
    }

    @Test
    fun `student history analytics generates calendar day hours, 24h distribution, and 7d trend`() {
        val group = StudyGroupsData.groups.first()
        val member = group.members.first()
        val nowCal = Calendar.getInstance().apply { set(2026, Calendar.NOVEMBER, 19, 18, 0, 0) }

        val todayRec = StudyHistoryAnalytics.getDayRecord(
            member = member,
            group = group,
            year = 2026,
            monthZeroBased = Calendar.NOVEMBER,
            dayOfMonth = 19,
            isBangla = false,
            nowCal = nowCal
        )
        assertEquals(member.todayStudySeconds, todayRec.totalSeconds)
        assertEquals(24, todayRec.hourlyMinutes.size)
        assertTrue(todayRec.isToday)
        assertFalse(todayRec.isFuture)
        assertEquals(100, todayRec.subjectShares.sumOf { it.percent })

        val futureRec = StudyHistoryAnalytics.getDayRecord(
            member = member,
            group = group,
            year = 2026,
            monthZeroBased = Calendar.NOVEMBER,
            dayOfMonth = 25,
            isBangla = false,
            nowCal = nowCal
        )
        assertTrue(futureRec.isFuture)
        assertEquals(0L, futureRec.totalSeconds)

        val trend = StudyHistoryAnalytics.getSevenDayTrend(
            member = member,
            group = group,
            year = 2026,
            monthZeroBased = Calendar.NOVEMBER,
            selectedDay = 19,
            isBangla = false,
            nowCal = nowCal
        )
        assertEquals(7, trend.size)
        assertTrue(trend.last().isSelected)
    }
}
