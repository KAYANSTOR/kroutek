package com.example.core.domain

import com.example.core.model.Resource

/**
 * Domain Validators
 * دوال نقية (Pure Functions) للتحقق من صحة المدخلات قبل وصولها للـ UseCases.
 */

object Validators {

    /**
     * تحقق من رقم الهاتف (يمني: يبدأ بـ 7 ومكون من 9 أرقام)
     */
    fun validateYemeniPhoneNumber(phone: String): Resource<String> {
        val cleanPhone = phone.replace("\\s+".toRegex(), "")
        if (cleanPhone.isEmpty()) return Resource.Error(Exception("رقم الهاتف لا يمكن أن يكون فارغاً"))
        
        // إذا كان يحتوي على مفتاح اليمن
        val localPhone = if (cleanPhone.startsWith("+967")) {
            cleanPhone.substring(4)
        } else if (cleanPhone.startsWith("00967")) {
            cleanPhone.substring(5)
        } else {
            cleanPhone
        }

        if (localPhone.length != 9) return Resource.Error(Exception("رقم الهاتف يجب أن يتكون من 9 أرقام"))
        if (!localPhone.startsWith("7")) return Resource.Error(Exception("رقم الهاتف اليمني يجب أن يبدأ بـ 7"))
        
        return Resource.Success(localPhone)
    }

    /**
     * تحقق من كود الكرت المدخل يدوياً
     */
    fun validateCardCode(code: String): Resource<String> {
        val cleanCode = code.trim()
        if (cleanCode.isEmpty()) return Resource.Error(Exception("كود الكرت لا يمكن أن يكون فارغاً"))
        if (cleanCode.length < 5) return Resource.Error(Exception("كود الكرت قصير جداً"))
        return Resource.Success(cleanCode)
    }

    /**
     * تحقق من صحة السيريال المدخل من المستخدم (للترخيص)
     */
    fun validateLicenseFormat(serial: String): Resource<String> {
        val cleanSerial = serial.trim().uppercase()
        if (cleanSerial.isEmpty()) return Resource.Error(Exception("السيريال لا يمكن أن يكون فارغاً"))
        if (cleanSerial.length < 10) return Resource.Error(Exception("صيغة السيريال غير صحيحة"))
        return Resource.Success(cleanSerial)
    }
}
