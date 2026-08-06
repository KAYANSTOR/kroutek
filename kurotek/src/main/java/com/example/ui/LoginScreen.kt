package com.example.ui

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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.material.icons.outlined.WifiTethering
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
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
fun LoginScreen(
    authViewModel: AuthViewModel,
    mainViewModel: MainViewModel,
    smsViewModel: SmsViewModel? = null,
    onLoginSuccess: () -> Unit = {}
) {
    val isLight = BrandBackground.luminance() > 0.5f
    val titleColor = if (isLight) Color(0xFF111111) else Color(0xFFFFFFFF)

    val networkName by (smsViewModel?.networkName?.collectAsStateWithLifecycle()
        ?: remember { androidx.compose.runtime.mutableStateOf("كروت الدحشة") })

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandBackground)
            .testTag("welcome_screen_container"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 460.dp)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .background(
                            color = if (isLight) Color(0xFFF0FDFA) else Color(0xFF042F2E),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Wifi,
                        contentDescription = null,
                        tint = BrandPrimary,
                        modifier = Modifier.size(44.dp)
                    )
                }

                Text(
                    text = "مرحباً بك",
                    color = titleColor,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "أدخل اسم شبكتك للبدء بالاستخدام",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )
            }

            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = BrandSurface)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedTextField(
                        value = networkName,
                        onValueChange = { newName ->
                            smsViewModel?.updateNetworkName(newName)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("welcome_network_name"),
                        singleLine = true,
                        label = { Text("اسم الشبكة") },
                        placeholder = { Text("مثال: شبكة كيان تك") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.WifiTethering,
                                contentDescription = null,
                                tint = BrandPrimary
                            )
                        },
                        shape = RoundedCornerShape(14.dp)
                    )

                    TextButton(
                        onClick = {
                            val trimmed = networkName.trim()
                            if (trimmed.isEmpty()) {
                                return@TextButton
                            }
                            authViewModel.setInitialLoginDone(true)
                            onLoginSuccess()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp),
                        shape = RoundedCornerShape(14.dp),
                        contentPadding = PaddingValues()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(BrandPrimary, RoundedCornerShape(14.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "متابعة",
                                color = Color.Black,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {},
                    modifier = Modifier.weight(1f).height(44.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("تجربة مجانية", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
                OutlinedButton(
                    onClick = {
                    },
                    modifier = Modifier.weight(1f).height(44.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("تفعيل بالمفتاح", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "النسخة التجريبية مجانية لمدة 7 أيام",
                color = TextSecondary,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}
