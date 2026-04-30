-- Corrige el typo en el nombre del torneo si ya existe con short_name 'champleage'
UPDATE tournaments
SET name = 'UEFA Champions League'
WHERE short_name = 'champleage'
  AND name       = 'UEFA Champions Leage';

-- Inserta el torneo solo si no existe todavía (BD limpia)
INSERT INTO tournaments (name, short_name, icon, season, enabled, sort_order)
SELECT 'UEFA Champions League', 'champleage', '⭐', '2026', true, 4
WHERE NOT EXISTS (
    SELECT 1 FROM tournaments WHERE short_name = 'champleage'
);
