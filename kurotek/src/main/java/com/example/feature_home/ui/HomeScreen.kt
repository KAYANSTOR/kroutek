package com.example.feature_home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DeepBlack

@Composable
fun HomeScreen(
    onNavigateToCards: () -> Unit = {},
    onNavigateToAccounts: () -> Unit = {},
    onNavigateToOffers: () -> Unit = {},
    onNavigateToReports: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A1A))
            .testTag("home_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Status Bar Section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "9:41",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.SignalCellularAlt,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = null,
                        tint = Color(0xFF1A9B8E),
                        modifier = Modifier.size(20.dp)
                    )
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier
                            .size(20.dp)
                            .clickable { onNavigateToSettings() }
                    )
                }
            }

            // Header with Network Name
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "شبكة كيان تك",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = "صباح الخير — الاثنين 26 أكتوبر 2021",
                    color = Color(0xFFB0B0B0),
                    fontSize = 12.sp
                )
            }

            // Status Badges
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Badge(
                    text = "الرسائل المرسلة: 100",
                    backgroundColor = Color(0xFFFF6B6B),
                    textColor = Color.White,
                    modifier = Modifier.weight(1f)
                )
                Badge(
                    text = "نشطة ✓",
                    backgroundColor = Color(0xFF1A9B8E),
                    textColor = Color.White,
                    modifier = Modifier.weight(1f)
                )
            }

            // Balance Card with Gradient
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF1A9B8E),
                                    Color(0xFFE85E97),
                                    Color(0xFFC2185B)
                                )
                            ),
                            RoundedCornerShape(16.dp)
                        )
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "إجمالي رصيد العملات (المعلق)",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 12.sp
                        )
                        
                        Text(
                            text = "0 ريال",
                            color = Color.White,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            content = {
                                Text(
                                    text = "0 حسابات",
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = "0 كروت مفعولة",
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 11.sp
                                )
                            }
                        )
                    }
                }
            }

            // Main Action Cards Grid
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Row 1
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HomeCard(
                        title = "مبيعات اليوم",
                        icon = Icons.Outlined.CalendarToday,
                        value = "0 ريال",
                        subtitle = "0 كرت",
                        modifier = Modifier.weight(1f)
                    )
                    HomeCard(
                        title = "مبيعات الشهر",
                        icon = Icons.Outlined.DateRange,
                        value = "0 ريال",
                        subtitle = "0 كرت",
                        modifier = Modifier.weight(1f)
                    )
                }

                // Row 2
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HomeCard(
                        title = "حسابات نقاط البيع",
                        icon = Icons.Outlined.Store,
                        value = "0",
                        subtitle = "حساب",
                        modifier = Modifier.weight(1f)
                    )
                    HomeCard(
                        title = "بيع مباشر - بدوي",
                        icon = Icons.Outlined.Add,
                        value = "جديد",
                        subtitle = "+",
                        modifier = Modifier.weight(1f)
                    )
                }

                // Row 3
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HomeCard(
                        title = "إدارة ملفات الاستيراد",
                        icon = Icons.Outlined.InsertDriveFile,
                        value = "0",
                        subtitle = "ملف",
                        modifier = Modifier.weight(1f)
                    )
                    HomeCard(
                        title = "الأرقام المحظورة",
                        icon = Icons.Outlined.Shield,
                        value = "0",
                        subtitle = "رقم",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Recent Transactions Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(
                                    Color(0xFFFF6B6B),
                                    RoundedCornerShape(4.dp)
                                )
                        )
                        Text(
                            text = "آخر العمليات",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "عرض الكل →",
                        color = Color(0xFF1A9B8E),
                        fontSize = 12.sp
                    )
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A2A)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "لا توجد عمليات حديثة",
                            color = Color(0xFF808080),
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }

        // Bottom Navigation
        BottomNavigation(
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun Badge(
    text: String,
    backgroundColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(32.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor.copy(alpha = 0.15f)),
        border = BorderStroke(1.dp, backgroundColor.copy(alpha = 0.3f)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = textColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun HomeCard(
    title: String,
    icon: androidx.compose.material.icons.materialIcon?,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(120.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A2A)),
        border = BorderStroke(1.dp, Color(0xFF333333)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon ?: Icons.Outlined.MoreVert,
                contentDescription = null,
                tint = Color(0xFF1A9B8E),
                modifier = Modifier.size(28.dp)
            )
            
            Text(
                text = title,
                color = Color(0xFFB0B0B0),
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                lineHeight = 13.sp
            )
            
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = value,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    color = Color(0xFF808080),
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
private fun BottomNavigation(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF222222)),
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        border = BorderStroke(1.dp, Color(0xFF333333))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavItem("الكروت", Icons.Outlined.CreditCard)
            NavItem("الحسابات", Icons.Outlined.AccountBox)
            NavItem("العروض", Icons.Outlined.LocalOffer)
            NavItem("التقارير", Icons.Outlined.Assessment)
            NavItem("الرئيسية", Icons.Outlined.Home, isSelected = true)
        }
    }
}

@Composable
private fun NavItem(
    label: String,
    icon: androidx.compose.material.icons.materialIcon,
    isSelected: Boolean = false
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) Color(0xFF1A9B8E) else Color(0xFF808080),
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = label,
            color = if (isSelected) Color(0xFF1A9B8E) else Color(0xFF808080),
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}
