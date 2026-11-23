package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.local.ExamGroundDefaults
import com.example.ui.screens.CreateExamScreen
import com.example.ui.screens.DuaScreen
import com.example.ui.screens.DzikirScreen
import com.example.ui.screens.ExamAccountScreen
import com.example.ui.screens.ExamGroundScreen
import com.example.ui.screens.ExamHscSubjectsScreen
import com.example.ui.screens.ExamHubScreen
import com.example.ui.screens.ExamPaperScreen
import com.example.ui.screens.FocusScreen
import com.example.ui.screens.GroupStudyRoomScreen
import com.example.ui.screens.GroupsScreen
import com.example.notifications.MissionScheduler
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MissionScreen
import com.example.ui.screens.MoreScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.QuranListScreen
import com.example.ui.screens.StoryBookReaderScreen
import com.example.ui.screens.StoryHomeScreen
import com.example.ui.screens.SurahDetailScreen
import com.example.ui.screens.TrackerScreen
import com.example.ui.theme.BanglaStrings
import com.example.ui.theme.EnglishStrings
import com.example.ui.theme.LocalAppStrings
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.MainViewModel

object AppRoutes {
    const val ROUTE_HOME = "home"
    const val ROUTE_QURAN = "quran"
    const val ROUTE_SURAH_DETAIL = "surah/{surahNumber}"
    const val ROUTE_DUA = "dua"
    const val ROUTE_DZIKIR = "dzikir"
    const val ROUTE_GROUPS = "groups"
    const val ROUTE_MY_GROUPS = "my_groups"
    const val ROUTE_GROUP_ROOM = "group_room/{groupId}"
    const val ROUTE_FOCUS = "focus"
    const val ROUTE_TRACKER = "tracker"
    const val ROUTE_EXAM = "exam"
    const val ROUTE_EXAM_HSC = "exam_hsc"
    const val ROUTE_EXAM_PAPER = "exam_paper"
    const val ROUTE_CREATE_EXAM = "create_exam?examId={examId}"
    const val ROUTE_EXAM_ACCOUNT = "exam_account"
    const val ROUTE_EXAM_GROUND = "exam_ground"
    const val ROUTE_STORY = "story"
    const val ROUTE_STORY_READER = "story_reader/{bookId}?page={page}"
    const val ROUTE_MISSION = "mission"
    const val ROUTE_MORE = "more"
    const val ROUTE_PROFILE = "profile"

