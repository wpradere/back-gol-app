-- users
CREATE TABLE users (
    id         BIGSERIAL    PRIMARY KEY,
    username   VARCHAR(50)  NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    role       VARCHAR(20)  NOT NULL DEFAULT 'USER',
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- teams
CREATE TABLE teams (
    id            BIGSERIAL    PRIMARY KEY,
    name          VARCHAR(100) NOT NULL UNIQUE,
    flag          VARCHAR(10),
    confederation VARCHAR(20),
    group_name    VARCHAR(2),
    played        INT DEFAULT 0,
    won           INT DEFAULT 0,
    drawn         INT DEFAULT 0,
    lost          INT DEFAULT 0,
    goals_for     INT DEFAULT 0,
    goals_against INT DEFAULT 0
);

-- players
CREATE TABLE players (
    id         BIGSERIAL    PRIMARY KEY,
    name       VARCHAR(100),
    number     INT,
    position   VARCHAR(3),
    age        INT,
    club       VARCHAR(100),
    games      INT     DEFAULT 0,
    goals      INT     DEFAULT 0,
    assists    INT     DEFAULT 0,
    is_starter BOOLEAN DEFAULT FALSE,
    team_id    BIGINT  NOT NULL REFERENCES teams(id)
);

-- scorers
CREATE TABLE scorers (
    id      BIGSERIAL   PRIMARY KEY,
    name    VARCHAR(100),
    goals   INT DEFAULT 0,
    team_id BIGINT NOT NULL REFERENCES teams(id)
);

-- matches
CREATE TABLE matches (
    id         BIGSERIAL    PRIMARY KEY,
    group_name VARCHAR(2),
    match_date VARCHAR(30),
    stadium    VARCHAR(100),
    team_a_id  BIGINT  NOT NULL REFERENCES teams(id),
    team_b_id  BIGINT  NOT NULL REFERENCES teams(id),
    played     BOOLEAN DEFAULT FALSE,
    score_a    INT,
    score_b    INT
);

-- predictions
CREATE TABLE predictions (
    id                BIGSERIAL PRIMARY KEY,
    user_id           BIGINT NOT NULL REFERENCES users(id),
    match_id          BIGINT NOT NULL REFERENCES matches(id),
    predicted_score_a INT DEFAULT 0,
    predicted_score_b INT DEFAULT 0,
    points            INT,
    updated_at        TIMESTAMP,
    CONSTRAINT uq_prediction_user_match UNIQUE (user_id, match_id)
);
