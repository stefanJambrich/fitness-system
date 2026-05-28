-- DEFINE ENUMS
CREATE TYPE reservation_status AS ENUM ('RESERVED', 'ATTENDED', 'CANCELLED', 'NO_SHOW');
CREATE TYPE transaction_action AS ENUM ('RECHARGE', 'DEDUCT', 'REFUND');

-- CREATE TABLES
CREATE TABLE roles (
                       id SERIAL PRIMARY KEY,
                       name VARCHAR(50) NOT NULL UNIQUE,
                       description VARCHAR(255)
);

CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       fullname VARCHAR(255) NOT NULL,
                       unique_code VARCHAR(50) UNIQUE,
                       available_credits INT NOT NULL DEFAULT 0,
                       created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE user_roles (
                            user_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                            role_id INT NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
                            PRIMARY KEY (user_id, role_id)
);

CREATE TABLE otp_codes (
                           id SERIAL PRIMARY KEY,
                           email VARCHAR(255) NOT NULL,
                           code_hash VARCHAR(255) NOT NULL,
                           expires_at TIMESTAMPTZ NOT NULL,
                           is_used BOOLEAN NOT NULL DEFAULT FALSE,
                           created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE devices (
                         id SERIAL PRIMARY KEY,
                         user_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                         public_key_hash VARCHAR(500) NOT NULL UNIQUE,
                         created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                         last_used_at TIMESTAMPTZ
);

CREATE TABLE training_groups (
                                 id SERIAL PRIMARY KEY,
                                 trainer_user_id INT NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
                                 name VARCHAR(255) NOT NULL,
                                 invite_code VARCHAR(100) NOT NULL UNIQUE,
                                 created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE group_members (
                               group_id INT NOT NULL REFERENCES training_groups(id) ON DELETE CASCADE,
                               user_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                               PRIMARY KEY (group_id, user_id)
);

CREATE TABLE training_sessions (
                                   id SERIAL PRIMARY KEY,
                                   group_id INT NOT NULL REFERENCES training_groups(id) ON DELETE CASCADE,
                                   scheduled_at TIMESTAMPTZ NOT NULL,
                                   created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE reservations (
                              id SERIAL PRIMARY KEY,
                              session_id INT NOT NULL REFERENCES training_sessions(id) ON DELETE CASCADE,
                              user_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                              status reservation_status NOT NULL DEFAULT 'RESERVED',
                              created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                              UNIQUE (session_id, user_id)
);

CREATE TABLE credit_transactions (
                                     id SERIAL PRIMARY KEY,
                                     target_user_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                                     actor_user_id INT REFERENCES users(id) ON DELETE SET NULL,
                                     amount INT NOT NULL,
                                     action transaction_action NOT NULL,
                                     created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

-- INSERT DEFAULT ROLES INTO ROLES TABLE
INSERT INTO roles (name, description) VALUES
                                          ('MEMBER', 'Běžný cvičenec, který se hlásí na tréninky'),
                                          ('TRAINER', 'Trenér, zakládá tréninky a spravuje kredity');