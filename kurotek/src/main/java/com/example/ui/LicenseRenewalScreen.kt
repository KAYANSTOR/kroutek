package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.KurotekApplication
import com.example.database.CardRepository
import com.example.ui.theme.TealPrimary
import com.example.ui.theme.StatusRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LicenseRenewalScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val app = context.applicationContext as KurotekApplication
    
    val viewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(
            repository = CardRepository(context),
            coreContainer = app.coreContainer
        )
    )

    val lightBackground = Color(0xFFF5F7F9)
    val textPrimary = Color(0xFF1A1A1A)
    
    var licenseKey by remember { mutableStateOf("") }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    
    val isLoading by viewModel.licenseLoading.collectAsState()
    
    val licenseStatus by viewModel.licenseStatus.collectAsState()

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { 
                        Text("تجديد الترخيص", color = textPrimary, fontWeight = FontWeight.Bold, fontSize = 20.sp, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.Outlined.ArrowForward, contentDescription = "رجوع", tint = textPrimary)
                        }
                    },
                    actions = {
                        Spacer(modifier = Modifier.width(48.dp))
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = lightBackground)
                )
            },
            containerColor = lightBackground
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // License Summary Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Outlined.ReceiptLong, contentDescription = null, tint = TealPrimary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("ملخص الترخيص", color = textPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            }
                            
                            val isExpired = licenseStatus?.state?.name == "EXPIRED"
                            Box(modifier = Modifier.background(if (isExpired) StatusRed.copy(alpha = 0.15f) else Color(0xFFE8F5E9), RoundedCornerShape(16.dp)).padding(horizontal = 12.dp, vertical = 4.dp)) {
                                Text(if (isExpired) "منتهي" else "ساري", color = if (isExpired) StatusRed else Color(0xFF4CAF50), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        Divider(color = Color(0xFFF0F0F0))
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        LicenseSummaryRow("حالة الترخيص", licenseStatus?.state?.name ?: "غير معروف")
                        Spacer(modifier = Modifier.height(12.dp))
                        LicenseSummaryRow("السيريال المربوط", licenseStatus?.licenseKey?.take(8)?.let { "$it..." } ?: "لا يوجد")
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // We could format the expiry date properly if available
                        // LicenseSummaryRow("تاريخ الانتهاء", licenseStatus?.expiryDate.toString())
                    }
                }
                
                // Key Input Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text("أدخل مفتاح التجديد", color = textPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        OutlinedTextField(
                            value = licenseKey,
                            onValueChange = { licenseKey = it },
                            placeholder = { Text("مفتاح التجديد", color = Color(0xFF999999)) },
                            leadingIcon = { Icon(Icons.Outlined.VpnKey, contentDescription = null, tint = Color(0xFF666666)) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = TealPrimary,
                                unfocusedBorderColor = Color(0xFFE0E0E0),
                            ),
                            singleLine = true
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Button(
                            onClick = { 
                                if (licenseKey.isNotBlank()) {
                                    viewModel.activateLicense(licenseKey) { success, msg ->
                                        if (success) {
                                            showSuccessDialog = true
                                        } else {
                                            errorMessage = msg
                                            showErrorDialog = true
                                        }
                                    }
                                }
                            },
                            enabled = !isLoading && licenseKey.isNotBlank(),
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                            } else {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Outlined.Refresh, contentDescription = null, tint = Color.White)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("تجديد الترخيص", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            if (showSuccessDialog) {
                AlertDialog(
                    onDismissRequest = { 
                        showSuccessDialog = false 
                        onNavigateBack()
                    },
                    title = { Text("تم التجديد", color = textPrimary, fontWeight = FontWeight.Bold) },
                    text = { Text("تم تجديد الترخيص بنجاح.", color = Color(0xFF666666)) },
                    confirmButton = {
                        TextButton(onClick = { 
                            showSuccessDialog = false
                            onNavigateBack()
                        }) {
                            Text("موافق", color = TealPrimary)
                        }
                    },
                    containerColor = Color.White,
                    titleContentColor = textPrimary,                
                    textContentColor = Color(0xFF666666)
                )
            }

            if (showErrorDialog) {
                AlertDialog(
                    onDismissRequest = { showErrorDialog = false },
                    title = { Text("خطأ في التجديد", color = StatusRed, fontWeight = FontWeight.Bold) },
                    text = { Text(errorMessage, color = Color(0xFF666666)) },
                    confirmButton = {
                        TextButton(onClick = { showErrorDialog = false }) {
                            Text("حسناً", color = StatusRed)
                        }
                    },
                    containerColor = Color.White,
                    titleContentColor = StatusRed,
                    textContentColor = Color(0xFF666666)
                )
            }
        }
    }
}
