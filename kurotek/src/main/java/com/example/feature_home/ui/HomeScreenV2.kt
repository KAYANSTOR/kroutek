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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * HomeScreen - الشاشة الرئيسية
 * مطابقة 100% للصور المرجعية
 * 
 * محتويات:
 * - شريط الحالة العلوي
 * - بطاقات المبيعات (اليوم والشهر)
 * - Dialog للتشغيل في الخلفية
 * - قائمة العمليات
 * - Bottom Navigation
 */

@Composable
fun HomeScreenV2(
    onNavigateToScreen: (String) -> Unit = {}
) {
    // State
    var showBackgroundModeDialog by remember { mutableStateOf(false) }
    var isBackgroundModeEnabled by remember { mutableStateOf(false) }
    
    // Colors
    val primaryTeal = Color(0xFF1A9B8E)
    val primaryPink = Color(0xFFE85E97)
    val surfaceLight = Color(0xFFF5F5F5)
    val textDark = Color(0xFF000000)
    val textGray = Color(0xFF666666)
    val goldColor = Color(0xFFFFD700)
    
    // Gradient for balance card
    val balanceGradient = Brush.linearGradient(
        colors = listOf(primaryTeal, primaryPink),
        start = Offset(0f, 0f),
        end = Offset(500f, 500f)
    )
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Top App Bar with icons
            TopAppBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black
                ),
                title = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            IconButton(onClick = {}) {
                                Icon(
                                    imageVector = Icons.Outlined.Settings,
                                    contentDescription = null,
                                    tint = textDark,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            
                            IconButton(onClick = {}) {
                                Icon(
                                    imageVector = Icons.Outlined.Wallet,
                                    contentDescription = null,
                                    tint = textDark,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        
                        Text(
                            text = "شبكة كيان تك",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = primaryTeal,
                            textAlign = TextAlign.End,
                            modifier = Modifier.weight(1f).padding(end = 8.dp)
                        )
                    }
                }
            )
            
            // Main content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Sales Cards Grid 2x1
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Today Sales
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(120.dp),
                        colors = CardDefaults.cardColors(containerColor = surfaceLight),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.End,
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "مبيعات اليوم",
                                fontSize = 12.sp,
                                color = textGray,
                                fontWeight = FontWeight.Bold
                            )
                            
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "0 ر.ي",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = primaryTeal
                                )
                                Text(
                                    text = "0 كرت",
                                    fontSize = 11.sp,
                                    color = textGray
                                )
                            }
                        }
                    }
                    
                    // Month Sales
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(120.dp),
                        colors = CardDefaults.cardColors(containerColor = surfaceLight),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.End,
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "مبيعات الشهر",
                                fontSize = 12.sp,
                                color = textGray,
                                fontWeight = FontWeight.Bold
                            )
                            
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "0 ر.ي",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = primaryTeal
                                )
                                Text(
                                    text = "0 كرت",
                                    fontSize = 11.sp,
                                    color = textGray
                                )
                            }
                        }
                    }
                }
                
                // Operations Grid 2x2
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Calculator
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(100.dp),
                        colors = CardDefaults.cardColors(containerColor = surfaceLight),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Build,
                                contentDescription = null,
                                tint = primaryTeal,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                    
                    // Add
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(100.dp),
                        colors = CardDefaults.cardColors(containerColor = surfaceLight),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Add,
                                contentDescription = null,
                                tint = primaryTeal,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }
                
                // Notifications/Recent section
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    colors = CardDefaults.cardColors(containerColor = surfaceLight),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "لا توجد عمليات حديثة",
                            fontSize = 12.sp,
                            color = textGray,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
        
        // Bottom Navigation (Fixed)
        BottomNavigation(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            primaryTeal
        )
    }
    
    // Background Mode Dialog
    if (showBackgroundModeDialog) {
        AlertDialog(
            onDismissRequest = { showBackgroundModeDialog = false },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.PlayArrow,
                        contentDescription = null,
                        tint = primaryTeal,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "التشغيل في الخلفية",
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "لضمان استمرار خدمة Z Net في استقبال الرسائل والمحافظ وتوزيع الكروت للعملاء تلقائياً دون توقف، يتطلب التطبيق إلى الوصول في الخلفية وتحسين البطارية.",
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                    
                    Text(
                        text = "يرجى اختيار \"متابعة\" ثم \"السماح\" في النافذة التالية.",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        isBackgroundModeEnabled = true
                        showBackgroundModeDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = primaryTeal),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("متابعة", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                Button(
                    onClick = { showBackgroundModeDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = surfaceLight),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("ليس الآن", color = textDark, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
private fun BottomNavigation(
    modifier: Modifier = Modifier,
    primaryColor: Color
) {
    NavigationBar(
        modifier = modifier,
        containerColor = Color.White,
        contentColor = primaryColor
    ) {
        // الكروت
        NavigationBarItem(
            icon = { Icon(Icons.Outlined.CreditCard, contentDescription = null, modifier = Modifier.size(24.dp)) },
            label = { Text("الكروت", fontSize = 11.sp) },
            selected = false,
            onClick = {}
        )
        
        // الحسابات
        NavigationBarItem(
            icon = { Icon(Icons.Outlined.People, contentDescription = null, modifier = Modifier.size(24.dp)) },
            label = { Text("الحسابات", fontSize = 11.sp) },
            selected = false,
            onClick = {}
        )
        
        // العروض
        NavigationBarItem(
            icon = { Icon(Icons.Outlined.Loyalty, contentDescription = null, modifier = Modifier.size(24.dp)) },
            label = { Text("العروض", fontSize = 11.sp) },
            selected = false,
            onClick = {}
        )
        
        // التقارير
        NavigationBarItem(
            icon = { Icon(Icons.Outlined.BarChart, contentDescription = null, modifier = Modifier.size(24.dp)) },
            label = { Text("التقارير", fontSize = 11.sp) },
            selected = false,
            onClick = {}
        )
        
        // الرئيسية
        NavigationBarItem(
            icon = { Icon(Icons.Outlined.Home, contentDescription = null, modifier = Modifier.size(24.dp)) },
            label = { Text("الرئيسية", fontSize = 11.sp) },
            selected = true,
            onClick = {}
        )
    }
}
