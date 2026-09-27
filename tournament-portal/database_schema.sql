-- ============================================
-- Mobile Gaming Tournament Portal - DB Schema
-- ============================================

CREATE DATABASE IF NOT EXISTS tournament_portal;
USE tournament_portal;

DROP TABLE IF EXISTS match_results;
DROP TABLE IF EXISTS matches;
DROP TABLE IF EXISTS players;
DROP TABLE IF EXISTS squads;
DROP TABLE IF EXISTS admins;

-- Squads (teams) registered by squad leaders
CREATE TABLE squads (
    squad_id INT AUTO_INCREMENT PRIMARY KEY,
    squad_name VARCHAR(50) NOT NULL UNIQUE,
    leader_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    wins INT DEFAULT 0,
    losses INT DEFAULT 0,
    points INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Players belonging to a squad
CREATE TABLE players (
    player_id INT AUTO_INCREMENT PRIMARY KEY,
    squad_id INT NOT NULL,
    player_name VARCHAR(100) NOT NULL,
    in_game_id VARCHAR(50) NOT NULL,
    role VARCHAR(30),
    FOREIGN KEY (squad_id) REFERENCES squads(squad_id) ON DELETE CASCADE
);

-- Scheduled / completed matches between two squads
CREATE TABLE matches (
    match_id INT AUTO_INCREMENT PRIMARY KEY,
    squad1_id INT NOT NULL,
    squad2_id INT NOT NULL,
    match_time DATETIME NOT NULL,
    status ENUM('SCHEDULED','COMPLETED') DEFAULT 'SCHEDULED',
    squad1_score INT DEFAULT 0,
    squad2_score INT DEFAULT 0,
    winner_id INT DEFAULT NULL,
    FOREIGN KEY (squad1_id) REFERENCES squads(squad_id),
    FOREIGN KEY (squad2_id) REFERENCES squads(squad_id),
    FOREIGN KEY (winner_id) REFERENCES squads(squad_id)
);

-- Admin accounts
CREATE TABLE admins (
    admin_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(128) NOT NULL
);

-- Default admin login: username = admin / password = admin123
-- (hash generated with SHA-256, see PasswordUtil.java)
INSERT INTO admins (username, password_hash) VALUES
('admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9');

-- Leaderboard view — ranks squads by points, then win rate
CREATE OR REPLACE VIEW v_leaderboard AS
SELECT
    s.squad_id,
    s.squad_name,
    s.wins,
    s.losses,
    s.points,
    ROW_NUMBER() OVER (ORDER BY s.points DESC, s.wins DESC) AS rank_position
FROM squads s
ORDER BY s.points DESC, s.wins DESC;

-- Sample squads for testing
INSERT INTO squads (squad_name, leader_name, email, phone, wins, losses, points) VALUES
('Nova Strikers', 'Rahul Verma', 'rahul@example.com', '9990001111', 3, 1, 9),
('Shadow Reapers', 'Aisha Khan', 'aisha@example.com', '9990002222', 2, 2, 6),
('Iron Wolves', 'Dev Patel', 'dev@example.com', '9990003333', 1, 3, 3);

INSERT INTO players (squad_id, player_name, in_game_id, role) VALUES
(1, 'Rahul Verma', 'NOVA_Rahul', 'IGL'),
(1, 'Sameer Joshi', 'NOVA_Smr', 'Fragger'),
(2, 'Aisha Khan', 'SR_Aisha', 'IGL'),
(2, 'Farhan Ali', 'SR_Farhan', 'Sniper'),
(3, 'Dev Patel', 'IW_Dev', 'IGL');

-- Sample matches
INSERT INTO matches (squad1_id, squad2_id, match_time, status, squad1_score, squad2_score, winner_id) VALUES
(1, 2, '2026-09-10 18:00:00', 'COMPLETED', 2, 1, 1),
(2, 3, '2026-09-15 18:00:00', 'COMPLETED', 2, 0, 2),
(1, 3, '2026-09-25 18:00:00', 'SCHEDULED', 0, 0, NULL);
