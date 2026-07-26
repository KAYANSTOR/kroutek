package com.example.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.scale

@Composable
fun LicenseSuccessScreen(
    networkName: String = "شبكتك",
    onContinue: () -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "success_animation")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Color(0xFF09090B)
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Success Icon with animation
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .scale(scale),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .background(Color(0xFF10B981).copy(alpha = 0.15f), CircleShape)
                        .border(BorderStroke(3.dp, Color(0xFF10B981)), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = "نجح",
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(80.dp)
                    )
                }
            }

            // Success Title
            Text(
                text = "تم التفعيل بنجاح",
                color = Color(0xFFFAFAFA),
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )

            // Success Message
            Text(
                text = "مرحباً بك في $networkName!\nتم تفعيل تطبيق Z Net بنجاح على جهازك.",
                color = Color(0xFFA1A1AA),
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )

            // Details Card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF18181B)),
                border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.2f)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Detail Item
                    DetailItem(
                        label = "اسم الشبكة",
                        value = networkName,
                        icon = "🌐"
                    )

                    DetailItem(
                        label = "حالة التفعيل",
                        value = "مفعّل ومجاز",
                        icon = "✓"
                    )

                    DetailItem(
                        label = "فترة الاشتراك",
                        value = "نشط حتى 31/12/2025",
                        icon = "📅"
                    )

                    DetailItem(
                        label = "نوع الخدمة",
                        value = "كامل المميزات",
                        icon = "⭐"
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Features List
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A9B8E).copy(alpha = 0.1f)),
                border = BorderStroke(1.dp, Color(0xFF1A9B8E).copy(alpha = 0.2f)),
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
                        text = "المميزات المفعلة:",
                        color = Color(0xFF1A9B8E),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )

                    listOf(
                        "توليد كروت الشحن",
                        "إدارة المبيعات والأرباح",
                        "تقارير شاملة",
                        "مزامنة SMS",
                        "الدعم الفني 24/7"
                    ).forEach { feature ->
                        Text(
                            text = "✓ $feature",
                            color = Color(0xFF71717A),
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Continue Button
            Button(
                onClick = onContinue,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A9B8E)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    text = "ابدأ الآن",
                    color = Color(0xFFFAFAFA),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun DetailItem(
    label: String,
    value: String,
    icon: String = "•"
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = value,
            color = Color(0xFF10B981),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                color = Color(0xFF71717A),
                fontSize = 12.sp
            )
            Text(
                text = icon,
                fontSize = 14.sp
            )
        }
    }
}
