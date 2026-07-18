package com.example.core.domain

/**
 * Business Rules & Policies
 * هذه الطبقة تحتوي على القوانين الثابتة للبزنس.
 */

object SalesPolicies {
    // المبالغ المسموح ببيعها ككروت شحن (Business Rule)
    val ALLOWED_SMS_AMOUNTS = listOf(100, 200, 250, 300, 500, 1000, 3000)

    // الحد الأدنى لتنبيه نقص المخزون
    const val LOW_STOCK_THRESHOLD = 10

    // المحافظ المعتمدة في النظام
    val SUPPORTED_WALLETS = listOf("جيب", "جوالي", "الكريمي", "حاسب", "ون كاش", "ام فلوس")
}

object LicensePolicies {
    const val TRIAL_DAYS = 5
    const val GRACE_PERIOD_HOURS = 24
}

object DistributorPolicies {
    const val MAX_DEBT_LIMIT_WARNING = 500_000.0 // تحذير إذا تجاوز الموزع نصف مليون مديونية
}
