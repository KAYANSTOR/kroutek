package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RejectedMessagesScreen(
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("الرسائل المرفوضة", color = PureWhite, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "رجوع", tint = PureWhite)
                    }
                },
                actions = {
                    IconButton(onClick = { /* Filter */ }) {
                        Icon(Icons.Outlined.FilterList, contentDescription = "تصفية", tint = TealPrimary)
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
                .padding(horizontal = 16.dp)
        ) {
            
            val rejectedMsgs = listOf(
                RejectedMsgData("771234567", "تجديد اشتراك", "الرصيد غير كافي", "منذ 10 دقائق"),
                RejectedMsgData("779876543", "شراء كرت", "فئة الكرت غير متوفرة", "منذ ساعة"),
                RejectedMsgData("774561239", "استعلام", "صيغة الرسالة خاطئة", "أمس 09:30 ص")
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(rejectedMsgs) { msg ->
                    RejectedMessageCard(msg)
                }
            }
        }
    }
}

data class RejectedMsgData(val phone: String, val requestType: String, val reason: String, val time: String)

@Composable
fun RejectedMessageCard(msg: RejectedMsgData) {
    Card(
        colors = CardDefaults.cardColors(containerColor = ZNetSurfaceDark),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.SmsFailed, contentDescription = null, tint = StatusRed, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(msg.phone, color = PureWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                Text(msg.time, color = TextSecondary, fontSize = 12.sp)
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = BorderDark)
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("نوع الطلب", color = TextSecondary, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(msg.requestType, color = PureWhite, fontSize = 14.sp)
                }
                Column(horizontalAlignment = Alignment.Start) {
                    Text("سبب الرفض", color = TextSecondary, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(msg.reason, color = StatusRed, fontSize = 14.sp)
                }
            }
        }
    }
}
