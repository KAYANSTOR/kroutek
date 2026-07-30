package com.example.feature_home.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.Store
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BrandBackground
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandSecondary
import com.example.ui.theme.BrandSurface
import com.example.ui.theme.BrandSurfaceVariant
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.isDarkThemeState
import androidx.compose.ui.platform.testTag

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToCards: () -> Unit = {},
    onNavigateToAccounts: () -> Unit = {},
    onNavigateToOffers: () -> Unit = {},
    onNavigateToReports: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    networkName: String = "شبكة كيان تك",
    subscriptionExpiry: String = "الاشتراك حتى 24 أكتوبر 2026",
    remainingMessages: Int = 100,
    salesToday: String = "0 ر.ي",
    salesTodayCards: String = "0 كرت",
    salesMonth: String = "0 ر.ي",
    salesMonthCards: String = "0 كرت",
    accountsCount: Int = 0,
    activeCardsCount: Int = 0
) {
    val isLight = BrandBackground.luminance() > 0.5f
    val titleColor = if (isLight) Color(0xFF111111) else Color(0xFFFFFFFF)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandBackground)
            .testTag("home_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // App Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "9:41",
                    color = titleColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = null,
                        tint = if (isLight) Color(0xFF1A9B8E) else titleColor,
                        modifier = Modifier.size(20.dp)
                    )
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = null,
                            tint = titleColor
                        )
                    }
                }
            }

            // Network header
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = networkName,
                    color = titleColor,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "الاشتراك حتى 2026/10/24",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            }

            // Subscription + status row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedCard(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.outlinedCardColors(containerColor = BrandSurface)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "الرسائل المتبقية",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                        Text(
                            text = remainingMessages.toString(),
                            color = titleColor,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = subscriptionExpiry,
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                OutlinedCard(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.outlinedCardColors(containerColor = BrandSurface)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(
                                        color = if (isLight) Color(0xFF10B981) else Color(0xFF34D399),
                                        shape = CircleShape
                                    )
                            )
                            Text(
                                text = "النظام يعمل بشكل سليم",
                                color = titleColor,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Text(
                            text = "نشط",
                            color = titleColor,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        LinearProgressIndicator(
                            progress = 0.72f,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp),
                            color = if (isLight) Color(0xFF03DAC5) else Color(0xFF14B8A6),
                            trackColor = if (isLight) Color(0xFFE5E7EB) else Color(0xFF27272A)
                        )
                    }
                }
            }

            // Sales cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedCard(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.outlinedCardColors(containerColor = BrandSurface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "مبيعات اليوم",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                            Text(
                                text = salesToday,
                                color = titleColor,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = salesTodayCards,
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                        Icon(
                            imageVector = Icons.Outlined.CalendarToday,
                            contentDescription = null,
                            tint = if (isLight) Color(0xFF03DAC5) else Color(0xFF14B8A6)
                        )
                    }
                }

                OutlinedCard(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.outlinedCardColors(containerColor = BrandSurface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "مبيعات الشهر",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                            Text(
                                text = salesMonth,
                                color = titleColor,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = salesMonthCards,
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                        Icon(
                            imageVector = Icons.Outlined.DateRange,
                            contentDescription = null,
                            tint = if (isLight) Color(0xFFBB86FC) else Color(0xFFC084FC)
                        )
                    }
                }
            }

            // Quick actions grid
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickActionCard(
                        title = "حسابات نقاط البيع",
                        value = "0",
                        subtitle = "حساب",
                        icon = Icons.Outlined.Store,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToAccounts,
                        isLight = isLight
                    )
                    QuickActionCard(
                        title = "البيع المباشر",
                        value = "بدء",
                        subtitle = "عملية جديدة",
                        icon = Icons.Outlined.CalendarToday,
                        modifier = Modifier.weight(1f),
                        onClick = {},
                        isLight = isLight
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickActionCard(
                        title = "إدارة الملفات",
                        value = "0",
                        subtitle = "ملف",
                        icon = Icons.Outlined.DateRange,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToCards,
                        isLight = isLight
                    )
                    QuickActionCard(
                        title = "الأرقام المحظورة",
                        value = "0",
                        subtitle = "رقم",
                        icon = Icons.Outlined.Shield,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToOffers,
                        isLight = isLight
                    )
                }
            }

            // Recent operations
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(
                                    color = if (isLight) Color(0xFFEF4444) else Color(0xFFFF6B6B),
                                    shape = CircleShape
                                )
                        )
                        Text(
                            text = "آخر العمليات",
                            color = titleColor,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    TextButton(onClick = onNavigateToReports) {
                        Text(
                            text = "عرض الكل →",
                            color = if (isLight) Color(0xFF03DAC5) else Color(0xFF14B8A6),
                            fontSize = 13.sp
                        )
                    }
                }

                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = BrandSurface)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "لا توجد عمليات حديثة",
                            color = TextSecondary,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickActionCard(
    title: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.material.icons.Icons.Outlined?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    isLight: Boolean
) {
    OutlinedCard(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.outlinedCardColors(containerColor = BrandSurface),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(
                        color = if (isLight) Color(0xFFF5F5F5) else Color(0xFF1E1E21),
                        shape = RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon ?: Icons.Outlined.MoreVert,
                    contentDescription = null,
                    tint = if (isLight) Color(0xFF03DAC5) else Color(0xFF14B8A6),
                    modifier = Modifier.size(26.dp)
                )
            }
            Text(
                text = title,
                color = if (isLight) Color(0xFF444444) else TextSecondary,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                lineHeight = 14.sp
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = value,
                    color = if (isLight) Color(0xFF111111) else Color(0xFFFFFFFF),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }
        }
    }
}
