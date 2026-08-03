import { SYNC_REGISTRY, SyncableEntity } from './sync.registry';

export type IncomingRecord = {
  id: string;
  localId?: number | null;
  updatedAt: string; // ISO string من الجهاز
  deletedAt?: string | null;
  [key: string]: unknown;
};

export type PushRecordResult = {
  id: string;
  status: 'applied' | 'skipped_stale' | 'error';
  reason?: string;
};

/**
 * تطبيق دفعة تغييرات لكيان واحد بمنطق Last-Write-Wins:
 * - إن لم يوجد السجل على السيرفر: يُنشأ مباشرة.
 * - إن وُجد: يُحدَّث فقط إذا كان updatedAt الوارد من الجهاز أحدث (أو يساوي)
 *   من updatedAt المخزَّن على السيرفر. غير ذلك يُتجاهل (النسخة السيرفرية
 *   أحدث بالفعل، على الأرجح جاءت من مزامنة جهاز آخر).
 * - الحذف يُطبَّق كـ Tombstone (تعيين deletedAt) لا حذف فعلي، حتى تصل
 *   إشارة الحذف لبقية الأجهزة عبر Pull لاحقاً.
 *
 * ⚠️ accountId يُمرَّر دائماً من طبقة المصادقة (deviceAuth) وليس من جسم
 * الطلب — لا يمكن لأي عميل الكتابة في حساب غير حسابه.
 */
export async function pushEntityChanges(
  entityType: SyncableEntity,
  accountId: string,
  records: IncomingRecord[]
): Promise<PushRecordResult[]> {
  const config = SYNC_REGISTRY[entityType];
  const delegate = config.delegate();
  const results: PushRecordResult[] = [];

  for (const record of records) {
    try {
      if (!record.id || !record.updatedAt) {
        results.push({ id: record.id ?? 'unknown', status: 'error', reason: 'id و updatedAt إلزاميان' });
        continue;
      }

      const incomingUpdatedAt = new Date(record.updatedAt);
      const existing = await delegate.findUnique({ where: { id: record.id } });

      // منع تسرّب بيانات بين الحسابات: سجل موجود لحساب آخر لا يُعدَّل أبداً.
      if (existing && existing.accountId !== accountId) {
        results.push({ id: record.id, status: 'error', reason: 'السجل يخص حساباً آخر' });
        continue;
      }

      if (existing && existing.updatedAt >= incomingUpdatedAt) {
        results.push({ id: record.id, status: 'skipped_stale' });
        continue;
      }

      const data: Record<string, unknown> = { accountId };
      for (const field of config.allowedFields) {
        if (field in record) data[field] = record[field];
      }
      if (config.hasLocalId && record.localId != null) data.localId = record.localId;
      data.deletedAt = record.deletedAt ? new Date(record.deletedAt) : null;

      await delegate.upsert({
        where: { id: record.id },
        create: { id: record.id, ...data },
        update: data,
      });

      results.push({ id: record.id, status: 'applied' });
    } catch (err) {
      results.push({ id: record.id ?? 'unknown', status: 'error', reason: err instanceof Error ? err.message : 'خطأ غير معروف' });
    }
  }

  return results;
}

/**
 * سحب كل التغييرات لكيان معيّن منذ مؤشر زمني معيّن (يشمل السجلات المحذوفة
 * ناعماً/Tombstones، حتى يطبّق الجهاز الحذف محلياً أيضاً).
 */
export async function pullEntityChanges(entityType: SyncableEntity, accountId: string, since: Date) {
  const config = SYNC_REGISTRY[entityType];
  const delegate = config.delegate();
  return delegate.findMany({
    where: { accountId, updatedAt: { gt: since } },
    orderBy: { updatedAt: 'asc' },
  });
}
