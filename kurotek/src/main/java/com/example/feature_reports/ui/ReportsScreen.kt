package com.example.feature_reports.ui

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
import androidx.compose.material.icons.outlined.AreaChart
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.FilterList
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.ui.theme.BrandSecondary
import com.example.ui.theme.BrandSurface
import com.example.ui.theme.BrandSurfaceVariant
import com.example.ui.theme.TextSecondary
import androidx.compose.ui.platform.testTag

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsTab(
    salesViewModel: com.example.ui.SalesViewModel,
    dashboardViewModel: com.example.ui.DashboardViewModel,
    mainViewModel: com.example.ui.MainViewModel
) {
    val isLight = BrandBackground.luminance() > 0.5f
    val titleColor = if (isLight) Color(0xFF111111) else Color(0xFFFFFFFF)
    val accent = BrandSecondary
    val reportData by reportsViewModel.reportData.collectAsState()
    val transactions by salesViewModel.transactions.collectAsState()
    var selectedReport by remember { mutableIntStateOf(0) }
    val reportTypes = listOf("يومي", "أسبوعي", "شهري")
    val wallets = listOf("الكل", "جيب", "جوالي", "ون كاش", "كريمي")

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "التقارير",
                            color = titleColor,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Right
                        )
                        Text(
                            text = "مراقبة وتحليل绩效業績",
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
                actions = {
                    IconButton(onClick = {}, modifier = Modifier.testTag("reports_download")) {
                        Icon(
                            imageVector = Icons.Outlined.Download,
                            contentDescription = null,
                            tint = titleColor
                        )
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
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    reportTypes.forEachIndexed { index, label ->
                        ReportTypeChip(
                            label = label,
                            selected = selectedReport == index,
                            onClick = { selectedReport = index },
                            isLight = isLight,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    wallets.take(3).forEachIndexed { index, wallet ->
                        WalletFilterChip(
                            label = wallet,
                            selected = index == 0,
                            onClick = {},
                            isLight = isLight,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    wallets.drop(3).forEach { wallet ->
                        WalletFilterChip(
                            label = wallet,
                            selected = false,
                            onClick = {},
                            isLight = isLight,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = BrandSurface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
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
                                    imageVector = Icons.Outlined.AreaChart,
                                    contentDescription = null,
                                    tint = BrandPrimary
                                )
                            }
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "إجمالي المبيعات",
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = transactions.size.toString(),
                                    color = titleColor,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                        Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "التاريخ",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "2026-07-30",
                                    color = titleColor,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Icon(
                                    imageVector = Icons.Outlined.CalendarToday,
                                    contentDescription = null,
                                    tint = TextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ReportStatCard(
                        label = "العمليات",
                        value = transactions.size.toString(),
                        accent = accent,
                        isLight = isLight,
                        modifier = Modifier.weight(1f)
                    )
                    ReportStatCard(
                        label = "المبالغ",
                        value = "${transactions.sumOf { it.amount }} ر.ي",
                        accent = Color(0xFF22C55E),
                        isLight = isLight,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val pendingCount by dashboardViewModel.pendingApprovalsCount.collectAsState()
                    ReportStatCard(
                        label = "الموافق عليها",
                        value = "0",
                        accent = Color(0xFF3B82F6),
                        isLight = isLight,
                        modifier = Modifier.weight(1f)
                    )
                    ReportStatCard(
                        label = "قيد الانتظار",
                        value = pendingCount.toString(),
                        accent = Color(0xFFF59E0B),
                        isLight = isLight,
                        modifier = Modifier.weight(1f)
                    )
                }

                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = BrandSurfaceVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "أفضل العملاء",
                                color = titleColor,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "تصاعدية المبيعات",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                        Text(
                            text = "لا يوجد",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }

                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = BrandSurface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "آخر العمليات",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (transactions.isEmpty()) {
                        Text(
                            text = "لا توجد عمليات بعد",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    } else {
                        transactions.take(5).forEach { txn ->
                            ReportRowItem(
                                title = txn.walletType.ifEmpty { "عملية"},
                                subtitle = "${txn.walletType} | ${txn.amount} | ${java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date(txn.createdAt))}",
                                color = if (txn.amount > 0) Color(0xFF22C55E) else Color(0xFFEF4444),
                                isLight = isLight
                            )
                        }
                    }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun ReportTypeChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    isLight: Boolean,
    modifier: Modifier = Modifier
) {
    TextButton(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
        modifier = modifier
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
private fun WalletFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    isLight: Boolean,
    modifier: Modifier = Modifier
) {
    TextButton(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
        modifier = modifier
    ) {
        Text(
            text = label,
            color = if (selected) BrandPrimary else TextSecondary,
            fontSize = 12.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
private fun ReportStatCard(label: String, value: String, accent: Color, isLight: Boolean, modifier: Modifier = Modifier) {
    ElevatedCard(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = BrandSurface)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = label, color = TextSecondary, fontSize = 12.sp)
            Text(text = value, color = if (isLight) Color(0xFF111111) else Color(0xFFFFFFFF), fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(color = accent.copy(alpha = 0.35f), shape = RoundedCornerShape(4.dp))
            )
        }
    }
}

@Composable
private fun ReportRowItem(title: String, subtitle: String, color: Color, isLight: Boolean) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = BrandSurface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = title, color = if (isLight) Color(0xFF111111) else Color(0xFFFFFFFF), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text(text = subtitle, color = TextSecondary, fontSize = 11.sp)
            }
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(color = color, shape = CircleShape)
            )
        }
    }
}
