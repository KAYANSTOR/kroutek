package com.example.feature_approvals.ui

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
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
fun PendingApprovalsTab(
    dashboardViewModel: com.example.ui.DashboardViewModel,
    reportsViewModel: com.example.ui.ReportsViewModel,
    mainViewModel: com.example.ui.MainViewModel
) {
    val isLight = BrandBackground.luminance() > 0.5f
    val titleColor = if (isLight) Color(0xFF111111) else Color(0xFFFFFFFF)
    var showLinkDialog by remember { mutableStateOf(false) }
    var selectedStatus by remember { mutableIntStateOf(0) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "التفويضات المعلقة",
                            color = titleColor,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Right
                        )
                        Text(
                            text = "العمليات التي تنتظر موافقتك",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Right
                        )
                    }
                },
                navigationIcon = {
                    TextButton(onClick = {}) {
                        Text(text = "إغلاق", color = TextSecondary, fontSize = 14.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BrandBackground,
                    titleContentColor = titleColor
                )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ApprovalFilterChip(
                        label = "الكل",
                        selected = selectedStatus == 0,
                        onClick = { selectedStatus = 0 },
                        isLight = isLight
                    )
                    ApprovalFilterChip(
                        label = "معلق",
                        selected = selectedStatus == 1,
                        onClick = { selectedStatus = 1 },
                        isLight = isLight
                    )
                    ApprovalFilterChip(
                        label = "مكتمل",
                        selected = selectedStatus == 2,
                        onClick = { selectedStatus = 2 },
                        isLight = isLight
                    )
                }

                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = BrandSurfaceVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "إجمالي المعلقات",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "0",
                                color = titleColor,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                        LinearProgressIndicator(
                            progress = { 0f },
                            modifier = Modifier
                                .fillMaxWidth(0.45f)
                                .height(6.dp),
                            color = BrandPrimary,
                            trackColor = if (isLight) Color(0xFFE5E7EB) else Color(0xFF27272A)
                        )
                    }
                }

                listOf(
                    ApprovalItemData("طلب تعبئة كرت 500", "العميل: 777123456", "لا يوجد مخزون", Color(0xFFFF6B6B)),
                    ApprovalItemData("طلب تعبئة كرت 200", "العميل: 771234567", "فشل الإرسال", Color(0xFFF59E0B)),
                    ApprovalItemData("طلب تعبئة كرت 300", "العميل: 770987654", "بانتظار التأكيد", Color(0xFF3B82F6))
                ).forEach { item ->
                    ApprovalCard(item = item, isLight = isLight)
                }

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

data class ApprovalItemData(
    val title: String,
    val subtitle: String,
    val status: String,
    val statusColor: Color
)

@Composable
private fun ApprovalFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    isLight: Boolean
) {
    TextButton(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            color = if (selected) BrandPrimary else TextSecondary,
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
private fun ApprovalCard(item: ApprovalItemData, isLight: Boolean) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = BrandSurface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.title,
                    color = if (isLight) Color(0xFF111111) else Color(0xFFFFFFFF),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                ElevatedCard(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = item.statusColor.copy(alpha = if (isLight) 0.12f else 0.18f)
                    )
                ) {
                    Text(
                        text = item.status,
                        color = item.statusColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }
            }
            Text(
                text = item.subtitle,
                color = TextSecondary,
                fontSize = 12.sp
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TextButton(onClick = {}, modifier = Modifier.weight(1f)) {
                    Text("رفض", color = Color(0xFFEF4444), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
                androidx.compose.material3.Button(
                    onClick = {},
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                ) {
                    Text("موافقة", color = Color.Black, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
