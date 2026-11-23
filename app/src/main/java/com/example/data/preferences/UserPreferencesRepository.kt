package com.example.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.data.model.CalculationMethodOption
import com.example.data.model.CustomPublishedExam
import com.example.data.model.ExamPaperStyle
import com.example.data.model.ExamQuestion
import com.example.data.model.MissionItem
import com.example.data.model.MissionTimeHelper
import com.example.data.model.PrepQuestionTag
import com.example.data.model.StudyDashboardDefaults
import com.example.data.model.StudySubjectItem
import com.example.data.model.StudyTodoItem
import com.example.data.model.TrackerChapter
import com.example.data.model.TrackerDefaults
import com.example.data.model.TrackerSubject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "one_day_one_prefs")

data class UserPrefsState(
    val userName: String = "Brother / Sister",
    val cityId: String = "dhaka",
    val cityName: String = "Dhaka, Bangladesh",
    val latitude: Double = 23.8103,
    val longitude: Double = 90.4125,
    val timeZoneHours: Double = 6.0,
    val calcMethod: CalculationMethodOption = CalculationMethodOption.BANGLADESH,
    val adzanNotificationsEnabled: Boolean = false,
    val enabledPrayersForAdzan: Set<String> = setOf("Fajr", "Dhuhr", "Asr", "Maghrib", "Isha"),
    val completedPrayersDate: String = "",
    val completedPrayersToday: Set<String> = emptySet(),
    val bookmarkedAyahKeys: Set<String> = setOf("55:13"),
    val favoriteDuaIds: Set<String> = setOf("dua_pagi_1"),
    val dzikirPresetId: String = "subhanallah",
    val dzikirCount: Int = 0,
    val dzikirTarget: Int = 33,
    val dzikirLifetimeTotal: Long = 0L,
    val quranFontSizeSp: Int = 24,
    val quranTranslationLang: String = "en", // "en" or "bn"
    val appLanguage: String = "en", // "en" or "bn"
    val joinedGroupId: String = "grp_dhaka_quran",
    val joinedGroupIds: Set<String> = setOf("grp_dhaka_quran", "grp_hsc_science"),
    val studySubject: String = "Physics",
    val studyTodayDate: String = "",
    val studyTodaySeconds: Long = 128L,
    val studyBatchTag: String = "HSC-26",
    val dailyFocusGoalHours: Int = 6,
    val studyStreakDays: Int = 0,
    val focusSessionModeId: String = "STOPWATCH",
    val studySubjects: List<StudySubjectItem> = StudyDashboardDefaults.defaultSubjects,
    val studyTodos: List<StudyTodoItem> = StudyDashboardDefaults.defaultTodos,
    val trackerSubjects: List<TrackerSubject> = TrackerDefaults.defaultSubjects,
    val examBestScores: Map<String, Int> = emptyMap(),
    val wrongExamQuestionIds: Set<String> = emptySet(),
    val totalExamsTaken: Int = 0,
    val userCreatedExams: List<CustomPublishedExam> = emptyList(),
    val storyLastReadPages: Map<String, Int> = emptyMap(),
    val storyBookmarkedPages: Set<String> = emptySet(),
    val missions: List<MissionItem> = MissionTimeHelper.createStarterMissions(),
    val missionLiveNotifEnabled: Boolean = false
)

class UserPreferencesRepository(private val context: Context) {

