package com.example.di

import android.content.Context
import com.example.database.CardRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * وحدة Hilt الأساسية للتطبيق. تُثبَّت في SingletonComponent، أي أن أي كائن
 * توفّره تبقى نسخة واحدة فقط طوال عمر التطبيق (Singleton حقيقي مضمون من
 * إطار العمل، وليس Singleton يدوي عرضة للأخطاء كما كان سابقاً).
 *
 * ملاحظة: provideCardRepository يعيد استخدام CardRepository.getInstance()
 * الموجودة أصلاً (بدل استدعاء المُنشئ مباشرة) حتى يبقى هناك مصدر واحد
 * للحقيقة سواء استُدعيت عبر Hilt أو عبر أي كود قديم لم يُهاجَر بعد
 * (مثل SmsReceiver و PendingApprovalReceiver اللذين لا يزالان يستخدمان
 * CardRepository.getInstance() مباشرة إلى حين ترحيلهما لـ Hilt في مرحلة لاحقة).
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideCardRepository(@ApplicationContext context: Context): CardRepository {
        return CardRepository.getInstance(context)
    }
}
