import express, { Request, Response } from 'express';
import cors from 'cors';
import { rateLimiter } from './middleware/rateLimiter';
import { authRouter } from './auth/auth.routes';
import { licenseRouter } from './license/license.routes';
import { adminPanelRouter } from './admin-panel/admin-panel.routes';
import { syncRouter } from './sync/sync.routes';

export function createApp() {
  const app = express();

  app.use(cors());
  app.use(express.json());
  app.use(rateLimiter);

  // نفس مسارات api_server.js تماماً: /api/v1/serial/*, /api/v1/admin/*
  app.use('/api/v1', licenseRouter);
  app.use('/api/v1/admin', authRouter);
  app.use('/api/v1/sync', syncRouter);
  app.use('/', adminPanelRouter);

  // JSON API Status endpoint (منقول 1:1)
  app.get('/api/v1/status', (_req: Request, res: Response) => {
    res.json({ name: 'KayanSoft Security API Gateway', version: '1.0.0', status: 'SECURE' });
  });

  return app;
}
