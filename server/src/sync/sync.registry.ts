import { prisma } from '../config/prisma';

/**
 * سجل مركزي لكل كيان قابل للمزامنة: اسمه في الـ API، الحقول المسموح
 * استقبالها من العميل (Allow-list صارمة — أي حقل غير مذكور هنا يُتجاهل
 * تماماً حتى لو أرسله العميل)، ومُفوِّض Prisma الخاص به.
 *
 * ⚠️ ملاحظة تصميم مهمة: استخدام مُفوِّض Prisma ديناميكياً هنا (بدل كتابة
 * معالج Push/Pull منفصل لكل كيان من الكيانات العشرة) قرار متعمّد لتفادي
 * تكرار نفس المنطق عشر مرات تقريباً بالحرف. الثمن: بعض الدقة في الأنواع
 * (TypeScript) تُستبدل بـ `any` في نقطة الوصول الديناميكي فقط — بقية
 * الملف (الحقول المسموحة، منطق LWW) يبقى مضبوطاً بدقة لكل كيان.
 *
 * ⚠️ حالة الجاهزية على جانب الأندرويد (مهم، موثّق سابقاً في القسم 5.5 من
 * docs/RESTRUCTURING_PLAN.md): الكيانات الستة الأولى أدناه لا تزال تستخدم
 * مفتاحاً أساسياً Int تلقائي التزايد في Room حالياً، ولم تُهاجَر لـ UUID
 * بعد. المزامنة الفعلية من التطبيق لهذه الكيانات الستة **لن تعمل** قبل
 * إنجاز تلك الهجرة على جانب الأندرويد. الكيانات الأربعة الخاصة بنظام
 * الموزّع تستخدم UUID أصلاً في Room ولا تحتاج أي هجرة لتكون جاهزة للمزامنة.
 */

export type SyncableEntity =
  | 'cards'
  | 'cardTransactions'
  | 'pendingApprovals'
  | 'deposits'
  | 'customerMappings'
  | 'generatedMikrotikCards'
  | 'distributorCustomers'
  | 'distributorTransactions'
  | 'distributorExpenses'
  | 'distributorCapitals';

type EntityConfig = {
  /** مُفوِّض Prisma المطابق (prisma.card, prisma.cardTransaction, ...) */
  delegate: () => any;
  /** الحقول المسموح للعميل إرسالها (غير ذلك يُتجاهل بصمت) */
  allowedFields: string[];
  /** هل يحمل هذا الكيان حقل localId (بقايا Room Int PK القديم)؟ */
  hasLocalId: boolean;
};

export const SYNC_REGISTRY: Record<SyncableEntity, EntityConfig> = {
  cards: {
    delegate: () => prisma.card,
    allowedFields: ['category', 'code', 'username', 'password', 'used'],
    hasLocalId: true,
  },
  cardTransactions: {
    delegate: () => prisma.cardTransaction,
    allowedFields: ['phone', 'amount', 'cardCode', 'walletType', 'createdAt'],
    hasLocalId: true,
  },
  pendingApprovals: {
    delegate: () => prisma.pendingApproval,
    allowedFields: ['phone', 'amount', 'walletType', 'isAccountCode', 'depositLocalId', 'createdAt'],
    hasLocalId: true,
  },
  deposits: {
    delegate: () => prisma.deposit,
    allowedFields: ['phone', 'amount', 'walletType', 'isShared', 'cardDetails', 'createdAt'],
    hasLocalId: true,
  },
  customerMappings: {
    delegate: () => prisma.customerMapping,
    allowedFields: ['customerUniqueId', 'basicPhone', 'customerName', 'walletType'],
    hasLocalId: true,
  },
  generatedMikrotikCards: {
    delegate: () => prisma.generatedMikrotikCard,
    allowedFields: ['category', 'pin', 'username', 'password', 'printed', 'transferred', 'createdAt'],
    hasLocalId: true,
  },
  distributorCustomers: {
    delegate: () => prisma.distributorCustomer,
    allowedFields: ['name', 'totalSales', 'totalPayments', 'currentBalance', 'createdAt'],
    hasLocalId: false,
  },
  distributorTransactions: {
    delegate: () => prisma.distributorTransaction,
    allowedFields: ['customerId', 'date', 'type', 'amount', 'notes'],
    hasLocalId: false,
  },
  distributorExpenses: {
    delegate: () => prisma.distributorExpense,
    allowedFields: ['category', 'amount', 'description', 'date'],
    hasLocalId: false,
  },
  distributorCapitals: {
    delegate: () => prisma.distributorCapital,
    allowedFields: ['type', 'amount', 'description', 'date'],
    hasLocalId: false,
  },
};

export const ALL_SYNC_ENTITIES = Object.keys(SYNC_REGISTRY) as SyncableEntity[];
