package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.KurotekApplication
import com.example.database.CardRepository
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LicenseActivationScreen(
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

    val licenseStatus by viewModel.licenseStatus.collectAsState()

    // Assuming we refresh or get status on load
    LaunchedEffect(Unit) {
        viewModel.refreshTrialStatus() // or validateLicenseFromServer()
    }

    val isTrial = licenseStatus?.state?.name == "TRIAL"
    val isValid = licenseStatus?.state?.name == "VALID" || isTrial
    val statusText = when (licenseStatus?.state?.name) {
        "VALID" -> "نشط"
        "TRIAL" -> "تجريبي"
        "EXPIRED" -> "منتهي"
        "BLOCKED" -> "محظور"
        else -> "غير مسجل"
    }
    
    val statusColor = if (isValid) StatusGreen else StatusRed

    val expiryDateStr = licenseStatus?.expiryDate?.let {
        if (it > 0) SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(it))
        else "مدى الحياة"
    } ?: "غير محدد"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("حالة الترخيص", color = PureWhite, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Outlined.Close, contentDescription = "إغلاق", tint = PureWhite)
                    }
                },
                actions = {
                    IconButton(onClick = { /* Help */ }) {
                        Icon(Icons.Outlined.HelpOutline, contentDescription = "مساعدة", tint = TealPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ZNetBackgroundDark)
            )
        },
        containerColor = ZNetBackgroundDark
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Success/Status Icon
            Icon(
                imageVector = if (isValid) Icons.Outlined.CheckCircle else Icons.Outlined.Close,
                contentDescription = null,
                tint = statusColor,
                modifier = Modifier.size(100.dp)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = if (isValid) "النسخة مفعلة!" else "غير مفعل",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = PureWhite
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Card(
                colors = CardDefaults.cardColors(containerColor = ZNetSurfaceDark),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    LicenseInfoRow("نوع الترخيص", if(isTrial) "نسخة تجريبية" else "نسخة الموزع (الكاملة)", TealPrimary)
                    Divider(color = BorderDark)
                    LicenseInfoRow("حالة الترخيص", statusText, statusColor)
                    Divider(color = BorderDark)
                    LicenseInfoRow("تاريخ الانتهاء", expiryDateStr, PureWhite)
                    Divider(color = BorderDark)
                    LicenseInfoRow("رقم التسجيل", licenseStatus?.licenseKey?.take(8)?.let { "$it..." } ?: "غير متوفر", TextSecondary)
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            Button(
                onClick = onNavigateBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("متابعة", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PureWhite)
            }
        }
    }
}

@Composable
fun LicenseInfoRow(label: String, value: String, valueColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = TextSecondary, fontSize = 14.sp)
        Text(text = value, color = valueColor, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}
