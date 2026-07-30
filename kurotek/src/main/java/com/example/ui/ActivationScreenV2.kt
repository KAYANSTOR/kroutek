@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivationScreenV2(
    activationViewModel: ActivationViewModel,
    onActivationSuccess: () -> Unit = {}
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val clipboardManager = LocalClipboardManager.current
    val uriHandler = LocalUriHandler.current
    
    // Collect all state from ViewModel
    val currentStep by activationViewModel.currentStep.collectAsState()
    val selectedVersion by activationViewModel.selectedVersion.collectAsState()
    val phoneNumber by activationViewModel.phoneNumber.collectAsState()
    val networkName by activationViewModel.networkName.collectAsState()
    val activationKey by activationViewModel.activationKey.collectAsState()
    val showPassword by activationViewModel.showPassword.collectAsState()
    val errorMessage by activationViewModel.errorMessage.collectAsState()
    val isLoading by activationViewModel.isLoading.collectAsState()
    
    // Colors from design
    val primaryTeal = Color(0xFF1A9B8E)
    val primaryPink = Color(0xFFE85E97)
    val surfaceLight = Color(0xFFF5F5F5)
    val textDark = Color(0xFF000000)
    val textGray = Color(0xFF666666)
    val textError = Color(0xFFFF5252)
    
    // Gradient for logo
    val gradientBrush = Brush.linearGradient(
        colors = listOf(primaryTeal, primaryPink),
        start = Offset(0f, 0f),
        end = Offset(200f, 200f)
    )
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(gradientBrush, shape = RoundedCornerShape(50.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Z",
                    color = Color.White,
                    fontSize = 56.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
            
            Text(
                text = "تفعيل تطبيق زدنت",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = textDark,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = "أدخل بياناتك لتفعيل الترخيص أو بدء النسخة التجريبية المجانية",
                fontSize = 13.sp,
                color = textGray,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )
            
            // Progress Indicator - 4 steps
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(4) { step ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(4.dp)
                            .background(
                                color = if (step <= currentStep) primaryTeal else surfaceLight,
                                shape = RoundedCornerShape(2.dp)
                            )
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Step Content
            when (currentStep) {
                0 -> StepOneVersionSelection(
                    selectedVersion = selectedVersion,
                    onVersionSelected = { activationViewModel.selectVersion(it) },
                    tealColor = primaryTeal
                )
                1 -> StepTwoPhoneNumber(
                    phoneNumber = phoneNumber,
                    onPhoneChange = { activationViewModel.updatePhone(it) },
                    tealColor = primaryTeal
                )
                2 -> StepThreeNetworkName(
                    networkName = networkName,
                    onNetworkChange = { activationViewModel.updateNetwork(it) },
                    tealColor = primaryTeal
                )
                3 -> StepFourActivationKey(
                    activationKey = activationKey,
                    showKeyField = !selectedVersion.equals("Free", ignoreCase = true),
                    onKeyChange = { activationViewModel.updateKey(it) },
                    showPassword = showPassword,
                    onPasswordToggle = { activationViewModel.togglePasswordVisibility() },
                    tealColor = primaryTeal
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Navigation Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (currentStep > 0) {
                    TextButton(
                        onClick = { activationViewModel.goToPreviousStep() },
                        enabled = !isLoading
                    ) {
                        Text(
                            text = "السابق",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isLoading) textGray else textDark
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }
                
                // Main action button (Next or Activate)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Button(
                            onClick = {
                                // Handle activation or proceed to next step
                                when (currentStep) {
                                    0 -> if (selectedVersion.isNotEmpty()) {
                                        activationViewModel.goToNextStep()
                                    }
                                    1 -> if (phoneNumber.isNotEmpty()) {
                                        activationViewModel.goToNextStep()
                                    }
                                    2 -> if (networkName.isNotEmpty()) {
                                        activationViewModel.goToNextStep()
                                    }
                                    3 -> {
                                        // Attempt activation
                                        activationViewModel.activate(
                                            onSuccess = {
                                                // Activation successful
                                                onActivationSuccess()
                                            },
                                            onError = { errorMsg ->
                                                // Error is already set in ViewModel via state
                                                // Just show it to user
                                            }
                                        )
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isLoading) 
                                    primaryTeal.copy(alpha = 0.5f) 
                                else 
                                    primaryTeal,
                                disabledContentColor = Color.White.copy(alpha = 0.5f),
                                disabledContainerColor = if (isLoading) 
                                    primaryTeal.copy(alpha = 0.5f) 
                                else 
                                    primaryTeal
                            )
                        ) {
                            Text(
                                text = when (currentStep) {
                                    0 -> "التالي"
                                    1 -> "التالي"
                                    2 -> "التالي"
                                    3 -> if (selectedVersion.equals("Free", ignoreCase = true)) 
                                        "تجربة مجانية" 
                                    else 
                                        "تفعيل"
                                },
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
            
            // Error message (if any)
            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = textError,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(top = 8.dp)
                        .fillMaxWidth()
                )
            }
            
            // Support Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable {
                        try {
                            uriHandler.openUri("https://wa.me/967773303455")
                        } catch (e: Exception) {
                            Toast.makeText(context, "فشل فتح الرابط", Toast.LENGTH_SHORT).show()
                        }
                    },
                colors = CardDefaults.cardColors(
                    containerColor = primaryTeal.copy(alpha = 0.15f)
                ),
                border = BorderStroke(1.dp, primaryTeal.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = null,
                        tint = primaryTeal,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "تواصل مع الدعم: 773303455",
                        color = primaryTeal,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// Reusable component for version selection (Step 1)
@Composable
private fun StepOneVersionSelection(
    selectedVersion: String,
    onVersionSelected: (String) -> Unit,
    tealColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "اختر نسخة التطبيق",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            
            listOf(
                "Free" to "30 يوم مجاني",
                "Yearly" to "سنة واحدة",
                "Professional" to "نسخة احترافية"
            ).forEach { (version, description) ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onVersionSelected(version) }
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedVersion.equals(version, ignoreCase = true)) 
                            tealColor.copy(alpha = 0.2f) 
                        else 
                            Color.White
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (selectedVersion.equals(version, ignoreCase = true)) 
                            tealColor 
                        else 
                            Color(0xFFE0E0E0E0E0)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = version,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Text(
                                text = description,
                                fontSize = 11.sp,
                                color = Color(0xFF666666)
                            )
                        }
                        if (selectedVersion.equals(version, ignoreCase = true)) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = null,
                                tint = tealColor,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// Reusable component for phone number input (Step 2)
@Composable
private fun StepTwoPhoneNumber(
    phoneNumber: String,
    onPhoneChange: (String) -> Unit,
    tealColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "رقم الهاتف",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = onPhoneChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("773303455") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = tealColor,
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                )
            )
        }
    }
}

// Reusable component for network name input (Step 3)
@Composable
private fun StepThreeNetworkName(
    networkName: String,
    onNetworkChange: (String) -> Unit,
    tealColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "اسم الشبكة",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            
            OutlinedTextField(
                value = networkName,
                onValueChange = onNetworkChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("كيان تك") },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = tealColor,
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                )
            )
        }
    }
}

