package com.example.feature_settings.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Help
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsTab(
    mainViewModel: MainViewModel,
    authViewModel: com.example.ui.AuthViewModel,
    settingsViewModel: SettingsViewModel,
    distributorViewModel: com.example.ui.DistributorViewModel,
    onLogout: () -> Unit
) {
    val darkTheme by mainViewModel.isDarkTheme.collectAsState()
    SettingsScreenV2()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreenV2() {
    val primaryTeal = Color(0xFF1A9B8E)
    val surfaceLight = Color(0xFFF5F5F5)
    val textDark = Color(0xFF000000)
    val textGray = Color(0xFF666666)
    val goldColor = Color(0xFFFFD700)

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = { Text("الإعدادات", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White, titleContentColor = textDark)
            )

            Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                SettingsSection(
                    title = "بيانات وصيانة النظام",
                    items = listOf(
                        SettingsItem(icon = Icons.Outlined.Storage, title = "حجم قاعدة البيانات الحالية", subtitle = "0.10 ميجابايت", color = primaryTeal),
                        SettingsItem(icon = Icons.Outlined.Download, title = "تصدير دفتر الحسابات (CSV)", subtitle = "حفظ نسخة احتياطية من العمليات بصيغة ملف جدول البيانات", color = primaryTeal),
                        SettingsItem(icon = Icons.Outlined.Delete, title = "تنظيف السجلات وتفريغ المساحة", subtitle = "إزالة السجلات التلقائية أو تنظيف البيانات الموقوتة", color = goldColor),
                        SettingsItem(icon = Icons.Outlined.Settings, title = "تنظيف عميق للنظام", subtitle = "إعادة بناء قواعد البيانات لتحرير المساحة وتسريع الأداء", color = goldColor)
                    ),
                    primaryTeal
                )

                SettingsSection(
                    title = "المساعدة",
                    items = listOf(
                        SettingsItem(icon = Icons.Outlined.Help, title = "مركز المساعدة", subtitle = "دليل الاستخدام والأسئلة الشائعة", color = primaryTeal)
                    ),
                    primaryTeal
                )

                SettingsSection(
                    title = "عن التطبيق",
                    items = listOf(
                        SettingsItem(icon = Icons.Outlined.Info, title = "Z Net", subtitle = "الإصدار 1.0.1", color = primaryTeal),
                        SettingsItem(icon = Icons.Outlined.Person, title = "المطور", subtitle = "دروبش عبدالله\nالهاتف: 779776919", color = primaryTeal),
                        SettingsItem(icon = null, title = "الحقوق", subtitle = "© Z Net 2026 جميع الحقوق محفوظة", color = Color.Transparent)
                    ),
                    primaryTeal
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

data class SettingsItem(
    val icon: ImageVector? = null,
    val title: String,
    val subtitle: String,
    val color: Color
)

@Composable
private fun SettingsSection(title: String, items: List<SettingsItem>, primaryColor: Color) {
    val textDark = Color(0xFF000000)
    val textGray = Color(0xFF666666)
    val surfaceLight = Color(0xFFF5F5F5)

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = primaryColor, modifier = Modifier.padding(horizontal = 8.dp))

        items.forEach { item ->
            Card(
                modifier = Modifier.fillMaxWidth().clickable {}.height(if (item.subtitle.contains("\n")) 100.dp else 70.dp),
                colors = CardDefaults.cardColors(containerColor = surfaceLight),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(modifier = Modifier.fillMaxSize().padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (item.icon != null) {
                        Box(modifier = Modifier.size(50.dp).background(color = item.color.copy(alpha = 0.2f), shape = CircleShape), contentAlignment = Alignment.Center) {
                            Icon(imageVector = item.icon, contentDescription = null, tint = item.color, modifier = Modifier.size(24.dp))
                        }
                    }

                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp), horizontalAlignment = Alignment.End) {
                        Text(text = item.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = textDark, textAlign = TextAlign.Right)
                        Text(text = item.subtitle, fontSize = 11.sp, color = textGray, textAlign = TextAlign.Right, lineHeight = 15.sp)
                    }

                    Icon(imageVector = Icons.Outlined.ChevronLeft, contentDescription = null, tint = textGray, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}
