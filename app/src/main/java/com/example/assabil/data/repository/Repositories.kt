package com.assabil.data.repository

import com.assabil.data.local.dao.*
import com.assabil.data.local.entity.*
import com.assabil.data.remote.api.QuranApiService
import com.assabil.domain.model.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

// ─── QURAN REPOSITORY ─────────────────────────────────────────────
@Singleton
class QuranRepository @Inject constructor(
    private val api: QuranApiService,
    private val surahDao: SurahDao,
    private val ayahDao: AyahDao,
    private val bookmarkDao: BookmarkDao,
    private val progressDao: ReadingProgressDao
) {
    // ── Surahs ────────────────────────────────────────────────────
    fun getAllSurahs(): Flow<List<Surah>> =
        surahDao.getAllSurahs().map { list -> list.map { it.toDomain() } }

    fun searchSurahs(query: String): Flow<List<Surah>> =
        surahDao.searchSurahs(query).map { list -> list.map { it.toDomain() } }

    suspend fun getSurahById(id: Int): Surah? =
        surahDao.getSurahById(id)?.toDomain()

    suspend fun fetchAndCacheSurahs(): Result<Unit> = runCatching {
        val response = api.getAllSurahs()
        if (response.isSuccessful) {
            val surahs = response.body()?.data?.map { dto ->
                SurahEntity(
                    id = dto.number,
                    name = dto.englishName,
                    arabicName = dto.name,
                    englishName = dto.englishName,
                    frenchName = dto.englishNameTranslation,
                    versesCount = dto.numberOfAyahs,
                    revelationType = dto.revelationType,
                    pageStart = 0
                )
            } ?: emptyList()
            surahDao.insertAll(surahs)
        }
    }

    // ── Ayahs ─────────────────────────────────────────────────────
    fun getAyahsBySurah(surahId: Int): Flow<List<Ayah>> =
        ayahDao.getAyahsBySurah(surahId).map { list -> list.map { it.toDomain() } }

    suspend fun fetchAyahsForSurah(surahId: Int): Result<Unit> = runCatching {
        // Fetch Arabic + French translations in parallel
        val arabicResponse = api.getSurahArabic(surahId)
        val frenchResponse = api.getSurahTranslation(surahId, "fr.hamidullah")
        val englishResponse = api.getSurahTranslation(surahId, "en.sahih")

        if (arabicResponse.isSuccessful) {
            val arabicAyahs = arabicResponse.body()?.data?.ayahs ?: emptyList()
            val frenchAyahs = frenchResponse.body()?.data?.ayahs ?: emptyList()
            val englishAyahs = englishResponse.body()?.data?.ayahs ?: emptyList()

            val entities = arabicAyahs.mapIndexed { index, arabic ->
                AyahEntity(
                    globalNumber = arabic.number,
                    surahId = surahId,
                    numberInSurah = arabic.numberInSurah,
                    arabicText = arabic.text,
                    frenchTranslation = frenchAyahs.getOrNull(index)?.text ?: "",
                    englishTranslation = englishAyahs.getOrNull(index)?.text ?: "",
                    juzNumber = arabic.juz,
                    hizbNumber = arabic.hizb,
                    sajda = arabic.sajda
                )
            }
            ayahDao.insertAll(entities)
        }
    }

    // ── Bookmarks ─────────────────────────────────────────────────
    fun getAllBookmarks(): Flow<List<Bookmark>> =
        bookmarkDao.getAllBookmarks().map { list -> list.map { it.toDomain() } }

    suspend fun isBookmarked(surahId: Int, ayahNumber: Int): Boolean =
        bookmarkDao.isBookmarked(surahId, ayahNumber)

    suspend fun toggleBookmark(surahId: Int, ayahNumber: Int, surahName: String, ayahText: String) {
        if (bookmarkDao.isBookmarked(surahId, ayahNumber)) {
            bookmarkDao.deleteBookmark(surahId, ayahNumber)
        } else {
            bookmarkDao.insertBookmark(
                BookmarkEntity(
                    surahId = surahId,
                    ayahNumber = ayahNumber,
                    surahName = surahName,
                    ayahText = ayahText
                )
            )
        }
    }

    // ── Progress ──────────────────────────────────────────────────
    fun getReadingProgress(): Flow<ReadingProgress?> =
        progressDao.getProgress().map { it?.toDomain() }

    suspend fun saveProgress(surahId: Int, ayahNumber: Int) {
        progressDao.saveProgress(
            ReadingProgressEntity(lastSurahId = surahId, lastAyahNumber = ayahNumber)
        )
    }
}

