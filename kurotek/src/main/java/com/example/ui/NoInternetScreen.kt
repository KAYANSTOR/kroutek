package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.widget.Toast
import androidx.compose.foundation.BorderStroke

@Composable
fun NoInternetScreen(
    isConnected: Boolean = false,
    onRetry: () -> Unit = {}
) {
    val context = LocalContext.current

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
            // WiFi Icon with animation effect
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(Color(0xFF1A9B8E).copy(alpha = 0.15f), CircleShape)
                    .border(BorderStroke(2.dp, Color(0xFF1A9B8E)), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isConnected) Icons.Filled.Wifi else Icons.Filled.WifiOff,
                    contentDescription = "WiFi Status",
                    tint = Color(0xFF1A9B8E),
                    modifier = Modifier.size(60.dp)
                )
            }

            // Title
            Text(
                text = if (isConnected) "متصل بنجاح" else "لا يوجد اتصال إنترنت",
                color = Color(0xFFFAFAFA),
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )

            // Description
            Text(
                text = if (isConnected) 
                    "تم استعادة الاتصال بالإنترنت بنجاح. يمكنك الآن متابعة استخدام التطبيق."
                else
                    "يبدو أن الاتصال بالإنترنت مقطوع. تحقق من اتصالك بشبكة WiFi أو البيانات الخلوية.",
                color = Color(0xFFA1A1AA),
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            // WiFi Status Details Card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF18181B)),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "حالة الاتصال",
                            color = Color(0xFFE11D48),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(
                                    if (isConnected) Color(0xFF10B981) else Color(0xFFEF4444),
                                    CircleShape
                                )
                        )
                    }

                    Text(
                        text = if (isConnected) "متصل - 5G" else "غير متصل",
                        color = Color(0xFFFAFAFA),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = if (isConnected) "القوة: قوية جداً" else "حاول الاتصال مرة أخرى",
                        color = Color(0xFF71717A),
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Retry Button
            Button(
                onClick = {
                    onRetry()
                    Toast.makeText(context, "جاري محاولة الاتصال...", Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A9B8E)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(
                    text = "إعادة محاولة",
                    color = Color(0xFFFAFAFA),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Help Card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A9B8E).copy(alpha = 0.1f)),
                border = BorderStroke(1.dp, Color(0xFF1A9B8E).copy(alpha = 0.3f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "نصائح للاتصال",
                        color = Color(0xFF1A9B8E),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Text(
                        text = "• تأكد من تفعيل WiFi أو البيانات\n• تحقق من قوة الإشارة\n• أعد تشغيل الجهاز إذا لزم الأمر\n• تواصل مع فريق الدعم: 773303455",
                        color = Color(0xFF71717A),
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}
