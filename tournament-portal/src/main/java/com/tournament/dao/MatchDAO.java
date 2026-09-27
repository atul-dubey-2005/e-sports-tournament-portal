package com.tournament.dao;

import com.tournament.db.DBConnection;
import com.tournament.model.Match;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MatchDAO {

    private static final String SELECT_BASE =
        "SELECT m.match_id, m.squad1_id, m.squad2_id, m.match_time, m.status, " +
        "       m.squad1_score, m.squad2_score, m.winner_id, " +
        "       s1.squad_name AS squad1_name, s2.squad_name AS squad2_name, s1.game AS game " +
        "FROM matches m " +
        "JOIN squads s1 ON m.squad1_id = s1.squad_id " +
        "JOIN squads s2 ON m.squad2_id = s2.squad_id ";

    public List<Match> getAllMatches() {
        List<Match> matches = new ArrayList<>();
        String sql = SELECT_BASE + "ORDER BY m.match_time ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                matches.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return matches;
    }

    public List<Match> getScheduledMatches() {
        List<Match> matches = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE m.status = 'SCHEDULED' ORDER BY m.match_time ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                matches.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return matches;
    }

    public Match getMatchById(int matchId) {
        String sql = SELECT_BASE + "WHERE m.match_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, matchId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /** Admin: schedule a new match between two squads. */
    public boolean scheduleMatch(int squad1Id, int squad2Id, Timestamp matchTime) {
        String sql = "INSERT INTO matches (squad1_id, squad2_id, match_time, status) VALUES (?, ?, ?, 'SCHEDULED')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, squad1Id);
            ps.setInt(2, squad2Id);
            ps.setTimestamp(3, matchTime);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Admin: record the result of a match. Updates the match row AND the two squads'
     * wins/losses/points in one transaction. Returns false if the match is already completed
     * or does not exist (prevents double-counting points).
     */
    public boolean recordResult(int matchId, int squad1Score, int squad2Score) {
        String checkSql = "SELECT squad1_id, squad2_id, status FROM matches WHERE match_id = ? FOR UPDATE";
        String updateMatchSql = "UPDATE matches SET squad1_score = ?, squad2_score = ?, winner_id = ?, status = 'COMPLETED' WHERE match_id = ?";
        String updateSquadSql = "UPDATE squads SET wins = wins + ?, losses = losses + ?, points = points + ? WHERE squad_id = ?";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int squad1Id, squad2Id;
                try (PreparedStatement ps = conn.prepareStatement(checkSql)) {
                    ps.setInt(1, matchId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) { conn.rollback(); return false; }
                        if ("COMPLETED".equals(rs.getString("status"))) { conn.rollback(); return false; }
                        squad1Id = rs.getInt("squad1_id");
                        squad2Id = rs.getInt("squad2_id");
                    }
                }

                int winnerId;
                if (squad1Score == squad2Score) {
                    // Draw: no winner_id, both get 1 point, no win/loss change
                    winnerId = 0;
                } else {
                    winnerId = squad1Score > squad2Score ? squad1Id : squad2Id;
                }

                try (PreparedStatement ps = conn.prepareStatement(updateMatchSql)) {
                    ps.setInt(1, squad1Score);
                    ps.setInt(2, squad2Score);
                    if (winnerId == 0) ps.setNull(3, Types.INTEGER); else ps.setInt(3, winnerId);
                    ps.setInt(4, matchId);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = conn.prepareStatement(updateSquadSql)) {
                    if (winnerId == 0) {
                        // draw: +1 point each, no win/loss
                        ps.setInt(1, 0); ps.setInt(2, 0); ps.setInt(3, 1); ps.setInt(4, squad1Id);
                        ps.executeUpdate();
                        ps.setInt(1, 0); ps.setInt(2, 0); ps.setInt(3, 1); ps.setInt(4, squad2Id);
                        ps.executeUpdate();
                    } else {
                        int loserId = (winnerId == squad1Id) ? squad2Id : squad1Id;
                        // winner: +1 win, +3 points
                        ps.setInt(1, 1); ps.setInt(2, 0); ps.setInt(3, 3); ps.setInt(4, winnerId);
                        ps.executeUpdate();
                        // loser: +1 loss, +1 point
                        ps.setInt(1, 0); ps.setInt(2, 1); ps.setInt(3, 1); ps.setInt(4, loserId);
                        ps.executeUpdate();
                    }
                }

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Match mapRow(ResultSet rs) throws SQLException {
        Match m = new Match();
        m.setMatchId(rs.getInt("match_id"));
        m.setSquad1Id(rs.getInt("squad1_id"));
        m.setSquad2Id(rs.getInt("squad2_id"));
        m.setSquad1Name(rs.getString("squad1_name"));
        m.setSquad2Name(rs.getString("squad2_name"));
        m.setGame(rs.getString("game"));
        m.setMatchTime(rs.getTimestamp("match_time"));
        m.setStatus(rs.getString("status"));
        m.setSquad1Score(rs.getInt("squad1_score"));
        m.setSquad2Score(rs.getInt("squad2_score"));
        int winnerId = rs.getInt("winner_id");
        m.setWinnerId(rs.wasNull() ? null : winnerId);
        return m;
    }
}
