package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FileDownload
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

data class POSTransaction(
    val id: String,
    val date: String,
    val time: String,
    val amount: Double,
    val status: String,
    val paymentMethod: String,
    val itemCount: Int
)

@Composable
fun POSReportsScreen(
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current

    val transactions = listOf(
        POSTransaction("001", "2026-07-26", "09:30", 150.0, "نجح", "نقد", 3),
        POSTransaction("002", "2026-07-26", "10:15", 200.0, "نجح", "بطاقة", 5),
        POSTransaction("003", "2026-07-26", "11:45", 75.0, "معلق", "محفظة رقمية", 2),
        POSTransaction("004", "2026-07-26", "14:20", 320.0, "نجح", "نقد", 8),
        POSTransaction("005", "2026-07-26", "15:50", 180.0, "نجح", "بطاقة", 4),
    )

    val totalSales = transactions.sumOf { it.amount }
    val successfulTransactions = transactions.filter { it.status == "نجح" }.size
    val pendingTransactions = transactions.filter { it.status == "معلق" }.size

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
                            text = "تقرير نقاط البيع",
                            color = Color(0xFFFAFAFA),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "اليوم - 26 يوليو 2026",
                            color = Color(0xFFA1A1AA),
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // Summary Cards
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Total Sales
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF18181B)),
                        border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.2f)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.SpaceBetween,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.AttachMoney,
                                contentDescription = null,
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(24.dp)
                            )
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "$totalSales ر.ي",
                                    color = Color(0xFFFAFAFA),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "إجمالي المبيعات",
                                    color = Color(0xFFA1A1AA),
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }

                    // Successful Transactions
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF18181B)),
                        border = BorderStroke(1.dp, Color(0xFF3B82F6).copy(alpha = 0.2f)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.SpaceBetween,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF3B82F6),
                                modifier = Modifier.size(24.dp)
                            )
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "$successfulTransactions",
                                    color = Color(0xFFFAFAFA),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "معاملات ناجحة",
                                    color = Color(0xFFA1A1AA),
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }

                    // Pending Transactions
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF18181B)),
                        border = BorderStroke(1.dp, Color(0xFFF59E0B).copy(alpha = 0.2f)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.SpaceBetween,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = Color(0xFFF59E0B),
                                modifier = Modifier.size(24.dp)
                            )
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "$pendingTransactions",
                                    color = Color(0xFFFAFAFA),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "معاملات معلقة",
                                    color = Color(0xFFA1A1AA),
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }

            // Export Button
            item {
                Button(
                    onClick = {
                        Toast.makeText(context, "جاري تصدير التقرير...", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A9B8E)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.FileDownload,
                        contentDescription = null,
                        tint = Color(0xFFFAFAFA),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "تصدير التقرير (CSV/PDF)",
                        color = Color(0xFFFAFAFA),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Transactions Table Header
            item {
                Text(
                    text = "المعاملات",
                    color = Color(0xFFFAFAFA),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Transactions List
            items(transactions) { transaction ->
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
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Header Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Status Badge
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = when (transaction.status) {
                                            "نجح" -> Color(0xFF10B981).copy(alpha = 0.15f)
                                            "معلق" -> Color(0xFFF59E0B).copy(alpha = 0.15f)
                                            else -> Color(0xFFEF4444).copy(alpha = 0.15f)
                                        },
                                        shape = RoundedCornerShape(6.dp)
                                    )
                                    .border(
                                        BorderStroke(
                                            1.dp,
                                            when (transaction.status) {
                                                "نجح" -> Color(0xFF10B981)
                                                "معلق" -> Color(0xFFF59E0B)
                                                else -> Color(0xFFEF4444)
                                            }
                                        ),
                                        RoundedCornerShape(6.dp)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = transaction.status,
                                    color = when (transaction.status) {
                                        "نجح" -> Color(0xFF10B981)
                                        "معلق" -> Color(0xFFF59E0B)
                                        else -> Color(0xFFEF4444)
                                    },
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Text(
                                text = transaction.id,
                                color = Color(0xFFA1A1AA),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Details Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                horizontalAlignment = Alignment.End,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "${transaction.amount} ر.ي",
                                    color = Color(0xFFFAFAFA),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = transaction.paymentMethod,
                                    color = Color(0xFFA1A1AA),
                                    fontSize = 10.sp
                                )
                            }

                            Column(
                                horizontalAlignment = Alignment.End,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = transaction.time,
                                    color = Color(0xFFFAFAFA),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${transaction.itemCount} منتج",
                                    color = Color(0xFFA1A1AA),
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
