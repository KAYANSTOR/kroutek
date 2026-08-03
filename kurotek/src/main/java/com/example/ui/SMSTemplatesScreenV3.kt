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
fun SMSTemplatesScreenV3() {
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
                        text = "قوالب رسائل العملاء والعروض",
                        color = Color.Black,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Right
                    )
                    Text(
                        text = "تخصيص وإدارة قوالب رسائل SMS المرسلة للعملاء ومكافآت العروض",
                        color = Color.Gray,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Right
                    )
                }
            },
            navigationIcon = {
                IconButton(onClick = {}) {
                    Icon(Icons.Outlined.Close, contentDescription = null)
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
            listOf("قوالب رسائل العملاء", "قوالب رسائل العروض").forEachIndexed { index, label ->
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 12.dp),
                    horizontalAlignment = if (index == 0) Alignment.End else Alignment.Start
                ) {
                    Text(
                        text = label,
                        color = if (selectedTab == index) Color(0xFF1A9B8E) else Color.Gray,
                        fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Right
                    )
                    if (selectedTab == index) {
                        Divider(
                            modifier = Modifier
                                .height(3.dp)
                                .fillMaxWidth()
                                .background(Color(0xFF1A9B8E))
                        )
                    }
                }
            }
        }

        // Content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (selectedTab == 0) {
                // Customer Templates
                TemplateCard(
                    title = "الترحاب (عام)",
                    templateContent = "شكراً على استخدامك خدماتنا.\nكود الكرت: 1234567\nفئة: 10 ريـ",
                    status = "عام",
                    statusColor = Color(0xFF4CAF50),
                    isActive = true,
                    networkName = "شبكة كيان تك"
                )
                
                TemplateCard(
                    title = "الترحاب (حسب الشبكة)",
                    templateContent = "شكراً على استخدامك شبكة كيان تك.\nكود الكرت: 1234567\nفئة: 10 ريـ",
                    status = "نشط",
                    statusColor = Color(0xFF4CAF50),
                    isActive = true,
                    networkName = "شبكة كيان تك"
                )

                TemplateCard(
                    title = "اسم الشبكة - الكود - الفئة",
                    templateContent = "شبكة كيان تك\nكود الكرت: 1234567\nفئة: 10 ريـ",
                    status = "عام",
                    statusColor = Color(0xFFFFC107),
                    isActive = false,
                    networkName = null
                )
            } else {
                // Offer Templates
                TemplateCard(
                    title = "العروض الأسبوعية",
                    templateContent = "عروض مميزة هذا الأسبوع على جميع فئات الشحن",
                    status = "نشط",
                    statusColor = Color(0xFF4CAF50),
                    isActive = true,
                    networkName = "شبكة كيان تك"
                )
            }
        }

        // Add Button
        FloatingActionButton(
            onClick = {},
            modifier = Modifier
                .align(Alignment.End)
                .padding(16.dp),
            containerColor = Color(0xFFE85E97),
            contentColor = Color.White
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "قالب جديد",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun TemplateCard(
    title: String,
    templateContent: String,
    status: String,
    statusColor: Color,
    isActive: Boolean,
    networkName: String?
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, shape = RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEEEEEE))
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
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = title,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    if (networkName != null) {
                        Surface(
                            modifier = Modifier
                                .padding(top = 6.dp)
                                .background(Color(0xFFE8F5F5), shape = RoundedCornerShape(6.dp)),
                            color = Color(0xFFE8F5F5),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = networkName,
                                color = Color(0xFF1A9B8E),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                IconButton(onClick = {}, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = Icons.Outlined.MoreVert,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Template Preview
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF5F5F5), shape = RoundedCornerShape(8.dp)),
                color = Color(0xFFF5F5F5),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = templateContent,
                    color = Color.Gray,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(12.dp),
                    textAlign = TextAlign.Right
                )
            }

            // Status Badge
            Surface(
                modifier = Modifier
                    .align(Alignment.End)
                    .background(statusColor.copy(alpha = 0.15f), shape = RoundedCornerShape(6.dp)),
                color = statusColor.copy(alpha = 0.15f),
                shape = RoundedCornerShape(6.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = null,
                        tint = statusColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = status,
                        color = statusColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
