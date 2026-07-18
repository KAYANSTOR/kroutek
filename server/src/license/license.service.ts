import crypto from 'crypto';
import jwt from 'jsonwebtoken';
import { prisma } from '../config/prisma';
import { env } from '../config/env';

export type SerialValidationResult = {
  httpStatus: number;
  body: {
    success: boolean;
    status: 'ACTIVE' | 'EXPIRED' | 'REVOKED' | 'NOT_FOUND';
    message: string;
    token?: string;
  };
};

/**
 * التحقق من السيريال وربط الجهاز (منطق منقول 1:1 من api_server.js من
 * الناحية السلوكية/الرسائل، لكن معاد تنفيذه فوق Account/Device/License
 * بدل مصفوفة serials المسطّحة القديمة).
 */
export async function validateSerial(serial: string, deviceId: string): Promise<SerialValidationResult> {
  const license = await prisma.license.findFirst({
    where: { serialKey: { equals: serial, mode: 'insensitive' } },
    include: { device: true },
  });

  if (!license) {
    return {
      httpStatus: 200,
      body: { success: false, status: 'NOT_FOUND', message: 'السيريال غير صحيح! يرجى التواصل مع الدعم لطلب مفتاح تنشيط.' },
    };
  }

  if (license.status === 'REVOKED') {
    return {
      httpStatus: 200,
      body: { success: false, status: 'REVOKED', message: 'تم إلغاء تفعيل هذا الترخيص بطلب من الموزع! يرجى مراجعة الدعم.' },
    };
  }

  const now = new Date();
  if (now > license.endDate || license.status === 'EXPIRED') {
    if (license.status !== 'EXPIRED') {
      await prisma.license.update({ where: { id: license.id }, data: { status: 'EXPIRED' } });
    }
    return {
      httpStatus: 200,
      body: { success: false, status: 'EXPIRED', message: 'هذا الترخيص منتهي الصلاحية! يرجى تجديد الاشتراك.' },
    };
  }

  const normalizedDeviceId = deviceId.trim().toUpperCase();

  // First-time activation: لا يوجد جهاز مربوط بعد
  if (!license.device) {
    const device = await prisma.device.upsert({
      where: { deviceId: normalizedDeviceId },
      update: { accountId: license.accountId, lastSeenAt: now },
      create: { deviceId: normalizedDeviceId, accountId: license.accountId, lastSeenAt: now },
    });
    await prisma.license.update({ where: { id: license.id }, data: { deviceId: device.id, status: 'ACTIVE' } });

    return {
      httpStatus: 200,
      body: {
        success: true,
        status: 'ACTIVE',
        message: '🔓 تم تفعيل وترخيص التطبيق وربطه بجهازك بنجاح للمرة الأولى!',
        token: jwt.sign({ serial, deviceId }, env.JWT_SECRET, { expiresIn: '365d' }),
      },
    };
  }

  // الجهاز مربوط مسبقاً — تحقق من التطابق (Anti-Cloning Device Lock)
  if (license.device.deviceId.trim().toUpperCase() !== normalizedDeviceId) {
    return {
      httpStatus: 200,
      body: {
        success: false,
        status: 'REVOKED',
        message: '❌ فشل التفعيل! هذا الترخيص مخصص لجهاز هاتف آخر ومقفل أمنياً ضد الاستنساخ.',
      },
    };
  }

  await prisma.license.update({ where: { id: license.id }, data: { status: 'ACTIVE' } });
  await prisma.device.update({ where: { id: license.device.id }, data: { lastSeenAt: now } });

  return {
    httpStatus: 200,
    body: {
      success: true,
      status: 'ACTIVE',
      message: '✔ الترخيص ساري ومفعّل لجهازك بنجاح!',
      token: jwt.sign({ serial, deviceId }, env.JWT_SECRET, { expiresIn: '365d' }),
    },
  };
}

/** شكل رد لوحة التحكم — يطابق شكل {clients, serials, logs} القديم تماماً حفاظاً على توافق admin_panel.html */
export async function getDashboardData() {
  const [accounts, licenses, logs] = await Promise.all([
    prisma.account.findMany({ orderBy: { createdAt: 'asc' } }),
    prisma.license.findMany({ include: { device: true }, orderBy: { createdAt: 'asc' } }),
    prisma.securityLog.findMany({ orderBy: { timestamp: 'desc' }, take: 100 }),
  ]);

  return {
    clients: accounts.map((a) => ({
      id: a.id,
      name: a.name,
      network_name: a.networkName,
      phone: a.phone,
      notes: a.notes,
    })),
    serials: licenses.map((l) => ({
      id: l.id,
      client_id: l.accountId,
      serial_key: l.serialKey,
      device_id: l.device?.deviceId ?? null,
      duration_months: l.durationMonths,
      start_date: l.startDate.toISOString().split('T')[0],
      end_date: l.endDate.toISOString().split('T')[0],
      status: l.status,
      notes: l.notes,
    })),
    logs: logs.map((log) => ({
      id: log.id,
      ip: log.ipAddress,
      endpoint: log.endpoint,
      statusCode: log.statusCode,
      message: log.message,
      payload: log.requestPayload,
      timestamp: log.timestamp.toISOString(),
    })),
  };
}

export type CreateSerialInput = {
  name: string;
  network_name: string;
  phone: string;
  duration_months?: number | string;
  notes?: string;
  device_id?: string;
};

