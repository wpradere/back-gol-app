CREATE TABLE tournaments (
    id         BIGSERIAL    PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    short_name VARCHAR(30)  NOT NULL,
    icon       VARCHAR(10),
    season     VARCHAR(20),
    enabled    BOOLEAN      NOT NULL DEFAULT FALSE,
    sort_order INT          NOT NULL DEFAULT 0
);

INSERT INTO tournaments (name, short_name, icon, season, enabled, sort_order) VALUES
    ('FIFA World Cup 2026',   'Mundial 2026', '🌍', '2026',    TRUE,  1),
    ('Liga BetPlay Dimayor',  'Liga Colombia','🇨🇴', '2025',    FALSE, 2),
    ('UEFA Champions League', 'Champions',   '⭐', '2024-25', FALSE, 3);
