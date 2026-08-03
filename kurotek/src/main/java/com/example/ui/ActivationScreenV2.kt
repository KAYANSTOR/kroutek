package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.widget.Toast

/**
 * ActivationScreen - شاشة التفعيل بـ 4 خطوات
 * مطابقة 100% للصور المرجعية
 * 
 * الخطوات:
 * 1. اختيار نسخة التطبيق (Free/Yearly/Professional)
 * 2. إدخال رقم الهاتف
 * 3. إدخال اسم الشبكة
 * 4. إدخال رمز التفعيل
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivationScreenV2(
    onActivationSuccess: () -> Unit = {}
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val clipboardManager = LocalClipboardManager.current
    val uriHandler = LocalUriHandler.current
    
    // State
    var currentStep by remember { mutableStateOf(0) } // 0-3: اختيار نسخة، رقم، شبكة، رمز
    var selectedVersion by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var networkName by remember { mutableStateOf("") }
    var activationKey by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    
    // Colors from design
    val primaryTeal = Color(0xFF1A9B8E)
    val primaryPink = Color(0xFFE85E97)
    val surfaceLight = Color(0xFFF5F5F5)
    val textDark = Color(0xFF000000)
    val textGray = Color(0xFF666666)
    val textError = Color(0xFFFF5252)
    
    // Gradient for logo
    val gradientBrush = Brush.linearGradient(
        colors = listOf(primaryTeal, primaryPink),
        start = Offset(0f, 0f),
        end = Offset(200f, 200f)
    )
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(gradientBrush, shape = RoundedCornerShape(50.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Z",
                    color = Color.White,
                    fontSize = 56.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
            
            Text(
                text = "تفعيل تطبيق زدنت",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = textDark,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = "أدخل بياناتك لتفعيل الترخيص أو بدء النسخة التجريبية المجانية",
                fontSize = 13.sp,
                color = textGray,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )
            
            // Progress Indicator - 4 steps
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
                                color = if (step <= currentStep) primaryTeal else surfaceLight,
                                shape = RoundedCornerShape(2.dp)
                            )
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Content based on step
            when (currentStep) {
                0 -> StepOneVersionSelection(
                    selectedVersion,
                    { selectedVersion = it },
                    primaryTeal
                )
                1 -> StepTwoPhoneNumber(
                    phoneNumber,
                    { phoneNumber = it },
                    primaryTeal
                )
                2 -> StepThreeNetworkName(
                    networkName,
                    { networkName = it },
                    primaryTeal
                )
                3 -> StepFourActivationKey(
                    activationKey,
                    { activationKey = it },
                    showPassword,
                    { showPassword = it },
                    errorMessage,
                    primaryTeal
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Navigation Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (currentStep > 0) {
                    Button(
                        onClick = { currentStep-- },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = surfaceLight,
                            contentColor = textDark
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Text("رجوع", fontWeight = FontWeight.Bold)
                    }
                }
                
                Button(
                    onClick = {
                        when (currentStep) {
                            0 -> if (selectedVersion.isNotEmpty()) currentStep++
                            1 -> if (phoneNumber.isNotEmpty()) currentStep++
                            2 -> if (networkName.isNotEmpty()) currentStep++
                            3 -> {
                                isLoading = true
                                // Simulate API call
                                Toast.makeText(context, "تم التفعيل بنجاح", Toast.LENGTH_SHORT).show()
                                onActivationSuccess()
                            }
                        }
                    },
                    enabled = !isLoading && when (currentStep) {
                        0 -> selectedVersion.isNotEmpty()
                        1 -> phoneNumber.isNotEmpty()
                        2 -> networkName.isNotEmpty()
                        3 -> activationKey.isNotEmpty()
                        else -> false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryTeal,
                        disabledContainerColor = primaryTeal.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(if (currentStep == 0) 1f else 1.5f)
                        .height(48.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = if (currentStep == 3) "تفعيل" else "التالي",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            // Support Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable {
                        try {
                            uriHandler.openUri("https://wa.me/967773303455")
                        } catch (e: Exception) {
                            Toast.makeText(context, "فشل فتح الرابط", Toast.LENGTH_SHORT).show()
                        }
                    },
                colors = CardDefaults.cardColors(
                    containerColor = primaryTeal.copy(alpha = 0.15f)
                ),
                border = BorderStroke(1.dp, primaryTeal.copy(alpha = 0.3f))
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
                        tint = primaryTeal,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "تواصل مع الدعم: 773303455",
                        color = primaryTeal,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun StepOneVersionSelection(
    selectedVersion: String,
    onVersionSelected: (String) -> Unit,
    tealColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "اختر نسخة التطبيق",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            
            listOf(
                "نسخة تجريبية مجانية" to "30 يوم مجاني",
                "نسخة الاشتراك السنوي" to "سنة واحدة",
                "نسخة احترافية" to "نسخة احترافية"
            ).forEach { (version, description) ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onVersionSelected(version) },
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedVersion == version) tealColor.copy(alpha = 0.2f) else Color.White
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (selectedVersion == version) tealColor else Color(0xFFE0E0E0)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(version, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(description, fontSize = 11.sp, color = Color(0xFF666666))
                        }
                        if (selectedVersion == version) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = null,
                                tint = tealColor,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StepTwoPhoneNumber(
    phoneNumber: String,
    onPhoneChange: (String) -> Unit,
    tealColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "رقم الهاتف",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = onPhoneChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("773303455") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = tealColor,
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                )
            )
        }
    }
}

@Composable
private fun StepThreeNetworkName(
    networkName: String,
    onNetworkChange: (String) -> Unit,
    tealColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "اسم الشبكة",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            
            OutlinedTextField(
                value = networkName,
                onValueChange = onNetworkChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("كيان تك") },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = tealColor,
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                )
            )
        }
    }
}

@Composable
private fun StepFourActivationKey(
    activationKey: String,
    onKeyChange: (String) -> Unit,
    showPassword: Boolean,
    onShowPasswordChange: (Boolean) -> Unit,
    errorMessage: String,
    tealColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "رمز التفعيل",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            
            OutlinedTextField(
                value = activationKey,
                onValueChange = onKeyChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("أدخل رمز التفعيل") },
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { onShowPasswordChange(!showPassword) }) {
                        Icon(
                            imageVector = if (showPassword) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                            contentDescription = null,
                            tint = tealColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = tealColor,
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    cursorColor = tealColor
                )
            )
            
            if (errorMessage.isNotEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFF5252).copy(alpha = 0.15f)
                    ),
                    border = BorderStroke(1.dp, Color(0xFFFF5252).copy(alpha = 0.3f)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = errorMessage,
                        color = Color(0xFFFF5252),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }
}
