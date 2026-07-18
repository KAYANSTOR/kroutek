package com.example.feature_reports.ui
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.feature_customers.ui.exportTransactionsToCsv
import com.example.ui.DashboardMetricItem
import com.example.ui.theme.*
import com.example.models.*
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.platform.testTag
import android.widget.Toast
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.ImeAction
import com.example.utils.DocumentExporter
import androidx.core.content.FileProvider
import android.content.Intent
import android.net.Uri
import android.util.Log
import java.io.File
import java.nio.charset.StandardCharsets
import kotlinx.coroutines.withContext
import com.example.ui.MainViewModel

@Composable
fun ReportsTab(
    salesViewModel: com.example.ui.SalesViewModel,
    dashboardViewModel: com.example.ui.DashboardViewModel,
    mainViewModel: com.example.ui.MainViewModel
) {
    val allTransactions by salesViewModel.transactions.collectAsState()
    val allDeposits by salesViewModel.deposits.collectAsState()
    val allPendingApprovals by dashboardViewModel.pendingApprovals.collectAsState()
    val isDark by mainViewModel.isDarkTheme.collectAsState()
    val context = LocalContext.current

    var filterDateByFormatted by remember { 
        mutableStateOf(SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())) 
    }
    var selectedSourceFilter by remember { mutableStateOf("all") }
    
    // Status filter: "all", "success", "pending"
    var selectedStatusFilter by remember { mutableStateOf("all") }

    val filteredTransactions = remember(allTransactions, filterDateByFormatted, selectedSourceFilter) {
        allTransactions.filter { 
            val formatted = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date(it.createdAt))
            val matchesDate = formatted == filterDateByFormatted
            val matchesSource = when (selectedSourceFilter) {
                "جيب" -> it.walletType == "جيب"
                "جوالي" -> it.walletType == "جوالي"
                "ون كاش" -> it.walletType == "ون كاش"
                else -> true
            }
            matchesDate && matchesSource
        }
    }

    val filteredDeposits = remember(allDeposits, filterDateByFormatted, selectedSourceFilter) {
        allDeposits.filter { 
            val formatted = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date(it.createdAt))
            val matchesDate = formatted == filterDateByFormatted
            val matchesSource = when (selectedSourceFilter) {
                "جيب" -> it.walletType == "جيب"
                "جوالي" -> it.walletType == "جوالي"
                "ون كاش" -> it.walletType == "ون كاش"
                else -> true
            }
            matchesDate && matchesSource
        }
    }

    val filteredPendingApprovals = remember(allPendingApprovals, filterDateByFormatted, selectedSourceFilter) {
        allPendingApprovals.filter { 
            val formatted = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date(it.createdAt))
            val matchesDate = formatted == filterDateByFormatted
            val matchesSource = when (selectedSourceFilter) {
                "جيب" -> it.walletType == "جيب"
                "جوالي" -> it.walletType == "جوالي"
                "ون كاش" -> it.walletType == "ون كاش"
                else -> true
            }
            matchesDate && matchesSource
        }
    }

    val jeebTx = remember(allTransactions) { allTransactions.filter { it.walletType == "جيب" } }
    val jawaliTx = remember(allTransactions) { allTransactions.filter { it.walletType == "جوالي" } }
    val oneCashTx = remember(allTransactions) { allTransactions.filter { it.walletType == "ون كاش" } }
    val otherTx = remember(allTransactions) { allTransactions.filter { it.walletType != "جيب" && it.walletType != "جوالي" && it.walletType != "ون كاش" } }

    val jeebCount = jeebTx.size
    val jeebValue = jeebTx.sumOf { it.amount }

    val jawaliCount = jawaliTx.size
    val jawaliValue = jawaliTx.sumOf { it.amount }

    val oneCashCount = oneCashTx.size
    val oneCashValue = oneCashTx.sumOf { it.amount }

    val otherCount = otherTx.size
    val otherValue = otherTx.sumOf { it.amount }

    val totalCount = allTransactions.size
    val totalValue = allTransactions.sumOf { it.amount }

    // Calculations for bottom summary panel
    val (totalSoldQty, totalRevenue) = remember(filteredTransactions) {
        val qty = filteredTransactions.size
        val revenue = filteredTransactions.sumOf { it.amount }
        Pair(qty, revenue)
    }

    val totalPendingQty = filteredPendingApprovals.size
    val totalPendingRevenue = filteredPendingApprovals.sumOf { it.amount }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.End
        ) {
        // 📊 Header Statistics Card with segmented wallet sources visualization
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = BorderStroke(1.5.dp, if (isDark) Color(0xFF2D2D2D) else Color(0x0A000000)),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("history_stats_header_card"),
                elevation = CardDefaults.cardElevation(defaultElevation = if (isDark) 0.dp else 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    // Header title
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Analytics,
                            contentDescription = "Analytics Icon",
                            tint = if (isDark) GlowOrangeGold else Color(0xFFE65100),
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "تحليلات وإحصائيات العمليات والمحافظ 📊",
                            color = PureWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            textAlign = TextAlign.Right
                        )
                    }

                    Divider(color = if (isDark) Color(0xFF2D2D2D) else Color(0x0F000000), thickness = 1.dp)

                    // Unified KPIs Row (Total Count / Total Value)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Total Value Card
                        Card(
                            colors = CardDefaults.cardColors(containerColor = DeepBlack),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, if (isDark) Color(0xFF212121) else Color(0x05000000)),
                            modifier = Modifier.weight(1.2f)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "إجمالي قيمة المبيعات",
                                    color = TextSecondary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "$totalValue ر.ي",
                                    color = if (isDark) GlowOrangeGold else Color(0xFFE65100),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }

                        // Total Count Card
                        Card(
                            colors = CardDefaults.cardColors(containerColor = DeepBlack),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, if (isDark) Color(0xFF212121) else Color(0x05000000)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "الكروت الموزعة",
                                    color = TextSecondary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "$totalCount كرت",
                                    color = if (isDark) GlowEmeraldGreen else Color(0xFF2E7D32),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }

                    // Segmented Data Visualization Bar Chart (Clean progress-like block)
                    Text(
                        text = "توزيع حجم العمليات وقيمتها حسب المحفظة المانحة:",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Right,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Proportional horizontal bar layout
                    val totalSum = (jeebValue + jawaliValue + oneCashValue + otherValue).toDouble()
                    val jeebPercentage = if (totalSum > 0) jeebValue / totalSum else if (totalCount > 0 && jeebCount > 0) jeebCount.toDouble() / totalCount else 0.25
                    val jawaliPercentage = if (totalSum > 0) jawaliValue / totalSum else if (totalCount > 0 && jawaliCount > 0) jawaliCount.toDouble() / totalCount else 0.25
                    val oneCashPercentage = if (totalSum > 0) oneCashValue / totalSum else if (totalCount > 0 && oneCashCount > 0) oneCashCount.toDouble() / totalCount else 0.25
                    val otherPercentage = if (totalSum > 0) otherValue / totalSum else if (totalCount > 0 && otherCount > 0) otherCount.toDouble() / totalCount else 0.25

                    Column(
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Segmented bar
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(14.dp)
                                .clip(RoundedCornerShape(7.dp))
                                .background(if (isDark) Color(0xFF1E1E1E) else Color(0x0F000000))
                        ) {
                            if (jeebPercentage > 0) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .weight(jeebPercentage.toFloat().coerceAtLeast(0.02f))
                                        .background(Color(0xFF2196F3))
                                )
                            }
                            if (jawaliPercentage > 0) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .weight(jawaliPercentage.toFloat().coerceAtLeast(0.02f))
                                        .background(Color(0xFFAB47BC))
                                )
                            }
                            if (oneCashPercentage > 0) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .weight(oneCashPercentage.toFloat().coerceAtLeast(0.02f))
                                        .background(Color(0xFFE91E63))
                                )
                            }
                            if (otherPercentage > 0) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .weight(otherPercentage.toFloat().coerceAtLeast(0.02f))
                                        .background(if (isDark) GlowOrangeGold else Color(0xFFE65100))
                                )
                            }
                        }

                        // Share descriptors row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(if (isDark) GlowOrangeGold else Color(0xFFE65100)))
                                Text("أخرى: ${(otherPercentage * 100).toInt()}%", color = TextSecondary, fontSize = 9.sp)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFE91E63)))
                                Text("ون كاش: ${(oneCashPercentage * 100).toInt()}%", color = TextSecondary, fontSize = 9.sp)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFAB47BC)))
                                Text("جوالي: ${(jawaliPercentage * 100).toInt()}%", color = TextSecondary, fontSize = 9.sp)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF2196F3)))
                                Text("محفظة كاش: ${(jeebPercentage * 100).toInt()}%", color = TextSecondary, fontSize = 9.sp)
                            }
                        }
                    }

                    // Detailed source cards metrics
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Jeeb Row details
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF2196F3).copy(alpha = 0.08f), RoundedCornerShape(10.dp))
                                .border(BorderStroke(0.5.dp, Color(0xFF2196F3).copy(alpha = 0.15f)), RoundedCornerShape(10.dp))
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "$jeebCount كارت | $jeebValue ر.ي", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            Text(text = "محفظة كاش", color = Color(0xFF90CAF9), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }

                        // Jawali Row details
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFAB47BC).copy(alpha = 0.08f), RoundedCornerShape(10.dp))
                                .border(BorderStroke(0.5.dp, Color(0xFFAB47BC).copy(alpha = 0.15f)), RoundedCornerShape(10.dp))
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "$jawaliCount كارت | $jawaliValue ر.ي", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            Text(text = "محفظة جوالي (Jawali)", color = Color(0xFFE1BEE7), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }

                        // One Cash Row details
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFE91E63).copy(alpha = 0.08f), RoundedCornerShape(10.dp))
                                .border(BorderStroke(0.5.dp, Color(0xFFE91E63).copy(alpha = 0.15f)), RoundedCornerShape(10.dp))
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "$oneCashCount كارت | $oneCashValue ر.ي", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            Text(text = "محفظة ون كاش (One Cash)", color = Color(0xFFF8BBD0), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }

                        // Others Row details
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(if (isDark) GlowOrangeGold.copy(alpha = 0.05f) else Color(0xFFE65100).copy(alpha = 0.04f), RoundedCornerShape(10.dp))
                                .border(BorderStroke(0.5.dp, if (isDark) GlowOrangeGold.copy(alpha = 0.15f) else Color(0xFFE65100).copy(alpha = 0.1f)), RoundedCornerShape(10.dp))
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "$otherCount كارت | $otherValue ر.ي", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            Text(text = "طرق سداد ومبيعات أخرى", color = if (isDark) GlowOrangeGold else Color(0xFFE65100), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Date input filter section
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = BorderStroke(1.dp, if (isDark) Color(0xFF2D2D2D) else Color(0x0A000000)),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = if (isDark) 0.dp else 4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "فلترة وتصفية التقارير والعمليات 📅",
                        color = if (isDark) GlowOrangeGold else Color(0xFFE65100),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )

                    OutlinedTextField(
                        value = filterDateByFormatted,
                        onValueChange = { filterDateByFormatted = it },
                        label = { Text("أدخل التاريخ للبحث والتصفية (yyyy-MM-dd)") },
                        placeholder = { Text("مثال: ${SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())}") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("filter_date_input"),
                        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center, color = PureWhite),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = PureWhite,
                            unfocusedTextColor = PureWhite,
                            focusedBorderColor = if (isDark) GlowOrangeGold else Color(0xFFF57C00),
                            unfocusedBorderColor = TextSecondary.copy(alpha = 0.25f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Today Button with OrangeGoldGradient
                        Button(
                            onClick = { 
                                filterDateByFormatted = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()) 
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Color.White),
                            contentPadding = PaddingValues(),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(OrangeGoldGradient),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("اليوم", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }

                        // Yesterday Button
                        Button(
                            onClick = { 
                                val cal = Calendar.getInstance()
                                cal.add(Calendar.DATE, -1)
                                filterDateByFormatted = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(cal.time) 
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SurfaceDark, contentColor = if (isDark) GlowOrangeGold else Color(0xFFE65100)),
                            border = BorderStroke(1.dp, (if (isDark) GlowOrangeGold else Color(0xFFE65100)).copy(alpha = 0.3f)),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                        ) {
                            Text("الأمس", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "خيارات التصدير والطباعة 🖨️",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Right,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // PDF Button
                        Button(
                            onClick = {
                                val headers = listOf("الرقم", "المحفظة", "القيمة (ر.ي)", "التاريخ", "الحالة")
                                val rows = filteredTransactions.map { tx ->
                                    val dateStr = SimpleDateFormat("HH:mm a", Locale.getDefault()).format(Date(tx.createdAt))
                                    listOf(
                                        tx.phone.ifEmpty { "بدون رقم" },
                                        if (tx.walletType == "جيب") "محفظة كاش" else tx.walletType,
                                        "${tx.amount} ر.ي",
                                        dateStr,
                                        "ناجحة"
                                    )
                                }
                                DocumentExporter.exportToPdf(
                                    context = context,
                                    fileName = "تقرير_المبيعات",
                                    title = "تقرير مبيعات الموزع لتاريخ $filterDateByFormatted",
                                    headers = headers,
                                    rows = rows
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f).height(38.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Outlined.PictureAsPdf, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                                Text("PDF", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.White)
                            }
                        }

                        // Excel Button
                        Button(
                            onClick = {
                                val headers = listOf("الرقم", "المحفظة", "القيمة (ر.ي)", "التاريخ", "الحالة")
                                val rows = filteredTransactions.map { tx ->
                                    val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(tx.createdAt))
                                    listOf(
                                        tx.phone.ifEmpty { "بدون رقم" },
                                        if (tx.walletType == "جيب") "محفظة كاش" else tx.walletType,
                                        "${tx.amount}",
                                        dateStr,
                                        "ناجحة"
                                    )
                                }
                                DocumentExporter.exportToExcel(
                                    context = context,
                                    fileName = "تقرير_المبيعات",
                                    title = "تقرير مبيعات الموزع لتاريخ $filterDateByFormatted",
                                    headers = headers,
                                    rows = rows
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f).height(38.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Outlined.TableChart, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                                Text("Excel", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.White)
                            }
                        }

                        // Print Button
                        Button(
                            onClick = {
                                val headers = listOf("الرقم", "المحفظة", "القيمة (ر.ي)", "التاريخ", "الحالة")
                                val rows = filteredTransactions.map { tx ->
                                    val dateStr = SimpleDateFormat("HH:mm a", Locale.getDefault()).format(Date(tx.createdAt))
                                    listOf(
                                        tx.phone.ifEmpty { "بدون رقم" },
                                        if (tx.walletType == "جيب") "محفظة كاش" else tx.walletType,
                                        "${tx.amount} ر.ي",
                                        dateStr,
                                        "ناجحة"
                                    )
                                }
                                DocumentExporter.printReport(
                                    context = context,
                                    title = "تقرير مبيعات الموزع لتاريخ $filterDateByFormatted",
                                    headers = headers,
                                    rows = rows
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f).height(38.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                                Text("طباعة", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.White)
                            }
                        }
                    }
                }
            }
        }

        // Filter Source Chip Group (Toggle between Jeb and Jawali / One Cash / All)
        item {
            Column(
                modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "مصدر العمليات المحفظية:",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Right
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val filterOptions = listOf(
                        Triple("all", "الكل", "All"),
                        Triple("جيب", "محفظة كاش", "Jeeb"),
                        Triple("جوالي", "جوالي", "Jawali"),
                        Triple("ون كاش", "ون كاش", "OneCash")
                    )
                    filterOptions.forEach { opt ->
                        val isSelected = selectedSourceFilter == opt.first
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .clip(RoundedCornerShape(19.dp))
                                .background(
                                    if (isSelected) {
                                        if (isDark) GlowOrangeGold.copy(alpha = 0.15f) else Color(0xFFE65100).copy(alpha = 0.12f)
                                    } else {
                                        SurfaceDark
                                    }
                                )
                                .clickable { selectedSourceFilter = opt.first }
                                .border(
                                    BorderStroke(
                                        width = 1.dp,
                                        color = if (isSelected) {
                                            if (isDark) GlowOrangeGold else Color(0xFFE65100)
                                        } else {
                                            if (isDark) Color(0xFF2D2D2D) else Color(0x0A000000)
                                        }
                                    ),
                                    RoundedCornerShape(19.dp)
                                )
                                .testTag("source_filter_${opt.third.lowercase()}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = opt.second,
                                color = if (isSelected) {
                                    if (isDark) GlowOrangeGold else Color(0xFFE65100)
                                } else {
                                    TextSecondary
                                },
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Filter Status Chip Group (All / Success / Pending)
        item {
            Column(
                modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "حالة العمليات التوزيعية:",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Right
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val statusOptions = listOf(
                        Triple("all", "الكل", "All"),
                        Triple("success", "الناجحة ✔", "Success"),
                        Triple("pending", "المعلقة (يدوي) ⏳", "Pending")
                    )
                    statusOptions.forEach { opt ->
                        val isSelected = selectedStatusFilter == opt.first
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .clip(RoundedCornerShape(19.dp))
                                .background(
                                    if (isSelected) {
                                        if (isDark) GlowOrangeGold.copy(alpha = 0.15f) else Color(0xFFE65100).copy(alpha = 0.12f)
                                    } else {
                                        SurfaceDark
                                    }
                                )
                                .clickable { selectedStatusFilter = opt.first }
                                .border(
                                    BorderStroke(
                                        width = 1.dp,
                                        color = if (isSelected) {
                                            if (isDark) GlowOrangeGold else Color(0xFFE65100)
                                        } else {
                                            if (isDark) Color(0xFF2D2D2D) else Color(0x0A000000)
                                        }
                                    ),
                                    RoundedCornerShape(19.dp)
                                )
                                .testTag("status_filter_${opt.third.lowercase()}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = opt.second,
                                color = if (isSelected) {
                                    if (isDark) GlowOrangeGold else Color(0xFFE65100)
                                } else {
                                    TextSecondary
                                },
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Section Title: Pending manual approvals
        if (selectedStatusFilter == "all" || selectedStatusFilter == "pending") {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(StatusRed.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "$totalPendingQty معلقة",
                            color = StatusRed,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "العمليات المعلقة التي تتطلب توزيعاً يدوياً ⏳",
                        color = PureWhite,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Right
                    )
                }
            }

            if (filteredPendingApprovals.isEmpty()) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SurfaceDark.copy(alpha = 0.4f)),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, if (isDark) Color(0xFF2D2D2D) else Color(0x0A000000)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                            Text(text = "لا توجد عمليات معلقة مع مطابقة هذه الفلاتر اليوم.", color = TextSecondary, fontSize = 12.sp)
                        }
                    }
                }
            } else {
                items(filteredPendingApprovals) { pending ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                        border = BorderStroke(1.dp, StatusRed.copy(alpha = 0.3f)),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = if (isDark) 0.dp else 1.5.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(pending.createdAt)),
                                color = TextSecondary,
                                fontSize = 11.sp
                            )

                            Column(horizontalAlignment = Alignment.End) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = pending.walletType,
                                        color = when (pending.walletType) {
                                            "جيب" -> Color(0xFF90CAF9)
                                            "جوالي" -> Color(0xFFA5D6A7)
                                            "ون كاش" -> Color(0xFFF48FB1)
                                            else -> Color(0xFFFFCC80)
                                        },
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier
                                            .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(4.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                    Text(
                                        text = "مبلغ ${pending.amount} ر.ي",
                                        color = PureWhite,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(
                                    text = "الزبون/الحساب: ${pending.phone}",
                                    color = TextSecondary,
                                    fontSize = 11.sp,
                                    textAlign = TextAlign.Right
                                )
                                Box(
                                    modifier = Modifier
                                        .padding(top = 4.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(StatusRed.copy(alpha = 0.1f))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = if (pending.isAccountCode) "رمز حساب - تتطلب مطابقة يدوية ⚠️" else "تتطلب توزيع كارت يدوي ⏳",
                                        color = StatusRed,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section Title: Successful operations (Deposits and delivery status)
        if (selectedStatusFilter == "all" || selectedStatusFilter == "success") {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(StatusGreen.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "${filteredDeposits.count { it.isShared }} ناجحة",
                            color = StatusGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "العمليات الناجحة (سجل التحصيل الموزع) ✔",
                        color = PureWhite,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Right
                    )
                }
            }

            val successDeposits = filteredDeposits.filter { it.isShared }

            if (successDeposits.isEmpty()) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SurfaceDark.copy(alpha = 0.4f)),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, if (isDark) Color(0xFF2D2D2D) else Color(0x0A000000)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                            Text(text = "لا توجد عمليات ناجحة مسجلة اليوم.", color = TextSecondary, fontSize = 12.sp)
                        }
                    }
                }
            } else {
                items(successDeposits) { deposit ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                        border = BorderStroke(1.dp, StatusGreen.copy(alpha = 0.2f)),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = if (isDark) 0.dp else 1.5.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(deposit.createdAt)),
                                color = TextSecondary,
                                fontSize = 11.sp
                            )

                            Column(horizontalAlignment = Alignment.End) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = deposit.walletType,
                                        color = when (deposit.walletType) {
                                            "جيب" -> Color(0xFF90CAF9)
                                            "جوالي" -> Color(0xFFA5D6A7)
                                            "ون كاش" -> Color(0xFFF48FB1)
                                            else -> Color(0xFFFFCC80)
                                        },
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier
                                            .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(4.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                    Text(
                                        text = "مبلغ ${deposit.amount} ر.ي",
                                        color = PureWhite,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(
                                    text = "الزبون: ${deposit.phone}",
                                    color = TextSecondary,
                                    fontSize = 11.sp,
                                    textAlign = TextAlign.Right
                                )
                                Row(
                                    modifier = Modifier.padding(top = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(StatusGreen.copy(alpha = 0.1f))
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text(
                                            text = "تم تسليم الكرت للزبون بنجاح ✔",
                                            color = StatusGreen,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section Title: Logs of transactions
        item {
            Text(
                text = "سجل الكروت الموزعة (Transaction Log) 📋",
                color = PureWhite,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                textAlign = TextAlign.Right
            )
        }

        if (filteredTransactions.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, if (isDark) Color(0xFF2D2D2D) else Color(0x0A000000)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                        Text(text = "لا توجد معاملات مبيعات مسجلة بالتاريخ المحدد.", color = TextSecondary, fontSize = 12.sp)
                    }
                }
            }
        } else {
            items(filteredTransactions, key = { it.id }) { trans ->
                val isSuccessfulDistribution = !trans.cardCode.contains("غير متوفر") && !trans.cardCode.contains("فشل")
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                    border = BorderStroke(1.dp, if (isDark) Color(0xFF2D2D2D) else Color(0x0A000000)),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = if (isDark) 0.dp else 1.5.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSuccessfulDistribution) StatusGreen.copy(alpha = 0.1f) else StatusRed.copy(alpha = 0.1f))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = if (isSuccessfulDistribution) "ناجح ✔" else "فاشل ✖",
                                    color = if (isSuccessfulDistribution) StatusGreen else StatusRed,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            
                            Text(
                                text = "كرت فئة ${trans.amount} ر.ي",
                                color = if (isDark) GlowOrangeGold else Color(0xFFE65100),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }

                        Text(
                            text = "تفاصيل الكرت: ${trans.cardCode}",
                            color = PureWhite,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Right,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text(
                            text = "تاريخ العملية: ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(trans.createdAt))} | هاتف العميل: ${trans.phone}",
                            color = TextSecondary,
                            fontSize = 10.sp,
                            textAlign = TextAlign.Right,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        // Bottom Summary Panel (Vibrant Orange-Gold Gradient Card)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                    .testTag("reports_summary_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(OrangeGoldGradient)
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "ملخص عمليات وتوزيع اليوم 📊",
                        color = Color.Black,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp
                    )

                    HorizontalDivider(color = Color.Black.copy(alpha = 0.15f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "$totalSoldQty كارت", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(text = "إجمالي الكروت المباعة التلقائية:", color = Color.Black.copy(alpha = 0.8f), fontSize = 13.sp)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "$totalPendingQty عملية", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(text = "إجمالي العمليات المعلقة حالياً:", color = Color.Black.copy(alpha = 0.8f), fontSize = 13.sp)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "$totalRevenue ر.ي", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 17.sp)
                        Text(text = "إجمالي المبالغ المالية المحصلة الموزعة:", color = Color.Black.copy(alpha = 0.8f), fontSize = 13.sp)
                    }
                }
            }
        }
    }

    // floating action button to export CSV
    FloatingActionButton(
        onClick = { exportTransactionsToCsv(context, filteredTransactions, filterDateByFormatted) },
        containerColor = Color.Transparent,
        modifier = Modifier
            .align(Alignment.BottomStart)
            .padding(16.dp)
            .height(50.dp)
            .clip(RoundedCornerShape(18.dp))
            .testTag("export_csv_fab"),
        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .background(OrangeGoldGradient)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Share,
                    contentDescription = "تصدير CSV",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "تصدير CSV 📥",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
    }
}
}

