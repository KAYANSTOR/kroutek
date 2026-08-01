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
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.ui.GraphicsLayer
import androidx.compose.ui.color.Color
import androidx.compose.ui.draw.Blur
import androidx.compose.ui.input.keyboard.Key
import androidx.compose.ui.input.keyboard.keyboardActions
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.network.SyncService
import com.example.ui.*
import com.example.ui.ActivationViewModelFactory
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.DeepBlack
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.PureWhite
import dagger.hilt.android.AndroidEntryPoint

enum class AppScreen {
    WELCOME,
    ACTIVATION,
    MAIN,
    PIN_LOCK
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.e("STARTUP", "STEP 4: MainActivity.onCreate() START")
        try {
            super.onCreate(savedInstanceState)
            Log.e("STARTUP", "STEP 4a: MainActivity.onCreate() super.onCreate() SUCCESS")
        } catch (e: Throwable) {
            Log.e("STARTUP", "STEP 4a FAILED: MainActivity.onCreate() super.onCreate()", e)
            throw e
        }

        try {
            enableEdgeToEdge()
            Log.e("STARTUP", "STEP 4b: MainActivity.enableEdgeToEdge() SUCCESS")
        } catch (e: Throwable) {
            Log.e("STARTUP", "STEP 4b FAILED: MainActivity.enableEdgeToEdge()", e)
            throw e
        }

        Log.e("STARTUP", "STEP 5: MainActivity.setContent() START")
        try {
            setContent {
                MyApplicationTheme {
                    val context = LocalContext.current
                    Log.e("STARTUP", "STEP 5a: MainActivity.setContent() LocalContext SUCCESS")
                    
                    var coreContainer: com.example.core.CoreContainer? = null
                    var initError: String? = null
                    try {
                        Log.e("STARTUP", "STEP 6: MainActivity.CoreContainer.getInstance() START")
                        coreContainer = remember { com.example.core.CoreContainer.getInstance(context) }
                        Log.e("STARTUP", "STEP 6: MainActivity.CoreContainer.getInstance() SUCCESS")
                    } catch (e: Exception) {
                        initError = "فشل تهيئة المكونات الأساسية: ${e.localizedMessage ?: "خطأ غير معروف"}"
                        Log.e("STARTUP", "STEP 6 FAILED: MainActivity.CoreContainer.getInstance()", e)
                    }

                    if (coreContainer == null || initError != null) {
                        Log.e("STARTUP", "STEP 6 FAILED: CoreContainer is null, showing error screen")
                        SafeErrorScreen(message = initError ?: "خطأ غير متوقع")
                        return@MyApplicationTheme
                    }

                    Log.e("STARTUP", "STEP 7: MainActivity.SafeAppShell() START")
                    SafeAppShell(coreContainer = coreContainer)
                }
            }
            Log.e("STARTUP", "STEP 5: MainActivity.setContent() SUCCESS")
        } catch (e: Throwable) {
            Log.e("STARTUP", "STEP 5 FAILED: MainActivity.setContent()", e)
            throw e
        }
        Log.e("STARTUP", "STEP 4: MainActivity.onCreate() END")
    }
}

