package com.example.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.models.DistributorCustomer
import com.example.models.DistributorTransaction
import com.example.models.DistributorExpense
import com.example.models.DistributorCapital
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DistributorSystemScreen(
    viewModel: com.example.ui.DistributorViewModel,
    initialTab: Int = 0,
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val customers by viewModel.distributorCustomers.collectAsState()
    val transactions by viewModel.distributorTransactions.collectAsState()
    val expenses by viewModel.distributorExpenses.collectAsState()
    val capitals by viewModel.distributorCapitals.collectAsState()
    
    var selectedTab by remember { mutableStateOf(initialTab) } // 0: Calculator, 1: Customers, 2: Financials, 3: Reports, 4: Pricing Settings
    
    // Custom prices preferences keys (stored in SharedPrefs)
    val sharedPrefs = remember { context.getSharedPreferences("distributor_prices", Context.MODE_PRIVATE) }
    
    // Load pricing
    val categories = listOf(100, 200, 250, 300, 500, 1000)
    
    // In-memory local prices (category -> (buyPrice, regularSell, proSell))
    var pricesState = remember {
        mutableStateMapOf<Int, Triple<Double, Double, Double>>().apply {
            categories.forEach { cat ->
                val buy = sharedPrefs.getFloat("buy_$cat", when (cat) {
                    100 -> 80f; 200 -> 160f; 250 -> 200f; 300 -> 240f; 500 -> 400f; else -> 800f
                }).toDouble()
                val reg = sharedPrefs.getFloat("reg_$cat", when (cat) {
                    100 -> 85f; 200 -> 170f; 250 -> 210f; 300 -> 260f; 500 -> 420f; else -> 850f
                }).toDouble()
                val pro = sharedPrefs.getFloat("pro_$cat", when (cat) {
                    100 -> 85f; 200 -> 180f; 250 -> 230f; 300 -> 270f; 500 -> 450f; else -> 900f
                }).toDouble()
                put(cat, Triple(buy, reg, pro))
            }
        }
    }

    // Calculator State
    var calcType by remember { mutableStateOf("REGULAR") } // "REGULAR" or "PRO"
    val quantities = remember { mutableStateMapOf<Int, Int>().apply { categories.forEach { put(it, 0) } } }
    var selectedCustomerForCalc by remember { mutableStateOf<DistributorCustomer?>(null) }
    var receivedAmountInput by remember { mutableStateOf("") }
    
    // Computed Values using derivedStateOf to react correctly to SnapShotStateMap entry updates
    val totalAmount by remember {
        derivedStateOf {
            var sum = 0.0
            categories.forEach { cat ->
                val qty = quantities[cat] ?: 0
                val pricePair = pricesState[cat] ?: Triple(0.0, 0.0, 0.0)
                val sellPrice = if (calcType == "REGULAR") pricePair.second else pricePair.third
                sum += qty * sellPrice
            }
            sum
        }
    }

    val totalBuyingCost by remember {
        derivedStateOf {
            var sum = 0.0
            categories.forEach { cat ->
                val qty = quantities[cat] ?: 0
                val pricePair = pricesState[cat] ?: Triple(0.0, 0.0, 0.0)
                sum += qty * pricePair.first
            }
            sum
        }
    }

    val calcProfits by remember {
        derivedStateOf {
            totalAmount - totalBuyingCost
        }
    }

    // Dialog state for completed invoice
    var showInvoiceDialog by remember { mutableStateOf(false) }
    var lastInvoiceText by remember { mutableStateOf("") }
    var lastInvoicePhone by remember { mutableStateOf("") }

    // Tab labels and icons
    val tabItems = listOf(
        Triple("الحاسبة", Icons.Default.Calculate, Icons.Outlined.Calculate),
        Triple("العملاء", Icons.Default.People, Icons.Outlined.People),
        Triple("المالية", Icons.Default.AccountBalance, Icons.Outlined.AccountBalance),
        Triple("التقارير", Icons.Default.BarChart, Icons.Outlined.BarChart),
        Triple("التسعيرة", Icons.Default.PriceChange, Icons.Outlined.PriceChange),
    )

    Scaffold(
        containerColor = DeepBlack,
        topBar = {
            Surface(
                color = Color(0xFF0F172A),
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // زر رجوع / تبديل
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF1E293B))
                            .clickable { onBack() }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.SwapHoriz, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(18.dp))
                            Text("SMS", color = Color(0xFF10B981), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // العنوان
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🏪 نظام الموزع", color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp)
                        Text(tabItems.getOrNull(selectedTab)?.first ?: "", color = Color(0xFF94A3B8), fontSize = 11.sp)
                    }

                    // أيقونة الوضع
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFF10B981).copy(alpha = 0.15f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Store, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(22.dp))
                    }
                }
            }
        },
        bottomBar = {
            Surface(
                color = Color(0xFF0F172A),
                shadowElevation = 8.dp,
                modifier = Modifier.navigationBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    tabItems.forEachIndexed { index, (label, filledIcon, outlinedIcon) ->
                        val isSelected = selectedTab == index
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clickable { selectedTab = index },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(
                                            if (isSelected) Color(0xFF10B981).copy(alpha = 0.15f) else Color.Transparent,
                                            RoundedCornerShape(8.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (isSelected) filledIcon else outlinedIcon,
                                        contentDescription = label,
                                        tint = if (isSelected) Color(0xFF10B981) else Color(0xFF64748B),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = label,
                                    color = if (isSelected) Color(0xFF10B981) else Color(0xFF64748B),
                                    fontSize = 9.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            when (selectedTab) {
                0 -> {
                    // CALCULATOR & SALES TAB
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Toggle Regular / Pro - مبسط
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color(0xFF0F172A))
                                    .padding(4.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                listOf("REGULAR" to "عادي 🟡", "PRO" to "بور ⚡").forEach { (type, label) ->
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(
                                                if (calcType == type) Color(0xFF10B981) else Color.Transparent
                                            )
                                            .clickable { calcType = type }
                                            .padding(vertical = 12.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            label,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                    }
                                }
                            }
                        }

                        // Categories Quantities Inputs - محسّنة
                        item {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                categories.forEach { cat ->
                                    val qty = quantities[cat] ?: 0
                                    val triple = pricesState[cat] ?: Triple(0.0, 0.0, 0.0)
                                    val price = if (calcType == "REGULAR") triple.second else triple.third
                                    val subtotal = qty * price
                                    val hasQty = qty > 0

                                    Card(
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (hasQty) Color(0xFF10B981).copy(alpha = 0.1f) else Color(0xFF0F172A)
                                        ),
                                        shape = RoundedCornerShape(14.dp),
                                        border = BorderStroke(
                                            1.dp,
                                            if (hasQty) Color(0xFF10B981).copy(alpha = 0.4f) else Color.White.copy(alpha = 0.06f)
                                        )
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 14.dp, vertical = 10.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            // Controls + Subtotal
                                            Column(horizontalAlignment = Alignment.Start) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(34.dp)
                                                            .background(
                                                                if (qty > 0) Color(0xFFDC2626) else Color(0xFF1E293B),
                                                                RoundedCornerShape(8.dp)
                                                            )
                                                            .clickable { if (qty > 0) quantities[cat] = qty - 1 },
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Icon(Icons.Default.Remove, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                                    }

                                                    // Text input for direct quantity typing
                                                    var qtyText by remember(qty) { mutableStateOf(if(qty > 0) qty.toString() else "") }
                                                    
                                                    androidx.compose.foundation.text.BasicTextField(
                                                        value = qtyText,
                                                        onValueChange = { newVal ->
                                                            // Only allow digits
                                                            val digitsOnly = newVal.filter { it.isDigit() }
                                                            qtyText = digitsOnly
                                                            val newQty = digitsOnly.toIntOrNull() ?: 0
                                                            quantities[cat] = newQty
                                                        },
                                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                                        textStyle = androidx.compose.ui.text.TextStyle(
                                                            color = if (hasQty) Color(0xFF10B981) else Color.White,
                                                            fontWeight = FontWeight.Black,
                                                            fontSize = 18.sp,
                                                            textAlign = TextAlign.Center
                                                        ),
                                                        modifier = Modifier
                                                            .width(44.dp)
                                                            .background(Color(0xFF0F172A), RoundedCornerShape(6.dp))
                                                            .border(1.dp, if (hasQty) Color(0xFF10B981).copy(alpha = 0.5f) else Color.White.copy(alpha = 0.1f), RoundedCornerShape(6.dp))
                                                            .padding(vertical = 4.dp),
                                                        decorationBox = { innerTextField ->
                                                            Box(contentAlignment = Alignment.Center) {
                                                                if (qtyText.isEmpty()) {
                                                                    Text("0", color = Color.White.copy(alpha = 0.5f), fontSize = 18.sp, fontWeight = FontWeight.Black)
                                                                }
                                                                innerTextField()
                                                            }
                                                        }
                                                    )

                                                    Box(
                                                        modifier = Modifier
                                                            .size(34.dp)
                                                            .background(Color(0xFF10B981), RoundedCornerShape(8.dp))
                                                            .clickable { quantities[cat] = qty + 1 },
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                                    }
                                                }
                                                if (hasQty) {
                                                    Spacer(modifier = Modifier.height(2.dp))
                                                    Text(
                                                        "= ${subtotal.toInt()} ر.ي",
                                                        color = Color(0xFF10B981),
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }

                                            // Info
                                            Column(horizontalAlignment = Alignment.End) {
                                                Text(
                                                    "فئة $cat ر.ي",
                                                    color = Color.White,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 15.sp
                                                )
                                                Text(
                                                    "السعر: ${price.toInt()} ر.ي",
                                                    color = Color(0xFF94A3B8),
                                                    fontSize = 12.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }


                        // Select Customer Shop Dropdown
                        item {
                            var isCustomerMenuOpen by remember { mutableStateOf(false) }
                            
                            Column(
                                horizontalAlignment = Alignment.End,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("اختر بقالة / عميل الموزع لتقييد الدين:", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Spacer(modifier = Modifier.height(6.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFF0F172A))
                                        .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)), RoundedCornerShape(12.dp))
                                        .clickable { isCustomerMenuOpen = true }
                                        .padding(14.dp),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            text = selectedCustomerForCalc?.name ?: "البيع كاش مباشر (بدون تقييد)",
                                            color = if (selectedCustomerForCalc == null) TextSecondary else Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = Color.White)
                                    }
                                    
                                    DropdownMenu(
                                        expanded = isCustomerMenuOpen,
                                        onDismissRequest = { isCustomerMenuOpen = false },
                                        modifier = Modifier.background(Color(0xFF0F172A))
                                    ) {
                                        DropdownMenuItem(
                                            text = { Text("البيع كاش مباشر (بدون تقييد)", color = Color.White) },
                                            onClick = {
                                                selectedCustomerForCalc = null
                                                isCustomerMenuOpen = false
                                            }
                                        )
                                        customers.forEach { cust ->
                                            DropdownMenuItem(
                                                text = { Text(cust.name, color = Color.White) },
                                                onClick = {
                                                    selectedCustomerForCalc = cust
                                                    isCustomerMenuOpen = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Payment received and change
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp),
                                    horizontalAlignment = Alignment.End
                                ) {
                                    Text("بيانات الدفع والتحصيل", color = BrandPrimaryRed, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    
                                    OutlinedTextField(
                                        value = receivedAmountInput,
                                        onValueChange = { receivedAmountInput = it },
                                        label = { Text("المبلغ المستلم كاش") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = BrandPrimaryRed,
                                            unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                                            focusedTextColor = Color.White,
                                            unfocusedTextColor = Color.White
                                        ),
                                        modifier = Modifier.fillMaxWidth(),
                                        textStyle = TextAlign.Right.let { androidx.compose.ui.text.TextStyle(textAlign = TextAlign.Right) }
                                    )

                                    val received = receivedAmountInput.toDoubleOrNull() ?: 0.0
                                    val change = received - totalAmount
                                    val debtValue = totalAmount - received

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = if (change > 0) "$change ر.ي" else "0 ر.ي",
                                            color = GlowEmeraldGreen,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                        Text("الفائض المرتجع للعميل:", color = TextSecondary, fontSize = 12.sp)
                                    }

                                    if (debtValue > 0 && selectedCustomerForCalc != null) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "$debtValue ر.ي",
                                                color = Color.Red,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp
                                            )
                                            Text("مبلغ الدين المتقيد بحسابه:", color = TextSecondary, fontSize = 12.sp)
                                        }
                                    }
                                }
                            }
                        }

                        // Summary Statistics
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = BrandPrimaryRed.copy(alpha = 0.15f)),
                                border = BorderStroke(1.dp, BrandPrimaryRed.copy(alpha = 0.3f)),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    horizontalAlignment = Alignment.End
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("${totalBuyingCost} ر.ي", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text("تكلفة الشراء الكلية:", color = TextSecondary, fontSize = 12.sp)
                                    }
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("${calcProfits} ر.ي", color = GlowEmeraldGreen, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                        Text("صافي أرباح الفاتورة الكلي:", color = TextSecondary, fontSize = 12.sp)
                                    }
                                    Divider(color = Color.White.copy(alpha = 0.05f))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("${totalAmount} ر.ي", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                                        Text("إجمالي الفاتورة المطلوب:", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    }
                                }
                            }
                        }

                        // Save Invoice / Record Sale Button
                        item {
                            Button(
                                onClick = {
                                    if (totalAmount <= 0) {
                                        Toast.makeText(context, "الرجاء اختيار كروت للبيع أولاً!", Toast.LENGTH_SHORT).show()
                                        return@Button
                                    }

                                    // Create formatted slip
                                    val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
                                    val shopName = selectedCustomerForCalc?.name ?: "زبون نقدي مباشر"
                                    
                                    val invoiceDetails = buildString {
                                        appendLine("===============================")
                                        appendLine("         فاتورة مبيعات كروت         ")
                                        appendLine("===============================")
                                        appendLine("التاريخ: $dateStr")
                                        appendLine("العميل: $shopName")
                                        appendLine("-------------------------------")
                                        categories.forEach { cat ->
                                            val qty = quantities[cat] ?: 0
                                            if (qty > 0) {
                                                val triple = pricesState[cat] ?: Triple(0.0, 0.0, 0.0)
                                                val sellPrice = if (calcType == "REGULAR") triple.second else triple.third
                                                appendLine("• فئة $cat: $qty كرت × $sellPrice = ${qty * sellPrice} ر.ي")
                                            }
                                        }
                                        appendLine("-------------------------------")
                                        appendLine("الإجمالي الكلي: $totalAmount ر.ي")
                                        val received = receivedAmountInput.toDoubleOrNull() ?: 0.0
                                        appendLine("المدفوع نقداً: $received ر.ي")
                                        if (received < totalAmount) {
                                            appendLine("المتبقي كدين: ${totalAmount - received} ر.ي")
                                        } else {
                                            appendLine("الفائض المرتجع: ${received - totalAmount} ر.ي")
                                        }
                                        appendLine("===============================")
                                        appendLine("  شكراً لتعاملكم معنا - شبكة الدحشة  ")
                                    }

                                    // Save transaction in database using consolidated transaction logic
                                    val customerId = selectedCustomerForCalc?.id ?: "CASH"
                                    val paid = receivedAmountInput.toDoubleOrNull() ?: 0.0

                                    viewModel.performDistributorSale(
                                        customerId = customerId,
                                        quantities = quantities.toMap(),
                                        totalAmount = totalAmount,
                                        totalBuyingCost = totalBuyingCost,
                                        calcProfits = calcProfits,
                                        receivedAmount = paid,
                                        onComplete = { success, msg ->
                                            if (success) {
                                                // Reset quantities
                                                categories.forEach { quantities[it] = 0 }
                                                receivedAmountInput = ""
                                                lastInvoiceText = invoiceDetails
                                                showInvoiceDialog = true
                                                Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                                            } else {
                                                Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                                            }
                                        }
                                    )
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimaryRed),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                            ) {
                                Icon(Icons.Default.Receipt, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("حفظ الفاتورة وتأكيد البيع 🧾", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }
                        }
                    }
                }

                1 -> {
                    // CUSTOMERS / SHOPS TAB
                    var newCustomerName by remember { mutableStateOf("") }
                    var searchQuery by remember { mutableStateOf("") }
                    var activeCustomerForStatement by remember { mutableStateOf<DistributorCustomer?>(null) }
                    var paymentAmountInput by remember { mutableStateOf("") }
                    var paymentNotesInput by remember { mutableStateOf("") }
                    var showAddPaymentDialog by remember { mutableStateOf(false) }

                    val filteredCustomers = remember(customers, searchQuery) {
                        customers.filter { it.name.contains(searchQuery) }
                    }

                    if (activeCustomerForStatement != null) {
                        // CUSTOMER ACCOUNT STATEMENT VIEW (كشف حساب العميل)
                        val customerTx = transactions.filter { it.customerId == activeCustomerForStatement!!.id }
                        
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Button(
                                        onClick = { activeCustomerForStatement = null },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A)),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("رجوع للقائمة", color = Color.White, fontSize = 12.sp)
                                    }

                                    Text(
                                        text = "كشف حساب: ${activeCustomerForStatement!!.name}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = Color.White
                                    )
                                }
                            }

                            // Summary balance
                            item {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        horizontalAlignment = Alignment.End,
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            text = "إجمالي ديون العميل: ${activeCustomerForStatement!!.currentBalance} ر.ي",
                                            color = if (activeCustomerForStatement!!.currentBalance > 0) Color.Red else GlowEmeraldGreen,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        )
                                        Text("إجمالي المبيعات الآجلة: ${activeCustomerForStatement!!.totalSales} ر.ي", color = TextSecondary, fontSize = 12.sp)
                                        Text("إجمالي السداد النقدي: ${activeCustomerForStatement!!.totalPayments} ر.ي", color = TextSecondary, fontSize = 12.sp)
                                        
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Button(
                                                onClick = { showAddPaymentDialog = true },
                                                colors = ButtonDefaults.buttonColors(containerColor = GlowEmeraldGreen),
                                                modifier = Modifier.weight(1f),
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Icon(Icons.Default.Payment, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("تسجيل سداد 💵", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            }

                                            Button(
                                                onClick = {
                                                    // Share account statement
                                                    val statementReport = buildString {
                                                        appendLine("===============================")
                                                        appendLine("     كشف حساب عميل الموزع     ")
                                                        appendLine("===============================")
                                                        appendLine("العميل: ${activeCustomerForStatement!!.name}")
                                                        appendLine("إجمالي المبيعات: ${activeCustomerForStatement!!.totalSales} ر.ي")
                                                        appendLine("إجمالي المسدد: ${activeCustomerForStatement!!.totalPayments} ر.ي")
                                                        appendLine("الرصيد المتبقي (دين): ${activeCustomerForStatement!!.currentBalance} ر.ي")
                                                        appendLine("-------------------------------")
                                                        customerTx.forEachIndexed { i, tx ->
                                                            val typeStr = if (tx.type == "sale") "مبيعات" else "سداد نقدي"
                                                            val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(tx.date))
                                                            appendLine("${i+1}. [$typeStr] بمبلغ ${tx.amount} ر.ي")
                                                            appendLine("   التاريخ: $dateStr")
                                                            if (tx.notes.isNotEmpty()) appendLine("   ملاحظة: ${tx.notes}")
                                                            appendLine("-------------------------------")
                                                        }
                                                        appendLine("===============================")
                                                    }
                                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                                    val clip = android.content.ClipData.newPlainText("Statement", statementReport)
                                                    clipboard.setPrimaryClip(clip)
                                                    Toast.makeText(context, "تم نسخ كشف الحساب بالتفصيل لمشاركته! 📋", Toast.LENGTH_LONG).show()
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
                                                modifier = Modifier.weight(1f),
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Icon(Icons.Default.Share, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("نسخ الكشف 📋", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }

                            // Transactions History
                            if (customerTx.isEmpty()) {
                                item {
                                    Text("لا توجد قيود مبيعات أو سندات سداد لهذا العميل حالياً.", color = TextSecondary, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                                }
                            } else {
                                items(customerTx) { tx ->
                                    val isSale = tx.type == "sale"
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(12.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            IconButton(
                                                onClick = {
                                                    viewModel.deleteDistributorTransaction(tx.id, tx.customerId)
                                                    Toast.makeText(context, "تم حذف المعاملة وتحديث كشف الحساب.", Toast.LENGTH_SHORT).show()
                                                },
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .background(Color.Red.copy(alpha = 0.1f), CircleShape)
                                            ) {
                                                Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red, modifier = Modifier.size(16.dp))
                                            }

                                            Column(horizontalAlignment = Alignment.End) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    Text(
                                                        text = if (isSale) "مبيعات" else "سداد نقدي",
                                                        color = if (isSale) Color.Red else GlowEmeraldGreen,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 13.sp
                                                    )
                                                    Text(
                                                        text = "${tx.amount} ر.ي",
                                                        color = Color.White,
                                                        fontWeight = FontWeight.Black,
                                                        fontSize = 14.sp
                                                    )
                                                }
                                                Text(
                                                    text = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(tx.date)),
                                                    color = TextSecondary,
                                                    fontSize = 10.sp
                                                )
                                                if (tx.notes.isNotEmpty()) {
                                                    Text(
                                                        text = tx.notes,
                                                        color = TextSecondary.copy(alpha = 0.8f),
                                                        fontSize = 11.sp
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Add Payment Dialog inside Statement screen
                        if (showAddPaymentDialog) {
                            AlertDialog(
                                onDismissRequest = { showAddPaymentDialog = false },
                                title = { Text("تسجيل سند قبض / دفعة نقدية", fontWeight = FontWeight.Bold, color = Color.White) },
                                text = {
                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(12.dp),
                                        horizontalAlignment = Alignment.End
                                    ) {
                                        OutlinedTextField(
                                            value = paymentAmountInput,
                                            onValueChange = { paymentAmountInput = it },
                                            label = { Text("المبلغ النقدي المسدد") },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = BrandPrimaryRed,
                                                unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                                                focusedTextColor = Color.White,
                                                unfocusedTextColor = Color.White
                                            )
                                        )

                                        OutlinedTextField(
                                            value = paymentNotesInput,
                                            onValueChange = { paymentNotesInput = it },
                                            label = { Text("ملاحظات / رقم السند") },
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = BrandPrimaryRed,
                                                unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                                                focusedTextColor = Color.White,
                                                unfocusedTextColor = Color.White
                                            )
                                        )
                                    }
                                },
                                confirmButton = {
                                    Button(
                                        onClick = {
                                            val amount = paymentAmountInput.toDoubleOrNull() ?: 0.0
                                            if (amount <= 0) {
                                                Toast.makeText(context, "الرجاء إدخال مبلغ صحيح!", Toast.LENGTH_SHORT).show()
                                                return@Button
                                            }
                                            viewModel.insertDistributorTransaction(
                                                customerId = activeCustomerForStatement!!.id,
                                                type = "payment",
                                                amount = amount,
                                                notes = paymentNotesInput.ifEmpty { "دفعة نقدية مستلمة" }
                                            )
                                            paymentAmountInput = ""
                                            paymentNotesInput = ""
                                            showAddPaymentDialog = false
                                            Toast.makeText(context, "🟢 تم تسجيل السداد بنجاح!", Toast.LENGTH_SHORT).show()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = GlowEmeraldGreen)
                                    ) {
                                        Text("تأكيد وحفظ السند", color = Color.White)
                                    }
                                },
                                dismissButton = {
                                    TextButton(onClick = { showAddPaymentDialog = false }) {
                                        Text("إلغاء", color = Color.White)
                                    }
                                },
                                containerColor = Color(0xFF0F172A)
                            )
                        }
                    } else {
                        // NORMAL CUSTOMER LIST
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Add New Customer Form
                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Button(
                                        onClick = {
                                            val name = newCustomerName.trim()
                                            if (name.isEmpty()) {
                                                Toast.makeText(context, "يرجى كتابة اسم البقالة!", Toast.LENGTH_SHORT).show()
                                                return@Button
                                            }
                                            viewModel.insertDistributorCustomer(name)
                                            newCustomerName = ""
                                            Toast.makeText(context, "تم إضافة البقالة بنجاح!", Toast.LENGTH_SHORT).show()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimaryRed),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text("إضافة بقالة", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }

                                    OutlinedTextField(
                                        value = newCustomerName,
                                        onValueChange = { newCustomerName = it },
                                        label = { Text("اسم البقالة / العميل الجديد") },
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = BrandPrimaryRed,
                                            unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                                            focusedTextColor = Color.White,
                                            unfocusedTextColor = Color.White
                                        ),
                                        modifier = Modifier.weight(1f),
                                        textStyle = TextAlign.Right.let { androidx.compose.ui.text.TextStyle(textAlign = TextAlign.Right) }
                                    )
                                }
                            }

                            // Search Field
                            item {
                                OutlinedTextField(
                                    value = searchQuery,
                                    onValueChange = { searchQuery = it },
                                    label = { Text("بحث عن بقالة / عميل بالاسم") },
                                    trailingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.White) },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = BrandPrimaryRed,
                                        unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    ),
                                    modifier = Modifier.fillMaxWidth(),
                                    textStyle = TextAlign.Right.let { androidx.compose.ui.text.TextStyle(textAlign = TextAlign.Right) }
                                )
                            }

                            if (filteredCustomers.isEmpty()) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 40.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("لا يوجد بقالات مضافة حالياً. أضف اسماً للبدء!", color = TextSecondary, fontSize = 13.sp)
                                    }
                                }
                            } else {
                                items(filteredCustomers) { cust ->
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
                                        shape = RoundedCornerShape(16.dp),
                                        modifier = Modifier.clickable { activeCustomerForStatement = cust }
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(14.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            // Delete Button
                                            IconButton(
                                                onClick = {
                                                    viewModel.deleteDistributorCustomer(cust.id)
                                                    Toast.makeText(context, "تم حذف العميل وكشف حسابه بنجاح.", Toast.LENGTH_SHORT).show()
                                                },
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .background(Color.Red.copy(alpha = 0.1f), CircleShape)
                                            ) {
                                                Icon(Icons.Default.Delete, contentDescription = "حذف العميل", tint = Color.Red, modifier = Modifier.size(16.dp))
                                            }

                                            // Customer Info
                                            Column(horizontalAlignment = Alignment.End) {
                                                Text(
                                                    text = cust.name,
                                                    color = Color.White,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 15.sp
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = "صافي الدين: ${cust.currentBalance} ر.ي",
                                                    color = if (cust.currentBalance > 0) Color.Red else GlowEmeraldGreen,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 13.sp
                                                )
                                                Row(
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    Text("سداد: ${cust.totalPayments}", color = TextSecondary, fontSize = 10.sp)
                                                    Text("مبيعات: ${cust.totalSales}", color = TextSecondary, fontSize = 10.sp)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // FINANCIALS & EXPENSES TAB
                    var selectedExpenseCategory by remember { mutableStateOf("وقود (Fuel)") }
                    var expenseAmountInput by remember { mutableStateOf("") }
                    var expenseDescriptionInput by remember { mutableStateOf("") }

                    var capitalType by remember { mutableStateOf("deposit") } // "deposit" or "withdraw"
                    var capitalAmountInput by remember { mutableStateOf("") }
                    var capitalDescriptionInput by remember { mutableStateOf("") }

                    val totalExpensesSum = remember(expenses) { expenses.sumOf { it.amount } }
                    val totalCapitalsSum = remember(capitals) { 
                        capitals.sumOf { if (it.type == "deposit") it.amount else -it.amount } 
                    }
                    val totalOutstandingDebts = remember(customers) { customers.sumOf { it.currentBalance } }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Financial statistics summary
                        item {
                            Text("بطاقة الأداء والمركز المالي الذكي للموزع 📊", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("رأس المال", color = TextSecondary, fontSize = 11.sp)
                                        Text("${totalCapitalsSum} ر.ي", color = GlowOrangeGold, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    }
                                }
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("ديون بقالات", color = TextSecondary, fontSize = 11.sp)
                                        Text("${totalOutstandingDebts} ر.ي", color = Color.Red, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    }
                                }
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("المصروفات", color = TextSecondary, fontSize = 11.sp)
                                        Text("${totalExpensesSum} ر.ي", color = Color.Red, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    }
                                }
                            }
                        }

                        // Add Expense Form
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp),
                                    horizontalAlignment = Alignment.End
                                ) {
                                    Text("تسجيل مصروف جديد", color = BrandPrimaryRed, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    
                                    // Expense Category Selection dropdown
                                    var isExpenseMenuOpen by remember { mutableStateOf(false) }
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color.White.copy(alpha = 0.05f))
                                            .clickable { isExpenseMenuOpen = true }
                                            .padding(12.dp),
                                        contentAlignment = Alignment.CenterEnd
                                    ) {
                                        Text(selectedExpenseCategory, color = Color.White, fontWeight = FontWeight.Bold)
                                        DropdownMenu(
                                            expanded = isExpenseMenuOpen,
                                            onDismissRequest = { isExpenseMenuOpen = false },
                                            modifier = Modifier.background(Color(0xFF0F172A))
                                        ) {
                                            listOf("وقود (Fuel)", "إيجار (Rent)", "رواتب (Salaries)", "صيانة (Maintenance)", "نثرية أخرى (Other)").forEach { cat ->
                                                DropdownMenuItem(
                                                    text = { Text(cat, color = Color.White) },
                                                    onClick = {
                                                        selectedExpenseCategory = cat
                                                        isExpenseMenuOpen = false
                                                    }
                                                )
                                            }
                                        }
                                    }

                                    OutlinedTextField(
                                        value = expenseAmountInput,
                                        onValueChange = { expenseAmountInput = it },
                                        label = { Text("مبلغ المصروف") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = BrandPrimaryRed,
                                            unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                                            focusedTextColor = Color.White,
                                            unfocusedTextColor = Color.White
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    OutlinedTextField(
                                        value = expenseDescriptionInput,
                                        onValueChange = { expenseDescriptionInput = it },
                                        label = { Text("تفاصيل وبيان المصروف") },
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = BrandPrimaryRed,
                                            unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                                            focusedTextColor = Color.White,
                                            unfocusedTextColor = Color.White
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    Button(
                                        onClick = {
                                            val amount = expenseAmountInput.toDoubleOrNull() ?: 0.0
                                            if (amount <= 0) {
                                                Toast.makeText(context, "يرجى كتابة مبلغ صحيح للمصروف!", Toast.LENGTH_SHORT).show()
                                                return@Button
                                            }
                                            viewModel.insertDistributorExpense(
                                                category = selectedExpenseCategory,
                                                amount = amount,
                                                description = expenseDescriptionInput.ifEmpty { "مصروف تشغيلي" }
                                            )
                                            expenseAmountInput = ""
                                            expenseDescriptionInput = ""
                                            Toast.makeText(context, "🟢 تم تسجيل المصروف بنجاح!", Toast.LENGTH_SHORT).show()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimaryRed),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("حفظ وتسجيل المصروف", color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        // Expenses History List
                        if (expenses.isNotEmpty()) {
                            item {
                                Text("تاريخ المصروفات المسجلة:", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                            items(expenses) { exp ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        IconButton(onClick = { viewModel.deleteDistributorExpense(exp.id) }) {
                                            Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red)
                                        }

                                        Column(horizontalAlignment = Alignment.End) {
                                            Text(exp.category, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                            Text("${exp.amount} ر.ي", color = Color.Red, fontWeight = FontWeight.Black, fontSize = 14.sp)
                                            Text(exp.description, color = TextSecondary, fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                3 -> {
                    // REPORTS TAB
                    val totalOutstandingDebts = remember(customers) { customers.sumOf { it.currentBalance } }
                    val totalSalesSum = remember(transactions) { 
                        transactions.filter { it.type == "sale" }.sumOf { it.amount } 
                    }
                    val totalPaymentsSum = remember(transactions) { 
                        transactions.filter { it.type == "payment" }.sumOf { it.amount } 
                    }
                    val totalExpensesSum = remember(expenses) { expenses.sumOf { it.amount } }

                    val salesData = remember(transactions) {
                        var realCost = 0.0
                        var realProfit = 0.0
                        transactions.filter { it.type == "sale" }.forEach { tx ->
                            val notes = tx.notes
                            if (notes.contains("التكلفة:") && notes.contains("الأرباح:")) {
                                try {
                                    val costStr = notes.substringAfter("التكلفة:").substringBefore("|").trim()
                                    val profitStr = notes.substringAfter("الأرباح:").substringBefore("|").trim()
                                    realCost += costStr.toDoubleOrNull() ?: 0.0
                                    realProfit += profitStr.toDoubleOrNull() ?: 0.0
                                } catch (e: Exception) {
                                    realProfit += tx.amount * 0.05
                                    realCost += tx.amount * 0.95
                                }
                            } else {
                                realProfit += tx.amount * 0.05
                                realCost += tx.amount * 0.95
                            }
                        }
                        Pair(realCost, realProfit)
                    }
                    val totalRealProfits = salesData.second
                    val netProfit = totalRealProfits - totalExpensesSum

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        item {
                            Text("ملخص وتقارير الأرباح والتحصيل العام 📈", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }

                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp),
                                    horizontalAlignment = Alignment.End
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("${totalSalesSum} ر.ي", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text("إجمالي المبيعات الكلية للموزع:", color = TextSecondary, fontSize = 12.sp)
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("${salesData.first} ر.ي", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text("إجمالي التكلفة الفعلية للمشتريات:", color = TextSecondary, fontSize = 12.sp)
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("${totalRealProfits} ر.ي", color = GlowEmeraldGreen, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text("إجمالي الأرباح الكلية المحققة:", color = TextSecondary, fontSize = 12.sp)
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("${totalPaymentsSum} ر.ي", color = GlowEmeraldGreen, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text("إجمالي المبالغ النقدية المحصلة:", color = TextSecondary, fontSize = 12.sp)
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("${totalOutstandingDebts} ر.ي", color = Color.Red, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text("إجمالي الديون المعلقة بالخارج:", color = TextSecondary, fontSize = 12.sp)
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("${totalExpensesSum} ر.ي", color = Color.Red, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text("إجمالي المصاريف والعمولات التشغيلية:", color = TextSecondary, fontSize = 12.sp)
                                    }

                                    Divider(color = Color.White.copy(alpha = 0.05f))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "${netProfit} ر.ي",
                                            color = if (netProfit > 0) GlowEmeraldGreen else Color.Red,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 18.sp
                                        )
                                        Text("صافي الأرباح الفعلية بعد تصفية المصاريف:", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }
                                }
                            }
                        }

                        item {
                            Button(
                                onClick = {
                                    val dailyReport = buildString {
                                        appendLine("===============================")
                                        appendLine("   تقرير مبيعات وأرباح الموزع اليومي   ")
                                        appendLine("===============================")
                                        appendLine("التاريخ: " + SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date()))
                                        appendLine("إجمالي المبيعات: $totalSalesSum ر.ي")
                                        appendLine("إجمالي تكلفة المشتريات: ${salesData.first} ر.ي")
                                        appendLine("إجمالي الأرباح الكلية: $totalRealProfits ر.ي")
                                        appendLine("المبالغ المحصلة كاش: $totalPaymentsSum ر.ي")
                                        appendLine("الديون المتبقية: $totalOutstandingDebts ر.ي")
                                        appendLine("المصاريف والعمولات: $totalExpensesSum ر.ي")
                                        appendLine("صافي الأرباح المحققة بعد المصاريف: $netProfit ر.ي")
                                        appendLine("===============================")
                                    }
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                    val clip = android.content.ClipData.newPlainText("Daily Distributor Report", dailyReport)
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(context, "تم توليد التقرير المالي ونسخه لمشاركته بنجاح! 📋", Toast.LENGTH_LONG).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimaryRed),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().height(48.dp)
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("تصدير ومشاركة التقرير المالي الكلي 📋", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                4 -> {
                    // PRICING SETTINGS TAB
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        item {
                            Text("تعديل تسعيرة الموزع ( Regular & PRO Pricing ) ⚙️", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("تعديل أسعار الكروت للموزعين. سعر الشراء (التكلفة عليك) يحدد صافي الأرباح لكل فئة بشكل دقيق.", color = TextSecondary, fontSize = 11.sp, lineHeight = 16.sp)
                        }

                        items(categories) { cat ->
                            var triple = pricesState[cat] ?: Triple(0.0, 0.0, 0.0)
                            var buyPriceInput by remember(triple) { mutableStateOf(triple.first.toInt().toString()) }
                            var regPriceInput by remember(triple) { mutableStateOf(triple.second.toInt().toString()) }
                            var proPriceInput by remember(triple) { mutableStateOf(triple.third.toInt().toString()) }

                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text("فئة $cat ر.ي", color = BrandPrimaryRed, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Right)
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        // Buy
                                        OutlinedTextField(
                                            value = buyPriceInput,
                                            onValueChange = {
                                                buyPriceInput = it
                                                val buyVal = it.toDoubleOrNull() ?: triple.first
                                                pricesState[cat] = Triple(buyVal, triple.second, triple.third)
                                                sharedPrefs.edit().putFloat("buy_$cat", buyVal.toFloat()).apply()
                                            },
                                            label = { Text("سعر الشراء") },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = BrandPrimaryRed,
                                                unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                                                focusedTextColor = Color.White,
                                                unfocusedTextColor = Color.White
                                            ),
                                            modifier = Modifier.weight(1f)
                                        )

                                        // Regular
                                        OutlinedTextField(
                                            value = regPriceInput,
                                            onValueChange = {
                                                regPriceInput = it
                                                val regVal = it.toDoubleOrNull() ?: triple.second
                                                pricesState[cat] = Triple(triple.first, regVal, triple.third)
                                                sharedPrefs.edit().putFloat("reg_$cat", regVal.toFloat()).apply()
                                            },
                                            label = { Text("بيع عادي") },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = BrandPrimaryRed,
                                                unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                                                focusedTextColor = Color.White,
                                                unfocusedTextColor = Color.White
                                            ),
                                            modifier = Modifier.weight(1f)
                                        )

                                        // PRO
                                        OutlinedTextField(
                                            value = proPriceInput,
                                            onValueChange = {
                                                proPriceInput = it
                                                val proVal = it.toDoubleOrNull() ?: triple.third
                                                pricesState[cat] = Triple(triple.first, triple.second, proVal)
                                                sharedPrefs.edit().putFloat("pro_$cat", proVal.toFloat()).apply()
                                            },
                                            label = { Text("بيع بور (Pro)") },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = BrandPrimaryRed,
                                                unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                                                focusedTextColor = Color.White,
                                                unfocusedTextColor = Color.White
                                            ),
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            } // closes when

        } // closes Box
    } // closes Scaffold

    // Invoice Sharing / Print dialogue
    if (showInvoiceDialog) {
        AlertDialog(
            onDismissRequest = { showInvoiceDialog = false },
            title = { Text("مشاركة وطباعة الفاتورة 🧾", fontWeight = FontWeight.Bold, color = Color.White) },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = lastInvoiceText,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    )
                }
            },
            confirmButton = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                            val clip = android.content.ClipData.newPlainText("Distributor Invoice", lastInvoiceText)
                            clipboard.setPrimaryClip(clip)
                            
                            // Share via WhatsApp
                            try {
                                val url = "https://wa.me/?text=" + Uri.encode(lastInvoiceText)
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "لم يتم العثور على تطبيق واتساب لمشاركة الفاتورة!", Toast.LENGTH_SHORT).show()
                            }
                            showInvoiceDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GlowEmeraldGreen),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("مشاركة عبر الواتساب 🚀", color = Color.White)
                    }

                    Button(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                            val clip = android.content.ClipData.newPlainText("Distributor Invoice", lastInvoiceText)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "تم نسخ نص الفاتورة بنجاح! جاهز للطباعة 📋", Toast.LENGTH_SHORT).show()
                            showInvoiceDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimaryRed),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("نسخ الفاتورة فقط 📋", color = Color.White)
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showInvoiceDialog = false }) {
                    Text("إغلاق", color = Color.White)
                }
            },
            containerColor = Color(0xFF0F172A)
        )
    }
}
