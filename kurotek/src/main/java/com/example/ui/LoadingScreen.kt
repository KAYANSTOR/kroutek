package com.example.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.scale

@Composable
fun LoadingScreen(
    message: String = "جاري تفعيل الترخيص..."
) {
    val infiniteTransition = rememberInfiniteTransition(label = "loading_transition")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale_animation"
    )

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation_animation"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF09090B)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Animated Loading Circle with Gradient
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .scale(scale),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF1A9B8E),
                                    Color(0xFFE85E97),
                                    Color(0xFFC2185B)
                                )
                            ),
                            shape = CircleShape
                        )
                )

                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .background(Color(0xFF09090B), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(80.dp),
                        color = Color(0xFF1A9B8E),
                        strokeWidth = 4.dp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Loading Message
            Text(
                text = message,
                color = Color(0xFFFAFAFA),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            // Status Description
            Text(
                text = "يرجى الانتظار قليلاً... قد يستغرق التفعيل بضع ثوان",
                color = Color(0xFFA1A1AA),
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Progress Dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                repeat(3) { index ->
                    val infiniteTransition2 = rememberInfiniteTransition(label = "dot_transition_$index")
                    val alpha by infiniteTransition2.animateFloat(
                        initialValue = 0.3f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(
                                durationMillis = 1000,
                                delayMillis = index * 200
                            ),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "dot_alpha_$index"
                    )

                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                Color(0xFF1A9B8E).copy(alpha = alpha),
                                CircleShape
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Tips Card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF18181B)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "ملاحظات مهمة:",
                        color = Color(0xFFE11D48),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "• لا تغلق التطبيق أثناء التفعيل\n• تأكد من اتصالك بالإنترنت\n• احتفظ بقوة البطارية كافية",
                        color = Color(0xFF71717A),
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun LicenseActivationLoadingScreen() {
    LoadingScreen(message = "جاري تفعيل الترخيص...")
}

@Composable
fun NetworkSyncLoadingScreen() {
    LoadingScreen(message = "جاري مزامنة البيانات...")
}

@Composable
fun DataExportLoadingScreen() {
    LoadingScreen(message = "جاري تصدير البيانات...")
}
