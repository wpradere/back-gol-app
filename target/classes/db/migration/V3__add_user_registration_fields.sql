ALTER TABLE users
    ADD COLUMN email                      VARCHAR(255),
    ADD COLUMN status                     VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    ADD COLUMN verification_token         VARCHAR(255),
    ADD COLUMN verification_token_expiry  TIMESTAMP;

CREATE UNIQUE INDEX uq_users_email ON users (email) WHERE email IS NOT NULL;