// ─── ADKAR REPOSITORY ─────────────────────────────────────────────
@Singleton
class AdkarRepository @Inject constructor(
    private val adkarDao: AdkarDao
) {
    fun getAdkarBySection(sectionId: String): Flow<List<Adkar>> =
        adkarDao.getAdkarBySection(sectionId).map { list -> list.map { it.toDomain() } }

    fun getFavoriteAdkar(): Flow<List<Adkar>> =
        adkarDao.getFavoriteAdkar().map { list -> list.map { it.toDomain() } }

    suspend fun incrementCount(adkar: Adkar) {
        val updated = adkar.toEntity().copy(
            currentCount = (adkar.currentCount + 1).coerceAtMost(adkar.repeatCount)
        )
        adkarDao.updateAdkar(updated)
    }

    suspend fun resetSection(sectionId: String) {
        adkarDao.resetCountsForSection(sectionId)
    }

    suspend fun toggleFavorite(adkar: Adkar) {
        adkarDao.updateAdkar(
            adkar.toEntity().copy(isFavorite = !adkar.isFavorite)
        )
    }
}

// ─── HADITH REPOSITORY ────────────────────────────────────────────
@Singleton
class HadithRepository @Inject constructor(
    private val hadithDao: HadithDao
) {
    fun getNawawi40(): Flow<List<Hadith>> =
        hadithDao.getHadithsByCollection("nawawi40").map { list -> list.map { it.toDomain() } }

    fun getFavoriteHadiths(): Flow<List<Hadith>> =
        hadithDao.getFavoriteHadiths().map { list -> list.map { it.toDomain() } }

    suspend fun getDailyHadith(): Hadith? =
        hadithDao.getRandomHadith()?.toDomain()

    suspend fun toggleFavorite(hadith: Hadith) {
        hadithDao.updateHadith(hadith.toEntity().copy(isFavorite = !hadith.isFavorite))
    }
}

// ─── MAPPER EXTENSIONS ────────────────────────────────────────────
fun SurahEntity.toDomain() = Surah(id, name, arabicName, frenchName, englishName, versesCount, revelationType)
fun AyahEntity.toDomain() = Ayah(globalNumber, surahId, numberInSurah, arabicText, frenchTranslation, englishTranslation, juzNumber, sajda)
fun BookmarkEntity.toDomain() = Bookmark(id, surahId, ayahNumber, surahName, ayahText, savedAt)
fun ReadingProgressEntity.toDomain() = ReadingProgress(lastSurahId, lastAyahNumber, lastReadAt)
fun AdkarEntity.toDomain() = Adkar(id, sectionId, arabicText, transliteration, frenchTranslation, englishTranslation, repeatCount, currentCount, isFavorite, source)
fun HadithEntity.toDomain() = Hadith(id, collectionId, number, arabicText, frenchText, englishText, narrator, source, grade, isFavorite)
fun Adkar.toEntity() = AdkarEntity(id, sectionId, arabicText, transliteration, frenchTranslation, englishTranslation, repeatCount, currentCount, isFavorite, source)
fun Hadith.toEntity() = HadithEntity(id, collectionId, number, arabicText, frenchText, englishText, narrator, source, grade, isFavorite)
