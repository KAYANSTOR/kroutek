# هيكلية وتصميم محرك الشبكة (Network Architecture)

هذه الوثيقة هي المرجع الرسمي لطريقة بناء، تشغيل، ومعالجة الطلبات الخاصة بـ **Network Engine** في مشروع Kurotek. لا يُسمح بتعديل هذه السلوكيات الأساسية دون تحديث هذه الوثيقة أولاً.

## 1. دورة حياة الطلب (Request Lifecycle)
الطلب لا يخرج من التطبيق مباشرةً، بل يمر بالمراحل التالية:
`UseCase` ➔ `Repository` ➔ `NetworkClient (Interface)` ➔ `NetworkEngine (Implementation)` ➔ `Security/Metrics Hooks` ➔ `Retrofit/OkHttp` ➔ `API`

## 2. سياسة طابور الطلبات (Queue Policy)
الطلبات غير الحرجة (مثل المزامنة المتأخرة) أو الطلبات التي تفشل بسبب انقطاع الإنترنت (Offline) يتم إرسالها إلى `PendingRequestQueue`. 
يقوم المحرك لاحقاً بمحاولة إرسالها مجدداً (Retry) حال توفر اتصال بالشبكة، بناءً على توصيات `NetworkMonitor`.

## 3. دورة تحديث التوكن (Refresh Token Cycle)
عند استلام رد HTTP بـ 401 (Unauthorized):
1. يتم تعليق جميع الطلبات الأخرى مؤقتاً.
2. يتواصل المحرك مع `TokenProvider` لطلب `Refresh Token`.
3. يُعاد إرسال الطلب الأساسي المرفوض.
4. إذا فشل التحديث مرة أخرى، يتم تسجيل خروج المستخدم تلقائياً (Logout/Session Invalidation) وإرسال الحدث إلى الـ UI.

## 4. سياسة إعادة المحاولة (Retry Policy - Exponential Backoff)
لا نعتمد على محاولات متتالية سريعة ترهق الخادم. أي فشل بسبب الشبكة أو أخطاء الخادم (5xx) يعتمد سياسة التراجع الأسي:
* المحاولة 1: تأخير 1 ثانية
* المحاولة 2: تأخير 2 ثانية
* المحاولة 3: تأخير 4 ثواني
* المحاولة 4: تأخير 8 ثواني
* المحاولة 5: تأخير 16 ثانية (وهو الحد الأقصى للمحاولات قبل إرجاع الخطأ النهائي `ApiError.TimeoutError`).

## 5. معالجة الأخطاء (Error Mapping)
كل خطأ ناتج عن الاتصال (مثل `IOException` أو `SSLHandshakeException` أو `HttpException`) يمر على `ErrorMapper` مخصص داخل المحرك ليتم تحويله إلى نماذجنا الخاصة `ApiError`، مما يمنع تسرب أخطاء `Retrofit/OkHttp` لطبقة الـ Repository و UseCase.

## 6. المصادقة والتشفير (Authentication & Encryption)
* **المصادقة:** تتم حصراً عبر `AuthInterceptor` الذي يعتمد على `TokenProvider`. المحرك لا يتعامل إطلاقاً مع DataStore.
* **التشفير:** الـ Endpoints التي تتطلب تشفير تمر عبر `SecurityEngine` لتشفير البيانات الحساسة (Paylaod) قبل إرسالها.

## 7. المراقبة والسجلات (Metrics & Logging)
* يعتمد المحرك على `Logger Interface` لتصدير السجلات، مما يتيح تغيير الوجهة مستقبلاً من Logcat إلى ملفات محلية أو Crashlytics.
* يحتوي المحرك على `Metrics Hook` فارغ حالياً، سيتم استخدامه لاحقاً لقياس زمن الاستجابة، وحجم البيانات المستهلكة، ونسبة نجاح/فشل الطلبات.

## 8. مراقبة حالة الشبكة (Network Monitor)
مكون مستقل `NetworkMonitor` يعلم المحرك بحالة الاتصال (Online, Offline, Metered, WiFi). في حالة الـ Offline، يتم تعليق الطلبات وإيداعها في الـ Queue تلقائياً، تجنباً للأخطاء الوهمية والانهيارات (Crash).
