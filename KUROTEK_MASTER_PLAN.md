# 📋 خطة التطوير الرئيسية — مشروع كروتك
## المرجع الرسمي للمشروع | الإصدار 1.0

---

## 🔍 تشخيص الوضع الحالي (المشاكل)

### مشكلة 1: ملف واجهة أحادي ضخم
```
MainDashboardScreen.kt = 4,185 سطر / 219KB في ملف واحد ❌
```
هذا يخالف كل معايير هندسة البرمجيات.

### مشكلة 2: دمج المسؤوليات
```
CardRepository.kt يجمع:
- إعدادات التطبيق (SharedPreferences)
- منطق الأعمال
- الوصول للبيانات (DAO)
```
مخالفة لمبدأ Single Responsibility.

### مشكلة 3: لا يوجد حقن تبعيات (Dependency Injection)
```
CardRepository(val context: Context) ← context مباشر في Repository ❌
```

### مشكلة 4: ViewModel يحتوي على منطق أعمال
```
approvePendingApproval() في ViewModel = 70 سطر من منطق الأعمال ❌
```

### مشكلة 5: بنية Monolithic
```
كل شيء في package واحد: com.example ❌
لا يوجد feature separation
```

---

## 🏗️ البنية المعمارية الجديدة

### النمط: Clean Architecture + MVVM + Multi-Module

```
┌─────────────────────────────────────────┐
│           Presentation Layer            │
│    (Composable UI + ViewModel)          │
└─────────────┬───────────────────────────┘
              │
┌─────────────▼───────────────────────────┐
│             Domain Layer                │
│    (Use Cases + Domain Models)          │
└─────────────┬───────────────────────────┘
              │
┌─────────────▼───────────────────────────┐
│              Data Layer                 │
│  (Repository + Room DB + Network)       │
└─────────────────────────────────────────┘
```

---

## 📁 هيكل المشروع الجديد الكامل

