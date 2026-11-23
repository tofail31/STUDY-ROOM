package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StudyStatus
import com.example.ui.theme.AmiriArabicFamily
import com.example.ui.theme.StoneAccent
import com.example.ui.theme.StoneInkPrimary
import com.example.ui.theme.StoneInkSecondary
import com.example.ui.theme.StoneSuccess
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ProfileRingIcon(
    modifier: Modifier = Modifier,
    tint: Color = StoneInkSecondary
) {
    Canvas(modifier = modifier) {
        val strokeW = size.minDimension * 0.11f
        val r = (size.minDimension - strokeW) / 2f
        val cx = size.width / 2f
        val cy = size.height / 2f

        // Outer ring
        drawCircle(
            color = tint,
            radius = r,
            center = Offset(cx, cy),
            style = Stroke(width = strokeW)
        )
        // Head
        drawCircle(
            color = tint,
            radius = r * 0.30f,
            center = Offset(cx, cy - r * 0.22f)
        )
        // Shoulders arc clipped inside ring
        val shoulderPath = Path().apply {
            val topY = cy + r * 0.20f
            moveTo(cx - r * 0.62f, cy + r * 0.72f)
            cubicTo(
                cx - r * 0.50f, topY,
                cx + r * 0.50f, topY,
                cx + r * 0.62f, cy + r * 0.72f
            )
            arcTo(
                rect = Rect(cx - r * 0.85f, cy - r * 0.85f, cx + r * 0.85f, cy + r * 0.85f),
                startAngleDegrees = 42f,
                sweepAngleDegrees = 96f,
                forceMoveTo = false
            )
            close()
        }
        drawPath(shoulderPath, color = tint)
    }
}

@Composable
fun QuranStandIcon(
    modifier: Modifier = Modifier,
    tint: Color = StoneInkPrimary
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = Stroke(width = w * 0.048f, cap = StrokeCap.Round, join = StrokeJoin.Round)

        // Left & right open pages
        val leftPage = Path().apply {
            moveTo(w * 0.50f, h * 0.38f)
            lineTo(w * 0.20f, h * 0.29f)
            lineTo(w * 0.14f, h * 0.59f)
            lineTo(w * 0.50f, h * 0.68f)
            close()
        }
        val rightPage = Path().apply {
            moveTo(w * 0.50f, h * 0.38f)
            lineTo(w * 0.80f, h * 0.29f)
            lineTo(w * 0.86f, h * 0.59f)
            lineTo(w * 0.50f, h * 0.68f)
            close()
        }
        drawPath(leftPage, color = tint, style = stroke)
        drawPath(rightPage, color = tint, style = stroke)

        // Text lines on left & right pages
        for (i in 0..2) {
            val yOff = i * 0.075f
            drawLine(
                color = tint,
                start = Offset(w * 0.24f, h * (0.39f + yOff)),
                end = Offset(w * 0.43f, h * (0.45f + yOff)),
                strokeWidth = w * 0.032f,
                cap = StrokeCap.Round
            )
            drawLine(
                color = tint,
                start = Offset(w * 0.57f, h * (0.45f + yOff)),
                end = Offset(w * 0.76f, h * (0.39f + yOff)),
                strokeWidth = w * 0.032f,
                cap = StrokeCap.Round
            )
        }

        // Crossed Rehal stand legs
        drawLine(
            color = tint,
            start = Offset(w * 0.24f, h * 0.64f),
            end = Offset(w * 0.64f, h * 0.86f),
            strokeWidth = w * 0.052f,
            cap = StrokeCap.Round
        )
        drawLine(
            color = tint,
            start = Offset(w * 0.76f, h * 0.64f),
            end = Offset(w * 0.36f, h * 0.86f),
            strokeWidth = w * 0.052f,
            cap = StrokeCap.Round
        )

        // Three 4-point sparkles above
        fun drawSparkle(cx: Float, cy: Float, r: Float) {
            drawLine(tint, Offset(cx, cy - r), Offset(cx, cy + r), strokeWidth = w * 0.035f, cap = StrokeCap.Round)
            drawLine(tint, Offset(cx - r, cy), Offset(cx + r, cy), strokeWidth = w * 0.035f, cap = StrokeCap.Round)
        }
        drawSparkle(w * 0.50f, h * 0.16f, w * 0.055f)
        drawSparkle(w * 0.31f, h * 0.21f, w * 0.038f)
        drawSparkle(w * 0.69f, h * 0.21f, w * 0.038f)
    }
}

