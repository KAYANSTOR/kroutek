import path from 'path';
import dotenv from 'dotenv';

// يحمّل متغيرات ملف server/.env المحلي تلقائياً إن وُجد (لا يؤثر إطلاقاً إن
// كانت متغيرات البيئة مضبوطة فعلاً عبر الاستضافة أو PM2 أو أي مصدر آخر).
dotenv.config({ path: path.join(__dirname, '..', '..', '.env') });

// ⚠️ لا قيم افتراضية للأسرار هنا (نفس قرار الأمان المطبَّق سابقاً في
// api_server.js — انظر docs/RESTRUCTURING_PLAN.md القسم 9 البند 1).
const REQUIRED_ENV_VARS = ['JWT_SECRET', 'HMAC_SECRET', 'ADMIN_USERNAME', 'ADMIN_PASSWORD', 'DATABASE_URL'] as const;

function assertRequiredEnvVars(): void {
  const missing = REQUIRED_ENV_VARS.filter((key) => !process.env[key] || !process.env[key]!.trim());
  if (missing.length > 0) {
    console.error('=============================================================');
    console.error('❌ فشل بدء تشغيل السيرفر: متغيرات بيئة إلزامية غير مضبوطة:');
    missing.forEach((key) => console.error(`   - ${key}`));
    console.error('راجع ملف server/.env.example، وقم بإنشاء ملف .env حقيقي بقيم جديدة قوية.');
    console.error('=============================================================');
    process.exit(1);
  }
}

assertRequiredEnvVars();

export const env = {
  PORT: parseInt(process.env.PORT || '3000', 10),
  JWT_SECRET: process.env.JWT_SECRET as string,
  HMAC_SECRET: process.env.HMAC_SECRET as string,
  ADMIN_USERNAME: process.env.ADMIN_USERNAME as string,
  ADMIN_PASSWORD: process.env.ADMIN_PASSWORD as string,
  DATABASE_URL: process.env.DATABASE_URL as string,
};
