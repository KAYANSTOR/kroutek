import { PrismaClient } from '@prisma/client';
import './env'; // يضمن التحقق من DATABASE_URL قبل إنشاء العميل

// نسخة واحدة فقط من PrismaClient لكامل عمر السيرفر (تفادياً لاستنفاد
// اتصالات قاعدة البيانات، وهو خطأ شائع عند إنشاء PrismaClient جديد في
// كل طلب).
export const prisma = new PrismaClient();
