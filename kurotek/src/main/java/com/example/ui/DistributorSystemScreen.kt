package com.example.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.PriceChange
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BrandBackground
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandSecondary
import com.example.ui.theme.BrandSurface
import com.example.ui.theme.BrandSurfaceVariant
import com.example.ui.theme.TextSecondary
import androidx.compose.ui.platform.testTag

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DistributorSystemScreen(
    viewModel: com.example.ui.DistributorViewModel,
    initialTab: Int = 0,
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val isLight = BrandBackground.luminance() > 0.5f
    val titleColor = if (isLight) Color(0xFF111111) else Color(0xFFFFFFFF)
    val customers by viewModel.distributorCustomers.collectAsState()
    val transactions by viewModel.distributorTransactions.collectAsState()
    val expenses by viewModel.distributorExpenses.collectAsState()
    val capitals by viewModel.distributorCapitals.collectAsState()
    var selectedTab by remember { mutableIntStateOf(initialTab) }
    var calcType by remember { mutableStateOf("REGULAR") }

    val tabs = listOf(
        Icons.Outlined.Calculate to "الحاسبة",
        Icons.Outlined.People to "العملاء",
        Icons.Outlined.AccountBalance to "المالية",
        Icons.Outlined.BarChart to "التقارير",
        Icons.Outlined.PriceChange to "التسعيرة",
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = {
                    Text(
                        text = "نظام الموزعين",
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
                                text = pair.second,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 12.sp
                            )
                        },
                        icon = {
                            Icon(
                                imageVector = pair.first,
                                contentDescription = null,
                                tint = if (selectedTab == index) BrandPrimary else TextSecondary
                            )
                        }
                    )
                }
            }

            when (selectedTab) {
                0 -> DistributorCalcTab(isLight = isLight, titleColor = titleColor, calcType = calcType, onCalcTypeChange = { calcType = it })
                1 -> DistributorCustomersTab(customers = customers, isLight = isLight, titleColor = titleColor)
                2 -> DistributorFinancialsTab(transactions = transactions, expenses = expenses, capitals = capitals, isLight = isLight, titleColor = titleColor)
                3 -> DistributorReportsTab(transactions = transactions, capitals = capitals, isLight = isLight, titleColor = titleColor)
                4 -> DistributorPricingTab(isLight = isLight, titleColor = titleColor)
                else -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("جاري التطوير...", color = TextSecondary)
                }
            }
        }
    }
}

