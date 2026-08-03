package com.example.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardsManagementScreen(
    onNavigateBack: () -> Unit,
    onNavigateToCategories: () -> Unit
) {
    onNavigateBack: () -> Unit,
    onNavigateToCategories: () -> Unit,
    onShowImportOptions: () -> Unit,
    onShowAddCardOptions: () -> Unit
) {
    
    var showImportSheet by remember { mutableStateOf(false) }
    var showAddCardSheet by remember { mutableStateOf(false) }

    if (showImportSheet) {
        ImportCardsBottomSheet(onDismiss = { showImportSheet = false })
    }

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
            
            // Top Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(
                        onClick = { showAddCardSheet = true },
                        modifier = Modifier
                            .background(Color.White, RoundedCornerShape(12.dp))
                            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
                            .size(48.dp)
                    ) {
                        Icon(Icons.Outlined.Add, contentDescription = "إضافة", tint = PureWhite)
                    }
                    IconButton(
                        onClick = { /* Delete */ },
                        modifier = Modifier
                            .background(Color.White, RoundedCornerShape(12.dp))
                            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
                            .size(48.dp)
                    ) {
                        Icon(Icons.Outlined.DeleteOutline, contentDescription = "حذف", tint = PureWhite)
                    }
                }
                
                Text(
                    text = "إدارة الكروت",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = PureWhite
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { showImportSheet = true },
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f).height(48.dp)
                ) {
                    Icon(Icons.Outlined.CloudUpload, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("استيراد من ملف", color = Color.White, fontWeight = FontWeight.Bold)
                }
                
                Button(
                    onClick = onNavigateToCategories,
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f).height(48.dp)
                ) {
                    Icon(Icons.Outlined.Category, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("الفئات", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Search Bar
            OutlinedTextField(
                value = "",
                onValueChange = {},
                placeholder = { Text("البحث برقم الكرت...", color = TextSecondary) },
                trailingIcon = { Icon(Icons.Outlined.Search, contentDescription = null, tint = TextSecondary) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    unfocusedContainerColor = Color(0xFFEEF0F4),
                    focusedContainerColor = Color(0xFFEEF0F4),
                ),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Categories Filter
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("كرت 200", color = PureWhite, fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(modifier = Modifier.size(8.dp).background(Color(0xFFC2185B), CircleShape))
                    }
                }
                
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("كرت 100", color = PureWhite, fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(modifier = Modifier.size(8.dp).background(TealPrimary, CircleShape))
                    }
                }
                
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = TealPrimary
                ) {
                    Text("الكل", color = Color.White, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Status Filter
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("كروت مستخدمة", color = TextSecondary, modifier = Modifier.padding(end = 16.dp))
                Text("كروت متوفرة", color = TextSecondary, modifier = Modifier.padding(end = 16.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, TealPrimary)
                ) {
                    Text("الكل", color = TealPrimary, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            val cards = listOf(
                CardData("2939383", "100 ر.ي", "مستخدم", "773303455"),
                CardData("72720393", "100 ر.ي", "متوفر", "")
            )
            
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(cards) { card ->
                    TicketCard(card)
                }
            }
        }
    }
}

data class CardData(val pin: String, val amount: String, val status: String, val phone: String)

@Composable
fun TicketCard(card: CardData) {
    val isSold = card.status == "مستخدم"
    val statusColor = if (isSold) Color(0xFFD32F2F) else Color(0xFF388E3C)
    
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
        modifier = Modifier.fillMaxWidth().height(90.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            // Left side (PIN code)
            Column(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("رمز الكود", color = TextSecondary, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(card.pin, color = PureWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
            
            // Dashed Divider
            Canvas(modifier = Modifier.width(1.dp).fillMaxHeight().padding(vertical = 8.dp)) {
                drawLine(
                    color = Color(0xFFE0E0E0),
                    start = Offset(0f, 0f),
                    end = Offset(0f, size.height),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                )
            }
            
            // Right side (Details)
            Column(
                modifier = Modifier.weight(1.2f).fillMaxHeight().padding(end = 16.dp),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                    if (isSold && card.phone.isNotEmpty()) {
                        Text(card.phone, color = TextSecondary, fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.Transparent,
                        border = BorderStroke(1.dp, statusColor)
                    ) {
                        Text(
                            text = card.status, 
                            color = statusColor, 
                            fontSize = 12.sp, 
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(card.amount, color = PureWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
