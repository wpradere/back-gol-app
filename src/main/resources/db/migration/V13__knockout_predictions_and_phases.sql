-- Per-phase published flags
ALTER TABLE knockout_config
    ADD COLUMN IF NOT EXISTS r16_published  BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS r8_published   BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS r4_published   BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS semi_published BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS final_published BOOLEAN NOT NULL DEFAULT FALSE;

-- Predictions for knockout matches
CREATE TABLE IF NOT EXISTS knockout_predictions (
    id                 BIGSERIAL PRIMARY KEY,
    user_id            BIGINT  NOT NULL REFERENCES users(id),
    match_id           INTEGER NOT NULL REFERENCES knockout_matches(id),
    predicted_score_a  INTEGER NOT NULL,
    predicted_score_b  INTEGER NOT NULL,
    points             INTEGER,
    CONSTRAINT uq_ko_pred UNIQUE (user_id, match_id)
);
