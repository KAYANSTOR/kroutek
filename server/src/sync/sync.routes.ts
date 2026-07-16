import { Router, Response } from 'express';
import { deviceAuth, SyncRequest } from './sync.middleware';
import { ALL_SYNC_ENTITIES, SyncableEntity, SYNC_REGISTRY } from './sync.registry';
import { pushEntityChanges, pullEntityChanges, IncomingRecord, PushRecordResult } from './sync.service';
import { addLog } from '../shared/auditLog';

export const syncRouter = Router();
syncRouter.use(deviceAuth);

function isValidEntity(name: string): name is SyncableEntity {
  return (ALL_SYNC_ENTITIES as string[]).includes(name);
}

/**
 * POST /api/v1/sync/push
 * body: { changes: { [entityType]: IncomingRecord[] } }
 * يمكن إرسال أكثر من نوع كيان في نفس الطلب (رحلة شبكة واحدة بدل عشر).
 */
syncRouter.post('/push', async (req: SyncRequest, res: Response) => {
  const accountId = req.accountId as string;
  const changes = (req.body?.changes ?? {}) as Record<string, IncomingRecord[]>;

  const results: Record<string, PushRecordResult[]> = {};
  let totalApplied = 0;

  for (const [entityType, records] of Object.entries(changes)) {
    if (!isValidEntity(entityType) || !Array.isArray(records)) continue;
    if (records.length > 500) {
      results[entityType] = [{ id: 'batch', status: 'error', reason: 'الحد الأقصى 500 سجل لكل نوع في الطلب الواحد' }];
      continue;
    }
    const entityResults = await pushEntityChanges(entityType, accountId, records);
    results[entityType] = entityResults;
    totalApplied += entityResults.filter((r) => r.status === 'applied').length;
  }

  addLog(req.ip || 'unknown', '/api/v1/sync/push', 200, `Sync push: ${totalApplied} records applied for account ${accountId}`);
  res.json({ success: true, results });
});

/**
 * GET /api/v1/sync/pull?since=<ISO timestamp>&entities=cards,deposits,...
 * إن لم تُحدَّد entities، تُسحَب كل الكيانات القابلة للمزامنة.
 * الرد يتضمن serverTime — يجب أن يخزّنه الجهاز ويستخدمه كمؤشر "since" في
 * الطلب التالي (أدق من الاعتماد على أحدث updatedAt بين السجلات نفسها).
 */
syncRouter.get('/pull', async (req: SyncRequest, res: Response) => {
  const accountId = req.accountId as string;
  const sinceParam = req.query.since as string | undefined;
  const since = sinceParam ? new Date(sinceParam) : new Date(0);

  if (sinceParam && isNaN(since.getTime())) {
    res.status(400).json({ success: false, message: 'صيغة since غير صالحة (يجب أن تكون ISO timestamp).' });
    return;
  }

  const requestedEntities = (req.query.entities as string | undefined)?.split(',').filter(isValidEntity) ?? ALL_SYNC_ENTITIES;

  const serverTime = new Date();
  const changes: Record<string, unknown[]> = {};
  for (const entityType of requestedEntities) {
    changes[entityType] = await pullEntityChanges(entityType, accountId, since);
  }

  res.json({ success: true, serverTime: serverTime.toISOString(), changes });
});

/** قائمة الكيانات القابلة للمزامنة وحقولها المسموحة — مرجع تشخيصي مفيد للتطوير */
syncRouter.get('/schema', (_req: SyncRequest, res: Response) => {
  const schema = Object.fromEntries(
    ALL_SYNC_ENTITIES.map((entity) => [entity, { allowedFields: SYNC_REGISTRY[entity].allowedFields, hasLocalId: SYNC_REGISTRY[entity].hasLocalId }])
  );
  res.json({ success: true, entities: schema });
});