@Composable
fun DuaPrayingIcon(
    modifier: Modifier = Modifier,
    tint: Color = StoneInkPrimary
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = Stroke(width = w * 0.048f, cap = StrokeCap.Round, join = StrokeJoin.Round)

        // Prayer mat base
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.14f, h * 0.78f),
            size = Size(w * 0.72f, h * 0.09f),
            cornerRadius = CornerRadius(w * 0.03f),
            style = stroke
        )

        // Head
        drawCircle(
            color = tint,
            radius = w * 0.09f,
            center = Offset(w * 0.42f, h * 0.28f),
            style = stroke
        )

        // Kneeling body + raised arms
        val bodyPath = Path().apply {
            moveTo(w * 0.34f, h * 0.40f)
            lineTo(w * 0.26f, h * 0.78f)
            moveTo(w * 0.26f, h * 0.78f)
            lineTo(w * 0.56f, h * 0.78f)
            lineTo(w * 0.52f, h * 0.62f)
            lineTo(w * 0.44f, h * 0.42f)
            // Raised arms in Dua
            moveTo(w * 0.44f, h * 0.48f)
            lineTo(w * 0.62f, h * 0.48f)
            lineTo(w * 0.66f, h * 0.38f)
        }
        drawPath(bodyPath, color = tint, style = stroke)

        // Crescent moon top-right
        drawArc(
            color = tint,
            startAngle = -65f,
            sweepAngle = 215f,
            useCenter = false,
            topLeft = Offset(w * 0.64f, h * 0.14f),
            size = Size(w * 0.22f, h * 0.22f),
            style = stroke
        )
    }
}

@Composable
fun DzikirTasbihIcon(
    modifier: Modifier = Modifier,
    tint: Color = StoneInkPrimary
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cx = w * 0.46f
        val cy = h * 0.52f
        val loopR = w * 0.29f
        val beadR = w * 0.046f
        val stroke = Stroke(width = w * 0.036f, cap = StrokeCap.Round)

        val count = 13
        for (i in 0 until count) {
            val angle = (2.0 * PI * i / count) - PI / 4
            val bx = cx + (loopR * cos(angle)).toFloat()
            val by = cy + (loopR * sin(angle)).toFloat()
            drawCircle(
                color = tint,
                radius = beadR,
                center = Offset(bx, by),
                style = stroke
            )
        }

        // Imam bead & tassel top-right
        val imamX = cx + (loopR * cos(-PI / 4)).toFloat() + w * 0.08f
        val imamY = cy + (loopR * sin(-PI / 4)).toFloat() - w * 0.08f
        drawLine(
            color = tint,
            start = Offset(imamX, imamY),
            end = Offset(imamX + w * 0.12f, imamY - h * 0.12f),
            strokeWidth = w * 0.042f,
            cap = StrokeCap.Round
        )
        drawLine(
            color = tint,
            start = Offset(imamX + w * 0.05f, imamY - h * 0.05f),
            end = Offset(imamX + w * 0.14f, imamY + h * 0.02f),
            strokeWidth = w * 0.032f,
            cap = StrokeCap.Round
        )

        // Tiny motion sparkles top-left
        drawLine(tint, Offset(w * 0.16f, h * 0.22f), Offset(w * 0.22f, h * 0.26f), strokeWidth = w * 0.032f, cap = StrokeCap.Round)
        drawLine(tint, Offset(w * 0.22f, h * 0.14f), Offset(w * 0.26f, h * 0.20f), strokeWidth = w * 0.032f, cap = StrokeCap.Round)
    }
}

@Composable
fun GroupsIcon(
    modifier: Modifier = Modifier,
    tint: Color = StoneInkPrimary
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = Stroke(width = w * 0.048f, cap = StrokeCap.Round, join = StrokeJoin.Round)

        // Center person head + shoulders
        drawCircle(
            color = tint,
            radius = w * 0.105f,
            center = Offset(w * 0.50f, h * 0.34f),
            style = stroke
        )
        val centerBody = Path().apply {
            moveTo(w * 0.30f, h * 0.74f)
            cubicTo(w * 0.30f, h * 0.56f, w * 0.70f, h * 0.56f, w * 0.70f, h * 0.74f)
        }
        drawPath(centerBody, color = tint, style = stroke)

        // Left person head + shoulder
        drawCircle(
            color = tint,
            radius = w * 0.08f,
            center = Offset(w * 0.24f, h * 0.41f),
            style = stroke
        )
        val leftBody = Path().apply {
            moveTo(w * 0.11f, h * 0.72f)
            cubicTo(w * 0.11f, h * 0.58f, w * 0.26f, h * 0.56f, w * 0.33f, h * 0.60f)
        }
        drawPath(leftBody, color = tint, style = stroke)

        // Right person head + shoulder
        drawCircle(
            color = tint,
            radius = w * 0.08f,
            center = Offset(w * 0.76f, h * 0.41f),
            style = stroke
        )
        val rightBody = Path().apply {
            moveTo(w * 0.89f, h * 0.72f)
            cubicTo(w * 0.89f, h * 0.58f, w * 0.74f, h * 0.56f, w * 0.67f, h * 0.60f)
        }
        drawPath(rightBody, color = tint, style = stroke)
    }
}

@Composable
fun HalalStampIcon(
    modifier: Modifier = Modifier,
    tint: Color = StoneInkPrimary
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width / 2f
            val cy = size.height / 2f
            drawCircle(
                color = tint,
                radius = size.minDimension * 0.42f,
                center = Offset(cx, cy),
                style = Stroke(width = size.minDimension * 0.042f)
            )
            drawCircle(
                color = tint,
                radius = size.minDimension * 0.34f,
                center = Offset(cx, cy),
                style = Stroke(width = size.minDimension * 0.025f)
            )
        }
        Text(
            text = "حلال",
            fontFamily = AmiriArabicFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            color = tint,
            lineHeight = 11.sp
        )
    }
}

