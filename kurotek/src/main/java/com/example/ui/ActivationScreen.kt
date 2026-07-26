package com.example.ui

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
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
    val isDark by mainViewModel.isDarkTheme.collectAsState()
    var serialInput by remember { mutableStateOf("") }
    var serialVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var isVerifying by remember { mutableStateOf(false) }
    var currentStep by remember { mutableStateOf(0) } // 0: version selection, 1: device ID, 2: network, 3: activation key
    var selectedVersion by remember { mutableStateOf("") }
    var deviceNameInput by remember { mutableStateOf("") }
    var networkNameInput by remember { mutableStateOf("") }
    
    val keyboardController = LocalSoftwareKeyboardController.current
    val clipboardManager = LocalClipboardManager.current
    val uriHandler = LocalUriHandler.current
    
    val deviceId = remember { DeviceSecurity.getSecureDeviceId(context) }
    val isRooted = remember { DeviceSecurity.isDeviceRooted() }
    val isEmulator = remember { DeviceSecurity.isRunningOnEmulator() }

    val handleActivationAttempt = {
        val trimmedInput = serialInput.trim()
        if (trimmedInput.isEmpty()) {
            errorMessage = "يرجى إدخال رمز التفعيل أولاً"
        } else {
            errorMessage = ""
            isVerifying = true
            
            SecurityApiService.validateSerial(context, trimmedInput, deviceId) { success, message ->
                isVerifying = false
                if (success) {
                    errorMessage = ""
                    authViewModel.setActivated(true, trimmedInput)
                    authViewModel.setInitialLoginDone(true)
                    keyboardController?.hide()
                    Toast.makeText(context, "تم تفعيل التطبيق بنجاح", Toast.LENGTH_LONG).show()
                    onActivationSuccess()
                } else {
                    errorMessage = message
                    serialInput = ""
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepBlack)
            .testTag("activation_screen_container"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Header Logo Section
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
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF1A9B8E),
                                    Color(0xFFE85E97),
                                    Color(0xFFC2185B)
                                )
                            ),
                            CircleShape
                        )
                ) {
                    Text(
                        text = "Z",
                        color = PureWhite,
                        fontSize = 54.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Text(
                    text = "تفعيل تطبيق زدنت",
                    color = PureWhite,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "تفعيل التطبيق والخدمات الموثوقة",
                    color = TextSecondary,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }

            // Steps Indicator
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(4) { step ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(4.dp)
                            .background(
                                if (step <= currentStep) Color(0xFF1A9B8E) else Color.White.copy(alpha = 0.1f),
                                RoundedCornerShape(2.dp)
                            )
                    )
                }
            }

            // Content based on current step
            when (currentStep) {
                0 -> {
                    // Version Selection
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "اختر نسخة التطبيق",
                                color = PureWhite,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.fillMaxWidth()
                            )

                            listOf(
                                "نسخة تجريبية مجانية",
                                "نسخة الاشتراك السنوي",
                                "نسخة احترافية"
                            ).forEach { version ->
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (selectedVersion == version) Color(0xFF1A9B8E).copy(alpha = 0.2f) else SurfaceLight
                                    ),
                                    border = BorderStroke(
                                        1.dp,
                                        if (selectedVersion == version) Color(0xFF1A9B8E) else Color.White.copy(alpha = 0.1f)
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { selectedVersion = version }
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(
                                            modifier = Modifier.weight(1f),
                                            verticalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text(
                                                text = version,
                                                color = PureWhite,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = when (version) {
                                                    "نسخة تجريبية مجانية" -> "30 يوم مجاني"
                                                    "نسخة الاشتراك السنوي" -> "سنة واحدة"
                                                    else -> "نسخة احترافية"
                                                },
                                                color = TextSecondary,
                                                fontSize = 11.sp
                                            )
                                        }
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
                }

                1 -> {
                    // Device ID Entry
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "معرف الجهاز",
                                color = PureWhite,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.3f)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = deviceId,
                                        color = Color(0xFF1A9B8E),
                                        fontSize = 12.sp,
                                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                        modifier = Modifier.weight(1f)
                                    )
                                    IconButton(
                                        onClick = {
                                            clipboardManager.setText(AnnotatedString(deviceId))
                                            Toast.makeText(context, "تم النسخ", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.ContentCopy,
                                            contentDescription = null,
                                            tint = Color(0xFF1A9B8E),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }

                            Text(
                                text = "أرسل معرف الجهاز للموزع للحصول على رمز التفعيل",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )

                            OutlinedTextField(
                                value = deviceNameInput,
                                onValueChange = { deviceNameInput = it },
                                label = { Text("اسم الجهاز") },
                                placeholder = { Text("مثال: متجر الرياض") },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF1A9B8E),
                                    unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                                    focusedLabelColor = Color(0xFF1A9B8E),
                                    unfocusedLabelColor = TextSecondary,
                                    focusedTextColor = PureWhite,
                                    unfocusedTextColor = PureWhite
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                2 -> {
                    // Network Name Entry
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "اسم الشبكة",
                                color = PureWhite,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = "أدخل اسم الشبكة الخاصة بك",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )

                            OutlinedTextField(
                                value = networkNameInput,
                                onValueChange = { networkNameInput = it },
                                label = { Text("اسم الشبكة") },
                                placeholder = { Text("مثال: Z Net Premium") },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF1A9B8E),
                                    unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                                    focusedLabelColor = Color(0xFF1A9B8E),
                                    unfocusedLabelColor = TextSecondary,
                                    focusedTextColor = PureWhite,
                                    unfocusedTextColor = PureWhite
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                3 -> {
                    // Activation Key Entry
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "رمز التفعيل",
                                color = PureWhite,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )

                            OutlinedTextField(
                                value = serialInput,
                                onValueChange = {
                                    serialInput = it
                                    errorMessage = ""
                                },
                                label = { Text("أدخل رمز التفعيل") },
                                placeholder = { Text("XXXX-XXXX-XXXX") },
                                singleLine = true,
                                visualTransformation = if (serialVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Password,
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(onDone = { handleActivationAttempt() }),
                                trailingIcon = {
                                    val icon = if (serialVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff
                                    IconButton(onClick = { serialVisible = !serialVisible }) {
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = null,
                                            tint = Color(0xFF1A9B8E),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF1A9B8E),
                                    unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                                    focusedLabelColor = Color(0xFF1A9B8E),
                                    unfocusedLabelColor = TextSecondary,
                                    focusedTextColor = PureWhite,
                                    unfocusedTextColor = PureWhite,
                                    cursorColor = Color(0xFF1A9B8E)
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("activation_serial")
                            )

                            if (errorMessage.isNotEmpty()) {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = StatusRed.copy(alpha = 0.15f)),
                                    border = BorderStroke(1.dp, StatusRed.copy(alpha = 0.3f)),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = errorMessage,
                                        color = StatusRed,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(12.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Navigation Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (currentStep > 0) {
                    Button(
                        onClick = { currentStep-- },
                        colors = ButtonDefaults.buttonColors(containerColor = SurfaceLight),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Text(
                            text = "رجوع",
                            color = PureWhite,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Button(
                    onClick = {
                        when (currentStep) {
                            0 -> if (selectedVersion.isNotEmpty()) currentStep++
                            1 -> if (deviceNameInput.isNotEmpty()) currentStep++
                            2 -> if (networkNameInput.isNotEmpty()) currentStep++
                            3 -> handleActivationAttempt()
                        }
                    },
                    enabled = !isVerifying && when (currentStep) {
                        0 -> selectedVersion.isNotEmpty()
                        1 -> deviceNameInput.isNotEmpty()
                        2 -> networkNameInput.isNotEmpty()
                        3 -> serialInput.isNotEmpty()
                        else -> false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1A9B8E),
                        disabledContainerColor = Color(0xFF1A9B8E).copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(if (currentStep == 0) 1f else 1.5f)
                        .height(48.dp)
                        .testTag("activation_submit_button")
                ) {
                    if (isVerifying) {
                        CircularProgressIndicator(
                            color = PureWhite,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Text(
                            text = if (currentStep == 3) "تفعيل" else "التالي",
                            color = PureWhite,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Support Card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A9B8E).copy(alpha = 0.15f)),
                border = BorderStroke(1.dp, Color(0xFF1A9B8E).copy(alpha = 0.3f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        try {
                            uriHandler.openUri("https://wa.me/967773303455")
                        } catch (e: Exception) {
                            Toast.makeText(context, "فشل فتح الرابط", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .testTag("support_whatsapp_card")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    content = {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = null,
                            tint = Color(0xFF1A9B8E),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "تواصل مع الدعم: 773303455",
                            color = Color(0xFF1A9B8E),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                )
            }
        }
    }
}
