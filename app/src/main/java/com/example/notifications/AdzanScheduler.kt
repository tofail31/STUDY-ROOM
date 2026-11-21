package com.example.notifications

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.example.data.calc.PrayerTimeCalculator
import com.example.data.model.PrayerSchedule
import java.util.Calendar

class AdzanAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val prayerName = intent.getStringExtra(EXTRA_PRAYER_NAME) ?: "Sholat"
        val prayerTime = intent.getStringExtra(EXTRA_PRAYER_TIME) ?: ""
        val cityName = intent.getStringExtra(EXTRA_CITY_NAME) ?: ""

        AdzanScheduler.ensureNotificationChannel(context)

        val openIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val contentPending = PendingIntent.getActivity(
            context,
            prayerName.hashCode(),
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, AdzanScheduler.CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle("$prayerName Prayer Time ($prayerTime)")
            .setContentText("It is time for $prayerName prayer • $cityName")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(contentPending)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(prayerName.hashCode(), notification)
    }

    companion object {
        const val EXTRA_PRAYER_NAME = "extra_prayer_name"
        const val EXTRA_PRAYER_TIME = "extra_prayer_time"
        const val EXTRA_CITY_NAME = "extra_city_name"
    }
}

object AdzanScheduler {
    const val CHANNEL_ID = "adzan_prayer_channel"

    fun ensureNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Adzan Prayer Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for five daily prayer times"
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    fun syncAlarms(
        context: Context,
        enabled: Boolean,
        enabledPrayers: Set<String>,
        schedule: PrayerSchedule,
        cityName: String
    ) {
        ensureNotificationChannel(context)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val prayers = listOf(
            "Fajr" to schedule.subuh,
            "Dhuhr" to schedule.dzuhur,
            "Asr" to schedule.ashar,
            "Maghrib" to schedule.maghrib,
            "Isha" to schedule.isya
        )

        val now = Calendar.getInstance()
        val nowMinutes = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE)

        prayers.forEachIndexed { index, (name, timeStr) ->
            val intent = Intent(context, AdzanAlarmReceiver::class.java).apply {
                putExtra(AdzanAlarmReceiver.EXTRA_PRAYER_NAME, name)
                putExtra(AdzanAlarmReceiver.EXTRA_PRAYER_TIME, timeStr)
                putExtra(AdzanAlarmReceiver.EXTRA_CITY_NAME, cityName)
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                1000 + index,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            alarmManager.cancel(pendingIntent)

            if (enabled && name in enabledPrayers) {
                val targetMin = PrayerTimeCalculator.parseMinutes(timeStr)
                val targetCal = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, targetMin / 60)
                    set(Calendar.MINUTE, targetMin % 60)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                    if (targetMin <= nowMinutes) {
                        add(Calendar.DAY_OF_YEAR, 1)
                    }
                }
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    targetCal.timeInMillis,
                    pendingIntent
                )
            }
        }
    }
}
