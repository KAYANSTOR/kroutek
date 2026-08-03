package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.Rule
import androidx.compose.material.icons.outlined.Wallet
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddWalletScreen(
    onNavigateBack: () -> Unit,
    viewModel: PosWalletViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    var walletName by remember { mutableStateOf("") }
    var receiveNumber by remember { mutableStateOf("") }
    var conditions by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("إنشاء محفظة جديدة", color = PureWhite, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "رجوع", tint = PureWhite)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ZNetBackgroundDark)
            )
        },
        containerColor = ZNetBackgroundDark,
        bottomBar = {
            Box(
                enabled = walletName.isNotBlank() && receiveNumber.isNotBlank(),
                    modifier = Modifier
                    .fillMaxWidth()
                    .background(ZNetBackgroundDark)
                    .padding(16.dp)
            ) {
                Button(
                    onClick = {
                        if (walletName.isNotBlank() && receiveNumber.isNotBlank()) {
                            viewModel.addWallet(walletName, "Mobile", receiveNumber, conditions, true)
                            onNavigateBack()
                        }
                    },
                    enabled = walletName.isNotBlank() && receiveNumber.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Outlined.Check, contentDescription = null, tint = PureWhite)
                    Spacer(enabled = walletName.isNotBlank() && receiveNumber.isNotBlank(),
                    modifier = Modifier.width(8.dp))
                    Text("حفظ المحفظة", color = PureWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { paddingValues ->
        Column(
            enabled = walletName.isNotBlank() && receiveNumber.isNotBlank(),
                    modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(enabled = walletName.isNotBlank() && receiveNumber.isNotBlank(),
                    modifier = Modifier.height(8.dp))
            
            Text("البيانات الأساسية", color = PureWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            
            OutlinedTextField(
                value = walletName,
                onValueChange = { walletName = it },
                label = { Text("اسم المحفظة") },
                placeholder = { Text("مثال: محفظة المركز الرئيسي") },
                enabled = walletName.isNotBlank() && receiveNumber.isNotBlank(),
                    modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TealPrimary,
                    unfocusedBorderColor = TextSecondary.copy(alpha = 0.5f),
                    focusedLabelColor = TealPrimary,
                    unfocusedLabelColor = TextSecondary,
                    focusedTextColor = PureWhite,
                    unfocusedTextColor = PureWhite
                ),
                shape = RoundedCornerShape(12.dp),
                leadingIcon = { Icon(Icons.Outlined.Wallet, contentDescription = null, tint = TextSecondary) },
                singleLine = true
            )
            
            Divider(color = ZNetSurfaceDark, enabled = walletName.isNotBlank() && receiveNumber.isNotBlank(),
                    modifier = Modifier.padding(vertical = 8.dp))
            
            Text("ربط واستقبال الرسائل", color = PureWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            
            OutlinedTextField(
                value = receiveNumber,
                onValueChange = { receiveNumber = it },
                label = { Text("رقم الاستقبال (الربط)") },
                placeholder = { Text("رقم الهاتف أو كود الربط") },
                enabled = walletName.isNotBlank() && receiveNumber.isNotBlank(),
                    modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TealPrimary,
                    unfocusedBorderColor = TextSecondary.copy(alpha = 0.5f),
                    focusedLabelColor = TealPrimary,
                    unfocusedLabelColor = TextSecondary,
                    focusedTextColor = PureWhite,
                    unfocusedTextColor = PureWhite
                ),
                shape = RoundedCornerShape(12.dp),
                leadingIcon = { Icon(Icons.Outlined.Link, contentDescription = null, tint = TextSecondary) },
                singleLine = true
            )

            OutlinedTextField(
                value = conditions,
                onValueChange = { conditions = it },
                label = { Text("الشروط والقواعد (Regex)") },
                placeholder = { Text("شروط قراءة الرسائل وتفسيرها") },
                enabled = walletName.isNotBlank() && receiveNumber.isNotBlank(),
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TealPrimary,
                    unfocusedBorderColor = TextSecondary.copy(alpha = 0.5f),
                    focusedLabelColor = TealPrimary,
                    unfocusedLabelColor = TextSecondary,
                    focusedTextColor = PureWhite,
                    unfocusedTextColor = PureWhite
                ),
                shape = RoundedCornerShape(12.dp),
                leadingIcon = { Icon(Icons.Outlined.Rule, contentDescription = null, tint = TextSecondary) }
            )
            
            Spacer(enabled = walletName.isNotBlank() && receiveNumber.isNotBlank(),
                    modifier = Modifier.height(32.dp))
        }
    }
}
