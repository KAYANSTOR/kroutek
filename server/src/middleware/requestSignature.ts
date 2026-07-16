import crypto from 'crypto';
import { Request, Response, NextFunction } from 'express';
import { env } from '../config/env';

/**
 * Custom Request Signature Verification Middleware (HMAC-SHA256)
 * منقول 1:1 من api_server.js — نفس صيغة التوقيع، نفس نافذة الـ 5 دقائق
 * لمنع Replay Attacks، بلا أي تغيير في السلوك.
 */
export function verifyRequestSignature(req: Request, res: Response, next: NextFunction): void {
  const signature = (req.headers['x-signature'] as string) || req.body.signature;
  const timestamp = (req.headers['x-timestamp'] as string) || req.body.timestamp;

  if (!signature || !timestamp) {
    res.status(401).json({ success: false, status: 'REVOKED', message: '🛡️ طلب غير مصرح به: توقيع الأمان مفقود!' });
    return;
  }

  // Check for replay attacks: Block requests older than 5 minutes
  const now = Date.now();
  if (Math.abs(now - parseInt(timestamp, 10)) > 300000) {
    res.status(403).json({ success: false, status: 'REVOKED', message: '🛡️ فشل تأكيد أمان الطلب (Replay Attack block)!' });
    return;
  }

  const body = req.body;
  const dataToSign = `${body.serial}:${body.deviceId}:${body.timestamp}:${body.nonce}`;
  const expectedSignature = crypto.createHmac('sha256', env.HMAC_SECRET).update(dataToSign).digest('hex').toUpperCase();

  if (String(signature).toUpperCase() !== expectedSignature) {
    res.status(403).json({ success: false, status: 'REVOKED', message: '🛡️ فشل مطابقة تشفير الطلب (Tamper attempt blocked)!' });
    return;
  }

  next();
}
