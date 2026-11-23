package com.example.notifications

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.example.data.model.MissionItem
import com.example.data.model.MissionTimeHelper
import com.example.data.preferences.MissionStorageHelper
import com.example.widget.MissionWidgetProvider
import java.util.Calendar

class MissionAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ACTION_MISSION_ALARM -> {
                val title = intent.getStringExtra(EXTRA_TITLE) ?: "4-Hour Mission Reminder"
                val blockRange = intent.getStringExtra(EXTRA_BLOCK_RANGE) ?: ""
                val category = intent.getStringExtra(EXTRA_CATEGORY) ?: "Study"
                val missionId = intent.getStringExtra(EXTRA_MISSION_ID) ?: title
                val useAlarmTone = intent.getBooleanExtra(EXTRA_USE_ALARM_TONE, true)
                MissionScheduler.postMissionAlarmNotification(
                    context = context,
                    missionId = missionId,
                    title = title,
                    blockRange = blockRange,
                    category = category,
                    useAlarmTone = useAlarmTone
                )
                MissionWidgetProvider.updateAllWidgets(context)
            }

            ACTION_4H_ROLLOVER -> {
                val missions = MissionStorageHelper.readMissionsFromMirror(context)
                val liveEnabled = MissionStorageHelper.isLiveNotificationEnabledFromMirror(context)
                if (liveEnabled) {
                    MissionScheduler.postOrUpdateLiveMissionNotification(context, missions)
                }
                MissionWidgetProvider.updateAllWidgets(context)
                MissionScheduler.scheduleNext4HourRollover(context)
            }

            ACTION_COMPLETE_FROM_NOTIF -> {
                val missionId = intent.getStringExtra(EXTRA_MISSION_ID).orEmpty()
                if (missionId.isNotBlank()) {
                    val updated = MissionStorageHelper.toggleMissionCompletedInMirror(context, missionId)
                    if (MissionStorageHelper.isLiveNotificationEnabledFromMirror(context)) {
                        MissionScheduler.postOrUpdateLiveMissionNotification(context, updated)
                    }
                    MissionWidgetProvider.updateAllWidgets(context)
                }
            }
        }
    }

    companion object {
        const val ACTION_MISSION_ALARM = "com.example.ACTION_MISSION_ALARM"
        const val ACTION_4H_ROLLOVER = "com.example.ACTION_MISSION_4H_ROLLOVER"
        const val ACTION_COMPLETE_FROM_NOTIF = "com.example.ACTION_COMPLETE_MISSION_NOTIF"

        const val EXTRA_MISSION_ID = "extra_mission_id"
        const val EXTRA_TITLE = "extra_mission_title"
        const val EXTRA_BLOCK_RANGE = "extra_mission_block_range"
        const val EXTRA_CATEGORY = "extra_mission_category"
        const val EXTRA_USE_ALARM_TONE = "extra_mission_alarm_tone"
    }
}

object MissionScheduler {
    const val CHANNEL_ALARM_ID = "mission_alarm_channel"
    const val CHANNEL_SOFT_ID = "mission_soft_channel"
    const val CHANNEL_LIVE_ID = "mission_live_channel"
    const val LIVE_NOTIFICATION_ID = 4401
    private const val ROLLOVER_REQUEST_CODE = 4400
    const val EXTRA_OPEN_ROUTE = "open_route"
    const val ROUTE_MISSION_TARGET = "mission"

    fun ensureChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val alarmAttrs = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            val alarmChannel = NotificationChannel(
                CHANNEL_ALARM_ID,
                "Mission Alarms (4h Blocks)",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "High-priority alarm notifications for your 4-hour mission blocks"
                enableVibration(true)
                vibrationPattern = longArrayOf(0L, 350L, 200L, 350L)
                setSound(alarmUri, alarmAttrs)
            }

            val softChannel = NotificationChannel(
                CHANNEL_SOFT_ID,
                "Mission Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Gentle mission notifications for 4-hour study blocks"
                enableVibration(true)
            }

            val liveChannel = NotificationChannel(
                CHANNEL_LIVE_ID,
                "Live 4-Hour Mission Banner",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Real-time active 4-hour mission status that auto-switches every 4 hours"
                setShowBadge(false)
            }

