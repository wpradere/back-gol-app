-- ── teams ──────────────────────────────────────────────────────────────────
ALTER TABLE teams ADD COLUMN tournament_id BIGINT REFERENCES tournaments(id);

-- Todos los equipos existentes pertenecen al Mundial 2026 (id = 1)
UPDATE teams SET tournament_id = 1;

ALTER TABLE teams ALTER COLUMN tournament_id SET NOT NULL;

-- El nombre es único dentro de un torneo (no globalmente)
ALTER TABLE teams DROP CONSTRAINT IF EXISTS teams_name_key;
ALTER TABLE teams ADD CONSTRAINT uq_team_name_tournament UNIQUE (name, tournament_id);

-- ── matches ─────────────────────────────────────────────────────────────────
ALTER TABLE matches ADD COLUMN tournament_id BIGINT REFERENCES tournaments(id);

UPDATE matches SET tournament_id = 1;

ALTER TABLE matches ALTER COLUMN tournament_id SET NOT NULL;
