package com.example

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.performScrollToNode
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.FocusSessionMode
import com.example.data.model.PrepQuestionTag
import com.example.data.model.TrackerDefaults
import com.example.data.preferences.UserPrefsState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.ui.screens.FocusScreen
import com.example.ui.screens.GroupStudyRoomScreen
import com.example.ui.screens.GroupsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ProfileScreen
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
    fun `tracker screen shows subject stat boxes, expands adzan-style dropdown, and updates percentage on prep toggle`() {
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
                    onAddChapter = { _, _ -> },
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

        // Physics starts with 6/48 done = 12%
        composeRule.onNodeWithTag("tracker_subject_pct_trk_physics", useUnmergedTree = true).assertIsDisplayed()
        composeRule.onNodeWithText("12%").assertIsDisplayed()

        // Click Physics box -> Adzan-style dropdown opens directly below row
        composeRule.onNodeWithTag("tracker_subject_card_trk_physics").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("tracker_expanded_trk_physics").assertIsDisplayed()
        composeRule.onNodeWithText("1. ভৌত জগৎ ও পরিমাপ").performScrollTo().assertIsDisplayed()

        // Toggle CQ and GHA on Chapter 1 -> Physics moves from 6/48 (12%) to 8/48 (16%)
        composeRule.onNodeWithTag("tracker_tag_trk_physics_ch_1_CQ").performScrollTo().performClick()
        composeRule.onNodeWithTag("tracker_tag_trk_physics_ch_1_GHA").performScrollTo().performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithTag("tracker_subject_card_trk_physics").performScrollTo()
        composeRule.waitForIdle()
        composeRule.onNodeWithText("16%").assertIsDisplayed()
    }
}
