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
fun LoginScreen(
    viewModel: MainViewModel,
    onLoginSuccess: () -> Unit
) {
    val context = LocalContext.current
    val isDark by viewModel.isDarkTheme.collectAsState()
    val savedNetworkName by viewModel.networkName.collectAsState()
    var networkNameInput by remember(savedNetworkName) { mutableStateOf(savedNetworkName) }
    var serialInput by remember { mutableStateOf("") }
    var serialVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var isVerifying by remember { mutableStateOf(false) }
    
    val keyboardController = LocalSoftwareKeyboardController.current
    val clipboardManager = LocalClipboardManager.current
    val uriHandler = LocalUriHandler.current
    
    // Dynamic Security checks
    val deviceId = remember { DeviceSecurity.getSecureDeviceId(context) }
    val isRooted = remember { DeviceSecurity.isDeviceRooted() }
    val isEmulator = remember { DeviceSecurity.isRunningOnEmulator() }

    val handleNormalEntry = {
        val trimmedNetwork = networkNameInput.trim()
        if (trimmedNetwork.isEmpty()) {
            errorMessage = "⚠️ يرجى إدخال اسم شبكتك الخاصة أولاً!"
        } else {
            errorMessage = ""
            viewModel.updateNetworkName(trimmedNetwork)
            viewModel.setInitialLoginDone(true)
            keyboardController?.hide()
            Toast.makeText(context, "⚡ تم حفظ اسم الشبكة والدخول للنظام بنجاح!", Toast.LENGTH_SHORT).show()
            onLoginSuccess()
        }
    }

    val handleActivationAttempt = {
        val trimmedInput = serialInput.trim()
        if (trimmedInput.isEmpty()) {
            errorMessage = "⚠️ يرجى إدخال رمز السيريال أولاً!"
        } else {
            errorMessage = ""
            isVerifying = true
            
            // Call senior secure endpoint verification with fallback
            SecurityApiService.validateSerial(context, trimmedInput, deviceId) { success, message ->
                isVerifying = false
                if (success) {
                    errorMessage = ""
                    viewModel.updateNetworkName(networkNameInput.trim().ifEmpty { "شبكة الدحشة" })
                    viewModel.setActivated(true, trimmedInput)
                    viewModel.setInitialLoginDone(true)
                    viewModel.verifyPassword("PY_7MD") // Automatic login session success trigger
                    keyboardController?.hide()
                    Toast.makeText(context, "🔑 تم تفعيل وترخيص التطبيق بنجاح!", Toast.LENGTH_LONG).show()
                    onLoginSuccess()
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
            .padding(24.dp)
            .testTag("activation_screen_container"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 460.dp)
                .navigationBarsPadding()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header Logo and App Title
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(100.dp)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(Color(0xFFE91E63).copy(alpha = 0.12f), Color.Transparent)
                            ),
                            CircleShape
                        )
                        .border(
                            BorderStroke(1.5.dp, Color(0xFFE91E63).copy(alpha = 0.25f)),
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Wifi,
                        contentDescription = "كروتك",
                        tint = GlowPurplePink,
                        modifier = Modifier.size(46.dp)
                    )
                }

                Text(
                    text = "كروتك (Kurotek)",
                    color = PureWhite,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "التحقق من التفعيل والترخيص الأمني 🔑",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }

            // Real-time Root and Emulator Warning Alerts (Enterprise Compliance)
            if (isRooted || isEmulator) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2D161B)),
                    border = BorderStroke(1.dp, Color(0xFFE91E63).copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(
                                text = "تنبيه بيئة غير موثوقة! 🛡️",
                                color = Color(0xFFFF5252),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Right
                            )
                            Text(
                                text = "تم كشف صلاحيات جذر (Root) أو محاكي تشغيل. يرجى المتابعة بحذر لضمان حماية كروت الطباعة.",
                                color = TextSecondary,
                                fontSize = 10.sp,
                                textAlign = TextAlign.Right,
                                lineHeight = 15.sp
                            )
                        }
                        Icon(
                            imageVector = Icons.Outlined.Warning,
                            contentDescription = "تحذير أمني",
                            tint = Color(0xFFFF5252),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            // Secure Device Identification Display Card
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "معرّف الجهاز الآمن (Device ID) 📱",
                        color = PureWhite,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Right,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(deviceId))
                                Toast.makeText(context, "📋 تم نسخ معرّف الجهاز للمحفظة!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ContentCopy,
                                contentDescription = "نسخ",
                                tint = GlowPurplePink,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        
                        Text(
                            text = deviceId,
                            color = GlowPurplePink,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                            textAlign = TextAlign.Left
                        )
                    }
                    
                    Text(
                        text = "* أرسل هذا المعرف للموزع/المدير ليقوم بتوليد السيريال المخصص لهاتفك لمنع الاستنساخ.",
                        color = TextSecondary,
                        fontSize = 9.sp,
                        textAlign = TextAlign.Right,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Main Activation Card
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                shape = RoundedCornerShape(22.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "بيانات التفعيل والترخيص",
                        color = PureWhite,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Right,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Network Name Input
                    OutlinedTextField(
                        value = networkNameInput,
                        onValueChange = {
                            networkNameInput = it
                            errorMessage = ""
                        },
                        label = { Text("اسم الشبكة") },
                        placeholder = { Text("مثال: شبكة الدحشة") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        ),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.WifiTethering, 
                                contentDescription = null, 
                                tint = GlowPurplePink.copy(alpha = 0.7f)
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GlowPurplePink,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                            focusedLabelColor = GlowPurplePink,
                            unfocusedLabelColor = TextSecondary,
                            focusedTextColor = PureWhite,
                            unfocusedTextColor = PureWhite,
                            cursorColor = GlowPurplePink
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("activation_network_name")
                    )

                    // Serial Number Input
                    OutlinedTextField(
                        value = serialInput,
                        onValueChange = {
                            serialInput = it
                            errorMessage = ""
                        },
                        label = { Text("السيريال نمبر المخصص (Activation Key)") },
                        placeholder = { Text("أدخل رمز السيريل هنا...") },
                        singleLine = true,
                        visualTransformation = if (serialVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { handleActivationAttempt() }
                        ),
                        trailingIcon = {
                            val icon = if (serialVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff
                            IconButton(onClick = { serialVisible = !serialVisible }) {
                                Icon(
                                    imageVector = icon, 
                                    contentDescription = null, 
                                    tint = GlowPurplePink.copy(alpha = 0.8f)
                                )
                            }
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Lock, 
                                contentDescription = null, 
                                tint = GlowPurplePink.copy(alpha = 0.7f)
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GlowPurplePink,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                            focusedLabelColor = GlowPurplePink,
                            unfocusedLabelColor = TextSecondary,
                            focusedTextColor = PureWhite,
                            unfocusedTextColor = PureWhite,
                            cursorColor = GlowPurplePink
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("activation_serial")
                    )

                    if (errorMessage.isNotEmpty()) {
                        Text(
                            text = errorMessage,
                            color = StatusRed,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    // Submit Trigger Button
                    Button(
                        onClick = { handleActivationAttempt() },
                        enabled = !isVerifying,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color.White,
                            disabledContainerColor = Color.Transparent,
                            disabledContentColor = Color.White.copy(alpha = 0.5f)
                        ),
                        contentPadding = PaddingValues(),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("activation_submit_button")
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(PurplePinkGradient)
                                .padding(horizontal = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isVerifying) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                            } else {
                                Text(
                                    text = "تأكيد التفعيل والربط بالجهاز",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (isVerifying) {
                        Text(
                            text = "⏳ قد يستغرق التفعيل حتى 30 ثانية عند أول محاولة، يرجى الانتظار...",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.85f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    // Normal System Entry Button
                    Button(
                        onClick = { handleNormalEntry() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF151922),
                            contentColor = GlowPurplePink
                        ),
                        border = BorderStroke(1.dp, GlowPurplePink.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("normal_entry_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Login,
                                contentDescription = null,
                                tint = GlowPurplePink,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "دخول اعتيادي للنظام مباشرة 🚀",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = GlowPurplePink
                            )
                        }
                    }
                }
            }

            // Help Support Information Display Card
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark.copy(alpha = 0.4f)),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        try {
                            uriHandler.openUri("https://wa.me/967773303455")
                        } catch (e: Exception) {
                            Toast.makeText(context, "فشل فتح رابط واتساب!", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .testTag("support_whatsapp_card")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = null,
                        tint = GlowPurplePink,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "تواصل مع فريق الدعم لطلب التنشيط 773303455",
                        color = GlowPurplePink,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