export type SerialResponseDTO = {
  id: string;
  client_id: string;
  serial_key: string;
  device_id: string | null;
  duration_months: number;
  start_date: string;
  end_date: string;
  status: string;
  notes: string | null;
};

export type CreateSerialResult =
  | { ok: true; serial: SerialResponseDTO }
  | { ok: false; httpStatus: number; message: string };

async function mapLicenseForResponse(license: {
  id: string;
  accountId: string;
  serialKey: string;
  durationMonths: number;
  startDate: Date;
  endDate: Date;
  status: string;
  notes: string | null;
}, deviceIdStr: string | null): Promise<SerialResponseDTO> {
  return {
    id: license.id,
    client_id: license.accountId,
    serial_key: license.serialKey,
    device_id: deviceIdStr,
    duration_months: license.durationMonths,
    start_date: license.startDate.toISOString().split('T')[0],
    end_date: license.endDate.toISOString().split('T')[0],
    status: license.status,
    notes: license.notes,
  };
}

/** توليد وإضافة سيريال جديد مربوط بعميل (Account) جديد — منقول 1:1 من api_server.js */
export async function createSerial(input: CreateSerialInput): Promise<CreateSerialResult> {
  const { name, network_name, phone, duration_months, notes, device_id } = input;

  if (!name || !network_name || !phone) {
    return { ok: false, httpStatus: 400, message: 'يرجى تعبئة الحقول الإلزامية الاسم، الشبكة، والهاتف!' };
  }

  // 1. توليد بصمة الأمان (نفس Salt والخوارزمية القديمة تماماً — لا تغيير،
  //    لأن تدوير هذا السر قرار منفصل موثّق في القسم 5.4 من الخطة، ويتطلب
  //    تحديث تطبيق الأندرويد أولاً).
  const salt = 'KayanSoftSecureSalt2026';
  const bindingValue = device_id && device_id.trim() ? device_id.trim().toUpperCase() : phone.trim().toUpperCase();
  const rawData = bindingValue + salt;
  const hash = crypto.createHash('sha256').update(rawData).digest('hex').substring(0, 6).toUpperCase();
  const finalSerialKey = `${phone.trim().toUpperCase()}-KS${hash}`;

  const existing = await prisma.license.findUnique({ where: { serialKey: finalSerialKey } });
  if (existing) {
    return { ok: false, httpStatus: 400, message: 'السيريال الخاص بهذا الهاتف تم توليده مسبقاً!' };
  }

  const start = new Date();
  const end = new Date();
  const months = parseInt(String(duration_months ?? 12), 10);
  end.setMonth(end.getMonth() + months);

  const isPreBound = Boolean(device_id && device_id.trim());

  const account = await prisma.account.create({
    data: { name, networkName: network_name, phone, notes: notes || null },
  });

  let deviceRow = null;
  if (isPreBound) {
    const normalizedDeviceId = device_id!.trim().toUpperCase();
    deviceRow = await prisma.device.upsert({
      where: { deviceId: normalizedDeviceId },
      update: { accountId: account.id },
      create: { deviceId: normalizedDeviceId, accountId: account.id },
    });
  }

  const license = await prisma.license.create({
    data: {
      accountId: account.id,
      deviceId: deviceRow?.id ?? null,
      serialKey: finalSerialKey,
      durationMonths: months,
      startDate: start,
      endDate: end,
      status: isPreBound ? 'ACTIVE' : 'UNUSED',
      notes: notes || null,
    },
  });

  const serialForResponse = await mapLicenseForResponse(license, deviceRow?.deviceId ?? null);
  return { ok: true, serial: serialForResponse };
}

export async function resetDeviceLock(licenseId: string): Promise<{ ok: boolean; httpStatus: number; message: string }> {
  const license = await prisma.license.findUnique({ where: { id: licenseId } });
  if (!license) {
    return { ok: false, httpStatus: 404, message: 'السيريال غير موجود!' };
  }
  await prisma.license.update({ where: { id: licenseId }, data: { deviceId: null, status: 'UNUSED' } });
  return { ok: true, httpStatus: 200, message: 'تم إلغاء قفل الجهاز بنجاح! السيريال جاهز للربط بهاتف جديد.' };
}

export async function toggleSerial(licenseId: string): Promise<{ ok: boolean; httpStatus: number; message: string }> {
  const license = await prisma.license.findUnique({ where: { id: licenseId } });
  if (!license) {
    return { ok: false, httpStatus: 404, message: 'السيريال غير موجود!' };
  }

  if (license.status === 'REVOKED') {
    const newStatus = license.deviceId ? 'ACTIVE' : 'UNUSED';
    await prisma.license.update({ where: { id: licenseId }, data: { status: newStatus } });
    return { ok: true, httpStatus: 200, message: 'تمت إعادة تفعيل الترخيص بنجاح!' };
  }

  await prisma.license.update({ where: { id: licenseId }, data: { status: 'REVOKED' } });
  return { ok: true, httpStatus: 200, message: 'تم إلغاء وتجميد ترخيص هذا السيريال بنجاح!' };
}

export async function deleteSerial(licenseId: string): Promise<{ ok: boolean; httpStatus: number; message: string }> {
  const license = await prisma.license.findUnique({ where: { id: licenseId } });
  if (!license) {
    return { ok: false, httpStatus: 404, message: 'السيريال غير موجود!' };
  }
  await prisma.license.delete({ where: { id: licenseId } });
  return { ok: true, httpStatus: 200, message: 'تم حذف السيريال والعميل نهائياً من النظام!' };
}
