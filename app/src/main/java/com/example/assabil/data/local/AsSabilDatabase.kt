package com.assabil.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.assabil.data.local.dao.*
import com.assabil.data.local.entity.*

@Database(
    entities = [
        SurahEntity::class,
        AyahEntity::class,
        BookmarkEntity::class,
        ReadingProgressEntity::class,
        AdkarEntity::class,
        HadithEntity::class,
        TasbihSessionEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AsSabilDatabase : RoomDatabase() {
    abstract fun surahDao(): SurahDao
    abstract fun ayahDao(): AyahDao
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun readingProgressDao(): ReadingProgressDao
    abstract fun adkarDao(): AdkarDao
    abstract fun hadithDao(): HadithDao
    abstract fun tasbihDao(): TasbihDao
}
