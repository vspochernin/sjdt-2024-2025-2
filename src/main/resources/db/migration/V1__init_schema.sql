-- Создание таблицы пользователей.
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    email VARCHAR(255),
    telegram VARCHAR(255),
    phone VARCHAR(20)
);

-- Создание таблицы OTP кодов.
CREATE TABLE otp_codes (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL,
    user_id BIGINT NOT NULL REFERENCES users(id),
    operation_id VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL
);

-- Создание таблицы конфигурации OTP.
CREATE TABLE otp_config (
    id BIGSERIAL PRIMARY KEY,
    code_length INTEGER NOT NULL,
    expiration_time_seconds INTEGER NOT NULL
);

-- Инициализация конфигурации OTP.
INSERT INTO otp_config (code_length, expiration_time_seconds) VALUES (6, 300);