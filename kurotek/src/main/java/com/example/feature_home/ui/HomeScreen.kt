package com.example.feature_home.ui

import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.Store
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.feature_cards.ui.CardsTab
import com.example.feature_approvals.ui.PendingApprovalsTab
import com.example.feature_customers.ui.SpecialCustomersTab
import com.example.feature_reports.ui.ReportsTab
import com.example.ui.theme.BrandBackground
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandSecondary
import com.example.ui.theme.BrandSurface
import com.example.ui.theme.BrandSurfaceVariant
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.isDarkThemeState
import androidx.compose.ui.platform.testChannel
import kotlinx.coroutines.datetime.format
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    authViewModel: AuthViewModel,
    settingsViewModel: SettingsViewModel,
    smsViewModel: SmsViewModel,
    inventoryViewModel: InventoryViewModel,
    salesViewModel: SalesViewModel,
    onNavigateToCards: () -> Unit = {},
    onNavigateToAccounts: () -> Unit = {},
    onNavigateToOffers: () -> Unit = {},
    onNavigateToReports: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {}
) {
    val isLight = BrandBackground.luminance() > 0.5f
    val titleColor = if (isLight) Color(0xFF111111) else Color(0xFFFFFFFF)
    
    // Collect and format data from ViewModels
    val networkName by settingsViewModel.networkName.collectAsStateWithLifecycle()
    
    // Format expiration date from license status
    val subscriptionExpiry by authViewModel.licenseStatus
        .map { licenseStatus ->
            licenseStatus?.expiryDate?.let { timestamp ->
                val date = Date(timestamp)
                // Format as "YYYY/MM/DD" to match UI
                String.format(Locale.getDefault(), "%04d/%02d/%02d",
                    date.year + 1900, date.month + 1, date.date)
            } ?: "غير محدد"
        }
        .collectAsStateWithLifecycle()
    
    // Remaining messages - using SMS daily limit remaining as placeholder
    // TODO: Replace with actual SMS quota tracking when available
    val remainingMessages by smsViewModel.isAutoSendSmsEnabled
        .map { if (it) "غير محدود" else "100" } // Placeholder logic
        .collectAsStateWithLifecycle()
    
    // Today's sales
    val salesToday by salesViewModel.transactions
        .map { transactions ->
            val today = Date()
            val todayStart = Date(today.year + 1900, today.month, 1, 0, 0, 0) // Actually need proper today start
            // For now, simple placeholder - will improve with proper date filtering
            val todayTransactions = transactions.filter { 
                // This is a simplified check - in reality we'd compare dates properly
                it.date.after(getStartOfDay(today)) && it.date.before(getEndOfDay(today))
            }
            val total = todayTransactions.sumOf { it.amount.toDouble() }
            val count = todayTransactions.size
            "${"%.0f".format(total)} ر.ي" to count
        }
        .collectAsStateWithLifecycle()
        .let { (amount, count) -> amount to count }
    
    // This month's sales
    val salesMonth by salesViewModel.transactions
        .map { transactions ->
            val now = Date()
            val monthTransactions = transactions.filter { 
                it.date.month + 1 == now.month + 1 && 
                it.date.year + 1900 == now.year + 1900
            }
            val total = monthTransactions.sumOf { it.amount.toDouble() }
            val count = monthTransactions.size
            "${"%.0f".format(total)} ر.ي" to count
        }
        .collectAsStateWithLifecycle()
        .let { (amount, count) -> amount to count }
    
    // Account count - using customer mappings as proxy for now
    // TODO: Replace with actual accounts/customers count when available
    val accountsCount by smsViewModel.allMappings
        .map { it.size }
        .collectAsStateWithLifecycle()
    
    // Active cards count (unused cards)
    val activeCardsCount by inventoryViewModel.totalUnusedCount.collectAsStateWithLifecycle()
    
    // Helper functions for date range filtering
    private fun getStartOfDay(date: Date): Date {
        return Date(date.year + 1900, date.month, date.date, 0, 0, 0)
    }
    
    private fun getEndOfDay(date: Date): Date {
        return Date(date.year + 1900, date.month, date.date, 23, 59, 59)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandBackground)
            .testTag("home_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // App Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "9:41",
                    color = titleColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = null,
                        tint = if (isLight) Color(0xFF1A9B8E) else titleColor,
                        modifier = Modifier.size(20.dp)
                    )
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = null,
                            tint = titleColor
                        )
                    }
                }
            }

            // Network header
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = networkName,
                    color = titleColor,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "الاشتراك حتى $subscriptionExpiry",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            }

            // Subscription + status row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Remaining messages card
                OutlinedCard(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.outlinedCardColors(containerColor = BrandSurface)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "الرسائل المتبقية",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "$remainingMessages",
                            color = titleColor,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // System status card
                OutlinedCard(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.outlinedCardColors(containerColor = BrandSurface)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(
                                        color = if (isLight) Color(0xFF10B981) else Color(0xFF34D399),
                                        shape = CircleShape
                                    )
                            )
                            Text(
                                text = "النظام يعمل بشكل سليم",
                                color = titleColor,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Text(
                            text = "نشط",
                            color = titleColor,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        LinearProgressIndicator(
                            progress = 0.72f,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp),
                            color = if (isLight) Color(0xFF03DAC5) else Color(0xFF14B8A6),
                            trackColor = if (isLight) Color(0xFFE5E7EB) else Color(0xFF27272A)
                        )
                    }
                }
            }

            // Sales cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Today's sales
                OutlinedCard(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.outlinedCardColors(containerColor = BrandSurface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "مبيعات اليوم",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "${salesToday.first}",
                                color = titleColor,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${salesToday.second} كرت",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                        Icon(
                            imageVector = Icons.Outlined.CalendarToday,
                            contentDescription = null,
                            tint = if (isLight) Color(0xFF03DAC5) else Color(0xFF14B8A6)
                        )
                    }
                }

                // Month's sales
                OutlinedCard(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.outlinedCardColors(containerColor = BrandSurface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "مبيعات الشهر",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "${salesMonth.first}",
                                color = titleColor,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${salesMonth.second} كرت",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                        Icon(
                            imageVector = Icons.Outlined.DateRange,
                            contentDescription = null,
                            tint = if (isLight) Color(0xFFBB86FC) else Color(0xFFC084FC)
                        )
                    }
                }
            }

            // Quick actions grid
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickActionCard(
                        title = "حسابات نقاط البيع",
                        value = "$accountsCount",
                        subtitle = "حساب",
                        icon = Icons.Outlined.Store,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToAccounts,
                        isLight = isLight
                    )
                    QuickActionCard(
                        title = "البيع المباشر",
                        value = "بدء",
                        subtitle = "عملية جديدة",
                        icon = Icons.Outlined.CalendarToday,
                        modifier = Modifier.weight(1f),
                        onClick = {},
                        isLight = isLight
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickActionCard(
                        title = "إدارة الملفات",
                        value = "0", // TODO: Replace with actual count
                        subtitle = "ملف",
                        icon = Icons.Outlined.DateRange,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToCards,
                        isLight = isLight
                    )
                    QuickActionCard(
                        title = "الأرقام المحظورة",
                        value = "0", // TODO: Replace with actual count
                        subtitle = "رقم",
                        icon = Icons.Outlined.Shield,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToOffers,
                        isLight = isLight
                    )
                }
            }

            // Recent operations
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(
                                    color = if (isLight) Color(0xFFEF4444) else Color(0xFFFF6B6B),
                                    shape = CircleShape
                                )
                        )
                        Text(
                            text = "آخر العمليات",
                            color = titleColor,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    TextButton(onClick = onNavigateToReports) {
                        Text(
                            text = "عرض الكل →",
                            color = if (isLight) Color(0xFF03DAC5) else Color(0xFF14B8A6),
                            fontSize = 13.sp
                        )
                    }
                }

                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = BrandSurface)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "لا توجد عمليات حديثة",
                            color = TextSecondary,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickActionCard(
    title: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.material.icons.Icons.Outlined?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    isLight: Boolean
) {
    OutlinedCard(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.outlinedCardColors(containerColor = BrandSurface),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(
                        color = if (isLight) Color(0xFFF5F5F5) else Color(0xFF1E1E21),
                        shape = RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon ?: Icons.Outlined.MoreVert,
                    contentDescription = null,
                    tint = if (isLight) Color(0xFF03DAC5) else Color(0xFF14B8A6),
                    modifier = Modifier.size(26.dp)
                )
            }
            Text(
                text = title,
                color = if (isLight) Color(0xFF444444) else TextSecondary,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                lineHeight = 14.sp
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = value,
                    color = if (isLight) Color(0xFF111111) else Color(0xFFFFFFFF),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }
        }
    }
}