    private object Keys {
        val USER_NAME = stringPreferencesKey("user_name")
        val CITY_ID = stringPreferencesKey("city_id")
        val CITY_NAME = stringPreferencesKey("city_name")
        val LATITUDE = doublePreferencesKey("latitude")
        val LONGITUDE = doublePreferencesKey("longitude")
        val TIMEZONE = doublePreferencesKey("timezone")
        val CALC_METHOD = stringPreferencesKey("calc_method")
        val ADZAN_ENABLED = booleanPreferencesKey("adzan_enabled")
        val ADZAN_PRAYERS = stringSetPreferencesKey("adzan_prayers")
        val COMPLETED_DATE = stringPreferencesKey("completed_date")
        val COMPLETED_PRAYERS = stringSetPreferencesKey("completed_prayers")
        val BOOKMARKED_AYAHS = stringSetPreferencesKey("bookmarked_ayahs")
        val FAVORITE_DUAS = stringSetPreferencesKey("favorite_duas")
        val DZIKIR_PRESET = stringPreferencesKey("dzikir_preset")
        val DZIKIR_COUNT = intPreferencesKey("dzikir_count")
        val DZIKIR_TARGET = intPreferencesKey("dzikir_target")
        val DZIKIR_LIFETIME = longPreferencesKey("dzikir_lifetime")
        val QURAN_FONT_SIZE = intPreferencesKey("quran_font_size")
        val QURAN_LANG = stringPreferencesKey("quran_lang")
        val APP_LANG = stringPreferencesKey("app_lang")
        val JOINED_GROUP_ID = stringPreferencesKey("joined_group_id")
        val JOINED_GROUP_IDS = stringSetPreferencesKey("joined_group_ids")
        val STUDY_SUBJECT = stringPreferencesKey("study_subject")
        val STUDY_TODAY_DATE = stringPreferencesKey("study_today_date")
        val STUDY_TODAY_SECONDS = longPreferencesKey("study_today_seconds")
        val STUDY_BATCH_TAG = stringPreferencesKey("study_batch_tag")
        val FOCUS_GOAL_HOURS = intPreferencesKey("focus_goal_hours")
        val STUDY_STREAK_DAYS = intPreferencesKey("study_streak_days")
        val FOCUS_SESSION_MODE = stringPreferencesKey("focus_session_mode")
        val STUDY_SUBJECTS_JSON = stringPreferencesKey("study_subjects_json")
        val STUDY_TODOS_JSON = stringPreferencesKey("study_todos_json")
        val TRACKER_SUBJECTS_JSON = stringPreferencesKey("tracker_subjects_json")
        val TRACKER_SCHEMA_VERSION = intPreferencesKey("tracker_schema_version")
        val EXAM_BEST_SCORES_JSON = stringPreferencesKey("exam_best_scores_json")
        val EXAM_WRONG_IDS = stringSetPreferencesKey("exam_wrong_ids")
        val TOTAL_EXAMS_TAKEN = intPreferencesKey("total_exams_taken")
        val CUSTOM_EXAMS_JSON = stringPreferencesKey("custom_exams_json")
        val STORY_LAST_READ_JSON = stringPreferencesKey("story_last_read_json")
        val STORY_BOOKMARKS = stringSetPreferencesKey("story_bookmarks")
        val MISSIONS_JSON = stringPreferencesKey("missions_json")
        val MISSION_LIVE_NOTIF = booleanPreferencesKey("mission_live_notif")
    }

    private companion object {
        const val CURRENT_TRACKER_SCHEMA = 2
    }

