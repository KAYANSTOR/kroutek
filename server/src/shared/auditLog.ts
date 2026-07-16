import { prisma } from '../config/prisma';

/**
 * تسجيل حدث أمني/تدقيقي. بديل مباشر لدالة addLog القديمة في api_server.js،
 * لكن يكتب في جدول security_logs عبر Prisma بدل مصفوفة logs في الذاكرة
 * (التي كانت تُفقَد بالكامل مع كل إعادة تشغيل للسيرفر).
 *
 * ⚠️ الكتابة هنا "fire and forget" عمداً (لا await في نقاط الاستدعاء) حتى
 * لا يُبطئ التسجيل الاستجابة للمستخدم؛ أي خطأ في الكتابة يُطبع في الكونسول
 * فقط ولا يُفشل الطلب الأصلي.
 */
export function addLog(
  ip: string,
  endpoint: string,
  statusCode: number,
  message: string,
  payload: unknown = ''
): void {
  const timestamp = new Date().toISOString();
  console.log(`[LOG] [${timestamp}] [${ip}] [${endpoint}] [Status: ${statusCode}] - ${message}`);

  prisma.securityLog
    .create({
      data: {
        ipAddress: ip || 'unknown',
        endpoint,
        statusCode,
        message,
        requestPayload: typeof payload === 'object' ? JSON.stringify(payload) : String(payload ?? ''),
      },
    })
    .catch((err: unknown) => {
      console.error('⚠️ فشل كتابة سجل التدقيق في قاعدة البيانات:', err);
    });
}
