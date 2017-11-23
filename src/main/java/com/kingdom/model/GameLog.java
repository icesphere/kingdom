package com.kingdom.model;


import javax.persistence.*;

@Table(name = "game_log")
@Entity
public class GameLog {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "logid")
    private int logId;

    @Column(name = "gameid")
    private int gameId;

    private String log;

    public int getLogId() {
        return logId;
    }

    public void setLogId(int logId) {
        this.logId = logId;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }
}
