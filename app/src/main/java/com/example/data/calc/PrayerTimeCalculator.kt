package com.example.data.calc

import com.example.data.model.CalculationMethodOption
import com.example.data.model.PrayerSchedule
import java.util.Calendar
import java.util.Locale
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.tan

object PrayerTimeCalculator {

    private val gregorianMonthsEn = listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )

    private val gregorianMonthsBn = listOf(
        "জানুয়ারি", "ফেব্রুয়ারি", "মার্চ", "এপ্রিল", "মে", "জুন",
        "জুলাই", "আগস্ট", "সেপ্টেম্বর", "অক্টোবর", "নভেম্বর", "ডিসেম্বর"
    )

    private val hijriMonthsEn = listOf(
        "Muharram", "Safar", "Rabi' al-Awwal", "Rabi' al-Thani",
        "Jumada al-Ula", "Jumada al-Akhirah", "Rajab", "Sha'ban",
        "Ramadan", "Shawwal", "Dhu al-Qi'dah", "Dhu al-Hijjah"
    )

    private val hijriMonthsBn = listOf(
        "মুহাররম", "সফর", "রবিউল আউয়াল", "রবিউস সানি",
        "জুমাদাল উলা", "জুমাদাস সানি", "রজব", "শাবান",
        "রমজান", "শাওয়াল", "জিলকদ", "জিলহজ"
    )

    fun toBanglaDigits(input: String): String {
        val bn = charArrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')
        return buildString(input.length) {
            for (ch in input) {
                if (ch in '0'..'9') append(bn[ch - '0']) else append(ch)
            }
        }
    }

    fun gregorianToHijri(calendar: Calendar, offsetDays: Int = 0): Triple<Int, Int, Int> {
        val cal = (calendar.clone() as Calendar).apply {
            add(Calendar.DAY_OF_MONTH, offsetDays)
        }
        val d = cal.get(Calendar.DAY_OF_MONTH)
        val m = cal.get(Calendar.MONTH) + 1
        val y = cal.get(Calendar.YEAR)

        val jd = if ((y > 1582) || (y == 1582 && m > 10) || (y == 1582 && m == 10 && d > 14)) {
            ((1461 * (y + 4800 + (m - 14) / 12)) / 4) +
                ((367 * (m - 2 - 12 * ((m - 14) / 12))) / 12) -
                ((3 * ((y + 4900 + (m - 14) / 12) / 100)) / 4) +
                d - 32075
        } else {
            367 * y - ((7 * (y + 5001 + (m - 9) / 7)) / 4) + ((275 * m) / 9) + d + 1729777
        }

        var l = jd - 1948440 + 10632
        val n = (l - 1) / 10631
        l = l - 10631 * n + 354
        val j = ((10985 - l) / 5316) * ((50 * l) / 17719) + (l / 5670) * ((43 * l) / 15238)
        l = l - ((30 - j) / 15) * ((17719 * j) / 50) - (j / 16) * ((15238 * j) / 43) + 29
        val hMonth = (24 * l) / 709
        val hDay = l - (709 * hMonth) / 24
        val hYear = 30 * n + j - 30

        return Triple(hDay, hMonth, hYear)
    }

    fun formatCombinedDate(
        calendar: Calendar = Calendar.getInstance(),
        hijriOffsetDays: Int = 0,
        isBangla: Boolean = false
    ): String {
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val monthIdx = calendar.get(Calendar.MONTH).coerceIn(0, 11)
        val (hDay, hMonth, hYear) = gregorianToHijri(calendar, hijriOffsetDays)
        val hIdx = (hMonth - 1).coerceIn(0, 11)
        return if (isBangla) {
            toBanglaDigits("$day ${gregorianMonthsBn[monthIdx]} / $hDay ${hijriMonthsBn[hIdx]} $hYear হি.")
        } else {
            "$day ${gregorianMonthsEn[monthIdx]} / $hDay ${hijriMonthsEn[hIdx]} $hYear H"
        }
    }

    fun getHijriMonthYear(
        calendar: Calendar = Calendar.getInstance(),
        hijriOffsetDays: Int = 0,
        isBangla: Boolean = false
    ): Pair<String, Int> {
        val (_, hMonth, hYear) = gregorianToHijri(calendar, hijriOffsetDays)
        val hIdx = (hMonth - 1).coerceIn(0, 11)
        val name = if (isBangla) hijriMonthsBn[hIdx] else hijriMonthsEn[hIdx]
        return name to hYear
    }

    fun calculateDaySchedule(
        latitude: Double,
        longitude: Double,
        timeZoneHours: Double,
        method: CalculationMethodOption,
        now: Calendar = Calendar.getInstance()
    ): PrayerSchedule {
        val year = now.get(Calendar.YEAR)
        val month = now.get(Calendar.MONTH) + 1
        val day = now.get(Calendar.DAY_OF_MONTH)

        val jd = julianDate(year, month, day) - longitude / (15.0 * 24.0)
        val (declination, eqTime) = sunPosition(jd + 0.5)

        val transit = 12.0 + timeZoneHours - longitude / 15.0 - eqTime
        val dhuhr = transit + 2.0 / 60.0

        val sunriseAngle = 0.833
        val sunriseDiff = hourAngle(latitude, declination, sunriseAngle)
        val syuruq = transit - sunriseDiff
        val maghrib = transit + sunriseDiff + 2.0 / 60.0

        val dhuha = syuruq + 20.0 / 60.0
        val fajrDiff = hourAngle(latitude, declination, method.fajrAngle)
        val subuh = transit - fajrDiff

        val asrAlt = Math.toDegrees(atan2(1.0, 1.0 + tan(Math.toRadians(kotlin.math.abs(latitude - declination)))))
        val asrZenith = 90.0 - asrAlt
        val ashar = transit + hourAngle(latitude, declination, asrZenith) + 1.0 / 60.0

        val isya = if (method == CalculationMethodOption.UMM_AL_QURA) {
            maghrib + 1.5
        } else {
            transit + hourAngle(latitude, declination, method.ishaAngle) + 1.0 / 60.0
        }

        val nowMinutes = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE)
        val nowSeconds = nowMinutes * 60 + now.get(Calendar.SECOND)

        val subuhMin = toMinutes(subuh)
        val syuruqMin = toMinutes(syuruq)
        val dhuhaMin = toMinutes(dhuha)
        val dzuhurMin = toMinutes(dhuhr)
        val asharMin = toMinutes(ashar)
        val maghribMin = toMinutes(maghrib)
        val isyaMin = toMinutes(isya)

        val currentWindow = when {
            nowMinutes < subuhMin -> "Tahajjud Time"
            nowMinutes < syuruqMin -> "Fajr Time"
            nowMinutes < dhuhaMin -> "Sunrise"
            nowMinutes < dzuhurMin -> "Dhuha Time"
            nowMinutes < asharMin -> "Dhuhr Time"
            nowMinutes < maghribMin -> "Asr Time"
            nowMinutes < isyaMin -> "Maghrib Time"
            else -> "Isha Time"
        }

        val (nextName, nextMin) = when {
            nowMinutes < subuhMin -> "Fajr" to subuhMin
            nowMinutes < syuruqMin -> "Sunrise" to syuruqMin
            nowMinutes < dzuhurMin -> "Dhuhr" to dzuhurMin
            nowMinutes < asharMin -> "Asr" to asharMin
            nowMinutes < maghribMin -> "Maghrib" to maghribMin
            nowMinutes < isyaMin -> "Isha" to isyaMin
            else -> "Fajr" to (subuhMin + 1440)
        }

        val diffSec = (nextMin * 60 - nowSeconds).coerceAtLeast(0)
        val remH = diffSec / 3600
        val remM = (diffSec % 3600) / 60
        val remS = diffSec % 60
        val countdown = String.format(Locale.US, "%02d:%02d:%02d", remH, remM, remS)

        return PrayerSchedule(
            subuh = formatHours(subuh),
            syuruq = formatHours(syuruq),
            dhuha = formatHours(dhuha),
            dzuhur = formatHours(dhuhr),
            ashar = formatHours(ashar),
            maghrib = formatHours(maghrib),
            isya = formatHours(isya),
            currentWindowLabel = currentWindow,
            nextPrayerName = nextName,
            nextPrayerTime = formatHours((nextMin % 1440) / 60.0),
            countdownText = countdown,
            isFromApi = false
        )
    }

    fun enrichWithApiTimes(
        base: PrayerSchedule,
        fajr: String,
        sunrise: String,
        dhuhr: String,
        asr: String,
        maghrib: String,
        isha: String,
        now: Calendar = Calendar.getInstance()
    ): PrayerSchedule {
        val cleanFajr = fajr.take(5)
        val cleanSunrise = sunrise.take(5)
        val cleanDhuhr = dhuhr.take(5)
        val cleanAsr = asr.take(5)
        val cleanMaghrib = maghrib.take(5)
        val cleanIsha = isha.take(5)

        val sunriseMin = parseMinutes(cleanSunrise)
        val dhuhaMin = sunriseMin + 20
        val cleanDhuha = String.format(Locale.US, "%02d:%02d", (dhuhaMin / 60) % 24, dhuhaMin % 60)

        val nowMin = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE)
        val nowSec = nowMin * 60 + now.get(Calendar.SECOND)

        val fM = parseMinutes(cleanFajr)
        val sM = sunriseMin
        val dzM = parseMinutes(cleanDhuhr)
        val aM = parseMinutes(cleanAsr)
        val mM = parseMinutes(cleanMaghrib)
        val iM = parseMinutes(cleanIsha)

        val currentWindow = when {
            nowMin < fM -> "Tahajjud Time"
            nowMin < sM -> "Fajr Time"
            nowMin < dhuhaMin -> "Sunrise"
            nowMin < dzM -> "Dhuha Time"
            nowMin < aM -> "Dhuhr Time"
            nowMin < mM -> "Asr Time"
            nowMin < iM -> "Maghrib Time"
            else -> "Isha Time"
        }

        val (nextName, nextTime, nextMin) = when {
            nowMin < fM -> Triple("Fajr", cleanFajr, fM)
            nowMin < sM -> Triple("Sunrise", cleanSunrise, sM)
            nowMin < dzM -> Triple("Dhuhr", cleanDhuhr, dzM)
            nowMin < aM -> Triple("Asr", cleanAsr, aM)
            nowMin < mM -> Triple("Maghrib", cleanMaghrib, mM)
            nowMin < iM -> Triple("Isha", cleanIsha, iM)
            else -> Triple("Fajr", cleanFajr, fM + 1440)
        }

        val diffSec = (nextMin * 60 - nowSec).coerceAtLeast(0)
        val countdown = String.format(
            Locale.US,
            "%02d:%02d:%02d",
            diffSec / 3600,
            (diffSec % 3600) / 60,
            diffSec % 60
        )

        return base.copy(
            subuh = cleanFajr,
            syuruq = cleanSunrise,
            dhuha = cleanDhuha,
            dzuhur = cleanDhuhr,
            ashar = cleanAsr,
            maghrib = cleanMaghrib,
            isya = cleanIsha,
            currentWindowLabel = currentWindow,
            nextPrayerName = nextName,
            nextPrayerTime = nextTime,
            countdownText = countdown,
            isFromApi = true
        )
    }

    private fun julianDate(year: Int, month: Int, day: Int): Double {
        var y = year
        var m = month
        if (m <= 2) {
            y -= 1
            m += 12
        }
        val a = floor(y / 100.0)
        val b = 2 - a + floor(a / 4.0)
        return floor(365.25 * (y + 4716)) + floor(30.6001 * (m + 1)) + day + b - 1524.5
    }

    private fun sunPosition(jd: Double): Pair<Double, Double> {
        val d = jd - 2451545.0
        val g = fixAngle(357.529 + 0.98560028 * d)
        val q = fixAngle(280.459 + 0.98564736 * d)
        val l = fixAngle(q + 1.915 * sin(Math.toRadians(g)) + 0.020 * sin(Math.toRadians(2 * g)))
        val e = 23.439 - 0.00000036 * d
        val ra = Math.toDegrees(atan2(cos(Math.toRadians(e)) * sin(Math.toRadians(l)), cos(Math.toRadians(l)))) / 15.0
        val eqTime = q / 15.0 - fixHour(ra)
        val decl = Math.toDegrees(asin(sin(Math.toRadians(e)) * sin(Math.toRadians(l))))
        return decl to eqTime
    }

    private fun hourAngle(latitude: Double, declination: Double, angle: Double): Double {
        val latRad = Math.toRadians(latitude)
        val declRad = Math.toRadians(declination)
        val cosH = (-sin(Math.toRadians(angle)) - sin(latRad) * sin(declRad)) /
            (cos(latRad) * cos(declRad))
        val clamped = cosH.coerceIn(-1.0, 1.0)
        return Math.toDegrees(acos(clamped)) / 15.0
    }

    private fun toMinutes(hours: Double): Int {
        val fixed = fixHour(hours)
        return (fixed * 60.0).roundToInt() % 1440
    }

    private fun formatHours(hours: Double): String {
        val totalMin = toMinutes(hours)
        val h = (totalMin / 60) % 24
        val m = totalMin % 60
        return String.format(Locale.US, "%02d:%02d", h, m)
    }

    fun parseMinutes(hhmm: String): Int {
        val parts = hhmm.split(":")
        val h = parts.getOrNull(0)?.toIntOrNull() ?: 0
        val m = parts.getOrNull(1)?.toIntOrNull() ?: 0
        return h * 60 + m
    }

    private fun fixAngle(a: Double): Double = a - 360.0 * floor(a / 360.0)
    private fun fixHour(h: Double): Double = h - 24.0 * floor(h / 24.0)
}
