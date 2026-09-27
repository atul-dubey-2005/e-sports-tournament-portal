package com.tournament.model;

public class Player {
    private int playerId;
    private int squadId;
    private String playerName;
    private String inGameId;
    private String role;

    public Player() {}

    public Player(String playerName, String inGameId, String role) {
        this.playerName = playerName;
        this.inGameId = inGameId;
        this.role = role;
    }

    public int getPlayerId() { return playerId; }
    public void setPlayerId(int playerId) { this.playerId = playerId; }

    public int getSquadId() { return squadId; }
    public void setSquadId(int squadId) { this.squadId = squadId; }

    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }

    public String getInGameId() { return inGameId; }
    public void setInGameId(String inGameId) { this.inGameId = inGameId; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
