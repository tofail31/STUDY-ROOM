package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.StoryLibraryRepository
import com.example.data.model.FolioBook
import com.example.ui.theme.FrauncesFamily
import com.example.ui.theme.SourceSerif4Family
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val FolioWoodDark = Color(0xFF1D1713)
private val FolioWoodWarm = Color(0xFF3A2C22)
private val FolioShelfTop = Color(0xFF5A4333)
private val FolioShelfBottom = Color(0xFF34261C)
private val FolioBrass = Color(0xFFD9B36A)
private val FolioFoilGold = Color(0xFFF2D98F)
private val FolioTitleWarm = Color(0xFFF3E7D0)
private val FolioSubMuted = Color(0xFFB9A88F)
private val FolioCountMuted = Color(0xFF8F8068)

@Composable
fun StoryHomeScreen(
    userName: String,
    lastReadPages: Map<String, Int> = emptyMap(),
    bookmarkedPages: Set<String> = emptySet(),
    onBack: () -> Unit,
    onOpenBook: (bookId: String, initialPageIndex: Int) -> Unit
) {
    val books = remember { StoryLibraryRepository.books }
    val scope = rememberCoroutineScope()
    var openingBookId by remember { mutableStateOf<String?>(null) }

    // Reset opening animation cleanly if returning back to the library
    LaunchedEffect(Unit) {
        openingBookId = null
    }

    fun triggerBookOpen(book: FolioBook) {
        if (openingBookId != null) return
        openingBookId = book.id
        scope.launch {
            delay(380)
            val savedPage = (lastReadPages[book.id] ?: 0).coerceIn(0, book.totalLeaves - 1)
            onOpenBook(book.id, savedPage)
            openingBookId = null
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D0A08))
            .testTag("story_home_screen"),
        contentAlignment = Alignment.TopCenter
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 460.dp)
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(FolioWoodWarm, FolioWoodDark, FolioWoodDark),
                        center = Offset(520f, 80f),
                        radius = 1250f
                    )
                )
                .statusBarsPadding()
                .windowInsetsPadding(WindowInsets.navigationBars)
        ) {
            LazyColumn(
                contentPadding = PaddingValues(top = 18.dp, bottom = 40.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("story_home_scroll")
            ) {
                item(key = "folio_header") {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 22.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            IconButton(
                                onClick = onBack,
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.08f))
                                    .testTag("story_back_btn")
                            ) {
                                Text(
                                    text = "‹",
                                    fontFamily = SourceSerif4Family,
                                    fontSize = 24.sp,
                                    color = Color(0xFFF0E2C6)
                                )
                            }

                            Text(
                                text = "Folio",
                                fontFamily = FrauncesFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 30.sp,
                                letterSpacing = 0.5.sp,
                                color = FolioBrass
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Three books on your shelf. Tap one to open it, then swipe the page to turn it.",
                            fontFamily = SourceSerif4Family,
                            fontSize = 15.sp,
                            lineHeight = 22.sp,
                            color = FolioSubMuted
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                    }
                }

                items(books, key = { it.id }) { book ->
                    val isOpening = openingBookId == book.id
                    val savedLeaf = lastReadPages[book.id]?.takeIf { it > 0 }
                    FolioBookShelfRow(
                        book = book,
                        isOpening = isOpening,
                        savedLeaf = savedLeaf,
                        onOpen = { triggerBookOpen(book) }
                    )
                }
            }
        }
    }
}

