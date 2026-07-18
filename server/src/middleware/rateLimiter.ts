import { Request, Response, NextFunction } from 'express';
import { addLog } from '../shared/auditLog';

// Simple Rate Limiting Map (نفس المنطق تماماً من api_server.js، منقول بلا تغيير)
const rateLimitMap = new Map<string, number[]>();

export function rateLimiter(req: Request, res: Response, next: NextFunction): void {
  const ip = req.ip || req.socket.remoteAddress || 'unknown';
  const now = Date.now();
  const windowMs = 60000; // 1 minute window
  const maxRequests = 60; // 60 requests per minute limit

  if (!rateLimitMap.has(ip)) {
    rateLimitMap.set(ip, []);
  }

  const requests = rateLimitMap.get(ip)!.filter((timestamp) => now - timestamp < windowMs);
  requests.push(now);
  rateLimitMap.set(ip, requests);

  if (requests.length > maxRequests) {
    addLog(ip, req.originalUrl, 429, 'Rate limit exceeded! Potential DDoS threat.');
    res.status(429).json({ success: false, message: '⚠️ تم تجاوز حد الطلبات المسموح بها! يرجى الانتظار دقيقة.' });
    return;
  }
  next();
}
