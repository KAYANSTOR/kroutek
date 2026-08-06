package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberScrollState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BrandBackground
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivationScreen(
    activationViewModel: ActivationViewModel,
    onActivationSuccess: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    val isLight = BrandBackground.luminance() > 0.5f
    val titleColor = if (isLight) Color(0xFF111111) else Color(0xFFFFFFFF)
    val subtitleColor = if (isLight) Color(0xFF555555) else TextSecondary
    val selectedBorder = if (isLight) Color(0xFF03DAC5) else Color(0xFF14B8A6)
    val selectedBg = if (isLight) Color(0xFFF0FDFA) else Color(0xFF042F2E)
    val fieldBg = if (isLight) Color(0xFFF5F5F5) else Color(0xFF1E1E21)

    val selectedStep by activationViewModel.selectedStep.collectAsState()
    val phone by activationViewModel.phone.collectAsState()
    val networkName by activationViewModel.networkName.collectAsState()
    val activationKey by activationViewModel.activationKey.collectAsState()
    val isTrial by activationViewModel.isTrial.collectAsState()
    val showKeyField by activationViewModel.showKeyField.collectAsState()
    val isLoading by activationViewModel.isLoading.collectAsState()
    val errorMessage by activationViewModel.errorMessage.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(BrandBackground).testTag("activation_screen")) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(modifier = Modifier.size(96.dp).background(color = if (isLight) Color(0xFFF0FDFA) else Color(0xFF042F2E), shape = CircleShape), contentAlignment = Alignment.Center) {
                    Text(text = "Z", color = Color(0xFF03DAC5), fontSize = 44.sp, fontWeight = FontWeight.ExtraBold)
                }
                Text(text = "تفعيل تطبيق كروت الدحشة", color = titleColor, fontSize = 22.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                Text(text = "أدخل بيانات التفعيل أو ابدأ النسخة التجريبية المجانية", color = subtitleColor, fontSize = 13.sp, textAlign = TextAlign.Center, lineHeight = 18.sp)
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(3) { index ->
                    Box(modifier = Modifier.weight(1f).height(4.dp).background(color = if (index <= selectedStep) if (isLight) Color(0xFF03DAC5) else Color(0xFF14B8A6) else if (isLight) Color(0xFFE5E7EB) else Color(0xFF27272A), shape = RoundedCornerShape(2.dp)))
                }
            }

            when (selectedStep) {
                0 -> VersionStep(isTrial = isTrial, onTrialChanged = { activationViewModel.onTrialSelected(it) }, isLight = isLight, selectedBorder = selectedBorder, selectedBg = selectedBg)
                1 -> PhoneStep(phone = phone, onPhoneChanged = { activationViewModel.onPhoneChanged(it) }, isLight = isLight)
                2 -> KeyStep(networkName = networkName, activationKey = activationKey, showKeyField = showKeyField, onNetworkChanged = { activationViewModel.onNetworkChanged(it) }, onKeyChanged = { activationViewModel.onKeyChanged(it) }, isLight = isLight, fieldBg = fieldBg)
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                if (selectedStep > 0) {
                    TextButton(onClick = { activationViewModel.goToPreviousStep() }, enabled = !isLoading) {
                        Text(text = "السابق", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Button(onClick = {
                    when (selectedStep) {
                        0 -> activationViewModel.goToNextStep()
                        1 -> activationViewModel.goToNextStep()
                        2 -> activationViewModel.activate(onSuccess = onActivationSuccess, onError = {})
                    }
                }, modifier = Modifier.weight(1f).height(48.dp), shape = RoundedCornerShape(14.dp), enabled = !isLoading, colors = ButtonDefaults.buttonColors(
                    containerColor = if (isLoading) BrandPrimary.copy(alpha = 0.5f) else BrandPrimary,
                    disabledContentColor = Color.White.copy(alpha = 0.5f)
                )) {
                    if (isLoading) {
                        androidx.compose.material3.CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Text(text = when (selectedStep) { 0 -> "التالي" 1 -> "التالي" 2 -> if (isTrial) "تجربة مجانية" else "تفعيل" }, fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                }
            }

            if (errorMessage.isNotEmpty()) {
                Text(text = errorMessage, color = Color(0xFFEF4444), fontSize = 12.sp, fontWeight = FontWeight.Medium, modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp))
            }
        }
    }
}

@Composable
private fun VersionStep(isTrial: Boolean, onTrialChanged: (Boolean) -> Unit, isLight: Boolean, selectedBorder: Color, selectedBg: Color) {
    val titleColor = if (isLight) Color(0xFF111111) else Color(0xFFFFFFFF)
    val subtitleColor = if (isLight) Color(0xFF555555) else TextSecondary
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(text = "اختر النسخة", color = titleColor, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        SelectableOption(title = "النسخة التجريبية", subtitle = "تجربة مجانية لمدة 30 يوم", selected = isTrial, onClick = { onTrialChanged(true) }, isLight = isLight, selectedBorder = selectedBorder, selectedBg = selectedBg)
        SelectableOption(title = "النسخة السنوية", subtitle = "اشتراك سنة كاملة", selected = !isTrial, onClick = { onTrialChanged(false) }, isLight = isLight, selectedBorder = selectedBorder, selectedBg = selectedBg)
    }
}

@Composable
private fun PhoneStep(phone: String, onPhoneChanged: (String) -> Unit, isLight: Boolean) {
    val fieldBg = if (isLight) Color(0xFFF5F5F5) else Color(0xFF1E1E21)
    val textColor = if (isLight) Color(0xFF111111) else Color(0xFFFFFFFF)
    OutlinedCard(shape = RoundedCornerShape(16.dp), colors = CardDefaults.outlinedCardColors(containerColor = fieldBg)) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(text = "رقم الجوال", color = textColor, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            OutlinedTextField(value = phone, onValueChange = onPhoneChanged, modifier = Modifier.fillMaxWidth(), singleLine = true, placeholder = { Text(text = "773303455") }, textStyle = androidx.compose.ui.text.TextStyle(textAlign = TextAlign.Right, color = textColor), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = if (isLight) Color(0xFF03DAC5) else Color(0xFF14B8A6), unfocusedBorderColor = TextSecondary.copy(alpha = 0.25f)), shape = RoundedCornerShape(16.dp))
        }
    }
}