@Composable
private fun SafeErrorScreen(message: String) {
    Box(modifier = Modifier.fillMaxSize().background(DeepBlack), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.padding(24.dp)) {
            Text("خطأ في تشغيل التطبيق", color = GoldPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Text(message, color = PureWhite, fontSize = 14.sp, textAlign = TextAlign.Center)
            Text("يرجى إعادة تشغيل التطبيق أو الاتصال بالدعم.", color = PureWhite.copy(alpha = 0.7f), fontSize = 13.sp, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun SafeAppShell(coreContainer: com.example.core.CoreContainer) {
    Log.e("STARTUP", "STEP 7a: SafeAppShell START")
    val context = LocalContext.current
    var startupError by remember { mutableStateOf<String?>(null) }

    val mainViewModel = try {
        Log.e("STARTUP", "STEP 7b: MainViewModel creation START")
        val vm = hiltViewModel<MainViewModel>()
        Log.e("STARTUP", "STEP 7b: MainViewModel creation SUCCESS")
        vm
    } catch (e: Exception) {
        Log.e("STARTUP", "STEP 7b FAILED: MainViewModel creation", e)
        startupError = "فشل تحميل الإعدادات العامة"
        null
    }

    val authViewModel = try {
        Log.e("STARTUP", "STEP 7c: AuthViewModel creation START")
        val factory = AuthViewModelFactory(coreContainer.cardRepository, coreContainer)
        val vm = viewModel(factory = factory)
        Log.e("STARTUP", "STEP 7c: AuthViewModel creation SUCCESS")
        vm
    } catch (e: Exception) {
        Log.e("STARTUP", "STEP 7c FAILED: AuthViewModel creation", e)
        startupError = "فشل تحميل بيانات المصادقة"
        null
    } as? com.example.ui.AuthViewModel

    val smsViewModel = try {
        Log.e("STARTUP", "STEP 7d: SmsViewModel creation START")
        val factory = SmsViewModelFactory(coreContainer.cardRepository, coreContainer)
        val vm = viewModel(factory = factory)
        Log.e("STARTUP", "STEP 7d: SmsViewModel creation SUCCESS")
        vm
    } catch (e: Exception) {
        Log.e("STARTUP", "STEP 7d FAILED: SmsViewModel creation", e)
        null
    } as? com.example.ui.SmsViewModel

    val settingsViewModel = try {
        Log.e("STARTUP", "STEP 7e: SettingsViewModel creation START")
        val factory = SettingsViewModelFactory(coreContainer.cardRepository, coreContainer)
        val vm = viewModel(factory = factory)
        Log.e("STARTUP", "STEP 7e: SettingsViewModel creation SUCCESS")
        vm
    } catch (e: Exception) {
        Log.e("STARTUP", "STEP 7e FAILED: SettingsViewModel creation", e)
        null
    } as? com.example.ui.SettingsViewModel

    val activationViewModel = try {
        Log.e("STARTUP", "STEP 7f: ActivationViewModel creation START")
        val factory = ActivationViewModelFactory(authViewModel ?: return, coreContainer.cardRepository)
        val vm = viewModel(factory = factory)
        Log.e("STARTUP", "STEP 7f: ActivationViewModel creation SUCCESS")
        vm
    } catch (e: Exception) {
        Log.e("STARTUP", "STEP 7f FAILED: ActivationViewModel creation", e)
        null
    } as? com.example.ui.ActivationViewModel

    if (startupError != null || authViewModel == null || activationViewModel == null) {
        Log.e("STARTUP", "STEP 7 FAILED: startupError=$startupError authViewModel=${authViewModel != null} activationViewModel=${activationViewModel != null}")
        SafeErrorScreen(message = startupError ?: "خطأ في تحميل بيانات التطبيق")
        return
    }

    if (mainViewModel == null || settingsViewModel == null || smsViewModel == null) {
        Log.e("STARTUP", "STEP 7 FAILED: mainViewModel=${mainViewModel != null} settingsViewModel=${settingsViewModel != null} smsViewModel=${smsViewModel != null}")
        SafeErrorScreen(message = "خطأ في تحميل الإعدادات")
        return
    }

    val safeMainViewModel = mainViewModel
    val safeSettingsViewModel = settingsViewModel
    val safeSmsViewModel = smsViewModel

    val isDarkTheme by safeMainViewModel.isDarkTheme.collectAsState()
    LaunchedEffect(isDarkTheme) {
        com.example.ui.theme.isDarkThemeState.value = isDarkTheme
    }
    
    val isActivated by authViewModel.isActivated.collectAsState()
    val isTrialActive by authViewModel.isTrialActive.collectAsState()
    val isInitialLoginDone by authViewModel.isInitialLoginDone.collectAsState()
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
    val isPinEnabled by remember { mutableStateOf(coreContainer.cardRepository.isAppPinEnabled()) }
    
    LaunchedEffect(isActivated, isTrialActive, isInitialLoginDone, isLoggedIn, isPinEnabled, hasSmsPermissions) {
        currentScreen = when {
            isActivated || isTrialActive -> {
                if (isPinEnabled && !isLoggedIn) AppScreen.PIN_LOCK
                else if (!isInitialLoginDone) AppScreen.WELCOME
                else AppScreen.MAIN
            }
            else -> AppScreen.ACTIVATION
        }
        if (currentScreen == AppScreen.MAIN && !hasSmsPermissions) {
            permissionLauncher.launch(requiredPermissions)
        }
    }
    
    Scaffold(
        modifier = Modifier.fillMaxSize().testTag("app_scaffold")
    ) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize().background(DeepBlack).padding(innerPadding)
        ) {
            when (currentScreen) {
                AppScreen.WELCOME -> {
                    Log.e("STARTUP", "STEP 8: Navigation -> WELCOME screen")
                    LoginScreen(
                        authViewModel = authViewModel,
                        mainViewModel = safeMainViewModel,
                        smsViewModel = safeSmsViewModel,
                        onLoginSuccess = {}
                    )
                }
                AppScreen.ACTIVATION -> {
                    Log.e("STARTUP", "STEP 8: Navigation -> ACTIVATION screen")
                    ActivationScreen(
                        activationViewModel = activationViewModel,
                        onActivationSuccess = {}
                    )
                }
                AppScreen.PIN_LOCK -> {
                    Log.e("STARTUP", "STEP 8: Navigation -> PIN_LOCK screen")
                    PinLockScreen(
                        authViewModel = authViewModel,
                        onUnlocked = {}
                    )
                }
                AppScreen.MAIN -> {
                    Log.e("STARTUP", "STEP 8: Navigation -> MAIN screen START")
                    LaunchedEffect(Unit) {
                        try {
                            com.example.network.SyncService.startService(context)
                        } catch (e: Exception) {
                            Log.e("MainActivity", "Failed to start SyncService", e)
                        }
                    }
                    val safeDistributorViewModel = try {
                        Log.e("STARTUP", "STEP 9a: DistributorViewModel creation START")
                        val factory = DistributorViewModelFactory(coreContainer.cardRepository, coreContainer)
                        val vm = viewModel(factory = factory)
                        Log.e("STARTUP", "STEP 9a: DistributorViewModel creation SUCCESS")
                        vm
                    } catch (e: Exception) {
                        Log.e("STARTUP", "STEP 9a FAILED: DistributorViewModel creation", e)
                        null
                    } as? com.example.ui.DistributorViewModel

                    val safeDashboardViewModel = try {
                        Log.e("STARTUP", "STEP 9b: DashboardViewModel creation START")
                        val factory = DashboardViewModelFactory(coreContainer)
                        val vm = viewModel(factory = factory)
                        Log.e("STARTUP", "STEP 9b: DashboardViewModel creation SUCCESS")
                        vm
                    } catch (e: Exception) {
                        Log.e("STARTUP", "STEP 9b FAILED: DashboardViewModel creation", e)
                        null
                    } as? com.example.ui.DashboardViewModel

                    val safeInventoryViewModel = try {
                        Log.e("STARTUP", "STEP 9c: InventoryViewModel creation START")
                        val factory = InventoryViewModelFactory(coreContainer)
                        val vm = viewModel(factory = factory)
                        Log.e("STARTUP", "STEP 9c: InventoryViewModel creation SUCCESS")
                        vm
                    } catch (e: Exception) {
                        Log.e("STARTUP", "STEP 9c FAILED: InventoryViewModel creation", e)
                        null
                    } as? com.example.ui.InventoryViewModel

                    val safeSalesViewModel = try {
                        Log.e("STARTUP", "STEP 9d: SalesViewModel creation START")
                        val factory = SalesViewModelFactory(coreContainer)
                        val vm = viewModel(factory = factory)
                        Log.e("STARTUP", "STEP 9d: SalesViewModel creation SUCCESS")
                        vm
                    } catch (e: Exception) {
                        Log.e("STARTUP", "STEP 9d FAILED: SalesViewModel creation", e)
                        null
                    } as? com.example.ui.SalesViewModel

                    val safeReportsViewModel = try {
                        Log.e("STARTUP", "STEP 9e: ReportsViewModel creation START")
                        val factory = ReportsViewModelFactory(coreContainer)
                        val vm = viewModel(factory = factory)
                        Log.e("STARTUP", "STEP 9e: ReportsViewModel creation SUCCESS")
                        vm
                    } catch (e: Exception) {
                        Log.e("STARTUP", "STEP 9e FAILED: ReportsViewModel creation", e)
                        null
                    } as? com.example.ui.ReportsViewModel

                    val safeWalletViewModel = try {
                        Log.e("STARTUP", "STEP 9f: WalletViewModel creation START")
                        val factory = WalletViewModelFactory(coreContainer)
                        val vm = viewModel(factory = factory)
                        Log.e("STARTUP", "STEP 9f: WalletViewModel creation SUCCESS")
                        vm
                    } catch (e: Exception) {
                        Log.e("STARTUP", "STEP 9f FAILED: WalletViewModel creation", e)
                        null
                    } as? com.example.ui.WalletViewModel

                    val safeMikrotikViewModel = try {
                        Log.e("STARTUP", "STEP 9g: MikrotikViewModel creation START")
                        val factory = MikrotikViewModelFactory(coreContainer)
                        val vm = viewModel(factory = factory)
                        Log.e("STARTUP", "STEP 9g: MikrotikViewModel creation SUCCESS")
                        vm
                    } catch (e: Exception) {
                        Log.e("STARTUP", "STEP 9g FAILED: MikrotikViewModel creation", e)
                        null
                    } as? com.example.ui.MikrotikViewModel

                    if (safeDistributorViewModel == null || safeDashboardViewModel == null ||
                        safeInventoryViewModel == null || safeSalesViewModel == null ||
                        safeReportsViewModel == null || safeWalletViewModel == null ||
                        safeMikrotikViewModel == null) {
                        Log.e("STARTUP", "STEP 9 FAILED: One or more MAIN screen ViewModels is null")
                        SafeErrorScreen(message = "خطأ في تحميل بيانات الشاشة الرئيسية")
                        return@Box
                    }

                    Log.e("STARTUP", "STEP 8: Navigation -> MAIN screen SUCCESS")
                    MainDashboardScreen(
                        mainViewModel = safeMainViewModel,
                        authViewModel = authViewModel,
                        settingsViewModel = safeSettingsViewModel,
                        distributorViewModel = safeDistributorViewModel,
                        dashboardViewModel = safeDashboardViewModel,
                        inventoryViewModel = safeInventoryViewModel,
                        salesViewModel = safeSalesViewModel,
                        reportsViewModel = safeReportsViewModel,
                        walletViewModel = safeWalletViewModel,
                        mikrotikViewModel = safeMikrotikViewModel,
                        onLogout = {
                            authViewModel.logout()
                            try {
                                com.example.network.SyncService.stopService(context)
                            } catch (e: Exception) {
                                Log.e("MainActivity", "Failed to stop SyncService", e)
                            }
                        }
                    )
                }
            }
            
            // Permission dialog
            AnimatedVisibility(
                visible = isActivated && !hasSmsPermissions,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(modifier = Modifier.fillMaxSize().background(DeepBlack.copy(alpha = 0.85f)).padding(24.dp), contentAlignment = Alignment.Center) {
                    Card(colors = CardDefaults.cardColors(containerColor = SurfaceDark), shape = RoundedCornerShape(16.dp), border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f)), modifier = Modifier.fillMaxWidth().testTag("permission_dialog_card")) {
                        Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            Icon(imageVector = Icons.Default.Sms, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(56.dp))
                            Text("مطلوب صلاحيات الرسائل SMS", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = GoldPrimary, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                            Text("لكي يعمل تطبيق كروت الدحشة بشكل صحيح، يحتاج إلى صلاحية قراءة واستقبال وإرسال رسائل SMS.", fontSize = 14.sp, color = PureWhite, lineHeight = 20.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                            Button(onClick = { permissionLauncher.launch(requiredPermissions) }, colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = DeepBlack), shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth().height(48.dp).testTag("btn_request_permissions")) {
                                Text("منح الصلاحيات المطلوبة والبدء", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }
                        }
                    }
                }
            }
        }
    }
    Log.e("STARTUP", "STEP 7a: SafeAppShell END")
}

    val authViewModel = try {
        val factory = AuthViewModelFactory(coreContainer.cardRepository, coreContainer)
        viewModel(factory = factory)
    } catch (e: Exception) {
        startupError = "فشل تحميل بيانات المصادقة"
        null
    } as? com.example.ui.AuthViewModel

    val smsViewModel = try {
        val factory = SmsViewModelFactory(coreContainer.cardRepository, coreContainer)
        viewModel(factory = factory)
    } catch (e: Exception) {
        null
    } as? com.example.ui.SmsViewModel

    val settingsViewModel = try {
        val factory = SettingsViewModelFactory(coreContainer.cardRepository, coreContainer)
        viewModel(factory = factory)
    } catch (e: Exception) {
        null
    } as? com.example.ui.SettingsViewModel

    val activationViewModel = try {
        val factory = ActivationViewModelFactory(authViewModel ?: return, coreContainer.cardRepository)
        viewModel(factory = factory)
    } catch (e: Exception) {
        null
    } as? com.example.ui.ActivationViewModel

    if (startupError != null || authViewModel == null || activationViewModel == null) {
        SafeErrorScreen(message = startupError ?: "خطأ في تحميل بيانات التطبيق")
        return
    }

    var currentScreen by remember { mutableStateOf(AppScreen.WELCOME) }
    LaunchedEffect(currentScreen) {
        Log.d("MainActivity", "SafeAppShell screen: $currentScreen")
    }

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

    var safeMainViewModel = mainViewModel
    var safeSettingsViewModel = settingsViewModel
    var safeSmsViewModel = smsViewModel

    if (safeMainViewModel == null || safeSettingsViewModel == null) {
        SafeErrorScreen(message = "خطأ في تحميل الإعدادات")
        return
    }

    val isDarkTheme by safeMainViewModel.isDarkTheme.collectAsState()
    LaunchedEffect(isDarkTheme) {
        com.example.ui.theme.isDarkThemeState.value = isDarkTheme
    }
    
    val isActivated by authViewModel.isActivated.collectAsState()
    val isTrialActive by authViewModel.isTrialActive.collectAsState()
    val isInitialLoginDone by authViewModel.isInitialLoginDone.collectAsState()
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
    val isPinEnabled by remember { mutableStateOf(coreContainer.cardRepository.isAppPinEnabled()) }
    
    LaunchedEffect(isActivated, isTrialActive, isInitialLoginDone, isLoggedIn, isPinEnabled, hasSmsPermissions) {
        currentScreen = when {
            isActivated || isTrialActive -> {
                if (isPinEnabled && !isLoggedIn) AppScreen.PIN_LOCK
                else if (!isInitialLoginDone) AppScreen.WELCOME
                else AppScreen.MAIN
            }
            else -> AppScreen.ACTIVATION
        }
        if (currentScreen == AppScreen.MAIN && !hasSmsPermissions) {
            permissionLauncher.launch(requiredPermissions)
        }
    }
    
    Scaffold(
        modifier = Modifier.fillMaxSize().testTag("app_scaffold")
    ) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize().background(DeepBlack).padding(innerPadding)
        ) {
            when (currentScreen) {
                AppScreen.WELCOME -> {
                    LoginScreen(
                        authViewModel = authViewModel,
                        mainViewModel = safeMainViewModel,
                        smsViewModel = safeSmsViewModel,
                        onLoginSuccess = {}
                    )
                }
                AppScreen.ACTIVATION -> {
                    ActivationScreen(
                        activationViewModel = activationViewModel,
                        onActivationSuccess = {}
                    )
                }
                AppScreen.PIN_LOCK -> {
                    PinLockScreen(
                        authViewModel = authViewModel,
                        onUnlocked = {}
                    )
                }
                AppScreen.MAIN -> {
                    val safeDistributorViewModel = try {
                        val factory = DistributorViewModelFactory(coreContainer.cardRepository, coreContainer)
                        viewModel(factory = factory)
                    } catch (e: Exception) {
                        Log.e("MainActivity", "Failed to create DistributorViewModel", e)
                        null
                    } as? com.example.ui.DistributorViewModel

                    val safeDashboardViewModel = try {
                        val factory = DashboardViewModelFactory(coreContainer)
                        viewModel(factory = factory)
                    } catch (e: Exception) {
                        Log.e("MainActivity", "Failed to create DashboardViewModel", e)
                        null
                    } as? com.example.ui.DashboardViewModel

                    val safeInventoryViewModel = try {
                        val factory = InventoryViewModelFactory(coreContainer)
                        viewModel(factory = factory)
                    } catch (e: Exception) {
                        Log.e("MainActivity", "Failed to create InventoryViewModel", e)
                        null
                    } as? com.example.ui.InventoryViewModel

                    val safeSalesViewModel = try {
                        val factory = SalesViewModelFactory(coreContainer)
                        viewModel(factory = factory)
                    } catch (e: Exception) {
                        Log.e("MainActivity", "Failed to create SalesViewModel", e)
                        null
                    } as? com.example.ui.SalesViewModel

                    val safeReportsViewModel = try {
                        val factory = ReportsViewModelFactory(coreContainer)
                        viewModel(factory = factory)
                    } catch (e: Exception) {
                        Log.e("MainActivity", "Failed to create ReportsViewModel", e)
                        null
                    } as? com.example.ui.ReportsViewModel

                    val safeWalletViewModel = try {
                        val factory = WalletViewModelFactory(coreContainer)
                        viewModel(factory = factory)
                    } catch (e: Exception) {
                        Log.e("MainActivity", "Failed to create WalletViewModel", e)
                        null
                    } as? com.example.ui.WalletViewModel

                    val safeMikrotikViewModel = try {
                        val factory = MikrotikViewModelFactory(coreContainer)
                        viewModel(factory = factory)
                    } catch (e: Exception) {
                        Log.e("MainActivity", "Failed to create MikrotikViewModel", e)
                        null
                    } as? com.example.ui.MikrotikViewModel

                    if (safeDistributorViewModel == null || safeDashboardViewModel == null ||
                        safeInventoryViewModel == null || safeSalesViewModel == null ||
                        safeReportsViewModel == null || safeWalletViewModel == null ||
                        safeMikrotikViewModel == null) {
                        SafeErrorScreen(message = "خطأ في تحميل بيانات الشاشة الرئيسية")
                        return@Box
                    }

                    LaunchedEffect(Unit) {
                        try {
                            com.example.network.SyncService.startService(context)
                        } catch (e: Exception) {
                            Log.e("MainActivity", "Failed to start SyncService", e)
                        }
                    }
                    MainDashboardScreen(
                        mainViewModel = safeMainViewModel,
                        authViewModel = authViewModel,
                        settingsViewModel = safeSettingsViewModel,
                        distributorViewModel = safeDistributorViewModel,
                        dashboardViewModel = safeDashboardViewModel,
                        inventoryViewModel = safeInventoryViewModel,
                        salesViewModel = safeSalesViewModel,
                        reportsViewModel = safeReportsViewModel,
                        walletViewModel = safeWalletViewModel,
                        mikrotikViewModel = safeMikrotikViewModel,
                        onLogout = {
                            authViewModel.logout()
                            try {
                                com.example.network.SyncService.stopService(context)
                            } catch (e: Exception) {
                                Log.e("MainActivity", "Failed to stop SyncService", e)
                            }
                        }
                    )
                }
            }
            
            // Permission dialog
            AnimatedVisibility(
                visible = isActivated && !hasSmsPermissions,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(modifier = Modifier.fillMaxSize().background(DeepBlack.copy(alpha = 0.85f)).padding(24.dp), contentAlignment = Alignment.Center) {
                    Card(colors = CardDefaults.cardColors(containerColor = SurfaceDark), shape = RoundedCornerShape(16.dp), border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f)), modifier = Modifier.fillMaxWidth().testTag("permission_dialog_card")) {
                        Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            Icon(imageVector = Icons.Default.Sms, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(56.dp))
                            Text("مطلوب صلاحيات الرسائل SMS", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = GoldPrimary, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                            Text("لكي يعمل تطبيق كروت الدحشة بشكل صحيح، يحتاج إلى صلاحية قراءة واستقبال وإرسال رسائل SMS.", fontSize = 14.sp, color = PureWhite, lineHeight = 20.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                            Button(onClick = { permissionLauncher.launch(requiredPermissions) }, colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = DeepBlack), shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth().height(48.dp).testTag("btn_request_permissions")) {
                                Text("منح الصلاحيات المطلوبة والبدء", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
