package com.example.data.local

import com.example.data.calc.PrayerTimeCalculator
import com.example.data.model.AyahItem
import com.example.data.model.CalculationMethodOption
import com.example.data.model.PrayerSchedule
import com.example.data.model.SurahContent
import com.example.data.network.NetworkModule
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class IslamicRepository {

    private val surahCache = mutableMapOf<Int, SurahContent>()

    suspend fun fetchPrayerTimes(
        latitude: Double,
        longitude: Double,
        timeZoneHours: Double,
        method: CalculationMethodOption
    ): PrayerSchedule {
        val now = Calendar.getInstance()
        val offlineBase = PrayerTimeCalculator.calculateDaySchedule(
            latitude = latitude,
            longitude = longitude,
            timeZoneHours = timeZoneHours,
            method = method,
            now = now
        )
        return try {
            val dateStr = SimpleDateFormat("dd-MM-yyyy", Locale.US).format(now.time)
            val response = NetworkModule.aladhanApi.getTimings(
                date = dateStr,
                latitude = latitude,
                longitude = longitude,
                method = method.id
            )
            val timings = response.data?.timings
            if (response.code == 200 && timings != null) {
                PrayerTimeCalculator.enrichWithApiTimes(
                    base = offlineBase,
                    fajr = timings.fajr,
                    sunrise = timings.sunrise,
                    dhuhr = timings.dhuhr,
                    asr = timings.asr,
                    maghrib = timings.maghrib,
                    isha = timings.isha,
                    now = now
                )
            } else {
                offlineBase
            }
        } catch (_: Exception) {
            offlineBase
        }
    }

    suspend fun getSurahContent(surahNumber: Int): Result<SurahContent> {
        surahCache[surahNumber]?.let { return Result.success(it) }

        val summary = QuranCatalogData.allSurahs.firstOrNull { it.number == surahNumber }
            ?: return Result.failure(IllegalArgumentException("Surah tidak ditemukan"))

        return try {
            val response = NetworkModule.alQuranApi.getSurahWithTranslations(surahNumber)
            val editions = response.data
            if (response.code == 200 && editions != null && editions.size >= 2) {
                val arabicAyahs = editions[0].ayahs
                val enAyahs = editions[1].ayahs
                val bnAyahs = editions.getOrNull(2)?.ayahs
                val merged = arabicAyahs.mapIndexed { idx, ar ->
                    AyahItem(
                        numberInSurah = ar.numberInSurah,
                        arabic = ar.text,
                        translation = enAyahs.getOrNull(idx)?.text ?: "",
                        translationBn = bnAyahs?.getOrNull(idx)?.text ?: ""
                    )
                }
                val content = SurahContent(summary = summary, ayahs = merged, isFromNetwork = true)
                surahCache[surahNumber] = content
                Result.success(content)
            } else {
                fallbackOfflineSurah(summary)
            }
        } catch (_: Exception) {
            fallbackOfflineSurah(summary)
        }
    }

    private fun fallbackOfflineSurah(summary: com.example.data.model.SurahSummary): Result<SurahContent> {
        val offline = QuranCatalogData.offlineSurahAyahs[summary.number]
        return if (offline != null) {
            Result.success(SurahContent(summary = summary, ayahs = offline, isFromNetwork = false))
        } else {
            Result.failure(
                IllegalStateException(
                    "Surah ${summary.nameLatin} memerlukan koneksi internet untuk memuat seluruh ${summary.ayahCount} ayat."
                )
            )
        }
    }
}