@Composable
private fun FolioBookShelfRow(
    book: FolioBook,
    isOpening: Boolean,
    savedLeaf: Int?,
    onOpen: () -> Unit
) {
    val density = LocalDensity.current.density
    val rotationAngle by animateFloatAsState(
        targetValue = if (isOpening) -62f else -22f,
        animationSpec = tween(durationMillis = 420, easing = FastOutSlowInEasing),
        label = "book_cover_rotation"
    )
    val scaleFactor by animateFloatAsState(
        targetValue = if (isOpening) 1.06f else 1.0f,
        animationSpec = tween(durationMillis = 420, easing = FastOutSlowInEasing),
        label = "book_cover_scale"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onOpen
            )
            .testTag("story_book_card_${book.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // 3D Tilted Hardcover Book + Stacked Page Block behind cover (.bk)
            Box(
                modifier = Modifier
                    .width(116.dp)
                    .height(164.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                // Cream stacked pages edge sticking out behind the cover (.bk::before)
                Box(
                    modifier = Modifier
                        .padding(start = 10.dp, top = 7.dp, bottom = 5.dp)
                        .fillMaxSize()
                        .shadow(12.dp, RoundedCornerShape(topEnd = 3.dp, bottomEnd = 3.dp))
                        .clip(RoundedCornerShape(topEnd = 3.dp, bottomEnd = 3.dp))
                        .background(Color(0xFFF1E6CB))
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val step = 2.dp.toPx()
                        var x = 0f
                        while (x < size.width) {
                            drawLine(
                                color = Color(0xFFC2B186),
                                start = Offset(x, 0f),
                                end = Offset(x, size.height),
                                strokeWidth = 1.dp.toPx()
                            )
                            x += step
                        }
                    }
                }

                // Tilted 3D Leather Cover (.cover)
                Box(
                    modifier = Modifier
                        .width(112.dp)
                        .height(164.dp)
                        .graphicsLayer {
                            rotationY = rotationAngle
                            scaleX = scaleFactor
                            scaleY = scaleFactor
                            transformOrigin = TransformOrigin(0f, 0.5f)
                            cameraDistance = 14f * density
                        }
                        .shadow(
                            elevation = 16.dp,
                            shape = RoundedCornerShape(topStart = 3.dp, bottomStart = 3.dp, topEnd = 7.dp, bottomEnd = 7.dp)
                        )
                        .clip(RoundedCornerShape(topStart = 3.dp, bottomStart = 3.dp, topEnd = 7.dp, bottomEnd = 7.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(book.gradientStartHex), Color(book.gradientEndHex)),
                                start = Offset.Zero,
                                end = Offset(320f, 480f)
                            )
                        )
                        .border(
                            width = 1.dp,
                            color = Color.White.copy(alpha = 0.14f),
                            shape = RoundedCornerShape(topStart = 3.dp, bottomStart = 3.dp, topEnd = 7.dp, bottomEnd = 7.dp)
                        )
                ) {
                    // Left spine groove shadow highlight
                    Box(
                        modifier = Modifier
                            .width(12.dp)
                            .fillMaxHeight()
                            .background(
                                Brush.horizontalGradient(
                                    0f to Color.Black.copy(alpha = 0.48f),
                                    0.55f to Color.White.copy(alpha = 0.14f),
                                    1f to Color.Black.copy(alpha = 0.22f)
                                )
                            )
                    )

                    // Inner embossed frame + Gold foil text
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(start = 16.dp, top = 8.dp, end = 8.dp, bottom = 8.dp)
                            .border(1.dp, Color.White.copy(alpha = 0.35f), RoundedCornerShape(2.dp))
                            .padding(horizontal = 6.dp, vertical = 10.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = book.title,
                                fontFamily = FrauncesFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                lineHeight = 17.sp,
                                textAlign = TextAlign.Center,
                                color = FolioFoilGold,
                                style = TextStyle(
                                    shadow = Shadow(
                                        color = Color(0xCC321900),
                                        offset = Offset(0f, 1.5f),
                                        blurRadius = 3f
                                    )
                                ),
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Text(
                                text = book.symbol,
                                fontSize = 25.sp,
                                color = FolioFoilGold,
                                style = TextStyle(
                                    shadow = Shadow(
                                        color = Color(0xCC321900),
                                        offset = Offset(0f, 1.5f),
                                        blurRadius = 3f
                                    )
                                )
                            )

                            Text(
                                text = book.author,
                                fontFamily = SourceSerif4Family,
                                fontSize = 9.5.sp,
                                letterSpacing = 0.5.sp,
                                textAlign = TextAlign.Center,
                                color = FolioFoilGold.copy(alpha = 0.90f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            // Book Metadata & Open Book Brass Pill (.info)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = book.title,
                    fontFamily = FrauncesFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    lineHeight = 24.sp,
                    color = FolioTitleWarm
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "by ${book.author}",
                    fontFamily = SourceSerif4Family,
                    fontSize = 14.sp,
                    color = FolioSubMuted
                )
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(FolioBrass)
                        .clickable(onClick = onOpen)
                        .padding(horizontal = 16.dp, vertical = 9.dp)
                        .testTag("story_open_pill_${book.id}")
                ) {
                    Text(
                        text = if (savedLeaf != null) "Continue book" else "Open book",
                        fontFamily = SourceSerif4Family,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = Color(0xFF2B1D10)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (savedLeaf != null) {
                        "${book.totalLeaves} pages · Page ${savedLeaf.coerceAtMost(book.pages.size)} of ${book.pages.size}"
                    } else {
                        "${book.totalLeaves} pages"
                    },
                    fontFamily = SourceSerif4Family,
                    fontSize = 12.5.sp,
                    color = FolioCountMuted
                )
            }
        }

        // Physical Wooden Shelf Bar + Drop Shadow (.shelf::after)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .shadow(elevation = 10.dp)
                .background(Brush.verticalGradient(listOf(FolioShelfTop, FolioShelfBottom)))
        )
    }
}
