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
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Storefront
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
fun AddPosScreen(
    onNavigateBack: () -> Unit,
    viewModel: PosWalletViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    var posName by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var isActive by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("إضافة نقطة بيع", color = PureWhite, fontWeight = FontWeight.Bold) },
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
                enabled = posName.isNotBlank() && location.isNotBlank(),
                    modifier = Modifier
                    .fillMaxWidth()
                    .background(ZNetBackgroundDark)
                    .padding(16.dp)
            ) {
                Button(
                    onClick = {
                        if (posName.isNotBlank() && location.isNotBlank()) {
                            viewModel.addPointOfSale(posName, location, isActive)
                            onNavigateBack()
                        }
                    },
                    enabled = posName.isNotBlank() && location.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Outlined.Check, contentDescription = null, tint = PureWhite)
                    Spacer(enabled = posName.isNotBlank() && location.isNotBlank(),
                    modifier = Modifier.width(8.dp))
                    Text("حفظ نقطة البيع", color = PureWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { paddingValues ->
        Column(
            enabled = posName.isNotBlank() && location.isNotBlank(),
                    modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(enabled = posName.isNotBlank() && location.isNotBlank(),
                    modifier = Modifier.height(8.dp))
            
            Text("معلومات نقطة البيع", color = PureWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            
            OutlinedTextField(
                value = posName,
                onValueChange = { posName = it },
                label = { Text("اسم نقطة البيع") },
                placeholder = { Text("مثال: سوبر ماركت السلام") },
                enabled = posName.isNotBlank() && location.isNotBlank(),
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
                leadingIcon = { Icon(Icons.Outlined.Storefront, contentDescription = null, tint = TextSecondary) },
                singleLine = true
            )
            
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("العنوان / الموقع") },
                placeholder = { Text("الشارع العام، بجوار المسجد") },
                enabled = posName.isNotBlank() && location.isNotBlank(),
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
                leadingIcon = { Icon(Icons.Outlined.LocationOn, contentDescription = null, tint = TextSecondary) },
                singleLine = true
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = ZNetSurfaceDark),
                shape = RoundedCornerShape(12.dp),
                enabled = posName.isNotBlank() && location.isNotBlank(),
                    modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    enabled = posName.isNotBlank() && location.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("حالة نقطة البيع", color = PureWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text(if (isActive) "نشط ويمكنه إجراء العمليات" else "موقوف مؤقتاً", color = TextSecondary, fontSize = 12.sp)
                    }
                    Switch(
                        checked = isActive,
                        onCheckedChange = { isActive = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = PureWhite,
                            checkedTrackColor = TealPrimary,
                            uncheckedThumbColor = TextSecondary,
                            uncheckedTrackColor = ZNetBackgroundDark
                        )
                    )
                }
            }
            
            Spacer(enabled = posName.isNotBlank() && location.isNotBlank(),
                    modifier = Modifier.height(32.dp))
        }
    }
}
