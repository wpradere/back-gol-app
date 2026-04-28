-- Los partidos de fase eliminatoria no tienen equipos hasta que se definen los clasificados
ALTER TABLE matches ALTER COLUMN team_a_id DROP NOT NULL;
ALTER TABLE matches ALTER COLUMN team_b_id DROP NOT NULL;
