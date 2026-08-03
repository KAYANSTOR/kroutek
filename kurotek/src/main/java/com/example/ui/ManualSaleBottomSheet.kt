package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.ContactPhone
import androidx.compose.material.icons.outlined.Money
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material.icons.outlined.Receipt
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
fun ManualSaleBottomSheet(
    onDismiss: () -> Unit
) {
    var phoneNumber by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var customerName by remember { mutableStateOf("") }
    var selectedPaymentMethod by remember { mutableStateOf(0) } // 0 = Cash, 1 = Credit

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = PureWhite,
        dragHandle = { BottomSheetDefaults.DragHandle(color = Color.LightGray) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Outlined.Close, contentDescription = "إغلاق", tint = Color.Gray)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("بيع مباشر - يدوي", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(androidx.compose.foundation.shape.CircleShape)
                            .background(TealPrimary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Outlined.Add, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(20.dp))
                    }
                }
            }
            
            Text("أدخل بيانات العميل لتسجيل بيع يدوي:", color = Color.Gray, fontSize = 14.sp, modifier = Modifier.padding(vertical = 16.dp))
            
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                placeholder = { Text("رقم الجوال", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TealPrimary,
                    unfocusedBorderColor = Color.LightGray,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                ),
                shape = RoundedCornerShape(12.dp),
                trailingIcon = { Icon(Icons.Outlined.PhoneAndroid, contentDescription = null, tint = Color.Gray) },
                leadingIcon = { Icon(Icons.Outlined.ContactPhone, contentDescription = null, tint = TealPrimary) },
                singleLine = true
            )
            Text("9 أرقام تبدأ بـ 7", color = Color.Gray, fontSize = 10.sp, modifier = Modifier.padding(top = 4.dp, bottom = 12.dp))
            
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                placeholder = { Text("المبلغ", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TealPrimary,
                    unfocusedBorderColor = Color.LightGray,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                ),
                shape = RoundedCornerShape(12.dp),
                trailingIcon = { Icon(Icons.Outlined.AttachMoney, contentDescription = null, tint = Color.Gray) },
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            OutlinedTextField(
                value = customerName,
                onValueChange = { customerName = it },
                placeholder = { Text("الاسم", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TealPrimary,
                    unfocusedBorderColor = Color.LightGray,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                ),
                shape = RoundedCornerShape(12.dp),
                trailingIcon = { Icon(Icons.Outlined.Person, contentDescription = null, tint = Color.Gray) },
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text("طريقة البيع", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.padding(bottom = 8.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(
                    onClick = { selectedTab = 1 },
                    modifier = Modifier.weight(1f).height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedTab == 1) TealPrimary.copy(alpha = 0.1f) else PureWhite,
                        contentColor = if (selectedTab == 1) TealPrimary else Color.Gray
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (selectedTab == 1) TealPrimary else Color.LightGray)
                ) {
                    Icon(Icons.Outlined.Receipt, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("آجل", fontWeight = FontWeight.Bold)
                }
                
                Button(
                    onClick = { selectedTab = 0 },
                    modifier = Modifier.weight(1f).height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedTab == 0) TealPrimary.copy(alpha = 0.1f) else PureWhite,
                        contentColor = if (selectedTab == 0) TealPrimary else Color.Gray
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (selectedTab == 0) TealPrimary else Color.LightGray)
                ) {
                    Icon(Icons.Outlined.Money, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("نقدي", fontWeight = FontWeight.Bold)
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onDismiss) {
                    Text("إلغاء", color = Color.Gray, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(
                    onClick = { /* Confirm */ },
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.height(48.dp).weight(1f)
                ) {
                    Text("تأكيد البيع المباشر", color = PureWhite, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = PureWhite)
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
