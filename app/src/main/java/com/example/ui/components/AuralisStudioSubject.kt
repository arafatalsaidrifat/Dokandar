package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import com.example.ui.theme.InkPrimary
import com.example.ui.theme.SignalAccent
import com.example.ui.theme.SignalAccentLift

/**
 * Studio cut-out optical metrology sensor head & telecentric assembly.
 * Engineered with a machined satin anodized aluminum & titanium chassis (#DCDFE6 to #848894)
 * so the near-black (#0D0D0F) back BAZARLY wordmark layer (zIndex 1) and front 'L' letter (zIndex 3)
 * interlock with high-contrast studio depth.
 */
@Composable
fun AuralisStudioSubject(
    modifier: Modifier = Modifier,
    rotationDegrees: Float = 0f
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Ground contact drop shadow: 0 26px 34px rgba(13,13,15,.20)
        drawOval(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0x360D0D0F),
                    Color(0x140D0D0F),
                    Color.Transparent
                ),
                center = Offset(w * 0.54f, h * 0.88f),
                radius = w * 0.45f
            ),
            topLeft = Offset(w * 0.08f, h * 0.75f),
            size = Size(w * 0.88f, h * 0.22f)
        )

        rotate(degrees = rotationDegrees, pivot = Offset(w * 0.52f, h * 0.50f)) {
            drawMachinedSatinMetrologyHead(w, h)
        }
    }
}

