package com.tournament.dao;

import com.tournament.db.DBConnection;
import com.tournament.model.Player;
import com.tournament.model.Squad;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SquadDAO {

    /** Registers a squad and its players in a single transaction. Returns the new squad_id, or -1 on failure. */
    public int registerSquad(Squad squad) {
        String squadSql = "INSERT INTO squads (squad_name, leader_name, email, phone, game) VALUES (?, ?, ?, ?, ?)";
        String playerSql = "INSERT INTO players (squad_id, player_name, in_game_id, role) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int squadId;
                try (PreparedStatement ps = conn.prepareStatement(squadSql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, squad.getSquadName());
                    ps.setString(2, squad.getLeaderName());
                    ps.setString(3, squad.getEmail());
                    ps.setString(4, squad.getPhone());
                    ps.setString(5, squad.getGame());
                    ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) {
                            squadId = rs.getInt(1);
                        } else {
                            conn.rollback();
                            return -1;
                        }
                    }
                }

                try (PreparedStatement ps = conn.prepareStatement(playerSql)) {
                    for (Player p : squad.getPlayers()) {
                        ps.setInt(1, squadId);
                        ps.setString(2, p.getPlayerName());
                        ps.setString(3, p.getInGameId());
                        ps.setString(4, p.getRole());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }

                conn.commit();
                return squadId;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /** True if a squad with this name already exists. */
    public boolean squadNameExists(String squadName) {
        String sql = "SELECT 1 FROM squads WHERE squad_name = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, squadName);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Leaderboard ordered by points desc, then wins desc. */
    public List<Squad> getLeaderboard() {
        List<Squad> list = new ArrayList<>();
        String sql = "SELECT squad_id, squad_name, leader_name, email, phone, game, wins, losses, points " +
                     "FROM squads ORDER BY points DESC, wins DESC, squad_name ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Squad> getAllSquads() {
        return getLeaderboard();
    }

    public Squad getSquadById(int squadId) {
        String sql = "SELECT squad_id, squad_name, leader_name, email, phone, game, wins, losses, points " +
                     "FROM squads WHERE squad_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, squadId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Squad s = mapRow(rs);
                    s.setPlayers(getPlayersForSquad(squadId));
                    return s;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Player> getPlayersForSquad(int squadId) {
        List<Player> players = new ArrayList<>();
        String sql = "SELECT player_id, player_name, in_game_id, role FROM players WHERE squad_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, squadId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Player p = new Player();
                    p.setPlayerId(rs.getInt("player_id"));
                    p.setSquadId(squadId);
                    p.setPlayerName(rs.getString("player_name"));
                    p.setInGameId(rs.getString("in_game_id"));
                    p.setRole(rs.getString("role"));
                    players.add(p);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return players;
    }

    /** Increments wins/points or losses/points for a squad after a match result. */
    public void applyMatchOutcome(int squadId, boolean won) {
        String sql = won
                ? "UPDATE squads SET wins = wins + 1, points = points + 3 WHERE squad_id = ?"
                : "UPDATE squads SET losses = losses + 1, points = points + 1 WHERE squad_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, squadId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Squad mapRow(ResultSet rs) throws SQLException {
        Squad s = new Squad();
        s.setSquadId(rs.getInt("squad_id"));
        s.setSquadName(rs.getString("squad_name"));
        s.setLeaderName(rs.getString("leader_name"));
        s.setEmail(rs.getString("email"));
        s.setPhone(rs.getString("phone"));
        s.setGame(rs.getString("game"));
        s.setWins(rs.getInt("wins"));
        s.setLosses(rs.getInt("losses"));
        s.setPoints(rs.getInt("points"));
        return s;
    }
}