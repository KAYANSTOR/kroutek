import { Router, Request, Response } from 'express';
import jwt from 'jsonwebtoken';
import { env } from '../config/env';
import { addLog } from '../shared/auditLog';

export const authRouter = Router();

// Admin login to get JWT Token (منقول 1:1 من api_server.js)
authRouter.post('/login', (req: Request, res: Response) => {
  const { username, password } = req.body;

  if (username === env.ADMIN_USERNAME && password === env.ADMIN_PASSWORD) {
    const token = jwt.sign({ username, role: 'super_admin' }, env.JWT_SECRET, { expiresIn: '24h' });
    addLog(req.ip || 'unknown', '/api/v1/admin/login', 200, 'Admin logged in successfully.');
    res.json({ success: true, token });
    return;
  }

  addLog(req.ip || 'unknown', '/api/v1/admin/login', 401, `Failed admin login attempt: Username: ${username}`);
  res.status(401).json({ success: false, message: 'خطأ في اسم المستخدم أو كلمة المرور!' });
});
