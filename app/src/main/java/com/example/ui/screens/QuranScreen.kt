package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.calc.PrayerTimeCalculator
import com.example.data.local.QuranCatalogData
import com.example.data.model.SurahSummary
import com.example.ui.components.NeumorphicCard
import com.example.ui.components.NeumorphicCircleButton
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
import com.example.ui.viewmodel.SurahLoadState
import kotlinx.coroutines.launch

@Composable
fun QuranListScreen(
    bookmarkedAyahKeys: Set<String>,
    onBack: () -> Unit,
    onSelectSurah: (Int) -> Unit
) {
    val s = LocalAppStrings.current
    var query by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("ALL") }

    val filters = listOf(
        "ALL" to s.filterAll,
        "Makkiyah" to if (s.isBangla) "মক্কী" else "Makkiyah",
        "Madaniyah" to if (s.isBangla) "মাদানী" else "Madaniyah",
        "SAVED" to s.filterSaved
    )

    val bookmarkedSurahNumbers = remember(bookmarkedAyahKeys) {
        bookmarkedAyahKeys.mapNotNull { it.substringBefore(":").toIntOrNull() }.toSet()
    }

    val filteredSurahs = remember(query, selectedFilter, bookmarkedSurahNumbers) {
        QuranCatalogData.allSurahs.filter { surah ->
            val matchesFilter = when (selectedFilter) {
                "Makkiyah" -> surah.revelationType == "Makkiyah"
                "Madaniyah" -> surah.revelationType == "Madaniyah"
                "SAVED" -> surah.number in bookmarkedSurahNumbers
                else -> true
            }
            val matchesQuery = query.isBlank() ||
                surah.nameLatin.contains(query, ignoreCase = true) ||
                surah.meaning.contains(query, ignoreCase = true) ||
                surah.number.toString() == query.trim()
            matchesFilter && matchesQuery
        }
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
            title = s.quranTitle,
            subtitle = s.quranSubtitle,
            onBack = onBack
        )

        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            placeholder = { Text(s.quranSearchHint) },
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
                .testTag("quran_search_input")
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filters) { (key, label) ->
                NeumorphicPillButton(
                    text = label,
                    selected = selectedFilter == key,
                    onClick = { selectedFilter = key }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (filteredSurahs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = s.quranEmpty,
                    style = MaterialTheme.typography.bodyLarge,
                    color = StoneInkSecondary,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(
                    start = 20.dp,
                    end = 20.dp,
                    top = 4.dp,
                    bottom = 28.dp
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                items(filteredSurahs, key = { it.number }) { surah ->
                    SurahRowCard(
                        surah = surah,
                        hasBookmark = surah.number in bookmarkedSurahNumbers,
                        isBangla = s.isBangla,
                        onClick = { onSelectSurah(surah.number) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SurahRowCard(
    surah: SurahSummary,
    hasBookmark: Boolean,
    isBangla: Boolean,
    onClick: () -> Unit
) {
    val numText = if (isBangla) PrayerTimeCalculator.toBanglaDigits(surah.number.toString()) else surah.number.toString()
    val ayahCountText = if (isBangla) PrayerTimeCalculator.toBanglaDigits(surah.ayahCount.toString()) else surah.ayahCount.toString()
    val ayahLabel = if (isBangla) "আয়াত" else "Ayahs"
    val revLabel = if (isBangla) {
        if (surah.revelationType == "Makkiyah") "মক্কী" else "মাদানী"
    } else surah.revelationType

    NeumorphicCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("surah_card_${surah.number}"),
        cornerRadius = 20.dp,
        shadowElevation = 6.dp,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(StoneAccent.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = numText,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = StoneAccent
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = surah.nameLatin,
                        style = MaterialTheme.typography.titleMedium,
                        color = StoneInkPrimary
                    )
                    if (hasBookmark) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Rounded.Bookmark,
                            contentDescription = if (isBangla) "সংরক্ষিত" else "Saved",
                            tint = StoneAccent,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
                Text(
                    text = "${surah.meaning} • $ayahCountText $ayahLabel ($revLabel)",
                    style = MaterialTheme.typography.labelMedium,
                    color = StoneInkSecondary
                )
            }

            Text(
                text = surah.nameArabic,
                fontFamily = AmiriArabicFamily,
                fontSize = 22.sp,
                color = StoneInkPrimary
            )
        }
    }
}

@Composable
fun SurahDetailScreen(
    surahNumber: Int,
    loadState: SurahLoadState,
    bookmarkedAyahKeys: Set<String>,
    fontSizeSp: Int,
    translationLang: String,
    onBack: () -> Unit,
    onLoadSurah: (Int) -> Unit,
    onToggleBookmark: (Int, Int) -> Unit,
    onChangeFontSize: (Int) -> Unit,
    onChangeTranslationLang: (String) -> Unit
) {
    val s = LocalAppStrings.current
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(surahNumber) {
        onLoadSurah(surahNumber)
    }

    val summary = remember(surahNumber) {
        QuranCatalogData.allSurahs.firstOrNull { it.number == surahNumber }
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
            val ayahLabel = if (s.isBangla) "আয়াত" else "Ayahs"
            NeumorphicSubScreenHeader(
                title = summary?.nameLatin ?: "Surah $surahNumber",
                subtitle = summary?.let {
                    val count = if (s.isBangla) PrayerTimeCalculator.toBanglaDigits(it.ayahCount.toString()) else it.ayahCount.toString()
                    "${it.meaning} • $count $ayahLabel"
                },
                onBack = onBack,
                trailingAction = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        NeumorphicPillButton(
                            text = if (translationLang == "bn") "বাংলা" else "EN",
                            selected = true,
                            onClick = { onChangeTranslationLang(if (translationLang == "bn") "en" else "bn") },
                            modifier = Modifier.testTag("surah_translation_toggle")
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        NeumorphicCircleButton(
                            onClick = { onChangeFontSize(fontSizeSp - 2) },
                            size = 38.dp
                        ) {
                            Icon(Icons.Rounded.Remove, contentDescription = "Decrease font", tint = StoneInkPrimary, modifier = Modifier.size(16.dp))
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        NeumorphicCircleButton(
                            onClick = { onChangeFontSize(fontSizeSp + 2) },
                            size = 38.dp
                        ) {
                            Icon(Icons.Rounded.Add, contentDescription = "Increase font", tint = StoneInkPrimary, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            )

            when (loadState) {
                is SurahLoadState.Idle, is SurahLoadState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = StoneAccent)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(s.loadingSurah, style = MaterialTheme.typography.bodyMedium, color = StoneInkSecondary)
                        }
                    }
                }

                is SurahLoadState.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        NeumorphicCard(modifier = Modifier.fillMaxWidth()) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = loadState.message,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = StoneInkPrimary,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                NeumorphicPillButton(
                                    text = s.retryLoad,
                                    selected = true,
                                    onClick = { onLoadSurah(surahNumber) }
                                )
                            }
                        }
                    }
                }

                is SurahLoadState.Success -> {
                    val content = loadState.content
                    LazyColumn(
                        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 6.dp, bottom = 32.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .windowInsetsPadding(WindowInsets.navigationBars)
                    ) {
                        if (surahNumber != 1 && surahNumber != 9) {
                            item {
                                NeumorphicCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 20.dp) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                                            fontFamily = AmiriArabicFamily,
                                            fontSize = (fontSizeSp + 2).sp,
                                            color = StoneInkPrimary,
                                            textAlign = TextAlign.Center
                                        )
                                        if (!content.isFromNetwork) {
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = s.offlineModeNote,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = StoneAccent
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        items(content.ayahs, key = { it.numberInSurah }) { ayah ->
                            val key = "$surahNumber:${ayah.numberInSurah}"
                            val isBookmarked = key in bookmarkedAyahKeys
                            val shownTranslation = if (translationLang == "bn" && ayah.translationBn.isNotBlank()) {
                                ayah.translationBn
                            } else {
                                ayah.translation
                            }
                            val ayahBadge = if (s.isBangla) {
                                PrayerTimeCalculator.toBanglaDigits(ayah.numberInSurah.toString())
                            } else {
                                ayah.numberInSurah.toString()
                            }

                            NeumorphicCard(
                                modifier = Modifier.fillMaxWidth(),
                                cornerRadius = 22.dp,
                                shadowElevation = 6.dp
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(18.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clip(CircleShape)
                                                .background(StoneAccent.copy(alpha = 0.14f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = ayahBadge,
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = StoneAccent
                                            )
                                        }

                                        Row {
                                            IconButton(onClick = {
                                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                val text = "${ayah.arabic}\n\n\"$shownTranslation\" (Q.S. ${content.summary.nameLatin}: ${ayah.numberInSurah})"
                                                clipboard.setPrimaryClip(ClipData.newPlainText("Ayah", text))
                                                scope.launch { snackbarHostState.showSnackbar(s.copiedAyah) }
                                            }) {
                                                Icon(Icons.Outlined.ContentCopy, contentDescription = "Copy ayah", tint = StoneInkSecondary, modifier = Modifier.size(18.dp))
                                            }
                                            IconButton(onClick = {
                                                val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                                    type = "text/plain"
                                                    putExtra(
                                                        Intent.EXTRA_TEXT,
                                                        "${ayah.arabic}\n\n\"$shownTranslation\"\n(Q.S. ${content.summary.nameLatin}: ${ayah.numberInSurah})"
                                                    )
                                                }
                                                context.startActivity(Intent.createChooser(sendIntent, "Share Ayah"))
                                            }) {
                                                Icon(Icons.Outlined.Share, contentDescription = "Share ayah", tint = StoneInkSecondary, modifier = Modifier.size(18.dp))
                                            }
                                            IconButton(onClick = { onToggleBookmark(surahNumber, ayah.numberInSurah) }) {
                                                Icon(
                                                    imageVector = if (isBookmarked) Icons.Rounded.Bookmark else Icons.Outlined.BookmarkBorder,
                                                    contentDescription = "Save ayah",
                                                    tint = if (isBookmarked) StoneAccent else StoneInkSecondary,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Text(
                                        text = ayah.arabic,
                                        fontFamily = AmiriArabicFamily,
                                        fontSize = fontSizeSp.sp,
                                        lineHeight = (fontSizeSp * 1.65f).sp,
                                        color = StoneInkPrimary,
                                        textAlign = TextAlign.End,
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    if (ayah.transliteration.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = ayah.transliteration,
                                            style = MaterialTheme.typography.labelMedium,
                                            color = StoneAccent
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Text(
                                        text = shownTranslation,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = StoneInkSecondary
                                    )
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
