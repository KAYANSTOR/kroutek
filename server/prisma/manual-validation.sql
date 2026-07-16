-- =============================================================
-- ترجمة يدوية لـ server/prisma/schema.prisma إلى SQL خام
-- =============================================================
-- الغرض الوحيد من هذا الملف: التحقق الفعلي من أن تصميم العلاقات
-- والمفاتيح في schema.prisma سليم منطقياً، عبر تشغيله على PostgreSQL
-- محلي حقيقي (تعذّر استخدام `npx prisma migrate dev` نفسه لأن ثنائيات
-- Prisma تُحمَّل من binaries.prisma.sh، وهذا النطاق غير مسموح به في
-- بيئة التنفيذ الحالية).
--
-- ⚠️ هذا الملف ليس بديلاً عن Migration رسمي من Prisma. لا يُستخدم في
-- الإنتاج. شغّل `npx prisma migrate dev --name init` محلياً عندك
-- لتوليد الـ migration الرسمي المعتمد فعلياً.

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE admin_users (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username      TEXT UNIQUE NOT NULL,
    password_hash TEXT NOT NULL,
    role          TEXT NOT NULL DEFAULT 'admin',
    created_at    TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE accounts (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name         TEXT NOT NULL,
    network_name TEXT NOT NULL,
    phone        TEXT NOT NULL,
    notes        TEXT,
    created_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at   TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE devices (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    account_id   UUID NOT NULL REFERENCES accounts(id) ON DELETE CASCADE,
    device_id    TEXT UNIQUE NOT NULL,
    last_seen_at TIMESTAMPTZ,
    created_at   TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TYPE license_status AS ENUM ('UNUSED', 'ACTIVE', 'EXPIRED', 'REVOKED');

CREATE TABLE licenses (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    account_id      UUID NOT NULL REFERENCES accounts(id) ON DELETE CASCADE,
    device_id       UUID REFERENCES devices(id),
    serial_key      TEXT UNIQUE NOT NULL,
    duration_months INT NOT NULL DEFAULT 12,
    start_date      TIMESTAMPTZ NOT NULL,
    end_date        TIMESTAMPTZ NOT NULL,
    status          license_status NOT NULL DEFAULT 'UNUSED',
    notes           TEXT,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE security_logs (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ip_address      TEXT NOT NULL,
    endpoint        TEXT NOT NULL,
    request_payload TEXT,
    status_code     INT NOT NULL,
    message         TEXT,
    timestamp       TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE cards (
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    local_id   INT,
    account_id UUID NOT NULL REFERENCES accounts(id) ON DELETE CASCADE,
    category   INT NOT NULL,
    code       TEXT NOT NULL,
    username   TEXT NOT NULL DEFAULT '',
    password   TEXT NOT NULL DEFAULT '',
    used       BOOLEAN NOT NULL DEFAULT false,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    deleted_at TIMESTAMPTZ
);
CREATE INDEX idx_cards_account_category_used ON cards(account_id, category, used);

CREATE TABLE card_transactions (
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    local_id   INT,
    account_id UUID NOT NULL REFERENCES accounts(id) ON DELETE CASCADE,
    phone      TEXT NOT NULL,
    amount     INT NOT NULL,
    card_code  TEXT NOT NULL,
    wallet_type TEXT NOT NULL DEFAULT '',
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    deleted_at TIMESTAMPTZ
);
CREATE INDEX idx_cardtx_account_created ON card_transactions(account_id, created_at);

CREATE TABLE pending_approvals (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    local_id         INT,
    account_id       UUID NOT NULL REFERENCES accounts(id) ON DELETE CASCADE,
    phone            TEXT NOT NULL,
    amount           INT NOT NULL,
    wallet_type      TEXT NOT NULL,
    is_account_code  BOOLEAN NOT NULL DEFAULT false,
    deposit_local_id INT NOT NULL DEFAULT 0,
    created_at       TIMESTAMPTZ NOT NULL,
    updated_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
    deleted_at       TIMESTAMPTZ
);
CREATE INDEX idx_pending_account ON pending_approvals(account_id);

CREATE TABLE deposits (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    local_id     INT,
    account_id   UUID NOT NULL REFERENCES accounts(id) ON DELETE CASCADE,
    phone        TEXT NOT NULL,
    amount       INT NOT NULL,
    wallet_type  TEXT NOT NULL,
    is_shared    BOOLEAN NOT NULL,
    card_details TEXT NOT NULL DEFAULT '',
    created_at   TIMESTAMPTZ NOT NULL,
    updated_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
    deleted_at   TIMESTAMPTZ
);
CREATE INDEX idx_deposits_account ON deposits(account_id);

CREATE TABLE customer_mappings (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    local_id            INT,
    account_id          UUID NOT NULL REFERENCES accounts(id) ON DELETE CASCADE,
    customer_unique_id  TEXT NOT NULL,
    basic_phone         TEXT NOT NULL,
    customer_name       TEXT NOT NULL DEFAULT '',
    wallet_type         TEXT NOT NULL DEFAULT 'جيب',
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    deleted_at          TIMESTAMPTZ
);
CREATE INDEX idx_custmap_account_uid ON customer_mappings(account_id, customer_unique_id);

CREATE TABLE generated_mikrotik_cards (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    local_id    INT,
    account_id  UUID NOT NULL REFERENCES accounts(id) ON DELETE CASCADE,
    category    INT NOT NULL,
    pin         TEXT NOT NULL,
    username    TEXT NOT NULL DEFAULT '',
    password    TEXT NOT NULL DEFAULT '',
    printed     BOOLEAN NOT NULL DEFAULT false,
    transferred BOOLEAN NOT NULL DEFAULT false,
    created_at  TIMESTAMPTZ NOT NULL,
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    deleted_at  TIMESTAMPTZ
);
CREATE INDEX idx_mikrotik_account ON generated_mikrotik_cards(account_id);

CREATE TABLE distributor_customers (
    id              TEXT PRIMARY KEY,
    account_id      UUID NOT NULL REFERENCES accounts(id) ON DELETE CASCADE,
    name            TEXT NOT NULL,
    total_sales     NUMERIC(14,2) NOT NULL DEFAULT 0,
    total_payments  NUMERIC(14,2) NOT NULL DEFAULT 0,
    current_balance NUMERIC(14,2) NOT NULL DEFAULT 0,
    created_at      TIMESTAMPTZ NOT NULL,
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    deleted_at      TIMESTAMPTZ
);
CREATE INDEX idx_distcust_account ON distributor_customers(account_id);

CREATE TABLE distributor_transactions (
    id          TEXT PRIMARY KEY,
    account_id  UUID NOT NULL REFERENCES accounts(id) ON DELETE CASCADE,
    customer_id TEXT NOT NULL,
    date        TIMESTAMPTZ NOT NULL,
    type        TEXT NOT NULL,
    amount      NUMERIC(14,2) NOT NULL,
    notes       TEXT NOT NULL DEFAULT '',
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    deleted_at  TIMESTAMPTZ
);
CREATE INDEX idx_disttx_account_customer ON distributor_transactions(account_id, customer_id);

CREATE TABLE distributor_expenses (
    id          TEXT PRIMARY KEY,
    account_id  UUID NOT NULL REFERENCES accounts(id) ON DELETE CASCADE,
    category    TEXT NOT NULL,
    amount      NUMERIC(14,2) NOT NULL,
    description TEXT NOT NULL DEFAULT '',
    date        TIMESTAMPTZ NOT NULL,
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    deleted_at  TIMESTAMPTZ
);
CREATE INDEX idx_distexp_account ON distributor_expenses(account_id);

CREATE TABLE distributor_capitals (
    id          TEXT PRIMARY KEY,
    account_id  UUID NOT NULL REFERENCES accounts(id) ON DELETE CASCADE,
    type        TEXT NOT NULL,
    amount      NUMERIC(14,2) NOT NULL,
    description TEXT NOT NULL DEFAULT '',
    date        TIMESTAMPTZ NOT NULL,
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    deleted_at  TIMESTAMPTZ
);
CREATE INDEX idx_distcap_account ON distributor_capitals(account_id);
