package com.example.data.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class AladhanResponse(
    val code: Int,
    val data: AladhanData?
)

@JsonClass(generateAdapter = true)
data class AladhanData(
    val timings: AladhanTimings
)

@JsonClass(generateAdapter = true)
data class AladhanTimings(
    @Json(name = "Fajr") val fajr: String,
    @Json(name = "Sunrise") val sunrise: String,
    @Json(name = "Dhuhr") val dhuhr: String,
    @Json(name = "Asr") val asr: String,
    @Json(name = "Maghrib") val maghrib: String,
    @Json(name = "Isha") val isha: String
)

@JsonClass(generateAdapter = true)
data class AlQuranEditionsResponse(
    val code: Int,
    val data: List<AlQuranSurahEdition>?
)

@JsonClass(generateAdapter = true)
data class AlQuranSurahEdition(
    val number: Int,
    val ayahs: List<AlQuranAyahDto>
)

@JsonClass(generateAdapter = true)
data class AlQuranAyahDto(
    val numberInSurah: Int,
    val text: String
)

interface AladhanApiService {
    @GET("v1/timings/{date}")
    suspend fun getTimings(
        @Path("date") date: String,
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("method") method: Int
    ): AladhanResponse
}

interface AlQuranApiService {
    @GET("v1/surah/{number}/editions/quran-uthmani,en.sahih,bn.bengali")
    suspend fun getSurahWithTranslations(
        @Path("number") surahNumber: Int
    ): AlQuranEditionsResponse
}

object NetworkModule {
    private val moshi: Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(8, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    val aladhanApi: AladhanApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.aladhan.com/")
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(AladhanApiService::class.java)
    }

    val alQuranApi: AlQuranApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.alquran.cloud/")
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(AlQuranApiService::class.java)
    }
}