    fun surahDetail(surahNumber: Int) = "surah/$surahNumber"
    fun groupRoom(groupId: String) = "group_room/$groupId"
    fun createExam(examId: String? = null) =
        if (examId.isNullOrBlank()) "create_exam" else "create_exam?examId=$examId"
    fun storyReader(bookId: String, pageIndex: Int = 0) = "story_reader/$bookId?page=$pageIndex"
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme(darkTheme = false) {
                val navController = rememberNavController()
                val viewModel: MainViewModel = viewModel()
                val homeState by viewModel.homeState.collectAsStateWithLifecycle()
                val prefs by viewModel.prefsState.collectAsStateWithLifecycle()
                val surahLoadState by viewModel.surahLoadState.collectAsStateWithLifecycle()
                val studyRoomState by viewModel.studyRoomState.collectAsStateWithLifecycle()
                val studyDashboardState by viewModel.studyDashboardState.collectAsStateWithLifecycle()
                val trackerSubjects by viewModel.trackerSubjects.collectAsStateWithLifecycle()
                val examState by viewModel.examState.collectAsStateWithLifecycle()
                val userCreatedExams by viewModel.userCreatedExams.collectAsStateWithLifecycle()
                val missions by viewModel.missions.collectAsStateWithLifecycle()
                val missionLiveNotifEnabled by viewModel.missionLiveNotifEnabled.collectAsStateWithLifecycle()
                val strings = if (prefs.appLanguage == "bn") BanglaStrings else EnglishStrings

                val initialOpenRoute = intent?.getStringExtra(MissionScheduler.EXTRA_OPEN_ROUTE)
                androidx.compose.runtime.LaunchedEffect(initialOpenRoute) {
                    if (initialOpenRoute == MissionScheduler.ROUTE_MISSION_TARGET) {
                        intent?.removeExtra(MissionScheduler.EXTRA_OPEN_ROUTE)
                        navController.navigate(AppRoutes.ROUTE_MISSION) {
                            launchSingleTop = true
                        }
                    }
                }

                val liveStudyingPeers = studyRoomState.groups
                    .firstOrNull { it.id == studyRoomState.activeGroupId }
                    ?.members
                    ?.count { it.status == com.example.data.model.StudyStatus.STUDYING }
                    ?: 4
                val myGroupsCount = studyRoomState.groups
                    .count { it.id in studyRoomState.joinedGroupIds }

                CompositionLocalProvider(LocalAppStrings provides strings) {
                    NavHost(
                        navController = navController,
                        startDestination = AppRoutes.ROUTE_HOME,
                        enterTransition = { fadeIn() },
                        exitTransition = { fadeOut() }
                    ) {
                        composable(AppRoutes.ROUTE_HOME) {
                            HomeScreen(
                                homeState = homeState,
                                prefs = prefs,
                                onOpenProfile = { navController.navigate(AppRoutes.ROUTE_PROFILE) },
                                onOpenQuran = { navController.navigate(AppRoutes.ROUTE_QURAN) },
                                onOpenSurah = { num -> navController.navigate(AppRoutes.surahDetail(num)) },
                                onOpenDua = { navController.navigate(AppRoutes.ROUTE_DUA) },
                                onOpenDzikir = { navController.navigate(AppRoutes.ROUTE_DZIKIR) },
                                onOpenGroups = { navController.navigate(AppRoutes.ROUTE_GROUPS) },
                                onOpenTracker = { navController.navigate(AppRoutes.ROUTE_TRACKER) },
                                onOpenExam = { navController.navigate(AppRoutes.ROUTE_EXAM) },
                                onOpenStory = { navController.navigate(AppRoutes.ROUTE_STORY) },
                                onOpenMission = { navController.navigate(AppRoutes.ROUTE_MISSION) },
                                onOpenMore = { navController.navigate(AppRoutes.ROUTE_MORE) },
                                onCycleDailyAyah = viewModel::cycleDailyAyah,
                                onToggleAyahBookmark = viewModel::toggleAyahBookmark,
                                onToggleAdzanMaster = viewModel::setAdzanNotificationsEnabled,
                                onToggleAdzanPrayer = viewModel::toggleAdzanForPrayer,
                                onTogglePrayerDone = viewModel::togglePrayerCompletedToday,
                                studyDashboard = studyDashboardState,
                                myGroupsCount = myGroupsCount,
                                liveStudyingCount = liveStudyingPeers,
                                onToggleSubjectTimer = viewModel::toggleSubjectTimer,
                                onStartSubjectAndOpenRoom = { subjectId ->
                                    val targetGroupId = viewModel.startSubjectStudyAndOpenLiveRoom(subjectId)
                                    navController.navigate(AppRoutes.groupRoom(targetGroupId))
                                },
                                onOpenFocusMode = { navController.navigate(AppRoutes.ROUTE_FOCUS) },
                                onStartFocusAndOpen = {
                                    viewModel.startFocusIfIdle()
                                    navController.navigate(AppRoutes.ROUTE_FOCUS)
                                },
                                onToggleFocusButton = viewModel::toggleFocusButton,
                                onOpenMyGroups = { navController.navigate(AppRoutes.ROUTE_MY_GROUPS) },
                                onAddSubject = viewModel::addStudySubject,
                                onUpdateSubjectGoal = viewModel::updateSubjectGoal,
                                onResetSubjectTimer = viewModel::resetSubjectTimer,
                                onDeleteSubject = viewModel::deleteStudySubject,
                                onAddTodo = viewModel::addTodoItem,
                                onToggleTodo = viewModel::toggleTodoItem,
                                onDeleteTodo = viewModel::deleteTodoItem,
                                onUpdateFocusGoalHours = viewModel::updateFocusGoalHours,
                                onUpdateBatchTag = viewModel::updateStudyBatchTag,
                                onSelectFocusMode = viewModel::selectFocusSessionMode,
                                onSelectWeekDayIndex = viewModel::selectWeekDayIndex
                            )
                        }

                        composable(AppRoutes.ROUTE_QURAN) {
                            QuranListScreen(
                                bookmarkedAyahKeys = prefs.bookmarkedAyahKeys,
                                onBack = { navController.popBackStack() },
                                onSelectSurah = { num -> navController.navigate(AppRoutes.surahDetail(num)) }
                            )
                        }

                        composable(
                            route = AppRoutes.ROUTE_SURAH_DETAIL,
                            arguments = listOf(navArgument("surahNumber") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val surahNum = backStackEntry.arguments?.getInt("surahNumber") ?: 1
                            SurahDetailScreen(
                                surahNumber = surahNum,
                                loadState = surahLoadState,
                                bookmarkedAyahKeys = prefs.bookmarkedAyahKeys,
                                fontSizeSp = prefs.quranFontSizeSp,
                                translationLang = prefs.quranTranslationLang,
                                onBack = { navController.popBackStack() },
                                onLoadSurah = viewModel::loadSurah,
                                onToggleBookmark = viewModel::toggleAyahBookmark,
                                onChangeFontSize = viewModel::updateQuranFontSize,
                                onChangeTranslationLang = viewModel::updateQuranTranslationLang
                            )
                        }

                        composable(AppRoutes.ROUTE_DUA) {
                            DuaScreen(
                                favoriteDuaIds = prefs.favoriteDuaIds,
                                onBack = { navController.popBackStack() },
                                onToggleFavorite = viewModel::toggleDuaFavorite
                            )
                        }

                        composable(AppRoutes.ROUTE_DZIKIR) {
                            DzikirScreen(
                                prefs = prefs,
                                currentPreset = viewModel.getCurrentDzikirPreset(),
                                onBack = { navController.popBackStack() },
                                onIncrement = viewModel::incrementDzikir,
                                onReset = viewModel::resetDzikirCount,
                                onSelectPreset = viewModel::selectDzikirPreset,
                                onUpdateTarget = viewModel::updateDzikirTarget
                            )
                        }

                        composable(AppRoutes.ROUTE_GROUPS) {
                            GroupsScreen(
                                studyState = studyRoomState,
                                initialMyGroupsOnly = false,
                                onBack = { navController.popBackStack() },
                                onEnterGroupRoom = { groupId, subject ->
                                    viewModel.enterGroupRoom(groupId, subject)
                                    navController.navigate(AppRoutes.groupRoom(groupId))
                                }
                            )
                        }

                        composable(AppRoutes.ROUTE_MY_GROUPS) {
                            GroupsScreen(
                                studyState = studyRoomState,
                                initialMyGroupsOnly = true,
                                onBack = { navController.popBackStack() },
                                onEnterGroupRoom = { groupId, subject ->
                                    viewModel.enterGroupRoom(groupId, subject)
                                    navController.navigate(AppRoutes.groupRoom(groupId))
                                }
                            )
                        }

                        composable(
                            route = AppRoutes.ROUTE_GROUP_ROOM,
                            arguments = listOf(navArgument("groupId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val groupId = backStackEntry.arguments?.getString("groupId").orEmpty()
                            GroupStudyRoomScreen(
                                groupId = groupId,
                                userName = prefs.userName,
                                studyState = studyRoomState,
                                onBack = { navController.popBackStack() },
                                onToggleStudying = viewModel::toggleStudying,
                                onSelectSubject = viewModel::updateStudySubject,
                                onCheerMember = viewModel::cheerMember,
                                onLeaveGroup = {
                                    viewModel.leaveGroupRoom(studyRoomState.activeGroupId.ifBlank { groupId })
                                    navController.popBackStack()
                                },
                                onSwitchGroup = viewModel::switchActiveGroupInRoom,
                                onBrowseAllGroups = { navController.navigate(AppRoutes.ROUTE_GROUPS) }
                            )
                        }

                        composable(AppRoutes.ROUTE_FOCUS) {
                            FocusScreen(
                                dashboard = studyDashboardState,
                                onBack = { navController.popBackStack() },
                                onTogglePlayPause = viewModel::toggleFocusButton,
                                onResetSession = viewModel::resetFocusSession,
                                onSelectMode = viewModel::selectFocusSessionMode,
                                onSelectSubject = viewModel::selectFocusSubject,
                                onAddSubject = viewModel::addStudySubject,
                                onSkipBreak = viewModel::skipBreakToFocus,
                                onToggleKeepScreenOn = viewModel::toggleKeepScreenOnInFocus,
                                onToggleAmbientSound = viewModel::toggleAmbientSoundInFocus,
                                onOpenLiveRoom = {
                                    val targetGroupId = viewModel.openLiveRoomForCurrentFocus()
                                    navController.navigate(AppRoutes.groupRoom(targetGroupId))
                                }
                            )
                        }

                        composable(AppRoutes.ROUTE_TRACKER) {
                            TrackerScreen(
                                subjects = trackerSubjects,
                                onBack = { navController.popBackStack() },
                                onToggleTag = viewModel::toggleTrackerTag,
                                onToggleAllInChapter = viewModel::toggleAllTagsInChapter,
                                onAddChapter = viewModel::addTrackerChapter,
                                onDeleteChapter = viewModel::deleteTrackerChapter,
                                onAddSubject = viewModel::addTrackerSubject
                            )
                        }

                        composable(AppRoutes.ROUTE_EXAM) {
                            val publishedCount = userCreatedExams.count { it.isPublished } +
                                ExamGroundDefaults.defaultCommunityExams.size
                            ExamHubScreen(
                                trackerSubjects = trackerSubjects,
                                examBestScores = prefs.examBestScores,
                                wrongQuestionIds = prefs.wrongExamQuestionIds,
                                totalExamsTaken = prefs.totalExamsTaken,
                                publishedGroundCount = publishedCount,
                                userCreatedExamsCount = userCreatedExams.size,
                                onBack = { navController.popBackStack() },
                                onOpenHscSubjects = { navController.navigate(AppRoutes.ROUTE_EXAM_HSC) },
                                onOpenTracker = { navController.navigate(AppRoutes.ROUTE_TRACKER) },
                                onStartWrongAnswersExam = {
                                    if (viewModel.startWrongAnswersExam()) {
                                        navController.navigate(AppRoutes.ROUTE_EXAM_PAPER)
                                    }
                                },
                                onOpenCreateExam = { navController.navigate(AppRoutes.createExam()) },
                                onOpenExamAccount = { navController.navigate(AppRoutes.ROUTE_EXAM_ACCOUNT) },
                                onOpenExamGround = { navController.navigate(AppRoutes.ROUTE_EXAM_GROUND) }
                            )
                        }

                        composable(
                            route = AppRoutes.ROUTE_CREATE_EXAM,
                            arguments = listOf(
                                navArgument("examId") {
                                    type = NavType.StringType
                                    nullable = true
                                    defaultValue = null
                                }
                            )
                        ) { backStackEntry ->
                            val editExamId = backStackEntry.arguments?.getString("examId")
                            val initialExam = editExamId?.let { id ->
                                userCreatedExams.firstOrNull { it.id == id }
                            }
                            CreateExamScreen(
                                trackerSubjects = trackerSubjects,
                                extraSubjectNames = userCreatedExams.map { it.subjectName }.distinct(),
                                initialExam = initialExam,
                                authorName = prefs.userName,
                                authorBatch = studyDashboardState.batchTag,
                                onBack = { navController.popBackStack() },
                                onDeleteExistingExam = initialExam?.let { existing ->
                                    {
                                        viewModel.deleteCustomExam(existing.id)
                                        navController.popBackStack()
                                    }
                                },
                                onSaveOrPublish = { exam, publishToGround ->
                                    viewModel.saveOrPublishCustomExam(exam)
                                    navController.popBackStack()
                                    if (publishToGround) {
                                        navController.navigate(AppRoutes.ROUTE_EXAM_GROUND)
                                    } else if (initialExam == null) {
                                        navController.navigate(AppRoutes.ROUTE_EXAM_ACCOUNT)
                                    }
                                }
                            )
                        }

                        composable(AppRoutes.ROUTE_EXAM_ACCOUNT) {
                            ExamAccountScreen(
                                userName = prefs.userName,
                                batchTag = studyDashboardState.batchTag,
                                totalExamsTaken = prefs.totalExamsTaken,
                                userCreatedExams = userCreatedExams,
                                onBack = { navController.popBackStack() },
                                onCreateExamClick = { navController.navigate(AppRoutes.createExam()) },
                                onTogglePublish = viewModel::toggleCustomExamPublished,
                                onDeleteExam = viewModel::deleteCustomExam,
                                onTakeCustomExam = { exam ->
                                    viewModel.startCustomExam(exam)
                                    navController.navigate(AppRoutes.ROUTE_EXAM_PAPER)
                                },
                                onEditExam = { exam ->
                                    navController.navigate(AppRoutes.createExam(exam.id))
                                },
                                onUpdateExam = viewModel::saveOrPublishCustomExam
                            )
                        }

                        composable(AppRoutes.ROUTE_EXAM_GROUND) {
                            ExamGroundScreen(
                                userCreatedExams = userCreatedExams,
                                onBack = { navController.popBackStack() },
                                onCreateExamClick = { navController.navigate(AppRoutes.createExam()) },
                                onEnterExam = { exam ->
                                    viewModel.startCustomExam(exam)
                                    navController.navigate(AppRoutes.ROUTE_EXAM_PAPER)
                                }
                            )
                        }

                        composable(AppRoutes.ROUTE_EXAM_HSC) {
                            ExamHscSubjectsScreen(
                                subjects = trackerSubjects,
                                examBestScores = prefs.examBestScores,
                                onBack = { navController.popBackStack() },
                                onSelectChapterExam = { subject, chapter ->
                                    viewModel.startChapterExam(subject, chapter)
                                    navController.navigate(AppRoutes.ROUTE_EXAM_PAPER)
                                }
                            )
                        }

                        composable(AppRoutes.ROUTE_EXAM_PAPER) {
                            ExamPaperScreen(
                                examState = examState,
                                onBack = {
                                    viewModel.stopExamTimer()
                                    navController.popBackStack()
                                },
                                onSelectOption = viewModel::selectExamOption,
                                onSubmitExam = viewModel::submitExam,
                                onShowAnswers = viewModel::showExamAnswers,
                                onShowResultPaper = viewModel::showExamResultPaper,
                                onRetakeExam = viewModel::retakeExam,
                                onSwitchPaperStyle = viewModel::setExamPaperStyle
                            )
                        }

                        composable(AppRoutes.ROUTE_STORY) {
                            StoryHomeScreen(
                                userName = prefs.userName,
                                lastReadPages = prefs.storyLastReadPages,
                                bookmarkedPages = prefs.storyBookmarkedPages,
                                onBack = { navController.popBackStack() },
                                onOpenBook = { bookId, pageIndex ->
                                    navController.navigate(AppRoutes.storyReader(bookId, pageIndex))
                                }
                            )
                        }

                        composable(
                            route = AppRoutes.ROUTE_STORY_READER,
                            arguments = listOf(
                                navArgument("bookId") { type = NavType.StringType },
                                navArgument("page") {
                                    type = NavType.IntType
                                    defaultValue = 0
                                }
                            )
                        ) { backStackEntry ->
                            val bookId = backStackEntry.arguments?.getString("bookId").orEmpty()
                            val startPage = backStackEntry.arguments?.getInt("page") ?: 0
                            StoryBookReaderScreen(
                                bookId = bookId,
                                initialPageIndex = startPage,
                                bookmarkedPages = prefs.storyBookmarkedPages,
                                onBack = { navController.popBackStack() },
                                onPageChanged = viewModel::updateStoryLastReadPage,
                                onToggleBookmarkPage = viewModel::toggleStoryBookmarkPage
                            )
                        }

                        composable(AppRoutes.ROUTE_MISSION) {
                            MissionScreen(
                                missions = missions,
                                liveNotifEnabled = missionLiveNotifEnabled,
                                trackerSubjects = trackerSubjects,
                                onBack = { navController.popBackStack() },
                                onSaveMission = viewModel::saveMission,
                                onToggleMissionCompleted = viewModel::toggleMissionCompleted,
                                onToggleMissionTag = viewModel::toggleMissionPrepTag,
                                onToggleMissionAlarm = viewModel::toggleMissionAlarm,
                                onDeleteMission = viewModel::deleteMission,
                                onToggleLiveNotification = viewModel::setMissionLiveNotifEnabled,
                                onCopyDayToNext7Days = viewModel::copyDayMissionsToNext7Days,
                                onSyncWidgetNow = viewModel::syncMissionWidgetAndNotifications
                            )
                        }

                        composable(AppRoutes.ROUTE_MORE) {
                            MoreScreen(
                                onBack = { navController.popBackStack() },
                                onOpenProfile = { navController.navigate(AppRoutes.ROUTE_PROFILE) }
                            )
                        }

                        composable(AppRoutes.ROUTE_PROFILE) {
                            ProfileScreen(
                                homeState = homeState,
                                prefs = prefs,
                                onBack = { navController.popBackStack() },
                                onUpdateUserName = viewModel::updateUserName,
                                onUpdateLanguage = viewModel::updateAppLanguage,
                                onDetectGpsLocation = viewModel::detectGpsLocation,
                                onSelectCity = viewModel::selectCityPreset,
                                onSelectCalcMethod = viewModel::updateCalculationMethod,
                                onClearStatusMessage = viewModel::clearLocationStatusMessage
                            )
                        }
                    }
                }
            }
        }
    }
}
