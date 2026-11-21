package com.example.ui.components

import android.graphics.BlurMaskFilter
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.theme.NeumorphicBevel
import com.example.ui.theme.NeumorphicHighlight
import com.example.ui.theme.NeumorphicShadow
import com.example.ui.theme.StoneInkPrimary
import com.example.ui.theme.StoneInkSecondary
import com.example.ui.theme.StoneSurface
import com.example.ui.theme.StoneSurfaceBottom
import com.example.ui.theme.StoneSurfaceTop

fun Modifier.neumorphicRaised(
    cornerRadius: Dp = 24.dp,
    shadowElevation: Dp = 7.dp,
    lightAlpha: Float = 0.72f,
    darkAlpha: Float = 0.52f,
    backgroundColor: Color = StoneSurface,
    isPressed: Boolean = false
): Modifier = this.drawBehind {
    val radiusPx = cornerRadius.toPx()
    val elevationPx = if (isPressed) (shadowElevation.toPx() * 0.35f) else shadowElevation.toPx()
    val blurPx = (elevationPx * 1.45f).coerceAtLeast(1f)
    val offsetDist = elevationPx * 0.65f

    drawIntoCanvas { canvas ->
        val lightPaint = Paint().apply {
            asFrameworkPaint().apply {
                isAntiAlias = true
                color = NeumorphicHighlight.copy(alpha = lightAlpha).toArgb()
                maskFilter = BlurMaskFilter(blurPx, BlurMaskFilter.Blur.NORMAL)
            }
        }
        val darkPaint = Paint().apply {
            asFrameworkPaint().apply {
                isAntiAlias = true
                color = NeumorphicShadow.copy(alpha = darkAlpha).toArgb()
                maskFilter = BlurMaskFilter(blurPx, BlurMaskFilter.Blur.NORMAL)
            }
        }

        if (!isPressed) {
            canvas.drawRoundRect(
                left = -offsetDist,
                top = -offsetDist,
                right = size.width - offsetDist,
                bottom = size.height - offsetDist,
                radiusX = radiusPx,
                radiusY = radiusPx,
                paint = lightPaint
            )
            canvas.drawRoundRect(
                left = offsetDist,
                top = offsetDist,
                right = size.width + offsetDist,
                bottom = size.height + offsetDist,
                radiusX = radiusPx,
                radiusY = radiusPx,
                paint = darkPaint
            )
        }
    }

    drawRoundRect(
        brush = Brush.linearGradient(
            colors = if (isPressed) {
                listOf(StoneSurfaceBottom, backgroundColor)
            } else {
                listOf(StoneSurfaceTop, backgroundColor, StoneSurfaceBottom)
            },
            start = Offset.Zero,
            end = Offset(size.width, size.height)
        ),
        cornerRadius = CornerRadius(radiusPx, radiusPx)
    )

    // Subtle crisp top-left inner bevel highlight
    drawRoundRect(
        brush = Brush.linearGradient(
            colors = listOf(
                NeumorphicBevel.copy(alpha = if (isPressed) 0.25f else 0.85f),
                Color.Transparent
            ),
            start = Offset.Zero,
            end = Offset(size.width * 0.6f, size.height * 0.6f)
        ),
        cornerRadius = CornerRadius(radiusPx, radiusPx),
        style = Stroke(width = 1.2.dp.toPx())
    )
}