@Composable
fun TrackerChecklistIcon(
    modifier: Modifier = Modifier,
    tint: Color = StoneInkPrimary
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = Stroke(width = w * 0.048f, cap = StrokeCap.Round, join = StrokeJoin.Round)

        // Outer rounded syllabus board
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.18f, h * 0.18f),
            size = Size(w * 0.64f, h * 0.68f),
            cornerRadius = CornerRadius(w * 0.10f),
            style = stroke
        )
        // Top clip tab
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.36f, h * 0.11f),
            size = Size(w * 0.28f, h * 0.12f),
            cornerRadius = CornerRadius(w * 0.05f),
            style = stroke
        )
        // Row 1 check + line
        drawPath(
            Path().apply {
                moveTo(w * 0.28f, h * 0.39f)
                lineTo(w * 0.34f, h * 0.45f)
                lineTo(w * 0.44f, h * 0.34f)
            },
            color = StoneAccent,
            style = stroke
        )
        drawLine(tint, Offset(w * 0.50f, h * 0.40f), Offset(w * 0.72f, h * 0.40f), strokeWidth = w * 0.045f, cap = StrokeCap.Round)

        // Row 2 check + line
        drawPath(
            Path().apply {
                moveTo(w * 0.28f, h * 0.56f)
                lineTo(w * 0.34f, h * 0.62f)
                lineTo(w * 0.44f, h * 0.51f)
            },
            color = StoneAccent,
            style = stroke
        )
        drawLine(tint, Offset(w * 0.50f, h * 0.57f), Offset(w * 0.72f, h * 0.57f), strokeWidth = w * 0.045f, cap = StrokeCap.Round)

        // Row 3 dot + line
        drawCircle(color = tint, radius = w * 0.036f, center = Offset(w * 0.34f, h * 0.72f))
        drawLine(tint, Offset(w * 0.50f, h * 0.72f), Offset(w * 0.68f, h * 0.72f), strokeWidth = w * 0.045f, cap = StrokeCap.Round)
    }
}

@Composable
fun ExamQuizIcon(
    modifier: Modifier = Modifier,
    tint: Color = StoneInkPrimary
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = Stroke(width = w * 0.048f, cap = StrokeCap.Round, join = StrokeJoin.Round)

        // Exam paper sheet
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.16f, h * 0.14f),
            size = Size(w * 0.56f, h * 0.72f),
            cornerRadius = CornerRadius(w * 0.09f),
            style = stroke
        )
        // A+ / Q header bar
        drawLine(tint, Offset(w * 0.27f, h * 0.30f), Offset(w * 0.56f, h * 0.30f), strokeWidth = w * 0.045f, cap = StrokeCap.Round)
        // Bubble 1 (filled accent) + line
        drawCircle(color = StoneAccent, radius = w * 0.045f, center = Offset(w * 0.31f, h * 0.46f))
        drawLine(tint, Offset(w * 0.41f, h * 0.46f), Offset(w * 0.61f, h * 0.46f), strokeWidth = w * 0.04f, cap = StrokeCap.Round)
        // Bubble 2 + line
        drawCircle(color = tint, radius = w * 0.045f, center = Offset(w * 0.31f, h * 0.61f), style = Stroke(width = w * 0.035f))
        drawLine(tint, Offset(w * 0.41f, h * 0.61f), Offset(w * 0.61f, h * 0.61f), strokeWidth = w * 0.04f, cap = StrokeCap.Round)
        // Pencil angled bottom-right
        val pencilPath = Path().apply {
            moveTo(w * 0.62f, h * 0.78f)
            lineTo(w * 0.84f, h * 0.48f)
            lineTo(w * 0.91f, h * 0.54f)
            lineTo(w * 0.69f, h * 0.84f)
            lineTo(w * 0.59f, h * 0.87f)
            close()
        }
        drawPath(pencilPath, color = StoneAccent, style = Fill)
        drawPath(pencilPath, color = tint, style = Stroke(width = w * 0.036f, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
fun StoryBookIcon(
    modifier: Modifier = Modifier,
    tint: Color = StoneInkPrimary
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = Stroke(width = w * 0.048f, cap = StrokeCap.Round, join = StrokeJoin.Round)

        // Open book left page
        val leftPage = Path().apply {
            moveTo(w * 0.50f, h * 0.25f)
            cubicTo(w * 0.36f, h * 0.20f, w * 0.22f, h * 0.21f, w * 0.14f, h * 0.27f)
            lineTo(w * 0.14f, h * 0.77f)
            cubicTo(w * 0.22f, h * 0.71f, w * 0.36f, h * 0.70f, w * 0.50f, h * 0.75f)
            close()
        }
        drawPath(leftPage, color = tint, style = stroke)

        // Open book right page
        val rightPage = Path().apply {
            moveTo(w * 0.50f, h * 0.25f)
            cubicTo(w * 0.64f, h * 0.20f, w * 0.78f, h * 0.21f, w * 0.86f, h * 0.27f)
            lineTo(w * 0.86f, h * 0.77f)
            cubicTo(w * 0.78f, h * 0.71f, w * 0.64f, h * 0.70f, w * 0.50f, h * 0.75f)
            close()
        }
        drawPath(rightPage, color = tint, style = stroke)

        // Curled page accent lines on left & right pages + terracotta bookmark ribbon
        drawLine(tint, Offset(w * 0.24f, h * 0.40f), Offset(w * 0.41f, h * 0.38f), strokeWidth = w * 0.036f, cap = StrokeCap.Round)
        drawLine(tint, Offset(w * 0.24f, h * 0.52f), Offset(w * 0.41f, h * 0.50f), strokeWidth = w * 0.036f, cap = StrokeCap.Round)
        drawLine(tint, Offset(w * 0.59f, h * 0.40f), Offset(w * 0.76f, h * 0.42f), strokeWidth = w * 0.036f, cap = StrokeCap.Round)

        val bookmark = Path().apply {
            moveTo(w * 0.68f, h * 0.22f)
            lineTo(w * 0.76f, h * 0.22f)
            lineTo(w * 0.76f, h * 0.53f)
            lineTo(w * 0.72f, h * 0.47f)
            lineTo(w * 0.68f, h * 0.53f)
            close()
        }
        drawPath(bookmark, color = StoneAccent, style = Fill)
    }
}

@Composable
fun SelengkapnyaGridIcon(
    modifier: Modifier = Modifier,
    tint: Color = StoneInkPrimary
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val boxSize = w * 0.30f
        val corner = CornerRadius(w * 0.07f)
        val stroke = Stroke(width = w * 0.06f)

        // Top-left outlined square
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.14f, h * 0.16f),
            size = Size(boxSize, boxSize),
            cornerRadius = corner,
            style = stroke
        )
        // Top-right rotated diamond outlined
        rotate(degrees = 45f, pivot = Offset(w * 0.69f, h * 0.31f)) {
            drawRoundRect(
                color = tint,
                topLeft = Offset(w * 0.55f, h * 0.17f),
                size = Size(boxSize * 0.92f, boxSize * 0.92f),
                cornerRadius = corner,
                style = stroke
            )
        }
        // Bottom-left outlined square
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.14f, h * 0.54f),
            size = Size(boxSize, boxSize),
            cornerRadius = corner,
            style = stroke
        )
        // Bottom-right filled square
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.54f, h * 0.54f),
            size = Size(boxSize, boxSize),
            cornerRadius = corner
        )
    }
}

