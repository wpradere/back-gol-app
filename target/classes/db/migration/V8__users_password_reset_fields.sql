-- Campos para el flujo de reset de contraseña y force-reset por admin
-- IF NOT EXISTS evita el error si la BD de desarrollo ya los tiene
ALTER TABLE users ADD COLUMN IF NOT EXISTS force_password_reset BOOLEAN      NOT NULL DEFAULT FALSE;
ALTER TABLE users ADD COLUMN IF NOT EXISTS reset_token          VARCHAR(255);
ALTER TABLE users ADD COLUMN IF NOT EXISTS reset_code           VARCHAR(10);
ALTER TABLE users ADD COLUMN IF NOT EXISTS reset_token_expiry   TIMESTAMP;
