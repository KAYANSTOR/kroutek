package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun PinLockScreen(
    authViewModel: AuthViewModel,
    onUnlocked: () -> Unit
) {
    var pin by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepBlack)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(0.9f),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, SurfaceLight),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text(
                    text = "قفل التطبيق",
                    color = BrandPrimaryRed,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black
                )

                Text(
                    text = "الرجاء إدخال رمز المرور (PIN) للمتابعة",
                    color = TextSecondary,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )

                OutlinedTextField(
                    value = pin,
                    onValueChange = { 
                        if (it.length <= 6) { // Max 6 digits
                            pin = it.filter { char -> char.isDigit() }
                            errorMsg = null
                        }
                    },
                    label = { Text("رمز المرور") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandPrimaryRed,
                        focusedLabelColor = BrandPrimaryRed,
                        unfocusedBorderColor = SurfaceLight,
                        unfocusedLabelColor = TextSecondary,
                        focusedTextColor = PureWhite,
                        unfocusedTextColor = PureWhite,
                        focusedContainerColor = DeepBlack,
                        unfocusedContainerColor = DeepBlack
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("pin_input")
                )

                if (errorMsg != null) {
                    Text(
                        text = errorMsg!!,
                        color = StatusRed,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Button(
                    onClick = {
                        if (authViewModel.verifyPassword(pin)) {
                            onUnlocked()
                        } else {
                            errorMsg = "الرمز غير صحيح"
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("unlock_button"),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = PureWhite),
                    contentPadding = PaddingValues()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(PrimaryRedGradient),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("دخول", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}
