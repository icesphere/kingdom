package com.kingdom.model;

import javax.persistence.*;
import java.util.Date;

@Table(name = "errors")
@Entity
public class GameError {

    public static final int COMPUTER_ERROR = 1;
    public static final int GAME_ERROR = 2;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "errorid")
    private int errorId;

    private int type;

    private String error;

    private Date date;

    private String history;

    public GameError() {
    }

    public GameError(int type, String error) {
        this.type = type;
        this.error = error;
        this.date = new Date();
    }

    public int getErrorId() {
        return errorId;
    }

    public void setErrorId(int errorId) {
        this.errorId = errorId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getHistory() {
        return history;
    }

    public void setHistory(String history) {
        this.history = history;
    }

    public boolean isComputerError() {
        return type == COMPUTER_ERROR;
    }
}
