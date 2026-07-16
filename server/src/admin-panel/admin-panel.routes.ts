import path from 'path';
import fs from 'fs';
import { Router, Request, Response, NextFunction } from 'express';
import { env } from '../config/env';

export const adminPanelRouter = Router();

// Basic auth gateway middleware for the website admin panel (منقول 1:1)
function gatewayAuth(req: Request, res: Response, next: NextFunction): void {
  const authHeader = req.headers.authorization;
  if (!authHeader) {
    res.setHeader('WWW-Authenticate', 'Basic realm="Secure Serial Dashboard"');
    res.status(401).send('🔒 الدخول إلى لوحة كروتك يتطلب مصادقة أمنية.');
    return;
  }

  const auth = Buffer.from(authHeader.split(' ')[1], 'base64').toString().split(':');
  const user = auth[0];
  const pass = auth[1];

  if (user === env.ADMIN_USERNAME && pass === env.ADMIN_PASSWORD) {
    next();
    return;
  }

  res.setHeader('WWW-Authenticate', 'Basic realm="Secure Serial Dashboard"');
  res.status(401).send('❌ بيانات المرور غير صالحة!');
}

// ⚠️ ملاحظة ترحيل مهمة: في api_server.js الأصلي، كانت دالة gatewayAuth
// *معرَّفة* لكن *غير مُطبَّقة فعلياً* على مسار '/' (لم تُستخدم في أي
// app.use أو كوسيط للمسار). أي أن لوحة التحكم HTML تُقدَّم فعلياً بلا أي
// مصادقة في الإنتاج الحالي. هذا الملف يحافظ على نفس السلوك الفعلي (وليس
// السلوك المقصود على الأرجح) في هذه المرحلة تحديداً لأنها ترحيل 1:1 بحت.
// يجب اتخاذ قرار صريح من صاحب المشروع لتفعيل gatewayAuth أدناه، لأن هذا
// تغيير سلوك حقيقي (سيطلب اسم مستخدم/كلمة مرور لم تكن مطلوبة سابقاً).
adminPanelRouter.get('/', (_req: Request, res: Response) => {
  const parentPath = path.join(__dirname, '..', '..', '..', 'admin_panel.html');
  const localPath = path.join(__dirname, '..', '..', 'admin_panel.html');
  if (fs.existsSync(parentPath)) {
    res.sendFile(parentPath);
  } else if (fs.existsSync(localPath)) {
    res.sendFile(localPath);
  } else {
    res.status(404).send('❌ لوحة التحكم admin_panel.html غير موجودة في السيرفر!');
  }
});
