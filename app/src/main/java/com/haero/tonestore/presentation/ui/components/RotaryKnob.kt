package com.haero.tonestore.presentation.ui.components

import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.haero.tonestore.ui.designsystem.Obsidian
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * 실제 앰프/이펙터 노브를 모방한 회전 노브 컴포넌트
 */
@Composable
fun RotaryKnob(
    value: Float,
    onValueChange: (Float) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    size: Dp = 64.dp,
    enabled: Boolean = true,
    steps: Int = 20,
    labelColor: Color? = null,
    isPedalKnob: Boolean = false
) {
    val view = LocalView.current
    val context = LocalContext.current

    val vibrator = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(VibratorManager::class.java)
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Vibrator::class.java)
        }
    }

    val startAngle = -90f
    val sweepAngle = 300f

    val normalizedValue = (value / 10f).coerceIn(0f, 1f)
    val currentAngle = startAngle + (normalizedValue * sweepAngle)

    var previousAngle by remember { mutableFloatStateOf(0f) }
    var accumulatedValue by remember { mutableFloatStateOf(value) }
    var previousStep by remember { mutableIntStateOf((value * steps / 10f).roundToInt()) }

    // Colors mapping to Slate Studio Theme
    val knobRingColor = if (isPedalKnob) Color.Black else Obsidian.colors.surfaceHighlight
    val fillRingColor = if (isPedalKnob) Color.White.copy(alpha = 0.5f) else Obsidian.colors.primary
    val fillRingGlow = if (isPedalKnob) Color.White.copy(alpha = 0.2f) else Obsidian.colors.primary.copy(alpha = 0.3f)
    val pointerColor = if (isPedalKnob) Color.White else Obsidian.colors.primaryLight
    val actualLabelColor = labelColor ?: Obsidian.colors.textSecondary

    fun performTickHaptic() {
        vibrator?.let { vib ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                vib.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK))
            } else {
                @Suppress("DEPRECATION")
                vib.vibrate(10L)
            }
        }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val darkMetal = Color(0xFF1E1E24)
        val metalHighlight = Color(0xFF2C2C35)

        Canvas(
            modifier = Modifier
                .size(size)
                .pointerInput(enabled) {
                    if (enabled.not()) return@pointerInput

                    awaitEachGesture {
                        val down = awaitFirstDown(requireUnconsumed = false)
                        down.consume()

                        val centerX = this@pointerInput.size.width / 2f
                        val centerY = this@pointerInput.size.height / 2f
                        previousAngle = atan2(
                            down.position.y - centerY,
                            down.position.x - centerX
                        ) * (180f / PI.toFloat())
                        accumulatedValue = value
                        previousStep = (value * steps / 10f).roundToInt()

                        do {
                            val event = awaitPointerEvent()
                            event.changes.forEach { change ->
                                if (change.pressed) {
                                    change.consume()

                                    val currentDragAngle = atan2(
                                        change.position.y - centerY,
                                        change.position.x - centerX
                                    ) * (180f / PI.toFloat())

                                    var delta = currentDragAngle - previousAngle
                                    if (delta > 180) delta -= 360
                                    if (delta < -180) delta += 360

                                    val sensitivity = 0.5f
                                    accumulatedValue = (accumulatedValue + delta * sensitivity / 27f).coerceIn(0f, 10f)

                                    val currentStep = (accumulatedValue * steps / 10f).roundToInt()
                                    if (currentStep != previousStep) {
                                        performTickHaptic()
                                        previousStep = currentStep
                                    }

                                    onValueChange(accumulatedValue)
                                    previousAngle = currentDragAngle
                                }
                            }
                        } while (event.changes.any { it.pressed })
                    }
                }
        ) {
            val strokeWidth = size.toPx() * 0.12f
            val radius = (size.toPx() - strokeWidth) / 2f
            val center = Offset(size.toPx() / 2f, size.toPx() / 2f)

            // Outer Base Track
            drawArc(
                color = knobRingColor,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = strokeWidth * 1.5f, cap = StrokeCap.Round),
                topLeft = Offset(strokeWidth / 2f, strokeWidth / 2f),
                size = androidx.compose.ui.geometry.Size(radius * 2f, radius * 2f)
            )

            // Glow Base Track
            drawArc(
                color = fillRingGlow,
                startAngle = startAngle,
                sweepAngle = normalizedValue * sweepAngle,
                useCenter = false,
                style = Stroke(width = strokeWidth * 1.5f, cap = StrokeCap.Round),
                topLeft = Offset(strokeWidth / 2f, strokeWidth / 2f),
                size = androidx.compose.ui.geometry.Size(radius * 2f, radius * 2f)
            )

            // Active Track
            drawArc(
                color = fillRingColor,
                startAngle = startAngle,
                sweepAngle = normalizedValue * sweepAngle,
                useCenter = false,
                style = Stroke(width = strokeWidth * 0.7f, cap = StrokeCap.Round),
                topLeft = Offset(strokeWidth / 2f, strokeWidth / 2f),
                size = androidx.compose.ui.geometry.Size(radius * 2f, radius * 2f)
            )

            // Central Area Base
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(metalHighlight, darkMetal),
                    center = center,
                    radius = radius * 0.8f
                ),
                radius = radius * 0.8f,
                center = center
            )

            // Central Inner Bevel
            drawCircle(
                color = Color.Black.copy(alpha = 0.3f),
                radius = radius * 0.75f,
                center = center,
                style = Stroke(width = 2f)
            )

            // Pointer
            val pointerAngle = currentAngle * (PI.toFloat() / 180f)
            val pointerLength = radius * 0.6f
            val pointerStart = Offset(
                center.x + cos(pointerAngle) * (radius * 0.2f),
                center.y + sin(pointerAngle) * (radius * 0.2f)
            )
            val pointerEnd = Offset(
                center.x + cos(pointerAngle) * pointerLength,
                center.y + sin(pointerAngle) * pointerLength
            )

            // Pointer glow/shadow
            drawLine(
                color = pointerColor.copy(alpha = 0.4f),
                start = pointerStart,
                end = pointerEnd,
                strokeWidth = strokeWidth * 0.6f,
                cap = StrokeCap.Round
            )

            // Pointer Core
            drawLine(
                color = pointerColor,
                start = pointerStart,
                end = pointerEnd,
                strokeWidth = strokeWidth * 0.3f,
                cap = StrokeCap.Round
            )
        }

        // Value & Label container
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = String.format("%.1f", value),
                style = Obsidian.typography.titleMedium,
                color = labelColor ?: Obsidian.colors.textPrimary
            )
            Text(
                text = label,
                style = Obsidian.typography.labelMedium,
                color = actualLabelColor,
                textAlign = TextAlign.Center,
                maxLines = 1,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}