fun Modifier.neumorphicCircle(
    shadowElevation: Dp = 6.dp,
    isPressed: Boolean = false
): Modifier = this.drawBehind {
    val radius = size.minDimension / 2f
    val center = Offset(size.width / 2f, size.height / 2f)
    val elevationPx = if (isPressed) shadowElevation.toPx() * 0.3f else shadowElevation.toPx()
    val blurPx = (elevationPx * 1.4f).coerceAtLeast(1f)
    val offsetDist = elevationPx * 0.6f

    drawIntoCanvas { canvas ->
        val lightPaint = Paint().apply {
            asFrameworkPaint().apply {
                isAntiAlias = true
                color = NeumorphicHighlight.copy(alpha = 0.78f).toArgb()
                maskFilter = BlurMaskFilter(blurPx, BlurMaskFilter.Blur.NORMAL)
            }
        }
        val darkPaint = Paint().apply {
            asFrameworkPaint().apply {
                isAntiAlias = true
                color = NeumorphicShadow.copy(alpha = 0.56f).toArgb()
                maskFilter = BlurMaskFilter(blurPx, BlurMaskFilter.Blur.NORMAL)
            }
        }

        if (!isPressed) {
            canvas.drawCircle(
                center = Offset(center.x - offsetDist, center.y - offsetDist),
                radius = radius,
                paint = lightPaint
            )
            canvas.drawCircle(
                center = Offset(center.x + offsetDist, center.y + offsetDist),
                radius = radius,
                paint = darkPaint
            )
        }
    }

    drawCircle(
        brush = Brush.linearGradient(
            colors = if (isPressed) {
                listOf(StoneSurfaceBottom, StoneSurface)
            } else {
                listOf(StoneSurfaceTop, StoneSurface, StoneSurfaceBottom)
            },
            start = Offset.Zero,
            end = Offset(size.width, size.height)
        ),
        radius = radius,
        center = center
    )

    drawCircle(
        brush = Brush.linearGradient(
            colors = listOf(
                NeumorphicHighlight.copy(alpha = if (isPressed) 0.3f else 0.85f),
                Color.Transparent
            ),
            start = Offset.Zero,
            end = Offset(size.width * 0.75f, size.height * 0.75f)
        ),
        radius = radius - 0.8.dp.toPx(),
        center = center,
        style = Stroke(width = 1.2.dp.toPx())
    )
}

@Composable
fun NeumorphicCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 24.dp,
    shadowElevation: Dp = 7.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed && onClick != null) 0.985f else 1f,
        animationSpec = spring(stiffness = 600f),
        label = "card_scale"
    )
    val shape: Shape = RoundedCornerShape(cornerRadius)

    val baseModifier = modifier
        .scale(scale)
        .neumorphicRaised(
            cornerRadius = cornerRadius,
            shadowElevation = shadowElevation,
            isPressed = isPressed && onClick != null
        )
        .clip(shape)

    val finalModifier = if (onClick != null) {
        baseModifier.clickable(
            interactionSource = interactionSource,
            indication = ripple(color = StoneInkPrimary.copy(alpha = 0.14f)),
            onClick = onClick
        )
    } else {
        baseModifier
    }

    Box(modifier = finalModifier, content = content)
}

@Composable
fun NeumorphicCircleButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 62.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1f,
        animationSpec = spring(stiffness = 700f),
        label = "circle_btn_scale"
    )
    val elevation by animateDpAsState(
        targetValue = if (isPressed) 2.dp else 6.dp,
        label = "circle_btn_elev"
    )

    Box(
        modifier = modifier
            .size(size)
            .minimumInteractiveComponentSize()
            .scale(scale)
            .neumorphicCircle(shadowElevation = elevation, isPressed = isPressed)
            .clip(CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = true, color = StoneInkPrimary.copy(alpha = 0.15f)),
                onClick = onClick
            ),
        contentAlignment = Alignment.Center,
        content = content
    )
}

@Composable
fun NeumorphicPillButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    leadingContent: (@Composable () -> Unit)? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    Box(
        modifier = modifier
            .minimumInteractiveComponentSize()
            .neumorphicRaised(
                cornerRadius = 18.dp,
                shadowElevation = if (selected) 2.dp else 5.dp,
                backgroundColor = if (selected) Color(0xFFD2C8BD) else StoneSurface,
                isPressed = isPressed || selected
            )
            .clip(RoundedCornerShape(18.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = StoneInkPrimary.copy(alpha = 0.14f)),
                onClick = onClick
            )
            .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (leadingContent != null) {
                leadingContent()
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold,
                color = if (selected) StoneInkPrimary else StoneInkSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun NeumorphicSubScreenHeader(
    title: String,
    subtitle: String? = null,
    onBack: () -> Unit,
    trailingAction: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        NeumorphicCircleButton(
            onClick = onBack,
            size = 46.dp,
            modifier = Modifier.testTag("back_button")
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = stringResource(R.string.cd_back),
                tint = StoneInkPrimary,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = StoneInkPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (!subtitle.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelMedium,
                    color = StoneInkSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        if (trailingAction != null) {
            Spacer(modifier = Modifier.width(8.dp))
            trailingAction()
        }
    }
}
