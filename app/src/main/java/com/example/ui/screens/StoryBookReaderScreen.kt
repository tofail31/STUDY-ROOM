package com.example.ui.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas as AndroidCanvas
import android.graphics.DashPathEffect
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path as AndroidPath
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.text.TextPaint
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.res.ResourcesCompat
import com.example.R
import com.example.data.local.StoryLibraryRepository
import com.example.data.model.FolioBook
import com.example.data.model.FolioLeafItem
import com.example.ui.theme.FrauncesFamily
import com.example.ui.theme.SourceSerif4Family
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan
import kotlin.random.Random

private val ReaderOuterBg = Color(0xFF0D0A08)
private val ReaderGlowCenter = Color(0xFF3B2D23)
private val ReaderGlowEdge = Color(0xFF17110D)
private val ReaderBarText = Color(0xFFD8C8AC)
private val ReaderBrass = Color(0xFFD9B36A)
private val ReaderFootText = Color(0xFFCDBB9C)

private val EaseInOutCubic = CubicBezierEasing(0.65f, 0f, 0.35f, 1f)
private val EaseOutCubic = CubicBezierEasing(0.22f, 1f, 0.36f, 1f)

private const val MESH_W = 42
private const val MESH_H = 18

private data class PageTurnAnimation(
    val fromX: Float,
    val fromY: Float,
    val toX: Float,
    val toY: Float,
    val durationSec: Float,
    val easing: Easing,
    val commitDelta: Int,
    var startNanos: Long = -1L
)

