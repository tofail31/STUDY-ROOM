package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.MyLocation
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.data.local.IslamicContentData
import com.example.data.model.CalculationMethodOption
import com.example.data.model.CityPreset
import com.example.data.preferences.UserPrefsState
import com.example.ui.components.NeumorphicCard
import com.example.ui.components.NeumorphicPillButton
import com.example.ui.components.NeumorphicSubScreenHeader
import com.example.ui.components.ProfileRingIcon
import com.example.ui.theme.StoneAccent
import com.example.ui.theme.StoneBackground
import com.example.ui.theme.StoneBackgroundBottom
import com.example.ui.theme.StoneBackgroundTop
import com.example.ui.theme.StoneDivider
import com.example.ui.theme.StoneInkPrimary
import com.example.ui.theme.StoneInkSecondary
import com.example.ui.theme.StoneSurface
import com.example.ui.viewmodel.HomeUiState

@Composable
fun ProfileScreen(
    homeState: HomeUiState,
    prefs: UserPrefsState,
    onBack: () -> Unit,
    onUpdateUserName: (String) -> Unit,
    onUpdateLanguage: (String) -> Unit,
    onDetectGpsLocation: () -> Unit,
    onSelectCity: (CityPreset) -> Unit,
    onSelectCalcMethod: (CalculationMethodOption) -> Unit,
    onClearStatusMessage: () -> Unit
) {
    val context = LocalContext.current
    val s = com.example.ui.theme.LocalAppStrings.current
    val snackbarHostState = remember { SnackbarHostState() }
    var nameInput by remember(prefs.userName) { mutableStateOf(prefs.userName) }

    val locationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            onDetectGpsLocation()
        }
    }

    LaunchedEffect(homeState.locationStatusMessage) {
        val msg = homeState.locationStatusMessage
        if (!msg.isNullOrBlank()) {
            snackbarHostState.showSnackbar(msg)
            onClearStatusMessage()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(StoneBackgroundTop, StoneBackground, StoneBackgroundBottom))
            )
            .statusBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            NeumorphicSubScreenHeader(
                title = s.profileTitle,
                subtitle = s.profileSubtitle,
                onBack = onBack
            )

            LazyColumn(
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                // Language Switcher Card (English / বাংলা)
                item {
                    NeumorphicCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 22.dp) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                text = s.languageSectionTitle,
                                style = MaterialTheme.typography.titleMedium,
                                color = StoneInkPrimary
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                NeumorphicPillButton(
                                    text = s.languageEnglish,
                                    selected = prefs.appLanguage == "en",
                                    onClick = { onUpdateLanguage("en") },
                                    modifier = Modifier.weight(1f).testTag("lang_en")
                                )
                                NeumorphicPillButton(
                                    text = s.languageBangla,
                                    selected = prefs.appLanguage == "bn",
                                    onClick = { onUpdateLanguage("bn") },
                                    modifier = Modifier.weight(1f).testTag("lang_bn")
                                )
                            }
                        }
                    }
                }

                item {
                    NeumorphicCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 22.dp) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                ProfileRingIcon(modifier = Modifier.size(48.dp))
                                Spacer(modifier = Modifier.width(14.dp))
                                Column {
                                    Text(s.greetingSalam, style = MaterialTheme.typography.labelMedium, color = StoneInkSecondary)
                                    Text(prefs.userName, style = MaterialTheme.typography.titleLarge, color = StoneInkPrimary)
                                }
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                            OutlinedTextField(
                                value = nameInput,
                                onValueChange = { nameInput = it },
                                label = { Text(s.displayNameLabel) },
                                singleLine = true,
                                shape = RoundedCornerShape(16.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = StoneAccent,
                                    unfocusedBorderColor = StoneDivider,
                                    focusedContainerColor = StoneSurface,
                                    unfocusedContainerColor = StoneSurface,
                                    focusedTextColor = StoneInkPrimary,
                                    unfocusedTextColor = StoneInkPrimary
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            NeumorphicPillButton(
                                text = s.saveName,
                                selected = true,
                                onClick = { onUpdateUserName(nameInput) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                // Ibadah summary stats
                item {
                    val isSameDate = prefs.completedPrayersDate == homeState.todayKey
                    val doneCount = if (isSameDate) prefs.completedPrayersToday.size else 0

                    NeumorphicCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 22.dp) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(s.worshipSummary, style = MaterialTheme.typography.titleMedium, color = StoneInkPrimary)
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                StatMiniColumn(s.prayersToday, "$doneCount / 5")
                                StatMiniColumn(s.totalDhikr, prefs.dzikirLifetimeTotal.toString())
                                StatMiniColumn(s.savedAyahs, prefs.bookmarkedAyahKeys.size.toString())
                            }
                        }
                    }
                }

                // GPS & City selector
                item {
                    NeumorphicCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 22.dp) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(s.locationSectionTitle, style = MaterialTheme.typography.titleMedium, color = StoneInkPrimary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("${s.activeLabel} ${prefs.cityName}", style = MaterialTheme.typography.bodyMedium, color = StoneAccent, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.height(12.dp))

                            NeumorphicPillButton(
                                text = if (homeState.isRefreshingLocation) s.detectingGps else s.useDeviceGps,
                                selected = true,
                                leadingContent = {
                                    if (homeState.isRefreshingLocation) {
                                        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = StoneAccent)
                                    } else {
                                        Icon(Icons.Rounded.MyLocation, contentDescription = null, tint = StoneInkPrimary, modifier = Modifier.size(16.dp))
                                    }
                                },
                                onClick = {
                                    val granted = ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    ) == PackageManager.PERMISSION_GRANTED
                                    if (granted) {
                                        onDetectGpsLocation()
                                    } else {
                                        locationLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(14.dp))
                            Text(s.orSelectCity, style = MaterialTheme.typography.labelMedium, color = StoneInkSecondary)
                            Spacer(modifier = Modifier.height(8.dp))

                            IslamicContentData.cityPresets.forEach { city ->
                                val isSelected = prefs.cityId == city.id
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable { onSelectCity(city) }
                                        .padding(horizontal = 10.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${city.name}, ${city.country}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) StoneAccent else StoneInkPrimary
                                    )
                                    if (isSelected) {
                                        Icon(Icons.Rounded.Check, contentDescription = "Selected", tint = StoneAccent, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                // Calculation Method
                item {
                    NeumorphicCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 22.dp) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(s.calcMethodTitle, style = MaterialTheme.typography.titleMedium, color = StoneInkPrimary)
                            Spacer(modifier = Modifier.height(8.dp))
                            CalculationMethodOption.entries.forEach { method ->
                                val isSelected = prefs.calcMethod == method
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable { onSelectCalcMethod(method) }
                                        .padding(horizontal = 10.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = method.label,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) StoneAccent else StoneInkPrimary,
                                        modifier = Modifier.weight(1f)
                                    )
                                    if (isSelected) {
                                        Icon(Icons.Rounded.Check, contentDescription = "Dipilih", tint = StoneAccent, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .windowInsetsPadding(WindowInsets.navigationBars)
        )
    }
}

@Composable
private fun StatMiniColumn(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = StoneInkPrimary)
        Text(label, style = MaterialTheme.typography.labelSmall, color = StoneInkSecondary)
    }
}
