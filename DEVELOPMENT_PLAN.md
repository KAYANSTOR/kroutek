# خطة التطوير (Development Plan)

## 🎯 الهدف من المشروع
استكمال وتطوير تطبيق الأندرويد الحالي (نظام نقاط البيع، المحافظ، وإدارة الكروت والرسائل). التطبيق مبني باستخدام تقنيات **Android Native (Jetpack Compose)** وقاعدة بيانات محليّة **Room Database**. الهدف هو تعزيز وظائف التطبيق دون تغيير بنيته الأساسية أو تحويله إلى تطبيق ويب.

---

## ✅ ما تم إنجازه حتى الآن (Current Progress)

### 1. تحديث قاعدة البيانات (Room Database)
بدلاً من إنشاء قاعدة بيانات جديدة، قمنا بإضافة الجداول (الكيانات) الناقصة إلى قاعدة البيانات الحالية `AppDatabase` مع رفع الإصدار من 9 إلى 10 (`MIGRATION_9_10`).
- **الكيانات (Models) التي تمت إضافتها:**
  - `MessageTemplate`: لحفظ قوالب الرسائل الجاهزة.
  - `MessageLog`: لحفظ سجل الرسائل (المقروءة، المرفوضة، إلخ).
  - `PointOfSale`: لإدارة نقاط البيع ومعلوماتها.
  - `Wallet`: لإدارة المحافظ وربطها.
- **واجهات الوصول للبيانات (DAOs):**
  - تم إنشاء `MessageTemplateDao`, `MessageLogDao`, `PointOfSaleDao`, `WalletDao` لتسهيل عمليات (الإضافة، الحذف، التعديل، الاستعلام).
- **تحديث `AppDatabase`:**
  - إضافة الكيانات الجديدة إلى قائمة الـ Entities.
  - تضمين الـ DAOs الجديدة.
  - كتابة كود التحديث (Migration) لضمان عدم فقدان البيانات السابقة للمستخدمين.

### 2. تحديثات واجهة المستخدم (UI Updates)
- **شاشة الرئيسية (`HomeScreen`):** تمت إضافة أيقونة ومسار "مركز المساعدة" (Help Center) إلى قائمة الخدمات.
- **شاشة قوالب الرسائل (`MessageTemplatesScreen`):**
  - تم تصميم وبرمجة نافذة سفلية (BottomSheet) لـ "إضافة قالب رسالة جديد" (`AddMessageTemplateBottomSheet`).
  - تم ربط زر الإضافة في الشاشة ليقوم بفتح هذه النافذة.

### 3. ربط واجهات المستخدم بقاعدة البيانات (ViewModels & DB Integration)
- **قوالب الرسائل:**
  - تم إنشاء `MessageTemplateViewModel` للربط بـ `MessageTemplateDao`.
  - تم استبدال البيانات الوهمية في `MessageTemplatesScreen` بالبيانات الحقيقية وجعلها تُحدّث تلقائياً (Reactive).
  - تم تفعيل زر "حفظ القالب" في `AddMessageTemplateBottomSheet`.
- **سجل الرسائل:**
  - تم إنشاء `MessageLogViewModel` وربط `MessageLogsScreen` بقاعدة البيانات لعرض الرسائل المقروءة والمرفوضة (مع دالة لإدخال بيانات وهمية مؤقتاً للتجربة).
- **إدارة المحافظ ونقاط البيع:**
  - تم إنشاء `PosWalletViewModel`.
  - تم تحديث `AddPosScreen` و `AddWalletScreen` ليقوما بحفظ البيانات الجديدة.
  - تم تحديث `WalletAndPosManagementScreen` لعرض إحصائيات وقوائم المحافظ ونقاط البيع الفعلية من القاعدة.

### 4. تحسين وظائف العمليات (Business Logic & Validation)
- تم إضافة قواعد التحقق (Validation) عند إدخال البيانات؛ تم تعطيل زر الحفظ (Save button disabled) حتى يتم ملء الحقول المطلوبة بالكامل في كل من:
  - إضافة نقطة بيع.
  - إضافة محفظة.
  - إنشاء قالب رسالة.

---

## 🚀 الخطوات القادمة (Next Steps)

بما أنك أشرت إلى أنك ستقوم باختبار التطبيق الفعلي على الأندرويد لاحقاً، فهذه هي المقترحات للمرحلة التالية بعد نجاح الاختبار:

### المرحلة الأولى: شاشة التقارير والمبيعات (تم الإنجاز ✅)
1. **تقرير المبيعات المكتمل (تم الإنجاز ✅):**
   - ربط شاشة `SalesReportScreen` مع قاعدة البيانات (جدول `transactions` والمحافظ).
   - توفير فلترة للمبيعات حسب (اليوم، الأسبوع، الشهر) وعرض الرسوم البيانية.
2. **شاشة سجل العمليات (تم الإنجاز ✅):**
   - ربط `OperationsLogScreen` لعرض العمليات المالية الدقيقة (سحب، إيداع، تحويل).

### المرحلة الثانية: إدارة الكروت والربط الشبكي (Cards Management)
1. **استيراد وتصدير الكروت:**
   - إنهاء برمجة النافذة السفلية `ImportCardsBottomSheet` لإضافة كروت الميكروتك إلى `CardDao`.
   - إدارة فئات الكروت وتفعيلها.

### المرحلة الثالثة: إعدادات التطبيق وتفعيل التراخيص (Settings & Licensing)
1. **حفظ الإعدادات:**
   - استخدام `DataStore` (أو Room) لحفظ إعدادات المستخدم كالمظهر واللغة وتفضيلات الرسائل.
2. **نظام التراخيص (تم الإنجاز ✅):**
   - ربط واجهة تفعيل التراخيص `LicenseActivationScreen` و `LicenseRenewalScreen` مع منطق التحقق من الترخيص المحلي.
   - تم بناء واجهات التفعيل الحقيقية (AppActivationScreen, LicenseRenewalScreen) وربطها مع AuthViewModel.\n   - تم تحديث LicenseEngine ليقوم بالاتصال الفعلي بخادم التراخيص (API) عبر ValidateSerialRequestDto بدلاً من التحقق المحلي فقط.\n   - تم تجهيز LicenseActivationScreen لعرض الحالة المستوردة من السيرفر (نشط، منتهي، تجريبي).


### Phase 4: UI Refinement
1. **Redesigned Cards Management:** Updated to match Light Theme references.
2. **Redesigned Categories Management:** Updated to match Light Theme references.
3. **Sim Settings Screen:** Created detailed screen based on provided UI reference for managing SMS dual SIMs.