@Composable
fun AyatBookIcon(
    modifier: Modifier = Modifier,
    tint: Color = StoneInkSecondary
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Left page filled
        val left = Path().apply {
            moveTo(w * 0.48f, h * 0.25f)
            cubicTo(w * 0.34f, h * 0.14f, w * 0.18f, h * 0.18f, w * 0.08f, h * 0.24f)
            lineTo(w * 0.08f, h * 0.80f)
            cubicTo(w * 0.20f, h * 0.74f, w * 0.36f, h * 0.76f, w * 0.48f, h * 0.86f)
            close()
        }
        // Right page filled
        val right = Path().apply {
            moveTo(w * 0.52f, h * 0.25f)
            cubicTo(w * 0.66f, h * 0.14f, w * 0.82f, h * 0.18f, w * 0.92f, h * 0.24f)
            lineTo(w * 0.92f, h * 0.80f)
            cubicTo(w * 0.80f, h * 0.74f, w * 0.64f, h * 0.76f, w * 0.52f, h * 0.86f)
            close()
        }
        drawPath(left, color = tint)
        drawPath(right, color = tint)
    }
}

@Composable
fun StudyTableIllustration(
    status: StudyStatus,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val targetAspect = 1.16f
        val maxDeskH = h * 0.58f
        val deskW = minOf(w * 0.94f, maxDeskH * targetAspect)
        val deskH = deskW / targetAspect
        val ox = (w - deskW) / 2f
        val oy = h * 0.13f

        fun tx(x: Float) = ox + deskW * x
        fun ty(y: Float) = oy + deskH * y

        val isStudying = status == StudyStatus.STUDYING
        val isResting = status == StudyStatus.RESTING

        val outlineColor = when (status) {
            StudyStatus.STUDYING -> StoneInkPrimary.copy(alpha = 0.88f)
            StudyStatus.RESTING -> StoneInkPrimary.copy(alpha = 0.68f)
            StudyStatus.OFFLINE -> StoneInkSecondary.copy(alpha = 0.46f)
        }
        val mainStroke = Stroke(
            width = (deskW * 0.012f).coerceAtLeast(1.15f),
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )
        val fineStroke = Stroke(
            width = (deskW * 0.0085f).coerceAtLeast(0.85f),
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )

        val topSurfaceFill = when (status) {
            StudyStatus.STUDYING -> Color(0xFFFDFBF8)
            StudyStatus.RESTING -> Color(0xFFF6F1EB)
            StudyStatus.OFFLINE -> Color(0xFFEDE8E2)
        }
        val frontWoodFill = when (status) {
            StudyStatus.STUDYING -> Color(0xFFEAE3DA)
            StudyStatus.RESTING -> Color(0xFFE5DDD4)
            StudyStatus.OFFLINE -> Color(0xFFDCD5CD)
        }
        val recessFill = when (status) {
            StudyStatus.STUDYING -> Color(0xFFD3C9BD)
            StudyStatus.RESTING -> Color(0xFFD0C7BC)
            StudyStatus.OFFLINE -> Color(0xFFCBC3BA)
        }
        val propFill = when (status) {
            StudyStatus.STUDYING -> Color(0xFFF4EFE8)
            StudyStatus.RESTING -> Color(0xFFEEE8E0)
            StudyStatus.OFFLINE -> Color(0xFFE3DDD6)
        }
        val leafColor = when (status) {
            StudyStatus.STUDYING -> StoneSuccess.copy(alpha = 0.88f)
            StudyStatus.RESTING -> StoneAccent.copy(alpha = 0.74f)
            StudyStatus.OFFLINE -> StoneInkSecondary.copy(alpha = 0.42f)
        }
        val accentHighlight = when (status) {
            StudyStatus.STUDYING -> StoneSuccess
            StudyStatus.RESTING -> StoneAccent
            StudyStatus.OFFLINE -> StoneInkSecondary.copy(alpha = 0.5f)
        }

        // Full-box subtle studio backdrop & perspective tile floor
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    accentHighlight.copy(alpha = if (isStudying) 0.09f else 0.04f),
                    Color.Transparent,
                    StoneInkSecondary.copy(alpha = if (isStudying) 0.11f else 0.06f)
                )
            )
        )

        val gridColor = StoneInkSecondary.copy(alpha = if (isStudying) 0.16f else 0.10f)
        val gridWidth = (deskW * 0.0055f).coerceAtLeast(0.65f)
        val horizonY = ty(0.79f)
        listOf(horizonY, ty(0.88f), ty(0.97f), (h * 0.76f)).distinct().forEach { gy ->
            if (gy < h) {
                drawLine(gridColor, Offset(0f, gy), Offset(w, gy), gridWidth)
            }
        }
        listOf(0.18f, 0.38f, 0.50f, 0.62f, 0.82f).forEach { gx ->
            val topX = w * gx
            val botX = w * (0.5f + (gx - 0.5f) * 1.45f)
            drawLine(gridColor, Offset(topX, horizonY), Offset(botX, h), gridWidth)
        }

        // Floor shadow under pedestal & leg
        drawOval(
            color = StoneInkPrimary.copy(alpha = if (isStudying) 0.13f else 0.07f),
            topLeft = Offset(tx(0.06f), ty(0.91f)),
            size = Size(deskW * 0.88f, deskH * 0.075f)
        )

        // 2. Left 3-Drawer Pedestal
        val pedSide = Path().apply {
            moveTo(tx(0.395f), ty(0.645f))
            lineTo(tx(0.425f), ty(0.630f))
            lineTo(tx(0.425f), ty(0.920f))
            lineTo(tx(0.395f), ty(0.945f))
            close()
        }
        drawPath(pedSide, color = recessFill, style = Fill)
        drawPath(pedSide, color = outlineColor, style = fineStroke)

        drawRect(
            color = frontWoodFill,
            topLeft = Offset(tx(0.095f), ty(0.645f)),
            size = Size(deskW * 0.300f, deskH * 0.300f)
        )
        drawRect(
            color = outlineColor,
            topLeft = Offset(tx(0.095f), ty(0.645f)),
            size = Size(deskW * 0.300f, deskH * 0.300f),
            style = mainStroke
        )
        listOf(0.745f, 0.845f).forEach { dy ->
            drawLine(outlineColor, Offset(tx(0.095f), ty(dy)), Offset(tx(0.395f), ty(dy)), fineStroke.width)
        }
        listOf(0.695f, 0.795f, 0.895f).forEach { hy ->
            drawArc(
                color = outlineColor,
                startAngle = 15f,
                sweepAngle = 150f,
                useCenter = false,
                topLeft = Offset(tx(0.220f), ty(hy - 0.012f)),
                size = Size(deskW * 0.050f, deskH * 0.024f),
                style = fineStroke
            )
        }

        // 3. Right Side Support Leg Panel
        val rightLeg = Path().apply {
            moveTo(tx(0.775f), ty(0.635f))
            lineTo(tx(0.875f), ty(0.645f))
            lineTo(tx(0.875f), ty(0.945f))
            lineTo(tx(0.775f), ty(0.905f))
            close()
        }
        drawPath(rightLeg, color = recessFill, style = Fill)
        drawPath(rightLeg, color = outlineColor, style = mainStroke)

        // 4. Upper Hutch Recessed Back & Shaped Side Walls
        val hutchBack = Path().apply {
            moveTo(tx(0.145f), ty(0.215f))
            lineTo(tx(0.855f), ty(0.215f))
            lineTo(tx(0.855f), ty(0.575f))
            lineTo(tx(0.145f), ty(0.575f))
            close()
        }
        drawPath(hutchBack, color = recessFill.copy(alpha = 0.58f), style = Fill)

        if (isStudying) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(accentHighlight.copy(alpha = 0.25f), Color.Transparent),
                    center = Offset(tx(0.49f), ty(0.51f)),
                    radius = deskW * 0.30f
                ),
                radius = deskW * 0.30f,
                center = Offset(tx(0.49f), ty(0.51f))
            )
        }

        val leftHutch = Path().apply {
            moveTo(tx(0.105f), ty(0.575f))
            lineTo(tx(0.115f), ty(0.390f))
            lineTo(tx(0.130f), ty(0.215f))
            lineTo(tx(0.155f), ty(0.215f))
            lineTo(tx(0.155f), ty(0.575f))
            close()
        }
        drawPath(leftHutch, color = frontWoodFill, style = Fill)
        drawPath(leftHutch, color = outlineColor, style = mainStroke)

        val rightHutch = Path().apply {
            moveTo(tx(0.895f), ty(0.575f))
            lineTo(tx(0.885f), ty(0.390f))
            lineTo(tx(0.870f), ty(0.215f))
            lineTo(tx(0.845f), ty(0.215f))
            lineTo(tx(0.845f), ty(0.575f))
            close()
        }
        drawPath(rightHutch, color = frontWoodFill, style = Fill)
        drawPath(rightHutch, color = outlineColor, style = mainStroke)

        // 5. Middle Shelf & Top Shelf Canopy
        drawRect(frontWoodFill, Offset(tx(0.145f), ty(0.390f)), Size(deskW * 0.710f, deskH * 0.032f))
        drawRect(outlineColor, Offset(tx(0.145f), ty(0.390f)), Size(deskW * 0.710f, deskH * 0.032f), style = fineStroke)

        drawRect(topSurfaceFill, Offset(tx(0.095f), ty(0.180f)), Size(deskW * 0.810f, deskH * 0.035f))
        drawRect(outlineColor, Offset(tx(0.095f), ty(0.180f)), Size(deskW * 0.810f, deskH * 0.035f), style = mainStroke)

        // 6. Top Shelf Items: Scroll on left, Potted Plant on right
        drawRoundRect(
            color = propFill,
            topLeft = Offset(tx(0.125f), ty(0.132f)),
            size = Size(deskW * 0.255f, deskH * 0.048f),
            cornerRadius = CornerRadius(deskW * 0.015f)
        )
        drawRoundRect(
            color = outlineColor,
            topLeft = Offset(tx(0.125f), ty(0.132f)),
            size = Size(deskW * 0.255f, deskH * 0.048f),
            cornerRadius = CornerRadius(deskW * 0.015f),
            style = fineStroke
        )
        drawLine(outlineColor, Offset(tx(0.245f), ty(0.132f)), Offset(tx(0.245f), ty(0.180f)), fineStroke.width)

        val potPath = Path().apply {
            moveTo(tx(0.755f), ty(0.110f))
            lineTo(tx(0.850f), ty(0.110f))
            lineTo(tx(0.835f), ty(0.180f))
            lineTo(tx(0.770f), ty(0.180f))
            close()
        }
        drawPath(potPath, color = propFill, style = Fill)
        drawPath(potPath, color = outlineColor, style = fineStroke)
        val leavesPath = Path().apply {
            moveTo(tx(0.802f), ty(0.110f))
            lineTo(tx(0.748f), ty(0.068f))
            lineTo(tx(0.785f), ty(0.088f))
            lineTo(tx(0.798f), ty(0.035f))
            lineTo(tx(0.815f), ty(0.086f))
            lineTo(tx(0.858f), ty(0.062f))
            lineTo(tx(0.818f), ty(0.110f))
            close()
        }
        drawPath(leavesPath, color = leafColor, style = Fill)
        drawPath(leavesPath, color = outlineColor, style = fineStroke)

        // 7. Middle Shelf Items
        drawRect(propFill, Offset(tx(0.195f), ty(0.358f)), Size(deskW * 0.145f, deskH * 0.032f))
        drawRect(outlineColor, Offset(tx(0.195f), ty(0.358f)), Size(deskW * 0.145f, deskH * 0.032f), style = fineStroke)
        drawRect(frontWoodFill, Offset(tx(0.200f), ty(0.328f)), Size(deskW * 0.140f, deskH * 0.030f))
        drawRect(outlineColor, Offset(tx(0.200f), ty(0.328f)), Size(deskW * 0.140f, deskH * 0.030f), style = fineStroke)
        val bookmark = Path().apply {
            moveTo(tx(0.300f), ty(0.390f))
            lineTo(tx(0.320f), ty(0.390f))
            lineTo(tx(0.320f), ty(0.440f))
            lineTo(tx(0.310f), ty(0.426f))
            lineTo(tx(0.300f), ty(0.440f))
            close()
        }
        drawPath(bookmark, color = accentHighlight.copy(alpha = 0.85f), style = Fill)
        drawPath(bookmark, color = outlineColor, style = fineStroke)

        drawRect(topSurfaceFill, Offset(tx(0.215f), ty(0.252f)), Size(deskW * 0.085f, deskH * 0.076f))
        drawRect(outlineColor, Offset(tx(0.215f), ty(0.252f)), Size(deskW * 0.085f, deskH * 0.076f), style = fineStroke)

        drawRect(propFill, Offset(tx(0.355f), ty(0.348f)), Size(deskW * 0.068f, deskH * 0.042f))
        drawRect(outlineColor, Offset(tx(0.355f), ty(0.348f)), Size(deskW * 0.068f, deskH * 0.042f), style = fineStroke)

        val hgLeft = tx(0.500f)
        val hgRight = tx(0.548f)
        val hgTop = ty(0.278f)
        val hgBot = ty(0.390f)
        drawLine(outlineColor, Offset(hgLeft, hgTop), Offset(hgRight, hgTop), fineStroke.width)
        drawLine(outlineColor, Offset(hgLeft, hgBot), Offset(hgRight, hgBot), fineStroke.width)
        val hgGlass = Path().apply {
            moveTo(hgLeft + deskW * 0.005f, hgTop)
            lineTo(hgRight - deskW * 0.005f, hgTop)
            lineTo(hgLeft + deskW * 0.005f, hgBot)
            lineTo(hgRight - deskW * 0.005f, hgBot)
            close()
        }
        drawPath(hgGlass, color = accentHighlight.copy(alpha = 0.25f), style = Fill)
        drawPath(hgGlass, color = outlineColor, style = fineStroke)

        drawRect(propFill, Offset(tx(0.562f), ty(0.342f)), Size(deskW * 0.025f, deskH * 0.048f))
        drawRect(outlineColor, Offset(tx(0.562f), ty(0.342f)), Size(deskW * 0.025f, deskH * 0.048f), style = fineStroke)
        drawRect(propFill, Offset(tx(0.594f), ty(0.328f)), Size(deskW * 0.026f, deskH * 0.062f))
        drawRect(outlineColor, Offset(tx(0.594f), ty(0.328f)), Size(deskW * 0.026f, deskH * 0.062f), style = fineStroke)

        val leanBook = Path().apply {
            moveTo(tx(0.625f), ty(0.390f))
            lineTo(tx(0.680f), ty(0.275f))
            lineTo(tx(0.706f), ty(0.286f))
            lineTo(tx(0.652f), ty(0.390f))
            close()
        }
        drawPath(leanBook, color = propFill, style = Fill)
        drawPath(leanBook, color = outlineColor, style = fineStroke)

        listOf(0.720f, 0.754f, 0.790f).forEachIndexed { i, bx ->
            val bh = if (i == 1) 0.125f else 0.110f
            drawRect(if (i == 1) frontWoodFill else propFill, Offset(tx(bx), ty(0.390f - bh)), Size(deskW * 0.030f, deskH * bh))
            drawRect(outlineColor, Offset(tx(bx), ty(0.390f - bh)), Size(deskW * 0.030f, deskH * bh), style = fineStroke)
        }

        // 8. Main Desk Surface Trapezoid & Front Lip
        val deskTop = Path().apply {
            moveTo(tx(0.085f), ty(0.565f))
            lineTo(tx(0.915f), ty(0.565f))
            lineTo(tx(0.955f), ty(0.620f))
            lineTo(tx(0.045f), ty(0.620f))
            close()
        }
        drawPath(deskTop, color = topSurfaceFill, style = Fill)
        drawPath(deskTop, color = outlineColor, style = mainStroke)

        drawRect(frontWoodFill, Offset(tx(0.045f), ty(0.620f)), Size(deskW * 0.910f, deskH * 0.032f))
        drawRect(outlineColor, Offset(tx(0.045f), ty(0.620f)), Size(deskW * 0.910f, deskH * 0.032f), style = mainStroke)

        // 9. Desk Top Props
        drawRect(propFill, Offset(tx(0.158f), ty(0.452f)), Size(deskW * 0.026f, deskH * 0.122f))
        drawRect(outlineColor, Offset(tx(0.158f), ty(0.452f)), Size(deskW * 0.026f, deskH * 0.122f), style = fineStroke)
        drawRect(frontWoodFill, Offset(tx(0.184f), ty(0.464f)), Size(deskW * 0.024f, deskH * 0.110f))
        drawRect(outlineColor, Offset(tx(0.184f), ty(0.464f)), Size(deskW * 0.024f, deskH * 0.110f), style = fineStroke)

        drawOval(
            color = propFill,
            topLeft = Offset(tx(0.305f), ty(0.558f)),
            size = Size(deskW * 0.075f, deskH * 0.026f)
        )
        drawOval(
            color = outlineColor,
            topLeft = Offset(tx(0.305f), ty(0.558f)),
            size = Size(deskW * 0.075f, deskH * 0.026f),
            style = fineStroke
        )
        drawRoundRect(
            color = topSurfaceFill,
            topLeft = Offset(tx(0.320f), ty(0.515f)),
            size = Size(deskW * 0.045f, deskH * 0.048f),
            cornerRadius = CornerRadius(deskW * 0.014f)
        )
        drawRoundRect(
            color = outlineColor,
            topLeft = Offset(tx(0.320f), ty(0.515f)),
            size = Size(deskW * 0.045f, deskH * 0.048f),
            cornerRadius = CornerRadius(deskW * 0.014f),
            style = fineStroke
        )
        if (isResting) {
            val steam = Path().apply {
                moveTo(tx(0.335f), ty(0.502f))
                cubicTo(tx(0.325f), ty(0.482f), tx(0.348f), ty(0.470f), tx(0.338f), ty(0.450f))
                moveTo(tx(0.352f), ty(0.502f))
                cubicTo(tx(0.362f), ty(0.484f), tx(0.342f), ty(0.472f), tx(0.352f), ty(0.454f))
            }
            drawPath(steam, color = StoneAccent.copy(alpha = 0.85f), style = fineStroke)
        }

        val openBookLeft = Path().apply {
            moveTo(tx(0.482f), ty(0.552f))
            lineTo(tx(0.402f), ty(0.542f))
            lineTo(tx(0.372f), ty(0.596f))
            lineTo(tx(0.470f), ty(0.606f))
            close()
        }
        val openBookRight = Path().apply {
            moveTo(tx(0.482f), ty(0.552f))
            lineTo(tx(0.562f), ty(0.542f))
            lineTo(tx(0.585f), ty(0.596f))
            lineTo(tx(0.470f), ty(0.606f))
            close()
        }
        val pageFill = if (isStudying) Color(0xFFFFFFFF) else topSurfaceFill
        drawPath(openBookLeft, color = pageFill, style = Fill)
        drawPath(openBookRight, color = pageFill, style = Fill)
        drawPath(openBookLeft, color = if (isStudying) accentHighlight else outlineColor, style = mainStroke)
        drawPath(openBookRight, color = if (isStudying) accentHighlight else outlineColor, style = mainStroke)
        drawLine(outlineColor.copy(alpha = 0.45f), Offset(tx(0.405f), ty(0.564f)), Offset(tx(0.460f), ty(0.570f)), fineStroke.width)
        drawLine(outlineColor.copy(alpha = 0.45f), Offset(tx(0.396f), ty(0.580f)), Offset(tx(0.456f), ty(0.586f)), fineStroke.width)
        drawLine(outlineColor.copy(alpha = 0.45f), Offset(tx(0.496f), ty(0.568f)), Offset(tx(0.552f), ty(0.562f)), fineStroke.width)
        drawLine(outlineColor.copy(alpha = 0.45f), Offset(tx(0.502f), ty(0.584f)), Offset(tx(0.560f), ty(0.578f)), fineStroke.width)

        val sheet = Path().apply {
            moveTo(tx(0.585f), ty(0.555f))
            lineTo(tx(0.688f), ty(0.555f))
            lineTo(tx(0.706f), ty(0.602f))
            lineTo(tx(0.600f), ty(0.602f))
            close()
        }
        drawPath(sheet, color = topSurfaceFill, style = Fill)
        drawPath(sheet, color = outlineColor.copy(alpha = 0.7f), style = fineStroke)
        drawLine(outlineColor, Offset(tx(0.222f), ty(0.594f)), Offset(tx(0.278f), ty(0.575f)), mainStroke.width)

        drawRect(frontWoodFill, Offset(tx(0.610f), ty(0.494f)), Size(deskW * 0.145f, deskH * 0.056f))
        drawRect(outlineColor, Offset(tx(0.610f), ty(0.494f)), Size(deskW * 0.145f, deskH * 0.056f), style = fineStroke)

        drawLine(outlineColor, Offset(tx(0.796f), ty(0.500f)), Offset(tx(0.785f), ty(0.455f)), fineStroke.width)
        drawLine(outlineColor, Offset(tx(0.818f), ty(0.500f)), Offset(tx(0.828f), ty(0.452f)), fineStroke.width)
        drawRect(propFill, Offset(tx(0.780f), ty(0.498f)), Size(deskW * 0.055f, deskH * 0.072f))
        drawRect(outlineColor, Offset(tx(0.780f), ty(0.498f)), Size(deskW * 0.055f, deskH * 0.072f), style = fineStroke)
    }
}

