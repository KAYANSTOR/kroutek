package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.SimCard
import androidx.compose.material3.*
import androidx.compose.runtime.*

import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalContext
import com.example.database.CardRepository
import com.example.core.CoreContainer

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimSettingsScreen(
    onNavigateBack: () -> Unit
) {
    
    val context = LocalContext.current
    val repository = remember { CardRepository(context) }
    val coreContainer = remember { CoreContainer(context) } // Or pass null if not strictly needed
    val viewModel: SettingsViewModel = viewModel(factory = SettingsViewModelFactory(repository, coreContainer))

    val receiveSim by viewModel.receiveSim.collectAsState()
    val sendSim by viewModel.sendSim.collectAsState()

    

    Scaffold(
        containerColor = ZNetBackgroundDark
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.AutoMirrored.Outlined.ArrowForward, contentDescription = "رجوع", tint = PureWhite)
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text("إعدادات شرائح الاتصال", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                    Text("إدارة شرائح القراءة والإرسال", color = TextSecondary, fontSize = 14.sp)
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Warning Banner
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFFFF8E1), // Light amber
                border = BorderStroke(1.dp, Color(0xFFFFB300)), // Amber 600
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Outlined.Info, contentDescription = null, tint = Color(0xFFFF8F00))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("تم اكتشاف شريحة واحدة فقط على الجهاز.", color = Color(0xFFF57C00), fontWeight = FontWeight.Bold)
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Section 1: Receive SIM
            SimSelectionSection(
                title = "شريحة قراءة رسائل المحافظ",
                subtitle = "اختر الشريحة التي سيعتمد عليها النظام لقراءة رسائل الإيداع الواردة.",
                selectedOption = receiveSim,
                onOptionSelected = { viewModel.setReceiveSim(it) }
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Section 2: Send SIM
            SimSelectionSection(
                title = "شريحة إرسال الرسائل للعملاء",
                subtitle = "اختر الشريحة المستخدمة لإرسال الكروت والإشعارات للعملاء.",
                selectedOption = sendSim,
                onOptionSelected = { viewModel.setSendSim(it) }
            )
        }
    }
}

@Composable
fun SimSelectionSection(
    title: String,
    subtitle: String,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.align(Alignment.End))
            Spacer(modifier = Modifier.height(4.dp))
            Text(subtitle, color = TextSecondary, fontSize = 14.sp, modifier = Modifier.align(Alignment.End))
            
            Spacer(modifier = Modifier.height(16.dp))
            
            SimOptionCard(
                title = "شريحة 1",
                subtitle = "42103",
                isSelected = selectedOption == "sim1",
                onClick = { onOptionSelected("sim1") }
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            SimOptionCard(
                title = "كلا الشريحتين",
                subtitle = "النظام سيتعامل مع كلتا الشريحتين",
                isSelected = selectedOption == "both",
                onClick = { onOptionSelected("both") },
                isBoth = true
            )
        }
    }
}

@Composable
fun SimOptionCard(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    isBoth: Boolean = false
) {
    val borderColor = if (isSelected) TealPrimary else Color(0xFFE0E0E0)
    val bgColor = if (isSelected) TealPrimary.copy(alpha = 0.05f) else Color.White
    
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = bgColor,
        border = BorderStroke(1.dp, borderColor),
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(selectedColor = TealPrimary)
            )
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(title, color = if (isSelected) TealPrimary else TextSecondary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(subtitle, color = TextSecondary, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Icon(
                    if (isBoth) Icons.Outlined.SimCard else Icons.Outlined.SimCard, 
                    contentDescription = null, 
                    tint = TextSecondary
                )
            }
        }
    }
}