@Composable
fun StoryBookReaderScreen(
    bookId: String,
    initialPageIndex: Int = 0,
    bookmarkedPages: Set<String> = emptySet(),
    onBack: () -> Unit,
    onPageChanged: (bookId: String, pageIndex: Int) -> Unit,
    onToggleBookmarkPage: (bookId: String, pageIndex: Int) -> Unit
) {
    val book = remember(bookId) {
        StoryLibraryRepository.getBookById(bookId) ?: StoryLibraryRepository.books.first()
    }
    val leaves = remember(book.id) { StoryLibraryRepository.buildLeaves(book) }
    val totalLeaves = leaves.size

    var currentLeaf by remember(book.id) {
        mutableIntStateOf(initialPageIndex.coerceIn(0, (totalLeaves - 1).coerceAtLeast(0)))
    }
    var showHint by remember(book.id) { mutableStateOf(true) }

    // 60/120fps VSYNC physical page-turn state
    var isTurning by remember { mutableStateOf(false) }
    var isDragging by remember { mutableStateOf(false) }
    var turnDir by remember { mutableIntStateOf(0) }
    var turningLeafIdx by remember { mutableIntStateOf(0) }
    var activeAnimation by remember { mutableStateOf<PageTurnAnimation?>(null) }

    val dragTargetX = remember { mutableFloatStateOf(0f) }
    val dragTargetY = remember { mutableFloatStateOf(0f) }
    var renderFx by remember { mutableFloatStateOf(0f) }
    var renderFy by remember { mutableFloatStateOf(0f) }
    var turnOriginY by remember { mutableFloatStateOf(0f) }

    var stageWidthPx by remember { mutableFloatStateOf(0f) }
    var stageHeightPx by remember { mutableFloatStateOf(0f) }

    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current

    // Inner book-page margin inside Canvas so mid-turn 3D camera zoom & hardcover board breathe naturally
    val bookPadPx = with(density) { 12.dp.toPx() }

    val pageTurnSound = remember { FolioPaperSoundPlayer() }
    DisposableEffect(Unit) {
        onDispose { pageTurnSound.release() }
    }

    LaunchedEffect(currentLeaf, book.id) {
        onPageChanged(book.id, currentLeaf)
    }

    LaunchedEffect(book.id) {
        showHint = true
        delay(3200)
        showHint = false
    }

    // Smooth VSYNC physics simulation for curved 2D mesh drag tracking & settling
    LaunchedEffect(isTurning) {
        if (!isTurning) return@LaunchedEffect
        var lastNanos = -1L
        while (isTurning) {
            withFrameNanos { now ->
                val dt = if (lastNanos < 0L) 0.016f else ((now - lastNanos) / 1_000_000_000f).coerceIn(0.001f, 0.05f)
                lastNanos = now

                val anim = activeAnimation
                if (anim != null) {
                    if (anim.startNanos < 0L) {
                        anim.startNanos = now
                    }
                    val elapsedSec = (now - anim.startNanos).coerceAtLeast(0L) / 1_000_000_000f
                    val t = (elapsedSec / anim.durationSec.coerceAtLeast(0.05f)).coerceIn(0f, 1f)
                    val eased = anim.easing.transform(t)
                    renderFx = anim.fromX + (anim.toX - anim.fromX) * eased
                    renderFy = anim.fromY + (anim.toY - anim.fromY) * eased
                    if (t >= 1f) {
                        activeAnimation = null
                        if (anim.commitDelta != 0) {
                            currentLeaf = (currentLeaf + anim.commitDelta).coerceIn(0, totalLeaves - 1)
                        }
                        isTurning = false
                        turnDir = 0
                    }
                } else if (isDragging) {
                    val follow = 1f - exp(-dt * 44f)
                    renderFx += (dragTargetX.floatValue - renderFx) * follow
                    renderFy += (dragTargetY.floatValue - renderFy) * follow
                }
            }
        }
    }

    fun playTurnFeedback() {
        runCatching { haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove) }
        scope.launch(Dispatchers.Default) {
            pageTurnSound.playRustle()
        }
    }

    fun tapTurn(direction: Int) {
        if (stageWidthPx <= 1f || stageHeightPx <= 1f) return
        if (direction == 1 && currentLeaf >= totalLeaves - 1) return
        if (direction == -1 && currentLeaf <= 0) return
        if (isTurning || activeAnimation != null) return

        val pageW = (stageWidthPx - bookPadPx * 2f).coerceAtLeast(1f)
        val pageH = (stageHeightPx - bookPadPx * 2f).coerceAtLeast(1f)

        showHint = false
        val fk = if (direction == 1) currentLeaf else currentLeaf - 1
        turnDir = direction
        turningLeafIdx = fk
        turnOriginY = pageH * 0.72f
        renderFx = if (direction == 1) pageW else -pageW
        renderFy = turnOriginY
        dragTargetX.floatValue = renderFx
        dragTargetY.floatValue = renderFy
        playTurnFeedback()

        activeAnimation = if (direction == 1) {
            PageTurnAnimation(
                fromX = pageW,
                fromY = turnOriginY,
                toX = -pageW * 1.03f,
                toY = pageH * 0.56f,
                durationSec = 0.72f,
                easing = EaseInOutCubic,
                commitDelta = 1
            )
        } else {
            PageTurnAnimation(
                fromX = -pageW,
                fromY = turnOriginY,
                toX = pageW,
                toY = pageH * 0.54f,
                durationSec = 0.68f,
                easing = EaseInOutCubic,
                commitDelta = -1
            )
        }
        isTurning = true
    }

    val isBookmarked = "${book.id}:$currentLeaf" in bookmarkedPages
    val activeLeaf = leaves[currentLeaf]
    val activeA11yDescription = remember(book.id, currentLeaf) {
        when (activeLeaf) {
            is FolioLeafItem.Cover -> "Cover: ${book.title} by ${book.author}"
            is FolioLeafItem.ContentPage -> "${activeLeaf.page.heading}. ${activeLeaf.page.paragraphs.joinToString(" ")}"
            is FolioLeafItem.EndPage -> "The End. Thank you for reading."
        }
    }

    val pageLabel = when {
        currentLeaf == 0 -> "Cover"
        currentLeaf >= totalLeaves - 1 -> "The end"
        else -> "Page $currentLeaf of ${book.pages.size}"
    }
    val progressFraction by animateFloatAsState(
        targetValue = currentLeaf.toFloat() / (totalLeaves - 1).coerceAtLeast(1).toFloat(),
        animationSpec = tween(durationMillis = 420, easing = FastOutSlowInEasing),
        label = "folio_progress"
    )

    val rendererCache = remember(book.id) {
        FolioBitmapCache(
            context = context,
            book = book,
            leaves = leaves,
            density = density.density,
            fontScale = density.fontScale
        )
    }
    val meshBuffers = remember { Folio3DMeshBuffers() }
    DisposableEffect(rendererCache) {
        onDispose { rendererCache.clear() }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ReaderOuterBg)
            .testTag("story_reader_screen"),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 460.dp)
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(ReaderGlowCenter, ReaderGlowEdge, ReaderGlowEdge),
                        center = Offset(520f, 380f),
                        radius = 1350f
                    )
                )
                .statusBarsPadding()
                .windowInsetsPadding(WindowInsets.navigationBars)
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.08f))
                        .testTag("story_reader_back_btn")
                ) {
                    Text(
                        text = "‹",
                        fontFamily = SourceSerif4Family,
                        fontSize = 24.sp,
                        color = Color(0xFFF0E2C6)
                    )
                }

                Text(
                    text = book.title,
                    fontFamily = FrauncesFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    color = ReaderBarText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = { onToggleBookmarkPage(book.id, currentLeaf) },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.08f))
                        .testTag("story_reader_bookmark_btn")
                ) {
                    Icon(
                        imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = if (isBookmarked) "Remove bookmark" else "Bookmark page",
                        tint = if (isBookmarked) ReaderBrass else Color(0xFFF0E2C6)
                    )
                }
            }

            // Unified 3D Physical Hardcover Book Stage (Cover board, stacked edges, pages & mesh move in 3D lockstep)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 2.dp)
                    .onSizeChanged { sz ->
                        if (sz.width > 0 && sz.height > 0) {
                            stageWidthPx = sz.width.toFloat()
                            stageHeightPx = sz.height.toFloat()
                        }
                    }
                    .semantics { contentDescription = activeA11yDescription }
                    .testTag("story_reader_stage")
                    .pointerInput(currentLeaf, totalLeaves) {
                        awaitEachGesture {
                            val down = awaitFirstDown(requireUnconsumed = false)
                            val canvasW = size.width.toFloat().coerceAtLeast(1f)
                            val canvasH = size.height.toFloat().coerceAtLeast(1f)
                            val w = (canvasW - bookPadPx * 2f).coerceAtLeast(1f)
                            val h = (canvasH - bookPadPx * 2f).coerceAtLeast(1f)

                            // Allow grabbing a page mid-flight if already turning, or start fresh drag
                            val wasSettling = activeAnimation != null && isTurning
                            var caughtDir = 0
                            if (wasSettling) {
                                activeAnimation = null
                                caughtDir = turnDir
                                isDragging = true
                            }

                            val x0 = (down.position.x - bookPadPx).coerceIn(0f, w)
                            val y0 = (down.position.y - bookPadPx).coerceIn(0f, h)
                            val t0 = System.currentTimeMillis()
                            var lx = x0
                            var lt = t0
                            var vx = 0f
                            var dragDir = caughtDir

                            while (true) {
                                val event = awaitPointerEvent()
                                val change = event.changes.firstOrNull { it.id == down.id }
                                    ?: event.changes.firstOrNull()
                                    ?: break

                                if (!change.pressed) {
                                    isDragging = false
                                    if (dragDir == 0) {
                                        val elapsed = System.currentTimeMillis() - t0
                                        if (elapsed < 340) {
                                            tapTurn(if (x0 > w * 0.35f) 1 else -1)
                                        }
                                    } else {
                                        val curFx = renderFx
                                        val speed = max(0.85f, abs(vx) * 1.25f)
                                        val commit = if (dragDir == 1) {
                                            curFx < w * 0.42f || vx < -0.40f
                                        } else {
                                            curFx > -w * 0.42f || vx > 0.40f
                                        }
                                        val targetX = if (dragDir == 1) {
                                            if (commit) -w * 1.03f else w
                                        } else {
                                            if (commit) w else -w
                                        }
                                        val durSec = (abs(targetX - curFx) / (speed * 1000f)).coerceIn(0.22f, 0.74f)
                                        if (commit) playTurnFeedback()

                                        activeAnimation = PageTurnAnimation(
                                            fromX = curFx,
                                            fromY = renderFy,
                                            toX = targetX,
                                            toY = if (commit) h * 0.55f else turnOriginY,
                                            durationSec = durSec,
                                            easing = EaseOutCubic,
                                            commitDelta = if (commit) dragDir else 0
                                        )
                                    }
                                    break
                                }

                                val px = (change.position.x - bookPadPx).coerceIn(-w * 0.25f, w * 1.25f)
                                val py = (change.position.y - bookPadPx).coerceIn(0f, h)
                                val now = System.currentTimeMillis()
                                if (now > lt) {
                                    vx = 0.6f * vx + 0.4f * (px - lx) / (now - lt).toFloat()
                                    lx = px
                                    lt = now
                                }

                                if (dragDir == 0 && abs(px - x0) >= 5f) {
                                    val dr = if (px < x0) 1 else -1
                                    if ((dr == 1 && currentLeaf >= totalLeaves - 1) ||
                                        (dr == -1 && currentLeaf <= 0)
                                    ) {
                                        break
                                    }
                                    dragDir = dr
                                    showHint = false
                                    turnDir = dr
                                    turningLeafIdx = if (dr == 1) currentLeaf else currentLeaf - 1
                                    turnOriginY = y0.coerceIn(h * 0.08f, h * 0.92f)
                                    val initialX = if (dr == 1) w else -w
                                    renderFx = initialX
                                    renderFy = turnOriginY
                                    dragTargetX.floatValue = initialX
                                    dragTargetY.floatValue = turnOriginY
                                    activeAnimation = null
                                    isDragging = true
                                    isTurning = true
                                }

                                if (dragDir != 0) {
                                    change.consume()
                                    dragTargetX.floatValue = if (dragDir == 1) {
                                        val g = 1.25f + 0.85f * (1f - x0 / w)
                                        (w - (x0 - px) * g).coerceIn(-w, w)
                                    } else {
                                        val g2 = 2.4f - x0 / w
                                        (-w + (px - x0) * g2).coerceIn(-w, w)
                                    }
                                    dragTargetY.floatValue = (turnOriginY + (py - y0)).coerceIn(h * 0.04f, h * 0.96f)
                                }
                            }
                        }
                    }
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val canvasW = size.width.coerceAtLeast(1f)
                    val canvasH = size.height.coerceAtLeast(1f)
                    stageWidthPx = canvasW
                    stageHeightPx = canvasH

                    val pageW = (canvasW - bookPadPx * 2f).coerceAtLeast(60f)
                    val pageH = (canvasH - bookPadPx * 2f).coerceAtLeast(90f)
                    rendererCache.ensureSize(pageW, pageH)

                    val activeTurnIdx = if (isTurning) turningLeafIdx else currentLeaf
                    val fx = if (isTurning) renderFx else pageW
                    val fy = if (isTurning) renderFy else pageH * 0.5f

                    drawIntoCanvas { canvas ->
                        renderPhysical3DBookScene(
                            nc = canvas.nativeCanvas,
                            offsetX = bookPadPx,
                            offsetY = bookPadPx,
                            w = pageW,
                            h = pageH,
                            currentLeaf = currentLeaf,
                            totalLeaves = totalLeaves,
                            isTurning = isTurning,
                            fk = activeTurnIdx,
                            fx = fx,
                            fy = fy,
                            boardColor = (0xFF000000L or (book.boardHex and 0x00FFFFFFL)).toInt(),
                            density = density.density,
                            cache = rendererCache,
                            mesh = meshBuffers
                        )
                    }
                }

                // Invisible bottom-right corner tap-to-turn hotspot (ZERO visible corner fold!)
                if (!isTurning && currentLeaf in 1 until (totalLeaves - 1)) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = 10.dp, bottom = 10.dp)
                            .size(56.dp)
                            .semantics { contentDescription = "Next page corner" }
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = { tapTurn(1) }
                            )
                            .testTag("story_reader_curl_corner")
                    )
                }

                // Floating Drag Hint
                androidx.compose.animation.AnimatedVisibility(
                    visible = showHint,
                    enter = fadeIn(tween(300)),
                    exit = fadeOut(tween(500)),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 28.dp)
                        .testTag("story_reader_hint")
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xCC140C06))
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "Touch and drag the page like real paper",
                            fontFamily = SourceSerif4Family,
                            fontSize = 13.sp,
                            color = Color(0xFFF3E7D0)
                        )
                    }
                }
            }

            // Bottom Progress Bar + Accessible Navigation Controls
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp)
                    .padding(top = 4.dp, bottom = 14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color.White.copy(alpha = 0.12f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(progressFraction.coerceIn(0f, 1f))
                            .background(ReaderBrass)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = { tapTurn(-1) },
                        enabled = currentLeaf > 0,
                        modifier = Modifier
                            .size(48.dp)
                            .alpha(if (currentLeaf > 0) 0.9f else 0.25f)
                            .testTag("story_reader_prev_btn")
                    ) {
                        Text(
                            text = "‹",
                            fontFamily = SourceSerif4Family,
                            fontSize = 22.sp,
                            color = ReaderFootText
                        )
                    }

                    Text(
                        text = pageLabel,
                        fontFamily = SourceSerif4Family,
                        fontSize = 13.sp,
                        color = ReaderFootText,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.testTag("story_reader_page_indicator")
                    )

                    IconButton(
                        onClick = { tapTurn(1) },
                        enabled = currentLeaf < totalLeaves - 1,
                        modifier = Modifier
                            .size(48.dp)
                            .alpha(if (currentLeaf < totalLeaves - 1) 0.9f else 0.25f)
                            .testTag("story_reader_next_btn")
                    ) {
                        Text(
                            text = "›",
                            fontFamily = SourceSerif4Family,
                            fontSize = 22.sp,
                            color = ReaderFootText
                        )
                    }
                }
            }
        }
    }
}

