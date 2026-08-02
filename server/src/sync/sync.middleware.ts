import { Request, Response, NextFunction } from 'express';
import jwt from 'jsonwebtoken';
import { env } from '../config/env';
import { prisma } from '../config/prisma';

export interface SyncRequest extends Request {
  accountId?: string;
  deviceRowId?: string;
}

/**
 * مصادقة الجهاز لعمليات المزامنة — تعيد استخدام نفس التوكن الصادر أصلاً
 * من /api/v1/serial/validate (الذي يحمل {serial, deviceId})، بدل اختراع
 * نظام مصادقة جديد منفصل.
 *
 * ⚠️ نقطة أمان حاسمة: accountId يُشتَق دائماً من الترخيص/الجهاز المرتبط
 * بالتوكن نفسه بعد التحقق من قاعدة البيانات — لا يُقبل accountId من جسم
 * الطلب (body) أو من أي مصدر يتحكم به العميل مطلقاً. هذا يمنع أي حساب من
 * قراءة أو الكتابة في بيانات حساب آخر حتى لو تلاعب بالطلب.
 */
export async function deviceAuth(req: SyncRequest, res: Response, next: NextFunction): Promise<void> {
  const authHeader = req.headers.authorization;
  if (!authHeader || !authHeader.startsWith('Bearer ')) {
    res.status(401).json({ success: false, message: 'غير مصرح به: يلزم توكن تفعيل صالح لاستخدام المزامنة.' });
    return;
  }

  const token = authHeader.split(' ')[1];
  let payload: { serial?: string; deviceId?: string };
  try {
    payload = jwt.verify(token, env.JWT_SECRET) as { serial?: string; deviceId?: string };
  } catch {
    res.status(403).json({ success: false, message: 'توكن التفعيل منتهي الصلاحية أو غير صالح.' });
    return;
  }

  if (!payload.serial || !payload.deviceId) {
    res.status(403).json({ success: false, message: 'توكن غير صالح لعمليات المزامنة.' });
    return;
  }

  const license = await prisma.license.findFirst({
    where: { serialKey: { equals: payload.serial, mode: 'insensitive' } },
    include: { device: true },
  });

  if (!license || !license.device || license.device.deviceId.toUpperCase() !== payload.deviceId.toUpperCase()) {
    res.status(403).json({ success: false, message: 'الجهاز غير مرتبط بترخيص صالح.' });
    return;
  }

  if (license.status !== 'ACTIVE') {
    res.status(403).json({ success: false, message: 'الترخيص غير نشط حالياً؛ لا يمكن إجراء المزامنة.' });
    return;
  }

  req.accountId = license.accountId;
  req.deviceRowId = license.device.id;
  next();
}
