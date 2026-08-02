package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.*
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletAndPosManagementScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAddPos: () -> Unit,
    onNavigateToAddWallet: () -> Unit,
    viewModel: PosWalletViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    var showAddMenu by remember { mutableStateOf(false) }
    val pointsOfSale by viewModel.pointsOfSale.collectAsState()
    val wallets by viewModel.wallets.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("إدارة المحافظ ونقاط البيع", color = PureWhite, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "رجوع", tint = PureWhite)
                    }
                },
                actions = {
                    Box {
                        IconButton(onClick = { showAddMenu = true }) {
                            Icon(Icons.Outlined.Add, contentDescription = "إضافة", tint = TealPrimary)
                        }
                        DropdownMenu(
                            expanded = showAddMenu,
                            onDismissRequest = { showAddMenu = false },
                            modifier = Modifier.background(ZNetSurfaceDark)
                        ) {
                            DropdownMenuItem(
                                text = { Text("إضافة نقطة بيع", color = PureWhite) },
                                onClick = {
                                    showAddMenu = false
                                    onNavigateToAddPos()
                                },
                                leadingIcon = {
                                    Icon(Icons.Outlined.Storefront, contentDescription = null, tint = TealPrimary)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("إضافة محفظة", color = PureWhite) },
                                onClick = {
                                    showAddMenu = false
                                    onNavigateToAddWallet()
                                },
                                leadingIcon = {
                                    Icon(Icons.Outlined.AccountBalanceWallet, contentDescription = null, tint = TealPrimary)
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ZNetBackgroundDark)
            )
        },
        containerColor = ZNetBackgroundDark
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            
            // Stats Row
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = ZNetSurfaceDark),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Outlined.Storefront, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("نقاط البيع", color = TextSecondary, fontSize = 12.sp)
                        Text(pointsOfSale.size.toString(), color = PureWhite, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    }
                }
                
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = ZNetSurfaceDark),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Outlined.AccountBalanceWallet, contentDescription = null, tint = StatusGreen, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("المحافظ", color = TextSecondary, fontSize = 12.sp)
                        Text(wallets.size.toString(), color = PureWhite, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Text("قائمة المحافظ ونقاط البيع", color = PureWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp))
            
            val posList = pointsOfSale.map { POSData(it.name, if(it.isActive) "نشط" else "غير نشط", "${it.balance} ر.ي", false) }
            val walletList = wallets.map { POSData(it.name, if(it.isActive) "نشط" else "غير نشط", "${it.balance} ر.ي", true) }
            val combinedList = posList + walletList

            LazyVerticalGrid(
                columns = GridCells.Fixed(1),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(combinedList) { pos ->
                    POSCard(pos)
                }
            }
        }
    }
}

data class POSData(val name: String, val status: String, val balance: String, val isWallet: Boolean)

@Composable
fun POSCard(pos: POSData) {
    val isActive = pos.status == "نشط"
    Card(
        colors = CardDefaults.cardColors(containerColor = ZNetSurfaceDark),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isActive) TealPrimary.copy(alpha = 0.15f) else TextSecondary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (pos.isWallet) Icons.Outlined.AccountBalanceWallet else Icons.Outlined.Storefront,
                    contentDescription = null,
                    tint = if (isActive) TealPrimary else TextSecondary
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(pos.name, color = PureWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(androidx.compose.foundation.shape.CircleShape)
                            .background(if (isActive) StatusGreen else StatusRed)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(pos.status, color = TextSecondary, fontSize = 12.sp)
                }
            }
            
            Column(horizontalAlignment = Alignment.Start) {
                Text("الرصيد", color = TextSecondary, fontSize = 12.sp)
                Text(
                    pos.balance,
                    color = PureWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}
