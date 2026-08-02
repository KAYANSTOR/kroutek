package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Title
import androidx.compose.material.icons.outlined.Description
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
fun AddMessageTemplateBottomSheet(
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = ZNetBackgroundDark,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            enabled = title.isNotBlank() && content.isNotBlank(),
                    modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                enabled = title.isNotBlank() && content.isNotBlank(),
                    modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "إنشاء قالب رسالة جديد",
                    color = PureWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Outlined.Close, contentDescription = "إغلاق", tint = PureWhite)
                }
            }

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("عنوان القالب") },
                placeholder = { Text("مثال: تسديد باقة انترنت") },
                enabled = title.isNotBlank() && content.isNotBlank(),
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
                leadingIcon = { Icon(Icons.Outlined.Title, contentDescription = null, tint = TextSecondary) },
                singleLine = true
            )

            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("محتوى الرسالة") },
                placeholder = { Text("اكتب محتوى الرسالة هنا... يمكنك استخدام المتغيرات مثل [الرصيد]") },
                enabled = title.isNotBlank() && content.isNotBlank(),
                    modifier = Modifier.fillMaxWidth().height(150.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TealPrimary,
                    unfocusedBorderColor = TextSecondary.copy(alpha = 0.5f),
                    focusedLabelColor = TealPrimary,
                    unfocusedLabelColor = TextSecondary,
                    focusedTextColor = PureWhite,
                    unfocusedTextColor = PureWhite
                ),
                shape = RoundedCornerShape(12.dp),
                leadingIcon = { Icon(Icons.Outlined.Description, contentDescription = null, tint = TextSecondary) }
            )

            Spacer(enabled = title.isNotBlank() && content.isNotBlank(),
                    modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if(title.isNotBlank() && content.isNotBlank()) onSave(title, content)
                    onDismiss()
                },
                enabled = title.isNotBlank() && content.isNotBlank(),
                    modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Outlined.Check, contentDescription = null, tint = PureWhite)
                Spacer(enabled = title.isNotBlank() && content.isNotBlank(),
                    modifier = Modifier.width(8.dp))
                Text("حفظ القالب", color = PureWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