@Composable
fun MissionTargetIcon(
    modifier: Modifier = Modifier,
    tint: Color = StoneInkPrimary
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cx = w * 0.48f
        val cy = h * 0.54f
        val stroke = Stroke(width = w * 0.052f, cap = StrokeCap.Round, join = StrokeJoin.Round)

        // Outer target ring with small top-right gap for arrow
        drawArc(
            color = tint,
            startAngle = -20f,
            sweepAngle = 315f,
            useCenter = false,
            topLeft = Offset(cx - w * 0.34f, cy - w * 0.34f),
            size = Size(w * 0.68f, w * 0.68f),
            style = stroke
        )
        // Inner ring
        drawCircle(
            color = tint,
            radius = w * 0.19f,
            center = Offset(cx, cy),
            style = Stroke(width = w * 0.044f)
        )
        // Bullseye center
        drawCircle(
            color = StoneAccent,
            radius = w * 0.078f,
            center = Offset(cx, cy)
        )
        // Diagonal arrow shaft into bullseye
        drawLine(
            color = StoneAccent,
            start = Offset(cx + w * 0.04f, cy - h * 0.04f),
            end = Offset(w * 0.85f, h * 0.16f),
            strokeWidth = w * 0.055f,
            cap = StrokeCap.Round
        )
        // Arrow fletching
        drawLine(
            color = tint,
            start = Offset(w * 0.75f, h * 0.16f),
            end = Offset(w * 0.85f, h * 0.16f),
            strokeWidth = w * 0.045f,
            cap = StrokeCap.Round
        )
        drawLine(
            color = tint,
            start = Offset(w * 0.85f, h * 0.16f),
            end = Offset(w * 0.85f, h * 0.27f),
            strokeWidth = w * 0.045f,
            cap = StrokeCap.Round
        )
    }
}

