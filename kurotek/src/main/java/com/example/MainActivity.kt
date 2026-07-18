package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.database.CardRepository
import com.example.network.SyncService
import com.example.security.FirebaseManager
import com.example.ui.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.DeepBlack
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.PureWhite

enum class AppScreen {
    LOGIN,
    MAIN,
    ADD_CARDS,
    LOGS
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val context = LocalContext.current
                val coreContainer = remember { com.example.core.CoreContainer.getInstance(context) }
                
                // Old ViewModels (being phased out or refactored)
                val factory = remember { MainViewModelFactory(coreContainer.cardRepository) }
                val mainViewModel: MainViewModel = viewModel(factory = factory)

                val authFactory = remember { AuthViewModelFactory(coreContainer) }
                val authViewModel: com.example.ui.AuthViewModel = viewModel(factory = authFactory)

                val smsFactory = remember { SmsViewModelFactory(coreContainer) }
                // SmsViewModel is kept for background SMS processing service only
                @Suppress("UNUSED_VARIABLE")
                val smsViewModel: com.example.ui.SmsViewModel = viewModel(factory = smsFactory)

                val settingsFactory = remember { SettingsViewModelFactory(coreContainer.cardRepository, coreContainer) }
                val settingsViewModel: com.example.ui.SettingsViewModel = viewModel(factory = settingsFactory)

                val distFactory = remember { DistributorViewModelFactory(coreContainer) }
                val distributorViewModel: com.example.ui.DistributorViewModel = viewModel(factory = distFactory)

                // New Clean Architecture ViewModels
                val dashboardFactory = remember { DashboardViewModelFactory(coreContainer) }
                val dashboardViewModel: com.example.ui.DashboardViewModel = viewModel(factory = dashboardFactory)

                val inventoryFactory = remember { InventoryViewModelFactory(coreContainer) }
                val inventoryViewModel: com.example.ui.InventoryViewModel = viewModel(factory = inventoryFactory)

                val salesFactory = remember { SalesViewModelFactory(coreContainer) }
                val salesViewModel: com.example.ui.SalesViewModel = viewModel(factory = salesFactory)

                val reportsFactory = remember { ReportsViewModelFactory(coreContainer) }
                val reportsViewModel: com.example.ui.ReportsViewModel = viewModel(factory = reportsFactory)

                val walletFactory = remember { WalletViewModelFactory(coreContainer) }
                val walletViewModel: com.example.ui.WalletViewModel = viewModel(factory = walletFactory)

                val mikrotikFactory = remember { MikrotikViewModelFactory(coreContainer) }
                val mikrotikViewModel: com.example.ui.MikrotikViewModel = viewModel(factory = mikrotikFactory)

                val isDarkTheme by mainViewModel.isDarkTheme.collectAsState()
                LaunchedEffect(isDarkTheme) {
                    com.example.ui.theme.isDarkThemeState.value = isDarkTheme
                }

                val isActivated by authViewModel.isActivated.collectAsState()
                var currentScreen by remember { mutableStateOf(AppScreen.LOGIN) }

                // 🔥 Firebase: تسجيل الجهاز وفحص Kill Switch
                val firebaseStatus by remember {
                    FirebaseManager.observeKillSwitch(context)
                }.collectAsState(initial = FirebaseManager.DeviceStatus.LOADING)

                // تسجيل أو تحديث بيانات الجهاز في Firebase
                LaunchedEffect(Unit) {
                    FirebaseManager.registerOrUpdateDevice(context)
                }

                // Check and request run-time SMS & notification permissions
                val requiredPermissions = remember {
                    val permissions = mutableListOf(
                        Manifest.permission.RECEIVE_SMS,
                        Manifest.permission.READ_SMS,
                        Manifest.permission.SEND_SMS
                    )
                    if (android.os.Build.VERSION.SDK_INT >= 33) {
                        permissions.add(Manifest.permission.POST_NOTIFICATIONS)
                    }
                    permissions.toTypedArray()
                }

                var hasSmsPermissions by remember {
                    mutableStateOf(
                        requiredPermissions.all {
                            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
                        }
                    )
                }

