package com.example.feature_settings.ui

import androidx.compose.runtime.Composable
import com.example.ui.theme.isDarkThemeState

@Composable
fun SettingsTab(
    mainViewModel: com.example.ui.MainViewModel,
    authViewModel: com.example.ui.AuthViewModel,
    settingsViewModel: com.example.ui.SettingsViewModel,
    distributorViewModel: com.example.ui.DistributorViewModel,
    onLogout: () -> Unit
) {
    SettingsScreen(
        onNavigateToHelpCenter = {},
        onNavigateToHome = onLogout,
        darkTheme = isDarkThemeState.value
    )
}

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.outlined.ArrowForwardIos
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Help
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BrandBackground
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandSurface
import com.example.ui.theme.BrandSurfaceVariant
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.isDarkThemeState
import androidx.compose.ui.platform.testTag

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateToHelpCenter: () -> Unit = {},
    onNavigateToHome: () -> Unit = {},
    darkTheme: Boolean = isDarkThemeState.value,
    onThemeChanged: (Boolean) -> Unit = { isDarkThemeState.value = it }
) {
    val isLight = BrandBackground.luminance() > 0.5f
    val titleColor = if (isLight) Color(0xFF111111) else Color(0xFFFFFFFF)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandBackground)
            .testTag("settings_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "الإعدادات",
                    color = titleColor,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onNavigateToHome) {
                    Text(
                        text = "إغلاق",
                        color = TextSecondary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            SettingsCard(
                title = "العرض",
                icon = if (darkTheme) Icons.Outlined.ArrowForwardIos else Icons.Outlined.ArrowForwardIos,
                isLight = isLight
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .background(
                                    color = if (isLight) Color(0xFFF0FDFA) else Color(0xFF042F2E),
                                    shape = RoundedCornerShape(14.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (darkTheme) Icons.Outlined.ArrowForwardIos else Icons.Outlined.ArrowForwardIos,
                                contentDescription = null,
                                tint = BrandPrimary
                            )
                        }
                        Column {
                            Text(
                                text = if (darkTheme) "الوضع الغامق" else "الوضع الفاتح",
                                color = titleColor,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (darkTheme) "مظهر داكن مريح للعين" else "مظهر فاتح واضح نقي",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }
                    Switch(
                        checked = darkTheme,
                        onCheckedChange = onThemeChanged,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                            checkedTrackColor = MaterialTheme.colorScheme.primary,
                            uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                            uncheckedTrackColor = MaterialTheme.colorScheme.surface
                        )
                    )
                }
            }

            SettingsCard(
                title = "بيانات وصيانة النظام",
                icon = Icons.Outlined.Storage,
                isLight = isLight
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    SettingsRow(
                        title = "حجم قاعدة البيانات الحالية",
                        value = "0.10 ميجابايت",
                        trailingIcon = Icons.Outlined.Storage,
                        isLight = isLight
                    )
                    SettingsRow(
                        title = "تصدير دفتر الحسابات (CSV)",
                        value = "نسخة احتياطية من العمليات",
                        trailingIcon = Icons.Outlined.Download,
                        isLight = isLight,
                        isWarning = false
                    )
                    SettingsRow(
                        title = "تنظيف السجلات وتفريغ المساحة",
                        value = "إزالة السجلات التلقائية",
                        trailingIcon = Icons.Outlined.Delete,
                        isLight = isLight,
                        isWarning = true
                    )
                    SettingsRow(
                        title = "تنظيف عميق للنظام",
                        value = "إعادة بناء قواعد البيانات",
                        trailingIcon = Icons.Outlined.Storage,
                        isLight = isLight,
                        isWarning = true
                    )
                }
            }

            SettingsCard(
                title = "المساعدة",
                icon = Icons.Outlined.Help,
                isLight = isLight,
                actionText = "عرض الكل",
                onActionClick = onNavigateToHelpCenter
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    SettingsRow(
                        title = "مركز المساعدة",
                        value = "دليل الاستخدام والأسئلة الشائعة",
                        trailingIcon = Icons.Outlined.ArrowForwardIos,
                        isLight = isLight,
                        onClick = onNavigateToHelpCenter
                    )
                }
            }

            SettingsCard(
                title = "عن التطبيق",
                icon = Icons.Outlined.Info,
                isLight = isLight
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    AboutRow(title = "الإصدار", value = "1.0.1", isLight = isLight)
                    AboutRow(title = "المطور", value = "دروبش عبدالله", isLight = isLight)
                    AboutRow(title = "الهاتف", value = "779776919", isLight = isLight)
                    AboutRow(
                        title = "الحقوق",
                        value = "© Z Net 2026 جميع الحقوق محفوظة",
                        isLight = isLight,
                        isLast = true
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun SettingsCard(
    title: String,
    icon: androidx.compose.material.icons.Icons.Outlined,
    isLight: Boolean,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val containerColor = if (isLight) BrandSurface else BrandSurfaceVariant

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = containerColor)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = if (isLight) Color(0xFFF0FDFA) else Color(0xFF042F2E),
                            shape = RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = BrandPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Text(
                    text = title,
                    color = if (isLight) Color(0xFF111111) else Color(0xFFFFFFFF),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                if (!actionText.isNullOrEmpty() && onActionClick != null) {
                    TextButton(onClick = onActionClick) {
                        Text(
                            text = actionText,
                            color = BrandPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                content()
            }
        }
    }
}

@Composable
private fun SettingsRow(
    title: String,
    value: String,
    trailingIcon: androidx.compose.material.icons.Icons.Outlined,
    isLight: Boolean,
    isWarning: Boolean = false,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        color = when {
                            isWarning -> if (isLight) Color(0xFFFFFBEB) else Color(0xFF451A03)
                            else -> if (isLight) Color(0xFFF0FDFA) else Color(0xFF042F2E)
                        },
                        shape = RoundedCornerShape(10.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = trailingIcon,
                    contentDescription = null,
                    tint = when {
                        isWarning -> Color(0xFFF59E0B)
                        else -> BrandPrimary
                    },
                    modifier = Modifier.size(18.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = if (isLight) Color(0xFF111111) else Color(0xFFFFFFFF),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = value,
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        }
        Icon(
            imageVector = Icons.Outlined.ArrowForwardIos,
            contentDescription = null,
            tint = TextSecondary,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
private fun AboutRow(title: String, value: String, isLight: Boolean, isLast: Boolean = false) {
    val dividerColor = if (isLight) Color(0xFFE5E7EB) else Color(0xFF2E2E33)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (!isLast) Modifier.padding(bottom = 10.dp) else Modifier),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            color = TextSecondary,
            fontSize = 14.sp
        )
        Text(
            text = value,
            color = if (isLight) Color(0xFF111111) else Color(0xFFFFFFFF),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.End
        )
    }
    if (!isLast) {
        androidx.compose.foundation.layout.Divider(
            modifier = Modifier.padding(vertical = 10.dp),
            thickness = 1.dp,
            color = dividerColor
        )
    }
}
