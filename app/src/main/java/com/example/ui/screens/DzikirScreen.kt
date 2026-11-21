package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.calc.PrayerTimeCalculator
import com.example.data.local.IslamicContentData
import com.example.data.model.DzikirPreset
import com.example.data.preferences.UserPrefsState
import com.example.ui.components.NeumorphicCard
import com.example.ui.components.NeumorphicCircleButton
import com.example.ui.components.NeumorphicPillButton
import com.example.ui.components.NeumorphicSubScreenHeader
import com.example.ui.components.neumorphicCircle
import com.example.ui.theme.AmiriArabicFamily
import com.example.ui.theme.BarlowCondensedFamily
import com.example.ui.theme.LocalAppStrings
import com.example.ui.theme.StoneAccent
import com.example.ui.theme.StoneBackground
import com.example.ui.theme.StoneBackgroundBottom
import com.example.ui.theme.StoneBackgroundTop
import com.example.ui.theme.StoneDivider
import com.example.ui.theme.StoneInkPrimary
import com.example.ui.theme.StoneInkSecondary
import com.example.ui.theme.StoneSuccess
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun DzikirScreen(
    prefs: UserPrefsState,
    currentPreset: DzikirPreset,
    onBack: () -> Unit,
    onIncrement: () -> Boolean,
    onReset: () -> Unit,
    onSelectPreset: (DzikirPreset) -> Unit,
    onUpdateTarget: (Int) -> Unit
) {
    val s = LocalAppStrings.current
    val haptic = LocalHapticFeedback.current
    val progress = if (prefs.dzikirTarget > 0) {
        (prefs.dzikirCount.toFloat() / prefs.dzikirTarget.toFloat()).coerceIn(0f, 1f)
    } else {
        (prefs.dzikirCount % 33) / 33f
    }
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = spring(stiffness = 400f),
        label = "tasbih_progress"
    )

    val lifetimeText = if (s.isBangla) {
        PrayerTimeCalculator.toBanglaDigits(prefs.dzikirLifetimeTotal.toString())
    } else {
        prefs.dzikirLifetimeTotal.toString()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(StoneBackgroundTop, StoneBackground, StoneBackgroundBottom))
            )
            .statusBarsPadding()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        NeumorphicSubScreenHeader(
            title = s.dzikirTitle,
            subtitle = "${s.lifetimeTotal} $lifetimeText",
            onBack = onBack,
            trailingAction = {
                NeumorphicCircleButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onReset()
                    },
                    size = 44.dp,
                    modifier = Modifier.testTag("dzikir_reset_button")
                ) {
                    Icon(Icons.Rounded.Refresh, contentDescription = "Reset counter", tint = StoneInkPrimary, modifier = Modifier.size(20.dp))
                }
            }
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(IslamicContentData.dzikirPresets, key = { it.id }) { preset ->
                NeumorphicPillButton(
                    text = preset.title,
                    selected = preset.id == currentPreset.id,
                    onClick = { onSelectPreset(preset) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        NeumorphicCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            cornerRadius = 24.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = currentPreset.arabic,
                    fontFamily = AmiriArabicFamily,
                    fontSize = 28.sp,
                    lineHeight = 44.sp,
                    color = StoneInkPrimary,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = currentPreset.latin,
                    style = MaterialTheme.typography.titleMedium,
                    color = StoneAccent,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "\"${currentPreset.translation}\"",
                    style = MaterialTheme.typography.bodyMedium,
                    color = StoneInkSecondary,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(26.dp))

        // Interactive Neumorphic Bead Ring + Central Tap Button
        val interactionSource = remember { MutableInteractionSource() }
        val completedTarget = prefs.dzikirTarget > 0 && prefs.dzikirCount >= prefs.dzikirTarget
        val ringScale by animateFloatAsState(
            targetValue = if (completedTarget) 1.03f else 1f,
            animationSpec = spring(stiffness = 300f),
            label = "tasbih_ring_scale"
        )

        Box(
            modifier = Modifier
                .size(250.dp)
                .scale(ringScale)
                .testTag("tasbih_tap_button"),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val cx = size.width / 2f
                val cy = size.height / 2f
                val radius = size.minDimension / 2f - 16.dp.toPx()

                // Track arc
                drawCircle(
                    color = StoneDivider.copy(alpha = 0.55f),
                    radius = radius,
                    center = Offset(cx, cy),
                    style = Stroke(width = 8.dp.toPx())
                )

                // Progress arc
                drawArc(
                    color = if (completedTarget) StoneSuccess else StoneAccent,
                    startAngle = -90f,
                    sweepAngle = animatedProgress * 360f,
                    useCenter = false,
                    style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                )

                // Subtle bead markers around the circle
                val beads = 12
                for (i in 0 until beads) {
                    val angle = -PI / 2 + (2.0 * PI * i / beads)
                    val bx = cx + (radius * cos(angle)).toFloat()
                    val by = cy + (radius * sin(angle)).toFloat()
                    val active = (i.toFloat() / beads) < animatedProgress
                    drawCircle(
                        color = if (active) (if (completedTarget) StoneSuccess else StoneAccent) else StoneDivider,
                        radius = 4.dp.toPx(),
                        center = Offset(bx, by)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(182.dp)
                    .neumorphicCircle(shadowElevation = 9.dp)
                    .clip(CircleShape)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = ripple(bounded = true, color = StoneAccent.copy(alpha = 0.25f)),
                        onClick = {
                            val hitTarget = onIncrement()
                            haptic.performHapticFeedback(
                                if (hitTarget) HapticFeedbackType.LongPress else HapticFeedbackType.TextHandleMove
                            )
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val countDisplay = if (s.isBangla) {
                        PrayerTimeCalculator.toBanglaDigits(prefs.dzikirCount.toString())
                    } else {
                        prefs.dzikirCount.toString()
                    }
                    val targetDisplay = if (s.isBangla) {
                        PrayerTimeCalculator.toBanglaDigits(prefs.dzikirTarget.toString())
                    } else {
                        prefs.dzikirTarget.toString()
                    }
                    Text(
                        text = countDisplay,
                        fontFamily = BarlowCondensedFamily,
                        fontSize = 68.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (completedTarget) StoneSuccess else StoneInkPrimary
                    )
                    Text(
                        text = if (prefs.dzikirTarget > 0) "${s.targetLabel} $targetDisplay" else s.tapToDhikr,
                        style = MaterialTheme.typography.labelMedium,
                        color = StoneInkSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = s.selectTargetTitle,
            style = MaterialTheme.typography.labelMedium,
            color = StoneInkSecondary
        )
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val targets = listOf(
                33 to if (s.isBangla) "৩৩×" else "33x",
                99 to if (s.isBangla) "৯৯×" else "99x",
                100 to if (s.isBangla) "১০০×" else "100x",
                0 to s.unlimitedLabel
            )
            targets.forEach { (target, label) ->
                NeumorphicPillButton(
                    text = label,
                    selected = prefs.dzikirTarget == target,
                    onClick = { onUpdateTarget(target) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