                val permissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestMultiplePermissions()
                ) { permissionsResult ->
                    hasSmsPermissions = permissionsResult.values.all { it }
                }

                // Auto route from/to activation screen and request permissions
                LaunchedEffect(isActivated, hasSmsPermissions) {
                    if (isActivated) {
                        currentScreen = AppScreen.MAIN
                        if (!hasSmsPermissions) {
                            permissionLauncher.launch(requiredPermissions)
                        }
                    } else {
                        currentScreen = AppScreen.LOGIN
                    }
                }

                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("app_scaffold")
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(DeepBlack)
                            .padding(innerPadding)
                    ) {
                        // 🔴 Kill Switch: إذا حظر الأدمن هذا الجهاز، تُعرض شاشة الحظر فوراً
                        if (firebaseStatus == FirebaseManager.DeviceStatus.BLOCKED) {
                            KurotekBlockedScreen()
                            return@Box
                        }

                        when (currentScreen) {
                            AppScreen.LOGIN -> {
                                LoginScreen(
                                    authViewModel = authViewModel,
                                    mainViewModel = mainViewModel,
                                    smsViewModel = settingsViewModel,
                                    onLoginSuccess = { currentScreen = AppScreen.MAIN }
                                )
                            }
                            AppScreen.MAIN -> {
                                LaunchedEffect(Unit) {
                                    SyncService.startService(context)
                                }
                                MainDashboardScreen(
                                    mainViewModel = mainViewModel,
                                    authViewModel = authViewModel,
                                    smsViewModel = settingsViewModel,
                                    distributorViewModel = distributorViewModel,
                                    dashboardViewModel = dashboardViewModel,
                                    inventoryViewModel = inventoryViewModel,
                                    salesViewModel = salesViewModel,
                                    reportsViewModel = reportsViewModel,
                                    walletViewModel = walletViewModel,
                                    mikrotikViewModel = mikrotikViewModel,
                                    onLogout = {
                                        authViewModel.setActivated(false)
                                        SyncService.stopService(context)
                                        currentScreen = AppScreen.LOGIN
                                    }
                                )
                            }
                            else -> {
                                // Fallback
                                MainDashboardScreen(
                                    mainViewModel = mainViewModel,
                                    authViewModel = authViewModel,
                                    smsViewModel = settingsViewModel,
                                    distributorViewModel = distributorViewModel,
                                    dashboardViewModel = dashboardViewModel,
                                    inventoryViewModel = inventoryViewModel,
                                    salesViewModel = salesViewModel,
                                    reportsViewModel = reportsViewModel,
                                    walletViewModel = walletViewModel,
                                    mikrotikViewModel = mikrotikViewModel,
                                    onLogout = {
                                        authViewModel.setActivated(false)
                                        currentScreen = AppScreen.LOGIN
                                    }
                                )
                            }
                        }

                        // Display custom permission notice if logged in but permissions are missing
                        AnimatedVisibility(
                            visible = isActivated && !hasSmsPermissions,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(DeepBlack.copy(alpha = 0.85f))
                                    .padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                                    shape = RoundedCornerShape(16.dp),
                                    border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f)),
                                    modifier = Modifier.fillMaxWidth().testTag("permission_dialog_card")
                                ) {
                                    Column(
                                        modifier = Modifier.padding(24.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Sms,
                                            contentDescription = null,
                                            tint = GoldPrimary,
                                            modifier = Modifier.size(56.dp)
                                        )

                                        Text(
                                            text = "مطلوب صلاحيات الرسائل SMS",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp,
                                            color = GoldPrimary,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.fillMaxWidth()
                                        )

                                        Text(
                                            text = "لكي يتمكن تطبيق كروت الدحشة من قراءة وتصفية رسائل الإشعارات الواردة وتحديداً من محفظة 'جيب' ومحفظة 'جوالي' وتوزيع كروت الشحن تلقائياً لعملائك، يتطلب منح الهاتف صلاحية قراءة واستقبال وإرسال رسائل SMS.",
                                            fontSize = 14.sp,
                                            color = PureWhite,
                                            lineHeight = 20.sp,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.fillMaxWidth()
                                        )

                                        Button(
                                            onClick = {
                                                permissionLauncher.launch(requiredPermissions)
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = GoldPrimary,
                                                contentColor = DeepBlack
                                            ),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(48.dp)
                                                .testTag("btn_request_permissions")
                                        ) {
                                            Text(
                                                text = "منح الصلاحيات المطلوبة والبدء",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
