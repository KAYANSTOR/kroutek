package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.widget.Toast

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivationScreenV2(
    activationViewModel: ActivationViewModel,
    onActivationSuccess: () -> Unit = {}
) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current

    val selectedStep by activationViewModel.selectedStep.collectAsState()
    val phone by activationViewModel.phone.collectAsState()
    val networkName by activationViewModel.networkName.collectAsState()
    val activationKey by activationViewModel.activationKey.collectAsState()
    val isTrial by activationViewModel.isTrial.collectAsState()
    val showKeyField by activationViewModel.showKeyField.collectAsState()
    val isLoading by activationViewModel.isLoading.collectAsState()
    val errorMessage by activationViewModel.errorMessage.collectAsState()

    val primaryTeal = Color(0xFF1A9B8E)
    val primaryPink = Color(0xFFE85E97)
    val surfaceLight = Color(0xFFF5F5F5)
    val textDark = Color(0xFF000000)
    val textGray = Color(0xFF666666)
    val textError = Color(0xFFFF5252)

    val gradientBrush = Brush.linearGradient(colors = listOf(primaryTeal, primaryPink), start = androidx.compose.ui.geometry.Offset(0f, 0f), end = androidx.compose.ui.geometry.Offset(200f, 200f))

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(20.dp)) {
            Box(modifier = Modifier.size(100.dp).background(gradientBrush, shape = RoundedCornerShape(50.dp)), contentAlignment = Alignment.Center) {
                Text(text = "Z", color = Color.White, fontSize = 56.sp, fontWeight = FontWeight.ExtraBold)
            }

            Text(text = "تفعيل تطبيق كروت الدحشة", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = textDark, textAlign = TextAlign.Center)
            Text(text = "أدخل بياناتك لتفعيل الترخيص أو بدء النسخة التجريبية المجانية", fontSize = 13.sp, color = textGray, textAlign = TextAlign.Center, lineHeight = 18.sp)

            Row(modifier = Modifier.fillMaxWidth().height(4.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(4) { step ->
                    Box(modifier = Modifier.weight(1f).height(4.dp).background(color = if (step <= selectedStep) primaryTeal else surfaceLight, shape = RoundedCornerShape(2.dp)))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            when (selectedStep) {
                0 -> V2StepOneVersionSelection(selectedVersion = if (isTrial) "Free" else "Yearly", onVersionSelected = { activationViewModel.onTrialSelected(it == "Free") }, tealColor = primaryTeal)
                1 -> V2StepTwoPhoneNumber(phoneNumber = phone, onPhoneChange = { activationViewModel.onPhoneChanged(it) }, tealColor = primaryTeal)
                2 -> V2StepThreeNetworkName(networkName = networkName, onNetworkChange = { activationViewModel.onNetworkChanged(it) }, tealColor = primaryTeal)
                3 -> V2StepFourActivationKey(activationKey = activationKey, showKeyField = showKeyField, onKeyChange = { activationViewModel.onKeyChanged(it) }, tealColor = primaryTeal)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                if (selectedStep > 0) {
                    TextButton(onClick = { activationViewModel.goToPreviousStep() }, enabled = !isLoading) {
                        Text(text = "السابق", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = if (isLoading) textGray else textDark)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }

                Box(modifier = Modifier.weight(1f).height(48.dp)) {
                    if (isLoading) {
                        androidx.compose.material3.CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Button(onClick = {
                            when (selectedStep) {
                                0 -> activationViewModel.goToNextStep()
                                1 -> activationViewModel.goToNextStep()
                                2 -> activationViewModel.goToNextStep()
                                3 -> activationViewModel.activate(onSuccess = onActivationSuccess, onError = {})
                            }
                        }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = if (isLoading) primaryTeal.copy(alpha = 0.5f) else primaryTeal, disabledContentColor = Color.White.copy(alpha = 0.5f), disabledContainerColor = if (isLoading) primaryTeal.copy(alpha = 0.5f) else primaryTeal)) {
                            Text(text = when (selectedStep) { 0 -> "التالي" 1 -> "التالي" 2 -> "التالي" 3 -> if (isTrial) "تجربة مجانية" else "تفعيل" }, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }

            if (errorMessage.isNotEmpty()) {
                Text(text = errorMessage, color = textError, fontSize = 14.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(horizontal = 16.dp).padding(top = 8.dp).fillMaxWidth())
            }

            Card(modifier = Modifier.fillMaxWidth().padding(top = 24.dp).clip(RoundedCornerShape(12.dp)).clickable {
                try { uriHandler.openUri("https://wa.me/967773303455") } catch (e: Exception) { Toast.makeText(context, "فشل فتح الرابط", Toast.LENGTH_SHORT).show() }
            }, colors = CardDefaults.cardColors(containerColor = primaryTeal.copy(alpha = 0.15f)), border = androidx.compose.foundation.BorderStroke(1.dp, primaryTeal.copy(alpha = 0.3f))) {
                Row(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                    Icon(imageVector = Icons.Outlined.Info, contentDescription = null, tint = primaryTeal, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "تواصل مع الدعم: 773303455", color = primaryTeal, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun V2StepOneVersionSelection(selectedVersion: String, onVersionSelected: (String) -> Unit, tealColor: Color) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)), shape = RoundedCornerShape(16.dp)) {
        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(text = "اختر نسخة التطبيق", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            listOf("Free" to "30 يوم مجاني", "Yearly" to "سنة واحدة", "Professional" to "نسخة احترافية").forEach { (version, description) ->
                Card(modifier = Modifier.fillMaxWidth().clickable { onVersionSelected(version) }.padding(vertical = 4.dp), colors = CardDefaults.cardColors(containerColor = if (selectedVersion.equals(version, ignoreCase = true)) tealColor.copy(alpha = 0.2f) else Color.White), border = androidx.compose.foundation.BorderStroke(1.dp, if (selectedVersion.equals(version, ignoreCase = true)) tealColor else Color(0xFFE0E0E0)), shape = RoundedCornerShape(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(text = version, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            Text(text = description, fontSize = 11.sp, color = Color(0xFF666666))
                        }
                        if (selectedVersion.equals(version, ignoreCase = true)) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = tealColor, modifier = Modifier.size(24.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun V2StepTwoPhoneNumber(phoneNumber: String, onPhoneChange: (String) -> Unit, tealColor: Color) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)), shape = RoundedCornerShape(16.dp)) {
        Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
            Text(text = "رقم الهاتف", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            OutlinedTextField(value = phoneNumber, onValueChange = onPhoneChange, modifier = Modifier.fillMaxWidth(), placeholder = { Text("773303455") }, keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next), shape = RoundedCornerShape(12.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = tealColor, unfocusedBorderColor = Color(0xFFE0E0E0), focusedTextColor = Color.Black, unfocusedTextColor = Color.Black))
        }
    }
}

@Composable
private fun V2StepThreeNetworkName(networkName: String, onNetworkChange: (String) -> Unit, tealColor: Color) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)), shape = RoundedCornerShape(16.dp)) {
        Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
            Text(text = "اسم الشبكة", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            OutlinedTextField(value = networkName, onValueChange = onNetworkChange, modifier = Modifier.fillMaxWidth(), placeholder = { Text("كيان تك") }, keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(imeAction = ImeAction.Next), shape = RoundedCornerShape(12.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = tealColor, unfocusedBorderColor = Color(0xFFE0E0E0), focusedTextColor = Color.Black, unfocusedTextColor = Color.Black))
        }
    }
}

@Composable
private fun V2StepFourActivationKey(activationKey: String, showKeyField: Boolean, onKeyChange: (String) -> Unit, tealColor: Color) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(text = "رمز التفعيل", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        if (showKeyField) {
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)), shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
                    OutlinedTextField(value = activationKey, onValueChange = onKeyChange, modifier = Modifier.fillMaxWidth(), placeholder = { Text("XXXX-XXXX-XXXX") }, visualTransformation = PasswordVisualTransformation(), keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(imeAction = ImeAction.Done), shape = RoundedCornerShape(12.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = tealColor, unfocusedBorderColor = Color(0xFFE0E0E0), focusedTextColor = Color.Black, unfocusedTextColor = Color.Black))
                }
            }
        }
    }
}
