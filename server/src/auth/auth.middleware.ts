import { Request, Response, NextFunction } from 'express';
import jwt from 'jsonwebtoken';
import { env } from '../config/env';

export interface AuthenticatedRequest extends Request {
  admin?: string | jwt.JwtPayload;
}

// Authentication middleware for administrative actions (منقول 1:1)
export function authenticateAdmin(req: AuthenticatedRequest, res: Response, next: NextFunction): void {
  const authHeader = req.headers.authorization;
  if (!authHeader || !authHeader.startsWith('Bearer ')) {
    res.status(401).json({ success: false, message: 'غير مصرح به: الرجاء تسجيل الدخول أولاً!' });
    return;
  }
  const token = authHeader.split(' ')[1];
  try {
    const decoded = jwt.verify(token, env.JWT_SECRET);
    req.admin = decoded;
    next();
  } catch (err) {
    res.status(403).json({ success: false, message: 'فشلت المصادقة: توقيع الأمان منتهي أو غير صالح!' });
  }
}