            manager.createNotificationChannel(alarmChannel)
            manager.createNotificationChannel(softChannel)
            manager.createNotificationChannel(liveChannel)
        }
    }

    fun syncAll(
        context: Context,
        missions: List<MissionItem>,
        liveNotifEnabled: Boolean
    ) {
        ensureChannels(context)
        MissionStorageHelper.writeWidgetMirror(context, missions, liveNotifEnabled)
        syncMissionAlarms(context, missions)
        scheduleNext4HourRollover(context)
        if (liveNotifEnabled) {
            postOrUpdateLiveMissionNotification(context, missions)
        } else {
            cancelLiveMissionNotification(context)
        }
        MissionWidgetProvider.updateAllWidgets(context)
    }

    fun scheduleNext4HourRollover(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, MissionAlarmReceiver::class.java).apply {
            action = MissionAlarmReceiver.ACTION_4H_ROLLOVER
        }
        val pi = PendingIntent.getBroadcast(
            context,
            ROLLOVER_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pi)
        val triggerAt = MissionTimeHelper.next4HourBoundaryMillis()
        scheduleWakeupCompat(alarmManager, triggerAt, pi)
    }

    private fun syncMissionAlarms(context: Context, missions: List<MissionItem>) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Cancel previously scheduled mission request codes
        val oldCodes = MissionStorageHelper.getScheduledRequestCodes(context)
        oldCodes.forEach { code ->
            val cancelIntent = Intent(context, MissionAlarmReceiver::class.java).apply {
                action = MissionAlarmReceiver.ACTION_MISSION_ALARM
            }
            val pi = PendingIntent.getBroadcast(
                context,
                code,
                cancelIntent,
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )
            if (pi != null) {
                alarmManager.cancel(pi)
                pi.cancel()
            }
        }

        val nowMillis = System.currentTimeMillis()
        val sevenDaysLater = nowMillis + 7L * 24L * 3600L * 1000L
        val activeCodes = mutableSetOf<Int>()

        missions.forEach { mission ->
            if (!mission.alarmEnabled || mission.isCompleted) return@forEach
            val triggerAt = MissionTimeHelper.resolveAlarmTriggerTimeMillis(mission)
            if (triggerAt <= nowMillis + 2_000L || triggerAt > sevenDaysLater) return@forEach

            val slot = MissionTimeHelper.blockForIndex(mission.blockIndex)
            val reqCode = 50_000 + (mission.id.hashCode() and 0x7FFF)
            activeCodes.add(reqCode)

            val intent = Intent(context, MissionAlarmReceiver::class.java).apply {
                action = MissionAlarmReceiver.ACTION_MISSION_ALARM
                putExtra(MissionAlarmReceiver.EXTRA_MISSION_ID, mission.id)
                putExtra(MissionAlarmReceiver.EXTRA_TITLE, mission.title)
                putExtra(MissionAlarmReceiver.EXTRA_BLOCK_RANGE, slot.rangeShortEn)
                putExtra(MissionAlarmReceiver.EXTRA_CATEGORY, mission.category)
                putExtra(MissionAlarmReceiver.EXTRA_USE_ALARM_TONE, mission.useAlarmTone)
            }
            val pi = PendingIntent.getBroadcast(
                context,
                reqCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            scheduleWakeupCompat(alarmManager, triggerAt, pi)
        }

        MissionStorageHelper.setScheduledRequestCodes(context, activeCodes)
    }

    private fun scheduleWakeupCompat(
        alarmManager: AlarmManager,
        triggerAtMillis: Long,
        pendingIntent: PendingIntent
    ) {
        try {
            val canExact = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                alarmManager.canScheduleExactAlarms()
            } else {
                true
            }
            if (canExact) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
            } else {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
            }
        } catch (_: SecurityException) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
        }
    }

    fun postMissionAlarmNotification(
        context: Context,
        missionId: String,
        title: String,
        blockRange: String,
        category: String,
        useAlarmTone: Boolean
    ) {
        ensureChannels(context)
        val openPending = buildOpenMissionPendingIntent(context, missionId.hashCode())

        val doneIntent = Intent(context, MissionAlarmReceiver::class.java).apply {
            action = MissionAlarmReceiver.ACTION_COMPLETE_FROM_NOTIF
            putExtra(MissionAlarmReceiver.EXTRA_MISSION_ID, missionId)
        }
        val donePending = PendingIntent.getBroadcast(
            context,
            ("done_$missionId").hashCode(),
            doneIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = if (useAlarmTone) CHANNEL_ALARM_ID else CHANNEL_SOFT_ID
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle("Mission Alarm • $blockRange")
            .setContentText("$title ($category)")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("$title\nWindow: $blockRange • Category: $category")
            )
            .setPriority(if (useAlarmTone) NotificationCompat.PRIORITY_HIGH else NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(if (useAlarmTone) NotificationCompat.CATEGORY_ALARM else NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setContentIntent(openPending)
            .addAction(0, "Mark Done", donePending)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(60_000 + (missionId.hashCode() and 0x7FFF), notification)
    }

    fun postOrUpdateLiveMissionNotification(
        context: Context,
        missions: List<MissionItem>,
        now: Calendar = Calendar.getInstance()
    ) {
        ensureChannels(context)
        val todayKey = MissionTimeHelper.todayDateKey(now)
        val currentIdx = MissionTimeHelper.blockIndexForHour(now.get(Calendar.HOUR_OF_DAY))
        val nextIdx = (currentIdx + 1) % MissionTimeHelper.blocks.size
        val currentSlot = MissionTimeHelper.blockForIndex(currentIdx)
        val nextSlot = MissionTimeHelper.blockForIndex(nextIdx)

        val todayMissions = missions.filter { it.dateKey == todayKey }
        val activeSlotMissions = todayMissions.filter { it.blockIndex == currentIdx }
        val activeMission = activeSlotMissions.firstOrNull { !it.isCompleted }
            ?: activeSlotMissions.firstOrNull()
        val nextSlotMissions = todayMissions.filter { it.blockIndex == nextIdx }
        val nextMission = nextSlotMissions.firstOrNull()

        val remainingText = MissionTimeHelper.formatRemainingCountdown(
            MissionTimeHelper.remainingMillisInCurrentBlock(now),
            isBangla = false
        )

        val title = if (activeMission != null) {
            val statusMark = if (activeMission.isCompleted) "✓ " else ""
            val setBadge = if (activeSlotMissions.size > 1) " (${activeSlotMissions.size} sets)" else ""
            "$statusMark${activeMission.title}$setBadge • ${currentSlot.rangeShortEn}"
        } else {
            "No mission set • ${currentSlot.rangeShortEn}"
        }

        val activeAcademic = activeMission?.let {
            MissionTimeHelper.formatAcademicSummary(it, isBangla = false)
        }.orEmpty()

        val nextPreview = if (nextMission != null) {
            "Next (${nextSlot.rangeShortEn}): ${nextMission.title}"
        } else {
            "Next (${nextSlot.rangeShortEn}): Open slot"
        }

        val summaryLine = buildString {
            if (activeAcademic.isNotBlank()) {
                append(activeAcademic)
                append(" • ")
            }
            append(remainingText)
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_LIVE_ID)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle(title)
            .setContentText(summaryLine)
            .setStyle(
                NotificationCompat.BigTextStyle().bigText(
                    buildString {
                        if (activeAcademic.isNotBlank()) {
                            append(activeAcademic)
                            append("\n")
                        }
                        if (activeMission != null && activeMission.notes.isNotBlank()) {
                            append(activeMission.notes)
                            append("\n")
                        }
                        append("${currentSlot.phaseTitleEn} ($remainingText)\n$nextPreview")
                    }
                )
            )
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(buildOpenMissionPendingIntent(context, LIVE_NOTIFICATION_ID))

        if (activeMission != null && !activeMission.isCompleted) {
            val doneIntent = Intent(context, MissionAlarmReceiver::class.java).apply {
                action = MissionAlarmReceiver.ACTION_COMPLETE_FROM_NOTIF
                putExtra(MissionAlarmReceiver.EXTRA_MISSION_ID, activeMission.id)
            }
            val donePending = PendingIntent.getBroadcast(
                context,
                ("live_done_${activeMission.id}").hashCode(),
                doneIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            builder.addAction(0, "Complete Mission", donePending)
        }

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(LIVE_NOTIFICATION_ID, builder.build())
    }

    fun cancelLiveMissionNotification(context: Context) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.cancel(LIVE_NOTIFICATION_ID)
    }

    private fun buildOpenMissionPendingIntent(context: Context, reqCode: Int): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra(EXTRA_OPEN_ROUTE, ROUTE_MISSION_TARGET)
        }
        return PendingIntent.getActivity(
            context,
            reqCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
