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
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import kotlinx.coroutines.launch

@Composable
fun DuaScreen(
    favoriteDuaIds: Set<String>,
    onBack: () -> Unit,
    onToggleFavorite: (String) -> Unit
) {
    val s = LocalAppStrings.current
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var query by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("ALL") }

    val categories = remember(s.isBangla) {
        listOf(
            "ALL" to s.filterAll,
            "FAV" to s.filterFavorite,
            "Morning & Evening" to if (s.isBangla) "সকাল ও সন্ধ্যা" else "Morning & Evening",
            "Daily" to if (s.isBangla) "দৈনন্দিন" else "Daily",
            "Salah" to if (s.isBangla) "নামাজ" else "Salah",
            "Travel" to if (s.isBangla) "সফর" else "Travel",
            "Sustenance & Heart" to if (s.isBangla) "রিযিক ও অন্তর" else "Sustenance & Heart"
        )
    }

    val filtered = remember(query, selectedCategory, favoriteDuaIds) {
        IslamicContentData.duas.filter { dua ->
            val matchesCat = when (selectedCategory) {
                "ALL" -> true
                "FAV" -> dua.id in favoriteDuaIds
                else -> dua.category == selectedCategory
            }
            val matchesQuery = query.isBlank() ||
                dua.title.contains(query, ignoreCase = true) ||
                dua.translation.contains(query, ignoreCase = true)
            matchesCat && matchesQuery
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
                title = s.duaTitle,
                subtitle = s.duaSubtitle,
                onBack = onBack
            )

            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text(s.duaSearchHint) },
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
                    .testTag("dua_search_input")
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(categories) { (key, label) ->
                    NeumorphicPillButton(
                        text = label,
                        selected = selectedCategory == key,
                        onClick = { selectedCategory = key }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (filtered.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize().padding(28.dp), contentAlignment = Alignment.Center) {
                    Text(
                        text = s.duaEmpty,
                        style = MaterialTheme.typography.bodyLarge,
                        color = StoneInkSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .windowInsetsPadding(WindowInsets.navigationBars)
                ) {
                    items(filtered, key = { it.id }) { dua ->
                        val isFav = dua.id in favoriteDuaIds
                        val categoryDisplay = categories.firstOrNull { it.first == dua.category }?.second ?: dua.category
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
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = categoryDisplay.uppercase(),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = StoneAccent,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = dua.title,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = StoneInkPrimary
                                        )
                                    }

                                    IconButton(onClick = {
                                        val clip = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        clip.setPrimaryClip(
                                            ClipData.newPlainText(dua.title, "${dua.title}\n\n${dua.arabic}\n${dua.latin}\n\"${dua.translation}\"")
                                        )
                                        scope.launch { snackbarHostState.showSnackbar(s.copiedDua) }
                                    }) {
                                        Icon(Icons.Outlined.ContentCopy, contentDescription = "Copy du'a", tint = StoneInkSecondary, modifier = Modifier.size(18.dp))
                                    }

                                    IconButton(onClick = {
                                        val share = Intent(Intent.ACTION_SEND).apply {
                                            type = "text/plain"
                                            putExtra(Intent.EXTRA_TEXT, "${dua.title}\n\n${dua.arabic}\n${dua.latin}\n\"${dua.translation}\"\n(${dua.reference})")
                                        }
                                        context.startActivity(Intent.createChooser(share, "Share Du'a"))
                                    }) {
                                        Icon(Icons.Outlined.Share, contentDescription = "Share du'a", tint = StoneInkSecondary, modifier = Modifier.size(18.dp))
                                    }

                                    IconButton(onClick = { onToggleFavorite(dua.id) }) {
                                        Icon(
                                            imageVector = if (isFav) Icons.Rounded.Favorite else Icons.Outlined.FavoriteBorder,
                                            contentDescription = "Favorite du'a",
                                            tint = if (isFav) StoneAccent else StoneInkSecondary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = dua.arabic,
                                    fontFamily = AmiriArabicFamily,
                                    fontSize = 24.sp,
                                    lineHeight = 40.sp,
                                    color = StoneInkPrimary,
                                    textAlign = TextAlign.End,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = dua.latin,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontStyle = FontStyle.Italic,
                                    color = StoneAccent
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "\"${dua.translation}\"",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = StoneInkPrimary
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = dua.reference,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = StoneInkSecondary
                                )
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
