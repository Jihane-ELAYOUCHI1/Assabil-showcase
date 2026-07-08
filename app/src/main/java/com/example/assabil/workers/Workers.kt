package com.assabil.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.assabil.R
import com.assabil.data.repository.QuranRepository
import com.assabil.data.repository.HadithRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

// ─── DAILY VERSE WORKER ───────────────────────────────────────────
@HiltWorker
class DailyVerseWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val hadithRepo: HadithRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val hadith = hadithRepo.getDailyHadith() ?: return Result.retry()
        showNotification(
            context = applicationContext,
            channelId = CHANNEL_DAILY,
            title = "Hadith du Jour",
            body = hadith.frenchText.take(120) + "…",
            notifId = 1001
        )
        return Result.success()
    }

    companion object {
        const val CHANNEL_DAILY = "channel_daily"
        const val WORK_NAME = "daily_verse_work"

        fun schedule(context: Context) {
            val request = PeriodicWorkRequestBuilder<DailyVerseWorker>(24, TimeUnit.HOURS)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }
    }
}

// ─── ADKAR REMINDER WORKER ────────────────────────────────────────
@HiltWorker
class AdkarMorningWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        showNotification(
            context = applicationContext,
            channelId = CHANNEL_ADKAR,
            title = "أذكار الصباح",
            body = "N'oubliez pas vos Adkar du matin! 🌅",
            notifId = 2001
        )
        return Result.success()
    }

    companion object {
        const val CHANNEL_ADKAR = "channel_adkar"
        const val WORK_NAME_MORNING = "adkar_morning_work"

        fun scheduleMorning(context: Context) {
            val request = PeriodicWorkRequestBuilder<AdkarMorningWorker>(24, TimeUnit.HOURS)
                .build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME_MORNING,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }
    }
}

@HiltWorker
class AdkarEveningWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        showNotification(
            context = applicationContext,
            channelId = AdkarMorningWorker.CHANNEL_ADKAR,
            title = "أذكار المساء",
            body = "Il est temps de faire vos Adkar du soir! 🌙",
            notifId = 2002
        )
        return Result.success()
    }
}

// ─── BOOT RECEIVER ────────────────────────────────────────────────
class BootReceiver : android.content.BroadcastReceiver() {
    override fun onReceive(context: Context, intent: android.content.Intent) {
        if (intent.action == android.content.Intent.ACTION_BOOT_COMPLETED) {
            DailyVerseWorker.schedule(context)
            AdkarMorningWorker.scheduleMorning(context)
        }
    }
}

// ─── NOTIFICATION HELPER ──────────────────────────────────────────
fun showNotification(
    context: Context,
    channelId: String,
    title: String,
    body: String,
    notifId: Int
) {
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            channelId,
            "As-Sabil Notifications",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Rappels islamiques quotidiens"
        }
        notificationManager.createNotificationChannel(channel)
    }

    val notification = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.ic_notification)
        .setContentTitle(title)
        .setContentText(body)
        .setStyle(NotificationCompat.BigTextStyle().bigText(body))
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setAutoCancel(true)
        .build()

    notificationManager.notify(notifId, notification)
}
