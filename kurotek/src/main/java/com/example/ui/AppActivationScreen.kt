package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.CopyAll
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.KurotekApplication
import com.example.database.CardRepository
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppActivationScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateToLicense: () -> Unit = {}
) {
    val context = LocalContext.current
    val app = context.applicationContext as KurotekApplication
    
    val viewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(
            repository = CardRepository(context),
            coreContainer = app.coreContainer
        )
    )

    var activationKey by remember { mutableStateOf("") }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    
    val isLoading by viewModel.licenseLoading.collectAsState()

    Scaffold(
        containerColor = ZNetBackgroundDark
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            
            // Header Image (Simulated icon)
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(ZNetSurfaceDark),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Key,
                    contentDescription = null,
                    tint = TealPrimary,
                    modifier = Modifier.size(50.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "تفعيل التطبيق",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = PureWhite
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "يرجى إدخال مفتاح التفعيل للتحقق من الصلاحيات",
                fontSize = 14.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            OutlinedTextField(
                value = activationKey,
                onValueChange = { activationKey = it },
                label = { Text("مفتاح التفعيل", color = TextSecondary) },
                leadingIcon = { Icon(Icons.Outlined.Key, contentDescription = null, tint = TealPrimary) },
                trailingIcon = { 
                    IconButton(onClick = { /* Paste from clipboard */ }) {
                         Icon(Icons.Outlined.CopyAll, contentDescription = "Paste", tint = TealPrimary)
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TealPrimary,
                    unfocusedBorderColor = BorderDark,
                    focusedTextColor = PureWhite,
                    unfocusedTextColor = PureWhite
                ),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = { 
                    if(activationKey.isNotBlank()) {
                        viewModel.activateLicense(activationKey) { success, msg ->
                            if (success) {
                                showSuccessDialog = true
                            } else {
                                errorMessage = msg
                                showErrorDialog = true
                            }
                        }
                    }
                },
                enabled = !isLoading && activationKey.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = PureWhite, modifier = Modifier.size(24.dp))
                } else {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.Check, contentDescription = null, tint = PureWhite)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("تفعيل الآن", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PureWhite)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedButton(
                onClick = { /* Request Key via WhatsApp/Support */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TealPrimary),
                border = androidx.compose.foundation.BorderStroke(1.dp, TealPrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.Send, contentDescription = null, tint = TealPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("طلب مفتاح تفعيل", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
        
        if (showSuccessDialog) {
            AlertDialog(
                onDismissRequest = { 
                    showSuccessDialog = false 
                    onNavigateToLicense()
                },
                title = { Text("تم التفعيل", color = PureWhite, fontWeight = FontWeight.Bold) },
                text = { Text("تم تفعيل التطبيق بنجاح. يمكنك الآن استخدام كافة الصلاحيات.", color = TextSecondary) },
                confirmButton = {
                    TextButton(onClick = { 
                        showSuccessDialog = false
                        onNavigateToLicense()
                    }) {
                        Text("موافق", color = TealPrimary)
                    }
                },
                containerColor = ZNetSurfaceDark,
                titleContentColor = PureWhite,                
                textContentColor = TextSecondary
            )
        }

        if (showErrorDialog) {
            AlertDialog(
                onDismissRequest = { showErrorDialog = false },
                title = { Text("خطأ في التفعيل", color = StatusRed, fontWeight = FontWeight.Bold) },
                text = { Text(errorMessage, color = TextSecondary) },
                confirmButton = {
                    TextButton(onClick = { showErrorDialog = false }) {
                        Text("حسناً", color = StatusRed)
                    }
                },
                containerColor = ZNetSurfaceDark,
                titleContentColor = StatusRed,
                textContentColor = TextSecondary
            )
        }
    }
}
