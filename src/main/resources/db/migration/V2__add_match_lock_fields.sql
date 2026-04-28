ALTER TABLE matches
    ADD COLUMN predictions_locked BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN kickoff_at         TIMESTAMP;