@Composable
private fun DistributorCalcTab(isLight: Boolean, titleColor: Color, calcType: String, onCalcTypeChange: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CalcTypeChip(label = "عادي", selected = calcType == "REGULAR", isLight = isLight) { onCalcTypeChange("REGULAR") }
            CalcTypeChip(label = "بروفيشنال", selected = calcType == "PRO", isLight = isLight) { onCalcTypeChange("PRO") }
        }

        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = BrandSurface)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "إجمالي البيع", color = TextSecondary, fontSize = 12.sp)
                    Text(text = "0", color = titleColor, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "التكلفة", color = TextSecondary, fontSize = 12.sp)
                    Text(text = "0", color = titleColor, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "الربح", color = TextSecondary, fontSize = 12.sp)
                    Text(text = "0", color = BrandSecondary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = BrandSurfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                listOf(100, 200, 250, 300, 500).forEach { cat ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "فئة $cat", color = titleColor, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            IconButton(onClick = {}, modifier = Modifier.size(28.dp)) {
                                Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                            }
                            Text(text = "0", color = titleColor, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(28.dp), textAlign = TextAlign.Center)
                            IconButton(onClick = {}, modifier = Modifier.size(28.dp)) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = BrandPrimary, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedCard(
                onClick = {},
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.outlinedCardColors(containerColor = BrandSurface)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "إعادة تعيين", color = TextSecondary, fontSize = 12.sp)
                    Text(text = "مسح الكل", color = titleColor, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
            ElevatedCard(
                onClick = {},
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = BrandPrimary)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "حساب", color = Color(0xFF006B5F), fontSize = 12.sp)
                    Text(text = "طباعة الفاتورة", color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
private fun DistributorCustomersTab(
    customers: List<com.example.models.DistributorCustomer>,
    isLight: Boolean,
    titleColor: Color
) {
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
                Text(text = "العملاء", color = titleColor, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(text = "إدارة عملاء الموزعين", color = TextSecondary, fontSize = 12.sp)
            }
            ElevatedCard(
                onClick = {},
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = BrandSurfaceVariant)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = BrandPrimary, modifier = Modifier.size(18.dp))
                    Text(text = "إضافة عميل", color = titleColor, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = BrandSurface)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "الرصيد الحالي", color = TextSecondary, fontSize = 12.sp)
                    Text(text = "0", color = titleColor, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "عدد العملاء", color = TextSecondary, fontSize = 12.sp)
                    Text(text = customers.size.toString(), color = BrandPrimary, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedCard(
                    onClick = {},
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.outlinedCardColors(containerColor = BrandSurfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "إيداع", color = TextSecondary, fontSize = 11.sp)
                        Text(text = "0", color = Color(0xFF22C55E), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
                OutlinedCard(
                    onClick = {},
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.outlinedCardColors(containerColor = BrandSurfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "سحب", color = TextSecondary, fontSize = 11.sp)
                        Text(text = "0", color = Color(0xFFEF4444), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = BrandSurface)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = "العملاء", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text(text = "قائمة العملاء المسجلين", color = TextSecondary, fontSize = 11.sp)
                }
                Text(text = "0", color = BrandPrimary, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
            }
        }

        if (customers.isEmpty()) {
                        Text(
                            text = "لا يوجد عملاء",
                            color = TextSecondary,
                            fontSize = 14.sp
                        )
                    } else {
                        customers.take(5).forEach { customer ->
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
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                        Box(
                                            modifier = Modifier.size(42.dp).background(color = if (isLight) Color(0xFFF0FDFA) else Color(0xFF042F2E), shape = CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Outlined.People, contentDescription = null, tint = BrandPrimary, modifier = Modifier.size(20.dp))
                                        }
                                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Text(text = customer.name, color = titleColor, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                            Text(text = customer.phone, color = TextSecondary, fontSize = 12.sp)
                                        }
                                    }
                                    Text(text = "+0", color = Color(0xFF22C55E), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
private fun DistributorFinancialsTab(
    transactions: List<com.example.models.DistributorTransaction>,
    expenses: List<com.example.models.DistributorExpense>,
    capitals: List<com.example.models.DistributorCapital>,
    isLight: Boolean,
    titleColor: Color
) {
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
            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(text = "إيداع رأس المال", color = titleColor, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                OutlinedCard(onClick = {}, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), colors = CardDefaults.outlinedCardColors(containerColor = BrandSurfaceVariant)) {
                    Row(modifier = Modifier.fillMaxWidth().padding(14.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "رصيد حالي", color = TextSecondary, fontSize = 12.sp)
                        Text(text = capitals.sumOf { it.amount }.toString(), color = titleColor, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
                OutlinedCard(onClick = {}, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), colors = CardDefaults.outlinedCardColors(containerColor = BrandSurfaceVariant)) {
                    Row(modifier = Modifier.fillMaxWidth().padding(14.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        OutlinedCard(onClick = {}, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), colors = CardDefaults.outlinedCardColors(containerColor = BrandSurfaceVariant)) {
                            Text("إيداع جديد", color = titleColor, fontSize = 12.sp, modifier = Modifier.padding(10.dp), textAlign = TextAlign.Center)
                        }
                        OutlinedCard(onClick = {}, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), colors = CardDefaults.outlinedCardColors(containerColor = BrandSurfaceVariant)) {
                            Text("سحب", color = titleColor, fontSize = 12.sp, modifier = Modifier.padding(10.dp), textAlign = TextAlign.Center)
                        }
                    }
                }
            }
        }

        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = BrandSurfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(text = "المصروفات", color = titleColor, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedCard(onClick = {}, modifier = Modifier.weight(1f), shape = RoundedCornerShape(14.dp), colors = CardDefaults.outlinedCardColors(containerColor = BrandSurface)) {
                        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("إضافة مصروف", color = titleColor, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Text("+0", color = Color(0xFF22C55E), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    OutlinedCard(onClick = {}, modifier = Modifier.weight(1f), shape = RoundedCornerShape(14.dp), colors = CardDefaults.outlinedCardColors(containerColor = BrandSurface)) {
                        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("إجمالي المصروفات", color = titleColor, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Text(text = expenses.size.toString(), color = Color(0xFFEF4444), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
private fun DistributorReportsTab(
    transactions: List<com.example.models.DistributorTransaction>,
    capitals: List<com.example.models.DistributorCapital>,
    isLight: Boolean,
    titleColor: Color
) {
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
                Text(text = "ملخص شهري", color = titleColor, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedCard(onClick = {}, modifier = Modifier.weight(1f), shape = RoundedCornerShape(14.dp), colors = CardDefaults.outlinedCardColors(containerColor = BrandSurfaceVariant)) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("الأرباح", color = TextSecondary, fontSize = 11.sp)
                            Text(text = (transactions.sumOf { it.amount } - expenses.sumOf { it.amount }).toString(), color = Color(0xFF22C55E), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    OutlinedCard(onClick = {}, modifier = Modifier.weight(1f), shape = RoundedCornerShape(14.dp), colors = CardDefaults.outlinedCardColors(containerColor = BrandSurfaceVariant)) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("المبيعات", color = TextSecondary, fontSize = 11.sp)
Text(text = capitals.sumOf { it.amount }.toString(), color = titleColor, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
private fun DistributorPricingTab(isLight: Boolean, titleColor: Color) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(text = "إعدادات التسعيرة", color = titleColor, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        listOf(100, 200, 250, 300, 500).forEach { cat ->
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = BrandSurface)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "فئة $cat", color = titleColor, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    OutlinedCard(onClick = {}, shape = RoundedCornerShape(14.dp), colors = CardDefaults.outlinedCardColors(containerColor = BrandSurfaceVariant)) {
                        Text("تعديل", color = BrandPrimary, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp))
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
private fun CalcTypeChip(label: String, selected: Boolean, isLight: Boolean, onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            color = if (selected) BrandPrimary else TextSecondary,
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}
