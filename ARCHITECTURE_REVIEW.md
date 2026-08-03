# 📋 تقرير مراجعة معمارية Kurotek — المرحلة 1 & 2

## ✅ المرحلة 1: تنظيف المشروع — منتهية

### ViewModels الجديدة المضافة
| ViewModel | المسؤولية | يحل محل |
|---|---|---|
| `SettingsViewModel` | إعدادات التطبيق، المحافظ، القوالب، اسم الشبكة | `SmsViewModel` في الـ UI |
| `MikrotikViewModel` | توليد وإدارة كروت المايكروتك | `SmsViewModel` في الـ UI |
| `DashboardViewModel` | الموافقات المعلقة، الإحصائيات | `SmsViewModel` في الـ UI |
| `InventoryViewModel` | المخزون، إضافة/حذف الكروت | `SmsViewModel` في الـ UI |
| `SalesViewModel` | المعاملات، الإيداعات | `SmsViewModel` في الـ UI |
| `ReportsViewModel` | التقارير، العملاء الاستثنائيين | `SmsViewModel` في الـ UI |

### ملخص إزالة SmsViewModel من الـ UI
| ملف | الحالة |
|---|---|
| `CardsScreen.kt` | ✅ تم الترحيل إلى `SettingsViewModel` |
| `ApprovalsScreen.kt` | ✅ تم الترحيل إلى `DashboardViewModel` + `ReportsViewModel` |
| `CustomersScreen.kt` | ✅ تم الترحيل إلى `ReportsViewModel` + `SalesViewModel` |
| `ReportsScreen.kt` | ✅ تم الترحيل إلى `SalesViewModel` + `DashboardViewModel` |
| `SettingsScreen.kt` | ✅ تم الترحيل إلى `SettingsViewModel` |
| `LoginScreen.kt` | ✅ تم الترحيل إلى `SettingsViewModel` |
| `MainDashboardScreen.kt` | ✅ تم الترحيل إلى `SettingsViewModel` |
| `MikrotikGeneratorScreen.kt` | ✅ تم الترحيل إلى `MikrotikViewModel` |

> **ملاحظة:** `SmsViewModel` يبقى في `MainActivity` فقط كـ `@Suppress("UNUSED_VARIABLE")` لأن خدمة معالجة الرسائل (SMS Background Service) قد تحتاجه. لا يُمرَّر لأي شاشة.

---

## ✅ المرحلة 2: مراجعة المعمارية — نتائج التدقيق

### 1. ❌ Network Calls داخل Compose
**النتيجة: مطابقة ✅**
لا توجد أي استدعاءات شبكة مباشرة داخل أي Composable.

### 2. ❌ SQL Queries داخل الـ UI
**النتيجة: مطابقة ✅**
جميع عمليات قاعدة البيانات تمر عبر `Repository → ViewModel → UI (StateFlow)` فقط.

### 3. ❌ Business Rules داخل ViewModels
**النتيجة: مطابقة جزئية ⚠️**
- `SmsViewModel.approvePendingApproval` و `DashboardViewModel.approvePending` يحتويان منطق تسلسل العمليات.
- **التوصية:** استخراج `ApprovePendingUseCase` لاحقاً (مرحلة 5).

### 4. ❌ ViewModel تعتمد على ViewModel أخرى
**النتيجة: مطابقة ✅**
لا توجد أي ViewModel تستدعي ViewModel أخرى مباشرة. كل ViewModel تتعامل مع `Repository` فقط.

### 5. Repositories تستخدم Interfaces
**النتيجة: مطابقة ✅**
`CoreContainer` يعرّف جميع الـ Repositories عبر Interfaces موجودة في `DomainRepositories.kt`.

### 6. UseCases مستقلة
**النتيجة: مطابقة ✅**
كل UseCase يستقبل Repository فقط ولا يعتمد على ViewModels أو Services.

### 7. StateFlow موحّد
**النتيجة: مطابقة ✅**
جميع الـ StateFlows تستخدم `SharingStarted.WhileSubscribed(5000)` بشكل موحّد.

### 8. Circular Dependencies
**النتيجة: لا توجد ✅**
```
UI → ViewModel → Repository/UseCase → Engine/DB
```
اتجاه الاعتماد أحادي.

---

## 📋 المرحلة 3: خطة الاختبارات (Unit & Integration)

### ما يجب اختباره:
| الاختبار | النوع | الأولوية |
|---|---|---|
| تسجيل الدخول (صحيح/خاطئ) | Unit - `AuthViewModel` | 🔴 قصوى |
| بيع كرت تلقائي | Unit - `SellCardUseCase` | 🔴 قصوى |
| إضافة كروت بالجملة | Unit - `AddCardsUseCase` | 🟠 عالية |
| الموافقة على معاملة معلقة | Integration - `DashboardViewModel` | 🔴 قصوى |
| مزامنة يدوية | Unit - `SyncNowUseCase` | 🟠 عالية |
| العمل بدون إنترنت | Integration - `SyncRepository` | 🟡 متوسطة |
| استعادة الاتصال بعد قطعه | Integration - `NetworkEngine` | 🟡 متوسطة |
| انتهاء الترخيص | Unit - `ValidateLicenseUseCase` | 🔴 قصوى |
| تحديث التوكن | Unit - `AuthRepository` | 🔴 قصوى |
| حالات الخطأ (Network/DB) | Unit - كل Repository | 🟠 عالية |

---

## 🔜 المرحلة 4: WorkManager — الجاهز للتنفيذ

### المهام الخلفية المخططة:
```
WorkManager
├── BackgroundSyncWorker       ← SyncNowUseCase كل 15 دقيقة
├── RetryPendingWorker         ← UploadPendingOperationsUseCase عند الاتصال
├── LicenseCheckerWorker       ← ValidateLicenseUseCase كل 24 ساعة
├── TokenRefreshWorker         ← AuthRepository.refreshToken قبل انتهاء التوكن
└── CleanupWorker              ← حذف البيانات المؤقتة أسبوعياً
```

> [!IMPORTANT]
> يجب إضافة `androidx.work:work-runtime-ktx` إلى `build.gradle` قبل البدء.

---

## 🔜 المرحلة 5: تصدير PDF (مخطط)

### الخطة:
- استخدام `PdfDocument` (Android API) أو مكتبة `iText` للتقارير المعقدة.
- إضافة `exportToPdf()` في `ReportsViewModel` عبر UseCase جديد `ExportReportUseCase`.
- المخرجات: PDF مخزّن في `context.cacheDir` يُشارَك عبر `FileProvider`.
