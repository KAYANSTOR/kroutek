package com.example.ui

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.security.DeviceSecurity
import com.example.security.SecurityApiService
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivationScreen(
    authViewModel: AuthViewModel,
    mainViewModel: MainViewModel,
    smsViewModel: SettingsViewModel,
    onActivationSuccess: () -> Unit
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    
    var currentStep by remember { mutableStateOf(0) }
    var selectedVersion by remember { mutableStateOf("") }
    var phoneInput by remember { mutableStateOf("") }
    var networkNameInput by remember { mutableStateOf("") }
    var activationKeyInput by remember { mutableStateOf("") }
    var isActivating by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A1A))
            .testTag("activation_screen"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Header Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(100.dp)
                        .background(
                            Color(0xFF1A9B8E),
                            RoundedCornerShape(50.dp)
                        )
                ) {
                    Text(
                        text = "Z",
                        color = Color.White,
                        fontSize = 54.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                
                Text(
                    text = "تفعيل تطبيق زدنت",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                
                Text(
                    text = "أدخل بيانات التفعيل أو بدء النسخة التجريبية المجانية",
                    color = Color(0xFFB0B0B0),
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )
            }

            // Progress Indicator
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(4) { index ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(4.dp)
                            .background(
                                if (index <= currentStep) Color(0xFF1A9B8E) else Color(0xFF333333),
                                RoundedCornerShape(2.dp)
                            )
                    )
                }
            }

            // Content based on step
            when (currentStep) {
                0 -> VersionSelectionStep(
                    selectedVersion = selectedVersion,
                    onVersionSelected = { selectedVersion = it }
                )
                1 -> PhoneInputStep(
                    phoneInput = phoneInput,
                    onPhoneChange = { phoneInput = it }
                )
                2 -> NetworkNameStep(
                    networkName = networkNameInput,
                    onNetworkNameChange = { networkNameInput = it }
                )
                3 -> ActivationKeyStep(
                    activationKey = activationKeyInput,
                    onKeyChange = { activationKeyInput = it },
                    errorMessage = errorMessage
                )
            }

            // Navigation Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (currentStep > 0) {
                    OutlinedButton(
                        onClick = { currentStep-- },
                        modifier = Modifier
                            .weight(0.8f)
                            .height(48.dp),
                        border = BorderStroke(1.dp, Color(0xFF1A9B8E)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "رجوع",
                            color = Color(0xFF1A9B8E),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Button(
                    onClick = {
                        when (currentStep) {
                            0 -> if (selectedVersion.isNotEmpty()) currentStep++
                            1 -> if (phoneInput.isNotEmpty()) currentStep++
                            2 -> if (networkNameInput.isNotEmpty()) currentStep++
                            3 -> {
                                if (activationKeyInput.isNotEmpty()) {
                                    isActivating = true
                                    // Simulate activation
                                    Toast.makeText(context, "جاري تفعيل الترخيص...", Toast.LENGTH_SHORT).show()
                                    onActivationSuccess()
                                }
                            }
                        }
                    },
                    enabled = !isActivating && when (currentStep) {
                        0 -> selectedVersion.isNotEmpty()
                        1 -> phoneInput.isNotEmpty()
                        2 -> networkNameInput.isNotEmpty()
                        3 -> activationKeyInput.isNotEmpty()
                        else -> false
                    },
                    modifier = Modifier
                        .weight(1.2f)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1A9B8E),
                        disabledContainerColor = Color(0xFF1A9B8E).copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isActivating) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Text(
                            text = if (currentStep == 3) "تفعيل الآن" else "التالي",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Warning Text
            Text(
                text = "بعد التفعيل، سيتم ربط الترخيص بهذا الجهاز ولا يمكن نقله لجهاز آخر",
                color = Color(0xFF999999),
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            // Support Contact
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { },
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1A9B8E).copy(alpha = 0.15f)
                ),
                border = BorderStroke(1.dp, Color(0xFF1A9B8E).copy(alpha = 0.3f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = null,
                        tint = Color(0xFF1A9B8E),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "للدعم: 773303455",
                        color = Color(0xFF1A9B8E),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun VersionSelectionStep(
    selectedVersion: String,
    onVersionSelected: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "اختر نسخة التطبيق",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        listOf(
            "باقة النسخة التجريبية المجانية",
            "رقم الجوال (773303455)",
            "اسم الشبكة (كيان تك)"
        ).forEach { version ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onVersionSelected(version) },
                colors = CardDefaults.cardColors(
                    containerColor = if (selectedVersion == version) 
                        Color(0xFF1A9B8E).copy(alpha = 0.2f) 
                    else 
                        Color(0xFF2A2A2A)
                ),
                border = BorderStroke(
                    1.dp,
                    if (selectedVersion == version) 
                        Color(0xFF1A9B8E) 
                    else 
                        Color(0xFF333333)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = version,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    if (selectedVersion == version) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF1A9B8E),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PhoneInputStep(
    phoneInput: String,
    onPhoneChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "رقم الجوال",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        
        OutlinedTextField(
            value = phoneInput,
            onValueChange = onPhoneChange,
            label = { Text("أدخل رقم الجوال") },
            placeholder = { Text("773XXXXXX") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("phone_input"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF1A9B8E),
                unfocusedBorderColor = Color(0xFF333333),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp)
        )
    }
}

@Composable
private fun NetworkNameStep(
    networkName: String,
    onNetworkNameChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "اسم الشبكة",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        
        OutlinedTextField(
            value = networkName,
            onValueChange = onNetworkNameChange,
            label = { Text("أدخل اسم الشبكة") },
            placeholder = { Text("مثال: كيان تك") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("network_name_input"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF1A9B8E),
                unfocusedBorderColor = Color(0xFF333333),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp)
        )
    }
}

@Composable
private fun ActivationKeyStep(
    activationKey: String,
    onKeyChange: (String) -> Unit,
    errorMessage: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "رمز التفعيل",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        
        OutlinedTextField(
            value = activationKey,
            onValueChange = { onKeyChange(it) },
            label = { Text("أدخل رمز التفعيل") },
            placeholder = { Text("XXXX-XXXX-XXXX") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("activation_key_input"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF1A9B8E),
                unfocusedBorderColor = Color(0xFF333333),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp)
        )
        
        if (errorMessage.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFF6B6B).copy(alpha = 0.15f)
                ),
                border = BorderStroke(1.dp, Color(0xFFFF6B6B).copy(alpha = 0.3f)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = errorMessage,
                    color = Color(0xFFFF6B6B),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    }
}
