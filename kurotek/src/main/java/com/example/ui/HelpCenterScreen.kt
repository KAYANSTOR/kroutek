package com.example.ui

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
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Store
import androidx.compose.material.icons.outlined.Warehouse
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.testTag

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpCenterScreen(
    onBackClick: () -> Unit = {}
) {
    val isLight = BrandBackground.luminance() > 0.5f
    val titleColor = if (isLight) Color(0xFF111111) else Color(0xFFFFFFFF)
    val divider = if (isLight) Color(0xFFE5E7EB) else Color(0xFF2E2E33)
    val sectionBg = if (isLight) BrandSurface else BrandSurfaceVariant

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandBackground)
            .testTag("help_center_screen")
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
                    text = "مركز المساعدة",
                    color = titleColor,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = null,
                        tint = titleColor
                    )
                }
            }

            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = sectionBg)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = null,
                        tint = TextSecondary
                    )
                    Text(
                        text = "ابحث في مركز المساعدة",
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                }
            }

            HelpSection(
                title = "مرحباً بك في Z Net",
                icon = Icons.Outlined.Info,
                items = listOf(
                    "مفهوم وأهداف النظام",
                    "دورة العمل الكاملة",
                    "مزايا الاشتراك الشهري",
                    "نقاط البيع والعمليات"
                ),
                isLight = isLight
            )
            HelpSection(
                title = "مفهوم النظام وأهدافه",
                icon = Icons.Outlined.Info,
                items = listOf(
                    "كيف يعمل زد نت",
                    "الجهوزية والثبات",
                    "حماية التراخيص",
                    "خدمة العملاء"
                ),
                isLight = isLight
            )
            HelpSection(
                title = "دورة العمل الكاملة",
                icon = Icons.Outlined.BarChart,
                items = listOf(
                    "من إيداع العميل حتى الترحيل",
                    "القوالب والردود التلقائية",
                    "نظام الأقساط والمتابعة",
                    "التقارير اليومية"
                ),
                isLight = isLight
            )
            HelpSection(
                title = "تهيئة التطبيق والترخيص",
                icon = Icons.Outlined.Settings,
                items = listOf(
                    "التنشيط الأول وصيانة الجهاز",
                    "النسخة التجريبية والاشتراكات",
                    "فترة السماح والتعطيل التلقائي",
                    "عملية التفعيل خطوة بخطوة"
                ),
                isLight = isLight
            )
            HelpSection(
                title = "إدارة العملاء والمشتركين",
                icon = Icons.Outlined.People,
                items = listOf(
                    "المعرفات المتعددة للمشترك",
                    "دمج الحسابات وتوحيد السجلات",
                    "قائمة العملاء المحظورين",
                    "المستحقات والرصيد"
                ),
                isLight = isLight
            )
            HelpSection(
                title = "فئات الخروج ومستودع المخزون",
                icon = Icons.Outlined.Warehouse,
                items = listOf(
                    "إعداد الفئات المالية والعمولة",
                    "الاستيراد الجماعي للمخزون",
                    "منع البيع المرجوع والحجز الدائم",
                    "مستويات الكروت والكميات"
                ),
                isLight = isLight
            )
            HelpSection(
                title = "نقاط البيع والتسويات والتحصيل",
                icon = Icons.Outlined.Store,
                items = listOf(
                    "حسابات نقاط البيع والتراكم",
                    "تسجيل التسويات المالية",
                    "تصدير وبيع كشف الحساب",
                    "إغلاق الحسابات وتصفية المبالغ"
                ),
                isLight = isLight
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "تحتاج مساعدة إضافية؟",
                color = titleColor,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            TextButton(
                onClick = {},
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "تواصل مع فريق الدعم",
                    color = BrandPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun HelpSection(
    title: String,
    icon: androidx.compose.material.icons.Icons.Outlined,
    items: List<String>,
    isLight: Boolean
) {
    var expanded by remember { mutableStateOf(false) }
    val containerColor = if (isLight) BrandSurface else BrandSurfaceVariant

    Column(modifier = Modifier.fillMaxWidth()) {
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = containerColor)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expanded = !expanded }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
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

                    Icon(
                        imageVector = if (expanded) Icons.Outlined.ArrowForwardIos else Icons.Outlined.ArrowForwardIos,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier
                            .size(18.dp)
                            .then(
                                if (expanded) Modifier then Modifier else Modifier
                            )
                            .then(
                                Modifier then
                                    androidx.compose.ui.draw.rotate(
                                        if (expanded) 90f else 0f
                                    )
                            )
                    )
                }

                AnimatedVisibility(
                    visible = expanded,
                    enter = fadeIn(tween(180)) + expandVertically(),
                    exit = fadeOut(tween(150)) + androidx.compose.animation.shrinkVertically()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items.forEachIndexed { index, item ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .background(
                                            color = if (isLight) Color(0xFFF0FDFA) else Color(0xFF042F2E),
                                            shape = RoundedCornerShape(6.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${index + 1}",
                                        color = BrandPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(
                                    text = item,
                                    color = if (isLight) Color(0xFF444444) else TextSecondary,
                                    fontSize = 14.sp,
                                    lineHeight = 20.sp,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
