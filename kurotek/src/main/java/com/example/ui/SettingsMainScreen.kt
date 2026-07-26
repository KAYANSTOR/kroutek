package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsMainScreen(
    onNavigateToSettings: (String) -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepBlack)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 24.dp)
        ) {
            // Header
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "الإعدادات",
                                color = PureWhite,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = "تخصيص النظام وإدارة البيانات",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                        IconButton(
                            onClick = {},
                            modifier = Modifier
                                .size(40.dp)
                                .background(SurfaceLight, RoundedCornerShape(8.dp))
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Settings,
                                contentDescription = "إعدادات",
                                tint = Color(0xFF1A9B8E),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            // System Settings
            item {
                Text(
                    text = "النظام",
                    color = PureWhite,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp, top = 12.dp)
                )
            }

            item {
                SettingsItem(
                    icon = Icons.Outlined.PhonelinkLock,
                    title = "اسم الشبكة",
                    subtitle = "kayan",
                    onClick = { onNavigateToSettings("network_name") }
                )
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                SettingsToggleItem(
                    icon = Icons.Outlined.Sms,
                    title = "المعالجة التلقائية للرسائل",
                    subtitle = "الخدمة تعمل — يتم استقبال ومعالجة الرسائل تلقائياً"
                )
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                SettingsToggleItem(
                    icon = Icons.Outlined.FilterList,
                    title = "معالجة مبالغ الفئات فقط",
                    subtitle = "عند التفعيل، سيتم فقط معالجة رسائل المحافظ التي تطابق مبالغ الفئات"
                )
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                SettingsToggleItem(
                    icon = Icons.Outlined.History,
                    title = "معالجة الرسائل القديمة (عند التوقف)",
                    subtitle = "تفعيل لمعالجة رسائل SMS التي وصلت أثناء إغلاق التطبيق أو توقفه مجددا"
                )
            }

            // Connection Settings
            item {
                Text(
                    text = "إعدادات شرائج الاتصال",
                    color = PureWhite,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 24.dp, bottom = 12.dp)
                )
            }

            item {
                SettingsItem(
                    icon = Icons.Outlined.Phonelink,
                    title = "إعدادات شرائج الاتصال",
                    subtitle = "Failover و الإرسال",
                    onClick = { onNavigateToSettings("connection") }
                )
            }

            // License Section
            item {
                Text(
                    text = "الرخيص",
                    color = PureWhite,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 24.dp, bottom = 12.dp)
                )
            }

            item {
                SettingsItem(
                    icon = Icons.Outlined.CardGiftcard,
                    title = "تجديد الاشتراك",
                    subtitle = "تجديد الرخيص أو إضافة رصيد SMS قبل انتهاء الباقة الحالية",
                    onClick = { onNavigateToSettings("license") }
                )
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                SettingsItem(
                    icon = Icons.Outlined.PhonelinkSetup,
                    title = "إدارة قيود البطارية والتشغيل في الخلفية",
                    subtitle = "منع أندرويد من إغلاق التطبيق لتوفير الذاكرة",
                    onClick = { onNavigateToSettings("battery") }
                )
            }

            // Spacing at bottom
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit = {}
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceLight),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = title,
                    color = PureWhite,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    color = TextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Color(0xFF1A9B8E),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun SettingsToggleItem(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    var isEnabled by remember { mutableStateOf(true) }

    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceLight),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Color(0xFF1A9B8E),
                    modifier = Modifier.size(24.dp)
                )
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = title,
                        color = PureWhite,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = subtitle,
                        color = TextSecondary,
                        fontSize = 10.sp,
                        lineHeight = 13.sp
                    )
                }
            }
            Switch(
                checked = isEnabled,
                onCheckedChange = { isEnabled = it },
                modifier = Modifier.size(width = 48.dp, height = 24.dp)
            )
        }
    }
}
