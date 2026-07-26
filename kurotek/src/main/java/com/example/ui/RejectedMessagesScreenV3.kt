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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RejectedMessagesScreenV3() {
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
                        text = "الرسائل المرفوضة",
                        color = Color.Black,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Right
                    )
                    Text(
                        text = "مراجعة وتحليل الرسائل المرفوضة",
                        color = Color.Gray,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Right
                    )
                }
            },
            navigationIcon = {
                IconButton(onClick = {}) {
                    Icon(Icons.Outlined.MoreVert, contentDescription = null)
                }
            },
            actions = {
                IconButton(onClick = {}) {
                    Icon(Icons.Outlined.FilterList, contentDescription = null)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.White,
                titleContentColor = Color.Black
            )
        )

        // Tab Selection
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp)
        ) {
            listOf("لا يوجد مخزون كروت", "فشل إرسال الكود للعميل").forEachIndexed { index, label ->
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = label,
                        color = if (selectedTab == index) Color(0xFF1A9B8E) else Color.Gray,
                        fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        maxLines = 1
                    )
                    if (selectedTab == index) {
                        Divider(
                            modifier = Modifier
                                .height(3.dp)
                                .fillMaxWidth(0.9f)
                                .background(Color(0xFF1A9B8E))
                        )
                    }
                }
            }
        }

        // Summary Info
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(Color(0xFFFFF8DC), shape = RoundedCornerShape(12.dp)),
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFFFF8DC)
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
                        text = if (selectedTab == 0) 
                            "إجمالي الرسائل المرفوضة\n4 رسالة مرفوضة"
                        else
                            "إجمالي الرسائل المرفوضة\n4 رسالة مرفوضة",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Right,
                        lineHeight = 18.sp
                    )
                }
                Icon(
                    imageVector = Icons.Outlined.Warning,
                    contentDescription = null,
                    tint = Color(0xFFFF9800),
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        // Content List
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            RejectedMessageCard(
                reason = if (selectedTab == 0) 
                    "لا يوجد مخزون كروت كافٍ ببغطي الرصيد المتاح (200 ريـ)"
                else
                    "صيغة الرسالة لا تطابق أي قالب نشط للمحفظة\nأحمد المنتصر",
                details = listOf(
                    "$ 100",
                    "773086403",
                    "773086403",
                    "ص 1:1٧"
                ),
                time = "ص 1:1٧"
            )

            RejectedMessageCard(
                reason = "صيغة الرسالة لا تطابق أي قالب نشط للمحفظة\nأحمد المنتصر",
                details = listOf(
                    "ص 1:1٧",
                    "773086403"
                ),
                time = "ص 1:1٠"
            )

            RejectedMessageCard(
                reason = "صيغة الرسالة لا تطابق أي قالب نشط للمحفظة\nأحمد المنتصر",
                details = listOf(
                    "ص 1:1٠",
                    "777989192"
                ),
                time = "Jaib"
            )

            RejectedMessageCard(
                reason = "المبيغ المستقلم (15000 ريـ) لا يطابق فئات الكروت المتوفرة",
                details = listOf(
                    "ص 1:1:0",
                    "$ 15000"
                ),
                time = "777989192"
            )
        }
    }
}

@Composable
fun RejectedMessageCard(
    reason: String,
    details: List<String>,
    time: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, shape = RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(
            2.dp,
            when {
                reason.contains("لا يوجد مخزون") -> Color(0xFFFF6B6B)
                else -> Color(0xFFFF6B6B)
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Reason with icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Outlined.Error,
                    contentDescription = null,
                    tint = Color(0xFFFF6B6B),
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = reason,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    textAlign = TextAlign.Right,
                    modifier = Modifier.weight(1f)
                )
            }

            Divider(thickness = 1.dp, color = Color(0xFFEEEEEE))

            // Details row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                details.forEach { detail ->
                    Surface(
                        modifier = Modifier
                            .background(Color(0xFFE8E8E8), shape = RoundedCornerShape(6.dp)),
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFE8E8E8)
                    ) {
                        Text(
                            text = detail,
                            color = Color(0xFF1A9B8E),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Time
            Text(
                text = time,
                color = Color.Gray,
                fontSize = 11.sp,
                textAlign = TextAlign.Right,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
