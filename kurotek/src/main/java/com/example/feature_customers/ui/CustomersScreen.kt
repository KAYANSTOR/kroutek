package com.example.feature_customers.ui
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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpecialCustomersTab(viewModel: MainViewModel) {
    val context = LocalContext.current
    val allMappings by viewModel.allMappings.collectAsState()
    val allTransactions by viewModel.allTransactions.collectAsState()
    val isDark by viewModel.isDarkTheme.collectAsState()

    var customerName by remember { mutableStateOf("") }
    var customerUniqueId by remember { mutableStateOf("") }
    var basicPhone by remember { mutableStateOf("") }
    var walletType by remember { mutableStateOf("جيب") }

    val walletOptions = listOf("جيب", "جوالي", "كريمي", "حاسب", "ون كاش", "ام فلوس")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.End
    ) {
        // Title block
        item {
            Column(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "العملاء الاستثنائيين 👑",
                    color = if (isDark) GlowPurplePink else Color(0xFF7B1FA2),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Right
                )
                Text(
                    text = "قم بإضافة وتوجيه حسابات العملاء الذين يرسلون دفعات بأرقام محافظ مجهولة لتصل لرقمه الشخصي تلقائياً.",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Right,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        // Add Customer Form Card
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
                        text = "إضافة عميل جديد 👤",
                        color = if (isDark) GlowPurplePink else Color(0xFF7B1FA2),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )

                    // Partner/Customer Name
                    OutlinedTextField(
                        value = customerName,
                        onValueChange = { customerName = it },
                        label = { Text("اسم العميل / الزبون") },
                        placeholder = { Text("مثال: احمد جابر حسن") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("special_cust_name"),
                        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Right, color = PureWhite),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (isDark) GlowPurplePink else Color(0xFF7B1FA2),
                            unfocusedBorderColor = TextSecondary.copy(alpha = 0.25f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )

                    // Wallet Type selection via customizable Chips (Material 3 style)
                    Text(
                        text = "نوع المحفظة:",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        walletOptions.take(3).forEach { option ->
                            FilterChip(
                                selected = walletType == option,
                                onClick = { walletType = option },
                                label = { Text(if (option == "جيب") "محفظة كاش" else option, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                shape = RoundedCornerShape(22.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = (if (isDark) GlowPurplePink else Color(0xFF7B1FA2)).copy(alpha = 0.15f),
                                    selectedLabelColor = if (isDark) GlowPurplePink else Color(0xFF7B1FA2),
                                    containerColor = SurfaceDark,
                                    labelColor = TextSecondary
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = walletType == option,
                                    borderColor = if (walletType == option) (if (isDark) GlowPurplePink else Color(0xFF7B1FA2)) else TextSecondary.copy(alpha = 0.2f),
                                    selectedBorderColor = if (isDark) GlowPurplePink else Color(0xFF7B1FA2),
                                    borderWidth = 1.dp,
                                    selectedBorderWidth = 1.5.dp
                                )
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        walletOptions.drop(3).forEach { option ->
                            FilterChip(
                                selected = walletType == option,
                                onClick = { walletType = option },
                                label = { Text(if (option == "جيب") "محفظة كاش" else option, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                shape = RoundedCornerShape(22.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = (if (isDark) GlowPurplePink else Color(0xFF7B1FA2)).copy(alpha = 0.15f),
                                    selectedLabelColor = if (isDark) GlowPurplePink else Color(0xFF7B1FA2),
                                    containerColor = SurfaceDark,
                                    labelColor = TextSecondary
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = walletType == option,
                                    borderColor = if (walletType == option) (if (isDark) GlowPurplePink else Color(0xFF7B1FA2)) else TextSecondary.copy(alpha = 0.2f),
                                    selectedBorderColor = if (isDark) GlowPurplePink else Color(0xFF7B1FA2),
                                    borderWidth = 1.dp,
                                    selectedBorderWidth = 1.5.dp
                                )
                            )
                        }
                    }

                    // Account Unique ID / Wallet character code
                    OutlinedTextField(
                        value = customerUniqueId,
                        onValueChange = { customerUniqueId = it },
                        label = { Text("رمز الحساب أو معرّف المحفضه للزبون") },
                        placeholder = { Text("مثال: 120025 أو الاسم كما in الكريمي") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("special_cust_wallet_id"),
                        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Right, color = PureWhite),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (isDark) GlowPurplePink else Color(0xFF7B1FA2),
                            unfocusedBorderColor = TextSecondary.copy(alpha = 0.25f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )

                    // Target / Basic Personal Phone Number
                    OutlinedTextField(
                        value = basicPhone,
                        onValueChange = { basicPhone = it },
                        label = { Text("رقم هاتفه الأساسي الشخصي للمطابقة") },
                        placeholder = { Text("مثال: 770118275") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("special_cust_phone_id"),
                        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Right, color = PureWhite),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (isDark) GlowPurplePink else Color(0xFF7B1FA2),
                            unfocusedBorderColor = TextSecondary.copy(alpha = 0.25f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )

                    // Add Customer Button styled with PurplePinkGradient
                    Button(
                        onClick = {
                            if (customerName.trim().isEmpty() || customerUniqueId.trim().isEmpty() || basicPhone.trim().isEmpty()) {
                                Toast.makeText(context, "الرجاء تعبئة كافة بيانات العميل المطلوب أولاً!", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            viewModel.insertMapping(
                                customerUniqueId = customerUniqueId.trim(),
                                basicPhone = basicPhone.trim(),
                                customerName = customerName.trim(),
                                walletType = walletType
                            )
                            customerName = ""
                            customerUniqueId = ""
                            basicPhone = ""
                            Toast.makeText(context, "تم إضافة العميل الاستثنائي وتنشيط المطابقة بنجاح! 🎉", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Color.White),
                        contentPadding = PaddingValues(),
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(PurplePinkGradient),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("إضافة وحفظ العميل", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }
        }

        // Active Special Customers list header
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Export CSV Button with Emerald Green Gradient
                Button(
                    onClick = {
                        exportCustomerTransactionsToCsv(context, allMappings, allTransactions)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Color.White),
                    contentPadding = PaddingValues(),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .height(42.dp)
                        .testTag("export_csv_button")
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .background(EmeraldGreenGradient)
                            .padding(horizontal = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Outlined.Share, contentDescription = "تصدير CSV", modifier = Modifier.size(16.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("تصدير الحسابات (CSV)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }

                Text(
                    text = "قائمة العملاء الاستثنائيين النشطة 📂 (${allMappings.size})",
                    color = TextSecondary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                )
            }
        }

        if (allMappings.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, if (isDark) Color(0xFF2D2D2D) else Color(0x0A000000)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "لا يوجد أي زبائن استثنائيين مضافين حالياً.",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(24.dp)
                    )
                }
            }
        } else {
            items(allMappings.size) { index ->
                val mapping = allMappings[index]
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                    border = BorderStroke(1.dp, if (isDark) Color(0xFF2D2D2D) else Color(0x0A000000)),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = if (isDark) 0.dp else 2.dp),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Delete Button (Outlined)
                        IconButton(
                            onClick = { viewModel.deleteMapping(mapping.id) }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = "حذف العميل",
                                tint = StatusRed
                            )
                        }

                        // Info
                        Column(
                            horizontalAlignment = Alignment.End,
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.End
                            ) {
                                // Wallet Badge (Beautiful pastel semi-transparent indicators)
                                val badgeBgColor = when (mapping.walletType) {
                                    "جيب" -> Color(0xFF1565C0).copy(alpha = 0.15f)
                                    "جوالي" -> Color(0xFF2E7D32).copy(alpha = 0.15f)
                                    "كريمي" -> BrandPrimaryDark.copy(alpha = 0.15f)
                                    "حاسب" -> Color(0xFFEF6C00).copy(alpha = 0.15f)
                                    "ون كاش" -> Color(0xFF880E4F).copy(alpha = 0.15f)
                                    else -> Color(0xFF4527A0).copy(alpha = 0.15f)
                                }
                                val badgeTextColor = when (mapping.walletType) {
                                    "جيب" -> Color(0xFF90CAF9)
                                    "جوالي" -> Color(0xFFA5D6A7)
                                    "كريمي" -> Color(0xFFEF9A9A)
                                    "حاسب" -> Color(0xFFFFCC80)
                                    "ون كاش" -> Color(0xFFF48FB1)
                                    else -> Color(0xFFB39DDB)
                                }
                                Surface(
                                    color = badgeBgColor,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.padding(end = 8.dp)
                                ) {
                                    Text(
                                        text = mapping.walletType,
                                        color = badgeTextColor,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                                
                                Text(
                                    text = mapping.customerName.ifEmpty { "عميل استثنائي" },
                                    color = PureWhite,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            Text(
                                text = "رمز مجهول: ${mapping.customerUniqueId}",
                                color = if (isDark) GlowPurplePink else Color(0xFF7B1FA2),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Normal
                            )
                            
                            Text(
                                text = "رقم التوجيه (الأساسي): ${mapping.basicPhone}",
                                color = TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

fun exportTransactionsToCsv(
    context: Context,
    transactions: List<Transaction>,
    dateLabel: String
) {
    try {
        if (transactions.isEmpty()) {
            Toast.makeText(context, "لا توجد أي معاملات لتصديرها!", Toast.LENGTH_LONG).show()
            return
        }

        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val csvBuilder = StringBuilder()
        csvBuilder.append("\uFEFF") // UTF-8 BOM
        
        // Header row
        csvBuilder.append("رقم الحركة,اسم الجزء/العميل,الرقم الهاتفي,القيمة/الفئة (ريال),كود الكارت والشحن,نوع المحفظة,تاريخ المعاملة\n")
        
        for (tx in transactions) {
            val phone = tx.phone
            val amount = tx.amount.toString()
            val cardDetails = tx.cardCode
            val wallet = tx.walletType.ifEmpty { "عميل مباشر" }
            val dateStr = sdf.format(Date(tx.createdAt))
            
            csvBuilder.append("${tx.id},")
            csvBuilder.append("عميل مباشر,")
            csvBuilder.append("${escapeCsv(phone)},")
            csvBuilder.append("${escapeCsv(amount)},")
            csvBuilder.append("${escapeCsv(cardDetails)},")
            csvBuilder.append("${escapeCsv(wallet)},")
            csvBuilder.append("${escapeCsv(dateStr)}\n")
        }

        val filename = "تقرير_توزيع_الكروت_${dateLabel}_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}.csv"
        val cacheFile = File(context.cacheDir, filename)
        cacheFile.writeText(csvBuilder.toString(), StandardCharsets.UTF_8)

        val authority = "${context.packageName}.fileprovider"
        val fileUri: Uri = FileProvider.getUriForFile(context, authority, cacheFile)

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_SUBJECT, "تقرير مبيعات وتوزيع الكروت - $dateLabel")
            putExtra(Intent.EXTRA_STREAM, fileUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        
        val chooser = Intent.createChooser(intent, "تصدير تقرير الكروت الموزعة CSV")
        context.startActivity(chooser)
    } catch (e: Exception) {
        Log.e("CSV_EXPORT", "Error exporting transaction CSV", e)
        Toast.makeText(context, "حدث خطأ أثناء التصدير", Toast.LENGTH_SHORT).show()
    }
}

fun exportCustomerTransactionsToCsv(
    context: Context,
    mappings: List<CustomerMapping>,
    transactions: List<Transaction>
) {
    try {
        val mappingPhones = mappings.associateBy { it.basicPhone.trim() }
        val matchedList = transactions.filter { mappingPhones.containsKey(it.phone.trim()) }
        
        if (matchedList.isEmpty()) {
            Toast.makeText(context, "لا توجد أي معاملات مسجلة للعملاء الاستثنائيين لتصديرها!", Toast.LENGTH_LONG).show()
            return
        }

        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val csvBuilder = StringBuilder()
        csvBuilder.append("\uFEFF") // UTF-8 BOM
        
        // Header row
        csvBuilder.append("رقم الحركة,اسم العميل الاستثنائي,معرّف المحفظة الرقمي,الرقم الهاتفي الموجه,القيمة/الفئة (ريال),كود الكارت والشحن,نوع المحفظة,تاريخ ووقت المعاملة\n")
        
        for (tx in matchedList) {
            val mapping = mappingPhones[tx.phone.trim()]
            val name = mapping?.customerName?.ifEmpty { "عميل استثنائي" } ?: "عميل استثنائي"
            val uniqueId = mapping?.customerUniqueId ?: ""
            val phone = tx.phone
            val amount = tx.amount.toString()
            val cardDetails = tx.cardCode
            val wallet = tx.walletType.ifEmpty { mapping?.walletType ?: "" }
            val dateStr = sdf.format(Date(tx.createdAt))
            
            csvBuilder.append("${tx.id},")
            csvBuilder.append("${escapeCsv(name)},")
            csvBuilder.append("${escapeCsv(uniqueId)},")
            csvBuilder.append("${escapeCsv(phone)},")
            csvBuilder.append("${escapeCsv(amount)},")
            csvBuilder.append("${escapeCsv(cardDetails)},")
            csvBuilder.append("${escapeCsv(wallet)},")
            csvBuilder.append("${escapeCsv(dateStr)}\n")
        }

        val filename = "تقرير_معاملات_العملاء_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}.csv"
        val cacheFile = File(context.cacheDir, filename)
        cacheFile.writeText(csvBuilder.toString(), StandardCharsets.UTF_8)

        val authority = "${context.packageName}.fileprovider"
        val fileUri: Uri = FileProvider.getUriForFile(context, authority, cacheFile)

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_SUBJECT, "تقرير معاملات اليوم والعملاء الاستثنائيين")
            putExtra(Intent.EXTRA_STREAM, fileUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        
        val chooser = Intent.createChooser(intent, "مشاركة وتصدير ملف الحسابات الشامل CSV")
        context.startActivity(chooser)
    } catch (e: Exception) {
        Log.e("CSV_EXPORT", "Error exporting CSV", e)
        Toast.makeText(context, "حدث خطأ أثناء تصدير الملف: ${e.message}", Toast.LENGTH_LONG).show()
    }
}

private fun escapeCsv(value: String): String {
    val clean = value.replace("\"", "\"\"")
    return if (clean.contains(",") || clean.contains("\n") || clean.contains("\"")) {
        "\"$clean\""
    } else {
        clean
    }
}