    val prefsFlow: Flow<UserPrefsState> = context.dataStore.data.map { prefs ->
        val methodName = prefs[Keys.CALC_METHOD]
        val method = CalculationMethodOption.entries.firstOrNull { it.name == methodName }
            ?: CalculationMethodOption.BANGLADESH

        UserPrefsState(
            userName = prefs[Keys.USER_NAME] ?: "Brother / Sister",
            cityId = prefs[Keys.CITY_ID] ?: "dhaka",
            cityName = prefs[Keys.CITY_NAME] ?: "Dhaka, Bangladesh",
            latitude = prefs[Keys.LATITUDE] ?: 23.8103,
            longitude = prefs[Keys.LONGITUDE] ?: 90.4125,
            timeZoneHours = prefs[Keys.TIMEZONE] ?: 6.0,
            calcMethod = method,
            adzanNotificationsEnabled = prefs[Keys.ADZAN_ENABLED] ?: false,
            enabledPrayersForAdzan = prefs[Keys.ADZAN_PRAYERS]
                ?: setOf("Fajr", "Dhuhr", "Asr", "Maghrib", "Isha"),
            completedPrayersDate = prefs[Keys.COMPLETED_DATE] ?: "",
            completedPrayersToday = prefs[Keys.COMPLETED_PRAYERS] ?: emptySet(),
            bookmarkedAyahKeys = prefs[Keys.BOOKMARKED_AYAHS] ?: setOf("55:13"),
            favoriteDuaIds = prefs[Keys.FAVORITE_DUAS] ?: setOf("dua_pagi_1"),
            dzikirPresetId = prefs[Keys.DZIKIR_PRESET] ?: "subhanallah",
            dzikirCount = prefs[Keys.DZIKIR_COUNT] ?: 0,
            dzikirTarget = prefs[Keys.DZIKIR_TARGET] ?: 33,
            dzikirLifetimeTotal = prefs[Keys.DZIKIR_LIFETIME] ?: 0L,
            quranFontSizeSp = prefs[Keys.QURAN_FONT_SIZE] ?: 24,
            quranTranslationLang = prefs[Keys.QURAN_LANG] ?: "en",
            appLanguage = prefs[Keys.APP_LANG] ?: "en",
            joinedGroupId = prefs[Keys.JOINED_GROUP_ID] ?: "grp_dhaka_quran",
            joinedGroupIds = prefs[Keys.JOINED_GROUP_IDS] ?: setOf("grp_dhaka_quran", "grp_hsc_science"),
            studySubject = prefs[Keys.STUDY_SUBJECT] ?: "Physics",
            studyTodayDate = prefs[Keys.STUDY_TODAY_DATE] ?: "",
            studyTodaySeconds = prefs[Keys.STUDY_TODAY_SECONDS] ?: 128L,
            studyBatchTag = prefs[Keys.STUDY_BATCH_TAG] ?: "HSC-26",
            dailyFocusGoalHours = prefs[Keys.FOCUS_GOAL_HOURS] ?: 6,
            studyStreakDays = prefs[Keys.STUDY_STREAK_DAYS] ?: 0,
            focusSessionModeId = prefs[Keys.FOCUS_SESSION_MODE] ?: "STOPWATCH",
            studySubjects = decodeSubjects(prefs[Keys.STUDY_SUBJECTS_JSON]),
            studyTodos = decodeTodos(prefs[Keys.STUDY_TODOS_JSON]),
            trackerSubjects = decodeTrackerSubjects(
                raw = prefs[Keys.TRACKER_SUBJECTS_JSON],
                schemaVersion = prefs[Keys.TRACKER_SCHEMA_VERSION] ?: 1
            ),
            examBestScores = decodeExamBestScores(prefs[Keys.EXAM_BEST_SCORES_JSON]),
            wrongExamQuestionIds = prefs[Keys.EXAM_WRONG_IDS] ?: emptySet(),
            totalExamsTaken = prefs[Keys.TOTAL_EXAMS_TAKEN] ?: 0,
            userCreatedExams = decodeCustomExams(prefs[Keys.CUSTOM_EXAMS_JSON]),
            storyLastReadPages = decodeExamBestScores(prefs[Keys.STORY_LAST_READ_JSON]),
            storyBookmarkedPages = prefs[Keys.STORY_BOOKMARKS] ?: emptySet(),
            missions = MissionStorageHelper.decodeMissions(prefs[Keys.MISSIONS_JSON])
                ?: MissionTimeHelper.createStarterMissions(),
            missionLiveNotifEnabled = prefs[Keys.MISSION_LIVE_NOTIF] ?: false
        )
    }

    suspend fun updateUserName(name: String) {
        context.dataStore.edit { it[Keys.USER_NAME] = name.trim().ifEmpty { "Brother / Sister" } }
    }

    suspend fun updateLocation(cityId: String, cityName: String, lat: Double, lng: Double, tzHours: Double) {
        context.dataStore.edit {
            it[Keys.CITY_ID] = cityId
            it[Keys.CITY_NAME] = cityName
            it[Keys.LATITUDE] = lat
            it[Keys.LONGITUDE] = lng
            it[Keys.TIMEZONE] = tzHours
        }
    }

    suspend fun updateCalcMethod(method: CalculationMethodOption) {
        context.dataStore.edit { it[Keys.CALC_METHOD] = method.name }
    }

