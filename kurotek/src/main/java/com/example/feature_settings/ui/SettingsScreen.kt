package com.example.feature_settings.ui

import androidx.compose.animation.animateContentSize
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SettingsScreen(
    onNavigateToHelpCenter: () -> Unit = {},
    onNavigateToHome: () -> Unit = {}
) {
    var expandedItems by remember { mutableStateOf(setOf<String>()) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A1A))
            .testTag("settings_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 80.dp)
        ) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF222222))
                    .padding(16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "الإعدادات",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { onNavigateToHome() }
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Database Section
                SettingsSection(
                    title = "البيانات والنظام",
                    isExpanded = expandedItems.contains("database"),
                    onToggle = {
                        expandedItems = if (expandedItems.contains("database")) {
                            expandedItems - "database"
                        } else {
                            expandedItems + "database"
                        }
                    },
                    items = listOf(
                        "حجم قاعدة البيانات: 0.10 ميجابايت",
                        "تصدير الحسابات (CSV)",
                        "تنظيف السجلات وتفريغ المساحة",
                        "تنظيف عميق للنظام"
                    )
                )

                // Help Section
                SettingsSection(
                    title = "المساعدة",
                    isExpanded = expandedItems.contains("help"),
                    onToggle = {
                        expandedItems = if (expandedItems.contains("help")) {
                            expandedItems - "help"
                        } else {
                            expandedItems + "help"
                        }
                    },
                    items = listOf(
                        "مركز المساعدة",
                        "حول التطبيق",
                        "سياسة الخصوصية"
                    ),
                    onHelpCenterClick = onNavigateToHelpCenter
                )
            }
        }

        // Bottom Navigation
        SettingsBottomNav(
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun SettingsSection(
    title: String,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    items: List<String>,
    onHelpCenterClick: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggle() },
            colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A2A)),
            border = BorderStroke(1.dp, Color(0xFF333333)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = if (title == "البيانات والنظام") 
                            Icons.Outlined.Storage 
                        else 
                            Icons.Outlined.Help,
                        contentDescription = null,
                        tint = Color(0xFF1A9B8E),
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = title,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Icon(
                    imageVector = if (isExpanded) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore,
                    contentDescription = null,
                    tint = Color(0xFF808080),
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        if (isExpanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items.forEach { item ->
                    SettingsItem(
                        text = item,
                        onClick = {
                            if (item == "مركز المساعدة" && onHelpCenterClick != null) {
                                onHelpCenterClick()
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsItem(
    text: String,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF222222)),
        border = BorderStroke(1.dp, Color(0xFF333333)),
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                color = Color(0xFFB0B0B0),
                fontSize = 13.sp,
                modifier = Modifier.weight(1f)
            )
            
            if (text == "مركز المساعدة") {
                Icon(
                    imageVector = Icons.Outlined.ChevronRight,
                    contentDescription = null,
                    tint = Color(0xFF1A9B8E),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun SettingsBottomNav(
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
            NavItem("التقارير", Icons.Outlined.Assessment)
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
