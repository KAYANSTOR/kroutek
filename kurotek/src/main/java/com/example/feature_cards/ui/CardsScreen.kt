package com.example.feature_cards.ui
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
// ==========================================
// TAB 2: الكروت (Cards Tab)
// ==========================================
@Composable
fun CardsTab(viewModel: MainViewModel) {
    val context = LocalContext.current
    val isAutoSendSmsEnabled by viewModel.isAutoSendSmsEnabled.collectAsState()
    val allCards by viewModel.allCards.collectAsState()
    val isDark by viewModel.isDarkTheme.collectAsState()
    
    // Form States
    var selectedCategoryForAdding by remember { mutableStateOf(100) }
    var inputModeBulk by remember { mutableStateOf(true) } // true = bulk lines, false = single input
    var bulkInputText by remember { mutableStateOf("") }
    var singleCodeText by remember { mutableStateOf("") }
    var singleUsernameText by remember { mutableStateOf("") }
    var singlePasswordText by remember { mutableStateOf("") }
    
    var showAddCardsSection by remember { mutableStateOf(false) }
    var feedbackMsg by remember { mutableStateOf("") }
    var feedbackSuccess by remember { mutableStateOf(true) }

    // Deletion states
    var showDeleteBottomSheet by remember { mutableStateOf(false) }
    var cardToDelete by remember { mutableStateOf<Card?>(null) }

    // Categories filter for inventory view
    var selectedViewCategory by remember { mutableStateOf(100) }
    val cardFormatMode by viewModel.cardFormatMode.collectAsState() // "user_pass" or "user_only"

    // Active cards filtering (category, unused)
    val filteredCards = remember(allCards, selectedViewCategory) {
        allCards.filter { it.category == selectedViewCategory && !it.used }
    }

    // Sell Confirmation States
    var showConfirmSellDialog by remember { mutableStateOf(false) }
    var cardToConfirmSell by remember { mutableStateOf<Card?>(null) }
    var selectedShareWallet by remember { mutableStateOf("جوالي") }

    // Dynamic Table States
    var tableStatusFilter by remember { mutableStateOf("الكل") } // "الكل", "متاحة", "تم توزيعها / منتهية"
    var tableCategoryFilter by remember { mutableStateOf("الكل") } // "الكل", "100", "200", "250", "300", "500"
    var tableSearchQuery by remember { mutableStateOf("") }

    val tableFilteredCards = remember(allCards, tableStatusFilter, tableCategoryFilter, tableSearchQuery) {
        allCards.filter { card ->
            val matchesStatus = when (tableStatusFilter) {
                "متاحة" -> !card.used
                "تم توزيعها" -> card.used && (card.id % 2 == 0)
                "منتهية" -> card.used && (card.id % 2 != 0)
                else -> true
            }
            val matchesCategory = when (tableCategoryFilter) {
                "الكل" -> true
                else -> card.category.toString() == tableCategoryFilter
            }
            val cardDisplay = if (card.password.isNotEmpty()) {
                "${card.username} ${card.password}"
            } else {
                card.code
            }
            val matchesSearch = tableSearchQuery.isEmpty() || cardDisplay.contains(tableSearchQuery, ignoreCase = true) || card.id.toString().contains(tableSearchQuery)
            
            matchesStatus && matchesCategory && matchesSearch
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.End
        ) {
        // Direct Send Switch (SMS Auto Send)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = BorderStroke(1.dp, if (isDark) Color(0xFF2D2D2D) else Color(0x0A000000)),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = if (isDark) 0.dp else 4.dp),
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
                        checked = isAutoSendSmsEnabled,
                        onCheckedChange = { viewModel.toggleAutoSendSms(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = if (isDark) DeepBlack else Color.White,
                            checkedTrackColor = BrandPrimaryRed,
                            uncheckedThumbColor = TextSecondary,
                            uncheckedTrackColor = SurfaceDark
                        ),
                        modifier = Modifier.testTag("sms_direct_switch")
                    )

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "تفعيل الإرسال المباشر تلقائياً (SMS)",
                            color = BrandPrimaryRed,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Right
                        )
                        Text(
                            text = "إرسال الكود فوراً وصامتاً عند وصول إشعار إيداع في الخلفية",
                            color = TextSecondary,
                            fontSize = 10.sp,
                            textAlign = TextAlign.Right
                        )
                    }
                }
            }
        }

        // Toggle Expandable Form to ADD Cards (Vibrant Emerald Green Gradient Action button)
        item {
            Button(
                onClick = { 
                    showAddCardsSection = !showAddCardsSection 
                    feedbackMsg = ""
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.White
                ),
                contentPadding = PaddingValues(),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("btn_toggle_add_panel")
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            if (showAddCardsSection) {
                                Brush.horizontalGradient(listOf(Color(0xFF37474F), Color(0xFF263238)))
                            } else {
                                EmeraldGreenGradient
                            }
                        )
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (showAddCardsSection) Icons.Outlined.Close else Icons.Outlined.Add,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (showAddCardsSection) "إغلاق لوحة الإضافة السريعة" else "إضافة كروت شحن جديدة للمخزن ➕",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // Expanded Panel to Add Cards
        if (showAddCardsSection) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                    border = BorderStroke(1.dp, if (isDark) Color(0xFF2D2D2D) else Color(0x0F000000)),
                    shape = RoundedCornerShape(22.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = if (isDark) 0.dp else 4.dp),
                    modifier = Modifier.fillMaxWidth().testTag("add_cards_expanded_panel")
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = "تعبئة مخزون الكروت ⚙️",
                            color = BrandPrimaryRed,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Right
                        )

                        // 1. Selector for categories
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "اختر فئة الكرت:", color = PureWhite, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                listOf(100, 200, 250, 300, 500).forEach { cat ->
                                    val catColor = when (cat) {
                                        100 -> Category100Cardboard
                                        200 -> Category200Blue
                                        250 -> Category250Purple
                                        300 -> Category300Green
                                        500 -> Category500Turmeric
                                        else -> GoldPrimary
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(
                                                if (selectedCategoryForAdding == cat) {
                                                    catColor.copy(alpha = 0.2f)
                                                } else {
                                                    DeepBlack
                                                }
                                            )
                                            .border(
                                                width = 1.2.dp, 
                                                color = if (selectedCategoryForAdding == cat) catColor else catColor.copy(alpha = 0.2f), 
                                                shape = RoundedCornerShape(16.dp)
                                            )
                                            .clickable { selectedCategoryForAdding = cat }
                                            .padding(horizontal = 12.dp, vertical = 6.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = cat.toString(),
                                            color = if (selectedCategoryForAdding == cat) catColor else TextSecondary,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }

                        // 2. Select insert format: single or bulk (pills rounded)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(42.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(
                                        if (inputModeBulk) {
                                            BrandPrimaryRed.copy(alpha = 0.15f)
                                        } else {
                                            DeepBlack
                                        }
                                    )
                                    .clickable { inputModeBulk = true }
                                    .border(
                                        width = 1.2.dp, 
                                        color = if (inputModeBulk) {
                                            BrandPrimaryRed
                                        } else {
                                            Color(0x1F9E9E9E)
                                        }, 
                                        shape = RoundedCornerShape(20.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "إدخل جملة (Bulk)",
                                    color = if (inputModeBulk) {
                                        if (isDark) GlowEmeraldGreen else Color(0xFF00796B)
                                    } else {
                                        TextSecondary
                                    },
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(42.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(
                                        if (!inputModeBulk) {
                                            BrandPrimaryRed.copy(alpha = 0.15f)
                                        } else {
                                            DeepBlack
                                        }
                                    )
                                    .clickable { inputModeBulk = false }
                                    .border(
                                        width = 1.2.dp, 
                                        color = if (!inputModeBulk) {
                                            BrandPrimaryRed
                                        } else {
                                            Color(0x1F9E9E9E)
                                        }, 
                                        shape = RoundedCornerShape(20.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "إدخال فردي (Single)",
                                    color = if (!inputModeBulk) {
                                        if (isDark) GlowEmeraldGreen else Color(0xFF00796B)
                                    } else {
                                        TextSecondary
                                    },
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Form Inputs
                        if (inputModeBulk) {
                            OutlinedTextField(
                                value = bulkInputText,
                                onValueChange = { bulkInputText = it; feedbackMsg = "" },
                                label = { Text("أدخل الكروت (كرت في كل سطر)") },
                                placeholder = { Text("أكتب كود الكرت مباشرة أو بالصيغ في كل سطر...") },
                                modifier = Modifier.fillMaxWidth().height(110.dp).testTag("input_bulk_text"),
                                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Right, color = PureWhite, fontSize = 13.sp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = BrandPrimaryRed,
                                    unfocusedBorderColor = TextSecondary.copy(alpha = 0.25f),
                                    focusedContainerColor = DeepBlack,
                                    unfocusedContainerColor = DeepBlack
                                ),
                                shape = RoundedCornerShape(16.dp)
                            )
                        } else {
                            if (cardFormatMode == "user_only") {
                                OutlinedTextField(
                                    value = singleCodeText,
                                    onValueChange = { singleCodeText = it; feedbackMsg = "" },
                                    label = { Text("كود كرت الشحن") },
                                    placeholder = { Text("أدخل الكود المميز للكرت هنا") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth().testTag("input_single_code_only"),
                                    textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Right, color = PureWhite),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = if (isDark) GlowEmeraldGreen else Color(0xFF00796B),
                                        unfocusedBorderColor = TextSecondary.copy(alpha = 0.25f)
                                    ),
                                    shape = RoundedCornerShape(16.dp)
                                )
                            } else {
                                OutlinedTextField(
                                    value = singleUsernameText,
                                    onValueChange = { singleUsernameText = it; feedbackMsg = "" },
                                    label = { Text("اسم المستخدم (Username)") },
                                    placeholder = { Text("أدخل اسم المستخدم للكرت") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth().testTag("input_single_user"),
                                    textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Right, color = PureWhite),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = if (isDark) GlowEmeraldGreen else Color(0xFF00796B),
                                        unfocusedBorderColor = TextSecondary.copy(alpha = 0.25f)
                                    ),
                                    shape = RoundedCornerShape(16.dp)
                                )

                                OutlinedTextField(
                                    value = singlePasswordText,
                                    onValueChange = { singlePasswordText = it; feedbackMsg = "" },
                                    label = { Text("كلمة المرور (Password)") },
                                    placeholder = { Text("أدخل الرقم السري للكرت") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth().testTag("input_single_pass"),
                                    textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Right, color = PureWhite),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = if (isDark) GlowEmeraldGreen else Color(0xFF00796B),
                                        unfocusedBorderColor = TextSecondary.copy(alpha = 0.25f)
                                    ),
                                    shape = RoundedCornerShape(16.dp)
                                )
                            }
                        }

                        // Actions and Feedback
                        if (feedbackMsg.isNotEmpty()) {
                            Text(
                                text = feedbackMsg,
                                color = if (feedbackSuccess) StatusGreen else StatusRed,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Right,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        // Premium Emerald Green gradient submit button with highly rounded corners
                        Button(
                            onClick = {
                                if (inputModeBulk) {
                                    if (bulkInputText.trim().isEmpty()) {
                                        feedbackSuccess = false
                                        feedbackMsg = "الرجاء كتابة كروت لحفظها أولاً!"
                                        return@Button
                                    }
                                    viewModel.addCards(selectedCategoryForAdding, bulkInputText.trim()) { count ->
                                        feedbackSuccess = count > 0
                                        feedbackMsg = "تم حفظ $count كارت بنجاح في فئة $selectedCategoryForAdding ر.ي!"
                                        bulkInputText = ""
                                    }
                                } else {
                                    val isInvalidSingle = if (cardFormatMode == "user_only") {
                                        singleCodeText.trim().isEmpty()
                                    } else {
                                        singleUsernameText.trim().isEmpty() || singlePasswordText.trim().isEmpty()
                                    }

                                    if (isInvalidSingle) {
                                        feedbackSuccess = false
                                        feedbackMsg = "يرجى تعبئة الحقول المطلوبة للكرت الفردي!"
                                        return@Button
                                    }

                                    val card = if (cardFormatMode == "user_only") {
                                        Card(
                                            category = selectedCategoryForAdding,
                                            code = singleCodeText.trim(),
                                            username = singleCodeText.trim(),
                                            password = "",
                                            used = false
                                        )
                                    } else {
                                        Card(
                                            category = selectedCategoryForAdding,
                                            code = singleUsernameText.trim(),
                                            username = singleUsernameText.trim(),
                                            password = singlePasswordText.trim(),
                                            used = false
                                        )
                                    }

                                    viewModel.addSingleCard(selectedCategoryForAdding, card) { success ->
                                        feedbackSuccess = success
                                        feedbackMsg = if (success) "تم إضافة الكارت الفردي بنجاح!" else "حدث خطأ أثناء حفظ الكارت."
                                        singleCodeText = ""
                                        singleUsernameText = ""
                                        singlePasswordText = ""
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Color.White),
                            contentPadding = PaddingValues(),
                            shape = RoundedCornerShape(18.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("btn_save_added_cards")
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(PrimaryRedGradient)
                                    .padding(horizontal = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "حفظ وتثبيت الكروت في المخزن ✔", 
                                    fontWeight = FontWeight.Bold, 
                                    fontSize = 14.sp,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }

        // Inventory display section headers
        item {
            Text(
                text = "استعراض كروت المخزن الحالية 🗃️",
                color = PureWhite,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                textAlign = TextAlign.Right
            )
        }

        // Horizontal selectors to toggle categories view (pill rounded)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(100, 200, 250, 300, 500).forEach { cat ->
                    val catColor = when (cat) {
                        100 -> Category100Cardboard
                        200 -> Category200Blue
                        250 -> Category250Purple
                        300 -> Category300Green
                        500 -> Category500Turmeric
                        else -> GoldPrimary
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(22.dp))
                            .background(
                                if (selectedViewCategory == cat) {
                                    catColor.copy(alpha = 0.15f)
                                } else {
                                    SurfaceDark
                                }
                            )
                            .clickable { selectedViewCategory = cat }
                            .border(
                                BorderStroke(
                                    width = 1.5.dp, 
                                    color = if (selectedViewCategory == cat) catColor else Color(0x1F9E9E9E)
                                ),
                                RoundedCornerShape(22.dp)
                            )
                            .testTag("selector_view_cat_$cat"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$cat ر.ي",
                            color = if (selectedViewCategory == cat) catColor else TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
        }

        // Active List showing available cards for current selection with Copy and Automatic Sell Logic
        if (filteredCards.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, if (isDark) Color(0xFF2D2D2D).copy(alpha = 0.5f) else Color(0x0A000000)),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "لا توجد أي كروت متوفرة حالياً لفئة $selectedViewCategory ر.ي في مخزنك.",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(filteredCards, key = { it.id }) { card ->
                val cardDisplayDetails = if (card.password.isNotEmpty()) {
                    "المستخدم: ${card.username} | السر: ${card.password}"
                } else {
                    card.code
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(
                        width = 1.dp, 
                        color = if (isDark) Color(0xFF2D2D2D) else Color(0x0A000000)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = if (isDark) 0.dp else 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            cardToConfirmSell = card
                            showConfirmSellDialog = true
                        }
                        .testTag("card_item_${card.id}")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Actions container on the left
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Deletion option wrapper to trigger our custom bottom sheet
                            IconButton(
                                onClick = {
                                    cardToDelete = card
                                    showDeleteBottomSheet = true
                                },
                                modifier = Modifier
                                    .size(36.dp)
                                    .testTag("delete_card_btn_${card.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Delete,
                                    contentDescription = "حذف الكرت",
                                    tint = StatusRed,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            // Subtitle or action guidance for copying
                            Row(
                                modifier = Modifier.padding(start = 2.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.ContentCopy,
                                    contentDescription = "نسخ وبيع",
                                    tint = BrandPrimaryRed,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "النسخ والبيع",
                                    color = BrandPrimaryRed,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Card credential on the right
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                  text = cardDisplayDetails,
                                  color = PureWhite,
                                  fontSize = 13.sp,
                                  fontWeight = FontWeight.Bold,
                                  textAlign = TextAlign.Right
                            )
                            Text(
                                  text = "مُعرّف الكرت بالمخزون: #${card.id} | فئة ${card.category} ر.ي",
                                  color = TextSecondary,
                                  fontSize = 10.sp,
                                  textAlign = TextAlign.Right
                            )
                        }
                    }
                }
            }
        }

        // --- 📊 جدول عرض حالة الكروت الشامل 📊 ---
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = BorderStroke(1.dp, if (isDark) Color(0xFF2D2D2D) else Color(0x0A000000)),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = if (isDark) 0.dp else 4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    // Header title
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "إجمالي المطابقات: ${tableFilteredCards.size}",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Analytics,
                                contentDescription = "مراقبة المخزون",
                                tint = if (isDark) GlowOrangeGold else Color(0xFFE65100),
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "جدول مراقبة حالة الكروت الشامل 📊",
                                color = PureWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Right
                            )
                        }
                    }

                    // 1. Search input for the table
                    OutlinedTextField(
                        value = tableSearchQuery,
                        onValueChange = { tableSearchQuery = it },
                        label = { Text("ابحث برقم الكرت، المستخدم، أو الكود...") },
                        placeholder = { Text("مثال: 777123...") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Right, color = PureWhite),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = PureWhite,
                            unfocusedTextColor = PureWhite,
                            focusedBorderColor = if (isDark) GlowOrangeGold else Color(0xFFE65100),
                            unfocusedBorderColor = TextSecondary.copy(alpha = 0.2f)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Search, contentDescription = "بحث", tint = TextSecondary)
                        }
                    )

                    // 2. Filters Row (Category & Status)
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Category Filters (Chips)
                        Text(
                            text = "تصفية الفئة المحددة:",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Right,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val categories = listOf("الكل", "100", "200", "250", "300", "500")
                            categories.forEach { cat ->
                                val isSelected = tableCategoryFilter == cat
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(30.dp)
                                        .clip(RoundedCornerShape(15.dp))
                                        .background(
                                            if (isSelected) (if (isDark) GlowOrangeGold.copy(alpha = 0.15f) else Color(0xFFE65100).copy(alpha = 0.12f))
                                            else SurfaceDark.copy(alpha = 0.4f)
                                        )
                                        .clickable { tableCategoryFilter = cat }
                                        .border(
                                            BorderStroke(
                                                width = 1.dp,
                                                color = if (isSelected) (if (isDark) GlowOrangeGold else Color(0xFFE65100)) else Color(0x1F9E9E9E)
                                            ),
                                            RoundedCornerShape(15.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = cat,
                                        color = if (isSelected) (if (isDark) GlowOrangeGold else Color(0xFFE65100)) else TextSecondary,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        // Status Filters (Chips)
                        Text(
                            text = "تصفية حالة الكرت:",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Right,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val statuses = listOf("الكل", "متاحة", "تم توزيعها", "منتهية")
                            statuses.forEach { status ->
                                val isSelected = tableStatusFilter == status
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(30.dp)
                                        .clip(RoundedCornerShape(15.dp))
                                        .background(
                                            if (isSelected) {
                                                when (status) {
                                                    "متاحة" -> StatusGreen.copy(alpha = 0.15f)
                                                    "الكل" -> (if (isDark) GlowOrangeGold.copy(alpha = 0.15f) else Color(0xFFE65100).copy(alpha = 0.12f))
                                                    "تم توزيعها" -> Color(0xFF1E88E5).copy(alpha = 0.15f)
                                                    else -> StatusRed.copy(alpha = 0.15f)
                                                }
                                            } else SurfaceDark.copy(alpha = 0.4f)
                                        )
                                        .clickable { tableStatusFilter = status }
                                        .border(
                                            BorderStroke(
                                                width = 1.dp,
                                                color = if (isSelected) {
                                                    when (status) {
                                                        "متاحة" -> StatusGreen
                                                        "الكل" -> (if (isDark) GlowOrangeGold else Color(0xFFE65100))
                                                        "تم توزيعها" -> Color(0xFF1E88E5)
                                                        else -> StatusRed
                                                    }
                                                } else Color(0x1F9E9E9E)
                                            ),
                                            RoundedCornerShape(15.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = status,
                                        color = if (isSelected) {
                                            when (status) {
                                                "متاحة" -> StatusGreen
                                                "الكل" -> (if (isDark) GlowOrangeGold else Color(0xFFE65100))
                                                "تم توزيعها" -> Color(0xFF1E88E5)
                                                else -> StatusRed
                                            }
                                        } else TextSecondary,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    // 3. Table Layout
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(BorderStroke(1.dp, if (isDark) Color(0xFF2D2D2D) else Color(0x0A000000)), RoundedCornerShape(12.dp))
                            .clip(RoundedCornerShape(12.dp))
                    ) {
                        // Table Header row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(if (isDark) Color(0xFF161616) else Color(0x0A000000))
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "الحالة", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.weight(1.5f), textAlign = TextAlign.Center)
                            Text(text = "الفئة", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                            Text(text = "كود / بيانات الكارت", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.weight(3f), textAlign = TextAlign.Right)
                            Text(text = "م", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.weight(0.5f), textAlign = TextAlign.Center)
                        }

                        // Table Rows
                        if (tableFilteredCards.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "لا توجد كروت مطابقة للفلاتر المحددة.", color = TextSecondary, fontSize = 11.sp)
                            }
                        } else {
                            tableFilteredCards.take(40).forEachIndexed { index, card ->
                                val cardDisplayDetails = if (card.password.isNotEmpty()) {
                                    "${card.username} | ${card.password}"
                                } else {
                                    card.code
                                }
                                val rowBg = if (index % 2 == 0) Color.Transparent else (if (isDark) Color(0xFF1C1C1C).copy(alpha = 0.5f) else Color(0x05000000))
                                
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(rowBg)
                                        .clickable {
                                            cardToConfirmSell = card
                                            showConfirmSellDialog = true
                                        }
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Status Badge Column (weight 1.5)
                                    Box(
                                        modifier = Modifier
                                            .weight(1.5f)
                                            .padding(end = 4.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        val badgeText = if (!card.used) {
                                            "متاحة 🟢"
                                        } else if (card.id % 2 == 0) {
                                            "تم توزيعها 🔵"
                                        } else {
                                            "منتهية ⚠️"
                                        }
                                        val badgeColor = if (!card.used) {
                                            StatusGreen
                                        } else if (card.id % 2 == 0) {
                                            Color(0xFF1E88E5)
                                        } else {
                                            StatusRed
                                        }
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(badgeColor.copy(alpha = 0.1f))
                                                .padding(horizontal = 6.dp, vertical = 3.dp)
                                        ) {
                                            Text(
                                                text = badgeText,
                                                color = badgeColor,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }

                                    // Category Column (weight 1)
                                    Text(
                                        text = "${card.category} ر.ي",
                                        color = PureWhite,
                                        fontSize = 11.sp,
                                        modifier = Modifier.weight(1f),
                                        textAlign = TextAlign.Center
                                    )

                                    // Card Details Column (weight 3)
                                    Text(
                                        text = cardDisplayDetails,
                                        color = PureWhite,
                                        fontSize = 11.sp,
                                        modifier = Modifier.weight(3f),
                                        textAlign = TextAlign.Right,
                                        maxLines = 1
                                    )

                                    // Index Column (weight 0.5)
                                    Text(
                                        text = "${index + 1}",
                                        color = TextSecondary,
                                        fontSize = 10.sp,
                                        modifier = Modifier.weight(0.5f),
                                        textAlign = TextAlign.Center
                                    )
                                }
                                if (index < tableFilteredCards.take(40).size - 1) {
                                    HorizontalDivider(color = if (isDark) Color(0xFF222222) else Color(0x05000000))
                                }
                            }
                            if (tableFilteredCards.size > 40) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(if (isDark) Color(0xFF161616) else Color(0x0A000000))
                                        .padding(8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "يتم عرض أول 40 كارت فقط من إجمالي ${tableFilteredCards.size}... استخدم الفلاتر والبحث للتحديد الدقيق.",
                                        color = TextSecondary,
                                        fontSize = 9.sp,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

    // --- 💸 نافذة تأكيد بيع ومشاركة كرت الشحن 💸 ---
    if (showConfirmSellDialog && cardToConfirmSell != null) {
        val card = cardToConfirmSell!!
        val cardDisplayDetails = if (card.password.isNotEmpty()) {
            "اسم المستخدم :\n${card.username}\nكلمة السر :\n${card.password}"
        } else {
            card.code
        }
        AlertDialog(
            onDismissRequest = { 
                showConfirmSellDialog = false
                cardToConfirmSell = null 
            },
            title = {
                Text(
                    text = "تأكيد بيع ومشاركة الكرت 💸",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
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
                        text = "هل تريد بيع هذا الكرت وتسجيله في التقارير ومشاركته؟",
                        color = PureWhite,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Right,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = "تفاصيل الكرت:\n$cardDisplayDetails",
                        color = if (isDark) GlowOrangeGold else Color(0xFFE65100),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Right,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SurfaceDark.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                            .padding(10.dp)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "تحديد محفظة العميل للمشاركة والفتح المباشر:",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Right,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val wallets = listOf("جيب", "جوالي", "ون كاش")
                        wallets.forEach { wallet ->
                            val isSelected = selectedShareWallet == wallet
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) GlowOrangeGold.copy(alpha = 0.2f) else SurfaceDark)
                                    .border(
                                        BorderStroke(1.dp, if (isSelected) GlowOrangeGold else Color(0x1F9E9E9E)),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { selectedShareWallet = wallet },
                                contentAlignment = Alignment.Center
                             ) {
                                Text(
                                    text = if (wallet == "جيب") "محفظة كاش" else wallet,
                                    color = if (isSelected) GlowOrangeGold else TextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = {
                            // Copy, Mark as used, Insert Manual Transaction
                            viewModel.markCardAsUsed(card.id)
                            viewModel.insertManualTransaction("عميل مباشر", card.category, cardDisplayDetails, "بيع يدوي نسخ ومشاركة")
                            
                            val shareMessage = "كود كرت الشحن فئة ${card.category} ر.ي هو:\n$cardDisplayDetails"
                            com.example.utils.SmsSender.launchWalletApp(context, selectedShareWallet, shareMessage)

                            showConfirmSellDialog = false
                            cardToConfirmSell = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = if (isDark) GlowEmeraldGreen else Color(0xFF2E7D32)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(44.dp)
                    ) {
                        Text("بيع ومشاركة عبر تطبيق $selectedShareWallet 🚀", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    Button(
                        onClick = {
                            // Copy to clipboard
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = android.content.ClipData.newPlainText("Card Details", cardDisplayDetails)
                            clipboard.setPrimaryClip(clip)

                            // Mark as used automatically in Room Database
                            viewModel.markCardAsUsed(card.id)
                            viewModel.insertManualTransaction("عميل مباشر", card.category, cardDisplayDetails, "بيع يدوي نسخ")

                            Toast.makeText(context, "تم نسخ الكرت وتسجيله كمباع بنجاح! 💸", Toast.LENGTH_SHORT).show()
                            
                            showConfirmSellDialog = false
                            cardToConfirmSell = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = if (isDark) GlowOrangeGold else Color(0xFFE65100)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(44.dp)
                    ) {
                        Text("بيع ونسخ الكود فقط 📋", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    OutlinedButton(
                        onClick = {
                            showConfirmSellDialog = false
                            cardToConfirmSell = null
                        },
                        border = BorderStroke(1.dp, TextSecondary.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(44.dp)
                    ) {
                        Text("تراجع / رجوع عادي ✖", fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                    }
                }
            },
            dismissButton = null,
            containerColor = SurfaceDark,
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.border(BorderStroke(1.5.dp, if (isDark) Color(0xFF2D2D2D) else Color(0x1F000000)), RoundedCornerShape(20.dp))
        )
    }

    // Custom Bottom Sheet Dialog Overlay
    if (showDeleteBottomSheet && cardToDelete != null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.6f))
                .clickable { showDeleteBottomSheet = false; cardToDelete = null },
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(SurfaceDark)
                    .clickable(enabled = false) {} // prevent click-through
                    .border(
                        BorderStroke(1.5.dp, if (isDark) Color(0xFF2D2D2D) else Color(0x1F000000)),
                        RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                    )
                    .padding(24.dp),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Sheet handle
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(4.dp)
                        .clip(CircleShape)
                        .background(TextSecondary.copy(alpha = 0.4f))
                        .align(Alignment.CenterHorizontally)
                )

                Text(
                    text = "تأكيد حذف الكارت ⚠️",
                    color = StatusRed,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Right,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "هل أنت متأكد من رغبتك في حذف هذا الكارت من المخزن؟ لا يمكن التراجع عن هذا الإجراء لاحقاً.",
                    color = PureWhite,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Right,
                    modifier = Modifier.fillMaxWidth()
                )

                // Card details preview inside sheet
                Card(
                    colors = CardDefaults.cardColors(containerColor = DeepBlack),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, TextSecondary.copy(alpha = 0.15f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = if (cardToDelete!!.password.isNotEmpty()) {
                                "المستخدم: ${cardToDelete!!.username} | السر: ${cardToDelete!!.password}"
                            } else {
                                cardToDelete!!.code
                            },
                            color = PureWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Right
                        )
                        Text(
                            text = "فئة ${cardToDelete!!.category} ر.ي | معرّف الكرت: #${cardToDelete!!.id}",
                            color = TextSecondary,
                            fontSize = 10.sp,
                            textAlign = TextAlign.Right
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Cancel Button
                    Button(
                        onClick = { showDeleteBottomSheet = false; cardToDelete = null },
                        border = BorderStroke(1.dp, if (isDark) Color(0xFF2D2D2D) else Color(0x1F000000)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = PureWhite),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f).height(46.dp)
                    ) {
                        Text("إلغاء", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    // Delete Confirm Button
                    Button(
                        onClick = {
                            viewModel.deleteCard(cardToDelete!!.id)
                            Toast.makeText(context, "تم حذف الكارت بنجاح 🗑️", Toast.LENGTH_SHORT).show()
                            showDeleteBottomSheet = false
                            cardToDelete = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = StatusRed, contentColor = Color.White),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1.2f)
                            .height(46.dp)
                            .testTag("confirm_delete_btn")
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Outlined.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                            Text("تأكيد الحذف", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}

