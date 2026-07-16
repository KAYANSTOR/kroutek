import { Router, Request, Response } from 'express';
import { verifyRequestSignature } from '../middleware/requestSignature';
import { authenticateAdmin } from '../auth/auth.middleware';
import { addLog } from '../shared/auditLog';
import * as licenseService from './license.service';

export const licenseRouter = Router();

/**
 * Endpoint: Validate Serial & Bind Device ID (called on activation)
 * منقول 1:1 من api_server.js (نفس التحقق من صيغة الإدخال بالضبط لمنع الحقن).
 */
licenseRouter.post('/serial/validate', verifyRequestSignature, async (req: Request, res: Response) => {
  const { serial, deviceId } = req.body;
  const clientIp = req.ip || 'unknown';

  if (!/^[A-Z0-9_-]{4,60}$/i.test(serial) || !/^[A-Z0-9_-]{4,40}$/i.test(deviceId)) {
    addLog(clientIp, '/api/v1/serial/validate', 400, `Potential injection attempt blocked: Serial: ${serial}, Device: ${deviceId}`);
    res.status(400).json({ success: false, status: 'NOT_FOUND', message: 'بيانات الإدخال تحتوي على رموز غير مسموح بها!' });
    return;
  }

  const result = await licenseService.validateSerial(serial, deviceId);

  const logMessage = result.body.success
    ? `Activation success for serial ${serial}, device ${deviceId}`
    : `Activation rejected (${result.body.status}) for serial ${serial}, device ${deviceId}`;
  addLog(clientIp, '/api/v1/serial/validate', result.body.success ? 200 : 403, logMessage);

  res.status(result.httpStatus).json(result.body);
});

// -------------------------------------------------------------
// SECURE ADMIN ENDPOINTS (منقولة 1:1، فوق طبقة الخدمة الجديدة)
// -------------------------------------------------------------

licenseRouter.get('/admin/dashboard', authenticateAdmin, async (_req: Request, res: Response) => {
  const data = await licenseService.getDashboardData();
  res.json({ success: true, ...data });
});

licenseRouter.post('/admin/serial/create', authenticateAdmin, async (req: Request, res: Response) => {
  const result = await licenseService.createSerial(req.body);
  if (!result.ok) {
    res.status(result.httpStatus).json({ success: false, message: result.message });
    return;
  }
  addLog(req.ip || 'unknown', '/api/v1/admin/serial/create', 200, `Generated serial ${result.serial.serial_key}`);
  res.json({ success: true, serial: result.serial });
});

licenseRouter.post('/admin/serial/reset/:id', authenticateAdmin, async (req: Request, res: Response) => {
  const result = await licenseService.resetDeviceLock(String(req.params.id));
  if (!result.ok) {
    res.status(result.httpStatus).json({ success: false, message: result.message });
    return;
  }
  addLog(req.ip || 'unknown', '/api/v1/admin/serial/reset', 200, `Reset device lock for license ID ${String(req.params.id)}`);
  res.json({ success: true, message: result.message });
});

licenseRouter.post('/admin/serial/toggle/:id', authenticateAdmin, async (req: Request, res: Response) => {
  const result = await licenseService.toggleSerial(String(req.params.id));
  if (!result.ok) {
    res.status(result.httpStatus).json({ success: false, message: result.message });
    return;
  }
  addLog(req.ip || 'unknown', '/api/v1/admin/serial/toggle', 200, `Toggled license ID ${String(req.params.id)}`);
  res.json({ success: true, message: result.message });
});

licenseRouter.delete('/admin/serial/delete/:id', authenticateAdmin, async (req: Request, res: Response) => {
  const result = await licenseService.deleteSerial(String(req.params.id));
  if (!result.ok) {
    res.status(result.httpStatus).json({ success: false, message: result.message });
    return;
  }
  addLog(req.ip || 'unknown', '/api/v1/admin/serial/delete', 200, `Deleted license ID ${String(req.params.id)}`);
  res.json({ success: true, message: result.message });
});