/**
 * Reusable pre-allocated vertex & color buffers for 2-pass 42x18 drawBitmapMesh rendering
 * (zero per-frame heap allocations during 60/120fps swipes).
 */
private class Folio3DMeshBuffers {
    val count = (MESH_W + 1) * (MESH_H + 1)
    val frontVerts = FloatArray(count * 2)
    val frontColors = IntArray(count)
    val backVerts = FloatArray(count * 2)
    val backColors = IntArray(count)
    val shadowEdgeX = FloatArray(MESH_H + 1)
    val shadowEdgeY = FloatArray(MESH_H + 1)
    val shadowLeftX = FloatArray(MESH_H + 1)
    val shadowLeftY = FloatArray(MESH_H + 1)
    val shadowPath = AndroidPath()
}

/**
 * Renders the unified physical hardcover book scene inside a single 3D-projected Canvas:
 * - Soft table drop shadow beneath the leather cover board
 * - Hardcover back board & open-book left cover board scaling/panning with the same 3D camera
 * - Dynamic left & right stacked page-edge thicknesses (deckle lines)
 * - Gouraud-shaded 42x18 2D curved cylindrical/conical page mesh with curved diagonal cast shadow,
 *   paper crest highlight, spine binding dip, and faint 6% back-page ink bleed-through
 */
