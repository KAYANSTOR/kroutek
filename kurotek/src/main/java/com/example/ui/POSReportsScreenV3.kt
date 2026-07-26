package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun POSReportsScreenV3() {
    var selectedTab by remember { mutableStateOf(0) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Top Bar
        TopAppBar(
            title = {
                Column {
                    Text(
                        text = "تقرير نقاط البيع",
                        color = Color.Black,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Right
                    )
                    Text(
                        text = "تقرير التسوية المالية لنقاط البيع حسب الفترة",
                        color = Color.Gray,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Right
                    )
                }
            },
            navigationIcon = {
                IconButton(onClick = {}) {
                    Icon(Icons.Outlined.Menu, contentDescription = null)
                }
            },
            actions = {
                IconButton(onClick = {}) {
                    Icon(Icons.Filled.DateRange, contentDescription = null)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.White,
                titleContentColor = Color.Black
            )
        )

        // Date Filter Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("هذا الشهر", "هذا الأسبوع", "الأمس", "اليوم").forEachIndexed { index, label ->
                Button(
                    onClick = { selectedTab = index },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedTab == index) Color(0xFF1A9B8E) else Color(0xFFE8E8E8),
                        contentColor = if (selectedTab == index) Color.White else Color.Black
                    ),
                    modifier = Modifier.height(40.dp),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Content Scroll
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Summary Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SummaryCard(
                    title = "إجمالي العمولة",
                    value = "0",
                    unit = "ريـ",
                    icon = "%",
                    bgColor = Color(0xFFFFE8E8),
                    modifier = Modifier.weight(1f)
                )
                SummaryCard(
                    title = "إجمالي المبيعات",
                    value = "0",
                    unit = "ريـ",
                    icon = "🏷️",
                    bgColor = Color(0xFFE8F5F5),
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SummaryCard(
                    title = "إجمالي المسدد",
                    value = "0",
                    unit = "ريـ",
                    icon = "💵",
                    bgColor = Color(0xFFE8F0E8),
                    modifier = Modifier.weight(1f)
                )
                SummaryCard(
                    title = "المستحق الصافي",
                    value = "0",
                    unit = "ريـ",
                    icon = "💼",
                    bgColor = Color(0xFFF0E8F5),
                    modifier = Modifier.weight(1f)
                )
            }

            // Full Width Summary
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFFFAE8), shape = RoundedCornerShape(12.dp))
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFAE8))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "إجمالي الرصيد المتبقي",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Icon(
                            imageVector = Icons.Filled.DateRange,
                            contentDescription = null,
                            tint = Color(0xFFFF9800),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Text(
                        text = "4 رسالة مرفوضة",
                        color = Color(0xFFFF9800),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            // Stores Section
            Text(
                text = "حسابات نقاط البيع",
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 8.dp)
            )

            // Store Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "كيان تك",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "773303455 — نقطة بيع",
                            color = Color.Gray,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                    Icon(
                        imageVector = Icons.Filled.DateRange,
                        contentDescription = null,
                        tint = Color(0xFF1A9B8E),
                        modifier = Modifier.size(28.dp)
                    )
                }

                Divider(thickness = 1.dp, color = Color(0xFFEEEEEE))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatItem(
                        label = "إجمالي المبيعات",
                        value = "0",
                        unit = "ريـ"
                    )
                    StatItem(
                        label = "العمولة المكتسبة",
                        value = "0",
                        unit = "ريـ"
                    )
                    StatItem(
                        label = "المسدد",
                        value = "0",
                        unit = "ريـ"
                    )
                    StatItem(
                        label = "المتبقي",
                        value = "0",
                        unit = "ريـ"
                    )
                }
            }
        }
    }
}

@Composable
fun SummaryCard(
    title: String,
    value: String,
    unit: String,
    icon: String,
    bgColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .background(bgColor, shape = RoundedCornerShape(12.dp))
            .height(100.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = title,
                color = Color.Black,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = icon,
                    fontSize = 20.sp
                )
                Text(
                    text = "$value",
                    color = Color(0xFF1A9B8E),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = unit,
                color = Color.Gray,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
fun StatItem(
    label: String,
    value: String,
    unit: String
) {
    Column(
        modifier = Modifier
            .weight(1f)
            .background(Color(0xFFF5F5F5), shape = RoundedCornerShape(8.dp))
            .padding(8.dp),
        horizontalAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = Color.Gray,
            fontSize = 10.sp,
            textAlign = TextAlign.Center
        )
        Text(
            text = value,
            color = Color(0xFF1A9B8E),
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            modifier = Modifier.padding(top = 4.dp)
        )
        Text(
            text = unit,
            color = Color.Gray,
            fontSize = 9.sp
        )
    }
}
