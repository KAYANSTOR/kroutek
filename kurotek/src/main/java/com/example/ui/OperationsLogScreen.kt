package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.FilterAlt
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Sort
import androidx.compose.material.icons.outlined.SyncAlt
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
import com.example.models.Transaction
import com.example.models.Deposit
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Calendar

data class OperationItem(val id: String, val type: String, val title: String, val subtitle: String, val amount: String, val date: Long, val isPositive: Boolean)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OperationsLogScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val app = context.applicationContext as KurotekApplication
    
    val viewModel: ReportsViewModel = viewModel(
        factory = ReportsViewModelFactory(
            coreContainer = app.coreContainer
        )
    )

    var searchQuery by remember { mutableStateOf("") }
    
    val reportData by viewModel.reportData.collectAsState()
    
    LaunchedEffect(Unit) {
        val calendar = Calendar.getInstance()
        val toTimestamp = calendar.timeInMillis
        calendar.add(Calendar.DAY_OF_YEAR, -30)
        val fromTimestamp = calendar.timeInMillis
        viewModel.generateReport(fromTimestamp, toTimestamp)
    }
    
    val transactions = (reportData?.get("transactions") as? List<Transaction>) ?: emptyList()
    val deposits = (reportData?.get("deposits") as? List<Deposit>) ?: emptyList()
    
    val allOperations = (transactions.map { 
        OperationItem(it.id, "بيع", "بيع كرت شبكة", "للرقم ${it.phone} (${it.walletType})", "+${it.amount}", it.createdAt, true) 
    } + deposits.map { 
        OperationItem(it.id, "إيداع", "إيداع محفظة", "للرقم ${it.phone} (${it.walletType})", "${it.amount}", it.createdAt, it.isShared) 
    }).sortedByDescending { it.date }
    
    val filteredOperations = allOperations.filter {
        it.title.contains(searchQuery, ignoreCase = true) || it.subtitle.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column(horizontalAlignment = Alignment.Start, modifier = Modifier.fillMaxWidth()) {
                        Text("سجل العمليات", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Text("عرض وتصفية جميع المعاملات المسجلة", color = TextSecondary, fontSize = 12.sp)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "رجوع", tint = PureWhite)
                    }
                },
                actions = {
                    IconButton(onClick = { /* Filter */ }, modifier = Modifier.padding(end = 8.dp)) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(ZNetSurfaceDark),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Outlined.FilterAlt, contentDescription = "تصفية", tint = PureWhite)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ZNetBackgroundDark)
            )
        },
        containerColor = ZNetBackgroundDark
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("بحث برقم الجوال، الاسم، رقم الكرت...", color = TextSecondary) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TealPrimary,
                    unfocusedBorderColor = ZNetSurfaceDark,
                    focusedContainerColor = ZNetSurfaceDark,
                    unfocusedContainerColor = ZNetSurfaceDark,
                    focusedTextColor = PureWhite,
                    unfocusedTextColor = PureWhite
                ),
                shape = RoundedCornerShape(12.dp),
                trailingIcon = { Icon(Icons.Outlined.Search, contentDescription = null, tint = TextSecondary) },
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
            ) {
                Button(
                    onClick = { /* All Dates */ },
                    colors = ButtonDefaults.buttonColors(containerColor = ZNetSurfaceDark),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text("آخر 30 يوم", color = PureWhite)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Outlined.DateRange, contentDescription = null, tint = PureWhite, modifier = Modifier.size(18.dp))
                }
                
                Button(
                    onClick = { /* All Types */ },
                    colors = ButtonDefaults.buttonColors(containerColor = ZNetSurfaceDark),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text("كل الأنواع", color = PureWhite)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Outlined.Sort, contentDescription = null, tint = PureWhite, modifier = Modifier.size(18.dp))
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            if (filteredOperations.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(androidx.compose.foundation.shape.CircleShape)
                                .background(TealPrimary.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Outlined.ReceiptLong, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(40.dp))
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("لا توجد عمليات مسجلة", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "ستظهر العمليات هنا بمجرد استلام التحويلات أو صرف الكروت",
                            color = TextSecondary,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 32.dp)
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredOperations) { operation ->
                        OperationCard(operation)
                    }
                    item {
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun OperationCard(operation: OperationItem) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("ar"))
    Card(
        colors = CardDefaults.cardColors(containerColor = ZNetSurfaceDark),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (operation.isPositive) StatusGreen.copy(alpha = 0.15f) else Color(0xFFE57373).copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.SyncAlt, 
                    contentDescription = null, 
                    tint = if (operation.isPositive) StatusGreen else Color(0xFFE57373),
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(operation.title, color = PureWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(operation.subtitle, color = TextSecondary, fontSize = 12.sp)
            }
            
            Column(horizontalAlignment = Alignment.Start) {
                Text(
                    text = "${operation.amount} ر.ي", 
                    color = if (operation.isPositive) StatusGreen else PureWhite, 
                    fontSize = 16.sp, 
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(dateFormat.format(Date(operation.date)), color = TextSecondary, fontSize = 10.sp)
            }
        }
    }
}