// Reusable component for activation key input (Step 4)
@Composable
private fun StepFourActivationKey(
    activationKey: String,
    showKeyField: Boolean,
    onKeyChange: (String) -> Unit,
    showPassword: Boolean,
    onPasswordToggle: () -> Unit,
    tealColor: Color
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "اسم الشبكة",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        
        // Network name display (read-only)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = networkName,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            )
        }
        
        // Activation key input
        if (showKeyField) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "رمز التفعيل",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    
                    OutlinedTextField(
                        value = activationKey,
                        onValueChange = onKeyChange,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("XXXX-XXXX-XXXX") },
                        visualTransformation = if (showPassword) null else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done
                        ),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = tealColor,
                            unfocusedBorderColor = Color(0xFFE0E0E0),
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        )
                    )
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Spacer(modifier = Modifier.weight(1f))
                            IconButton(
                                onClick = onPasswordToggle,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = if (showPassword) 
                                        Icons.Outlined.Visibility 
                                    else 
                                        Icons.Outlined.VisibilityOff,
                                    contentDescription = if (showPassword) 
                                        "إخفاء كلمة المرور" 
                                    else 
                                        "إظهار كلمة المرور",
                                    tint = if (isLight) Color(0xFF666666) else Color(0xFFFFFFFF),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}