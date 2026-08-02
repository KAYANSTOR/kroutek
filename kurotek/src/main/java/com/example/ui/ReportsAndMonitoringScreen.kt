package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.PointOfSale
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.SmsFailed
import androidx.compose.material.icons.outlined.PendingActions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsAndMonitoringScreen(
    onNavigateBack: () -> Unit,
    onNavigateToRejectedMessages: () -> Unit,
    onNavigateToPendingMessages: () -> Unit,
    onNavigateToOperationsLog: () -> Unit,
    onNavigateToSalesReport: () -> Unit,
    onNavigateToPOSAccounts: () -> Unit
) {
    Scaffold(
        containerColor = ZNetBackgroundDark,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "رجوع", tint = PureWhite)
                    }
                },
                title = { 
                    Column(horizontalAlignment = Alignment.Start, modifier = Modifier.fillMaxWidth().padding(end = 16.dp)) {
                        Text("التقارير والمراقبة", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Text("سجلات العمليات وأخطاء النظام وتقرير المبيعات", color = TextSecondary, fontSize = 12.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ZNetBackgroundDark)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            
            // Messages Section
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "الرسائل",
                    color = TealPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 8.dp).align(Alignment.End)
                )
                
                Card(
                    colors = CardDefaults.cardColors(containerColor = ZNetSurfaceDark),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column {
                        ReportItemRow(
                            title = "الرسائل المرفوضة",
                            subtitle = "قراءة الرسائل التي لم تُعالج لأسباب فنية أو إدارية",
                            icon = Icons.Outlined.SmsFailed,
                            iconColor = StatusRed,
                            onClick = onNavigateToRejectedMessages
                        )
                        Divider(color = ZNetBackgroundDark, modifier = Modifier.padding(horizontal = 16.dp))
                        ReportItemRow(
                            title = "الرسائل المعلقة (قيد المعالجة)",
                            subtitle = "مراقبة الرسائل التي تم تسليمها للشبكة وبانتظار تأكيد الاستلام",
                            icon = Icons.Outlined.PendingActions,
                            iconColor = TealPrimary,
                            onClick = onNavigateToPendingMessages
                        )
                    }
                }
            }
            
            // Reports Section
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "التقارير",
                    color = TealPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 8.dp).align(Alignment.End)
                )
                
                Card(
                    colors = CardDefaults.cardColors(containerColor = ZNetSurfaceDark),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column {
                        ReportItemRow(
                            title = "سجل العمليات",
                            subtitle = "عرض وتتبع كامل لسجلات حركات الإيداعات وصرف الكروت للعملاء",
                            icon = Icons.Outlined.ReceiptLong,
                            iconColor = TextSecondary,
                            onClick = onNavigateToOperationsLog
                        )
                        Divider(color = ZNetBackgroundDark, modifier = Modifier.padding(horizontal = 16.dp))
                        ReportItemRow(
                            title = "تقرير المبيعات",
                            subtitle = "مبيعات الكروت اليومية والشهرية والسنوية والرسوم البيانية للمبيعات",
                            icon = Icons.Outlined.Analytics,
                            iconColor = TealPrimary,
                            onClick = onNavigateToSalesReport
                        )
                        Divider(color = ZNetBackgroundDark, modifier = Modifier.padding(horizontal = 16.dp))
                        ReportItemRow(
                            title = "حسابات نقاط البيع",
                            subtitle = "تقرير التسوية المالية والعمولات لنقاط البيع",
                            icon = Icons.Outlined.PointOfSale,
                            iconColor = TextSecondary,
                            onClick = onNavigateToPOSAccounts
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun ReportItemRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconColor: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.AutoMirrored.Outlined.ArrowBack,
            contentDescription = null,
            tint = TextSecondary
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.Start
        ) {
            Text(title, color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(subtitle, color = TextSecondary, fontSize = 12.sp, textAlign = androidx.compose.ui.text.style.TextAlign.End)
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(iconColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = iconColor)
        }
    }
}
