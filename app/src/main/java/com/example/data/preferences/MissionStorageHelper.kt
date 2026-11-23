package com.example.data.preferences

import android.content.Context
import com.example.data.model.MissionAlarmMode
import com.example.data.model.MissionItem
import com.example.data.model.MissionPriority
import com.example.data.model.MissionTimeHelper
import com.example.data.model.PrepQuestionTag
import org.json.JSONArray
import org.json.JSONObject

/**
 * Synchronous JSON & SharedPreferences bridge so BroadcastReceivers, AlarmManager rollovers,
 * and Home Screen AppWidgets can always read/update the latest 4-hour missions instantaneously
 * even when the main Compose UI is closed.
 */
object MissionStorageHelper {
    private const val PREF_NAME = "mission_widget_prefs"
    private const val KEY_MISSIONS_JSON = "missions_json"
    private const val KEY_MISSIONS_INITIALIZED = "missions_initialized"
    private const val KEY_LIVE_NOTIF_ENABLED = "live_notif_enabled"
    private const val KEY_SCHEDULED_ALARM_IDS = "scheduled_alarm_ids"

    fun encodeMissions(list: List<MissionItem>): String {
        val arr = JSONArray()
        list.forEach { m ->
            val targetArr = JSONArray().apply { m.targetTags.forEach { put(it.name) } }
            val completedArr = JSONArray().apply { m.completedTags.forEach { put(it.name) } }
            arr.put(
                JSONObject().apply {
                    put("id", m.id)
                    put("dateKey", m.dateKey)
                    put("blockIndex", m.blockIndex)
                    put("title", m.title)
                    put("notes", m.notes)
                    put("category", m.category)
                    put("subjectId", m.subjectId)
                    put("subjectName", m.subjectName)
                    put("paper", m.paper)
                    put("chapterId", m.chapterId)
                    put("chapterTitle", m.chapterTitle)
                    put("targetTags", targetArr)
                    put("completedTags", completedArr)
                    put("priority", m.priority.name)
                    put("isCompleted", m.isCompleted)
                    put("alarmEnabled", m.alarmEnabled)
                    put("alarmMode", m.alarmMode.name)
                    put("customAlarmHour", m.customAlarmHour)
                    put("customAlarmMinute", m.customAlarmMinute)
                    put("useAlarmTone", m.useAlarmTone)
                    put("showInLiveNotification", m.showInLiveNotification)
                    put("updatedAtMillis", m.updatedAtMillis)
                }
            )
        }
        return arr.toString()
    }

    fun decodeMissions(raw: String?): List<MissionItem>? {
        if (raw == null) return null
        if (raw.isBlank()) return emptyList()
        return try {
            val arr = JSONArray(raw)
            val result = mutableListOf<MissionItem>()
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                val priName = obj.optString("priority", MissionPriority.HIGH.name)
                val modeName = obj.optString("alarmMode", MissionAlarmMode.AT_BLOCK_START.name)
                val blockIdx = obj.optInt("blockIndex", 0).coerceIn(0, 5)
                val slotStart = MissionTimeHelper.blockForIndex(blockIdx).startHour24

                val targetTags = decodeTagSet(obj.optJSONArray("targetTags"))
                val completedTags = decodeTagSet(obj.optJSONArray("completedTags"))

                result.add(
                    MissionItem(
                        id = obj.getString("id"),
                        dateKey = obj.getString("dateKey"),
                        blockIndex = blockIdx,
                        title = obj.getString("title"),
                        notes = obj.optString("notes", ""),
                        category = obj.optString("category", "Study"),
                        subjectId = obj.optString("subjectId", ""),
                        subjectName = obj.optString("subjectName", ""),
                        paper = obj.optInt("paper", 1),
                        chapterId = obj.optString("chapterId", ""),
                        chapterTitle = obj.optString("chapterTitle", ""),
                        targetTags = targetTags,
                        completedTags = completedTags,
                        priority = MissionPriority.entries.firstOrNull { it.name == priName }
                            ?: MissionPriority.HIGH,
                        isCompleted = obj.optBoolean("isCompleted", false),
                        alarmEnabled = obj.optBoolean("alarmEnabled", true),
                        alarmMode = MissionAlarmMode.entries.firstOrNull { it.name == modeName }
                            ?: MissionAlarmMode.AT_BLOCK_START,
                        customAlarmHour = obj.optInt("customAlarmHour", slotStart).coerceIn(0, 23),
                        customAlarmMinute = obj.optInt("customAlarmMinute", 0).coerceIn(0, 59),
                        useAlarmTone = obj.optBoolean("useAlarmTone", true),
                        showInLiveNotification = obj.optBoolean("showInLiveNotification", true),
                        updatedAtMillis = obj.optLong("updatedAtMillis", 0L)
                    )
                )
            }
            result
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun decodeTagSet(arr: JSONArray?): Set<PrepQuestionTag> {
        if (arr == null) return emptySet()
        val set = mutableSetOf<PrepQuestionTag>()
        for (i in 0 until arr.length()) {
            val name = arr.optString(i)
            PrepQuestionTag.entries.firstOrNull { it.name == name }?.let { set.add(it) }
        }
        return set
    }

    fun writeWidgetMirror(
        context: Context,
        missions: List<MissionItem>,
        liveNotifEnabled: Boolean
    ) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putString(KEY_MISSIONS_JSON, encodeMissions(missions))
            .putBoolean(KEY_MISSIONS_INITIALIZED, true)
            .putBoolean(KEY_LIVE_NOTIF_ENABLED, liveNotifEnabled)
            .apply()
    }

    fun readMissionsFromMirror(context: Context): List<MissionItem> {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val initialized = prefs.getBoolean(KEY_MISSIONS_INITIALIZED, false)
        val raw = prefs.getString(KEY_MISSIONS_JSON, null)
        return if (!initialized && raw == null) {
            MissionTimeHelper.createStarterMissions()
        } else {
            decodeMissions(raw) ?: emptyList()
        }
    }

    fun isLiveNotificationEnabledFromMirror(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_LIVE_NOTIF_ENABLED, false)
    }

    fun toggleMissionCompletedInMirror(context: Context, missionId: String): List<MissionItem> {
        val current = readMissionsFromMirror(context)
        val updated = current.map { m ->
            if (m.id == missionId) m.copy(isCompleted = !m.isCompleted, updatedAtMillis = System.currentTimeMillis())
            else m
        }
        val liveEnabled = isLiveNotificationEnabledFromMirror(context)
        writeWidgetMirror(context, updated, liveEnabled)
        return updated
    }

    fun getScheduledRequestCodes(context: Context): Set<Int> {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val raw = prefs.getStringSet(KEY_SCHEDULED_ALARM_IDS, emptySet()) ?: emptySet()
        return raw.mapNotNull { it.toIntOrNull() }.toSet()
    }

    fun setScheduledRequestCodes(context: Context, codes: Set<Int>) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putStringSet(KEY_SCHEDULED_ALARM_IDS, codes.map { it.toString() }.toSet())
            .apply()
    }
}
