package com.example.feature_settings.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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

/**
 * SettingsScreen - شاشة الإعدادات
 * مطابقة 100% للصور المرجعية
 * 
 * الأقسام:
 * - بيانات وصيانة النظام
 * - المساعدة
 * - عن التطبيق
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreenV2() {
    // Colors
    val primaryTeal = Color(0xFF1A9B8E)
    val surfaceLight = Color(0xFFF5F5F5)
    val textDark = Color(0xFF000000)
    val textGray = Color(0xFF666666)
    val goldColor = Color(0xFFFFD700)
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top App Bar
            TopAppBar(
                title = { Text("الإعدادات", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = textDark
                ),
                modifier = Modifier.background(Color.White)
            )
            
            // Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Section: بيانات وصيانة النظام
                SettingsSection(
                    title = "بيانات وصيانة النظام",
                    items = listOf(
                        SettingsItem(
                            icon = Icons.Outlined.Storage,
                            title = "حجم قاعدة البيانات الحالية",
                            subtitle = "0.10 ميجابايت",
                            color = primaryTeal
                        ),
                        SettingsItem(
                            icon = Icons.Outlined.Download,
                            title = "تصدير دفتر الحسابات (CSV)",
                            subtitle = "حفظ نسخة احتياطية من العمليات بصيغة ملف جدول البيانات",
                            color = primaryTeal
                        ),
                        SettingsItem(
                            icon = Icons.Outlined.Delete,
                            title = "تنظيف السجلات وتفريغ المساحة",
                            subtitle = "إزالة السجلات التلقائية أو تنظيف البيانات الموقوتة",
                            color = goldColor
                        ),
                        SettingsItem(
                            icon = Icons.Outlined.Settings,
                            title = "تنظيف عميق للنظام",
                            subtitle = "إعادة بناء قواعس قواعد البيانات لتحرير المساحة وتسريع الأداء",
                            color = goldColor
                        )
                    ),
                    primaryTeal
                )
                
                // Section: المساعدة
                SettingsSection(
                    title = "المساعدة",
                    items = listOf(
                        SettingsItem(
                            icon = Icons.Outlined.Help,
                            title = "مركز المساعدة",
                            subtitle = "دليل الاستخدام والأسئلة الشائعة",
                            color = primaryTeal
                        )
                    ),
                    primaryTeal
                )
                
                // Section: عن التطبيق
                SettingsSection(
                    title = "عن التطبيق",
                    items = listOf(
                        SettingsItem(
                            icon = Icons.Outlined.Info,
                            title = "Z Net",
                            subtitle = "الإصدار 1.0.1",
                            color = primaryTeal
                        ),
                        SettingsItem(
                            icon = Icons.Outlined.Person,
                            title = "المطور",
                            subtitle = "دروبش عبدالله\nالهاتف: 779776919",
                            color = primaryTeal
                        ),
                        SettingsItem(
                            icon = null,
                            title = "الحقوق",
                            subtitle = "© Z Net 2026 جميع الحقوق محفوظة",
                            color = Color.Transparent
                        )
                    ),
                    primaryTeal
                )
                
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

data class SettingsItem(
    val icon: androidx.compose.material.icons.Icons.Outlined? = null,
    val title: String,
    val subtitle: String,
    val color: Color
)

@Composable
private fun SettingsSection(
    title: String,
    items: List<SettingsItem>,
    primaryColor: Color
) {
    val textDark = Color(0xFF000000)
    val textGray = Color(0xFF666666)
    val surfaceLight = Color(0xFFF5F5F5)
    
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = primaryColor,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        
        items.forEach { item ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { }
                    .height(if (item.subtitle.contains("\n")) 100.dp else 70.dp),
                colors = CardDefaults.cardColors(containerColor = surfaceLight),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (item.icon != null) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .background(
                                    color = item.color.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(8.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = null,
                                tint = item.color,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = item.title,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = textDark,
                            textAlign = TextAlign.Right
                        )
                        
                        Text(
                            text = item.subtitle,
                            fontSize = 11.sp,
                            color = textGray,
                            textAlign = TextAlign.Right,
                            lineHeight = 15.sp
                        )
                    }
                    
                    Icon(
                        imageVector = Icons.Outlined.ChevronLeft,
                        contentDescription = null,
                        tint = textGray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