    suspend fun setAdzanNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.ADZAN_ENABLED] = enabled }
    }

    suspend fun toggleAdzanForPrayer(prayerName: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[Keys.ADZAN_PRAYERS] ?: setOf("Fajr", "Dhuhr", "Asr", "Maghrib", "Isha")
            prefs[Keys.ADZAN_PRAYERS] = if (prayerName in current) current - prayerName else current + prayerName
        }
    }

    suspend fun togglePrayerCompletedToday(todayKey: String, prayerName: String) {
        context.dataStore.edit { prefs ->
            val storedDate = prefs[Keys.COMPLETED_DATE] ?: ""
            val current = if (storedDate == todayKey) {
                prefs[Keys.COMPLETED_PRAYERS] ?: emptySet()
            } else {
                emptySet()
            }
            prefs[Keys.COMPLETED_DATE] = todayKey
            prefs[Keys.COMPLETED_PRAYERS] = if (prayerName in current) current - prayerName else current + prayerName
        }
    }

    suspend fun toggleAyahBookmark(key: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[Keys.BOOKMARKED_AYAHS] ?: setOf("55:13")
            prefs[Keys.BOOKMARKED_AYAHS] = if (key in current) current - key else current + key
        }
    }

    suspend fun toggleDuaFavorite(id: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[Keys.FAVORITE_DUAS] ?: setOf("dua_pagi_1")
            prefs[Keys.FAVORITE_DUAS] = if (id in current) current - id else current + id
        }
    }

    suspend fun updateDzikirState(presetId: String, count: Int, target: Int, lifetimeDelta: Long = 0L) {
        context.dataStore.edit { prefs ->
            prefs[Keys.DZIKIR_PRESET] = presetId
            prefs[Keys.DZIKIR_COUNT] = count
            prefs[Keys.DZIKIR_TARGET] = target
            if (lifetimeDelta > 0L) {
                prefs[Keys.DZIKIR_LIFETIME] = (prefs[Keys.DZIKIR_LIFETIME] ?: 0L) + lifetimeDelta
            }
        }
    }

    suspend fun updateQuranFontSize(sizeSp: Int) {
        context.dataStore.edit { prefs ->
            prefs[Keys.QURAN_FONT_SIZE] = sizeSp.coerceIn(18, 36)
        }
    }

    suspend fun updateQuranTranslationLang(lang: String) {
        context.dataStore.edit { prefs ->
            prefs[Keys.QURAN_LANG] = if (lang == "bn") "bn" else "en"
        }
    }

    suspend fun updateAppLanguage(lang: String) {
        val normalized = if (lang == "bn") "bn" else "en"
        context.dataStore.edit { prefs ->
            prefs[Keys.APP_LANG] = normalized
            prefs[Keys.QURAN_LANG] = normalized
        }
    }

    suspend fun updateStudyProgress(
        groupId: String,
        joinedGroupIds: Set<String>,
        subject: String,
        todayDate: String,
        todaySeconds: Long
    ) {
        context.dataStore.edit { prefs ->
            prefs[Keys.JOINED_GROUP_ID] = groupId
            prefs[Keys.JOINED_GROUP_IDS] = joinedGroupIds
            prefs[Keys.STUDY_SUBJECT] = subject
            prefs[Keys.STUDY_TODAY_DATE] = todayDate
            prefs[Keys.STUDY_TODAY_SECONDS] = todaySeconds
        }
    }

    suspend fun saveStudySubjects(subjects: List<StudySubjectItem>, todayDate: String, todaySeconds: Long) {
        context.dataStore.edit { prefs ->
            prefs[Keys.STUDY_SUBJECTS_JSON] = encodeSubjects(subjects)
            prefs[Keys.STUDY_TODAY_DATE] = todayDate
            prefs[Keys.STUDY_TODAY_SECONDS] = todaySeconds
        }
    }

    suspend fun saveStudyTodos(todos: List<StudyTodoItem>) {
        context.dataStore.edit { prefs ->
            prefs[Keys.STUDY_TODOS_JSON] = encodeTodos(todos)
        }
    }

    suspend fun saveTrackerSubjects(subjects: List<TrackerSubject>) {
        context.dataStore.edit { prefs ->
            prefs[Keys.TRACKER_SUBJECTS_JSON] = encodeTrackerSubjects(subjects)
            prefs[Keys.TRACKER_SCHEMA_VERSION] = CURRENT_TRACKER_SCHEMA
        }
    }

    suspend fun updateDashboardSettings(
        batchTag: String? = null,
        goalHours: Int? = null,
        sessionModeId: String? = null,
        streakDays: Int? = null
    ) {
        context.dataStore.edit { prefs ->
            if (batchTag != null) prefs[Keys.STUDY_BATCH_TAG] = batchTag.trim().ifEmpty { "HSC-26" }
            if (goalHours != null) prefs[Keys.FOCUS_GOAL_HOURS] = goalHours.coerceIn(1, 16)
            if (sessionModeId != null) prefs[Keys.FOCUS_SESSION_MODE] = sessionModeId
            if (streakDays != null) prefs[Keys.STUDY_STREAK_DAYS] = streakDays.coerceAtLeast(0)
        }
    }

    suspend fun recordExamResult(
        chapterId: String?,
        score: Int,
        newlyWrongIds: Set<String>,
        newlyCorrectIds: Set<String>
    ) {
        context.dataStore.edit { prefs ->
            prefs[Keys.TOTAL_EXAMS_TAKEN] = (prefs[Keys.TOTAL_EXAMS_TAKEN] ?: 0) + 1
            val currentWrong = prefs[Keys.EXAM_WRONG_IDS] ?: emptySet()
            prefs[Keys.EXAM_WRONG_IDS] = (currentWrong - newlyCorrectIds) + newlyWrongIds

            if (!chapterId.isNullOrBlank()) {
                val scores = decodeExamBestScores(prefs[Keys.EXAM_BEST_SCORES_JSON]).toMutableMap()
                val existing = scores[chapterId] ?: -1
                if (score > existing) {
                    scores[chapterId] = score
                }
                prefs[Keys.EXAM_BEST_SCORES_JSON] = encodeExamBestScores(scores)
            }
        }
    }

    private fun encodeExamBestScores(map: Map<String, Int>): String {
        val obj = JSONObject()
        map.forEach { (k, v) -> obj.put(k, v) }
        return obj.toString()
    }

    private fun decodeExamBestScores(raw: String?): Map<String, Int> {
        if (raw.isNullOrBlank()) return emptyMap()
        return try {
            val obj = JSONObject(raw)
            val result = mutableMapOf<String, Int>()
            val keys = obj.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                result[key] = obj.optInt(key, 0)
            }
            result
        } catch (_: Exception) {
            emptyMap()
        }
    }

    suspend fun saveCustomExams(exams: List<CustomPublishedExam>) {
        context.dataStore.edit { prefs ->
            prefs[Keys.CUSTOM_EXAMS_JSON] = encodeCustomExams(exams)
        }
    }

    suspend fun updateStoryLastReadPage(bookId: String, pageIndex: Int) {
        context.dataStore.edit { prefs ->
            val current = decodeExamBestScores(prefs[Keys.STORY_LAST_READ_JSON]).toMutableMap()
            current[bookId] = pageIndex
            prefs[Keys.STORY_LAST_READ_JSON] = encodeExamBestScores(current)
        }
    }

    suspend fun toggleStoryBookmarkPage(bookId: String, pageIndex: Int) {
        val key = "$bookId:$pageIndex"
        context.dataStore.edit { prefs ->
            val current = prefs[Keys.STORY_BOOKMARKS] ?: emptySet()
            prefs[Keys.STORY_BOOKMARKS] = if (key in current) current - key else current + key
        }
    }

    suspend fun saveMissions(missions: List<MissionItem>, liveNotifEnabled: Boolean) {
        MissionStorageHelper.writeWidgetMirror(context, missions, liveNotifEnabled)
        context.dataStore.edit { prefs ->
            prefs[Keys.MISSIONS_JSON] = MissionStorageHelper.encodeMissions(missions)
            prefs[Keys.MISSION_LIVE_NOTIF] = liveNotifEnabled
        }
    }

    suspend fun setMissionLiveNotifEnabled(enabled: Boolean, missions: List<MissionItem>) {
        MissionStorageHelper.writeWidgetMirror(context, missions, enabled)
        context.dataStore.edit { prefs ->
            prefs[Keys.MISSION_LIVE_NOTIF] = enabled
        }
    }

    private fun encodeCustomExams(list: List<CustomPublishedExam>): String {
        val arr = JSONArray()
        list.forEach { exam ->
            val qArr = JSONArray()
            exam.questions.forEach { q ->
                val optsArr = JSONArray()
                q.options.forEach { opt -> optsArr.put(opt) }
                qArr.put(
                    JSONObject().apply {
                        put("id", q.id)
                        put("subjectId", q.subjectId)
                        put("chapterId", q.chapterId)
                        put("paper", q.paper)
                        put("uddipok", q.uddipok)
                        put("questionText", q.questionText)
                        put("options", optsArr)
                        put("correctIndex", q.correctIndex)
                        put("explanation", q.explanation)
                    }
                )
            }
            arr.put(
                JSONObject().apply {
                    put("id", exam.id)
                    put("title", exam.title)
                    put("authorName", exam.authorName)
                    put("authorBatch", exam.authorBatch)
                    put("subjectId", exam.subjectId)
                    put("subjectName", exam.subjectName)
                    put("subjectColorHex", exam.subjectColorHex)
                    put("paper", exam.paper)
                    put("chapterId", exam.chapterId)
                    put("chapterTitle", exam.chapterTitle)
                    put("password", exam.password)
                    put("description", exam.description)
                    put("rules", exam.rules)
                    put("showDetailsInEntryPopup", exam.showDetailsInEntryPopup)
                    put("paperStyle", exam.paperStyle.name)
                    put("isPublished", exam.isPublished)
                    put("createdAtMillis", exam.createdAtMillis)
                    put("attemptCount", exam.attemptCount)
                    put("questions", qArr)
                }
            )
        }
        return arr.toString()
    }

    private fun decodeCustomExams(raw: String?): List<CustomPublishedExam> {
        if (raw.isNullOrBlank()) return emptyList()
        return try {
            val arr = JSONArray(raw)
            val result = mutableListOf<CustomPublishedExam>()
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                val qArr = obj.optJSONArray("questions") ?: JSONArray()
                val qs = mutableListOf<ExamQuestion>()
                for (j in 0 until qArr.length()) {
                    val qObj = qArr.getJSONObject(j)
                    val optsArr = qObj.optJSONArray("options") ?: JSONArray()
                    val opts = mutableListOf<String>()
                    for (k in 0 until optsArr.length()) {
                        opts.add(optsArr.optString(k))
                    }
                    qs.add(
                        ExamQuestion(
                            id = qObj.getString("id"),
                            subjectId = qObj.optString("subjectId", ""),
                            chapterId = qObj.optString("chapterId", ""),
                            paper = qObj.optInt("paper", 1),
                            uddipok = qObj.optString("uddipok", ""),
                            questionText = qObj.getString("questionText"),
                            options = opts,
                            correctIndex = qObj.optInt("correctIndex", 0),
                            explanation = qObj.optString("explanation", "")
                        )
                    )
                }
                val styleName = obj.optString("paperStyle", ExamPaperStyle.CLASSIC_CARDS.name)
                val resolvedStyle = ExamPaperStyle.entries.firstOrNull { it.name == styleName }
                    ?: ExamPaperStyle.CLASSIC_CARDS
                result.add(
                    CustomPublishedExam(
                        id = obj.getString("id"),
                        title = obj.getString("title"),
                        authorName = obj.optString("authorName", "Student"),
                        authorBatch = obj.optString("authorBatch", "HSC-26"),
                        subjectId = obj.optString("subjectId", "trk_physics"),
                        subjectName = obj.optString("subjectName", "Physics"),
                        subjectColorHex = obj.optLong("subjectColorHex", 0xFFE66D45),
                        paper = obj.optInt("paper", 1),
                        chapterId = obj.optString("chapterId", ""),
                        chapterTitle = obj.optString("chapterTitle", ""),
                        password = obj.optString("password", ""),
                        description = obj.optString("description", ""),
                        rules = obj.optString("rules", ""),
                        showDetailsInEntryPopup = obj.optBoolean("showDetailsInEntryPopup", true),
                        paperStyle = resolvedStyle,
                        isPublished = obj.optBoolean("isPublished", true),
                        createdAtMillis = obj.optLong("createdAtMillis", 0L),
                        attemptCount = obj.optInt("attemptCount", 0),
                        questions = qs
                    )
                )
            }
            result
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun encodeSubjects(list: List<StudySubjectItem>): String {
        val arr = JSONArray()
        list.forEach { item ->
            arr.put(
                JSONObject().apply {
                    put("id", item.id)
                    put("name", item.name)
                    put("colorHex", item.colorHex)
                    put("todaySeconds", item.todaySeconds)
                    put("goalHours", item.goalHours)
                }
            )
        }
        return arr.toString()
    }

    private fun decodeSubjects(raw: String?): List<StudySubjectItem> {
        if (raw.isNullOrBlank()) return StudyDashboardDefaults.defaultSubjects
        return try {
            val arr = JSONArray(raw)
            val result = mutableListOf<StudySubjectItem>()
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                result.add(
                    StudySubjectItem(
                        id = obj.getString("id"),
                        name = obj.getString("name"),
                        colorHex = obj.getLong("colorHex"),
                        todaySeconds = obj.getLong("todaySeconds"),
                        goalHours = obj.optDouble("goalHours", 2.0)
                    )
                )
            }
            if (result.isEmpty()) StudyDashboardDefaults.defaultSubjects else result
        } catch (_: Exception) {
            StudyDashboardDefaults.defaultSubjects
        }
    }

    private fun encodeTodos(list: List<StudyTodoItem>): String {
        val arr = JSONArray()
        list.forEach { item ->
            arr.put(
                JSONObject().apply {
                    put("id", item.id)
                    put("title", item.title)
                    put("isDone", item.isDone)
                }
            )
        }
        return arr.toString()
    }

    private fun decodeTodos(raw: String?): List<StudyTodoItem> {
        if (raw.isNullOrBlank()) return StudyDashboardDefaults.defaultTodos
        return try {
            val arr = JSONArray(raw)
            val result = mutableListOf<StudyTodoItem>()
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                result.add(
                    StudyTodoItem(
                        id = obj.getString("id"),
                        title = obj.getString("title"),
                        isDone = obj.optBoolean("isDone", false)
                    )
                )
            }
            result
        } catch (_: Exception) {
            StudyDashboardDefaults.defaultTodos
        }
    }

    private fun encodeTrackerSubjects(list: List<TrackerSubject>): String {
        val arr = JSONArray()
        list.forEach { subj ->
            val chArr = JSONArray()
            subj.chapters.forEach { ch ->
                val tagsArr = JSONArray()
                ch.completedTags.forEach { tag -> tagsArr.put(tag.name) }
                chArr.put(
                    JSONObject().apply {
                        put("id", ch.id)
                        put("title", ch.title)
                        put("paper", ch.paper)
                        put("tags", tagsArr)
                    }
                )
            }
            arr.put(
                JSONObject().apply {
                    put("id", subj.id)
                    put("nameEn", subj.nameEn)
                    put("nameBn", subj.nameBn)
                    put("colorHex", subj.colorHex)
                    put("chapters", chArr)
                }
            )
        }
        return arr.toString()
    }

    private fun decodeTrackerSubjects(raw: String?, schemaVersion: Int): List<TrackerSubject> {
        if (raw.isNullOrBlank()) return TrackerDefaults.defaultSubjects
        return try {
            val arr = JSONArray(raw)
            val savedList = mutableListOf<TrackerSubject>()
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                val chArr = obj.optJSONArray("chapters") ?: JSONArray()
                val chapters = mutableListOf<TrackerChapter>()
                for (j in 0 until chArr.length()) {
                    val chObj = chArr.getJSONObject(j)
                    val tagsArr = chObj.optJSONArray("tags") ?: JSONArray()
                    val tags = mutableSetOf<PrepQuestionTag>()
                    for (k in 0 until tagsArr.length()) {
                        val tagName = tagsArr.optString(k)
                        PrepQuestionTag.entries.firstOrNull { it.name == tagName }?.let { tags.add(it) }
                    }
                    chapters.add(
                        TrackerChapter(
                            id = chObj.getString("id"),
                            title = chObj.getString("title"),
                            paper = chObj.optInt("paper", 1).coerceIn(1, 2),
                            completedTags = tags
                        )
                    )
                }
                savedList.add(
                    TrackerSubject(
                        id = obj.getString("id"),
                        nameEn = obj.getString("nameEn"),
                        nameBn = obj.optString("nameBn", obj.getString("nameEn")),
                        colorHex = obj.getLong("colorHex"),
                        chapters = chapters
                    )
                )
            }
            if (savedList.isEmpty()) return TrackerDefaults.defaultSubjects
            if (schemaVersion < CURRENT_TRACKER_SCHEMA) {
                val savedBySubj = savedList.associateBy { it.id }
                val defaultIds = TrackerDefaults.defaultSubjects.map { it.id }.toSet()
                val upgradedDefaults = TrackerDefaults.defaultSubjects.map { defSubj ->
                    val oldSubj = savedBySubj[defSubj.id] ?: return@map defSubj
                    val oldTagsById = oldSubj.chapters.associate { it.id to it.completedTags }
                    defSubj.copy(
                        chapters = defSubj.chapters.map { ch ->
                            val legacyId = ch.id.replace("_p1_ch_", "_ch_")
                            val preserved = oldTagsById[ch.id] ?: oldTagsById[legacyId]
                            if (preserved != null) ch.copy(completedTags = preserved) else ch
                        }
                    )
                }
                val customSubjects = savedList.filter { it.id !in defaultIds }
                return upgradedDefaults + customSubjects
            }
            savedList
        } catch (_: Exception) {
            TrackerDefaults.defaultSubjects
        }
    }
}
