package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesManagementScreen(
    onNavigateBack: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    
    val categories = listOf(
        CategoryData(
            name = "كرت 100 ر.ي",
            iconColor = TealPrimary,
            bgBorderColor = TealPrimary.copy(alpha = 0.3f),
            icon = Icons.Outlined.ConfirmationNumber,
            isEnabled = true,
            totalCount = 0,
            availableCount = 0,
            usedCount = 0
        ),
        CategoryData(
            name = "كرت 200 ر.ي",
            iconColor = PinkSecondary,
            bgBorderColor = PinkSecondary.copy(alpha = 0.3f),
            icon = Icons.Outlined.LocalMall,
            isEnabled = true,
            totalCount = 0,
            availableCount = 0,
            usedCount = 0
        )
    )

    Scaffold(
        containerColor = ZNetBackgroundDark,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { /* Add Category */ },
                containerColor = PinkSecondary,
                contentColor = Color.White,
                icon = { Icon(Icons.Outlined.Add, contentDescription = null) },
                text = { Text("فئة جديدة", fontWeight = FontWeight.Bold) },
                modifier = Modifier.padding(16.dp),
                shape = RoundedCornerShape(16.dp)
            )
        },
        floatingActionButtonPosition = FabPosition.Start
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
                    Text("إدارة الفئات", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                    Text("تنظيم وتصنيف الكروت حسب القيمة", color = TextSecondary, fontSize = 14.sp)
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Search
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("ابحث باسم الفئة أو القيمة...", color = TextSecondary) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = Color(0xFFEEF0F4),
                    unfocusedContainerColor = Color(0xFFEEF0F4),
                ),
                shape = RoundedCornerShape(16.dp),
                trailingIcon = { Icon(Icons.Outlined.Search, contentDescription = null, tint = TextSecondary) },
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(categories) { category ->
                    CategoryCard(category)
                }
            }
        }
    }
}

data class CategoryData(
    val name: String,
    val iconColor: Color,
    val bgBorderColor: Color,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val isEnabled: Boolean,
    val totalCount: Int,
    val availableCount: Int,
    val usedCount: Int
)

@Composable
fun CategoryCard(category: CategoryData) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        border = BorderStroke(1.dp, category.bgBorderColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Side: Switch and Menu
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Switch(
                    checked = category.isEnabled,
                    onCheckedChange = { /* Toggle */ },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = TealPrimary
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Icon(Icons.Outlined.MoreVert, contentDescription = "خيارات", tint = TextSecondary)
            }
            
            // Right Side: Details and Icon
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(category.name, color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Tags row 1 (Available and Total)
                    Row(horizontalArrangement = Arrangement.End) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFFE8F5E9)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                                Text("${category.availableCount} متوفر", color = Color(0xFF2E7D32), fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(14.dp))
                            }
                        }
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFFE0F2F1)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                                Text("${category.totalCount} كرت", color = TealPrimary, fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.Outlined.Inventory2, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Tags row 2 (Used)
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFFFEBEE)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                            Text("${category.usedCount} مستخدم", color = Color(0xFFC62828), fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Outlined.LocalOffer, contentDescription = null, tint = Color(0xFFC62828), modifier = Modifier.size(14.dp))
                        }
                    }
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                // Icon Box
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(category.iconColor.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(category.icon, contentDescription = null, tint = category.iconColor, modifier = Modifier.size(28.dp))
                }
            }
        }
    }
}
