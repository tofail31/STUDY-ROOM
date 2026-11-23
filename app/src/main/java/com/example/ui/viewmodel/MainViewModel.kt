package com.example.ui.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.location.Geocoder
import android.location.LocationManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.calc.PrayerTimeCalculator
import com.example.data.local.ExamGroundDefaults
import com.example.data.local.ExamQuestionBank
import com.example.data.local.IslamicContentData
import com.example.data.local.IslamicRepository
import com.example.data.local.QuranCatalogData
import com.example.data.local.StudyGroupsData
import com.example.data.model.CalculationMethodOption
import com.example.data.model.CityPreset
import com.example.data.model.CustomPublishedExam
import com.example.data.model.DailyAyah
import com.example.data.model.DzikirPreset
import com.example.data.model.ExamPaperStage
import com.example.data.model.ExamPaperStyle
import com.example.data.model.ExamUiState
import com.example.data.model.FocusSessionMode
import com.example.data.model.MissionItem
import com.example.data.model.MissionTimeHelper
import com.example.data.model.PrayerSchedule
import com.example.data.model.PrepQuestionTag
import com.example.data.model.StudyDashboardDefaults
import com.example.data.model.StudyGroup
import com.example.data.model.StudyStatus
import com.example.data.model.StudySubjectItem
import com.example.data.model.StudyTodoItem
import com.example.data.model.SurahContent
import com.example.data.model.TrackerChapter
import com.example.data.model.TrackerDefaults
import com.example.data.model.TrackerSubject
import com.example.data.preferences.UserPreferencesRepository
import com.example.data.preferences.UserPrefsState
import com.example.notifications.AdzanScheduler
import com.example.notifications.MissionScheduler
import com.example.widget.MissionWidgetProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

sealed interface SurahLoadState {
    data object Idle : SurahLoadState
    data object Loading : SurahLoadState
    data class Success(val content: SurahContent) : SurahLoadState
    data class Error(val message: String, val surahNumber: Int) : SurahLoadState
}

data class HomeUiState(
    val clockDisplay: String = "06:04",
    val combinedDateDisplay: String = "",
    val todayKey: String = "",
    val prayerSchedule: PrayerSchedule = PrayerTimeCalculator.calculateDaySchedule(
        latitude = 23.8103,
        longitude = 90.4125,
        timeZoneHours = 6.0,
        method = CalculationMethodOption.BANGLADESH
    ),
    val dailyAyah: DailyAyah = QuranCatalogData.dailyAyahs.first(),
    val isRefreshingLocation: Boolean = false,
    val locationStatusMessage: String? = null
)

data class StudyRoomUiState(
    val activeGroupId: String = "grp_dhaka_quran",
    val joinedGroupIds: Set<String> = setOf("grp_dhaka_quran", "grp_hsc_science"),
    val isStudying: Boolean = false,
    val currentSessionSeconds: Long = 0L,
    val todayStudySeconds: Long = 0L,
    val currentSubject: String = "Physics",
    val groups: List<StudyGroup> = StudyGroupsData.groups,
    val cheeredMemberIds: Set<String> = emptySet()
)