private fun renderPhysical3DBookScene(
    nc: AndroidCanvas,
    offsetX: Float,
    offsetY: Float,
    w: Float,
    h: Float,
    currentLeaf: Int,
    totalLeaves: Int,
    isTurning: Boolean,
    fk: Int,
    fx: Float,
    fy: Float,
    boardColor: Int,
    density: Float,
    cache: FolioBitmapCache,
    mesh: Folio3DMeshBuffers
) {
    val km = if (fk == 0) 0.16f else 1.08f
    val clampedX = if (isTurning) (fx / w).coerceIn(-1f, 1f) else 1f
    val th = acos(clampedX)
    val p = (th / PI).toFloat()
    val tiltT = if (isTurning) ((fy / h - 0.5f) * 2f).coerceIn(-1f, 1f) else 0f

    // 24deg perspective camera that glides smoothly left & zooms out mid-turn to reveal open book spread
    val zb = (h / 2f) / tan(12.0 * PI / 180.0).toFloat()
    val sn = if (isTurning) sin(PI * p).toFloat() else 0f
    val camX = -w * 0.28f * sn
    val camZ = zb * (1f + 0.42f * sn)

    fun projectX(x3: Float, z3: Float): Float {
        val scale = zb / (camZ - z3).coerceAtLeast(12f)
        return offsetX + w * 0.5f + (x3 - w * 0.5f - camX) * scale
    }

    fun projectY(py: Float, z3: Float): Float {
        val scale = zb / (camZ - z3).coerceAtLeast(12f)
        return offsetY + h * 0.5f + (py * h) * scale
    }

    val boardPadL = if (isTurning && fk > 0 || (!isTurning && currentLeaf > 0)) 6f * density else 4f * density
    val boardPadR = 8f * density
    val boardPadV = 6f * density
    val openLeftVisible = (isTurning && fk > 0) || (isTurning && fk == 0 && p > 0.04f)
    val bookMinX3 = if (isTurning && fk > 0) -w - boardPadL else -boardPadL
    val bookMaxX3 = w + boardPadR

    // 0. Soft table drop shadow & unified leather hardcover board projected in exact 3D camera lockstep
    val bx0 = projectX(bookMinX3, -3f)
    val bx1 = projectX(bookMaxX3, -3f)
    val byTop = projectY(-0.5f, -3f) - boardPadV * (zb / camZ)
    val byBot = projectY(0.5f, -3f) + boardPadV * (zb / camZ)

    val tableShadow = cache.shadowPaint
    tableShadow.color = android.graphics.Color.argb(125, 0, 0, 0)
    tableShadow.shader = LinearGradient(
        0f, byBot - 6f * density, 0f, byBot + 14f * density,
        intArrayOf(android.graphics.Color.argb(150, 0, 0, 0), android.graphics.Color.TRANSPARENT),
        null,
        Shader.TileMode.CLAMP
    )
    nc.drawRoundRect(
        RectF(bx0 - 4f * density, byBot - 8f * density, bx1 + 4f * density, byBot + 14f * density),
        12f * density, 12f * density, tableShadow
    )
    tableShadow.shader = null

    val boardPaint = cache.overlayPaint
    boardPaint.color = boardColor
    val boardRect = RectF(bx0, byTop, bx1, byBot)
    val cornerR = 8f * density
    nc.drawRoundRect(boardRect, cornerR, cornerR, boardPaint)

    // Subtle 1dp leather cover border sheen
    val boardStroke = cache.strokePaint
    boardStroke.color = android.graphics.Color.argb(36, 255, 255, 255)
    boardStroke.strokeWidth = max(1f, density)
    nc.drawRoundRect(boardRect, cornerR, cornerR, boardStroke)

    // Paper stack right edge (.edgeR) & left edge (.edgeL) rendered inside the 3D hardcover board
    val rightLeafIdx = if (isTurning) fk + 1 else currentLeaf
    val rightCount = (totalLeaves - 1 - rightLeafIdx).coerceAtLeast(0)
    val rightStackW = if (rightLeafIdx < totalLeaves - 1) min(9f, rightCount * 1.9f + 1.2f) * density else 0f
    val leftCount = if (isTurning) fk else currentLeaf
    val leftStackW = if (leftCount > 0 && isTurning) min(9f, leftCount * 1.9f) * density else 0f

    val ux0 = projectX(0f, 0f)
    val ux1 = projectX(w, 0f)
    val uyTop = projectY(-0.5f, 0f)
    val uyBot = projectY(0.5f, 0f)

    if (rightStackW > 1f) {
        val stackRightX = projectX(w + rightStackW * 0.9f, -1f)
        val stackRect = RectF(ux1 - 1f, uyTop + 2f * density, stackRightX, uyBot - 2f * density)
        boardPaint.color = android.graphics.Color.parseColor("#EFE4C9")
        nc.drawRoundRect(stackRect, 2f * density, 2f * density, boardPaint)
        val lineP = cache.strokePaint
        lineP.color = android.graphics.Color.parseColor("#BFAE82")
        lineP.strokeWidth = max(1f, 0.9f * density)
        var sx = ux1 + 1.5f * density
        while (sx < stackRightX - 1f) {
            nc.drawLine(sx, stackRect.top, sx, stackRect.bottom, lineP)
            sx += 2f * density
        }
    }

    if (openLeftVisible && leftStackW > 1f) {
        val lx0 = projectX(-w, 0f)
        val stackLeftX = projectX(-w - leftStackW * 0.9f, -1f)
        val stackRect = RectF(stackLeftX, uyTop + 2f * density, lx0 + 1f, uyBot - 2f * density)
        boardPaint.color = android.graphics.Color.parseColor("#EFE4C9")
        nc.drawRoundRect(stackRect, 2f * density, 2f * density, boardPaint)
        val lineP = cache.strokePaint
        lineP.color = android.graphics.Color.parseColor("#BFAE82")
        lineP.strokeWidth = max(1f, 0.9f * density)
        var sx = stackLeftX + 1.5f * density
        while (sx < lx0 - 1f) {
            nc.drawLine(sx, stackRect.top, sx, stackRect.bottom, lineP)
            sx += 2f * density
        }
    }

    // Idle state: crisp 1:1 native resting leaf + realistic spine binding shadow + warm highlight
    if (!isTurning) {
        val idleBm = cache.getLeafBitmap(currentLeaf)
        val dstIdle = RectF(ux0, uyTop, ux1, uyBot)
        nc.drawBitmap(idleBm, null, dstIdle, cache.smoothPaint)

        // Small decorative left stack sliver when resting on inner pages
        val restingLeftW = min(8f, currentLeaf * 1.8f) * density
        if (restingLeftW > 1f) {
            val leftSliver = RectF(ux0 - restingLeftW, uyTop + 2f * density, ux0, uyBot - 2f * density)
            boardPaint.color = android.graphics.Color.parseColor("#E5D8B9")
            nc.drawRect(leftSliver, boardPaint)
            val lineP = cache.strokePaint
            lineP.color = android.graphics.Color.parseColor("#B8A57B")
            lineP.strokeWidth = max(1f, 0.8f * density)
            var sx = leftSliver.left + 1.4f * density
            while (sx < ux0 - 1f) {
                nc.drawLine(sx, leftSliver.top, sx, leftSliver.bottom, lineP)
                sx += 1.9f * density
            }
        }
        return
    }

    // 1. Draw left-hand resting spread (mL = back of leaf fk - 1) during open-book turns
    if (fk > 0) {
        val lx0 = projectX(-w, 0f)
        val lx1 = projectX(0f, 0f)
        val lyTop = projectY(-0.5f, 0f)
        val lyBot = projectY(0.5f, 0f)
        val leftBm = cache.getTurnBackBitmap(fk - 1)
        val dstLeft = RectF(lx0, lyTop, lx1, lyBot)
        nc.drawBitmap(leftBm, null, dstLeft, cache.smoothPaint)

        // Dynamic ambient shadow cast onto left page as the turning leaf flips toward it
        val dimAlpha = (0.34f * max(0f, sin(th)) * min(1f, th / 1.25f)).coerceIn(0f, 0.56f)
        if (dimAlpha > 0.01f) {
            boardPaint.color = android.graphics.Color.argb((dimAlpha * 255).toInt(), 22, 13, 4)
            nc.drawRect(dstLeft, boardPaint)
        }
        // Central binding valley shadow on left side
        tableShadow.shader = LinearGradient(
            lx1 - (lx1 - lx0) * 0.20f, 0f, lx1, 0f,
            intArrayOf(android.graphics.Color.TRANSPARENT, android.graphics.Color.argb(90, 38, 22, 8)),
            null,
            Shader.TileMode.CLAMP
        )
        nc.drawRect(lx1 - (lx1 - lx0) * 0.20f, lyTop, lx1, lyBot, tableShadow)
        tableShadow.shader = null
    }

    // 2. Draw underlying revealed page (U = leaf fk + 1)
    val underBm = cache.getLeafBitmap(fk + 1)
    val dstUnder = RectF(ux0, uyTop, ux1, uyBot)
    nc.drawBitmap(underBm, null, dstUnder, cache.smoothPaint)

    // Compute full 42x18 2D curved conical/cylindrical mesh & per-row curved cast shadow
    val lightDirX = -0.35f
    val lightDirZ = 0.89f
    val lightLen = sqrt(lightDirX * lightDirX + lightDirZ * lightDirZ)
    val lxLight = lightDirX / lightLen
    val lzLight = lightDirZ / lightLen

    val leadAmplitude = if (fk == 0) 0f else 0.34f * sin(th) * tiltT
    var anyBackVisible = false
    var maxLift = 0f

    fun shadeColor(al: Float, isFront: Boolean, s: Float): Int {
        var nx = -sin(al)
        var nz = cos(al)
        if (!isFront) {
            nx = -nx
            nz = -nz
        }
        val df = max(0f, nx * lxLight + nz * lzLight)
        val dotNL = nx * lxLight + nz * lzLight
        val refZ = 2f * dotNL * nz - lzLight
        val sp = max(0f, refZ).pow(18f) * (if (fk == 0) 0.26f else 0.16f)
        val gutter = if (s < w * 0.08f) 0.90f + 0.10f * (s / (w * 0.08f)) else 1.0f
        val lum = (((0.50f + 0.56f * df) + sp) * gutter).coerceIn(0.38f, 1.0f)
        val v = (lum * 255f).roundToInt()
        return android.graphics.Color.rgb(v, v, (v * 0.985f).roundToInt())
    }

    for (row in 0..MESH_H) {
        val py = (row / MESH_H.toFloat()) - 0.5f // -0.5 top .. +0.5 bottom
        val thRow = (th + leadAmplitude * py).coerceIn(0.001f, PI.toFloat() - 0.001f)
        val pRow = (thRow / PI).toFloat()
        val bendRow = min(km * sin(PI * pRow).toFloat(), min(2f * thRow, 2f * (PI.toFloat() - thRow)))
        val a0Row = thRow - bendRow * 0.5f
        val kRow = bendRow / w

        val sFold = if (abs(kRow) < 1e-5f) {
            if (cos(a0Row) >= 0f) w + 1f else -1f
        } else {
            ((PI.toFloat() * 0.5f - a0Row) / kRow)
        }

        // Cast shadow trailing contour edge for this vertical row
        val xtRow = if (abs(kRow) < 1e-5f) {
            w * cos(a0Row)
        } else {
            (sin(a0Row + kRow * w) - sin(a0Row)) / kRow
        }
        val zMidRow = if (abs(kRow) < 1e-5f) (w * 0.5f) * sin(a0Row) else (cos(a0Row) - cos(a0Row + kRow * w * 0.5f)) / kRow
        if (zMidRow > maxLift) maxLift = zMidRow

        val shRightX = projectX((xtRow + 0.15f * w).coerceIn(0f, w), 0f)
        mesh.shadowEdgeX[row] = shRightX.coerceIn(ux0, ux1)
        mesh.shadowEdgeY[row] = projectY(py, 0f)
        mesh.shadowLeftX[row] = ux0
        mesh.shadowLeftY[row] = projectY(py, 0f)

        for (col in 0..MESH_W) {
            val s = w * (col / MESH_W.toFloat())
            val idx = row * (MESH_W + 1) + col

            // Pass 1 vertex: Front-facing curve clipped smoothly along sFold tangent line
            val sFront = if (sFold >= 0f) min(s, min(w, sFold)) else 0f
            val alFront = a0Row + kRow * sFront
            val x3F: Float
            val z3F: Float
            if (abs(kRow) < 1e-5f) {
                x3F = sFront * cos(a0Row)
                z3F = sFront * sin(a0Row)
            } else {
                x3F = (sin(alFront) - sin(a0Row)) / kRow
                z3F = (cos(a0Row) - cos(alFront)) / kRow
            }
            mesh.frontVerts[idx * 2] = projectX(x3F, z3F + 0.8f)
            mesh.frontVerts[idx * 2 + 1] = projectY(py, z3F + 0.8f)
            mesh.frontColors[idx] = shadeColor(alFront, isFront = true, s = sFront)

            // Pass 2 vertex: Back-facing reversed curl from sFold outward to s = W
            val sBackStart = max(0f, sFold)
            if (sBackStart < w) anyBackVisible = true
            val sBack = max(s, min(w, sBackStart))
            val alBack = a0Row + kRow * sBack
            val x3B: Float
            val z3B: Float
            if (abs(kRow) < 1e-5f) {
                x3B = sBack * cos(a0Row)
                z3B = sBack * sin(a0Row)
            } else {
                x3B = (sin(alBack) - sin(a0Row)) / kRow
                z3B = (cos(a0Row) - cos(alBack)) / kRow
            }
            mesh.backVerts[idx * 2] = projectX(x3B, z3B + 0.8f)
            mesh.backVerts[idx * 2 + 1] = projectY(py, z3B + 0.8f)
            mesh.backColors[idx] = shadeColor(alBack, isFront = false, s = sBack)
        }
    }

    // 2b. Curved per-row cast shadow projected onto the underlying page
    val liftNorm = min(1f, maxLift / (0.14f * w))
    if (liftNorm > 0.01f) {
        mesh.shadowPath.reset()
        mesh.shadowPath.moveTo(mesh.shadowLeftX[0], mesh.shadowLeftY[0])
        for (row in 0..MESH_H) {
            mesh.shadowPath.lineTo(mesh.shadowEdgeX[row], mesh.shadowEdgeY[row])
        }
        for (row in MESH_H downTo 0) {
            mesh.shadowPath.lineTo(mesh.shadowLeftX[row], mesh.shadowLeftY[row])
        }
        mesh.shadowPath.close()

        val maxEdgeX = mesh.shadowEdgeX.maxOrNull() ?: ux0
        if (maxEdgeX > ux0 + 2f) {
            val shAlpha = (liftNorm * 0.34f * 255f).toInt().coerceIn(0, 120)
            tableShadow.shader = LinearGradient(
                ux0, 0f, maxEdgeX, 0f,
                intArrayOf(
                    android.graphics.Color.argb(shAlpha, 22, 13, 4),
                    android.graphics.Color.argb((shAlpha * 0.65f).toInt(), 22, 13, 4),
                    android.graphics.Color.TRANSPARENT
                ),
                floatArrayOf(0f, 0.55f, 1f),
                Shader.TileMode.CLAMP
            )
            nc.drawPath(mesh.shadowPath, tableShadow)
            tableShadow.shader = null
        }
    }

    // 3. Draw Gouraud-shaded 42x18 front mesh + back mesh in two ultra-fast drawBitmapMesh calls
    val frontBm = cache.getLeafBitmap(fk)
    nc.drawBitmapMesh(frontBm, MESH_W, MESH_H, mesh.frontVerts, 0, mesh.frontColors, 0, cache.smoothPaint)

    if (anyBackVisible) {
        val backBm = cache.getTurnBackBitmap(fk)
        nc.drawBitmapMesh(backBm, MESH_W, MESH_H, mesh.backVerts, 0, mesh.backColors, 0, cache.smoothPaint)
    }
}

