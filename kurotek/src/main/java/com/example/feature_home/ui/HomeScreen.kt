package com.example.feature_home.ui
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
// ==========================================
// TAB 1: الرئيسية (Home Tab)
// ==========================================
@Composable
fun HomeScreen(
    authViewModel: com.example.ui.AuthViewModel,
    inventoryViewModel: com.example.ui.InventoryViewModel,
    salesViewModel: com.example.ui.SalesViewModel,
    mainViewModel: com.example.ui.MainViewModel,
    distributorViewModel: com.example.ui.DistributorViewModel,
    onNavigateToSubScreen: (String) -> Unit,
    onNavigateToTab: (Int) -> Unit
) {
    val context = LocalContext.current
    val isActivated by authViewModel.isActivated.collectAsState()
    val isTrialActive by authViewModel.isTrialActive.collectAsState()
    val networkName by authViewModel.networkName.collectAsState()
    val totalUnusedCount by inventoryViewModel.totalUnusedCount.collectAsState()
    val isDark by mainViewModel.isDarkTheme.collectAsState()
    
    // Unused counts per category
    val count100 by inventoryViewModel.getCountForCategory(100).collectAsState(initial = 0)
    val count200 by inventoryViewModel.getCountForCategory(200).collectAsState(initial = 0)
    val count250 by inventoryViewModel.getCountForCategory(250).collectAsState(initial = 0)
    val count300 by inventoryViewModel.getCountForCategory(300).collectAsState(initial = 0)
    val count500 by inventoryViewModel.getCountForCategory(500).collectAsState(initial = 0)
    
    val allTransactions by salesViewModel.transactions.collectAsState()
    val allDeposits by salesViewModel.deposits.collectAsState()
    val todayDateStr = remember { SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()) }
    
    // Filter transactions to just show today's movements
    val todayTransactions = remember(allTransactions) {
        allTransactions.filter { 
            val dateFormatted = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date(it.createdAt))
            dateFormatted == todayDateStr
        }
    }

    val todayDeposits = remember(allDeposits) {
        allDeposits.filter { 
            val dateFormatted = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date(it.createdAt))
            dateFormatted == todayDateStr
        }
    }

    var isBalanceVisible by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepBlack)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. App Top Header Bar (matches Screenshot 1's top layout)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Side: Action Notification Bell and Agent/Robot Icons in light circles
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // WhatsApp Developer Contact Button
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(Color(0xFF25D366).copy(alpha = 0.15f), CircleShape)
                            .border(BorderStroke(1.2.dp, Color(0xFF25D366)), CircleShape)
                            .clickable {
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/967773303455"))
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "الرجاء تثبيت تطبيق واتساب للتواصل", Toast.LENGTH_SHORT).show()
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Chat,
                            contentDescription = "تواصل واتساب مع المطور",
                            tint = Color(0xFF25D366),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Notification Bell with Red Badge
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(SurfaceDark, CircleShape)
                            .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)), CircleShape)
                            .clickable {
                                Toast.makeText(context, "🔔 لا توجد إشعارات جديدة غير مقروءة.", Toast.LENGTH_SHORT).show()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        BadgedBox(
                            badge = {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(Color.Red, CircleShape)
                                )
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Notifications,
                                contentDescription = "الإشعارات",
                                tint = PureWhite,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // Right Side: Greeting (صباح الخير / جارالله)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "صباح الخير 👋",
                            color = TextSecondary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = networkName.ifEmpty { "جارالله" },
                            color = PureWhite,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    // Rounded Avatar Container
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(BrandPrimaryRed.copy(alpha = 0.15f), CircleShape)
                            .border(BorderStroke(2.dp, BrandPrimaryRed), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (networkName.isNotEmpty()) networkName.take(1) else "ج",
                            color = BrandPrimaryRed,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }



        // Trial warning/notice banner (if trial is active and not fully activated)
        if (isTrialActive && !isActivated) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E2F)),
                    border = BorderStroke(1.dp, GlowPurplePink.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().clickable {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/967773303455"))
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "فشل فتح تطبيق واتساب!", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(
                                text = "الفترة التجريبية المجانية مفعلة ⏳",
                                color = GlowPurplePink,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Right
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            val remainingDays = authViewModel.getRemainingTrialDays()
                            Text(
                                text = "متبقي لديك $remainingDays أيام لتجربة التطبيق مجاناً. اضغط هنا للتواصل مع المطور لشراء السيريال والتنشيط الدائم.",
                                color = TextSecondary,
                                fontSize = 11.sp,
                                textAlign = TextAlign.Right,
                                lineHeight = 16.sp
                            )
                        }
                        Icon(
                            imageVector = Icons.Outlined.Timer,
                            contentDescription = "مؤقت التجربة",
                            tint = GlowPurplePink,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }

        // 2. Beautiful Professional Daily Dashboard
        item {
            val totalCardsSoldToday = todayTransactions.size
            val totalSoldAmountToday = todayTransactions.sumOf { it.amount }
            val totalDepositsToday = todayDeposits.sumOf { it.amount }
            val totalOperationsToday = totalCardsSoldToday + todayDeposits.size

            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF13151E)), // Deep luxury space blue surface
                border = BorderStroke(1.2.dp, Color(0xFF262936)),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .testTag("app_dashboard_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    // Header inside card
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Refresh timestamp
                        val formatter = remember { java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault()) }
                        val lastUpdateStr = remember(todayTransactions, todayDeposits) {
                            formatter.format(java.util.Date())
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Sync,
                                contentDescription = null,
                                tint = Color(0xFF10B981), // Emerald Green
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = "تحديث تلقائي: $lastUpdateStr",
                                color = TextSecondary.copy(alpha = 0.8f),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        Text(
                            text = "لوحة التحكم اليومية 📊",
                            color = BrandPrimaryRed,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Right
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(14.dp))
                    
                    // 2x2 grid layout of metrics
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                DashboardMetricItem(
                                    title = "إجمالي مبيعات اليوم",
                                    value = "$totalSoldAmountToday ر.ي",
                                    icon = Icons.Outlined.Payments,
                                    iconColor = Color(0xFF34D399) // Emerald
                                )
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                DashboardMetricItem(
                                    title = "إيداعات اليوم المستلمة",
                                    value = "$totalDepositsToday ر.ي",
                                    icon = Icons.Outlined.AccountBalanceWallet,
                                    iconColor = Color(0xFF60A5FA) // Blue
                                )
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                DashboardMetricItem(
                                    title = "كروت مباعة اليوم",
                                    value = "$totalCardsSoldToday كارت",
                                    icon = Icons.Outlined.ConfirmationNumber,
                                    iconColor = Color(0xFFFBBF24) // Gold
                                )
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                DashboardMetricItem(
                                    title = "عمليات منُفذة اليوم",
                                    value = "$totalOperationsToday عملية",
                                    icon = Icons.Outlined.ReceiptLong,
                                    iconColor = Color(0xFFA78BFA) // Purple
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. Services Grid (6 Buttons matching Screenshot 2 precisely)
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "الخدمات والأنظمة المتكاملة ⚡",
                    color = PureWhite,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Right
                )
                Spacer(modifier = Modifier.height(4.dp))
                
                // Row 1
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Service 1: وضع الموزع
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                        border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.15f)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(90.dp)
                            .clickable { 
                                distributorViewModel.setDistributorModeActive(true)
                                Toast.makeText(context, "تم التبديل إلى وضع الموزع 🔄", Toast.LENGTH_SHORT).show()
                            }
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.Calculate, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("وضع الموزع", color = PureWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Service 2: ديون البقالات
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                        border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.15f)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(90.dp)
                            .clickable {
                                distributorViewModel.setDistributorModeActive(true)
                            }
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.Storefront, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("ديون البقالات", color = PureWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Service 3: توليد كروت
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                        border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.15f)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(90.dp)
                            .clickable { onNavigateToSubScreen("mikrotik") }
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.Router, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("توليد كروت", color = PureWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Row 2
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Service 4: مخزن الكروت
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                        border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.15f)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(90.dp)
                            .clickable { onNavigateToTab(1) }
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.Inventory, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("مخزن الكروت", color = PureWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Service 5: مزامنة SMS
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                        border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.15f)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(90.dp)
                            .clickable { onNavigateToTab(2) }
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.Sync, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("مزامنة SMS", color = PureWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Service 6: سندات ومصاريف
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                        border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.15f)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(90.dp)
                            .clickable {
                                distributorViewModel.setDistributorModeActive(true)
                            }
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("سندات ومصاريف", color = PureWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // 4. Highly Polished 3-column separate card categories grid (Screenshot 2 Quick Actions style)
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "خيارات سريعة وتحديثات",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "فئات كروت الشحن المتوفرة ⚡",
                        color = PureWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Grid 3 columns
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Row 1 (F100, F200, F250)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        listOf(
                            Triple(100, count100, Category100Cardboard),
                            Triple(200, count200, Category200Blue),
                            Triple(250, count250, Category250Purple)
                        ).forEach { (category, count, color) ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                                border = BorderStroke(1.dp, if (isDark) color.copy(alpha = 0.25f) else color.copy(alpha = 0.15f)),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(102.dp)
                                    .testTag("cat_card_$category")
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize().padding(10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .background(color.copy(alpha = 0.1f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CardMembership,
                                            contentDescription = null,
                                            tint = color,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    Text(
                                        text = "فئة $category ر.ي",
                                        color = TextSecondary,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "$count كارت",
                                        color = PureWhite,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }
                        }
                    }

                    // Row 2 (F300, F500, Total Stock)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        listOf(
                            Triple(300, count300, Category300Green),
                            Triple(500, count500, Category500Turmeric),
                            Triple(0, totalUnusedCount, BrandPrimaryRed) // Total
                        ).forEach { (category, count, color) ->
                            val isTotal = category == 0
                            Card(
                                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                                border = BorderStroke(1.dp, if (isDark) color.copy(alpha = 0.25f) else color.copy(alpha = 0.15f)),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(102.dp)
                                    .testTag(if (isTotal) "cat_card_total" else "cat_card_$category")
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize().padding(10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .background(color.copy(alpha = 0.1f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (isTotal) Icons.Default.Inventory else Icons.Default.CardMembership,
                                            contentDescription = null,
                                            tint = color,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    Text(
                                        text = if (isTotal) "إجمالي المخزون" else "فئة $category ر.ي",
                                        color = TextSecondary,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "$count كارت",
                                        color = PureWhite,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 5. Recent Transactions Movements List (Separate Rounded Cards with left-aligned prices and right-aligned category icons)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "اليوم",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "العمليات وحركة اليوم 🔄",
                    color = PureWhite,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        if (todayTransactions.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.03f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "لا توجد عمليات مبيعات أو مزامنة كروت مسجلة لليوم.",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(todayTransactions) { transaction ->
                val isSuccess = !transaction.cardCode.contains("غير متوفر") && !transaction.cardCode.contains("فشل") && !transaction.cardCode.contains("تجاهل")
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.04f)),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("transaction_item_${transaction.id}")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left: Price details in red or green (following Screenshot 2 style)
                        Column(horizontalAlignment = Alignment.Start) {
                            Text(
                                text = "-${transaction.amount} ر.ي",
                                color = if (isSuccess) BrandPrimaryRed else StatusRed,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(transaction.createdAt)),
                                color = TextSecondary,
                                fontSize = 10.sp
                            )
                        }

                        // Right: Title, client phone, wallet type and category icon
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "كرت فئة ${transaction.amount} تم بيعه بنجاح",
                                    color = PureWhite,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Right
                                )
                                Text(
                                    text = "المحفظة: ${transaction.walletType} | العميل: ${transaction.phone}",
                                    color = TextSecondary,
                                    fontSize = 11.sp,
                                    textAlign = TextAlign.Right
                                )
                            }

                            // Circular icon container matching screenshot
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(
                                        if (isSuccess) BrandPrimaryRed.copy(alpha = 0.1f) else StatusRed.copy(alpha = 0.1f),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isSuccess) Icons.Outlined.CheckCircle else Icons.Outlined.Warning,
                                    contentDescription = null,
                                    tint = if (isSuccess) BrandPrimaryRed else StatusRed,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

