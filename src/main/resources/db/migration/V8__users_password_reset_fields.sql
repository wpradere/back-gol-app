-- Campos para el flujo de reset de contraseña y force-reset por admin
ALTER TABLE users
    ADD COLUMN force_password_reset BOOLEAN      NOT NULL DEFAULT FALSE,
    ADD COLUMN reset_token          VARCHAR(255),
    ADD COLUMN reset_code           VARCHAR(10),
    ADD COLUMN reset_token_expiry   TIMESTAMP;
