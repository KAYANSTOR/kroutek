package com.example.ui

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
fun HelpCenterScreen(
    onBackClick: () -> Unit = {}
) {
    var expandedSections by remember { mutableStateOf(setOf<String>()) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A1A))
            .testTag("help_center_screen")
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
                        text = "مركز المساعدة",
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
                            .clickable { onBackClick() }
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Section 1: مرحباً بك في Z Net
                HelpSection(
                    sectionId = "section_1",
                    title = "مرحباً بك في Z Net",
                    icon = Icons.Outlined.Info,
                    expandedSections = expandedSections,
                    onToggle = { sectionId ->
                        expandedSections = if (expandedSections.contains(sectionId)) {
                            expandedSections - sectionId
                        } else {
                            expandedSections + sectionId
                        }
                    },
                    content = listOf(
                        "مفهوم وأهداف النظام",
                        "دورة العمل الكاملة",
                        "مزايا الاشتراك الشهري",
                        "نقاط البيع والعمليات"
                    )
                )

                // Section 2: تهيئة التطبيق والترخيص
                HelpSection(
                    sectionId = "section_2",
                    title = "تهيئة التطبيق والترخيص",
                    icon = Icons.Outlined.Settings,
                    expandedSections = expandedSections,
                    onToggle = { sectionId ->
                        expandedSections = if (expandedSections.contains(sectionId)) {
                            expandedSections - sectionId
                        } else {
                            expandedSections + sectionId
                        }
                    },
                    content = listOf(
                        "التنشيط الأول وصيانة الجهاز",
                        "النسخة التجريبية وباقات التفعيل",
                        "فترة السماح والتعطيل التلقائي",
                        "عملية التفعيل خطوة بخطوة"
                    )
                )

                // Section 3: لوحة التحكم وإحصائيات التشغيل
                HelpSection(
                    sectionId = "section_3",
                    title = "لوحة التحكم وإحصائيات التشغيل",
                    icon = Icons.Outlined.BarChart,
                    expandedSections = expandedSections,
                    onToggle = { sectionId ->
                        expandedSections = if (expandedSections.contains(sectionId)) {
                            expandedSections - sectionId
                        } else {
                            expandedSections + sectionId
                        }
                    },
                    content = listOf(
                        "مراقبة الخدمة في الخلفية",
                        "مؤشرات المبيعات والمخزون",
                        "عرض التقارير المتقدمة",
                        "تصدير البيانات والإحصائيات"
                    )
                )

                // Section 4: إدارة العملاء والمشتركين
                HelpSection(
                    sectionId = "section_4",
                    title = "إدارة العملاء والمشتركين",
                    icon = Icons.Outlined.People,
                    expandedSections = expandedSections,
                    onToggle = { sectionId ->
                        expandedSections = if (expandedSections.contains(sectionId)) {
                            expandedSections - sectionId
                        } else {
                            expandedSections + sectionId
                        }
                    },
                    content = listOf(
                        "المعرفات المتعددة للمشترك",
                        "دمج الحسابات وتوحيد السجلات",
                        "قائمة السوداء للعملاء المحظورين",
                        "إدارة المستحقات والرصيد"
                    )
                )

                // Section 5: فئات الخروج ومستودع المخزون
                HelpSection(
                    sectionId = "section_5",
                    title = "فئات الخروج ومستودع المخزون",
                    icon = Icons.Outlined.Warehouse,
                    expandedSections = expandedSections,
                    onToggle = { sectionId ->
                        expandedSections = if (expandedSections.contains(sectionId)) {
                            expandedSections - sectionId
                        } else {
                            expandedSections + sectionId
                        }
                    },
                    content = listOf(
                        "إعداد الفئات المالية والعمولة",
                        "الاستيراد الجماعي للمخزون",
                        "منع البيع المرجود والحجز الدائم",
                        "إدارة المستويات والكميات"
                    )
                )

                // Section 6: نقاط البيع والتسويات والتحصيل
                HelpSection(
                    sectionId = "section_6",
                    title = "نقاط البيع والتسويات والتحصيل",
                    icon = Icons.Outlined.Store,
                    expandedSections = expandedSections,
                    onToggle = { sectionId ->
                        expandedSections = if (expandedSections.contains(sectionId)) {
                            expandedSections - sectionId
                        } else {
                            expandedSections + sectionId
                        }
                    },
                    content = listOf(
                        "حسابات نقاط البيع وتراكم المستحقات",
                        "تسجيل التسويات المالية والتحصيلات",
                        "تصدير وبيع ومشاركة كشف الحساب",
                        "إغلاق الحسابات وتصفية المبالغ"
                    )
                )
            }
        }

        // Bottom Navigation
        HelpCenterBottomNav(
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun HelpSection(
    sectionId: String,
    title: String,
    icon: androidx.compose.material.icons.materialIcon,
    expandedSections: Set<String>,
    onToggle: (String) -> Unit,
    content: List<String>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggle(sectionId) },
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
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color(0xFF1A9B8E),
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = title,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Icon(
                    imageVector = if (expandedSections.contains(sectionId)) 
                        Icons.Outlined.ExpandLess 
                    else 
                        Icons.Outlined.ExpandMore,
                    contentDescription = null,
                    tint = Color(0xFF808080),
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        if (expandedSections.contains(sectionId)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                content.forEachIndexed { index, item ->
                    HelpContentItem(
                        number = index + 1,
                        text = item
                    )
                }
            }
        }
    }
}

@Composable
private fun HelpContentItem(
    number: Int,
    text: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF222222)),
        border = BorderStroke(1.dp, Color(0xFF333333)),
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(
                        Color(0xFF1A9B8E).copy(alpha = 0.2f),
                        RoundedCornerShape(6.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = number.toString(),
                    color = Color(0xFF1A9B8E),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Text(
                text = text,
                color = Color(0xFFB0B0B0),
                fontSize = 13.sp,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Start
            )
        }
    }
}

@Composable
private fun HelpCenterBottomNav(
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