```
remix_-كروتك/
│
├── 📱 kurotek/                          ← التطبيق الأول (العميل)
│   ├── build.gradle.kts
│   └── src/main/java/com/kurotek/
│       │
│       ├── 🏠 app/                      ← Module: Main App
│       │   ├── KurotekApplication.kt    ← Application class + Hilt
│       │   ├── MainActivity.kt          ← Entry point (دخول وحماية)
│       │   └── navigation/
│       │       └── AppNavGraph.kt       ← Navigation Compose
│       │
│       ├── 🔧 core/                     ← Module: Core (مشترك بين كل الميزات)
│       │   ├── database/
│       │   │   ├── AppDatabase.kt
│       │   │   ├── dao/
│       │   │   │   ├── CardDao.kt
│       │   │   │   ├── TransactionDao.kt
│       │   │   │   ├── DepositDao.kt
│       │   │   │   ├── PendingApprovalDao.kt
│       │   │   │   └── CustomerMappingDao.kt
│       │   │   └── entities/
│       │   │       ├── CardEntity.kt
│       │   │       ├── TransactionEntity.kt
│       │   │       ├── DepositEntity.kt
│       │   │       ├── PendingApprovalEntity.kt
│       │   │       └── CustomerMappingEntity.kt
│       │   ├── preferences/
│       │   │   └── AppPreferences.kt    ← DataStore (بديل SharedPrefs)
│       │   ├── di/
│       │   │   └── CoreModule.kt        ← Hilt Module
│       │   └── common/
│       │       ├── extensions/          ← Kotlin extensions
│       │       └── Result.kt            ← Sealed class للنتائج
│       │
│       ├── 🔐 feature-auth/             ← Module: التفعيل والترخيص
│       │   ├── data/
│       │   │   ├── LicenseRepository.kt
│       │   │   └── remote/
│       │   │       └── LicenseApiService.kt
│       │   ├── domain/
│       │   │   ├── models/
│       │   │   │   └── LicenseStatus.kt
│       │   │   └── usecases/
│       │   │       ├── ValidateSerialUseCase.kt
│       │   │       ├── StartTrialUseCase.kt
│       │   │       └── CheckLicenseStatusUseCase.kt
│       │   └── ui/
│       │       ├── ActivationScreen.kt  ← شاشة التفعيل
│       │       ├── ActivationViewModel.kt
│       │       └── LoginScreen.kt       ← شاشة PIN التطبيق
│       │
│       ├── 🏠 feature-home/             ← Module: الرئيسية
│       │   ├── domain/usecases/
│       │   │   └── GetDashboardSummaryUseCase.kt
│       │   └── ui/
│       │       ├── HomeScreen.kt
│       │       └── HomeViewModel.kt
│       │
│       ├── 💳 feature-cards/            ← Module: الكروت
│       │   ├── data/
│       │   │   └── CardRepository.kt    ← فقط للكروت
│       │   ├── domain/usecases/
│       │   │   ├── AddCardsBulkUseCase.kt
│       │   │   ├── SellCardUseCase.kt   ← منطق البيع
│       │   │   └── GetCardsInventoryUseCase.kt
│       │   └── ui/
│       │       ├── CardsScreen.kt
│       │       └── CardsViewModel.kt
│       │
│       ├── 📨 feature-sms/              ← Module: محرك SMS (الأهم)
│       │   ├── data/
│       │   │   └── SmsRepository.kt
│       │   ├── domain/
│       │   │   ├── models/
│       │   │   │   └── ParsedDeposit.kt
│       │   │   └── usecases/
│       │   │       ├── ParseSmsUseCase.kt
│       │   │       ├── SendCardCodeUseCase.kt
│       │   │       └── ProcessDepositUseCase.kt ← القلب
│       │   ├── engine/
│       │   │   ├── SmsParser.kt
│       │   │   ├── SmsSender.kt
│       │   │   └── GeminiAnalyzer.kt
│       │   └── receiver/
│       │       ├── SmsReceiver.kt
│       │       └── PendingApprovalReceiver.kt
│       │
│       ├── ⏳ feature-approvals/        ← Module: التفويضات المعلقة
│       │   ├── domain/usecases/
│       │   │   ├── ApprovePendingUseCase.kt
│       │   │   └── RejectPendingUseCase.kt
│       │   └── ui/
│       │       ├── ApprovalsScreen.kt
│       │       └── ApprovalsViewModel.kt
│       │
│       ├── 👥 feature-customers/        ← Module: العملاء
│       │   ├── data/
│       │   │   └── CustomerRepository.kt
│       │   ├── domain/usecases/
│       │   │   └── MapCustomerUseCase.kt
│       │   └── ui/
│       │       ├── CustomersScreen.kt
│       │       └── CustomersViewModel.kt
│       │
│       ├── 📊 feature-reports/          ← Module: التقارير
│       │   ├── domain/usecases/
│       │   │   ├── GetSalesReportUseCase.kt
│       │   │   └── ExportReportUseCase.kt
│       │   └── ui/
│       │       ├── ReportsScreen.kt
│       │       └── ReportsViewModel.kt
│       │
│       ├── ⚙️ feature-settings/         ← Module: الإعدادات
│       │   ├── domain/usecases/
│       │   │   └── UpdateSettingsUseCase.kt
│       │   └── ui/
│       │       ├── SettingsScreen.kt
│       │       └── SettingsViewModel.kt
│       │
│       ├── 🔄 feature-sync/             ← Module: المزامنة
│       │   ├── data/
│       │   │   └── SyncRepository.kt
│       │   ├── domain/usecases/
│       │   │   ├── UploadTransactionsUseCase.kt
│       │   │   └── CreateBackupUseCase.kt
│       │   └── workers/
│       │       ├── SyncWorker.kt        ← WorkManager
│       │       └── BackupWorker.kt
│       │
│       └── 🎨 design-system/            ← Module: نظام التصميم
│           ├── theme/
│           │   ├── Color.kt
│           │   ├── Typography.kt
│           │   └── Theme.kt
│           └── components/
│               ├── KurotekCard.kt
│               ├── KurotekButton.kt
│               └── KurotekDialog.kt
│
├── 🔑 app/                             ← التطبيق الثاني (مدير المشروع)
│   └── src/main/.../licensemanager/
│       └── MainActivity.kt             ← يبقى كما هو (يعمل جيداً)
│
└── 🌐 server/                          ← سيرفر التراخيص (يبقى)
    └── api_server.js
```

---

## 🔌 التقنيات المستخدمة

