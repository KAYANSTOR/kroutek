package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Add
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
import android.widget.Toast
import androidx.compose.foundation.BorderStroke

data class InventoryItem(
    val id: String,
    val name: String,
    val quantity: Int,
    val price: Double,
    val status: String, // "متوفر", "قليل", "نفد"
    val category: String,
    val lastUpdate: String
)

@Composable
fun InventoryManagementScreen(
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.context
    var inventoryItems by remember {
        mutableStateOf(listOf(
            InventoryItem("1", "محفظة الكاش", 150, 50.0, "متوفر", "محافظ رقمية", "منذ ساعة"),
            InventoryItem("2", "محفظة جوالي", 45, 40.0, "قليل", "محافظ رقمية", "منذ ساعتين"),
            InventoryItem("3", "محفظة كريمي", 0, 35.0, "نفد", "محافظ رقمية", "منذ يومين"),
            InventoryItem("4", "بطاقات شحن", 200, 25.0, "متوفر", "بطاقات شحن", "الآن"),
            InventoryItem("5", "رموز تفعيل", 89, 100.0, "قليل", "رموز رقمية", "منذ 3 ساعات"),
        ))
    }

    val totalItems = inventoryItems.sumOf { it.quantity }
    val totalValue = inventoryItems.sumOf { it.quantity * it.price }
    val availableCount = inventoryItems.filter { it.status == "متوفر" }.size
    val lowStockCount = inventoryItems.filter { it.status == "قليل" }.size
    val outOfStockCount = inventoryItems.filter { it.status == "نفد" }.size

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF09090B))
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "رجوع",
                            tint = Color(0xFF1A9B8E),
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "إدارة المحافظ والمبيعات",
                            color = Color(0xFFFAFAFA),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "${inventoryItems.size} منتج",
                            color = Color(0xFFA1A1AA),
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // Statistics Grid
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Top Row - Stock Overview
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(90.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            label = "إجمالي المخزون",
                            value = "$totalItems وحدة",
                            icon = Icons.Default.Inventory2,
                            color = Color(0xFF10B981),
                            modifier = Modifier.weight(1f)
                        )

                        StatCard(
                            label = "القيمة الإجمالية",
                            value = "$totalValue ر.ي",
                            icon = Icons.Default.AttachMoney,
                            color = Color(0xFF3B82F6),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Bottom Row - Status Overview
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(90.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            label = "متوفر",
                            value = "$availableCount",
                            icon = Icons.Default.CheckCircle,
                            color = Color(0xFF10B981),
                            modifier = Modifier.weight(1f)
                        )

                        StatCard(
                            label = "قليل",
                            value = "$lowStockCount",
                            icon = Icons.Default.Warning,
                            color = Color(0xFFF59E0B),
                            modifier = Modifier.weight(1f)
                        )

                        StatCard(
                            label = "نفد",
                            value = "$outOfStockCount",
                            icon = Icons.Default.RemoveCircle,
                            color = Color(0xFFEF4444),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Add New Item Button
            item {
                Button(
                    onClick = {
                        Toast.makeText(context, "إضافة منتج جديد", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A9B8E)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = null,
                        tint = Color(0xFFFAFAFA),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "إضافة منتج جديد",
                        color = Color(0xFFFAFAFA),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Inventory Items Header
            item {
                Text(
                    text = "المنتجات والمخزون",
                    color = Color(0xFFFAFAFA),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Inventory Items List
            items(inventoryItems) { item ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF18181B)),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Header Row with Status
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Status Badge
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = when (item.status) {
                                            "متوفر" -> Color(0xFF10B981).copy(alpha = 0.15f)
                                            "قليل" -> Color(0xFFF59E0B).copy(alpha = 0.15f)
                                            else -> Color(0xFFEF4444).copy(alpha = 0.15f)
                                        },
                                        shape = RoundedCornerShape(6.dp)
                                    )
                                    .border(
                                        BorderStroke(
                                            1.dp,
                                            when (item.status) {
                                                "متوفر" -> Color(0xFF10B981)
                                                "قليل" -> Color(0xFFF59E0B)
                                                else -> Color(0xFFEF4444)
                                            }
                                        ),
                                        RoundedCornerShape(6.dp)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = item.status,
                                    color = when (item.status) {
                                        "متوفر" -> Color(0xFF10B981)
                                        "قليل" -> Color(0xFFF59E0B)
                                        else -> Color(0xFFEF4444)
                                    },
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Text(
                                text = item.name,
                                color = Color(0xFFFAFAFA),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Details Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(Color.White.copy(alpha = 0.05f))
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Right side details
                            Column(
                                horizontalAlignment = Alignment.End,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "${item.quantity} وحدة",
                                    color = Color(0xFFFAFAFA),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${item.price} ر.ي للوحدة",
                                    color = Color(0xFFA1A1AA),
                                    fontSize = 10.sp
                                )
                            }

                            // Left side details
                            Column(
                                horizontalAlignment = Alignment.End,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = item.category,
                                    color = Color(0xFF1A9B8E),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = item.lastUpdate,
                                    color = Color(0xFFA1A1AA),
                                    fontSize = 10.sp
                                )
                            }
                        }

                        // Progress Bar for Stock
                        LinearProgressIndicator(
                            progress = (item.quantity / 200f).coerceIn(0f, 1f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp),
                            color = when (item.status) {
                                "متوفر" -> Color(0xFF10B981)
                                "قليل" -> Color(0xFFF59E0B)
                                else -> Color(0xFFEF4444)
                            },
                            trackColor = Color.White.copy(alpha = 0.1f)
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun StatCard(
    label: String,
    value: String,
    icon: androidx.compose.material.icons.Icons.Filled,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF18181B)),
        border = BorderStroke(1.dp, color.copy(alpha = 0.2f)),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = value,
                    color = Color(0xFFFAFAFA),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = label,
                    color = Color(0xFFA1A1AA),
                    fontSize = 10.sp
                )
            }
        }
    }
}
