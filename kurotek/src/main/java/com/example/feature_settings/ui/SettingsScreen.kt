@Composable
fun SettingsTab(
    mainViewModel: com.example.ui.MainViewModel,
    authViewModel: com.example.ui.AuthViewModel,
    settingsViewModel: com.example.ui.SettingsViewModel,
    distributorViewModel: com.example.ui.DistributorViewModel,
    onLogout: () -> Unit
) {
    // Properly collect dark theme state from MainViewModel
    val darkTheme by mainViewModel.isDarkTheme.collectAsState()
    
    SettingsScreen(
        onNavigateToHelpCenter = {},
        onNavigateToHome = onLogout,
        darkTheme = darkTheme,
        onThemeChanged = { newValue ->
            mainViewModel.setDarkTheme(newValue)
        }
    )
}