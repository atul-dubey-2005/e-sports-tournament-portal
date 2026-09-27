package com.tournament.model;

import java.util.ArrayList;
import java.util.List;

public class Squad {
    private int squadId;
    private String squadName;
    private String leaderName;
    private String email;
    private String phone;
    private String game;
    private int wins;
    private int losses;
    private int points;
    private List<Player> players = new ArrayList<>();

    public Squad() {}

    public int getSquadId() { return squadId; }
    public void setSquadId(int squadId) { this.squadId = squadId; }

    public String getSquadName() { return squadName; }
    public void setSquadName(String squadName) { this.squadName = squadName; }

    public String getLeaderName() { return leaderName; }
    public void setLeaderName(String leaderName) { this.leaderName = leaderName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getGame() { return game; }
    public void setGame(String game) { this.game = game; }

    public int getWins() { return wins; }
    public void setWins(int wins) { this.wins = wins; }

    public int getLosses() { return losses; }
    public void setLosses(int losses) { this.losses = losses; }

    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }

    public List<Player> getPlayers() { return players; }
    public void setPlayers(List<Player> players) { this.players = players; }
}