@Composable
private fun KeyStep(networkName: String, activationKey: String, showKeyField: Boolean, onNetworkChanged: (String) -> Unit, onKeyChanged: (String) -> Unit, isLight: Boolean, fieldBg: Color) {
    val textColor = if (isLight) Color(0xFF111111) else Color(0xFFFFFFFF)
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(text = "اسم الشبكة", color = textColor, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        OutlinedTextField(value = networkName, onValueChange = onNetworkChanged, modifier = Modifier.fillMaxWidth(), singleLine = true, placeholder = { Text(text = "شبكة كيان تك") }, textStyle = androidx.compose.ui.text.TextStyle(textAlign = TextAlign.Right, color = textColor), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = if (isLight) Color(0xFF03DAC5) else Color(0xFF14B8A6), unfocusedBorderColor = TextSecondary.copy(alpha = 0.25f)), shape = RoundedCornerShape(16.dp))
        if (showKeyField) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(text = "رمز التفعيل", color = textColor, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                OutlinedTextField(value = activationKey, onValueChange = onKeyChanged, modifier = Modifier.fillMaxWidth(), singleLine = true, placeholder = { Text(text = "XXXX-XXXX-XXXX") }, textStyle = androidx.compose.ui.text.TextStyle(textAlign = TextAlign.Right, color = textColor), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = if (isLight) Color(0xFF03DAC5) else Color(0xFF14B8A6), unfocusedBorderColor = TextSecondary.copy(alpha = 0.25f)), shape = RoundedCornerShape(16.dp))
            }
        }
    }
}

@Composable
private fun SelectableOption(title: String, subtitle: String, selected: Boolean, onClick: () -> Unit, isLight: Boolean, selectedBorder: Color, selectedBg: Color) {
    val titleColor = if (isLight) Color(0xFF111111) else Color(0xFFFFFFFF)
    val subtitleColor = if (isLight) Color(0xFF666666) else TextSecondary
    OutlinedCard(onClick = onClick, shape = RoundedCornerShape(12.dp), contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp), colors = CardDefaults.outlinedCardColors(containerColor = if (selected) selectedBg else Color(0xFFF5F5F5)), border = if (selected) androidx.compose.foundation.BorderStroke(2.dp, selectedBorder) else null) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(modifier = Modifier.size(22.dp).background(color = if (selected) selectedBorder else Color.Transparent, shape = CircleShape), contentAlignment = Alignment.Center) {
                if (selected) {
                    Box(modifier = Modifier.size(10.dp).background(color = Color.White, shape = CircleShape))
                }
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = title, color = titleColor, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Text(text = subtitle, color = subtitleColor, fontSize = 12.sp)
            }
        }
    }
}
