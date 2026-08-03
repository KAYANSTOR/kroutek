package com.example.ui

import com.example.ui.components.UnifiedBottomNavItem
import com.example.feature_approvals.ui.PendingApprovalsTab
import com.example.feature_customers.ui.SpecialCustomersTab
import com.example.feature_settings.ui.SettingsTab
import com.example.feature_reports.ui.ReportsTab
import com.example.feature_cards.ui.CardsTab
import com.example.feature_home.ui.HomeScreen
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.models.Card
import com.example.models.CustomerMapping
import com.example.models.Deposit
import com.example.models.Transaction
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import com.example.utils.DocumentExporter

@Composable
fun MainDashboardScreen(
    mainViewModel: MainViewModel,
    authViewModel: com.example.ui.AuthViewModel,
    settingsViewModel: com.example.ui.SettingsViewModel,
    distributorViewModel: com.example.ui.DistributorViewModel,
    dashboardViewModel: com.example.ui.DashboardViewModel,
    inventoryViewModel: com.example.ui.InventoryViewModel,
    salesViewModel: com.example.ui.SalesViewModel,
    reportsViewModel: com.example.ui.ReportsViewModel,
    walletViewModel: com.example.ui.WalletViewModel,
    mikrotikViewModel: com.example.ui.MikrotikViewModel,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(0) }
    val isDarkTheme by mainViewModel.isDarkTheme.collectAsState()
    val networkName by settingsViewModel.networkName.collectAsState()
    val allPendingApprovals by dashboardViewModel.pendingApprovals.collectAsState()

    var activeEventNotification by remember { mutableStateOf<com.example.utils.NotificationBus.NewCardExtractedEvent?>(null) }
    var isCenterMenuOpen by remember { mutableStateOf(false) }
    var currentSubScreen by remember { mutableStateOf<String?>(null) } // "mikrotik", null
    val isDistributorModeActive by distributorViewModel.isDistributorModeActive.collectAsState()
    var distributorInitialTab by remember { mutableStateOf(0) }
    var showExitConfirmDialog by remember { mutableStateOf(false) }

    // Intercept system Back Press
    androidx.activity.compose.BackHandler(enabled = true) {
        if (isCenterMenuOpen) {
            isCenterMenuOpen = false
        } else if (currentSubScreen != null) {
            currentSubScreen = null
        } else if (selectedTab != 0) {
            selectedTab = 0
        } else {
            showExitConfirmDialog = true
        }
    }

    if (showExitConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showExitConfirmDialog = false },
            title = {
                Text(
                    text = "تأكيد الخروج ⚠️",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Right,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Text(
                    text = "هل تريد الخروج من التطبيق بالفعل؟",
                    color = TextSecondary,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Right,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showExitConfirmDialog = false
                        (context as? android.app.Activity)?.finish()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimaryRed),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("نعم", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showExitConfirmDialog = false }
                ) {
                    Text("إلغاء", color = TextSecondary, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = SurfaceDark,
            shape = RoundedCornerShape(16.dp)
        )
    }

    LaunchedEffect(Unit) {
        com.example.utils.NotificationBus.newCardExtractedEvents.collect { event ->
            activeEventNotification = event
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(DeepBlack)
    ) {
        // Global Mode Switcher
        Surface(
            color = if (isDistributorModeActive) Color(0xFF0C1A14) else Color(0xFF151922),
            shadowElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Premium Segmented Toggle (Sleek UI)
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0xFF18181B)) // Zinc 900
                        .border(1.dp, Color(0xFF27272A), RoundedCornerShape(24.dp)) // Zinc 800
                        .padding(4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Direct Sales Button
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (!isDistributorModeActive) BrandPrimaryRed else Color.Transparent)
                                .clickable { distributorViewModel.setDistributorModeActive(false) }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "بائع مباشر (SMS)", 
                                color = if (!isDistributorModeActive) Color.White else TextSecondary,
                                fontWeight = if (!isDistributorModeActive) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 14.sp
                            )
                        }
                        
                        // Distributor Button
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isDistributorModeActive) Color(0xFF10B981) else Color.Transparent) // Emerald
                                .clickable { distributorViewModel.setDistributorModeActive(true) }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "موزع الجملة", 
                                color = if (isDistributorModeActive) Color.White else TextSecondary,
                                fontWeight = if (isDistributorModeActive) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }

        Box(modifier = Modifier.weight(1f)) {
            // ✅ وضع الموزع: عرض مستقل تماًبدون الـ Scaffold العادي
            if (isDistributorModeActive) {
                DistributorSystemScreen(
                    viewModel = distributorViewModel,
                    initialTab = distributorInitialTab,
                    onBack = { distributorViewModel.setDistributorModeActive(false) }
                )
            }
            else if (currentSubScreen == "mikrotik") {
                MikrotikGeneratorScreen(
                    viewModel = mikrotikViewModel,
                    onBack = { currentSubScreen = null }
                )
            }
            else {
        Scaffold(
            bottomBar = {
                // Beautiful Custom Bottom Bar matching the screenshots (4 tabs + floating central button)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(68.dp)
                        .testTag("dashboard_bottom_nav"),
                    contentAlignment = Alignment.TopCenter
                ) {
                    // White background card with top rounded corners
                    Surface(
                        color = Color.White,
                        tonalElevation = 0.dp,
                        shadowElevation = 16.dp,
                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Left-most Tab: الملف (Settings)
                            UnifiedBottomNavItem(
                                selected = selectedTab == 5,
                                onClick = {
                                    isCenterMenuOpen = false
                                    selectedTab = 5
                                },
                                icon = Icons.Outlined.Person,
                                selectedIcon = Icons.Filled.Person,
                                label = "الملف",
                                activeColor = BrandPrimaryRed
                            )
                            // Left-middle Tab: التقارير (Reports)
                            UnifiedBottomNavItem(
                                selected = selectedTab == 4,
                                onClick = {
                                    isCenterMenuOpen = false
                                    selectedTab = 4
                                },
                                icon = Icons.Outlined.FormatListBulleted,
                                selectedIcon = Icons.Filled.FormatListBulleted,
                                label = "التقارير",
                                activeColor = BrandPrimaryRed
                            )
                            // Center Spacer
                            Box(modifier = Modifier.weight(1f).fillMaxHeight())
                            // Right-middle Tab: الخدمات (Services)
                            UnifiedBottomNavItem(
                                selected = selectedTab == 1,
                                onClick = {
                                    isCenterMenuOpen = false
                                    selectedTab = 1
                                },
                                icon = Icons.Outlined.ShoppingBag,
                                selectedIcon = Icons.Filled.ShoppingBag,
                                label = "الخدمات",
                                activeColor = BrandPrimaryRed
                            )
                            // Right-most Tab: الرئيسية (Home)
                            UnifiedBottomNavItem(
                                selected = selectedTab == 0,
                                onClick = {
                                    isCenterMenuOpen = false
                                    selectedTab = 0
                                },
                                icon = Icons.Outlined.Home,
                                selectedIcon = Icons.Filled.Home,
                                label = "الرئيسية",
                                activeColor = BrandPrimaryRed
                            )
                        }
                    }

                    // Circular Floating Center Menu Button (diameter 64dp, half protruding)
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .offset(y = (-32).dp)
                            .size(64.dp)
                            .shadow(elevation = 12.dp, shape = CircleShape)
                            .background(
                                color = if (isCenterMenuOpen) Color(0xFF1E293B) else Color(0xFFDC2626),
                                shape = CircleShape
                            )
                            .clickable { isCenterMenuOpen = !isCenterMenuOpen },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isCenterMenuOpen) Icons.Default.Close else Icons.Filled.KeyboardDoubleArrowUp,
                            contentDescription = "القائمة",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            },
            containerColor = DeepBlack
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (selectedTab) {
                    0 -> HomeScreen(
                        authViewModel = authViewModel,
                        inventoryViewModel = inventoryViewModel,
                        salesViewModel = salesViewModel,
                        mainViewModel = mainViewModel,
                        distributorViewModel = distributorViewModel,
                        onNavigateToSubScreen = { screen ->
                            if (screen.startsWith("distributor")) {
                                distributorViewModel.setDistributorModeActive(true)
                            } else {
                                currentSubScreen = screen
                            }
                        },
                        onNavigateToTab = { selectedTab = it }
                    )
                    1 -> CardsTab(
                        inventoryViewModel = inventoryViewModel,
                        salesViewModel = salesViewModel,
                        smsViewModel = settingsViewModel,
                        mainViewModel = mainViewModel
                    )
                    2 -> PendingApprovalsTab(
                        dashboardViewModel = dashboardViewModel,
                        reportsViewModel = reportsViewModel,
                        mainViewModel = mainViewModel
                    )
                    3 -> SpecialCustomersTab(
                        reportsViewModel = reportsViewModel,
                        salesViewModel = salesViewModel,
                        mainViewModel = mainViewModel
                    )
                    4 -> ReportsTab(
                        salesViewModel = salesViewModel,
                        dashboardViewModel = dashboardViewModel,
                        mainViewModel = mainViewModel
                    )
                    5 -> SettingsTab(
                        mainViewModel = mainViewModel,
                        authViewModel = authViewModel,
                        settingsViewModel = settingsViewModel,
                        distributorViewModel = distributorViewModel,
                        onLogout = onLogout
                    )
                }
            }
        }

        // Animated Central Popover Overlay Menu (matches Screenshot 1's dark 4-card grid overlay)
        AnimatedVisibility(
            visible = isCenterMenuOpen,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable { isCenterMenuOpen = false },
                contentAlignment = Alignment.BottomCenter
            ) {
                // Overlay Bottom Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 0.dp)
                        .clickable(enabled = false) { } // prevent clicks from closing
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "الوصول السريع والخدمات الإضافية",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                        )

                        // Card 1: التفويضات (Approvals)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(100.dp)
                                    .clickable {
                                        isCenterMenuOpen = false
                                        selectedTab = 2
                                    }
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize().padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.DoneAll,
                                            contentDescription = null,
                                            tint = BrandPrimaryRed,
                                            modifier = Modifier.size(28.dp)
                                        )
                                        if (allPendingApprovals.isNotEmpty()) {
                                            Box(
                                                modifier = Modifier
                                                    .offset(x = 10.dp, y = (-10).dp)
                                                    .background(BrandPrimaryRed, CircleShape)
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = allPendingApprovals.size.toString(),
                                                    color = Color.White,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "التفويضات المعلقة",
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // Card 2: تبديل إلى وضع الموزع
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                                border = BorderStroke(1.5.dp, Color(0xFF10B981).copy(alpha = 0.4f)),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(100.dp)
                                    .clickable {
                                        isCenterMenuOpen = false
                                        distributorViewModel.setDistributorModeActive(true)
                                        Toast.makeText(context, "تم التبديل إلى نظام الموزع 🏪", Toast.LENGTH_SHORT).show()
                                    }
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize().padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.SwapHoriz,
                                        contentDescription = null,
                                        tint = Color(0xFF10B981),
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "وضع الموزع 🏪",
                                        color = Color(0xFF10B981),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Card 3: إضافة كارت (Add Card quick action)
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(100.dp)
                                    .clickable {
                                        isCenterMenuOpen = false
                                        selectedTab = 1
                                    }
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize().padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AddCard,
                                        contentDescription = null,
                                        tint = BrandPrimaryRed,
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "إضافة كروت جديدة",
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // Card 4: مزامنة وتحديث
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(100.dp)
                                    .clickable {
                                        isCenterMenuOpen = false
                                        selectedTab = 3
                                    }
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize().padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.People,
                                        contentDescription = null,
                                        tint = BrandPrimaryRed,
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "العملاء الاستثنائيين",
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }


                        Spacer(modifier = Modifier.height(8.dp))

                        // Large red rounded Close circular floating button matching the bottom layout precisely
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(Color(0xFF0F172A), CircleShape)
                                .border(BorderStroke(2.dp, Color.White.copy(alpha = 0.1f)), CircleShape)
                                .clickable { isCenterMenuOpen = false },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "إغلاق",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    if (activeEventNotification != null) {
        val event = activeEventNotification!!
        val isDark = isDarkTheme
        val shareMessage = "كود كرت الشحن فئة ${event.amount} ر.ي هو:\n${event.cardDetails}"
        
        AlertDialog(
            onDismissRequest = { activeEventNotification = null },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "🎉 تم استخراج كرت جديد بنجاح",
                        color = if (isDark) GlowOrangeGold else Color(0xFFE65100),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Right,
                        modifier = Modifier.weight(1f)
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "تفاصيل العملية المستلمة ومشاركة الكود:",
                        color = PureWhite,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Right,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Card Info Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SurfaceDark.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.Start) {
                            Text("المبلغ: ${event.amount} ر.ي", color = PureWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text("المحفظة: ${event.walletType}", color = TextSecondary, fontSize = 10.sp)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("الرقم: ${event.recipientPhone}", color = PureWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            val statusText = if (event.isAutoSent) "تم الإرسال تلقائياً ✔" else "فشل الإرسال التلقائي ⚠️"
                            val statusColor = if (event.isAutoSent) GlowEmeraldGreen else StatusRed
                            Text(statusText, color = statusColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = "كود الكرت المستخرج:",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Right,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = event.cardDetails,
                        color = if (isDark) GlowOrangeGold else Color(0xFFE65100),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SurfaceDark.copy(alpha = 0.8f), RoundedCornerShape(10.dp))
                            .padding(12.dp)
                    )
                }
            },
            confirmButton = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                            val clip = android.content.ClipData.newPlainText("Card Details", event.cardDetails)
                            clipboard.setPrimaryClip(clip)

                            com.example.utils.SmsSender.launchWalletApp(context, event.walletType, shareMessage)
                            activeEventNotification = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = if (isDark) GlowEmeraldGreen else Color(0xFF2E7D32)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(44.dp)
                    ) {
                        Text("مشاركة وفتح تطبيق ${event.walletType} 🚀", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    Button(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                            val clip = android.content.ClipData.newPlainText("Card Details", event.cardDetails)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "تم نسخ كود الكارت بنجاح! 📋", Toast.LENGTH_SHORT).show()
                            activeEventNotification = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = if (isDark) GlowOrangeGold else Color(0xFFE65100)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(44.dp)
                    ) {
                        Text("نسخ الكود فقط 📋", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    OutlinedButton(
                        onClick = {
                            activeEventNotification = null
                        },
                        border = BorderStroke(1.dp, TextSecondary.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(44.dp)
                    ) {
                        Text("إغلاق ✖", fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                    }
                }
            },
            dismissButton = null,
            containerColor = SurfaceDark,
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.border(BorderStroke(1.5.dp, if (isDarkTheme) Color(0xFF2D2D2D) else Color(0x1F000000)), RoundedCornerShape(20.dp))
        )
    }
}



@Composable
fun DashboardMetricItem(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1E2B)), // Dark slate card background
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFF282C3D)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    color = TextSecondary,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Right
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = value,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Right
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(iconColor.copy(alpha = 0.12f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
