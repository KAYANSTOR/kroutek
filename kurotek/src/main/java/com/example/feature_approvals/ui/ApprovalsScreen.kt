package com.example.feature_approvals.ui
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.feature_customers.ui.exportTransactionsToCsv
import com.example.ui.DashboardMetricItem
import com.example.ui.theme.*
import com.example.models.*
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.platform.testTag
import android.widget.Toast
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.ImeAction
import com.example.utils.DocumentExporter
import androidx.core.content.FileProvider
import android.content.Intent
import android.net.Uri
import android.util.Log
import java.io.File
import java.nio.charset.StandardCharsets
import kotlinx.coroutines.withContext
import com.example.ui.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PendingApprovalsTab(
    dashboardViewModel: com.example.ui.DashboardViewModel,
    reportsViewModel: com.example.ui.ReportsViewModel,
    mainViewModel: com.example.ui.MainViewModel
) {
    val context = LocalContext.current
    val allPendingApprovals by dashboardViewModel.pendingApprovals.collectAsState()
    val isDark by mainViewModel.isDarkTheme.collectAsState()

    var showLinkDialog by remember { mutableStateOf(false) }
    var pendingToLink by remember { mutableStateOf<com.example.models.PendingApproval?>(null) }
    var enteredPhoneNumber by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.End
    ) {
        // Tab Title
        item {
            Column(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "التفويضات المعلقة والموافقة ⏳",
                    color = if (isDark) GlowEmeraldGreen else Color(0xFF00796B),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Right
                )
                Text(
                    text = "العمليات التي تنتظر موافقتك اليدوية لإرسال الكود وتأكيد حركة الدفع.",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Right,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        if (allPendingApprovals.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                    border = BorderStroke(1.dp, if (isDark) Color(0xFF2D2D2D) else Color(0x0A000000)),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = if (isDark) 0.dp else 2.dp),
                    modifier = Modifier.fillMaxWidth().padding(top = 24.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CheckCircle,
                            contentDescription = "لا توجد تفويضات",
                            tint = if (isDark) GlowEmeraldGreen else Color(0xFF00796B),
                            modifier = Modifier.size(54.dp)
                        )
                        Text(
                            text = "لا توجد عمليات معلقة حالياً 🎉",
                            color = PureWhite,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = "جميع العمليات الواردة الأخرى يتم تفعيلها تلقائياً بنجاح.",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        } else {
            items(allPendingApprovals.size) { index ->
                val pending = allPendingApprovals[index]
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                    border = BorderStroke(1.dp, if (isDark) GlowEmeraldGreen.copy(alpha = 0.25f) else Color(0x3BFFCC80)),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = if (isDark) 0.dp else 4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        // Card Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Wallet Badge
                            val badgeBgColor = when (pending.walletType) {
                                "جيب" -> Color(0xFF1565C0).copy(alpha = 0.15f)
                                "جوالي" -> Color(0xFF2E7D32).copy(alpha = 0.15f)
                                "كريمي" -> BrandPrimaryDark.copy(alpha = 0.15f)
                                "حاسب" -> Color(0xFFEF6C00).copy(alpha = 0.15f)
                                "ون كاش" -> Color(0xFF880E4F).copy(alpha = 0.15f)
                                else -> Color(0xFF4527A0).copy(alpha = 0.15f)
                            }
                            val badgeTextColor = when (pending.walletType) {
                                "جيب" -> Color(0xFF90CAF9)
                                "جوالي" -> Color(0xFFA5D6A7)
                                "كريمي" -> Color(0xFFEF9A9A)
                                "حاسب" -> Color(0xFFFFCC80)
                                "ون كاش" -> Color(0xFFF48FB1)
                                else -> Color(0xFFB39DDB)
                            }
                            Surface(
                                color = badgeBgColor,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = pending.walletType,
                                    color = badgeTextColor,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }

                            // Amount Label
                            Text(
                                text = "فئة ${pending.amount} ر.ي",
                                color = if (isDark) GlowEmeraldGreen else Color(0xFF00796B),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        HorizontalDivider(color = if (isDark) Color(0xFF2D2D2D) else Color(0x0A000000))

                        // Details Block
                        Column(
                            horizontalAlignment = Alignment.End,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.End,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = pending.phone,
                                    color = PureWhite,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = ":رقم المودع/المستلم",
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.End,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = if (pending.isAccountCode) "كود حساب (مطابقة فريدة)" else "شراء كارت مباشر",
                                    color = if (pending.isAccountCode) (if (isDark) GlowOrangeGold else Color(0xFFE65100)) else PureWhite,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = ":نوع الدفعة",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        val isName = pending.phone.any { it.isLetter() }
                        if (isName) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isDark) Color(0xFF331F00) else Color(0xFFFFF3E0))
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextButton(
                                    onClick = {
                                        pendingToLink = pending
                                        enteredPhoneNumber = ""
                                        showLinkDialog = true
                                    },
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    colors = ButtonDefaults.textButtonColors(contentColor = if (isDark) GlowOrangeGold else Color(0xFFE65100))
                                ) {
                                    Icon(imageVector = Icons.Default.Phone, contentDescription = "ربط", modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("ربط رقم العميل 📞", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                                Text(
                                    text = "الحساب اسم وليس رقم هاتف!",
                                    color = if (isDark) GlowOrangeGold else Color(0xFFE65100),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Right
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Action Buttons Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Reject Button (Red outline)
                            OutlinedButton(
                                onClick = {
                                    dashboardViewModel.rejectPending(pending.id)
                                    Toast.makeText(context, "تم رفض المعاملة اليدوية وإلغاؤها", Toast.LENGTH_SHORT).show()
                                },
                                border = BorderStroke(1.dp, StatusRed),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusRed),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.weight(1f).height(42.dp)
                            ) {
                                Text("رفض الدفعة ✖", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            // Approve Button (Emerald Green style)
                            Button(
                                onClick = {
                                    dashboardViewModel.approvePending(pending.id) { success, message ->
                                        if (success) {
                                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Color.White),
                                contentPadding = PaddingValues(),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.weight(1.3f).height(42.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(EmeraldGreenGradient),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("موافقة وإرسال ✔", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // --- 📞 نافذة ربط هاتف العميل للتفويض المعلق 📞 ---
    if (showLinkDialog && pendingToLink != null) {
        val pending = pendingToLink!!
        AlertDialog(
            onDismissRequest = { 
                showLinkDialog = false
                pendingToLink = null 
            },
            title = {
                Text(
                    text = "ربط رقم هاتف للعميل 📞",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Right,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "الاسم المُستلم: ${pending.phone}",
                        color = PureWhite,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Right,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "أدخل رقم الهاتف الصحيح لهذا العميل لإرسال كود الشحن إليه تلقائياً عند الموافقة وحفظه للمستقبل:",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Right,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = enteredPhoneNumber,
                        onValueChange = { enteredPhoneNumber = it },
                        label = { Text("رقم جوال العميل") },
                        placeholder = { Text("مثال: 777123456") },
                        singleLine = true,
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Phone
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Right, color = PureWhite),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = PureWhite,
                            unfocusedTextColor = PureWhite,
                            focusedBorderColor = if (isDark) GlowEmeraldGreen else Color(0xFF00796B),
                            unfocusedBorderColor = TextSecondary.copy(alpha = 0.2f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val cleanedPhone = enteredPhoneNumber.trim()
                        if (cleanedPhone.isNotEmpty()) {
                            // 1. Create mapping in the database
                            reportsViewModel.addMapping(
                                uniqueId = pending.phone.trim(),
                                phone = cleanedPhone,
                                name = pending.phone.trim(),
                                walletType = pending.walletType
                            )
                            // 2. Update the pending approval phone number itself in DB
                            dashboardViewModel.updatePendingApprovalPhone(pending.id, cleanedPhone)
                            
                            Toast.makeText(context, "تم ربط الرقم وحفظ الارتباط بنجاح! 📱", Toast.LENGTH_SHORT).show()
                            showLinkDialog = false
                            pendingToLink = null
                        } else {
                            Toast.makeText(context, "الرجاء إدخال رقم هاتف صحيح", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = if (isDark) GlowEmeraldGreen else Color(0xFF00796B)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("حفظ وربط ✔", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        showLinkDialog = false
                        pendingToLink = null
                    },
                    border = BorderStroke(1.dp, TextSecondary.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("إلغاء", fontSize = 12.sp, color = TextSecondary)
                }
            },
            containerColor = SurfaceDark,
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.border(BorderStroke(1.5.dp, if (isDark) Color(0xFF2D2D2D) else Color(0x1F000000)), RoundedCornerShape(20.dp))
        )
    }
}

