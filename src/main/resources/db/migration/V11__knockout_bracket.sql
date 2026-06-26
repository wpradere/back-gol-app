CREATE TABLE knockout_config (
    id   BIGINT  NOT NULL PRIMARY KEY,
    enabled   BOOLEAN NOT NULL DEFAULT FALSE,
    published BOOLEAN NOT NULL DEFAULT FALSE
);

INSERT INTO knockout_config (id, enabled, published) VALUES (1, FALSE, FALSE);

CREATE TABLE knockout_matches (
    id           INTEGER NOT NULL PRIMARY KEY,
    round        VARCHAR(10) NOT NULL,
    sort_order   INTEGER NOT NULL,
    team_a_id    BIGINT REFERENCES teams(id),
    team_b_id    BIGINT REFERENCES teams(id),
    score_a      INTEGER,
    score_b      INTEGER,
    winner_id    BIGINT REFERENCES teams(id),
    played       BOOLEAN NOT NULL DEFAULT FALSE,
    next_match_id INTEGER,
    next_slot    CHAR(1)
);

-- FINAL (sin siguiente)
INSERT INTO knockout_matches (id, round, sort_order, next_match_id, next_slot) VALUES
(31, 'FINAL', 1, NULL, NULL);

-- Semifinales
INSERT INTO knockout_matches (id, round, sort_order, next_match_id, next_slot) VALUES
(29, 'SEMI', 1, 31, 'A'),
(30, 'SEMI', 2, 31, 'B');

-- 4tos de final
INSERT INTO knockout_matches (id, round, sort_order, next_match_id, next_slot) VALUES
(25, 'R4', 1, 29, 'A'),
(26, 'R4', 2, 29, 'B'),
(27, 'R4', 3, 30, 'A'),
(28, 'R4', 4, 30, 'B');

-- 8avos de final
INSERT INTO knockout_matches (id, round, sort_order, next_match_id, next_slot) VALUES
(17, 'R8', 1, 25, 'A'),
(18, 'R8', 2, 25, 'B'),
(19, 'R8', 3, 26, 'A'),
(20, 'R8', 4, 26, 'B'),
(21, 'R8', 5, 27, 'A'),
(22, 'R8', 6, 27, 'B'),
(23, 'R8', 7, 28, 'A'),
(24, 'R8', 8, 28, 'B');

-- 16avos de final (lado izquierdo)
INSERT INTO knockout_matches (id, round, sort_order, next_match_id, next_slot) VALUES
(1,  'R16', 1,  17, 'A'),
(2,  'R16', 2,  17, 'B'),
(3,  'R16', 3,  18, 'A'),
(4,  'R16', 4,  18, 'B'),
(5,  'R16', 5,  19, 'A'),
(6,  'R16', 6,  19, 'B'),
(7,  'R16', 7,  20, 'A'),
(8,  'R16', 8,  20, 'B');

-- 16avos de final (lado derecho)
INSERT INTO knockout_matches (id, round, sort_order, next_match_id, next_slot) VALUES
(9,  'R16', 9,  21, 'A'),
(10, 'R16', 10, 21, 'B'),
(11, 'R16', 11, 22, 'A'),
(12, 'R16', 12, 22, 'B'),
(13, 'R16', 13, 23, 'A'),
(14, 'R16', 14, 23, 'B'),
(15, 'R16', 15, 24, 'A'),
(16, 'R16', 16, 24, 'B');