data class StudyDashboardUiState(
    val subjects: List<StudySubjectItem> = StudyDashboardDefaults.defaultSubjects,
    val todos: List<StudyTodoItem> = StudyDashboardDefaults.defaultTodos,
    val activeSubjectId: String? = null,
    val focusSubjectId: String = StudyDashboardDefaults.defaultSubjects.first().id,
    val batchTag: String = "HSC-26",
    val dailyGoalHours: Int = 6,
    val streakDays: Int = 0,
    val focusMode: FocusSessionMode = FocusSessionMode.STOPWATCH,
    val focusCountdownSeconds: Int = 0,
    val focusSessionElapsedSeconds: Long = 0L,
    val isFocusBreakPhase: Boolean = false,
    val completedPomodoros: Int = 0,
    val keepScreenOnInFocus: Boolean = true,
    val isAmbientSoundOn: Boolean = false,
    val selectedWeekDayIdx: Int = -1 // -1 means follow today
) {
    val totalTodaySeconds: Long get() = subjects.sumOf { it.todaySeconds }
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val prefsRepo = UserPreferencesRepository(application.applicationContext)
    private val repository = IslamicRepository()

    val prefsState: StateFlow<UserPrefsState> = prefsRepo.prefsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UserPrefsState()
    )

    private val _homeState = MutableStateFlow(HomeUiState())
    val homeState: StateFlow<HomeUiState> = _homeState.asStateFlow()

    private val _surahLoadState = MutableStateFlow<SurahLoadState>(SurahLoadState.Idle)
    val surahLoadState: StateFlow<SurahLoadState> = _surahLoadState.asStateFlow()

    private val _studyRoomState = MutableStateFlow(StudyRoomUiState())
    val studyRoomState: StateFlow<StudyRoomUiState> = _studyRoomState.asStateFlow()

    private val _studyDashboardState = MutableStateFlow(StudyDashboardUiState())
    val studyDashboardState: StateFlow<StudyDashboardUiState> = _studyDashboardState.asStateFlow()

    private val _trackerSubjects = MutableStateFlow(TrackerDefaults.defaultSubjects)
    val trackerSubjects: StateFlow<List<TrackerSubject>> = _trackerSubjects.asStateFlow()

    private val _examState = MutableStateFlow(ExamUiState())
    val examState: StateFlow<ExamUiState> = _examState.asStateFlow()

    private val _userCreatedExams = MutableStateFlow<List<CustomPublishedExam>>(emptyList())
    val userCreatedExams: StateFlow<List<CustomPublishedExam>> = _userCreatedExams.asStateFlow()

    private val _missions = MutableStateFlow<List<MissionItem>>(MissionTimeHelper.createStarterMissions())
    val missions: StateFlow<List<MissionItem>> = _missions.asStateFlow()

    private val _missionLiveNotifEnabled = MutableStateFlow(false)
    val missionLiveNotifEnabled: StateFlow<Boolean> = _missionLiveNotifEnabled.asStateFlow()

    private var dailyAyahIdx = 0
    private var lastSyncedLocationKey = ""
    private var apiFetchedSchedule: PrayerSchedule? = null
    private var studySyncLoaded = false
    private var dashboardSyncLoaded = false
    private var studyPersistTicks = 0
    private var dashboardPersistTicks = 0
    private var isExamTimerRunning = false

    init {
        startClockTicker()
        observePrefsAndSyncSchedule()
    }

    private fun startClockTicker() {
        viewModelScope.launch {
            while (true) {
                val now = Calendar.getInstance()
                val clockFmt = SimpleDateFormat("HH:mm", Locale.US).format(now.time)
                val todayKey = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(now.time)
                val prefs = prefsState.value
                val combinedDate = PrayerTimeCalculator.formatCombinedDate(
                    calendar = now,
                    isBangla = prefs.appLanguage == "bn"
                )

                val updatedSchedule = apiFetchedSchedule?.let { cached ->
                    PrayerTimeCalculator.enrichWithApiTimes(
                        base = cached,
                        fajr = cached.subuh,
                        sunrise = cached.syuruq,
                        dhuhr = cached.dzuhur,
                        asr = cached.ashar,
                        maghrib = cached.maghrib,
                        isha = cached.isya,
                        now = now
                    )
                } ?: PrayerTimeCalculator.calculateDaySchedule(
                    latitude = prefs.latitude,
                    longitude = prefs.longitude,
                    timeZoneHours = prefs.timeZoneHours,
                    method = prefs.calcMethod,
                    now = now
                )

                    _homeState.update {
                        it.copy(
                            clockDisplay = clockFmt,
                            combinedDateDisplay = combinedDate,
                            todayKey = todayKey,
                            prayerSchedule = updatedSchedule
                        )
                    }

                    // Tick live YPT study timers for active members and user
                    _studyRoomState.update { room ->
                        val updatedGroups = room.groups.map { group ->
                            group.copy(
                                members = group.members.map { m ->
                                    if (m.status == StudyStatus.STUDYING) {
                                        m.copy(todayStudySeconds = m.todayStudySeconds + 1L)
                                    } else m
                                }
                            )
                        }
                        if (room.isStudying) {
                            room.copy(
                                groups = updatedGroups,
                                currentSessionSeconds = room.currentSessionSeconds + 1L,
                                todayStudySeconds = room.todayStudySeconds + 1L
                            )
                        } else {
                            room.copy(groups = updatedGroups)
                        }
                    }

                    val currentRoom = _studyRoomState.value
                    if (currentRoom.isStudying) {
                        studyPersistTicks++
                        if (studyPersistTicks >= 10) {
                            studyPersistTicks = 0
                            persistCurrentStudyProgress(todayKey)
                        }
                    }

                    // Tick active subject timer on Home Study Dashboard & Focus Mode
                    val activeSubjId = _studyDashboardState.value.activeSubjectId
                    if (activeSubjId != null) {
                        var breakJustFinished = false
                        _studyDashboardState.update { dash ->
                            if (dash.isFocusBreakPhase) {
                                val nextBreakLeft = (dash.focusCountdownSeconds - 1).coerceAtLeast(0)
                                if (nextBreakLeft == 0) {
                                    breakJustFinished = true
                                    dash.copy(
                                        activeSubjectId = null,
                                        isFocusBreakPhase = false,
                                        focusCountdownSeconds = dash.focusMode.durationSeconds
                                    )
                                } else {
                                    dash.copy(focusCountdownSeconds = nextBreakLeft)
                                }
                            } else {
                                val updatedSubjects = dash.subjects.map { subj ->
                                    if (subj.id == activeSubjId) {
                                        subj.copy(todaySeconds = subj.todaySeconds + 1L)
                                    } else subj
                                }
                                val nextElapsed = dash.focusSessionElapsedSeconds + 1L
                                if (dash.focusMode == FocusSessionMode.STOPWATCH) {
                                    dash.copy(
                                        subjects = updatedSubjects,
                                        focusSessionElapsedSeconds = nextElapsed
                                    )
                                } else {
                                    val nextCountdown = (dash.focusCountdownSeconds - 1).coerceAtLeast(0)
                                    if (nextCountdown == 0) {
                                        val hasBreak = dash.focusMode.breakDurationSeconds > 0
                                        dash.copy(
                                            subjects = updatedSubjects,
                                            focusSessionElapsedSeconds = nextElapsed,
                                            completedPomodoros = dash.completedPomodoros + 1,
                                            isFocusBreakPhase = hasBreak,
                                            focusCountdownSeconds = if (hasBreak) {
                                                dash.focusMode.breakDurationSeconds
                                            } else {
                                                dash.focusMode.durationSeconds
                                            }
                                        )
                                    } else {
                                        dash.copy(
                                            subjects = updatedSubjects,
                                            focusSessionElapsedSeconds = nextElapsed,
                                            focusCountdownSeconds = nextCountdown
                                        )
                                    }
                                }
                            }
                        }
                        if (breakJustFinished) {
                            _studyRoomState.update { it.copy(isStudying = false) }
                        }
                        dashboardPersistTicks++
                        if (dashboardPersistTicks >= 10) {
                            dashboardPersistTicks = 0
                            persistDashboardSubjects(todayKey)
                        }
                    }

                    // Tick elapsed seconds for an active chapter/wrong-answer exam
                    if (isExamTimerRunning && _examState.value.stage == ExamPaperStage.TAKING) {
                        _examState.update { it.copy(elapsedSeconds = it.elapsedSeconds + 1L) }
                    }

                    delay(1_000L)
                }
            }
        }

    private fun observePrefsAndSyncSchedule() {
        viewModelScope.launch {
            prefsState.collectLatest { prefs ->
                if (!studySyncLoaded) {
                    studySyncLoaded = true
                    val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Calendar.getInstance().time)
                    val restoredSeconds = if (prefs.studyTodayDate == today) prefs.studyTodaySeconds else 0L
                    _studyRoomState.update {
                        val mergedIds = if (prefs.joinedGroupId.isNotBlank()) {
                            prefs.joinedGroupIds + prefs.joinedGroupId
                        } else {
                            prefs.joinedGroupIds
                        }
                        it.copy(
                            activeGroupId = prefs.joinedGroupId.ifBlank { mergedIds.firstOrNull().orEmpty() },
                            joinedGroupIds = mergedIds,
                            currentSubject = prefs.studySubject,
                            todayStudySeconds = restoredSeconds
                        )
                    }
                }
                if (!dashboardSyncLoaded) {
                    dashboardSyncLoaded = true
                    val mode = FocusSessionMode.entries.firstOrNull { it.name == prefs.focusSessionModeId }
                        ?: FocusSessionMode.STOPWATCH
                    _studyDashboardState.update {
                        it.copy(
                            subjects = prefs.studySubjects,
                            focusSubjectId = prefs.studySubjects.firstOrNull()?.id ?: it.focusSubjectId,
                            todos = prefs.studyTodos,
                            batchTag = prefs.studyBatchTag,
                            dailyGoalHours = prefs.dailyFocusGoalHours,
                            streakDays = prefs.studyStreakDays,
                            focusMode = mode,
                            focusCountdownSeconds = mode.durationSeconds
                        )
                    }
                    _trackerSubjects.value = prefs.trackerSubjects
                    _userCreatedExams.value = prefs.userCreatedExams
                    _missions.value = prefs.missions
                    _missionLiveNotifEnabled.value = prefs.missionLiveNotifEnabled
                    MissionScheduler.syncAll(
                        context = getApplication<Application>().applicationContext,
                        missions = prefs.missions,
                        liveNotifEnabled = prefs.missionLiveNotifEnabled
                    )
                }
                val locKey = "${prefs.latitude}:${prefs.longitude}:${prefs.calcMethod.id}"
                if (locKey != lastSyncedLocationKey) {
                    lastSyncedLocationKey = locKey
                    val fetched = withContext(Dispatchers.IO) {
                        repository.fetchPrayerTimes(
                            latitude = prefs.latitude,
                            longitude = prefs.longitude,
                            timeZoneHours = prefs.timeZoneHours,
                            method = prefs.calcMethod
                        )
                    }
                    apiFetchedSchedule = if (fetched.isFromApi) fetched else null
                    _homeState.update {
                        it.copy(
                            prayerSchedule = fetched
                        )
                    }
                    syncAlarmsWithCurrentState(prefs, fetched)
                }
            }
        }
    }

    private fun syncAlarmsWithCurrentState(prefs: UserPrefsState, schedule: PrayerSchedule) {
        AdzanScheduler.syncAlarms(
            context = getApplication<Application>().applicationContext,
            enabled = prefs.adzanNotificationsEnabled,
            enabledPrayers = prefs.enabledPrayersForAdzan,
            schedule = schedule,
            cityName = prefs.cityName
        )
    }

    fun cycleDailyAyah() {
        val list = QuranCatalogData.dailyAyahs
        dailyAyahIdx = (dailyAyahIdx + 1) % list.size
        _homeState.update { it.copy(dailyAyah = list[dailyAyahIdx]) }
    }

    fun selectCityPreset(city: CityPreset) {
        viewModelScope.launch {
            prefsRepo.updateLocation(
                cityId = city.id,
                cityName = "${city.name}, ${city.country}",
                lat = city.latitude,
                lng = city.longitude,
                tzHours = city.timeZoneHours
            )
            _homeState.update { it.copy(locationStatusMessage = "Lokasi diperbarui ke ${city.name}") }
        }
    }

    @SuppressLint("MissingPermission")
    fun detectGpsLocation() {
        viewModelScope.launch {
            _homeState.update { it.copy(isRefreshingLocation = true, locationStatusMessage = null) }
            val context = getApplication<Application>().applicationContext
            val result = withContext(Dispatchers.IO) {
                try {
                    val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                    val providers = lm.getProviders(true)
                    var bestLoc: android.location.Location? = null
                    for (p in providers) {
                        val l = lm.getLastKnownLocation(p) ?: continue
                        if (bestLoc == null || l.accuracy < bestLoc.accuracy) {
                            bestLoc = l
                        }
                    }
                    bestLoc
                } catch (_: SecurityException) {
                    null
                } catch (_: Exception) {
                    null
                }
            }

            if (result != null) {
                val cityLabel = withContext(Dispatchers.IO) {
                    try {
                        @Suppress("DEPRECATION")
                        val addresses = Geocoder(context, Locale("id", "ID"))
                            .getFromLocation(result.latitude, result.longitude, 1)
                        val addr = addresses?.firstOrNull()
                        val city = addr?.locality ?: addr?.subAdminArea ?: addr?.adminArea
                        val country = addr?.countryName
                        listOfNotNull(city, country).joinToString(", ").ifBlank { "Lokasi GPS" }
                    } catch (_: Exception) {
                        "Lokasi GPS (${String.format(Locale.US, "%.2f, %.2f", result.latitude, result.longitude)})"
                    }
                }
                val tzHours = TimeZone.getDefault().rawOffset / 3_600_000.0
                prefsRepo.updateLocation(
                    cityId = "gps",
                    cityName = cityLabel,
                    lat = result.latitude,
                    lng = result.longitude,
                    tzHours = tzHours
                )
                _homeState.update {
                    it.copy(
                        isRefreshingLocation = false,
                        locationStatusMessage = "Lokasi GPS aktif: $cityLabel"
                    )
                }
            } else {
                _homeState.update {
                    it.copy(
                        isRefreshingLocation = false,
                        locationStatusMessage = "GPS belum tersedia, gunakan daftar kota di bawah."
                    )
                }
            }
        }
    }

    fun clearLocationStatusMessage() {
        _homeState.update { it.copy(locationStatusMessage = null) }
    }

    fun updateUserName(name: String) {
        viewModelScope.launch { prefsRepo.updateUserName(name) }
    }

    fun updateCalculationMethod(method: CalculationMethodOption) {
        viewModelScope.launch { prefsRepo.updateCalcMethod(method) }
    }

    fun setAdzanNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            prefsRepo.setAdzanNotificationsEnabled(enabled)
            val updatedPrefs = prefsState.value.copy(adzanNotificationsEnabled = enabled)
            syncAlarmsWithCurrentState(updatedPrefs, _homeState.value.prayerSchedule)
        }
    }

    fun toggleAdzanForPrayer(prayerName: String) {
        viewModelScope.launch {
            prefsRepo.toggleAdzanForPrayer(prayerName)
            val current = prefsState.value.enabledPrayersForAdzan
            val nextSet = if (prayerName in current) current - prayerName else current + prayerName
            syncAlarmsWithCurrentState(
                prefsState.value.copy(enabledPrayersForAdzan = nextSet),
                _homeState.value.prayerSchedule
            )
        }
    }

    fun togglePrayerCompletedToday(prayerName: String) {
        val today = _homeState.value.todayKey.ifBlank {
            SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Calendar.getInstance().time)
        }
        viewModelScope.launch {
            prefsRepo.togglePrayerCompletedToday(today, prayerName)
        }
    }

    fun loadSurah(surahNumber: Int) {
        viewModelScope.launch {
            _surahLoadState.value = SurahLoadState.Loading
            val res = withContext(Dispatchers.IO) { repository.getSurahContent(surahNumber) }
            _surahLoadState.value = res.fold(
                onSuccess = { SurahLoadState.Success(it) },
                onFailure = { SurahLoadState.Error(it.message ?: "Gagal memuat surah", surahNumber) }
            )
        }
    }

    fun toggleAyahBookmark(surahNumber: Int, ayahNumber: Int) {
        viewModelScope.launch { prefsRepo.toggleAyahBookmark("$surahNumber:$ayahNumber") }
    }

    fun updateQuranFontSize(sizeSp: Int) {
        viewModelScope.launch { prefsRepo.updateQuranFontSize(sizeSp) }
    }

    fun updateQuranTranslationLang(lang: String) {
        viewModelScope.launch { prefsRepo.updateQuranTranslationLang(lang) }
    }

    fun updateAppLanguage(lang: String) {
        viewModelScope.launch { prefsRepo.updateAppLanguage(lang) }
    }

    fun toggleDuaFavorite(duaId: String) {
        viewModelScope.launch { prefsRepo.toggleDuaFavorite(duaId) }
    }

    fun incrementDzikir(): Boolean {
        val prefs = prefsState.value
        val target = prefs.dzikirTarget
        val nextCount = if (target > 0 && prefs.dzikirCount >= target) 1 else prefs.dzikirCount + 1
        val reachedTarget = target > 0 && nextCount == target
        viewModelScope.launch {
            prefsRepo.updateDzikirState(
                presetId = prefs.dzikirPresetId,
                count = nextCount,
                target = target,
                lifetimeDelta = 1L
            )
        }
        return reachedTarget
    }

    fun resetDzikirCount() {
        val prefs = prefsState.value
        viewModelScope.launch {
            prefsRepo.updateDzikirState(
                presetId = prefs.dzikirPresetId,
                count = 0,
                target = prefs.dzikirTarget,
                lifetimeDelta = 0L
            )
        }
    }

    fun selectDzikirPreset(preset: DzikirPreset) {
        viewModelScope.launch {
            prefsRepo.updateDzikirState(
                presetId = preset.id,
                count = 0,
                target = preset.defaultTarget,
                lifetimeDelta = 0L
            )
        }
    }

    fun updateDzikirTarget(target: Int) {
        val prefs = prefsState.value
        viewModelScope.launch {
            prefsRepo.updateDzikirState(
                presetId = prefs.dzikirPresetId,
                count = if (target > 0 && prefs.dzikirCount > target) 0 else prefs.dzikirCount,
                target = target,
                lifetimeDelta = 0L
            )
        }
    }

    fun getCurrentDzikirPreset(): DzikirPreset {
        val id = prefsState.value.dzikirPresetId
        return IslamicContentData.dzikirPresets.firstOrNull { it.id == id }
            ?: IslamicContentData.dzikirPresets.first()
    }

    fun enterGroupRoom(groupId: String, subject: String) {
        val cleanSubject = subject.trim().ifEmpty { "Physics" }
        _studyRoomState.update {
            it.copy(
                activeGroupId = groupId,
                joinedGroupIds = it.joinedGroupIds + groupId,
                currentSubject = cleanSubject
            )
        }
        persistCurrentStudyProgress()
    }

    fun switchActiveGroupInRoom(groupId: String) {
        _studyRoomState.update {
            it.copy(
                activeGroupId = groupId,
                joinedGroupIds = it.joinedGroupIds + groupId
            )
        }
        persistCurrentStudyProgress()
    }

    fun toggleStudying() {
        val nextStudying = !_studyRoomState.value.isStudying
        _studyRoomState.update {
            it.copy(
                isStudying = nextStudying,
                currentSessionSeconds = if (nextStudying) 0L else it.currentSessionSeconds
            )
        }
        if (!nextStudying) {
            _studyDashboardState.update { it.copy(activeSubjectId = null) }
            persistDashboardSubjects()
        } else if (_studyDashboardState.value.activeSubjectId == null) {
            val matchingSubj = _studyDashboardState.value.subjects.firstOrNull {
                it.name.equals(_studyRoomState.value.currentSubject, ignoreCase = true)
            } ?: _studyDashboardState.value.subjects.firstOrNull()
            if (matchingSubj != null) {
                _studyDashboardState.update { it.copy(activeSubjectId = matchingSubj.id) }
            }
        }
        persistCurrentStudyProgress()
    }

    fun updateStudySubject(subject: String) {
        val clean = subject.trim().ifEmpty { "Physics" }
        _studyRoomState.update { it.copy(currentSubject = clean) }
        persistCurrentStudyProgress()
    }

    fun cheerMember(memberId: String) {
        _studyRoomState.update { it.copy(cheeredMemberIds = it.cheeredMemberIds + memberId) }
    }

    fun leaveGroupRoom(groupId: String = _studyRoomState.value.activeGroupId) {
        _studyRoomState.update { state ->
            val remaining = state.joinedGroupIds - groupId
            state.copy(
                isStudying = false,
                joinedGroupIds = remaining,
                activeGroupId = remaining.firstOrNull().orEmpty()
            )
        }
        _studyDashboardState.update { it.copy(activeSubjectId = null) }
        persistDashboardSubjects()
        persistCurrentStudyProgress()
    }

    private fun persistCurrentStudyProgress(todayOverride: String? = null) {
        val room = _studyRoomState.value
        val today = todayOverride ?: _homeState.value.todayKey.ifBlank {
            SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Calendar.getInstance().time)
        }
        viewModelScope.launch {
            prefsRepo.updateStudyProgress(
                groupId = room.activeGroupId,
                joinedGroupIds = room.joinedGroupIds,
                subject = room.currentSubject,
                todayDate = today,
                todaySeconds = room.todayStudySeconds
            )
        }
    }

    // --- Home Study Dashboard Actions ---

    fun startSubjectStudyAndOpenLiveRoom(subjectId: String): String {
        val current = _studyDashboardState.value
        val targetSubj = current.subjects.firstOrNull { it.id == subjectId }
            ?: current.subjects.firstOrNull()
        val subjectName = targetSubj?.name ?: _studyRoomState.value.currentSubject

        _studyDashboardState.update {
            val resetCountdown = if (it.focusCountdownSeconds <= 0 && it.focusMode != FocusSessionMode.STOPWATCH) {
                it.focusMode.durationSeconds
            } else {
                it.focusCountdownSeconds
            }
            it.copy(
                activeSubjectId = targetSubj?.id,
                focusSubjectId = targetSubj?.id ?: it.focusSubjectId,
                isFocusBreakPhase = false,
                focusCountdownSeconds = resetCountdown
            )
        }

        val targetGroupId = _studyRoomState.value.activeGroupId.ifBlank {
            _studyRoomState.value.joinedGroupIds.firstOrNull()
                ?: StudyGroupsData.groups.first().id
        }

        _studyRoomState.update { room ->
            room.copy(
                activeGroupId = targetGroupId,
                joinedGroupIds = room.joinedGroupIds + targetGroupId,
                currentSubject = subjectName,
                isStudying = true,
                currentSessionSeconds = if (room.isStudying) room.currentSessionSeconds else 0L
            )
        }

        persistDashboardSubjects()
        persistCurrentStudyProgress()
        return targetGroupId
    }

    fun toggleSubjectTimer(subjectId: String) {
        val current = _studyDashboardState.value
        val nextActiveId = if (current.activeSubjectId == subjectId) null else subjectId
        val targetSubjectName = current.subjects.firstOrNull { it.id == subjectId }?.name
        _studyDashboardState.update {
            val resetCountdown = if (nextActiveId != null && it.focusCountdownSeconds <= 0 && it.focusMode != FocusSessionMode.STOPWATCH) {
                it.focusMode.durationSeconds
            } else {
                it.focusCountdownSeconds
            }
            it.copy(
                activeSubjectId = nextActiveId,
                focusSubjectId = if (nextActiveId != null) nextActiveId else it.focusSubjectId,
                focusCountdownSeconds = resetCountdown
            )
        }
        _studyRoomState.update {
            it.copy(
                isStudying = nextActiveId != null,
                currentSubject = if (nextActiveId != null && !targetSubjectName.isNullOrBlank()) targetSubjectName else it.currentSubject
            )
        }
        persistDashboardSubjects()
        persistCurrentStudyProgress()
    }

    fun toggleFocusButton() {
        val current = _studyDashboardState.value
        if (current.activeSubjectId != null) {
            _studyDashboardState.update { it.copy(activeSubjectId = null) }
            _studyRoomState.update { it.copy(isStudying = false) }
            persistDashboardSubjects()
            persistCurrentStudyProgress()
        } else {
            val targetId = current.subjects.firstOrNull { it.id == current.focusSubjectId }?.id
                ?: current.subjects.firstOrNull()?.id
                ?: return
            toggleSubjectTimer(targetId)
        }
    }

    fun startFocusIfIdle() {
        val current = _studyDashboardState.value
        if (current.activeSubjectId != null) return
        val targetId = current.subjects.firstOrNull { it.id == current.focusSubjectId }?.id
            ?: current.subjects.firstOrNull()?.id
            ?: return
        toggleSubjectTimer(targetId)
    }

    fun selectFocusSubject(subjectId: String) {
        val target = _studyDashboardState.value.subjects.firstOrNull { it.id == subjectId } ?: return
        _studyDashboardState.update { dash ->
            dash.copy(
                focusSubjectId = target.id,
                activeSubjectId = if (dash.activeSubjectId != null) target.id else null
            )
        }
        _studyRoomState.update { it.copy(currentSubject = target.name) }
        persistCurrentStudyProgress()
    }

    fun resetFocusSession() {
        _studyDashboardState.update { dash ->
            dash.copy(
                activeSubjectId = null,
                focusSessionElapsedSeconds = 0L,
                isFocusBreakPhase = false,
                focusCountdownSeconds = dash.focusMode.durationSeconds
            )
        }
        _studyRoomState.update { it.copy(isStudying = false) }
        persistDashboardSubjects()
        persistCurrentStudyProgress()
    }

    fun skipBreakToFocus() {
        _studyDashboardState.update { dash ->
            dash.copy(
                isFocusBreakPhase = false,
                focusCountdownSeconds = dash.focusMode.durationSeconds
            )
        }
    }

    fun toggleKeepScreenOnInFocus() {
        _studyDashboardState.update { it.copy(keepScreenOnInFocus = !it.keepScreenOnInFocus) }
    }

    fun toggleAmbientSoundInFocus() {
        _studyDashboardState.update { it.copy(isAmbientSoundOn = !it.isAmbientSoundOn) }
    }

    fun openLiveRoomForCurrentFocus(): String {
        val dash = _studyDashboardState.value
        val targetSubj = dash.subjects.firstOrNull { it.id == (dash.activeSubjectId ?: dash.focusSubjectId) }
            ?: dash.subjects.firstOrNull()
        val targetGroupId = _studyRoomState.value.activeGroupId.ifBlank {
            _studyRoomState.value.joinedGroupIds.firstOrNull() ?: StudyGroupsData.groups.first().id
        }
        if (targetSubj != null) {
            _studyRoomState.update {
                it.copy(
                    activeGroupId = targetGroupId,
                    joinedGroupIds = it.joinedGroupIds + targetGroupId,
                    currentSubject = targetSubj.name,
                    isStudying = dash.activeSubjectId != null && !dash.isFocusBreakPhase
                )
            }
            persistCurrentStudyProgress()
        }
        return targetGroupId
    }

    fun addStudySubject(name: String, colorHex: Long, goalHours: Double) {
        val clean = name.trim()
        if (clean.isEmpty()) return
        val newItem = StudySubjectItem(
            id = "subj_${System.currentTimeMillis()}",
            name = clean,
            colorHex = colorHex,
            todaySeconds = 0L,
            goalHours = goalHours.coerceIn(0.5, 12.0)
        )
        _studyDashboardState.update { it.copy(subjects = it.subjects + newItem) }
        persistDashboardSubjects()
    }

    fun updateSubjectGoal(subjectId: String, goalHours: Double) {
        _studyDashboardState.update { dash ->
            dash.copy(
                subjects = dash.subjects.map {
                    if (it.id == subjectId) it.copy(goalHours = goalHours.coerceIn(0.5, 12.0)) else it
                }
            )
        }
        persistDashboardSubjects()
    }

    fun resetSubjectTimer(subjectId: String) {
        _studyDashboardState.update { dash ->
            dash.copy(
                activeSubjectId = if (dash.activeSubjectId == subjectId) null else dash.activeSubjectId,
                subjects = dash.subjects.map {
                    if (it.id == subjectId) it.copy(todaySeconds = 0L) else it
                }
            )
        }
        persistDashboardSubjects()
    }

    fun deleteStudySubject(subjectId: String) {
        _studyDashboardState.update { dash ->
            if (dash.subjects.size <= 1) return@update dash
            dash.copy(
                activeSubjectId = if (dash.activeSubjectId == subjectId) null else dash.activeSubjectId,
                subjects = dash.subjects.filterNot { it.id == subjectId }
            )
        }
        persistDashboardSubjects()
    }

    fun addTodoItem(title: String) {
        val clean = title.trim()
        if (clean.isEmpty()) return
        val newItem = StudyTodoItem(
            id = "todo_${System.currentTimeMillis()}",
            title = clean,
            isDone = false
        )
        _studyDashboardState.update { it.copy(todos = it.todos + newItem) }
        persistDashboardTodos()
    }

    fun toggleTodoItem(todoId: String) {
        _studyDashboardState.update { dash ->
            dash.copy(
                todos = dash.todos.map {
                    if (it.id == todoId) it.copy(isDone = !it.isDone) else it
                }
            )
        }
        persistDashboardTodos()
    }

    fun deleteTodoItem(todoId: String) {
        _studyDashboardState.update { dash ->
            dash.copy(todos = dash.todos.filterNot { it.id == todoId })
        }
        persistDashboardTodos()
    }

    fun updateFocusGoalHours(hours: Int) {
        val safe = hours.coerceIn(1, 16)
        _studyDashboardState.update { it.copy(dailyGoalHours = safe) }
        viewModelScope.launch { prefsRepo.updateDashboardSettings(goalHours = safe) }
    }

    fun updateStudyBatchTag(tag: String) {
        val clean = tag.trim().ifEmpty { "HSC-26" }
        _studyDashboardState.update { it.copy(batchTag = clean) }
        viewModelScope.launch { prefsRepo.updateDashboardSettings(batchTag = clean) }
    }

    fun selectFocusSessionMode(mode: FocusSessionMode) {
        _studyDashboardState.update {
            it.copy(
                focusMode = mode,
                isFocusBreakPhase = false,
                focusSessionElapsedSeconds = 0L,
                focusCountdownSeconds = mode.durationSeconds
            )
        }
        viewModelScope.launch { prefsRepo.updateDashboardSettings(sessionModeId = mode.name) }
    }

    fun selectWeekDayIndex(index: Int) {
        _studyDashboardState.update {
            val next = if (it.selectedWeekDayIdx == index) -1 else index
            it.copy(selectedWeekDayIdx = next)
        }
    }

    private fun persistDashboardSubjects(todayOverride: String? = null) {
        val dash = _studyDashboardState.value
        val today = todayOverride ?: _homeState.value.todayKey.ifBlank {
            SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Calendar.getInstance().time)
        }
        viewModelScope.launch {
            prefsRepo.saveStudySubjects(
                subjects = dash.subjects,
                todayDate = today,
                todaySeconds = dash.totalTodaySeconds
            )
        }
    }

    private fun persistDashboardTodos() {
        val todos = _studyDashboardState.value.todos
        viewModelScope.launch {
            prefsRepo.saveStudyTodos(todos)
        }
    }

    // --- Syllabus & Chapter Preparation Tracker Actions ---

    fun toggleTrackerTag(subjectId: String, chapterId: String, tag: PrepQuestionTag) {
        _trackerSubjects.update { list ->
            list.map { subj ->
                if (subj.id != subjectId) subj
                else subj.copy(
                    chapters = subj.chapters.map { ch ->
                        if (ch.id != chapterId) ch
                        else {
                            val nextTags = if (tag in ch.completedTags) ch.completedTags - tag else ch.completedTags + tag
                            ch.copy(completedTags = nextTags)
                        }
                    }
                )
            }
        }
        persistTrackerSubjects()
    }

    fun toggleAllTagsInChapter(subjectId: String, chapterId: String) {
        _trackerSubjects.update { list ->
            list.map { subj ->
                if (subj.id != subjectId) subj
                else subj.copy(
                    chapters = subj.chapters.map { ch ->
                        if (ch.id != chapterId) ch
                        else {
                            val allDone = ch.completedTags.size == PrepQuestionTag.entries.size
                            ch.copy(completedTags = if (allDone) emptySet() else PrepQuestionTag.entries.toSet())
                        }
                    }
                )
            }
        }
        persistTrackerSubjects()
    }

    fun addTrackerChapter(subjectId: String, paper: Int, chapterTitle: String) {
        val clean = chapterTitle.trim()
        if (clean.isEmpty()) return
        val normalizedPaper = paper.coerceIn(1, 2)
        val newChapter = TrackerChapter(
            id = "trk_ch_${System.currentTimeMillis()}",
            title = clean,
            paper = normalizedPaper,
            completedTags = emptySet()
        )
        _trackerSubjects.update { list ->
            list.map { subj ->
                if (subj.id == subjectId) subj.copy(chapters = subj.chapters + newChapter) else subj
            }
        }
        persistTrackerSubjects()
    }

    fun deleteTrackerChapter(subjectId: String, chapterId: String) {
        _trackerSubjects.update { list ->
            list.map { subj ->
                if (subj.id != subjectId || subj.chapters.size <= 1) subj
                else subj.copy(chapters = subj.chapters.filterNot { it.id == chapterId })
            }
        }
        persistTrackerSubjects()
    }

    fun addTrackerSubject(name: String, colorHex: Long, firstChapterTitle: String) {
        val cleanName = name.trim()
        if (cleanName.isEmpty()) return
        val chTitle = firstChapterTitle.trim().ifEmpty { "Chapter 1" }
        val newSubjId = "trk_subj_${System.currentTimeMillis()}"
        val newSubject = TrackerSubject(
            id = newSubjId,
            nameEn = cleanName,
            nameBn = cleanName,
            colorHex = colorHex,
            chapters = listOf(
                TrackerChapter(
                    id = "${newSubjId}_p1_ch_1",
                    title = chTitle,
                    paper = 1,
                    completedTags = emptySet()
                )
            )
        )
        _trackerSubjects.update { it + newSubject }
        persistTrackerSubjects()
    }

    fun deleteTrackerSubject(subjectId: String) {
        _trackerSubjects.update { list ->
            if (list.size <= 1) list else list.filterNot { it.id == subjectId }
        }
        persistTrackerSubjects()
    }

    private fun persistTrackerSubjects() {
        val snapshot = _trackerSubjects.value
        viewModelScope.launch {
            prefsRepo.saveTrackerSubjects(snapshot)
        }
    }

    // --- Exam Center Actions ---

    fun startChapterExam(subject: TrackerSubject, chapter: TrackerChapter) {
        val loadedQuestions = ExamQuestionBank.getQuestionsForChapter(
            subjectId = subject.id,
            chapterId = chapter.id,
            chapterTitle = chapter.title,
            paper = chapter.paper
        )
        isExamTimerRunning = true
        _examState.value = ExamUiState(
            activeSubjectId = subject.id,
            activeSubjectNameEn = subject.nameEn,
            activeSubjectNameBn = subject.nameBn,
            activeChapterId = chapter.id,
            activeChapterTitle = chapter.title,
            activePaper = chapter.paper,
            isWrongAnswersExam = false,
            questions = loadedQuestions,
            selectedAnswers = emptyMap(),
            stage = ExamPaperStage.TAKING,
            elapsedSeconds = 0L,
            correctCount = 0,
            wrongCount = 0,
            skippedCount = 0
        )
    }

    fun startWrongAnswersExam(): Boolean {
        val wrongIds = prefsState.value.wrongExamQuestionIds
        val bankQuestions = ExamQuestionBank.getQuestionsByIds(wrongIds)
        val customLookup = (_userCreatedExams.value + ExamGroundDefaults.defaultCommunityExams)
            .flatMap { it.questions }
            .associateBy { it.id }
        val customWrong = wrongIds.mapNotNull { customLookup[it] }
        val loadedQuestions = (bankQuestions + customWrong).distinctBy { it.id }
        if (loadedQuestions.isEmpty()) return false
        isExamTimerRunning = true
        _examState.value = ExamUiState(
            activeSubjectId = "wrong_ans",
            activeSubjectNameEn = "Wrong Answers Review",
            activeSubjectNameBn = "ভুল উত্তরের পরীক্ষা",
            activeChapterId = "wrong_ans_queue",
            activeChapterTitle = "${loadedQuestions.size} Missed Questions",
            activePaper = 1,
            isWrongAnswersExam = true,
            questions = loadedQuestions,
            selectedAnswers = emptyMap(),
            stage = ExamPaperStage.TAKING,
            elapsedSeconds = 0L,
            correctCount = 0,
            wrongCount = 0,
            skippedCount = 0
        )
        return true
    }

    fun startCustomExam(exam: CustomPublishedExam) {
        if (exam.questions.isEmpty()) return
        // Increment attempt count if it's a user-created exam
        val updated = _userCreatedExams.value.map {
            if (it.id == exam.id) it.copy(attemptCount = it.attemptCount + 1) else it
        }
        if (updated != _userCreatedExams.value) {
            _userCreatedExams.value = updated
            persistCustomExams()
        }
        isExamTimerRunning = true
        _examState.value = ExamUiState(
            activeSubjectId = exam.subjectId,
            activeSubjectNameEn = exam.subjectName,
            activeSubjectNameBn = exam.subjectName,
            activeChapterId = "custom_exam_${exam.id}",
            activeChapterTitle = exam.title,
            activePaper = exam.paper,
            paperStyle = exam.paperStyle,
            isWrongAnswersExam = false,
            questions = exam.questions,
            selectedAnswers = emptyMap(),
            stage = ExamPaperStage.TAKING,
            elapsedSeconds = 0L,
            correctCount = 0,
            wrongCount = 0,
            skippedCount = 0
        )
    }

    fun setExamPaperStyle(style: ExamPaperStyle) {
        _examState.update { it.copy(paperStyle = style) }
    }

    fun saveOrPublishCustomExam(exam: CustomPublishedExam) {
        _userCreatedExams.update { current ->
            val exists = current.any { it.id == exam.id }
            if (exists) {
                current.map { if (it.id == exam.id) exam else it }
            } else {
                listOf(exam) + current
            }
        }
        persistCustomExams()
    }

    fun toggleCustomExamPublished(examId: String) {
        _userCreatedExams.update { current ->
            current.map { if (it.id == examId) it.copy(isPublished = !it.isPublished) else it }
        }
        persistCustomExams()
    }

    fun deleteCustomExam(examId: String) {
        _userCreatedExams.update { current ->
            current.filterNot { it.id == examId }
        }
        persistCustomExams()
    }

    private fun persistCustomExams() {
        val snapshot = _userCreatedExams.value
        viewModelScope.launch {
            prefsRepo.saveCustomExams(snapshot)
        }
    }

    fun selectExamOption(questionId: String, optionIndex: Int) {
        _examState.update { state ->
            if (state.stage != ExamPaperStage.TAKING) return@update state
            state.copy(selectedAnswers = state.selectedAnswers + (questionId to optionIndex))
        }
    }

    fun submitExam() {
        val current = _examState.value
        if (current.questions.isEmpty()) return
        isExamTimerRunning = false

        var correct = 0
        var wrong = 0
        var skipped = 0
        val newlyWrongIds = mutableSetOf<String>()
        val newlyCorrectIds = mutableSetOf<String>()

        current.questions.forEach { q ->
            val picked = current.selectedAnswers[q.id]
            when {
                picked == null -> {
                    skipped++
                    newlyWrongIds.add(q.id)
                }
                picked == q.correctIndex -> {
                    correct++
                    newlyCorrectIds.add(q.id)
                }
                else -> {
                    wrong++
                    newlyWrongIds.add(q.id)
                }
            }
        }

        _examState.update {
            it.copy(
                stage = ExamPaperStage.RESULT,
                correctCount = correct,
                wrongCount = wrong,
                skippedCount = skipped
            )
        }

        val chapterKey = if (current.isWrongAnswersExam) null else current.activeChapterId
        viewModelScope.launch {
            prefsRepo.recordExamResult(
                chapterId = chapterKey,
                score = correct,
                newlyWrongIds = newlyWrongIds,
                newlyCorrectIds = newlyCorrectIds
            )
        }
    }

    fun showExamAnswers() {
        _examState.update { it.copy(stage = ExamPaperStage.REVIEW_ANSWERS) }
    }

    fun showExamResultPaper() {
        _examState.update { it.copy(stage = ExamPaperStage.RESULT) }
    }

    fun retakeExam() {
        isExamTimerRunning = true
        _examState.update {
            it.copy(
                selectedAnswers = emptyMap(),
                stage = ExamPaperStage.TAKING,
                elapsedSeconds = 0L,
                correctCount = 0,
                wrongCount = 0,
                skippedCount = 0
            )
        }
    }

    fun stopExamTimer() {
        isExamTimerRunning = false
    }

    fun updateStoryLastReadPage(bookId: String, pageIndex: Int) {
        viewModelScope.launch {
            prefsRepo.updateStoryLastReadPage(bookId, pageIndex)
        }
    }

    fun toggleStoryBookmarkPage(bookId: String, pageIndex: Int) {
        viewModelScope.launch {
            prefsRepo.toggleStoryBookmarkPage(bookId, pageIndex)
        }
    }

    // --- 24h 4-Hour Mission Planner & Widget / Alarm Sync ---

    fun saveMission(item: MissionItem, repeatNext7Days: Boolean = false) {
        val toAddOrUpdate = mutableListOf(item)
        if (repeatNext7Days) {
            val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val cal = Calendar.getInstance()
            try {
                fmt.parse(item.dateKey)?.let { cal.time = it }
            } catch (_: Exception) {
            }
            for (dayOffset in 1..7) {
                cal.add(Calendar.DAY_OF_YEAR, 1)
                val targetDate = fmt.format(cal.time)
                toAddOrUpdate.add(
                    item.copy(
                        id = "${item.id}_d$dayOffset",
                        dateKey = targetDate,
                        isCompleted = false,
                        updatedAtMillis = System.currentTimeMillis()
                    )
                )
            }
        }

        _missions.update { current ->
            val updated = current.toMutableList()
            toAddOrUpdate.forEach { incoming ->
                val idx = updated.indexOfFirst { it.id == incoming.id }
                if (idx >= 0) {
                    updated[idx] = incoming
                } else {
                    updated.add(incoming)
                }
            }
            updated.sortedWith(compareBy<MissionItem> { it.dateKey }.thenBy { it.blockIndex })
        }
        persistAndSyncMissions()
    }

    fun toggleMissionCompleted(missionId: String) {
        _missions.update { current ->
            current.map { m ->
                if (m.id == missionId) {
                    val nextDone = !m.isCompleted
                    val nextCompletedTags = if (nextDone && m.targetTags.isNotEmpty()) {
                        m.targetTags
                    } else if (!nextDone && m.completedTags.containsAll(m.targetTags)) {
                        emptySet()
                    } else {
                        m.completedTags
                    }
                    m.copy(
                        isCompleted = nextDone,
                        completedTags = nextCompletedTags,
                        updatedAtMillis = System.currentTimeMillis()
                    )
                } else {
                    m
                }
            }
        }
        persistAndSyncMissions()
    }

    fun toggleMissionPrepTag(missionId: String, tag: PrepQuestionTag) {
        _missions.update { current ->
            current.map { m ->
                if (m.id != missionId) {
                    m
                } else {
                    val nextTargets = if (tag in m.targetTags) m.targetTags else m.targetTags + tag
                    val nextDoneTags = if (tag in m.completedTags) m.completedTags - tag else m.completedTags + tag
                    val allDone = nextTargets.isNotEmpty() && nextDoneTags.containsAll(nextTargets)
                    m.copy(
                        targetTags = nextTargets,
                        completedTags = nextDoneTags,
                        isCompleted = allDone,
                        updatedAtMillis = System.currentTimeMillis()
                    )
                }
            }
        }
        persistAndSyncMissions()
    }

    fun toggleMissionAlarm(missionId: String) {
        _missions.update { current ->
            current.map { m ->
                if (m.id == missionId) {
                    m.copy(alarmEnabled = !m.alarmEnabled, updatedAtMillis = System.currentTimeMillis())
                } else {
                    m
                }
            }
        }
        persistAndSyncMissions()
    }

    fun deleteMission(missionId: String) {
        _missions.update { current ->
            current.filterNot { it.id == missionId }
        }
        persistAndSyncMissions()
    }

    fun setMissionLiveNotifEnabled(enabled: Boolean) {
        _missionLiveNotifEnabled.value = enabled
        val ctx = getApplication<Application>().applicationContext
        val snapshot = _missions.value
        viewModelScope.launch {
            prefsRepo.setMissionLiveNotifEnabled(enabled, snapshot)
        }
        MissionScheduler.syncAll(ctx, snapshot, enabled)
    }

    fun copyDayMissionsToNext7Days(sourceDateKey: String) {
        val sourceMissions = _missions.value.filter { it.dateKey == sourceDateKey }
        if (sourceMissions.isEmpty()) return

        val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val cal = Calendar.getInstance()
        try {
            fmt.parse(sourceDateKey)?.let { cal.time = it }
        } catch (_: Exception) {
        }

        val generated = mutableListOf<MissionItem>()
        for (dayOffset in 1..7) {
            cal.add(Calendar.DAY_OF_YEAR, 1)
            val targetDate = fmt.format(cal.time)
            sourceMissions.forEach { src ->
                generated.add(
                    src.copy(
                        id = "msn_cp_${targetDate}_${src.blockIndex}_${src.id.hashCode() and 0xFFF}",
                        dateKey = targetDate,
                        isCompleted = false,
                        updatedAtMillis = System.currentTimeMillis()
                    )
                )
            }
        }

        _missions.update { current ->
            val targetDates = generated.map { it.dateKey }.toSet()
            val preserved = current.filterNot { it.dateKey in targetDates }
            (preserved + generated).sortedWith(compareBy<MissionItem> { it.dateKey }.thenBy { it.blockIndex })
        }
        persistAndSyncMissions()
    }

    fun syncMissionWidgetAndNotifications() {
        val ctx = getApplication<Application>().applicationContext
        MissionScheduler.syncAll(ctx, _missions.value, _missionLiveNotifEnabled.value)
        MissionWidgetProvider.updateAllWidgets(ctx)
    }

    private fun persistAndSyncMissions() {
        val snapshot = _missions.value
        val liveEnabled = _missionLiveNotifEnabled.value
        val ctx = getApplication<Application>().applicationContext
        viewModelScope.launch {
            prefsRepo.saveMissions(snapshot, liveEnabled)
        }
        MissionScheduler.syncAll(ctx, snapshot, liveEnabled)
    }
}
