package com.example.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BrandBackground
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandSurface
import com.example.ui.theme.BrandSurfaceVariant
import com.example.ui.theme.TextSecondary
import androidx.compose.ui.platform.testTag

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoadingScreen(
    message: String = "جاري تحميل البيانات...",
    details: String = "يرجى الانتظار",
    progress: Float? = null,
    modifier: Modifier = Modifier
) {
    val isLight = BrandBackground.luminance() > 0.5f
    val titleColor = if (isLight) Color(0xFF111111) else Color(0xFFFFFFFF)
    val tintColor = if (isLight) Color(0xFF018786) else BrandPrimary

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandBackground)
            .testTag("loading_screen")
            .then(modifier),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            Box(
                modifier = Modifier.size(110.dp),
                contentAlignment = Alignment.Center
            ) {
                val rotation by rememberInfiniteTransition(label = "loader_rotation")
                    .animateFloat(
                        initialValue = 0f,
                        targetValue = 360f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(2200, easing = LinearEasing),
                            repeatMode = RepeatMode.Restart
                        ),
                        label = "rotation"
                    )

                CircularProgressIndicator(
                    progress = { progress ?: 0.7f },
                    modifier = Modifier
                        .fillMaxSize(),
                    strokeWidth = 5.dp,
                    trackColor = if (isLight) Color(0xFFE5E7EB) else Color(0xFF27272A),
                    color = tintColor,
                    strokeCap = StrokeCap.Round
                )

                Text(
                    text = "${((progress ?: 0.7f) * 100).toInt()}%",
                    color = titleColor,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = message,
                color = titleColor,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Text(
                text = details,
                color = TextSecondary,
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )

            LinearProgressIndicator(
                progress = progress ?: 0.7f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = tintColor,
                trackColor = if (isLight) Color(0xFFE5E7EB) else Color(0xFF27272A)
            )

            if (progress == null) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    LoadingDot(delay = 0)
                    LoadingDot(delay = 140)
                    LoadingDot(delay = 280)
                }
            }
        }
    }
}

@Composable
private fun LoadingDot(delay: Int) {
    val alpha by rememberInfiniteTransition(label = "dot_$delay")
        .animateFloat(
            initialValue = 0.25f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(1100, easing = androidx.compose.animation.core.EaseInOutCubic),
                repeatMode = RepeatMode.Reverse,
                initialDelayMillis = delay
            ),
            label = "alpha_$delay"
        )

    Box(
        modifier = Modifier
            .size(10.dp)
            .background(
                color = if (BrandBackground.luminance() > 0.5f) Color(0xFF018786) else BrandPrimary,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    )
}

@Composable
fun LoadingScreenWithProgress(
    progress: Float = 0.5f,
    message: String = "جاري التحميل...",
    detailedMessage: String = "50%"
) {
    LoadingScreen(
        message = message,
        details = "$detailedMessage • يُستكمل تلقائياً",
        progress = progress
    )
}
