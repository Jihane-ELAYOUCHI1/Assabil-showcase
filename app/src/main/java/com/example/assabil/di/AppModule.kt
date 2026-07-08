package com.assabil.di

import android.content.Context
import androidx.room.Room
import com.assabil.data.local.AsSabilDatabase
import com.assabil.data.local.dao.*
import com.assabil.data.remote.api.QuranApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private const val QURAN_API_BASE_URL = "https://api.alquran.cloud/v1/"

    // ── Room ──────────────────────────────────────────────────────
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AsSabilDatabase =
        Room.databaseBuilder(context, AsSabilDatabase::class.java, "as_sabil_db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides fun provideSurahDao(db: AsSabilDatabase): SurahDao = db.surahDao()
    @Provides fun provideAyahDao(db: AsSabilDatabase): AyahDao = db.ayahDao()
    @Provides fun provideBookmarkDao(db: AsSabilDatabase): BookmarkDao = db.bookmarkDao()
    @Provides fun provideProgressDao(db: AsSabilDatabase): ReadingProgressDao = db.readingProgressDao()
    @Provides fun provideAdkarDao(db: AsSabilDatabase): AdkarDao = db.adkarDao()
    @Provides fun provideHadithDao(db: AsSabilDatabase): HadithDao = db.hadithDao()
    @Provides fun provideTasbihDao(db: AsSabilDatabase): TasbihDao = db.tasbihDao()

    // ── OkHttp ────────────────────────────────────────────────────
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(
            HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
        )
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    // ── Retrofit ──────────────────────────────────────────────────
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(QURAN_API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideQuranApiService(retrofit: Retrofit): QuranApiService =
        retrofit.create(QuranApiService::class.java)
}
