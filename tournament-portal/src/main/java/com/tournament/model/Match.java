package com.tournament.model;

import java.sql.Timestamp;

public class Match {
    private int matchId;
    private int squad1Id;
    private int squad2Id;
    private String squad1Name;
    private String squad2Name;
    private String game;
    private Timestamp matchTime;
    private String status;
    private int squad1Score;
    private int squad2Score;
    private Integer winnerId;

    public int getMatchId() { return matchId; }
    public void setMatchId(int matchId) { this.matchId = matchId; }

    public int getSquad1Id() { return squad1Id; }
    public void setSquad1Id(int squad1Id) { this.squad1Id = squad1Id; }

    public int getSquad2Id() { return squad2Id; }
    public void setSquad2Id(int squad2Id) { this.squad2Id = squad2Id; }

    public String getSquad1Name() { return squad1Name; }
    public void setSquad1Name(String squad1Name) { this.squad1Name = squad1Name; }

    public String getSquad2Name() { return squad2Name; }
    public void setSquad2Name(String squad2Name) { this.squad2Name = squad2Name; }

    public String getGame() { return game; }
    public void setGame(String game) { this.game = game; }

    public Timestamp getMatchTime() { return matchTime; }
    public void setMatchTime(Timestamp matchTime) { this.matchTime = matchTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getSquad1Score() { return squad1Score; }
    public void setSquad1Score(int squad1Score) { this.squad1Score = squad1Score; }

    public int getSquad2Score() { return squad2Score; }
    public void setSquad2Score(int squad2Score) { this.squad2Score = squad2Score; }

    public Integer getWinnerId() { return winnerId; }
    public void setWinnerId(Integer winnerId) { this.winnerId = winnerId; }
}
