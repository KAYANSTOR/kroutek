/**
 * بذر بيانات تجريبية مطابقة تماماً للبيانات الافتراضية القديمة التي كانت
 * مكتوبة يدوياً داخل مصفوفات api_server.js (نفس الاسم، الهاتف، السيريال).
 *
 * التشغيل: npm run prisma:seed (بعد npx prisma migrate dev محلياً عندك)
 */
import { PrismaClient } from '@prisma/client';

const prisma = new PrismaClient();

async function main() {
  const account = await prisma.account.upsert({
    where: { id: '00000000-0000-0000-0000-000000000001' },
    update: {},
    create: {
      id: '00000000-0000-0000-0000-000000000001',
      name: 'أحمد بن سعد',
      networkName: 'شبكة الدحشة',
      phone: '771112223',
      notes: 'العميل الافتراضي الأول',
    },
  });

  const device = await prisma.device.upsert({
    where: { deviceId: '9774D56D682E549C' },
    update: {},
    create: { deviceId: '9774D56D682E549C', accountId: account.id },
  });

  await prisma.license.upsert({
    where: { serialKey: '771112223-KS0D8F3E' },
    update: {},
    create: {
      accountId: account.id,
      deviceId: device.id,
      serialKey: '771112223-KS0D8F3E',
      durationMonths: 12,
      startDate: new Date('2026-07-01'),
      endDate: new Date('2027-07-01'),
      status: 'ACTIVE',
      notes: 'سيريال تجريبي نشط',
    },
  });

  console.log('✅ تم بذر البيانات التجريبية بنجاح.');
}

main()
  .catch((err) => {
    console.error('❌ فشل بذر البيانات:', err);
    process.exit(1);
  })
  .finally(async () => {
    await prisma.$disconnect();
  });