| التقنية | الدور | السبب |
|---------|-------|-------|
| **Kotlin** | لغة البرمجة | اللغة الرسمية لـ Android |
| **Jetpack Compose** | واجهة المستخدم | يُبقى (الكود الحالي) |
| **Hilt** | Dependency Injection | يُبقى الكود نظيفاً وقابل للاختبار |
| **Room** | قاعدة البيانات المحلية | يُبقى (معتمد حالياً) |
| **DataStore** | تخزين الإعدادات | بديل SharedPreferences |
| **WorkManager** | المزامنة الخلفية | Offline First مضمون |
| **Kotlin Coroutines + Flow** | البرمجة غير المتزامنة | يُبقى (معتمد حالياً) |
| **Retrofit** | API calls | للترخيص والمزامنة |
| **Navigation Compose** | التنقل بين الشاشات | بديل الـ tabs اليدوية |

---

## 🗓️ خطة المراحل

### المرحلة 1: إعادة الهيكلة (الأهم)
**المدة المقدرة:** 2-3 جلسات

#### الخطوات:
1. إنشاء بنية الـ packages الجديدة
2. نقل `AppDatabase.kt` + كل الـ DAOs إلى `core/database/`
3. تقسيم `MainDashboardScreen.kt` (4185 سطر) إلى 6 ملفات منفصلة:
   - `HomeScreen.kt`
   - `CardsScreen.kt`
   - `ApprovalsScreen.kt`
   - `CustomersScreen.kt`
   - `ReportsScreen.kt`
   - `SettingsScreen.kt`
4. تقسيم `CardRepository.kt` إلى:
   - `AppPreferences.kt` (الإعدادات فقط)
   - `CardRepository.kt` (الكروت فقط)
   - `TransactionRepository.kt`
   - `DepositRepository.kt`
5. إنشاء Domain Layer (Use Cases)
6. إضافة Hilt للـ DI

### المرحلة 2: التنظيف ✅ (مكتمل)
- ~~حذف `kayan_repo/`~~ ✅
- ~~حذف `temp_kayan_inspect/`~~ ✅

### المرحلة 3: إكمال الوظائف الموجودة
1. تحسين شاشة التفعيل (كما في الوثيقة):
   - اسم الشبكة
   - Device ID مع نسخ وإرسال
   - خانة السيريال
   - زر تجربة مجانية 7 أيام
   - زر "تواصل معنا"
2. التحقق من منطق التجربة المجانية (7 أيام)
3. ضمان عمل SMS Receiver بشكل مستقل عن حالة التطبيق
4. إضافة DataStore بدلاً من SharedPreferences
5. إضافة WorkManager للمزامنة

### المرحلة 4: الاختبار الداخلي
- اختبار الأداء
- اختبار الاستقرار
- اختبار الترخيص
- اختبار المزامنة
- اختبار النسخ الاحتياطية

### المرحلة 5: إصلاح الأخطاء
- بناءً على نتائج الاختبار

### المرحلة 6: الميزات الجديدة
- ← **تحدد بعد موافقتك**

### المرحلة 7: Google Play
- إعداد Signing Key
- كتابة وصف المتجر
- رفع APK / AAB

---

## 🚀 ما الخطوة الأولى؟

### نبدأ بتقسيم `MainDashboardScreen.kt`

هذا هو أكبر مشكلة وأهم إصلاح:

```kotlin
// الوضع الحالي ❌
MainDashboardScreen.kt → 4185 سطر

// الوضع الجديد ✅
feature-home/ui/HomeScreen.kt          ← ~400 سطر
feature-cards/ui/CardsScreen.kt        ← ~800 سطر
feature-approvals/ui/ApprovalsScreen.kt ← ~400 سطر
feature-customers/ui/CustomersScreen.kt ← ~400 سطر
feature-reports/ui/ReportsScreen.kt    ← ~400 سطر
feature-settings/ui/SettingsScreen.kt  ← ~600 سطر
app/navigation/AppNavGraph.kt          ← ~100 سطر
```

---

## ⚠️ قواعد العمل المتفق عليها

1. ✅ لا يتم حذف أي ميزة إلا بموافقتك
2. ✅ كل الميزات الحالية تُنقل كما هي (بدون تغيير الوظيفة)
3. ✅ Offline First أولاً دائماً
4. ✅ فصل UI عن Business Logic عن Data Layer
5. ✅ كل ملف = مسؤولية واحدة فقط
6. ✅ هذه الوثيقة هي المرجع الرسمي

---

**هل توافق على هذه الخطة؟ وهل تريد البدء بالمرحلة 1؟**
