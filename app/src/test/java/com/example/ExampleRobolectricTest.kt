package com.example

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onLast
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.performScrollToNode
import androidx.test.core.app.ApplicationProvider
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import com.example.data.local.ExamGroundDefaults
import com.example.data.local.ExamQuestionBank
import com.example.data.model.CustomPublishedExam
import com.example.data.model.ExamPaperStage
import com.example.data.model.ExamPaperStyle
import com.example.data.model.ExamUiState
import com.example.data.model.FocusSessionMode
import com.example.data.model.PrepQuestionTag
import com.example.data.model.TrackerDefaults
import com.example.data.preferences.UserPrefsState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.data.local.StoryLibraryRepository
import com.example.data.model.MissionAlarmMode
import com.example.data.model.MissionItem
import com.example.data.model.MissionTimeHelper
import com.example.data.preferences.MissionStorageHelper
import com.example.notifications.MissionScheduler
import com.example.ui.screens.CreateExamScreen
import com.example.ui.screens.ExamAccountScreen
import com.example.ui.screens.ExamGroundScreen
import com.example.ui.screens.ExamHscSubjectsScreen
import com.example.ui.screens.ExamHubScreen
import com.example.ui.screens.ExamPaperScreen
import com.example.ui.screens.FocusScreen
import com.example.ui.screens.GroupStudyRoomScreen
import com.example.ui.screens.GroupsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MissionScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.StoryBookReaderScreen
import com.example.ui.screens.StoryHomeScreen
import com.example.ui.screens.TrackerScreen
import com.example.ui.theme.BanglaStrings
import com.example.ui.theme.EnglishStrings
import com.example.ui.theme.LocalAppStrings
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.HomeUiState
import com.example.ui.viewmodel.StudyDashboardUiState
import com.example.ui.viewmodel.StudyRoomUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("STUDY ROOM", appName)
    }

    @Test
    fun `home screen displays hero clock, 5 menu buttons, ayat, and study dashboard`() {
        var quranOpened = false
        var groupsOpened = false
        var trackerOpened = false
        var examOpened = false
        var refreshedAyah = false
        var toggledSubjectId: String? = null
        var startedSubjectRoomId: String? = null
        var focusOpened = false
        var focusStartedAndOpened = false
        var myGroupsOpened = false
        var toggledTodoId: String? = null

        composeRule.setContent {
            MyApplicationTheme {
                HomeScreen(
                    homeState = HomeUiState(
                        clockDisplay = "06:04",
                        combinedDateDisplay = "31 January / 9 Rajab 1444 H"
                    ),
                    prefs = UserPrefsState(),
                    onOpenProfile = {},
                    onOpenQuran = { quranOpened = true },
                    onOpenSurah = {},
                    onOpenDua = {},
                    onOpenDzikir = {},
                    onOpenGroups = { groupsOpened = true },
                    onOpenTracker = { trackerOpened = true },
                    onOpenExam = { examOpened = true },
                    onOpenMore = {},
                    onCycleDailyAyah = { refreshedAyah = true },
                    onToggleAyahBookmark = { _, _ -> },
                    onToggleAdzanMaster = {},
                    onToggleAdzanPrayer = {},
                    onTogglePrayerDone = {},
                    onToggleSubjectTimer = { toggledSubjectId = it },
                    onStartSubjectAndOpenRoom = { startedSubjectRoomId = it },
                    onOpenFocusMode = { focusOpened = true },
                    onStartFocusAndOpen = { focusStartedAndOpened = true },
                    onOpenMyGroups = { myGroupsOpened = true },
                    onToggleTodo = { toggledTodoId = it }
                )
            }
        }

        composeRule.onNodeWithText("STUDY ROOM").assertIsDisplayed()
        composeRule.onNodeWithText("06:04").assertIsDisplayed()
        composeRule.onNodeWithText("31 January / 9 Rajab 1444 H").assertIsDisplayed()
        composeRule.onNodeWithTag("menu_zakat").assertDoesNotExist()
        composeRule.onNodeWithTag("menu_kalam").assertDoesNotExist()
        composeRule.onNodeWithTag("menu_kiblat").assertDoesNotExist()
        composeRule.onNodeWithTag("menu_alquran").performScrollTo().performClick()
        assertTrue(quranOpened)
        composeRule.onNodeWithTag("menu_groups").performScrollTo().performClick()
        assertTrue(groupsOpened)
        composeRule.onNodeWithTag("menu_tracker").performScrollTo().performClick()
        assertTrue(trackerOpened)
        composeRule.onNodeWithTag("menu_exam").performScrollTo().performClick()
        assertTrue(examOpened)
        composeRule.onNodeWithText("Ayat of the Day").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag("refresh_daily_ayah").performScrollTo().performClick()
        assertTrue(refreshedAyah)

        // Verify Study Dashboard cards from shared screenshots & My Groups flow
        composeRule.onNodeWithText("STUDY TIME").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText("FOCUS GOAL").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText("STREAK").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText("#9").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText("My Groups").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag("dashboard_focus_card", useUnmergedTree = true).performScrollTo().performClick()
        assertTrue(focusOpened)
        composeRule.onNodeWithTag("dashboard_focus_start_btn", useUnmergedTree = true).performScrollTo().performClick()
        assertTrue(focusStartedAndOpened)
        composeRule.onNodeWithTag("dashboard_my_groups_btn", useUnmergedTree = true).performScrollTo().performClick()
        assertTrue(myGroupsOpened)
        composeRule.onNodeWithText("SUBJECTS").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText("0:02:04").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag("play_subject_subj_physics").performScrollTo().performClick()
        assertEquals("subj_physics", startedSubjectRoomId)
        composeRule.onNodeWithText("SESSION SETUP").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText("TO-DO LIST").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText("physics chapter 4").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag("todo_row_todo_1").performScrollTo().performClick()
        assertEquals("todo_1", toggledTodoId)
    }

    @Test
    fun `profile language switcher updates language and home screen renders bangla`() {
        var selectedLang = "en"
        composeRule.setContent {
            MyApplicationTheme {
                CompositionLocalProvider(
                    LocalAppStrings provides if (selectedLang == "bn") BanglaStrings else EnglishStrings
                ) {
                    ProfileScreen(
                        homeState = HomeUiState(),
                        prefs = UserPrefsState(appLanguage = selectedLang),
                        onBack = {},
                        onUpdateUserName = {},
                        onUpdateLanguage = { selectedLang = it },
                        onDetectGpsLocation = {},
                        onSelectCity = {},
                        onSelectCalcMethod = {},
                        onClearStatusMessage = {}
                    )
                }
            }
        }

        composeRule.onNodeWithTag("lang_en").assertIsDisplayed()
        composeRule.onNodeWithTag("lang_bn").performClick()
        assertEquals("bn", selectedLang)
    }

    @Test
    fun `groups screen opens preview sheet on enter and confirms entry`() {
        var enteredGroupId: String? = null
        composeRule.setContent {
            MyApplicationTheme {
                GroupsScreen(
                    studyState = StudyRoomUiState(),
                    onBack = {},
                    onEnterGroupRoom = { groupId, _ -> enteredGroupId = groupId }
                )
            }
        }

        composeRule.onNodeWithText("Groups").assertIsDisplayed()
        composeRule.onNodeWithTag("groups_search_toggle").assertIsDisplayed().performClick()
        composeRule.onNodeWithTag("groups_search_input").assertIsDisplayed()
        composeRule.onNodeWithTag("groups_search_toggle").performClick()
        composeRule.onNodeWithText("Verified • Hifz Active").assertIsDisplayed()
        composeRule.onNodeWithText("4.9").assertIsDisplayed()
        composeRule.onNodeWithTag("enter_group_grp_dhaka_quran").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("group_organizers_card").assertIsDisplayed()
        composeRule.onNodeWithText("Group Creator").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText("Group Leader").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText("Moderator").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText("Enter Preview • Active Students").assertDoesNotExist()
        composeRule.onNodeWithText("Group Description").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText("Group Rules (Max 30 Members)").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag("confirm_enter_group").assertIsDisplayed().performClick()
        assertEquals("grp_dhaka_quran", enteredGroupId)
    }

    @Test
    fun `group study room displays 30-member ypt grid and toggles study timer`() {
        var toggled = false
        composeRule.setContent {
            MyApplicationTheme {
                GroupStudyRoomScreen(
                    groupId = "grp_dhaka_quran",
                    userName = "Tofail",
                    studyState = StudyRoomUiState(activeGroupId = "grp_dhaka_quran", todayStudySeconds = 125L),
                    onBack = {},
                    onToggleStudying = { toggled = true },
                    onSelectSubject = {},
                    onCheerMember = {},
                    onLeaveGroup = {}
                )
            }
        }

        composeRule.onNodeWithTag("room_search_toggle").assertIsDisplayed().performClick()
        composeRule.onNodeWithTag("room_search_input").assertIsDisplayed()
        composeRule.onNodeWithTag("room_search_toggle").performClick()
        composeRule.onNodeWithTag("user_study_timer").assertIsDisplayed()
        composeRule.onNodeWithTag("tab_study_grid").assertIsDisplayed()
        composeRule.onNodeWithTag("toggle_study_timer").performClick()
        assertTrue(toggled)
        composeRule.onNodeWithTag("study_room_grid").performScrollToIndex(3)
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("student_desk_current_user", useUnmergedTree = true).assertIsDisplayed()
        composeRule.onNodeWithTag("student_desk_grp_dhaka_quran_m_0", useUnmergedTree = true).assertIsDisplayed()
    }

    @Test
    fun `clicking student opens detail popup with calendar, selected date hours, and graphs`() {
        var cheeredMemberId: String? = null
        composeRule.setContent {
            MyApplicationTheme {
                GroupStudyRoomScreen(
                    groupId = "grp_dhaka_quran",
                    userName = "Tofail",
                    studyState = StudyRoomUiState(activeGroupId = "grp_dhaka_quran", todayStudySeconds = 125L),
                    onBack = {},
                    onToggleStudying = {},
                    onSelectSubject = {},
                    onCheerMember = { cheeredMemberId = it },
                    onLeaveGroup = {}
                )
            }
        }

        composeRule.onNodeWithTag("study_room_grid").performScrollToIndex(3)
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("student_tile_grp_dhaka_quran_m_0").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithText("Study Calendar").assertIsDisplayed()
        composeRule.onNodeWithTag("calendar_day_1").performScrollTo().performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("selected_day_study_time").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag("hourly_focus_graph").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag("weekly_trend_graph").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag("student_sheet_cheer_btn").performScrollTo().performClick()
        assertEquals("grp_dhaka_quran_m_0", cheeredMemberId)
    }

    @Test
    fun `scrolling study room reveals compact top timer and allows start pause toggle`() {
        var toggled = false
        composeRule.setContent {
            MyApplicationTheme {
                GroupStudyRoomScreen(
                    groupId = "grp_dhaka_quran",
                    userName = "Tofail",
                    studyState = StudyRoomUiState(
                        activeGroupId = "grp_dhaka_quran",
                        todayStudySeconds = 125L,
                        isStudying = true
                    ),
                    onBack = {},
                    onToggleStudying = { toggled = true },
                    onSelectSubject = {},
                    onCheerMember = {},
                    onLeaveGroup = {}
                )
            }
        }

        composeRule.onNodeWithTag("user_study_timer").assertIsDisplayed()
        composeRule.onNodeWithTag("compact_top_timer").assertDoesNotExist()

        composeRule.onNodeWithTag("study_room_grid").performScrollToIndex(14)
        composeRule.waitForIdle()

        composeRule.onNodeWithTag("compact_top_timer").assertIsDisplayed()
        composeRule.onNodeWithTag("compact_top_timer_toggle").assertIsDisplayed().performClick()
        assertTrue(toggled)
    }

    @Test
    fun `groups screen shows entered groups in my groups tab`() {
        composeRule.setContent {
            MyApplicationTheme {
                GroupsScreen(
                    studyState = StudyRoomUiState(),
                    initialMyGroupsOnly = true,
                    onBack = {},
                    onEnterGroupRoom = { _, _ -> }
                )
            }
        }

        composeRule.onNodeWithTag("tab_my_groups").assertIsDisplayed()
        composeRule.onNodeWithText("Dhaka Hifz & Tafsir Circle").assertIsDisplayed()
        composeRule.onNodeWithTag("groups_list").performScrollToNode(hasTestTag("group_card_grp_hsc_science"))
        composeRule.waitForIdle()
        composeRule.onNodeWithText("HSC Science & Fajr Focus").assertIsDisplayed()
        composeRule.onNodeWithText("BUET & DU Admission Hall").assertDoesNotExist()

        composeRule.onNodeWithTag("groups_list").performScrollToIndex(0)
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("tab_all_groups").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("groups_list").performScrollToNode(hasTestTag("group_card_grp_buet_du"))
        composeRule.waitForIdle()
        composeRule.onNodeWithText("BUET & DU Admission Hall").assertIsDisplayed()
    }

    @Test
    fun `live room top group switcher switches displayed group and students`() {
        var switchedGroupId: String? = null
        composeRule.setContent {
            MyApplicationTheme {
                GroupStudyRoomScreen(
                    groupId = "grp_dhaka_quran",
                    userName = "Tofail",
                    studyState = StudyRoomUiState(activeGroupId = "grp_dhaka_quran", todayStudySeconds = 125L),
                    onBack = {},
                    onToggleStudying = {},
                    onSelectSubject = {},
                    onCheerMember = {},
                    onLeaveGroup = {},
                    onSwitchGroup = { switchedGroupId = it }
                )
            }
        }

        composeRule.onNodeWithTag("live_room_groups_switcher").assertIsDisplayed()
        composeRule.onNodeWithTag("room_group_chip_grp_hsc_science").assertIsDisplayed().performClick()
        composeRule.waitForIdle()
        assertEquals("grp_hsc_science", switchedGroupId)
        composeRule.onNodeWithText("HSC Science & Fajr Focus").assertIsDisplayed()
    }

    @Test
    fun `focus screen renders hero dial, stopwatch mode, pomodoro switch, and subject picker`() {
        var playPauseToggled = false
        var resetClicked = false
        var selectedMode: FocusSessionMode? = null
        var selectedSubjectId: String? = null

        composeRule.setContent {
            MyApplicationTheme {
                FocusScreen(
                    dashboard = StudyDashboardUiState(
                        activeSubjectId = "subj_physics",
                        focusSubjectId = "subj_physics",
                        focusMode = FocusSessionMode.STOPWATCH,
                        focusSessionElapsedSeconds = 1L
                    ),
                    onBack = {},
                    onTogglePlayPause = { playPauseToggled = true },
                    onResetSession = { resetClicked = true },
                    onSelectMode = { selectedMode = it },
                    onSelectSubject = { selectedSubjectId = it },
                    onAddSubject = { _, _, _ -> },
                    onSkipBreak = {},
                    onToggleKeepScreenOn = {},
                    onToggleAmbientSound = {},
                    onOpenLiveRoom = {}
                )
            }
        }

        composeRule.onNodeWithText("Physics").assertIsDisplayed()
        composeRule.onNodeWithTag("focus_ring_canvas").assertIsDisplayed()
        composeRule.onNodeWithTag("focus_timer_readout").assertIsDisplayed()
        composeRule.onNodeWithText("0:00:01").assertIsDisplayed()
        composeRule.onNodeWithText("Focusing · ring = 1 hour").assertIsDisplayed()
        composeRule.onNodeWithTag("focus_mode_STOPWATCH").assertIsDisplayed()
        composeRule.onNodeWithTag("focus_mode_POMODORO_25").assertIsDisplayed().performClick()
        assertEquals(FocusSessionMode.POMODORO_25, selectedMode)

        composeRule.onNodeWithTag("focus_play_pause_btn").performScrollTo().performClick()
        assertTrue(playPauseToggled)
        composeRule.onNodeWithTag("focus_reset_btn").performScrollTo().performClick()
        assertTrue(resetClicked)

        composeRule.onNodeWithTag("focus_subject_selector").performScrollTo().performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("focus_pick_subject_subj_chem").assertIsDisplayed().performClick()
        assertEquals("subj_chem", selectedSubjectId)
    }

    @Test
    fun `focus screen displays pomodoro countdown and break phase cleanly`() {
        var skippedBreak = false
        composeRule.setContent {
            MyApplicationTheme {
                FocusScreen(
                    dashboard = StudyDashboardUiState(
                        activeSubjectId = "subj_math",
                        focusSubjectId = "subj_math",
                        focusMode = FocusSessionMode.POMODORO_25,
                        focusCountdownSeconds = 5 * 60,
                        isFocusBreakPhase = true,
                        completedPomodoros = 1
                    ),
                    onBack = {},
                    onTogglePlayPause = {},
                    onResetSession = {},
                    onSelectMode = {},
                    onSelectSubject = {},
                    onAddSubject = { _, _, _ -> },
                    onSkipBreak = { skippedBreak = true },
                    onToggleKeepScreenOn = {},
                    onToggleAmbientSound = {},
                    onOpenLiveRoom = {}
                )
            }
        }

        composeRule.onNodeWithText("Math").assertIsDisplayed()
        composeRule.onNodeWithText("05:00").assertIsDisplayed()
        composeRule.onNodeWithText("Break · 5 min recharge").assertIsDisplayed()
        composeRule.onNodeWithTag("focus_skip_break_btn").performScrollTo().performClick()
        assertTrue(skippedBreak)
    }

    @Test
    fun `tracker screen opens group-entry style modal popup on subject click and updates hsc paper progress`() {
        composeRule.setContent {
            MyApplicationTheme {
                var subjects by remember { mutableStateOf(TrackerDefaults.defaultSubjects) }
                TrackerScreen(
                    subjects = subjects,
                    onBack = {},
                    onToggleTag = { subjId, chId, tag ->
                        subjects = subjects.map { s ->
                            if (s.id != subjId) s
                            else s.copy(
                                chapters = s.chapters.map { ch ->
                                    if (ch.id != chId) ch
                                    else {
                                        val next = if (tag in ch.completedTags) ch.completedTags - tag else ch.completedTags + tag
                                        ch.copy(completedTags = next)
                                    }
                                }
                            )
                        }
                    },
                    onToggleAllInChapter = { _, _ -> },
                    onAddChapter = { _, _, _ -> },
                    onDeleteChapter = { _, _ -> },
                    onAddSubject = { _, _, _ -> }
                )
            }
        }

        // Verify Bangla, English stat boxes appear at top, then scroll to Physics row
        composeRule.onNodeWithText("BANGLA").assertIsDisplayed()
        composeRule.onNodeWithText("ENGLISH").assertIsDisplayed()
        composeRule.onNodeWithTag("tracker_list").performScrollToIndex(2)
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("tracker_subject_card_trk_physics").assertIsDisplayed()

        // Physics HSC initial stats: 1st: 10% • 2nd: 0%
        composeRule.onNodeWithTag("tracker_subject_pct_trk_physics", useUnmergedTree = true).assertIsDisplayed()
        composeRule.onNodeWithText("1st: 10%  •  2nd: 0%", useUnmergedTree = true).assertIsDisplayed()

        // Click Physics box -> Group-entry style ModalBottomSheet popup opens
        composeRule.onNodeWithTag("tracker_subject_card_trk_physics").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("tracker_popup_trk_physics").assertIsDisplayed()
        composeRule.onNodeWithText("Pending").assertDoesNotExist()
        composeRule.onNodeWithText("Done").assertDoesNotExist()
        composeRule.onNodeWithTag("tracker_paper_tab_1_trk_physics").assertIsDisplayed()
        composeRule.onNodeWithTag("tracker_paper_tab_2_trk_physics").assertIsDisplayed()
        composeRule.onNodeWithText("1. ভৌত জগৎ ও পরিমাপ").assertIsDisplayed()

        // Toggle CQ and GHA on Physics 1st Paper Ch 1 -> 1st paper moves 10% -> 13%
        composeRule.onNodeWithTag("tracker_tag_trk_physics_p1_ch_1_CQ").performScrollTo().performClick()
        composeRule.onNodeWithTag("tracker_tag_trk_physics_p1_ch_1_GHA").performScrollTo().performClick()
        composeRule.waitForIdle()

        // Switch to 2nd Paper tab and check HSC Physics 2nd Paper chapters appear & toggle works
        composeRule.onNodeWithTag("tracker_paper_tab_2_trk_physics").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithText("1. তাপগতিবিদ্যা").assertIsDisplayed()
        composeRule.onNodeWithTag("tracker_tag_trk_physics_p2_ch_1_KA").performScrollTo().performClick()
        composeRule.waitForIdle()

        // Close bottom sheet popup and verify updated stats on Physics box
        composeRule.onNodeWithTag("tracker_popup_close").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("tracker_popup_trk_physics").assertDoesNotExist()
        composeRule.onNodeWithText("1st: 13%  •  2nd: 1%", useUnmergedTree = true).assertIsDisplayed()
    }

    @Test
    fun `exam question bank covers all 134 hsc chapters with 3 questions per chapter`() {
        val allChapters = TrackerDefaults.defaultSubjects.flatMap { subj ->
            subj.chapters.map { subj.id to it }
        }
        assertEquals(134, allChapters.size)
        assertEquals(402, ExamQuestionBank.totalQuestionsCount())
        allChapters.forEach { (subjId, ch) ->
            val qs = ExamQuestionBank.getQuestionsForChapter(subjId, ch.id, ch.title, ch.paper)
            assertEquals("Expected 3 questions for ${ch.id}", 3, qs.size)
        }
    }

    @Test
    fun `exam hub shows tracker cards, 5 exam tracks, coming soon modal, and opens hsc`() {
        var hscOpened = false
        composeRule.setContent {
            MyApplicationTheme {
                ExamHubScreen(
                    trackerSubjects = TrackerDefaults.defaultSubjects,
                    examBestScores = emptyMap(),
                    wrongQuestionIds = emptySet(),
                    totalExamsTaken = 0,
                    onBack = {},
                    onOpenHscSubjects = { hscOpened = true },
                    onOpenTracker = {},
                    onStartWrongAnswersExam = {}
                )
            }
        }

        composeRule.onNodeWithTag("exam_tracker_preview_card").assertIsDisplayed()
        composeRule.onNodeWithTag("exam_track_card_HSC").assertIsDisplayed()
        composeRule.onNodeWithTag("exam_track_card_MEDICAL").assertIsDisplayed()
        composeRule.onNodeWithTag("exam_hub_list").performScrollToNode(hasTestTag("exam_track_card_BUET"))
        composeRule.onNodeWithTag("exam_track_card_BUET").assertIsDisplayed()
        composeRule.onNodeWithTag("exam_track_card_DU").assertIsDisplayed()
        composeRule.onNodeWithTag("exam_hub_list").performScrollToNode(hasTestTag("exam_track_card_WRONG_ANS"))
        composeRule.onNodeWithTag("exam_track_card_WRONG_ANS").assertIsDisplayed()

        // Tap MEDICAL -> Coming Soon dialog
        composeRule.onNodeWithTag("exam_hub_list").performScrollToIndex(0)
        composeRule.onNodeWithTag("exam_track_card_MEDICAL").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("exam_coming_soon_dialog").assertIsDisplayed()
        composeRule.onNodeWithTag("exam_coming_soon_ok").performClick()
        composeRule.waitForIdle()

        // Tap HSC -> Opens HSC Subjects screen
        composeRule.onNodeWithTag("exam_track_card_HSC").performClick()
        assertTrue(hscOpened)
    }

    @Test
    fun `exam hsc subjects screen opens 1st and 2nd paper chapter bottom sheet and starts exam`() {
        var selectedChapterId: String? = null
        composeRule.setContent {
            MyApplicationTheme {
                ExamHscSubjectsScreen(
                    subjects = TrackerDefaults.defaultSubjects,
                    examBestScores = emptyMap(),
                    onBack = {},
                    onSelectChapterExam = { _, ch -> selectedChapterId = ch.id }
                )
            }
        }

        composeRule.onNodeWithTag("exam_hsc_subjects_list").performScrollToIndex(2)
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("exam_subject_card_trk_physics").performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithTag("exam_subject_popup_trk_physics").assertIsDisplayed()
        composeRule.onNodeWithTag("exam_paper_tab_1_trk_physics").assertIsDisplayed()
        composeRule.onNodeWithTag("exam_paper_tab_2_trk_physics").assertIsDisplayed()
        composeRule.onNodeWithText("1. ভৌত জগৎ ও পরিমাপ").assertIsDisplayed()

        // Switch to 2nd Paper and tap chapter 1
        composeRule.onNodeWithTag("exam_paper_tab_2_trk_physics").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithText("1. তাপগতিবিদ্যা").assertIsDisplayed()
        composeRule.onNodeWithTag("exam_chapter_row_trk_physics_p2_ch_1").performClick()
        assertEquals("trk_physics_p2_ch_1", selectedChapterId)
    }

    @Test
    fun `exam paper renders 3 questions single page, submits to result paper, and shows selected and correct answers`() {
        val physicsQuestions = ExamQuestionBank.getQuestionsForChapter(
            subjectId = "trk_physics",
            chapterId = "trk_physics_p1_ch_1",
            chapterTitle = "ভৌত জগৎ ও পরিমাপ",
            paper = 1
        )
        composeRule.setContent {
            MyApplicationTheme {
                var state by remember {
                    mutableStateOf(
                        ExamUiState(
                            activeSubjectId = "trk_physics",
                            activeSubjectNameEn = "Physics",
                            activeSubjectNameBn = "পদার্থবিজ্ঞান",
                            activeChapterId = "trk_physics_p1_ch_1",
                            activeChapterTitle = "ভৌত জগৎ ও পরিমাপ",
                            activePaper = 1,
                            questions = physicsQuestions,
                            stage = ExamPaperStage.TAKING
                        )
                    )
                }
                ExamPaperScreen(
                    examState = state,
                    onBack = {},
                    onSelectOption = { qId, optIdx ->
                        state = state.copy(selectedAnswers = state.selectedAnswers + (qId to optIdx))
                    },
                    onSubmitExam = {
                        var c = 0
                        var w = 0
                        var sk = 0
                        state.questions.forEach { q ->
                            val p = state.selectedAnswers[q.id]
                            if (p == null) sk++ else if (p == q.correctIndex) c++ else w++
                        }
                        state = state.copy(
                            stage = ExamPaperStage.RESULT,
                            correctCount = c,
                            wrongCount = w,
                            skippedCount = sk
                        )
                    },
                    onShowAnswers = { state = state.copy(stage = ExamPaperStage.REVIEW_ANSWERS) },
                    onShowResultPaper = { state = state.copy(stage = ExamPaperStage.RESULT) },
                    onRetakeExam = { state = state.copy(selectedAnswers = emptyMap(), stage = ExamPaperStage.TAKING) }
                )
            }
        }

        // Verify all 3 questions live on single top-to-bottom scrollable paper
        val q1 = physicsQuestions[0]
        val q2 = physicsQuestions[1]
        val q3 = physicsQuestions[2]
        composeRule.onNodeWithTag("exam_question_card_1").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag("exam_q_${q1.id}_opt_${q1.correctIndex}").performScrollTo().performClick()

        val q2WrongOpt = (q2.correctIndex + 1) % 4
        composeRule.onNodeWithTag("exam_question_card_2").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag("exam_q_${q2.id}_opt_$q2WrongOpt").performScrollTo().performClick()

        composeRule.onNodeWithTag("exam_question_card_3").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag("exam_q_${q3.id}_opt_${q3.correctIndex}").performScrollTo().performClick()

        // Submit Exam -> Result Paper
        composeRule.onNodeWithTag("exam_submit_button").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("exam_result_paper").assertIsDisplayed()
        composeRule.onNodeWithText("2 / 3").assertIsDisplayed()

        // Click Show Answer -> Answer Review View showing selected & correct answers + explanation
        composeRule.onNodeWithTag("exam_show_answers_btn").performScrollTo().performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("exam_review_scroll").assertIsDisplayed()
        composeRule.onNodeWithTag("exam_marker_${q1.id}_${q1.correctIndex}", useUnmergedTree = true).performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag("exam_marker_${q2.id}_$q2WrongOpt", useUnmergedTree = true).performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag("exam_marker_${q2.id}_${q2.correctIndex}", useUnmergedTree = true).performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag("exam_explanation_2", useUnmergedTree = true).performScrollTo().assertIsDisplayed()
    }

    @Test
    fun `exam hub shows create exam with account and exam ground in same 2-column row`() {
        var createClicked = false
        var accountClicked = false
        var groundClicked = false

        composeRule.setContent {
            MyApplicationTheme {
                ExamHubScreen(
                    trackerSubjects = TrackerDefaults.defaultSubjects,
                    examBestScores = emptyMap(),
                    wrongQuestionIds = emptySet(),
                    totalExamsTaken = 0,
                    publishedGroundCount = 3,
                    userCreatedExamsCount = 1,
                    onBack = {},
                    onOpenHscSubjects = {},
                    onOpenTracker = {},
                    onStartWrongAnswersExam = {},
                    onOpenCreateExam = { createClicked = true },
                    onOpenExamAccount = { accountClicked = true },
                    onOpenExamGround = { groundClicked = true }
                )
            }
        }

        composeRule.onNodeWithTag("exam_hub_list").performScrollToNode(hasTestTag("exam_create_ground_row"))
        composeRule.onNodeWithTag("exam_create_ground_row").assertIsDisplayed()
        composeRule.onNodeWithTag("exam_track_card_CREATE_EXAM").assertIsDisplayed()
        composeRule.onNodeWithTag("exam_track_card_EXAM_GROUND").assertIsDisplayed()
        composeRule.onNodeWithTag("exam_btn_create_exam").performClick()
        assertTrue(createClicked)
        composeRule.onNodeWithTag("exam_btn_exam_account").performClick()
        assertTrue(accountClicked)
        composeRule.onNodeWithTag("exam_track_card_EXAM_GROUND").performClick()
        assertTrue(groundClicked)
    }

    @Test
    fun `create exam supports creator subject name, exam heading, paper style selection, collapsible settings, uddipok, next question, and publish`() {
        var publishedExam: CustomPublishedExam? = null
        var publishedFlag = false

        composeRule.setContent {
            MyApplicationTheme {
                CreateExamScreen(
                    authorName = "Tofail",
                    authorBatch = "HSC-26",
                    onBack = {},
                    onSaveOrPublish = { exam, pub ->
                        publishedExam = exam
                        publishedFlag = pub
                    }
                )
            }
        }

        // Enter Subject Name & Exam Heading text (no chapter), and pick Plain Floor paper style
        composeRule.onNodeWithTag("create_exam_subject_input").performScrollTo().performTextInput("Higher Math 1st Paper")
        composeRule.onNodeWithTag("create_exam_heading_input").performScrollTo().performTextInput("Calculus Live Test 01")
        composeRule.onNodeWithTag("create_exam_style_plain").performScrollTo().performClick()

        // Toggle Hide/Show on settings card
        composeRule.onNodeWithTag("create_exam_password_input").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag("create_exam_settings_toggle").performScrollTo().performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("create_exam_password_input").assertDoesNotExist()
        composeRule.onNodeWithTag("create_exam_settings_toggle").performScrollTo().performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("create_exam_password_input").performScrollTo().performTextInput("pass99")

        // Enable Uddipok, enter question + 4 options, choose option 1 as correct, tap Create Next
        composeRule.onNodeWithTag("create_exam_uddipok_toggle").performScrollTo().performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("create_exam_uddipok_input").performScrollTo().performTextInput("উদ্দীপক: বেগ 10 m/s")
        composeRule.onNodeWithTag("create_exam_question_input").performScrollTo().performTextInput("ত্বরণ কত?")
        composeRule.onNodeWithTag("create_exam_opt_0").performScrollTo().performTextInput("0 m/s2")
        composeRule.onNodeWithTag("create_exam_opt_1").performScrollTo().performTextInput("5 m/s2")
        composeRule.onNodeWithTag("create_exam_opt_2").performScrollTo().performTextInput("10 m/s2")
        composeRule.onNodeWithTag("create_exam_opt_3").performScrollTo().performTextInput("2 m/s2")
        composeRule.onNodeWithTag("create_exam_correct_1").performScrollTo().performClick()
        composeRule.onNodeWithTag("create_exam_next_q_btn").performScrollTo().performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithTag("create_exam_q_count_pill").assertIsDisplayed()
        composeRule.onNodeWithText("1 Qs").assertIsDisplayed()

        // Publish Exam
        composeRule.onNodeWithTag("create_exam_publish_btn").performClick()
        assertTrue(publishedFlag)
        assertEquals("Higher Math 1st Paper", publishedExam?.subjectName)
        assertEquals("Calculus Live Test 01", publishedExam?.title)
        assertEquals("", publishedExam?.chapterTitle)
        assertEquals(ExamPaperStyle.PLAIN_FLOOR, publishedExam?.paperStyle)
        assertEquals(1, publishedExam?.questions?.size)
        assertEquals("pass99", publishedExam?.password)
        assertEquals("উদ্দীপক: বেগ 10 m/s", publishedExam?.questions?.firstOrNull()?.uddipok)
        assertEquals(1, publishedExam?.questions?.firstOrNull()?.correctIndex)
    }

    @Test
    fun `exam paper screen supports plain floor boxless text-only mode and switches to boxed mode`() {
        val sampleQuestions = ExamGroundDefaults.defaultCommunityExams.last().questions
        composeRule.setContent {
            MyApplicationTheme {
                var state by remember {
                    mutableStateOf(
                        ExamUiState(
                            activeSubjectId = "custom_subj",
                            activeSubjectNameEn = "Biology",
                            activeSubjectNameBn = "Biology",
                            activeChapterId = "custom_exam_bio",
                            activeChapterTitle = "Cell Division Plain Paper",
                            paperStyle = ExamPaperStyle.PLAIN_FLOOR,
                            questions = sampleQuestions,
                            stage = ExamPaperStage.TAKING
                        )
                    )
                }
                ExamPaperScreen(
                    examState = state,
                    onBack = {},
                    onSelectOption = { qId, optIdx ->
                        state = state.copy(selectedAnswers = state.selectedAnswers + (qId to optIdx))
                    },
                    onSubmitExam = {
                        state = state.copy(stage = ExamPaperStage.RESULT, correctCount = 1, wrongCount = 0, skippedCount = 1)
                    },
                    onShowAnswers = { state = state.copy(stage = ExamPaperStage.REVIEW_ANSWERS) },
                    onShowResultPaper = { state = state.copy(stage = ExamPaperStage.RESULT) },
                    onRetakeExam = {},
                    onSwitchPaperStyle = { nextStyle ->
                        state = state.copy(paperStyle = nextStyle)
                    }
                )
            }
        }

        // Plain floor text-only header and question item visible (no boxed card)
        composeRule.onNodeWithTag("exam_plain_floor_header").assertIsDisplayed()
        composeRule.onNodeWithTag("exam_plain_question_1").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag("exam_question_card_1").assertDoesNotExist()

        // Tap switch to Box View -> boxed card mode renders
        composeRule.onNodeWithTag("exam_switch_to_cards_btn").performScrollTo().performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("exam_boxed_cards_header").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag("exam_question_card_1").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun `exam ground enforces password in entry popup and launches unlocked exam`() {
        var enteredExamId: String? = null
        composeRule.setContent {
            MyApplicationTheme {
                ExamGroundScreen(
                    userCreatedExams = emptyList(),
                    onBack = {},
                    onCreateExamClick = {},
                    onEnterExam = { enteredExamId = it.id }
                )
            }
        }

        val lockedExam = ExamGroundDefaults.defaultCommunityExams[1] // pass: "2026"
        composeRule.onNodeWithTag("exam_ground_list").performScrollToNode(hasTestTag("exam_ground_card_${lockedExam.id}"))
        composeRule.onNodeWithTag("exam_ground_card_${lockedExam.id}").performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithTag("exam_ground_entry_sheet_${lockedExam.id}").assertIsDisplayed()
        composeRule.onNodeWithTag("exam_entry_password_input").performScrollTo().performTextInput("wrong")
        composeRule.onNodeWithTag("exam_ground_confirm_enter").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("exam_entry_password_error").performScrollTo().assertIsDisplayed()

        // Enter correct password ("2026") and enter exam
        composeRule.onNodeWithTag("exam_entry_password_input").performScrollTo().performTextReplacement("2026")
        composeRule.onNodeWithTag("exam_ground_confirm_enter").performClick()
        assertEquals(lockedExam.id, enteredExamId)
    }

    @Test
    fun `exam account screen lists user exams and toggles publish status`() {
        var toggledId: String? = null
        var deletedId: String? = null
        val customExam = ExamGroundDefaults.defaultCommunityExams.first().copy(
            id = "my_exam_1",
            title = "My Custom Physics Test",
            isPublished = false
        )

        composeRule.setContent {
            MyApplicationTheme {
                ExamAccountScreen(
                    userName = "Tofail",
                    batchTag = "HSC-26",
                    totalExamsTaken = 4,
                    userCreatedExams = listOf(customExam),
                    onBack = {},
                    onCreateExamClick = {},
                    onTogglePublish = { toggledId = it },
                    onDeleteExam = { deletedId = it },
                    onTakeCustomExam = {}
                )
            }
        }

        composeRule.onNodeWithTag("exam_account_list").performScrollToNode(hasTestTag("exam_account_item_my_exam_1"))
        composeRule.onNodeWithText("My Custom Physics Test").assertIsDisplayed()
        composeRule.onNodeWithText("DRAFT").assertIsDisplayed()
        composeRule.onNodeWithTag("exam_account_toggle_pub_my_exam_1").performClick()
        assertEquals("my_exam_1", toggledId)
        composeRule.onNodeWithTag("exam_account_delete_my_exam_1").performClick()
        assertEquals("my_exam_1", deletedId)
    }

    @Test
    fun `create exam subject chip sets subject, quick create json generates questions, and preview shows all created exam`() {
        var publishedExam: CustomPublishedExam? = null
        var publishedFlag = false

        composeRule.setContent {
            MyApplicationTheme {
                CreateExamScreen(
                    trackerSubjects = TrackerDefaults.defaultSubjects,
                    extraSubjectNames = listOf("Statistics"),
                    authorName = "Tofail",
                    authorBatch = "HSC-26",
                    onBack = {},
                    onSaveOrPublish = { exam, pub ->
                        publishedExam = exam
                        publishedFlag = pub
                    }
                )
            }
        }

        // Tap first subject chip ("Bangla") to quick-fill subject field
        composeRule.onNodeWithTag("create_exam_subj_chip_0").performScrollTo().performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("create_exam_subject_input").assertTextContains(TrackerDefaults.defaultSubjects.first().nameEn)

        // Switch to Quick Create (JSON), paste sample template, and auto-create questions
        composeRule.onNodeWithTag("create_exam_mode_quick").performScrollTo().performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("create_exam_quick_template_btn").performScrollTo().performClick()
        composeRule.onNodeWithTag("create_exam_quick_parse_btn").performScrollTo().performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithTag("create_exam_q_count_pill").assertIsDisplayed()
        composeRule.onNodeWithText("2 Qs").assertIsDisplayed()

        // Open full Preview and verify both created questions are visible
        composeRule.onNodeWithTag("create_exam_preview_btn").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("create_exam_preview_dialog").assertIsDisplayed()
        composeRule.onNodeWithTag("create_exam_preview_q_1").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag("create_exam_preview_q_2").performScrollTo().assertIsDisplayed()

        // Publish directly from Preview
        composeRule.onNodeWithTag("create_exam_preview_publish_btn").performClick()
        composeRule.waitForIdle()
        assertTrue(publishedFlag)
        assertEquals(2, publishedExam?.questions?.size)
        assertEquals("Physics 1st Paper", publishedExam?.subjectName)
        assertEquals("Quick JSON Model Test 01", publishedExam?.title)
        assertEquals(ExamPaperStyle.PLAIN_FLOOR, publishedExam?.paperStyle)
    }

    @Test
    fun `create exam preview lets creator edit all questions and delete questions`() {
        var publishedExam: CustomPublishedExam? = null

        composeRule.setContent {
            MyApplicationTheme {
                CreateExamScreen(
                    trackerSubjects = TrackerDefaults.defaultSubjects,
                    authorName = "Tofail",
                    authorBatch = "HSC-26",
                    onBack = {},
                    onSaveOrPublish = { exam, _ -> publishedExam = exam }
                )
            }
        }

        // Load 2 sample questions via Quick Create (JSON)
        composeRule.onNodeWithTag("create_exam_mode_quick").performScrollTo().performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("create_exam_quick_template_btn").performScrollTo().performClick()
        composeRule.onNodeWithTag("create_exam_quick_parse_btn").performScrollTo().performClick()
        composeRule.waitForIdle()

        // Open Preview
        composeRule.onNodeWithTag("create_exam_preview_btn").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("create_exam_preview_dialog").assertIsDisplayed()

        // Tap Edit All so all questions can be edited together
        composeRule.onNodeWithTag("create_exam_preview_edit_all_btn").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("create_exam_preview_editor_1").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag("create_exam_preview_editor_2").performScrollTo().assertIsDisplayed()

        // Modify Question 1 text and correct option, save all changes
        composeRule.onNodeWithTag("create_exam_preview_qtext_1").performScrollTo().performTextReplacement("Updated Height Question?")
        composeRule.onNodeWithTag("create_exam_preview_correct_1_2").performScrollTo().performClick()
        composeRule.onNodeWithTag("create_exam_preview_save_all_btn").performClick()
        composeRule.waitForIdle()

        // Delete Question 2 directly inside Preview
        composeRule.onNodeWithTag("create_exam_preview_delete_q_2").performScrollTo().performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("create_exam_preview_q_2").assertDoesNotExist()

        // Publish from Preview and verify updated + remaining question persisted
        composeRule.onNodeWithTag("create_exam_preview_publish_btn").performClick()
        composeRule.waitForIdle()
        assertEquals(1, publishedExam?.questions?.size)
        assertEquals("Updated Height Question?", publishedExam?.questions?.firstOrNull()?.questionText)
        assertEquals(2, publishedExam?.questions?.firstOrNull()?.correctIndex)
    }

    @Test
    fun `exam account lets creator edit existing exam and edit or delete questions in preview`() {
        val initialExam = ExamGroundDefaults.defaultCommunityExams.first().copy(
            id = "acc_exam_99",
            title = "Creator Physics Exam",
            isPublished = false
        )
        var editedExamId: String? = null
        var updatedFromPreview: CustomPublishedExam? = null

        composeRule.setContent {
            var exams by remember { mutableStateOf(listOf(initialExam)) }
            MyApplicationTheme {
                ExamAccountScreen(
                    userName = "Tofail",
                    batchTag = "HSC-26",
                    totalExamsTaken = 2,
                    userCreatedExams = exams,
                    onBack = {},
                    onCreateExamClick = {},
                    onTogglePublish = {},
                    onDeleteExam = { id -> exams = exams.filterNot { it.id == id } },
                    onTakeCustomExam = {},
                    onEditExam = { editedExamId = it.id },
                    onUpdateExam = { updated ->
                        updatedFromPreview = updated
                        exams = exams.map { if (it.id == updated.id) updated else it }
                    }
                )
            }
        }

        composeRule.onNodeWithTag("exam_account_list").performScrollToNode(hasTestTag("exam_account_item_acc_exam_99"))

        // Verify Edit Exam action triggers edit navigation callback
        composeRule.onNodeWithTag("exam_account_edit_acc_exam_99").performClick()
        assertEquals("acc_exam_99", editedExamId)

        // Open Edit Qs preview dialog directly from Exam Account
        composeRule.onNodeWithTag("exam_account_preview_qs_acc_exam_99").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("create_exam_preview_dialog").assertIsDisplayed()

        // Edit Q1 inline and save
        composeRule.onNodeWithTag("create_exam_preview_edit_q_1").performScrollTo().performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("create_exam_preview_qtext_1").performScrollTo().performTextReplacement("Account Edited Q1")
        composeRule.onNodeWithTag("create_exam_preview_save_q_1").performScrollTo().performClick()
        composeRule.waitForIdle()

        assertEquals("Account Edited Q1", updatedFromPreview?.questions?.firstOrNull()?.questionText)
        assertEquals("acc_exam_99", updatedFromPreview?.id)

        // Delete Q2 directly inside Exam Account Preview
        if (initialExam.questions.size >= 2) {
            composeRule.onNodeWithTag("create_exam_preview_delete_q_2").performScrollTo().performClick()
            composeRule.waitForIdle()
            assertEquals(initialExam.questions.size - 1, updatedFromPreview?.questions?.size)
        }
    }

    @Test
    fun `home screen displays story option and folio library renders books on wooden shelves`() {
        var storyOpened = false
        var openedBookId: String? = null

        composeRule.setContent {
            var showStory by remember { mutableStateOf(false) }
            MyApplicationTheme {
                if (!showStory) {
                    HomeScreen(
                        homeState = HomeUiState(),
                        prefs = UserPrefsState(userName = "Jenny"),
                        onOpenProfile = {},
                        onOpenQuran = {},
                        onOpenSurah = {},
                        onOpenDua = {},
                        onOpenDzikir = {},
                        onOpenGroups = {},
                        onOpenStory = {
                            storyOpened = true
                            showStory = true
                        },
                        onOpenMore = {},
                        onCycleDailyAyah = {},
                        onToggleAyahBookmark = { _, _ -> },
                        onToggleAdzanMaster = {},
                        onToggleAdzanPrayer = {},
                        onTogglePrayerDone = {}
                    )
                } else {
                    StoryHomeScreen(
                        userName = "Jenny",
                        onBack = {},
                        onOpenBook = { bookId, _ -> openedBookId = bookId }
                    )
                }
            }
        }

        // Open Story from Home feature grid
        composeRule.onNodeWithTag("menu_story").performScrollTo().performClick()
        composeRule.waitForIdle()
        assertTrue(storyOpened)

        // Verify Folio wooden shelf UI matches uploaded Folio Pocket Library
        composeRule.onNodeWithTag("story_home_screen").assertIsDisplayed()
        composeRule.onNodeWithText("Folio").assertIsDisplayed()
        composeRule.onNodeWithText("Three books on your shelf. Tap one to open it, then swipe the page to turn it.").assertIsDisplayed()
        composeRule.onNodeWithTag("story_book_card_lantern_keeper").assertIsDisplayed()
        composeRule.onNodeWithTag("story_book_card_quiet_mornings").assertIsDisplayed()
        composeRule.onNodeWithTag("story_home_scroll").performScrollToNode(hasTestTag("story_book_card_atlas_small_wonders"))
        composeRule.onNodeWithTag("story_book_card_atlas_small_wonders").assertIsDisplayed()

        // Scroll back to The Lantern Keeper and tap its Open book pill
        composeRule.onNodeWithTag("story_home_scroll").performScrollToNode(hasTestTag("story_open_pill_lantern_keeper"))
        composeRule.onNodeWithTag("story_open_pill_lantern_keeper").performClick()
        composeRule.mainClock.advanceTimeBy(600)
        org.robolectric.shadows.ShadowLooper.idleMainLooper(600, java.util.concurrent.TimeUnit.MILLISECONDS)
        composeRule.waitForIdle()
        assertEquals("lantern_keeper", openedBookId)
    }

    @Test
    fun `folio 3d book reader renders cover, turns pages forward and back, and reaches the end`() {
        val sampleBook = StoryLibraryRepository.getBookById("lantern_keeper")!!
        var lastLoggedPage = -1
        var toggledBookmark = false

        composeRule.setContent {
            MyApplicationTheme {
                StoryBookReaderScreen(
                    bookId = sampleBook.id,
                    initialPageIndex = 0,
                    bookmarkedPages = emptySet(),
                    onBack = {},
                    onPageChanged = { _, pageIdx -> lastLoggedPage = pageIdx },
                    onToggleBookmarkPage = { _, _ -> toggledBookmark = true }
                )
            }
        }

        // Verify Cover leaf (leaf 0), running bar title, and drag hint
        composeRule.onNodeWithTag("story_reader_screen").assertIsDisplayed()
        composeRule.onNodeWithText("The Lantern Keeper").assertIsDisplayed()
        composeRule.onNodeWithTag("story_reader_page_indicator").assertTextContains("Cover")
        composeRule.onNodeWithTag("story_reader_hint").assertIsDisplayed()
        assertEquals(0, lastLoggedPage)

        // Turn from Cover to Page 1 of 4
        composeRule.onNodeWithTag("story_reader_next_btn").performClick()
        composeRule.mainClock.advanceTimeBy(1000)
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("story_reader_page_indicator").assertTextContains("Page 1 of 4")
        composeRule.onNodeWithTag("story_reader_curl_corner").assertIsDisplayed()
        assertEquals(1, lastLoggedPage)

        // Turn via curled corner to Page 2 of 4
        composeRule.onNodeWithTag("story_reader_curl_corner").performClick()
        composeRule.mainClock.advanceTimeBy(1000)
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("story_reader_page_indicator").assertTextContains("Page 2 of 4")
        assertEquals(2, lastLoggedPage)

        // Bookmark current page
        composeRule.onNodeWithTag("story_reader_bookmark_btn").performClick()
        assertTrue(toggledBookmark)

        // Turn back to Page 1 of 4 via Prev button
        composeRule.onNodeWithTag("story_reader_prev_btn").performClick()
        composeRule.mainClock.advanceTimeBy(1000)
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("story_reader_page_indicator").assertTextContains("Page 1 of 4")
        assertEquals(1, lastLoggedPage)

        // Turn forward through remaining pages to reach The end
        repeat(4) {
            composeRule.onNodeWithTag("story_reader_next_btn").performClick()
            composeRule.mainClock.advanceTimeBy(1000)
            composeRule.waitForIdle()
        }
        composeRule.onNodeWithTag("story_reader_page_indicator").assertTextContains("The end")
        assertEquals(5, lastLoggedPage)
    }

    @Test
    fun `mission time helper splits 24 hours into exact 6 four-hour blocks starting 5 AM`() {
        assertEquals(6, MissionTimeHelper.blocks.size)
        assertEquals("05:00 AM – 09:00 AM", MissionTimeHelper.blocks[0].rangeFullEn)
        assertEquals("09:00 AM – 01:00 PM", MissionTimeHelper.blocks[1].rangeFullEn)
        assertEquals("01:00 PM – 05:00 PM", MissionTimeHelper.blocks[2].rangeFullEn)
        assertEquals("05:00 PM – 09:00 PM", MissionTimeHelper.blocks[3].rangeFullEn)
        assertEquals("09:00 PM – 01:00 AM", MissionTimeHelper.blocks[4].rangeFullEn)
        assertEquals("01:00 AM – 05:00 AM", MissionTimeHelper.blocks[5].rangeFullEn)

        // Verify hour-to-block mapping including midnight wrap-around
        assertEquals(0, MissionTimeHelper.blockIndexForHour(5))
        assertEquals(0, MissionTimeHelper.blockIndexForHour(8))
        assertEquals(1, MissionTimeHelper.blockIndexForHour(9))
        assertEquals(1, MissionTimeHelper.blockIndexForHour(12))
        assertEquals(2, MissionTimeHelper.blockIndexForHour(13))
        assertEquals(3, MissionTimeHelper.blockIndexForHour(17))
        assertEquals(4, MissionTimeHelper.blockIndexForHour(21))
        assertEquals(4, MissionTimeHelper.blockIndexForHour(0))
        assertEquals(5, MissionTimeHelper.blockIndexForHour(1))
        assertEquals(5, MissionTimeHelper.blockIndexForHour(4))
    }

    @Test
    fun `home opens mission hub, clicking a date opens 24h 4-hour blocks, and user sets mission with custom alarm`() {
        var missionOpened = false
        val todayKey = MissionTimeHelper.todayDateKey()
        val futureDateKeys = MissionTimeHelper.generateDateStripKeys(todayKey, daysBefore = 0, daysAfter = 3)
        val tomorrowKey = futureDateKeys[1]

        composeRule.setContent {
            var showMission by remember { mutableStateOf(false) }
            var missionList by remember { mutableStateOf(emptyList<MissionItem>()) }
            var liveNotif by remember { mutableStateOf(false) }

            MyApplicationTheme {
                if (!showMission) {
                    HomeScreen(
                        homeState = HomeUiState(),
                        prefs = UserPrefsState(),
                        onOpenProfile = {},
                        onOpenQuran = {},
                        onOpenSurah = {},
                        onOpenDua = {},
                        onOpenDzikir = {},
                        onOpenGroups = {},
                        onOpenMission = {
                            missionOpened = true
                            showMission = true
                        },
                        onOpenMore = {},
                        onCycleDailyAyah = {},
                        onToggleAyahBookmark = { _, _ -> },
                        onToggleAdzanMaster = {},
                        onToggleAdzanPrayer = {},
                        onTogglePrayerDone = {}
                    )
                } else {
                    MissionScreen(
                        missions = missionList,
                        liveNotifEnabled = liveNotif,
                        onBack = { showMission = false },
                        onSaveMission = { item, _ ->
                            missionList = missionList + item
                        },
                        onToggleMissionCompleted = { id ->
                            missionList = missionList.map {
                                if (it.id == id) it.copy(isCompleted = !it.isCompleted) else it
                            }
                        },
                        onDeleteMission = { id ->
                            missionList = missionList.filterNot { it.id == id }
                        },
                        onToggleLiveNotification = { liveNotif = it },
                        onCopyDayToNext7Days = {},
                        onSyncWidgetNow = {}
                    )
                }
            }
        }

        // Open Mission from Home grid
        composeRule.onNodeWithTag("menu_mission").performScrollTo().performClick()
        composeRule.waitForIdle()
        assertTrue(missionOpened)
        composeRule.onNodeWithTag("mission_screen").assertIsDisplayed()
        composeRule.onNodeWithTag("mission_live_hero_card").assertIsDisplayed()

        // Click tomorrow's date card in Date Hub to open that date in 24h (6 x 4h blocks) view
        composeRule.onNodeWithTag("mission_date_hub_list").performScrollToNode(hasTestTag("mission_date_card_$tomorrowKey"))
        composeRule.onNodeWithTag("mission_date_card_$tomorrowKey").performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithTag("mission_24h_list").assertIsDisplayed()
        composeRule.onNodeWithTag("mission_24h_list").performScrollToNode(hasTestTag("mission_slot_card_0"))
        composeRule.onNodeWithTag("mission_slot_card_0").assertIsDisplayed()
        composeRule.onNodeWithText("05:00 AM – 09:00 AM").performScrollTo().assertIsDisplayed()

        // Tap Set Mission for 5 AM - 9 AM block
        composeRule.onNodeWithTag("mission_set_slot_0").performScrollTo().performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("mission_editor_dialog").assertIsDisplayed()

        // Fill mission title, choose Custom Alarm Time (+15m -> 05:15 AM), and Save
        composeRule.onNodeWithTag("mission_title_input").performScrollTo().performTextInput("Morning Physics Optics")
        composeRule.onNodeWithTag("mission_alarm_mode_${MissionAlarmMode.CUSTOM_TIME.name}").performScrollTo().performClick()
        composeRule.onNodeWithTag("mission_custom_min_plus").performScrollTo().performClick()
        composeRule.onNodeWithTag("mission_custom_time_display").assertTextContains("05:15 AM")
        composeRule.onNodeWithTag("mission_save_btn").performScrollTo().performClick()
        composeRule.waitForIdle()

        // Open Adzan-style popup for 05:00 AM - 09:00 AM block and verify saved mission & alarm summary
        composeRule.onNodeWithTag("mission_24h_list").performScrollToNode(hasTestTag("mission_slot_card_0"))
        composeRule.onNodeWithTag("mission_slot_dropdown_0").performScrollTo().performClick()
        composeRule.waitForIdle()
        composeRule.onAllNodesWithText("Morning Physics Optics").onLast().performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText("Alarm 05:15 AM").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun `mission storage mirror and scheduler handle live notification and 4h rollover sync`() {
        val ctx = ApplicationProvider.getApplicationContext<Context>()
        val today = MissionTimeHelper.todayDateKey()
        val activeSlotIdx = MissionTimeHelper.blockIndexForHour(java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY))
        val item = MissionItem(
            id = "msn_test_live",
            dateKey = today,
            blockIndex = activeSlotIdx,
            title = "Real-Time 4h Focus Sprint",
            notes = "Solve past paper set",
            category = "Physics",
            isCompleted = false,
            alarmEnabled = true
        )

        MissionScheduler.syncAll(ctx, listOf(item), liveNotifEnabled = true)
        val restored = MissionStorageHelper.readMissionsFromMirror(ctx)
        assertEquals(1, restored.size)
        assertEquals("Real-Time 4h Focus Sprint", restored.first().title)
        assertTrue(MissionStorageHelper.isLiveNotificationEnabledFromMirror(ctx))

        val toggled = MissionStorageHelper.toggleMissionCompletedInMirror(ctx, "msn_test_live")
        assertTrue(toggled.first().isCompleted)
    }

    @Test
    fun `hero mission timer card mimics home top timer box and expands adzan-style 4h popup`() {
        composeRule.setContent {
            MyApplicationTheme {
                MissionScreen(
                    missions = MissionTimeHelper.createStarterMissions(),
                    liveNotifEnabled = true,
                    onBack = {},
                    onSaveMission = { _, _ -> },
                    onToggleMissionCompleted = {},
                    onDeleteMission = {},
                    onToggleLiveNotification = {},
                    onCopyDayToNext7Days = {},
                    onSyncWidgetNow = {}
                )
            }
        }

        // Top timer box renders live HH:MM:SS countdown
        composeRule.onNodeWithTag("mission_hero_countdown").assertIsDisplayed()
        composeRule.onNodeWithTag("mission_dropdown_button").assertIsDisplayed().performClick()
        composeRule.waitForIdle()

        // Adzan-style popup opens with all 6 x 4-hour rows
        composeRule.onNodeWithTag("mission_adzan_popup_content").assertIsDisplayed()
        for (idx in 0..5) {
            composeRule.onNodeWithTag("mission_adzan_row_$idx").performScrollTo().assertIsDisplayed()
        }
    }

    @Test
    fun `24h mission supports subject paper chapter ক খ গ ঘ cq mcq selection and stacking multiple sets`() {
        composeRule.setContent {
            var missionList by remember { mutableStateOf(emptyList<MissionItem>()) }

            MyApplicationTheme {
                MissionScreen(
                    missions = missionList,
                    liveNotifEnabled = false,
                    trackerSubjects = com.example.data.model.TrackerDefaults.defaultSubjects,
                    onBack = {},
                    onSaveMission = { item, _ ->
                        missionList = missionList + item
                    },
                    onToggleMissionCompleted = { id ->
                        missionList = missionList.map {
                            if (it.id == id) it.copy(isCompleted = !it.isCompleted) else it
                        }
                    },
                    onToggleMissionTag = { id, tag ->
                        missionList = missionList.map { item ->
                            if (item.id == id) {
                                val nextTags = if (tag in item.completedTags) {
                                    item.completedTags - tag
                                } else {
                                    item.completedTags + tag
                                }
                                item.copy(
                                    completedTags = nextTags,
                                    isCompleted = item.targetTags.isNotEmpty() && item.targetTags.all { it in nextTags }
                                )
                            } else item
                        }
                    },
                    onDeleteMission = { id ->
                        missionList = missionList.filterNot { it.id == id }
                    },
                    onToggleLiveNotification = {},
                    onCopyDayToNext7Days = {},
                    onSyncWidgetNow = {}
                )
            }
        }

        // Switch directly to 24h Day view
        composeRule.onNodeWithTag("mission_tab_24h").performClick()
        composeRule.waitForIdle()

        // Add first set inside 05:00 AM - 09:00 AM block with Subject, Paper 2, and ক, গ, CQ, MCQ
        composeRule.onNodeWithTag("mission_24h_list").performScrollToNode(hasTestTag("mission_set_slot_0"))
        composeRule.onNodeWithTag("mission_set_slot_0").performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithTag("mission_subject_chip_trk_physics").performScrollTo().performClick()
        composeRule.onNodeWithTag("mission_paper_btn_2").performScrollTo().performClick()
        // Default tags include KA, KHA, MCQ -> toggle off KHA, toggle on GA and CQ so selection is KA, GA, CQ, MCQ
        composeRule.onNodeWithTag("mission_tag_select_${com.example.data.model.PrepQuestionTag.KHA.name}").performScrollTo().performClick()
        composeRule.onNodeWithTag("mission_tag_select_${com.example.data.model.PrepQuestionTag.GA.name}").performScrollTo().performClick()
        composeRule.onNodeWithTag("mission_tag_select_${com.example.data.model.PrepQuestionTag.CQ.name}").performScrollTo().performClick()
        composeRule.onNodeWithTag("mission_title_input").performScrollTo().performTextInput("Thermodynamics Drill")
        composeRule.onNodeWithTag("mission_save_btn").performScrollTo().performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithTag("mission_24h_list").performScrollToNode(hasTestTag("mission_slot_card_0"))
        composeRule.onNodeWithTag("mission_slot_dropdown_0").performScrollTo().performClick()
        composeRule.waitForIdle()
        composeRule.onAllNodesWithText("Thermodynamics Drill").onLast().performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText("Physics · 2nd Paper", substring = true).performScrollTo().assertIsDisplayed()

        // Tap "Add More Set" in the SAME 05:00 AM - 09:00 AM block to stack a Chemistry set
        composeRule.onNodeWithTag("mission_24h_list").performScrollToNode(hasTestTag("mission_add_more_set_0"))
        composeRule.onNodeWithTag("mission_add_more_set_0").performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithTag("mission_subject_chip_trk_chemistry").performScrollTo().performClick()
        composeRule.onNodeWithTag("mission_title_input").performScrollTo().performTextInput("Organic Synthesis Set")
        composeRule.onNodeWithTag("mission_save_btn").performScrollTo().performClick()
        composeRule.waitForIdle()

        // Both sets exist inside the same 4-hour block
        composeRule.onNodeWithTag("mission_24h_list").performScrollToNode(hasTestTag("mission_slot_card_0"))
        composeRule.onAllNodesWithText("Thermodynamics Drill").onLast().performScrollTo().assertIsDisplayed()
        composeRule.onAllNodesWithText("Organic Synthesis Set").onLast().performScrollTo().assertIsDisplayed()
    }

    @Test
    fun `every 4h card looks like top timer and opens adzan-style down popup with click-to-select options`() {
        composeRule.setContent {
            var missionList by remember { mutableStateOf(emptyList<MissionItem>()) }

            MyApplicationTheme {
                MissionScreen(
                    missions = missionList,
                    liveNotifEnabled = true,
                    trackerSubjects = com.example.data.model.TrackerDefaults.defaultSubjects,
                    onBack = {},
                    onSaveMission = { item, _ ->
                        missionList = missionList + item
                    },
                    onToggleMissionCompleted = { id ->
                        missionList = missionList.map {
                            if (it.id == id) it.copy(isCompleted = !it.isCompleted) else it
                        }
                    },
                    onToggleMissionTag = { id, tag ->
                        missionList = missionList.map { item ->
                            if (item.id == id) {
                                val nextTags = if (tag in item.completedTags) {
                                    item.completedTags - tag
                                } else {
                                    item.completedTags + tag
                                }
                                item.copy(
                                    completedTags = nextTags,
                                    isCompleted = item.targetTags.isNotEmpty() && item.targetTags.all { it in nextTags }
                                )
                            } else item
                        }
                    },
                    onDeleteMission = { id ->
                        missionList = missionList.filterNot { it.id == id }
                    },
                    onToggleLiveNotification = {},
                    onCopyDayToNext7Days = {},
                    onSyncWidgetNow = {}
                )
            }
        }

        // Open 24h view -> verify every 4h block renders Top-Timer clock + dropdown button
        composeRule.onNodeWithTag("mission_tab_24h").performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithTag("mission_24h_list").performScrollToNode(hasTestTag("mission_slot_card_0"))
        composeRule.onNodeWithTag("mission_slot_clock_0", useUnmergedTree = true).assertIsDisplayed()
        composeRule.onNodeWithTag("mission_slot_dropdown_0").performScrollTo().performClick()
        composeRule.waitForIdle()

        // Adzan-style down popup opens inside 5 AM - 9 AM card with click-to-select options
        composeRule.onNodeWithTag("mission_slot_popup_0").assertExists()
        composeRule.onNodeWithTag("mission_slot_inline_add_0").performScrollTo().assertIsDisplayed()

        // Click Subject (Physics), Paper 2, select option ক/খ/গ/MCQ (add GA), and click Add without typing anything
        composeRule.onNodeWithTag("mission_inline_subj_0_trk_physics").performScrollTo().performClick()
        composeRule.onNodeWithTag("mission_inline_paper_0_2").performScrollTo().performClick()
        composeRule.onNodeWithTag("mission_inline_tag_0_${com.example.data.model.PrepQuestionTag.GA.name}")
            .performScrollTo().performClick()
        composeRule.onNodeWithTag("mission_inline_save_0").performScrollTo().performClick()
        composeRule.waitForIdle()

        // Verify saved mission immediately appears inside the opened Adzan popup rows
        composeRule.onNodeWithTag("mission_slot_popup_0").assertExists()
        composeRule.onNodeWithText("Physics · 2nd Paper", substring = true).performScrollTo().assertIsDisplayed()
    }
}
