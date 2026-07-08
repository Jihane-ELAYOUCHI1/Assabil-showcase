package com.assabil.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// ─── SURAH ENTITY ─────────────────────────────────────────────────
@Entity(tableName = "surahs")
data class SurahEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val arabicName: String,
    val englishName: String,
    val frenchName: String,
    val versesCount: Int,
    val revelationType: String,  // "Meccan" | "Medinan"
    val pageStart: Int
)

// ─── AYAH ENTITY ──────────────────────────────────────────────────
@Entity(tableName = "ayahs")
data class AyahEntity(
    @PrimaryKey val globalNumber: Int,
    val surahId: Int,
    val numberInSurah: Int,
    val arabicText: String,
    val frenchTranslation: String,
    val englishTranslation: String,
    val tafsir: String = "",
    val juzNumber: Int,
    val hizbNumber: Int,
    val sajda: Boolean = false
)

// ─── BOOKMARK ENTITY ──────────────────────────────────────────────
@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val surahId: Int,
    val ayahNumber: Int,
    val surahName: String,
    val ayahText: String,
    val savedAt: Long = System.currentTimeMillis(),
    val note: String = ""
)

// ─── READING PROGRESS ENTITY ──────────────────────────────────────
@Entity(tableName = "reading_progress")
data class ReadingProgressEntity(
    @PrimaryKey val id: Int = 1,
    val lastSurahId: Int,
    val lastAyahNumber: Int,
    val lastReadAt: Long = System.currentTimeMillis()
)

// ─── ADKAR ENTITY ─────────────────────────────────────────────────
@Entity(tableName = "adkar")
data class AdkarEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sectionId: String,      // "sabah" | "masaa" | "prayer" | "sleep"
    val arabicText: String,
    val transliteration: String,
    val frenchTranslation: String,
    val englishTranslation: String,
    val repeatCount: Int,
    val currentCount: Int = 0,
    val isFavorite: Boolean = false,
    val source: String = ""
)

// ─── HADITH ENTITY ────────────────────────────────────────────────
@Entity(tableName = "hadiths")
data class HadithEntity(
    @PrimaryKey val id: Int,
    val collectionId: String,   // "nawawi40" | "daily"
    val number: Int,
    val arabicText: String,
    val frenchText: String,
    val englishText: String,
    val narrator: String,
    val source: String,
    val grade: String = "Sahih",
    val isFavorite: Boolean = false
)

// ─── TASBIH SESSION ENTITY ────────────────────────────────────────
@Entity(tableName = "tasbih_sessions")
data class TasbihSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dhikrText: String,
    val targetCount: Int,
    val currentCount: Int,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null
)