private fun DrawScope.drawMachinedSatinMetrologyHead(w: Float, h: Float) {
    // 1. Machined Satin Aluminum Lower Dovetail Sled (contrasts sharply against #0D0D0F wordmark)
    val baseLeft = w * 0.18f
    val baseTop = h * 0.58f
    val baseWidth = w * 0.72f
    val baseHeight = h * 0.24f

    drawRoundRect(
        brush = Brush.linearGradient(
            colors = listOf(
                Color(0xFFD6D9E0),
                Color(0xFFA2A6B2),
                Color(0xFFC8CBD4),
                Color(0xFF7D818C)
            ),
            start = Offset(baseLeft, baseTop),
            end = Offset(baseLeft + baseWidth, baseTop + baseHeight)
        ),
        topLeft = Offset(baseLeft, baseTop),
        size = Size(baseWidth, baseHeight),
        cornerRadius = CornerRadius(w * 0.02f, w * 0.02f)
    )
    // Crisp specular top bevel on lower sled
    drawRoundRect(
        color = Color(0xFFF4F5F7),
        topLeft = Offset(baseLeft, baseTop),
        size = Size(baseWidth, baseHeight),
        cornerRadius = CornerRadius(w * 0.02f, w * 0.02f),
        style = Stroke(width = 2f)
    )

    // 2. Upper Anodized Titanium Optical Bridge (extends up-left so front 'L' slices over it)
    val bridgePath = Path().apply {
        addRoundRect(
            RoundRect(
                rect = Rect(
                    left = w * 0.22f,
                    top = h * 0.14f,
                    right = w * 0.80f,
                    bottom = h * 0.62f
                ),
                cornerRadius = CornerRadius(w * 0.025f, w * 0.025f)
            )
        )
    }
    drawPath(
        path = bridgePath,
        brush = Brush.linearGradient(
            colors = listOf(
                Color(0xFFE4E6EC),
                Color(0xFFB4B8C4),
                Color(0xFF8E93A0)
            ),
            start = Offset(w * 0.22f, h * 0.14f),
            end = Offset(w * 0.80f, h * 0.62f)
        )
    )
    drawPath(
        path = bridgePath,
        color = Color(0xFF5A5E68),
        style = Stroke(width = 1.5f)
    )

    // Machined cooling fins on upper left titanium bridge
    for (i in 0..5) {
        val finY = h * (0.20f + i * 0.036f)
        drawLine(
            color = Color(0xFF686C77),
            start = Offset(w * 0.26f, finY),
            end = Offset(w * 0.46f, finY),
            strokeWidth = 2.4f
        )
        drawLine(
            color = Color(0xFFF2F3F6),
            start = Offset(w * 0.26f, finY + 2f),
            end = Offset(w * 0.46f, finY + 2f),
            strokeWidth = 1.2f
        )
    }

    // 3. Large Telecentric Optical Objective Assembly (Center-Right)
    val lensCenter = Offset(w * 0.58f, h * 0.44f)
    val outerRadius = w * 0.24f

    // Outer knurled titanium barrel ring
    drawCircle(
        brush = Brush.sweepGradient(
            colors = listOf(
                Color(0xFF4A4D57),
                Color(0xFF9EA3B0),
                Color(0xFF2B2D34),
                Color(0xFF8A8F9C),
                Color(0xFF4A4D57)
            ),
            center = lensCenter
        ),
        radius = outerRadius,
        center = lensCenter
    )

    // Bright diamond-turned aluminum index ring
    drawCircle(
        color = Color(0xFFECEEF2),
        radius = outerRadius * 0.85f,
        center = lensCenter,
        style = Stroke(width = outerRadius * 0.065f)
    )

    // Deep anodized inner optical baffle
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color(0xFF0A0B0E),
                Color(0xFF1E2129),
                Color(0xFF0D0D0F)
            ),
            center = lensCenter,
            radius = outerRadius * 0.78f
        ),
        radius = outerRadius * 0.78f,
        center = lensCenter
    )

    // Multi-coated optical glass reflection
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color(0xFF2E3A50),
                Color(0xFF141924),
                Color(0xFF090A0E)
            ),
            center = Offset(lensCenter.x - outerRadius * 0.14f, lensCenter.y - outerRadius * 0.14f),
            radius = outerRadius * 0.58f
        ),
        radius = outerRadius * 0.56f,
        center = lensCenter
    )

    // Specular studio softbox highlight arc on glass
    drawArc(
        color = Color(0x66FFFFFF),
        startAngle = 205f,
        sweepAngle = 68f,
        useCenter = false,
        topLeft = Offset(lensCenter.x - outerRadius * 0.44f, lensCenter.y - outerRadius * 0.44f),
        size = Size(outerRadius * 0.88f, outerRadius * 0.88f),
        style = Stroke(width = outerRadius * 0.075f, cap = StrokeCap.Round)
    )

    // Single saturated signal accent calibration dot (#2F5BFF) & crosshair reticle
    drawCircle(
        color = SignalAccent,
        radius = outerRadius * 0.055f,
        center = lensCenter
    )
    drawCircle(
        color = SignalAccentLift.copy(alpha = 0.65f),
        radius = outerRadius * 0.24f,
        center = lensCenter,
        style = Stroke(width = 1.6f)
    )

    // 4. Secondary Coaxial Laser Triangulation Emitter (Left-Middle cantilever)
    val emitterCenter = Offset(w * 0.29f, h * 0.56f)
    val emitterRadius = w * 0.115f
    drawCircle(
        brush = Brush.linearGradient(
            colors = listOf(Color(0xFFE0E2E8), Color(0xFF7D818D)),
            start = Offset(emitterCenter.x - emitterRadius, emitterCenter.y - emitterRadius),
            end = Offset(emitterCenter.x + emitterRadius, emitterCenter.y + emitterRadius)
        ),
        radius = emitterRadius,
        center = emitterCenter
    )
    drawCircle(
        color = InkPrimary,
        radius = emitterRadius * 0.68f,
        center = emitterCenter
    )
    drawCircle(
        color = SignalAccent,
        radius = emitterRadius * 0.15f,
        center = emitterCenter
    )

    // 5. Precision machined hex fasteners on satin sled
    val boltOffsets = listOf(
        Offset(w * 0.23f, h * 0.72f),
        Offset(w * 0.46f, h * 0.72f),
        Offset(w * 0.84f, h * 0.72f),
        Offset(w * 0.75f, h * 0.20f)
    )
    boltOffsets.forEach { pt ->
        drawCircle(
            color = Color(0xFF343740),
            radius = w * 0.013f,
            center = pt
        )
    }
}
