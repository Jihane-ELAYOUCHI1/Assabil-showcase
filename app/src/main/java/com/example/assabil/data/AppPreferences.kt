package com.assabil.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

// ─── DATASTORE PREFERENCES ────────────────────────────────────────
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "as_sabil_prefs")

@Singleton
class AppPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        val KEY_DARK_MODE      = booleanPreferencesKey("dark_mode")
        val KEY_LANGUAGE       = stringPreferencesKey("language")
        val KEY_ARABIC_SIZE    = floatPreferencesKey("arabic_font_size")
        val KEY_RECITER        = stringPreferencesKey("reciter")
        val KEY_LAST_SURAH     = intPreferencesKey("last_surah")
        val KEY_LAST_AYAH      = intPreferencesKey("last_ayah")
        val KEY_TRANSLATION_ON = booleanPreferencesKey("translation_on")
        val KEY_NOTIFS_ENABLED = booleanPreferencesKey("notifications_enabled")
    }

    val isDarkMode: Flow<Boolean> = context.dataStore.data.map { it[KEY_DARK_MODE] ?: false }
    val language: Flow<String>   = context.dataStore.data.map { it[KEY_LANGUAGE] ?: "fr" }
    val arabicFontSize: Flow<Float> = context.dataStore.data.map { it[KEY_ARABIC_SIZE] ?: 22f }
    val reciter: Flow<String>    = context.dataStore.data.map { it[KEY_RECITER] ?: "ar.alafasy" }

    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { it[KEY_DARK_MODE] = enabled }
    }
    suspend fun setLanguage(lang: String) {
        context.dataStore.edit { it[KEY_LANGUAGE] = lang }
    }
    suspend fun setArabicFontSize(size: Float) {
        context.dataStore.edit { it[KEY_ARABIC_SIZE] = size }
    }
    suspend fun setReciter(id: String) {
        context.dataStore.edit { it[KEY_RECITER] = id }
    }
}

// ─── QURAN AUDIO SERVICE ──────────────────────────────────────────
package com.assabil.data.service

import android.app.*
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.assabil.R

class QuranAudioService : Service() {

    private lateinit var player: ExoPlayer

    override fun onCreate() {
        super.onCreate()
        player = ExoPlayer.Builder(this).build()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val audioUrl = intent?.getStringExtra("audio_url") ?: return START_NOT_STICKY
        val surahName = intent.getStringExtra("surah_name") ?: "Al-Quran"

        when (intent.action) {
            ACTION_PLAY -> {
                player.setMediaItem(MediaItem.fromUri(audioUrl))
                player.prepare()
                player.play()
                startForeground(NOTIF_ID, buildNotification(surahName))
            }
            ACTION_PAUSE -> player.pause()
            ACTION_STOP  -> { player.stop(); stopForeground(STOP_FOREGROUND_REMOVE); stopSelf() }
        }
        return START_STICKY
    }

    override fun onDestroy() {
        player.release()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun buildNotification(surahName: String): Notification {
        val channelId = "quran_audio"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Quran Audio", NotificationManager.IMPORTANCE_LOW)
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("As-Sabil - Quran")
            .setContentText("Lecture: $surahName")
            .setSmallIcon(R.drawable.ic_notification)
            .setOngoing(true)
            .build()
    }

    companion object {
        const val ACTION_PLAY  = "ACTION_PLAY"
        const val ACTION_PAUSE = "ACTION_PAUSE"
        const val ACTION_STOP  = "ACTION_STOP"
        const val NOTIF_ID = 9001
    }
}
