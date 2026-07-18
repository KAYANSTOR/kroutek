package com.example.feature_settings.ui
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
// ==========================================
// TAB 4: الإعدادات (Settings Tab)
// ==========================================
@Composable
fun SettingsTab(
    mainViewModel: MainViewModel,
    authViewModel: com.example.ui.AuthViewModel,
    settingsViewModel: com.example.ui.SettingsViewModel,
    distributorViewModel: com.example.ui.DistributorViewModel,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val isAutoSendSmsEnabled by settingsViewModel.isAutoSendSmsEnabled.collectAsState()
    val isNotificationClickComposeEnabled by settingsViewModel.isNotificationClickComposeEnabled.collectAsState()
    val generalSmsTemplate by settingsViewModel.generalSmsTemplate.collectAsState()
    val networkName by settingsViewModel.networkName.collectAsState()
    val isDarkTheme by mainViewModel.isDarkTheme.collectAsState()

    val isJeebEnabled by settingsViewModel.isJeebEnabled.collectAsState()
    val isJawaliEnabled by settingsViewModel.isJawaliEnabled.collectAsState()
    val isKuraimiEnabled by settingsViewModel.isKuraimiEnabled.collectAsState()
    val isHasebEnabled by settingsViewModel.isHasebEnabled.collectAsState()
    val isOneCashEnabled by settingsViewModel.isOneCashEnabled.collectAsState()
    val isMFloosEnabled by settingsViewModel.isMFloosEnabled.collectAsState()

    // Configuration Inputs
    var editNetworkNameText by remember { mutableStateOf(networkName) }
    var editSmsTemplateText by remember { mutableStateOf(generalSmsTemplate) }
    var editNewSerialText by remember { mutableStateOf("") }

    var feedbackMsg by remember { mutableStateOf("") }
    var feedbackSuccess by remember { mutableStateOf(true) }

    val isDistributorModeActive by distributorViewModel.isDistributorModeActive.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.End
    ) {
        // ✅ بطاقة تبديل وضع الموزع / SMS - في أعلى الإعدادات
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = BorderStroke(1.5.dp, if (isDistributorModeActive) androidx.compose.ui.graphics.Color(0xFF10B981).copy(alpha = 0.4f) else (if (isDarkTheme) androidx.compose.ui.graphics.Color(0xFF2D2D2D) else androidx.compose.ui.graphics.Color(0x0A000000))),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Switch(
                        checked = isDistributorModeActive,
                        onCheckedChange = { distributorViewModel.setDistributorModeActive(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = androidx.compose.ui.graphics.Color.White,
                            checkedTrackColor = androidx.compose.ui.graphics.Color(0xFF10B981),
                            uncheckedThumbColor = TextSecondary,
                            uncheckedTrackColor = SurfaceDark
                        )
                    )
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = if (isDistributorModeActive) "وضع الموزع النشط 🏪" else "وضع نظام SMS/الرسائل 📱",
                            color = if (isDistributorModeActive) androidx.compose.ui.graphics.Color(0xFF10B981) else (if (isDarkTheme) GlowPurplePink else androidx.compose.ui.graphics.Color(0xFF7B1FA2)),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = if (isDistributorModeActive) "انقر للتبديل إلى نظام الرسائل SMS" else "انقر للتبديل إلى نظام الموزع والحاسبة",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = BorderStroke(1.dp, if (isDarkTheme) Color(0xFF2D2D2D) else Color(0x0A000000)),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = if (isDarkTheme) 0.dp else 4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "خيارات إرسال ومشاركة كروت الشحن ⚙️",
                        color = if (isDarkTheme) GlowPurplePink else Color(0xFF7B1FA2),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )

                    // 1. Switch Auto Send SMS in background
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Switch(
                            checked = isAutoSendSmsEnabled,
                            onCheckedChange = { settingsViewModel.toggleAutoSendSms(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = if (isDarkTheme) DeepBlack else Color.White,
                                checkedTrackColor = if (isDarkTheme) GlowPurplePink else Color(0xFF7B1FA2)
                            )
                        )
                        Text(
                            text = "تفعيل الإرسال المباشر تلقائياً (SMS)",
                            color = PureWhite,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // 2. Switch share notification opens default edit composer
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Switch(
                            checked = isNotificationClickComposeEnabled,
                            onCheckedChange = { settingsViewModel.toggleNotificationClickCompose(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = if (isDarkTheme) DeepBlack else Color.White,
                                checkedTrackColor = if (isDarkTheme) GlowPurplePink else Color(0xFF7B1FA2)
                            )
                        )
                        Text(
                            text = "مشاركة فتح الرسالة تلقائياً من الإشعار",
                            color = PureWhite,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    HorizontalDivider(color = GoldAccent.copy(alpha = 0.15f), modifier = Modifier.padding(vertical = 4.dp))
                    
                    Text(
                        text = "تفعيل دفعات المحافظ المدعومة 💳",
                        color = if (isDarkTheme) GlowPurplePink else Color(0xFF7B1FA2),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )

                    // Switches for each individual wallet
                    val walletList = listOf(
                        Triple("تفعيل استقبال دفعات (محفظة كاش)", isJeebEnabled) { v: Boolean -> settingsViewModel.toggleJeeb(v) },
                        Triple("تفعيل استقبال دفعات (جوالي)", isJawaliEnabled) { v: Boolean -> settingsViewModel.toggleJawali(v) },
                        Triple("تفعيل استقبال دفعات (كريمي)", isKuraimiEnabled) { v: Boolean -> settingsViewModel.toggleKuraimi(v) },
                        Triple("تفعيل استقبال دفعات (حاسب)", isHasebEnabled) { v: Boolean -> settingsViewModel.toggleHaseb(v) },
                        Triple("تفعيل استقبال دفعات (ون كاش)", isOneCashEnabled) { v: Boolean -> settingsViewModel.toggleOneCash(v) },
                        Triple("تفعيل استقبال دفعات (ام فلوس)", isMFloosEnabled) { v: Boolean -> settingsViewModel.toggleMFloos(v) }
                    )

                    walletList.forEach { (label, isEnabled, onToggle) ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Switch(
                                checked = isEnabled,
                                onCheckedChange = onToggle,
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = if (isDarkTheme) DeepBlack else Color.White,
                                    checkedTrackColor = if (isDarkTheme) GlowPurplePink else Color(0xFF7B1FA2)
                                )
                            )
                            Text(
                                text = label,
                                color = PureWhite,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // Customizable Templates & Brand
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = BorderStroke(1.dp, if (isDarkTheme) Color(0xFF2D2D2D) else Color(0x0A000000)),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = if (isDarkTheme) 0.dp else 4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "تخصيص البيانات والرسائل 📝",
                        color = if (isDarkTheme) GlowPurplePink else Color(0xFF7B1FA2),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )

                    // Network Name
                    OutlinedTextField(
                        value = editNetworkNameText,
                        onValueChange = { editNetworkNameText = it; feedbackMsg = "" },
                        label = { Text("اسم الشبكة (بدون التفعيل)") },
                        modifier = Modifier.fillMaxWidth().testTag("settings_network_name_fld"),
                        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Right, color = PureWhite),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (isDarkTheme) GlowPurplePink else Color(0xFF7B1FA2),
                            unfocusedBorderColor = TextSecondary.copy(alpha = 0.25f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )

                    // Message Template
                    OutlinedTextField(
                        value = editSmsTemplateText,
                        onValueChange = { editSmsTemplateText = it; feedbackMsg = "" },
                        label = { Text("صيغة الرسالة المرسلة تلقائياً لشبكتك") },
                        modifier = Modifier.fillMaxWidth().height(115.dp).testTag("settings_sms_tpl_fld"),
                        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Right, color = PureWhite, fontSize = 13.sp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (isDarkTheme) GlowPurplePink else Color(0xFF7B1FA2),
                            unfocusedBorderColor = TextSecondary.copy(alpha = 0.25f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )

                    // Change Serial directly
                    OutlinedTextField(
                        value = editNewSerialText,
                        onValueChange = { editNewSerialText = it; feedbackMsg = "" },
                        label = { Text("تغيير السيريال (كلمة السر الجديدة)") },
                        placeholder = { Text("أدخل رمز تفعيل أو باسورد جديد مباشرة هنا") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("settings_password_fld"),
                        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Right, color = PureWhite),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (isDarkTheme) GlowPurplePink else Color(0xFF7B1FA2),
                            unfocusedBorderColor = TextSecondary.copy(alpha = 0.25f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )

                    if (feedbackMsg.isNotEmpty()) {
                        Text(
                            text = feedbackMsg,
                            color = if (feedbackSuccess) StatusGreen else StatusRed,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Right
                        )
                    }

                    // Save Modifications (Premium Purple-Pink Gradient)
                    Button(
                        onClick = {
                            if (editNetworkNameText.trim().isNotEmpty()) {
                                settingsViewModel.updateNetworkName(editNetworkNameText.trim())
                            }
                            if (editSmsTemplateText.trim().isNotEmpty()) {
                                settingsViewModel.updateGeneralSmsTemplate(editSmsTemplateText.trim())
                            }
                            if (editNewSerialText.trim().isNotEmpty()) {
                                authViewModel.setAppPasswordDirectly(editNewSerialText.trim())
                                editNewSerialText = ""
                            }
                            feedbackSuccess = true
                            feedbackMsg = "تم حفظ التعديلات والبيانات الجديدة بنجاح! ✔"
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Color.White),
                        contentPadding = PaddingValues(),
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("settings_save_btn")
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(PurplePinkGradient)
                                .padding(horizontal = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("حفظ التخصيصات والبيانات", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }
        }

        // Appearance
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = BorderStroke(1.dp, if (isDarkTheme) Color(0xFF2D2D2D) else Color(0x0A000000)),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = if (isDarkTheme) 0.dp else 4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Switch(
                        checked = isDarkTheme,
                        onCheckedChange = { mainViewModel.setDarkTheme(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = if (isDarkTheme) DeepBlack else Color.White,
                            checkedTrackColor = if (isDarkTheme) GlowPurplePink else Color(0xFF7B1FA2)
                        )
                    )

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "مظهر التطبيق والموضوع 🌗",
                            color = if (isDarkTheme) GlowPurplePink else Color(0xFF7B1FA2),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "التبديل بين المظهر الداكن (OLED) والمظهر الفاتح",
                            color = TextSecondary,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }

        // Developer info card fixed at the bottom
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark.copy(alpha = 0.6f)),
                border = BorderStroke(1.dp, if (isDarkTheme) Color(0xFF2D2D2D) else Color(0x0A000000)),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth().testTag("developer_card")
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "معلومات المطور والدعم الفني 👨‍💻",
                        color = if (isDarkTheme) GlowPurplePink else Color(0xFF7B1FA2),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                    HorizontalDivider(color = GoldAccent.copy(alpha = 0.15f))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "كيان سوفت", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text(text = "المطور:", color = TextSecondary, fontSize = 12.sp)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "773303455", color = if (isDarkTheme) GlowPurplePink else Color(0xFF7B1FA2), fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
                        Text(text = "للتواصل:", color = TextSecondary, fontSize = 12.sp)
                    }
                }
            }
        }

        // Logout panel/Action button
        item {
            Button(
                onClick = onLogout,
                colors = ButtonDefaults.buttonColors(containerColor = StatusRed.copy(alpha = 0.1f), contentColor = StatusRed),
                border = BorderStroke(1.dp, StatusRed.copy(alpha = 0.3f)),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth().height(50.dp).testTag("settings_logout_btn")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Outlined.ExitToApp, contentDescription = "تسجيل خروج", modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "إلغاء التفعيل والاشتراك الحالي", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }
}

