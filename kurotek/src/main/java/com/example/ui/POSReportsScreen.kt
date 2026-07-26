package com.example.ui

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

@Composable
fun POSReportsScreen(
    onBackClick: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(0) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A1A))
            .testTag("pos_reports_screen")
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF222222))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "تقارير نقاط البيع",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                modifier = Modifier.fillMaxWidth(),
                containerColor = Color(0xFF222222),
                divider = {}
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            text = "اليوم",
                            color = if (selectedTab == 0) Color(0xFF1A9B8E) else Color(0xFF808080),
                            fontSize = 13.sp,
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    unselectedContentColor = Color(0xFF808080)
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            text = "هذا الأسبوع",
                            color = if (selectedTab == 1) Color(0xFF1A9B8E) else Color(0xFF808080),
                            fontSize = 13.sp,
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    unselectedContentColor = Color(0xFF808080)
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = {
                        Text(
                            text = "هذا الشهر",
                            color = if (selectedTab == 2) Color(0xFF1A9B8E) else Color(0xFF808080),
                            fontSize = 13.sp,
                            fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    unselectedContentColor = Color(0xFF808080)
                )
            }

            // Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 80.dp)
            ) {
                when (selectedTab) {
                    0 -> TodayReportsContent()
                    1 -> WeeklyReportsContent()
                    2 -> MonthlyReportsContent()
                }
            }
        }

        // Bottom Navigation
        POSReportsBottomNav(
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun TodayReportsContent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Summary Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            shape = RoundedCornerShape(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
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
                        text = "إجمالي مبيعات اليوم",
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
                                text = "0 عملية",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 11.sp
                            )
                            Text(
                                text = "0 نقطة",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 11.sp
                            )
                        }
                    )
                }
            }
        }

        // Transactions List
        Text(
            text = "آخر العمليات",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        repeat(3) {
            TransactionItem(
                date = "26 أكتوبر 2021",
                time = "10:${30 + it}",
                amount = "0 ريال",
                type = "بيع",
                status = "نجح"
            )
        }
    }
}

@Composable
private fun WeeklyReportsContent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "إحصائيات الأسبوع",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        repeat(4) { day ->
            DayStatItem(
                day = when (day) {
                    0 -> "الأحد"
                    1 -> "الإثنين"
                    2 -> "الثلاثاء"
                    else -> "الأربعاء"
                },
                amount = "0 ريال",
                transactions = "0"
            )
        }
    }
}

@Composable
private fun MonthlyReportsContent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "إحصائيات الشهر",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        repeat(4) { week ->
            WeekStatItem(
                week = "الأسبوع ${week + 1}",
                amount = "0 ريال",
                percentage = when (week) {
                    0 -> 20f
                    1 -> 50f
                    2 -> 75f
                    else -> 45f
                }
            )
        }
    }
}

@Composable
private fun TransactionItem(
    date: String,
    time: String,
    amount: String,
    type: String,
    status: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A2A)),
        border = BorderStroke(1.dp, Color(0xFF333333)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = date,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = time,
                        color = Color(0xFF808080),
                        fontSize = 11.sp
                    )
                }
                Text(
                    text = type,
                    color = Color(0xFF1A9B8E),
                    fontSize = 11.sp
                )
            }
            
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = amount,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Badge(
                    text = status,
                    backgroundColor = Color(0xFF1A9B8E)
                )
            }
        }
    }
}

@Composable
private fun DayStatItem(
    day: String,
    amount: String,
    transactions: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A2A)),
        border = BorderStroke(1.dp, Color(0xFF333333)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = day,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = amount,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$transactions عملية",
                    color = Color(0xFF808080),
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
private fun WeekStatItem(
    week: String,
    amount: String,
    percentage: Float
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A2A)),
        border = BorderStroke(1.dp, Color(0xFF333333)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = week,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = amount,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            LinearProgressIndicator(
                progress = { percentage / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = Color(0xFF1A9B8E),
                trackColor = Color(0xFF333333),
                shape = RoundedCornerShape(3.dp)
            )
            
            Text(
                text = "${percentage.toInt()}% من إجمالي الشهر",
                color = Color(0xFF808080),
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun Badge(
    text: String,
    backgroundColor: Color
) {
    Card(
        modifier = Modifier.height(20.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor.copy(alpha = 0.2f)),
        border = BorderStroke(1.dp, backgroundColor.copy(alpha = 0.4f)),
        shape = RoundedCornerShape(6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = backgroundColor,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun POSReportsBottomNav(
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
            NavItem("التقارير", Icons.Outlined.Assessment, isSelected = true)
            NavItem("الرئيسية", Icons.Outlined.Home)
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
