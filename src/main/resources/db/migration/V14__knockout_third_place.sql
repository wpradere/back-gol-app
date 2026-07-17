-- Partido por el Tercer y Cuarto Puesto
INSERT INTO knockout_matches (id, round, sort_order, next_match_id, next_slot)
VALUES (32, '3RD', 1, NULL, NULL);

-- Columnas para avanzar al PERDEDOR al partido de 3er puesto
ALTER TABLE knockout_matches
    ADD COLUMN IF NOT EXISTS loser_next_match_id INTEGER,
    ADD COLUMN IF NOT EXISTS loser_next_slot      CHAR(1);

-- Los perdedores de cada semifinal van al partido #32
UPDATE knockout_matches SET loser_next_match_id = 32, loser_next_slot = 'A' WHERE id = 29;
UPDATE knockout_matches SET loser_next_match_id = 32, loser_next_slot = 'B' WHERE id = 30;

-- Flag de publicación para la fase de 3er puesto
ALTER TABLE knockout_config
    ADD COLUMN IF NOT EXISTS third_published BOOLEAN NOT NULL DEFAULT FALSE;
