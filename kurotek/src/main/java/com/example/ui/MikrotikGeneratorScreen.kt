package com.example.ui

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Router
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BrandBackground
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandSecondary
import com.example.ui.theme.BrandSurface
import com.example.ui.theme.BrandSurfaceVariant
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.isDarkThemeState
import androidx.compose.ui.platform.testTag

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MikrotikGeneratorScreen(
    viewModel: MikrotikViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val isLight = BrandBackground.luminance() > 0.5f
    val titleColor = if (isLight) Color(0xFF111111) else Color(0xFFFFFFFF)
    val generatedCards by viewModel.allGeneratedCards.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }

    val tabs = listOf(
        "توليد" to Icons.Outlined.Router,
        "الكروت" to Icons.Default.Check,
        "الإعدادات" to Icons.Outlined.Router
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = {
                    Text(
                        text = "مولد كروت المايكروتك",
                        color = titleColor,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Right
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = titleColor
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BrandBackground,
                    titleContentColor = titleColor
                )
            )

            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = BrandBackground,
                contentColor = BrandPrimary,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = BrandPrimary
                    )
                }
            ) {
                tabs.forEachIndexed { index, pair ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = pair.first,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 12.sp
                            )
                        },
                        icon = {
                            Icon(
                                imageVector = pair.second,
                                contentDescription = null,
                                tint = if (selectedTab == index) BrandPrimary else TextSecondary
                            )
                        }
                    )
                }
            }

            when (selectedTab) {
                0 -> MikrotikGeneratorTab(isLight = isLight, titleColor = titleColor, onBack = onBack)
                1 -> MikrotikCardsTab(isLight = isLight, titleColor = titleColor)
                2 -> MikrotikSettingsTab(isLight = isLight, titleColor = titleColor)
            }
        }
    }
}

@Composable
private fun MikrotikGeneratorTab(isLight: Boolean, titleColor: Color, onBack: () -> Unit) {
    var category by remember { mutableIntStateOf(100) }
    var quantity by remember { mutableStateOf("10") }
    var prefix by remember { mutableStateOf("DAHSHA_") }
    var codeLength by remember { mutableStateOf("6") }
    var formatMode by remember { mutableStateOf("user_pass") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = BrandSurface)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(text = "إعدادات التوليد", color = titleColor, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                OutlinedCard(onClick = {}, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), colors = CardDefaults.outlinedCardColors(containerColor = BrandSurfaceVariant)) {
                    Row(modifier = Modifier.fillMaxWidth().padding(14.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("فئة الكرت", color = titleColor, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            Text("اختر الفئة", color = TextSecondary, fontSize = 12.sp)
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            CategoryBtn("100", category == 100) { category = 100 }
                            CategoryBtn("500", category == 500) { category = 500 }
                        }
                    }
                }

                OutlinedCard(onClick = {}, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), colors = CardDefaults.outlinedCardColors(containerColor = BrandSurfaceVariant)) {
                    Column(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(text = "الكمية", color = titleColor, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            IconButton(onClick = {}) {
                                Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(18.dp))
                            }
                            Text(value = quantity.toString(), color = titleColor, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            IconButton(onClick = {}) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = BrandPrimary, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }

                OutlinedCard(onClick = {}, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), colors = CardDefaults.outlinedCardColors(containerColor = BrandSurfaceVariant)) {
                    Column(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(text = "البادئة", color = titleColor, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            IconButton(onClick = {}) {
                                Icon(Icons.Default.Edit, contentDescription = null, tint = BrandPrimary, modifier = Modifier.size(18.dp))
                            }
                            Text(text = prefix, color = titleColor, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedCard(onClick = {}, modifier = Modifier.weight(1f), shape = RoundedCornerShape(14.dp), colors = CardDefaults.outlinedCardColors(containerColor = BrandSurfaceVariant)) {
                        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("طول الكود", color = TextSecondary, fontSize = 11.sp)
                            Text("6", color = titleColor, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    OutlinedCard(onClick = {}, modifier = Modifier.weight(1f), shape = RoundedCornerShape(14.dp), colors = CardDefaults.outlinedCardColors(containerColor = BrandSurfaceVariant)) {
                        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("الصيغة", color = TextSecondary, fontSize = 11.sp)
                            Text("user+pass", color = titleColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                ElevatedCard(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = BrandPrimary)
                ) {
                    Column(modifier = Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(text = "توليد الكروت الآن", color = Color.Black, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Text(text = "جاري الاتصال بالمايكروتك", color = Color(0xFF006B5F), fontSize = 12.sp)
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
private fun MikrotikCardsTab(isLight: Boolean, titleColor: Color) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.End) {
                Text(text = "الكروت المتاحة", color = titleColor, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(text = "${generatedCards.size} كرت", color = TextSecondary, fontSize = 12.sp)
            }
            ElevatedCard(
                onClick = {},
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = BrandSurfaceVariant)
            ) {
                Text("تحديث", color = titleColor, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp))
            }
        }

        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = BrandSurface)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "الكروت", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text(text = generatedCards.size.toString(), color = BrandPrimary, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
            }
        }

        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = BrandSurfaceVariant)
        ) {
if (generatedCards.isEmpty()) {
                        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(text = "لا توجد كروت حالياً", color = TextSecondary, fontSize = 14.sp)
                            Text(text = "قم بتوليد كروت جديدة من التبويب الأول", color = TextSecondary.copy(alpha = 0.7f), fontSize = 12.sp)
                        }
                    } else {
                        generatedCards.take(5).forEach { card ->
                            ElevatedCard(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.elevatedCardColors(containerColor = BrandSurface)
                            ) {
                                Row(modifier = Modifier.fillMaxWidth().padding(14.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Column {
                                        Text(text = card.pin, color = titleColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        Text(text = card.username, color = TextSecondary, fontSize = 11.sp)
                                    }
                                    OutlinedCard(shape = RoundedCornerShape(12.dp), colors = CardDefaults.outlinedCardColors(containerColor = BrandSurfaceVariant)) {
                                        Text(text = "فئة ${card.category}", color = titleColor, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp))
                                    }
                                }
                            }
                        }
                    }
        }
        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
private fun MikrotikSettingsTab(isLight: Boolean, titleColor: Color) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = BrandSurface)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(text = "إعداد الاتصال", color = titleColor, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                listOf("عنوان IP", "192.168.88.1", "اسم المستخدم", "admin").forEach { label ->
                    OutlinedCard(onClick = {}, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.outlinedCardColors(containerColor = BrandSurfaceVariant)) {
                        Row(modifier = Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(text = label, color = titleColor, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            Text(text = if (label == "عنوان IP") "192.168.88.1" else if (label == "اسم المستخدم") "admin" else "••••••", color = TextSecondary, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        ElevatedCard(
            onClick = {},
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = BrandPrimary)
        ) {
            Column(modifier = Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(text = "حفظ الإعدادات", color = Color.Black, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Text(text = "تطبيق التغييرات", color = Color(0xFF006B5F), fontSize = 12.sp)
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
private fun CategoryBtn(label: String, selected: Boolean, onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp)
    ) {
        Text(text = label, color = if (selected) Color.Black else TextSecondary, fontSize = 12.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
    }
}
