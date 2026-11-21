package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.calc.PrayerTimeCalculator
import com.example.data.local.AsmaulHusnaData
import com.example.data.local.IslamicContentData
import com.example.ui.components.NeumorphicCard
import com.example.ui.components.NeumorphicPillButton
import com.example.ui.components.NeumorphicSubScreenHeader
import com.example.ui.theme.AmiriArabicFamily
import com.example.ui.theme.LocalAppStrings
import com.example.ui.theme.StoneAccent
import com.example.ui.theme.StoneBackground
import com.example.ui.theme.StoneBackgroundBottom
import com.example.ui.theme.StoneBackgroundTop
import com.example.ui.theme.StoneDivider
import com.example.ui.theme.StoneInkPrimary
import com.example.ui.theme.StoneInkSecondary
import com.example.ui.theme.StoneSurface

@Composable
fun MoreScreen(
    onBack: () -> Unit,
    onOpenProfile: () -> Unit
) {
    val s = LocalAppStrings.current
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Asmaul Husna, 1 = Hijri Calendar, 2 = Wudhu Guide
    var asmaQuery by remember { mutableStateOf("") }

    val filteredAsma = remember(asmaQuery) {
        AsmaulHusnaData.items.filter { item ->
            asmaQuery.isBlank() ||
                item.latin.contains(asmaQuery, ignoreCase = true) ||
                item.meaning.contains(asmaQuery, ignoreCase = true) ||
                item.number.toString() == asmaQuery.trim()
        }
    }

    val (hijriMonth, hijriYear) = remember(s.isBangla) {
        PrayerTimeCalculator.getHijriMonthYear(isBangla = s.isBangla)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(StoneBackgroundTop, StoneBackground, StoneBackgroundBottom))
            )
            .statusBarsPadding()
    ) {
        NeumorphicSubScreenHeader(
            title = s.moreTitle,
            subtitle = s.moreSubtitle,
            onBack = onBack,
            trailingAction = {
                NeumorphicPillButton(text = s.profilePill, onClick = onOpenProfile)
            }
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            NeumorphicPillButton(
                text = s.tabAsmaulHusna,
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                modifier = Modifier.weight(1f)
            )
            NeumorphicPillButton(
                text = s.tabCalendar,
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                modifier = Modifier.weight(1f)
            )
            NeumorphicPillButton(
                text = s.tabWudhu,
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        when (selectedTab) {
            0 -> {
                OutlinedTextField(
                    value = asmaQuery,
                    onValueChange = { asmaQuery = it },
                    placeholder = { Text(s.asmaSearchHint) },
                    leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null, tint = StoneInkSecondary) },
                    singleLine = true,
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = StoneAccent,
                        unfocusedBorderColor = StoneDivider,
                        focusedContainerColor = StoneSurface,
                        unfocusedContainerColor = StoneSurface,
                        focusedTextColor = StoneInkPrimary,
                        unfocusedTextColor = StoneInkPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                LazyColumn(
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .windowInsetsPadding(WindowInsets.navigationBars)
                ) {
                    items(filteredAsma, key = { it.number }) { item ->
                        NeumorphicCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 20.dp) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(StoneAccent.copy(alpha = 0.14f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = item.number.toString(),
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = StoneAccent
                                    )
                                }
                                Spacer(modifier = Modifier.width(14.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(item.latin, style = MaterialTheme.typography.titleMedium, color = StoneInkPrimary)
                                    Text(item.meaning, style = MaterialTheme.typography.bodyMedium, color = StoneInkSecondary)
                                }
                                Text(
                                    text = item.arabic,
                                    fontFamily = AmiriArabicFamily,
                                    fontSize = 24.sp,
                                    color = StoneInkPrimary
                                )
                            }
                        }
                    }
                }
            }

            1 -> {
                LazyColumn(
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .windowInsetsPadding(WindowInsets.navigationBars)
                ) {
                    item {
                        val yearDisplay = if (s.isBangla) PrayerTimeCalculator.toBanglaDigits("$hijriYear হি.") else "$hijriYear H"
                        NeumorphicCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 22.dp) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Text(s.currentHijriMonth, style = MaterialTheme.typography.labelMedium, color = StoneInkSecondary)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "$hijriMonth $yearDisplay",
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = StoneInkPrimary
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = PrayerTimeCalculator.formatCombinedDate(isBangla = s.isBangla),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = StoneAccent,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    items(IslamicContentData.hijriMilestones, key = { it.hijriDate }) { milestone ->
                        NeumorphicCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 20.dp) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = milestone.hijriDate,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = StoneAccent
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(milestone.title, style = MaterialTheme.typography.titleMedium, color = StoneInkPrimary)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(milestone.description, style = MaterialTheme.typography.bodyMedium, color = StoneInkSecondary)
                            }
                        }
                    }
                }
            }

            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .windowInsetsPadding(WindowInsets.navigationBars)
                ) {
                    items(IslamicContentData.wudhuSteps, key = { it.stepNumber }) { step ->
                        NeumorphicCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 20.dp) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(30.dp)
                                            .clip(CircleShape)
                                            .background(StoneAccent.copy(alpha = 0.16f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = step.stepNumber.toString(),
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = StoneAccent
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(step.title, style = MaterialTheme.typography.titleMedium, color = StoneInkPrimary)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(step.description, style = MaterialTheme.typography.bodyMedium, color = StoneInkSecondary)

                                if (!step.arabic.isNullOrBlank()) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = step.arabic,
                                        fontFamily = AmiriArabicFamily,
                                        fontSize = 22.sp,
                                        color = StoneInkPrimary,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                                if (!step.latin.isNullOrBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = step.latin,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontStyle = FontStyle.Italic,
                                        color = StoneAccent
                                    )
                                }
                                if (!step.translation.isNullOrBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "\"${step.translation}\"",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = StoneInkPrimary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
