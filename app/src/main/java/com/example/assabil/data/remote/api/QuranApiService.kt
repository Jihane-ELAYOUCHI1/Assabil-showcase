package com.assabil.data.remote.api

import com.assabil.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

// Base URL: https://api.alquran.cloud/v1/

interface QuranApiService {

    // Get all surahs metadata
    @GET("surah")
    suspend fun getAllSurahs(): Response<QuranListResponse<SurahDto>>

    // Get full surah in Arabic
    @GET("surah/{surahId}")
    suspend fun getSurahArabic(
        @Path("surahId") surahId: Int
    ): Response<QuranSurahResponse>

    // Get surah with specific translation
    // editions: fr.hamidullah (French), en.sahih (English)
    @GET("surah/{surahId}/{edition}")
    suspend fun getSurahTranslation(
        @Path("surahId") surahId: Int,
        @Path("edition") edition: String
    ): Response<QuranSurahResponse>

    // Get audio URL for a surah by reciter
    // editions: ar.alafasy, ar.abdurrahmaansudais, etc.
    @GET("surah/{surahId}/{edition}")
    suspend fun getSurahAudio(
        @Path("surahId") surahId: Int,
        @Path("edition") edition: String = "ar.alafasy"
    ): Response<QuranSurahResponse>

    // Search Quran
    @GET("search/{keyword}/{surahs}/{language}")
    suspend fun searchQuran(
        @Path("keyword") keyword: String,
        @Path("surahs") surahs: String = "all",
        @Path("language") language: String = "fr"
    ): Response<QuranSearchResponse>

    // Get a specific ayah
    @GET("ayah/{reference}/{edition}")
    suspend fun getAyah(
        @Path("reference") reference: String, // "2:255"
        @Path("edition") edition: String = "quran-uthmani"
    ): Response<QuranAyahResponse>

    // Get list of audio editions (reciters)
    @GET("edition/format/audio")
    suspend fun getAudioEditions(): Response<QuranListResponse<EditionDto>>
}

// ─── DTOs ─────────────────────────────────────────────────────────
package com.assabil.data.remote.dto

import com.google.gson.annotations.SerializedName

data class QuranListResponse<T>(
    val code: Int,
    val status: String,
    val data: List<T>
)

data class QuranSurahResponse(
    val code: Int,
    val status: String,
    val data: SurahDataDto
)

data class QuranAyahResponse(
    val code: Int,
    val status: String,
    val data: AyahDto
)

data class QuranSearchResponse(
    val code: Int,
    val status: String,
    val data: SearchResultDto
)

data class SurahDto(
    val number: Int,
    val name: String,
    @SerializedName("englishName") val englishName: String,
    @SerializedName("englishNameTranslation") val englishNameTranslation: String,
    @SerializedName("numberOfAyahs") val numberOfAyahs: Int,
    @SerializedName("revelationType") val revelationType: String
)

data class SurahDataDto(
    val number: Int,
    val name: String,
    @SerializedName("englishName") val englishName: String,
    @SerializedName("numberOfAyahs") val numberOfAyahs: Int,
    @SerializedName("revelationType") val revelationType: String,
    val ayahs: List<AyahDto>,
    val edition: EditionDto? = null
)

data class AyahDto(
    val number: Int,
    @SerializedName("numberInSurah") val numberInSurah: Int,
    val text: String,
    val juz: Int = 0,
    val hizb: Int = 0,
    val sajda: Boolean = false,
    val audio: String? = null,
    val audioSecondary: List<String>? = null
)

data class EditionDto(
    val identifier: String,
    val language: String,
    val name: String,
    @SerializedName("englishName") val englishName: String,
    val format: String,
    val type: String
)

data class SearchResultDto(
    val count: Int,
    val matches: List<AyahSearchDto>
)

data class AyahSearchDto(
    val number: Int,
    val text: String,
    val surah: SurahDto,
    @SerializedName("numberInSurah") val numberInSurah: Int
)
