package com.example.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    
    // Start at login
    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                onLoginSuccess = { navController.navigate("home") { popUpTo("login") { inclusive = true } } }
            )
        }
        composable("home") {
            HomeScreen(
                onNavigate = { route -> navController.navigate(route) }
            )
        }
        composable("Wallets") {
            WalletAndPosManagementScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAddPos = { navController.navigate("AddPos") },
                onNavigateToAddWallet = { navController.navigate("AddWallet") }
            )
        }
        composable("AddPos") {
            AddPosScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("AddWallet") {
            AddWalletScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("SmsTemplates") {
            MessageTemplatesScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("RejectedMessages") {
            MessageLogsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("HelpCenter") {
            HelpCenterScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("SimSettings") {
            SimSettingsScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable("Settings") {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToLicense = { navController.navigate("LicenseRenewal") },
                onNavigateToSimSettings = { navController.navigate("SimSettings") }
            )
        }
        composable("LicenseRenewal") {
            LicenseRenewalScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("Reports") {
            ReportsAndMonitoringScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToRejectedMessages = { navController.navigate("RejectedMessages") },
                onNavigateToPendingMessages = { navController.navigate("PendingMessages") },
                onNavigateToOperationsLog = { navController.navigate("OperationsLog") },
                onNavigateToSalesReport = { navController.navigate("SalesReport") }, // We will create this next
                onNavigateToPOSAccounts = { navController.navigate("AccountsLedger") } // Using the existing POSReportsScreen for now or accounts ledger
            )
        }
        composable("PendingMessages") {
            PendingMessagesScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable("SalesReport") {
            SalesReportScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable("OperationsLog") {
            OperationsLogScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable("AccountsLedger") {
            AccountsLedgerScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable("CardsManagement") {
            CardsManagementScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCategories = { navController.navigate("CategoriesManagement") },
                onShowImportOptions = { /* Handle bottom sheet */ },
                onShowAddCardOptions = { /* Handle bottom sheet */ }
            )
        }
        composable("CategoriesManagement") {
            CategoriesManagementScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable("Activation") {
            AppActivationScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToLicense = { navController.navigate("License") { popUpTo("Activation") { inclusive = true } } }
            )
        }
        composable("License") {
            LicenseActivationScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
