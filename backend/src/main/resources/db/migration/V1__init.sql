CREATE EXTENSION IF NOT EXISTS citext;   -- case-insensitive e-mails

-- ---------------------------------------------------------------------
-- Enumerations
-- ---------------------------------------------------------------------
CREATE TYPE otp_purpose        AS ENUM ('trainer_login', 'member_email_verify', 'member_recovery');
CREATE TYPE group_trainer_role AS ENUM ('owner', 'co_trainer');
CREATE TYPE membership_end     AS ENUM ('left', 'removed');
CREATE TYPE training_status    AS ENUM ('scheduled', 'cancelled', 'closed');
CREATE TYPE booking_status     AS ENUM ('booked', 'cancelled', 'attended', 'no_show', 'released');
CREATE TYPE booking_source     AS ENUM ('member', 'walk_in');
CREATE TYPE cancel_reason      AS ENUM ('member', 'trainer', 'training_cancelled', 'member_left', 'member_removed');
CREATE TYPE credit_tx_type     AS ENUM ('top_up', 'attendance', 'no_show', 'correction');

-- =====================================================================
-- 1. Member identity (UC-01 … UC-09)
-- =====================================================================

-- A member has no account, only a nickname + short code (nickname#code)
-- and one or more device keys. E-mail is optional, used only for recovery.
CREATE TABLE member (
    id                  uuid        PRIMARY KEY DEFAULT gen_random_uuid(),
    nickname            varchar(32) NOT NULL,
    code                char(4)     NOT NULL,
    recovery_email      citext,
    recovery_email_verified_at timestamptz,
    created_at          timestamptz NOT NULL DEFAULT now(),
    deleted_at          timestamptz,
    CONSTRAINT member_code_format CHECK (code ~ '^[A-Z0-9]{4}$'),
    CONSTRAINT member_identifier_unique UNIQUE (nickname, code),
    CONSTRAINT member_email_verified_needs_email
        CHECK (recovery_email_verified_at IS NULL OR recovery_email IS NOT NULL)
);
CREATE UNIQUE INDEX member_recovery_email_uq
    ON member (recovery_email) WHERE recovery_email IS NOT NULL AND deleted_at IS NULL;

-- One row per device (key pair). The private key never leaves the device.
CREATE TABLE member_device (
    id                  uuid        PRIMARY KEY DEFAULT gen_random_uuid(),
    member_id           uuid        NOT NULL REFERENCES member(id) ON DELETE CASCADE,
    public_key          bytea       NOT NULL,           -- SPKI-encoded public key (ECDSA P-256 / Ed25519)
    public_key_hash     bytea       NOT NULL UNIQUE,    -- SHA-256(public_key), lookup key sent with each request (UC-04)
    algorithm           varchar(16) NOT NULL DEFAULT 'ECDSA-P256',
    label               varchar(64),                    -- "iPhone", "Chrome – work profile" (UC-08)
    created_at          timestamptz NOT NULL DEFAULT now(),
    last_seen_at        timestamptz,
    revoked_at          timestamptz,                    -- lost device (UC-08)
    CONSTRAINT member_device_hash_len CHECK (octet_length(public_key_hash) = 32)
);
CREATE INDEX member_device_member_idx ON member_device (member_id);

-- Replay protection for signed requests (UC-04). Rows expire (UC-51).
CREATE TABLE request_nonce (
    device_id           uuid        NOT NULL REFERENCES member_device(id) ON DELETE CASCADE,
    nonce               bytea       NOT NULL,
    expires_at          timestamptz NOT NULL,
    PRIMARY KEY (device_id, nonce)
);
CREATE INDEX request_nonce_expires_idx ON request_nonce (expires_at);

-- Short-lived pairing token for device-to-device transfer via QR (UC-07).
CREATE TABLE device_pairing (
    id                  uuid        PRIMARY KEY DEFAULT gen_random_uuid(),
    member_id           uuid        NOT NULL REFERENCES member(id) ON DELETE CASCADE,
    token_hash          bytea       NOT NULL UNIQUE,    -- hash of the secret shown in the QR code
    created_by_device_id uuid       NOT NULL REFERENCES member_device(id) ON DELETE CASCADE,
    created_at          timestamptz NOT NULL DEFAULT now(),
    expires_at          timestamptz NOT NULL,
    consumed_at         timestamptz,
    consumed_by_device_id uuid      REFERENCES member_device(id) ON DELETE SET NULL
);

-- =====================================================================
-- 2. Trainer account (UC-20 … UC-22)
-- =====================================================================

CREATE TABLE trainer (
    id                  uuid        PRIMARY KEY DEFAULT gen_random_uuid(),
    email               citext      NOT NULL UNIQUE,
    display_name        varchar(64) NOT NULL,
    email_verified_at   timestamptz,
    created_at          timestamptz NOT NULL DEFAULT now(),
    deactivated_at      timestamptz
);

-- A login on one device. "Remember this device" = long expires_at (UC-21, UC-22).
CREATE TABLE trainer_session (
    id                  uuid        PRIMARY KEY DEFAULT gen_random_uuid(),
    trainer_id          uuid        NOT NULL REFERENCES trainer(id) ON DELETE CASCADE,
    token_hash          bytea       NOT NULL UNIQUE,    -- hash of the opaque token stored on the device
    remembered          boolean     NOT NULL DEFAULT false,
    device_label        varchar(128),                   -- from User-Agent, shown in device list
    created_at          timestamptz NOT NULL DEFAULT now(),
    last_used_at        timestamptz NOT NULL DEFAULT now(),
    expires_at          timestamptz NOT NULL,
    revoked_at          timestamptz
);
CREATE INDEX trainer_session_trainer_idx ON trainer_session (trainer_id);

-- One-time codes sent by e-mail: trainer login, member e-mail verification and recovery.
CREATE TABLE one_time_code (
    id                  uuid        PRIMARY KEY DEFAULT gen_random_uuid(),
    purpose             otp_purpose NOT NULL,
    email               citext      NOT NULL,
    code_hash           bytea       NOT NULL,           -- never store the code in plain text
    trainer_id          uuid        REFERENCES trainer(id) ON DELETE CASCADE,
    member_id           uuid        REFERENCES member(id)  ON DELETE CASCADE,
    attempts            smallint    NOT NULL DEFAULT 0, -- brute-force limit
    created_at          timestamptz NOT NULL DEFAULT now(),
    expires_at          timestamptz NOT NULL,
    consumed_at         timestamptz,
    CONSTRAINT otp_owner CHECK (
        (purpose = 'trainer_login' AND member_id IS NULL) OR
        (purpose <> 'trainer_login' AND trainer_id IS NULL AND member_id IS NOT NULL))
);
CREATE INDEX one_time_code_lookup_idx ON one_time_code (email, purpose) WHERE consumed_at IS NULL;
CREATE INDEX one_time_code_expires_idx ON one_time_code (expires_at);

-- =====================================================================
-- 3. Groups and memberships (UC-10 … UC-12, UC-23 … UC-26)
-- =====================================================================

CREATE TABLE training_group (                         -- "group" is a reserved word
    id                  uuid        PRIMARY KEY DEFAULT gen_random_uuid(),
    name                varchar(80) NOT NULL,
    description         text,
    invite_code         varchar(12) NOT NULL UNIQUE,  -- link / QR / typed code (UC-24); regenerate = overwrite
    invite_code_rotated_at timestamptz NOT NULL DEFAULT now(),
    timezone            varchar(64) NOT NULL DEFAULT 'Europe/Prague',
    default_cancel_hours smallint   NOT NULL DEFAULT 0 CHECK (default_cancel_hours >= 0), -- open question 3
    created_at          timestamptz NOT NULL DEFAULT now(),
    deleted_at          timestamptz
);

-- Trainers of a group; exactly one owner, any number of co-trainers (UC-26).
CREATE TABLE group_trainer (
    group_id            uuid        NOT NULL REFERENCES training_group(id) ON DELETE CASCADE,
    trainer_id          uuid        NOT NULL REFERENCES trainer(id)        ON DELETE CASCADE,
    role                group_trainer_role NOT NULL,
    added_at            timestamptz NOT NULL DEFAULT now(),
    PRIMARY KEY (group_id, trainer_id)
);
CREATE UNIQUE INDEX group_trainer_one_owner ON group_trainer (group_id) WHERE role = 'owner';
CREATE INDEX group_trainer_trainer_idx ON group_trainer (trainer_id);

-- Member in a group. Owns the credit balance. Soft-deleted on leave/removal,
-- reactivated on rejoin (same row => same ledger => same balance).
CREATE TABLE group_membership (
    id                  uuid        PRIMARY KEY DEFAULT gen_random_uuid(),
    group_id            uuid        NOT NULL REFERENCES training_group(id) ON DELETE CASCADE,
    member_id           uuid        NOT NULL REFERENCES member(id)         ON DELETE CASCADE,
    joined_at           timestamptz NOT NULL DEFAULT now(),   -- first join
    rejoined_at         timestamptz,                          -- last reactivation
    left_at             timestamptz,                          -- NULL = active
    end_reason          membership_end,
    removed_by_trainer_id uuid      REFERENCES trainer(id) ON DELETE SET NULL,
    CONSTRAINT membership_unique UNIQUE (group_id, member_id),
    CONSTRAINT membership_end_consistent CHECK (
        (left_at IS NULL AND end_reason IS NULL AND removed_by_trainer_id IS NULL) OR
        (left_at IS NOT NULL AND end_reason = 'left'    AND removed_by_trainer_id IS NULL) OR
        (left_at IS NOT NULL AND end_reason = 'removed'))
);
CREATE INDEX group_membership_member_idx ON group_membership (member_id);
CREATE INDEX group_membership_active_idx ON group_membership (group_id) WHERE left_at IS NULL;

-- =====================================================================
-- 4. Trainings (UC-13, UC-27 … UC-29, UC-50)
-- =====================================================================

-- Recurring rule, e.g. every Tuesday 18:00 for 90 min (UC-28).
CREATE TABLE training_series (
    id                  uuid        PRIMARY KEY DEFAULT gen_random_uuid(),
    group_id            uuid        NOT NULL REFERENCES training_group(id) ON DELETE CASCADE,
    title               varchar(80) NOT NULL,
    location            varchar(160),
    weekday             smallint    NOT NULL CHECK (weekday BETWEEN 1 AND 7), -- ISO: 1 = Monday
    start_time          time        NOT NULL,             -- local time in group timezone
    duration_minutes    smallint    NOT NULL CHECK (duration_minutes > 0),
    capacity            smallint    CHECK (capacity > 0), -- NULL = unlimited
    valid_from          date        NOT NULL,
    valid_until         date,
    generated_until     date,                             -- how far UC-50 has generated
    created_by_trainer_id uuid      REFERENCES trainer(id) ON DELETE SET NULL,
    created_at          timestamptz NOT NULL DEFAULT now(),
    ended_at            timestamptz,                      -- series stopped
    CHECK (valid_until IS NULL OR valid_until >= valid_from)
);
CREATE INDEX training_series_group_idx ON training_series (group_id);

-- One concrete training. Copied from a series or created one-off (UC-27).
CREATE TABLE training (
    id                  uuid        PRIMARY KEY DEFAULT gen_random_uuid(),
    group_id            uuid        NOT NULL REFERENCES training_group(id) ON DELETE CASCADE,
    series_id           uuid        REFERENCES training_series(id) ON DELETE SET NULL,
    title               varchar(80) NOT NULL,
    location            varchar(160),
    starts_at           timestamptz NOT NULL,
    ends_at             timestamptz NOT NULL,
    capacity            smallint    CHECK (capacity > 0),     -- NULL = unlimited
    cancel_deadline     timestamptz,                          -- last moment a member can cancel (UC-15)
    status              training_status NOT NULL DEFAULT 'scheduled',
    cancelled_at        timestamptz,
    closed_at           timestamptz,                          -- UC-52
    created_by_trainer_id uuid      REFERENCES trainer(id) ON DELETE SET NULL,
    created_at          timestamptz NOT NULL DEFAULT now(),
    CHECK (ends_at > starts_at),
    CHECK (cancel_deadline IS NULL OR cancel_deadline <= starts_at),
    CHECK ((status = 'cancelled') = (cancelled_at IS NOT NULL)),
    CHECK ((status = 'closed')    = (closed_at    IS NOT NULL)),
    CONSTRAINT training_series_occurrence UNIQUE (series_id, starts_at) -- UC-50 idempotent
);
CREATE INDEX training_group_time_idx ON training (group_id, starts_at);
CREATE INDEX training_open_idx ON training (starts_at) WHERE status = 'scheduled';

-- =====================================================================
-- 5. Bookings (UC-14 … UC-16, UC-30 … UC-32, UC-52)
-- =====================================================================

-- A 'booked' row blocks one credit in the membership's group.
-- attended / no_show turn the block into a ledger deduction.
-- cancelled / released give the credit back (nothing to write in the ledger).
CREATE TABLE booking (
    id                  uuid        PRIMARY KEY DEFAULT gen_random_uuid(),
    training_id         uuid        NOT NULL REFERENCES training(id)         ON DELETE CASCADE,
    membership_id       uuid        NOT NULL REFERENCES group_membership(id) ON DELETE CASCADE,
    status              booking_status NOT NULL DEFAULT 'booked',
    source              booking_source NOT NULL DEFAULT 'member',  -- walk_in = UC-32
    created_at          timestamptz NOT NULL DEFAULT now(),
    signed_by_device_id uuid        REFERENCES member_device(id) ON DELETE SET NULL, -- proof of who signed up
    cancelled_at        timestamptz,
    cancel_reason       cancel_reason,
    resolved_at         timestamptz,                               -- attended / no_show / released
    resolved_by_trainer_id uuid     REFERENCES trainer(id) ON DELETE SET NULL,  -- NULL = system (UC-52)
    CHECK ((status = 'cancelled') = (cancelled_at IS NOT NULL AND cancel_reason IS NOT NULL)),
    CHECK ((status IN ('attended', 'no_show', 'released')) = (resolved_at IS NOT NULL)),
    CHECK (source = 'member' OR status <> 'booked')               -- walk-in is created as attended
);
-- One live booking per member per training; a cancelled one can be followed by a new sign-up.
CREATE UNIQUE INDEX booking_one_live
    ON booking (training_id, membership_id) WHERE status <> 'cancelled';
CREATE INDEX booking_membership_blocked_idx ON booking (membership_id) WHERE status = 'booked';
CREATE INDEX booking_training_idx ON booking (training_id);

-- =====================================================================
-- 6. Credit ledger (UC-17, UC-18, UC-31, UC-34 … UC-37)
-- =====================================================================

-- Append-only. Balance of a membership = SUM(amount). Never updated or deleted;
-- a mistake is fixed by a new 'correction' row (UC-35).
CREATE TABLE credit_transaction (
    id                  bigint      GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    membership_id       uuid        NOT NULL REFERENCES group_membership(id) ON DELETE RESTRICT,
    type                credit_tx_type NOT NULL,
    amount              integer     NOT NULL CHECK (amount <> 0),
    booking_id          uuid        REFERENCES booking(id) ON DELETE RESTRICT,
    trainer_id          uuid        REFERENCES trainer(id) ON DELETE SET NULL, -- who did it; NULL = system
    note                varchar(200),
    created_at          timestamptz NOT NULL DEFAULT now(),
    CHECK (type <> 'top_up'                    OR amount > 0),
    CHECK (type NOT IN ('attendance','no_show') OR (amount < 0 AND booking_id IS NOT NULL)),
    CHECK (type <> 'correction'                OR note IS NOT NULL)
);
-- A booking is charged at most once.
CREATE UNIQUE INDEX credit_tx_one_per_booking
    ON credit_transaction (booking_id) WHERE type IN ('attendance', 'no_show');
CREATE INDEX credit_tx_membership_idx ON credit_transaction (membership_id, created_at);
CREATE INDEX credit_tx_trainer_idx ON credit_transaction (trainer_id, created_at);

CREATE FUNCTION credit_transaction_append_only() RETURNS trigger
LANGUAGE plpgsql AS $$
BEGIN
    RAISE EXCEPTION 'credit_transaction is append-only; add a correction instead';
END $$;

CREATE TRIGGER credit_transaction_no_update_delete
    BEFORE UPDATE OR DELETE ON credit_transaction
    FOR EACH ROW EXECUTE FUNCTION credit_transaction_append_only();

-- Booking and its membership must belong to the same group as the training.
CREATE FUNCTION booking_same_group() RETURNS trigger
LANGUAGE plpgsql AS $$
BEGIN
    IF (SELECT group_id FROM training WHERE id = NEW.training_id)
       IS DISTINCT FROM (SELECT group_id FROM group_membership WHERE id = NEW.membership_id) THEN
        RAISE EXCEPTION 'booking: training and membership belong to different groups';
    END IF;
    RETURN NEW;
END $$;

CREATE TRIGGER booking_same_group_chk
    BEFORE INSERT OR UPDATE OF training_id, membership_id ON booking
    FOR EACH ROW EXECUTE FUNCTION booking_same_group();

-- =====================================================================
-- 7. Views
-- =====================================================================

-- UC-17 / UC-36: balance, blocked and available credits per membership.
CREATE VIEW membership_credit AS
SELECT gm.id                         AS membership_id,
       gm.group_id,
       gm.member_id,
       gm.left_at,
       COALESCE(tx.balance, 0)       AS balance,
       COALESCE(bk.blocked, 0)       AS blocked,
       COALESCE(tx.balance, 0) - COALESCE(bk.blocked, 0) AS available
FROM group_membership gm
LEFT JOIN (SELECT membership_id, SUM(amount)::int AS balance
           FROM credit_transaction GROUP BY membership_id) tx ON tx.membership_id = gm.id
LEFT JOIN (SELECT membership_id, COUNT(*)::int AS blocked
           FROM booking WHERE status = 'booked' GROUP BY membership_id) bk ON bk.membership_id = gm.id;

-- UC-30 / UC-14: how many places are taken on each training.
CREATE VIEW training_occupancy AS
SELECT t.id AS training_id,
       t.capacity,
       COUNT(b.id) FILTER (WHERE b.status IN ('booked', 'attended', 'no_show'))::int AS taken
FROM training t
LEFT JOIN booking b ON b.training_id = t.id
GROUP BY t.id, t.capacity;
