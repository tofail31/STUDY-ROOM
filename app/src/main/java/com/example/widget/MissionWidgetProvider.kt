package com.example.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.MainActivity
import com.example.R
import com.example.data.model.MissionTimeHelper
import com.example.data.preferences.MissionStorageHelper
import com.example.notifications.MissionScheduler
import java.util.Calendar

class MissionWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        MissionScheduler.scheduleNext4HourRollover(context)
        appWidgetIds.forEach { appWidgetId ->
            updateSingleWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        when (intent.action) {
            ACTION_REFRESH_WIDGET -> {
                updateAllWidgets(context)
                MissionScheduler.scheduleNext4HourRollover(context)
            }
            ACTION_TOGGLE_ACTIVE_DONE -> {
                val missionId = intent.getStringExtra(EXTRA_WIDGET_MISSION_ID).orEmpty()
                if (missionId.isNotBlank()) {
                    val updated = MissionStorageHelper.toggleMissionCompletedInMirror(context, missionId)
                    if (MissionStorageHelper.isLiveNotificationEnabledFromMirror(context)) {
                        MissionScheduler.postOrUpdateLiveMissionNotification(context, updated)
                    }
                    updateAllWidgets(context)
                }
            }
        }
    }

    companion object {
        const val ACTION_REFRESH_WIDGET = "com.example.ACTION_MISSION_WIDGET_REFRESH"
        const val ACTION_TOGGLE_ACTIVE_DONE = "com.example.ACTION_MISSION_WIDGET_TOGGLE_DONE"
        const val EXTRA_WIDGET_MISSION_ID = "extra_widget_mission_id"

        fun updateAllWidgets(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            val component = ComponentName(context, MissionWidgetProvider::class.java)
            val ids = manager.getAppWidgetIds(component)
            ids.forEach { id ->
                updateSingleWidget(context, manager, id)
            }
        }

        private fun updateSingleWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val now = Calendar.getInstance()
            val todayKey = MissionTimeHelper.todayDateKey(now)
            val currentIdx = MissionTimeHelper.blockIndexForHour(now.get(Calendar.HOUR_OF_DAY))
            val nextIdx = (currentIdx + 1) % MissionTimeHelper.blocks.size
            val currentSlot = MissionTimeHelper.blockForIndex(currentIdx)
            val nextSlot = MissionTimeHelper.blockForIndex(nextIdx)

            val allMissions = MissionStorageHelper.readMissionsFromMirror(context)
            val todayMissions = allMissions.filter { it.dateKey == todayKey }
            val activeSlotMissions = todayMissions.filter { it.blockIndex == currentIdx }
            val activeMission = activeSlotMissions.firstOrNull { !it.isCompleted }
                ?: activeSlotMissions.firstOrNull()
            val nextMission = todayMissions.firstOrNull { it.blockIndex == nextIdx }

            val doneCount = todayMissions.count { it.isCompleted }
            val totalCount = todayMissions.size
            val remainingText = MissionTimeHelper.formatRemainingCountdown(
                MissionTimeHelper.remainingMillisInCurrentBlock(now),
                isBangla = false
            )

            val views = RemoteViews(context.packageName, R.layout.widget_mission_card)
            views.setTextViewText(R.id.widget_slot_badge, currentSlot.rangeShortEn)
            views.setTextViewText(R.id.widget_countdown, remainingText)
            views.setTextViewText(
                R.id.widget_mission_title,
                when {
                    activeMission != null -> {
                        val prefix = if (activeMission.isCompleted) "✓ " else ""
                        val setTag = if (activeSlotMissions.size > 1) " (+${activeSlotMissions.size - 1})" else ""
                        "$prefix${activeMission.title}$setTag"
                    }
                    else -> "No mission set for ${currentSlot.rangeShortEn}"
                }
            )
            val academicSub = activeMission?.let {
                MissionTimeHelper.formatAcademicSummary(it, isBangla = false)
            }.orEmpty()
            views.setTextViewText(
                R.id.widget_mission_subtitle,
                when {
                    activeMission != null && academicSub.isNotBlank() -> academicSub
                    activeMission != null && activeMission.notes.isNotBlank() ->
                        "${activeMission.category} • ${activeMission.notes}"
                    activeMission != null ->
                        "${activeMission.category} • ${currentSlot.phaseTitleEn}"
                    else -> "Tap to schedule your 4-hour focus sets"
                }
            )
            views.setTextViewText(
                R.id.widget_next_preview,
                if (nextMission != null) {
                    "Next (${nextSlot.rangeShortEn}): ${nextMission.title}"
                } else {
                    "Next (${nextSlot.rangeShortEn}): Open slot"
                }
            )
            views.setTextViewText(
                R.id.widget_progress_pill,
                if (totalCount > 0) "$doneCount/$totalCount done" else "Plan 24h"
            )
            views.setTextViewText(
                R.id.widget_done_btn,
                when {
                    activeMission == null -> "Plan"
                    activeMission.isCompleted -> "Done ✓"
                    else -> "Mark Done"
                }
            )

            // Open Mission screen when tapping body
            val openAppIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                putExtra(MissionScheduler.EXTRA_OPEN_ROUTE, MissionScheduler.ROUTE_MISSION_TARGET)
            }
            val openPending = PendingIntent.getActivity(
                context,
                appWidgetId,
                openAppIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_root, openPending)

            // Refresh button
            val refreshIntent = Intent(context, MissionWidgetProvider::class.java).apply {
                action = ACTION_REFRESH_WIDGET
            }
            val refreshPending = PendingIntent.getBroadcast(
                context,
                7000 + appWidgetId,
                refreshIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_refresh_btn, refreshPending)

            // Quick mark done / open button
            if (activeMission != null) {
                val toggleIntent = Intent(context, MissionWidgetProvider::class.java).apply {
                    action = ACTION_TOGGLE_ACTIVE_DONE
                    putExtra(EXTRA_WIDGET_MISSION_ID, activeMission.id)
                }
                val togglePending = PendingIntent.getBroadcast(
                    context,
                    8000 + appWidgetId,
                    toggleIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                views.setOnClickPendingIntent(R.id.widget_done_btn, togglePending)
            } else {
                views.setOnClickPendingIntent(R.id.widget_done_btn, openPending)
            }

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
