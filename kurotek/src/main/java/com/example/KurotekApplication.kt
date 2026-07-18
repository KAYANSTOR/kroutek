package com.example

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * نقطة انطلاق Hilt لكامل التطبيق. لا يوجد هنا أي منطق أعمال عمداً — فقط
 * تفعيل حاوية الحقن (Dependency Injection Container) التي يولّدها Hilt.
 *
 * هذه أول لبنة في مسار حقن التبعيات الكامل الموصوف في القسم 5 و6 من
 * docs/RESTRUCTURING_PLAN.md.
 */
@HiltAndroidApp
class KurotekApplication : Application()
