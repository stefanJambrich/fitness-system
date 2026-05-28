-- Upgrade sequences created by SERIAL columns to BIGINT
ALTER SEQUENCE roles_id_seq AS BIGINT;
ALTER SEQUENCE users_id_seq AS BIGINT;
ALTER SEQUENCE otp_codes_id_seq AS BIGINT;
ALTER SEQUENCE devices_id_seq AS BIGINT;
ALTER SEQUENCE training_groups_id_seq AS BIGINT;
ALTER SEQUENCE training_sessions_id_seq AS BIGINT;
ALTER SEQUENCE reservations_id_seq AS BIGINT;
ALTER SEQUENCE credit_transactions_id_seq AS BIGINT;

-- Drop foreign keys so referenced columns can be altered safely
ALTER TABLE user_roles DROP CONSTRAINT IF EXISTS user_roles_user_id_fkey;
ALTER TABLE user_roles DROP CONSTRAINT IF EXISTS user_roles_role_id_fkey;
ALTER TABLE devices DROP CONSTRAINT IF EXISTS devices_user_id_fkey;
ALTER TABLE training_groups DROP CONSTRAINT IF EXISTS training_groups_trainer_user_id_fkey;
ALTER TABLE group_members DROP CONSTRAINT IF EXISTS group_members_group_id_fkey;
ALTER TABLE group_members DROP CONSTRAINT IF EXISTS group_members_user_id_fkey;
ALTER TABLE training_sessions DROP CONSTRAINT IF EXISTS training_sessions_group_id_fkey;
ALTER TABLE reservations DROP CONSTRAINT IF EXISTS reservations_session_id_fkey;
ALTER TABLE reservations DROP CONSTRAINT IF EXISTS reservations_user_id_fkey;
ALTER TABLE credit_transactions DROP CONSTRAINT IF EXISTS credit_transactions_target_user_id_fkey;
ALTER TABLE credit_transactions DROP CONSTRAINT IF EXISTS credit_transactions_actor_user_id_fkey;

-- Upgrade primary key columns to BIGINT
ALTER TABLE roles ALTER COLUMN id TYPE BIGINT USING id::BIGINT;
ALTER TABLE users ALTER COLUMN id TYPE BIGINT USING id::BIGINT;
ALTER TABLE otp_codes ALTER COLUMN id TYPE BIGINT USING id::BIGINT;
ALTER TABLE devices ALTER COLUMN id TYPE BIGINT USING id::BIGINT;
ALTER TABLE training_groups ALTER COLUMN id TYPE BIGINT USING id::BIGINT;
ALTER TABLE training_sessions ALTER COLUMN id TYPE BIGINT USING id::BIGINT;
ALTER TABLE reservations ALTER COLUMN id TYPE BIGINT USING id::BIGINT;
ALTER TABLE credit_transactions ALTER COLUMN id TYPE BIGINT USING id::BIGINT;

-- Upgrade foreign key columns to BIGINT
ALTER TABLE user_roles ALTER COLUMN user_id TYPE BIGINT USING user_id::BIGINT;
ALTER TABLE user_roles ALTER COLUMN role_id TYPE BIGINT USING role_id::BIGINT;
ALTER TABLE devices ALTER COLUMN user_id TYPE BIGINT USING user_id::BIGINT;
ALTER TABLE training_groups ALTER COLUMN trainer_user_id TYPE BIGINT USING trainer_user_id::BIGINT;
ALTER TABLE group_members ALTER COLUMN group_id TYPE BIGINT USING group_id::BIGINT;
ALTER TABLE group_members ALTER COLUMN user_id TYPE BIGINT USING user_id::BIGINT;
ALTER TABLE training_sessions ALTER COLUMN group_id TYPE BIGINT USING group_id::BIGINT;
ALTER TABLE reservations ALTER COLUMN session_id TYPE BIGINT USING session_id::BIGINT;
ALTER TABLE reservations ALTER COLUMN user_id TYPE BIGINT USING user_id::BIGINT;
ALTER TABLE credit_transactions ALTER COLUMN target_user_id TYPE BIGINT USING target_user_id::BIGINT;
ALTER TABLE credit_transactions ALTER COLUMN actor_user_id TYPE BIGINT USING actor_user_id::BIGINT;

-- Recreate foreign keys with BIGINT columns
ALTER TABLE user_roles
    ADD CONSTRAINT user_roles_user_id_fkey FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;
ALTER TABLE user_roles
    ADD CONSTRAINT user_roles_role_id_fkey FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE;
ALTER TABLE devices
    ADD CONSTRAINT devices_user_id_fkey FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;
ALTER TABLE training_groups
    ADD CONSTRAINT training_groups_trainer_user_id_fkey FOREIGN KEY (trainer_user_id) REFERENCES users(id) ON DELETE RESTRICT;
ALTER TABLE group_members
    ADD CONSTRAINT group_members_group_id_fkey FOREIGN KEY (group_id) REFERENCES training_groups(id) ON DELETE CASCADE;
ALTER TABLE group_members
    ADD CONSTRAINT group_members_user_id_fkey FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;
ALTER TABLE training_sessions
    ADD CONSTRAINT training_sessions_group_id_fkey FOREIGN KEY (group_id) REFERENCES training_groups(id) ON DELETE CASCADE;
ALTER TABLE reservations
    ADD CONSTRAINT reservations_session_id_fkey FOREIGN KEY (session_id) REFERENCES training_sessions(id) ON DELETE CASCADE;
ALTER TABLE reservations
    ADD CONSTRAINT reservations_user_id_fkey FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;
ALTER TABLE credit_transactions
    ADD CONSTRAINT credit_transactions_target_user_id_fkey FOREIGN KEY (target_user_id) REFERENCES users(id) ON DELETE CASCADE;
ALTER TABLE credit_transactions
    ADD CONSTRAINT credit_transactions_actor_user_id_fkey FOREIGN KEY (actor_user_id) REFERENCES users(id) ON DELETE SET NULL;

