package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowBackIosNew
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.TealPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToLicense: () -> Unit = {},
    onNavigateToSimSettings: () -> Unit = {}
) {
    val lightBackground = Color(0xFFF5F7F9)
    val textPrimary = Color(0xFF1A1A1A)
    val textSecondary = Color(0xFF666666)
    
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { 
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                            Text("الإعدادات", color = textPrimary, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                            Text("تخصيص النظام وإدارة البيانات", color = textSecondary, fontSize = 12.sp)
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.Outlined.ArrowForward, contentDescription = "رجوع", tint = textPrimary)
                        }
                    },
                    actions = {
                        Spacer(modifier = Modifier.width(48.dp))
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = lightBackground)
                )
            },
            containerColor = lightBackground
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 32.dp, top = 8.dp)
            ) {
                item {
                    SettingsSectionTitle("النظام")
                    SettingsCard {
                        SettingsItem(
                            icon = Icons.Outlined.Dns,
                            title = "اسم الشبكة",
                            subtitle = "الاسم الحالي: kayan",
                            onClick = {}
                        )
                        Divider(color = Color(0xFFF0F0F0))
                        SettingsSwitchItem(
                            icon = Icons.Outlined.CheckCircle,
                            iconTint = Color(0xFF4CAF50),
                            iconBg = Color(0xFFE8F5E9),
                            title = "المعالجة التلقائية للرسائل",
                            subtitle = "الخدمة تعمل — يتم استقبال ومعالجة الرسائل تلقائياً",
                            initialState = true
                        )
                        Divider(color = Color(0xFFF0F0F0))
                        SettingsSwitchItem(
                            icon = Icons.Outlined.FilterAlt,
                            title = "معالجة مبالغ الفئات فقط",
                            subtitle = "عند التفعيل، سيتم فقط معالجة رسائل المحافظ التي تطابق مبالغ الفئات المعرفة في النظام",
                            initialState = false
                        )
                        Divider(color = Color(0xFFF0F0F0))
                        SettingsSwitchItem(
                            icon = Icons.Outlined.History,
                            title = "معالجة الرسائل القديمة (عند التوقف)",
                            subtitle = "تفعيل لمعالجة رسائل SMS التي وصلت أثناء إغلاق أو توقف التطبيق عند فتحه مجدداً",
                            initialState = false
                        )
                        Divider(color = Color(0xFFF0F0F0))
                        SettingsItem(
                            icon = Icons.Outlined.SimCard,
                            title = "إعدادات شرائح الاتصال",
                            subtitle = "إدارة شرائح القراءة والإرسال و Failover",
                            onClick = onNavigateToSimSettings
                        )
                    }
                }

                item {
                    SettingsSectionTitle("الترخيص")
                    SettingsCard {
                        SettingsItem(
                            icon = Icons.Outlined.WorkspacePremium,
                            title = "تجديد الاشتراك",
                            subtitle = "تجديد الترخيص أو إضافة رصيد SMS قبل انتهاء الباقة الحالية",
                            onClick = onNavigateToLicense
                        )
                    }
                }

                item {
                    SettingsSectionTitle("إدارة قيود البطارية والتشغيل في الخلفية")
                    SettingsCard {
                        SettingsItem(
                            icon = Icons.Outlined.BatterySaver,
                            title = "إعدادات البطارية",
                            subtitle = "السماح بالتشغيل في الخلفية دون قيود",
                            onClick = {}
                        )
                    }
                }
                
                item {
                    SettingsSectionTitle("إعدادات المحافظ ونقاط البيع")
                    SettingsCard {
                        SettingsItem(
                            icon = Icons.Outlined.Storefront,
                            title = "إدارة المحافظ ونقاط البيع",
                            subtitle = "إضافة وتعديل المحافظ وموردي نقاط البيع",
                            onClick = {}
                        )
                        Divider(color = Color(0xFFF0F0F0))
                        SettingsItem(
                            icon = Icons.Outlined.Science,
                            title = "محاكاة القوالب",
                            subtitle = "اختبار ومحاكاة استخراج بيانات الرسائل",
                            onClick = {}
                        )
                    }
                }
                
                item {
                    SettingsSectionTitle("إعدادات قوالب الرسائل")
                    SettingsCard {
                        SettingsItem(
                            icon = Icons.Outlined.Message,
                            title = "قوالب رسائل العملاء",
                            subtitle = "تخصيص وإدارة قوالب رسائل SMS المرسلة للعملاء",
                            onClick = {}
                        )
                    }
                }
                
                item {
                    SettingsSectionTitle("بيانات وصيانة النظام")
                    SettingsCard {
                        SettingsItem(
                            icon = Icons.Outlined.Storage,
                            title = "حجم قاعدة البيانات الحالية",
                            subtitle = "0.00 ميجابايت",
                            actionIcon = Icons.Outlined.Refresh,
                            onClick = {}
                        )
                        Divider(color = Color(0xFFF0F0F0))
                        SettingsItem(
                            icon = Icons.Outlined.Download,
                            title = "تصدير دفتر الحسابات (CSV)",
                            subtitle = "حفظ نسخة احتياطية من العمليات بصيغة ملف جدول البيانات",
                            onClick = {}
                        )
                        Divider(color = Color(0xFFF0F0F0))
                        SettingsItem(
                            icon = Icons.Outlined.CleaningServices,
                            iconTint = Color(0xFFF57C00),
                            iconBg = Color(0xFFFFF3E0),
                            title = "تنظيف السجلات وتفريغ المساحة",
                            subtitle = "إزالة السجلات التلقائية أو تصفية البيانات المؤقتة",
                            onClick = {}
                        )
                        Divider(color = Color(0xFFF0F0F0))
                        SettingsItem(
                            icon = Icons.Outlined.Speed,
                            iconTint = Color(0xFFF57C00),
                            iconBg = Color(0xFFFFF3E0),
                            title = "تنظيف عميق للنظام",
                            subtitle = "إعادة بناء فهارس قاعدة البيانات لتحرير المساحة وتسريع الأداء",
                            onClick = {}
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsSectionTitle(title: String) {
    Text(
        text = title,
        color = TealPrimary,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp, start = 8.dp, end = 8.dp)
    )
}

@Composable
fun SettingsCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(vertical = 4.dp)) {
            content()
        }
    }
}

@Composable
fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    iconTint: Color = TealPrimary,
    iconBg: Color = TealPrimary.copy(alpha = 0.1f),
    actionIcon: androidx.compose.ui.graphics.vector.ImageVector = Icons.AutoMirrored.Outlined.ArrowBackIosNew,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(40.dp).background(iconBg, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(24.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = Color(0xFF1A1A1A), fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(subtitle, color = Color(0xFF666666), fontSize = 12.sp, lineHeight = 16.sp)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Icon(actionIcon, contentDescription = null, tint = Color(0xFF999999), modifier = Modifier.size(16.dp))
    }
}

@Composable
fun SettingsSwitchItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    iconTint: Color = TealPrimary,
    iconBg: Color = TealPrimary.copy(alpha = 0.1f),
    initialState: Boolean = false
) {
    var checked by remember { mutableStateOf(initialState) }
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(40.dp).background(iconBg, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(24.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = Color(0xFF1A1A1A), fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(subtitle, color = Color(0xFF666666), fontSize = 12.sp, lineHeight = 16.sp)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Switch(
            checked = checked,
            onCheckedChange = { checked = it },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = TealPrimary
            )
        )
    }
}
