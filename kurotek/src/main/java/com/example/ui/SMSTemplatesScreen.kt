package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Delete
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

data class SMSTemplate(
    val id: String,
    val title: String,
    val template: String,
    val category: String, // "customers" or "offers"
    val createdAt: Long = System.currentTimeMillis()
)

@Composable
fun SMSTemplatesScreen(
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current
    var templates by remember {
        mutableStateOf(listOf(
            SMSTemplate(
                id = "1",
                title = "ترحيب العملاء",
                template = "مرحباً بك في {storeName}. شكراً لك على زيارتك!",
                category = "customers"
            ),
            SMSTemplate(
                id = "2",
                title = "تأكيد الطلب",
                template = "تم استقبال طلبك برقم {orderNumber}. سيتم تسليمه في {deliveryDate}",
                category = "customers"
            ),
            SMSTemplate(
                id = "3",
                title = "عرض خاص",
                template = "عرض محدود! احصل على {discount}% على جميع المنتجات. ينتهي في {expiryDate}",
                category = "offers"
            )
        ))
    }

    var showAddDialog by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf("customers") }
    var newTitle by remember { mutableStateOf("") }
    var newTemplate by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF09090B))
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
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
                            text = "قوالب الرسائل",
                            color = Color(0xFFFAFAFA),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "${templates.size} قالب متاح",
                            color = Color(0xFFA1A1AA),
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // Category Tabs
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("customers" to "العملاء", "offers" to "العروض").forEach { (category, label) ->
                        Button(
                            onClick = { selectedCategory = category },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedCategory == category) 
                                    Color(0xFF1A9B8E) 
                                else 
                                    Color(0xFF18181B)
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).height(40.dp)
                        ) {
                            Text(
                                text = label,
                                color = Color(0xFFFAFAFA),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Add Template Button
            item {
                Button(
                    onClick = { showAddDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A9B8E)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = Color(0xFFFAFAFA),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "إضافة قالب جديد",
                            color = Color(0xFFFAFAFA),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Templates List
            items(templates.filter { it.category == selectedCategory }) { template ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF18181B)),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Title and Actions
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = template.title,
                                color = Color(0xFFFAFAFA),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = { Toast.makeText(context, "تعديل: ${template.title}", Toast.LENGTH_SHORT).show() },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Edit,
                                        contentDescription = "تعديل",
                                        tint = Color(0xFF1A9B8E),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        templates = templates.filter { it.id != template.id }
                                        Toast.makeText(context, "تم حذف القالب", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Delete,
                                        contentDescription = "حذف",
                                        tint = Color(0xFFEF4444),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        // Template Preview
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.3f)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = template.template,
                                color = Color(0xFFA1A1AA),
                                fontSize = 12.sp,
                                lineHeight = 16.sp,
                                modifier = Modifier.padding(12.dp)
                            )
                        }

                        // Copy Button
                        Button(
                            onClick = {
                                Toast.makeText(context, "تم نسخ القالب", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1A9B8E).copy(alpha = 0.2f)
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(36.dp)
                        ) {
                            Text(
                                text = "نسخ النموذج",
                                color = Color(0xFF1A9B8E),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Empty State
            if (templates.filter { it.category == selectedCategory }.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MailOutline,
                                contentDescription = null,
                                tint = Color(0xFF1A9B8E).copy(alpha = 0.3f),
                                modifier = Modifier.size(48.dp)
                            )
                            Text(
                                text = "لا توجد قوالب للعرض",
                                color = Color(0xFFA1A1AA),
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Add Template Dialog
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = {
                Text(
                    "إضافة قالب جديد",
                    color = Color(0xFFFAFAFA),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    OutlinedTextField(
                        value = newTitle,
                        onValueChange = { newTitle = it },
                        label = { Text("عنوان القالب") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF1A9B8E),
                            focusedTextColor = Color(0xFFFAFAFA),
                            unfocusedTextColor = Color(0xFFFAFAFA)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = newTemplate,
                        onValueChange = { newTemplate = it },
                        label = { Text("نص القالب") },
                        minLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF1A9B8E),
                            focusedTextColor = Color(0xFFFAFAFA),
                            unfocusedTextColor = Color(0xFFFAFAFA)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newTitle.isNotEmpty() && newTemplate.isNotEmpty()) {
                            templates = templates + SMSTemplate(
                                id = System.currentTimeMillis().toString(),
                                title = newTitle,
                                template = newTemplate,
                                category = selectedCategory
                            )
                            newTitle = ""
                            newTemplate = ""
                            showAddDialog = false
                            Toast.makeText(context, "تم إضافة القالب بنجاح", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A9B8E))
                ) {
                    Text("إضافة", color = Color(0xFFFAFAFA))
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("إلغاء", color = Color(0xFF1A9B8E))
                }
            },
            containerColor = Color(0xFF18181B),
            tonalElevation = 0.dp
        )
    }
}