/**
 * Renders 1:1 native-resolution print-quality Folio leaf bitmaps (Cover, Literary Paper Pages, End Leaf,
 * and Back Faces with subtle 6% ghosted bleed-through) using Fraunces + Source Serif 4.
 */
private class FolioBitmapCache(
    private val context: Context,
    private val book: FolioBook,
    private val leaves: List<FolioLeafItem>,
    private val density: Float,
    private val fontScale: Float
) {
    private var cachedWidthPx = 0
    private var cachedHeightPx = 0
    private var renderScale = 1f
    private val leafBitmaps = mutableMapOf<Int, Bitmap>()
    private val turnBackBitmaps = mutableMapOf<Int, Bitmap>()
    private var noiseBitmap: Bitmap? = null

    val smoothPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
    val overlayPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE }

    private val frauncesBold: Typeface by lazy {
        runCatching { ResourcesCompat.getFont(context, R.font.fraunces) }.getOrNull()
            ?.let { Typeface.create(it, Typeface.BOLD) }
            ?: Typeface.create(Typeface.SERIF, Typeface.BOLD)
    }

    private val serifRegular: Typeface by lazy {
        runCatching { ResourcesCompat.getFont(context, R.font.source_serif_4) }.getOrNull()
            ?: Typeface.SERIF
    }

    private val serifItalic: Typeface by lazy {
        Typeface.create(serifRegular, Typeface.ITALIC)
    }

    fun ensureSize(stageW: Float, stageH: Float) {
        val safeW = stageW.coerceAtLeast(120f)
        val safeH = stageH.coerceAtLeast(180f)
        val s = min(1f, 1440f / safeW)
        val targetW = (safeW * s).roundToInt().coerceAtLeast(120)
        val targetH = (safeH * s).roundToInt().coerceAtLeast(180)
        if (abs(targetW - cachedWidthPx) > 4 || abs(targetH - cachedHeightPx) > 4) {
            clear()
            cachedWidthPx = targetW
            cachedHeightPx = targetH
            renderScale = targetW.toFloat() / safeW
        }
    }

    fun getLeafBitmap(index: Int): Bitmap {
        val safeIdx = index.coerceIn(0, leaves.lastIndex)
        return leafBitmaps.getOrPut(safeIdx) {
            drawLeafBitmap(leaves[safeIdx], cachedWidthPx.coerceAtLeast(120), cachedHeightPx.coerceAtLeast(180))
        }
    }

    fun getTurnBackBitmap(index: Int): Bitmap {
        val safeIdx = index.coerceIn(0, leaves.lastIndex)
        return turnBackBitmaps.getOrPut(safeIdx) {
            drawBackBitmap(
                index = safeIdx,
                w = cachedWidthPx.coerceAtLeast(120),
                h = cachedHeightPx.coerceAtLeast(180)
            )
        }
    }

    fun clear() {
        leafBitmaps.values.forEach { it.recycle() }
        leafBitmaps.clear()
        turnBackBitmaps.values.forEach { it.recycle() }
        turnBackBitmaps.clear()
    }

    private fun dp(value: Float): Float = value * density * renderScale
    private fun sp(value: Float): Float = value * density * fontScale.coerceIn(0.88f, 1.25f) * renderScale

    private fun getOrBuildNoise(): Bitmap {
        return noiseBitmap ?: run {
            val sz = 180
            val bmp = Bitmap.createBitmap(sz, sz, Bitmap.Config.ARGB_8888)
            val c = AndroidCanvas(bmp)
            c.drawColor(android.graphics.Color.WHITE)
            val p = Paint()
            val rng = Random(42)
            for (i in 0 until 2200) {
                val a = (rng.nextFloat() * 0.11f * 255).toInt()
                p.color = android.graphics.Color.argb(a, 120, 90, 50)
                val rx = rng.nextFloat() * sz
                val ry = rng.nextFloat() * sz
                c.drawRect(rx, ry, rx + 1.3f, ry + 1.1f, p)
            }
            noiseBitmap = bmp
            bmp
        }
    }

    private fun drawPaperBackground(c: AndroidCanvas, w: Float, h: Float) {
        val p = Paint()
        p.color = android.graphics.Color.parseColor("#F7F0DF")
        c.drawRect(0f, 0f, w, h, p)

        // Subtle organic paper texture grain
        p.shader = BitmapShader(getOrBuildNoise(), Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)
        p.alpha = 32
        c.drawRect(0f, 0f, w, h, p)
        p.shader = null
        p.alpha = 255

        // Soft spine binding valley gradient (0..14% width)
        p.shader = LinearGradient(
            0f, 0f, w * 0.14f, 0f,
            intArrayOf(
                android.graphics.Color.argb(72, 60, 35, 10),
                android.graphics.Color.argb(16, 60, 35, 10),
                android.graphics.Color.TRANSPARENT
            ),
            floatArrayOf(0f, 0.45f, 1f),
            Shader.TileMode.CLAMP
        )
        c.drawRect(0f, 0f, w * 0.14f, h, p)

        // Delicate right edge paper bevel
        p.shader = LinearGradient(
            w, 0f, w * 0.965f, 0f,
            intArrayOf(
                android.graphics.Color.argb(34, 60, 35, 10),
                android.graphics.Color.TRANSPARENT
            ),
            null,
            Shader.TileMode.CLAMP
        )
        c.drawRect(w * 0.965f, 0f, w, h, p)
        p.shader = null
    }

    private fun drawBackBitmap(index: Int, w: Int, h: Int): Bitmap {
        val bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val c = AndroidCanvas(bm)
        val wf = w.toFloat()
        val hf = h.toFloat()
        drawPaperBackground(c, wf, hf)

        val p = Paint(Paint.ANTI_ALIAS_FLAG)
        // Left spine shadow (since drawBitmapMesh maps u=0 along spine x=0 and flips outward to x=-W)
        p.shader = LinearGradient(
            0f, 0f, wf * 0.15f, 0f,
            intArrayOf(
                android.graphics.Color.argb(78, 60, 35, 10),
                android.graphics.Color.TRANSPARENT
            ),
            null,
            Shader.TileMode.CLAMP
        )
        c.drawRect(0f, 0f, wf * 0.15f, hf, p)
        p.shader = null

        if (index == 0) {
            val coverTint = (book.gradientStartHex and 0x00FFFFFFL).toInt()
            p.color = android.graphics.Color.argb(46, (coverTint shr 16) and 0xFF, (coverTint shr 8) and 0xFF, coverTint and 0xFF)
            c.drawRect(0f, 0f, wf, hf, p)

            val stripeClr = (book.gradientEndHex and 0x00FFFFFFL).toInt()
            p.color = android.graphics.Color.argb(38, (stripeClr shr 16) and 0xFF, (stripeClr shr 8) and 0xFF, stripeClr and 0xFF)
            p.strokeWidth = dp(1f).coerceAtLeast(1f)
            var x = -hf
            val step = dp(9f).coerceAtLeast(8f)
            while (x < wf) {
                c.drawLine(x, 0f, x + hf, hf, p)
                x += step
            }
        } else {
            // Subtle 6% ghosted bleed-through of front page ink through real paper
            // Drawn un-flipped because drawBitmapMesh mirrors x from 0 -> -W automatically!
            val frontBm = leafBitmaps[index]
            if (frontBm != null && !frontBm.isRecycled) {
                val bleedPaint = Paint(Paint.FILTER_BITMAP_FLAG).apply { alpha = 15 }
                c.drawBitmap(frontBm, null, RectF(0f, 0f, wf, hf), bleedPaint)
            }
        }
        return bm
    }

    private fun drawLeafBitmap(leaf: FolioLeafItem, w: Int, h: Int): Bitmap {
        val bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val c = AndroidCanvas(bm)
        val wf = w.toFloat()
        val hf = h.toFloat()

        when (leaf) {
            is FolioLeafItem.Cover -> {
                val p = Paint(Paint.ANTI_ALIAS_FLAG)
                val startColor = (0xFF000000L or (leaf.book.gradientStartHex and 0x00FFFFFFL)).toInt()
                val endColor = (0xFF000000L or (leaf.book.gradientEndHex and 0x00FFFFFFL)).toInt()
                p.shader = LinearGradient(0f, 0f, wf, hf, startColor, endColor, Shader.TileMode.CLAMP)
                c.drawRect(0f, 0f, wf, hf, p)
                p.shader = null

                p.shader = BitmapShader(getOrBuildNoise(), Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)
                p.alpha = 46
                c.drawRect(0f, 0f, wf, hf, p)
                p.shader = null
                p.alpha = 255

                val spineW = dp(22f)
                p.shader = LinearGradient(
                    0f, 0f, spineW, 0f,
                    intArrayOf(
                        android.graphics.Color.argb(145, 0, 0, 0),
                        android.graphics.Color.argb(52, 255, 255, 255),
                        android.graphics.Color.argb(128, 0, 0, 0),
                        android.graphics.Color.TRANSPARENT
                    ),
                    floatArrayOf(0f, 0.34f, 0.52f, 1f),
                    Shader.TileMode.CLAMP
                )
                c.drawRect(0f, 0f, spineW, hf, p)
                p.shader = null

                val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    style = Paint.Style.STROKE
                    strokeWidth = dp(1.1f).coerceAtLeast(1f)
                    color = android.graphics.Color.argb(175, 242, 217, 143)
                    pathEffect = DashPathEffect(floatArrayOf(dp(6f), dp(4f)), 0f)
                }
                c.drawRect(dp(30f), dp(16f), wf - dp(16f), hf - dp(16f), borderPaint)

                val cx = (wf + dp(14f)) / 2f
                val titlePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                    typeface = frauncesBold
                    textSize = sp(31f)
                    textAlign = Paint.Align.CENTER
                }
                drawFoilLines(c, leaf.book.title, cx, hf * 0.27f, titlePaint, wf - dp(92f), sp(38f))

                val emblemPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                    typeface = Typeface.SERIF
                    textSize = sp(52f)
                    textAlign = Paint.Align.CENTER
                }
                drawFoilLines(c, leaf.book.symbol, cx, hf * 0.62f, emblemPaint, wf, 0f)

                val authorPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                    typeface = serifRegular
                    textSize = sp(15f)
                    textAlign = Paint.Align.CENTER
                }
                drawFoilLines(c, leaf.book.author, cx, hf - dp(54f), authorPaint, wf - dp(76f), 0f)
            }

            is FolioLeafItem.EndPage -> {
                drawPaperBackground(c, wf, hf)
                val tp = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                    textAlign = Paint.Align.CENTER
                }
                tp.typeface = Typeface.SERIF
                tp.textSize = sp(38f)
                tp.color = android.graphics.Color.parseColor("#7A3B22")
                c.drawText("❦", wf / 2f, hf * 0.41f, tp)

                tp.typeface = frauncesBold
                tp.textSize = sp(26f)
                tp.color = android.graphics.Color.parseColor("#2B2118")
                c.drawText("The End", wf / 2f, hf * 0.41f + dp(42f), tp)

                val rulePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = android.graphics.Color.argb(90, 139, 118, 85)
                    strokeWidth = dp(1f).coerceAtLeast(1f)
                }
                c.drawLine(wf / 2f - dp(28f), hf * 0.41f + dp(56f), wf / 2f + dp(28f), hf * 0.41f + dp(56f), rulePaint)

                tp.typeface = serifRegular
                tp.textSize = sp(15.5f)
                tp.color = android.graphics.Color.parseColor("#5B4A35")
                c.drawText("Thank you for reading", wf / 2f, hf * 0.41f + dp(80f), tp)

                tp.typeface = serifItalic
                tp.textSize = sp(12f)
                tp.color = android.graphics.Color.parseColor("#8B7655")
                c.drawText("${leaf.book.title} · ${leaf.book.author}", wf / 2f, hf - dp(26f), tp)
            }

            is FolioLeafItem.ContentPage -> {
                drawPaperBackground(c, wf, hf)
                val left = dp(28f)
                val right = wf - dp(24f)
                var y = dp(34f)

                val headerPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                    typeface = serifItalic
                    textSize = sp(11.5f)
                    color = android.graphics.Color.parseColor("#8B7655")
                    textAlign = Paint.Align.CENTER
                }
                c.drawText(leaf.book.title, wf / 2f, y, headerPaint)
                val hairPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = android.graphics.Color.argb(65, 139, 118, 85)
                    strokeWidth = dp(0.75f).coerceAtLeast(1f)
                }
                c.drawLine(wf / 2f - dp(22f), y + dp(8f), wf / 2f + dp(22f), y + dp(8f), hairPaint)
                y += dp(36f)

                val rawHeading = leaf.page.heading
                val colonIdx = rawHeading.indexOf(':')
                if (colonIdx > 0 && colonIdx < rawHeading.length - 1) {
                    val eyebrow = rawHeading.substring(0, colonIdx).trim().uppercase()
                    val mainTitle = rawHeading.substring(colonIdx + 1).trim()

                    val eyebrowPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                        typeface = serifRegular
                        textSize = sp(11f)
                        letterSpacing = 0.14f
                        color = android.graphics.Color.parseColor("#8C5327")
                        textAlign = Paint.Align.LEFT
                    }
                    c.drawText(eyebrow, left, y, eyebrowPaint)
                    y += dp(22f)

                    val headingPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                        typeface = frauncesBold
                        textSize = sp(24f)
                        color = android.graphics.Color.parseColor("#2B2118")
                        textAlign = Paint.Align.LEFT
                    }
                    for (line in wrapWords(headingPaint, mainTitle, right - left)) {
                        c.drawText(line, left, y, headingPaint)
                        y += sp(29f)
                    }
                } else {
                    val headingPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                        typeface = frauncesBold
                        textSize = sp(23.5f)
                        color = android.graphics.Color.parseColor("#2B2118")
                        textAlign = Paint.Align.LEFT
                    }
                    for (line in wrapWords(headingPaint, rawHeading, right - left)) {
                        c.drawText(line, left, y, headingPaint)
                        y += sp(28.5f)
                    }
                }
                y += dp(14f)

                val lineH = sp(27.5f)
                val bodyPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                    typeface = serifRegular
                    textSize = sp(16.5f)
                    color = android.graphics.Color.parseColor("#2B2118")
                    textAlign = Paint.Align.LEFT
                }
                val dropCapPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                    typeface = frauncesBold
                    textSize = sp(50f)
                    color = android.graphics.Color.parseColor("#7A3B22")
                    textAlign = Paint.Align.LEFT
                }

                leaf.page.paragraphs.forEachIndexed { idx, paraText ->
                    val trimmed = paraText.trim()
                    if (idx == 0 && trimmed.isNotEmpty()) {
                        val firstChar = trimmed.substring(0, 1)
                        val restText = trimmed.substring(1).let { rest ->
                            if (rest.startsWith(" ")) rest.trimStart() else rest
                        }
                        val dropW = (dropCapPaint.measureText(firstChar) + dp(7f)).coerceAtLeast(dp(30f))
                        val dropBaseline = y + lineH * 0.96f
                        c.drawText(firstChar, left, dropBaseline, dropCapPaint)
                        y = drawEditorialParagraph(
                            c = c,
                            paint = bodyPaint,
                            text = restText,
                            left = left,
                            right = right,
                            startY = y,
                            lineHeight = lineH,
                            dropCapWidth = dropW,
                            dropLines = 2
                        )
                    } else {
                        y = drawEditorialParagraph(
                            c = c,
                            paint = bodyPaint,
                            text = trimmed,
                            left = left,
                            right = right,
                            startY = y,
                            lineHeight = lineH,
                            dropCapWidth = 0f,
                            dropLines = 0
                        )
                    }
                }

                if (y + dp(34f) < hf - dp(44f)) {
                    val ornPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                        typeface = serifRegular
                        textSize = sp(13f)
                        color = android.graphics.Color.argb(120, 139, 118, 85)
                        textAlign = Paint.Align.CENTER
                    }
                    c.drawText("— ✦ —", wf / 2f, y + dp(16f), ornPaint)
                }

                val numPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                    typeface = serifRegular
                    textSize = sp(12f)
                    color = android.graphics.Color.parseColor("#8B7655")
                    textAlign = Paint.Align.CENTER
                }
                c.drawText("${leaf.page.pageNumber}", wf / 2f, hf - dp(18f), numPaint)
            }
        }
        return bm
    }

    private fun wrapWords(paint: Paint, text: String, maxWidth: Float): List<String> {
        val words = text.split(" ").filter { it.isNotEmpty() }
        val lines = mutableListOf<String>()
        var current = ""
        for (word in words) {
            val candidate = if (current.isEmpty()) word else "$current $word"
            if (paint.measureText(candidate) > maxWidth && current.isNotEmpty()) {
                lines.add(current)
                current = word
            } else {
                current = candidate
            }
        }
        if (current.isNotEmpty()) lines.add(current)
        return lines
    }

    private fun drawFoilLines(
        c: AndroidCanvas,
        text: String,
        cx: Float,
        startY: Float,
        paint: TextPaint,
        maxWidth: Float,
        lineHeight: Float
    ) {
        val lines = if (lineHeight > 0f) wrapWords(paint, text, maxWidth) else listOf(text)
        val shadowOffset = dp(1.2f)
        val highlightOffset = dp(0.8f)
        lines.forEachIndexed { idx, line ->
            val yy = startY + idx * lineHeight
            paint.color = android.graphics.Color.argb(216, 40, 18, 0)
            c.drawText(line, cx, yy + shadowOffset, paint)
            paint.color = android.graphics.Color.argb(76, 255, 255, 255)
            c.drawText(line, cx, yy - highlightOffset, paint)
            paint.color = android.graphics.Color.parseColor("#F2D98F")
            c.drawText(line, cx, yy, paint)
        }
    }

    private fun drawEditorialParagraph(
        c: AndroidCanvas,
        paint: TextPaint,
        text: String,
        left: Float,
        right: Float,
        startY: Float,
        lineHeight: Float,
        dropCapWidth: Float,
        dropLines: Int
    ): Float {
        val words = text.trim().split(" ").filter { it.isNotEmpty() }
        val lines = mutableListOf<List<String>>()
        var currentWords = mutableListOf<String>()

        for (w in words) {
            val offset = if (lines.size < dropLines && dropCapWidth > 0f) dropCapWidth else 0f
            val avail = (right - left - offset).coerceAtLeast(dp(40f))
            val candidate = (currentWords + w).joinToString(" ")
            if (paint.measureText(candidate) > avail && currentWords.isNotEmpty()) {
                lines.add(currentWords)
                currentWords = mutableListOf(w)
            } else {
                currentWords.add(w)
            }
        }
        if (currentWords.isNotEmpty()) lines.add(currentWords)

        var y = startY
        val normalSpace = paint.measureText(" ")
        val maxWordGap = normalSpace * 2.4f
        lines.forEachIndexed { idx, lineWords ->
            val offset = if (idx < dropLines && dropCapWidth > 0f) dropCapWidth else 0f
            val avail = (right - left - offset).coerceAtLeast(dp(40f))
            var cx = left + offset
            if (idx < lines.lastIndex && lineWords.size > 1) {
                val totalWordW = lineWords.sumOf { paint.measureText(it).toDouble() }.toFloat()
                val rawGap = (avail - totalWordW) / (lineWords.size - 1)
                if (rawGap in normalSpace * 0.7f..maxWordGap) {
                    for (w in lineWords) {
                        c.drawText(w, cx, y, paint)
                        cx += paint.measureText(w) + rawGap
                    }
                } else {
                    c.drawText(lineWords.joinToString(" "), cx, y, paint)
                }
            } else {
                c.drawText(lineWords.joinToString(" "), cx, y, paint)
            }
            y += lineHeight
        }
        return y + dp(12f)
    }
}

/**
 * Generates a gentle paper-rustle sound effect completely offline via AudioTrack.
 */
private class FolioPaperSoundPlayer {
    private var track: AudioTrack? = null

    fun playRustle() {
        runCatching {
            val sampleRate = 22050
            val durationSamples = (sampleRate * 0.24f).toInt()
            val pcm = ShortArray(durationSamples)
            val rng = Random(System.nanoTime())
            var lp = 0f
            for (i in 0 until durationSamples) {
                val t = i.toFloat() / durationSamples
                val env = (1f - t).pow(2.4f) * min(1f, t / 0.06f)
                val white = rng.nextFloat() * 2f - 1f
                lp = lp * 0.65f + white * 0.35f
                val bp = white - lp
                pcm[i] = (bp * env * 0.28f * Short.MAX_VALUE)
                    .toInt()
                    .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                    .toShort()
            }

            track?.release()
            val at = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setSampleRate(sampleRate)
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(pcm.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()
            at.write(pcm, 0, pcm.size)
            at.play()
            track = at
        }
    }

    fun release() {
        runCatching { track?.release() }
        track = null
    }
}
