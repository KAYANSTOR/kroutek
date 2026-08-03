package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.Sell
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.KurotekApplication
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesReportScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val app = context.applicationContext as KurotekApplication
    
    val viewModel: ReportsViewModel = viewModel(
        factory = ReportsViewModelFactory(
            coreContainer = app.coreContainer
        )
    )

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("يومي", "شهري", "سنوي")
    
    val reportData by viewModel.reportData.collectAsState()
    
    // Derived values
    val totalRevenue = (reportData?.get("totalRevenue") as? Double) ?: 0.0
    val transactionCount = (reportData?.get("transactionCount") as? Int) ?: 0

    // Fetch data whenever tab changes
    LaunchedEffect(selectedTab) {
        val calendar = Calendar.getInstance()
        val toTimestamp = calendar.timeInMillis
        
        when (selectedTab) {
            0 -> calendar.set(Calendar.HOUR_OF_DAY, 0) // اليومي
            1 -> calendar.set(Calendar.DAY_OF_MONTH, 1) // الشهري
            2 -> calendar.set(Calendar.DAY_OF_YEAR, 1) // السنوي
        }
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val fromTimestamp = calendar.timeInMillis
        
        viewModel.generateReport(fromTimestamp, toTimestamp)
    }

    val currentDisplayDate = SimpleDateFormat("dd MMMM yyyy", Locale("ar")).format(Date())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column(horizontalAlignment = Alignment.Start, modifier = Modifier.fillMaxWidth()) {
                        Text("تقرير المبيعات", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Text("مبيعات الكروت اليومية والشهرية والسنوية والرسوم البيانية للمبيعات", color = TextSecondary, fontSize = 12.sp)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "رجوع", tint = PureWhite)
                    }
                },
                actions = {
                    IconButton(onClick = { /* Pick Date */ }) {
                        Icon(Icons.Outlined.CalendarToday, contentDescription = "تاريخ", tint = PureWhite)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ZNetBackgroundDark)
            )
        },
        containerColor = ZNetBackgroundDark,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* Share */ },
                containerColor = Color(0xFFC2185B),
                contentColor = PureWhite,
                modifier = Modifier.padding(bottom = 64.dp)
            ) {
                Icon(Icons.Outlined.Share, contentDescription = "مشاركة")
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(TealPrimary)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Analytics, contentDescription = null, tint = PureWhite)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("عرض التفاصيل والتحليلات", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            
            // Date Range Card
            Card(
                colors = CardDefaults.cardColors(containerColor = TealPrimary.copy(alpha = 0.1f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text("الفترة المحددة للتقرير", color = TextSecondary, fontSize = 12.sp)
                    Text(currentDisplayDate, color = TealPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
            
            // Tabs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                tabs.forEachIndexed { index, title ->
                    val isSelected = selectedTab == index
                    Button(
                        onClick = { selectedTab = index },
                        modifier = Modifier.weight(1f).height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) TealPrimary else ZNetSurfaceDark
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(title, color = if (isSelected) PureWhite else TextSecondary, fontWeight = FontWeight.Bold)
                    }
                }
            }
            
            // Info banner
            Card(
                colors = CardDefaults.cardColors(containerColor = TealPrimary.copy(alpha = 0.1f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFE1BEE7).copy(alpha = 0.3f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("تحليل ذكي", color = Color(0xFFCE93D8), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(if (transactionCount > 0) "أداء ممتاز لهذه الفترة." else "لا توجد مبيعات في هذه الفترة.", color = PureWhite, fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Outlined.Info, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(18.dp))
                    }
                }
            }
            
            // Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatCard(
                    title = "كروت مباعة",
                    value = "$transactionCount كرت",
                    icon = Icons.Outlined.Sell,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "إجمالي المبيعات",
                    value = "${totalRevenue.toInt()} ر.ي",
                    icon = Icons.Outlined.Payments,
                    modifier = Modifier.weight(1f)
                )
            }
            
            // Chart Card (Placeholder for visual aesthetic)
            Card(
                colors = CardDefaults.cardColors(containerColor = ZNetSurfaceDark),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().height(250.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp).fillMaxSize()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("اضغط على الأعمدة للتفاصيل", color = TextSecondary, fontSize = 12.sp)
                        Text("منحنى المبيعات", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    
                    Spacer(modifier = Modifier.weight(1f))
                    
                    // Mock Chart based on transaction count
                    Row(
                        modifier = Modifier.fillMaxWidth().height(100.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        val maxBarHeight = 80.dp
                        val baseHeights = listOf(0.2f, 0.5f, 0.3f, 0.8f, 0.4f, 0.6f, 1.0f)
                        val multiplier = if (transactionCount > 0) 1f else 0.1f
                        
                        val dates = listOf("1", "2", "3", "4", "5", "6", "7")
                        dates.forEachIndexed { index, date ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .width(32.dp)
                                        .height(maxBarHeight * (baseHeights[index] * multiplier))
                                        .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                        .background(TealPrimary.copy(alpha = 0.5f))
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(date, color = TextSecondary, fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(100.dp)) // For bottom bar and FAB
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = TealPrimary.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(androidx.compose.foundation.shape.CircleShape)
                    .background(TealPrimary.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(title, color = TextSecondary, fontSize = 14.sp)
            Text(value, color = TealPrimary, fontWeight = FontWeight.Bold, fontSize = 24.sp)
        }
    }
}
