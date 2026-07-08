package com.assabil.data.local.dao

import androidx.room.*
import com.assabil.data.local.entity.*
import kotlinx.coroutines.flow.Flow

// ─── SURAH DAO ────────────────────────────────────────────────────
@Dao
interface SurahDao {
    @Query("SELECT * FROM surahs ORDER BY id ASC")
    fun getAllSurahs(): Flow<List<SurahEntity>>

    @Query("SELECT * FROM surahs WHERE id = :id")
    suspend fun getSurahById(id: Int): SurahEntity?

    @Query("SELECT * FROM surahs WHERE name LIKE '%' || :query || '%' OR arabicName LIKE '%' || :query || '%'")
    fun searchSurahs(query: String): Flow<List<SurahEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(surahs: List<SurahEntity>)
}

// ─── AYAH DAO ─────────────────────────────────────────────────────
@Dao
interface AyahDao {
    @Query("SELECT * FROM ayahs WHERE surahId = :surahId ORDER BY numberInSurah ASC")
    fun getAyahsBySurah(surahId: Int): Flow<List<AyahEntity>>

    @Query("SELECT * FROM ayahs WHERE globalNumber = :globalNumber")
    suspend fun getAyahByGlobalNumber(globalNumber: Int): AyahEntity?

    @Query("SELECT * FROM ayahs WHERE arabicText LIKE '%' || :query || '%' OR frenchTranslation LIKE '%' || :query || '%'")
    fun searchAyahs(query: String): Flow<List<AyahEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(ayahs: List<AyahEntity>)
}

// ─── BOOKMARK DAO ─────────────────────────────────────────────────
@Dao
interface BookmarkDao {
    @Query("SELECT * FROM bookmarks ORDER BY savedAt DESC")
    fun getAllBookmarks(): Flow<List<BookmarkEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE surahId = :surahId AND ayahNumber = :ayahNumber)")
    suspend fun isBookmarked(surahId: Int, ayahNumber: Int): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: BookmarkEntity)

    @Query("DELETE FROM bookmarks WHERE surahId = :surahId AND ayahNumber = :ayahNumber")
    suspend fun deleteBookmark(surahId: Int, ayahNumber: Int)

    @Delete
    suspend fun delete(bookmark: BookmarkEntity)
}

// ─── READING PROGRESS DAO ─────────────────────────────────────────
@Dao
interface ReadingProgressDao {
    @Query("SELECT * FROM reading_progress WHERE id = 1")
    fun getProgress(): Flow<ReadingProgressEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProgress(progress: ReadingProgressEntity)
}

// ─── ADKAR DAO ────────────────────────────────────────────────────
@Dao
interface AdkarDao {
    @Query("SELECT * FROM adkar WHERE sectionId = :sectionId ORDER BY id ASC")
    fun getAdkarBySection(sectionId: String): Flow<List<AdkarEntity>>

    @Query("SELECT * FROM adkar WHERE isFavorite = 1")
    fun getFavoriteAdkar(): Flow<List<AdkarEntity>>

    @Update
    suspend fun updateAdkar(adkar: AdkarEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(adkar: List<AdkarEntity>)

    @Query("UPDATE adkar SET currentCount = 0 WHERE sectionId = :sectionId")
    suspend fun resetCountsForSection(sectionId: String)
}

// ─── HADITH DAO ───────────────────────────────────────────────────
@Dao
interface HadithDao {
    @Query("SELECT * FROM hadiths WHERE collectionId = :collectionId ORDER BY number ASC")
    fun getHadithsByCollection(collectionId: String): Flow<List<HadithEntity>>

    @Query("SELECT * FROM hadiths WHERE isFavorite = 1")
    fun getFavoriteHadiths(): Flow<List<HadithEntity>>

    @Query("SELECT * FROM hadiths ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomHadith(): HadithEntity?

    @Update
    suspend fun updateHadith(hadith: HadithEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(hadiths: List<HadithEntity>)
}

// ─── TASBIH DAO ───────────────────────────────────────────────────
@Dao
interface TasbihDao {
    @Query("SELECT * FROM tasbih_sessions ORDER BY createdAt DESC")
    fun getAllSessions(): Flow<List<TasbihSessionEntity>>

    @Insert
    suspend fun insertSession(session: TasbihSessionEntity): Long

    @Update
    suspend fun updateSession(session: TasbihSessionEntity)
}
