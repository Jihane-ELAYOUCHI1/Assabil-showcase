package com.assabil.domain.model

data class Surah(
    val id: Int,
    val name: String,
    val arabicName: String,
    val frenchName: String,
    val englishName: String,
    val versesCount: Int,
    val revelationType: String
)

data class Ayah(
    val globalNumber: Int,
    val surahId: Int,
    val numberInSurah: Int,
    val arabicText: String,
    val frenchTranslation: String,
    val englishTranslation: String,
    val juzNumber: Int,
    val sajda: Boolean
)

data class Bookmark(
    val id: Long,
    val surahId: Int,
    val ayahNumber: Int,
    val surahName: String,
    val ayahText: String,
    val savedAt: Long
)

data class ReadingProgress(
    val lastSurahId: Int,
    val lastAyahNumber: Int,
    val lastReadAt: Long
)

data class Adkar(
    val id: Long,
    val sectionId: String,
    val arabicText: String,
    val transliteration: String,
    val frenchTranslation: String,
    val englishTranslation: String,
    val repeatCount: Int,
    val currentCount: Int,
    val isFavorite: Boolean,
    val source: String
)

data class Hadith(
    val id: Int,
    val collectionId: String,
    val number: Int,
    val arabicText: String,
    val frenchText: String,
    val englishText: String,
    val narrator: String,
    val source: String,
    val grade: String,
    val isFavorite: Boolean
)

data class KiblaInfo(
    val qiblaDirection: Double,  // degrees from North
    val userLatitude: Double,
    val userLongitude: Double,
    val distanceToKaaba: Double  // km
)

data class AudioState(
    val isPlaying: Boolean = false,
    val currentSurahId: Int = -1,
    val currentAyah: Int = 0,
    val reciter: String = "ar.alafasy",
    val progress: Float = 0f,
    val duration: Long = 0L
)
